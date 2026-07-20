package com.fastbee.ai.rag;

import java.util.List;

import com.fastbee.ai.rag.model.VectorSearchRequest;
import com.fastbee.ai.rag.model.VectorSearchResult;
import com.fastbee.ai.rag.model.VectorStoreDocument;

/**
 * 向量存储适配器。
 */
public interface VectorStoreAdapter {

    /**
     * 当前适配器类型。
     *
     * @return 存储类型
     */
    String getStoreType();

    /**
     * 写入或更新分段索引。
     *
     * @param documents 分段文档列表
     */
    void upsertDocuments(List<VectorStoreDocument> documents);

    /**
     * 执行向量检索。
     *
     * @param request 检索请求
     * @return 检索结果
     */
    List<VectorSearchResult> search(VectorSearchRequest request);

    /**
     * 删除指定文档索引。
     *
     * @param documentId 文档 ID
     */
    void deleteByDocumentId(Long documentId);
}
