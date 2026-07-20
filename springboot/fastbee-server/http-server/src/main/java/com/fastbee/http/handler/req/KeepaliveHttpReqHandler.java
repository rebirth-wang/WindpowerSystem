package com.fastbee.http.handler.req;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;

import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fastbee.common.enums.DeviceStatus;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.http.handler.IHttpReqHandler;
import com.fastbee.http.manager.HttpSessionManager;
import com.fastbee.http.server.HttpListener;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.service.IDeviceService;
import com.fastbee.iot.service.IDeviceUpdateService;

@Slf4j
@Component
public class KeepaliveHttpReqHandler implements InitializingBean, IHttpReqHandler {
    @Autowired
    private HttpListener httpListener;

    @Autowired
    private IDeviceService deviceService;

    @Resource
    private IDeviceUpdateService deviceUpdateService;

    @Autowired
    private HttpSessionManager sessionManager;

    @Override
    public void processMsg(FullHttpRequest req, HttpSession session) {
        String serialNumber = (String) session.getAttribute("SerialNumber");
        Device device = deviceService.selectDeviceBySerialNumber(serialNumber);
        if (device != null) {
            device.setActiveTime(DateUtils.getNowDate());
            device.setStatus(DeviceStatus.ONLINE.getType());
            deviceUpdateService.updateDevice(device);
            sessionManager.refreshSession(session.getId());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String uri = "/keepalive";
        httpListener.addRequestProcessor(uri, this);
    }
}
