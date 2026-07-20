package com.fastbee.sip.handler.req.message.notify.cmdType;

import java.text.ParseException;

import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.header.ViaHeader;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.xml.sax.SAXException;

import com.fastbee.common.enums.DeviceStatus;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.service.IDeviceService;
import com.fastbee.iot.service.IDeviceUpdateService;
import com.fastbee.sip.domain.SipDevice;
import com.fastbee.sip.handler.req.ReqAbstractHandler;
import com.fastbee.sip.handler.req.message.IMessageHandler;
import com.fastbee.sip.handler.req.message.notify.NotifyMessageHandler;
import com.fastbee.sip.server.MessageInvoker;
import com.fastbee.sip.service.ISipCacheService;
import com.fastbee.sip.service.ISipDeviceService;
import com.fastbee.sip.util.XmlUtil;

@Slf4j
@Component
public class KeepaliveHandler extends ReqAbstractHandler implements InitializingBean, IMessageHandler {

    @Autowired
    private NotifyMessageHandler notifyMessageHandler;

    @Autowired
    private ISipDeviceService sipDeviceService;

    @Autowired
    private MessageInvoker messageInvoker;

    @Autowired
    private IDeviceService deviceService;

    @Resource
    private IDeviceUpdateService deviceUpdateService;

    @Autowired
    private ISipCacheService sipCacheService;

    @Override
    public void handlerCmdType(RequestEvent evt, SipDevice device, Element element) {
        try {
            Element rootElement = getRootElement(evt);
            String deviceId = XmlUtil.getText(rootElement, "DeviceID");
            if (sipDeviceService.exists(deviceId)) {
                ViaHeader viaHeader = (ViaHeader) evt.getRequest().getHeader(ViaHeader.NAME);
                String received = viaHeader.getReceived();
                int rPort = viaHeader.getRPort();
                if (StringUtils.isEmpty(received) || rPort == -1) {
                    log.warn("本地地址替代! received:{},rPort:{} [{}:{}]", received, rPort, viaHeader.getHost(), viaHeader.getPort());
                    received = viaHeader.getHost();
                    rPort = viaHeader.getPort();
                }
                device.setLastConnectTime(DateUtils.getNowDate());
                device.setIp(received);
                device.setPort(rPort);
                device.setHostAddress(received.concat(":").concat(String.valueOf(rPort)));
                log.info("设备:{} 心跳上报时间:{}", deviceId, device.getLastConnectTime());
                recordKeepaliveStatus(deviceId, device, rootElement);
                //log.warn("设备:{} 心跳上报时间:{}",deviceId,device.getLastconnecttime());
                // 更新在线状态到emqx
                // mqttService.publishStatus(device, 3);
                // 更新在线状态
                sipDeviceService.updateSipDeviceStatus(device);
                Device dev = deviceService.selectDeviceBySerialNumber(deviceId);
                if (dev != null && dev.getStatus() != DeviceStatus.ONLINE.getType()) {
                    dev.setStatus(DeviceStatus.ONLINE.getType());
                    deviceUpdateService.updateDevice(dev);
                }
                // 更新通道状态
                messageInvoker.catalogQuery(device);
                // 回复200 OK
                responseAck(evt);
            } else {
                JSONObject json = new JSONObject();
                json.put("deviceId", deviceId);
                json.put("status", "UNKNOWN_DEVICE");
                json.put("message", "设备心跳上报但平台未找到该国标设备");
                json.put("xmlStatus", XmlUtil.getText(rootElement, "Status"));
                sipCacheService.recordDeviceStatus(deviceId, "KEEPALIVE", json);
            }

        } catch (ParseException | SipException | InvalidArgumentException | DocumentException | SAXException e) {
            e.printStackTrace();
        }
    }

    private void recordKeepaliveStatus(String deviceId, SipDevice device, Element rootElement) {
        JSONObject json = new JSONObject();
        json.put("deviceId", deviceId);
        json.put("status", "ONLINE");
        json.put("message", "设备心跳上报");
        json.put("ip", device.getIp());
        json.put("port", device.getPort());
        json.put("hostAddress", device.getHostAddress());
        json.put("lastConnectTime", device.getLastConnectTime());
        json.put("xmlStatus", XmlUtil.getText(rootElement, "Status"));
        if (!ObjectUtils.isEmpty(deviceId)) {
            sipCacheService.recordDeviceStatus(deviceId, "KEEPALIVE", json);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String cmdType = "Keepalive";
        notifyMessageHandler.addHandler(cmdType, this);
    }
}
