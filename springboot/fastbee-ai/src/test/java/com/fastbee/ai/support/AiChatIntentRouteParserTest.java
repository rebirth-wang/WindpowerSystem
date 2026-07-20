package com.fastbee.ai.support;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fastbee.ai.model.vo.AiChatIntentRouteVO;

/**
 * AI 会话路由技能结构化解析器测试。
 */
class AiChatIntentRouteParserTest {

    private final AiChatIntentRouteParser parser = new AiChatIntentRouteParser();

    @Test
    void shouldParseStrictJsonResponse() {
        String response = """
                {"mode":"NL2SQL","modeConfidence":0.92,"businessType":"DEVICE_RUNTIME_QUERY","subjectType":"DEVICE","deviceNameText":"talktest","thingModelText":"温度","thingModelTypeHint":"PROPERTY","timeIntent":"CURRENT","aggregateType":"NONE","needClarify":false,"reason":"用户在查询设备当前指标"}
                """;

        AiChatIntentRouteVO result = parser.parse("查询 talktest 当前温度是多少", "AUTO", response);

        Assertions.assertEquals("SUCCESS", result.getParseStatus());
        Assertions.assertEquals("NL2SQL", result.getMode());
        Assertions.assertEquals("DEVICE_RUNTIME_QUERY", result.getBusinessType());
        Assertions.assertEquals("DEVICE", result.getSubjectType());
        Assertions.assertEquals("talktest", result.getDeviceNameText());
        Assertions.assertEquals("温度", result.getThingModelText());
        Assertions.assertEquals("PROPERTY", result.getThingModelTypeHint());
        Assertions.assertEquals("CURRENT", result.getTimeIntent());
        Assertions.assertEquals("NONE", result.getAggregateType());
        Assertions.assertFalse(Boolean.TRUE.equals(result.getNeedClarify()));
    }

    @Test
    void shouldNormalizeChineseEnumValues() {
        String response = """
                ```json
                {
                  "mode":"设备控制",
                  "modeConfidence":0.81,
                  "businessType":"服务调用",
                  "subjectType":"设备",
                  "deviceNameText":"空压机1",
                  "thingModelText":"开关",
                  "thingModelTypeHint":"属性",
                  "actionText":"打开",
                  "actionValue":"1",
                  "timeIntent":"未知",
                  "aggregateType":"无",
                  "needClarify":"是",
                  "reason":"用户希望控制设备"
                }
                ```
                """;

        AiChatIntentRouteVO result = parser.parse("打开空压机1开关", "AUTO", response);

        Assertions.assertEquals("DEVICE_CONTROL", result.getMode());
        Assertions.assertEquals("DEVICE_SERVICE_INVOKE", result.getBusinessType());
        Assertions.assertEquals("DEVICE", result.getSubjectType());
        Assertions.assertEquals("PROPERTY", result.getThingModelTypeHint());
        Assertions.assertEquals("1", result.getActionValue());
        Assertions.assertTrue(Boolean.TRUE.equals(result.getNeedClarify()));
        Assertions.assertEquals("NONE", result.getAggregateType());
    }

    @Test
    void shouldStripSlotDecoratorsFromStructuredText() {
        String response = """
                {"mode":"NL2SQL","modeConfidence":0.92,"businessType":"DEVICE_RUNTIME_QUERY","subjectType":"DEVICE","deviceNameText":"“智能开关”","thingModelText":"{温度}","thingModelTypeHint":"PROPERTY","timeIntent":"CURRENT","aggregateType":"NONE","needClarify":false,"reason":"用户保留了模板占位符装饰"}
                """;

        AiChatIntentRouteVO result = parser.parse("查询设备“智能开关”当前{温度}是多少", "AUTO", response);

        Assertions.assertEquals("智能开关", result.getDeviceNameText());
        Assertions.assertEquals("温度", result.getThingModelText());
    }

    @Test
    void shouldFailWhenNoJsonReturned() {
        AiChatIntentRouteVO result = parser.parse("随便聊聊今天的天气", "AUTO", "这看起来更像普通对话。");

        Assertions.assertEquals("FAILED", result.getParseStatus());
        Assertions.assertEquals("AI_CHAT_INTENT_ROUTE_PARSE_FAILED", result.getParseErrorCode());
        Assertions.assertFalse(Boolean.TRUE.equals(result.getStructuredOutput()));
    }

    @Test
    void shouldNormalizeThingModelGenerateMode() {
        String response = """
                {"mode":"物模型生成","modeConfidence":0.88,"businessType":"物模型生成","subjectType":"PRODUCT","needClarify":false}
                """;

        AiChatIntentRouteVO result = parser.parse("请根据上传点位表生成物模型导入模板", "AUTO", response);

        Assertions.assertEquals("SUCCESS", result.getParseStatus());
        Assertions.assertEquals("THING_MODEL_GENERATE", result.getMode());
        Assertions.assertEquals("THING_MODEL_GENERATE", result.getBusinessType());
    }

    @Test
    void shouldNormalizeRequirementEvaluationMode() {
        String response = """
                {"mode":"需求评估","modeConfidence":0.86,"businessType":"需求评估","subjectType":"BUSINESS","needClarify":false}
                """;

        AiChatIntentRouteVO result = parser.parse("请评估这份需求文档", "AUTO", response);

        Assertions.assertEquals("SUCCESS", result.getParseStatus());
        Assertions.assertEquals("REQUIREMENT_EVALUATION", result.getMode());
        Assertions.assertEquals("REQUIREMENT_EVALUATION", result.getBusinessType());
    }
}
