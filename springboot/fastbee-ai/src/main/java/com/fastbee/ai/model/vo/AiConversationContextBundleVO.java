package com.fastbee.ai.model.vo;

import lombok.Data;

/**
 * AI 会话执行上下文装配结果。
 */
@Data
public class AiConversationContextBundleVO {

    /**
     * 当前轮最终执行问题。
     */
    private String executionQuestion;

    /**
     * 当前轮全局上下文。
     */
    private AiConversationGlobalContextVO globalContext = new AiConversationGlobalContextVO();

    /**
     * 当前技能私有上下文摘要。
     */
    private String skillContextSummary;
}
