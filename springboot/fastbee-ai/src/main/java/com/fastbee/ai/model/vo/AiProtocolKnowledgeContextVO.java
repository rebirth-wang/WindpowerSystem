package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 协议知识上下文。
 */
@Data
public class AiProtocolKnowledgeContextVO {

    /**
     * 原始问题。
     */
    private String question;

    /**
     * 是否命中协议知识。
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
     * 模块总数。
     */
    private Integer totalModules = 0;

    /**
     * 条目总数。
     */
    private Integer totalItems = 0;

    /**
     * 命中条目数。
     */
    private Integer matchedItems = 0;

    /**
     * 模块名称列表。
     */
    private List<String> modules = new ArrayList<>();

    /**
     * 提示词行。
     */
    private List<String> promptLines = new ArrayList<>();

    /**
     * 协议知识条目。
     */
    private List<AiProtocolKnowledgeItemVO> items = new ArrayList<>();
}
