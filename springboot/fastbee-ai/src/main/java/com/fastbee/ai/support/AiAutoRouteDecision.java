package com.fastbee.ai.support;

import java.util.List;

/**
 * AUTO 分层路由的最终裁决。
 */
public final class AiAutoRouteDecision {

    private final String finalMode;
    private final String taskType;
    private final String reason;
    private final double confidence;
    private final boolean deterministic;
    private final boolean requiresModelArbitration;
    private final List<AiRouteCandidate> candidates;

    public AiAutoRouteDecision(String finalMode,
                               String taskType,
                               String reason,
                               double confidence,
                               boolean deterministic,
                               boolean requiresModelArbitration,
                               List<AiRouteCandidate> candidates) {
        this.finalMode = finalMode;
        this.taskType = taskType;
        this.reason = reason;
        this.confidence = confidence;
        this.deterministic = deterministic;
        this.requiresModelArbitration = requiresModelArbitration;
        this.candidates = candidates == null ? List.of() : List.copyOf(candidates);
    }

    public String getFinalMode() {
        return finalMode;
    }

    public String getTaskType() {
        return taskType;
    }

    public String getReason() {
        return reason;
    }

    public double getConfidence() {
        return confidence;
    }

    public boolean isDeterministic() {
        return deterministic;
    }

    public boolean isRequiresModelArbitration() {
        return requiresModelArbitration;
    }

    public List<AiRouteCandidate> getCandidates() {
        return candidates;
    }
}
