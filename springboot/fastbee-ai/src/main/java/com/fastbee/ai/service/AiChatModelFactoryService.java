package com.fastbee.ai.service;

import org.springframework.ai.chat.model.ChatModel;

import com.fastbee.ai.model.runtime.AiRuntimeModelSnapshot;
import com.fastbee.ai.model.vo.AiModelRouteVO;

/**
 * AI 运行时模型工厂服务。
 */
public interface AiChatModelFactoryService {

    /**
     * 根据运行时快照解析可调用的 ChatModel。
     *
     * @param snapshot 运行时快照
     * @return 运行时模型
     */
    ChatModel resolveChatModel(AiRuntimeModelSnapshot snapshot);

    /**
     * 按当前路由解析可调用的 ChatModel。
     *
     * @param route 路由结果
     * @return 运行时模型
     */
    ChatModel resolveChatModel(AiModelRouteVO route);
}
