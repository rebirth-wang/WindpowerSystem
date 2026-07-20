package com.fastbee.sip.service.impl;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastbee.media.model.PtzInput;
import com.fastbee.sip.domain.SipDevice;
import com.fastbee.sip.enums.Direct;
import com.fastbee.sip.server.MessageInvoker;
import com.fastbee.sip.server.msg.DeviceControl;
import com.fastbee.sip.service.ISipDeviceService;
import com.fastbee.sip.service.ISipPtzCmdService;

@Slf4j
@Service
public class SipPtzCmdServiceImpl implements ISipPtzCmdService {

    @Autowired
    private MessageInvoker messageInvoker;

    @Autowired
    private ISipDeviceService sipDeviceService;

    @Override
    public Boolean ptz(SipDevice device, String channelId, PtzInput ptz) {
        Map<Direct, Integer> directAndSpeed = new HashMap<>();
        Direct direct;
        if (ptz.getLeftRight() == 1) {
            direct = Direct.RIGHT;
        } else if (ptz.getLeftRight() == 2) {
            direct = Direct.LEFT;
        } else if (ptz.getUpDown() == 1) {
            direct = Direct.UP;
        } else if (ptz.getUpDown() == 2) {
            direct = Direct.DOWN;
        } else if (ptz.getInOut() == 1) {
            direct = Direct.ZOOM_IN;
        } else if (ptz.getInOut() == 2) {
            direct = Direct.ZOOM_OUT;
        } else {
            direct = Direct.STOP;
        }
        directAndSpeed.put(direct, ptz.getMoveSpeed());
        DeviceControl control = new DeviceControl();
        control.setPtzDirect(directAndSpeed);
        control.setDeviceId(channelId);
        return messageInvoker.deviceControl(device, control);
    }

    public Boolean directPtzCmd(String deviceId, String channelId, Direct direct, Integer speed) {
        Map<Direct, Integer> directAndSpeed = new HashMap<>();
        directAndSpeed.put(direct, speed);
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        if (dev != null) {
            DeviceControl control = new DeviceControl();
            control.setPtzDirect(directAndSpeed);
            control.setDeviceId(channelId);
            return messageInvoker.deviceControl(dev, control);
        }
        return false;
    }

    @Override
    public Boolean ptzCmd(String deviceId, String channelId, String cmd) {
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        if (dev != null) {
            DeviceControl control = new DeviceControl();
            control.setPtzCmd(cmd);
            control.setDeviceId(channelId);
            return messageInvoker.deviceControl(dev, control);
        }
        return false;
    }

    @Override
    public Boolean frontEndCommand(String deviceId, String channelId, int cmdCode, int parameter1, int parameter2, int combindCode2) {
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        if (dev != null) {
            messageInvoker.frontEndCmd(dev, channelId, cmdCode, parameter1, parameter2, combindCode2);
            return true;
        }
        return false;
    }

    /**
     * 查询设备预置位置
     *
     * @param device 视频设备
     */
    @Override
    public Boolean presetQuery(SipDevice device, String channelId){
        messageInvoker.presetQuery(device, channelId);
        return true;
    }

    @Override
    public Boolean presetQuery(String deviceId, String channelId) {
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        if (dev != null) {
            messageInvoker.presetQuery(dev, channelId);
            return true;
        }
        return true;
    }

}
