package com.fastbee.ai.model.vo;

import lombok.Data;

/**
 * AI 语义值映射。
 */
@Data
public class AiSemanticValueMappingVO {

    /**
     * 展示标签。
     */
    private String label;

    /**
     * 实际值。
     */
    private String value;
}
