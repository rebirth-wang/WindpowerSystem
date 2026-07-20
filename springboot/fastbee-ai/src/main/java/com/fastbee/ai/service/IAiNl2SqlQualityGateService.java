package com.fastbee.ai.service;

import com.fastbee.ai.domain.AiKnowledgeBase;
import com.fastbee.ai.domain.AiKnowledgeVersion;
import com.fastbee.ai.model.vo.AiKnowledgeVersionQualityCheckVO;

/**
 * NL2SQL 发布质量门禁服务。
 */
public interface IAiNl2SqlQualityGateService {

    /**
     * 发布前执行版本质量预检。
     *
     * @param knowledgeBase 知识库
     * @param version       待检查版本
     * @return 预检结果
     */
    AiKnowledgeVersionQualityCheckVO checkBeforePublish(AiKnowledgeBase knowledgeBase, AiKnowledgeVersion version);

    /**
     * 发布前校验问数语义版本质量。
     *
     * @param knowledgeBase 知识库
     * @param version       待发布版本
     */
    void validateBeforePublish(AiKnowledgeBase knowledgeBase, AiKnowledgeVersion version);
}
