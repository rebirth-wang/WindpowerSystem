package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * AI 问数语义上下文。
 */
@Data
public class AiSemanticContextVO {

    /**
     * 原始问题。
     */
    private String question;

    /**
     * 是否命中有效语义上下文。
     */
    private Boolean matched = Boolean.FALSE;

    /**
     * 是否使用回退语义。
     */
    private Boolean fallbackUsed = Boolean.FALSE;

    /**
     * 当前语义上下文来源。
     */
    private String runtimeSource;

    /**
     * 语义总数。
     */
    private Integer totalBindings = 0;

    /**
     * 命中的语义数。
     */
    private Integer matchedBindings = 0;

    /**
     * 提示词行。
     */
    private List<String> promptLines = new ArrayList<>();

    /**
     * 命中的语义字段。
     */
    private List<AiSemanticFieldVO> fields = new ArrayList<>();

    /**
     * 运行时补充语义字段。
     * <p>当前用于承载物模型等动态语义，先进入上下文预览层，
     * 暂不直接作为 NL2SQL prompt 主输入，避免误导模型直接拼接 SQL。</p>
     */
    private List<AiSemanticFieldVO> runtimeFields = new ArrayList<>();

    /**
     * 运行时补充语义数量。
     */
    private Integer runtimeBindings = 0;

    /**
     * 参与本次补充的运行时提供者。
     */
    private List<String> runtimeProviders = new ArrayList<>();

    /**
     * 参与的知识库编码。
     */
    private List<String> knowledgeBases = new ArrayList<>();

    /**
     * 命中的业务对象名称。
     */
    private List<String> businessObjects = new ArrayList<>();

    /**
     * 命中的主事实表。
     */
    private List<String> primaryTables = new ArrayList<>();

    /**
     * 命中的指标口径。
     */
    private List<String> metricRules = new ArrayList<>();

    /**
     * 命中的澄清规则。
     */
    private List<String> clarifyRules = new ArrayList<>();

    /**
     * 参与的版本号。
     */
    private List<String> versionNos = new ArrayList<>();
}
