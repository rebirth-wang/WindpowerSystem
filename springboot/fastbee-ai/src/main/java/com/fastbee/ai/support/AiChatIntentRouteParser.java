package com.fastbee.ai.support;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Service;

import com.fastbee.ai.model.enums.AiChatMode;
import com.fastbee.ai.model.enums.AiIntentAggregateType;
import com.fastbee.ai.model.enums.AiIntentBusinessType;
import com.fastbee.ai.model.enums.AiIntentSubjectType;
import com.fastbee.ai.model.enums.AiIntentThingModelTypeHint;
import com.fastbee.ai.model.enums.AiIntentTimeType;
import com.fastbee.ai.model.vo.AiChatIntentRouteVO;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 会话路由技能结构化解析器。
 */
@Service
public class AiChatIntentRouteParser {

    private static final Pattern JSON_CODE_BLOCK_PATTERN = Pattern.compile("(?is)```(?:json)?\\s*(\\{.+?\\})\\s*```");

    /**
     * 解析模型返回的结构化路由结果。
     *
     * @param question      用户问题
     * @param requestedMode 前端请求模式
     * @param modelResponse 模型原始返回
     * @return 路由结果
     */
    public AiChatIntentRouteVO parse(String question, String requestedMode, String modelResponse) {
        JSONObject payload = extractResponseJson(modelResponse);
        if (payload == null) {
            return buildFailedResult(question, requestedMode, modelResponse,
                    "AI_CHAT_INTENT_ROUTE_PARSE_FAILED", "路由技能未返回可解析的 JSON");
        }
        return buildStructuredResult(question, requestedMode, modelResponse, payload);
    }

    private AiChatIntentRouteVO buildStructuredResult(String question, String requestedMode, String modelResponse, JSONObject payload) {
        AiChatIntentRouteVO result = new AiChatIntentRouteVO();
        result.setQuestion(question);
        result.setRequestedMode(requestedMode);
        result.setMode(normalizeMode(payload.getString("mode")));
        result.setModeConfidence(resolveConfidence(payload.get("modeConfidence")));
        result.setBusinessType(AiIntentBusinessType.normalizeCode(payload.getString("businessType")));
        result.setSubjectType(AiIntentSubjectType.normalizeCode(payload.getString("subjectType")));
        result.setDeviceNameText(trimToNull(payload.getString("deviceNameText")));
        result.setSerialNumberText(trimToNull(payload.getString("serialNumberText")));
        result.setProductNameText(trimToNull(payload.getString("productNameText")));
        result.setThingModelText(trimToNull(payload.getString("thingModelText")));
        result.setThingModelTypeHint(AiIntentThingModelTypeHint.normalizeCode(payload.getString("thingModelTypeHint")));
        result.setActionText(trimToNull(payload.getString("actionText")));
        result.setActionValue(trimToNull(payload.getString("actionValue")));
        result.setTimeIntent(AiIntentTimeType.normalizeCode(payload.getString("timeIntent")));
        result.setAggregateType(AiIntentAggregateType.normalizeCode(payload.getString("aggregateType")));
        result.setNeedClarify(resolveBoolean(payload.get("needClarify")));
        result.setReason(trimToNull(payload.getString("reason")));
        result.setModelResponse(modelResponse);
        result.setStructuredPayload(payload.toJSONString());
        result.setStructuredOutput(Boolean.TRUE);
        result.setParseStatus(StringUtils.isNotBlank(result.getMode()) ? "SUCCESS" : "DEGRADED");
        if (StringUtils.isBlank(result.getMode())) {
            result.setParseErrorCode("AI_CHAT_INTENT_ROUTE_MISSING_MODE");
            result.setParseErrorMessage("路由技能返回了 JSON，但缺少可识别的 mode 字段");
        }
        return result;
    }

    private AiChatIntentRouteVO buildFailedResult(String question, String requestedMode, String modelResponse,
                                                  String errorCode, String errorMessage) {
        AiChatIntentRouteVO result = new AiChatIntentRouteVO();
        result.setQuestion(question);
        result.setRequestedMode(requestedMode);
        result.setMode(null);
        result.setBusinessType(AiIntentBusinessType.UNKNOWN.name());
        result.setSubjectType(AiIntentSubjectType.UNKNOWN.name());
        result.setThingModelTypeHint(AiIntentThingModelTypeHint.UNKNOWN.name());
        result.setTimeIntent(AiIntentTimeType.UNKNOWN.name());
        result.setAggregateType(AiIntentAggregateType.NONE.name());
        result.setNeedClarify(Boolean.FALSE);
        result.setModelResponse(modelResponse);
        result.setStructuredOutput(Boolean.FALSE);
        result.setParseStatus("FAILED");
        result.setParseErrorCode(errorCode);
        result.setParseErrorMessage(errorMessage);
        return result;
    }

    private JSONObject extractResponseJson(String modelResponse) {
        if (StringUtils.isBlank(modelResponse)) {
            return null;
        }
        String trimmedResponse = modelResponse.trim();
        try {
            return JSONObject.parseObject(trimmedResponse);
        } catch (Exception ignore) {
            // 继续尝试代码块和括号提取。
        }

        Matcher codeBlockMatcher = JSON_CODE_BLOCK_PATTERN.matcher(modelResponse);
        if (codeBlockMatcher.find()) {
            try {
                return JSONObject.parseObject(codeBlockMatcher.group(1).trim());
            } catch (Exception ignore) {
                // 继续尝试平衡括号提取。
            }
        }

        String balancedJson = extractBalancedJson(modelResponse);
        if (StringUtils.isBlank(balancedJson)) {
            return null;
        }
        try {
            return JSONObject.parseObject(balancedJson);
        } catch (Exception ignore) {
            return null;
        }
    }

    private String extractBalancedJson(String content) {
        if (StringUtils.isBlank(content)) {
            return null;
        }
        int start = -1;
        int depth = 0;
        boolean inDoubleQuote = false;
        boolean escaped = false;
        for (int index = 0; index < content.length(); index++) {
            char current = content.charAt(index);
            if (escaped) {
                escaped = false;
                continue;
            }
            if (current == '\\') {
                escaped = true;
                continue;
            }
            if (current == '"') {
                inDoubleQuote = !inDoubleQuote;
                continue;
            }
            if (inDoubleQuote) {
                continue;
            }
            if (current == '{') {
                if (depth == 0) {
                    start = index;
                }
                depth++;
                continue;
            }
            if (current == '}' && depth > 0) {
                depth--;
                if (depth == 0 && start >= 0) {
                    return content.substring(start, index + 1);
                }
            }
        }
        return null;
    }

    private String normalizeMode(String rawMode) {
        if (StringUtils.isBlank(rawMode)) {
            return null;
        }
        String normalized = rawMode.trim().toUpperCase(Locale.ROOT)
                .replace('-', '_')
                .replace(' ', '_');
        return switch (normalized) {
            case "PLATFORM_ASSISTANT", "PLATFORM_HELP", "平台助手", "平台问答", "平台文档" -> AiChatMode.PLATFORM_ASSISTANT.name();
            case "GENERAL", "GENERAL_CHAT", "通用对话", "普通对话", "AI对话", "AI 对话" -> AiChatMode.GENERAL.name();
            case "NL2SQL", "智能问数", "问数" -> AiChatMode.NL2SQL.name();
            case "DEVICE_CONTROL", "设备控制", "控制设备" -> AiChatMode.DEVICE_CONTROL.name();
            case "PROTOCOL_PARSE", "协议解析", "协议生成", "协议骨架生成" -> AiChatMode.PROTOCOL_PARSE.name();
            case "THING_MODEL_GENERATE", "THING_MODEL", "物模型生成", "物模型解析", "物模型导入", "生成物模型" ->
                    AiChatMode.THING_MODEL_GENERATE.name();
            case "REQUIREMENT_EVALUATION", "REQUIREMENT_ASSESSMENT", "需求评估", "需求匹配", "需求比对", "需求分析" ->
                    AiChatMode.REQUIREMENT_EVALUATION.name();
            case "AUTO", "自动识别" -> AiChatMode.AUTO.name();
            default -> null;
        };
    }

    private Double resolveConfidence(Object value) {
        if (value == null) {
            return null;
        }
        try {
            double confidence = Double.parseDouble(String.valueOf(value));
            if (confidence < 0D) {
                return 0D;
            }
            if (confidence > 1D) {
                return 1D;
            }
            return confidence;
        } catch (Exception ignore) {
            return null;
        }
    }

    private Boolean resolveBoolean(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean booleanValue) {
            return booleanValue;
        }
        String text = String.valueOf(value).trim().toLowerCase(Locale.ROOT);
        if ("true".equals(text) || "1".equals(text) || "yes".equals(text) || "是".equals(text)) {
            return Boolean.TRUE;
        }
        if ("false".equals(text) || "0".equals(text) || "no".equals(text) || "否".equals(text)) {
            return Boolean.FALSE;
        }
        return null;
    }

    private String trimToNull(String value) {
        return AiCandidateMatchSupport.stripSlotDecorators(value);
    }
}
