package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * AI 知识库运行时状态。
 */
@Data
public class AiKnowledgeRuntimeStatusVO {

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
     * 知识库类型。
     */
    private String kbType;

    /**
     * 运行态场景编码。
     */
    private String runtimeScene;

    /**
     * 运行态对象名称。
     */
    private String runtimeTargetName;

    /**
     * 知识库状态。
     */
    private String knowledgeStatus;

    /**
     * 知识库发布状态。
     */
    private String publishStatus;

    /**
     * 已发布版本 ID。
     */
    private Long activeVersionId;

    /**
     * 已发布版本号。
     */
    private String activeVersionNo;

    /**
     * 预期运行时存储类型。
     */
    private String expectedStoreType;

    /**
     * 问数读取侧运行时存储类型。
     */
    private String readerStoreType;

    /**
     * 读取侧与发布侧运行时存储是否一致。
     */
    private Boolean readerStoreMatched;

    /**
     * 当前读取侧运行时实现是否存在。
     */
    private Boolean runtimeStoreImplemented;

    /**
     * 运行时是否已加载 active bundle。
     */
    private Boolean runtimeLoaded;

    /**
     * 运行时 active bundle 版本 ID。
     */
    private Long runtimeVersionId;

    /**
     * 运行时 active bundle 版本号。
     */
    private String runtimeVersionNo;

    /**
     * 运行时 active bundle 存储类型。
     */
    private String runtimeStoreType;

    /**
     * 预期字段数量。
     */
    private Integer expectedFieldCount;

    /**
     * 运行时字段数量。
     */
    private Integer runtimeFieldCount;

    /**
     * 运行时发布时间。
     */
    private Date runtimePublishTime;

    /**
     * 运行时发布人。
     */
    private String runtimePublishedBy;

    /**
     * 是否需要校验发布侧/读取侧存储一致性。
     */
    private Boolean storeCheckRequired = Boolean.FALSE;

    /**
     * 是否支持重建运行态。
     */
    private Boolean rebuildSupported = Boolean.FALSE;

    /**
     * 参与构建的源文件数。
     */
    private Integer sourceFileCount;

    /**
     * 预期条目数。
     */
    private Integer expectedItemCount;

    /**
     * 当前运行态条目数。
     */
    private Integer runtimeItemCount;

    /**
     * 构建摘要。
     */
    private String buildSummary;

    /**
     * 当前状态是否一致。
     */
    private Boolean consistent;

    /**
     * 状态摘要。
     */
    private String summary;

    /**
     * 不一致问题明细。
     */
    private List<String> issues = new ArrayList<>();
}
