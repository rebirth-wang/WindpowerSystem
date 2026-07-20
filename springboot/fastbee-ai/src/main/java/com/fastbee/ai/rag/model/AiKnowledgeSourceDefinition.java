package com.fastbee.ai.rag.model;

import lombok.Data;

/**
 * AI 知识源定义。
 */
@Data
public class AiKnowledgeSourceDefinition {

    /**
     * 知识库 ID。
     */
    private Long knowledgeBaseId;

    /**
     * 知识源编码。
     */
    private String sourceCode;

    /**
     * 知识源名称。
     */
    private String sourceName;

    /**
     * 知识源类型。
     */
    private String sourceType;

    /**
     * 向量库类型。
     */
    private String vectorStoreType;

    /**
     * 发布状态。
     */
    private String publishStatus;

    /**
     * 当前激活版本ID。
     */
    private Long activeVersionId;

    /**
     * 文档数量。
     */
    private Integer documentCount;

    /**
     * 当前是否启用。
     */
    private Boolean enabled;

    /**
     * 状态。
     */
    private String status;
}
