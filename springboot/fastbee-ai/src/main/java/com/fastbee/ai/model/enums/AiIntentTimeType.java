package com.fastbee.ai.model.enums;

import java.util.Locale;

import com.fastbee.common.utils.StringUtils;

/**
 * AI 路由技能识别出的时间意图类型。
 */
public enum AiIntentTimeType {

    CURRENT,
    HISTORY,
    TREND,
    STAT,
    UNKNOWN;

    /**
     * 归一化时间意图编码。
     *
     * @param rawValue 模型返回值
     * @return 标准编码
     */
    public static String normalizeCode(String rawValue) {
        if (StringUtils.isBlank(rawValue)) {
            return UNKNOWN.name();
        }
        String normalized = rawValue.trim().toUpperCase(Locale.ROOT)
                .replace('-', '_')
                .replace(' ', '_');
        return switch (normalized) {
            case "CURRENT", "REALTIME", "实时", "当前", "现在" -> CURRENT.name();
            case "HISTORY", "历史", "区间", "期间" -> HISTORY.name();
            case "TREND", "趋势", "曲线" -> TREND.name();
            case "STAT", "STATISTICS", "统计", "聚合" -> STAT.name();
            default -> UNKNOWN.name();
        };
    }
}
