package com.fastbee.ai.model.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

/**
 * AI 对话发送请求。
 */
@Data
public class AiChatSendRequest {

    /**
     * 会话 ID。
     */
    private Long sessionId;

    /**
     * 用户消息。
     */
    @NotBlank(message = "消息内容不能为空")
    private String message;

    /**
     * 当前选择的模型编码。
     */
    private String modelCode;

    /**
     * 当前选择的厂商编码。
     */
    private String providerCode;

    /**
     * 当前会话模式。
     */
    private String chatMode;

    /**
     * 会话策略，取值 AUTO / PINNED。
     */
    private String modePolicy;

    /**
     * 会话锁定模式，仅当会话策略为 PINNED 时生效。
     */
    private String pinnedMode;

    /**
     * 本轮临时指定模式，仅当前一轮请求生效。
     */
    private String modeOverride;

    /**
     * 当前交互来源，用于观测“纠偏重试”等操作入口。
     */
    private String interactionSource;

    /**
     * 纠偏重试时引用的原始用户消息 ID。
     */
    private Long retrySourceMessageId;

    /**
     * 流式澄清恢复令牌。
     */
    private String resumeToken;

    /**
     * 当前澄清节点标识。
     */
    private String clarifyKey;

    /**
     * 用户选择的值。
     */
    private String selectedValue;

    /**
     * 用户选择的展示文本。
     */
    private String selectedLabel;

    /**
     * 恢复执行时使用的原始问题。
     */
    private String resumeQuestion;

    /**
     * 本轮上传附件名称，由后端抽取后写入，不要求前端直接传入。
     */
    private String attachmentFileName;

    /**
     * 本轮上传附件内容类型，由后端抽取后写入。
     */
    private String attachmentContentType;

    /**
     * 本轮上传附件正文摘录，由后端抽取后写入，仅用于模型上下文。
     */
    private String attachmentText;
}
