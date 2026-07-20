package com.fastbee.ai.model.vo;

import lombok.Data;

/**
 * AI 澄清候选项。
 */
@Data
public class AiClarifyOptionVO {

    /**
     * 展示标题。
     */
    private String label;

    /**
     * 实际提交值。
     */
    private String value;

    /**
     * 候选描述。
     */
    private String description;

    /**
     * 候选类型。
     */
    private String type;

    /**
     * 可选分数。
     */
    private Integer score;
}
