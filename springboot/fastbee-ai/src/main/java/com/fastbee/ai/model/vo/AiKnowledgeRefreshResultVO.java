package com.fastbee.ai.model.vo;

import java.util.Date;

import lombok.Data;

/**
 * AI 知识更新结果。
 */
@Data
public class AiKnowledgeRefreshResultVO {

    /**
     * 目标类型。
     */
    private String targetType;

    /**
     * 目标 ID。
     */
    private Long targetId;

    /**
     * 是否成功。
     */
    private Boolean success;

    /**
     * 已刷新文档数。
     */
    private Integer indexedDocumentCount;

    /**
     * 刷新失败文档数。
     */
    private Integer failedDocumentCount;

    /**
     * 已刷新分段数。
     */
    private Integer indexedSegmentCount;

    /**
     * 结果说明。
     */
    private String message;

    /**
     * 失败原因。
     */
    private String failedReason;

    /**
     * 开始时间。
     */
    private Date startedTime;

    /**
     * 完成时间。
     */
    private Date finishedTime;
}
