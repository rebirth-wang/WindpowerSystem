package com.fastbee.ai.support;

/**
 * AI 会话观测常量。
 */
public final class AiChatObservabilityConstants {

    /**
     * 结果面板“纠偏重试”交互来源。
     */
    public static final String INTERACTION_SOURCE_MODE_CORRECTION_RETRY = "MODE_CORRECTION_RETRY";

    /**
     * 手动模式来源：会话锁定。
     */
    public static final String MANUAL_MODE_SOURCE_PINNED = "PINNED";

    /**
     * 手动模式来源：本轮指定。
     */
    public static final String MANUAL_MODE_SOURCE_OVERRIDE = "OVERRIDE";

    /**
     * 手动模式来源：旧端或其他直接指定。
     */
    public static final String MANUAL_MODE_SOURCE_DIRECT = "DIRECT";

    private AiChatObservabilityConstants() {
    }
}
