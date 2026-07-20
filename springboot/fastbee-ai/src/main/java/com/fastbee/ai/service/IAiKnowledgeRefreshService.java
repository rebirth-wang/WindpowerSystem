package com.fastbee.ai.service;

import com.fastbee.ai.model.vo.AiKnowledgeRefreshResultVO;

/**
 * AI 知识更新服务接口。
 */
public interface IAiKnowledgeRefreshService {

    /**
     * 增量刷新单个知识文档。
     *
     * @param documentId 文档 ID
     * @return 刷新结果
     */
    AiKnowledgeRefreshResultVO refreshDocument(Long documentId);

    /**
     * 全量重建指定知识库。
     *
     * @param knowledgeBaseId 知识库 ID
     * @return 刷新结果
     */
    AiKnowledgeRefreshResultVO rebuildKnowledgeBase(Long knowledgeBaseId);
}
