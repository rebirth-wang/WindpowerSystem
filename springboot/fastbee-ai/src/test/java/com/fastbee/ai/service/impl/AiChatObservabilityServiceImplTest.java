package com.fastbee.ai.service.impl;

import java.util.List;

import com.alibaba.fastjson2.JSON;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fastbee.ai.domain.AiChatMessage;
import com.fastbee.ai.model.enums.AiChatMode;
import com.fastbee.ai.model.vo.AiChatIntentRouteVO;
import com.fastbee.ai.model.vo.AiChatObservabilityStatsVO;
import com.fastbee.ai.support.AiChatObservabilityConstants;
import com.fastbee.common.utils.StringUtils;

class AiChatObservabilityServiceImplTest {

    private final AiChatObservabilityServiceImpl service = new AiChatObservabilityServiceImpl();

    @Test
    void shouldTreatAutoRequestWithoutCorrectionAsHit() {
        AiChatObservabilityStatsVO stats = service.buildStats(List.of(
                buildUserMessage(1L, 1001L, 1, AiChatMode.AUTO.name(), null, null, null),
                buildAssistantMessage(1L, 1002L, 2, AiChatMode.GENERAL.name(), "SUCCESS")
        ), 7);

        Assertions.assertEquals(1, stats.getRequestCount());
        Assertions.assertEquals(1, stats.getAutoRequestCount());
        Assertions.assertEquals(1, stats.getAutoHitCount());
        Assertions.assertEquals(0, stats.getAutoCorrectedCount());
        Assertions.assertEquals(0, stats.getManualModeRequestCount());
        Assertions.assertEquals(1D, stats.getAutoHitRate());
    }

    @Test
    void shouldCountCorrectionRetryAndCorrectionSuccess() {
        AiChatObservabilityStatsVO stats = service.buildStats(List.of(
                buildUserMessage(2L, 2001L, 1, AiChatMode.AUTO.name(), null, null, null),
                buildAssistantMessage(2L, 2002L, 2, AiChatMode.PLATFORM_ASSISTANT.name(), "SUCCESS"),
                buildUserMessage(2L, 2003L, 3, AiChatMode.NL2SQL.name(),
                        AiChatObservabilityConstants.MANUAL_MODE_SOURCE_OVERRIDE,
                        AiChatObservabilityConstants.INTERACTION_SOURCE_MODE_CORRECTION_RETRY,
                        2001L),
                buildAssistantMessage(2L, 2004L, 4, AiChatMode.NL2SQL.name(), "SUCCESS")
        ), 7);

        Assertions.assertEquals(2, stats.getRequestCount());
        Assertions.assertEquals(1, stats.getAutoRequestCount());
        Assertions.assertEquals(0, stats.getAutoHitCount());
        Assertions.assertEquals(1, stats.getAutoCorrectedCount());
        Assertions.assertEquals(1, stats.getManualModeRequestCount());
        Assertions.assertEquals(1, stats.getOverrideRequestCount());
        Assertions.assertEquals(1, stats.getCorrectionRetryCount());
        Assertions.assertEquals(1, stats.getCorrectionSuccessCount());
        Assertions.assertEquals(1D, stats.getCorrectionSuccessRate());
        Assertions.assertEquals(0.5D, stats.getManualModeRate());
    }

    private AiChatMessage buildUserMessage(Long sessionId,
                                           Long messageId,
                                           int messageNo,
                                           String requestedMode,
                                           String manualModeSource,
                                           String interactionSource,
                                           Long retrySourceMessageId) {
        AiChatIntentRouteVO audit = new AiChatIntentRouteVO();
        audit.setRequestedMode(requestedMode);
        audit.setMode(AiChatMode.GENERAL.name());
        audit.setFinalMode(StringUtils.defaultIfBlank(requestedMode, AiChatMode.GENERAL.name()));
        audit.setManualModeSource(manualModeSource);
        audit.setInteractionSource(interactionSource);
        audit.setRetrySourceMessageId(retrySourceMessageId);

        AiChatMessage message = new AiChatMessage();
        message.setSessionId(sessionId);
        message.setMessageId(messageId);
        message.setMessageNo(messageNo);
        message.setRoleType("user");
        message.setAbilityType(requestedMode);
        message.setToolName("auto_router_analyze");
        message.setToolResult(JSON.toJSONString(audit));
        return message;
    }

    private AiChatMessage buildAssistantMessage(Long sessionId,
                                                Long messageId,
                                                int messageNo,
                                                String abilityType,
                                                String status) {
        AiChatMessage message = new AiChatMessage();
        message.setSessionId(sessionId);
        message.setMessageId(messageId);
        message.setMessageNo(messageNo);
        message.setRoleType("assistant");
        message.setAbilityType(abilityType);
        message.setMessageStatus(status);
        return message;
    }
}
