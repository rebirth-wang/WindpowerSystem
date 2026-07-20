package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 源码导航知识条目。
 */
@Data
public class AiCodebaseGuideItemVO {

    /**
     * 安全相对路径。
     */
    private String sourcePath;

    /**
     * 模块名称。
     */
    private String moduleName;

    /**
     * 条目类型。
     */
    private String symbolType;

    /**
     * 分层定位。
     */
    private String layer;

    /**
     * 包名。
     */
    private String packageName;

    /**
     * 类名或组件名。
     */
    private String className;

    /**
     * 方法名。
     */
    private String methodName;

    /**
     * 符号名称。
     */
    private String symbolName;

    /**
     * 方法签名摘要，不包含方法体。
     */
    private String signature;

    /**
     * HTTP 方法。
     */
    private String httpMethod;

    /**
     * 接口路径。
     */
    private String endpointPath;

    /**
     * 行号定位。
     */
    private Integer lineStart;

    /**
     * 职责摘要。
     */
    private String summary;

    /**
     * 二开建议。
     */
    private String devHint;

    /**
     * 标签。
     */
    private List<String> tags = new ArrayList<>();

    /**
     * 别名。
     */
    private List<String> aliases = new ArrayList<>();

    /**
     * 检索内容摘要，不包含真实源码。
     */
    private String content;

    /**
     * 匹配分值。
     */
    private Integer matchScore = 0;
}
