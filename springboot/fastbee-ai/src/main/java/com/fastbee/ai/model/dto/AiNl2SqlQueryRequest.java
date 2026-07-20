package com.fastbee.ai.model.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

import com.fastbee.common.core.domain.PageEntity;

/**
 * AI 只读 SQL 查询请求。
 */
@Data
public class AiNl2SqlQueryRequest extends PageEntity {

    /**
     * 待执行的只读 SQL。
     */
    @NotBlank(message = "SQL 内容不能为空")
    private String sqlText;

    /**
     * 本次期望返回的最大行数。
     */
    private Integer rowLimit;
}
