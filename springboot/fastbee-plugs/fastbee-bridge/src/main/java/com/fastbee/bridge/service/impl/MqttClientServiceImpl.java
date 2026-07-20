package com.fastbee.bridge.service.impl;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

import com.fastbee.bridge.domain.Bridge;
import com.fastbee.bridge.model.Config;
import com.fastbee.bridge.model.MqttClientConfig;
import com.fastbee.bridge.ruleEngine.MqttCallbackonSuccess;
import com.fastbee.bridge.ruleEngine.MqttClientFactory;
import com.fastbee.bridge.ruleEngine.data.BridgeData;
import com.fastbee.bridge.service.IMqttClientService;

/**
 * mqtt桥接配置表Service业务层处理
 *
 * @author gx_ma
 * @date 2024-06-03
 */
@Service
public class MqttClientServiceImpl implements IMqttClientService {

    @Override
    public void deleteMqttClientByKey(String key) {
        MqttClientFactory.remove(key);
    }

    @Override
    public MqttAsyncClient instance(Config config) {
        MqttClientConfig conf = (MqttClientConfig) config;
        try {
            MqttCallbackonSuccess callback = null;
            if (conf.getDirection() == 1) {
                callback = new MqttCallbackonSuccess(conf.getTopic(), conf.getDirection());
            }
            return MqttClientFactory.instance(conf, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Config buildConfig(Bridge bridge) {
        MqttClientConfig config = new MqttClientConfig();
        config.load(bridge.getConfigJson());
        config.setDirection(bridge.getDirection());
        config.setTopic(bridge.getRoute());
        return config;
    }

    @Override
    public Config buildConfig(BridgeData data) {
        MqttClientConfig config = new MqttClientConfig();
        config.load(data.getConfigJson());
        config.setDirection(data.getDirection());
        config.setTopic(data.getRoute());
        return config;
    }

    @Override
    public String exec(Config config, Object connect) {
        return "";
    }

    @Override
    public String exec(Config config, Object connect, byte[] msg) {
        MqttAsyncClient client = (MqttAsyncClient) connect;
        MqttClientConfig conf = (MqttClientConfig) config;
        if (msg != null) {
            MqttMessage message = new MqttMessage();
            message.setPayload(msg);
            try {
                if (conf.getDirection() == 2) {
                    IMqttDeliveryToken token = client.publish(conf.getTopic(), message);
                    return token.getMessageId() + "";
                }
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
        }
        return "";
    }

    @Override
    public int connect(Object obj) {
        MqttAsyncClient client = (MqttAsyncClient) obj;
        if (client != null && client.isConnected()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public int disconnect(Object obj) {
        MqttAsyncClient client = (MqttAsyncClient) obj;
        if (client != null && client.isConnected()) {
            try {
                client.disconnect();
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
            return 1;
        } else {
            return 0;
        }
    }
}
