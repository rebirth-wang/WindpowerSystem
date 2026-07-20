package com.fastbee.sip.handler.req;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;

import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.header.*;
import javax.sip.message.Request;
import javax.sip.message.Response;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSONObject;
import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.header.Expires;
import gov.nist.javax.sip.header.SIPDateHeader;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.fastbee.common.enums.DeviceStatus;
import com.fastbee.common.extend.config.SysSipConfig;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.service.IDeviceService;
import com.fastbee.iot.service.IDeviceUpdateService;
import com.fastbee.media.util.DigestAuthUtil;
import com.fastbee.sip.domain.SipConfig;
import com.fastbee.sip.domain.SipDevice;
import com.fastbee.sip.handler.IReqHandler;
import com.fastbee.sip.model.SipDate;
import com.fastbee.sip.server.IGBListener;
import com.fastbee.sip.server.MessageInvoker;
import com.fastbee.sip.server.SIPSender;
import com.fastbee.sip.server.session.InviteSessionManager;
import com.fastbee.sip.service.IMqttService;
import com.fastbee.sip.service.ISipCacheService;
import com.fastbee.sip.service.ISipConfigService;
import com.fastbee.sip.service.ISipDeviceService;

@Slf4j
@Component
public class RegisterReqHandler extends ReqAbstractHandler implements InitializingBean, IReqHandler {

    @Autowired
    private ISipDeviceService sipDeviceService;
    @Autowired
    private IDeviceService deviceService;

    @Autowired
    private ISipConfigService sipConfigService;

    @Autowired
    private IGBListener sipListener;

    @Autowired
    private MessageInvoker messageInvoker;

    @Autowired
    private SysSipConfig sysSipConfig;

    @Autowired
    private IMqttService mqttService;

    @Autowired
    private SIPSender sipSender;

    @Resource
    private IDeviceUpdateService deviceUpdateService;

    @Autowired
    private InviteSessionManager inviteSessionManager;

    @Autowired
    private ISipCacheService sipCacheService;

    @Override
    public void processMsg(RequestEvent evt) {
        try {
            log.info("收到注册请求，开始处理");
            Request request = evt.getRequest();
            SIPRequest Sipreq = (SIPRequest) evt.getRequest();
            Response response;
            // 注册标志  0：未携带授权头或者密码错误  1：注册成功   2：注销成功
            int registerFlag;
            FromHeader fromHeader = (FromHeader) request.getHeader(FromHeader.NAME);
            AddressImpl address = (AddressImpl) fromHeader.getAddress();
            SipUri uri = (SipUri) address.getURI();
            String sipId = uri.getUser();
            //取默认Sip配置
            SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(sipId);
            if (sipConfig != null) {
                AuthorizationHeader authorhead = (AuthorizationHeader) request.getHeader(AuthorizationHeader.NAME);
                // 校验密码是否正确
                if (authorhead == null && !ObjectUtils.isEmpty(sipConfig.getPassword())) {
                    log.info("未携带授权头 回复401，sipId：" + sipId);
                    recordRegisterStatus(sipId, "UNAUTHORIZED", null, "未携带授权头");
                    response = getMessageFactory().createResponse(Response.UNAUTHORIZED, request);
                    new DigestAuthUtil().generateChallenge(getHeaderFactory(), response, sipConfig.getDomainAlias());
                    sipSender.transmitRequest(Sipreq.getLocalAddress().getHostAddress(), response);
                    return;
                }

                boolean pcheck = new DigestAuthUtil().doAuthenticatePlainTextPassword(request,
                        sipConfig.getPassword());

                boolean syscheck = new DigestAuthUtil().doAuthenticatePlainTextPassword(request,
                        sysSipConfig.getPassword());
                if (!pcheck && !syscheck) {
                    // 注册失败
                    response = getMessageFactory().createResponse(Response.FORBIDDEN, request);
                    response.setReasonPhrase("wrong password");
                    log.info("[注册请求] 密码/SIP服务器ID错误, 回复403 sipId：" + sipId);
                    recordRegisterStatus(sipId, "FORBIDDEN", null, "密码/SIP服务器ID错误");
                    sipSender.transmitRequest(Sipreq.getLocalAddress().getHostAddress(), response);
                    return;
                }
                response = getMessageFactory().createResponse(Response.OK, request);
                // 添加date头
                SIPDateHeader dateHeader = new SIPDateHeader();
                // 使用自己修改的
                SipDate sipDate = new SipDate(Calendar.getInstance(Locale.ENGLISH).getTimeInMillis());
                dateHeader.setDate(sipDate);
                response.addHeader(dateHeader);

                ExpiresHeader expiresHeader = (ExpiresHeader) request.getHeader(Expires.NAME);
                // 添加Contact头
                response.addHeader(request.getHeader(ContactHeader.NAME));
                // 添加Expires头
                response.addHeader(request.getExpires());
                ViaHeader viaHeader = (ViaHeader) request.getHeader(ViaHeader.NAME);
                String received = viaHeader.getReceived();
                int rPort = viaHeader.getRPort();
                // 本地模拟设备 received 为空 rPort 为 -1
                // 解析本地地址替代
                if (StringUtils.isEmpty(received) || rPort == -1) {
                    log.warn("本地地址替代! received:{},rPort:{} [{}:{}]", received, rPort, viaHeader.getHost(), viaHeader.getPort());
                    received = viaHeader.getHost();
                    rPort = viaHeader.getPort();
                }
                SipDevice device = new SipDevice();
                ;
                device.setStreamMode("UDP");
                device.setDeviceSipId(sipId);
                device.setIp(received);
                device.setPort(rPort);
                device.setHostAddress(received.concat(":").concat(String.valueOf(rPort)));
                // 注销成功
                if (expiresHeader != null && expiresHeader.getExpires() == 0) {
                    registerFlag = 2;
                } else {
                    registerFlag = 1;
                    ViaHeader reqViaHeader = (ViaHeader) request.getHeader(ViaHeader.NAME);
                    String transport = reqViaHeader.getTransport();
                    device.setTransport("TCP".equalsIgnoreCase(transport) ? "TCP" : "UDP");
                }
                sipSender.transmitRequest(Sipreq.getLocalAddress().getHostAddress(), response);
                Device iotdev = deviceService.selectDeviceBySerialNumber(device.getDeviceSipId());
                if(iotdev != null) {
                    // 注册成功
                    if (registerFlag == 1) {
                        log.info("注册成功! sipId:" + device.getDeviceSipId());
                        recordRegisterStatus(sipId, "ONLINE", device, "注册成功");
                        String newServerId = sysSipConfig.getServerId();
                        // 检测设备是否从其他实例迁移过来
                        SipDevice existDevice = sipDeviceService.selectSipDeviceBySipId(sipId);
                        if (existDevice != null
                                && existDevice.getServerId() != null
                                && !existDevice.getServerId().isEmpty()
                                && !existDevice.getServerId().equals(newServerId)) {
                            log.info("[设备迁移] 设备 {} 从实例 {} 迁移到 {}，清理旧会话",
                                    sipId, existDevice.getServerId(), newServerId);
                            // 清理该设备在 Redis 中的所有 Invite 会话（数据已全局共享）
                            inviteSessionManager.removeInviteInfo(null, sipId, null, null);
                        }
                        device.setRegisterTime(DateUtils.getNowDate());
                        device.setServerId(newServerId);
                        sipDeviceService.updateDevice(device);
                        if (iotdev.getStatus() != DeviceStatus.ONLINE.getType() && iotdev.getStatus() != DeviceStatus.FORBIDDEN.getType()) {
                            iotdev.setStatus(DeviceStatus.ONLINE.getType());
                            deviceUpdateService.updateDeviceStatusAndLocation(iotdev, device.getIp());
                        }
                        // 重新注册更新设备和通道，以免设备替换或更新后信息无法更新
                        onRegister(device);
                    } else if (registerFlag == 2) {
                        log.info("注销成功! deviceId:" + device.getDeviceId());
                        recordRegisterStatus(sipId, "OFFLINE", device, "注销成功");
                        mqttService.publishStatus(device, DeviceStatus.OFFLINE.getType());
                        iotdev.setStatus(DeviceStatus.OFFLINE.getType());
                        deviceUpdateService.updateDeviceStatusAndLocation(iotdev, null);
                    }
                } else {
                    log.warn("设备不存在,请先添加设备! sipId:" + device.getDeviceSipId());
                    recordRegisterStatus(sipId, "DEVICE_NOT_FOUND", device, "设备不存在");
                }
            } else {
                recordRegisterStatus(sipId, "SIP_CONFIG_NOT_FOUND", null, "未找到SIP配置");
            }
        } catch (SipException | NoSuchAlgorithmException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void recordRegisterStatus(String sipId, String status, SipDevice device, String message) {
        JSONObject json = new JSONObject();
        json.put("deviceId", sipId);
        json.put("status", status);
        json.put("message", message);
        if (device != null) {
            json.put("ip", device.getIp());
            json.put("port", device.getPort());
            json.put("hostAddress", device.getHostAddress());
            json.put("transport", device.getTransport());
            json.put("serverId", device.getServerId());
        }
        sipCacheService.recordDeviceStatus(sipId, "REGISTER", json);
    }

    public void onRegister(SipDevice device) {
        // TODO 查询设备信息和下挂设备信息 自动拉流
        messageInvoker.deviceInfoQuery(device);
        messageInvoker.catalogQuery(device);
        messageInvoker.subscribe(device, 0, 0, 3600, "0", null, null);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String method = "REGISTER";
        sipListener.addRequestProcessor(method, this);
    }
}
