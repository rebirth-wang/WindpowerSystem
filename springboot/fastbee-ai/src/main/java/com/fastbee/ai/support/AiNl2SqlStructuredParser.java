package com.fastbee.ai.support;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import com.fastbee.ai.model.vo.AiNl2SqlStructuredResultVO;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 智能问数结构化解析器。
 */
@Service
public class AiNl2SqlStructuredParser {

    private static final Pattern JSON_CODE_BLOCK_PATTERN = Pattern.compile("(?is)```(?:json)?\\s*(\\{.+?\\})\\s*```");
    private static final Pattern SQL_CODE_BLOCK_PATTERN = Pattern.compile("(?is)```(?:sql)?\\s*(select\\b.+?)```");
    private static final Pattern INLINE_SQL_PATTERN = Pattern.compile("(?is)(select\\b.+?)(?:$|```|\\n\\s*\\n)");
    private static final Pattern TABLE_PATTERN = Pattern.compile("(?i)\\b(?:from|join)\\s+([`a-zA-Z0-9_\\.]+)");

    /**
     * 解析模型输出并转换为结构化问数结果。
     *
     * @param question 用户问题
     * @param modelResponse 模型原始输出
     * @return 结构化结果
     */
    public AiNl2SqlStructuredResultVO parse(String question, String modelResponse) {
        JSONObject responseJson = extractResponseJson(modelResponse);
        if (responseJson != null && StringUtils.isNotBlank(responseJson.getString("sql"))) {
            return buildStructuredResult(question, modelResponse, responseJson, true, "SUCCESS", null, null);
        }

        String fallbackSql = extractSql(modelResponse, null);
        if (StringUtils.isBlank(fallbackSql)) {
            throw new ServiceException(message("ai.nl2sql.structured.output.parse.failed"));
        }

        JSONObject fallbackPayload = new JSONObject();
        fallbackPayload.put("sql", fallbackSql);
        fallbackPayload.put("summary", message("ai.nl2sql.structured.output.fallback.summary"));
        fallbackPayload.put("confidence", 0.60D);
        fallbackPayload.put("tables", extractTables(fallbackSql));
        return buildStructuredResult(
                question,
                modelResponse,
                fallbackPayload,
                false,
                "DEGRADED",
                "NL2SQL_STRUCTURED_OUTPUT_FALLBACK",
                message("ai.nl2sql.structured.output.fallback.error")
        );
    }

    private AiNl2SqlStructuredResultVO buildStructuredResult(String question, String modelResponse, JSONObject payload,
                                                             boolean structuredOutput, String parseStatus,
                                                             String parseErrorCode, String parseErrorMessage) {
        AiNl2SqlStructuredResultVO result = new AiNl2SqlStructuredResultVO();
        result.setQuestion(question);
        result.setSql(cleanSqlCandidate(payload.getString("sql")));
        result.setSummary(ObjectUtils.defaultIfNull(payload.getString("summary"), ""));
        result.setConfidence(resolveConfidence(payload.get("confidence")));
        result.setTables(resolveTables(payload.get("tables"), result.getSql()));
        result.setModelResponse(modelResponse);
        result.setStructuredPayload(payload.toJSONString());
        result.setStructuredOutput(structuredOutput);
        result.setParseStatus(parseStatus);
        result.setParseErrorCode(parseErrorCode);
        result.setParseErrorMessage(parseErrorMessage);
        return result;
    }

    private JSONObject extractResponseJson(String modelResponse) {
        if (StringUtils.isBlank(modelResponse)) {
            return null;
        }

        List<String> jsonCandidates = new ArrayList<>();
        jsonCandidates.add(modelResponse.trim());

        Matcher jsonCodeBlockMatcher = JSON_CODE_BLOCK_PATTERN.matcher(modelResponse);
        if (jsonCodeBlockMatcher.find()) {
            jsonCandidates.add(jsonCodeBlockMatcher.group(1).trim());
        }

        String balancedJson = extractBalancedJson(modelResponse);
        if (StringUtils.isNotBlank(balancedJson)) {
            jsonCandidates.add(balancedJson);
        }

        for (String candidate : jsonCandidates) {
            if (StringUtils.isBlank(candidate)) {
                continue;
            }
            try {
                JSONObject jsonObject = JSONObject.parseObject(candidate);
                if (jsonObject != null && jsonObject.containsKey("sql")) {
                    return jsonObject;
                }
            } catch (Exception ignore) {
                // 解析失败时继续尝试其他候选。
            }
        }
        return null;
    }

    private String extractBalancedJson(String content) {
        if (StringUtils.isBlank(content)) {
            return null;
        }
        int start = -1;
        int depth = 0;
        boolean inDoubleQuote = false;
        boolean escaped = false;
        for (int i = 0; i < content.length(); i++) {
            char current = content.charAt(i);
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
                    start = i;
                }
                depth++;
                continue;
            }
            if (current == '}' && depth > 0) {
                depth--;
                if (depth == 0 && start >= 0) {
                    return content.substring(start, i + 1);
                }
            }
        }
        return null;
    }

    private String extractSql(String modelResponse, JSONObject responseJson) {
        if (StringUtils.isBlank(modelResponse)) {
            return null;
        }
        if (responseJson != null && StringUtils.isNotBlank(responseJson.getString("sql"))) {
            return cleanSqlCandidate(responseJson.getString("sql"));
        }

        Matcher codeBlockMatcher = SQL_CODE_BLOCK_PATTERN.matcher(modelResponse);
        if (codeBlockMatcher.find()) {
            return cleanSqlCandidate(codeBlockMatcher.group(1));
        }

        Matcher inlineMatcher = INLINE_SQL_PATTERN.matcher(modelResponse);
        if (inlineMatcher.find()) {
            return cleanSqlCandidate(inlineMatcher.group(1));
        }
        return null;
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

    private List<String> resolveTables(Object value, String sqlText) {
        Set<String> tables = new LinkedHashSet<>();
        if (value instanceof JSONArray jsonArray) {
            for (Object item : jsonArray) {
                if (item != null && StringUtils.isNotBlank(String.valueOf(item))) {
                    tables.add(String.valueOf(item).trim());
                }
            }
        } else if (value instanceof List<?> list) {
            for (Object item : list) {
                if (item != null && StringUtils.isNotBlank(String.valueOf(item))) {
                    tables.add(String.valueOf(item).trim());
                }
            }
        } else if (value != null && StringUtils.isNotBlank(String.valueOf(value))) {
            tables.add(String.valueOf(value).trim());
        }
        if (tables.isEmpty() && StringUtils.isNotBlank(sqlText)) {
            tables.addAll(extractTables(sqlText));
        }
        return new ArrayList<>(tables);
    }

    private List<String> extractTables(String sqlText) {
        Set<String> tables = new LinkedHashSet<>();
        if (StringUtils.isBlank(sqlText)) {
            return new ArrayList<>();
        }
        Matcher matcher = TABLE_PATTERN.matcher(sqlText);
        while (matcher.find()) {
            String tableName = matcher.group(1);
            if (StringUtils.isBlank(tableName)) {
                continue;
            }
            tables.add(tableName.replace("`", "").toLowerCase(Locale.ROOT));
        }
        return new ArrayList<>(tables);
    }

    private String cleanSqlCandidate(String sqlCandidate) {
        if (StringUtils.isBlank(sqlCandidate)) {
            return null;
        }

        String cleanedSql = sqlCandidate.trim();
        cleanedSql = cleanedSql.replace("\\r", " ").replace("\\n", " ").replace("\\t", " ");
        cleanedSql = cleanedSql.replace("\\\"", "\"");
        cleanedSql = cleanedSql.replaceAll("(?is)^```[a-zA-Z]*\\s*", "");
        cleanedSql = cleanedSql.replaceAll("(?is)\\s*```$", "");
        cleanedSql = cleanedSql.replaceAll("(?is)\"\\s*,\\s*\"summary\".*$", "");
        cleanedSql = cleanedSql.replaceAll("(?is)\"\\s*}\\s*$", "");
        cleanedSql = cleanedSql.replaceAll("\\s+", " ").trim();
        while (cleanedSql.endsWith(";")) {
            cleanedSql = cleanedSql.substring(0, cleanedSql.length() - 1).trim();
        }
        return cleanedSql;
    }
}
