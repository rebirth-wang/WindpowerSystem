package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 平台文档运行时切块条目。
 */
@Data
public class AiPlatformDocKnowledgeChunkVO {

    /**
     * 章节名称。
     */
    private String sectionName;

    /**
     * 一级栏目。
     */
    private String sectionLevel1;

    /**
     * 栏目路径。
     */
    private String sectionPath;

    /**
     * 页面标题。
     */
    private String pageTitle;

    /**
     * 标题。
     */
    private String title;

    /**
     * 标题路径。
     */
    private String headingPath;

    /**
     * 知识类型。
     */
    private String knowledgeType;

    /**
     * 菜单路径。
     */
    private String menuPath;

    /**
     * 适用角色。
     */
    private String targetRole;

    /**
     * 前置条件。
     */
    private String preconditions;

    /**
     * 操作步骤。
     */
    private String actionSteps;

    /**
     * 结果说明。
     */
    private String resultDesc;

    /**
     * 注意事项。
     */
    private String cautions;

    /**
     * 切块编码。
     */
    private String chunkCode;

    /**
     * 切块序号。
     */
    private Integer chunkIndex = 0;

    /**
     * 标签列表。
     */
    private List<String> tags = new ArrayList<>();

    /**
     * 同义词列表。
     */
    private List<String> aliases = new ArrayList<>();

    /**
     * 内容摘要。
     */
    private String contentPreview;

    /**
     * 切块全文。
     */
    private String content;

    /**
     * 备注。
     */
    private String remark;

    /**
     * 关联文档。
     */
    private String relatedDocs;

    /**
     * 来源文件。
     */
    private String sourceFile;

    /**
     * 来源链接。
     */
    private String sourceUrl;

    /**
     * 来源类型。
     */
    private String sourceType;

    /**
     * 匹配分值。
     */
    private Integer matchScore = 0;
}
