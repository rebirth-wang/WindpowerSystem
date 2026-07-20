package com.fastbee.mqttclient;

import com.fastbee.common.extend.core.domin.mq.DeviceReportBo;

/**
 * @author bill
 */
public interface IEmqxMessageProducer {

    public void sendEmqxMessage(String topicName, DeviceReportBo deviceReportBo);


}
