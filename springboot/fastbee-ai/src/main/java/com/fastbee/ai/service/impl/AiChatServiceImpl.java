package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import com.fastbee.ai.config.properties.FastBeeAiProperties;
import com.fastbee.ai.constant.AiPromptConstant;
import com.fastbee.ai.domain.AiChatMessage;
import com.fastbee.ai.domain.AiChatSession;
import com.fastbee.ai.domain.AiProtocolAdaptationTask;
import com.fastbee.ai.model.dto.AiChatSendRequest;
import com.fastbee.ai.model.dto.AiNl2SqlQueryRequest;
import com.fastbee.ai.model.enums.AiChatMode;
import com.fastbee.ai.model.runtime.AiRuntimeModelSnapshot;
import com.fastbee.ai.model.vo.AiChatIntentRouteVO;
import com.fastbee.ai.model.vo.AiChatSendResponseVO;
import com.fastbee.ai.model.vo.AiChatStreamEventVO;
import com.fastbee.ai.model.vo.AiClarifyOptionVO;
import com.fastbee.ai.model.vo.AiClarifyPayloadVO;
import com.fastbee.ai.model.vo.AiCodebaseGuideContextVO;
import com.fastbee.ai.model.vo.AiCodebaseGuideItemVO;
import com.fastbee.ai.model.vo.AiConversationContextBundleVO;
import com.fastbee.ai.model.vo.AiConversationGlobalContextVO;
import com.fastbee.ai.model.vo.AiDeviceControlContextSnapshotVO;
import com.fastbee.ai.model.vo.AiDeviceControlIntentVO;
import com.fastbee.ai.model.vo.AiHybridQueryResultVO;
import com.fastbee.ai.model.vo.AiModelRouteVO;
import com.fastbee.ai.model.vo.AiNl2SqlContextSnapshotVO;
import com.fastbee.ai.model.vo.AiNl2SqlGenerateResultVO;
import com.fastbee.ai.model.vo.AiNl2SqlQueryResultVO;
import com.fastbee.ai.model.vo.AiPlatformAssistantContextSnapshotVO;
import com.fastbee.ai.model.vo.AiPlatformDocKnowledgeContextVO;
import com.fastbee.ai.model.vo.AiProtocolAdaptationArtifactVO;
import com.fastbee.ai.model.vo.AiProtocolAdaptationAutoRunResultVO;
import com.fastbee.ai.model.vo.AiProtocolAdaptationTaskVO;
import com.fastbee.ai.model.vo.AiProtocolGenerationRecordVO;
import com.fastbee.ai.model.vo.AiProtocolKnowledgeContextVO;
import com.fastbee.ai.model.vo.AiProtocolParseContextSnapshotVO;
import com.fastbee.ai.model.vo.AiQueryPlanVO;
import com.fastbee.ai.model.vo.AiRedisRealtimeQueryResultVO;
import com.fastbee.ai.model.vo.AiRequirementEvaluationResultVO;
import com.fastbee.ai.model.vo.AiSemanticContextVO;
import com.fastbee.ai.model.vo.AiSemanticFieldVO;
import com.fastbee.ai.model.vo.AiThingModelGenerateResultVO;
import com.fastbee.ai.model.vo.AiTsdbHistoryPointVO;
import com.fastbee.ai.model.vo.AiTsdbQueryResultVO;
import com.fastbee.ai.service.AiChatModelFactoryService;
import com.fastbee.ai.service.AiChatService;
import com.fastbee.ai.service.AiModelRoutingService;
import com.fastbee.ai.service.AiRuntimeModelSnapshotService;
import com.fastbee.ai.service.IAiChatIntentRouteService;
import com.fastbee.ai.service.IAiChatMessageService;
import com.fastbee.ai.service.IAiChatSessionService;
import com.fastbee.ai.service.IAiClarifySupportService;
import com.fastbee.ai.service.IAiCodebaseGuideKnowledgeService;
import com.fastbee.ai.service.IAiConversationContextAssembler;
import com.fastbee.ai.service.IAiDeviceControlConfirmService;
import com.fastbee.ai.service.IAiDeviceControlConversationContextService;
import com.fastbee.ai.service.IAiDeviceControlIntentService;
import com.fastbee.ai.service.IAiDeviceResolveService;
import com.fastbee.ai.service.IAiNl2SqlConversationContextService;
import com.fastbee.ai.service.IAiNl2SqlQueryService;
import com.fastbee.ai.service.IAiNl2SqlWorkflowService;
import com.fastbee.ai.service.IAiPlatformAssistantConversationContextService;
import com.fastbee.ai.service.IAiPlatformDocKnowledgeService;
import com.fastbee.ai.service.IAiProtocolAdaptationTaskService;
import com.fastbee.ai.service.IAiProtocolKnowledgeService;
import com.fastbee.ai.service.IAiProtocolParseConversationContextService;
import com.fastbee.ai.service.IAiRequirementEvaluationService;
import com.fastbee.ai.service.IAiSemanticNormalizationService;
import com.fastbee.ai.service.IAiThingModelGenerateService;
import com.fastbee.ai.skill.IAiDataQuerySkillService;
import com.fastbee.ai.skill.IAiDeviceControlSkillService;
import com.fastbee.ai.support.AiAutoRouteAdoptionPolicy;
import com.fastbee.ai.support.AiAutoRouteContext;
import com.fastbee.ai.support.AiAutoRouteDecision;
import com.fastbee.ai.support.AiAutoRouteDecisionEngine;
import com.fastbee.ai.support.AiAutoRouteModeMetadata;
import com.fastbee.ai.support.AiAutoRouteOptions;
import com.fastbee.ai.support.AiCandidateMatchSupport;
import com.fastbee.ai.support.AiChatAttachmentTextExtractor;
import com.fastbee.ai.support.AiChatObservabilityConstants;
import com.fastbee.ai.support.AiClarifyConstants;
import com.fastbee.ai.support.AiCodebaseAnswerSanitizer;
import com.fastbee.ai.support.AiCodebaseQuestionClassifier;
import com.fastbee.ai.support.AiIntentRoutePolicy;
import com.fastbee.ai.support.AiReplyLanguageSupport;
import com.fastbee.ai.support.AiRouteCandidate;
import com.fastbee.ai.support.AiRouteFeatureExtractor;
import com.fastbee.ai.support.AiSecuritySupport;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.mq.InvokeReqDto;
import com.fastbee.common.extend.core.domin.mq.MQSendMessageBo;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.DeviceShortOutput;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;
import com.fastbee.iot.model.param.DataCenterParam;
import com.fastbee.iot.model.vo.AlertCountVO;
import com.fastbee.iot.model.vo.ThingsModelLogCountVO;
import com.fastbee.system.domain.SysMenu;
import com.fastbee.system.mapper.SysMenuMapper;

@Service
public class AiChatServiceImpl implements AiChatService {

    private static final Logger log = LoggerFactory.getLogger(AiChatServiceImpl.class);

    private static final String MODE_POLICY_AUTO = "AUTO";
    private static final String MODE_POLICY_PINNED = "PINNED";
    private static final String ROUTE_MODE_DEFAULT = "DEFAULT";
    private static final String ROUTE_MODE_MANUAL = "MANUAL";
    private static final String SKILL_AUTO_ROUTER_ANALYZE = "auto_router_analyze";
    private static final String SKILL_AUTO_ROUTE_CONFIRM = "auto_route_mode_confirm";
    private static final String SKILL_PLATFORM_ASSISTANT = "platform_assistant_chat";
    private static final String SKILL_PLATFORM_ASSISTANT_FALLBACK = "platform_assistant_fallback_chat";
    private static final String SKILL_GENERAL_CHAT = "general_chat";
    private static final String SKILL_PROTOCOL_PARSE = "protocol_parse_chat";
    private static final String SKILL_THING_MODEL_GENERATE = "thing_model_generate";
    private static final String SKILL_REQUIREMENT_EVALUATION = "requirement_evaluation";
    private static final String ARTIFACT_TYPE_SOURCE_DOCUMENT = "SOURCE_DOCUMENT";
    private static final String SKILL_NL2SQL_GENERATE = "nl2sql_generate_execute";
    private static final String SKILL_NL2SQL_SQL_PREVIEW = "nl2sql_sql_preview";
    private static final String SKILL_NL2SQL_ALERT_PROCESS = "count_alert_process";
    private static final String SKILL_NL2SQL_ALERT_LEVEL = "count_alert_level";
    private static final String SKILL_NL2SQL_MODEL_INVOKE = "count_things_model_invoke";
    private static final String SKILL_NL2SQL_GUIDE = "nl2sql_guide";
    private static final String SKILL_NL2SQL_CLARIFY_DEVICE = "nl2sql_clarify_device";
    private static final String SKILL_NL2SQL_CLARIFY_THING_MODEL = "nl2sql_clarify_thing_model";
    private static final String DEFAULT_LANGUAGE = "zh-CN";
    private static final long PLATFORM_FEATURE_MENU_CACHE_TTL_MS = Duration.ofMinutes(10).toMillis();
    private static final String SKILL_DEVICE_INVOKE = "device_invoke";
    private static final String SKILL_DEVICE_INVOKE_REPLY = "device_invoke_reply";
    private static final String SKILL_DEVICE_COMMAND = "device_command_generate";
    private static final String SKILL_DEVICE_SCENE = "device_run_scene";
    private static final String SKILL_DEVICE_GUIDE = "device_control_guide";
    private static final String SKILL_DEVICE_CLARIFY_DEVICE = "device_control_clarify_device";
    private static final String SKILL_DEVICE_CLARIFY_THING_MODEL = "device_control_clarify_thing_model";
    private static final String SKILL_DEVICE_CONFIRM = "device_control_confirm";
    private static final long MODEL_STREAM_IDLE_TIMEOUT_SECONDS = 8L;
    private static final long STREAM_PARTIAL_PERSIST_INTERVAL_MS = 1000L;
    private static final int NL2SQL_CLARIFY_MIN_SCORE = 40;
    private static final int NL2SQL_CLARIFY_SCORE_GAP = 15;
    private static final int NL2SQL_CLARIFY_CANDIDATE_SCORE_WINDOW = 10;
    private static final int NL2SQL_CLARIFY_MAX_CANDIDATES = 20;
    private static final List<String> NL2SQL_CLARIFY_FOCUS_NOISE_TOKENS = List.of(
            "查询", "设备", "产品", "请问", "帮我", "一下",
            "当前值", "实时值", "是多少", "多少", "什么",
            "查看", "看下", "查下", "已确认", "确认", "物模型", "指标",
            "query", "device", "product", "currentvalue", "realtimevalue", "value", "metric"
    );
    private static final List<String> NL2SQL_CLARIFY_INTENT_TOKENS = List.of(
            "当前", "实时", "现在", "此刻", "最新", "历史", "最近", "当天", "今天", "昨天", "昨日",
            "趋势", "统计", "总计", "总和", "平均", "最大", "最小",
            "current", "realtime", "now", "latest", "history", "recent", "today", "yesterday",
            "trend", "statistics", "count", "sum", "average", "avg", "max", "min"
    );
    private static final Pattern SQL_CODE_BLOCK_PATTERN = Pattern.compile("(?is)```(?:sql)?\\s*(select\\b.+?)```");
    private static final Pattern INLINE_SQL_PATTERN = Pattern.compile("(?is)(select\\b.+)$");

    @Resource private FastBeeAiProperties properties;
    @Resource private AiModelRoutingService aiModelRoutingService;
    @Resource private AiRuntimeModelSnapshotService aiRuntimeModelSnapshotService;
    @Resource private AiChatModelFactoryService aiChatModelFactoryService;
    @Resource private IAiChatSessionService aiChatSessionService;
    @Resource private IAiChatMessageService aiChatMessageService;
    @Resource private IAiChatIntentRouteService aiChatIntentRouteService;
    @Resource private IAiDataQuerySkillService aiDataQuerySkillService;
    @Resource private IAiDeviceControlSkillService aiDeviceControlSkillService;
    @Resource private IAiDeviceControlConfirmService aiDeviceControlConfirmService;
    @Resource private IAiConversationContextAssembler aiConversationContextAssembler;
    @Resource private IAiDeviceControlConversationContextService aiDeviceControlConversationContextService;
    @Resource private IAiDeviceControlIntentService aiDeviceControlIntentService;
    @Resource private IAiClarifySupportService aiClarifySupportService;
    @Resource private IAiNl2SqlConversationContextService aiNl2SqlConversationContextService;
    @Resource private IAiNl2SqlQueryService aiNl2SqlQueryService;
    @Resource private IAiNl2SqlWorkflowService aiNl2SqlWorkflowService;
    @Resource private IAiPlatformAssistantConversationContextService aiPlatformAssistantConversationContextService;
    @Resource private IAiPlatformDocKnowledgeService aiPlatformDocKnowledgeService;
    @Resource private IAiCodebaseGuideKnowledgeService aiCodebaseGuideKnowledgeService;
    @Resource private IAiProtocolAdaptationTaskService aiProtocolAdaptationTaskService;
    @Resource private IAiProtocolKnowledgeService aiProtocolKnowledgeService;
    @Resource private IAiProtocolParseConversationContextService aiProtocolParseConversationContextService;
    @Resource private IAiThingModelGenerateService aiThingModelGenerateService;
    @Resource private IAiRequirementEvaluationService aiRequirementEvaluationService;
    @Resource private IAiSemanticNormalizationService aiSemanticNormalizationService;
    @Resource private IAiDeviceResolveService aiDeviceResolveService;
    @Resource private AiChatAttachmentTextExtractor aiChatAttachmentTextExtractor;
    @Resource private SysMenuMapper sysMenuMapper;
    private final AiAutoRouteDecisionEngine autoRouteDecisionEngine = new AiAutoRouteDecisionEngine();
    private final AiRouteFeatureExtractor autoRouteFeatureExtractor = new AiRouteFeatureExtractor();
    private volatile List<String> platformFeatureMenuTokensCache;
    private volatile long platformFeatureMenuTokensExpireAt;

    @Override
    public AiChatSendResponseVO send(AiChatSendRequest request) {
        validateChatRuntime();
        String question = request.getMessage().trim();
        String modePolicy = resolveModePolicy(request);
        String pinnedMode = resolvePinnedMode(request);
        String requestedMode = resolveRequestedMode(request, modePolicy, pinnedMode);
        String sessionChatMode = resolveSessionChatMode(request, modePolicy, pinnedMode, requestedMode);
        boolean manualRoute = StringUtils.isNotBlank(request.getModelCode()) || StringUtils.isNotBlank(request.getProviderCode());
        AiChatExecutionContext executionContext = prepareExecutionContext(request);
        AiModelRouteVO route = executionContext.route;
        SysUser currentUser = AiSecuritySupport.getCurrentUser();
        if (currentUser == null) {
            throw new ServiceException(message("ai.chat.current.user.required.create"));
        }

        AiChatSession session = resolveSession(request, question, route, currentUser, sessionChatMode, modePolicy, pinnedMode);
        int currentMessageNo = aiChatMessageService.countBySessionId(session.getSessionId());
        List<AiChatMessage> historyMessages = loadConversationHistory(session.getSessionId());
        Locale replyLocale = AiReplyLanguageSupport.resolveReplyLocale(question, historyMessages, AiReplyLanguageSupport.currentLocale());
        String operatorName = AiSecuritySupport.resolveUsername();
        long startAt = System.currentTimeMillis();
        AiChatIntentRouteVO intentRouteResult = analyzeIntentRouteQuietly(question, requestedMode, executionContext.chatModel, historyMessages, null, request);
        enrichIntentRouteAudit(intentRouteResult, request, requestedMode, modePolicy);
        String effectiveMode = resolveEffectiveMode(requestedMode, question, intentRouteResult, request, historyMessages);
        String executionQuestion = buildContextAwareExecutionQuestion(question, effectiveMode, intentRouteResult, historyMessages);

        AiChatMessage userMessage = buildMessage(session, currentMessageNo + 1, "user", question, route, requestedMode, resolveRouteMode(manualRoute));
        attachIntentRouteAudit(userMessage, intentRouteResult);
        userMessage.setMessageStatus("SUCCESS");
        aiChatMessageService.save(userMessage);

        AiClarifyPayloadVO clarifyPayload = AiReplyLanguageSupport.callWithLocale(replyLocale,
                () -> buildClarifyContext(requestedMode, effectiveMode,
                        resolveClarifyQuestion(effectiveMode, question, executionQuestion),
                        intentRouteResult));
        if (clarifyPayload != null) {
            AiChatMessage assistantMessage = buildMessage(session, currentMessageNo + 2, "assistant",
                    clarifyPayload.getDisplayContent(), route, effectiveMode, resolveRouteMode(manualRoute));
            assistantMessage.setToolName(clarifyPayload.getToolName());
            assistantMessage.setToolResult(clarifyPayload.getToolResult());
            assistantMessage.setMessageStatus("SUCCESS");
            aiChatMessageService.save(assistantMessage);
            refreshSession(session, route, sessionChatMode, effectiveMode);

            AiChatSendResponseVO response = new AiChatSendResponseVO();
            response.setSessionId(session.getSessionId());
            response.setSessionCode(session.getSessionCode());
            response.setQuestion(question);
            response.setAnswer(clarifyPayload.getDisplayContent());
            response.setModelCode(route.getModelCode());
            response.setProviderCode(route.getProviderCode());
            response.setChatMode(requestedMode);
            response.setModePolicy(resolveSessionModePolicy(session));
            response.setPinnedMode(resolveSessionPinnedMode(session));
            response.setLastEffectiveMode(resolveSessionLastEffectiveMode(session));
            response.setEffectiveChatMode(effectiveMode);
            response.setExecutedSkill(clarifyPayload.getToolName());
            response.setToolResult(clarifyPayload.getToolResult());
            response.setRouteAudit(serializeIntentRouteAudit(intentRouteResult));
            return response;
        }

        AiModeExecutionResult executionResult;
        long durationMs;
        try {
            executionResult = AiReplyLanguageSupport.callWithLocale(replyLocale,
                    () -> executeByMode(effectiveMode, executionQuestion, route, executionContext.chatModel, historyMessages, intentRouteResult));
            durationMs = System.currentTimeMillis() - startAt;
        } catch (Exception ex) {
            durationMs = System.currentTimeMillis() - startAt;
            AiChatMessage failedMessage = buildMessage(session, currentMessageNo + 2, "assistant",
                    message("ai.chat.mode.execution.failed.retry"), route, effectiveMode, resolveRouteMode(manualRoute));
            failedMessage.setDurationMs(durationMs);
            failedMessage.setMessageStatus("FAIL");
            aiChatMessageService.save(failedMessage);
            refreshSession(session, route, sessionChatMode, effectiveMode);
            throw ex instanceof ServiceException ? (ServiceException) ex
                    : new ServiceException(StringUtils.isNotBlank(ex.getMessage()) ? ex.getMessage() : message("ai.chat.mode.execution.failed.later"));
        }

        persistConversationContextSnapshot(userMessage, executionResult, operatorName);

        AiChatMessage assistantMessage = buildMessage(session, currentMessageNo + 2, "assistant", executionResult.answer, route, executionResult.effectiveChatMode, resolveRouteMode(manualRoute));
        assistantMessage.setToolName(executionResult.toolName);
        assistantMessage.setToolResult(executionResult.toolResult);
        assistantMessage.setDurationMs(durationMs);
        assistantMessage.setMessageStatus("SUCCESS");
        aiChatMessageService.save(assistantMessage);
        refreshSession(session, route, sessionChatMode, executionResult.effectiveChatMode);

        AiChatSendResponseVO response = new AiChatSendResponseVO();
        response.setSessionId(session.getSessionId());
        response.setSessionCode(session.getSessionCode());
        response.setQuestion(question);
        response.setAnswer(executionResult.answer);
        response.setModelCode(route.getModelCode());
        response.setProviderCode(route.getProviderCode());
        response.setChatMode(requestedMode);
        response.setModePolicy(resolveSessionModePolicy(session));
        response.setPinnedMode(resolveSessionPinnedMode(session));
        response.setLastEffectiveMode(resolveSessionLastEffectiveMode(session));
        response.setEffectiveChatMode(executionResult.effectiveChatMode);
        response.setExecutedSkill(executionResult.toolName);
        response.setToolResult(executionResult.toolResult);
        response.setRouteAudit(serializeIntentRouteAudit(intentRouteResult));
        return response;
    }

    @Override
    public Flux<AiChatStreamEventVO> sendStreamWithAttachment(AiChatSendRequest request, MultipartFile file) {
        Locale requestLocale = AiReplyLanguageSupport.currentLocale();
        return Flux.defer(() -> AiReplyLanguageSupport.callWithLocale(requestLocale, () -> {
            AiChatSendRequest actualRequest = request == null ? new AiChatSendRequest() : request;
            AiChatAttachmentTextExtractor.ExtractedAttachment attachment = aiChatAttachmentTextExtractor.extract(file);
            actualRequest.setAttachmentFileName(attachment.fileName());
            actualRequest.setAttachmentContentType(attachment.contentType());
            actualRequest.setAttachmentText(attachment.text());
            if (shouldUpgradeAttachmentToRequirementEvaluation(actualRequest, attachment)) {
                actualRequest.setModeOverride(AiChatMode.REQUIREMENT_EVALUATION.name());
                if (StringUtils.isBlank(actualRequest.getMessage())) {
                    actualRequest.setMessage(message("ai.chat.requirement.evaluation.default.question", attachment.fileName()));
                }
                return uploadRequirementEvaluationDocumentStream(actualRequest, file);
            }
            if (shouldUpgradeAttachmentToThingModelGenerate(actualRequest, attachment)) {
                actualRequest.setModeOverride(AiChatMode.THING_MODEL_GENERATE.name());
                if (StringUtils.isBlank(actualRequest.getMessage())) {
                    actualRequest.setMessage(message("ai.chat.thing.model.generate.default.question", attachment.fileName()));
                }
                return uploadThingModelDocumentStream(actualRequest, file);
            }
            if (shouldUpgradeAttachmentToProtocolParse(actualRequest, attachment)) {
                actualRequest.setModeOverride(AiChatMode.PROTOCOL_PARSE.name());
                if (StringUtils.isBlank(actualRequest.getMessage())) {
                    actualRequest.setMessage(message("ai.chat.protocol.default.question", attachment.fileName()));
                }
                return uploadProtocolDocumentStream(actualRequest, file);
            }
            if (StringUtils.isBlank(actualRequest.getMessage())) {
                actualRequest.setMessage(AiPromptConstant.ATTACHMENT_SUMMARY_QUESTION);
            }
            return sendStream(actualRequest);
        }));
    }

    @Override
    public Flux<AiChatStreamEventVO> sendStream(AiChatSendRequest request) {
        Locale requestLocale = AiReplyLanguageSupport.currentLocale();
        return Flux.defer(() -> AiReplyLanguageSupport.callWithLocale(requestLocale, () -> {
            long prepareStartAt = System.currentTimeMillis();
            validateChatRuntime();
            long runtimeValidateMs = System.currentTimeMillis() - prepareStartAt;
            String question = request.getMessage().trim();
            String modePolicy = resolveModePolicy(request);
            String pinnedMode = resolvePinnedMode(request);
            String requestedMode = resolveRequestedMode(request, modePolicy, pinnedMode);
            String sessionChatMode = resolveSessionChatMode(request, modePolicy, pinnedMode, requestedMode);
            boolean manualRoute = StringUtils.isNotBlank(request.getModelCode()) || StringUtils.isNotBlank(request.getProviderCode());
            String routeMode = resolveRouteMode(manualRoute);
            long modelRouteStartAt = System.currentTimeMillis();
            AiChatExecutionContext executionContext = prepareExecutionContext(request);
            long modelRouteMs = System.currentTimeMillis() - modelRouteStartAt;
            AiModelRouteVO route = executionContext.route;
            SysUser currentUser = AiSecuritySupport.getCurrentUser();
            if (currentUser == null) {
                throw new ServiceException(message("ai.chat.current.user.required.create"));
            }

            AiChatSession session = resolveSession(request, question, route, currentUser, sessionChatMode, modePolicy, pinnedMode);
            int currentMessageNo = aiChatMessageService.countBySessionId(session.getSessionId());
            long historyLoadStartAt = System.currentTimeMillis();
            List<AiChatMessage> historyMessages = loadConversationHistory(session.getSessionId());
            Locale replyLocale = AiReplyLanguageSupport.resolveReplyLocale(question, historyMessages, requestLocale);
            long historyLoadMs = System.currentTimeMillis() - historyLoadStartAt;
            String operatorName = AiSecuritySupport.resolveUsername();
            long routeAnalyzeStartAt = System.currentTimeMillis();
            AiChatIntentRouteVO intentRouteResult = analyzeIntentRouteQuietly(question, requestedMode, executionContext.chatModel, historyMessages, null, request);
            long routeAnalyzeMs = System.currentTimeMillis() - routeAnalyzeStartAt;
            enrichIntentRouteAudit(intentRouteResult, request, requestedMode, modePolicy);
            String effectiveMode = resolveEffectiveMode(requestedMode, question, intentRouteResult, request, historyMessages);
            String attachmentAwareQuestion = buildAttachmentAwareQuestion(question, request);
            String executionQuestion = buildContextAwareExecutionQuestion(attachmentAwareQuestion, effectiveMode, intentRouteResult, historyMessages);
            putIntentRoutePerformanceMetric(intentRouteResult, "runtimeValidateMs", runtimeValidateMs);
            putIntentRoutePerformanceMetric(intentRouteResult, "modelRouteMs", modelRouteMs);
            putIntentRoutePerformanceMetric(intentRouteResult, "historyLoadMs", historyLoadMs);
            putIntentRoutePerformanceMetric(intentRouteResult, "routeAnalyzeMs", routeAnalyzeMs);
            putIntentRoutePerformanceMetric(intentRouteResult, "historyMessageCount", historyMessages == null ? 0 : historyMessages.size());
            putIntentRoutePerformanceMetric(intentRouteResult, "questionChars", question.length());

            long persistStartAt = System.currentTimeMillis();
            AiChatMessage userMessage = buildMessage(session, currentMessageNo + 1, "user", question, route, requestedMode, routeMode);
            attachIntentRouteAudit(userMessage, intentRouteResult);
            userMessage.setMessageStatus("SUCCESS");
            aiChatMessageService.save(userMessage);

            AiChatMessage assistantMessage = buildMessage(session, currentMessageNo + 2, "assistant", "", route, effectiveMode, routeMode);
            assistantMessage.setMessageSummary(message("ai.chat.processing"));
            assistantMessage.setMessageStatus("RUNNING");
            aiChatMessageService.save(assistantMessage);
            putIntentRoutePerformanceMetric(intentRouteResult, "messagePersistMs", System.currentTimeMillis() - persistStartAt);
            putIntentRoutePerformanceMetric(intentRouteResult, "prepareMs", System.currentTimeMillis() - prepareStartAt);
            updateIntentRouteAuditMessage(userMessage, intentRouteResult, operatorName);
            refreshSessionWithOperator(session, route, sessionChatMode, operatorName);
            log.info("AI会话流式准备完成: sessionId={}, sessionCode={}, requestedMode={}, effectiveMode={}, modelCode={}, prepareMs={}",
                    session.getSessionId(), session.getSessionCode(), requestedMode, effectiveMode,
                    route == null ? null : route.getModelCode(), System.currentTimeMillis() - prepareStartAt);

            return Flux.create(sink -> AiReplyLanguageSupport.runWithLocale(replyLocale, () -> {
                long startAt = System.currentTimeMillis();
                AiChatStreamEventVO startEvent = buildStreamEvent("start", session, route, requestedMode, effectiveMode, null, "", "", null, null, false, null, null);
                startEvent.setRouteAudit(serializeIntentRouteAudit(intentRouteResult));
                sink.next(startEvent);
                if (supportsTokenStream(effectiveMode)) {
                    streamModelConversation(sink, session, route, executionContext.chatModel, requestedMode, effectiveMode,
                            executionQuestion, historyMessages, userMessage, assistantMessage, operatorName, startAt,
                            prepareStartAt, intentRouteResult);
                    return;
                }
                AiClarifyPayloadVO clarifyPayload = buildClarifyContext(requestedMode, effectiveMode,
                        resolveClarifyQuestion(effectiveMode, question, executionQuestion),
                        intentRouteResult);
                if (clarifyPayload != null) {
                    long durationMs = System.currentTimeMillis() - startAt;
                    putIntentRoutePerformanceMetric(intentRouteResult, "executionType", "CLARIFY");
                    putIntentRoutePerformanceMetric(intentRouteResult, "executeMs", durationMs);
                    putIntentRoutePerformanceMetric(intentRouteResult, "totalMs", System.currentTimeMillis() - prepareStartAt);
                    updateIntentRouteAuditMessage(userMessage, intentRouteResult, operatorName);
                    finishAssistantMessage(assistantMessage, clarifyPayload.getDisplayContent(), effectiveMode,
                            clarifyPayload.getToolName(), clarifyPayload.getToolResult(), durationMs, operatorName);
                    refreshSessionWithOperator(session, route, sessionChatMode, operatorName, effectiveMode);
                    log.info("AI会话阶段流式完成: sessionCode={}, effectiveMode={}, skill={}, durationMs={}",
                            session.getSessionCode(), effectiveMode, clarifyPayload.getToolName(), durationMs);
                    AiChatStreamEventVO clarifyEvent = buildClarifyStreamEvent(session, route, requestedMode, effectiveMode, clarifyPayload);
                    clarifyEvent.setRouteAudit(serializeIntentRouteAudit(intentRouteResult));
                    sink.next(clarifyEvent);
                    sink.complete();
                    return;
                }
                sink.next(buildStreamEvent("stage", session, route, requestedMode, effectiveMode, null,
                        resolveStageMessage(effectiveMode), "", null, null, false, null, null));
                try {
                    long executeStartAt = System.currentTimeMillis();
                    AiModeExecutionResult executionResult = executeByMode(effectiveMode, executionQuestion, route, executionContext.chatModel, historyMessages, intentRouteResult);
                    long executeMs = System.currentTimeMillis() - executeStartAt;
                    long durationMs = System.currentTimeMillis() - startAt;
                    putIntentRoutePerformanceMetric(intentRouteResult, "executionType", "STAGE");
                    putIntentRoutePerformanceMetric(intentRouteResult, "executeMs", executeMs);
                    putIntentRoutePerformanceMetric(intentRouteResult, "totalMs", System.currentTimeMillis() - prepareStartAt);
                    persistConversationContextSnapshot(userMessage, executionResult, operatorName);
                    updateIntentRouteAuditMessage(userMessage, intentRouteResult, operatorName);
                    finishAssistantMessage(assistantMessage, executionResult.answer, executionResult.effectiveChatMode,
                            executionResult.toolName, executionResult.toolResult, durationMs, operatorName);
                    refreshSessionWithOperator(session, route, sessionChatMode, operatorName, executionResult.effectiveChatMode);
                    log.info("AI会话阶段流式完成: sessionCode={}, effectiveMode={}, skill={}, durationMs={}",
                            session.getSessionCode(), executionResult.effectiveChatMode, executionResult.toolName, durationMs);
                    AiChatStreamEventVO doneEvent = buildStreamEvent("done", session, route, requestedMode, executionResult.effectiveChatMode,
                            executionResult.toolName, executionResult.answer, executionResult.answer, executionResult.toolResult,
                            extractQueryMode(executionResult.toolResult), true, null, null);
                    doneEvent.setRouteAudit(serializeIntentRouteAudit(intentRouteResult));
                    sink.next(doneEvent);
                    sink.complete();
                } catch (Exception ex) {
                    long durationMs = System.currentTimeMillis() - startAt;
                    String answer = buildStreamFailureAnswer(ex, effectiveMode);
                    String executedSkill = resolveFailureExecutedSkill(effectiveMode);
                    putIntentRoutePerformanceMetric(intentRouteResult, "executionType", "STAGE");
                    putIntentRoutePerformanceMetric(intentRouteResult, "executeMs", durationMs);
                    putIntentRoutePerformanceMetric(intentRouteResult, "totalMs", System.currentTimeMillis() - prepareStartAt);
                    putIntentRoutePerformanceMetric(intentRouteResult, "failed", true);
                    updateIntentRouteAuditMessage(userMessage, intentRouteResult, operatorName);
                    failAssistantMessage(assistantMessage, answer, effectiveMode, executedSkill, null, durationMs, operatorName);
                    refreshSessionWithOperator(session, route, sessionChatMode, operatorName, effectiveMode);
                    log.warn("AI会话阶段流式失败: sessionCode={}, effectiveMode={}, skill={}, durationMs={}, error={}",
                            session.getSessionCode(), effectiveMode, executedSkill, durationMs, ex.getMessage());
                    emitStreamError(sink, session, route, requestedMode, effectiveMode, ex, executedSkill, answer,
                            serializeIntentRouteAudit(intentRouteResult));
                }
            }), FluxSink.OverflowStrategy.BUFFER);
        }));
    }

    @Override
    public Flux<AiChatStreamEventVO> resumeStream(AiChatSendRequest request) {
        Locale requestLocale = AiReplyLanguageSupport.currentLocale();
        return Flux.defer(() -> AiReplyLanguageSupport.callWithLocale(requestLocale, () -> {
            validateChatRuntime();
            if (StringUtils.isBlank(request.getResumeToken())) {
                throw new ServiceException(message("ai.chat.resume.token.required"));
            }
            if (StringUtils.isBlank(request.getClarifyKey()) || StringUtils.isBlank(request.getSelectedValue())) {
                throw new ServiceException(message("ai.chat.clarify.selection.required"));
            }
            String requestedMode = aiClarifySupportService.resolveResumeEffectiveMode(request);
            if (!AiChatMode.NL2SQL.name().equals(requestedMode)
                    && !AiChatMode.DEVICE_CONTROL.name().equals(requestedMode)
                    && !AiChatMode.PLATFORM_ASSISTANT.name().equals(requestedMode)
                    && !AiChatMode.GENERAL.name().equals(requestedMode)) {
                throw new ServiceException(message("ai.chat.resume.mode.invalid"));
            }
            String modePolicy = resolveModePolicy(request);
            String pinnedMode = resolvePinnedMode(request);
            String sessionChatMode = resolveSessionChatMode(request, modePolicy, pinnedMode, requestedMode);
            boolean manualRoute = StringUtils.isNotBlank(request.getModelCode()) || StringUtils.isNotBlank(request.getProviderCode());
            String routeMode = resolveRouteMode(manualRoute);
            AiChatExecutionContext executionContext = prepareExecutionContext(request);
            AiModelRouteVO route = executionContext.route;
            SysUser currentUser = AiSecuritySupport.getCurrentUser();
            if (currentUser == null) {
                throw new ServiceException(message("ai.chat.current.user.required.resume"));
            }
            String displayMessage = aiClarifySupportService.buildResumeDisplayMessage(request);
            String baseExecutionQuestion = aiClarifySupportService.buildResumeExecutionQuestion(request);
            boolean rejectedRiskConfirm = aiDeviceControlConfirmService.isRejectedSelection(request);
            if (StringUtils.isBlank(baseExecutionQuestion) && !rejectedRiskConfirm) {
                throw new ServiceException(message("ai.chat.resume.question.required"));
            }

            AiChatSession session = resolveSession(request, displayMessage, route, currentUser, sessionChatMode, modePolicy, pinnedMode);
            int currentMessageNo = aiChatMessageService.countBySessionId(session.getSessionId());
            List<AiChatMessage> historyMessages = loadConversationHistory(session.getSessionId());
            String operatorName = AiSecuritySupport.resolveUsername();
            String executionQuestion = buildContextAwareExecutionQuestion(baseExecutionQuestion, requestedMode, null, historyMessages);

            AiChatMessage userMessage = buildMessage(session, currentMessageNo + 1, "user", displayMessage, route, requestedMode, routeMode);
            if (aiDeviceControlConfirmService.isApprovedSelection(request)) {
                userMessage.setRiskConfirmed("1");
            }
            userMessage.setMessageStatus("SUCCESS");
            aiChatMessageService.save(userMessage);

            AiChatMessage assistantMessage = buildMessage(session, currentMessageNo + 2, "assistant", "", route, requestedMode, routeMode);
            assistantMessage.setMessageSummary(message("ai.chat.processing"));
            assistantMessage.setMessageStatus("RUNNING");
            aiChatMessageService.save(assistantMessage);
            refreshSessionWithOperator(session, route, sessionChatMode, operatorName);

            return Flux.create(sink -> AiReplyLanguageSupport.runWithLocale(requestLocale, () -> {
                long startAt = System.currentTimeMillis();
                sink.next(buildStreamEvent("start", session, route, requestedMode, requestedMode, null, "", "", null, null, false, null, null));
                sink.next(buildStreamEvent("resume", session, route, requestedMode, requestedMode, null,
                        displayMessage, displayMessage, null, null, false, null, null));
                try {
                    if (rejectedRiskConfirm) {
                        long durationMs = System.currentTimeMillis() - startAt;
                        String rejectedAnswer = aiDeviceControlConfirmService.buildRejectedAnswer(request);
                        finishAssistantMessage(assistantMessage, rejectedAnswer, requestedMode,
                                SKILL_DEVICE_CONFIRM, buildDeviceControlConfirmToolResult("CANCELLED"), durationMs, operatorName);
                        refreshSessionWithOperator(session, route, sessionChatMode, operatorName, requestedMode);
                        sink.next(buildStreamEvent("done", session, route, requestedMode, requestedMode,
                                SKILL_DEVICE_CONFIRM, rejectedAnswer, rejectedAnswer, buildDeviceControlConfirmToolResult("CANCELLED"),
                                null, true, null, null));
                        sink.complete();
                        return;
                    }
                    AiClarifyPayloadVO clarifyPayload = buildClarifyContext(requestedMode, requestedMode, executionQuestion, null);
                    if (clarifyPayload != null) {
                        long durationMs = System.currentTimeMillis() - startAt;
                        finishAssistantMessage(assistantMessage, clarifyPayload.getDisplayContent(), requestedMode,
                                clarifyPayload.getToolName(), clarifyPayload.getToolResult(), durationMs, operatorName);
                        refreshSessionWithOperator(session, route, sessionChatMode, operatorName, requestedMode);
                        sink.next(buildClarifyStreamEvent(session, route, requestedMode, requestedMode, clarifyPayload));
                        sink.complete();
                        return;
                    }
                    sink.next(buildStreamEvent("stage", session, route, requestedMode, requestedMode, null,
                            resolveStageMessage(requestedMode), "", null, null, false, null, null));
                    if (aiDeviceControlConfirmService.isApprovedSelection(request)) {
                        assistantMessage.setRiskConfirmed("1");
                    }
                    AiModeExecutionResult executionResult = executeByMode(requestedMode, executionQuestion, route, executionContext.chatModel, historyMessages, null);
                    long durationMs = System.currentTimeMillis() - startAt;
                    persistConversationContextSnapshot(userMessage, executionResult, operatorName);
                    finishAssistantMessage(assistantMessage, executionResult.answer, executionResult.effectiveChatMode,
                            executionResult.toolName, executionResult.toolResult, durationMs, operatorName);
                    refreshSessionWithOperator(session, route, sessionChatMode, operatorName, executionResult.effectiveChatMode);
                    sink.next(buildStreamEvent("done", session, route, requestedMode, executionResult.effectiveChatMode,
                            executionResult.toolName, executionResult.answer, executionResult.answer, executionResult.toolResult,
                            extractQueryMode(executionResult.toolResult), true, null, null));
                    sink.complete();
                } catch (Exception ex) {
                    long durationMs = System.currentTimeMillis() - startAt;
                    String answer = buildStreamFailureAnswer(ex, requestedMode);
                    String executedSkill = resolveFailureExecutedSkill(requestedMode);
                    failAssistantMessage(assistantMessage, answer, requestedMode,
                            executedSkill, null, durationMs, operatorName);
                    refreshSessionWithOperator(session, route, sessionChatMode, operatorName, requestedMode);
                    emitStreamError(sink, session, route, requestedMode, requestedMode, ex, executedSkill, answer);
                }
            }), FluxSink.OverflowStrategy.BUFFER);
        }));
    }

    @Override
    public Flux<AiChatStreamEventVO> uploadProtocolDocumentStream(AiChatSendRequest request, MultipartFile file) {
        Locale requestLocale = AiReplyLanguageSupport.currentLocale();
        return Flux.defer(() -> AiReplyLanguageSupport.callWithLocale(requestLocale, () -> {
            validateChatRuntime();
            validateProtocolRuntime();
            if (file == null || file.isEmpty()) {
                throw new ServiceException(message("ai.chat.protocol.file.required"));
            }
            AiChatSendRequest actualRequest = request == null ? new AiChatSendRequest() : request;
            actualRequest.setModeOverride(AiChatMode.PROTOCOL_PARSE.name());
            if (StringUtils.isBlank(actualRequest.getChatMode())) {
                actualRequest.setChatMode(AiChatMode.AUTO.name());
            }
            if (StringUtils.isBlank(actualRequest.getModePolicy())) {
                actualRequest.setModePolicy(MODE_POLICY_AUTO);
            }

            String originalFilename = resolveUploadedProtocolFilename(file);
            String question = defaultText(actualRequest.getMessage(), buildDefaultProtocolUploadQuestion(originalFilename));
            Locale replyLocale = AiReplyLanguageSupport.resolveReplyLocale(question, null, requestLocale);
            actualRequest.setMessage(question);
            String modePolicy = resolveModePolicy(actualRequest);
            String pinnedMode = resolvePinnedMode(actualRequest);
            String requestedMode = AiChatMode.PROTOCOL_PARSE.name();
            AiChatIntentRouteVO intentRouteResult = buildProtocolUploadIntentRoute(question, actualRequest, modePolicy, originalFilename);
            String sessionChatMode = resolveSessionChatMode(actualRequest, modePolicy, pinnedMode, requestedMode);
            boolean manualRoute = StringUtils.isNotBlank(actualRequest.getModelCode())
                    || StringUtils.isNotBlank(actualRequest.getProviderCode());
            String routeMode = resolveRouteMode(manualRoute);
            AiModelRouteVO route = StringUtils.isNotBlank(actualRequest.getModelCode())
                    ? aiModelRoutingService.resolveByModelCode(actualRequest.getModelCode())
                    : aiModelRoutingService.resolveDefaultRoute();
            SysUser currentUser = AiSecuritySupport.getCurrentUser();
            if (currentUser == null) {
                throw new ServiceException(message("ai.chat.current.user.required.create"));
            }

            AiChatSession session = resolveSession(actualRequest, question, route, currentUser, sessionChatMode, modePolicy, pinnedMode);
            int currentMessageNo = aiChatMessageService.countBySessionId(session.getSessionId());
            String operatorName = AiSecuritySupport.resolveUsername();
            AiChatMessage userMessage = buildMessage(session, currentMessageNo + 1, "user",
                    buildProtocolUploadUserMessage(question, originalFilename), route, requestedMode, routeMode);
            userMessage.setMessageStatus("SUCCESS");
            attachIntentRouteAudit(userMessage, intentRouteResult);
            aiChatMessageService.save(userMessage);

            AiChatMessage assistantMessage = buildMessage(session, currentMessageNo + 2, "assistant", "",
                    route, requestedMode, routeMode);
            assistantMessage.setToolName(SKILL_PROTOCOL_PARSE);
            assistantMessage.setMessageSummary(message("ai.chat.protocol.upload.summary.running"));
            assistantMessage.setMessageStatus("RUNNING");
            aiChatMessageService.save(assistantMessage);
            refreshSessionWithOperator(session, route, sessionChatMode, operatorName);

            return Flux.create(sink -> AiReplyLanguageSupport.runWithLocale(replyLocale, () -> {
                long startAt = System.currentTimeMillis();
                AiProtocolAdaptationTask task = null;
                AiProtocolAdaptationArtifactVO sourceArtifact = null;
                sink.next(withRouteAudit(buildStreamEvent("start", session, route, requestedMode, requestedMode,
                        SKILL_PROTOCOL_PARSE, "", "", null, null, false, null, null), intentRouteResult));
                try {
                    sink.next(withRouteAudit(buildStreamEvent("stage", session, route, requestedMode, requestedMode,
                            SKILL_PROTOCOL_PARSE, message("ai.chat.protocol.upload.stage.create.task"), "",
                            null, null, false, null, null), intentRouteResult));
                    task = new AiProtocolAdaptationTask();
                    task.setTaskName(buildChatProtocolTaskName(originalFilename));
                    task.setRemark(message("ai.chat.protocol.upload.task.remark", session.getSessionId()));
                    aiProtocolAdaptationTaskService.insertAiProtocolAdaptationTask(task);
                    putIntentRoutePerformanceMetric(intentRouteResult, "protocolTaskId", task.getTaskId());
                    updateIntentRouteAuditMessage(userMessage, intentRouteResult, operatorName);

                    sink.next(withRouteAudit(buildStreamEvent("stage", session, route, requestedMode, requestedMode,
                            SKILL_PROTOCOL_PARSE, message("ai.chat.protocol.upload.stage.save.document"), "",
                            buildProtocolUploadProgressToolResult(task, null, "TASK_CREATED",
                                    message("ai.chat.protocol.upload.stage.save.document")),
                            null, false, null, null), intentRouteResult));
                    sourceArtifact = aiProtocolAdaptationTaskService
                            .uploadTaskArtifact(task.getTaskId(), ARTIFACT_TYPE_SOURCE_DOCUMENT, file);
                    if (sourceArtifact != null) {
                        putIntentRoutePerformanceMetric(intentRouteResult, "sourceArtifactId", sourceArtifact.getArtifactId());
                    }
                    updateIntentRouteAuditMessage(userMessage, intentRouteResult, operatorName);

                    sink.next(withRouteAudit(buildStreamEvent("stage", session, route, requestedMode, requestedMode,
                            SKILL_PROTOCOL_PARSE, message("ai.chat.protocol.upload.stage.auto.running"), "",
                            buildProtocolUploadProgressToolResult(task, sourceArtifact, "AUTO_RUNNING",
                                    message("ai.chat.protocol.upload.stage.auto.running")),
                            null, false, null, null), intentRouteResult));
                    AiProtocolAdaptationAutoRunResultVO autoRunResult = aiProtocolAdaptationTaskService.autoRunTask(task.getTaskId());
                    String answer = buildProtocolUploadAnswer(originalFilename, autoRunResult);
                    String toolResult = buildProtocolUploadToolResult(autoRunResult, sourceArtifact, answer);
                    long durationMs = System.currentTimeMillis() - startAt;
                    putIntentRoutePerformanceMetric(intentRouteResult, "totalMs", durationMs);
                    updateIntentRouteAuditMessage(userMessage, intentRouteResult, operatorName);
                    finishAssistantMessage(assistantMessage, answer, requestedMode, SKILL_PROTOCOL_PARSE, toolResult, durationMs, operatorName);
                    refreshSessionWithOperator(session, route, sessionChatMode, operatorName, requestedMode);
                    emitProtocolUploadAnswerChunks(sink, session, route, requestedMode, answer);
                    sink.next(withRouteAudit(buildStreamEvent("done", session, route, requestedMode, requestedMode,
                            SKILL_PROTOCOL_PARSE, answer, answer, toolResult, null, true, null, null), intentRouteResult));
                    sink.complete();
                } catch (Exception ex) {
                    String answer = message("ai.chat.protocol.upload.failed", trimErrorMessage(ex.getMessage()));
                    String toolResult = buildProtocolUploadFailureToolResult(answer, task, sourceArtifact);
                    long durationMs = System.currentTimeMillis() - startAt;
                    putIntentRoutePerformanceMetric(intentRouteResult, "totalMs", durationMs);
                    putIntentRoutePerformanceMetric(intentRouteResult, "failed", true);
                    putIntentRoutePerformanceMetric(intentRouteResult, "errorMessage", trimErrorMessage(ex.getMessage()));
                    updateIntentRouteAuditMessage(userMessage, intentRouteResult, operatorName);
                    failAssistantMessage(assistantMessage, answer, requestedMode, SKILL_PROTOCOL_PARSE, toolResult, durationMs, operatorName);
                    refreshSessionWithOperator(session, route, sessionChatMode, operatorName, requestedMode);
                    emitProtocolUploadAnswerChunks(sink, session, route, requestedMode, answer);
                    sink.next(withRouteAudit(buildStreamEvent("done", session, route, requestedMode, requestedMode,
                            SKILL_PROTOCOL_PARSE, answer, answer, toolResult, null, true, null, null), intentRouteResult));
                    sink.complete();
                }
            }), FluxSink.OverflowStrategy.BUFFER);
        }));
    }

    @Override
    public Flux<AiChatStreamEventVO> uploadThingModelDocumentStream(AiChatSendRequest request, MultipartFile file) {
        Locale requestLocale = AiReplyLanguageSupport.currentLocale();
        return Flux.defer(() -> AiReplyLanguageSupport.callWithLocale(requestLocale, () -> {
            validateChatRuntime();
            if (file == null || file.isEmpty()) {
                throw new ServiceException(message("ai.chat.thing.model.file.required"));
            }
            AiChatSendRequest actualRequest = request == null ? new AiChatSendRequest() : request;
            actualRequest.setModeOverride(AiChatMode.THING_MODEL_GENERATE.name());
            if (StringUtils.isBlank(actualRequest.getChatMode())) {
                actualRequest.setChatMode(AiChatMode.AUTO.name());
            }
            if (StringUtils.isBlank(actualRequest.getModePolicy())) {
                actualRequest.setModePolicy(MODE_POLICY_AUTO);
            }

            AiChatAttachmentTextExtractor.ExtractedAttachment attachment = aiChatAttachmentTextExtractor.extract(file);
            actualRequest.setAttachmentFileName(attachment.fileName());
            actualRequest.setAttachmentContentType(attachment.contentType());
            actualRequest.setAttachmentText(attachment.text());
            String originalFilename = defaultText(attachment.fileName(), message("ai.chat.thing.model.generate.file.fallback"));
            String question = defaultText(actualRequest.getMessage(), buildDefaultThingModelUploadQuestion(originalFilename));
            Locale replyLocale = AiReplyLanguageSupport.resolveReplyLocale(question, null, requestLocale);
            actualRequest.setMessage(question);
            String modePolicy = resolveModePolicy(actualRequest);
            String pinnedMode = resolvePinnedMode(actualRequest);
            String requestedMode = AiChatMode.THING_MODEL_GENERATE.name();
            AiChatIntentRouteVO intentRouteResult = buildThingModelUploadIntentRoute(question, actualRequest, modePolicy, originalFilename);
            String sessionChatMode = resolveSessionChatMode(actualRequest, modePolicy, pinnedMode, requestedMode);
            boolean manualRoute = StringUtils.isNotBlank(actualRequest.getModelCode())
                    || StringUtils.isNotBlank(actualRequest.getProviderCode());
            String routeMode = resolveRouteMode(manualRoute);
            AiModelRouteVO route = StringUtils.isNotBlank(actualRequest.getModelCode())
                    ? aiModelRoutingService.resolveByModelCode(actualRequest.getModelCode())
                    : aiModelRoutingService.resolveDefaultRoute();
            SysUser currentUser = AiSecuritySupport.getCurrentUser();
            if (currentUser == null) {
                throw new ServiceException(message("ai.chat.current.user.required.create"));
            }

            AiChatSession session = resolveSession(actualRequest, question, route, currentUser, sessionChatMode, modePolicy, pinnedMode);
            int currentMessageNo = aiChatMessageService.countBySessionId(session.getSessionId());
            String operatorName = AiSecuritySupport.resolveUsername();
            AiChatMessage userMessage = buildMessage(session, currentMessageNo + 1, "user",
                    buildThingModelUploadUserMessage(question, originalFilename), route, requestedMode, routeMode);
            userMessage.setMessageStatus("SUCCESS");
            attachIntentRouteAudit(userMessage, intentRouteResult);
            aiChatMessageService.save(userMessage);

            AiChatMessage assistantMessage = buildMessage(session, currentMessageNo + 2, "assistant", "",
                    route, requestedMode, routeMode);
            assistantMessage.setToolName(SKILL_THING_MODEL_GENERATE);
            assistantMessage.setMessageSummary(message("ai.chat.thing.model.generate.processing"));
            assistantMessage.setMessageStatus("RUNNING");
            aiChatMessageService.save(assistantMessage);
            refreshSessionWithOperator(session, route, sessionChatMode, operatorName);

            return Flux.create(sink -> AiReplyLanguageSupport.runWithLocale(replyLocale, () -> {
                long startAt = System.currentTimeMillis();
                sink.next(withRouteAudit(buildStreamEvent("start", session, route, requestedMode, requestedMode,
                        SKILL_THING_MODEL_GENERATE, "", "", null, null, false, null, null), intentRouteResult));
                try {
                    sink.next(withRouteAudit(buildStreamEvent("stage", session, route, requestedMode, requestedMode,
                            SKILL_THING_MODEL_GENERATE, message("ai.chat.thing.model.generate.stage.extract"), "",
                            buildThingModelGenerateProgressToolResult(originalFilename, "TEXT_EXTRACTED",
                                    message("ai.chat.thing.model.generate.stage.extract")),
                            null, false, null, null), intentRouteResult));
                    sink.next(withRouteAudit(buildStreamEvent("stage", session, route, requestedMode, requestedMode,
                            SKILL_THING_MODEL_GENERATE, message("ai.chat.thing.model.generate.stage.ai.generating"), "",
                            buildThingModelGenerateProgressToolResult(originalFilename, "AI_GENERATING",
                                    message("ai.chat.thing.model.generate.stage.ai.generating")),
                            null, false, null, null), intentRouteResult));
                    AiThingModelGenerateResultVO generateResult = aiThingModelGenerateService.generateFromText(
                            originalFilename,
                            attachment.contentType(),
                            attachment.text(),
                            question,
                            route);
                    String answer = buildThingModelGenerateAnswer(originalFilename, generateResult);
                    String toolResult = JSON.toJSONString(generateResult);
                    long durationMs = System.currentTimeMillis() - startAt;
                    putIntentRoutePerformanceMetric(intentRouteResult, "totalMs", durationMs);
                    putIntentRoutePerformanceMetric(intentRouteResult, "thingModelRowCount", generateResult.getRowCount());
                    putIntentRoutePerformanceMetric(intentRouteResult, "artifactCode", generateResult.getArtifactCode());
                    updateIntentRouteAuditMessage(userMessage, intentRouteResult, operatorName);
                    finishAssistantMessage(assistantMessage, answer, requestedMode, SKILL_THING_MODEL_GENERATE, toolResult, durationMs, operatorName);
                    refreshSessionWithOperator(session, route, sessionChatMode, operatorName, requestedMode);
                    emitAnswerChunks(sink, session, route, requestedMode, requestedMode, SKILL_THING_MODEL_GENERATE, answer);
                    sink.next(withRouteAudit(buildStreamEvent("done", session, route, requestedMode, requestedMode,
                            SKILL_THING_MODEL_GENERATE, answer, answer, toolResult, null, true, null, null), intentRouteResult));
                    sink.complete();
                } catch (Exception ex) {
                    String answer = message("ai.chat.thing.model.generate.failed", trimErrorMessage(ex.getMessage()));
                    String toolResult = buildThingModelGenerateFailureToolResult(answer, originalFilename);
                    long durationMs = System.currentTimeMillis() - startAt;
                    putIntentRoutePerformanceMetric(intentRouteResult, "totalMs", durationMs);
                    putIntentRoutePerformanceMetric(intentRouteResult, "failed", true);
                    putIntentRoutePerformanceMetric(intentRouteResult, "errorMessage", trimErrorMessage(ex.getMessage()));
                    updateIntentRouteAuditMessage(userMessage, intentRouteResult, operatorName);
                    failAssistantMessage(assistantMessage, answer, requestedMode, SKILL_THING_MODEL_GENERATE, toolResult, durationMs, operatorName);
                    refreshSessionWithOperator(session, route, sessionChatMode, operatorName, requestedMode);
                    emitAnswerChunks(sink, session, route, requestedMode, requestedMode, SKILL_THING_MODEL_GENERATE, answer);
                    sink.next(withRouteAudit(buildStreamEvent("done", session, route, requestedMode, requestedMode,
                            SKILL_THING_MODEL_GENERATE, answer, answer, toolResult, null, true, null, null), intentRouteResult));
                    sink.complete();
                }
            }), FluxSink.OverflowStrategy.BUFFER);
        }));
    }

    @Override
    public Flux<AiChatStreamEventVO> uploadRequirementEvaluationDocumentStream(AiChatSendRequest request, MultipartFile file) {
        Locale requestLocale = AiReplyLanguageSupport.currentLocale();
        return Flux.defer(() -> AiReplyLanguageSupport.callWithLocale(requestLocale, () -> {
            validateChatRuntime();
            if (file == null || file.isEmpty()) {
                throw new ServiceException(message("ai.chat.requirement.file.required"));
            }
            AiChatSendRequest actualRequest = request == null ? new AiChatSendRequest() : request;
            actualRequest.setModeOverride(AiChatMode.REQUIREMENT_EVALUATION.name());
            if (StringUtils.isBlank(actualRequest.getChatMode())) {
                actualRequest.setChatMode(AiChatMode.AUTO.name());
            }
            if (StringUtils.isBlank(actualRequest.getModePolicy())) {
                actualRequest.setModePolicy(MODE_POLICY_AUTO);
            }

            AiChatAttachmentTextExtractor.ExtractedAttachment attachment = aiChatAttachmentTextExtractor.extract(file);
            actualRequest.setAttachmentFileName(attachment.fileName());
            actualRequest.setAttachmentContentType(attachment.contentType());
            actualRequest.setAttachmentText(attachment.text());
            String originalFilename = defaultText(attachment.fileName(), message("ai.chat.requirement.evaluation.file.fallback"));
            String question = defaultText(actualRequest.getMessage(), buildDefaultRequirementUploadQuestion(originalFilename));
            Locale replyLocale = AiReplyLanguageSupport.resolveReplyLocale(question, null, requestLocale);
            actualRequest.setMessage(question);
            String modePolicy = resolveModePolicy(actualRequest);
            String pinnedMode = resolvePinnedMode(actualRequest);
            String requestedMode = AiChatMode.REQUIREMENT_EVALUATION.name();
            AiChatIntentRouteVO intentRouteResult = buildRequirementEvaluationUploadIntentRoute(question, actualRequest, modePolicy, originalFilename);
            String sessionChatMode = resolveSessionChatMode(actualRequest, modePolicy, pinnedMode, requestedMode);
            boolean manualRoute = StringUtils.isNotBlank(actualRequest.getModelCode())
                    || StringUtils.isNotBlank(actualRequest.getProviderCode());
            String routeMode = resolveRouteMode(manualRoute);
            AiModelRouteVO route = StringUtils.isNotBlank(actualRequest.getModelCode())
                    ? aiModelRoutingService.resolveByModelCode(actualRequest.getModelCode())
                    : aiModelRoutingService.resolveDefaultRoute();
            SysUser currentUser = AiSecuritySupport.getCurrentUser();
            if (currentUser == null) {
                throw new ServiceException(message("ai.chat.current.user.required.create"));
            }

            AiChatSession session = resolveSession(actualRequest, question, route, currentUser, sessionChatMode, modePolicy, pinnedMode);
            int currentMessageNo = aiChatMessageService.countBySessionId(session.getSessionId());
            String operatorName = AiSecuritySupport.resolveUsername();
            AiChatMessage userMessage = buildMessage(session, currentMessageNo + 1, "user",
                    buildRequirementUploadUserMessage(question, originalFilename), route, requestedMode, routeMode);
            userMessage.setMessageStatus("SUCCESS");
            attachIntentRouteAudit(userMessage, intentRouteResult);
            aiChatMessageService.save(userMessage);

            AiChatMessage assistantMessage = buildMessage(session, currentMessageNo + 2, "assistant", "",
                    route, requestedMode, routeMode);
            assistantMessage.setToolName(SKILL_REQUIREMENT_EVALUATION);
            assistantMessage.setMessageSummary(message("ai.chat.requirement.evaluation.processing"));
            assistantMessage.setMessageStatus("RUNNING");
            aiChatMessageService.save(assistantMessage);
            refreshSessionWithOperator(session, route, sessionChatMode, operatorName);

            return Flux.create(sink -> AiReplyLanguageSupport.runWithLocale(replyLocale, () -> {
                long startAt = System.currentTimeMillis();
                sink.next(withRouteAudit(buildStreamEvent("start", session, route, requestedMode, requestedMode,
                        SKILL_REQUIREMENT_EVALUATION, "", "", null, null, false, null, null), intentRouteResult));
                try {
                    sink.next(withRouteAudit(buildStreamEvent("stage", session, route, requestedMode, requestedMode,
                            SKILL_REQUIREMENT_EVALUATION, message("ai.chat.requirement.evaluation.stage.extract"), "",
                            buildRequirementEvaluationProgressToolResult(originalFilename, "TEXT_EXTRACTED",
                                    message("ai.chat.requirement.evaluation.stage.extract")),
                            null, false, null, null), intentRouteResult));
                    sink.next(withRouteAudit(buildStreamEvent("stage", session, route, requestedMode, requestedMode,
                            SKILL_REQUIREMENT_EVALUATION, message("ai.chat.requirement.evaluation.stage.ai.evaluating"), "",
                            buildRequirementEvaluationProgressToolResult(originalFilename, "AI_EVALUATING",
                                    message("ai.chat.requirement.evaluation.stage.ai.evaluating")),
                            null, false, null, null), intentRouteResult));
                    AiRequirementEvaluationResultVO evaluationResult = aiRequirementEvaluationService.evaluateFromText(
                            originalFilename,
                            attachment.contentType(),
                            attachment.text(),
                            attachment.fileBytes(),
                            question,
                            route);
                    String answer = buildRequirementEvaluationAnswer(originalFilename, evaluationResult);
                    String toolResult = JSON.toJSONString(evaluationResult);
                    long durationMs = System.currentTimeMillis() - startAt;
                    putIntentRoutePerformanceMetric(intentRouteResult, "totalMs", durationMs);
                    putIntentRoutePerformanceMetric(intentRouteResult, "requirementItemCount",
                            evaluationResult.getRequirementItems() == null ? 0 : evaluationResult.getRequirementItems().size());
                    putIntentRoutePerformanceMetric(intentRouteResult, "matchLevel", evaluationResult.getMatchLevel());
                    updateIntentRouteAuditMessage(userMessage, intentRouteResult, operatorName);
                    finishAssistantMessage(assistantMessage, answer, requestedMode, SKILL_REQUIREMENT_EVALUATION, toolResult, durationMs, operatorName);
                    refreshSessionWithOperator(session, route, sessionChatMode, operatorName, requestedMode);
                    emitAnswerChunks(sink, session, route, requestedMode, requestedMode, SKILL_REQUIREMENT_EVALUATION, answer);
                    sink.next(withRouteAudit(buildStreamEvent("done", session, route, requestedMode, requestedMode,
                            SKILL_REQUIREMENT_EVALUATION, answer, answer, toolResult, null, true, null, null), intentRouteResult));
                    sink.complete();
                } catch (Exception ex) {
                    String answer = message("ai.chat.requirement.evaluation.failed", trimErrorMessage(ex.getMessage()));
                    String toolResult = buildRequirementEvaluationFailureToolResult(answer, originalFilename);
                    long durationMs = System.currentTimeMillis() - startAt;
                    putIntentRoutePerformanceMetric(intentRouteResult, "totalMs", durationMs);
                    putIntentRoutePerformanceMetric(intentRouteResult, "failed", true);
                    putIntentRoutePerformanceMetric(intentRouteResult, "errorMessage", trimErrorMessage(ex.getMessage()));
                    updateIntentRouteAuditMessage(userMessage, intentRouteResult, operatorName);
                    failAssistantMessage(assistantMessage, answer, requestedMode, SKILL_REQUIREMENT_EVALUATION, toolResult, durationMs, operatorName);
                    refreshSessionWithOperator(session, route, sessionChatMode, operatorName, requestedMode);
                    emitAnswerChunks(sink, session, route, requestedMode, requestedMode, SKILL_REQUIREMENT_EVALUATION, answer);
                    sink.next(withRouteAudit(buildStreamEvent("done", session, route, requestedMode, requestedMode,
                            SKILL_REQUIREMENT_EVALUATION, answer, answer, toolResult, null, true, null, null), intentRouteResult));
                    sink.complete();
                }
            }), FluxSink.OverflowStrategy.BUFFER);
        }));
    }

    private void validateChatRuntime() {
        if (!properties.isEnabled()) {
            throw new ServiceException(message("ai.chat.global.disabled"));
        }
        if (properties.getRuntime() != null && !properties.getRuntime().isChatEnabled()) {
            throw new ServiceException(message("ai.chat.general.disabled"));
        }
    }

    private void validateNl2SqlRuntime() {
        if (properties.getRuntime() != null && !properties.getRuntime().isNl2sqlEnabled()) {
            throw new ServiceException(message("ai.chat.nl2sql.disabled"));
        }
        if (!properties.getNl2sql().isEnabled()) {
            throw new ServiceException(message("ai.chat.nl2sql.disabled"));
        }
    }

    private void validateDeviceControlRuntime() {
        if (properties.getRuntime() != null && !properties.getRuntime().isDeviceControlEnabled()) {
            throw new ServiceException(message("ai.chat.device.control.disabled"));
        }
    }

    private void validateProtocolRuntime() {
        if (properties.getRuntime() != null && !properties.getRuntime().isProtocolEnabled()) {
            throw new ServiceException(message("ai.chat.protocol.disabled"));
        }
    }

    private AiChatExecutionContext prepareExecutionContext(AiChatSendRequest request) {
        AiModelRouteVO route = StringUtils.isNotBlank(request.getModelCode())
                ? aiModelRoutingService.resolveByModelCode(request.getModelCode())
                : aiModelRoutingService.resolveDefaultRoute();
        AiRuntimeModelSnapshot snapshot = aiRuntimeModelSnapshotService.resolveSnapshot(route);
        ChatModel chatModel = aiChatModelFactoryService.resolveChatModel(snapshot);
        return new AiChatExecutionContext(route, snapshot, chatModel);
    }

    private AiChatSession resolveSession(AiChatSendRequest request,
                                         String question,
                                         AiModelRouteVO route,
                                         SysUser currentUser,
                                         String sessionChatMode,
                                         String modePolicy,
                                         String pinnedMode) {
        if (request.getSessionId() != null) {
            AiChatSession session = aiChatSessionService.getById(request.getSessionId());
            if (session == null) {
                throw new ServiceException(message("ai.chat.session.not.found.refresh"));
            }
            if (session.getUserId() != null && !session.getUserId().equals(currentUser.getUserId())) {
                throw new ServiceException(message("ai.chat.session.not.current.user"));
            }
            session.setProviderCode(route.getProviderCode());
            session.setModelCode(route.getModelCode());
            applySessionStrategySnapshot(session, sessionChatMode, modePolicy, pinnedMode);
            session.setLastMessageTime(AiSecuritySupport.now());
            session.setUpdateBy(AiSecuritySupport.resolveUsername());
            session.setUpdateTime(AiSecuritySupport.now());
            aiChatSessionService.updateById(session);
            return session;
        }
        AiChatSession session = new AiChatSession();
        session.setSessionCode(AiSecuritySupport.newSessionCode());
        session.setSessionTitle(buildSessionTitle(question));
        session.setUserId(currentUser.getUserId());
        session.setTenantId(AiSecuritySupport.resolveTenantId(currentUser));
        session.setTenantName(AiSecuritySupport.resolveTenantName(currentUser));
        session.setProviderCode(route.getProviderCode());
        session.setModelCode(route.getModelCode());
        applySessionStrategySnapshot(session, sessionChatMode, modePolicy, pinnedMode);
        session.setLastEffectiveMode(null);
        session.setIsArchived("0");
        session.setStatus("0");
        session.setLastMessageTime(AiSecuritySupport.now());
        session.setCreateBy(AiSecuritySupport.resolveUsername());
        session.setCreateTime(AiSecuritySupport.now());
        session.setUpdateBy(AiSecuritySupport.resolveUsername());
        session.setUpdateTime(AiSecuritySupport.now());
        aiChatSessionService.save(session);
        return session;
    }

    private void refreshSession(AiChatSession session, AiModelRouteVO route, String sessionChatMode) {
        refreshSessionWithOperator(session, route, sessionChatMode, AiSecuritySupport.resolveUsername(), null);
    }

    private void refreshSession(AiChatSession session, AiModelRouteVO route, String sessionChatMode, String latestEffectiveMode) {
        refreshSessionWithOperator(session, route, sessionChatMode, AiSecuritySupport.resolveUsername(), latestEffectiveMode);
    }

    /**
     * 流式链路在异步线程里可能拿不到 Spring Security 上下文。
     * 这里显式传入请求线程捕获的操作人，避免在刷新会话时再次读取登录态。
     */
    private void refreshSessionWithOperator(AiChatSession session,
                                            AiModelRouteVO route,
                                            String sessionChatMode,
                                            String operatorName) {
        refreshSessionWithOperator(session, route, sessionChatMode, operatorName, null);
    }

    /**
     * 流式链路在异步线程里可能拿不到 Spring Security 上下文。
     * 这里显式传入请求线程捕获的操作人，避免在刷新会话时再次读取登录态。
     */
    private void refreshSessionWithOperator(AiChatSession session,
                                            AiModelRouteVO route,
                                            String sessionChatMode,
                                            String operatorName,
                                            String latestEffectiveMode) {
        session.setProviderCode(route.getProviderCode());
        session.setModelCode(route.getModelCode());
        session.setChatMode(sessionChatMode);
        session.setModePolicy(resolveSessionModePolicy(session));
        session.setPinnedMode(resolveSessionPinnedMode(session));
        if (StringUtils.isNotBlank(latestEffectiveMode)) {
            session.setLastEffectiveMode(resolveExecutionMode(latestEffectiveMode));
        }
        session.setLastMessageTime(AiSecuritySupport.now());
        session.setUpdateBy(StringUtils.isBlank(operatorName) ? session.getUpdateBy() : operatorName);
        session.setUpdateTime(AiSecuritySupport.now());
        aiChatSessionService.updateById(session);
    }

    private void applySessionStrategySnapshot(AiChatSession session,
                                              String sessionChatMode,
                                              String modePolicy,
                                              String pinnedMode) {
        if (session == null) {
            return;
        }
        String normalizedModePolicy = normalizeModePolicy(modePolicy);
        session.setChatMode(resolveChatMode(sessionChatMode));
        session.setModePolicy(StringUtils.isBlank(normalizedModePolicy) ? MODE_POLICY_AUTO : normalizedModePolicy);
        session.setPinnedMode(resolvePinnedModeSnapshot(normalizedModePolicy, pinnedMode, sessionChatMode));
    }

    private AiChatMessage buildMessage(AiChatSession session, int messageNo, String roleType, String content, AiModelRouteVO route, String abilityType, String routeMode) {
        AiChatMessage message = new AiChatMessage();
        message.setSessionId(session.getSessionId());
        message.setMessageNo(messageNo);
        message.setRoleType(roleType);
        message.setMessageContent(content);
        message.setMessageSummary(buildSessionTitle(content));
        message.setProviderCode(route.getProviderCode());
        message.setModelCode(route.getModelCode());
        message.setAbilityType(abilityType);
        message.setRouteMode(routeMode);
        message.setTokenUsage(0);
        message.setDurationMs(0L);
        message.setRiskConfirmed("0");
        message.setCreateBy(AiSecuritySupport.resolveUsername());
        message.setCreateTime(AiSecuritySupport.now());
        message.setUpdateBy(AiSecuritySupport.resolveUsername());
        message.setUpdateTime(AiSecuritySupport.now());
        return message;
    }

    private List<AiChatMessage> loadConversationHistory(Long sessionId) {
        if (sessionId == null) {
            return new ArrayList<>();
        }
        AiChatMessage query = new AiChatMessage();
        query.setSessionId(sessionId);
        List<AiChatMessage> list = aiChatMessageService.listAiChatMessage(query);
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        int maxHistory = properties.getRuntime() == null ? 0 : properties.getRuntime().getMaxMessagesPerSession();
        return maxHistory > 0 && list.size() > maxHistory ? new ArrayList<>(list.subList(list.size() - maxHistory, list.size())) : new ArrayList<>(list);
    }

    private AiModeExecutionResult executeByMode(String effectiveMode,
                                                String question,
                                                AiModelRouteVO route,
                                                ChatModel chatModel,
                                                List<AiChatMessage> historyMessages,
                                                AiChatIntentRouteVO intentRouteResult) {
        if (AiChatMode.NL2SQL.name().equals(effectiveMode)) {
            return executeNl2SqlAbility(question, route);
        }
        if (AiChatMode.DEVICE_CONTROL.name().equals(effectiveMode)) {
            return executeDeviceControlAbility(question, intentRouteResult);
        }
        if (AiChatMode.PROTOCOL_PARSE.name().equals(effectiveMode)) {
            return executeProtocolParse(question, chatModel, historyMessages);
        }
        if (AiChatMode.THING_MODEL_GENERATE.name().equals(effectiveMode)) {
            return executeThingModelGenerateGuide();
        }
        if (AiChatMode.REQUIREMENT_EVALUATION.name().equals(effectiveMode)) {
            return executeRequirementEvaluationGuide();
        }
        if (AiChatMode.PLATFORM_ASSISTANT.name().equals(effectiveMode)) {
            return executePlatformAssistantChat(question, chatModel, historyMessages);
        }
        if (AiChatMode.GENERAL.name().equals(effectiveMode) && isAssistantModelIdentityQuestion(normalizeRouteQuestion(question))) {
            return executeAssistantModelIdentityQuestion(route);
        }
        return executeGeneralChat(question, chatModel, historyMessages, effectiveMode);
    }

    private AiModeExecutionResult executeThingModelGenerateGuide() {
        String answer = message("ai.chat.thing.model.generate.guide.answer");
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("summary", answer);
        result.put("status", "WAITING_FILE");
        result.put("missingInformation", List.of(message("ai.chat.thing.model.generate.guide.missing.file")));
        result.put("keyPoints", List.of(
                message("ai.chat.thing.model.generate.guide.supported.files"),
                message("ai.chat.thing.model.generate.guide.output")
        ));
        return new AiModeExecutionResult(answer,
                AiChatMode.THING_MODEL_GENERATE.name(),
                SKILL_THING_MODEL_GENERATE,
                JSON.toJSONString(result));
    }

    private AiModeExecutionResult executeRequirementEvaluationGuide() {
        String answer = message("ai.chat.requirement.evaluation.guide.answer");
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("summary", answer);
        result.put("overallConclusion", message("ai.chat.requirement.evaluation.guide.overall"));
        result.put("matchLevel", "UNKNOWN");
        result.put("status", "WAITING_FILE");
        result.put("pendingQuestions", List.of(message("ai.chat.requirement.evaluation.guide.pending.file")));
        result.put("keyPoints", List.of(
                message("ai.chat.requirement.evaluation.guide.supported.files"),
                message("ai.chat.requirement.evaluation.guide.result.types")
        ));
        result.put("disclaimer", message("ai.chat.requirement.evaluation.disclaimer"));
        return new AiModeExecutionResult(answer,
                AiChatMode.REQUIREMENT_EVALUATION.name(),
                SKILL_REQUIREMENT_EVALUATION,
                JSON.toJSONString(result));
    }

    private AiModeExecutionResult executeGeneralChat(String question, ChatModel chatModel, List<AiChatMessage> historyMessages, String effectiveMode) {
        return new AiModeExecutionResult(callModel(chatModel, buildConversationPrompt(historyMessages, question, effectiveMode, null)), effectiveMode, SKILL_GENERAL_CHAT, null);
    }

    private AiModeExecutionResult executeAssistantModelIdentityQuestion(AiModelRouteVO route) {
        return new AiModeExecutionResult(buildAssistantModelIdentityAnswer(route),
                AiChatMode.GENERAL.name(),
                SKILL_GENERAL_CHAT,
                buildAssistantModelIdentityToolResult(route));
    }

    private String buildAssistantModelIdentityAnswer(AiModelRouteVO route) {
        String modelText = resolveModelIdentityText(route == null ? null : route.getModelName(),
                route == null ? null : route.getModelCode(),
                "当前会话路由配置中的模型");
        String providerText = resolveModelIdentityText(route == null ? null : route.getProviderName(),
                route == null ? null : route.getProviderCode(),
                null);
        StringBuilder answer = new StringBuilder("当前这轮对话实际使用的模型是 ").append(modelText);
        if (StringUtils.isNotBlank(providerText)) {
            answer.append("，模型厂商是 ").append(providerText);
        }
        answer.append("。该信息来自本轮后端模型路由配置，不依赖模型自行推断；如果后台切换了会话模型配置，下一轮会以新的路由结果为准。");
        return answer.toString();
    }

    private String resolveModelIdentityText(String name, String code, String defaultText) {
        String actualName = StringUtils.isBlank(name) ? null : name.trim();
        String actualCode = StringUtils.isBlank(code) ? null : code.trim();
        if (StringUtils.isNotBlank(actualName) && StringUtils.isNotBlank(actualCode)) {
            return actualName + "（" + actualCode + "）";
        }
        if (StringUtils.isNotBlank(actualCode)) {
            return actualCode;
        }
        if (StringUtils.isNotBlank(actualName)) {
            return actualName;
        }
        return defaultText;
    }

    private String buildAssistantModelIdentityToolResult(AiModelRouteVO route) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("status", "STATIC_MODEL_IDENTITY");
        result.put("providerCode", route == null ? null : route.getProviderCode());
        result.put("providerName", route == null ? null : route.getProviderName());
        result.put("modelCode", route == null ? null : route.getModelCode());
        result.put("modelName", route == null ? null : route.getModelName());
        return JSON.toJSONString(result);
    }

    private AiModeExecutionResult executePlatformAssistantChat(String question, ChatModel chatModel, List<AiChatMessage> historyMessages) {
        AiPlatformDocKnowledgeContextVO context = resolvePlatformAssistantContext(question);
        String normalizedQuestion = normalizeAssistantQuestion(question);
        String safetyAnswer = buildCodebaseSafetyFallbackAnswer(question, context);
        if (StringUtils.isNotBlank(safetyAnswer)) {
            AiPlatformAssistantContextSnapshotVO contextSnapshot = aiPlatformAssistantConversationContextService == null
                    ? null
                    : aiPlatformAssistantConversationContextService.buildContextSnapshot(question, context);
            return new AiModeExecutionResult(safetyAnswer,
                    AiChatMode.PLATFORM_ASSISTANT.name(),
                    resolvePlatformAssistantSkill(context),
                    null,
                    contextSnapshot);
        }
        if (isGenericInterfaceCallSiteQuestion(normalizedQuestion)) {
            AiPlatformAssistantContextSnapshotVO contextSnapshot = aiPlatformAssistantConversationContextService == null
                    ? null
                    : aiPlatformAssistantConversationContextService.buildContextSnapshot(question, context);
            return new AiModeExecutionResult(buildGenericInterfaceCallSiteClarificationAnswer(),
                    AiChatMode.PLATFORM_ASSISTANT.name(),
                    resolvePlatformAssistantSkill(context),
                    null,
                    contextSnapshot);
        }
        String instruction = buildPlatformAssistantInstruction(context);
        String executedSkill = resolvePlatformAssistantSkill(context);
        String promptMode = resolvePlatformAssistantPromptMode(context);
        String answer = callModel(chatModel, buildConversationPrompt(historyMessages, question, promptMode, instruction));
        answer = sanitizeCodebaseAssistantAnswerIfNecessary(question, AiChatMode.PLATFORM_ASSISTANT.name(), answer, null);
        AiPlatformAssistantContextSnapshotVO contextSnapshot = aiPlatformAssistantConversationContextService == null
                ? null
                : aiPlatformAssistantConversationContextService.buildContextSnapshot(question, context);
        return new AiModeExecutionResult(answer,
                AiChatMode.PLATFORM_ASSISTANT.name(),
                executedSkill,
                null,
                contextSnapshot);
    }

    private AiModeExecutionResult executeProtocolParse(String question, ChatModel chatModel, List<AiChatMessage> historyMessages) {
        validateProtocolRuntime();
        AiProtocolKnowledgeContextVO context = resolveProtocolParseContext(question);
        String instruction = buildProtocolParseInstruction(context);
        String answer = callModel(chatModel, buildConversationPrompt(historyMessages, question, AiChatMode.PROTOCOL_PARSE.name(), instruction));
        AiProtocolParseContextSnapshotVO contextSnapshot = aiProtocolParseConversationContextService == null
                ? null
                : aiProtocolParseConversationContextService.buildContextSnapshot(question, context);
        return new AiModeExecutionResult(answer,
                AiChatMode.PROTOCOL_PARSE.name(),
                SKILL_PROTOCOL_PARSE,
                buildProtocolParseToolResult(answer),
                contextSnapshot);
    }

    private AiModeExecutionResult executeNl2SqlAbility(String question, AiModelRouteVO route) {
        validateNl2SqlRuntime();
        String sqlText = extractSqlStatement(question);
        if (StringUtils.isNotBlank(sqlText)) {
            AiNl2SqlQueryRequest queryRequest = new AiNl2SqlQueryRequest();
            queryRequest.setSqlText(sqlText);
            AiNl2SqlQueryResultVO result = aiNl2SqlQueryService.executeReadOnlyQuery(queryRequest);
            return new AiModeExecutionResult(formatNl2SqlPreviewResult(result), AiChatMode.NL2SQL.name(), SKILL_NL2SQL_SQL_PREVIEW, JSON.toJSONString(result));
        }
        try {
            AiNl2SqlGenerateResultVO generateResult = aiNl2SqlWorkflowService.generateAndExecute(question, null, route);
            AiNl2SqlContextSnapshotVO contextSnapshot = aiNl2SqlConversationContextService == null
                    ? null
                    : aiNl2SqlConversationContextService.buildContextSnapshot(question, generateResult);
            return new AiModeExecutionResult(formatUnifiedNl2SqlResult(generateResult),
                    AiChatMode.NL2SQL.name(),
                    SKILL_NL2SQL_GENERATE,
                    JSON.toJSONString(generateResult),
                    contextSnapshot);
        } catch (Exception ex) {
            if (isPresetNl2SqlQuestion(question)) {
                return executePresetNl2SqlSkill(question);
            }
            throw ex instanceof ServiceException ? (ServiceException) ex
                    : new ServiceException(StringUtils.isNotBlank(ex.getMessage()) ? ex.getMessage() : message("ai.chat.nl2sql.generate.sql.failed"));
        }
    }

    private boolean isPresetNl2SqlQuestion(String question) {
        String normalizedQuestion = StringUtils.defaultString(question).toLowerCase(Locale.ROOT);
        return containsAny(normalizedQuestion,
                "告警处理", "处理告警", "告警处理统计", "告警级别", "告警等级", "告警级别统计", "指令下发", "物模型调用", "调用统计",
                "alert process", "alert handling", "alert level", "alarm level", "command dispatch", "thing model invoke", "invoke statistics");
    }

    private AiModeExecutionResult executePresetNl2SqlSkill(String question) {
        DataCenterParam dataCenterParam = extractDataCenterParam(question);
        String normalizedQuestion = StringUtils.defaultString(question).toLowerCase(Locale.ROOT);
        if (containsAny(normalizedQuestion, "告警处理", "处理告警", "告警处理统计", "alert process", "alert handling")) {
            List<AlertCountVO> result = aiDataQuerySkillService.countAlertProcess(dataCenterParam);
            return new AiModeExecutionResult(formatAlertCountResult(message("ai.chat.nl2sql.alert.process.title"), result), AiChatMode.NL2SQL.name(), SKILL_NL2SQL_ALERT_PROCESS, JSON.toJSONString(result));
        }
        if (containsAny(normalizedQuestion, "告警级别", "告警等级", "告警级别统计", "alert level", "alarm level")) {
            List<AlertCountVO> result = aiDataQuerySkillService.countAlertLevel(dataCenterParam);
            return new AiModeExecutionResult(formatAlertCountResult(message("ai.chat.nl2sql.alert.level.title"), result), AiChatMode.NL2SQL.name(), SKILL_NL2SQL_ALERT_LEVEL, JSON.toJSONString(result));
        }
        if (containsAny(normalizedQuestion, "指令下发", "物模型调用", "调用统计", "command dispatch", "thing model invoke", "invoke statistics")) {
            if (StringUtils.isBlank(dataCenterParam.getSerialNumber())) {
                return new AiModeExecutionResult(buildNl2SqlGuide(), AiChatMode.NL2SQL.name(), SKILL_NL2SQL_GUIDE, null);
            }
            List<ThingsModelLogCountVO> result = aiDataQuerySkillService.countThingsModelInvoke(dataCenterParam);
            return new AiModeExecutionResult(formatThingsModelInvokeResult(dataCenterParam.getSerialNumber(), result), AiChatMode.NL2SQL.name(), SKILL_NL2SQL_MODEL_INVOKE, JSON.toJSONString(result));
        }
        return new AiModeExecutionResult(buildNl2SqlGuide(), AiChatMode.NL2SQL.name(), SKILL_NL2SQL_GUIDE, null);
    }

    private AiModeExecutionResult executeDeviceControlAbility(String question, AiChatIntentRouteVO intentRouteResult) {
        validateDeviceControlRuntime();
        String normalizedQuestion = StringUtils.defaultString(question).toLowerCase(Locale.ROOT);
        if (containsAny(normalizedQuestion, "服务下发回执", "等待回执", "invokereply", "invoke reply", "wait reply")) {
            String jsonBody = extractJsonBody(question);
            if (StringUtils.isBlank(jsonBody)) {
                return new AiModeExecutionResult(buildDeviceControlGuide(), AiChatMode.DEVICE_CONTROL.name(), SKILL_DEVICE_GUIDE, null);
            }
            return buildAjaxActionResult(message("ai.chat.device.control.invoke.reply.success"), AiChatMode.DEVICE_CONTROL.name(), SKILL_DEVICE_INVOKE_REPLY, aiDeviceControlSkillService.invokeReply(JSON.parseObject(jsonBody, InvokeReqDto.class)));
        }
        if (containsAny(normalizedQuestion, "服务下发", "invokenoreply", "invoke no reply", "invoke")) {
            String jsonBody = extractJsonBody(question);
            if (StringUtils.isBlank(jsonBody)) {
                return new AiModeExecutionResult(buildDeviceControlGuide(), AiChatMode.DEVICE_CONTROL.name(), SKILL_DEVICE_GUIDE, null);
            }
            return buildAjaxActionResult(message("ai.chat.device.control.invoke.success"), AiChatMode.DEVICE_CONTROL.name(), SKILL_DEVICE_INVOKE, aiDeviceControlSkillService.invokeNoReply(JSON.parseObject(jsonBody, InvokeReqDto.class)));
        }
        if (containsAny(normalizedQuestion, "指令生成", "commandgenerate", "command generate", "generate command")) {
            String jsonBody = extractJsonBody(question);
            if (StringUtils.isBlank(jsonBody)) {
                return new AiModeExecutionResult(buildDeviceControlGuide(), AiChatMode.DEVICE_CONTROL.name(), SKILL_DEVICE_GUIDE, null);
            }
            String command = aiDeviceControlSkillService.commandGenerate(JSON.parseObject(jsonBody, MQSendMessageBo.class));
            return new AiModeExecutionResult(message("ai.chat.device.control.command.success", command), AiChatMode.DEVICE_CONTROL.name(), SKILL_DEVICE_COMMAND, command);
        }
        if (containsAny(normalizedQuestion, "执行场景", "运行场景", "runscene", "run scene", "execute scene")) {
            Long sceneId = extractLongValue(question, "sceneId", "场景ID", "场景");
            if (sceneId == null) {
                return new AiModeExecutionResult(buildDeviceControlGuide(), AiChatMode.DEVICE_CONTROL.name(), SKILL_DEVICE_GUIDE, null);
            }
            aiDeviceControlSkillService.runScene(sceneId);
            return new AiModeExecutionResult(message("ai.chat.device.control.scene.triggered", sceneId), AiChatMode.DEVICE_CONTROL.name(), SKILL_DEVICE_SCENE, "{\"sceneId\":" + sceneId + "}");
        }
        AiDeviceControlIntentVO controlIntent = aiDeviceControlIntentService.resolveIntent(question, intentRouteResult);
        if (controlIntent.getDeviceCandidates() != null && controlIntent.getDeviceCandidates().size() > 1) {
            return new AiModeExecutionResult(buildDeviceControlDeviceClarifyPreview(limitDeviceClarifyCandidates(controlIntent.getDeviceCandidates())),
                    AiChatMode.DEVICE_CONTROL.name(), SKILL_DEVICE_GUIDE, null);
        }
        if (controlIntent.getThingModelCandidates() != null && controlIntent.getThingModelCandidates().size() > 1) {
            return new AiModeExecutionResult(buildDeviceControlThingModelClarifyPreview(limitThingModelClarifyCandidates(controlIntent.getThingModelCandidates())),
                    AiChatMode.DEVICE_CONTROL.name(), SKILL_DEVICE_GUIDE, null);
        }
        if (shouldGenerateDeviceCommand(question, intentRouteResult)) {
            if (controlIntent.getCommandRequest() == null) {
                return new AiModeExecutionResult(buildDeviceControlGuide(), AiChatMode.DEVICE_CONTROL.name(), SKILL_DEVICE_GUIDE, null);
            }
            String command = aiDeviceControlSkillService.commandGenerate(controlIntent.getCommandRequest());
            AiDeviceControlContextSnapshotVO contextSnapshot = buildDeviceControlContextSnapshot(question, intentRouteResult, controlIntent);
            return new AiModeExecutionResult(message("ai.chat.device.control.command.success", command),
                    AiChatMode.DEVICE_CONTROL.name(), SKILL_DEVICE_COMMAND, command, contextSnapshot);
        }
        if (controlIntent.getInvokeRequest() == null) {
            return new AiModeExecutionResult(buildDeviceControlIntentMissingMessage(controlIntent),
                    AiChatMode.DEVICE_CONTROL.name(), SKILL_DEVICE_GUIDE, null);
        }
        AiDeviceControlContextSnapshotVO contextSnapshot = buildDeviceControlContextSnapshot(question, intentRouteResult, controlIntent);
        if (shouldInvokeReply(question, intentRouteResult)) {
            return buildAjaxActionResult(message("ai.chat.device.control.invoke.reply.success"),
                    AiChatMode.DEVICE_CONTROL.name(),
                    SKILL_DEVICE_INVOKE_REPLY,
                    aiDeviceControlSkillService.invokeReply(controlIntent.getInvokeRequest()),
                    contextSnapshot);
        }
        return buildAjaxActionResult(message("ai.chat.device.control.invoke.success"),
                AiChatMode.DEVICE_CONTROL.name(),
                SKILL_DEVICE_INVOKE,
                aiDeviceControlSkillService.invokeNoReply(controlIntent.getInvokeRequest()),
                contextSnapshot);
    }

    private boolean shouldInvokeReply(String question, AiChatIntentRouteVO intentRouteResult) {
        String normalizedQuestion = StringUtils.defaultString(question).toLowerCase(Locale.ROOT);
        if (containsAny(normalizedQuestion, "服务下发回执", "等待回执", "invokereply", "invoke reply", "wait reply")) {
            return true;
        }
        return intentRouteResult != null && "等待回执".equals(intentRouteResult.getActionText());
    }

    private boolean shouldGenerateDeviceCommand(String question, AiChatIntentRouteVO intentRouteResult) {
        String normalizedQuestion = StringUtils.defaultString(question).toLowerCase(Locale.ROOT);
        if (containsAny(normalizedQuestion, "指令生成", "commandgenerate", "command generate", "generate command")) {
            return true;
        }
        return intentRouteResult != null && "DEVICE_COMMAND_GENERATE".equals(intentRouteResult.getBusinessType());
    }

    private AiModeExecutionResult buildAjaxActionResult(String successMessage, String effectiveMode, String skillName, AjaxResult ajaxResult) {
        return buildAjaxActionResult(successMessage, effectiveMode, skillName, ajaxResult, null);
    }

    private AiModeExecutionResult buildAjaxActionResult(String successMessage,
                                                        String effectiveMode,
                                                        String skillName,
                                                        AjaxResult ajaxResult,
                                                        AiDeviceControlContextSnapshotVO contextSnapshot) {
        String rawResult = JSON.toJSONString(ajaxResult);
        Object code = ajaxResult.get("code");
        if (code != null && !"200".equals(String.valueOf(code))) {
            throw new ServiceException(String.valueOf(ajaxResult.getOrDefault("msg", message("ai.chat.device.control.execute.failed"))));
        }
        Object data = ajaxResult.get("data");
        Object responseMessage = ajaxResult.get("msg");
        StringBuilder answerBuilder = new StringBuilder(successMessage);
        if (data != null && !"null".equals(String.valueOf(data))) {
            answerBuilder.append('\n').append(message("ai.chat.common.label.return.result")).append(JSON.toJSONString(data));
        } else if (responseMessage != null && !message("ai.chat.common.operation.success").equals(String.valueOf(responseMessage)) && !"操作成功".equals(String.valueOf(responseMessage))) {
            answerBuilder.append('\n').append(message("ai.chat.common.label.return.message")).append(responseMessage);
        }
        return new AiModeExecutionResult(answerBuilder.toString(), effectiveMode, skillName, rawResult, contextSnapshot);
    }

    private AiDeviceControlContextSnapshotVO buildDeviceControlContextSnapshot(String question,
                                                                               AiChatIntentRouteVO intentRouteResult,
                                                                               AiDeviceControlIntentVO controlIntent) {
        return aiDeviceControlConversationContextService.buildContextSnapshot(
                intentRouteResult,
                controlIntent,
                aiDeviceControlConfirmService.isRiskConfirmedQuestion(question) ? "1" : "0"
        );
    }

    private void streamModelConversation(FluxSink<AiChatStreamEventVO> sink,
                                         AiChatSession session,
                                         AiModelRouteVO route,
                                         ChatModel chatModel,
                                         String requestedMode,
                                         String effectiveMode,
                                         String question,
                                         List<AiChatMessage> historyMessages,
                                         AiChatMessage userMessage,
                                         AiChatMessage assistantMessage,
                                         String operatorName,
                                         long startAt,
                                         long prepareStartAt,
                                         AiChatIntentRouteVO intentRouteResult) {
        long contextStartAt = System.currentTimeMillis();
        String extraInstruction = null;
        String executedSkill = SKILL_GENERAL_CHAT;
        String promptMode = effectiveMode;
        AiPlatformDocKnowledgeContextVO platformContext = null;
        AiPlatformAssistantContextSnapshotVO platformContextSnapshot = null;
        AiProtocolKnowledgeContextVO protocolContext = null;
        AiProtocolParseContextSnapshotVO protocolContextSnapshot = null;
        if (AiChatMode.GENERAL.name().equals(effectiveMode) && isAssistantModelIdentityQuestion(normalizeRouteQuestion(question))) {
            String answer = buildAssistantModelIdentityAnswer(route);
            String toolResult = buildAssistantModelIdentityToolResult(route);
            long durationMs = System.currentTimeMillis() - startAt;
            putIntentRoutePerformanceMetric(intentRouteResult, "executionType", "STATIC_MODEL_IDENTITY");
            putIntentRoutePerformanceMetric(intentRouteResult, "contextBuildMs", System.currentTimeMillis() - contextStartAt);
            putIntentRoutePerformanceMetric(intentRouteResult, "modelStreamMs", 0L);
            putIntentRoutePerformanceMetric(intentRouteResult, "chunkCount", 1L);
            putIntentRoutePerformanceMetric(intentRouteResult, "answerChars", answer.length());
            putIntentRoutePerformanceMetric(intentRouteResult, "totalMs", System.currentTimeMillis() - prepareStartAt);
            updateIntentRouteAuditMessage(userMessage, intentRouteResult, operatorName);
            finishAssistantMessage(assistantMessage, answer, effectiveMode, executedSkill, toolResult, durationMs, operatorName);
            refreshSessionWithOperator(session, route, requestedMode, operatorName, effectiveMode);
            sink.next(buildStreamEvent("chunk", session, route, requestedMode, effectiveMode, executedSkill,
                    answer, answer, toolResult, null, false, null, null));
            AiChatStreamEventVO doneEvent = buildStreamEvent("done", session, route, requestedMode, effectiveMode,
                    executedSkill, answer, answer, toolResult, null, true, null, null);
            doneEvent.setRouteAudit(serializeIntentRouteAudit(intentRouteResult));
            sink.next(doneEvent);
            sink.complete();
            return;
        }
        if (AiChatMode.PROTOCOL_PARSE.name().equals(effectiveMode)) {
            validateProtocolRuntime();
            protocolContext = resolveProtocolParseContext(question);
            extraInstruction = buildProtocolParseInstruction(protocolContext);
            protocolContextSnapshot = aiProtocolParseConversationContextService == null
                    ? null
                    : aiProtocolParseConversationContextService.buildContextSnapshot(question, protocolContext);
            executedSkill = SKILL_PROTOCOL_PARSE;
        } else if (AiChatMode.PLATFORM_ASSISTANT.name().equals(effectiveMode)) {
            platformContext = resolvePlatformAssistantContext(question);
            extraInstruction = buildPlatformAssistantInstruction(platformContext);
            platformContextSnapshot = aiPlatformAssistantConversationContextService == null
                    ? null
                    : aiPlatformAssistantConversationContextService.buildContextSnapshot(question, platformContext);
            executedSkill = resolvePlatformAssistantSkill(platformContext);
            promptMode = resolvePlatformAssistantPromptMode(platformContext);
            putIntentRoutePerformanceMetric(intentRouteResult, "platformAssistantPromptMode", promptMode);
            String safetyAnswer = buildCodebaseSafetyFallbackAnswer(question, platformContext);
            if (StringUtils.isNotBlank(safetyAnswer)) {
                long durationMs = System.currentTimeMillis() - startAt;
                if (platformContextSnapshot != null) {
                    persistConversationContextSnapshot(userMessage, buildContextSnapshotExecutionResult(
                            safetyAnswer,
                            effectiveMode,
                            executedSkill,
                            null,
                            platformContextSnapshot,
                            null
                    ), operatorName);
                }
                putIntentRoutePerformanceMetric(intentRouteResult, "executionType", "STATIC_SAFETY_FALLBACK");
                putIntentRoutePerformanceMetric(intentRouteResult, "modelStreamMs", 0L);
                putIntentRoutePerformanceMetric(intentRouteResult, "chunkCount", 1L);
                putIntentRoutePerformanceMetric(intentRouteResult, "answerChars", safetyAnswer.length());
                putIntentRoutePerformanceMetric(intentRouteResult, "totalMs", System.currentTimeMillis() - prepareStartAt);
                putIntentRoutePerformanceMetric(intentRouteResult, "codebaseSafetyFallback", true);
                updateIntentRouteAuditMessage(userMessage, intentRouteResult, operatorName);
                finishAssistantMessage(assistantMessage, safetyAnswer, effectiveMode, executedSkill, null, durationMs, operatorName);
                refreshSessionWithOperator(session, route, requestedMode, operatorName, effectiveMode);
                sink.next(buildStreamEvent("chunk", session, route, requestedMode, effectiveMode, executedSkill,
                        safetyAnswer, safetyAnswer, null, null, false, null, null));
                AiChatStreamEventVO doneEvent = buildStreamEvent("done", session, route, requestedMode, effectiveMode,
                        executedSkill, safetyAnswer, safetyAnswer, null, null, true, null, null);
                doneEvent.setRouteAudit(serializeIntentRouteAudit(intentRouteResult));
                sink.next(doneEvent);
                sink.complete();
                return;
            }
            if (isGenericInterfaceCallSiteQuestion(normalizeAssistantQuestion(question))) {
                String answer = buildGenericInterfaceCallSiteClarificationAnswer();
                long durationMs = System.currentTimeMillis() - startAt;
                if (platformContextSnapshot != null) {
                    persistConversationContextSnapshot(userMessage, buildContextSnapshotExecutionResult(
                            answer,
                            effectiveMode,
                            executedSkill,
                            null,
                            platformContextSnapshot,
                            null
                    ), operatorName);
                }
                putIntentRoutePerformanceMetric(intentRouteResult, "executionType", "STATIC_CLARIFICATION");
                putIntentRoutePerformanceMetric(intentRouteResult, "modelStreamMs", 0L);
                putIntentRoutePerformanceMetric(intentRouteResult, "chunkCount", 1L);
                putIntentRoutePerformanceMetric(intentRouteResult, "answerChars", answer.length());
                putIntentRoutePerformanceMetric(intentRouteResult, "totalMs", System.currentTimeMillis() - prepareStartAt);
                updateIntentRouteAuditMessage(userMessage, intentRouteResult, operatorName);
                finishAssistantMessage(assistantMessage, answer, effectiveMode, executedSkill, null, durationMs, operatorName);
                refreshSessionWithOperator(session, route, requestedMode, operatorName);
                sink.next(buildStreamEvent("chunk", session, route, requestedMode, effectiveMode, executedSkill,
                        answer, answer, null, null, false, null, null));
                AiChatStreamEventVO doneEvent = buildStreamEvent("done", session, route, requestedMode, effectiveMode,
                        executedSkill, answer, answer, null, null, true, null, null);
                doneEvent.setRouteAudit(serializeIntentRouteAudit(intentRouteResult));
                sink.next(doneEvent);
                sink.complete();
                return;
            }
        }
        putIntentRoutePerformanceMetric(intentRouteResult, "executionType", "TOKEN_STREAM");
        putIntentRoutePerformanceMetric(intentRouteResult, "contextBuildMs", System.currentTimeMillis() - contextStartAt);
        String prompt = buildConversationPrompt(historyMessages, question, promptMode, extraInstruction);
        putIntentRoutePerformanceMetric(intentRouteResult, "promptChars", prompt.length());
        AtomicBoolean emittedChunk = new AtomicBoolean(false);
        AtomicBoolean terminalCompleted = new AtomicBoolean(false);
        AtomicLong firstChunkAt = new AtomicLong(0L);
        AtomicLong chunkCount = new AtomicLong(0L);
        AtomicLong lastPartialPersistAt = new AtomicLong(0L);
        StringBuilder answerBuilder = new StringBuilder();
        String finalExecutedSkill = executedSkill;
        final AiPlatformAssistantContextSnapshotVO finalPlatformContextSnapshot = platformContextSnapshot;
        final AiProtocolParseContextSnapshotVO finalProtocolContextSnapshot = protocolContextSnapshot;
        boolean bufferForCodebaseSafety = shouldBufferPlatformAssistantAnswerForSafety(effectiveMode, question);
        if (bufferForCodebaseSafety) {
            putIntentRoutePerformanceMetric(intentRouteResult, "codebaseSafetyBuffered", true);
        }
        Flux<String> modelStream = streamModel(chatModel, prompt)
                .timeout(Duration.ofSeconds(MODEL_STREAM_IDLE_TIMEOUT_SECONDS))
                .onErrorResume(ex -> {
                    if (isModelStreamIdleTimeout(ex) && emittedChunk.get()) {
                        return Flux.empty();
                    }
                    if (emittedChunk.get()) {
                        return Flux.error(ex);
                    }
                    return Flux.just(callModel(chatModel, prompt));
                });
        Disposable disposable = modelStream.subscribe(delta -> {
            if (StringUtils.isBlank(delta)) {
                return;
            }
            long now = System.currentTimeMillis();
            if (firstChunkAt.compareAndSet(0L, now)) {
                putIntentRoutePerformanceMetric(intentRouteResult, "firstTokenMs", now - startAt);
                log.info("AI会话模型流首片输出: sessionCode={}, effectiveMode={}, modelCode={}, firstTokenMs={}",
                        session.getSessionCode(), effectiveMode, route == null ? null : route.getModelCode(), now - startAt);
            }
            chunkCount.incrementAndGet();
            emittedChunk.set(true);
            answerBuilder.append(delta);
            if (bufferForCodebaseSafety) {
                return;
            }
            persistStreamingAssistantMessageIfNecessary(assistantMessage, answerBuilder.toString(), effectiveMode,
                    finalExecutedSkill, operatorName, lastPartialPersistAt, false);
            sink.next(buildStreamEvent("chunk", session, route, requestedMode, effectiveMode, finalExecutedSkill,
                    answerBuilder.toString(), delta, null, null, false, null, null));
        }, error -> {
            terminalCompleted.set(true);
            long durationMs = System.currentTimeMillis() - startAt;
            String partialAnswer = answerBuilder.toString();
            String failureAnswer = StringUtils.isNotBlank(partialAnswer)
                    ? partialAnswer
                    : buildStreamFailureAnswer(error, effectiveMode);
            failureAnswer = sanitizeCodebaseAssistantAnswerIfNecessary(question, effectiveMode, failureAnswer, intentRouteResult);
            putIntentRoutePerformanceMetric(intentRouteResult, "modelStreamMs", durationMs);
            putIntentRoutePerformanceMetric(intentRouteResult, "chunkCount", chunkCount.get());
            putIntentRoutePerformanceMetric(intentRouteResult, "answerChars", failureAnswer.length());
            putIntentRoutePerformanceMetric(intentRouteResult, "totalMs", System.currentTimeMillis() - prepareStartAt);
            putIntentRoutePerformanceMetric(intentRouteResult, "failed", true);
            updateIntentRouteAuditMessage(userMessage, intentRouteResult, operatorName);
            failAssistantMessage(assistantMessage,
                    failureAnswer,
                    effectiveMode, finalExecutedSkill, null, durationMs, operatorName);
            refreshSessionWithOperator(session, route, requestedMode, operatorName);
            log.warn("AI会话模型流失败: sessionCode={}, effectiveMode={}, modelCode={}, durationMs={}, firstTokenMs={}, chunkCount={}, error={}",
                    session.getSessionCode(), effectiveMode, route == null ? null : route.getModelCode(), durationMs,
                    firstChunkAt.get() == 0L ? null : firstChunkAt.get() - startAt, chunkCount.get(), error.getMessage());
            emitStreamError(sink, session, route, requestedMode, effectiveMode, error, finalExecutedSkill, failureAnswer,
                    serializeIntentRouteAudit(intentRouteResult));
        }, () -> {
            terminalCompleted.set(true);
            long durationMs = System.currentTimeMillis() - startAt;
            String finalAnswer = sanitizeCodebaseAssistantAnswerIfNecessary(question, effectiveMode,
                    answerBuilder.toString(), intentRouteResult);
            String toolResult = AiChatMode.PROTOCOL_PARSE.name().equals(effectiveMode)
                    ? buildProtocolParseToolResult(finalAnswer)
                    : null;
            if (finalPlatformContextSnapshot != null || finalProtocolContextSnapshot != null) {
                persistConversationContextSnapshot(userMessage, buildContextSnapshotExecutionResult(
                        finalAnswer,
                        effectiveMode,
                        finalExecutedSkill,
                        toolResult,
                        finalPlatformContextSnapshot,
                        finalProtocolContextSnapshot
                ), operatorName);
            }
            putIntentRoutePerformanceMetric(intentRouteResult, "modelStreamMs", durationMs);
            putIntentRoutePerformanceMetric(intentRouteResult, "chunkCount", chunkCount.get());
            putIntentRoutePerformanceMetric(intentRouteResult, "answerChars", finalAnswer.length());
            putIntentRoutePerformanceMetric(intentRouteResult, "totalMs", System.currentTimeMillis() - prepareStartAt);
            updateIntentRouteAuditMessage(userMessage, intentRouteResult, operatorName);
            finishAssistantMessage(assistantMessage, finalAnswer, effectiveMode, finalExecutedSkill, toolResult, durationMs, operatorName);
            refreshSessionWithOperator(session, route, requestedMode, operatorName);
            log.info("AI会话模型流完成: sessionCode={}, effectiveMode={}, modelCode={}, durationMs={}, firstTokenMs={}, chunkCount={}, answerChars={}",
                    session.getSessionCode(), effectiveMode, route == null ? null : route.getModelCode(), durationMs,
                    firstChunkAt.get() == 0L ? null : firstChunkAt.get() - startAt, chunkCount.get(), finalAnswer.length());
            if (bufferForCodebaseSafety && StringUtils.isNotBlank(finalAnswer)) {
                sink.next(buildStreamEvent("chunk", session, route, requestedMode, effectiveMode, finalExecutedSkill,
                        finalAnswer, finalAnswer, null, null, false, null, null));
            }
            AiChatStreamEventVO doneEvent = buildStreamEvent("done", session, route, requestedMode, effectiveMode, finalExecutedSkill,
                    finalAnswer, finalAnswer, toolResult, null, true, null, null);
            doneEvent.setRouteAudit(serializeIntentRouteAudit(intentRouteResult));
            sink.next(doneEvent);
            sink.complete();
        });
        sink.onDispose(() -> {
            if (!bufferForCodebaseSafety && !terminalCompleted.get() && emittedChunk.get()
                    && StringUtils.isNotBlank(answerBuilder.toString())) {
                persistStreamingAssistantMessageIfNecessary(assistantMessage, answerBuilder.toString(), effectiveMode,
                        finalExecutedSkill, operatorName, lastPartialPersistAt, true);
            }
            if (!terminalCompleted.get()) {
                log.info("AI会话模型流客户端断开: sessionCode={}, effectiveMode={}, modelCode={}, durationMs={}, chunkCount={}, partialChars={}",
                        session.getSessionCode(), effectiveMode, route == null ? null : route.getModelCode(),
                        System.currentTimeMillis() - startAt, chunkCount.get(), answerBuilder.length());
            }
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
        });
    }

    private boolean isModelStreamIdleTimeout(Throwable error) {
        Throwable current = error;
        while (current != null) {
            if (current instanceof TimeoutException) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private void persistStreamingAssistantMessageIfNecessary(AiChatMessage assistantMessage,
                                                             String answer,
                                                             String effectiveMode,
                                                             String toolName,
                                                             String operatorName,
                                                             AtomicLong lastPartialPersistAt,
                                                             boolean force) {
        if (assistantMessage == null || StringUtils.isBlank(answer)) {
            return;
        }
        long now = System.currentTimeMillis();
        if (!force && now - lastPartialPersistAt.get() < STREAM_PARTIAL_PERSIST_INTERVAL_MS) {
            return;
        }
        lastPartialPersistAt.set(now);
        assistantMessage.setMessageContent(answer);
        assistantMessage.setMessageSummary(buildSessionTitle(answer));
        assistantMessage.setAbilityType(effectiveMode);
        assistantMessage.setToolName(toolName);
        assistantMessage.setMessageStatus("RUNNING");
        assistantMessage.setUpdateBy(operatorName);
        assistantMessage.setUpdateTime(new Date());
        aiChatMessageService.updateById(assistantMessage);
    }

    private Flux<String> streamModel(ChatModel chatModel, String prompt) {
        return chatModel.stream(prompt);
    }

    private boolean supportsTokenStream(String effectiveMode) {
        return AiChatMode.GENERAL.name().equals(effectiveMode)
                || AiChatMode.PLATFORM_ASSISTANT.name().equals(effectiveMode)
                || AiChatMode.PROTOCOL_PARSE.name().equals(effectiveMode);
    }

    private String resolveStageMessage(String effectiveMode) {
        if (AiChatMode.NL2SQL.name().equals(effectiveMode)) {
            return message("ai.chat.stage.nl2sql.running");
        }
        if (AiChatMode.DEVICE_CONTROL.name().equals(effectiveMode)) {
            return message("ai.chat.stage.device.control.running");
        }
        if (AiChatMode.REQUIREMENT_EVALUATION.name().equals(effectiveMode)) {
            return message("ai.chat.stage.requirement.evaluation.running");
        }
        return message("ai.chat.stage.default.running");
    }

    private void finishAssistantMessage(AiChatMessage assistantMessage,
                                        String answer,
                                        String effectiveMode,
                                        String toolName,
                                        String toolResult,
                                        long durationMs,
                                        String operatorName) {
        assistantMessage.setMessageContent(answer);
        assistantMessage.setMessageSummary(buildSessionTitle(answer));
        assistantMessage.setAbilityType(effectiveMode);
        assistantMessage.setToolName(toolName);
        assistantMessage.setToolResult(toolResult);
        assistantMessage.setDurationMs(durationMs);
        assistantMessage.setMessageStatus("SUCCESS");
        assistantMessage.setUpdateBy(operatorName);
        assistantMessage.setUpdateTime(new Date());
        aiChatMessageService.updateById(assistantMessage);
    }

    private void failAssistantMessage(AiChatMessage assistantMessage,
                                      String answer,
                                      String effectiveMode,
                                      String toolName,
                                      String toolResult,
                                      long durationMs,
                                      String operatorName) {
        assistantMessage.setMessageContent(answer);
        assistantMessage.setMessageSummary(buildSessionTitle(answer));
        assistantMessage.setAbilityType(effectiveMode);
        assistantMessage.setToolName(toolName);
        assistantMessage.setToolResult(toolResult);
        assistantMessage.setDurationMs(durationMs);
        assistantMessage.setMessageStatus("FAIL");
        assistantMessage.setUpdateBy(operatorName);
        assistantMessage.setUpdateTime(new Date());
        aiChatMessageService.updateById(assistantMessage);
    }

    private void emitStreamError(FluxSink<AiChatStreamEventVO> sink,
                                 AiChatSession session,
                                 AiModelRouteVO route,
                                 String requestedMode,
                                 String effectiveMode,
                                 Throwable error,
                                 String executedSkill) {
        emitStreamError(sink, session, route, requestedMode, effectiveMode, error, executedSkill,
                buildStreamFailureAnswer(error, effectiveMode));
    }

    private void emitStreamError(FluxSink<AiChatStreamEventVO> sink,
                                 AiChatSession session,
                                 AiModelRouteVO route,
                                 String requestedMode,
                                 String effectiveMode,
                                 Throwable error,
                                 String executedSkill,
                                 String displayMessage) {
        emitStreamError(sink, session, route, requestedMode, effectiveMode, error, executedSkill, displayMessage, null);
    }

    private void emitStreamError(FluxSink<AiChatStreamEventVO> sink,
                                 AiChatSession session,
                                 AiModelRouteVO route,
                                 String requestedMode,
                                 String effectiveMode,
                                 Throwable error,
                                 String executedSkill,
                                 String displayMessage,
                                 String routeAudit) {
        String errorMessage = buildStreamFailureAnswer(error, effectiveMode);
        String answer = StringUtils.isNotBlank(displayMessage) ? displayMessage : errorMessage;
        AiChatStreamEventVO event = buildStreamEvent("error", session, route, requestedMode, effectiveMode, executedSkill,
                answer, "", null, null, true, "STREAM_ERROR", errorMessage);
        event.setRouteAudit(routeAudit);
        sink.next(event);
        sink.complete();
    }

    private String extractStreamErrorMessage(Throwable error) {
        if (error == null) {
            return message("ai.chat.stream.failed.retry");
        }
        String message = error.getMessage();
        return StringUtils.isNotBlank(message) ? message : message("ai.chat.stream.failed.retry");
    }

    private String buildStreamFailureAnswer(Throwable error, String effectiveMode) {
        String message = trimErrorMessage(extractStreamErrorMessage(error));
        String preciseMessage = resolvePreciseStreamFailureMessage(message, effectiveMode);
        if (StringUtils.isNotBlank(preciseMessage)) {
            return preciseMessage;
        }
        return message("ai.chat.ability.execution.failed", resolveAbilityLabel(effectiveMode), message);
    }

    private String resolvePreciseStreamFailureMessage(String message, String effectiveMode) {
        if (StringUtils.isBlank(message)) {
            return "";
        }
        if (containsAny(message, "无权控制该物模型", "没有该物模型的控制权限", "物模型控制权限")) {
            return message("ai.chat.device.control.failure.thing.model.permission");
        }
        if (containsAny(message, "无权访问该设备", "无设备操作权限", "没有该设备的操作权限", "没有设备操作权限")) {
            return message("ai.chat.device.control.failure.device.permission");
        }
        if (isDeviceResolveFailureMessage(message)) {
            if (containsAny(message, "多个设备候选", "候选设备", "请先明确具体设备")) {
                return message("ai.chat.device.control.failure.multiple.devices");
            }
            return message("ai.chat.device.control.failure.device.not.found");
        }
        if (isRuntimeThingModelFailureMessage(message)) {
            return message("ai.chat.device.control.failure.thing.model.not.found");
        }
        if (containsAny(message, "Redis 中未找到", "实时值不存在", "未找到该指标当前值")) {
            return message("ai.chat.nl2sql.failure.runtime.current.not.found");
        }
        if (containsAny(message, "时序库中未查询到", "未查询到历史", "历史点位为空")) {
            return message("ai.chat.nl2sql.failure.runtime.history.not.found");
        }
        if (containsAny(message, "未获取到当前登录用户", "登录用户")) {
            return message("ai.chat.failure.login.user.required");
        }
        if (isModelProviderFailureMessage(message)) {
            return message("ai.chat.failure.model.provider.unavailable");
        }
        return "";
    }

    private boolean isDeviceResolveFailureMessage(String message) {
        return containsAny(message,
                "未找到对应设备", "未查询到设备", "未成功解析到设备", "未解析到可访问的设备",
                "未定位设备", "缺少设备", "设备编号不能为空", "未查询到匹配设备",
                "未能从当前问句中解析到唯一设备", "未解析到唯一设备", "设备不存在",
                "命中了多个设备候选", "候选设备", "请先明确具体设备");
    }

    private boolean isRuntimeThingModelFailureMessage(String message) {
        boolean hasThingModelSignal = containsAny(message,
                "物模型", "运行时物模型", "运行时语义", "运行时指标", "identifier", "标识符", "可控模型");
        boolean hasBusinessFailure = containsAny(message,
                "未找到", "未查询到", "未命中", "未识别", "不存在", "未成功解析",
                "未定位", "缺少", "未配置", "不可用", "未解析出", "无法执行");
        return hasThingModelSignal && hasBusinessFailure;
    }

    private boolean isModelProviderFailureMessage(String message) {
        if (isRuntimeThingModelFailureMessage(message)) {
            return false;
        }
        boolean hasProviderSignal = containsAny(message,
                "API Key", "API key", "api-key", "apikey", "密钥", "模型厂商", "模型编码",
                "providerCode", "modelCode", "ChatModel", "OpenAI", "DashScope", "dashscope",
                "DeepSeek", "401", "Unauthorized", "认证失败");
        boolean hasConfigFailure = containsAny(message,
                "未配置", "不能为空", "must be set", "无效", "不可用", "invalid", "Invalid",
                "不存在", "找不到", "401", "Unauthorized", "认证失败");
        return hasProviderSignal && hasConfigFailure;
    }

    private String resolveFailureExecutedSkill(String effectiveMode) {
        if (AiChatMode.NL2SQL.name().equals(effectiveMode)) {
            return SKILL_NL2SQL_GENERATE;
        }
        if (AiChatMode.DEVICE_CONTROL.name().equals(effectiveMode)) {
            return SKILL_DEVICE_GUIDE;
        }
        if (AiChatMode.PROTOCOL_PARSE.name().equals(effectiveMode)) {
            return SKILL_PROTOCOL_PARSE;
        }
        if (AiChatMode.THING_MODEL_GENERATE.name().equals(effectiveMode)) {
            return SKILL_THING_MODEL_GENERATE;
        }
        if (AiChatMode.REQUIREMENT_EVALUATION.name().equals(effectiveMode)) {
            return SKILL_REQUIREMENT_EVALUATION;
        }
        if (AiChatMode.PLATFORM_ASSISTANT.name().equals(effectiveMode)) {
            return SKILL_PLATFORM_ASSISTANT;
        }
        return SKILL_GENERAL_CHAT;
    }

    private String resolveAbilityLabel(String effectiveMode) {
        if (AiChatMode.NL2SQL.name().equals(effectiveMode)) {
            return message("ai.chat.ability.nl2sql");
        }
        if (AiChatMode.DEVICE_CONTROL.name().equals(effectiveMode)) {
            return message("ai.chat.ability.device.control");
        }
        if (AiChatMode.PROTOCOL_PARSE.name().equals(effectiveMode)) {
            return message("ai.chat.ability.protocol.parse");
        }
        if (AiChatMode.THING_MODEL_GENERATE.name().equals(effectiveMode)) {
            return message("ai.chat.ability.thing.model.generate");
        }
        if (AiChatMode.REQUIREMENT_EVALUATION.name().equals(effectiveMode)) {
            return message("ai.chat.ability.requirement.evaluation");
        }
        if (AiChatMode.PLATFORM_ASSISTANT.name().equals(effectiveMode)) {
            return message("ai.chat.ability.platform.assistant");
        }
        return message("ai.chat.ability.general.chat");
    }

    private String extractQueryMode(String toolResult) {
        if (StringUtils.isBlank(toolResult)) {
            return null;
        }
        try {
            return JSON.parseObject(toolResult).getString("queryMode");
        } catch (Exception ignore) {
            return null;
        }
    }

    private AiChatStreamEventVO buildStreamEvent(String eventType,
                                                 AiChatSession session,
                                                 AiModelRouteVO route,
                                                 String chatMode,
                                                 String effectiveChatMode,
                                                 String executedSkill,
                                                 String content,
                                                 String delta,
                                                 String toolResult,
                                                 String queryMode,
                                                 boolean done,
                                                 String errorCode,
                                                 String errorMessage) {
        AiChatStreamEventVO event = new AiChatStreamEventVO();
        event.setEventType(eventType);
        event.setSessionId(session == null ? null : session.getSessionId());
        event.setSessionCode(session == null ? null : session.getSessionCode());
        event.setChatMode(chatMode);
        event.setModePolicy(resolveSessionModePolicy(session));
        event.setPinnedMode(resolveSessionPinnedMode(session));
        event.setLastEffectiveMode(resolveSessionLastEffectiveMode(session));
        event.setEffectiveChatMode(effectiveChatMode);
        event.setProviderCode(route == null ? null : route.getProviderCode());
        event.setModelCode(route == null ? null : route.getModelCode());
        event.setExecutedSkill(executedSkill);
        event.setContent(content);
        event.setDelta(delta);
        event.setToolResult(toolResult);
        event.setQueryMode(queryMode);
        event.setDone(done);
        event.setErrorCode(errorCode);
        event.setErrorMessage(errorMessage);
        return event;
    }

    private AiChatStreamEventVO withRouteAudit(AiChatStreamEventVO event, AiChatIntentRouteVO intentRouteResult) {
        if (event != null && intentRouteResult != null) {
            event.setRouteAudit(serializeIntentRouteAudit(intentRouteResult));
        }
        return event;
    }

    private AiClarifyPayloadVO buildClarifyContext(String requestedMode,
                                                   String effectiveMode,
                                                   String question,
                                                   AiChatIntentRouteVO intentRouteResult) {
        AiClarifyPayloadVO lowConfidencePayload = buildLowConfidenceModeClarifyContext(requestedMode, effectiveMode, question, intentRouteResult);
        if (lowConfidencePayload != null) {
            return lowConfidencePayload;
        }
        if (AiChatMode.DEVICE_CONTROL.name().equals(effectiveMode)) {
            return buildDeviceControlClarifyContext(question, intentRouteResult);
        }
        if (!AiChatMode.NL2SQL.name().equals(effectiveMode)) {
            return null;
        }
        if (StringUtils.isNotBlank(extractSqlStatement(question))) {
            return null;
        }

        AiClarifyPayloadVO deviceClarifyPayload = buildDeviceClarifyContext(question);
        if (deviceClarifyPayload != null) {
            return deviceClarifyPayload;
        }

        if (StringUtils.isNotBlank(extractStringValue(question, "identifier", "标识符"))) {
            return null;
        }

        AiSemanticContextVO semanticContext = aiSemanticNormalizationService.buildNl2SqlContext(question);
        if (semanticContext == null || semanticContext.getRuntimeFields() == null || semanticContext.getRuntimeFields().isEmpty()) {
            return null;
        }

        List<AiSemanticFieldVO> runtimeFields = new ArrayList<>(semanticContext.getRuntimeFields());
        runtimeFields.sort(Comparator.comparingInt(this::resolveSemanticScore).reversed());
        runtimeFields = filterClarifyThingModelCandidates(question, runtimeFields);
        LinkedHashMap<String, AiSemanticFieldVO> candidateMap = new LinkedHashMap<>();
        for (AiSemanticFieldVO field : runtimeFields) {
            if (field == null || StringUtils.isBlank(field.getSourceCode())) {
                continue;
            }
            if (!candidateMap.containsKey(field.getSourceCode())) {
                candidateMap.put(field.getSourceCode(), field);
            }
            if (candidateMap.size() >= resolveClarifyCandidateLimit()) {
                break;
            }
        }
        List<AiSemanticFieldVO> candidates = new ArrayList<>(candidateMap.values());
        if (candidates.size() < 2 || !shouldClarifyThingModel(question, candidates)) {
            return null;
        }

        return aiClarifySupportService.buildPayload(
                AiClarifyConstants.CLARIFY_TYPE_THING_MODEL,
                AiClarifyConstants.CLARIFY_KEY_NL2SQL_THING_MODEL,
                "命中了多个物模型，请选择具体指标后继续执行智能问数。",
                buildThingModelClarifyContent(candidates),
                SKILL_NL2SQL_CLARIFY_THING_MODEL,
                question,
                buildThingModelClarifyOptions(candidates)
        );
    }

    private AiClarifyPayloadVO buildLowConfidenceModeClarifyContext(String requestedMode,
                                                                    String effectiveMode,
                                                                    String question,
                                                                    AiChatIntentRouteVO intentRouteResult) {
        if (!AiChatMode.AUTO.name().equals(requestedMode) || intentRouteResult == null) {
            return null;
        }
        if (!"SUCCESS".equalsIgnoreCase(intentRouteResult.getParseStatus())) {
            return null;
        }
        String targetMode = resolveLowConfidenceClarifyTargetMode(intentRouteResult, effectiveMode);
        if (StringUtils.isBlank(targetMode)) {
            return null;
        }
        Double confidence = intentRouteResult.getModeConfidence();
        double threshold = resolveIntentRouteThreshold(targetMode);
        if (confidence != null && confidence >= threshold) {
            return null;
        }
        return aiClarifySupportService.buildPayload(
                AiClarifyConstants.CLARIFY_TYPE_MODE,
                AiClarifyConstants.CLARIFY_KEY_AUTO_MODE_CONFIRM,
                message("ai.chat.clarify.mode.title", resolveChatModeLabel(targetMode)),
                buildLowConfidenceModeClarifyContent(targetMode, intentRouteResult, threshold),
                SKILL_AUTO_ROUTE_CONFIRM,
                question,
                buildLowConfidenceModeClarifyOptions(targetMode)
        );
    }

    private String resolveLowConfidenceClarifyTargetMode(AiChatIntentRouteVO intentRouteResult, String effectiveMode) {
        String aiMode = intentRouteResult == null ? null : intentRouteResult.getMode();
        if (isHighRiskConversationMode(aiMode)) {
            return aiMode;
        }
        if (isHighRiskConversationMode(effectiveMode)) {
            return effectiveMode;
        }
        return null;
    }

    private boolean isHighRiskConversationMode(String mode) {
        return AiChatMode.NL2SQL.name().equals(mode) || AiChatMode.DEVICE_CONTROL.name().equals(mode);
    }

    private String buildLowConfidenceModeClarifyContent(String targetMode,
                                                        AiChatIntentRouteVO intentRouteResult,
                                                        double threshold) {
        StringBuilder content = new StringBuilder(message("ai.chat.clarify.mode.content", resolveChatModeLabel(targetMode)));
        if (intentRouteResult != null) {
            if (StringUtils.isNotBlank(intentRouteResult.getMode())) {
                content.append('\n').append(message("ai.chat.clarify.mode.ai.suggestion", resolveChatModeLabel(intentRouteResult.getMode())));
            }
            if (intentRouteResult.getModeConfidence() != null) {
                content.append('\n').append(message("ai.chat.clarify.mode.confidence", Math.round(intentRouteResult.getModeConfidence() * 100) + "%"));
            } else {
                content.append('\n').append(message("ai.chat.clarify.mode.confidence", message("ai.chat.common.not.returned")));
            }
            content.append('\n').append(message("ai.chat.clarify.mode.threshold", formatRouteThresholdPercent(threshold)));
            if (StringUtils.isNotBlank(intentRouteResult.getRuleMode())) {
                content.append('\n').append(message("ai.chat.clarify.mode.rule.mode", resolveChatModeLabel(intentRouteResult.getRuleMode())));
            }
            if (StringUtils.isNotBlank(intentRouteResult.getReason())) {
                content.append('\n').append(message("ai.chat.clarify.mode.reason", intentRouteResult.getReason()));
            }
        }
        content.append('\n').append(message("ai.chat.clarify.mode.general.tip"));
        return content.toString();
    }

    private List<AiClarifyOptionVO> buildLowConfidenceModeClarifyOptions(String targetMode) {
        List<AiClarifyOptionVO> options = new ArrayList<>();
        options.add(buildModeClarifyOption(targetMode, message("ai.chat.clarify.mode.option.continue", resolveChatModeLabel(targetMode)), buildModeClarifyDescription(targetMode)));
        if (!AiChatMode.PLATFORM_ASSISTANT.name().equals(targetMode)) {
            options.add(buildModeClarifyOption(AiChatMode.PLATFORM_ASSISTANT.name(),
                    message("ai.chat.clarify.mode.option.platform.assistant"),
                    message("ai.chat.clarify.mode.option.platform.assistant.desc")));
        }
        if (!AiChatMode.GENERAL.name().equals(targetMode)) {
            options.add(buildModeClarifyOption(AiChatMode.GENERAL.name(),
                    message("ai.chat.clarify.mode.option.general"),
                    message("ai.chat.clarify.mode.option.general.desc")));
        }
        return options;
    }

    private AiClarifyOptionVO buildModeClarifyOption(String mode, String label, String description) {
        AiClarifyOptionVO option = new AiClarifyOptionVO();
        option.setType(AiClarifyConstants.CLARIFY_TYPE_MODE);
        option.setValue(mode);
        option.setLabel(label);
        option.setDescription(description);
        return option;
    }

    private String buildModeClarifyDescription(String mode) {
        if (AiChatMode.NL2SQL.name().equals(mode)) {
            return message("ai.chat.clarify.mode.desc.nl2sql");
        }
        if (AiChatMode.DEVICE_CONTROL.name().equals(mode)) {
            return message("ai.chat.clarify.mode.desc.device.control");
        }
        if (AiChatMode.PLATFORM_ASSISTANT.name().equals(mode)) {
            return message("ai.chat.clarify.mode.desc.platform.assistant");
        }
        if (AiChatMode.GENERAL.name().equals(mode)) {
            return message("ai.chat.clarify.mode.desc.general");
        }
        return message("ai.chat.clarify.mode.desc.current");
    }

    private AiClarifyPayloadVO buildDeviceClarifyContext(String question) {
        List<DeviceShortOutput> rawCandidates = aiDeviceResolveService.listDeviceCandidates(question);
        if (rawCandidates == null || rawCandidates.isEmpty()) {
            return null;
        }
        List<DeviceShortOutput> candidates = limitDeviceClarifyCandidates(rawCandidates);
        if (candidates.size() < 2) {
            return null;
        }

        return aiClarifySupportService.buildPayload(
                AiClarifyConstants.CLARIFY_TYPE_DEVICE,
                AiClarifyConstants.CLARIFY_KEY_NL2SQL_DEVICE,
                message("ai.chat.clarify.nl2sql.device.title"),
                buildDeviceClarifyContent(candidates),
                SKILL_NL2SQL_CLARIFY_DEVICE,
                question,
                buildDeviceClarifyOptions(candidates)
        );
    }

    private AiClarifyPayloadVO buildDeviceControlClarifyContext(String question, AiChatIntentRouteVO intentRouteResult) {
        if (StringUtils.isNotBlank(extractJsonBody(question))) {
            return null;
        }
        if (containsAny(question, "执行场景", "运行场景", "runScene")) {
            return null;
        }
        AiDeviceControlIntentVO controlIntent = aiDeviceControlIntentService.resolveIntent(question, intentRouteResult);
        if (controlIntent.getDeviceCandidates() != null && controlIntent.getDeviceCandidates().size() > 1) {
            return buildDeviceControlDeviceClarifyContext(question, controlIntent.getDeviceCandidates());
        }
        if (StringUtils.isNotBlank(extractStringValue(question, "identifier", "标识符"))) {
            return null;
        }
        if (controlIntent.getThingModelCandidates() != null && controlIntent.getThingModelCandidates().size() > 1) {
            return buildDeviceControlThingModelClarifyContext(question, controlIntent.getThingModelCandidates());
        }
        AiClarifyPayloadVO confirmPayload = aiDeviceControlConfirmService.buildRiskConfirmPayload(question, intentRouteResult, controlIntent);
        if (confirmPayload != null) {
            return confirmPayload;
        }
        return null;
    }

    private AiClarifyPayloadVO buildDeviceControlDeviceClarifyContext(String question, List<DeviceShortOutput> rawCandidates) {
        List<DeviceShortOutput> candidates = limitDeviceClarifyCandidates(rawCandidates);
        if (candidates.size() < 2) {
            return null;
        }
        return aiClarifySupportService.buildPayload(
                AiClarifyConstants.CLARIFY_TYPE_DEVICE,
                AiClarifyConstants.CLARIFY_KEY_DEVICE_CONTROL_DEVICE,
                message("ai.chat.clarify.device.control.device.title"),
                buildDeviceControlDeviceClarifyPreview(candidates),
                SKILL_DEVICE_CLARIFY_DEVICE,
                question,
                buildDeviceClarifyOptions(candidates)
        );
    }

    private AiClarifyPayloadVO buildDeviceControlThingModelClarifyContext(String question, List<ThingsModelValueItem> rawCandidates) {
        List<ThingsModelValueItem> candidates = limitThingModelClarifyCandidates(rawCandidates);
        if (candidates.size() < 2) {
            return null;
        }
        return aiClarifySupportService.buildPayload(
                AiClarifyConstants.CLARIFY_TYPE_THING_MODEL,
                AiClarifyConstants.CLARIFY_KEY_DEVICE_CONTROL_THING_MODEL,
                message("ai.chat.clarify.device.control.thing.model.title"),
                buildDeviceControlThingModelClarifyPreview(candidates),
                SKILL_DEVICE_CLARIFY_THING_MODEL,
                question,
                buildDeviceControlThingModelClarifyOptions(candidates)
        );
    }

    private List<DeviceShortOutput> limitDeviceClarifyCandidates(List<DeviceShortOutput> rawCandidates) {
        return AiCandidateMatchSupport.deduplicateAndLimit(rawCandidates,
                DeviceShortOutput::getSerialNumber,
                resolveClarifyCandidateLimit());
    }

    private List<ThingsModelValueItem> limitThingModelClarifyCandidates(List<ThingsModelValueItem> rawCandidates) {
        return AiCandidateMatchSupport.deduplicateAndLimit(rawCandidates,
                ThingsModelValueItem::getId,
                resolveClarifyCandidateLimit());
    }

    private boolean shouldClarifyThingModel(String question, List<AiSemanticFieldVO> candidates) {
        if (candidates == null || candidates.size() < 2) {
            return false;
        }
        List<AiSemanticFieldVO> focusCandidates = applyClarifyScoreWindow(filterClarifyThingModelByFocus(question, candidates));
        if (focusCandidates.size() >= 2) {
            return true;
        }
        if (focusCandidates.size() == 1) {
            return false;
        }
        int firstScore = resolveSemanticScore(candidates.get(0));
        int secondScore = resolveSemanticScore(candidates.get(1));
        if (firstScore < NL2SQL_CLARIFY_MIN_SCORE || secondScore < NL2SQL_CLARIFY_MIN_SCORE) {
            return false;
        }
        return firstScore - secondScore <= NL2SQL_CLARIFY_SCORE_GAP;
    }

    private int resolveSemanticScore(AiSemanticFieldVO field) {
        return field == null || field.getMatchScore() == null ? 0 : field.getMatchScore();
    }

    private List<AiSemanticFieldVO> filterClarifyThingModelCandidates(String question, List<AiSemanticFieldVO> runtimeFields) {
        if (runtimeFields == null || runtimeFields.isEmpty()) {
            return new ArrayList<>();
        }
        List<AiSemanticFieldVO> focusCandidates = applyClarifyScoreWindow(filterClarifyThingModelByFocus(question, runtimeFields));
        if (!focusCandidates.isEmpty()) {
            return focusCandidates;
        }
        return applyClarifyScoreWindow(runtimeFields);
    }

    private List<AiSemanticFieldVO> applyClarifyScoreWindow(List<AiSemanticFieldVO> targetFields) {
        if (targetFields == null || targetFields.isEmpty()) {
            return new ArrayList<>();
        }
        int topScore = resolveSemanticScore(targetFields.get(0));
        if (topScore <= 0) {
            return new ArrayList<>(targetFields);
        }
        int minScore = Math.max(NL2SQL_CLARIFY_MIN_SCORE, topScore - NL2SQL_CLARIFY_CANDIDATE_SCORE_WINDOW);
        List<AiSemanticFieldVO> filtered = new ArrayList<>();
        for (AiSemanticFieldVO field : targetFields) {
            if (resolveSemanticScore(field) >= minScore) {
                filtered.add(field);
            }
        }
        return filtered.isEmpty() ? new ArrayList<>(targetFields) : filtered;
    }

    private List<AiSemanticFieldVO> filterClarifyThingModelByFocus(String question, List<AiSemanticFieldVO> runtimeFields) {
        if (runtimeFields == null || runtimeFields.isEmpty()) {
            return new ArrayList<>();
        }
        String normalizedFocus = extractClarifyFocusText(question);
        if (StringUtils.isBlank(normalizedFocus)) {
            return new ArrayList<>();
        }
        List<AiSemanticFieldVO> exactMatches = filterSemanticFieldsByFocus(runtimeFields, normalizedFocus, true);
        if (!exactMatches.isEmpty()) {
            return exactMatches;
        }
        return filterSemanticFieldsByFocus(runtimeFields, normalizedFocus, false);
    }

    private String extractClarifyFocusText(String question) {
        String normalizedQuestion = normalizeSemanticText(question);
        if (StringUtils.isBlank(normalizedQuestion)) {
            return "";
        }
        String focusText = normalizedQuestion;
        int intentIndex = locateLastClarifyIntentIndex(normalizedQuestion);
        if (intentIndex >= 0 && intentIndex < normalizedQuestion.length()) {
            String tail = normalizedQuestion.substring(intentIndex);
            String cleanedTail = removeClarifyIntentTokens(tail);
            if (StringUtils.isNotBlank(cleanedTail)) {
                focusText = cleanedTail;
            }
        }
        for (String token : NL2SQL_CLARIFY_FOCUS_NOISE_TOKENS) {
            focusText = removeNormalizedSemanticToken(focusText, token);
        }
        return focusText;
    }

    private int locateLastClarifyIntentIndex(String normalizedQuestion) {
        if (StringUtils.isBlank(normalizedQuestion)) {
            return -1;
        }
        int result = -1;
        for (String token : NL2SQL_CLARIFY_INTENT_TOKENS) {
            String normalizedToken = normalizeSemanticText(token);
            if (StringUtils.isBlank(normalizedToken)) {
                continue;
            }
            int index = normalizedQuestion.lastIndexOf(normalizedToken);
            if (index > result) {
                result = index;
            }
        }
        return result;
    }

    private String removeClarifyIntentTokens(String source) {
        String result = source;
        for (String token : NL2SQL_CLARIFY_INTENT_TOKENS) {
            result = removeNormalizedSemanticToken(result, token);
        }
        return result;
    }

    private String removeNormalizedSemanticToken(String source, String token) {
        if (StringUtils.isBlank(source) || StringUtils.isBlank(token)) {
            return source;
        }
        String normalizedToken = normalizeSemanticText(token);
        if (StringUtils.isBlank(normalizedToken)) {
            return source;
        }
        return source.replace(normalizedToken, "");
    }

    private List<AiSemanticFieldVO> filterSemanticFieldsByFocus(List<AiSemanticFieldVO> runtimeFields,
                                                                String normalizedFocus,
                                                                boolean exactMatch) {
        List<AiSemanticFieldVO> filtered = new ArrayList<>();
        if (runtimeFields == null || runtimeFields.isEmpty() || StringUtils.isBlank(normalizedFocus)) {
            return filtered;
        }
        for (AiSemanticFieldVO field : runtimeFields) {
            if (matchesSemanticFieldFocus(field, normalizedFocus, exactMatch)) {
                filtered.add(field);
            }
        }
        return filtered;
    }

    private boolean matchesSemanticFieldFocus(AiSemanticFieldVO field, String normalizedFocus, boolean exactMatch) {
        if (field == null || StringUtils.isBlank(normalizedFocus)) {
            return false;
        }
        return AiCandidateMatchSupport.matchesAliases(buildSemanticFieldAliases(field), normalizedFocus, exactMatch);
    }

    private String normalizeSemanticText(String text) {
        return AiCandidateMatchSupport.normalizeText(text);
    }

    private List<String> buildSemanticFieldAliases(AiSemanticFieldVO field) {
        List<String> aliases = new ArrayList<>();
        if (field == null) {
            return aliases;
        }
        if (StringUtils.isNotBlank(field.getSemanticName())) {
            aliases.add(field.getSemanticName());
        }
        if (StringUtils.isNotBlank(field.getSourceCode())) {
            aliases.add(field.getSourceCode());
        }
        if (field.getAliases() != null && !field.getAliases().isEmpty()) {
            aliases.addAll(filterClarifySemanticAliases(field, field.getAliases()));
        }
        return aliases;
    }

    private List<String> filterClarifySemanticAliases(AiSemanticFieldVO field, List<String> aliases) {
        List<String> filtered = new ArrayList<>();
        if (aliases == null || aliases.isEmpty()) {
            return filtered;
        }
        String normalizedDevice = normalizeSemanticText(extractQueryHintValue(field, "device"));
        String normalizedSerial = normalizeSemanticText(extractQueryHintValue(field, "serialNumber"));
        String normalizedProduct = normalizeSemanticText(extractQueryHintValue(field, "product"));
        String normalizedModelType = normalizeSemanticText(extractQueryHintValue(field, "model-type"));
        String normalizedUnit = normalizeSemanticText(extractQueryHintValue(field, "unit"));
        for (String alias : aliases) {
            if (StringUtils.isBlank(alias)) {
                continue;
            }
            String normalizedAlias = normalizeSemanticText(alias);
            if (StringUtils.isBlank(normalizedAlias)) {
                continue;
            }
            if (normalizedAlias.equals(normalizedDevice)
                    || normalizedAlias.equals(normalizedSerial)
                    || normalizedAlias.equals(normalizedProduct)
                    || normalizedAlias.equals(normalizedModelType)
                    || normalizedAlias.equals(normalizedUnit)) {
                continue;
            }
            filtered.add(alias);
        }
        return filtered;
    }

    private int resolveClarifyCandidateLimit() {
        int configuredLimit = properties == null || properties.getNl2sql() == null
                ? 0 : properties.getNl2sql().getMaxThingModelSemanticMatches();
        return Math.max(NL2SQL_CLARIFY_MAX_CANDIDATES, configuredLimit);
    }

    private String buildThingModelClarifyContent(List<AiSemanticFieldVO> candidates) {
        StringBuilder builder = new StringBuilder(message("ai.chat.clarify.nl2sql.thing.model.title"));
        int index = 1;
        for (AiSemanticFieldVO field : candidates) {
            builder.append("\n").append(index++).append(". ").append(defaultText(field == null ? null : field.getSemanticName()));
            if (field != null && StringUtils.isNotBlank(field.getSourceCode())) {
                builder.append("（").append(field.getSourceCode()).append("）");
            }
            String description = buildThingModelOptionDescription(field);
            if (StringUtils.isNotBlank(description)) {
                builder.append(" - ").append(description);
            }
        }
        return builder.toString();
    }

    private String buildDeviceClarifyContent(List<DeviceShortOutput> candidates) {
        StringBuilder builder = new StringBuilder(message("ai.chat.clarify.nl2sql.device.title"));
        int index = 1;
        for (DeviceShortOutput device : candidates) {
            builder.append("\n").append(index++).append(". ").append(defaultText(device == null ? null : device.getDeviceName()));
            String description = buildDeviceOptionDescription(device);
            if (StringUtils.isNotBlank(description)) {
                builder.append(" - ").append(description);
            }
        }
        return builder.toString();
    }

    private String buildDeviceControlDeviceClarifyPreview(List<DeviceShortOutput> candidates) {
        StringBuilder builder = new StringBuilder(message("ai.chat.clarify.device.control.device.title"));
        int index = 1;
        for (DeviceShortOutput device : candidates) {
            builder.append("\n").append(index++).append(". ").append(defaultText(device == null ? null : device.getDeviceName()));
            String description = buildDeviceOptionDescription(device);
            if (StringUtils.isNotBlank(description)) {
                builder.append(" - ").append(description);
            }
        }
        return builder.toString();
    }

    private String buildDeviceControlThingModelClarifyPreview(List<ThingsModelValueItem> candidates) {
        StringBuilder builder = new StringBuilder(message("ai.chat.clarify.device.control.thing.model.title"));
        int index = 1;
        for (ThingsModelValueItem item : candidates) {
            builder.append("\n").append(index++).append(". ").append(defaultText(item == null ? null : item.getName(), item == null ? null : item.getId()));
            String description = buildDeviceControlThingModelOptionDescription(item);
            if (StringUtils.isNotBlank(description)) {
                builder.append(" - ").append(description);
            }
        }
        return builder.toString();
    }

    private List<AiClarifyOptionVO> buildThingModelClarifyOptions(List<AiSemanticFieldVO> candidates) {
        List<AiClarifyOptionVO> options = new ArrayList<>();
        for (AiSemanticFieldVO field : candidates) {
            if (field == null || StringUtils.isBlank(field.getSourceCode())) {
                continue;
            }
            AiClarifyOptionVO option = new AiClarifyOptionVO();
            option.setLabel(StringUtils.isNotBlank(field.getSemanticName()) ? field.getSemanticName() : field.getSourceCode());
            option.setValue(field.getSourceCode());
            option.setDescription(buildThingModelOptionDescription(field));
            option.setType(AiClarifyConstants.CLARIFY_TYPE_THING_MODEL);
            option.setScore(resolveSemanticScore(field));
            options.add(option);
        }
        return options;
    }

    private List<AiClarifyOptionVO> buildDeviceClarifyOptions(List<DeviceShortOutput> candidates) {
        List<AiClarifyOptionVO> options = new ArrayList<>();
        for (DeviceShortOutput device : candidates) {
            if (device == null || StringUtils.isBlank(device.getSerialNumber())) {
                continue;
            }
            AiClarifyOptionVO option = new AiClarifyOptionVO();
            option.setLabel(StringUtils.isNotBlank(device.getDeviceName()) ? device.getDeviceName() : device.getSerialNumber());
            option.setValue(device.getSerialNumber());
            option.setDescription(buildDeviceOptionDescription(device));
            option.setType(AiClarifyConstants.CLARIFY_TYPE_DEVICE);
            options.add(option);
        }
        return options;
    }

    private List<AiClarifyOptionVO> buildDeviceControlThingModelClarifyOptions(List<ThingsModelValueItem> candidates) {
        List<AiClarifyOptionVO> options = new ArrayList<>();
        for (ThingsModelValueItem item : candidates) {
            if (item == null || StringUtils.isBlank(item.getId())) {
                continue;
            }
            AiClarifyOptionVO option = new AiClarifyOptionVO();
            option.setLabel(StringUtils.isNotBlank(item.getName()) ? item.getName() : item.getId());
            option.setValue(item.getId());
            option.setDescription(buildDeviceControlThingModelOptionDescription(item));
            option.setType(AiClarifyConstants.CLARIFY_TYPE_THING_MODEL);
            options.add(option);
        }
        return options;
    }

    private String buildThingModelOptionDescription(AiSemanticFieldVO field) {
        if (field == null) {
            return "";
        }
        List<String> parts = new ArrayList<>();
        String modelType = extractQueryHintValue(field, "model-type");
        if (StringUtils.isNotBlank(modelType)) {
            parts.add(message("ai.chat.common.label.model.type", modelType));
        }
        String unit = extractQueryHintValue(field, "unit");
        if (StringUtils.isNotBlank(unit)) {
            parts.add(message("ai.chat.common.label.unit", unit));
        }
        String currentRoute = extractQueryHintValue(field, "current-route");
        String historyRoute = extractQueryHintValue(field, "history-route");
        if (StringUtils.isNotBlank(currentRoute) || StringUtils.isNotBlank(historyRoute)) {
            String routeText = StringUtils.isNotBlank(currentRoute) && StringUtils.isNotBlank(historyRoute)
                    ? currentRoute + " / " + historyRoute
                    : (StringUtils.isNotBlank(currentRoute) ? currentRoute : historyRoute);
            parts.add(message("ai.chat.common.label.execution.route", routeText));
        } else if (StringUtils.isNotBlank(field.getDataSourceType())) {
            parts.add(message("ai.chat.common.label.execution.route", field.getDataSourceType()));
        }
        if (field.getMatchScore() != null) {
            parts.add(message("ai.chat.common.label.match.score", field.getMatchScore()));
        }
        return String.join(" | ", parts);
    }

    private String buildDeviceOptionDescription(DeviceShortOutput device) {
        if (device == null) {
            return "";
        }
        List<String> parts = new ArrayList<>();
        if (StringUtils.isNotBlank(device.getSerialNumber())) {
            parts.add(message("ai.chat.common.label.device.serial", device.getSerialNumber()));
        }
        if (StringUtils.isNotBlank(device.getProductName())) {
            parts.add(message("ai.chat.common.label.product.name", device.getProductName()));
        }
        if (StringUtils.isNotBlank(device.getTenantName())) {
            parts.add(message("ai.chat.common.label.tenant", device.getTenantName()));
        }
        return String.join(" | ", parts);
    }

    private String buildDeviceControlThingModelOptionDescription(ThingsModelValueItem item) {
        if (item == null) {
            return "";
        }
        List<String> parts = new ArrayList<>();
        if (StringUtils.isNotBlank(item.getId())) {
            parts.add(message("ai.chat.common.label.identifier", item.getId()));
        }
        parts.add(message("ai.chat.common.label.model.type", resolveThingModelTypeText(item)));
        String datatype = resolveThingModelDataType(item);
        if (StringUtils.isNotBlank(datatype)) {
            parts.add(message("ai.chat.common.label.data.type", datatype));
        }
        String valueHints = resolveThingModelValueHints(item);
        if (StringUtils.isNotBlank(valueHints)) {
            parts.add(message("ai.chat.common.label.available.values", valueHints));
        }
        return String.join(" | ", parts);
    }

    private String resolveThingModelTypeText(ThingsModelValueItem item) {
        if (item == null || item.getType() == null) {
            return message("ai.chat.common.unknown");
        }
        if (Integer.valueOf(1).equals(item.getType())) {
            return message("ai.chat.thing.model.type.property");
        }
        if (Integer.valueOf(2).equals(item.getType())) {
            return message("ai.chat.thing.model.type.function");
        }
        if (Integer.valueOf(3).equals(item.getType())) {
            return message("ai.chat.thing.model.type.event");
        }
        return message("ai.chat.common.unknown");
    }

    private String resolveThingModelDataType(ThingsModelValueItem item) {
        if (item == null || item.getDatatype() == null || StringUtils.isBlank(item.getDatatype().getType())) {
            return null;
        }
        return item.getDatatype().getType().trim();
    }

    private String resolveThingModelValueHints(ThingsModelValueItem item) {
        if (item == null || item.getDatatype() == null) {
            return null;
        }
        if (item.getDatatype().getEnumList() != null && !item.getDatatype().getEnumList().isEmpty()) {
            List<String> values = new ArrayList<>();
            item.getDatatype().getEnumList().forEach(enumItem -> {
                if (enumItem != null && StringUtils.isNotBlank(enumItem.getValue())) {
                    String text = StringUtils.isNotBlank(enumItem.getText()) ? enumItem.getText() : enumItem.getValue();
                    values.add(text + "=" + enumItem.getValue());
                }
            });
            return values.isEmpty() ? null : String.join(" / ", values);
        }
        if (StringUtils.isNotBlank(item.getDatatype().getTrueText()) || StringUtils.isNotBlank(item.getDatatype().getFalseText())) {
            String trueText = StringUtils.defaultIfBlank(item.getDatatype().getTrueText(), message("ai.chat.device.control.value.open"));
            String falseText = StringUtils.defaultIfBlank(item.getDatatype().getFalseText(), message("ai.chat.device.control.value.close"));
            return trueText + "=1 / " + falseText + "=0";
        }
        return null;
    }

    private String extractQueryHintValue(AiSemanticFieldVO field, String key) {
        if (field == null || StringUtils.isBlank(key) || field.getQueryHints() == null || field.getQueryHints().isEmpty()) {
            return null;
        }
        String prefix = key + "=";
        for (String hint : field.getQueryHints()) {
            if (StringUtils.isNotBlank(hint) && hint.startsWith(prefix)) {
                return hint.substring(prefix.length()).trim();
            }
        }
        return null;
    }

    private AiChatStreamEventVO buildClarifyStreamEvent(AiChatSession session,
                                                        AiModelRouteVO route,
                                                        String requestedMode,
                                                        String effectiveMode,
                                                        AiClarifyPayloadVO clarifyPayload) {
        AiChatStreamEventVO event = buildStreamEvent("clarify", session, route, requestedMode, effectiveMode,
                clarifyPayload.getToolName(), clarifyPayload.getDisplayContent(), clarifyPayload.getDisplayContent(),
                clarifyPayload.getToolResult(), extractQueryMode(clarifyPayload.getToolResult()), true, null, null);
        aiClarifySupportService.applyPayload(clarifyPayload, event);
        return event;
    }

    private String buildDeviceControlConfirmToolResult(String status) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("status", StringUtils.defaultIfBlank(status, "PENDING"));
        result.put("riskConfirmed", "CONFIRMED".equalsIgnoreCase(status) ? "1" : "0");
        return JSON.toJSONString(result);
    }

    private String resolveClarifyQuestion(String effectiveMode, String originalQuestion, String executionQuestion) {
        if (AiChatMode.DEVICE_CONTROL.name().equals(effectiveMode) && StringUtils.isNotBlank(executionQuestion)) {
            return executionQuestion;
        }
        return originalQuestion;
    }

    private String resolveEffectiveMode(String requestedMode, String question, AiChatIntentRouteVO intentRouteResult) {
        return resolveEffectiveMode(requestedMode, question, intentRouteResult, null, null);
    }

    private String resolveEffectiveMode(String requestedMode,
                                        String question,
                                        AiChatIntentRouteVO intentRouteResult,
                                        AiChatSendRequest request,
                                        List<AiChatMessage> historyMessages) {
        if (isCodebaseSafetyQuestion(question)) {
            markIntentRouteFallback(intentRouteResult, AiChatMode.PLATFORM_ASSISTANT.name(),
                    message("ai.chat.route.fallback.codebase.safety"));
            return AiChatMode.PLATFORM_ASSISTANT.name();
        }
        if (!AiChatMode.AUTO.name().equals(requestedMode)) {
            String safeFallbackMode = resolveSafeFallbackForNonAutoMode(requestedMode, question, request, historyMessages);
            if (StringUtils.isNotBlank(safeFallbackMode)) {
                if (intentRouteResult != null) {
                    markIntentRouteFallback(intentRouteResult, safeFallbackMode,
                            message("ai.chat.route.fallback.local.rule"));
                }
                return safeFallbackMode;
            }
            return requestedMode;
        }
        AiAutoRouteDecision localDecision = resolveAutoRouteDecision(question, requestedMode, request, historyMessages);
        applyAutoRouteDecisionTrace(intentRouteResult, localDecision);
        AiAutoRouteAdoptionPolicy.Decision adoptionDecision = AiAutoRouteAdoptionPolicy.decide(
                question, intentRouteResult, localDecision, resolvePlatformFeatureTokens(), resolveAutoRouteAdoptionOptions());
        applyAutoRouteAdoptionDecision(intentRouteResult, adoptionDecision);
        return adoptionDecision.getFinalMode();
    }

    private String resolveSafeFallbackForNonAutoMode(String requestedMode,
                                                     String question,
                                                     AiChatSendRequest request,
                                                     List<AiChatMessage> historyMessages) {
        if (AiChatMode.GENERAL.name().equals(requestedMode)
                || AiChatMode.AUTO.name().equals(requestedMode)
                || (request != null && StringUtils.isNotBlank(request.getModeOverride()))) {
            return null;
        }
        AiAutoRouteDecision localDecision = resolveAutoRouteDecision(question, AiChatMode.AUTO.name(), request, historyMessages);
        if (localDecision == null
                || !localDecision.isDeterministic()
                || localDecision.isRequiresModelArbitration()
                || !AiChatMode.GENERAL.name().equals(localDecision.getFinalMode())) {
            return null;
        }
        String taskType = localDecision.getTaskType();
        if ("GENERAL_CHAT".equals(taskType)
                || "FILE_REVIEW".equals(taskType)
                || "ASSISTANT_MODEL_IDENTITY".equals(taskType)) {
            return AiChatMode.GENERAL.name();
        }
        return null;
    }

    private String resolveRuleEffectiveMode(String question) {
        return resolveRuleEffectiveMode(question, null, null);
    }

    private String resolveRuleEffectiveMode(String question, AiChatSendRequest request, List<AiChatMessage> historyMessages) {
        String normalizedQuestion = normalizeRouteQuestion(question);
        if (isCodebaseSafetyQuestion(normalizedQuestion)) {
            return AiChatMode.PLATFORM_ASSISTANT.name();
        }
        if (StringUtils.isNotBlank(extractSqlStatement(question))
                && !isCodebaseNavigationQuestion(normalizedQuestion)
                && !isDeviceControlLocationQuestion(normalizedQuestion)) {
            return AiChatMode.NL2SQL.name();
        }
        return resolveAutoRouteDecision(question, AiChatMode.AUTO.name(), request, historyMessages).getFinalMode();
    }

    private AiAutoRouteDecision resolveAutoRouteDecision(String question,
                                                         String requestedMode,
                                                         AiChatSendRequest request,
                                                         List<AiChatMessage> historyMessages) {
        AiAutoRouteContext context = AiAutoRouteContext.builder(question)
                .requestedMode(requestedMode)
                .hasAttachment(hasAttachmentPayload(request))
                .attachmentFileName(request == null ? null : request.getAttachmentFileName())
                .previousEffectiveMode(resolvePreviousEffectiveMode(historyMessages))
                .previousTaskType(resolvePreviousTaskType(historyMessages))
                .retryRequest(request != null && request.getRetrySourceMessageId() != null)
                .build();
        return autoRouteDecisionEngine.decide(context, resolvePlatformFeatureTokens(), resolveAutoRouteOptions());
    }

    private AiAutoRouteOptions resolveAutoRouteOptions() {
        FastBeeAiProperties.RuntimeProperties runtime = properties == null ? null : properties.getRuntime();
        if (runtime == null) {
            return AiAutoRouteOptions.defaults();
        }
        return AiAutoRouteOptions.builder()
                .localDeterministicScore(runtime.getAutoRouteLocalDeterministicScore())
                .highRiskAcceptScore(runtime.getAutoRouteHighRiskAcceptScore())
                .dataQueryAcceptScore(runtime.getAutoRouteDataQueryAcceptScore())
                .deviceControlAcceptScore(runtime.getAutoRouteDeviceControlAcceptScore())
                .build();
    }

    private boolean hasAttachmentPayload(AiChatSendRequest request) {
        return request != null && (StringUtils.isNotBlank(request.getAttachmentFileName())
                || StringUtils.isNotBlank(request.getAttachmentText())
                || StringUtils.isNotBlank(request.getAttachmentContentType()));
    }

    private String resolvePreviousEffectiveMode(List<AiChatMessage> historyMessages) {
        if (historyMessages == null || historyMessages.isEmpty()) {
            return null;
        }
        for (int i = historyMessages.size() - 1; i >= 0; i--) {
            AiChatMessage message = historyMessages.get(i);
            if (message != null && StringUtils.isNotBlank(message.getAbilityType())) {
                return message.getAbilityType();
            }
        }
        return null;
    }

    private String resolvePreviousTaskType(List<AiChatMessage> historyMessages) {
        if (historyMessages == null || historyMessages.isEmpty()) {
            return null;
        }
        for (int i = historyMessages.size() - 1; i >= 0; i--) {
            AiChatMessage message = historyMessages.get(i);
            if (message != null && StringUtils.isNotBlank(message.getToolName())) {
                return message.getToolName();
            }
        }
        return null;
    }

    private void applyAutoRouteDecisionTrace(AiChatIntentRouteVO intentRouteResult, AiAutoRouteDecision decision) {
        if (intentRouteResult == null || decision == null) {
            return;
        }
        putIntentRoutePerformanceMetric(intentRouteResult, "autoRouteEngine", "LAYERED_LOCAL");
        putIntentRoutePerformanceMetric(intentRouteResult, "autoRouteTaskType", decision.getTaskType());
        putIntentRoutePerformanceMetric(intentRouteResult, "autoRouteFinalMode", decision.getFinalMode());
        putIntentRoutePerformanceMetric(intentRouteResult, "autoRouteConfidence", decision.getConfidence());
        putIntentRoutePerformanceMetric(intentRouteResult, "autoRouteDeterministic", decision.isDeterministic());
        putIntentRoutePerformanceMetric(intentRouteResult, "autoRouteNeedsModelArbitration", decision.isRequiresModelArbitration());
        putIntentRoutePerformanceMetric(intentRouteResult, "autoRouteReason", decision.getReason());
        List<Map<String, Object>> candidateAudit = decision.getCandidates().stream()
                .map(AiRouteCandidate::toAuditMap)
                .collect(Collectors.toList());
        putIntentRoutePerformanceMetric(intentRouteResult, "autoRouteCandidates", candidateAudit);
    }

    private void markIntentRouteFallback(AiChatIntentRouteVO intentRouteResult, String finalMode, String reason) {
        if (intentRouteResult == null) {
            return;
        }
        intentRouteResult.setFinalMode(finalMode);
        intentRouteResult.setAdoptedBySystem(Boolean.FALSE);
        intentRouteResult.setFallbackReason(reason);
    }

    private void applyAutoRouteAdoptionDecision(AiChatIntentRouteVO intentRouteResult,
                                                AiAutoRouteAdoptionPolicy.Decision adoptionDecision) {
        if (intentRouteResult == null || adoptionDecision == null) {
            return;
        }
        intentRouteResult.setFinalMode(adoptionDecision.getFinalMode());
        intentRouteResult.setAdoptedBySystem(adoptionDecision.isAdoptedBySystem());
        intentRouteResult.setFallbackReason(adoptionDecision.isAdoptedBySystem()
                ? null
                : resolveAutoRouteAdoptionFallbackReason(adoptionDecision));
    }

    private AiAutoRouteAdoptionPolicy.Options resolveAutoRouteAdoptionOptions() {
        FastBeeAiProperties.RuntimeProperties runtime = properties == null ? null : properties.getRuntime();
        if (runtime == null) {
            return AiAutoRouteAdoptionPolicy.Options.defaults();
        }
        return new AiAutoRouteAdoptionPolicy.Options(
                AiAutoRouteAdoptionPolicy.Options.DEFAULT_CONFIDENCE_THRESHOLD,
                runtime.getAutoRouteGeneralThreshold(),
                runtime.getAutoRoutePlatformAssistantThreshold(),
                runtime.getAutoRouteNl2sqlThreshold(),
                runtime.getAutoRouteDeviceControlThreshold(),
                runtime.getAutoRouteProtocolParseThreshold(),
                AiAutoRouteAdoptionPolicy.Options.DEFAULT_THING_MODEL_GENERATE_THRESHOLD,
                AiAutoRouteAdoptionPolicy.Options.DEFAULT_REQUIREMENT_EVALUATION_THRESHOLD
        );
    }

    private String resolveAutoRouteAdoptionFallbackReason(AiAutoRouteAdoptionPolicy.Decision adoptionDecision) {
        if (adoptionDecision == null) {
            return message("ai.chat.route.fallback.no.result");
        }
        AiAutoRouteAdoptionPolicy.FallbackReason reason = adoptionDecision.getFallbackReason();
        if (reason == AiAutoRouteAdoptionPolicy.FallbackReason.SUSPICIOUS_DEVICE_CONTROL) {
            return message("ai.chat.route.fallback.suspicious.device.control");
        }
        if (reason == AiAutoRouteAdoptionPolicy.FallbackReason.NL2SQL_LOCATION_QUESTION) {
            return message("ai.chat.route.fallback.nl2sql.location.question");
        }
        if (reason == AiAutoRouteAdoptionPolicy.FallbackReason.DEVICE_RUNTIME_READ) {
            return message("ai.chat.route.fallback.device.runtime.read");
        }
        if (reason == AiAutoRouteAdoptionPolicy.FallbackReason.NO_RESULT) {
            return message("ai.chat.route.fallback.no.result");
        }
        if (reason == AiAutoRouteAdoptionPolicy.FallbackReason.PARSE_FAILED) {
            return StringUtils.isNotBlank(adoptionDecision.getFallbackDetail())
                    ? adoptionDecision.getFallbackDetail()
                    : message("ai.chat.route.fallback.parse.failed");
        }
        if (reason == AiAutoRouteAdoptionPolicy.FallbackReason.NO_TARGET_MODE) {
            return message("ai.chat.route.fallback.no.target.mode");
        }
        if (reason == AiAutoRouteAdoptionPolicy.FallbackReason.NO_CONFIDENCE) {
            return message("ai.chat.route.fallback.no.confidence",
                    resolveChatModeLabel(adoptionDecision.getEvaluatedMode()));
        }
        if (reason == AiAutoRouteAdoptionPolicy.FallbackReason.LOW_CONFIDENCE) {
            return message("ai.chat.route.fallback.low.confidence",
                    formatRouteConfidencePercent(adoptionDecision.getConfidence()),
                    resolveChatModeLabel(adoptionDecision.getEvaluatedMode()),
                    formatRouteThresholdPercent(adoptionDecision.getThreshold()));
        }
        return message("ai.chat.route.fallback.local.rule");
    }

    private AiChatIntentRouteVO analyzeIntentRouteQuietly(String question,
                                                          String requestedMode,
                                                          ChatModel chatModel,
                                                          List<AiChatMessage> historyMessages,
                                                          String ruleMode,
                                                          AiChatSendRequest request) {
        if (StringUtils.isBlank(question) || chatModel == null) {
            return null;
        }
        if (!AiChatMode.AUTO.name().equals(requestedMode)
                && !AiChatMode.PLATFORM_ASSISTANT.name().equals(requestedMode)
                && !AiChatMode.NL2SQL.name().equals(requestedMode)
                && !AiChatMode.DEVICE_CONTROL.name().equals(requestedMode)) {
            return null;
        }
        AiAutoRouteDecision localDecision = resolveAutoRouteDecision(question, requestedMode, request, historyMessages);
        AiChatIntentRouteVO fastIntentRoute = buildFastIntentRoute(question, requestedMode, localDecision);
        if (fastIntentRoute != null) {
            return fastIntentRoute;
        }
        if (AiChatMode.AUTO.name().equals(requestedMode) && !shouldInvokeModelRouteArbitration(localDecision)) {
            return buildLayeredLocalIntentRouteResult(question, requestedMode, localDecision,
                    "LAYERED_LOCAL_NO_MODEL_ARBITRATION", true, message("ai.chat.route.fallback.local.rule"));
        }
        AiChatIntentRouteVO result = analyzeIntentRouteWithTimeout(question, requestedMode, chatModel,
                historyMessages, localDecision);
        if (result == null) {
            return null;
        }
        String actualRuleMode = StringUtils.isNotBlank(ruleMode) ? ruleMode : resolveRuleEffectiveMode(question, request, historyMessages);
        result.setRuleMode(actualRuleMode);
        applyAutoRouteDecisionTrace(result, localDecision);
        if (StringUtils.isNotBlank(result.getMode())) {
            result.setMatchedRuleMode(StringUtils.equals(result.getMode(), actualRuleMode));
        }
        return result;
    }

    private AiChatIntentRouteVO buildFastIntentRoute(String question, String requestedMode) {
        return buildFastIntentRoute(question, requestedMode, null, null);
    }

    private AiChatIntentRouteVO buildFastIntentRoute(String question,
                                                     String requestedMode,
                                                     AiChatSendRequest request,
                                                     List<AiChatMessage> historyMessages) {
        return buildFastIntentRoute(question, requestedMode,
                resolveAutoRouteDecision(question, requestedMode, request, historyMessages));
    }

    private AiChatIntentRouteVO buildFastIntentRoute(String question,
                                                     String requestedMode,
                                                     AiAutoRouteDecision decision) {
        if (!AiChatMode.AUTO.name().equals(requestedMode) || StringUtils.isBlank(question)) {
            return null;
        }
        String normalizedQuestion = normalizeRouteQuestion(question);
        if (StringUtils.isBlank(normalizedQuestion)) {
            return null;
        }
        String fastMode = resolveFastIntentRouteMode(decision);
        if (StringUtils.isBlank(fastMode)) {
            return null;
        }
        AiChatIntentRouteVO result = buildFastIntentRouteResult(question, requestedMode, fastMode);
        result.setRuleMode(decision.getFinalMode());
        result.setMatchedRuleMode(StringUtils.equals(fastMode, decision.getFinalMode()));
        applyAutoRouteDecisionTrace(result, decision);
        return result;
    }

    private String resolveFastIntentRouteMode(String normalizedQuestion) {
        if (isCodebaseSafetyQuestion(normalizedQuestion)) {
            return AiChatMode.PLATFORM_ASSISTANT.name();
        }
        return resolveFastIntentRouteMode(resolveAutoRouteDecision(normalizedQuestion, AiChatMode.AUTO.name(), null, null));
    }

    private String resolveFastIntentRouteMode(AiAutoRouteDecision decision) {
        if (decision == null || !decision.isDeterministic() || decision.isRequiresModelArbitration()) {
            return null;
        }
        if (AiChatMode.GENERAL.name().equals(decision.getFinalMode())
                && "GENERAL_CHAT".equals(decision.getTaskType())
                && decision.getConfidence() < resolveAutoRouteFastGeneralMinConfidence()) {
            return null;
        }
        return decision.getFinalMode();
    }

    private boolean shouldInvokeModelRouteArbitration(AiAutoRouteDecision localDecision) {
        if (localDecision == null) {
            return true;
        }
        FastBeeAiProperties.RuntimeProperties runtime = properties == null ? null : properties.getRuntime();
        if (runtime != null && !runtime.isAutoRouteModelArbitrationEnabled()) {
            return false;
        }
        return localDecision.isRequiresModelArbitration()
                || AiChatMode.GENERAL.name().equals(localDecision.getFinalMode());
    }

    private AiChatIntentRouteVO analyzeIntentRouteWithTimeout(String question,
                                                              String requestedMode,
                                                              ChatModel chatModel,
                                                              List<AiChatMessage> historyMessages,
                                                              AiAutoRouteDecision localDecision) {
        int timeoutMs = resolveAutoRouteArbitrationTimeoutMs();
        if (timeoutMs <= 0) {
            return aiChatIntentRouteService.analyze(question, requestedMode, chatModel, historyMessages);
        }
        CompletableFuture<AiChatIntentRouteVO> future = CompletableFuture.supplyAsync(
                () -> aiChatIntentRouteService.analyze(question, requestedMode, chatModel, historyMessages));
        try {
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (TimeoutException ex) {
            future.cancel(true);
            log.warn("AUTO 路由模型仲裁超时，已回退本地分层裁决，timeoutMs={}", timeoutMs);
            return buildLayeredLocalIntentRouteResult(question, requestedMode, localDecision,
                    "MODEL_ARBITRATION_TIMEOUT", false, message("ai.chat.route.fallback.model.arbitration.timeout"));
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return buildLayeredLocalIntentRouteResult(question, requestedMode, localDecision,
                    "MODEL_ARBITRATION_INTERRUPTED", false, message("ai.chat.route.fallback.model.arbitration.interrupted"));
        } catch (ExecutionException ex) {
            Throwable cause = ex.getCause() == null ? ex : ex.getCause();
            log.warn("AUTO 路由模型仲裁异常，已回退本地分层裁决：{}", cause.getMessage());
            return buildLayeredLocalIntentRouteResult(question, requestedMode, localDecision,
                    "MODEL_ARBITRATION_FAILED", false, message("ai.chat.route.fallback.model.arbitration.failed"));
        }
    }

    private AiChatIntentRouteVO buildLayeredLocalIntentRouteResult(String question,
                                                                   String requestedMode,
                                                                   AiAutoRouteDecision decision,
                                                                   String source,
                                                                   boolean routeAnalyzeSkipped,
                                                                   String fallbackReason) {
        String finalMode = decision == null ? AiChatMode.GENERAL.name() : decision.getFinalMode();
        AiChatIntentRouteVO result = buildFastIntentRouteResult(question, requestedMode, finalMode);
        result.setModeConfidence(decision == null
                ? AiAutoRouteAdoptionPolicy.Options.DEFAULT_GENERAL_THRESHOLD
                : decision.getConfidence());
        result.setReason(decision == null ? fallbackReason : decision.getReason());
        result.setFallbackReason(fallbackReason);
        result.setRuleMode(finalMode);
        result.setMatchedRuleMode(Boolean.TRUE);
        putIntentRoutePerformanceMetric(result, "routeAnalyzeSource", source);
        putIntentRoutePerformanceMetric(result, "routeAnalyzeSkipped", routeAnalyzeSkipped);
        applyAutoRouteDecisionTrace(result, decision);
        return result;
    }

    private int resolveAutoRouteArbitrationTimeoutMs() {
        FastBeeAiProperties.RuntimeProperties runtime = properties == null ? null : properties.getRuntime();
        return runtime == null ? 2500 : runtime.getAutoRouteArbitrationTimeoutMs();
    }

    private double resolveAutoRouteFastGeneralMinConfidence() {
        FastBeeAiProperties.RuntimeProperties runtime = properties == null ? null : properties.getRuntime();
        return runtime == null ? 0.90D : runtime.getAutoRouteFastGeneralMinConfidence();
    }

    private String normalizeRouteQuestion(String question) {
        return AiIntentRoutePolicy.normalizeQuestion(question);
    }

    private boolean isAssistantModelIdentityQuestion(String normalizedQuestion) {
        return AiIntentRoutePolicy.isAssistantModelIdentityQuestion(normalizedQuestion);
    }

    private boolean isDeviceControlLocationQuestion(String normalizedQuestion) {
        return AiIntentRoutePolicy.isDeviceControlLocationQuestion(normalizedQuestion);
    }

    private boolean isCodebaseSecondaryDevelopmentQuestion(String normalizedQuestion) {
        return autoRouteFeatureExtractor.isSecondaryDevelopmentQuestion(normalizedQuestion);
    }

    private boolean isCodebaseNavigationQuestion(String normalizedQuestion) {
        return autoRouteFeatureExtractor.isCodebaseGuideQuestion(normalizedQuestion);
    }

    private List<String> resolvePlatformFeatureTokens() {
        long now = System.currentTimeMillis();
        List<String> cachedTokens = platformFeatureMenuTokensCache;
        if (cachedTokens != null && now < platformFeatureMenuTokensExpireAt) {
            return cachedTokens;
        }
        synchronized (this) {
            cachedTokens = platformFeatureMenuTokensCache;
            if (cachedTokens != null && now < platformFeatureMenuTokensExpireAt) {
                return cachedTokens;
            }
            List<String> tokens = loadPlatformFeatureTokensFromMenu();
            platformFeatureMenuTokensCache = tokens;
            platformFeatureMenuTokensExpireAt = now + PLATFORM_FEATURE_MENU_CACHE_TTL_MS;
            return tokens;
        }
    }

    private List<String> loadPlatformFeatureTokensFromMenu() {
        List<String> tokens = new ArrayList<>(AiRouteFeatureExtractor.defaultPlatformFeatureTokens());
        if (sysMenuMapper == null) {
            return List.copyOf(tokens);
        }
        try {
            SysMenu query = new SysMenu();
            query.setVisible("0");
            query.setStatus(0);
            query.setLanguage(DEFAULT_LANGUAGE);
            List<SysMenu> menus = sysMenuMapper.selectMenuList(query);
            if (menus == null || menus.isEmpty()) {
                return List.copyOf(tokens);
            }
            for (SysMenu menu : menus) {
                if (!isPlatformFeatureMenu(menu)) {
                    continue;
                }
                appendPlatformFeatureToken(tokens, menu.getMenuName());
                appendPlatformFeatureToken(tokens, resolvePlatformFeatureSubject(menu.getMenuName()));
            }
        } catch (Exception ex) {
            log.debug("加载系统菜单平台功能词失败，AUTO 路由使用静态功能词兜底", ex);
        }
        return List.copyOf(tokens);
    }

    private boolean isPlatformFeatureMenu(SysMenu menu) {
        if (menu == null || StringUtils.isBlank(menu.getMenuName())) {
            return false;
        }
        return "M".equals(menu.getMenuType()) || "C".equals(menu.getMenuType());
    }

    private String resolvePlatformFeatureSubject(String menuName) {
        if (StringUtils.isBlank(menuName)) {
            return "";
        }
        return normalizePlatformFeatureToken(menuName)
                .replaceAll("(管理|配置|列表|页面|菜单|模块|文档|教程)$", "");
    }

    private void appendPlatformFeatureToken(List<String> tokens, String token) {
        String normalizedToken = normalizePlatformFeatureToken(token);
        if (!isUsablePlatformFeatureToken(normalizedToken) || tokens.contains(normalizedToken)) {
            return;
        }
        tokens.add(normalizedToken);
    }

    private String normalizePlatformFeatureToken(String token) {
        return StringUtils.isBlank(token) ? "" : token.trim().replaceAll("\\s+", "");
    }

    private boolean isUsablePlatformFeatureToken(String token) {
        if (StringUtils.isBlank(token) || token.length() < 2) {
            return false;
        }
        return token.length() >= 4 || !token.matches("[A-Za-z0-9_\\-]+");
    }

    private AiChatIntentRouteVO buildFastIntentRouteResult(String question, String requestedMode, String fastMode) {
        String actualRuleMode = resolveRuleEffectiveMode(question);
        AiChatIntentRouteVO result = new AiChatIntentRouteVO();
        result.setQuestion(question);
        result.setRequestedMode(requestedMode);
        result.setMode(fastMode);
        result.setModeConfidence(AiAutoRouteModeMetadata.fastConfidence(fastMode));
        result.setBusinessType(AiAutoRouteModeMetadata.businessType(fastMode));
        result.setSubjectType("UNKNOWN");
        result.setThingModelTypeHint("UNKNOWN");
        result.setTimeIntent("UNKNOWN");
        result.setAggregateType("NONE");
        result.setNeedClarify(Boolean.FALSE);
        result.setReason(message(AiAutoRouteModeMetadata.fastReasonMessageCode(fastMode)));
        result.setRuleMode(actualRuleMode);
        result.setFinalMode(fastMode);
        result.setMatchedRuleMode(StringUtils.equals(fastMode, actualRuleMode));
        result.setAdoptedBySystem(Boolean.TRUE);
        result.setStructuredOutput(Boolean.FALSE);
        result.setParseStatus("SUCCESS");
        putIntentRoutePerformanceMetric(result, "routeAnalyzeSource", "FAST_DETERMINISTIC");
        putIntentRoutePerformanceMetric(result, "routeAnalyzeSkipped", true);
        putIntentRoutePerformanceMetric(result, "fastRouteMode", fastMode);
        return result;
    }

    private AiChatIntentRouteVO buildProtocolUploadIntentRoute(String question,
                                                               AiChatSendRequest request,
                                                               String modePolicy,
                                                               String originalFilename) {
        AiChatIntentRouteVO result = buildFastIntentRouteResult(question, AiChatMode.AUTO.name(), AiChatMode.PROTOCOL_PARSE.name());
        enrichIntentRouteAudit(result, request, AiChatMode.AUTO.name(), modePolicy);
        result.setReason(message("ai.chat.route.upload.protocol.reason"));
        result.setSubjectType("PROTOCOL_FILE");
        result.setBusinessType("PROTOCOL_PARSE");
        result.setStructuredOutput(Boolean.TRUE);
        putIntentRoutePerformanceMetric(result, "executionType", "PROTOCOL_UPLOAD");
        putIntentRoutePerformanceMetric(result, "protocolFileName", originalFilename);
        putIntentRoutePerformanceMetric(result, "uploadStream", true);
        return result;
    }

    private AiChatIntentRouteVO buildThingModelUploadIntentRoute(String question,
                                                                 AiChatSendRequest request,
                                                                 String modePolicy,
                                                                 String originalFilename) {
        AiChatIntentRouteVO result = buildFastIntentRouteResult(question, AiChatMode.AUTO.name(), AiChatMode.THING_MODEL_GENERATE.name());
        enrichIntentRouteAudit(result, request, AiChatMode.AUTO.name(), modePolicy);
        result.setReason(message("ai.chat.route.upload.thing.model.reason"));
        result.setSubjectType("THING_MODEL_FILE");
        result.setBusinessType("THING_MODEL_GENERATE");
        result.setStructuredOutput(Boolean.TRUE);
        putIntentRoutePerformanceMetric(result, "executionType", "THING_MODEL_UPLOAD");
        putIntentRoutePerformanceMetric(result, "sourceFileName", originalFilename);
        putIntentRoutePerformanceMetric(result, "uploadStream", true);
        return result;
    }

    private AiChatIntentRouteVO buildRequirementEvaluationUploadIntentRoute(String question,
                                                                            AiChatSendRequest request,
                                                                            String modePolicy,
                                                                            String originalFilename) {
        AiChatIntentRouteVO result = buildFastIntentRouteResult(question, AiChatMode.AUTO.name(), AiChatMode.REQUIREMENT_EVALUATION.name());
        enrichIntentRouteAudit(result, request, AiChatMode.AUTO.name(), modePolicy);
        result.setReason(message("ai.chat.route.upload.requirement.evaluation.reason"));
        result.setSubjectType("REQUIREMENT_FILE");
        result.setBusinessType("REQUIREMENT_EVALUATION");
        result.setStructuredOutput(Boolean.TRUE);
        putIntentRoutePerformanceMetric(result, "executionType", "REQUIREMENT_EVALUATION_UPLOAD");
        putIntentRoutePerformanceMetric(result, "sourceFileName", originalFilename);
        putIntentRoutePerformanceMetric(result, "uploadStream", true);
        return result;
    }

    private double resolveIntentRouteThreshold(String mode) {
        return resolveAutoRouteAdoptionOptions().resolveThreshold(mode);
    }

    private String formatRouteConfidencePercent(Double confidence) {
        if (confidence == null) {
            return message("ai.chat.common.unknown");
        }
        return Math.round(confidence * 100) + "%";
    }

    private String formatRouteThresholdPercent(double threshold) {
        return Math.round(threshold * 100) + "%";
    }

    private void attachIntentRouteAudit(AiChatMessage userMessage, AiChatIntentRouteVO intentRouteResult) {
        if (userMessage == null || intentRouteResult == null) {
            return;
        }
        userMessage.setToolName(SKILL_AUTO_ROUTER_ANALYZE);
        userMessage.setToolResult(serializeIntentRouteAudit(intentRouteResult));
    }

    private void updateIntentRouteAuditMessage(AiChatMessage userMessage,
                                               AiChatIntentRouteVO intentRouteResult,
                                               String operatorName) {
        if (userMessage == null || intentRouteResult == null || userMessage.getMessageId() == null) {
            return;
        }
        userMessage.setToolName(SKILL_AUTO_ROUTER_ANALYZE);
        userMessage.setToolResult(serializeIntentRouteAudit(intentRouteResult));
        userMessage.setUpdateBy(StringUtils.defaultIfBlank(operatorName, "system"));
        userMessage.setUpdateTime(AiSecuritySupport.now());
        aiChatMessageService.updateById(userMessage);
    }

    private void putIntentRoutePerformanceMetric(AiChatIntentRouteVO intentRouteResult, String key, Object value) {
        if (intentRouteResult == null || StringUtils.isBlank(key) || value == null) {
            return;
        }
        Map<String, Object> performanceTrace = intentRouteResult.getPerformanceTrace();
        if (performanceTrace == null) {
            performanceTrace = new LinkedHashMap<>();
            intentRouteResult.setPerformanceTrace(performanceTrace);
        }
        performanceTrace.put(key, value);
    }

    private void enrichIntentRouteAudit(AiChatIntentRouteVO intentRouteResult,
                                        AiChatSendRequest request,
                                        String requestedMode,
                                        String modePolicy) {
        if (intentRouteResult == null || request == null) {
            return;
        }
        intentRouteResult.setInteractionSource(resolveInteractionSource(request.getInteractionSource()));
        intentRouteResult.setRetrySourceMessageId(request.getRetrySourceMessageId());
        intentRouteResult.setManualModeSource(resolveManualModeSource(requestedMode, modePolicy, request.getModeOverride()));
    }

    private String serializeIntentRouteAudit(AiChatIntentRouteVO intentRouteResult) {
        if (intentRouteResult == null) {
            return null;
        }
        return JSON.toJSONString(intentRouteResult);
    }

    private String buildContextAwareExecutionQuestion(String question,
                                                      String effectiveMode,
                                                      AiChatIntentRouteVO intentRouteResult,
                                                      List<AiChatMessage> historyMessages) {
        String executionQuestion = buildExecutionQuestion(question, effectiveMode, intentRouteResult);
        AiConversationContextBundleVO contextBundle = buildConversationContextBundle(executionQuestion, effectiveMode, intentRouteResult, historyMessages);
        return StringUtils.isNotBlank(contextBundle.getExecutionQuestion()) ? contextBundle.getExecutionQuestion() : executionQuestion;
    }

    private String buildAttachmentAwareQuestion(String question, AiChatSendRequest request) {
        if (request == null || StringUtils.isBlank(request.getAttachmentText())) {
            return question;
        }
        StringBuilder builder = new StringBuilder(StringUtils.isBlank(question) ? AiPromptConstant.ATTACHMENT_SUMMARY_QUESTION : question.trim());
        builder.append("\n\n").append(AiPromptConstant.ATTACHMENT_CONTEXT_HEADER);
        if (StringUtils.isNotBlank(request.getAttachmentFileName())) {
            builder.append("\n文件名：").append(request.getAttachmentFileName());
        }
        if (StringUtils.isNotBlank(request.getAttachmentContentType())) {
            builder.append("\n内容类型：").append(request.getAttachmentContentType());
        }
        builder.append("\n").append(AiPromptConstant.ATTACHMENT_CONTEXT_REQUIREMENT);
        builder.append("\n").append(AiPromptConstant.ATTACHMENT_CONTEXT_BODY_HEADER).append(":\n").append(request.getAttachmentText());
        return builder.toString();
    }

    private boolean shouldUpgradeAttachmentToProtocolParse(AiChatSendRequest request,
                                                           AiChatAttachmentTextExtractor.ExtractedAttachment attachment) {
        if (request == null || attachment == null) {
            return false;
        }
        if (StringUtils.isNotBlank(request.getModeOverride())
                && !AiChatMode.AUTO.name().equalsIgnoreCase(request.getModeOverride())) {
            return AiChatMode.PROTOCOL_PARSE.name().equalsIgnoreCase(request.getModeOverride());
        }
        if (MODE_POLICY_PINNED.equalsIgnoreCase(request.getModePolicy())
                && StringUtils.isNotBlank(request.getPinnedMode())) {
            return AiChatMode.PROTOCOL_PARSE.name().equalsIgnoreCase(request.getPinnedMode());
        }
        if (StringUtils.isNotBlank(request.getChatMode())
                && !AiChatMode.AUTO.name().equalsIgnoreCase(request.getChatMode())) {
            return AiChatMode.PROTOCOL_PARSE.name().equalsIgnoreCase(request.getChatMode());
        }
        if (isAttachmentFileReviewQuestion(request.getMessage())) {
            return false;
        }
        String signalText = (defaultText(request.getMessage(), "") + " " + defaultText(attachment.fileName(), "") + "\n"
                + defaultText(attachment.text(), "")).toLowerCase(Locale.ROOT);
        return containsAny(signalText,
                "协议", "通讯协议", "通信协议", "报文", "帧头", "帧尾", "功能码", "寄存器",
                "modbus", "mqtt payload", "crc", "checksum", "encoder", "decoder", "编解码",
                "protocol file", "parse protocol", "protocol parse", "protocol adaptation", "code package", "codec", "frame", "register");
    }

    private boolean isAttachmentFileReviewQuestion(String question) {
        return AiIntentRoutePolicy.isFileContentAnalysisQuestion(AiIntentRoutePolicy.normalizeQuestion(question));
    }

    private boolean shouldUpgradeAttachmentToThingModelGenerate(AiChatSendRequest request,
                                                                AiChatAttachmentTextExtractor.ExtractedAttachment attachment) {
        if (request == null || attachment == null) {
            return false;
        }
        if (StringUtils.isNotBlank(request.getModeOverride())
                && !AiChatMode.AUTO.name().equalsIgnoreCase(request.getModeOverride())) {
            return AiChatMode.THING_MODEL_GENERATE.name().equalsIgnoreCase(request.getModeOverride());
        }
        if (MODE_POLICY_PINNED.equalsIgnoreCase(request.getModePolicy())
                && StringUtils.isNotBlank(request.getPinnedMode())) {
            return AiChatMode.THING_MODEL_GENERATE.name().equalsIgnoreCase(request.getPinnedMode());
        }
        if (StringUtils.isNotBlank(request.getChatMode())
                && !AiChatMode.AUTO.name().equalsIgnoreCase(request.getChatMode())) {
            return AiChatMode.THING_MODEL_GENERATE.name().equalsIgnoreCase(request.getChatMode());
        }
        if (isAttachmentFileReviewQuestion(request.getMessage())) {
            return false;
        }
        String signalText = (defaultText(request.getMessage(), "") + " " + defaultText(attachment.fileName(), "") + "\n"
                + defaultText(attachment.text(), "")).toLowerCase(Locale.ROOT);
        boolean hasThingModelSignal = containsAny(signalText,
                "物模型生成", "生成物模型", "物模型导入", "物模型模板", "导入模板", "产品物模型",
                "设备属性", "属性清单", "点位表", "点表", "测点", "遥测", "telemetry", "datapoint", "point list",
                "thing model", "generate thing model", "thing model import", "import template", "property list", "device property");
        boolean hasGenerateIntent = containsAny(signalText,
                "生成", "导入", "模板", "提取", "解析文件", "解析这个文件", "解析这份文件", "上传文件", "生成excel", "生成 excel",
                "generate", "import", "template", "extract", "parse file", "upload file");
        return hasThingModelSignal && hasGenerateIntent;
    }

    private boolean shouldUpgradeAttachmentToRequirementEvaluation(AiChatSendRequest request,
                                                                   AiChatAttachmentTextExtractor.ExtractedAttachment attachment) {
        if (request == null || attachment == null) {
            return false;
        }
        if (StringUtils.isNotBlank(request.getModeOverride())
                && !AiChatMode.AUTO.name().equalsIgnoreCase(request.getModeOverride())) {
            return AiChatMode.REQUIREMENT_EVALUATION.name().equalsIgnoreCase(request.getModeOverride());
        }
        if (MODE_POLICY_PINNED.equalsIgnoreCase(request.getModePolicy())
                && StringUtils.isNotBlank(request.getPinnedMode())) {
            return AiChatMode.REQUIREMENT_EVALUATION.name().equalsIgnoreCase(request.getPinnedMode());
        }
        if (StringUtils.isNotBlank(request.getChatMode())
                && !AiChatMode.AUTO.name().equalsIgnoreCase(request.getChatMode())) {
            return AiChatMode.REQUIREMENT_EVALUATION.name().equalsIgnoreCase(request.getChatMode());
        }
        if (isAttachmentFileReviewQuestion(request.getMessage())) {
            return false;
        }
        String signalText = (defaultText(request.getMessage(), "") + " " + defaultText(attachment.fileName(), "") + "\n"
                + defaultText(attachment.text(), "")).toLowerCase(Locale.ROOT);
        boolean explicitSkillIntent = containsAny(signalText,
                "需求评估", "评估需求", "需求比对", "需求匹配", "需求分析",
                "改造评估", "二开评估", "工作量评估", "可行性评估",
                "requirement evaluation", "evaluate requirement", "requirement analysis", "feasibility evaluation");
        if (explicitSkillIntent) {
            return true;
        }
        boolean hasRequirementSignal = containsAny(signalText,
                "需求文档", "需求文件", "需求说明", "需求清单", "功能清单",
                "招标需求", "招标文件", "客户需求", "requirement", "requirements", "requirement document", "function list");
        boolean hasEvaluateIntent = containsAny(signalText,
                "评估", "比对", "匹配", "可行性", "能否实现", "是否支持",
                "差距", "缺口", "风险", "工作量", "二开", "改造",
                "evaluate", "evaluation", "compare", "match", "feasibility", "support", "gap", "risk", "workload", "customization");
        return hasRequirementSignal && hasEvaluateIntent;
    }

    private String buildExecutionQuestion(String question, String effectiveMode, AiChatIntentRouteVO intentRouteResult) {
        if ((!AiChatMode.NL2SQL.name().equals(effectiveMode) && !AiChatMode.DEVICE_CONTROL.name().equals(effectiveMode))
                || StringUtils.isBlank(question) || intentRouteResult == null) {
            return question;
        }
        StringBuilder builder = new StringBuilder(question.trim());
        boolean appended = false;
        appended = appendExecutionHint(builder, appended,
                extractStringValue(question, "serialNumber", "deviceNumber", "设备编号"),
                "serialNumber=", sanitizeExecutionHint(intentRouteResult.getSerialNumberText()));
        appended = appendExecutionHint(builder, appended,
                extractStringValue(question, "deviceName", "设备名称"),
                "deviceName=", sanitizeExecutionHint(intentRouteResult.getDeviceNameText()));
        appended = appendExecutionHint(builder, appended,
                extractStringValue(question, "productName", "产品名称", "产品名"),
                "productName=", sanitizeExecutionHint(intentRouteResult.getProductNameText()));

        String thingModelText = sanitizeExecutionHint(intentRouteResult.getThingModelText());
        if (StringUtils.isNotBlank(thingModelText) && !question.contains(thingModelText)) {
            builder.append('\n');
            builder.append("物模型 ").append(thingModelText);
            builder.append('\n').append("指标 ").append(thingModelText);
            appended = true;
        }
        return appended ? builder.toString() : question;
    }

    private void persistConversationContextSnapshot(AiChatMessage userMessage,
                                                    AiModeExecutionResult executionResult,
                                                    String operatorName) {
        if (userMessage == null || executionResult == null) {
            return;
        }
        try {
            boolean updated = false;
            if (executionResult.contextSnapshot != null) {
                aiDeviceControlConversationContextService.attachContextSnapshot(userMessage, executionResult.contextSnapshot);
                if ("1".equals(executionResult.contextSnapshot.getRiskConfirmed())) {
                    userMessage.setRiskConfirmed("1");
                }
                updated = true;
            }
            if (executionResult.nl2SqlContextSnapshot != null && aiNl2SqlConversationContextService != null) {
                aiNl2SqlConversationContextService.attachContextSnapshot(userMessage, executionResult.nl2SqlContextSnapshot);
                updated = true;
            }
            if (executionResult.platformAssistantContextSnapshot != null && aiPlatformAssistantConversationContextService != null) {
                aiPlatformAssistantConversationContextService.attachContextSnapshot(userMessage, executionResult.platformAssistantContextSnapshot);
                updated = true;
            }
            if (executionResult.protocolParseContextSnapshot != null && aiProtocolParseConversationContextService != null) {
                aiProtocolParseConversationContextService.attachContextSnapshot(userMessage, executionResult.protocolParseContextSnapshot);
                updated = true;
            }
            if (!updated) {
                return;
            }
            userMessage.setUpdateBy(StringUtils.defaultIfBlank(operatorName, "system"));
            userMessage.setUpdateTime(AiSecuritySupport.now());
            aiChatMessageService.updateById(userMessage);
        } catch (Exception ex) {
            log.warn("AI 会话上下文快照持久化失败，已忽略，不影响本次回答返回: messageId={}, error={}",
                    userMessage.getMessageId(), ex.getMessage(), ex);
        }
    }

    private boolean appendExecutionHint(StringBuilder builder,
                                        boolean appended,
                                        String existingValue,
                                        String prefix,
                                        String hintValue) {
        if (StringUtils.isBlank(prefix) || StringUtils.isBlank(hintValue) || StringUtils.isNotBlank(existingValue)) {
            return appended;
        }
        builder.append('\n');
        builder.append(prefix).append(hintValue);
        return true;
    }

    private String sanitizeExecutionHint(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        String sanitized = value.trim()
                .replace('\r', ' ')
                .replace('\n', ' ');
        return StringUtils.isBlank(sanitized) ? null : sanitized;
    }

    private void appendGlobalContextLine(StringBuilder summary, String value, String label) {
        if (summary == null || StringUtils.isBlank(value) || StringUtils.isBlank(label)) {
            return;
        }
        summary.append("- ").append(label).append("：").append(value.trim()).append('\n');
    }

    private String buildDeviceFocusSummary(AiConversationGlobalContextVO globalContext) {
        if (globalContext == null) {
            return null;
        }
        List<String> parts = new ArrayList<>();
        appendFocusPart(parts, globalContext.getFocusDeviceName());
        appendFocusPart(parts, globalContext.getFocusSerialNumber());
        appendFocusPart(parts, globalContext.getFocusThingModelName());
        appendFocusPart(parts, globalContext.getFocusIdentifier());
        return parts.isEmpty() ? null : String.join(" / ", parts);
    }

    private String buildPlatformFocusSummary(AiConversationGlobalContextVO globalContext) {
        if (globalContext == null) {
            return null;
        }
        List<String> parts = new ArrayList<>();
        appendFocusPart(parts, globalContext.getFocusMenuPath());
        appendFocusPart(parts, globalContext.getFocusPageTitle());
        return parts.isEmpty() ? null : String.join(" / ", parts);
    }

    private String buildProtocolFocusSummary(AiConversationGlobalContextVO globalContext) {
        if (globalContext == null) {
            return null;
        }
        List<String> parts = new ArrayList<>();
        appendFocusPart(parts, globalContext.getFocusProtocolModuleName());
        appendFocusPart(parts, globalContext.getFocusProtocolFieldName());
        appendFocusPart(parts, globalContext.getFocusProtocolFieldCode());
        appendFocusPart(parts, globalContext.getFocusProtocolDataType());
        return parts.isEmpty() ? null : String.join(" / ", parts);
    }

    private void appendFocusPart(List<String> parts, String value) {
        String sanitized = sanitizeExecutionHint(value);
        if (parts == null || StringUtils.isBlank(sanitized) || parts.contains(sanitized)) {
            return;
        }
        parts.add(sanitized);
    }

    private AiModeExecutionResult buildContextSnapshotExecutionResult(String answer,
                                                                      String effectiveMode,
                                                                      String skillName,
                                                                      String toolResult,
                                                                      AiPlatformAssistantContextSnapshotVO platformContextSnapshot,
                                                                      AiProtocolParseContextSnapshotVO protocolContextSnapshot) {
        if (platformContextSnapshot != null) {
            return new AiModeExecutionResult(answer, effectiveMode, skillName, toolResult, platformContextSnapshot);
        }
        if (protocolContextSnapshot != null) {
            return new AiModeExecutionResult(answer, effectiveMode, skillName, toolResult, protocolContextSnapshot);
        }
        return new AiModeExecutionResult(answer, effectiveMode, skillName, toolResult);
    }

    private String callModel(ChatModel chatModel, String prompt) {
        return chatModel.call(prompt);
    }

    private boolean shouldBufferPlatformAssistantAnswerForSafety(String effectiveMode, String question) {
        return AiChatMode.PLATFORM_ASSISTANT.name().equals(effectiveMode)
                && isLikelyCodebaseAssistantQuestion(question);
    }

    private String sanitizeCodebaseAssistantAnswerIfNecessary(String question,
                                                             String effectiveMode,
                                                             String answer,
                                                             AiChatIntentRouteVO intentRouteResult) {
        if (!shouldBufferPlatformAssistantAnswerForSafety(effectiveMode, question) || StringUtils.isBlank(answer)) {
            return answer;
        }
        AiCodebaseAnswerSanitizer.SanitizeResult sanitizeResult = AiCodebaseAnswerSanitizer.sanitize(answer, true);
        if (!sanitizeResult.isChanged()) {
            return answer;
        }
        if (intentRouteResult != null) {
            putIntentRoutePerformanceMetric(intentRouteResult, "codebaseSafetySanitized", true);
            putIntentRoutePerformanceMetric(intentRouteResult, "codebaseSafetyHitTypes",
                    String.join(",", sanitizeResult.getHitTypes()));
        }
        log.warn("源码导航回答触发后置安全拦截: hitTypes={}, question={}",
                sanitizeResult.getHitTypes(), trimLogText(question));
        return sanitizeResult.getContent();
    }

    private String trimLogText(String text) {
        if (StringUtils.isBlank(text)) {
            return "";
        }
        String actualText = text.replaceAll("\\s+", " ").trim();
        return actualText.length() > 120 ? actualText.substring(0, 120) : actualText;
    }

    private String buildConversationPrompt(List<AiChatMessage> historyMessages, String question, String effectiveMode, String extraInstruction) {
        StringBuilder prompt = new StringBuilder(AiPromptConstant.CONVERSATION_PROMPT_TEMPLATE.formatted(effectiveMode));
        prompt.append(AiPromptConstant.CONVERSATION_EXTRA_REQUIREMENT_LABEL)
                .append(AiReplyLanguageSupport.buildModelInstruction(question, historyMessages, AiReplyLanguageSupport.currentLocale()))
                .append('\n');
        if (StringUtils.isNotBlank(extraInstruction)) {
            prompt.append(AiPromptConstant.CONVERSATION_EXTRA_REQUIREMENT_LABEL).append(extraInstruction).append('\n');
        }
        AiConversationContextBundleVO contextBundle = buildConversationContextBundle(question, effectiveMode, null, historyMessages);
        String contextSummary = buildExecutionContextSummary(contextBundle, effectiveMode);
        if (StringUtils.isNotBlank(contextSummary)) {
            prompt.append(AiPromptConstant.CONVERSATION_CONTEXT_SUMMARY_LABEL).append(contextSummary);
        }
        List<AiChatMessage> executionHistoryMessages = filterExecutionHistoryMessages(historyMessages, effectiveMode);
        if (executionHistoryMessages != null && !executionHistoryMessages.isEmpty()) {
            prompt.append(AiPromptConstant.CONVERSATION_HISTORY_LABEL);
            for (AiChatMessage item : executionHistoryMessages) {
                prompt.append(resolveRoleLabel(item.getRoleType())).append('：').append(item.getMessageContent().trim()).append('\n');
            }
        }
        String executionQuestion = StringUtils.isNotBlank(contextBundle.getExecutionQuestion())
                ? contextBundle.getExecutionQuestion()
                : question;
        return prompt.append(AiPromptConstant.CONVERSATION_CURRENT_USER_LABEL).append(executionQuestion).toString();
    }

    private String buildExecutionContextSummary(AiConversationContextBundleVO contextBundle, String effectiveMode) {
        StringBuilder summary = new StringBuilder(256);
        summary.append("- 当前轮只继承与")
                .append(resolveChatModeLabel(effectiveMode))
                .append("相关的低风险对话历史，问数结果、设备控制回执和协议解析结构化结果默认仅作为回放记录。\n");
        AiConversationGlobalContextVO globalContext = contextBundle == null ? null : contextBundle.getGlobalContext();
        List<String> recentUserTopics = globalContext == null ? new ArrayList<>() : globalContext.getRecentUserTopics();
        if (recentUserTopics != null) {
            for (int index = 0; index < recentUserTopics.size(); index++) {
                summary.append("- 最近用户主题");
                if (recentUserTopics.size() > 1) {
                    summary.append(index + 1);
                }
                summary.append("：").append(recentUserTopics.get(index)).append('\n');
            }
        }
        if (AiChatMode.NL2SQL.name().equals(effectiveMode) || AiChatMode.DEVICE_CONTROL.name().equals(effectiveMode)) {
            appendGlobalContextLine(summary,
                    buildDeviceFocusSummary(globalContext),
                    "当前关注设备");
            appendGlobalContextLine(summary,
                    sanitizeExecutionHint(globalContext == null ? null : globalContext.getFocusProductName()),
                    "当前关注产品");
        }
        if (AiChatMode.PLATFORM_ASSISTANT.name().equals(effectiveMode)) {
            appendGlobalContextLine(summary,
                    buildPlatformFocusSummary(globalContext),
                    "当前关注平台页面");
        }
        if (AiChatMode.PROTOCOL_PARSE.name().equals(effectiveMode)) {
            appendGlobalContextLine(summary,
                    buildProtocolFocusSummary(globalContext),
                    "当前关注协议字段");
        }
        String skillContextSummary = contextBundle == null ? null : contextBundle.getSkillContextSummary();
        if (StringUtils.isNotBlank(skillContextSummary)) {
            summary.append("- 当前技能私有上下文：").append(skillContextSummary.trim()).append('\n');
        }
        return summary.toString();
    }

    private AiConversationContextBundleVO buildConversationContextBundle(String question,
                                                                         String effectiveMode,
                                                                         AiChatIntentRouteVO intentRouteResult,
                                                                         List<AiChatMessage> historyMessages) {
        if (aiConversationContextAssembler != null) {
            AiConversationContextBundleVO bundle = aiConversationContextAssembler.buildExecutionContext(question, effectiveMode, intentRouteResult, historyMessages);
            if (bundle != null) {
                return bundle;
            }
        }
        AiConversationContextBundleVO bundle = new AiConversationContextBundleVO();
        bundle.setExecutionQuestion(question);
        bundle.setGlobalContext(buildLegacyGlobalContext(historyMessages));
        return bundle;
    }

    private AiConversationGlobalContextVO buildLegacyGlobalContext(List<AiChatMessage> historyMessages) {
        AiConversationGlobalContextVO globalContext = new AiConversationGlobalContextVO();
        if (historyMessages == null || historyMessages.isEmpty()) {
            return globalContext;
        }
        List<String> recentUserTopics = new ArrayList<>();
        for (int index = historyMessages.size() - 1; index >= 0 && recentUserTopics.size() < 2; index--) {
            AiChatMessage item = historyMessages.get(index);
            if (item == null
                    || !"user".equalsIgnoreCase(item.getRoleType())
                    || StringUtils.isBlank(item.getMessageContent())
                    || "FAIL".equalsIgnoreCase(item.getMessageStatus())) {
                continue;
            }
            String content = item.getMessageContent().trim();
            if (recentUserTopics.contains(content)) {
                continue;
            }
            recentUserTopics.add(0, content);
        }
        globalContext.setRecentUserTopics(recentUserTopics);
        return globalContext;
    }

    private List<AiChatMessage> filterExecutionHistoryMessages(List<AiChatMessage> historyMessages, String effectiveMode) {
        if (historyMessages == null || historyMessages.isEmpty()) {
            return new ArrayList<>();
        }
        List<AiChatMessage> filtered = new ArrayList<>();
        for (AiChatMessage item : historyMessages) {
            if (item == null
                    || StringUtils.isBlank(item.getMessageContent())
                    || "FAIL".equalsIgnoreCase(item.getMessageStatus())
                    || !shouldIncludeExecutionHistoryMessage(item, effectiveMode)) {
                continue;
            }
            filtered.add(item);
        }
        return filtered;
    }

    private boolean shouldIncludeExecutionHistoryMessage(AiChatMessage item, String effectiveMode) {
        if (item == null) {
            return false;
        }
        if (!"assistant".equalsIgnoreCase(item.getRoleType())) {
            return true;
        }
        String abilityType = normalizeExecutionAbilityType(item.getAbilityType());
        if (StringUtils.isBlank(abilityType)) {
            return true;
        }
        if (AiChatMode.GENERAL.name().equals(effectiveMode)) {
            return AiChatMode.GENERAL.name().equals(abilityType)
                    || AiChatMode.PLATFORM_ASSISTANT.name().equals(abilityType);
        }
        if (AiChatMode.PROTOCOL_PARSE.name().equals(effectiveMode)) {
            return AiChatMode.PROTOCOL_PARSE.name().equals(abilityType)
                    || AiChatMode.GENERAL.name().equals(abilityType)
                    || AiChatMode.PLATFORM_ASSISTANT.name().equals(abilityType);
        }
        return AiChatMode.GENERAL.name().equals(abilityType)
                || AiChatMode.PLATFORM_ASSISTANT.name().equals(abilityType);
    }

    private String normalizeExecutionAbilityType(String abilityType) {
        if (StringUtils.isBlank(abilityType)) {
            return null;
        }
        if ("GENERAL_CHAT".equalsIgnoreCase(abilityType)) {
            return AiChatMode.GENERAL.name();
        }
        return abilityType.trim().toUpperCase();
    }

    private AiProtocolKnowledgeContextVO resolveProtocolParseContext(String question) {
        try {
            return aiProtocolKnowledgeService.buildProtocolContext(question);
        } catch (Exception ex) {
            return null;
        }
    }

    private String buildProtocolParseInstruction(AiProtocolKnowledgeContextVO context) {
        StringBuilder instruction = new StringBuilder(AiPromptConstant.CHAT_PROTOCOL_PARSE_INSTRUCTION);
        if (context != null && context.getPromptLines() != null && !context.getPromptLines().isEmpty()) {
            instruction.append('\n').append(AiPromptConstant.CHAT_PROTOCOL_PARSE_KNOWLEDGE_HEADER);
            for (String line : context.getPromptLines()) {
                if (StringUtils.isNotBlank(line)) {
                    instruction.append('\n').append(line);
                }
            }
            if (Boolean.FALSE.equals(context.getMatched())) {
                instruction.append('\n').append(AiPromptConstant.CHAT_PROTOCOL_PARSE_WEAK_MATCH_HINT);
            }
        } else {
            instruction.append('\n').append(AiPromptConstant.CHAT_PROTOCOL_PARSE_NO_MATCH_HINT);
        }
        return instruction.toString();
    }

    private AiPlatformDocKnowledgeContextVO resolvePlatformAssistantContext(String question) {
        AiPlatformDocKnowledgeContextVO platformContext = null;
        try {
            platformContext = aiPlatformDocKnowledgeService.buildPlatformContext(question);
        } catch (Exception ex) {
            platformContext = null;
        }
        AiCodebaseGuideContextVO codebaseContext = filterCodebaseContextForInjection(question, resolveCodebaseGuideContext(question));
        return mergePlatformAndCodebaseContext(question, platformContext, codebaseContext);
    }

    private AiCodebaseGuideContextVO filterCodebaseContextForInjection(String question, AiCodebaseGuideContextVO codebaseContext) {
        if (!isCodebaseContextMatched(codebaseContext) || !shouldInjectCodebaseGuideContext(question, codebaseContext)) {
            return null;
        }
        List<AiCodebaseGuideItemVO> items = codebaseContext.getItems();
        if (items == null || items.isEmpty()) {
            return codebaseContext;
        }
        List<AiCodebaseGuideItemVO> filteredItems = filterCodebaseGuideItemsForInjection(question, items);
        if (filteredItems.isEmpty()) {
            return null;
        }
        AiCodebaseGuideContextVO filteredContext = copyCodebaseContextMetadata(codebaseContext);
        filteredContext.setMatched(Boolean.TRUE);
        filteredContext.setMatchedItems(filteredItems.size());
        filteredContext.setItems(filteredItems);
        filteredContext.setPromptLines(buildCodebasePromptLines(filteredContext, filteredItems));
        return filteredContext;
    }

    private boolean isCodebaseContextMatched(AiCodebaseGuideContextVO codebaseContext) {
        return codebaseContext != null
                && Boolean.TRUE.equals(codebaseContext.getMatched())
                && codebaseContext.getPromptLines() != null
                && !codebaseContext.getPromptLines().isEmpty();
    }

    private boolean shouldInjectCodebaseGuideContext(String question, AiCodebaseGuideContextVO codebaseContext) {
        if (!isCodebaseContextMatched(codebaseContext)) {
            return false;
        }
        if (hasStrongCodebaseGuideIntent(question)) {
            return true;
        }
        return hasConcreteCodebaseHit(question, codebaseContext);
    }

    private boolean hasStrongCodebaseGuideIntent(String question) {
        if (StringUtils.isBlank(question)) {
            return false;
        }
        String normalizedQuestion = normalizeRouteQuestion(question);
        String normalizedAssistantQuestion = normalizeAssistantQuestion(question);
        if (isCodebaseSafetyQuestion(normalizedAssistantQuestion)
                || isCodebaseNavigationQuestion(normalizedQuestion)
                || isCodebaseSecondaryDevelopmentQuestion(normalizedQuestion)
                || isGenericInterfaceCallSiteQuestion(normalizedAssistantQuestion)
                || isDeviceControlCodeQuestion(normalizedAssistantQuestion)) {
            return true;
        }
        boolean deviceControlLocationWithCodeSignal = isDeviceControlLocationQuestion(normalizedQuestion)
                && containsAny(normalizedAssistantQuestion, "源码", "代码", "接口", "调用", "类", "方法",
                "二开", "二次开发", "开发", "controller", "service", "mapper", "api",
                "sourcecode", "code", "endpoint", "callsite", "class", "method");
        if (deviceControlLocationWithCodeSignal) {
            return true;
        }
        return AiCodebaseQuestionClassifier.isLocationOnlyQuestion(normalizedAssistantQuestion)
                && containsAny(normalizedAssistantQuestion, "源码", "代码", "二开", "二次开发", "改代码",
                "类", "方法", "接口", "调用", "表结构", "哪个表", "哪张表",
                "权限", "日志", "回执", "落库", "前端", "后端", "controller", "service", "mapper",
                "api", "sql", "sourcecode", "code", "class", "method", "endpoint", "callsite",
                "frontend", "backend", "table");
    }

    private boolean hasConcreteCodebaseHit(String question, AiCodebaseGuideContextVO codebaseContext) {
        if (StringUtils.isBlank(question) || codebaseContext == null || codebaseContext.getItems() == null) {
            return false;
        }
        String normalizedQuestion = normalizeAssistantQuestion(question);
        for (AiCodebaseGuideItemVO item : codebaseContext.getItems()) {
            if (item == null) {
                continue;
            }
            Integer score = item.getMatchScore();
            if (score == null || score < 90) {
                continue;
            }
            if (containsConcreteCodebaseToken(normalizedQuestion, item)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsConcreteCodebaseToken(String normalizedQuestion, AiCodebaseGuideItemVO item) {
        return containsNormalizedValue(normalizedQuestion, item.getSourcePath())
                || containsNormalizedValue(normalizedQuestion, item.getClassName())
                || containsNormalizedValue(normalizedQuestion, item.getMethodName())
                || containsNormalizedValue(normalizedQuestion, item.getSymbolName())
                || containsNormalizedValue(normalizedQuestion, item.getEndpointPath());
    }

    private boolean containsNormalizedValue(String normalizedQuestion, String value) {
        if (StringUtils.isBlank(normalizedQuestion) || StringUtils.isBlank(value)) {
            return false;
        }
        String normalizedValue = normalizeAssistantQuestion(value);
        return normalizedValue.length() >= 4 && normalizedQuestion.contains(normalizedValue);
    }

    private List<AiCodebaseGuideItemVO> filterCodebaseGuideItemsForInjection(String question, List<AiCodebaseGuideItemVO> items) {
        if (items == null || items.isEmpty()) {
            return new ArrayList<>();
        }
        List<AiCodebaseGuideItemVO> candidates = items.stream()
                .filter(Objects::nonNull)
                .filter(item -> item.getMatchScore() == null || item.getMatchScore() >= 20)
                .collect(Collectors.toCollection(ArrayList::new));
        if (candidates.isEmpty()) {
            return candidates;
        }
        if (shouldPreferBusinessCodebaseItems(question, candidates)) {
            List<AiCodebaseGuideItemVO> nonAiItems = candidates.stream()
                    .filter(item -> !isFastbeeAiCodebaseItem(item))
                    .collect(Collectors.toCollection(ArrayList::new));
            if (!nonAiItems.isEmpty()) {
                candidates = nonAiItems;
            }
        }
        int bestScore = candidates.stream()
                .map(AiCodebaseGuideItemVO::getMatchScore)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(0);
        int scoreFloor = Math.max(20, bestScore - 35);
        return candidates.stream()
                .filter(item -> item.getMatchScore() == null || item.getMatchScore() >= scoreFloor)
                .limit(8)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private boolean shouldPreferBusinessCodebaseItems(String question, List<AiCodebaseGuideItemVO> items) {
        if (StringUtils.isBlank(question) || items == null || items.isEmpty()) {
            return false;
        }
        String normalizedQuestion = normalizeAssistantQuestion(question);
        if (isAiCodebaseDomainQuestion(normalizedQuestion) || !isBusinessCodebaseDomainQuestion(normalizedQuestion)) {
            return false;
        }
        boolean hasAiItem = items.stream().anyMatch(this::isFastbeeAiCodebaseItem);
        boolean hasNonAiItem = items.stream().anyMatch(item -> !isFastbeeAiCodebaseItem(item));
        return hasAiItem && hasNonAiItem;
    }

    private boolean isBusinessCodebaseDomainQuestion(String normalizedQuestion) {
        return containsAny(normalizedQuestion,
                "设备", "设备控制", "设备下发", "产品", "产品管理", "物模型", "产品物模型",
                "原始数据", "上报数据", "数据上报", "时序数据库", "物联网数据表",
                "规则引擎", "场景联动", "告警", "通知", "数据中心", "数据桥接",
                "iot", "thingmodel", "device", "product", "runtime", "functionlog");
    }

    private boolean isAiCodebaseDomainQuestion(String normalizedQuestion) {
        return containsAny(normalizedQuestion,
                "ai会话", "ai助手", "ai模块", "平台助手", "智能问数", "问数", "源码导航",
                "知识库", "大模型", "模型路由", "nl2sql", "fastbee-ai", "aichat",
                "platformassistant", "codebaseguide");
    }

    private boolean isFastbeeAiCodebaseItem(AiCodebaseGuideItemVO item) {
        if (item == null) {
            return false;
        }
        String normalizedPath = normalizeAssistantQuestion(item.getSourcePath()).replace('\\', '/');
        String normalizedModule = normalizeAssistantQuestion(item.getModuleName());
        return normalizedPath.contains("fastbee-ai")
                || "fastbee-ai".equals(normalizedModule);
    }

    private AiCodebaseGuideContextVO copyCodebaseContextMetadata(AiCodebaseGuideContextVO source) {
        AiCodebaseGuideContextVO target = new AiCodebaseGuideContextVO();
        if (source == null) {
            return target;
        }
        target.setQuestion(source.getQuestion());
        target.setRuntimeSource(source.getRuntimeSource());
        target.setKnowledgeBaseId(source.getKnowledgeBaseId());
        target.setKbCode(source.getKbCode());
        target.setKbName(source.getKbName());
        target.setVersionId(source.getVersionId());
        target.setVersionNo(source.getVersionNo());
        target.setTotalItems(source.getTotalItems());
        return target;
    }

    private List<String> buildCodebasePromptLines(AiCodebaseGuideContextVO context, List<AiCodebaseGuideItemVO> items) {
        List<String> lines = new ArrayList<>();
        lines.add("当前已加载源码导航知识快照：知识库=" + defaultText(context == null ? null : context.getKbName())
                + "，版本=" + defaultText(context == null ? null : context.getVersionNo())
                + "，安全摘要条目数=" + defaultText(context == null ? null : context.getTotalItems())
                + "。注意：源码导航只提供路径、类名、方法名、接口路径和伪代码级思路，禁止输出真实源码正文、配置值或密钥。");
        if (items == null) {
            return lines;
        }
        for (AiCodebaseGuideItemVO item : items) {
            if (item == null) {
                continue;
            }
            lines.add(buildCodebasePromptLine(item));
        }
        return lines;
    }

    private String buildCodebasePromptLine(AiCodebaseGuideItemVO item) {
        StringBuilder line = new StringBuilder("- 源码定位：路径=").append(defaultText(item.getSourcePath()));
        if (StringUtils.isNotBlank(item.getLayer())) {
            line.append("；分层=").append(item.getLayer());
        }
        if (StringUtils.isNotBlank(item.getSymbolType())) {
            line.append("；类型=").append(item.getSymbolType());
        }
        if (StringUtils.isNotBlank(item.getClassName())) {
            line.append("；类/组件=").append(item.getClassName());
        }
        if (StringUtils.isNotBlank(item.getMethodName())) {
            line.append("；方法=").append(item.getMethodName());
        }
        if (StringUtils.isNotBlank(item.getSignature())) {
            line.append("；签名摘要=").append(shortenCodebaseText(item.getSignature(), 220));
        }
        if (StringUtils.isNotBlank(item.getHttpMethod()) || StringUtils.isNotBlank(item.getEndpointPath())) {
            line.append("；接口=").append(StringUtils.defaultIfBlank(item.getHttpMethod(), ""))
                    .append(" ").append(StringUtils.defaultIfBlank(item.getEndpointPath(), "-"));
        }
        if (StringUtils.isNotBlank(item.getSummary())) {
            line.append("；职责=").append(shortenCodebaseText(item.getSummary(), 160));
        }
        if (StringUtils.isNotBlank(item.getDevHint())) {
            line.append("；二开提示=").append(shortenCodebaseText(item.getDevHint(), 180));
        }
        if (item.getTags() != null && !item.getTags().isEmpty()) {
            line.append("；标签=").append(String.join("、", item.getTags()));
        }
        return line.toString();
    }

    private String shortenCodebaseText(String text, int maxLength) {
        if (StringUtils.isBlank(text) || maxLength <= 0) {
            return "";
        }
        String normalized = text.trim().replaceAll("\\s+", " ");
        return normalized.length() <= maxLength ? normalized : normalized.substring(0, maxLength);
    }

    private AiCodebaseGuideContextVO resolveCodebaseGuideContext(String question) {
        try {
            return aiCodebaseGuideKnowledgeService.buildCodebaseContext(question);
        } catch (Exception ex) {
            return null;
        }
    }

    private AiPlatformDocKnowledgeContextVO mergePlatformAndCodebaseContext(String question,
                                                                           AiPlatformDocKnowledgeContextVO platformContext,
                                                                           AiCodebaseGuideContextVO codebaseContext) {
        boolean codebaseMatched = codebaseContext != null
                && Boolean.TRUE.equals(codebaseContext.getMatched())
                && codebaseContext.getPromptLines() != null
                && !codebaseContext.getPromptLines().isEmpty();
        if (!codebaseMatched) {
            appendCodebaseNoMatchGuardIfNecessary(question, platformContext);
            return platformContext;
        }
        AiPlatformDocKnowledgeContextVO mergedContext = platformContext == null
                ? new AiPlatformDocKnowledgeContextVO()
                : platformContext;
        mergedContext.setQuestion(question);
        if (!Boolean.TRUE.equals(mergedContext.getMatched())) {
            mergedContext.setKnowledgeBaseId(codebaseContext.getKnowledgeBaseId());
            mergedContext.setKbCode(codebaseContext.getKbCode());
            mergedContext.setKbName(codebaseContext.getKbName());
            mergedContext.setVersionId(codebaseContext.getVersionId());
            mergedContext.setVersionNo(codebaseContext.getVersionNo());
            mergedContext.setTotalItems(codebaseContext.getTotalItems());
            mergedContext.setTotalChunks(codebaseContext.getMatchedItems());
        }
        mergedContext.setMatched(Boolean.TRUE);
        if (mergedContext.getPromptLines() == null) {
            mergedContext.setPromptLines(new ArrayList<>());
        }
        mergedContext.getPromptLines().addAll(codebaseContext.getPromptLines());
        appendCodebaseMatchedGuardIfNecessary(question, mergedContext);
        return mergedContext;
    }

    private void appendCodebaseMatchedGuardIfNecessary(String question, AiPlatformDocKnowledgeContextVO mergedContext) {
        if (!isLikelyCodebaseAssistantQuestion(question) || mergedContext == null) {
            return;
        }
        if (mergedContext.getPromptLines() == null) {
            mergedContext.setPromptLines(new ArrayList<>());
        }
        List<String> promptLines = mergedContext.getPromptLines();
        promptLines.add("- 源码导航回答边界：只能引用本轮“源码定位：路径=”行中已经出现的源码路径、类名、方法名和接口路径；没有出现在源码定位中的路径、类名、方法名、接口路径不得作为当前项目结论输出。只有当全部源码定位都来自 fastbee-ai 时，才可以说只命中 AI 会话扩展；如果源码定位中已出现基础 IoT 链路，不得再概括为“集中在 AI 会话设备控制扩展”。");
        promptLines.add("- 源码导航层级边界：如果某一层级没有出现在本轮“源码定位：路径=”行中，例如前端页面、前端 API、Mapper XML、SQL、定时任务等，不得用“通常可关注、可能在、建议看”补充任何推测路径；只能说明“当前源码导航未命中该层路径”，并建议用户补充更具体关键词或重建源码导航库。");
        appendCodebaseSafetyAnswerGuardIfNecessary(question, promptLines);
        appendSecondaryDevelopmentAnswerGuardIfNecessary(question, promptLines);
        appendDeviceControlCodebaseOwnershipGuardIfNecessary(question, promptLines);
        appendCodebaseLocationAnswerShapeGuardIfNecessary(question, promptLines);
    }

    private void appendDeviceControlCodebaseOwnershipGuardIfNecessary(String question, List<String> promptLines) {
        String normalizedQuestion = normalizeAssistantQuestion(question);
        if (!isDeviceControlCodeQuestion(normalizedQuestion) || promptLines == null || promptLines.isEmpty()) {
            return;
        }
        boolean hasIotSource = promptLines.stream().map(this::normalizeCodebasePromptLine)
                .filter(this::isCodebaseLocationLine)
                .anyMatch(AiCodebaseQuestionClassifier::containsDeviceControlIotSource);
        boolean hasAiSource = promptLines.stream().map(this::normalizeCodebasePromptLine)
                .filter(this::isCodebaseLocationLine)
                .anyMatch(AiCodebaseQuestionClassifier::containsDeviceControlAiSource);
        if (hasIotSource) {
            promptLines.add("- 源码导航归属提示：本轮已经命中基础 IoT 设备控制链路。回答开头应称为“基础 IoT 设备控制链路/设备服务调用链路”，不得称为“集中在 AI 会话设备控制扩展”。如果同时命中 AI 扩展，只能说明 AI 会话是自然语言入口或扩展层。");
        } else if (hasAiSource) {
            promptLines.add("- 源码导航归属提示：本轮只命中 fastbee-ai 里的 AI 会话设备控制扩展，未定位到基础 IoT 设备控制核心链路；回答必须明确该边界，不要补猜 IoT 模块路径。");
        }
    }

    private void appendCodebaseLocationAnswerShapeGuardIfNecessary(String question, List<String> promptLines) {
        String normalizedQuestion = normalizeAssistantQuestion(question);
        if (promptLines == null || !AiCodebaseQuestionClassifier.isLocationOnlyQuestion(normalizedQuestion)) {
            return;
        }
        promptLines.add("- 回答形态提示：用户当前主要是在问源码位置、接口入口、前端页面、权限校验、数据落库、调用链或二开修改点。应优先输出命中的源码定位清单、各位置职责和通用改造顺序；不要主动虚构用户没有提出的示例需求、字段名、参数名或接口参数。若需要描述顺序，只用“前端页面/API -> Controller -> Service -> Mapper/实体表 -> 验证”这类中性分层表述，不写“新增/增加/扩展某参数”这类示例。源码定位清单可以使用 Markdown 表格展示，表头建议为“源码路径、类/组件、方法/接口、职责”；权限、日志、回执、落库、表结构类问题要优先列出已命中的 Controller、Service、Mapper、实体表或 SQL 表，未命中的层级只说明未命中；通用改造顺序使用普通中文分段或短横线列表，不使用 #### 标题或堆叠 **加粗** 标记。");
        promptLines.add("- 数据访问命中提示：如果本轮源码定位或签名摘要已经出现某个 Mapper 接口、实体类、表名、ServiceImpl 中的 Mapper 调用，说明数据访问层已命中；MyBatis-Plus BaseMapper 场景不要求一定存在 Mapper XML 节点，不要再说 Mapper/XML/SQL 未命中。");
        if (isGenericInterfaceCallSiteQuestion(normalizedQuestion)) {
            promptLines.add("- 具体接口缺失提示：如果用户只说“某个接口/某个方法/某个 API 在哪里调用”，但没有给出具体接口路径、前端 API 方法名或明确业务功能，不要罗列随机接口作为答案；应先说明需要补充具体接口路径或方法名，并给出可用于继续定位的输入示例。");
        }
    }

    private void appendCodebaseNoMatchGuardIfNecessary(String question, AiPlatformDocKnowledgeContextVO platformContext) {
        if (!isLikelyCodebaseAssistantQuestion(question) || !isPlatformAssistantKnowledgeMatched(platformContext)) {
            return;
        }
        if (platformContext.getPromptLines() == null) {
            platformContext.setPromptLines(new ArrayList<>());
        }
        platformContext.getPromptLines().add("- 源码导航未命中：当前问题疑似询问当前项目源码、二开或代码位置，但本轮没有命中“源码定位：路径=”摘要。回答时不得编造源码路径、类名、方法名、接口路径或代码片段；只能基于已命中文档说明方向，并建议先重建并发布源码导航库，或让用户补充更具体的功能关键词。");
        appendCodebaseSafetyAnswerGuardIfNecessary(question, platformContext.getPromptLines());
        appendSecondaryDevelopmentAnswerGuardIfNecessary(question, platformContext.getPromptLines());
    }

    private void appendSecondaryDevelopmentAnswerGuardIfNecessary(String question, List<String> promptLines) {
        String normalizedQuestion = normalizeAssistantQuestion(question);
        if (promptLines == null || !isCodebaseSecondaryDevelopmentQuestion(normalizedQuestion)) {
            return;
        }
        promptLines.add(AiPromptConstant.CODEBASE_SECONDARY_DEVELOPMENT_ANSWER_SKELETON);
        promptLines.add(AiPromptConstant.CODEBASE_SECONDARY_DEVELOPMENT_FOLLOWUP_HINT);
    }

    private void appendCodebaseSafetyAnswerGuardIfNecessary(String question, List<String> promptLines) {
        if (promptLines == null || !isCodebaseSafetyQuestion(question)) {
            return;
        }
        promptLines.add("- 安全兜底：如果用户索要完整源码、真实源码片段、Mapper XML / SQL 原文、application-dev.yml、数据库密码、Redis 地址、API Key、Token、连接串或完整实现代码块，必须改成安全版回答；只给路径、类名、方法名、配置入口、抽象伪代码和脱敏排查步骤，不得输出原文。流式回答也不得先输出真实源码片段。");
    }

    private boolean isLikelyCodebaseAssistantQuestion(String question) {
        return AiCodebaseQuestionClassifier.isLikelyCodebaseQuestion(question);
    }

    private boolean isDeviceControlCodeQuestion(String normalizedQuestion) {
        return AiCodebaseQuestionClassifier.isDeviceControlCodeQuestion(normalizedQuestion);
    }

    private boolean isGenericInterfaceCallSiteQuestion(String normalizedQuestion) {
        return AiCodebaseQuestionClassifier.isGenericInterfaceCallSiteQuestion(normalizedQuestion);
    }

    private String buildGenericInterfaceCallSiteClarificationAnswer() {
        return message("ai.chat.codebase.generic.interface.clarification");
    }

    private String normalizeAssistantQuestion(String question) {
        return AiCodebaseQuestionClassifier.normalize(question);
    }

    private boolean isCodebaseSafetyQuestion(String question) {
        return AiCodebaseQuestionClassifier.isCodebaseSafetyQuestion(question);
    }

    private boolean isRawCodebaseLeakQuestion(String normalizedQuestion) {
        return AiCodebaseQuestionClassifier.isRawCodebaseLeakQuestion(normalizedQuestion);
    }

    private boolean isSensitiveConfigLeakQuestion(String normalizedQuestion) {
        return AiCodebaseQuestionClassifier.isSensitiveConfigLeakQuestion(normalizedQuestion);
    }

    private boolean isSqlXmlLeakQuestion(String normalizedQuestion) {
        return AiCodebaseQuestionClassifier.isSqlXmlLeakQuestion(normalizedQuestion);
    }

    private boolean isCompleteImplementationLeakQuestion(String normalizedQuestion) {
        return AiCodebaseQuestionClassifier.isCompleteImplementationLeakQuestion(normalizedQuestion);
    }

    private boolean isStreamingSourceLeakQuestion(String normalizedQuestion) {
        return AiCodebaseQuestionClassifier.isStreamingSourceLeakQuestion(normalizedQuestion);
    }

    private String buildCodebaseSafetyFallbackAnswer(String question, AiPlatformDocKnowledgeContextVO context) {
        if (!isCodebaseSafetyQuestion(question)) {
            return null;
        }
        String normalizedQuestion = normalizeAssistantQuestion(question).toLowerCase(Locale.ROOT);
        CodebaseSafetyFocus focus = resolveCodebaseSafetyFocus(normalizedQuestion);
        List<String> sections = new ArrayList<>();
        sections.add(AiPromptConstant.CODEBASE_SAFETY_FALLBACK_PREFIX);
        if (isCodebaseSecondaryDevelopmentQuestion(normalizedQuestion)) {
            sections.add(AiPromptConstant.CODEBASE_SECONDARY_DEVELOPMENT_ANSWER_SKELETON);
        }
        if (focus == CodebaseSafetyFocus.SQL_XML) {
            sections.add(AiPromptConstant.CODEBASE_SAFETY_SQL_HINT);
        } else if (focus == CodebaseSafetyFocus.CONFIG) {
            sections.add(AiPromptConstant.CODEBASE_SAFETY_CONFIG_HINT);
        } else if (focus == CodebaseSafetyFocus.COMPLETE_IMPLEMENTATION) {
            sections.add(AiPromptConstant.CODEBASE_SAFETY_PSEUDOCODE_HINT);
        } else if (focus == CodebaseSafetyFocus.STREAMING) {
            sections.add(AiPromptConstant.CODEBASE_SAFETY_STREAM_HINT);
        } else if (isRawCodebaseLeakQuestion(normalizedQuestion)) {
            sections.add(AiPromptConstant.CODEBASE_SAFETY_LOCATION_HINT);
        }
        if (focus != CodebaseSafetyFocus.COMPLETE_IMPLEMENTATION) {
            String locationTable = buildCodebaseLocationTable(normalizedQuestion, context, focus);
            if (StringUtils.isNotBlank(locationTable)) {
                sections.add(message("ai.chat.codebase.location.entry.title") + "\n" + locationTable);
            } else {
                sections.add(buildCodebaseSafetyLocationFallbackHint(focus));
            }
        }
        sections.add(AiPromptConstant.CODEBASE_SAFETY_FOLLOWUP_HINT);
        return String.join("\n\n", sections);
    }

    private String buildCodebaseLocationTable(String normalizedQuestion,
                                              AiPlatformDocKnowledgeContextVO context,
                                              CodebaseSafetyFocus focus) {
        if (context == null || context.getPromptLines() == null || context.getPromptLines().isEmpty()) {
            return "";
        }
        List<CodebaseLocationRow> rows = new ArrayList<>();
        Set<String> seenKeys = new LinkedHashSet<>();
        for (String line : context.getPromptLines()) {
            if (StringUtils.isBlank(line)) {
                continue;
            }
            String trimmed = line.trim();
            if (!trimmed.startsWith("- 源码定位：路径=") && !trimmed.startsWith("源码定位：路径=")) {
                continue;
            }
            CodebaseLocationRow row = parseCodebaseLocationRow(line);
            if (!looksLikeCodebaseSourcePath(row.sourcePath())) {
                continue;
            }
            if (!isQuestionRelevantLocationRow(normalizedQuestion, row, focus)) {
                continue;
            }
            String dedupeKey = row.sourcePath() + "|" + row.className() + "|" + row.methodName() + "|" + row.summary();
            if (!seenKeys.add(dedupeKey)) {
                continue;
            }
            rows.add(row);
            if (rows.size() >= 5) {
                break;
            }
        }
        if (rows.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        builder.append(message("ai.chat.codebase.location.table.header")).append('\n');
        builder.append("| --- | --- | --- | --- |\n");
        for (CodebaseLocationRow row : rows) {
            builder.append("| ")
                    .append(escapeMarkdownCell(row.sourcePath()))
                    .append(" | ")
                    .append(escapeMarkdownCell(row.className()))
                    .append(" | ")
                    .append(escapeMarkdownCell(row.methodName()))
                    .append(" | ")
                    .append(escapeMarkdownCell(row.summary()))
                    .append(" |\n");
        }
        return builder.toString().trim();
    }

    private boolean isQuestionRelevantLocationRow(String normalizedQuestion, CodebaseLocationRow row, CodebaseSafetyFocus focus) {
        String normalizedRow = normalizeCodebasePromptLine(String.join(" ",
                defaultText(row.sourcePath()),
                defaultText(row.className()),
                defaultText(row.methodName()),
                defaultText(row.summary())));
        return switch (focus) {
            case CONFIG -> isConfigLocationRow(normalizedRow);
            case SQL_XML -> isDataAccessLocationRow(normalizedRow);
            case COMPLETE_IMPLEMENTATION -> false;
            case STREAMING, RAW_SOURCE -> containsAny(normalizedRow, "源码定位", "java", "vue", "xml", "sql", "controller",
                    "service", "mapper", "config", "page", "api", "function", "device", "product", "knowledge", "protocol")
                    || containsAny(normalizedQuestion, "源码", "代码", "二开", "开发");
        };
    }

    private boolean isDataAccessLocationRow(String normalizedRow) {
        if (StringUtils.isBlank(normalizedRow)) {
            return false;
        }
        return containsAny(normalizedRow, "mapper", "mybatis", "sql", "xml", "dao", "repository", "table", "entity")
                && !containsAny(normalizedRow, "controller", "vue", "page", "frontend");
    }

    private boolean isConfigLocationRow(String normalizedRow) {
        if (StringUtils.isBlank(normalizedRow)) {
            return false;
        }
        return containsAny(normalizedRow, "application", "bootstrap", "config", "yml", "yaml", "properties",
                "redis", "datasource", "sa-token", "security", "oss", "storage", "nacos", "profile")
                || normalizedRow.endsWith(".yml")
                || normalizedRow.endsWith(".yaml")
                || normalizedRow.endsWith(".properties");
    }

    private String buildCodebaseSafetyLocationFallbackHint(CodebaseSafetyFocus focus) {
        return switch (focus) {
            case CONFIG -> message("ai.chat.codebase.fallback.config");
            case SQL_XML -> message("ai.chat.codebase.fallback.sql.xml");
            case COMPLETE_IMPLEMENTATION -> message("ai.chat.codebase.fallback.complete.implementation");
            case STREAMING, RAW_SOURCE -> message("ai.chat.codebase.fallback.source.location");
        };
    }

    private CodebaseSafetyFocus resolveCodebaseSafetyFocus(String normalizedQuestion) {
        if (isSensitiveConfigLeakQuestion(normalizedQuestion)) {
            return CodebaseSafetyFocus.CONFIG;
        }
        if (isCompleteImplementationLeakQuestion(normalizedQuestion)) {
            return CodebaseSafetyFocus.COMPLETE_IMPLEMENTATION;
        }
        if (isSqlXmlLeakQuestion(normalizedQuestion)) {
            return CodebaseSafetyFocus.SQL_XML;
        }
        if (isStreamingSourceLeakQuestion(normalizedQuestion)) {
            return CodebaseSafetyFocus.STREAMING;
        }
        return CodebaseSafetyFocus.RAW_SOURCE;
    }

    private CodebaseLocationRow parseCodebaseLocationRow(String line) {
        String sourcePath = extractCodebasePromptValue(line, "路径");
        String className = defaultText(extractCodebasePromptValue(line, "类/组件"), extractCodebasePromptValue(line, "类"));
        className = defaultText(className, extractCodebasePromptValue(line, "组件"));
        String methodName = defaultText(extractCodebasePromptValue(line, "方法/接口"), extractCodebasePromptValue(line, "方法"));
        methodName = defaultText(methodName, extractCodebasePromptValue(line, "接口"));
        String summary = defaultText(extractCodebasePromptValue(line, "职责"), extractCodebasePromptValue(line, "二开提示"));
        summary = defaultText(summary, extractCodebasePromptValue(line, "签名摘要"));
        summary = defaultText(summary, extractCodebasePromptValue(line, "分层"));
        return new CodebaseLocationRow(defaultText(sourcePath), defaultText(className), defaultText(methodName), defaultText(summary));
    }

    private String extractCodebasePromptValue(String line, String key) {
        if (StringUtils.isBlank(line) || StringUtils.isBlank(key)) {
            return null;
        }
        String prefix = key + "=";
        String[] segments = line.split("；");
        for (String segment : segments) {
            String trimmed = StringUtils.defaultString(segment).trim();
            int index = trimmed.indexOf(prefix);
            if (index >= 0) {
                return trimmed.substring(index + prefix.length()).trim();
            }
        }
        return null;
    }

    private String escapeMarkdownCell(String value) {
        String sanitized = defaultText(value);
        return sanitized.replace("|", "\\|").replace("\r", " ").replace("\n", " ").trim();
    }

    private boolean looksLikeCodebaseSourcePath(String sourcePath) {
        String normalized = StringUtils.defaultString(sourcePath).trim().replace('\\', '/');
        if (StringUtils.isBlank(normalized)) {
            return false;
        }
        if (containsAny(normalized, "源码定位", "安全提示", "回答", "解释", "摘要", "提示", "伪代码")) {
            return false;
        }
        if (normalized.contains(" ") || normalized.contains("。") || normalized.contains("，") || normalized.contains("；")) {
            return false;
        }
        if (normalized.contains("/")) {
            return true;
        }
        return normalized.matches("(?i).+\\.(java|kt|kts|xml|sql|vue|js|ts|tsx|jsx|yml|yaml|md|json|properties|groovy)$")
                || normalized.matches("(?i)(Dockerfile|Makefile|Jenkinsfile)");
    }

    private record CodebaseLocationRow(String sourcePath, String className, String methodName, String summary) {
    }

    private enum CodebaseSafetyFocus {
        RAW_SOURCE,
        SQL_XML,
        CONFIG,
        COMPLETE_IMPLEMENTATION,
        STREAMING
    }

    private String normalizeCodebasePromptLine(String line) {
        return StringUtils.defaultString(line)
                .replace('\\', '/')
                .replaceAll("\\s+", "")
                .toLowerCase(Locale.ROOT);
    }

    private boolean isCodebaseLocationLine(String normalizedLine) {
        return StringUtils.isNotBlank(normalizedLine) && normalizedLine.contains("源码定位：路径=");
    }

    private String resolvePlatformAssistantSkill(AiPlatformDocKnowledgeContextVO context) {
        return isPlatformAssistantKnowledgeMatched(context) ? SKILL_PLATFORM_ASSISTANT : SKILL_PLATFORM_ASSISTANT_FALLBACK;
    }

    private String resolvePlatformAssistantPromptMode(AiPlatformDocKnowledgeContextVO context) {
        return isPlatformAssistantKnowledgeMatched(context)
                ? AiChatMode.PLATFORM_ASSISTANT.name()
                : AiChatMode.GENERAL.name();
    }

    private boolean isPlatformAssistantKnowledgeMatched(AiPlatformDocKnowledgeContextVO context) {
        return context != null
                && Boolean.TRUE.equals(context.getMatched())
                && context.getPromptLines() != null
                && !context.getPromptLines().isEmpty();
    }

    private String buildPlatformAssistantInstruction(AiPlatformDocKnowledgeContextVO context) {
        if (!isPlatformAssistantKnowledgeMatched(context)) {
            return AiPromptConstant.PLATFORM_ASSISTANT_FALLBACK_INSTRUCTION;
        }
        StringBuilder instruction = new StringBuilder(AiPromptConstant.PLATFORM_ASSISTANT_MATCHED_INSTRUCTION);
        instruction.append('\n').append(AiPromptConstant.PLATFORM_ASSISTANT_KNOWLEDGE_HEADER);
        for (String line : context.getPromptLines()) {
            if (StringUtils.isNotBlank(line)) {
                instruction.append('\n').append(line);
            }
        }
        return instruction.toString();
    }

    private String trimProtocolKnowledgeError(String message) {
        if (StringUtils.isBlank(message)) {
            return message("ai.chat.common.error.unknown");
        }
        String actualMessage = message.trim();
        return actualMessage.length() > 180 ? actualMessage.substring(0, 180) : actualMessage;
    }

    private String resolveRoleLabel(String roleType) {
        return "assistant".equalsIgnoreCase(roleType) ? message("ai.chat.common.role.assistant") : message("ai.chat.common.role.user");
    }

    private String resolveRouteMode(boolean manualRoute) {
        return manualRoute ? ROUTE_MODE_MANUAL : ROUTE_MODE_DEFAULT;
    }

    private DataCenterParam extractDataCenterParam(String question) {
        DataCenterParam param = new DataCenterParam();
        param.setSerialNumber(extractStringValue(question, "serialNumber", "serial", "设备编号"));
        param.setBeginTime(extractStringValue(question, "beginTime", "开始时间", "start"));
        param.setEndTime(extractStringValue(question, "endTime", "结束时间", "end"));
        return param;
    }

    private String extractStringValue(String question, String... keys) {
        if (StringUtils.isBlank(question) || keys == null) {
            return null;
        }
        for (String key : keys) {
            Matcher matcher = Pattern.compile(Pattern.quote(key) + "\\s*[=:：]\\s*([^\\s,，；;]+)").matcher(question);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }

    private Long extractLongValue(String question, String... keys) {
        String value = extractStringValue(question, keys);
        if (StringUtils.isNotBlank(value) && StringUtils.isNumeric(value)) {
            return Long.parseLong(value);
        }
        Matcher matcher = Pattern.compile("(\\d+)").matcher(question);
        return matcher.find() ? Long.parseLong(matcher.group(1)) : null;
    }

    private String extractJsonBody(String question) {
        if (StringUtils.isBlank(question)) {
            return null;
        }
        int startIndex = question.indexOf('{');
        int endIndex = question.lastIndexOf('}');
        return startIndex < 0 || endIndex <= startIndex ? null : question.substring(startIndex, endIndex + 1);
    }

    private String extractSqlStatement(String question) {
        if (StringUtils.isBlank(question)) {
            return null;
        }
        Matcher codeBlockMatcher = SQL_CODE_BLOCK_PATTERN.matcher(question);
        if (codeBlockMatcher.find()) {
            return codeBlockMatcher.group(1).trim();
        }
        Matcher inlineMatcher = INLINE_SQL_PATTERN.matcher(question.trim());
        return inlineMatcher.find() ? inlineMatcher.group(1).trim() : null;
    }

    private boolean containsAny(String content, String... keywords) {
        if (StringUtils.isBlank(content) || keywords == null) {
            return false;
        }
        for (String keyword : keywords) {
            if (StringUtils.isNotBlank(keyword) && content.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsAny(String content, List<String> keywords) {
        if (StringUtils.isBlank(content) || keywords == null || keywords.isEmpty()) {
            return false;
        }
        for (String keyword : keywords) {
            if (StringUtils.isNotBlank(keyword) && content.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String formatAlertCountResult(String title, List<AlertCountVO> result) {
        if (result == null || result.isEmpty()) {
            return message("ai.chat.nl2sql.alert.no.data", title);
        }
        int total = 0;
        for (AlertCountVO item : result) {
            total += item.getCount() == null ? 0 : item.getCount();
        }
        StringBuilder builder = new StringBuilder(message("ai.chat.nl2sql.alert.completed", title, total));
        for (AlertCountVO item : result) {
            builder.append('\n').append(message("ai.chat.nl2sql.alert.type.count", item.getType(), item.getCount() == null ? 0 : item.getCount()));
        }
        return builder.toString();
    }

    private String formatThingsModelInvokeResult(String serialNumber, List<ThingsModelLogCountVO> result) {
        if (result == null || result.isEmpty()) {
            return message("ai.chat.nl2sql.thing.model.invoke.no.data", serialNumber);
        }
        StringBuilder builder = new StringBuilder(message("ai.chat.nl2sql.thing.model.invoke.completed", serialNumber));
        for (ThingsModelLogCountVO item : result) {
            builder.append("\n- ").append(StringUtils.isNotBlank(item.getModelName()) ? item.getModelName() : item.getIdentifier()).append("（").append(item.getIdentifier()).append("）：").append(item.getCounts() == null ? 0 : item.getCounts());
        }
        return builder.toString();
    }

    private String formatNl2SqlPreviewResult(AiNl2SqlQueryResultVO result) {
        StringBuilder builder = new StringBuilder(message("ai.chat.nl2sql.preview.completed"))
                .append('\n').append(message("ai.chat.common.label.row.count", result.getRowCount() == null ? 0 : result.getRowCount()))
                .append('\n').append(message("ai.chat.common.label.max.rows", result.getMaxRows()))
                .append('\n').append(message("ai.chat.nl2sql.label.data.scope", yesNo(result.getDataScopeApplied())));
        appendFriendlyQueryResult(builder, null, result);
        if (result.getColumns() != null && !result.getColumns().isEmpty()) {
            builder.append('\n').append(message("ai.chat.common.label.result.columns", String.join("、", result.getColumns())));
        }
        appendPreviewRows(builder, result.getRows());
        return builder.toString();
    }

    private String formatNl2SqlGenerateResult(AiNl2SqlGenerateResultVO result) {
        StringBuilder builder = new StringBuilder(message("ai.chat.nl2sql.generate.completed"));
        if (StringUtils.isNotBlank(result.getSummary())) {
            builder.append('\n').append(message("ai.chat.common.label.result.summary", result.getSummary()));
        }
        if (result.getQueryResult() != null) {
            appendFriendlyQueryResult(builder, result.getSummary(), result.getQueryResult());
        }
        builder.append('\n').append(message("ai.chat.nl2sql.label.generated.sql", result.getGeneratedSql()));
        if (result.getQueryResult() != null) {
            appendExecutedSqlIfChanged(builder, result);
            builder.append('\n').append(message("ai.chat.common.label.row.count", result.getQueryResult().getRowCount()));
            if (result.getQueryResult().getColumns() != null && !result.getQueryResult().getColumns().isEmpty()) {
                builder.append('\n').append(message("ai.chat.common.label.result.columns", String.join("、", result.getQueryResult().getColumns())));
            }
            appendPreviewRows(builder, result.getQueryResult().getRows());
        }
        return builder.toString();
    }

    private void appendExecutedSqlIfChanged(StringBuilder builder, AiNl2SqlGenerateResultVO result) {
        if (builder == null || result == null || result.getQueryResult() == null
                || StringUtils.isBlank(result.getQueryResult().getExecutedSql())) {
            return;
        }
        String generatedSql = normalizeSqlForDisplayCompare(result.getGeneratedSql());
        String executedSql = normalizeSqlForDisplayCompare(result.getQueryResult().getExecutedSql());
        if (StringUtils.isBlank(executedSql) || executedSql.equalsIgnoreCase(generatedSql)) {
            return;
        }
        builder.append('\n').append(message("ai.chat.nl2sql.label.executed.sql", result.getQueryResult().getExecutedSql()));
    }

    private String normalizeSqlForDisplayCompare(String sql) {
        return StringUtils.isBlank(sql) ? "" : sql.replaceAll("\\s+", " ").trim();
    }

    private String formatUnifiedNl2SqlResult(AiNl2SqlGenerateResultVO result) {
        if (result.getRealtimeResult() != null) {
            return formatRealtimeNl2SqlResult(result);
        }
        if (result.getTsdbResult() != null) {
            return formatTsdbNl2SqlResult(result);
        }
        if (result.getHybridResult() != null) {
            return formatHybridNl2SqlResult(result);
        }
        return formatNl2SqlGenerateResult(result);
    }

    private String formatRealtimeNl2SqlResult(AiNl2SqlGenerateResultVO result) {
        AiRedisRealtimeQueryResultVO realtimeResult = result.getRealtimeResult();
        StringBuilder builder = new StringBuilder(message("ai.chat.nl2sql.realtime.completed"));
        appendQueryPlanSummary(builder, result.getQueryPlan());
        if (StringUtils.isNotBlank(realtimeResult.getSummary())) {
            builder.append('\n').append(message("ai.chat.common.label.result.summary", realtimeResult.getSummary()));
        }
        builder.append('\n').append(message("ai.chat.common.label.device.serial", defaultText(realtimeResult.getSerialNumber())));
        builder.append('\n').append(message("ai.chat.common.label.device.name", defaultText(realtimeResult.getDeviceName())));
        builder.append('\n').append(message("ai.chat.common.label.metric.name", defaultText(realtimeResult.getSemanticName())));
        builder.append('\n').append(message("ai.chat.common.label.identifier", defaultText(realtimeResult.getIdentifier())));
        builder.append('\n').append(message("ai.chat.common.label.current.value", defaultText(realtimeResult.getCurrentValue())));
        if (StringUtils.isNotBlank(realtimeResult.getUnit())) {
            builder.append(' ').append(realtimeResult.getUnit());
        }
        if (StringUtils.isNotBlank(realtimeResult.getReportTime())) {
            builder.append('\n').append(message("ai.chat.common.label.report.time", realtimeResult.getReportTime()));
        }
        builder.append('\n').append(message("ai.chat.nl2sql.label.redis.hit", yesNo(realtimeResult.getCacheHit())));
        return builder.toString();
    }

    private String formatTsdbNl2SqlResult(AiNl2SqlGenerateResultVO result) {
        AiTsdbQueryResultVO tsdbResult = result.getTsdbResult();
        StringBuilder builder = new StringBuilder(message("ai.chat.nl2sql.tsdb.completed"));
        appendQueryPlanSummary(builder, result.getQueryPlan());
        if (StringUtils.isNotBlank(tsdbResult.getSummary())) {
            builder.append('\n').append(message("ai.chat.common.label.result.summary", tsdbResult.getSummary()));
        }
        builder.append('\n').append(message("ai.chat.common.label.query.type", defaultText(tsdbResult.getQueryType())));
        builder.append('\n').append(message("ai.chat.common.label.device.serial", defaultText(tsdbResult.getSerialNumber())));
        builder.append('\n').append(message("ai.chat.common.label.device.name", defaultText(tsdbResult.getDeviceName())));
        builder.append('\n').append(message("ai.chat.common.label.metric.name", defaultText(tsdbResult.getSemanticName())));
        builder.append('\n').append(message("ai.chat.common.label.identifier", defaultText(tsdbResult.getIdentifier())));
        if (StringUtils.isNotBlank(tsdbResult.getTimeWindowLabel())) {
            builder.append('\n').append(message("ai.chat.common.label.time.window", tsdbResult.getTimeWindowLabel()));
        }
        if (StringUtils.isNotBlank(tsdbResult.getLatestValue())) {
            builder.append('\n').append(message("ai.chat.common.label.latest.value", tsdbResult.getLatestValue()));
            if (StringUtils.isNotBlank(tsdbResult.getUnit())) {
                builder.append(' ').append(tsdbResult.getUnit());
            }
        }
        if (StringUtils.isNotBlank(tsdbResult.getLatestTime())) {
            builder.append('\n').append(message("ai.chat.common.label.latest.report.time", tsdbResult.getLatestTime()));
        }
        if (StringUtils.isNotBlank(tsdbResult.getStatisticOperation())) {
            builder.append('\n').append(message("ai.chat.common.label.statistic.operation", tsdbResult.getStatisticOperation()));
        }
        if (StringUtils.isNotBlank(tsdbResult.getStatisticValue())) {
            builder.append('\n').append(message("ai.chat.common.label.statistic.value", tsdbResult.getStatisticValue()));
            if (StringUtils.isNotBlank(tsdbResult.getUnit())) {
                builder.append(' ').append(tsdbResult.getUnit());
            }
        }
        if (tsdbResult.getStatisticSamples() != null && !tsdbResult.getStatisticSamples().isEmpty()) {
            builder.append('\n').append(message("ai.chat.common.label.statistic.sample.count", tsdbResult.getStatisticSamples().size()));
            builder.append('\n').append(message("ai.chat.common.label.statistic.sample.preview", formatStatisticSamplePreview(tsdbResult.getStatisticSamples(), tsdbResult.getUnit())));
        }
        if (tsdbResult.getHistoryPoints() != null && !tsdbResult.getHistoryPoints().isEmpty()) {
            builder.append('\n').append(message("ai.chat.common.label.history.point.count", tsdbResult.getHistoryPoints().size()));
            builder.append('\n').append(message("ai.chat.common.label.history.point.preview", formatHistoryPointPreview(tsdbResult.getHistoryPoints(), tsdbResult.getUnit())));
        }
        return builder.toString();
    }

    private String formatHybridNl2SqlResult(AiNl2SqlGenerateResultVO result) {
        AiHybridQueryResultVO hybridResult = result.getHybridResult();
        StringBuilder builder = new StringBuilder(message("ai.chat.nl2sql.hybrid.completed"));
        appendQueryPlanSummary(builder, result.getQueryPlan());
        if (StringUtils.isNotBlank(hybridResult.getSummary())) {
            builder.append('\n').append(message("ai.chat.common.label.result.summary", hybridResult.getSummary()));
        }
        builder.append('\n').append(message("ai.chat.nl2sql.label.plan.mode", defaultText(hybridResult.getQueryMode())));
        builder.append('\n').append(message("ai.chat.nl2sql.label.final.source", defaultText(hybridResult.getFinalQueryMode())));
        builder.append('\n').append(message("ai.chat.common.label.query.type", defaultText(hybridResult.getQueryType())));
        builder.append('\n').append(message("ai.chat.common.label.device.serial", defaultText(hybridResult.getSerialNumber())));
        builder.append('\n').append(message("ai.chat.common.label.device.name", defaultText(hybridResult.getDeviceName())));
        builder.append('\n').append(message("ai.chat.common.label.metric.name", defaultText(hybridResult.getSemanticName())));
        builder.append('\n').append(message("ai.chat.common.label.identifier", defaultText(hybridResult.getIdentifier())));
        if (StringUtils.isNotBlank(hybridResult.getTimeWindowLabel())) {
            builder.append('\n').append(message("ai.chat.common.label.time.window", hybridResult.getTimeWindowLabel()));
        }
        if (StringUtils.isNotBlank(hybridResult.getCurrentValue())) {
            builder.append('\n').append(message("ai.chat.common.label.current.value", hybridResult.getCurrentValue()));
            if (StringUtils.isNotBlank(hybridResult.getUnit())) {
                builder.append(' ').append(hybridResult.getUnit());
            }
        }
        if (StringUtils.isNotBlank(hybridResult.getCurrentTime())) {
            builder.append('\n').append(message("ai.chat.common.label.current.time", hybridResult.getCurrentTime()));
        }
        if (StringUtils.isNotBlank(hybridResult.getStatisticOperation())) {
            builder.append('\n').append(message("ai.chat.common.label.statistic.operation", hybridResult.getStatisticOperation()));
        }
        if (StringUtils.isNotBlank(hybridResult.getStatisticValue())) {
            builder.append('\n').append(message("ai.chat.common.label.statistic.value", hybridResult.getStatisticValue()));
            if (StringUtils.isNotBlank(hybridResult.getUnit())) {
                builder.append(' ').append(hybridResult.getUnit());
            }
        }
        if (hybridResult.getStatisticSamples() != null && !hybridResult.getStatisticSamples().isEmpty()) {
            builder.append('\n').append(message("ai.chat.common.label.statistic.sample.count", hybridResult.getStatisticSamples().size()));
            builder.append('\n').append(message("ai.chat.common.label.statistic.sample.preview", formatStatisticSamplePreview(hybridResult.getStatisticSamples(), hybridResult.getUnit())));
        }
        if (hybridResult.getHistoryPoints() != null && !hybridResult.getHistoryPoints().isEmpty()) {
            builder.append('\n').append(message("ai.chat.common.label.history.point.count", hybridResult.getHistoryPoints().size()));
            builder.append('\n').append(message("ai.chat.common.label.history.point.preview", formatHistoryPointPreview(hybridResult.getHistoryPoints(), hybridResult.getUnit())));
        }
        builder.append('\n').append(message("ai.chat.nl2sql.label.fallback.used", yesNo(hybridResult.getFallbackUsed())));
        if (StringUtils.isNotBlank(hybridResult.getRouteReason())) {
            builder.append('\n').append(message("ai.chat.nl2sql.label.route.reason", hybridResult.getRouteReason()));
        }
        return builder.toString();
    }

    private void appendQueryPlanSummary(StringBuilder builder, AiQueryPlanVO queryPlan) {
        if (queryPlan == null) {
            return;
        }
        if (StringUtils.isNotBlank(queryPlan.getQueryMode())) {
            builder.append('\n').append(message("ai.chat.nl2sql.label.execution.mode", queryPlan.getQueryMode()));
        }
        if (StringUtils.isNotBlank(queryPlan.getPrimarySemantic())) {
            builder.append('\n').append(message("ai.chat.nl2sql.label.primary.semantic", queryPlan.getPrimarySemantic()));
        }
        if (StringUtils.isNotBlank(queryPlan.getRuntimeSource())) {
            builder.append('\n').append(message("ai.chat.nl2sql.label.runtime.source", queryPlan.getRuntimeSource()));
        }
    }

    private String formatStatisticSamplePreview(List<String> statisticSamples, String unit) {
        if (statisticSamples == null || statisticSamples.isEmpty()) {
            return "-";
        }
        List<String> previews = new ArrayList<>();
        int limit = Math.min(5, statisticSamples.size());
        for (int i = 0; i < limit; i++) {
            String sample = statisticSamples.get(i);
            StringBuilder preview = new StringBuilder(defaultText(sample));
            if (StringUtils.isNotBlank(unit)) {
                preview.append(unit);
            }
            previews.add(preview.toString());
        }
        if (statisticSamples.size() > limit) {
            previews.add("...");
        }
        return String.join("；", previews);
    }

    private String formatHistoryPointPreview(List<AiTsdbHistoryPointVO> historyPoints, String unit) {
        if (historyPoints == null || historyPoints.isEmpty()) {
            return "-";
        }
        List<AiTsdbHistoryPointVO> sortedPoints = new ArrayList<>(historyPoints);
        sortedPoints.sort(Comparator.comparing(AiTsdbHistoryPointVO::getTime, Comparator.nullsLast(String::compareTo)));
        List<String> previews = new ArrayList<>();
        int limit = Math.min(5, sortedPoints.size());
        for (int i = 0; i < limit; i++) {
            AiTsdbHistoryPointVO point = sortedPoints.get(i);
            StringBuilder preview = new StringBuilder();
            preview.append(defaultText(point == null ? null : point.getTime()));
            preview.append('=').append(defaultText(point == null ? null : point.getValue()));
            if (StringUtils.isNotBlank(unit)) {
                preview.append(unit);
            }
            previews.add(preview.toString());
        }
        if (sortedPoints.size() > limit) {
            previews.add("...");
        }
        return String.join("；", previews);
    }

    private String defaultText(Object value) {
        return value == null ? "-" : String.valueOf(value);
    }

    private String resolveUploadedProtocolFilename(MultipartFile file) {
        String originalFilename = file == null ? "" : file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            return message("ai.chat.protocol.file.fallback");
        }
        String normalized = originalFilename.trim().replace('\\', '/');
        int lastSlashIndex = normalized.lastIndexOf('/');
        return lastSlashIndex >= 0 ? normalized.substring(lastSlashIndex + 1) : normalized;
    }

    private String buildDefaultProtocolUploadQuestion(String originalFilename) {
        return message("ai.chat.protocol.default.question",
                defaultText(originalFilename, message("ai.chat.protocol.file.fallback")));
    }

    private String buildDefaultThingModelUploadQuestion(String originalFilename) {
        return message("ai.chat.thing.model.generate.default.question",
                defaultText(originalFilename, message("ai.chat.thing.model.generate.file.fallback")));
    }

    private String buildDefaultRequirementUploadQuestion(String originalFilename) {
        return message("ai.chat.requirement.evaluation.default.question",
                defaultText(originalFilename, message("ai.chat.requirement.evaluation.file.fallback")));
    }

    private String buildThingModelUploadUserMessage(String question, String originalFilename) {
        StringBuilder builder = new StringBuilder(defaultText(question, buildDefaultThingModelUploadQuestion(originalFilename)));
        builder.append(AiPromptConstant.THING_MODEL_UPLOAD_FILE_CONTEXT_TEMPLATE.formatted(
                defaultText(originalFilename, message("ai.chat.thing.model.generate.file.fallback"))
        ));
        return builder.toString();
    }

    private String buildRequirementUploadUserMessage(String question, String originalFilename) {
        StringBuilder builder = new StringBuilder(defaultText(question, buildDefaultRequirementUploadQuestion(originalFilename)));
        builder.append(AiPromptConstant.REQUIREMENT_UPLOAD_FILE_CONTEXT_TEMPLATE.formatted(
                defaultText(originalFilename, message("ai.chat.requirement.evaluation.file.fallback"))
        ));
        return builder.toString();
    }

    private String buildRequirementEvaluationAnswer(String originalFilename, AiRequirementEvaluationResultVO result) {
        StringBuilder builder = new StringBuilder();
        builder.append(message("ai.chat.requirement.evaluation.answer.completed",
                defaultText(originalFilename, message("ai.chat.requirement.evaluation.file.fallback"))));
        if (result == null) {
            builder.append('\n').append(message("ai.chat.requirement.evaluation.answer.empty"));
            builder.append("\n\n").append(message("ai.chat.requirement.evaluation.disclaimer"));
            return builder.toString();
        }
        if (StringUtils.isNotBlank(result.getOverallConclusion())) {
            builder.append('\n').append(message("ai.chat.requirement.evaluation.label.overall.conclusion", result.getOverallConclusion()));
        } else if (StringUtils.isNotBlank(result.getSummary())) {
            builder.append('\n').append(message("ai.chat.requirement.evaluation.label.overall.conclusion", result.getSummary()));
        }
        if (StringUtils.isNotBlank(result.getMatchLevel())) {
            builder.append('\n').append(message("ai.chat.requirement.evaluation.label.match.level", resolveRequirementMatchLevelLabel(result.getMatchLevel())));
        }
        if (result.getRequirementItems() != null && !result.getRequirementItems().isEmpty()) {
            builder.append("\n\n").append(message("ai.chat.requirement.evaluation.label.items.match"));
            builder.append('\n').append(message("ai.chat.requirement.evaluation.table.header"));
            builder.append("\n| --- | --- | --- | --- |");
            int limit = Math.min(result.getRequirementItems().size(), 8);
            for (int index = 0; index < limit; index++) {
                LinkedHashMap<String, Object> item = result.getRequirementItems().get(index);
                builder.append("\n| ")
                        .append(escapeMarkdownTableCell(item.get("需求点")))
                        .append(" | ")
                        .append(escapeMarkdownTableCell(item.get("匹配结论")))
                        .append(" | ")
                        .append(escapeMarkdownTableCell(item.get("建议动作")))
                        .append(" | ")
                        .append(escapeMarkdownTableCell(item.get("复杂度")))
                        .append(" |");
            }
            if (result.getRequirementItems().size() > limit) {
                builder.append("\n\n").append(message("ai.chat.requirement.evaluation.answer.remaining.items", result.getRequirementItems().size() - limit));
            }
        }
        appendRequirementEvaluationList(builder, message("ai.chat.requirement.evaluation.label.risks"), result.getRisks());
        appendRequirementEvaluationList(builder, message("ai.chat.requirement.evaluation.label.pending.questions"), result.getPendingQuestions());
        appendRequirementEvaluationList(builder, message("ai.chat.requirement.evaluation.label.next.steps"), result.getNextSteps());
        if (StringUtils.isNotBlank(result.getArtifactCode())) {
            builder.append("\n\n").append(message("ai.chat.requirement.evaluation.label.artifact.file",
                    defaultText(result.getArtifactName(), message("ai.chat.requirement.evaluation.artifact.fallback"))));
            builder.append('\n').append(message("ai.chat.requirement.evaluation.label.download.link",
                    buildRequirementEvaluationReportDownloadLink(result)));
            builder.append("\n**").append(message("ai.chat.requirement.evaluation.answer.download.note")).append("**");
        }
        builder.append("\n\n**")
                .append(message("ai.chat.requirement.evaluation.disclaimer"))
                .append("**");
        return AiCodebaseAnswerSanitizer.sanitize(builder.toString(), true).getContent();
    }

    private void appendRequirementEvaluationList(StringBuilder builder, String title, List<String> list) {
        if (builder == null || list == null || list.isEmpty()) {
            return;
        }
        builder.append("\n\n").append(message("ai.chat.common.section.title", title));
        for (String item : list) {
            if (StringUtils.isNotBlank(item)) {
                builder.append("\n- ").append(item);
            }
        }
    }

    private String resolveRequirementMatchLevelLabel(String matchLevel) {
        if ("HIGH".equalsIgnoreCase(matchLevel)) {
            return message("ai.chat.requirement.evaluation.match.level.high");
        }
        if ("MEDIUM".equalsIgnoreCase(matchLevel)) {
            return message("ai.chat.requirement.evaluation.match.level.medium");
        }
        if ("LOW".equalsIgnoreCase(matchLevel)) {
            return message("ai.chat.requirement.evaluation.match.level.low");
        }
        return message("ai.chat.requirement.evaluation.match.level.unknown");
    }

    private String escapeMarkdownTableCell(Object value) {
        String text = defaultText(value).replace("|", "\\|").replace("\n", " ");
        return StringUtils.isBlank(text) ? "-" : text;
    }

    private String buildThingModelGenerateAnswer(String originalFilename, AiThingModelGenerateResultVO result) {
        StringBuilder builder = new StringBuilder();
        builder.append(message("ai.chat.thing.model.generate.answer.parsed",
                defaultText(originalFilename, message("ai.chat.thing.model.generate.file.fallback"))));
        if (result == null) {
            builder.append(message("ai.chat.thing.model.generate.answer.empty"));
            return builder.toString();
        }
        builder.append(message("ai.chat.thing.model.generate.answer.success"));
        if (StringUtils.isNotBlank(result.getSummary())) {
            builder.append("\n").append(result.getSummary());
        }
        if (result.getRowCount() != null) {
            builder.append('\n').append(message("ai.chat.thing.model.generate.label.row.count", result.getRowCount()));
        }
        if (StringUtils.isNotBlank(result.getArtifactName())) {
            builder.append('\n').append(message("ai.chat.thing.model.generate.label.artifact.file", result.getArtifactName()));
        }
        if (StringUtils.isNotBlank(result.getArtifactCode())) {
            builder.append('\n').append(message("ai.chat.thing.model.generate.label.download.code", result.getArtifactCode()));
            builder.append('\n').append(message("ai.chat.thing.model.generate.label.download.link", buildThingModelWorkbookDownloadLink(result)));
        }
        if (result.getQualityIssues() != null && !result.getQualityIssues().isEmpty()) {
            builder.append('\n').append(message("ai.chat.thing.model.generate.label.quality.review"));
            for (String issue : result.getQualityIssues()) {
                if (StringUtils.isNotBlank(issue)) {
                    builder.append("\n- ").append(issue);
                }
            }
        }
        return builder.toString();
    }

    private String buildThingModelWorkbookDownloadLink(AiThingModelGenerateResultVO result) {
        StringBuilder link = new StringBuilder("fastbee://thing-model-workbook?artifactCode=")
                .append(urlEncode(defaultText(result == null ? null : result.getArtifactCode(), "")));
        if (result != null && StringUtils.isNotBlank(result.getArtifactName())) {
            link.append("&filename=").append(urlEncode(result.getArtifactName()));
        }
        return link.toString();
    }

    private String buildRequirementEvaluationReportDownloadLink(AiRequirementEvaluationResultVO result) {
        StringBuilder link = new StringBuilder("fastbee://requirement-evaluation-report?artifactCode=")
                .append(urlEncode(defaultText(result == null ? null : result.getArtifactCode(), "")));
        if (result != null && StringUtils.isNotBlank(result.getArtifactName())) {
            link.append("&filename=").append(urlEncode(result.getArtifactName()));
        }
        return link.toString();
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(defaultText(value, ""), StandardCharsets.UTF_8);
    }

    private String buildProtocolUploadUserMessage(String question, String originalFilename) {
        StringBuilder builder = new StringBuilder(defaultText(question, buildDefaultProtocolUploadQuestion(originalFilename)));
        builder.append(AiPromptConstant.PROTOCOL_UPLOAD_FILE_CONTEXT_TEMPLATE.formatted(
                defaultText(originalFilename, AiPromptConstant.PROTOCOL_FILE_FALLBACK_NAME)
        ));
        return builder.toString();
    }

    private String buildChatProtocolTaskName(String originalFilename) {
        String normalized = defaultText(originalFilename, message("ai.chat.protocol.file.fallback")).replace('\\', '/');
        int lastSlashIndex = normalized.lastIndexOf('/');
        if (lastSlashIndex >= 0) {
            normalized = normalized.substring(lastSlashIndex + 1);
        }
        normalized = normalized.replaceFirst("\\.[^.]+$", "").replaceAll("\\s+", " ").trim();
        if (StringUtils.isBlank(normalized)) {
            normalized = message("ai.chat.protocol.file.fallback");
        }
        String taskName = message("ai.chat.protocol.task.name.prefix") + normalized;
        return taskName.length() <= 80 ? taskName : taskName.substring(0, 80);
    }

    private String buildProtocolUploadAnswer(String originalFilename, AiProtocolAdaptationAutoRunResultVO autoRunResult) {
        StringBuilder builder = new StringBuilder();
        String actualFilename = defaultText(originalFilename, message("ai.chat.protocol.file.fallback"));
        if (autoRunResult == null) {
            builder.append(message("ai.chat.protocol.upload.received.no.result", actualFilename));
            return builder.toString();
        }
        builder.append(message("ai.chat.protocol.upload.received.with.status",
                actualFilename, resolveAutoRunStatusLabel(autoRunResult.getRunStatus())));
        if (StringUtils.isNotBlank(autoRunResult.getSummary())) {
            builder.append("\n").append(autoRunResult.getSummary());
        }
        if (autoRunResult.getTaskId() != null) {
            builder.append('\n').append(message("ai.chat.protocol.label.task.id", autoRunResult.getTaskId()));
        }
        if (StringUtils.isNotBlank(autoRunResult.getCurrentStage())) {
            builder.append('\n').append(message("ai.chat.protocol.label.current.stage",
                    resolveAutoRunStageLabel(autoRunResult.getCurrentStage())));
        }
        if (autoRunResult.getGenerationRecord() != null && autoRunResult.getGenerationRecord().getRecordId() != null) {
            builder.append('\n').append(message("ai.chat.protocol.label.generation.record.id",
                    autoRunResult.getGenerationRecord().getRecordId()));
        }
        if (autoRunResult.getNextActions() != null && !autoRunResult.getNextActions().isEmpty()) {
            builder.append('\n').append(message("ai.chat.protocol.label.next.step"));
            for (String action : autoRunResult.getNextActions()) {
                if (StringUtils.isNotBlank(action)) {
                    builder.append("\n- ").append(action);
                }
            }
        }
        return builder.toString();
    }

    private void emitProtocolUploadAnswerChunks(FluxSink<AiChatStreamEventVO> sink,
                                                AiChatSession session,
                                                AiModelRouteVO route,
                                                String requestedMode,
                                                String answer) {
        if (sink == null || sink.isCancelled() || StringUtils.isBlank(answer)) {
            return;
        }
        StringBuilder content = new StringBuilder();
        for (String chunk : splitStreamChunks(answer, 36)) {
            if (sink.isCancelled()) {
                return;
            }
            content.append(chunk);
            sink.next(buildStreamEvent("chunk", session, route, requestedMode, requestedMode,
                    SKILL_PROTOCOL_PARSE, content.toString(), chunk, null, null, false, null, null));
            sleepStreamChunkInterval();
        }
    }

    private void emitAnswerChunks(FluxSink<AiChatStreamEventVO> sink,
                                  AiChatSession session,
                                  AiModelRouteVO route,
                                  String requestedMode,
                                  String effectiveMode,
                                  String skillName,
                                  String answer) {
        if (sink == null || sink.isCancelled() || StringUtils.isBlank(answer)) {
            return;
        }
        StringBuilder content = new StringBuilder();
        for (String chunk : splitStreamChunks(answer, 36)) {
            if (sink.isCancelled()) {
                return;
            }
            content.append(chunk);
            sink.next(buildStreamEvent("chunk", session, route, requestedMode, effectiveMode,
                    skillName, content.toString(), chunk, null, null, false, null, null));
            sleepStreamChunkInterval();
        }
    }

    private List<String> splitStreamChunks(String text, int maxChars) {
        List<String> chunks = new ArrayList<>();
        if (StringUtils.isBlank(text)) {
            return chunks;
        }
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            current.append(ch);
            if (ch == '\n' || current.length() >= maxChars) {
                chunks.add(current.toString());
                current.setLength(0);
            }
        }
        if (current.length() > 0) {
            chunks.add(current.toString());
        }
        return chunks;
    }

    private void sleepStreamChunkInterval() {
        try {
            Thread.sleep(35L);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private String buildProtocolUploadToolResult(AiProtocolAdaptationAutoRunResultVO autoRunResult,
                                                 AiProtocolAdaptationArtifactVO sourceArtifact,
                                                 String answer) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        String runStatus = autoRunResult == null ? "FAILED" : defaultText(autoRunResult.getRunStatus(), "FAILED");
        result.put("summary", autoRunResult == null
                ? message("ai.chat.protocol.orchestration.no.result")
                : defaultText(autoRunResult.getSummary(), message("ai.chat.protocol.orchestration.completed")));
        result.put("keyPoints", buildProtocolUploadKeyPoints(autoRunResult, sourceArtifact));
        result.put("missingInformation", buildProtocolUploadMissingInformation(autoRunResult));
        result.put("status", "COMPLETED".equals(runStatus) ? "ANALYZED" : "NEED_MORE_INFO");
        result.put("rawAnswer", answer);
        result.put("taskId", autoRunResult == null ? null : autoRunResult.getTaskId());
        result.put("runStatus", runStatus);
        result.put("runStatusLabel", resolveAutoRunStatusLabel(runStatus));
        result.put("currentStage", autoRunResult == null ? null : autoRunResult.getCurrentStage());
        result.put("currentStageLabel", autoRunResult == null ? "" : resolveAutoRunStageLabel(autoRunResult.getCurrentStage()));
        if (autoRunResult != null && autoRunResult.getTask() != null) {
            result.put("taskStatus", autoRunResult.getTask().getTaskStatus());
            result.put("parseStatus", autoRunResult.getTask().getParseStatus());
            result.put("validationStatus", autoRunResult.getTask().getValidationStatus());
            result.put("generationStatus", autoRunResult.getTask().getGenerationStatus());
        }
        if (autoRunResult != null && autoRunResult.getGenerationRecord() != null) {
            AiProtocolGenerationRecordVO record = autoRunResult.getGenerationRecord();
            result.put("generationRecordId", record.getRecordId());
            result.put("compileStatus", record.getCompileStatus());
            result.put("compileStatusLabel", resolveCompileStatusLabel(record.getCompileStatus()));
            result.put("validationErrorCount", record.getValidationErrorCount());
            result.put("validationWarningCount", record.getValidationWarningCount());
        }
        return JSON.toJSONString(result);
    }

    private List<String> buildProtocolUploadKeyPoints(AiProtocolAdaptationAutoRunResultVO autoRunResult,
                                                      AiProtocolAdaptationArtifactVO sourceArtifact) {
        List<String> keyPoints = new ArrayList<>();
        if (sourceArtifact != null && StringUtils.isNotBlank(sourceArtifact.getArtifactName())) {
            keyPoints.add(message("ai.chat.protocol.label.source.file", sourceArtifact.getArtifactName()));
        }
        if (autoRunResult == null) {
            return keyPoints;
        }
        if (autoRunResult.getTaskId() != null) {
            keyPoints.add(message("ai.chat.protocol.label.task.id", autoRunResult.getTaskId()));
        }
        if (StringUtils.isNotBlank(autoRunResult.getRunStatus())) {
            keyPoints.add(message("ai.chat.protocol.label.run.status", resolveAutoRunStatusLabel(autoRunResult.getRunStatus())));
        }
        if (StringUtils.isNotBlank(autoRunResult.getCurrentStage())) {
            keyPoints.add(message("ai.chat.protocol.label.current.stage", resolveAutoRunStageLabel(autoRunResult.getCurrentStage())));
        }
        if (autoRunResult.getGenerationRecord() != null && autoRunResult.getGenerationRecord().getRecordId() != null) {
            keyPoints.add(message("ai.chat.protocol.label.generation.record.id", autoRunResult.getGenerationRecord().getRecordId()));
        }
        if (autoRunResult.getGenerationRecord() != null
                && StringUtils.isNotBlank(autoRunResult.getGenerationRecord().getCompileStatus())) {
            keyPoints.add(message("ai.chat.protocol.label.static.verify",
                    resolveCompileStatusLabel(autoRunResult.getGenerationRecord().getCompileStatus())));
        }
        return keyPoints;
    }

    private List<String> buildProtocolUploadMissingInformation(AiProtocolAdaptationAutoRunResultVO autoRunResult) {
        if (autoRunResult == null || autoRunResult.getNextActions() == null) {
            return new ArrayList<>();
        }
        String runStatus = defaultText(autoRunResult.getRunStatus(), "FAILED");
        if ("COMPLETED".equals(runStatus)) {
            return new ArrayList<>();
        }
        return autoRunResult.getNextActions().stream()
                .filter(StringUtils::isNotBlank)
                .toList();
    }

    private String buildProtocolUploadProgressToolResult(AiProtocolAdaptationTask task,
                                                         AiProtocolAdaptationArtifactVO sourceArtifact,
                                                         String currentStage,
                                                         String answer) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        AiProtocolAdaptationTaskVO latestTask = safeSelectProtocolTask(task == null ? null : task.getTaskId());
        result.put("summary", defaultText(answer, message("ai.chat.protocol.task.processing")));
        result.put("keyPoints", buildProtocolUploadProgressKeyPoints(task, sourceArtifact, latestTask));
        result.put("missingInformation", new ArrayList<>());
        result.put("status", "PROCESSING");
        result.put("rawAnswer", defaultText(answer, message("ai.chat.protocol.task.processing")));
        result.put("taskId", resolveProtocolTaskId(task, latestTask));
        result.put("runStatus", "RUNNING");
        result.put("runStatusLabel", message("ai.chat.protocol.task.processing"));
        result.put("currentStage", currentStage);
        result.put("currentStageLabel", resolveProtocolProgressStageLabel(currentStage));
        putProtocolTaskStatus(result, task, latestTask);
        return JSON.toJSONString(result);
    }

    private List<String> buildProtocolUploadProgressKeyPoints(AiProtocolAdaptationTask task,
                                                              AiProtocolAdaptationArtifactVO sourceArtifact,
                                                              AiProtocolAdaptationTaskVO latestTask) {
        List<String> keyPoints = new ArrayList<>();
        Long taskId = resolveProtocolTaskId(task, latestTask);
        if (taskId != null) {
            keyPoints.add(message("ai.chat.protocol.label.task.id", taskId));
        }
        if (sourceArtifact != null && StringUtils.isNotBlank(sourceArtifact.getArtifactName())) {
            keyPoints.add(message("ai.chat.protocol.label.source.file", sourceArtifact.getArtifactName()));
        }
        if (latestTask != null && StringUtils.isNotBlank(latestTask.getTaskStatus())) {
            keyPoints.add(message("ai.chat.protocol.label.task.status", latestTask.getTaskStatus()));
        }
        if (latestTask != null && StringUtils.isNotBlank(latestTask.getParseStatus())) {
            keyPoints.add(message("ai.chat.protocol.label.parse.status", latestTask.getParseStatus()));
        }
        if (latestTask != null && StringUtils.isNotBlank(latestTask.getGenerationStatus())) {
            keyPoints.add(message("ai.chat.protocol.label.generation.status", latestTask.getGenerationStatus()));
        }
        return keyPoints;
    }

    private String buildProtocolUploadFailureToolResult(String answer,
                                                        AiProtocolAdaptationTask task,
                                                        AiProtocolAdaptationArtifactVO sourceArtifact) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        AiProtocolAdaptationTaskVO latestTask = safeSelectProtocolTask(task == null ? null : task.getTaskId());
        result.put("summary", answer);
        result.put("keyPoints", buildProtocolUploadProgressKeyPoints(task, sourceArtifact, latestTask));
        result.put("missingInformation", List.of(
                message("ai.chat.protocol.missing.file.format"),
                message("ai.chat.protocol.missing.document.content"),
                message("ai.chat.protocol.missing.reupload")
        ));
        result.put("status", "NEED_MORE_INFO");
        result.put("rawAnswer", answer);
        result.put("taskId", resolveProtocolTaskId(task, latestTask));
        result.put("runStatus", "FAILED");
        result.put("runStatusLabel", resolveAutoRunStatusLabel("FAILED"));
        putProtocolTaskStatus(result, task, latestTask);
        return JSON.toJSONString(result);
    }

    private String buildThingModelGenerateProgressToolResult(String originalFilename,
                                                             String currentStage,
                                                             String answer) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("summary", defaultText(answer, message("ai.chat.thing.model.generate.processing")));
        result.put("keyPoints", List.of(message("ai.chat.common.label.source.file",
                defaultText(originalFilename, message("ai.chat.thing.model.generate.file.fallback")))));
        result.put("missingInformation", new ArrayList<>());
        result.put("qualityIssues", new ArrayList<>());
        result.put("status", "PROCESSING");
        result.put("currentStage", currentStage);
        result.put("rawAnswer", defaultText(answer, message("ai.chat.thing.model.generate.processing")));
        return JSON.toJSONString(result);
    }

    private String buildThingModelGenerateFailureToolResult(String answer, String originalFilename) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("summary", answer);
        result.put("keyPoints", List.of(message("ai.chat.common.label.source.file",
                defaultText(originalFilename, message("ai.chat.thing.model.generate.file.fallback")))));
        result.put("missingInformation", List.of(
                message("ai.chat.thing.model.generate.missing.file.format"),
                message("ai.chat.thing.model.generate.missing.document.content"),
                message("ai.chat.thing.model.generate.missing.scanned.file")
        ));
        result.put("qualityIssues", List.of(answer));
        result.put("status", "NEED_MORE_INFO");
        result.put("rawAnswer", answer);
        return JSON.toJSONString(result);
    }

    private String buildRequirementEvaluationProgressToolResult(String originalFilename,
                                                                String currentStage,
                                                                String answer) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("summary", defaultText(answer, message("ai.chat.requirement.evaluation.processing")));
        result.put("keyPoints", List.of(message("ai.chat.common.label.source.file",
                defaultText(originalFilename, message("ai.chat.requirement.evaluation.file.fallback")))));
        result.put("requirementItems", new ArrayList<>());
        result.put("moduleImpacts", new ArrayList<>());
        result.put("risks", new ArrayList<>());
        result.put("pendingQuestions", new ArrayList<>());
        result.put("nextSteps", new ArrayList<>());
        result.put("references", new ArrayList<>());
        result.put("status", "PROCESSING");
        result.put("currentStage", currentStage);
        result.put("disclaimer", message("ai.chat.requirement.evaluation.disclaimer"));
        result.put("rawAnswer", defaultText(answer, message("ai.chat.requirement.evaluation.processing")));
        return JSON.toJSONString(result);
    }

    private String buildRequirementEvaluationFailureToolResult(String answer, String originalFilename) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        result.put("summary", answer);
        result.put("overallConclusion", message("ai.chat.requirement.evaluation.failure.overall"));
        result.put("matchLevel", "UNKNOWN");
        result.put("keyPoints", List.of(message("ai.chat.common.label.source.file",
                defaultText(originalFilename, message("ai.chat.requirement.evaluation.file.fallback")))));
        result.put("requirementItems", new ArrayList<>());
        result.put("moduleImpacts", new ArrayList<>());
        result.put("risks", List.of(answer));
        result.put("pendingQuestions", List.of(
                message("ai.chat.requirement.evaluation.missing.file.format"),
                message("ai.chat.requirement.evaluation.missing.document.content"),
                message("ai.chat.requirement.evaluation.missing.scanned.file")
        ));
        result.put("nextSteps", List.of(
                message("ai.chat.requirement.evaluation.next.retry"),
                message("ai.chat.requirement.evaluation.next.formal.review")
        ));
        result.put("references", new ArrayList<>());
        result.put("status", "NEED_MORE_INFO");
        result.put("disclaimer", message("ai.chat.requirement.evaluation.disclaimer"));
        result.put("rawAnswer", answer);
        return JSON.toJSONString(result);
    }

    private AiProtocolAdaptationTaskVO safeSelectProtocolTask(Long taskId) {
        if (taskId == null) {
            return null;
        }
        try {
            return aiProtocolAdaptationTaskService.selectAiProtocolAdaptationTaskVO(taskId);
        } catch (Exception ex) {
            log.warn("查询协议适配任务状态失败，taskId={}", taskId, ex);
            return null;
        }
    }

    private Long resolveProtocolTaskId(AiProtocolAdaptationTask task, AiProtocolAdaptationTaskVO latestTask) {
        if (latestTask != null && latestTask.getTaskId() != null) {
            return latestTask.getTaskId();
        }
        return task == null ? null : task.getTaskId();
    }

    private void putProtocolTaskStatus(LinkedHashMap<String, Object> result,
                                       AiProtocolAdaptationTask task,
                                       AiProtocolAdaptationTaskVO latestTask) {
        if (latestTask != null) {
            result.put("taskStatus", latestTask.getTaskStatus());
            result.put("parseStatus", latestTask.getParseStatus());
            result.put("validationStatus", latestTask.getValidationStatus());
            result.put("generationStatus", latestTask.getGenerationStatus());
            return;
        }
        if (task == null) {
            return;
        }
        result.put("taskStatus", task.getTaskStatus());
        result.put("parseStatus", task.getParseStatus());
        result.put("validationStatus", task.getValidationStatus());
        result.put("generationStatus", task.getGenerationStatus());
    }

    private String resolveProtocolProgressStageLabel(String currentStage) {
        if ("TASK_CREATED".equals(currentStage)) {
            return message("ai.chat.protocol.progress.task.created");
        }
        if ("AUTO_RUNNING".equals(currentStage)) {
            return message("ai.chat.protocol.progress.auto.running");
        }
        return defaultText(currentStage, message("ai.chat.protocol.progress.processing"));
    }

    private AiChatSendResponseVO buildChatSendResponse(AiChatSession session,
                                                       AiModelRouteVO route,
                                                       String question,
                                                       String requestedMode,
                                                       String effectiveMode,
                                                       String executedSkill,
                                                       String answer,
                                                       String toolResult,
                                                       String routeAudit) {
        AiChatSendResponseVO response = new AiChatSendResponseVO();
        response.setSessionId(session.getSessionId());
        response.setSessionCode(session.getSessionCode());
        response.setQuestion(question);
        response.setAnswer(answer);
        response.setModelCode(route.getModelCode());
        response.setProviderCode(route.getProviderCode());
        response.setChatMode(requestedMode);
        response.setModePolicy(resolveSessionModePolicy(session));
        response.setPinnedMode(resolveSessionPinnedMode(session));
        response.setLastEffectiveMode(resolveSessionLastEffectiveMode(session));
        response.setEffectiveChatMode(effectiveMode);
        response.setExecutedSkill(executedSkill);
        response.setToolResult(toolResult);
        response.setRouteAudit(routeAudit);
        return response;
    }

    private String resolveAutoRunStatusLabel(String runStatus) {
        if ("COMPLETED".equals(runStatus)) {
            return message("ai.chat.protocol.status.completed");
        }
        if ("COMPLETED_WITH_WARNINGS".equals(runStatus)) {
            return message("ai.chat.protocol.status.completed.with.warnings");
        }
        if ("NEED_REVIEW".equals(runStatus)) {
            return message("ai.chat.protocol.status.need.review");
        }
        if ("FAILED".equals(runStatus)) {
            return message("ai.chat.protocol.status.failed");
        }
        return defaultText(runStatus, message("ai.chat.protocol.status.unknown"));
    }

    private String resolveAutoRunStageLabel(String currentStage) {
        if ("DOCUMENT_PARSE".equals(currentStage)) {
            return message("ai.chat.protocol.stage.document.parse");
        }
        if ("QUALITY_GATE".equals(currentStage)) {
            return message("ai.chat.protocol.stage.quality.gate");
        }
        if ("CODE_GENERATION".equals(currentStage)) {
            return message("ai.chat.protocol.stage.code.generation");
        }
        if ("STATIC_VERIFY".equals(currentStage)) {
            return message("ai.chat.protocol.stage.static.verify");
        }
        return defaultText(currentStage, message("ai.chat.protocol.stage.unknown"));
    }

    private String resolveCompileStatusLabel(String compileStatus) {
        if ("STATIC_PASSED".equals(compileStatus)) {
            return message("ai.chat.protocol.compile.static.passed");
        }
        if ("STATIC_WARNING".equals(compileStatus)) {
            return message("ai.chat.protocol.compile.static.warning");
        }
        if ("STATIC_FAILED".equals(compileStatus)) {
            return message("ai.chat.protocol.compile.static.failed");
        }
        if ("PENDING".equals(compileStatus)) {
            return message("ai.chat.protocol.compile.pending");
        }
        return defaultText(compileStatus, message("ai.chat.protocol.compile.unverified"));
    }

    private String trimErrorMessage(String message) {
        if (StringUtils.isBlank(message)) {
            return message("ai.chat.protocol.error.retry.or.detail");
        }
        String normalized = message.replaceAll("\\s+", " ").trim();
        return normalized.length() <= 160 ? normalized : normalized.substring(0, 160);
    }

    private String buildProtocolParseToolResult(String answer) {
        LinkedHashMap<String, Object> result = new LinkedHashMap<>();
        List<String> normalizedLines = normalizeProtocolParseLines(answer);
        List<String> missingInformation = collectProtocolMissingInformation(answer, normalizedLines);
        String summary = extractProtocolSummary(answer, normalizedLines);
        List<String> keyPoints = extractProtocolKeyPoints(answer, normalizedLines, summary);
        result.put("summary", summary);
        result.put("keyPoints", keyPoints);
        result.put("missingInformation", missingInformation);
        result.put("status", missingInformation.isEmpty() ? "ANALYZED" : "NEED_MORE_INFO");
        result.put("rawAnswer", answer);
        return JSON.toJSONString(result);
    }

    private List<String> normalizeProtocolParseLines(String answer) {
        List<String> lines = new ArrayList<>();
        if (StringUtils.isBlank(answer)) {
            return lines;
        }
        String[] segments = answer.replace("\r", "").split("\n+");
        for (String segment : segments) {
            String line = cleanProtocolLine(segment);
            if (StringUtils.isNotBlank(line)) {
                lines.add(line);
            }
        }
        return lines;
    }

    private String cleanProtocolLine(String text) {
        if (StringUtils.isBlank(text)) {
            return "";
        }
        String cleaned = text.trim().replaceFirst("^[-*]+\\s*", "").replaceFirst("^\\d+[\\.)]?\\s*", "");
        return cleaned.trim();
    }

    private String extractProtocolSummary(String answer, List<String> lines) {
        if (lines != null && !lines.isEmpty()) {
            return lines.get(0);
        }
        if (StringUtils.isBlank(answer)) {
            return message("ai.chat.protocol.parse.completed.summary");
        }
        String compact = answer.trim();
        String normalizedText = compact.replace('。', '.').replace('；', ';');
        String[] sentences = normalizedText.split("[.;]");
        for (String sentence : sentences) {
            String normalized = cleanProtocolLine(sentence);
            if (StringUtils.isNotBlank(normalized)) {
                return normalized;
            }
        }
        return compact;
    }

    private List<String> extractProtocolKeyPoints(String answer, List<String> lines, String summary) {
        List<String> keyPoints = new ArrayList<>();
        if (lines != null && lines.size() > 1) {
            for (int i = 1; i < lines.size() && keyPoints.size() < 5; i++) {
                if (!summary.equals(lines.get(i))) {
                    keyPoints.add(lines.get(i));
                }
            }
            return keyPoints;
        }
        if (StringUtils.isBlank(answer)) {
            return keyPoints;
        }
        String normalizedText = answer.replace("\r", "").replace('。', '.').replace('；', ';');
        String[] sentences = normalizedText.split("[\\n.;]+");
        for (String sentence : sentences) {
            String normalized = cleanProtocolLine(sentence);
            if (StringUtils.isNotBlank(normalized) && !summary.equals(normalized) && !keyPoints.contains(normalized)) {
                keyPoints.add(normalized);
            }
            if (keyPoints.size() >= 5) {
                break;
            }
        }
        return keyPoints;
    }

    private List<String> collectProtocolMissingInformation(String answer, List<String> lines) {
        List<String> missingInformation = new ArrayList<>();
        List<String> candidates = new ArrayList<>();
        if (lines != null && !lines.isEmpty()) {
            candidates.addAll(lines);
        } else if (StringUtils.isNotBlank(answer)) {
            String normalizedText = answer.replace("\r", "").replace('。', '.').replace('；', ';');
            String[] sentences = normalizedText.split("[\\n.;]+");
            for (String sentence : sentences) {
                String normalized = cleanProtocolLine(sentence);
                if (StringUtils.isNotBlank(normalized)) {
                    candidates.add(normalized);
                }
            }
        }
        for (String candidate : candidates) {
            if (containsAny(candidate,
                    "缺失",
                    "补充",
                    "待确认",
                    "未提供",
                    "无法判断",
                    "不足",
                    "建议提供",
                    "建议补充")
                    && !missingInformation.contains(candidate)) {
                missingInformation.add(candidate);
            }
        }
        return missingInformation;
    }

    private void appendPreviewRows(StringBuilder builder, List<LinkedHashMap<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        builder.append('\n').append(message("ai.chat.nl2sql.label.preview.rows"));
        int previewSize = Math.min(rows.size(), 5);
        for (int i = 0; i < previewSize; i++) {
            builder.append("\n  ").append(i + 1).append(". ").append(JSON.toJSONString(rows.get(i)));
        }
    }

    private void appendFriendlyQueryResult(StringBuilder builder, String summary, AiNl2SqlQueryResultVO queryResult) {
        if (builder == null || queryResult == null) {
            return;
        }
        List<LinkedHashMap<String, Object>> rows = queryResult.getRows();
        if (rows == null || rows.isEmpty()) {
            builder.append('\n').append(message("ai.chat.nl2sql.query.result.empty"));
            return;
        }
        LinkedHashMap<String, Object> firstRow = rows.get(0);
        if (firstRow == null || firstRow.isEmpty()) {
            builder.append('\n').append(message("ai.chat.nl2sql.query.result.empty"));
            return;
        }
        if (rows.size() == 1 && firstRow.size() == 1) {
            String column = firstRow.keySet().iterator().next();
            Object value = firstRow.values().iterator().next();
            String label = StringUtils.isNotBlank(summary) ? trimResultLabel(summary) : resolveColumnDisplayName(column);
            builder.append('\n').append(message("ai.chat.nl2sql.query.result.single", label, formatFriendlyValue(value)));
            return;
        }
        builder.append('\n').append(message("ai.chat.nl2sql.query.result.preview"));
        int previewSize = Math.min(rows.size(), 5);
        for (int i = 0; i < previewSize; i++) {
            LinkedHashMap<String, Object> row = rows.get(i);
            if (row == null || row.isEmpty()) {
                continue;
            }
            builder.append("\n  ").append(i + 1).append(". ").append(formatFriendlyRow(row));
        }
    }

    private String formatFriendlyRow(LinkedHashMap<String, Object> row) {
        List<String> parts = new ArrayList<>();
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            parts.add(message("ai.chat.common.key.value", resolveColumnDisplayName(entry.getKey()), formatFriendlyValue(entry.getValue())));
        }
        return String.join(message("ai.chat.common.list.delimiter"), parts);
    }

    private String trimResultLabel(String summary) {
        if (StringUtils.isBlank(summary)) {
            return message("ai.chat.nl2sql.query.result.default.label");
        }
        return summary.trim()
                .replaceAll("[。；;，,：:]+$", "")
                .replaceAll("^结果说明[：:]", "")
                .trim();
    }

    private String formatFriendlyValue(Object value) {
        if (value == null) {
            return "-";
        }
        return String.valueOf(value);
    }

    private String yesNo(Boolean value) {
        return Boolean.TRUE.equals(value) ? message("ai.chat.common.yes") : message("ai.chat.common.no");
    }

    private String resolveColumnDisplayName(String column) {
        if (StringUtils.isBlank(column)) {
            return message("ai.chat.common.label.result");
        }
        String normalized = column.trim().replace("`", "");
        String lower = normalized.toLowerCase();
        if (lower.endsWith("_count") || lower.contains("count")) {
            if (lower.contains("online")) {
                return message("ai.chat.common.label.online.count");
            }
            if (lower.contains("offline")) {
                return message("ai.chat.common.label.offline.count");
            }
            if (lower.contains("device")) {
                return message("ai.chat.common.label.device.count");
            }
            if (lower.contains("product")) {
                return message("ai.chat.common.label.product.count");
            }
            if (lower.contains("alert") || lower.contains("alarm")) {
                return message("ai.chat.common.label.alert.count");
            }
            return message("ai.chat.common.label.count");
        }
        if (lower.contains("product")) {
            return message("ai.chat.common.label.product");
        }
        if (lower.contains("device")) {
            return message("ai.chat.common.label.device");
        }
        if (lower.contains("online")) {
            return message("ai.chat.common.label.online");
        }
        if (lower.contains("offline")) {
            return message("ai.chat.common.label.offline");
        }
        return normalized;
    }

    private String buildNl2SqlGuide() {
        return message("ai.chat.nl2sql.guide");
    }

    private String buildDeviceControlGuide() {
        return message("ai.chat.device.control.guide");
    }

    private String buildDeviceControlIntentMissingMessage(AiDeviceControlIntentVO controlIntent) {
        if (controlIntent == null) {
            return buildDeviceControlGuide();
        }
        if (controlIntent.getDeviceMetaData() == null) {
            return message("ai.chat.device.control.missing.device");
        }
        DeviceMetaData deviceMetaData = controlIntent.getDeviceMetaData();
        Device device = deviceMetaData.getDevice();
        String deviceName = device == null ? message("ai.chat.device.control.current.device") : defaultText(device.getDeviceName(), device.getSerialNumber());
        if (controlIntent.getThingModel() == null) {
            return message("ai.chat.device.control.missing.thing.model", deviceName, deviceName);
        }
        if (StringUtils.isBlank(controlIntent.getActionValue())) {
            ThingsModelValueItem thingModel = controlIntent.getThingModel();
            String thingModelName = defaultText(thingModel.getName(), thingModel.getId());
            return message("ai.chat.device.control.missing.action.value", deviceName, thingModelName);
        }
        return buildDeviceControlGuide();
    }

    private String defaultText(String primary, String fallback) {
        return StringUtils.isNotBlank(primary) ? primary.trim() : defaultText(fallback);
    }

    private String buildSessionTitle(String message) {
        if (StringUtils.isBlank(message)) {
            return message("ai.chat.session.title.default");
        }
        String title = message.trim().replaceAll("\\s+", " ");
        return title.length() <= 20 ? title : title.substring(0, 20);
    }

    private String resolveModePolicy(AiChatSendRequest request) {
        if (request != null && MODE_POLICY_PINNED.equalsIgnoreCase(request.getModePolicy())) {
            return MODE_POLICY_PINNED;
        }
        if (request != null && MODE_POLICY_AUTO.equalsIgnoreCase(request.getModePolicy())) {
            return MODE_POLICY_AUTO;
        }
        String legacyMode = resolveChatMode(request == null ? null : request.getChatMode());
        return AiChatMode.AUTO.name().equals(legacyMode) ? MODE_POLICY_AUTO : MODE_POLICY_PINNED;
    }

    private String resolvePinnedMode(AiChatSendRequest request) {
        if (request == null) {
            return null;
        }
        if (StringUtils.isNotBlank(request.getPinnedMode())) {
            String normalized = resolveChatMode(request.getPinnedMode());
            return AiChatMode.AUTO.name().equals(normalized) ? null : normalized;
        }
        if (hasExplicitModeStrategy(request)) {
            return null;
        }
        String legacyMode = resolveChatMode(request.getChatMode());
        return AiChatMode.AUTO.name().equals(legacyMode) ? null : legacyMode;
    }

    private String resolveRequestedMode(AiChatSendRequest request, String modePolicy, String pinnedMode) {
        if (request != null && StringUtils.isNotBlank(request.getModeOverride())) {
            String normalizedOverride = resolveChatMode(request.getModeOverride());
            if (!AiChatMode.AUTO.name().equals(normalizedOverride)) {
                return normalizedOverride;
            }
        }
        if (MODE_POLICY_PINNED.equals(modePolicy) && StringUtils.isNotBlank(pinnedMode)) {
            return pinnedMode;
        }
        if (!hasExplicitModeStrategy(request)) {
            return resolveChatMode(request == null ? null : request.getChatMode());
        }
        return AiChatMode.AUTO.name();
    }

    private String resolveSessionChatMode(AiChatSendRequest request,
                                          String modePolicy,
                                          String pinnedMode,
                                          String requestedMode) {
        if (MODE_POLICY_PINNED.equals(modePolicy) && StringUtils.isNotBlank(pinnedMode)) {
            return pinnedMode;
        }
        if (!hasExplicitModeStrategy(request)) {
            return requestedMode;
        }
        return AiChatMode.AUTO.name();
    }

    private boolean hasExplicitModeStrategy(AiChatSendRequest request) {
        return request != null && (
                StringUtils.isNotBlank(request.getModePolicy())
                        || StringUtils.isNotBlank(request.getPinnedMode())
                        || StringUtils.isNotBlank(request.getModeOverride())
        );
    }

    private String resolveSessionModePolicy(AiChatSession session) {
        if (session == null) {
            return MODE_POLICY_AUTO;
        }
        String explicitPolicy = normalizeModePolicy(session.getModePolicy());
        if (StringUtils.isNotBlank(explicitPolicy)) {
            return explicitPolicy;
        }
        String chatMode = resolveChatMode(session.getChatMode());
        return AiChatMode.AUTO.name().equals(chatMode) ? MODE_POLICY_AUTO : MODE_POLICY_PINNED;
    }

    private String resolveSessionPinnedMode(AiChatSession session) {
        if (session == null) {
            return null;
        }
        String explicitPinnedMode = resolveExecutionMode(session.getPinnedMode());
        if (StringUtils.isNotBlank(explicitPinnedMode) && !AiChatMode.AUTO.name().equals(explicitPinnedMode)) {
            return explicitPinnedMode;
        }
        String chatMode = resolveChatMode(session.getChatMode());
        return AiChatMode.AUTO.name().equals(chatMode) ? null : chatMode;
    }

    private String resolveSessionLastEffectiveMode(AiChatSession session) {
        if (session == null) {
            return null;
        }
        String lastEffectiveMode = resolveExecutionMode(session.getLastEffectiveMode());
        return AiChatMode.AUTO.name().equals(lastEffectiveMode) ? null : lastEffectiveMode;
    }

    private String normalizeModePolicy(String modePolicy) {
        if (StringUtils.isBlank(modePolicy)) {
            return null;
        }
        return MODE_POLICY_PINNED.equalsIgnoreCase(modePolicy) ? MODE_POLICY_PINNED : MODE_POLICY_AUTO;
    }

    private String resolvePinnedModeSnapshot(String modePolicy, String pinnedMode, String sessionChatMode) {
        if (!MODE_POLICY_PINNED.equals(modePolicy)) {
            return null;
        }
        String explicitPinnedMode = resolveExecutionMode(pinnedMode);
        if (StringUtils.isNotBlank(explicitPinnedMode) && !AiChatMode.AUTO.name().equals(explicitPinnedMode)) {
            return explicitPinnedMode;
        }
        String compatibilityMode = resolveChatMode(sessionChatMode);
        return AiChatMode.AUTO.name().equals(compatibilityMode) ? null : compatibilityMode;
    }

    private String resolveInteractionSource(String interactionSource) {
        if (StringUtils.isBlank(interactionSource)) {
            return null;
        }
        String normalized = interactionSource.trim().toUpperCase();
        if (AiChatObservabilityConstants.INTERACTION_SOURCE_MODE_CORRECTION_RETRY.equals(normalized)) {
            return AiChatObservabilityConstants.INTERACTION_SOURCE_MODE_CORRECTION_RETRY;
        }
        return normalized;
    }

    private String resolveManualModeSource(String requestedMode, String modePolicy, String modeOverride) {
        String normalizedRequestedMode = resolveExecutionMode(requestedMode);
        if (AiChatMode.AUTO.name().equals(normalizedRequestedMode)) {
            return null;
        }
        if (StringUtils.isNotBlank(resolveExecutionMode(modeOverride))) {
            return AiChatObservabilityConstants.MANUAL_MODE_SOURCE_OVERRIDE;
        }
        if (MODE_POLICY_PINNED.equals(normalizeModePolicy(modePolicy))) {
            return AiChatObservabilityConstants.MANUAL_MODE_SOURCE_PINNED;
        }
        return AiChatObservabilityConstants.MANUAL_MODE_SOURCE_DIRECT;
    }

    private String resolveChatModeLabel(String mode) {
        if (AiChatMode.AUTO.name().equals(mode)) {
            return message("ai.chat.mode.auto");
        }
        if (AiChatMode.PLATFORM_ASSISTANT.name().equals(mode)) {
            return message("ai.chat.mode.platform.assistant");
        }
        if (AiChatMode.GENERAL.name().equals(mode)) {
            return message("ai.chat.mode.general");
        }
        if (AiChatMode.NL2SQL.name().equals(mode)) {
            return message("ai.chat.mode.nl2sql");
        }
        if (AiChatMode.DEVICE_CONTROL.name().equals(mode)) {
            return message("ai.chat.mode.device.control");
        }
        if (AiChatMode.PROTOCOL_PARSE.name().equals(mode)) {
            return message("ai.chat.mode.protocol.parse");
        }
        if (AiChatMode.THING_MODEL_GENERATE.name().equals(mode)) {
            return message("ai.chat.mode.thing.model.generate");
        }
        if (AiChatMode.REQUIREMENT_EVALUATION.name().equals(mode)) {
            return message("ai.chat.mode.requirement.evaluation");
        }
        return StringUtils.defaultString(mode);
    }

    private String resolveExecutionMode(String mode) {
        if (StringUtils.isBlank(mode)) {
            return null;
        }
        return resolveChatMode(mode);
    }

    private String resolveChatMode(String chatMode) {
        if (StringUtils.isBlank(chatMode)) {
            return AiChatMode.AUTO.name();
        }
        String normalized = chatMode.trim().toUpperCase();
        for (AiChatMode item : AiChatMode.values()) {
            if (item.name().equals(normalized)) {
                return normalized;
            }
        }
        return AiChatMode.AUTO.name();
    }

    private static class AiChatExecutionContext {
        private final AiModelRouteVO route;
        private final AiRuntimeModelSnapshot snapshot;
        private final ChatModel chatModel;

        private AiChatExecutionContext(AiModelRouteVO route, AiRuntimeModelSnapshot snapshot, ChatModel chatModel) {
            this.route = route;
            this.snapshot = snapshot;
            this.chatModel = chatModel;
        }
    }

    private static class AiModeExecutionResult {
        private final String answer;
        private final String effectiveChatMode;
        private final String toolName;
        private final String toolResult;
        private final AiDeviceControlContextSnapshotVO contextSnapshot;
        private final AiNl2SqlContextSnapshotVO nl2SqlContextSnapshot;
        private final AiPlatformAssistantContextSnapshotVO platformAssistantContextSnapshot;
        private final AiProtocolParseContextSnapshotVO protocolParseContextSnapshot;

        private AiModeExecutionResult(String answer, String effectiveChatMode, String toolName, String toolResult) {
            this(answer, effectiveChatMode, toolName, toolResult, null, null, null, null);
        }

        private AiModeExecutionResult(String answer,
                                      String effectiveChatMode,
                                      String toolName,
                                      String toolResult,
                                      AiDeviceControlContextSnapshotVO contextSnapshot) {
            this(answer, effectiveChatMode, toolName, toolResult, contextSnapshot, null, null, null);
        }

        private AiModeExecutionResult(String answer,
                                      String effectiveChatMode,
                                      String toolName,
                                      String toolResult,
                                      AiNl2SqlContextSnapshotVO nl2SqlContextSnapshot) {
            this(answer, effectiveChatMode, toolName, toolResult, null, nl2SqlContextSnapshot, null, null);
        }

        private AiModeExecutionResult(String answer,
                                      String effectiveChatMode,
                                      String toolName,
                                      String toolResult,
                                      AiPlatformAssistantContextSnapshotVO platformAssistantContextSnapshot) {
            this(answer, effectiveChatMode, toolName, toolResult, null, null, platformAssistantContextSnapshot, null);
        }

        private AiModeExecutionResult(String answer,
                                      String effectiveChatMode,
                                      String toolName,
                                      String toolResult,
                                      AiProtocolParseContextSnapshotVO protocolParseContextSnapshot) {
            this(answer, effectiveChatMode, toolName, toolResult, null, null, null, protocolParseContextSnapshot);
        }

        private AiModeExecutionResult(String answer,
                                      String effectiveChatMode,
                                      String toolName,
                                      String toolResult,
                                      AiDeviceControlContextSnapshotVO contextSnapshot,
                                      AiNl2SqlContextSnapshotVO nl2SqlContextSnapshot,
                                      AiPlatformAssistantContextSnapshotVO platformAssistantContextSnapshot,
                                      AiProtocolParseContextSnapshotVO protocolParseContextSnapshot) {
            this.answer = answer;
            this.effectiveChatMode = effectiveChatMode;
            this.toolName = toolName;
            this.toolResult = toolResult;
            this.contextSnapshot = contextSnapshot;
            this.nl2SqlContextSnapshot = nl2SqlContextSnapshot;
            this.platformAssistantContextSnapshot = platformAssistantContextSnapshot;
            this.protocolParseContextSnapshot = protocolParseContextSnapshot;
        }
    }
}
