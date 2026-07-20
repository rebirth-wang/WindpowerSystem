package com.fastbee.ai.support;

/**
 * AUTO 分层路由可调策略项。
 */
public final class AiAutoRouteOptions {

    private static final int DEFAULT_LOCAL_DETERMINISTIC_SCORE = 70;
    private static final int DEFAULT_HIGH_RISK_ACCEPT_SCORE = 70;
    private static final int DEFAULT_DATA_QUERY_ACCEPT_SCORE = 70;
    private static final int DEFAULT_DEVICE_CONTROL_ACCEPT_SCORE = 70;

    private final int localDeterministicScore;
    private final int highRiskAcceptScore;
    private final int dataQueryAcceptScore;
    private final int deviceControlAcceptScore;

    private AiAutoRouteOptions(Builder builder) {
        this.localDeterministicScore = normalizeScore(builder.localDeterministicScore, DEFAULT_LOCAL_DETERMINISTIC_SCORE);
        this.highRiskAcceptScore = normalizeScore(builder.highRiskAcceptScore, DEFAULT_HIGH_RISK_ACCEPT_SCORE);
        this.dataQueryAcceptScore = normalizeScore(builder.dataQueryAcceptScore, DEFAULT_DATA_QUERY_ACCEPT_SCORE);
        this.deviceControlAcceptScore = normalizeScore(builder.deviceControlAcceptScore, DEFAULT_DEVICE_CONTROL_ACCEPT_SCORE);
    }

    public static AiAutoRouteOptions defaults() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getLocalDeterministicScore() {
        return localDeterministicScore;
    }

    public int getHighRiskAcceptScore() {
        return highRiskAcceptScore;
    }

    public int getDataQueryAcceptScore() {
        return dataQueryAcceptScore;
    }

    public int getDeviceControlAcceptScore() {
        return deviceControlAcceptScore;
    }

    private static int normalizeScore(Integer value, int defaultValue) {
        int score = value == null ? defaultValue : value;
        return Math.max(0, Math.min(100, score));
    }

    public static final class Builder {

        private Integer localDeterministicScore;
        private Integer highRiskAcceptScore;
        private Integer dataQueryAcceptScore;
        private Integer deviceControlAcceptScore;

        private Builder() {
        }

        public Builder localDeterministicScore(Integer localDeterministicScore) {
            this.localDeterministicScore = localDeterministicScore;
            return this;
        }

        public Builder highRiskAcceptScore(Integer highRiskAcceptScore) {
            this.highRiskAcceptScore = highRiskAcceptScore;
            return this;
        }

        public Builder dataQueryAcceptScore(Integer dataQueryAcceptScore) {
            this.dataQueryAcceptScore = dataQueryAcceptScore;
            return this;
        }

        public Builder deviceControlAcceptScore(Integer deviceControlAcceptScore) {
            this.deviceControlAcceptScore = deviceControlAcceptScore;
            return this;
        }

        public AiAutoRouteOptions build() {
            return new AiAutoRouteOptions(this);
        }
    }
}
