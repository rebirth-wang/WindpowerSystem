package com.fastbee.ai.support;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fastbee.common.utils.StringUtils;

/**
 * AUTO 分层路由的单个候选模式。
 */
public final class AiRouteCandidate {

    private final String mode;
    private final String taskType;
    private final int score;
    private final String riskLevel;
    private final List<String> positiveSignals;
    private final List<String> blockers;

    private AiRouteCandidate(Builder builder) {
        this.mode = builder.mode;
        this.taskType = builder.taskType;
        this.score = Math.max(0, Math.min(100, builder.score));
        this.riskLevel = StringUtils.defaultIfBlank(builder.riskLevel, "LOW");
        this.positiveSignals = List.copyOf(builder.positiveSignals);
        this.blockers = List.copyOf(builder.blockers);
    }

    public static Builder builder(String mode, String taskType) {
        return new Builder(mode, taskType);
    }

    public static AiRouteCandidate fromRouteDecision(AiRouteDecision decision, String riskLevel) {
        Builder builder = builder(decision.getModeCandidate(), decision.getIntentType())
                .score(decision.getScore())
                .riskLevel(riskLevel);
        decision.getPositiveSignals().forEach(builder::addPositiveSignal);
        decision.getBlockers().forEach(builder::addBlocker);
        return builder.build();
    }

    public String getMode() {
        return mode;
    }

    public String getTaskType() {
        return taskType;
    }

    public int getScore() {
        return score;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public List<String> getPositiveSignals() {
        return positiveSignals;
    }

    public List<String> getBlockers() {
        return blockers;
    }

    public boolean isAccepted(int threshold) {
        return score >= threshold && blockers.isEmpty();
    }

    public Map<String, Object> toAuditMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("mode", mode);
        map.put("taskType", taskType);
        map.put("score", score);
        map.put("riskLevel", riskLevel);
        map.put("positiveSignals", positiveSignals);
        map.put("blockers", blockers);
        return map;
    }

    public static final class Builder {

        private final String mode;
        private final String taskType;
        private int score;
        private String riskLevel;
        private final List<String> positiveSignals = new ArrayList<>();
        private final List<String> blockers = new ArrayList<>();

        private Builder(String mode, String taskType) {
            this.mode = mode;
            this.taskType = taskType;
        }

        public Builder score(int score) {
            this.score = score;
            return this;
        }

        public Builder addScore(int score, String signal) {
            this.score += score;
            addPositiveSignal(signal);
            return this;
        }

        public Builder riskLevel(String riskLevel) {
            this.riskLevel = riskLevel;
            return this;
        }

        public Builder addPositiveSignal(String signal) {
            if (StringUtils.isNotBlank(signal)) {
                this.positiveSignals.add(signal);
            }
            return this;
        }

        public Builder addBlocker(String blocker) {
            if (StringUtils.isNotBlank(blocker)) {
                this.blockers.add(blocker);
            }
            return this;
        }

        public AiRouteCandidate build() {
            return new AiRouteCandidate(this);
        }
    }
}
