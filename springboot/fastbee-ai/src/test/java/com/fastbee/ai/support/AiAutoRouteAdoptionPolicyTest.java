package com.fastbee.ai.support;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fastbee.ai.model.enums.AiChatMode;
import com.fastbee.ai.model.vo.AiChatIntentRouteVO;

class AiAutoRouteAdoptionPolicyTest {

    private final AiAutoRouteDecisionEngine engine = new AiAutoRouteDecisionEngine();

    @Test
    void shouldFallbackSuspiciousDeviceControlToLocalPlatformHelp() {
        AiChatIntentRouteVO route = route(AiChatMode.DEVICE_CONTROL.name(), 0.95D);
        AiAutoRouteAdoptionPolicy.Decision decision = AiAutoRouteAdoptionPolicy.decide(
                "在固件升级时设备离线，平台下发升级指令，设备再上线能收到吗",
                route,
                local(AiChatMode.PLATFORM_ASSISTANT.name(), "PLATFORM_HELP"),
                platformTokens(),
                AiAutoRouteAdoptionPolicy.Options.defaults());

        Assertions.assertEquals(AiChatMode.PLATFORM_ASSISTANT.name(), decision.getFinalMode());
        Assertions.assertFalse(decision.isAdoptedBySystem());
        Assertions.assertEquals(AiAutoRouteAdoptionPolicy.FallbackReason.SUSPICIOUS_DEVICE_CONTROL,
                decision.getFallbackReason());
    }

    @Test
    void shouldFallbackNl2SqlLocationQuestionToPlatformHelp() {
        AiChatIntentRouteVO route = route(AiChatMode.NL2SQL.name(), 0.96D);
        AiAutoRouteAdoptionPolicy.Decision decision = AiAutoRouteAdoptionPolicy.decide(
                "设备控制下发接口在哪里",
                route,
                local(AiChatMode.PLATFORM_ASSISTANT.name(), "CODEBASE_GUIDE"),
                platformTokens(),
                AiAutoRouteAdoptionPolicy.Options.defaults());

        Assertions.assertEquals(AiChatMode.PLATFORM_ASSISTANT.name(), decision.getFinalMode());
        Assertions.assertFalse(decision.isAdoptedBySystem());
        Assertions.assertEquals(AiAutoRouteAdoptionPolicy.FallbackReason.NL2SQL_LOCATION_QUESTION,
                decision.getFallbackReason());
    }

    @Test
    void shouldFallbackNl2SqlFieldMappingGuideToPlatformHelp() {
        AiChatIntentRouteVO route = route(AiChatMode.NL2SQL.name(), 0.97D);
        route.setBusinessType("RDB_QUERY");

        AiAutoRouteAdoptionPolicy.Decision decision = AiAutoRouteAdoptionPolicy.decide(
                "现在设备上报消息到MQTT的/property/post主题，我想通过平台将设备上报的原始数据字段名（如 temp）需要映射到平台物模型中的标准标识符（如 temperature），怎么操作",
                route,
                local(AiChatMode.PLATFORM_ASSISTANT.name(), "PLATFORM_HELP"),
                platformTokens(),
                AiAutoRouteAdoptionPolicy.Options.defaults());

        Assertions.assertEquals(AiChatMode.PLATFORM_ASSISTANT.name(), decision.getFinalMode());
        Assertions.assertFalse(decision.isAdoptedBySystem());
        Assertions.assertEquals(AiAutoRouteAdoptionPolicy.FallbackReason.NL2SQL_LOCATION_QUESTION,
                decision.getFallbackReason());
    }

    @Test
    void shouldFallbackPlatformAssistantGeneralQuestionToGeneral() {
        AiChatIntentRouteVO route = route(AiChatMode.PLATFORM_ASSISTANT.name(), 0.92D);
        AiAutoRouteAdoptionPolicy.Decision decision = AiAutoRouteAdoptionPolicy.decide(
                "宫爆鸡丁怎么做",
                route,
                local(AiChatMode.GENERAL.name(), "GENERAL_CHAT"),
                platformTokens(),
                AiAutoRouteAdoptionPolicy.Options.defaults());

        Assertions.assertEquals(AiChatMode.GENERAL.name(), decision.getFinalMode());
        Assertions.assertFalse(decision.isAdoptedBySystem());
        Assertions.assertEquals(AiAutoRouteAdoptionPolicy.FallbackReason.LOCAL_RULE,
                decision.getFallbackReason());
    }

    @Test
    void shouldFallbackNl2SqlGeneralQuestionToGeneral() {
        AiChatIntentRouteVO route = route(AiChatMode.NL2SQL.name(), 0.97D);
        route.setBusinessType("RDB_QUERY");

        AiAutoRouteAdoptionPolicy.Decision decision = AiAutoRouteAdoptionPolicy.decide(
                "宫爆鸡丁怎么做",
                route,
                local(AiChatMode.GENERAL.name(), "GENERAL_CHAT"),
                platformTokens(),
                AiAutoRouteAdoptionPolicy.Options.defaults());

        Assertions.assertEquals(AiChatMode.GENERAL.name(), decision.getFinalMode());
        Assertions.assertFalse(decision.isAdoptedBySystem());
        Assertions.assertEquals(AiAutoRouteAdoptionPolicy.FallbackReason.LOCAL_RULE,
                decision.getFallbackReason());
    }

    @Test
    void shouldForceDeviceRuntimeReadToNl2Sql() {
        AiChatIntentRouteVO route = route(AiChatMode.GENERAL.name(), 0.90D);
        route.setBusinessType("DEVICE_RUNTIME_QUERY");

        AiAutoRouteAdoptionPolicy.Decision decision = AiAutoRouteAdoptionPolicy.decide(
                "查询SN001当前灯光色值1是多少",
                route,
                local(AiChatMode.GENERAL.name(), "GENERAL_CHAT"),
                platformTokens(),
                AiAutoRouteAdoptionPolicy.Options.defaults());

        Assertions.assertEquals(AiChatMode.NL2SQL.name(), decision.getFinalMode());
        Assertions.assertFalse(decision.isAdoptedBySystem());
        Assertions.assertEquals(AiAutoRouteAdoptionPolicy.FallbackReason.DEVICE_RUNTIME_READ,
                decision.getFallbackReason());
    }

    @Test
    void shouldFallbackLowConfidenceHighRiskModelSuggestion() {
        AiChatIntentRouteVO route = route(AiChatMode.DEVICE_CONTROL.name(), 0.50D);

        AiAutoRouteAdoptionPolicy.Decision decision = AiAutoRouteAdoptionPolicy.decide(
                "把设备A的开关打开",
                route,
                local(AiChatMode.GENERAL.name(), "GENERAL_CHAT", false, true),
                platformTokens(),
                AiAutoRouteAdoptionPolicy.Options.defaults());

        Assertions.assertEquals(AiChatMode.GENERAL.name(), decision.getFinalMode());
        Assertions.assertFalse(decision.isAdoptedBySystem());
        Assertions.assertEquals(AiAutoRouteAdoptionPolicy.FallbackReason.LOW_CONFIDENCE,
                decision.getFallbackReason());
        Assertions.assertEquals(AiChatMode.DEVICE_CONTROL.name(), decision.getEvaluatedMode());
        Assertions.assertEquals(0.50D, decision.getConfidence());
    }

    @Test
    void shouldAdoptHighConfidenceModelSuggestionWhenLocalIsNotDeterministic() {
        AiChatIntentRouteVO route = route(AiChatMode.PLATFORM_ASSISTANT.name(), 0.90D);

        AiAutoRouteAdoptionPolicy.Decision decision = AiAutoRouteAdoptionPolicy.decide(
                "如何创建产品",
                route,
                local(AiChatMode.GENERAL.name(), "GENERAL_CHAT", false, true),
                platformTokens(),
                AiAutoRouteAdoptionPolicy.Options.defaults());

        Assertions.assertEquals(AiChatMode.PLATFORM_ASSISTANT.name(), decision.getFinalMode());
        Assertions.assertTrue(decision.isAdoptedBySystem());
        Assertions.assertEquals(AiAutoRouteAdoptionPolicy.FallbackReason.NONE, decision.getFallbackReason());
    }

    @Test
    void shouldRejectMustNotModesFromRouteCaseMatrix() throws Exception {
        for (RouteCase routeCase : loadRouteCases()) {
            if (!hasText(routeCase.getMustNotMode())) {
                continue;
            }
            AiAutoRouteDecision localDecision = engine.decide(buildContext(routeCase), platformTokens());
            AiChatIntentRouteVO modelSuggestion = route(routeCase.getMustNotMode(), 0.97D);
            modelSuggestion.setBusinessType(resolveBusinessType(routeCase.getMustNotMode()));

            AiAutoRouteAdoptionPolicy.Decision decision = AiAutoRouteAdoptionPolicy.decide(
                    routeCase.getQuestion(),
                    modelSuggestion,
                    localDecision,
                    platformTokens(),
                    AiAutoRouteAdoptionPolicy.Options.defaults());

            Assertions.assertEquals(routeCase.getExpectedMode(), decision.getFinalMode(), routeCase.getId());
            Assertions.assertNotEquals(routeCase.getMustNotMode(), decision.getFinalMode(), routeCase.getId());
            Assertions.assertFalse(decision.isAdoptedBySystem(), routeCase.getId());
        }
    }

    private AiChatIntentRouteVO route(String mode, Double confidence) {
        AiChatIntentRouteVO route = new AiChatIntentRouteVO();
        route.setParseStatus("SUCCESS");
        route.setMode(mode);
        route.setModeConfidence(confidence);
        return route;
    }

    private AiAutoRouteDecision local(String mode, String taskType) {
        return local(mode, taskType, true, false);
    }

    private AiAutoRouteDecision local(String mode, String taskType, boolean deterministic, boolean requiresModelArbitration) {
        return new AiAutoRouteDecision(mode, taskType, "test", 0.88D,
                deterministic, requiresModelArbitration, List.of());
    }

    private List<String> platformTokens() {
        return List.of("产品", "设备管理", "物模型", "数据桥接");
    }

    private AiAutoRouteContext buildContext(RouteCase routeCase) {
        return AiAutoRouteContext.builder(routeCase.getQuestion())
                .requestedMode(AiChatMode.AUTO.name())
                .hasAttachment(Boolean.TRUE.equals(routeCase.getHasAttachment()))
                .attachmentFileName(routeCase.getAttachmentFileName())
                .previousEffectiveMode(routeCase.getPreviousEffectiveMode())
                .previousTaskType(routeCase.getPreviousTaskType())
                .build();
    }

    private String resolveBusinessType(String mode) {
        if (AiChatMode.NL2SQL.name().equals(mode)) {
            return "RDB_QUERY";
        }
        if (AiChatMode.DEVICE_CONTROL.name().equals(mode)) {
            return "DEVICE_PROPERTY_CONTROL";
        }
        if (AiChatMode.PLATFORM_ASSISTANT.name().equals(mode)) {
            return "PLATFORM_HELP";
        }
        if (AiChatMode.GENERAL.name().equals(mode)) {
            return "GENERAL_CHAT";
        }
        return "UNKNOWN";
    }

    private List<RouteCase> loadRouteCases() throws Exception {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("ai-route-cases.json")) {
            Assertions.assertNotNull(inputStream, "ai-route-cases.json");
            String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return JSON.parseArray(json, RouteCase.class);
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    public static class RouteCase {

        private String id;
        private String question;
        private String expectedMode;
        private Boolean hasAttachment;
        private String attachmentFileName;
        private String previousEffectiveMode;
        private String previousTaskType;
        private String mustNotMode;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getExpectedMode() {
            return expectedMode;
        }

        public void setExpectedMode(String expectedMode) {
            this.expectedMode = expectedMode;
        }

        public Boolean getHasAttachment() {
            return hasAttachment;
        }

        public void setHasAttachment(Boolean hasAttachment) {
            this.hasAttachment = hasAttachment;
        }

        public String getAttachmentFileName() {
            return attachmentFileName;
        }

        public void setAttachmentFileName(String attachmentFileName) {
            this.attachmentFileName = attachmentFileName;
        }

        public String getPreviousEffectiveMode() {
            return previousEffectiveMode;
        }

        public void setPreviousEffectiveMode(String previousEffectiveMode) {
            this.previousEffectiveMode = previousEffectiveMode;
        }

        public String getPreviousTaskType() {
            return previousTaskType;
        }

        public void setPreviousTaskType(String previousTaskType) {
            this.previousTaskType = previousTaskType;
        }

        public String getMustNotMode() {
            return mustNotMode;
        }

        public void setMustNotMode(String mustNotMode) {
            this.mustNotMode = mustNotMode;
        }
    }
}
