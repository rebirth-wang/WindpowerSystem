package com.fastbee.ai.service;

import com.fastbee.ai.model.vo.AiProtocolKnowledgeContextVO;

/**
 * 协议知识上下文服务。
 */
public interface IAiProtocolKnowledgeService {

    /**
     * 根据问题构建协议知识上下文。
     *
     * @param question 用户问题
     * @return 协议知识上下文
     */
    AiProtocolKnowledgeContextVO buildProtocolContext(String question);
}
