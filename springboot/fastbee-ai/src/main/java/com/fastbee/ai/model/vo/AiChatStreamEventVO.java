package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import lombok.Data;

/**
 * AI 会话流式事件。
 */
@Data
public class AiChatStreamEventVO {

    /**
     * 事件类型，例如 start、chunk、stage、done、error。
     */
    private String eventType;

    /**
     * 会话 ID。
     */
    private Long sessionId;

    /**
     * 会话编码。
     */
    private String sessionCode;

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
     * 实际路由供应商编码。
     */
    private String providerCode;

    /**
     * 实际路由模型编码。
     */
    private String modelCode;

    /**
     * 本轮执行技能。
     */
    private String executedSkill;

    /**
     * 本轮增量内容。
     */
    private String delta;

    /**
     * 当前完整内容。
     */
    private String content;

    /**
     * 工具执行原始结果。
     */
    private String toolResult;

    /**
     * 当前问题的 AI 路由审计结果。
     */
    private String routeAudit;

    /**
     * 问数执行模式。
     */
    private String queryMode;

    /**
     * 是否完成。
     */
    private Boolean done;

    /**
     * 错误编码。
     */
    private String errorCode;

    /**
     * 错误信息。
     */
    private String errorMessage;

    /**
     * 澄清类型，例如 DEVICE、THING_MODEL、SERVICE。
     */
    private String clarifyType;

    /**
     * 澄清节点标识。
     */
    private String clarifyKey;

    /**
     * 澄清标题。
     */
    private String clarifyTitle;

    /**
     * 澄清候选项。
     */
    private List<LinkedHashMap<String, Object>> clarifyOptions = new ArrayList<>();

    /**
     * 恢复执行令牌。
     */
    private String resumeToken;

    /**
     * 继续执行时需要沿用的原始问题。
     */
    private String resumeQuestion;
}
