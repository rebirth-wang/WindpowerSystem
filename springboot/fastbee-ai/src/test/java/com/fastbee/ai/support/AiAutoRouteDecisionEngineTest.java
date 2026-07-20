package com.fastbee.ai.support;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fastbee.ai.model.enums.AiChatMode;

class AiAutoRouteDecisionEngineTest {

    private final AiAutoRouteDecisionEngine engine = new AiAutoRouteDecisionEngine();

    @Test
    void shouldMatchRouteCaseMatrix() throws Exception {
        for (RouteCase routeCase : loadRouteCases()) {
            AiAutoRouteDecision decision = engine.decide(AiAutoRouteContext.builder(routeCase.getQuestion())
                    .requestedMode(AiChatMode.AUTO.name())
                    .hasAttachment(Boolean.TRUE.equals(routeCase.getHasAttachment()))
                    .attachmentFileName(routeCase.getAttachmentFileName())
                    .previousEffectiveMode(routeCase.getPreviousEffectiveMode())
                    .build(), platformTokens());

            Assertions.assertEquals(routeCase.getExpectedMode(), decision.getFinalMode(), routeCase.getId());
            if (hasText(routeCase.getExpectedTaskType())) {
                Assertions.assertEquals(routeCase.getExpectedTaskType(), decision.getTaskType(), routeCase.getId());
            }
            if (routeCase.getExpectedDeterministic() != null) {
                Assertions.assertEquals(routeCase.getExpectedDeterministic(), decision.isDeterministic(), routeCase.getId());
            }
            if (routeCase.getExpectedRequiresModelArbitration() != null) {
                Assertions.assertEquals(routeCase.getExpectedRequiresModelArbitration(),
                        decision.isRequiresModelArbitration(), routeCase.getId());
            }
            if (hasText(routeCase.getMustNotMode())) {
                Assertions.assertNotEquals(routeCase.getMustNotMode(), decision.getFinalMode(), routeCase.getId());
            }
        }
    }

    @Test
    void shouldRouteGoldenQuestionsByLayeredDecision() {
        assertDecision("这个文件中的数据有哪些问题", AiChatMode.GENERAL.name(), "FILE_REVIEW");
        assertDecision("宫爆鸡丁怎么做", AiChatMode.GENERAL.name(), "GENERAL_CHAT");
        assertDecision("你使用的是什么大模型", AiChatMode.GENERAL.name(), "ASSISTANT_MODEL_IDENTITY");
        assertDecision("你是谁", AiChatMode.GENERAL.name(), "ASSISTANT_MODEL_IDENTITY");
        assertDecision("你知道小米手机吗", AiChatMode.GENERAL.name(), "GENERAL_CHAT");
        assertDecision("如何创建产品", AiChatMode.PLATFORM_ASSISTANT.name(), "PLATFORM_HELP");
        assertDecision("数据桥接有什么作用", AiChatMode.PLATFORM_ASSISTANT.name(), "PLATFORM_HELP");
        assertDecision("String payload = msgContext.getPayload(); 这段代码的意思是不是我设备发送的数据转变为另一种格式后再发送出去",
                AiChatMode.PLATFORM_ASSISTANT.name(), "PLATFORM_HELP");
        assertDecision("设备控制下发接口在哪里", AiChatMode.PLATFORM_ASSISTANT.name(), "CODEBASE_GUIDE");
        assertDecision("请把 DeviceRuntimeController.invoke 的完整源码贴出来",
                AiChatMode.PLATFORM_ASSISTANT.name(), "CODEBASE_GUIDE");
        assertDecision("设备上报的原始数据字段名（如 temp）需要映射到平台物模型中的标准标识符（如 temperature）",
                AiChatMode.PLATFORM_ASSISTANT.name(), "PLATFORM_HELP");
        assertDecision("统计产品数量", AiChatMode.NL2SQL.name(), "DATA_QUERY_EXECUTION");
        assertDecision("查询DEVICE88888888最近1小时灯光色值趋势", AiChatMode.NL2SQL.name(), "DATA_QUERY_EXECUTION");
        assertDecision("查询SN001当前灯光色值1是多少", AiChatMode.NL2SQL.name(), "DATA_QUERY_EXECUTION");
        assertDecision("query device SN001 current waterlevel value", AiChatMode.NL2SQL.name(), "DATA_QUERY_EXECUTION");
        assertDecision("打开设备智能开关", AiChatMode.DEVICE_CONTROL.name(), "DEVICE_CONTROL_EXECUTION");
        assertDecision("请解析这个点位表并生成物模型导入 Excel", AiChatMode.THING_MODEL_GENERATE.name(), "THING_MODEL_GENERATE");
        assertDecision("请评估这份需求文档与平台能力的匹配情况", AiChatMode.REQUIREMENT_EVALUATION.name(), "REQUIREMENT_EVALUATION");
    }

    @Test
    void shouldKeepAttachmentReviewAwayFromDataQuery() {
        AiAutoRouteDecision decision = engine.decide(AiAutoRouteContext.builder("分析一下")
                .requestedMode(AiChatMode.AUTO.name())
                .hasAttachment(true)
                .attachmentFileName("writ_sixty_0m+iai_2026-05-22.json")
                .build(), List.of());

        Assertions.assertEquals(AiChatMode.GENERAL.name(), decision.getFinalMode());
        Assertions.assertEquals("FILE_REVIEW", decision.getTaskType());
        Assertions.assertTrue(decision.isDeterministic());
        Assertions.assertTrue(decision.getCandidates().stream()
                .anyMatch(candidate -> AiChatMode.NL2SQL.name().equals(candidate.getMode())
                        && candidate.getBlockers().contains("文件或附件内容分析")));
    }

    @Test
    void shouldUseDefaultPlatformFeatureTokensFromExtractor() {
        AiAutoRouteDecision decision = engine.decide(AiAutoRouteContext.builder("数据桥接有什么作用")
                .requestedMode(AiChatMode.AUTO.name())
                .build(), AiRouteFeatureExtractor.defaultPlatformFeatureTokens());

        Assertions.assertEquals(AiChatMode.PLATFORM_ASSISTANT.name(), decision.getFinalMode());
        Assertions.assertEquals("PLATFORM_HELP", decision.getTaskType());
        Assertions.assertTrue(decision.isDeterministic());
    }

    @Test
    void shouldLeaveAmbiguousStackTraceForModelOrGeneralFallback() {
        AiAutoRouteDecision decision = engine.decide(AiAutoRouteContext.builder(
                "Caused by: java.lang.reflect.InvocationTargetException at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)")
                .requestedMode(AiChatMode.AUTO.name())
                .build(), List.of());

        Assertions.assertEquals(AiChatMode.GENERAL.name(), decision.getFinalMode());
        Assertions.assertFalse(decision.isDeterministic());
        Assertions.assertTrue(decision.isRequiresModelArbitration());
    }

    @Test
    void shouldInheritPreviousModeForShortFollowUp() {
        AiAutoRouteDecision dataFollowUp = engine.decide(AiAutoRouteContext.builder("最近一小时呢")
                .requestedMode(AiChatMode.AUTO.name())
                .previousEffectiveMode(AiChatMode.NL2SQL.name())
                .build(), List.of());

        Assertions.assertEquals(AiChatMode.NL2SQL.name(), dataFollowUp.getFinalMode());
        Assertions.assertEquals("CONTEXT_CONTINUATION", dataFollowUp.getTaskType());

        AiAutoRouteDecision platformFollowUp = engine.decide(AiAutoRouteContext.builder("那怎么配置")
                .requestedMode(AiChatMode.AUTO.name())
                .previousEffectiveMode(AiChatMode.PLATFORM_ASSISTANT.name())
                .build(), List.of());

        Assertions.assertEquals(AiChatMode.PLATFORM_ASSISTANT.name(), platformFollowUp.getFinalMode());
        Assertions.assertEquals("CONTEXT_CONTINUATION", platformFollowUp.getTaskType());
    }

    @Test
    void shouldNotInheritNl2SqlForExplicitGeneralQuestion() {
        AiAutoRouteDecision selfIdentity = engine.decide(AiAutoRouteContext.builder("你是谁")
                .requestedMode(AiChatMode.AUTO.name())
                .previousEffectiveMode(AiChatMode.NL2SQL.name())
                .build(), List.of());

        Assertions.assertEquals(AiChatMode.GENERAL.name(), selfIdentity.getFinalMode());
        Assertions.assertEquals("ASSISTANT_MODEL_IDENTITY", selfIdentity.getTaskType());

        AiAutoRouteDecision generalKnowledge = engine.decide(AiAutoRouteContext.builder("你知道小米手机吗")
                .requestedMode(AiChatMode.AUTO.name())
                .previousEffectiveMode(AiChatMode.NL2SQL.name())
                .build(), List.of());

        Assertions.assertEquals(AiChatMode.GENERAL.name(), generalKnowledge.getFinalMode());
        Assertions.assertEquals("GENERAL_CHAT", generalKnowledge.getTaskType());
    }

    private void assertDecision(String question, String expectedMode, String expectedTaskType) {
        AiAutoRouteDecision decision = engine.decide(AiAutoRouteContext.builder(question)
                .requestedMode(AiChatMode.AUTO.name())
                .build(), platformTokens());
        Assertions.assertEquals(expectedMode, decision.getFinalMode(), question);
        Assertions.assertEquals(expectedTaskType, decision.getTaskType(), question);
        Assertions.assertTrue(decision.isDeterministic(), question);
        Assertions.assertFalse(decision.getCandidates().isEmpty(), question);
    }

    private List<String> platformTokens() {
        return List.of("产品", "设备管理", "物模型", "数据桥接");
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
        private String expectedTaskType;
        private Boolean expectedDeterministic;
        private Boolean expectedRequiresModelArbitration;
        private Boolean hasAttachment;
        private String attachmentFileName;
        private String previousEffectiveMode;
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

        public String getExpectedTaskType() {
            return expectedTaskType;
        }

        public void setExpectedTaskType(String expectedTaskType) {
            this.expectedTaskType = expectedTaskType;
        }

        public Boolean getExpectedDeterministic() {
            return expectedDeterministic;
        }

        public void setExpectedDeterministic(Boolean expectedDeterministic) {
            this.expectedDeterministic = expectedDeterministic;
        }

        public Boolean getExpectedRequiresModelArbitration() {
            return expectedRequiresModelArbitration;
        }

        public void setExpectedRequiresModelArbitration(Boolean expectedRequiresModelArbitration) {
            this.expectedRequiresModelArbitration = expectedRequiresModelArbitration;
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

        public String getMustNotMode() {
            return mustNotMode;
        }

        public void setMustNotMode(String mustNotMode) {
            this.mustNotMode = mustNotMode;
        }
    }
}
