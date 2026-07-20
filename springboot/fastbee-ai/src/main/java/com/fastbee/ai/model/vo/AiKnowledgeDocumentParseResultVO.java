package com.fastbee.ai.model.vo;

import java.util.List;

import lombok.Data;

/**
 * 知识文档解析结果。
 */
@Data
public class AiKnowledgeDocumentParseResultVO {

    /**
     * 知识库 ID。
     */
    private Long knowledgeBaseId;

    /**
     * 文档 ID。
     */
    private Long documentId;

    /**
     * 知识库类型。
     */
    private String kbType;

    /**
     * 模板编码。
     */
    private String templateCode;

    /**
     * 模板版本。
     */
    private String templateVersion;

    /**
     * 解析器类型。
     */
    private String parserType;

    /**
     * 快照编码。
     */
    private String snapshotCode;

    /**
     * 解析行数。
     */
    private Integer rowCount;

    /**
     * 解析状态。
     */
    private String parseStatus;

    /**
     * 解析摘要。
     */
    private String parsedSummary;

    /**
     * 快照路径。
     */
    private String snapshotPath;

    /**
     * 校验错误数量。
     */
    private Integer validationErrorCount;

    /**
     * 校验告警数量。
     */
    private Integer validationWarningCount;

    /**
     * 校验明细。
     */
    private List<AiTemplateValidationIssueVO> validationIssues;

    /**
     * 结果说明。
     */
    private String message;
}
