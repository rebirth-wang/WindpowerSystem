package com.fastbee.ai.support;

import com.fastbee.ai.model.enums.AiChatMode;

/**
 * AUTO 路由模式元数据。
 */
public final class AiAutoRouteModeMetadata {

    private AiAutoRouteModeMetadata() {
    }

    public static double fastConfidence(String mode) {
        if (AiChatMode.DEVICE_CONTROL.name().equals(mode)) {
            return 0.92D;
        }
        if (AiChatMode.NL2SQL.name().equals(mode)) {
            return 0.90D;
        }
        return 0.99D;
    }

    public static String businessType(String mode) {
        if (AiChatMode.PLATFORM_ASSISTANT.name().equals(mode)) {
            return "PLATFORM_HELP";
        }
        if (AiChatMode.GENERAL.name().equals(mode)) {
            return "GENERAL_CHAT";
        }
        if (AiChatMode.NL2SQL.name().equals(mode)) {
            return "RDB_QUERY";
        }
        if (AiChatMode.DEVICE_CONTROL.name().equals(mode)) {
            return "DEVICE_PROPERTY_CONTROL";
        }
        if (AiChatMode.PROTOCOL_PARSE.name().equals(mode)) {
            return "PROTOCOL_PARSE";
        }
        if (AiChatMode.THING_MODEL_GENERATE.name().equals(mode)) {
            return "THING_MODEL_GENERATE";
        }
        if (AiChatMode.REQUIREMENT_EVALUATION.name().equals(mode)) {
            return "REQUIREMENT_EVALUATION";
        }
        return "UNKNOWN";
    }

    public static String fastReasonMessageCode(String mode) {
        if (AiChatMode.PLATFORM_ASSISTANT.name().equals(mode)) {
            return "ai.chat.route.fast.platform.assistant";
        }
        if (AiChatMode.NL2SQL.name().equals(mode)) {
            return "ai.chat.route.fast.nl2sql";
        }
        if (AiChatMode.DEVICE_CONTROL.name().equals(mode)) {
            return "ai.chat.route.fast.device.control";
        }
        if (AiChatMode.PROTOCOL_PARSE.name().equals(mode)) {
            return "ai.chat.route.fast.protocol.parse";
        }
        if (AiChatMode.THING_MODEL_GENERATE.name().equals(mode)) {
            return "ai.chat.route.fast.thing.model.generate";
        }
        if (AiChatMode.REQUIREMENT_EVALUATION.name().equals(mode)) {
            return "ai.chat.route.fast.requirement.evaluation";
        }
        return "ai.chat.route.fast.general";
    }
}
