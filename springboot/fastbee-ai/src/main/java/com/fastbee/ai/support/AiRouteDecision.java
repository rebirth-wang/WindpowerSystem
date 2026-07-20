package com.fastbee.ai.support;

import java.util.ArrayList;
import java.util.List;

/**
 * AI 会话本地路由策略的可解释决策结果。
 */
public final class AiRouteDecision {

    private final String modeCandidate;
    private final String intentType;
    private final int score;
    private final List<String> positiveSignals;
    private final List<String> blockers;

    private AiRouteDecision(Builder builder) {
        this.modeCandidate = builder.modeCandidate;
        this.intentType = builder.intentType;
        this.score = Math.max(0, Math.min(100, builder.score));
        this.positiveSignals = List.copyOf(builder.positiveSignals);
        this.blockers = List.copyOf(builder.blockers);
    }

    public static Builder builder(String modeCandidate, String intentType) {
        return new Builder(modeCandidate, intentType);
    }

    public String getModeCandidate() {
        return modeCandidate;
    }

    public String getIntentType() {
        return intentType;
    }

    public int getScore() {
        return score;
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

    public static final class Builder {

        private final String modeCandidate;
        private String intentType;
        private int score;
        private final List<String> positiveSignals = new ArrayList<>();
        private final List<String> blockers = new ArrayList<>();

        private Builder(String modeCandidate, String intentType) {
            this.modeCandidate = modeCandidate;
            this.intentType = intentType;
        }

        public Builder intentType(String intentType) {
            this.intentType = intentType;
            return this;
        }

        public Builder addScore(int value, String signal) {
            this.score += value;
            if (signal != null && !signal.isBlank()) {
                this.positiveSignals.add(signal);
            }
            return this;
        }

        public Builder capScore(int maxScore) {
            this.score = Math.min(this.score, maxScore);
            return this;
        }

        public Builder addBlocker(String blocker) {
            if (blocker != null && !blocker.isBlank()) {
                this.blockers.add(blocker);
            }
            return this;
        }

        public AiRouteDecision build() {
            return new AiRouteDecision(this);
        }
    }
}
