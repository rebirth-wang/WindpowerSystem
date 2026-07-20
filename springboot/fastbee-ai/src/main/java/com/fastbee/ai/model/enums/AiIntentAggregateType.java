package com.fastbee.ai.model.enums;

import java.util.Locale;

import com.fastbee.common.utils.StringUtils;

/**
 * AI 路由技能识别出的聚合类型。
 */
public enum AiIntentAggregateType {

    COUNT,
    SUM,
    AVG,
    MAX,
    MIN,
    NONE;

    /**
     * 归一化聚合类型编码。
     *
     * @param rawValue 模型返回值
     * @return 标准编码
     */
    public static String normalizeCode(String rawValue) {
        if (StringUtils.isBlank(rawValue)) {
            return NONE.name();
        }
        String normalized = rawValue.trim().toUpperCase(Locale.ROOT)
                .replace('-', '_')
                .replace(' ', '_');
        return switch (normalized) {
            case "COUNT", "统计数量", "数量", "计数" -> COUNT.name();
            case "SUM", "TOTAL", "总和", "求和", "总计", "累计" -> SUM.name();
            case "AVG", "AVERAGE", "平均", "均值" -> AVG.name();
            case "MAX", "最大", "峰值" -> MAX.name();
            case "MIN", "最小" -> MIN.name();
            default -> NONE.name();
        };
    }
}
