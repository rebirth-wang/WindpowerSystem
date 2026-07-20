package com.fastbee.iot.data.service.impl;

import java.util.Date;
import java.util.Objects;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.fastbee.common.enums.TopicType;
import com.fastbee.common.extend.core.domin.mq.DeviceTestReportBo;
import com.fastbee.common.extend.core.domin.mq.message.DeviceMessage;
import com.fastbee.common.utils.gateway.mq.TopicsUtils;
import com.fastbee.iot.data.service.IDeviceTestService;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.service.IDeviceService;
import com.fastbee.mqtt.manager.MqttRemoteManager;
import com.fastbee.mqtt.model.PushMessageBo;

/**
 * @author bill
 */
@Slf4j
@Component
public class DeviceTestHandler implements IDeviceTestService {

    @Resource
    private TopicsUtils topicsUtils;
    @Resource
    private MqttRemoteManager mqttRemoteManager;
    @Resource
    private IDeviceService deviceService;

    /**
     * 处理数据调试信息
     * @param testReportBo
     */
    @Override
    public void messageHandler(DeviceTestReportBo testReportBo){
        boolean isReply = testReportBo.getIsReply();
        Long productId = testReportBo.getProductId();
        String serialNumber = testReportBo.getSerialNumber();
        DeviceMessage deviceMessage = new DeviceMessage();
        deviceMessage.setMessage(testReportBo.getSources());
        deviceMessage.setTime(new Date());
        if (!isReply) {
            deviceMessage.setTopicName(TopicType.SERVICE_INVOKE_REPLY.getTopicSuffix());
        } else {
            deviceMessage.setTopicName(TopicType.PROPERTY_POST.getTopicSuffix());
        }
        if (Objects.isNull(productId)){
            Device device = deviceService.selectDeviceBySerialNumber(serialNumber);
            productId = device.getProductId();
        }
        //发送至前端
        PushMessageBo messageBo = new PushMessageBo();
        messageBo.setTopic(topicsUtils.buildTopic(productId, serialNumber, TopicType.WS_SERVICE_INVOKE));
        JSONObject pushObj = new JSONObject();
        pushObj.put("message", testReportBo.getThingsModelSimpleItem());
        pushObj.put("sources",testReportBo.getSources());
        messageBo.setMessage(JSON.toJSONString(pushObj));
        mqttRemoteManager.pushCommon(messageBo);
    }

}
