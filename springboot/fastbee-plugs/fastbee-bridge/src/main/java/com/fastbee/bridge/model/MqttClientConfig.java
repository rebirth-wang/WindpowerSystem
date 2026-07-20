package com.fastbee.bridge.model;

import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.yomahub.liteflow.core.NodeComponent;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.text.StringSubstitutor;

import com.fastbee.bridge.domain.Bridge;

/** mqtt配置信息*/

@Data
@NoArgsConstructor
public class MqttClientConfig implements Config {

    private String key;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 连接地址
     */
    private String hostUrl;
    /**
     * 客户Id
     */
    private String clientId;
    /**
     * 默认连接话题
     */
    private String defaultTopic;
    /**
     * 超时时间
     */
    private int timeout = 30;
    /**
     * 保持连接数
     */
    private int keepalive = 60;

    // 0:default 0  3:MQTT_VERSION_3_1 3  4:MQTT_VERSION_3_1_1
    private int version = 0;

    private boolean automaticReconnect = true;
    /**是否清除session*/
    private boolean clearSession = true;;
    /**是否共享订阅*/
    private boolean isShared;
    /**分组共享订阅*/
    private boolean isSharedGroup;

    private String topic;

    private Long direction;

    private Bridge bridge;

    @Override
    public void load(String configJson) {
        MqttClient config = JSON.parseObject(configJson, MqttClient.class);
        if (config != null) {
            this.key = config.getHostUrl() + config.getClientId();
            this.hostUrl = config.getHostUrl();
            this.username = config.getUsername();
            this.password = config.getPassword();
            this.clientId = config.getClientId();
            this.keepalive = config.getKeepalive();
            this.timeout = config.getTimeout();
            this.version = config.getVersion();
            this.automaticReconnect = config.isAutomaticReconnect();
            this.clearSession = config.isClearSession();
        }
    }

    @Override
    public void reload(NodeComponent node, Map<String, Object> dataMap) {
        StringSubstitutor substitutor = new StringSubstitutor(dataMap);
        this.topic = substitutor.replace(this.topic);
    }
}

