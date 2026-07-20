package com.fastbee.ai.rag.model;

import lombok.Data;

/**
 * 向量检索请求。
 */
@Data
public class VectorSearchRequest {

    /**
     * 知识库 ID。
     */
    private Long knowledgeBaseId;

    /**
     * 查询文本。
     */
    private String queryText;

    /**
     * 返回条数。
     */
    private Integer topK;

    /**
     * 目标向量存储类型。
     */
    private String storeType;
}
