package com.fastbee.ai.model.vo;

import lombok.Data;

/**
 * AI 平台助手会话私有上下文快照。
 */
@Data
public class AiPlatformAssistantContextSnapshotVO {

    /**
     * 最近一次平台助手问题。
     */
    private String question;

    /**
     * 最近一次命中的知识库编码。
     */
    private String kbCode;

    /**
     * 最近一次命中的知识库名称。
     */
    private String kbName;

    /**
     * 最近一次命中的版本号。
     */
    private String versionNo;

    /**
     * 最近一次命中的菜单路径。
     */
    private String menuPath;

    /**
     * 最近一次命中的页面标题。
     */
    private String pageTitle;

    /**
     * 最近一次命中的标题路径。
     */
    private String headingPath;

    /**
     * 最近一次命中的章节名称。
     */
    private String sectionName;

    /**
     * 最近一次命中的知识类型。
     */
    private String knowledgeType;

    /**
     * 最近一次命中的适用角色。
     */
    private String targetRole;

    /**
     * 最近一次命中的来源链接。
     */
    private String sourceUrl;

    /**
     * 最近一次命中的内容摘要。
     */
    private String contentPreview;
}
