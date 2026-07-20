package com.fastbee.ai.service;

import java.util.List;

import com.fastbee.ai.domain.AiChatMessage;
import com.fastbee.ai.model.vo.AiChatIntentRouteVO;
import com.fastbee.ai.model.vo.AiConversationContextBundleVO;

/**
 * AI 会话执行上下文装配器。
 */
public interface IAiConversationContextAssembler {

    /**
     * 为当前轮执行装配上下文。
     *
     * @param question          当前问题
     * @param effectiveMode     当前执行模式
     * @param intentRouteResult 路由结果
     * @param historyMessages   历史消息
     * @return 装配后的上下文
     */
    AiConversationContextBundleVO buildExecutionContext(String question,
                                                        String effectiveMode,
                                                        AiChatIntentRouteVO intentRouteResult,
                                                        List<AiChatMessage> historyMessages);
}
