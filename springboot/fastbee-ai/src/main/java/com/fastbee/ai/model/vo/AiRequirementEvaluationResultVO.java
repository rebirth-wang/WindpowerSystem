package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import lombok.Data;

/**
 * AI 需求评估结果。
 */
@Data
public class AiRequirementEvaluationResultVO {

    /**
     * 评估状态。
     */
    private String status;

    /**
     * 总体结论。
     */
    private String overallConclusion;

    /**
     * 匹配等级。
     */
    private String matchLevel;

    /**
     * 结果摘要。
     */
    private String summary;

    /**
     * 源文件名称。
     */
    private String sourceFileName;

    /**
     * 评估结果文件产物编码。
     */
    private String artifactCode;

    /**
     * 评估结果文件名称。
     */
    private String artifactName;

    /**
     * 评估结果文件大小。
     */
    private Long artifactSize;

    /**
     * 评估结果文件相对路径。
     */
    private String artifactRelativePath;

    /**
     * 评估结果文件类型。
     */
    private String artifactType;

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
     * 免责声明。
     */
    private String disclaimer;

    /**
     * 重点信息。
     */
    private List<String> keyPoints = new ArrayList<>();

    /**
     * 需求点匹配表。
     */
    private List<LinkedHashMap<String, Object>> requirementItems = new ArrayList<>();

    /**
     * 模块影响表。
     */
    private List<LinkedHashMap<String, Object>> moduleImpacts = new ArrayList<>();

    /**
     * 风险提示。
     */
    private List<String> risks = new ArrayList<>();

    /**
     * 待确认问题。
     */
    private List<String> pendingQuestions = new ArrayList<>();

    /**
     * 建议下一步。
     */
    private List<String> nextSteps = new ArrayList<>();

    /**
     * 参考来源。
     */
    private List<String> references = new ArrayList<>();
}
