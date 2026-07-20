package com.fastbee.ai.model.enums;

import java.util.Locale;

/**
 * AI 区域档位。
 */
public enum AiRegionProfile {

    CN,
    GLOBAL,
    LOCAL;

    public static AiRegionProfile fromCode(String code) {
        if (code == null || code.isBlank()) {
            return CN;
        }
        try {
            return valueOf(code.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return CN;
        }
    }
}
