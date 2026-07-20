package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * AI 多源问数执行计划。
 */
@Data
public class AiQueryPlanVO {

    /**
     * 原始问题。
     */
    private String question;

    /**
     * 执行模式。
     */
    private String queryMode;

    /**
     * 目标类型。
     */
    private String targetType;

    /**
     * 当前是否已具备直接执行能力。
     */
    private Boolean executableNow = Boolean.FALSE;

    /**
     * 是否需要先做实体/关系解析。
     */
    private Boolean requiresRelationResolution = Boolean.FALSE;

    /**
     * 是否命中运行时语义。
     */
    private Boolean runtimeSemanticMatched = Boolean.FALSE;

    /**
     * 是否走了回退语义。
     */
    private Boolean fallbackUsed = Boolean.FALSE;

    /**
     * 当前语义来源。
     */
    private String runtimeSource;

    /**
     * 主命中语义。
     */
    private String primarySemantic;

    /**
     * 计划摘要。
     */
    private String summary;

    /**
     * 规划原因。
     */
    private String reason;

    /**
     * 当前无法直接执行时的阻塞原因。
     */
    private String blockedReason;

    /**
     * 命中的静态语义数量。
     */
    private Integer matchedBindings = 0;

    /**
     * 命中的运行时语义数量。
     */
    private Integer runtimeBindings = 0;

    /**
     * 候选表。
     */
    private List<String> candidateTables = new ArrayList<>();

    /**
     * 命中的知识库编码。
     */
    private List<String> knowledgeBases = new ArrayList<>();

    /**
     * 命中的版本号。
     */
    private List<String> versionNos = new ArrayList<>();

    /**
     * 命中的静态语义名称。
     */
    private List<String> matchedSemantics = new ArrayList<>();

    /**
     * 命中的静态字段键。
     */
    private List<String> matchedFieldKeys = new ArrayList<>();

    /**
     * 命中的运行时语义名称。
     */
    private List<String> runtimeSemantics = new ArrayList<>();

    /**
     * 命中的运行时字段键。
     */
    private List<String> runtimeFieldKeys = new ArrayList<>();

    /**
     * 候选标识符。
     */
    private List<String> candidateIdentifiers = new ArrayList<>();

    /**
     * 参与的运行时提供者。
     */
    private List<String> runtimeProviders = new ArrayList<>();

    /**
     * 执行步骤。
     */
    private List<AiQueryPlanStepVO> steps = new ArrayList<>();
}
