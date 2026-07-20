package com.fastbee.http.service;

import java.util.List;

import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.fastbee.iot.domain.Device;

public interface IHttpMqttService {
    void publishInfo(Device device);

    void publishStatus(Device device, int deviceStatus);

    void publishEvent(Device device, List<ThingsModelSimpleItem> thingsList);

    void publishProperty(Device device, List<ThingsModelSimpleItem> thingsList, int delay);

    void publishMonitor(Device device, List<ThingsModelSimpleItem> thingsList);
}
