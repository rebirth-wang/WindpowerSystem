package com.fastbee.ai.rag.model;

import lombok.Data;

/**
 * 向量存储文档对象。
 */
@Data
public class VectorStoreDocument {

    /**
     * 向量索引 ID。
     */
    private String vectorId;

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
     * 原始内容。
     */
    private String content;

    /**
     * 所属知识源编码。
     */
    private String sourceCode;

    /**
     * 所属知识源名称。
     */
    private String sourceName;
}
