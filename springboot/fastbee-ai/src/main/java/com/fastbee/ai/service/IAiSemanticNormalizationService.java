package com.fastbee.ai.service;

import java.util.List;

import com.fastbee.ai.domain.AiKnowledgeBase;
import com.fastbee.ai.domain.AiKnowledgeVersion;
import com.fastbee.ai.model.vo.AiSemanticContextVO;
import com.fastbee.ai.model.vo.AiSemanticFieldVO;

/**
 * AI 语义归一服务。
 */
public interface IAiSemanticNormalizationService {

    /**
     * 构建 NL2SQL 提示词需要的语义上下文。
     *
     * @param question 用户问题
     * @return 语义上下文
     */
    AiSemanticContextVO buildNl2SqlContext(String question);

    /**
     * 按指定知识库版本解析语义字段。
     *
     * @param knowledgeBase 知识库
     * @param version       版本
     * @return 语义字段列表
     */
    List<AiSemanticFieldVO> listSemanticFieldsByVersion(AiKnowledgeBase knowledgeBase, AiKnowledgeVersion version);
}
