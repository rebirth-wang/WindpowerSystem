package com.fastbee.rule.context;

import java.util.concurrent.ConcurrentHashMap;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.yomahub.liteflow.log.LFLoggerManager;
import lombok.*;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastbee.mqttclient.PubMqttClient;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MsgContext extends BaseContext {
    private static Logger logger = LoggerFactory.getLogger("script");
    /**
     * 协议编码
     */
    private String protocolCode;

    /**
     * 消息主题
     */
    private String topic;

    /**
     * 消息内容
     */
    private String payload;

    public void printlog(String var1, Object... var2) {
        String requestId = "["+ LFLoggerManager.getRequestId()+"]:";
        logger.info(StrUtil.isBlank(requestId) ? var1 : requestId + var1, var2);
    }

    public void sendMqttMsg(String topic, String payload) {
        printlog("send mqtt msg, topic: {}, payload: {}", topic, payload);
        PubMqttClient mqttClient = SpringUtil.getBean(PubMqttClient.class);
        mqttClient.publish(0, false, topic, payload);
    }

    @Override
    public String placeholders(String str) {
        ConcurrentHashMap<String, Object> substitutorMap = new ConcurrentHashMap<>(this.dataMap);
        if (this.payload != null) {
            substitutorMap.put("payload", this.payload);
        }
        if (this.topic != null) {
            substitutorMap.put("topic", this.topic);
        }
        if (this.protocolCode != null) {
            substitutorMap.put("protocolCode", this.protocolCode);
        }
        if (this.getSerialNumber() != null) {
            substitutorMap.put("serialNumber", this.getSerialNumber());
        }
        if (this.getProductId() != null) {
            substitutorMap.put("productId", this.getProductId());
        }
        if (this.getRuleId() != null) {
            substitutorMap.put("ruleId", this.getRuleId());
        }
        if (this.getTriggerType() != null) {
            substitutorMap.put("triggerType", this.getTriggerType());
        }
        StringSubstitutor substitutor = new StringSubstitutor(substitutorMap);
        return substitutor.replace(str);
    }
}
