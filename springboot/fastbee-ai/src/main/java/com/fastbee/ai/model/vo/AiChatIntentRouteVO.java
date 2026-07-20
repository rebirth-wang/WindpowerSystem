package com.fastbee.ai.model.vo;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Data;

/**
 * AI 会话路由技能结构化结果。
 */
@Data
public class AiChatIntentRouteVO {

    /**
     * 原始问题。
     */
    private String question;

    /**
     * 前端请求模式。
     */
    private String requestedMode;

    /**
     * AI 建议模式。
     */
    private String mode;

    /**
     * 模式置信度。
     */
    private Double modeConfidence;

    /**
     * 业务子类型。
     */
    private String businessType;

    /**
     * 主体类型。
     */
    private String subjectType;

    /**
     * 设备名称文本。
     */
    private String deviceNameText;

    /**
     * 设备编号文本。
     */
    private String serialNumberText;

    /**
     * 产品名称文本。
     */
    private String productNameText;

    /**
     * 物模型文本。
     */
    private String thingModelText;

    /**
     * 物模型类型提示。
     */
    private String thingModelTypeHint;

    /**
     * 控制动作文本。
     */
    private String actionText;

    /**
     * 控制动作归一值。
     */
    private String actionValue;

    /**
     * 时间意图。
     */
    private String timeIntent;

    /**
     * 聚合类型。
     */
    private String aggregateType;

    /**
     * 是否建议进入澄清。
     */
    private Boolean needClarify;

    /**
     * AI 判断原因。
     */
    private String reason;

    /**
     * 当前规则模式。
     */
    private String ruleMode;

    /**
     * 系统最终采用模式。
     */
    private String finalMode;

    /**
     * AI 建议模式是否与当前规则模式一致。
     */
    private Boolean matchedRuleMode;

    /**
     * 系统是否采用了 AI 建议模式。
     */
    private Boolean adoptedBySystem;

    /**
     * 回退到规则模式的原因。
     */
    private String fallbackReason;

    /**
     * 当前交互来源。
     */
    private String interactionSource;

    /**
     * 手动模式来源。
     */
    private String manualModeSource;

    /**
     * 纠偏重试引用的原始用户消息 ID。
     */
    private Long retrySourceMessageId;

    /**
     * 模型原始返回内容。
     */
    private String modelResponse;

    /**
     * 结构化负载。
     */
    private String structuredPayload;

    /**
     * 是否来自严格结构化输出。
     */
    private Boolean structuredOutput;

    /**
     * 解析状态。
     */
    private String parseStatus;

    /**
     * 解析错误码。
     */
    private String parseErrorCode;

    /**
     * 解析错误信息。
     */
    private String parseErrorMessage;

    /**
     * 本轮会话性能观测数据。
     */
    private Map<String, Object> performanceTrace = new LinkedHashMap<>();
}
