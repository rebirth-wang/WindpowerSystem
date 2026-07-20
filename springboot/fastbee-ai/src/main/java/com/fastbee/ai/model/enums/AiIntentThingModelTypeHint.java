package com.fastbee.ai.model.enums;

import java.util.Locale;

import com.fastbee.common.utils.StringUtils;

/**
 * AI 路由技能识别出的物模型类型提示。
 */
public enum AiIntentThingModelTypeHint {

    PROPERTY,
    SERVICE,
    EVENT,
    UNKNOWN;

    /**
     * 归一化物模型类型提示编码。
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
            case "PROPERTY", "属性" -> PROPERTY.name();
            case "SERVICE", "服务", "功能" -> SERVICE.name();
            case "EVENT", "事件" -> EVENT.name();
            default -> UNKNOWN.name();
        };
    }
}
