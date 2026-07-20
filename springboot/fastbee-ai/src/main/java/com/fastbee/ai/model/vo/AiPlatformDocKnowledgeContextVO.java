package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 平台文档知识上下文。
 */
@Data
public class AiPlatformDocKnowledgeContextVO {

    /**
     * 原始问题。
     */
    private String question;

    /**
     * 是否命中平台文档知识。
     */
    private Boolean matched = Boolean.FALSE;

    /**
     * 上下文来源。
     */
    private String runtimeSource;

    /**
     * 知识库 ID。
     */
    private Long knowledgeBaseId;

    /**
     * 知识库编码。
     */
    private String kbCode;

    /**
     * 知识库名称。
     */
    private String kbName;

    /**
     * 版本 ID。
     */
    private Long versionId;

    /**
     * 版本号。
     */
    private String versionNo;

    /**
     * 文档条目总数。
     */
    private Integer totalItems = 0;

    /**
     * 切块总数。
     */
    private Integer totalChunks = 0;

    /**
     * 命中切块数。
     */
    private Integer matchedChunks = 0;

    /**
     * 提示词行。
     */
    private List<String> promptLines = new ArrayList<>();

    /**
     * 命中的切块列表。
     */
    private List<AiPlatformDocKnowledgeChunkVO> chunks = new ArrayList<>();
}
