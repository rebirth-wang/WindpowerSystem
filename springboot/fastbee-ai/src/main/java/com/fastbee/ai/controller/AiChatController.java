package com.fastbee.ai.controller;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import com.fastbee.ai.config.properties.FastBeeAiProperties;
import com.fastbee.ai.constant.AiPromptConstant;
import com.fastbee.ai.domain.AiChatMessage;
import com.fastbee.ai.domain.AiChatSession;
import com.fastbee.ai.model.dto.AiChatSendRequest;
import com.fastbee.ai.model.enums.AiChatMode;
import com.fastbee.ai.model.vo.AiChatObservabilityStatsVO;
import com.fastbee.ai.model.vo.AiChatSendResponseVO;
import com.fastbee.ai.model.vo.AiChatSessionVO;
import com.fastbee.ai.model.vo.AiChatStreamEventVO;
import com.fastbee.ai.model.vo.AiModelRouteVO;
import com.fastbee.ai.service.AiChatService;
import com.fastbee.ai.service.AiModelRoutingService;
import com.fastbee.ai.service.IAiChatMessageService;
import com.fastbee.ai.service.IAiChatObservabilityService;
import com.fastbee.ai.service.IAiChatSessionService;
import com.fastbee.ai.service.IAiRequirementEvaluationService;
import com.fastbee.ai.service.IAiThingModelGenerateService;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 会话控制器。
 */
@RestController
@RequestMapping("/ai/chat")
public class AiChatController extends BaseController {

    private static final String MODE_POLICY_PINNED = "PINNED";

    @Resource
    private AiChatService aiChatService;

    @Resource
    private AiModelRoutingService aiModelRoutingService;

    @Resource
    private IAiChatSessionService aiChatSessionService;

    @Resource
    private IAiChatMessageService aiChatMessageService;

    @Resource
    private IAiChatObservabilityService aiChatObservabilityService;

    @Resource
    private IAiThingModelGenerateService aiThingModelGenerateService;

    @Resource
    private IAiRequirementEvaluationService aiRequirementEvaluationService;

    @Resource
    private FastBeeAiProperties properties;

    /**
     * 获取默认模型路由。
     *
     * @return 默认路由
     */
    @PreAuthorize("@ss.hasPermi('ai:chat:list')")
    @GetMapping("/route/default")
    public AjaxResult defaultRoute() {
        AiModelRouteVO route = aiModelRoutingService.resolveDefaultRoute();
        return AjaxResult.success(route);
    }

    /**
     * 获取会话模式列表。
     *
     * @return 模式列表
     */
    @PreAuthorize("@ss.hasPermi('ai:chat:list')")
    @GetMapping("/modes")
    public AjaxResult modes() {
        List<String> modeList = Arrays.stream(AiChatMode.values()).map(Enum::name).collect(Collectors.toList());
        return AjaxResult.success(modeList);
    }

    /**
     * 获取 AI 会话运行时能力。
     *
     * @return 运行时能力快照
     */
    @PreAuthorize("@ss.hasPermi('ai:chat:list')")
    @GetMapping("/runtime")
    public AjaxResult runtime() {
        LinkedHashMap<String, Object> runtime = new LinkedHashMap<>();
        runtime.put("chatEnabled", properties.getRuntime() == null || properties.getRuntime().isChatEnabled());
        runtime.put("nl2sqlEnabled", properties.getRuntime() == null || properties.getRuntime().isNl2sqlEnabled());
        runtime.put("deviceControlEnabled", properties.getRuntime() == null || properties.getRuntime().isDeviceControlEnabled());
        runtime.put("protocolEnabled", properties.getRuntime() == null || properties.getRuntime().isProtocolEnabled());
        runtime.put("thingModelGenerateEnabled", properties.getRuntime() == null || properties.getRuntime().isChatEnabled());
        runtime.put("timeoutMs", properties.getRuntime() == null ? 30000 : properties.getRuntime().getTimeoutMs());
        return AjaxResult.success(runtime);
    }

    /**
     * 获取当前用户会话观测统计。
     *
     * @param days 统计窗口天数
     * @return 观测统计
     */
    @PreAuthorize("@ss.hasPermi('ai:chat:list')")
    @GetMapping("/observability")
    public AjaxResult observability(Integer days) {
        AiChatObservabilityStatsVO stats = aiChatObservabilityService.getCurrentUserStats(getUserId(), days);
        return AjaxResult.success(stats);
    }

    /**
     * 发送 AI 对话消息。
     *
     * @param request 对话请求
     * @return 对话结果
     */
    @PreAuthorize("@ss.hasPermi('ai:chat:add')")
    @PostMapping("/send")
    public AjaxResult send(@Validated @RequestBody AiChatSendRequest request) {
        AiChatSendResponseVO response = aiChatService.send(request);
        return AjaxResult.success(response);
    }

    /**
     * 发送 AI 流式对话消息。
     * <p>
     * 同一入口同时支持两种请求体：
     * 1. application/json：普通 AI 会话；
     * 2. multipart/form-data：带文件的会话，按显式技能或自动识别结果路由到协议解析或普通附件会话。
     * </p>
     *
     * @param httpRequest HTTP 请求
     * @return 流式事件
     */
    @PreAuthorize("@ss.hasPermi('ai:chat:add')")
    @PostMapping(value = "/send/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sendStream(HttpServletRequest httpRequest, HttpServletResponse response) throws IOException {
        SendStreamRequestContext requestContext = resolveSendStreamRequest(httpRequest);
        prepareSseResponse(response);
        if (requestContext.file != null && requestContext.requirementEvaluationUpload) {
            return subscribeStream(aiChatService.uploadRequirementEvaluationDocumentStream(requestContext.request, requestContext.file),
                    resolveFileStreamTimeoutMs(), "REQUIREMENT_EVALUATION_STREAM_ERROR", "需求文件评估失败");
        }
        if (requestContext.file != null && requestContext.thingModelUpload) {
            return subscribeStream(aiChatService.uploadThingModelDocumentStream(requestContext.request, requestContext.file),
                    resolveFileStreamTimeoutMs(), "THING_MODEL_UPLOAD_STREAM_ERROR", "物模型文件解析失败");
        }
        if (requestContext.file != null && requestContext.protocolUpload) {
            return subscribeStream(aiChatService.uploadProtocolDocumentStream(requestContext.request, requestContext.file),
                    resolveProtocolStreamTimeoutMs(), "PROTOCOL_UPLOAD_STREAM_ERROR", "协议文件流式解析失败");
        }
        if (requestContext.file != null) {
            return subscribeStream(aiChatService.sendStreamWithAttachment(requestContext.request, requestContext.file),
                    resolveFileStreamTimeoutMs(), "ATTACHMENT_STREAM_ERROR", "附件会话处理失败");
        }
        return subscribeStream(aiChatService.sendStream(requestContext.request),
                resolveChatStreamTimeoutMs(), "STREAM_SUBSCRIBE_ERROR", "流式对话失败");
    }

    /**
     * 继续执行上一轮澄清后的流式对话。
     *
     * @param request 恢复执行请求
     * @return 流式事件
     */
    @PreAuthorize("@ss.hasPermi('ai:chat:add')")
    @PostMapping(value = "/send/stream/resume", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter resumeStream(@Validated @RequestBody AiChatSendRequest request, HttpServletResponse response) {
        prepareSseResponse(response);
        long timeoutMs = resolveChatStreamTimeoutMs();
        SseEmitter emitter = new SseEmitter(timeoutMs);
        AtomicReference<Disposable> disposableRef = new AtomicReference<>();
        disposableRef.set(aiChatService.resumeStream(request).subscribe(
                event -> {
                    if (!sendStreamEvent(emitter, event)) {
                        dispose(disposableRef);
                    }
                },
                error -> {
                    try {
                        AiChatStreamEventVO errorEvent = new AiChatStreamEventVO();
                        errorEvent.setEventType("error");
                        errorEvent.setDone(Boolean.TRUE);
                        errorEvent.setErrorCode("STREAM_RESUME_ERROR");
                        errorEvent.setErrorMessage(error == null ? "流式恢复执行失败" : error.getMessage());
                        sendStreamEvent(emitter, errorEvent);
                    } finally {
                        emitter.complete();
                    }
                },
                emitter::complete
        ));
        emitter.onCompletion(() -> dispose(disposableRef));
        emitter.onTimeout(() -> {
            dispose(disposableRef);
            emitter.complete();
        });
        emitter.onError(ex -> dispose(disposableRef));
        return emitter;
    }

    /**
     * 查询当前用户会话列表。
     *
     * @param aiChatSession 查询条件
     * @return 会话列表
     */
    @PreAuthorize("@ss.hasPermi('ai:chat:list')")
    @GetMapping("/session/list")
    public TableDataInfo sessionList(AiChatSession aiChatSession) {
        aiChatSession.setIsArchived("0");
        Page<AiChatSessionVO> page = aiChatSessionService.pageCurrentUserSessionVO(aiChatSession);
        return getDataTable(page.getRecords(), page.getTotal());
    }

    /**
     * 查询当前用户会话消息列表。
     *
     * @param aiChatMessage 查询条件
     * @return 消息列表
     */
    @PreAuthorize("@ss.hasPermi('ai:chat:list')")
    @GetMapping("/message/list")
    public AjaxResult messageList(AiChatMessage aiChatMessage) {
        if (aiChatMessage.getSessionId() == null) {
            return AjaxResult.success(List.of());
        }
        AiChatSession sessionQuery = new AiChatSession();
        sessionQuery.setSessionId(aiChatMessage.getSessionId());
        sessionQuery.setUserId(getUserId());
        AiChatSession session = aiChatSessionService.selectAiChatSession(sessionQuery);
        if (session == null) {
            return AjaxResult.success(List.of());
        }
        return AjaxResult.success(aiChatMessageService.listAiChatMessage(aiChatMessage));
    }

    /**
     * 下载 AI 生成的物模型导入模板。
     *
     * @param artifactCode 产物编码
     * @param response 响应对象
     */
    @PreAuthorize("@ss.hasPermi('ai:chat:list')")
    @PostMapping("/thing-model/workbook/{artifactCode}/download")
    public void downloadThingModelWorkbook(@PathVariable("artifactCode") String artifactCode,
                                           HttpServletResponse response) {
        aiThingModelGenerateService.downloadWorkbook(artifactCode, response);
    }

    /**
     * 下载 AI 生成的需求评估结果文件。
     *
     * @param artifactCode 产物编码
     * @param response 响应对象
     */
    @PreAuthorize("@ss.hasPermi('ai:chat:list')")
    @PostMapping("/requirement-evaluation/report/{artifactCode}/download")
    public void downloadRequirementEvaluationReport(@PathVariable("artifactCode") String artifactCode,
                                                    HttpServletResponse response) {
        aiRequirementEvaluationService.downloadReport(artifactCode, response);
    }

    private SseEmitter subscribeStream(Flux<AiChatStreamEventVO> eventFlux,
                                       long timeoutMs,
                                       String errorCode,
                                       String defaultErrorMessage) {
        SseEmitter emitter = new SseEmitter(timeoutMs);
        AtomicReference<Disposable> disposableRef = new AtomicReference<>();
        disposableRef.set(eventFlux.subscribe(
                event -> {
                    if (!sendStreamEvent(emitter, event)) {
                        dispose(disposableRef);
                    }
                },
                error -> {
                    try {
                        AiChatStreamEventVO errorEvent = new AiChatStreamEventVO();
                        errorEvent.setEventType("error");
                        errorEvent.setDone(Boolean.TRUE);
                        errorEvent.setErrorCode(errorCode);
                        errorEvent.setErrorMessage(error == null ? defaultErrorMessage : error.getMessage());
                        sendStreamEvent(emitter, errorEvent);
                    } finally {
                        emitter.complete();
                    }
                },
                emitter::complete
        ));
        emitter.onCompletion(() -> dispose(disposableRef));
        emitter.onTimeout(() -> {
            dispose(disposableRef);
            emitter.complete();
        });
        emitter.onError(ex -> dispose(disposableRef));
        return emitter;
    }

    private void prepareSseResponse(HttpServletResponse response) {
        if (response == null) {
            return;
        }
        response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Cache-Control", "no-cache, no-transform");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("X-Accel-Buffering", "no");
    }

    private SendStreamRequestContext resolveSendStreamRequest(HttpServletRequest httpRequest) throws IOException {
        if (isMultipartRequest(httpRequest)) {
            return resolveMultipartSendStreamRequest(httpRequest);
        }
        AiChatSendRequest request = parseJsonSendStreamRequest(httpRequest);
        validateTextOnlyStreamRequest(request);
        return new SendStreamRequestContext(request, null, false, false, false);
    }

    private SendStreamRequestContext resolveMultipartSendStreamRequest(HttpServletRequest httpRequest) {
        if (!(httpRequest instanceof MultipartHttpServletRequest multipartRequest)) {
            throw new ServiceException(message("ai.chat.upload.request.not.multipart"));
        }
        AiChatSendRequest request = buildMultipartChatRequest(multipartRequest);
        MultipartFile file = resolveFirstMultipartFile(multipartRequest);
        if (file == null || file.isEmpty()) {
            validateTextOnlyStreamRequest(request);
            return new SendStreamRequestContext(request, null, false, false, false);
        }
        boolean requirementEvaluationUpload = shouldRouteToRequirementEvaluation(request, file);
        boolean thingModelUpload = !requirementEvaluationUpload && shouldRouteToThingModelGenerate(request, file);
        boolean protocolUpload = !requirementEvaluationUpload && !thingModelUpload && shouldRouteToProtocolParse(request, file);
        if (requirementEvaluationUpload) {
            request.setModeOverride(AiChatMode.REQUIREMENT_EVALUATION.name());
            if (StringUtils.isBlank(request.getChatMode()) || AiChatMode.AUTO.name().equalsIgnoreCase(request.getChatMode())) {
                request.setChatMode(AiChatMode.AUTO.name());
            }
            if (StringUtils.isBlank(request.getMessage())) {
                request.setMessage(message("ai.chat.requirement.evaluation.default.question", resolveUploadFilename(file)));
            }
        } else if (thingModelUpload) {
            request.setModeOverride(AiChatMode.THING_MODEL_GENERATE.name());
            if (StringUtils.isBlank(request.getChatMode()) || AiChatMode.AUTO.name().equalsIgnoreCase(request.getChatMode())) {
                request.setChatMode(AiChatMode.AUTO.name());
            }
            if (StringUtils.isBlank(request.getMessage())) {
                request.setMessage(message("ai.chat.thing.model.generate.default.question", resolveUploadFilename(file)));
            }
        } else if (protocolUpload) {
            request.setModeOverride(AiChatMode.PROTOCOL_PARSE.name());
            if (StringUtils.isBlank(request.getChatMode()) || AiChatMode.AUTO.name().equalsIgnoreCase(request.getChatMode())) {
                request.setChatMode(AiChatMode.AUTO.name());
            }
            if (StringUtils.isBlank(request.getMessage())) {
                request.setMessage(message("ai.chat.protocol.default.question", resolveUploadFilename(file)));
            }
        } else if (StringUtils.isBlank(request.getMessage())) {
            request.setMessage(AiPromptConstant.ATTACHMENT_SUMMARY_QUESTION + "：" + resolveUploadFilename(file));
        }
        return new SendStreamRequestContext(request, file, protocolUpload, thingModelUpload, requirementEvaluationUpload);
    }

    private AiChatSendRequest parseJsonSendStreamRequest(HttpServletRequest httpRequest) throws IOException {
        String body = new String(httpRequest.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        if (StringUtils.isBlank(body)) {
            throw new ServiceException(message("ai.chat.message.required"));
        }
        AiChatSendRequest request = JSON.parseObject(body, AiChatSendRequest.class);
        if (request == null) {
            throw new ServiceException(message("ai.chat.message.required"));
        }
        return request;
    }

    private AiChatSendRequest buildMultipartChatRequest(MultipartHttpServletRequest multipartRequest) {
        AiChatSendRequest request = new AiChatSendRequest();
        request.setSessionId(parseLong(multipartRequest.getParameter("sessionId")));
        request.setMessage(trimToNull(multipartRequest.getParameter("message")));
        request.setModelCode(trimToNull(multipartRequest.getParameter("modelCode")));
        request.setProviderCode(trimToNull(multipartRequest.getParameter("providerCode")));
        request.setChatMode(trimToNull(multipartRequest.getParameter("chatMode")));
        request.setModePolicy(trimToNull(multipartRequest.getParameter("modePolicy")));
        request.setPinnedMode(trimToNull(multipartRequest.getParameter("pinnedMode")));
        request.setModeOverride(trimToNull(multipartRequest.getParameter("modeOverride")));
        request.setInteractionSource(trimToNull(multipartRequest.getParameter("interactionSource")));
        request.setRetrySourceMessageId(parseLong(multipartRequest.getParameter("retrySourceMessageId")));
        request.setResumeToken(trimToNull(multipartRequest.getParameter("resumeToken")));
        request.setClarifyKey(trimToNull(multipartRequest.getParameter("clarifyKey")));
        request.setSelectedValue(trimToNull(multipartRequest.getParameter("selectedValue")));
        request.setSelectedLabel(trimToNull(multipartRequest.getParameter("selectedLabel")));
        request.setResumeQuestion(trimToNull(multipartRequest.getParameter("resumeQuestion")));
        return request;
    }

    private void validateTextOnlyStreamRequest(AiChatSendRequest request) {
        if (request == null || StringUtils.isBlank(request.getMessage())) {
            throw new ServiceException(message("ai.chat.message.required"));
        }
    }

    private boolean shouldRouteToProtocolParse(AiChatSendRequest request, MultipartFile file) {
        if (isExplicitProtocolMode(request)) {
            return true;
        }
        if (isExplicitNonProtocolMode(request)) {
            return false;
        }
        String signalText = (defaultText(request == null ? null : request.getMessage()) + " " + resolveUploadFilename(file)).toLowerCase();
        return containsAny(signalText,
                "协议", "通讯协议", "通信协议", "报文", "帧头", "帧尾", "寄存器", "功能码", "modbus", "mqtt payload",
                "encoder", "decoder", "编解码", "解析协议", "协议解析", "协议适配", "代码包",
                "protocol file", "parse protocol", "protocol parse", "protocol adaptation", "code package", "codec", "frame", "register");
    }

    private boolean shouldRouteToThingModelGenerate(AiChatSendRequest request, MultipartFile file) {
        if (isExplicitThingModelGenerateMode(request)) {
            return true;
        }
        if (isExplicitNonThingModelGenerateMode(request)) {
            return false;
        }
        String signalText = (defaultText(request == null ? null : request.getMessage()) + " " + resolveUploadFilename(file)).toLowerCase();
        boolean hasThingModelSignal = containsAny(signalText,
                "物模型生成", "生成物模型", "物模型导入", "物模型模板", "导入模板", "产品物模型",
                "设备属性", "属性清单", "点位表", "点表", "测点", "遥测", "telemetry", "datapoint", "point list",
                "thing model", "generate thing model", "thing model import", "import template", "property list", "device property");
        boolean hasGenerateIntent = containsAny(signalText,
                "生成", "导入", "模板", "提取", "解析文件", "解析这份", "解析这个", "上传文件", "生成excel", "生成 excel",
                "generate", "import", "template", "extract", "parse file", "upload file");
        return hasThingModelSignal && hasGenerateIntent;
    }

    private boolean shouldRouteToRequirementEvaluation(AiChatSendRequest request, MultipartFile file) {
        if (isExplicitRequirementEvaluationMode(request)) {
            return true;
        }
        if (isExplicitNonRequirementEvaluationMode(request)) {
            return false;
        }
        String signalText = (defaultText(request == null ? null : request.getMessage()) + " " + resolveUploadFilename(file)).toLowerCase();
        boolean hasRequirementSignal = containsAny(signalText,
                "需求", "需求文档", "需求说明", "功能清单", "招标", "方案", "客户需求", "requirement", "requirements",
                "requirement document", "function list", "tender", "proposal");
        boolean hasEvaluateIntent = containsAny(signalText,
                "评估", "比对", "匹配", "可行性", "能否实现", "是否支持", "差距", "缺口", "风险", "工作量", "二开",
                "evaluate", "evaluation", "compare", "match", "feasibility", "support", "gap", "risk", "workload", "customization");
        return hasRequirementSignal && hasEvaluateIntent;
    }

    private boolean isExplicitProtocolMode(AiChatSendRequest request) {
        if (request == null) {
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
        return StringUtils.isNotBlank(request.getChatMode())
                && !AiChatMode.AUTO.name().equalsIgnoreCase(request.getChatMode())
                && AiChatMode.PROTOCOL_PARSE.name().equalsIgnoreCase(request.getChatMode());
    }

    private boolean isExplicitNonProtocolMode(AiChatSendRequest request) {
        if (request == null) {
            return false;
        }
        if (StringUtils.isNotBlank(request.getModeOverride())
                && !AiChatMode.AUTO.name().equalsIgnoreCase(request.getModeOverride())
                && !AiChatMode.PROTOCOL_PARSE.name().equalsIgnoreCase(request.getModeOverride())) {
            return true;
        }
        if (MODE_POLICY_PINNED.equalsIgnoreCase(request.getModePolicy())
                && StringUtils.isNotBlank(request.getPinnedMode())
                && !AiChatMode.PROTOCOL_PARSE.name().equalsIgnoreCase(request.getPinnedMode())) {
            return true;
        }
        return StringUtils.isNotBlank(request.getChatMode())
                && !AiChatMode.AUTO.name().equalsIgnoreCase(request.getChatMode())
                && !AiChatMode.PROTOCOL_PARSE.name().equalsIgnoreCase(request.getChatMode());
    }

    private boolean isExplicitThingModelGenerateMode(AiChatSendRequest request) {
        if (request == null) {
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
        return StringUtils.isNotBlank(request.getChatMode())
                && !AiChatMode.AUTO.name().equalsIgnoreCase(request.getChatMode())
                && AiChatMode.THING_MODEL_GENERATE.name().equalsIgnoreCase(request.getChatMode());
    }

    private boolean isExplicitNonThingModelGenerateMode(AiChatSendRequest request) {
        if (request == null) {
            return false;
        }
        if (StringUtils.isNotBlank(request.getModeOverride())
                && !AiChatMode.AUTO.name().equalsIgnoreCase(request.getModeOverride())
                && !AiChatMode.THING_MODEL_GENERATE.name().equalsIgnoreCase(request.getModeOverride())) {
            return true;
        }
        if (MODE_POLICY_PINNED.equalsIgnoreCase(request.getModePolicy())
                && StringUtils.isNotBlank(request.getPinnedMode())
                && !AiChatMode.THING_MODEL_GENERATE.name().equalsIgnoreCase(request.getPinnedMode())) {
            return true;
        }
        return StringUtils.isNotBlank(request.getChatMode())
                && !AiChatMode.AUTO.name().equalsIgnoreCase(request.getChatMode())
                && !AiChatMode.THING_MODEL_GENERATE.name().equalsIgnoreCase(request.getChatMode());
    }

    private boolean isExplicitRequirementEvaluationMode(AiChatSendRequest request) {
        if (request == null) {
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
        return StringUtils.isNotBlank(request.getChatMode())
                && !AiChatMode.AUTO.name().equalsIgnoreCase(request.getChatMode())
                && AiChatMode.REQUIREMENT_EVALUATION.name().equalsIgnoreCase(request.getChatMode());
    }

    private boolean isExplicitNonRequirementEvaluationMode(AiChatSendRequest request) {
        if (request == null) {
            return false;
        }
        if (StringUtils.isNotBlank(request.getModeOverride())
                && !AiChatMode.AUTO.name().equalsIgnoreCase(request.getModeOverride())
                && !AiChatMode.REQUIREMENT_EVALUATION.name().equalsIgnoreCase(request.getModeOverride())) {
            return true;
        }
        if (MODE_POLICY_PINNED.equalsIgnoreCase(request.getModePolicy())
                && StringUtils.isNotBlank(request.getPinnedMode())
                && !AiChatMode.REQUIREMENT_EVALUATION.name().equalsIgnoreCase(request.getPinnedMode())) {
            return true;
        }
        return StringUtils.isNotBlank(request.getChatMode())
                && !AiChatMode.AUTO.name().equalsIgnoreCase(request.getChatMode())
                && !AiChatMode.REQUIREMENT_EVALUATION.name().equalsIgnoreCase(request.getChatMode());
    }

    private MultipartFile resolveFirstMultipartFile(MultipartHttpServletRequest multipartRequest) {
        MultipartFile file = multipartRequest.getFile("file");
        if (file != null) {
            return file;
        }
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        return fileMap == null || fileMap.isEmpty() ? null : fileMap.values().iterator().next();
    }

    private boolean isMultipartRequest(HttpServletRequest httpRequest) {
        String contentType = httpRequest == null ? "" : httpRequest.getContentType();
        return contentType != null && contentType.toLowerCase().startsWith(MediaType.MULTIPART_FORM_DATA_VALUE);
    }

    private boolean containsAny(String content, String... keywords) {
        if (StringUtils.isBlank(content) || keywords == null) {
            return false;
        }
        for (String keyword : keywords) {
            if (StringUtils.isNotBlank(keyword) && content.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private String resolveUploadFilename(MultipartFile file) {
        return file == null || StringUtils.isBlank(file.getOriginalFilename()) ? "上传文件" : file.getOriginalFilename();
    }

    private String trimToNull(String value) {
        return StringUtils.isBlank(value) ? null : value.trim();
    }

    private String defaultText(String value) {
        return StringUtils.isBlank(value) ? "" : value.trim();
    }

    private Long parseLong(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        try {
            return Long.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private long resolveChatStreamTimeoutMs() {
        return properties.getRuntime() == null
                ? 60000L
                : Math.max(properties.getRuntime().getTimeoutMs() * 2L, 60000L);
    }

    private long resolveProtocolStreamTimeoutMs() {
        return resolveFileStreamTimeoutMs();
    }

    private long resolveFileStreamTimeoutMs() {
        return properties.getRuntime() == null
                ? 300000L
                : Math.max(properties.getRuntime().getTimeoutMs() * 10L, 300000L);
    }

    private boolean sendStreamEvent(SseEmitter emitter, AiChatStreamEventVO event) {
        try {
            emitter.send(SseEmitter.event()
                    .name(event.getEventType() == null ? "message" : event.getEventType())
                    .data(event, MediaType.APPLICATION_JSON));
            return true;
        } catch (IOException ex) {
            if (emitter != null) {
                emitter.complete();
                return false;
            }
            throw new IllegalStateException("发送流式事件失败", ex);
        }
    }

    private void dispose(AtomicReference<Disposable> disposableRef) {
        Disposable disposable = disposableRef.getAndSet(null);
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    private static class SendStreamRequestContext {
        private final AiChatSendRequest request;
        private final MultipartFile file;
        private final boolean protocolUpload;
        private final boolean thingModelUpload;
        private final boolean requirementEvaluationUpload;

        private SendStreamRequestContext(AiChatSendRequest request, MultipartFile file,
                                         boolean protocolUpload, boolean thingModelUpload,
                                         boolean requirementEvaluationUpload) {
            this.request = request;
            this.file = file;
            this.protocolUpload = protocolUpload;
            this.thingModelUpload = thingModelUpload;
            this.requirementEvaluationUpload = requirementEvaluationUpload;
        }
    }
}
