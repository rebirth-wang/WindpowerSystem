package com.fastbee.ai.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import com.fastbee.ai.model.enums.AiChatMode;
import com.fastbee.ai.support.AiRouteFeatureExtractor.AiRouteFeatures;

/**
 * AUTO 分层路由决策引擎。
 */
public final class AiAutoRouteDecisionEngine {

    private static final int GENERAL_EXPLICIT_SCORE = 95;
    private static final int TASK_EXPLICIT_SCORE = 92;
    private static final int PLATFORM_EXPLICIT_SCORE = 88;
    private static final int DEFAULT_GENERAL_SCORE = 45;

    private final AiRouteFeatureExtractor featureExtractor = new AiRouteFeatureExtractor();

    public AiAutoRouteDecision decide(AiAutoRouteContext context, Collection<String> platformFeatureTokens) {
        return decide(context, platformFeatureTokens, AiAutoRouteOptions.defaults());
    }

    public AiAutoRouteDecision decide(AiAutoRouteContext context,
                                      Collection<String> platformFeatureTokens,
                                      AiAutoRouteOptions options) {
        AiAutoRouteOptions actualOptions = options == null ? AiAutoRouteOptions.defaults() : options;
        AiRouteFeatures features = featureExtractor.extract(context, platformFeatureTokens);
        List<AiRouteCandidate> candidates = buildCandidates(context, features, actualOptions);
        AiRouteCandidate selected = selectCandidate(context, features, candidates, actualOptions);
        boolean deterministic = selected.getScore() >= actualOptions.getLocalDeterministicScore()
                || features.isGeneralKnowledge();
        boolean requiresModelArbitration = !deterministic && !isHighRiskAccepted(selected, actualOptions);
        return new AiAutoRouteDecision(
                selected.getMode(),
                selected.getTaskType(),
                resolveReason(selected, deterministic),
                selected.getScore() / 100D,
                deterministic,
                requiresModelArbitration,
                candidates
        );
    }

    private List<AiRouteCandidate> buildCandidates(AiAutoRouteContext context,
                                                   AiRouteFeatures features,
                                                   AiAutoRouteOptions options) {
        List<AiRouteCandidate> candidates = new ArrayList<>();
        if (features.isAssistantModelIdentity()) {
            candidates.add(AiRouteCandidate.builder(AiChatMode.GENERAL.name(), "ASSISTANT_MODEL_IDENTITY")
                    .score(GENERAL_EXPLICIT_SCORE)
                    .riskLevel("LOW")
                    .addPositiveSignal("助手自身模型身份问句")
                    .build());
        }
        if (features.isFileReview()) {
            candidates.add(AiRouteCandidate.builder(AiChatMode.GENERAL.name(), "FILE_REVIEW")
                    .score(GENERAL_EXPLICIT_SCORE)
                    .riskLevel("LOW")
                    .addPositiveSignal("文件或附件内容分析")
                    .build());
        }
        if (features.isRequirementEvaluation()) {
            candidates.add(AiRouteCandidate.builder(AiChatMode.REQUIREMENT_EVALUATION.name(), "REQUIREMENT_EVALUATION")
                    .score(TASK_EXPLICIT_SCORE)
                    .riskLevel("MEDIUM")
                    .addPositiveSignal("需求评估任务")
                    .build());
        }
        if (features.isThingModelGenerate()) {
            candidates.add(AiRouteCandidate.builder(AiChatMode.THING_MODEL_GENERATE.name(), "THING_MODEL_GENERATE")
                    .score(TASK_EXPLICIT_SCORE)
                    .riskLevel("MEDIUM")
                    .addPositiveSignal("物模型生成任务")
                    .build());
        }
        if (features.isProtocolParse()) {
            candidates.add(AiRouteCandidate.builder(AiChatMode.PROTOCOL_PARSE.name(), "PROTOCOL_PARSE")
                    .score(TASK_EXPLICIT_SCORE)
                    .riskLevel("MEDIUM")
                    .addPositiveSignal("协议解析任务")
                    .build());
        }
        if (features.isCodebaseGuide()) {
            candidates.add(AiRouteCandidate.builder(AiChatMode.PLATFORM_ASSISTANT.name(), "CODEBASE_GUIDE")
                    .score(PLATFORM_EXPLICIT_SCORE)
                    .riskLevel("MEDIUM")
                    .addPositiveSignal("源码导航或二开咨询")
                    .build());
        }
        if (features.isPlatformConsultation()) {
            candidates.add(AiRouteCandidate.builder(AiChatMode.PLATFORM_ASSISTANT.name(), "PLATFORM_HELP")
                    .score(PLATFORM_EXPLICIT_SCORE)
                    .riskLevel("LOW")
                    .addPositiveSignal("非执行型平台咨询")
                    .build());
        } else if (features.isPlatformDomain()) {
            candidates.add(AiRouteCandidate.builder(AiChatMode.PLATFORM_ASSISTANT.name(), "PLATFORM_DOMAIN")
                    .score(68)
                    .riskLevel("LOW")
                    .addPositiveSignal("具备平台域证据")
                    .build());
        }
        candidates.add(buildDataCandidate(features));
        candidates.add(AiRouteCandidate.fromRouteDecision(features.getDeviceDecision(), "HIGH"));
        if (features.isGeneralKnowledge()) {
            candidates.add(AiRouteCandidate.builder(AiChatMode.GENERAL.name(), "GENERAL_CHAT")
                    .score(GENERAL_EXPLICIT_SCORE)
                    .riskLevel("LOW")
                    .addPositiveSignal("通用知识或生活问答")
                    .build());
        }
        AiRouteCandidate continuationCandidate = buildContextContinuationCandidate(context, features, options);
        if (continuationCandidate != null) {
            candidates.add(continuationCandidate);
        }
        candidates.add(AiRouteCandidate.builder(AiChatMode.GENERAL.name(), "GENERAL_CHAT")
                .score(DEFAULT_GENERAL_SCORE)
                .riskLevel("LOW")
                .addPositiveSignal("默认安全兜底")
                .build());
        candidates.sort(Comparator.comparingInt(AiRouteCandidate::getScore).reversed());
        return candidates;
    }

    private AiRouteCandidate buildDataCandidate(AiRouteFeatures features) {
        AiRouteCandidate baseCandidate = AiRouteCandidate.fromRouteDecision(features.getDataDecision(), "HIGH");
        if (!features.isFileReview()
                || baseCandidate.getBlockers().contains("文件或附件内容分析")) {
            return baseCandidate;
        }
        AiRouteCandidate.Builder builder = AiRouteCandidate.builder(baseCandidate.getMode(), baseCandidate.getTaskType())
                .score(baseCandidate.getScore())
                .riskLevel(baseCandidate.getRiskLevel());
        baseCandidate.getPositiveSignals().forEach(builder::addPositiveSignal);
        baseCandidate.getBlockers().forEach(builder::addBlocker);
        builder.addBlocker("文件或附件内容分析");
        return builder.build();
    }

    private AiRouteCandidate buildContextContinuationCandidate(AiAutoRouteContext context,
                                                               AiRouteFeatures features,
                                                               AiAutoRouteOptions options) {
        if (context == null || !isSupportedPreviousMode(context.getPreviousEffectiveMode())
                || !isContinuationQuestion(context.getNormalizedQuestion())) {
            return null;
        }
        if (features.isRequirementEvaluation() || features.isThingModelGenerate() || features.isProtocolParse()
                || features.isFileReview() || features.isCodebaseGuide() || features.isPlatformConsultation()
                || features.isAssistantModelIdentity() || features.isGeneralKnowledge()
                || features.getDataDecision().isAccepted(options.getDataQueryAcceptScore())
                || features.getDeviceDecision().isAccepted(options.getDeviceControlAcceptScore())) {
            return null;
        }
        return AiRouteCandidate.builder(context.getPreviousEffectiveMode(), "CONTEXT_CONTINUATION")
                .score(82)
                .riskLevel("LOW")
                .addPositiveSignal("多轮短追问继承上一轮模式")
                .build();
    }

    private boolean isSupportedPreviousMode(String previousMode) {
        return AiChatMode.GENERAL.name().equals(previousMode)
                || AiChatMode.PLATFORM_ASSISTANT.name().equals(previousMode)
                || AiChatMode.NL2SQL.name().equals(previousMode)
                || AiChatMode.DEVICE_CONTROL.name().equals(previousMode)
                || AiChatMode.PROTOCOL_PARSE.name().equals(previousMode)
                || AiChatMode.THING_MODEL_GENERATE.name().equals(previousMode)
                || AiChatMode.REQUIREMENT_EVALUATION.name().equals(previousMode);
    }

    private boolean isContinuationQuestion(String question) {
        if (question == null || question.isBlank()) {
            return false;
        }
        return question.length() <= 18
                || AiIntentRoutePolicy.containsAny(question, "那", "继续", "还有", "这个", "该", "它", "呢", "再查",
                "再看", "最近一小时", "最近1小时", "上一条", "刚才", "继续说", "继续分析",
                "continue", "that", "it", "more", "again", "lasthour");
    }

    private AiRouteCandidate selectCandidate(AiAutoRouteContext context,
                                             AiRouteFeatures features,
                                             List<AiRouteCandidate> candidates,
                                             AiAutoRouteOptions options) {
        if (features.isAssistantModelIdentity()) {
            return firstByTaskType(candidates, "ASSISTANT_MODEL_IDENTITY");
        }
        if (features.isRequirementEvaluation()) {
            return firstByTaskType(candidates, "REQUIREMENT_EVALUATION");
        }
        if (features.isThingModelGenerate()) {
            return firstByTaskType(candidates, "THING_MODEL_GENERATE");
        }
        if (features.isFileReview()) {
            return firstByTaskType(candidates, "FILE_REVIEW");
        }
        if (features.getDeviceDecision().isAccepted(options.getDeviceControlAcceptScore())) {
            return firstByMode(candidates, AiChatMode.DEVICE_CONTROL.name());
        }
        if (features.isCodebaseGuide()) {
            return firstByTaskType(candidates, "CODEBASE_GUIDE");
        }
        if (features.isPlatformConsultation()) {
            return firstByTaskType(candidates, "PLATFORM_HELP");
        }
        if (features.getDataDecision().isAccepted(options.getDataQueryAcceptScore())) {
            return firstByMode(candidates, AiChatMode.NL2SQL.name());
        }
        if (features.isProtocolParse()) {
            return firstByTaskType(candidates, "PROTOCOL_PARSE");
        }
        if (context != null && isContinuationQuestion(context.getNormalizedQuestion())) {
            AiRouteCandidate continuation = firstNullableByTaskType(candidates, "CONTEXT_CONTINUATION");
            if (continuation != null) {
                return continuation;
            }
        }
        if (features.isGeneralKnowledge()) {
            return firstByTaskType(candidates, "GENERAL_CHAT");
        }
        return firstByMode(candidates, AiChatMode.GENERAL.name());
    }

    private AiRouteCandidate firstByTaskType(List<AiRouteCandidate> candidates, String taskType) {
        return candidates.stream()
                .filter(candidate -> taskType.equals(candidate.getTaskType()))
                .findFirst()
                .orElseGet(() -> firstByMode(candidates, AiChatMode.GENERAL.name()));
    }

    private AiRouteCandidate firstNullableByTaskType(List<AiRouteCandidate> candidates, String taskType) {
        return candidates.stream()
                .filter(candidate -> taskType.equals(candidate.getTaskType()))
                .findFirst()
                .orElse(null);
    }

    private AiRouteCandidate firstByMode(List<AiRouteCandidate> candidates, String mode) {
        return candidates.stream()
                .filter(candidate -> mode.equals(candidate.getMode()))
                .findFirst()
                .orElse(candidates.get(candidates.size() - 1));
    }

    private boolean isHighRiskAccepted(AiRouteCandidate candidate, AiAutoRouteOptions options) {
        return candidate != null
                && "HIGH".equals(candidate.getRiskLevel())
                && candidate.getBlockers().isEmpty()
                && candidate.getScore() >= options.getHighRiskAcceptScore();
    }

    private String resolveReason(AiRouteCandidate selected, boolean deterministic) {
        if (selected == null) {
            return "AUTO 分层路由默认兜底";
        }
        if (deterministic) {
            return "AUTO 分层路由命中：" + selected.getTaskType();
        }
        return "AUTO 分层路由低置信兜底：" + selected.getTaskType();
    }
}
