package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import lombok.Data;

/**
 * AI 只读 SQL 查询结果。
 */
@Data
public class AiNl2SqlQueryResultVO {

    /**
     * 原始 SQL。
     */
    private String originalSql;

    /**
     * 守卫归一化后的 SQL。
     */
    private String normalizedSql;

    /**
     * 最终执行 SQL。
     */
    private String executedSql;

    /**
     * 命中的数据表。
     */
    private List<String> tables = new ArrayList<>();

    /**
     * 结果列。
     */
    private List<String> columns = new ArrayList<>();

    /**
     * 结果行。
     */
    private List<LinkedHashMap<String, Object>> rows = new ArrayList<>();

    /**
     * 实际返回行数。
     */
    private Integer rowCount = 0;

    /**
     * 本次最大返回行数。
     */
    private Integer maxRows;

    /**
     * 是否应用了数据范围过滤。
     */
    private Boolean dataScopeApplied = Boolean.FALSE;
}
