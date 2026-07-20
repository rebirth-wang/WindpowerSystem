package com.fastbee.ai.support;

import com.fastbee.common.utils.StringUtils;

/**
 * AUTO 分层路由的输入上下文。
 */
public final class AiAutoRouteContext {

    private final String question;
    private final String normalizedQuestion;
    private final String requestedMode;
    private final boolean hasAttachment;
    private final String attachmentFileName;
    private final String previousEffectiveMode;
    private final String previousTaskType;
    private final boolean retryRequest;

    private AiAutoRouteContext(Builder builder) {
        this.question = StringUtils.defaultString(builder.question);
        this.normalizedQuestion = AiIntentRoutePolicy.normalizeQuestion(builder.question);
        this.requestedMode = builder.requestedMode;
        this.hasAttachment = builder.hasAttachment;
        this.attachmentFileName = builder.attachmentFileName;
        this.previousEffectiveMode = builder.previousEffectiveMode;
        this.previousTaskType = builder.previousTaskType;
        this.retryRequest = builder.retryRequest;
    }

    public static Builder builder(String question) {
        return new Builder(question);
    }

    public String getQuestion() {
        return question;
    }

    public String getNormalizedQuestion() {
        return normalizedQuestion;
    }

    public String getRequestedMode() {
        return requestedMode;
    }

    public boolean hasAttachment() {
        return hasAttachment;
    }

    public String getAttachmentFileName() {
        return attachmentFileName;
    }

    public String getPreviousEffectiveMode() {
        return previousEffectiveMode;
    }

    public String getPreviousTaskType() {
        return previousTaskType;
    }

    public boolean isRetryRequest() {
        return retryRequest;
    }

    public static final class Builder {

        private final String question;
        private String requestedMode;
        private boolean hasAttachment;
        private String attachmentFileName;
        private String previousEffectiveMode;
        private String previousTaskType;
        private boolean retryRequest;

        private Builder(String question) {
            this.question = question;
        }

        public Builder requestedMode(String requestedMode) {
            this.requestedMode = requestedMode;
            return this;
        }

        public Builder hasAttachment(boolean hasAttachment) {
            this.hasAttachment = hasAttachment;
            return this;
        }

        public Builder attachmentFileName(String attachmentFileName) {
            this.attachmentFileName = attachmentFileName;
            return this;
        }

        public Builder previousEffectiveMode(String previousEffectiveMode) {
            this.previousEffectiveMode = previousEffectiveMode;
            return this;
        }

        public Builder previousTaskType(String previousTaskType) {
            this.previousTaskType = previousTaskType;
            return this;
        }

        public Builder retryRequest(boolean retryRequest) {
            this.retryRequest = retryRequest;
            return this;
        }

        public AiAutoRouteContext build() {
            return new AiAutoRouteContext(this);
        }
    }
}
