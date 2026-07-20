package com.fastbee.ai.service;

import java.util.List;

import com.fastbee.ai.domain.AiChatMessage;
import com.fastbee.ai.model.vo.AiPlatformAssistantContextSnapshotVO;
import com.fastbee.ai.model.vo.AiPlatformDocKnowledgeContextVO;

/**
 * AI 平台助手会话私有上下文支撑服务。
 */
public interface IAiPlatformAssistantConversationContextService {

    /**
     * 从历史消息中提取最近一次可复用的平台助手上下文快照。
     *
     * @param historyMessages 历史消息
     * @return 上下文快照
     */
    AiPlatformAssistantContextSnapshotVO resolveLatestContext(List<AiChatMessage> historyMessages);

    /**
     * 基于会话历史补全当前平台助手执行问题中的缺失线索。
     *
     * @param question        当前执行问题
     * @param historyMessages 历史消息
     * @return 补全后的执行问题
     */
    String enrichExecutionQuestion(String question, List<AiChatMessage> historyMessages);

    /**
     * 根据本次平台知识命中结果构建平台助手上下文快照。
     *
     * @param question 当前问题
     * @param context  平台知识上下文
     * @return 上下文快照
     */
    AiPlatformAssistantContextSnapshotVO buildContextSnapshot(String question,
                                                              AiPlatformDocKnowledgeContextVO context);

    /**
     * 生成当前平台助手私有上下文摘要。
     *
     * @param historyMessages 历史消息
     * @return 摘要文本
     */
    String buildContextSummary(List<AiChatMessage> historyMessages);

    /**
     * 将平台助手上下文快照附加到当前用户消息工具结果中。
     *
     * @param userMessage 用户消息
     * @param snapshot    上下文快照
     */
    void attachContextSnapshot(AiChatMessage userMessage, AiPlatformAssistantContextSnapshotVO snapshot);
}
