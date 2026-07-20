package com.fastbee.ai.support;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastbee.ai.model.enums.AiModelProviderType;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 厂商基础地址规范化支持。
 *
 * <p>当前模型工厂统一基于 OpenAI 兼容协议动态构建运行时模型，
 * 但不同厂商的兼容入口存在版本段、路径段差异。
 * 这里统一对运行时 baseUrl 做轻量规范化，优先兜住已知厂商的常见误配。</p>
 */
public final class AiProviderBaseUrlSupport {

    private static final Logger log = LoggerFactory.getLogger(AiProviderBaseUrlSupport.class);

    /**
     * 智谱 OpenAI 兼容官方基础地址。
     */
    static final String ZHIPU_OPENAI_COMPAT_BASE_URL = "https://open.bigmodel.cn/api/paas/v4/";

    /**
     * 智谱聊天补全相对路径。
     */
    static final String ZHIPU_CHAT_COMPLETIONS_PATH = "chat/completions";

    private AiProviderBaseUrlSupport() {
    }

    /**
     * 规范化 OpenAI 兼容 baseUrl。
     *
     * @param providerType 厂商类型
     * @param rawBaseUrl   原始配置地址
     * @return 规范化后的地址
     */
    public static String normalizeOpenAiCompatibleBaseUrl(AiModelProviderType providerType, String rawBaseUrl) {
        if (StringUtils.isBlank(rawBaseUrl)) {
            return rawBaseUrl;
        }
        String sanitizedBaseUrl = sanitizeBaseUrl(rawBaseUrl);
        if (providerType == AiModelProviderType.ZHIPU) {
            return normalizeZhipuBaseUrl(rawBaseUrl, sanitizedBaseUrl);
        }
        return ensureTrailingSlash(sanitizedBaseUrl);
    }

    /**
     * 解析厂商专属聊天补全路径。
     *
     * @param providerType 厂商类型
     * @return 若返回空，表示继续沿用 Spring AI 默认路径
     */
    public static String resolveChatCompletionsPath(AiModelProviderType providerType) {
        if (providerType == AiModelProviderType.ZHIPU) {
            return ZHIPU_CHAT_COMPLETIONS_PATH;
        }
        return null;
    }

    private static String normalizeZhipuBaseUrl(String rawBaseUrl, String sanitizedBaseUrl) {
        String trimmed = trimTrailingSlash(sanitizedBaseUrl);
        String lowerCaseUrl = trimmed.toLowerCase(Locale.ROOT);
        String normalizedBaseUrl;
        if ("https://open.bigmodel.cn".equals(lowerCaseUrl) || "http://open.bigmodel.cn".equals(lowerCaseUrl)) {
            normalizedBaseUrl = ZHIPU_OPENAI_COMPAT_BASE_URL;
        } else if (lowerCaseUrl.endsWith("/api/paas")) {
            normalizedBaseUrl = ensureTrailingSlash(trimmed + "/v4");
        } else if (lowerCaseUrl.endsWith("/api/paas/v1")) {
            normalizedBaseUrl = ensureTrailingSlash(trimmed.substring(0, trimmed.length() - 3) + "/v4");
        } else if (lowerCaseUrl.endsWith("/api/paas/v4")) {
            normalizedBaseUrl = ensureTrailingSlash(trimmed);
        } else {
            normalizedBaseUrl = ensureTrailingSlash(trimmed);
        }
        if (!normalizedBaseUrl.equals(rawBaseUrl)) {
            log.info("检测到智谱AI基础地址已自动归一化，原始地址：{}，归一化后地址：{}", rawBaseUrl, normalizedBaseUrl);
        }
        return normalizedBaseUrl;
    }

    private static String sanitizeBaseUrl(String rawBaseUrl) {
        String normalized = rawBaseUrl.trim();
        normalized = removeSuffixIgnoreCase(normalized, "/chat/completions");
        normalized = removeSuffixIgnoreCase(normalized, "/v1/chat/completions");
        normalized = removeSuffixIgnoreCase(normalized, "/v4/chat/completions");
        return normalized;
    }

    private static String removeSuffixIgnoreCase(String text, String suffix) {
        if (StringUtils.isBlank(text) || StringUtils.isBlank(suffix)) {
            return text;
        }
        if (text.toLowerCase(Locale.ROOT).endsWith(suffix.toLowerCase(Locale.ROOT))) {
            return text.substring(0, text.length() - suffix.length());
        }
        return text;
    }

    private static String ensureTrailingSlash(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        return text.endsWith("/") ? text : text + "/";
    }

    private static String trimTrailingSlash(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }
        String normalized = text;
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }
}
