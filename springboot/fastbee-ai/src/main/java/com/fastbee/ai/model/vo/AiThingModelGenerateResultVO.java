package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import lombok.Data;

/**
 * AI 物模型生成结果。
 */
@Data
public class AiThingModelGenerateResultVO {

    /**
     * 生成状态。
     */
    private String status;

    /**
     * 结果摘要。
     */
    private String summary;

    /**
     * 源文件名称。
     */
    private String sourceFileName;

    /**
     * 生成物模型行数。
     */
    private Integer rowCount;

    /**
     * 下载产物编码。
     */
    private String artifactCode;

    /**
     * 下载文件名称。
     */
    private String artifactName;

    /**
     * 产物大小。
     */
    private Long artifactSize;

    /**
     * 产物相对存储路径，仅用于审计，不给前端直接拼接文件路径。
     */
    private String artifactRelativePath;

    /**
     * 模型置信度。
     */
    private Double confidence;

    /**
     * 模型编码。
     */
    private String modelCode;

    /**
     * 厂商编码。
     */
    private String providerCode;

    /**
     * 重点信息。
     */
    private List<String> keyPoints = new ArrayList<>();

    /**
     * 质量问题。
     */
    private List<String> qualityIssues = new ArrayList<>();

    /**
     * 建议补充的信息。
     */
    private List<String> missingInformation = new ArrayList<>();

    /**
     * 预览行。
     */
    private List<LinkedHashMap<String, Object>> previewRows = new ArrayList<>();
}
