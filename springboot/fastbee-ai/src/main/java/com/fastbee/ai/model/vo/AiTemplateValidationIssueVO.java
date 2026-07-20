package com.fastbee.ai.model.vo;

import lombok.Data;

/**
 * 知识库模板校验问题。
 */
@Data
public class AiTemplateValidationIssueVO {

    /**
     * Excel 行号，从 2 开始表示第一条数据行。
     */
    private Integer rowNum;

    /**
     * 问题级别：ERROR / WARNING。
     */
    private String level;

    /**
     * 字段名。
     */
    private String fieldName;

    /**
     * 问题编码。
     */
    private String issueCode;

    /**
     * 当前值。
     */
    private String currentValue;

    /**
     * 问题说明。
     */
    private String message;
}
