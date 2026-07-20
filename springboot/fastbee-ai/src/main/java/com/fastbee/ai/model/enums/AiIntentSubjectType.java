package com.fastbee.ai.model.enums;

import java.util.Locale;

import com.fastbee.common.utils.StringUtils;

/**
 * AI 路由技能识别出的主体类型。
 */
public enum AiIntentSubjectType {

    DEVICE,
    PRODUCT,
    BUSINESS,
    UNKNOWN;

    /**
     * 归一化主体类型编码。
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
            case "DEVICE", "设备" -> DEVICE.name();
            case "PRODUCT", "产品" -> PRODUCT.name();
            case "BUSINESS", "业务", "表", "业务对象" -> BUSINESS.name();
            default -> UNKNOWN.name();
        };
    }
}
