package com.fastbee.ai.rag.model;

import java.util.Date;

import lombok.Data;

/**
 * AI 知识文档引用。
 */
@Data
public class AiKnowledgeDocumentReference {

    /**
     * 文档 ID。
     */
    private Long documentId;

    /**
     * 文档编码。
     */
    private String documentCode;

    /**
     * 知识库 ID。
     */
    private Long knowledgeBaseId;

    /**
     * 所属知识源编码。
     */
    private String sourceCode;

    /**
     * 所属知识源名称。
     */
    private String sourceName;

    /**
     * 文件名。
     */
    private String fileName;

    /**
     * 文件路径。
     */
    private String filePath;

    /**
     * 文件大小。
     */
    private Long fileSize;

    /**
     * 文件校验码。
     */
    private String checksum;

    /**
     * 解析状态。
     */
    private String parseStatus;

    /**
     * 分片数量。
     */
    private Integer chunkCount;

    /**
     * 解析快照路径。
     */
    private String parsedSnapshotPath;

    /**
     * 解析摘要。
     */
    private String parsedSummary;

    /**
     * 排序号。
     */
    private Integer sortNum;

    /**
     * 来源类型。
     */
    private String sourceOrigin;

    /**
     * 平台版本。
     */
    private String appVersion;

    /**
     * 发布状态。
     */
    private String publishStatus;

    /**
     * 当前是否启用。
     */
    private Boolean enabled;

    /**
     * 状态。
     */
    private String status;

    /**
     * 创建时间。
     */
    private Date createTime;

    /**
     * 更新时间。
     */
    private Date updateTime;
}
