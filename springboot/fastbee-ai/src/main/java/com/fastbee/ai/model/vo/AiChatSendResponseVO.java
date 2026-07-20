package com.fastbee.ai.model.vo;

import lombok.Data;

/**
 * AI 对话响应。
 */
@Data
public class AiChatSendResponseVO {

    /**
     * 会话 ID。
     */
    private Long sessionId;

    /**
     * 会话编码。
     */
    private String sessionCode;

    /**
     * 用户原始问题。
     */
    private String question;

    /**
     * 模型回复内容。
     */
    private String answer;

    /**
     * 实际路由模型编码。
     */
    private String modelCode;

    /**
     * 实际路由厂商编码。
     */
    private String providerCode;

    /**
     * 当前会话模式。
     */
    private String chatMode;

    /**
     * 当前会话策略，取值 AUTO / PINNED。
     */
    private String modePolicy;

    /**
     * 当前会话锁定模式，仅当会话策略为 PINNED 时返回。
     */
    private String pinnedMode;

    /**
     * 当前会话最近实际能力快照。
     */
    private String lastEffectiveMode;

    /**
     * 实际执行模式。
     */
    private String effectiveChatMode;

    /**
     * 本轮执行技能。
     */
    private String executedSkill;

    /**
     * 技能原始结果。
     */
    private String toolResult;

    /**
     * 当前问题的 AI 路由审计结果。
     */
    private String routeAudit;
}
