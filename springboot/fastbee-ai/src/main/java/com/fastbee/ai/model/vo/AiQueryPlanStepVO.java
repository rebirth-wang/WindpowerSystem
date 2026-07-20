package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * AI 问数执行计划步骤。
 */
@Data
public class AiQueryPlanStepVO {

    /**
     * 步骤序号。
     */
    private Integer stepNo;

    /**
     * 步骤类型。
     */
    private String stepType;

    /**
     * 推荐执行器类型。
     */
    private String executorType;

    /**
     * 步骤描述。
     */
    private String description;

    /**
     * 当前步骤目标。
     */
    private String target;

    /**
     * 是否必须执行。
     */
    private Boolean required = Boolean.TRUE;

    /**
     * 输入提示。
     */
    private List<String> inputs = new ArrayList<>();

    /**
     * 输出提示。
     */
    private List<String> outputs = new ArrayList<>();
}
