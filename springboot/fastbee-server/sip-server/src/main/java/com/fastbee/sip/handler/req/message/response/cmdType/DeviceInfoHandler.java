package com.fastbee.sip.handler.req.message.response.cmdType;

import java.text.ParseException;

import javax.annotation.Resource;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.message.Response;

import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.xml.sax.SAXException;

import com.fastbee.iot.domain.Device;
import com.fastbee.iot.service.IDeviceService;
import com.fastbee.sip.domain.SipDevice;
import com.fastbee.sip.handler.req.ReqAbstractHandler;
import com.fastbee.sip.handler.req.message.IMessageHandler;
import com.fastbee.sip.handler.req.message.response.ResponseMessageHandler;
import com.fastbee.sip.service.IMqttService;
import com.fastbee.sip.service.ISipDeviceService;
import com.fastbee.sip.util.XmlUtil;

@Slf4j
@Component
public class DeviceInfoHandler extends ReqAbstractHandler implements InitializingBean, IMessageHandler {

    @Autowired
    private ResponseMessageHandler responseMessageHandler;

    @Autowired
    private ISipDeviceService sipDeviceService;

    @Autowired
    private IMqttService mqttService;

    @Resource
    private IDeviceService deviceService;
    @Override
    public void handlerCmdType(RequestEvent evt, SipDevice device, Element element) {
        try {
            Device iotdev = deviceService.selectDeviceBySerialNumber(device.getDeviceSipId());
            if(iotdev != null) {
                // 回复200 OK
                responseAck(evt);
                Element rootElement = getRootElement(evt);
                device.setDeviceName(XmlUtil.getText(rootElement, "DeviceName"));
                device.setManufacturer(XmlUtil.getText(rootElement, "Manufacturer"));
                device.setModel(XmlUtil.getText(rootElement, "Model"));
                device.setFirmware(XmlUtil.getText(rootElement, "Firmware"));
                if (StringUtils.isEmpty(device.getStreamMode())) {
                    device.setStreamMode("UDP");
                }
                // 更新到数据库
                sipDeviceService.updateDevice(device);
                // 发布设备info到emqx
                mqttService.publishInfo(device);
            } else {
                responseAck(evt, Response.NOT_FOUND,null);
                log.warn("设备不存在,请先添加设备! sipId:" + device.getDeviceSipId());
            }
        } catch (DocumentException | SipException | InvalidArgumentException | ParseException | SAXException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String cmdType = "DeviceInfo";
        responseMessageHandler.addHandler(cmdType, this);
    }
}
