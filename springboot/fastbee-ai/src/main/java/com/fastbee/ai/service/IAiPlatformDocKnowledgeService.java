package com.fastbee.ai.service;

import com.fastbee.ai.model.vo.AiPlatformDocKnowledgeContextVO;

/**
 * 平台文档知识上下文服务。
 */
public interface IAiPlatformDocKnowledgeService {

    /**
     * 构建平台文档知识上下文。
     *
     * @param question 用户问题
     * @return 平台文档知识上下文
     */
    AiPlatformDocKnowledgeContextVO buildPlatformContext(String question);
}
