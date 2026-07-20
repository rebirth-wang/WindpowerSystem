package com.fastbee.ai.model.enums;

import java.util.Locale;

/**
 * AI 模型供应商类型。
 */
public enum AiModelProviderType {

    QWEN,
    DEEPSEEK,
    ZHIPU,
    MOONSHOT,
    GOOGLE,
    META,
    OPENAI,
    AZURE_OPENAI,
    OPENROUTER,
    ANTHROPIC,
    NVIDIA,
    BAIDU,
    HUAWEI,
    TENCENT,
    XIAOMI,
    XFYUN,
    SENSETIME,
    BYTEDANCE,
    MINIMAX,
    LANZHOU,
    VERTEX_AI,
    OLLAMA,
    LOCAL,
    UNKNOWN;

    public static AiModelProviderType fromCode(String code) {
        if (code == null || code.isBlank()) {
            return UNKNOWN;
        }
        String normalized = code.trim().replace('-', '_').toUpperCase(Locale.ROOT);
        if ("LOCAL".equals(normalized) || "OLLAMA".equals(normalized)) {
            return OLLAMA;
        }
        for (AiModelProviderType value : values()) {
            if (value.name().equals(normalized)) {
                return value;
            }
        }
        return UNKNOWN;
    }
}
