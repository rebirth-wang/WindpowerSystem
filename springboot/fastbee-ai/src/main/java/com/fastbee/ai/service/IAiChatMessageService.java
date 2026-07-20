package com.fastbee.ai.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.ai.domain.AiChatMessage;

/**
 * AI 会话消息服务接口。
 */
public interface IAiChatMessageService extends IService<AiChatMessage> {

    /**
     * 查询会话消息列表。
     *
     * @param aiChatMessage 查询条件
     * @return 消息列表
     */
    List<AiChatMessage> listAiChatMessage(AiChatMessage aiChatMessage);

    /**
     * 统计会话当前消息数量。
     *
     * @param sessionId 会话 ID
     * @return 消息数
     */
    int countBySessionId(Long sessionId);

    /**
     * 删除指定会话下的全部消息。
     *
     * @param sessionId 会话 ID
     * @return 影响行数
     */
    int deleteBySessionId(Long sessionId);
}
