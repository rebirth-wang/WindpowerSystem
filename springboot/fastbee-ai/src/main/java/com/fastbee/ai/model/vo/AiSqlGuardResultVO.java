package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * SQL 守卫校验结果。
 */
@Data
public class AiSqlGuardResultVO {

    /**
     * 原始 SQL。
     */
    private String originalSql;

    /**
     * 归一化后的 SQL。
     */
    private String normalizedSql;

    /**
     * 本次允许返回的最大行数。
     */
    private Integer maxRows;

    /**
     * 守卫解析出的数据表或视图。
     */
    private List<String> tables = new ArrayList<>();
}
