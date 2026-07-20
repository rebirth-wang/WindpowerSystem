package com.fastbee.sip.service;

import java.util.List;

import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.fastbee.media.model.CommonChannel;
import com.fastbee.media.model.RecordList;
import com.fastbee.sip.domain.SipDevice;
import com.fastbee.sip.server.msg.Alarm;

public interface IMqttService {
    void publishInfo(SipDevice device);
    void publishStatus(SipDevice device, int deviceStatus);
    void publishEvent(Alarm alarm);
    void publishProperty(Long productId, String deviceNum, List<ThingsModelSimpleItem> thingsList, int delay);
    void publishChannelsProperty(String DeviceId, List<CommonChannel> channels);
    void publishRecordsProperty(String DeviceId, RecordList recordList);
}
