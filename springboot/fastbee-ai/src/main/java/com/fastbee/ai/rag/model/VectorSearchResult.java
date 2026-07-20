package com.fastbee.ai.rag.model;

import lombok.Data;

/**
 * 向量检索结果。
 */
@Data
public class VectorSearchResult {

    /**
     * 知识库 ID。
     */
    private Long knowledgeBaseId;

    /**
     * 文档 ID。
     */
    private Long documentId;

    /**
     * 分段编码。
     */
    private String segmentCode;

    /**
     * 分段标题。
     */
    private String segmentTitle;

    /**
     * 相似度分值。
     */
    private Double score;

    /**
     * 所属知识源编码。
     */
    private String sourceCode;

    /**
     * 所属知识源名称。
     */
    private String sourceName;

    /**
     * 命中内容摘要。
     */
    private String contentPreview;

    /**
     * 命中原文。
     */
    private String content;
}
