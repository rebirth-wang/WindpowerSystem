package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * AI 运行时语义包。
 */
@Data
public class AiSemanticRuntimeBundleVO {

    /**
     * 租户 ID。
     */
    private Long tenantId;

    /**
     * 知识库 ID。
     */
    private Long knowledgeBaseId;

    /**
     * 知识库编码。
     */
    private String kbCode;

    /**
     * 知识库类型。
     */
    private String kbType;

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
    private String vectorStoreType;

    /**
     * 发布时间。
     */
    private Date publishTime;

    /**
     * 发布人。
     */
    private String publishedBy;

    /**
     * 语义字段数量。
     */
    private Integer fieldCount = 0;

    /**
     * 语义字段列表。
     */
    private List<AiSemanticFieldVO> fields = new ArrayList<>();
}
