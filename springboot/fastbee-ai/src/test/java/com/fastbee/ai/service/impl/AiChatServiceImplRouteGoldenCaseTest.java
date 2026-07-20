package com.fastbee.ai.service.impl;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fastbee.ai.domain.AiChatMessage;
import com.fastbee.ai.model.dto.AiChatSendRequest;
import com.fastbee.ai.model.enums.AiChatMode;
import com.fastbee.ai.support.AiIntentRoutePolicy;

class AiChatServiceImplRouteGoldenCaseTest {

    private final AiChatServiceImpl service = new AiChatServiceImpl();

    @Test
    void shouldMatchRouteGoldenCases() throws Exception {
        for (RouteCase routeCase : loadRouteCases()) {
            String ruleMode = resolveRuleMode(routeCase);
            Assertions.assertEquals(routeCase.getExpectedMode(), ruleMode, routeCase.getId());

            if (!hasContextPayload(routeCase)) {
                Object fastMode = invokePrivateMethod(
                        "resolveFastIntentRouteMode",
                        new Class[]{String.class},
                        AiIntentRoutePolicy.normalizeQuestion(routeCase.getQuestion())
                );
                if (AiChatMode.GENERAL.name().equals(routeCase.getExpectedMode())) {
                    Assertions.assertTrue(fastMode == null || AiChatMode.GENERAL.name().equals(String.valueOf(fastMode)), routeCase.getId());
                } else {
                    Assertions.assertEquals(routeCase.getExpectedMode(), String.valueOf(fastMode), routeCase.getId());
                }
            }
        }
    }

    private String resolveRuleMode(RouteCase routeCase) throws Exception {
        if (!hasContextPayload(routeCase)) {
            return String.valueOf(invokePrivateMethod(
                    "resolveRuleEffectiveMode",
                    new Class[]{String.class},
                    routeCase.getQuestion()
            ));
        }
        AiChatSendRequest request = new AiChatSendRequest();
        if (Boolean.TRUE.equals(routeCase.getHasAttachment())) {
            request.setAttachmentFileName(routeCase.getAttachmentFileName());
            request.setAttachmentText("test attachment");
        }
        return String.valueOf(invokePrivateMethod(
                "resolveRuleEffectiveMode",
                new Class[]{String.class, AiChatSendRequest.class, List.class},
                routeCase.getQuestion(),
                request,
                buildHistory(routeCase)
        ));
    }

    private List<AiChatMessage> buildHistory(RouteCase routeCase) {
        if (routeCase.getPreviousEffectiveMode() == null || routeCase.getPreviousEffectiveMode().isBlank()) {
            return List.of();
        }
        AiChatMessage message = new AiChatMessage();
        message.setRoleType("assistant");
        message.setAbilityType(routeCase.getPreviousEffectiveMode());
        message.setToolName(routeCase.getPreviousTaskType());
        message.setMessageContent("上一轮回答");
        message.setMessageStatus("SUCCESS");
        return List.of(message);
    }

    private boolean hasContextPayload(RouteCase routeCase) {
        return Boolean.TRUE.equals(routeCase.getHasAttachment())
                || routeCase.getPreviousEffectiveMode() != null && !routeCase.getPreviousEffectiveMode().isBlank();
    }

    private List<RouteCase> loadRouteCases() throws Exception {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("ai-route-cases.json")) {
            Assertions.assertNotNull(inputStream, "ai-route-cases.json");
            String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            return JSON.parseArray(json, RouteCase.class);
        }
    }

    private Object invokePrivateMethod(String name, Class<?>[] parameterTypes, Object... args) throws Exception {
        Method method = AiChatServiceImpl.class.getDeclaredMethod(name, parameterTypes);
        method.setAccessible(true);
        return method.invoke(service, args);
    }

    public static class RouteCase {

        private String id;
        private String question;
        private String expectedMode;
        private Boolean hasAttachment;
        private String attachmentFileName;
        private String previousEffectiveMode;
        private String previousTaskType;

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
    }
}
