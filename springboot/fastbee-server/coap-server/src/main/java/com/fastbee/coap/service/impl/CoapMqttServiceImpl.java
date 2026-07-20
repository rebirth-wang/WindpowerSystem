package com.fastbee.coap.service.impl;

import java.math.BigDecimal;
import java.util.List;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.fastbee.coap.service.ICoapMqttService;
import com.fastbee.common.enums.TopicType;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.fastbee.common.extend.enums.DeviceStatusEnum;
import com.fastbee.common.utils.gateway.mq.TopicsUtils;
import com.fastbee.iot.domain.Device;
import com.fastbee.mqttclient.PubMqttClient;

@Slf4j
@Service
public class CoapMqttServiceImpl implements ICoapMqttService {
    @Resource
    private PubMqttClient mqttClient;
    @Resource
    private TopicsUtils topicsUtils;

    @Override
    public void publishInfo(Device device) {
        device.setRssi(0);
        device.setStatus(DeviceStatusEnum.ONLINE.getStatus());
        device.setFirmwareVersion(BigDecimal.valueOf(1.0));
        String topic = topicsUtils.buildTopic(device.getProductId(), device.getSerialNumber(), TopicType.DEV_INFO_POST);
        mqttClient.publish(1, false, topic, JSON.toJSONString(device));

    }

    @Override
    public void publishStatus(Device device, int deviceStatus) {

    }

    public void publishFunction(Device device, List<ThingsModelSimpleItem> thingsList) {
        String topic = topicsUtils.buildTopic(device.getProductId(), device.getSerialNumber(), TopicType.FUNCTION_POST);
        if (thingsList == null) {
            mqttClient.publish(1, false, topic, "");
        } else {
            mqttClient.publish(1, false, topic, JSON.toJSONString(thingsList));
        }
    }

    @Override
    public void publishEvent(Device device, List<ThingsModelSimpleItem> thingsList) {
        String topic = topicsUtils.buildTopic(device.getProductId(), device.getSerialNumber(), TopicType.DEV_EVENT_POST);
        if (thingsList == null) {
            mqttClient.publish(1, false, topic, "");
        } else {
            mqttClient.publish(1, false, topic, JSON.toJSONString(thingsList));
        }
    }

    @Override
    public void publishProperty(Device device, List<ThingsModelSimpleItem> thingsList, int delay) {
        String pre = "";
        if (delay > 0) {
            pre = "$delayed/" + String.valueOf(delay) + "/";
        }
        String topic = topicsUtils.buildTopic(device.getProductId(), device.getSerialNumber(), TopicType.DEV_PROPERTY_POST);
        if (thingsList == null) {
            mqttClient.publish(1, false, topic, "");
        } else {
            mqttClient.publish(1, false, topic, JSON.toJSONString(thingsList));
        }
    }

    @Override
    public void publishMonitor(Device device, List<ThingsModelSimpleItem> thingsList) {
        String topic = topicsUtils.buildTopic(device.getProductId(), device.getSerialNumber(), TopicType.DEV_PROPERTY_POST);
        if (thingsList == null) {
            mqttClient.publish(1, false, topic, "");
        } else {
            mqttClient.publish(1, false, topic, JSON.toJSONString(thingsList));
        }
    }
}
