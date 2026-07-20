package com.fastbee.bridge.ruleEngine;

import java.nio.charset.StandardCharsets;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import com.fastbee.common.extend.enums.ScriptEventEnum;
import com.fastbee.common.utils.spring.SpringUtils;
import com.fastbee.iot.enums.ScriptPurposeType;
import com.fastbee.iot.model.ScriptCondition;
import com.fastbee.iot.service.IScriptService;
import com.fastbee.rule.context.MsgContext;

@Slf4j
class MessageArrivedHandler implements IMqttMessageListener {

    private String topic;
    private String clientId;

    private static IScriptService scriptService = SpringUtils.getBean(IScriptService.class);

    public MessageArrivedHandler(String topic, String clientId) {
        this.topic = topic;
        this.clientId = clientId;
    }

    @Override
    public void messageArrived(String messageTopic, MqttMessage mqttMessage) throws Exception {
        log.info("Message arrived clientId " + clientId +" on topic '" + messageTopic + "': " + new String(mqttMessage.getPayload()));
        // 支持+，#通配符
        if (isTopicMatch(this.topic, messageTopic)) {
            // 处理特定主题的消息
            ScriptCondition scriptCondition = ScriptCondition.builder()
                    .scriptPurpose(ScriptPurposeType.DATA_STREAM.getCode())
                    // mqtt接入
                    .scriptEvent(ScriptEventEnum.MQTT_BRIDGE.getType())
                    .route(this.topic)
                    .clientId("\"clientId\": \"" + clientId + "\",")
                    .build();
            String payload = new String(mqttMessage.getPayload(), StandardCharsets.UTF_8);
            MsgContext context = MsgContext.builder()
                    .topic(messageTopic)
                    .payload(payload)
                    .build();
            //返回处理完的消息上下文
            scriptService.execRuleScript(scriptCondition, context);
        }
    }

    /**
     * 检查主题是否匹配，支持MQTT通配符 + 和 #
     *
     * @param subscriptionTopic 订阅主题（可能包含通配符）
     * @param messageTopic 消息主题
     * @return 是否匹配
     */
    private boolean isTopicMatch(String subscriptionTopic, String messageTopic) {
        // 如果订阅主题中不包含通配符，直接进行字符串比较
        if (!subscriptionTopic.contains("+") && !subscriptionTopic.contains("#")) {
            return subscriptionTopic.equals(messageTopic);
        }

        String[] subTokens = subscriptionTopic.split("/");
        String[] msgTokens = messageTopic.split("/");

        int subIndex = 0;
        int msgIndex = 0;

        while (subIndex < subTokens.length && msgIndex < msgTokens.length) {
            String subToken = subTokens[subIndex];
            String msgToken = msgTokens[msgIndex];

            // 处理多级通配符 #
            if ("#".equals(subToken)) {
                // # 必须是订阅主题的最后一个字符
                if (subIndex == subTokens.length - 1) {
                    return true; // 如果是最后一个，则匹配成功
                } else {
                    // # 只能在最后使用
                    return false;
                }
            }

            // 处理单级通配符 +
            if ("+".equals(subToken)) {
                // 单级通配符匹配任何单个层级，继续下一个
                subIndex++;
                msgIndex++;
                continue;
            }

            // 普通字符串比较
            if (!subToken.equals(msgToken)) {
                return false;
            }

            subIndex++;
            msgIndex++;
        }

        // 如果订阅主题已经遍历完，但消息主题还有剩余，则不匹配
        // 如果消息主题遍历完，但订阅主题还有剩余，则检查剩余的是否是 # 通配符
        if (subIndex == subTokens.length && msgIndex == msgTokens.length) {
            return true;
        } else if (subIndex < subTokens.length && msgIndex == msgTokens.length) {
            // 检查剩余的订阅层级是否为 #
            return subIndex == subTokens.length - 1 && "#".equals(subTokens[subIndex]);
        } else {
            return false;
        }
    }
}
