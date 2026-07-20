package com.fastbee.ai.support;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fastbee.ai.model.enums.AiChatMode;

class AiAutoRouteModeMetadataTest {

    @Test
    void shouldResolveFastConfidenceByMode() {
        Assertions.assertEquals(0.90D, AiAutoRouteModeMetadata.fastConfidence(AiChatMode.NL2SQL.name()));
        Assertions.assertEquals(0.92D, AiAutoRouteModeMetadata.fastConfidence(AiChatMode.DEVICE_CONTROL.name()));
        Assertions.assertEquals(0.99D, AiAutoRouteModeMetadata.fastConfidence(AiChatMode.PLATFORM_ASSISTANT.name()));
    }

    @Test
    void shouldResolveBusinessTypeByMode() {
        Assertions.assertEquals("GENERAL_CHAT", AiAutoRouteModeMetadata.businessType(AiChatMode.GENERAL.name()));
        Assertions.assertEquals("PLATFORM_HELP", AiAutoRouteModeMetadata.businessType(AiChatMode.PLATFORM_ASSISTANT.name()));
        Assertions.assertEquals("RDB_QUERY", AiAutoRouteModeMetadata.businessType(AiChatMode.NL2SQL.name()));
        Assertions.assertEquals("DEVICE_PROPERTY_CONTROL", AiAutoRouteModeMetadata.businessType(AiChatMode.DEVICE_CONTROL.name()));
        Assertions.assertEquals("PROTOCOL_PARSE", AiAutoRouteModeMetadata.businessType(AiChatMode.PROTOCOL_PARSE.name()));
        Assertions.assertEquals("THING_MODEL_GENERATE", AiAutoRouteModeMetadata.businessType(AiChatMode.THING_MODEL_GENERATE.name()));
        Assertions.assertEquals("REQUIREMENT_EVALUATION", AiAutoRouteModeMetadata.businessType(AiChatMode.REQUIREMENT_EVALUATION.name()));
    }

    @Test
    void shouldResolveFastReasonMessageCodeByMode() {
        Assertions.assertEquals("ai.chat.route.fast.general",
                AiAutoRouteModeMetadata.fastReasonMessageCode(AiChatMode.GENERAL.name()));
        Assertions.assertEquals("ai.chat.route.fast.platform.assistant",
                AiAutoRouteModeMetadata.fastReasonMessageCode(AiChatMode.PLATFORM_ASSISTANT.name()));
        Assertions.assertEquals("ai.chat.route.fast.nl2sql",
                AiAutoRouteModeMetadata.fastReasonMessageCode(AiChatMode.NL2SQL.name()));
        Assertions.assertEquals("ai.chat.route.fast.device.control",
                AiAutoRouteModeMetadata.fastReasonMessageCode(AiChatMode.DEVICE_CONTROL.name()));
    }
}
