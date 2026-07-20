package com.fastbee.ai.support;

import java.util.Collection;

import com.fastbee.ai.model.enums.AiChatMode;
import com.fastbee.ai.model.vo.AiChatIntentRouteVO;
import com.fastbee.common.utils.StringUtils;

/**
 * AUTO 路由模型建议的采纳与回退策略。
 */
public final class AiAutoRouteAdoptionPolicy {

    private static final AiRouteFeatureExtractor FEATURE_EXTRACTOR = new AiRouteFeatureExtractor();

    private AiAutoRouteAdoptionPolicy() {
    }

    public static Decision decide(String question,
                                  AiChatIntentRouteVO intentRouteResult,
                                  AiAutoRouteDecision localDecision,
                                  Collection<String> platformFeatureTokens,
                                  Options options) {
        Options actualOptions = options == null ? Options.defaults() : options;
        String ruleMode = resolveRuleMode(localDecision);
        if (shouldForceNl2SqlForDeviceRuntimeRead(question, intentRouteResult, ruleMode)) {
            return Decision.fallback(AiChatMode.NL2SQL.name(), FallbackReason.DEVICE_RUNTIME_READ);
        }
        if (shouldFallbackSuspiciousDeviceControl(question, intentRouteResult, ruleMode)) {
            return Decision.fallback(ruleMode, FallbackReason.SUSPICIOUS_DEVICE_CONTROL);
        }
        if (shouldFallbackSuspiciousNl2SqlLocationQuestion(question, intentRouteResult, ruleMode)) {
            return Decision.fallback(ruleMode, FallbackReason.NL2SQL_LOCATION_QUESTION);
        }
        if (shouldFallbackSuspiciousNl2SqlGeneralQuestion(question, intentRouteResult, ruleMode)) {
            return Decision.fallback(ruleMode, FallbackReason.LOCAL_RULE);
        }
        if (shouldFallbackSuspiciousPlatformAssistantGeneralQuestion(question, intentRouteResult, ruleMode, platformFeatureTokens)) {
            return Decision.fallback(ruleMode, FallbackReason.LOCAL_RULE);
        }
        if (shouldPreferLayeredLocalDecision(localDecision, intentRouteResult)) {
            return Decision.fallback(ruleMode, FallbackReason.LOCAL_RULE);
        }
        RouteValidation validation = validateIntentRoute(intentRouteResult, actualOptions);
        if (!validation.isAdoptable()) {
            return Decision.fallback(ruleMode, validation.getFallbackReason(),
                    validation.getFallbackDetail(), validation.getEvaluatedMode(),
                    validation.getConfidence(), validation.getThreshold());
        }
        return Decision.adopt(intentRouteResult.getMode());
    }

    private static String resolveRuleMode(AiAutoRouteDecision localDecision) {
        if (localDecision == null || StringUtils.isBlank(localDecision.getFinalMode())) {
            return AiChatMode.GENERAL.name();
        }
        return localDecision.getFinalMode();
    }

    private static boolean shouldPreferLayeredLocalDecision(AiAutoRouteDecision localDecision,
                                                            AiChatIntentRouteVO intentRouteResult) {
        if (localDecision == null || !localDecision.isDeterministic()
                || localDecision.isRequiresModelArbitration()
                || intentRouteResult == null
                || StringUtils.isBlank(intentRouteResult.getMode())
                || StringUtils.equals(localDecision.getFinalMode(), intentRouteResult.getMode())) {
            return false;
        }
        return !AiChatMode.AUTO.name().equals(intentRouteResult.getMode());
    }

    private static boolean shouldForceNl2SqlForDeviceRuntimeRead(String question,
                                                                 AiChatIntentRouteVO intentRouteResult,
                                                                 String ruleMode) {
        String normalizedQuestion = AiIntentRoutePolicy.normalizeQuestion(question);
        if (AiChatMode.PLATFORM_ASSISTANT.name().equals(ruleMode)
                && AiIntentRoutePolicy.isNonExecutionPlatformConsultation(normalizedQuestion)) {
            return false;
        }
        if (intentRouteResult != null && "DEVICE_RUNTIME_QUERY".equalsIgnoreCase(intentRouteResult.getBusinessType())) {
            return true;
        }
        boolean hasResolvedDeviceOrProduct = intentRouteResult != null && (
                StringUtils.isNotBlank(intentRouteResult.getDeviceNameText())
                        || StringUtils.isNotBlank(intentRouteResult.getSerialNumberText())
                        || StringUtils.isNotBlank(intentRouteResult.getProductNameText())
        );
        boolean hasResolvedMetric = intentRouteResult != null && StringUtils.isNotBlank(intentRouteResult.getThingModelText());
        return AiIntentRoutePolicy.isDeviceRuntimeReadQuestion(normalizedQuestion, hasResolvedDeviceOrProduct, hasResolvedMetric);
    }

    private static boolean shouldFallbackSuspiciousNl2SqlLocationQuestion(String question,
                                                                          AiChatIntentRouteVO intentRouteResult,
                                                                          String ruleMode) {
        if (intentRouteResult == null
                || !AiChatMode.NL2SQL.name().equals(intentRouteResult.getMode())
                || !AiChatMode.PLATFORM_ASSISTANT.name().equals(ruleMode)) {
            return false;
        }
        String normalizedQuestion = AiIntentRoutePolicy.normalizeQuestion(question);
        return AiIntentRoutePolicy.isDeviceControlLocationQuestion(normalizedQuestion)
                || FEATURE_EXTRACTOR.isCodebaseGuideQuestion(normalizedQuestion)
                || AiIntentRoutePolicy.isNonExecutionPlatformConsultation(normalizedQuestion);
    }

    private static boolean shouldFallbackSuspiciousNl2SqlGeneralQuestion(String question,
                                                                         AiChatIntentRouteVO intentRouteResult,
                                                                         String ruleMode) {
        if (intentRouteResult == null
                || !AiChatMode.NL2SQL.name().equals(intentRouteResult.getMode())
                || !AiChatMode.GENERAL.name().equals(ruleMode)) {
            return false;
        }
        String normalizedQuestion = AiIntentRoutePolicy.normalizeQuestion(question);
        return AiIntentRoutePolicy.isFileContentAnalysisQuestion(normalizedQuestion)
                || FEATURE_EXTRACTOR.isGeneralQuestion(normalizedQuestion, null);
    }

    private static boolean shouldFallbackSuspiciousPlatformAssistantGeneralQuestion(String question,
                                                                                   AiChatIntentRouteVO intentRouteResult,
                                                                                   String ruleMode,
                                                                                   Collection<String> platformFeatureTokens) {
        if (intentRouteResult == null
                || !AiChatMode.PLATFORM_ASSISTANT.name().equals(intentRouteResult.getMode())
                || !AiChatMode.GENERAL.name().equals(ruleMode)) {
            return false;
        }
        return FEATURE_EXTRACTOR.isGeneralQuestion(AiIntentRoutePolicy.normalizeQuestion(question), platformFeatureTokens);
    }

    private static boolean shouldFallbackSuspiciousDeviceControl(String question,
                                                                 AiChatIntentRouteVO intentRouteResult,
                                                                 String ruleMode) {
        if (intentRouteResult == null
                || !AiChatMode.DEVICE_CONTROL.name().equals(intentRouteResult.getMode())
                || AiChatMode.DEVICE_CONTROL.name().equals(ruleMode)) {
            return false;
        }
        return !isExplicitDeviceControlQuestion(question);
    }

    private static boolean isExplicitDeviceControlQuestion(String question) {
        if (StringUtils.isBlank(question)) {
            return false;
        }
        String normalizedQuestion = AiIntentRoutePolicy.normalizeQuestion(question);
        if (AiIntentRoutePolicy.isDeviceControlExplainQuestion(normalizedQuestion)
                || AiIntentRoutePolicy.isDeviceControlLocationQuestion(normalizedQuestion)
                || AiIntentRoutePolicy.isDeviceCommandMechanismQuestion(normalizedQuestion)) {
            return false;
        }
        return AiIntentRoutePolicy.isDeviceControlExecutionQuestion(normalizedQuestion);
    }

    private static RouteValidation validateIntentRoute(AiChatIntentRouteVO intentRouteResult, Options options) {
        if (intentRouteResult == null) {
            return RouteValidation.rejected(FallbackReason.NO_RESULT);
        }
        if (!"SUCCESS".equalsIgnoreCase(intentRouteResult.getParseStatus())) {
            return RouteValidation.rejected(FallbackReason.PARSE_FAILED,
                    intentRouteResult.getParseErrorMessage(), intentRouteResult.getMode(), null, 0D);
        }
        if (StringUtils.isBlank(intentRouteResult.getMode()) || AiChatMode.AUTO.name().equals(intentRouteResult.getMode())) {
            return RouteValidation.rejected(FallbackReason.NO_TARGET_MODE);
        }
        Double confidence = intentRouteResult.getModeConfidence();
        double threshold = options.resolveThreshold(intentRouteResult.getMode());
        if (confidence == null && options.requiresStrictConfidence(intentRouteResult.getMode())) {
            return RouteValidation.rejected(FallbackReason.NO_CONFIDENCE,
                    null, intentRouteResult.getMode(), null, threshold);
        }
        if (confidence != null && confidence < threshold) {
            return RouteValidation.rejected(FallbackReason.LOW_CONFIDENCE,
                    null, intentRouteResult.getMode(), confidence, threshold);
        }
        return RouteValidation.accepted();
    }

    public enum FallbackReason {
        NONE,
        SUSPICIOUS_DEVICE_CONTROL,
        NL2SQL_LOCATION_QUESTION,
        LOCAL_RULE,
        DEVICE_RUNTIME_READ,
        NO_RESULT,
        PARSE_FAILED,
        NO_TARGET_MODE,
        NO_CONFIDENCE,
        LOW_CONFIDENCE
    }

    public static final class Decision {

        private final String finalMode;
        private final boolean adoptedBySystem;
        private final FallbackReason fallbackReason;
        private final String fallbackDetail;
        private final String evaluatedMode;
        private final Double confidence;
        private final double threshold;

        private Decision(String finalMode,
                         boolean adoptedBySystem,
                         FallbackReason fallbackReason,
                         String fallbackDetail,
                         String evaluatedMode,
                         Double confidence,
                         double threshold) {
            this.finalMode = StringUtils.defaultIfBlank(finalMode, AiChatMode.GENERAL.name());
            this.adoptedBySystem = adoptedBySystem;
            this.fallbackReason = fallbackReason == null ? FallbackReason.NONE : fallbackReason;
            this.fallbackDetail = fallbackDetail;
            this.evaluatedMode = evaluatedMode;
            this.confidence = confidence;
            this.threshold = threshold;
        }

        private static Decision adopt(String finalMode) {
            return new Decision(finalMode, true, FallbackReason.NONE, null, finalMode, null, 0D);
        }

        private static Decision fallback(String finalMode, FallbackReason fallbackReason) {
            return fallback(finalMode, fallbackReason, null, null, null, 0D);
        }

        private static Decision fallback(String finalMode,
                                         FallbackReason fallbackReason,
                                         String fallbackDetail,
                                         String evaluatedMode,
                                         Double confidence,
                                         double threshold) {
            return new Decision(finalMode, false, fallbackReason, fallbackDetail, evaluatedMode, confidence, threshold);
        }

        public String getFinalMode() {
            return finalMode;
        }

        public boolean isAdoptedBySystem() {
            return adoptedBySystem;
        }

        public FallbackReason getFallbackReason() {
            return fallbackReason;
        }

        public String getFallbackDetail() {
            return fallbackDetail;
        }

        public String getEvaluatedMode() {
            return evaluatedMode;
        }

        public Double getConfidence() {
            return confidence;
        }

        public double getThreshold() {
            return threshold;
        }
    }

    public static final class Options {

        public static final double DEFAULT_CONFIDENCE_THRESHOLD = 0.70D;
        public static final double DEFAULT_GENERAL_THRESHOLD = 0.60D;
        public static final double DEFAULT_PLATFORM_ASSISTANT_THRESHOLD = 0.65D;
        public static final double DEFAULT_NL2SQL_THRESHOLD = 0.78D;
        public static final double DEFAULT_DEVICE_CONTROL_THRESHOLD = 0.86D;
        public static final double DEFAULT_PROTOCOL_PARSE_THRESHOLD = 0.72D;
        public static final double DEFAULT_THING_MODEL_GENERATE_THRESHOLD = 0.76D;
        public static final double DEFAULT_REQUIREMENT_EVALUATION_THRESHOLD = 0.74D;

        private final double defaultThreshold;
        private final double generalThreshold;
        private final double platformAssistantThreshold;
        private final double nl2sqlThreshold;
        private final double deviceControlThreshold;
        private final double protocolParseThreshold;
        private final double thingModelGenerateThreshold;
        private final double requirementEvaluationThreshold;

        public Options(double defaultThreshold,
                       double generalThreshold,
                       double platformAssistantThreshold,
                       double nl2sqlThreshold,
                       double deviceControlThreshold,
                       double protocolParseThreshold,
                       double thingModelGenerateThreshold,
                       double requirementEvaluationThreshold) {
            this.defaultThreshold = defaultThreshold;
            this.generalThreshold = generalThreshold;
            this.platformAssistantThreshold = platformAssistantThreshold;
            this.nl2sqlThreshold = nl2sqlThreshold;
            this.deviceControlThreshold = deviceControlThreshold;
            this.protocolParseThreshold = protocolParseThreshold;
            this.thingModelGenerateThreshold = thingModelGenerateThreshold;
            this.requirementEvaluationThreshold = requirementEvaluationThreshold;
        }

        public static Options defaults() {
            return new Options(
                    DEFAULT_CONFIDENCE_THRESHOLD,
                    DEFAULT_GENERAL_THRESHOLD,
                    DEFAULT_PLATFORM_ASSISTANT_THRESHOLD,
                    DEFAULT_NL2SQL_THRESHOLD,
                    DEFAULT_DEVICE_CONTROL_THRESHOLD,
                    DEFAULT_PROTOCOL_PARSE_THRESHOLD,
                    DEFAULT_THING_MODEL_GENERATE_THRESHOLD,
                    DEFAULT_REQUIREMENT_EVALUATION_THRESHOLD
            );
        }

        public boolean requiresStrictConfidence(String mode) {
            return AiChatMode.NL2SQL.name().equals(mode) || AiChatMode.DEVICE_CONTROL.name().equals(mode);
        }

        public double resolveThreshold(String mode) {
            if (AiChatMode.GENERAL.name().equals(mode)) {
                return generalThreshold;
            }
            if (AiChatMode.PLATFORM_ASSISTANT.name().equals(mode)) {
                return platformAssistantThreshold;
            }
            if (AiChatMode.NL2SQL.name().equals(mode)) {
                return nl2sqlThreshold;
            }
            if (AiChatMode.DEVICE_CONTROL.name().equals(mode)) {
                return deviceControlThreshold;
            }
            if (AiChatMode.PROTOCOL_PARSE.name().equals(mode)) {
                return protocolParseThreshold;
            }
            if (AiChatMode.THING_MODEL_GENERATE.name().equals(mode)) {
                return thingModelGenerateThreshold;
            }
            if (AiChatMode.REQUIREMENT_EVALUATION.name().equals(mode)) {
                return requirementEvaluationThreshold;
            }
            return defaultThreshold;
        }

        public double getGeneralThreshold() {
            return generalThreshold;
        }
    }

    private static final class RouteValidation {

        private final boolean adoptable;
        private final FallbackReason fallbackReason;
        private final String fallbackDetail;
        private final String evaluatedMode;
        private final Double confidence;
        private final double threshold;

        private RouteValidation(boolean adoptable,
                                FallbackReason fallbackReason,
                                String fallbackDetail,
                                String evaluatedMode,
                                Double confidence,
                                double threshold) {
            this.adoptable = adoptable;
            this.fallbackReason = fallbackReason;
            this.fallbackDetail = fallbackDetail;
            this.evaluatedMode = evaluatedMode;
            this.confidence = confidence;
            this.threshold = threshold;
        }

        private static RouteValidation accepted() {
            return new RouteValidation(true, FallbackReason.NONE, null, null, null, 0D);
        }

        private static RouteValidation rejected(FallbackReason fallbackReason) {
            return rejected(fallbackReason, null, null, null, 0D);
        }

        private static RouteValidation rejected(FallbackReason fallbackReason,
                                                String fallbackDetail,
                                                String evaluatedMode,
                                                Double confidence,
                                                double threshold) {
            return new RouteValidation(false, fallbackReason, fallbackDetail, evaluatedMode, confidence, threshold);
        }

        private boolean isAdoptable() {
            return adoptable;
        }

        private FallbackReason getFallbackReason() {
            return fallbackReason;
        }

        private String getFallbackDetail() {
            return fallbackDetail;
        }

        private String getEvaluatedMode() {
            return evaluatedMode;
        }

        private Double getConfidence() {
            return confidence;
        }

        private double getThreshold() {
            return threshold;
        }
    }
}
