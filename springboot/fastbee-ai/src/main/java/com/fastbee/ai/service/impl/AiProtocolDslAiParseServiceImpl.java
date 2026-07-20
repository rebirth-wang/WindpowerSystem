package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import com.fastbee.ai.constant.AiPromptConstant;
import com.fastbee.ai.domain.AiProtocolAdaptationTask;
import com.fastbee.ai.model.vo.AiModelRouteVO;
import com.fastbee.ai.model.vo.AiProtocolKnowledgeContextVO;
import com.fastbee.ai.service.AiChatModelFactoryService;
import com.fastbee.ai.service.AiModelRoutingService;
import com.fastbee.ai.service.IAiProtocolDslAiParseService;
import com.fastbee.ai.service.IAiProtocolKnowledgeService;
import com.fastbee.ai.support.AiReplyLanguageSupport;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 协议 DSL 解析服务实现。
 */
@Service
public class AiProtocolDslAiParseServiceImpl implements IAiProtocolDslAiParseService {

    private static final int MAX_PROMPT_TEXT_LENGTH = 28000;
    private static final int MAX_PROMPT_TABLES = 4;
    private static final int MAX_PROMPT_TABLE_ROWS = 30;
    private static final String SCHEMA_VERSION = "protocol-dsl-0.1";

    @Resource
    private AiModelRoutingService aiModelRoutingService;

    @Resource
    private AiChatModelFactoryService aiChatModelFactoryService;

    @Resource
    private IAiProtocolKnowledgeService aiProtocolKnowledgeService;

    @Override
    public JSONObject parseToDsl(AiProtocolAdaptationTask task, String sourceFileName, String contentText, JSONArray sourceTables) {
        if (StringUtils.isBlank(contentText)) {
            throw new ServiceException(message("ai.protocol.dsl.parse.text.empty"));
        }
        AiModelRouteVO route = aiModelRoutingService.resolveDefaultRoute();
        if (route == null) {
            throw new ServiceException(message("ai.protocol.dsl.parse.model.route.required"));
        }
        ChatModel chatModel = aiChatModelFactoryService.resolveChatModel(route);
        AiProtocolKnowledgeContextVO knowledgeContext = aiProtocolKnowledgeService
                .buildProtocolContext(buildKnowledgeQuestion(task, sourceFileName, contentText));
        String prompt = buildPrompt(task, sourceFileName, contentText, sourceTables, knowledgeContext);
        String modelResponse = chatModel.call(prompt);
        JSONObject dsl = parseModelDsl(modelResponse);
        normalizeDslCandidate(dsl, route);
        return dsl;
    }

    private String buildPrompt(AiProtocolAdaptationTask task, String sourceFileName, String contentText,
                               JSONArray sourceTables, AiProtocolKnowledgeContextVO knowledgeContext) {
        StringBuilder builder = new StringBuilder(AiPromptConstant.PROTOCOL_DSL_PROMPT_TEMPLATE.formatted(SCHEMA_VERSION));
        builder.append("- 任务名称：").append(trimToEmpty(task == null ? null : task.getTaskName())).append('\n');
        builder.append("- 任务协议编码：").append(trimToEmpty(task == null ? null : task.getProtocolCode())).append('\n');
        builder.append("- 任务协议名称：").append(trimToEmpty(task == null ? null : task.getProtocolName())).append('\n');
        builder.append("- 源文件：").append(defaultIfBlank(sourceFileName, "-")).append('\n');
        appendKnowledgeContext(builder, knowledgeContext);
        appendTables(builder, sourceTables);
        builder.append(AiPromptConstant.PROTOCOL_DSL_DOCUMENT_HEADER).append('\n');
        builder.append(abbreviate(contentText, MAX_PROMPT_TEXT_LENGTH));
        builder.append('\n').append(AiReplyLanguageSupport.buildModelInstruction(null, null, AiReplyLanguageSupport.currentLocale()));
        return builder.toString();
    }

    private void appendKnowledgeContext(StringBuilder builder, AiProtocolKnowledgeContextVO context) {
        if (context == null || context.getPromptLines() == null || context.getPromptLines().isEmpty()) {
            return;
        }
        builder.append(AiPromptConstant.PROTOCOL_DSL_KNOWLEDGE_HEADER).append('\n');
        for (String line : context.getPromptLines()) {
            if (StringUtils.isNotBlank(line)) {
                builder.append(line).append('\n');
            }
        }
    }

    private void appendTables(StringBuilder builder, JSONArray sourceTables) {
        if (sourceTables == null || sourceTables.isEmpty()) {
            return;
        }
        builder.append(AiPromptConstant.PROTOCOL_DSL_TABLE_HEADER).append('\n');
        int tableCount = Math.min(sourceTables.size(), MAX_PROMPT_TABLES);
        for (int index = 0; index < tableCount; index++) {
            JSONObject table = sourceTables.getJSONObject(index);
            if (table == null) {
                continue;
            }
            builder.append("# 表格 ").append(index + 1)
                    .append("：").append(defaultIfBlank(table.getString("sheetName"), "未命名")).append('\n');
            JSONArray rows = table.getJSONArray("rows");
            if (rows == null || rows.isEmpty()) {
                continue;
            }
            int rowCount = Math.min(rows.size(), MAX_PROMPT_TABLE_ROWS);
            for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
                builder.append("- ").append(rows.get(rowIndex)).append('\n');
            }
        }
    }

    private String buildKnowledgeQuestion(AiProtocolAdaptationTask task, String sourceFileName, String contentText) {
        StringBuilder builder = new StringBuilder(1200);
        if (task != null) {
            builder.append(trimToEmpty(task.getProtocolCode())).append(' ')
                    .append(trimToEmpty(task.getProtocolName())).append(' ');
        }
        builder.append(defaultIfBlank(sourceFileName, "")).append(' ');
        builder.append(abbreviate(contentText, 800));
        return builder.toString();
    }

    private JSONObject parseModelDsl(String modelResponse) {
        if (StringUtils.isBlank(modelResponse)) {
            throw new ServiceException(message("ai.protocol.dsl.parse.response.empty"));
        }
        String candidate = extractJsonCandidate(modelResponse);
        if (StringUtils.isBlank(candidate)) {
            throw new ServiceException(message("ai.protocol.dsl.parse.invalid.json"));
        }
        try {
            JSONObject parsed = JSON.parseObject(candidate);
            JSONObject dsl = unwrapDslObject(parsed);
            if (!isDslCandidate(dsl)) {
                throw new ServiceException(message("ai.protocol.dsl.parse.required.structure.missing"));
            }
            return dsl;
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(message("ai.protocol.dsl.parse.json.failed", ex.getMessage()));
        }
    }

    private JSONObject unwrapDslObject(JSONObject parsed) {
        if (parsed == null) {
            return null;
        }
        JSONObject protocolDsl = parsed.getJSONObject("protocolDsl");
        if (protocolDsl != null) {
            return protocolDsl;
        }
        JSONObject dsl = parsed.getJSONObject("dsl");
        return dsl == null ? parsed : dsl;
    }

    private boolean isDslCandidate(JSONObject candidate) {
        if (candidate == null || candidate.getJSONObject("protocol") == null) {
            return false;
        }
        return hasNonEmptyArray(candidate, "messageTypes")
                || hasNonEmptyArray(candidate, "fields")
                || hasNonEmptyArray(candidate, "codecRules")
                || hasNonEmptyArray(candidate, "sampleFrames");
    }

    private boolean hasNonEmptyArray(JSONObject candidate, String key) {
        JSONArray array = candidate.getJSONArray(key);
        return array != null && !array.isEmpty();
    }

    private void normalizeDslCandidate(JSONObject dsl, AiModelRouteVO route) {
        dsl.put("schemaVersion", defaultIfBlank(dsl.getString("schemaVersion"), SCHEMA_VERSION));
        ensureJsonObject(dsl, "protocol");
        ensureJsonArray(dsl, "messageTypes");
        ensureJsonArray(dsl, "fields");
        ensureJsonArray(dsl, "codecRules");
        ensureJsonArray(dsl, "thingModelMappings");
        ensureJsonArray(dsl, "sampleFrames");
        ensureJsonArray(dsl, "qualityIssues");
        JSONObject strategy = ensureJsonObject(dsl, "generationStrategy");
        strategy.put("target", defaultIfBlank(strategy.getString("target"), "fastbee-protocol"));
        if (!strategy.containsKey("requiresManualConfirmation")) {
            strategy.put("requiresManualConfirmation", true);
        }
        if (!strategy.containsKey("allowCodeGeneration")) {
            strategy.put("allowCodeGeneration", hasCodeGenerationMinimum(dsl));
        }
        JSONObject aiMeta = ensureJsonObject(dsl, "aiMeta");
        aiMeta.put("modelCode", route == null ? "" : route.getModelCode());
        aiMeta.put("providerCode", route == null ? "" : route.getProviderCode());
        Double confidence = readDouble(aiMeta, "confidence");
        if (confidence == null) {
            confidence = readDouble(dsl, "confidence");
        }
        if (confidence == null) {
            confidence = 0.70D;
        }
        aiMeta.put("confidence", Math.max(0.0D, Math.min(confidence, 1.0D)));
        if (confidence < 0.60D) {
            addQualityIssue(dsl, "WARNING", "AI_CONFIDENCE_LOW", "AI 协议解析置信度较低，建议导出企业工作簿人工复核");
        }
    }

    private JSONObject ensureJsonObject(JSONObject root, String key) {
        JSONObject object = root.getJSONObject(key);
        if (object == null) {
            object = new JSONObject();
            root.put(key, object);
        }
        return object;
    }

    private void ensureJsonArray(JSONObject root, String key) {
        JSONArray array = root.getJSONArray(key);
        if (array == null) {
            root.put(key, new JSONArray());
        }
    }

    private void addQualityIssue(JSONObject dsl, String level, String code, String message) {
        JSONArray issues = dsl.getJSONArray("qualityIssues");
        if (issues == null) {
            issues = new JSONArray();
            dsl.put("qualityIssues", issues);
        }
        JSONObject issue = new JSONObject();
        issue.put("level", level);
        issue.put("code", code);
        issue.put("message", message);
        issues.add(issue);
    }

    private boolean hasCodeGenerationMinimum(JSONObject dsl) {
        if (dsl == null || dsl.getJSONObject("protocol") == null) {
            return false;
        }
        return hasNonEmptyArray(dsl, "messageTypes")
                && hasNonEmptyArray(dsl, "fields")
                && hasNonEmptyArray(dsl, "codecRules");
    }

    private Double readDouble(JSONObject object, String key) {
        if (object == null || StringUtils.isBlank(key)) {
            return null;
        }
        Object value = object.get(key);
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value instanceof String text && StringUtils.isNotBlank(text)) {
            try {
                return Double.parseDouble(text.trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private String extractJsonCandidate(String modelResponse) {
        String text = modelResponse.trim();
        String fenced = extractFencedJson(text);
        if (StringUtils.isNotBlank(fenced)) {
            return fenced;
        }
        int start = text.indexOf('{');
        if (start < 0) {
            return null;
        }
        return extractBalancedJsonObject(text, start);
    }

    private String extractFencedJson(String text) {
        int fenceStart = text.indexOf("```");
        while (fenceStart >= 0) {
            int contentStart = text.indexOf('\n', fenceStart + 3);
            if (contentStart < 0) {
                return null;
            }
            int fenceEnd = text.indexOf("```", contentStart + 1);
            if (fenceEnd < 0) {
                return null;
            }
            String block = text.substring(contentStart + 1, fenceEnd).trim();
            if (block.startsWith("{")) {
                return block;
            }
            fenceStart = text.indexOf("```", fenceEnd + 3);
        }
        return null;
    }

    private String extractBalancedJsonObject(String text, int objectStart) {
        int depth = 0;
        boolean inString = false;
        boolean escape = false;
        for (int index = objectStart; index < text.length(); index++) {
            char ch = text.charAt(index);
            if (inString) {
                if (escape) {
                    escape = false;
                } else if (ch == '\\') {
                    escape = true;
                } else if (ch == '"') {
                    inString = false;
                }
                continue;
            }
            if (ch == '"') {
                inString = true;
            } else if (ch == '{') {
                depth++;
            } else if (ch == '}') {
                depth--;
                if (depth == 0) {
                    return text.substring(objectStart, index + 1);
                }
            }
        }
        return null;
    }

    private String abbreviate(String text, int maxLength) {
        if (StringUtils.isBlank(text)) {
            return "";
        }
        String actual = text.trim();
        if (actual.length() <= maxLength) {
            return actual;
        }
        return actual.substring(0, Math.max(maxLength - 30, 0)) + "\n...[内容已截断]";
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value.trim();
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
