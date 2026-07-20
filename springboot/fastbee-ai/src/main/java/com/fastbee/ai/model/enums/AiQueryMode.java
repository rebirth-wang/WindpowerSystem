package com.fastbee.ai.model.enums;

import java.util.Locale;

/**
 * AI 问数执行模式。
 */
public enum AiQueryMode {

    /**
     * 关系库受控 SQL 问数。
     */
    RDB_SQL,

    /**
     * 时序库查询。
     */
    TSDB_QUERY,

    /**
     * Redis 实时值查询。
     */
    REDIS_VALUE,

    /**
     * 多源混合执行。
     */
    HYBRID_PIPELINE;

    /**
     * 按编码解析执行模式。
     *
     * @param code 执行模式编码
     * @return 执行模式
     */
    public static AiQueryMode fromCode(String code) {
        if (code == null || code.isBlank()) {
            return RDB_SQL;
        }
        String normalized = code.trim().replace('-', '_').toUpperCase(Locale.ROOT);
        for (AiQueryMode value : values()) {
            if (value.name().equals(normalized)) {
                return value;
            }
        }
        return RDB_SQL;
    }
}
