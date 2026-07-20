package com.fastbee.sip.service;

import com.fastbee.media.model.PtzInput;
import com.fastbee.sip.domain.SipDevice;
import com.fastbee.sip.enums.Direct;

public interface ISipPtzCmdService {
    Boolean ptz(SipDevice device, String channelId, PtzInput ptz);
    Boolean directPtzCmd(String deviceId, String channelId, Direct direct, Integer speed);
    Boolean ptzCmd(String deviceId, String channelId, String cmd);
    Boolean frontEndCommand(String deviceId, String channelId, int cmdCode, int parameter1, int parameter2, int combindCode2);
    Boolean presetQuery(SipDevice device, String channelId);
    Boolean presetQuery(String deviceId, String channelId);
}
