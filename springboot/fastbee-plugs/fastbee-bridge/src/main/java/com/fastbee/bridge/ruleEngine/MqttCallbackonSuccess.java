package com.fastbee.bridge.ruleEngine;

import java.util.Objects;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;

/**
 * @author zhuangpengli
 */
@Slf4j
@Data
public class MqttCallbackonSuccess implements IMqttActionListener {
    private String topic;
    private Long direction;
    private MqttAsyncClient client;

    public MqttCallbackonSuccess(String topic, Long direction) {
        this.topic = topic;
        this.direction = direction;
    }
    @Override
    public void onSuccess(IMqttToken iMqttToken) {
        log.info("MqttAsyncClientId：{}，ServerURI：{}", client.getClientId(), client.getServerURI());
        if (client != null && this.direction == 1 && !Objects.equals(topic, "")) {
            MqttClientFactory.addSubscribe(client, topic);
        }
    }

    @Override
    public void onFailure(IMqttToken iMqttToken, Throwable throwable) {

    }
}
