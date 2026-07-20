package com.fastbee.ai.rag.model;

import lombok.Data;

/**
 * AI RAG 查询请求。
 */
@Data
public class AiRagQueryRequest {

    /**
     * 知识库 ID。
     */
    private Long knowledgeBaseId;

    /**
     * 用户问题。
     */
    private String queryText;

    /**
     * 业务场景。
     */
    private String businessType;

    /**
     * 返回条数。
     */
    private Integer topK;
}
