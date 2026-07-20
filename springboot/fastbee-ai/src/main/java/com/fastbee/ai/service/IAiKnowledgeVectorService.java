package com.fastbee.ai.service;

import java.util.List;

import com.fastbee.ai.rag.model.VectorSearchResult;

/**
 * AI 知识向量检索服务接口。
 */
public interface IAiKnowledgeVectorService {

    /**
     * 为指定文档建立索引。
     *
     * @param documentId 文档 ID
     * @return 索引分段数
     */
    int indexDocument(Long documentId);

    /**
     * 删除指定文档索引。
     *
     * @param documentId 文档 ID
     */
    void removeDocumentIndex(Long documentId);

    /**
     * 执行知识检索。
     *
     * @param knowledgeBaseId 知识库 ID
     * @param queryText 查询内容
     * @param topK 返回条数
     * @return 检索结果
     */
    List<VectorSearchResult> search(Long knowledgeBaseId, String queryText, Integer topK);
}
