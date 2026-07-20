package com.fastbee.ai.service;

import com.fastbee.ai.model.vo.AiKnowledgeRuntimeStatusVO;
import com.fastbee.ai.model.vo.AiKnowledgeRuntimeSyncResultVO;

/**
 * AI 知识库运行时治理服务。
 */
public interface IAiKnowledgeRuntimeService {

    /**
     * 查询指定知识库的运行时状态。
     *
     * @param knowledgeBaseId 知识库 ID
     * @return 运行时状态
     */
    AiKnowledgeRuntimeStatusVO getRuntimeStatus(Long knowledgeBaseId);

    /**
     * 重建指定知识库的 NL2SQL 运行时语义包。
     *
     * @param knowledgeBaseId 知识库 ID
     * @return 重建结果
     */
    AiKnowledgeRuntimeSyncResultVO rebuildNl2SqlRuntime(Long knowledgeBaseId);
}
