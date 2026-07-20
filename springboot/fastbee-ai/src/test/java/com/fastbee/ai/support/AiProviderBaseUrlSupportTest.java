package com.fastbee.ai.support;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fastbee.ai.model.enums.AiModelProviderType;

/**
 * AI 厂商基础地址规范化回归测试。
 */
class AiProviderBaseUrlSupportTest {

    @Test
    void shouldNormalizeZhipuBaseUrlWithoutVersionSegment() {
        String normalized = AiProviderBaseUrlSupport.normalizeOpenAiCompatibleBaseUrl(
                AiModelProviderType.ZHIPU,
                "https://open.bigmodel.cn/api/paas"
        );

        Assertions.assertEquals("https://open.bigmodel.cn/api/paas/v4/", normalized);
    }

    @Test
    void shouldNormalizeZhipuFullChatEndpointToBaseUrl() {
        String normalized = AiProviderBaseUrlSupport.normalizeOpenAiCompatibleBaseUrl(
                AiModelProviderType.ZHIPU,
                "https://open.bigmodel.cn/api/paas/v1/chat/completions"
        );

        Assertions.assertEquals("https://open.bigmodel.cn/api/paas/v4/", normalized);
    }

    @Test
    void shouldResolveZhipuChatCompletionsPath() {
        String completionsPath = AiProviderBaseUrlSupport.resolveChatCompletionsPath(AiModelProviderType.ZHIPU);

        Assertions.assertEquals("chat/completions", completionsPath);
    }

    @Test
    void shouldKeepOpenAiCompatibleVersionSegmentForGenericProvider() {
        String normalized = AiProviderBaseUrlSupport.normalizeOpenAiCompatibleBaseUrl(
                AiModelProviderType.OPENAI,
                "https://api.openai.com/v1/chat/completions"
        );

        Assertions.assertEquals("https://api.openai.com/v1/", normalized);
    }
}
