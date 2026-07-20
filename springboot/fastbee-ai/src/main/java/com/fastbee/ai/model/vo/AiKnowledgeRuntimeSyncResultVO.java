package com.fastbee.ai.model.vo;

import java.util.Date;

import lombok.Data;

/**
 * AI 知识库运行时重建结果。
 */
@Data
public class AiKnowledgeRuntimeSyncResultVO {

    /**
     * 是否成功。
     */
    private Boolean success;

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
     * 运行时存储类型。
     */
    private String storeType;

    /**
     * 已重建字段数量。
     */
    private Integer fieldCount;

    /**
     * 结果摘要。
     */
    private String message;

    /**
     * 开始时间。
     */
    private Date startedTime;

    /**
     * 完成时间。
     */
    private Date finishedTime;
}
