package com.fastbee.ai.service.impl;

import java.util.List;

import jakarta.annotation.Resource;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import com.fastbee.ai.constant.AiPromptConstant;
import com.fastbee.ai.domain.AiChatMessage;
import com.fastbee.ai.model.enums.AiChatMode;
import com.fastbee.ai.model.vo.AiChatIntentRouteVO;
import com.fastbee.ai.model.vo.AiModelRouteVO;
import com.fastbee.ai.service.AiChatModelFactoryService;
import com.fastbee.ai.service.IAiChatIntentRouteService;
import com.fastbee.ai.support.AiChatIntentRouteParser;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 会话路由技能服务实现。
 */
@Service
public class AiChatIntentRouteServiceImpl implements IAiChatIntentRouteService {

    private static final int MAX_ROUTE_HISTORY = 8;

    @Resource
    private AiChatModelFactoryService aiChatModelFactoryService;

    @Resource
    private AiChatIntentRouteParser aiChatIntentRouteParser;

    @Override
    public AiChatIntentRouteVO analyze(String question, String requestedMode, AiModelRouteVO route, List<AiChatMessage> historyMessages) {
        if (StringUtils.isBlank(question)) {
            return buildInvokeFailedResult(question, requestedMode, "AI_CHAT_INTENT_ROUTE_EMPTY_QUESTION", "路由技能分析问题不能为空");
        }
        if (route == null) {
            return buildInvokeFailedResult(question, requestedMode, "AI_CHAT_INTENT_ROUTE_EMPTY_ROUTE", "当前未解析到可用模型路由");
        }
        try {
            ChatModel chatModel = aiChatModelFactoryService.resolveChatModel(route);
            return analyze(question, requestedMode, chatModel, historyMessages);
        } catch (Exception ex) {
            return buildInvokeFailedResult(question, requestedMode,
                    "AI_CHAT_INTENT_ROUTE_INVOKE_FAILED",
                    StringUtils.isNotBlank(ex.getMessage()) ? ex.getMessage() : "路由技能调用失败");
        }
    }

    @Override
    public AiChatIntentRouteVO analyze(String question, String requestedMode, ChatModel chatModel, List<AiChatMessage> historyMessages) {
        if (StringUtils.isBlank(question)) {
            return buildInvokeFailedResult(question, requestedMode, "AI_CHAT_INTENT_ROUTE_EMPTY_QUESTION", "路由技能分析问题不能为空");
        }
        if (chatModel == null) {
            return buildInvokeFailedResult(question, requestedMode, "AI_CHAT_INTENT_ROUTE_EMPTY_MODEL", "当前未准备可用模型，无法执行路由分析");
        }
        try {
            String prompt = buildPrompt(question, requestedMode, historyMessages);
            String modelResponse = chatModel.call(prompt);
            AiChatIntentRouteVO result = aiChatIntentRouteParser.parse(question, requestedMode, modelResponse);
            if (!AiChatMode.AUTO.name().equals(requestedMode)) {
                result.setMode(requestedMode);
            }
            if (StringUtils.isBlank(result.getBusinessType())) {
                result.setBusinessType("UNKNOWN");
            }
            return result;
        } catch (Exception ex) {
            return buildInvokeFailedResult(question, requestedMode,
                    "AI_CHAT_INTENT_ROUTE_INVOKE_FAILED",
                    StringUtils.isNotBlank(ex.getMessage()) ? ex.getMessage() : "路由技能调用失败");
        }
    }

    private String buildPrompt(String question, String requestedMode, List<AiChatMessage> historyMessages) {
        StringBuilder builder = new StringBuilder(
                AiPromptConstant.ROUTE_SKILL_PROMPT_TEMPLATE.formatted(
                        StringUtils.defaultIfBlank(requestedMode, AiChatMode.AUTO.name()))
        );
        appendHistoryMessages(builder, historyMessages);
        builder.append(AiPromptConstant.ROUTE_LAST_USER_MESSAGE_LABEL).append(question);
        return builder.toString();
    }

    private void appendHistoryMessages(StringBuilder builder, List<AiChatMessage> historyMessages) {
        if (historyMessages == null || historyMessages.isEmpty()) {
            return;
        }
        builder.append(AiPromptConstant.ROUTE_HISTORY_LABEL).append('\n');
        int startIndex = Math.max(0, historyMessages.size() - MAX_ROUTE_HISTORY);
        for (int index = startIndex; index < historyMessages.size(); index++) {
            AiChatMessage item = historyMessages.get(index);
            if (item == null
                    || StringUtils.isBlank(item.getMessageContent())
                    || "FAIL".equalsIgnoreCase(item.getMessageStatus())
                    || !shouldIncludeRouteHistoryMessage(item)) {
                continue;
            }
            builder.append(" - ")
                    .append("assistant".equalsIgnoreCase(item.getRoleType()) ? "助手" : "用户")
                    .append("：")
                    .append(item.getMessageContent().trim())
                    .append('\n');
        }
    }

    private boolean shouldIncludeRouteHistoryMessage(AiChatMessage item) {
        if (item == null) {
            return false;
        }
        if (!"assistant".equalsIgnoreCase(item.getRoleType())) {
            return true;
        }
        String abilityType = normalizeAbilityType(item.getAbilityType());
        if (StringUtils.isBlank(abilityType)) {
            return true;
        }
        return AiChatMode.GENERAL.name().equals(abilityType)
                || AiChatMode.PLATFORM_ASSISTANT.name().equals(abilityType);
    }

    private String normalizeAbilityType(String abilityType) {
        if (StringUtils.isBlank(abilityType)) {
            return null;
        }
        if ("GENERAL_CHAT".equalsIgnoreCase(abilityType)) {
            return AiChatMode.GENERAL.name();
        }
        return abilityType.trim().toUpperCase();
    }

    private AiChatIntentRouteVO buildInvokeFailedResult(String question, String requestedMode, String errorCode, String errorMessage) {
        AiChatIntentRouteVO result = new AiChatIntentRouteVO();
        result.setQuestion(question);
        result.setRequestedMode(requestedMode);
        result.setStructuredOutput(Boolean.FALSE);
        result.setParseStatus("FAILED");
        result.setParseErrorCode(errorCode);
        result.setParseErrorMessage(errorMessage);
        result.setBusinessType("UNKNOWN");
        result.setSubjectType("UNKNOWN");
        result.setThingModelTypeHint("UNKNOWN");
        result.setTimeIntent("UNKNOWN");
        result.setAggregateType("NONE");
        result.setNeedClarify(Boolean.FALSE);
        if (!AiChatMode.AUTO.name().equals(requestedMode)) {
            result.setMode(requestedMode);
        }
        return result;
    }
}
