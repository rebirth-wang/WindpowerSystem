package com.fastbee.mq.producer;

import org.springframework.stereotype.Component;

import com.fastbee.common.constant.FastBeeConstant;
import com.fastbee.common.extend.core.domin.mq.DeviceReportBo;
import com.fastbee.mqttclient.IEmqxMessageProducer;

/**
 * @author bill
 */
@Component
public class EmqxMessageProducer implements IEmqxMessageProducer {


    @Override
    public void sendEmqxMessage(String topicName, DeviceReportBo deviceReportBo) {
        if (topicName.contains("property/post")) {
            MessageProducer.sendPublishMsg(deviceReportBo);
        } else if (topicName.contains(FastBeeConstant.MQTT.OTA_REPLY)) {
            MessageProducer.sendDeviceReplyMsg(deviceReportBo);
        } else {
            MessageProducer.sendOtherMsg(deviceReportBo);
        }
    }
}
