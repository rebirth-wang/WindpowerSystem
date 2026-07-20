package com.fastbee.ai.rag.model;

import lombok.Data;

/**
 * AI 知识分段。
 */
@Data
public class AiKnowledgeSegment {

    /**
     * 分段编码。
     */
    private String segmentCode;

    /**
     * 知识库 ID。
     */
    private Long knowledgeBaseId;

    /**
     * 文档 ID。
     */
    private Long documentId;

    /**
     * 分段序号。
     */
    private Integer chunkIndex;

    /**
     * 分段标题。
     */
    private String segmentTitle;

    /**
     * 分段内容。
     */
    private String content;

    /**
     * 内容长度。
     */
    private Integer contentLength;

    /**
     * 所属知识源编码。
     */
    private String sourceCode;

    /**
     * 所属知识源名称。
     */
    private String sourceName;
}
