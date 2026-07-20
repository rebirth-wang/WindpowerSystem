package com.fastbee.ai.model.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

/**
 * AI 智能问数生成请求。
 */
@Data
public class AiNl2SqlGenerateRequest {

    /**
     * 自然语言问题。
     */
    @NotBlank(message = "问题内容不能为空")
    private String question;

    /**
     * 模型编码。
     */
    private String modelCode;

    /**
     * 厂商编码。
     */
    private String providerCode;

    /**
     * 行数限制。
     */
    private Integer rowLimit;
}
