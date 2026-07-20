package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * AI 智能问数结构化生成结果。
 */
@Data
public class AiNl2SqlStructuredResultVO {

    /**
     * 原始问题。
     */
    private String question;

    /**
     * 结构化 SQL。
     */
    private String sql;

    /**
     * 中文摘要。
     */
    private String summary;

    /**
     * 置信度。
     */
    private Double confidence;

    /**
     * 涉及的数据表。
     */
    private List<String> tables = new ArrayList<>();

    /**
     * 模型原始输出。
     */
    private String modelResponse;

    /**
     * 结构化输出 JSON。
     */
    private String structuredPayload;

    /**
     * 结构化解析状态。
     */
    private String parseStatus;

    /**
     * 结构化解析错误码。
     */
    private String parseErrorCode;

    /**
     * 结构化解析错误说明。
     */
    private String parseErrorMessage;

    /**
     * 是否来自严格结构化输出。
     */
    private Boolean structuredOutput;
}
