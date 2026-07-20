package com.fastbee.ai.rag.model;

import lombok.Data;

/**
 * AI RAG 引用片段。
 */
@Data
public class AiRagReference {

    /**
     * 展示序号。
     */
    private Integer orderNum;

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
     * 来源编码。
     */
    private String sourceCode;

    /**
     * 来源名称。
     */
    private String sourceName;

    /**
     * 相关性分值。
     */
    private Double score;

    /**
     * 命中摘要。
     */
    private String contentPreview;

    /**
     * 片段正文。
     */
    private String content;
}
