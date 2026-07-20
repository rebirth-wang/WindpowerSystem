package com.fastbee.iot.data.emq;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jakarta.annotation.Resource;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

import com.fastbee.common.enums.ServerType;
import com.fastbee.common.enums.TopicType;
import com.fastbee.common.extend.core.domin.mq.DeviceReportBo;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.common.utils.gateway.mq.TopicsUtils;
import com.fastbee.iot.service.IScriptService;
import com.fastbee.mqttclient.IEmqxMessageProducer;

@Slf4j
@Component
public class subscribeCallback implements IMqttMessageListener {
    @Resource
    private IScriptService scriptService;
    @Resource
    private TopicsUtils topicsUtils;
    @Resource
    private IEmqxMessageProducer emqxMessageSerice;

    // 使用独立的线程池处理消息
    private final ExecutorService messageExecutor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(),
            new ThreadFactoryBuilder()
                    .setNameFormat("mqtt-message-processor-%d")
                    .build()
    );


    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        messageExecutor.submit(() -> {
            processMessageAsync(topic, mqttMessage);
        });
    }

    private void processMessageAsync(String topic, MqttMessage mqttMessage) {
        try {
            byte[] payload = mqttMessage.getPayload();

            // 避免不必要的字符串转换
            String message;
            if (isPrintableAscii(payload)) {
                message = new String(payload, StandardCharsets.UTF_8);
            } else {
                message = ByteBufUtil.hexDump(Unpooled.wrappedBuffer(payload));
            }

            processBusinessLogic(topic, mqttMessage, message);

        } catch (Exception e) {
            log.error("处理MQTT消息异常: topic={}", topic, e);
        }
    }


    private void processBusinessLogic(String topic, MqttMessage mqttMessage,
                                      String message) {
        // 原有的业务逻辑，现在在异步线程中执行
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        if (bytes.length != mqttMessage.getPayload().length) {
            bytes = mqttMessage.getPayload();
        }

        String serialNumber = topicsUtils.parseSerialNumber(topic);
        log.debug("设备：{},消息：{}",serialNumber, message);
        Long productId = 0L;
        if (!topic.contains(TopicType.FETCH_UPGRADE_REPLY.getTopicSuffix()) &&
                !topic.contains(TopicType.HTTP_UPGRADE_REPLY.getTopicSuffix())) {
            productId = topicsUtils.parseProductId(topic);
        }

        String name = topicsUtils.parseTopicName(topic);
        DeviceReportBo reportBo = DeviceReportBo.builder()
                .serialNumber(serialNumber)
                .productId(productId)
                .data(bytes)
                .platformDate(DateUtils.getNowDate())
                .topicName(topic)
                .serverType(ServerType.MQTT)
                .build();

        emqxMessageSerice.sendEmqxMessage(name, reportBo);
    }

    private boolean isPrintableAscii(byte[] bytes) {
        for (byte b : bytes) {
            if (b < 32 || b > 126) {
                return false;
            }
        }
        return true;
    }
}
