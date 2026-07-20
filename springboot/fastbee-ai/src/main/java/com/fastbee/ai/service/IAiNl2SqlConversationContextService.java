package com.fastbee.ai.service;

import java.util.List;

import com.fastbee.ai.domain.AiChatMessage;
import com.fastbee.ai.model.vo.AiChatIntentRouteVO;
import com.fastbee.ai.model.vo.AiNl2SqlContextSnapshotVO;
import com.fastbee.ai.model.vo.AiNl2SqlGenerateResultVO;

/**
 * AI 智能问数会话私有上下文支撑服务。
 */
public interface IAiNl2SqlConversationContextService {

    /**
     * 从历史消息中提取最近一次可复用的智能问数上下文快照。
     *
     * @param historyMessages 历史消息
     * @return 上下文快照
     */
    AiNl2SqlContextSnapshotVO resolveLatestContext(List<AiChatMessage> historyMessages);

    /**
     * 基于会话历史补全当前智能问数执行问题中的缺失条件。
     *
     * @param question          当前执行问题
     * @param intentRouteResult 路由结果
     * @param historyMessages   历史消息
     * @return 补全后的执行问题
     */
    String enrichExecutionQuestion(String question,
                                   AiChatIntentRouteVO intentRouteResult,
                                   List<AiChatMessage> historyMessages);

    /**
     * 根据最近一次成功问数结果构建智能问数上下文快照。
     *
     * @param question       当前问题
     * @param generateResult 问数执行结果
     * @return 上下文快照
     */
    AiNl2SqlContextSnapshotVO buildContextSnapshot(String question,
                                                   AiNl2SqlGenerateResultVO generateResult);

    /**
     * 生成当前问数私有上下文摘要。
     *
     * @param historyMessages 历史消息
     * @return 摘要文本
     */
    String buildContextSummary(List<AiChatMessage> historyMessages);

    /**
     * 将智能问数上下文快照附加到当前用户消息的工具结果中。
     *
     * @param userMessage 用户消息
     * @param snapshot    上下文快照
     */
    void attachContextSnapshot(AiChatMessage userMessage, AiNl2SqlContextSnapshotVO snapshot);
}
