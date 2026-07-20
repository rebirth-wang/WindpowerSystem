package com.fastbee.common.extend.ai.enums;

/**
 * AI 问数语义来源类型。
 */
public enum AiSemanticSourceType {

    /**
     * 人工维护或人工确认。
     */
    MANUAL,

    /**
     * 系统字典。
     */
    DICT,

    /**
     * Java 枚举。
     */
    ENUM,

    /**
     * 数据库字段注释自动识别。
     */
    AUTO_COMMENT
}
