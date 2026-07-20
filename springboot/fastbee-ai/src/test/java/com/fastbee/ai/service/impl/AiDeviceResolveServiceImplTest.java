package com.fastbee.ai.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * AI 设备名称解析测试。
 */
class AiDeviceResolveServiceImplTest {

    private final AiDeviceResolveServiceImpl service = new AiDeviceResolveServiceImpl();

    @Test
    void shouldStopExplicitDeviceTokenBeforePossessiveThingModelText() {
        String token = ReflectionTestUtils.invokeMethod(service, "extractExplicitDeviceToken", "关闭设备智能开关的开关");

        Assertions.assertEquals("智能开关", token);
    }

    @Test
    void shouldExtractExplicitDeviceTokenBeforeCurrentMetricText() {
        String token = ReflectionTestUtils.invokeMethod(service, "extractExplicitDeviceToken", "查询设备wifi智能开关当前温度是多少");

        Assertions.assertEquals("wifi智能开关", token);
    }

    @Test
    void shouldStripChineseQuotesFromExplicitDeviceToken() {
        String token = ReflectionTestUtils.invokeMethod(service, "extractExplicitDeviceToken", "查询设备“智能开关”当前“温度”是多少");

        Assertions.assertEquals("智能开关", token);
    }

    @Test
    void shouldStripPlaceholderBracesFromExplicitDeviceToken() {
        String token = ReflectionTestUtils.invokeMethod(service, "extractExplicitDeviceToken", "查询设备{智能开关}当前{温度}是多少");

        Assertions.assertEquals("智能开关", token);
    }

    @Test
    void shouldNormalizeDeviceNameWithTemplateDecorators() {
        String normalized = ReflectionTestUtils.invokeMethod(service, "normalizeDeviceName", "“{智能开关}”");

        Assertions.assertEquals("智能开关", normalized);
    }
}
