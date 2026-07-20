package com.fastbee.ai.service;

import java.util.List;

import org.springframework.ai.chat.model.ChatModel;

import com.fastbee.ai.domain.AiChatMessage;
import com.fastbee.ai.model.vo.AiChatIntentRouteVO;
import com.fastbee.ai.model.vo.AiModelRouteVO;

/**
 * AI 会话路由技能服务。
 */
public interface IAiChatIntentRouteService {

    /**
     * 基于当前问题与最近会话上下文，分析本轮建议执行模式与关键槽位。
     *
     * @param question       用户问题
     * @param requestedMode  前端指定模式
     * @param route          当前模型路由
     * @param historyMessages 最近会话消息
     * @return 路由技能结构化结果
     */
    AiChatIntentRouteVO analyze(String question, String requestedMode, AiModelRouteVO route, List<AiChatMessage> historyMessages);

    /**
     * 基于已准备好的 ChatModel 分析本轮建议执行模式与关键槽位。
     *
     * @param question        用户问题
     * @param requestedMode   前端指定模式
     * @param chatModel       已准备好的运行时模型
     * @param historyMessages 最近会话消息
     * @return 路由技能结构化结果
     */
    AiChatIntentRouteVO analyze(String question, String requestedMode, ChatModel chatModel, List<AiChatMessage> historyMessages);
}
