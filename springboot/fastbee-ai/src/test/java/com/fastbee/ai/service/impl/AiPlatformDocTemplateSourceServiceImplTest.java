package com.fastbee.ai.service.impl;

import java.lang.reflect.Method;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * 平台知识企业版模板导出源测试。
 */
class AiPlatformDocTemplateSourceServiceImplTest {

    private final AiPlatformDocTemplateSourceServiceImpl service = new AiPlatformDocTemplateSourceServiceImpl();

    @Test
    void shouldIncludeEnglishLocalDocsAndOnlyExcludeInternalDocs() throws Exception {
        Assertions.assertTrue(invokeShouldIncludeLocalMarkdown(Path.of("en", "README.md")));
        Assertions.assertTrue(invokeShouldIncludeLocalMarkdown(Path.of("manual", "product.md")));
        Assertions.assertFalse(invokeShouldIncludeLocalMarkdown(Path.of("internal", "tenant.md")));
        Assertions.assertFalse(invokeShouldIncludeLocalMarkdown(Path.of(".vuepress", "config.md")));
    }

    @Test
    void shouldIncludeEnglishWebDocsAndOnlyExcludeInternalDocs() throws Exception {
        Assertions.assertTrue(invokeShouldIncludeWebUrl("https://fastbee.cn/doc/en/intro.html"));
        Assertions.assertTrue(invokeShouldIncludeWebUrl("https://fastbee.cn/doc/manual/product.html"));
        Assertions.assertFalse(invokeShouldIncludeWebUrl("https://fastbee.cn/doc/internal/tenant.html"));
        Assertions.assertFalse(invokeShouldIncludeWebUrl("https://example.com/doc/manual/product.html"));
    }

    @Test
    void shouldResolveLanguageByRelativePath() throws Exception {
        Assertions.assertEquals("en-US", invokeResolveLanguageByRelativePath("en/intro.md"));
        Assertions.assertEquals("zh-CN", invokeResolveLanguageByRelativePath("manual/product.md"));
    }

    private boolean invokeShouldIncludeLocalMarkdown(Path relativePath) throws Exception {
        Method method = AiPlatformDocTemplateSourceServiceImpl.class.getDeclaredMethod("shouldIncludeLocalMarkdown", Path.class);
        method.setAccessible(true);
        return (boolean) method.invoke(service, relativePath);
    }

    private boolean invokeShouldIncludeWebUrl(String url) throws Exception {
        Method method = AiPlatformDocTemplateSourceServiceImpl.class.getDeclaredMethod("shouldIncludeWebUrl", String.class);
        method.setAccessible(true);
        return (boolean) method.invoke(service, url);
    }

    private String invokeResolveLanguageByRelativePath(String relativePath) throws Exception {
        Method method = AiPlatformDocTemplateSourceServiceImpl.class.getDeclaredMethod("resolveLanguageByRelativePath", String.class);
        method.setAccessible(true);
        return (String) method.invoke(service, relativePath);
    }
}
