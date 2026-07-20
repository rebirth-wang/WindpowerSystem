package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.fastbee.ai.constant.AiPromptConstant;
import com.fastbee.ai.model.vo.AiModelRouteVO;
import com.fastbee.ai.model.vo.AiThingModelGenerateResultVO;
import com.fastbee.ai.service.AiChatModelFactoryService;
import com.fastbee.ai.service.AiModelRoutingService;
import com.fastbee.ai.service.IAiThingModelGenerateService;
import com.fastbee.ai.support.AiReplyLanguageSupport;
import com.fastbee.ai.support.AiThingModelWorkbookSupport;
import com.fastbee.common.config.RuoYiConfig;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.file.FileUtils;

/**
 * AI 物模型生成服务实现。
 */
@Service
public class AiThingModelGenerateServiceImpl implements IAiThingModelGenerateService {

    private static final int MAX_PROMPT_TEXT_LENGTH = 28000;
    private static final int PREVIEW_ROW_LIMIT = 20;
    private static final Pattern ARTIFACT_CODE_PATTERN = Pattern.compile("^\\d{8}_[A-Za-z0-9]{32}$");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Resource
    private AiModelRoutingService aiModelRoutingService;

    @Resource
    private AiChatModelFactoryService aiChatModelFactoryService;

    @Resource
    private AiThingModelWorkbookSupport aiThingModelWorkbookSupport;

    @Override
    public AiThingModelGenerateResultVO generateFromText(String sourceFileName,
                                                         String contentType,
                                                         String contentText,
                                                         String requirement,
                                                         AiModelRouteVO route) {
        if (StringUtils.isBlank(contentText)) {
            throw new ServiceException(message("ai.thing.model.generate.text.required"));
        }
        AiModelRouteVO actualRoute = route == null ? aiModelRoutingService.resolveDefaultRoute() : route;
        if (actualRoute == null) {
            throw new ServiceException(message("ai.thing.model.generate.model.route.required"));
        }
        ChatModel chatModel = aiChatModelFactoryService.resolveChatModel(actualRoute);
        String prompt = AiPromptConstant.THING_MODEL_GENERATE_PROMPT_TEMPLATE.formatted(
                defaultIfBlank(requirement, "请从文件中提取设备属性、功能或事件，并生成 FastBee 物模型导入模板"),
                defaultIfBlank(sourceFileName, AiPromptConstant.THING_MODEL_FILE_FALLBACK_NAME),
                defaultIfBlank(contentType, "-"),
                abbreviate(contentText, MAX_PROMPT_TEXT_LENGTH)
        );
        prompt = AiReplyLanguageSupport.appendModelInstruction(prompt, requirement, null, AiReplyLanguageSupport.currentLocale());
        String modelResponse = chatModel.call(prompt);
        JSONObject parsed = parseModelResult(modelResponse);
        JSONArray qualityIssues = normalizeIssueArray(parsed.getJSONArray("qualityIssues"));
        JSONArray rawMappings = parsed.getJSONArray("thingModelMappings");
        JSONArray sanitizedRows = sanitizeGeneratedRows(rawMappings, qualityIssues);
        JSONArray workbookRows = aiThingModelWorkbookSupport.normalizeThingModelMappingsForWorkbook(sanitizedRows);
        if (workbookRows.isEmpty()) {
            throw new ServiceException(message("ai.thing.model.generate.no.rows"));
        }

        try {
            byte[] workbookBytes = aiThingModelWorkbookSupport.buildThingModelImportWorkbookBytes(workbookRows);
            AiThingModelGenerateResultVO result = buildResult(sourceFileName, parsed, workbookRows, qualityIssues, actualRoute);
            saveArtifact(result, parsed, workbookRows, workbookBytes);
            return result;
        } catch (IOException ex) {
            throw new ServiceException(message("ai.thing.model.generate.workbook.build.failed", ex.getMessage()));
        }
    }

    @Override
    public void downloadWorkbook(String artifactCode, HttpServletResponse response) {
        if (StringUtils.isBlank(artifactCode) || !ARTIFACT_CODE_PATTERN.matcher(artifactCode).matches()) {
            throw new ServiceException(message("ai.thing.model.generate.artifact.code.invalid"));
        }
        String datePart = artifactCode.substring(0, 8);
        Path root = Paths.get(RuoYiConfig.getProfile(), "ai", "thing-model", "generated").toAbsolutePath().normalize();
        Path filePath = root.resolve(datePart).resolve(artifactCode + ".xlsx").normalize();
        if (!filePath.startsWith(root)) {
            throw new ServiceException(message("ai.thing.model.generate.artifact.path.invalid"));
        }
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            throw new ServiceException(message("ai.thing.model.generate.artifact.not.exists"));
        }
        String downloadName = resolveArtifactDownloadName(root.resolve(datePart).resolve(artifactCode + ".json"), filePath);
        try {
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, downloadName);
            FileUtils.writeBytes(filePath.toString(), response.getOutputStream());
        } catch (IOException ex) {
            throw new ServiceException(message("ai.thing.model.generate.artifact.download.failed", ex.getMessage()));
        }
    }

    private AiThingModelGenerateResultVO buildResult(String sourceFileName,
                                                     JSONObject parsed,
                                                     JSONArray workbookRows,
                                                     JSONArray qualityIssues,
                                                     AiModelRouteVO route) {
        AiThingModelGenerateResultVO result = new AiThingModelGenerateResultVO();
        JSONObject aiMeta = parsed == null ? null : parsed.getJSONObject("aiMeta");
        result.setStatus("GENERATED");
        result.setSummary(defaultIfBlank(aiMeta == null ? null : aiMeta.getString("summary"),
                "已从文件中识别到 " + workbookRows.size() + " 条物模型，并生成 FastBee 物模型导入模板。"));
        result.setSourceFileName(defaultIfBlank(sourceFileName, AiPromptConstant.THING_MODEL_FILE_FALLBACK_NAME));
        result.setRowCount(workbookRows.size());
        result.setConfidence(readDouble(aiMeta == null ? null : aiMeta.get("confidence")));
        result.setModelCode(route == null ? "" : route.getModelCode());
        result.setProviderCode(route == null ? "" : route.getProviderCode());
        result.setQualityIssues(toStringList(qualityIssues));
        result.setMissingInformation(toStringList(parsed == null ? null : parsed.getJSONArray("missingInformation")));
        result.setPreviewRows(buildPreviewRows(workbookRows));
        result.setKeyPoints(buildKeyPoints(result, workbookRows));
        return result;
    }

    private void saveArtifact(AiThingModelGenerateResultVO result,
                              JSONObject parsed,
                              JSONArray workbookRows,
                              byte[] workbookBytes) throws IOException {
        String datePart = LocalDate.now().format(DATE_FORMATTER);
        String artifactCode = datePart + "_" + UUID.randomUUID().toString().replace("-", "");
        String artifactName = buildArtifactName(result.getSourceFileName());
        Path targetDir = Paths.get(RuoYiConfig.getProfile(), "ai", "thing-model", "generated", datePart)
                .toAbsolutePath().normalize();
        Files.createDirectories(targetDir);
        Path workbookPath = targetDir.resolve(artifactCode + ".xlsx").normalize();
        Path metadataPath = targetDir.resolve(artifactCode + ".json").normalize();
        Files.write(workbookPath, workbookBytes);

        JSONObject metadata = new JSONObject();
        metadata.put("artifactCode", artifactCode);
        metadata.put("artifactName", artifactName);
        metadata.put("sourceFileName", result.getSourceFileName());
        metadata.put("rowCount", workbookRows.size());
        metadata.put("generatedAt", LocalDateTime.now().format(DATE_TIME_FORMATTER));
        metadata.put("summary", result.getSummary());
        metadata.put("modelCode", result.getModelCode());
        metadata.put("providerCode", result.getProviderCode());
        metadata.put("workbookFileName", workbookPath.getFileName().toString());
        metadata.put("qualityIssues", result.getQualityIssues());
        metadata.put("missingInformation", result.getMissingInformation());
        metadata.put("aiMeta", parsed == null ? null : parsed.getJSONObject("aiMeta"));
        Files.writeString(metadataPath, JSON.toJSONString(metadata), StandardCharsets.UTF_8);

        result.setArtifactCode(artifactCode);
        result.setArtifactName(artifactName);
        result.setArtifactSize((long) workbookBytes.length);
        result.setArtifactRelativePath(Paths.get("ai", "thing-model", "generated", datePart, workbookPath.getFileName().toString())
                .toString().replace("\\", "/"));
        result.setKeyPoints(buildKeyPoints(result, workbookRows));
    }

    private JSONArray sanitizeGeneratedRows(JSONArray rawMappings, JSONArray qualityIssues) {
        JSONArray rows = new JSONArray();
        JSONArray actualMappings = rawMappings == null ? new JSONArray() : rawMappings;
        for (int index = 0; index < actualMappings.size(); index++) {
            Object item = actualMappings.get(index);
            if (!(item instanceof JSONObject source)) {
                continue;
            }
            JSONObject row = new JSONObject();
            row.putAll(source);
            String modelName = getFirstNonBlankString(row, "modelName", "displayName", "modelName_zh_CN", "物模型名称");
            String identifier = sanitizeIdentifier(getFirstNonBlankString(row, "identifier", "modelName_en_US", "fieldCode", "fieldName"));
            if (StringUtils.isBlank(modelName) && StringUtils.isBlank(identifier)) {
                continue;
            }
            if (StringUtils.isBlank(identifier)) {
                identifier = "tm_" + (index + 1);
                addQualityIssue(qualityIssues, "第 " + (index + 1) + " 行缺少可用标识符，已临时使用 " + identifier + "，建议人工复核");
            }
            row.put("identifier", identifier);
            if (StringUtils.isBlank(row.getString("modelName"))) {
                row.put("modelName", StringUtils.isBlank(modelName) ? identifier : modelName);
            }
            if (StringUtils.isBlank(row.getString("modelName_en_US"))) {
                row.put("modelName_en_US", identifier);
            }
            String datatype = aiThingModelWorkbookSupport.normalizeThingModelDataType(
                    getFirstNonBlankString(row, "datatype", "dataType", "valueType", "数据类型"));
            if (StringUtils.isBlank(datatype)) {
                datatype = "string";
                addQualityIssue(qualityIssues, "物模型“" + row.getString("modelName") + "”缺少明确数据类型，已按 string 输出，建议人工复核");
            }
            row.put("datatype", datatype);
            if (StringUtils.isBlank(row.getString("modelOrder"))) {
                row.put("modelOrder", String.valueOf(index + 1));
            }
            rows.add(row);
        }
        return rows;
    }

    private String sanitizeIdentifier(String value) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        String normalized = value.trim()
                .replaceAll("([a-z0-9])([A-Z])", "$1_$2")
                .replaceAll("[^A-Za-z0-9_]+", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_+|_+$", "")
                .toLowerCase(Locale.ROOT);
        if (StringUtils.isBlank(normalized)) {
            return "";
        }
        if (Character.isDigit(normalized.charAt(0))) {
            return "tm_" + normalized;
        }
        return normalized;
    }

    private List<String> buildKeyPoints(AiThingModelGenerateResultVO result, JSONArray rows) {
        List<String> keyPoints = new ArrayList<>();
        keyPoints.add("源文件：" + result.getSourceFileName());
        keyPoints.add("物模型数量：" + result.getRowCount());
        if (result.getConfidence() != null) {
            keyPoints.add("AI 置信度：" + Math.round(result.getConfidence() * 100) + "%");
        }
        long propertyCount = countByType(rows, "属性");
        long functionCount = countByType(rows, "功能");
        long eventCount = countByType(rows, "事件");
        keyPoints.add("模型类别：属性 " + propertyCount + " / 功能 " + functionCount + " / 事件 " + eventCount);
        if (StringUtils.isNotBlank(result.getArtifactName())) {
            keyPoints.add("下载文件：" + result.getArtifactName());
        }
        return keyPoints;
    }

    private long countByType(JSONArray rows, String typeText) {
        long count = 0;
        JSONArray actualRows = rows == null ? new JSONArray() : rows;
        for (int index = 0; index < actualRows.size(); index++) {
            JSONObject row = actualRows.getJSONObject(index);
            if (row != null && typeText.equals(row.getString("typeStr"))) {
                count++;
            }
        }
        return count;
    }

    private List<LinkedHashMap<String, Object>> buildPreviewRows(JSONArray workbookRows) {
        List<LinkedHashMap<String, Object>> previewRows = new ArrayList<>();
        JSONArray rows = workbookRows == null ? new JSONArray() : workbookRows;
        int limit = Math.min(rows.size(), PREVIEW_ROW_LIMIT);
        for (int index = 0; index < limit; index++) {
            JSONObject row = rows.getJSONObject(index);
            if (row == null) {
                continue;
            }
            LinkedHashMap<String, Object> preview = new LinkedHashMap<>();
            preview.put("物模型名称", row.getString("modelName"));
            preview.put("标识符", row.getString("identifier"));
            preview.put("数据类型", row.getString("datatype"));
            preview.put("单位", row.getString("unit"));
            preview.put("有效值范围", row.getString("limitValue"));
            preview.put("模型类别", row.getString("typeStr"));
            previewRows.add(preview);
        }
        return previewRows;
    }

    private JSONObject parseModelResult(String modelResponse) {
        if (StringUtils.isBlank(modelResponse)) {
            throw new ServiceException(message("ai.thing.model.generate.ai.empty"));
        }
        String candidate = extractJsonCandidate(modelResponse);
        if (StringUtils.isBlank(candidate)) {
            throw new ServiceException(message("ai.thing.model.generate.ai.json.invalid"));
        }
        try {
            JSONObject parsed = JSON.parseObject(candidate);
            JSONObject result = unwrapResultObject(parsed);
            if (result == null || result.getJSONArray("thingModelMappings") == null) {
                throw new ServiceException(message("ai.thing.model.generate.ai.mapping.required"));
            }
            return result;
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ServiceException(message("ai.thing.model.generate.ai.json.parse.failed", ex.getMessage()));
        }
    }

    private JSONObject unwrapResultObject(JSONObject parsed) {
        if (parsed == null) {
            return null;
        }
        JSONObject thingModelGenerate = parsed.getJSONObject("thingModelGenerate");
        if (thingModelGenerate != null) {
            return thingModelGenerate;
        }
        JSONObject result = parsed.getJSONObject("result");
        return result == null ? parsed : result;
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

    private JSONArray normalizeIssueArray(JSONArray array) {
        JSONArray result = new JSONArray();
        if (array == null || array.isEmpty()) {
            return result;
        }
        for (Object item : array) {
            if (item == null) {
                continue;
            }
            if (item instanceof JSONObject object) {
                String message = getFirstNonBlankString(object, "message", "description", "reason", "issue");
                if (StringUtils.isNotBlank(message)) {
                    result.add(message);
                }
            } else if (StringUtils.isNotBlank(String.valueOf(item))) {
                result.add(String.valueOf(item).trim());
            }
        }
        return result;
    }

    private void addQualityIssue(JSONArray issues, String message) {
        if (issues == null || StringUtils.isBlank(message) || issues.contains(message)) {
            return;
        }
        issues.add(message);
    }

    private List<String> toStringList(JSONArray array) {
        List<String> list = new ArrayList<>();
        if (array == null || array.isEmpty()) {
            return list;
        }
        for (Object item : array) {
            if (item == null) {
                continue;
            }
            if (item instanceof JSONObject object) {
                String message = getFirstNonBlankString(object, "message", "description", "reason", "issue");
                if (StringUtils.isNotBlank(message)) {
                    list.add(message);
                }
            } else if (StringUtils.isNotBlank(String.valueOf(item))) {
                list.add(String.valueOf(item).trim());
            }
        }
        return list;
    }

    private String resolveArtifactDownloadName(Path metadataPath, Path fallbackFilePath) {
        if (metadataPath != null && Files.exists(metadataPath)) {
            try {
                JSONObject metadata = JSON.parseObject(Files.readString(metadataPath, StandardCharsets.UTF_8));
                String artifactName = metadata == null ? null : metadata.getString("artifactName");
                if (StringUtils.isNotBlank(artifactName)) {
                    return artifactName;
                }
            } catch (Exception ignored) {
                // 元数据损坏时仍允许按文件名下载。
            }
        }
        return fallbackFilePath == null ? "物模型导入模板.xlsx" : fallbackFilePath.getFileName().toString();
    }

    private String buildArtifactName(String sourceFileName) {
        String baseName = defaultIfBlank(sourceFileName, "设备物模型");
        int slashIndex = Math.max(baseName.lastIndexOf('/'), baseName.lastIndexOf('\\'));
        if (slashIndex >= 0) {
            baseName = baseName.substring(slashIndex + 1);
        }
        baseName = baseName.replaceFirst("\\.[^.]+$", "").replaceAll("[\\\\/:*?\"<>|\\s]+", "_");
        if (StringUtils.isBlank(baseName)) {
            baseName = "设备物模型";
        }
        if (baseName.length() > 60) {
            baseName = baseName.substring(0, 60);
        }
        return baseName + "_物模型导入模板.xlsx";
    }

    private Double readDouble(Object value) {
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

    private String getFirstNonBlankString(JSONObject object, String... keys) {
        if (object == null || keys == null) {
            return "";
        }
        for (String key : keys) {
            Object value = object.get(key);
            if (value == null) {
                continue;
            }
            String text = value instanceof JSONObject || value instanceof JSONArray
                    ? JSON.toJSONString(value)
                    : String.valueOf(value);
            if (StringUtils.isNotBlank(text)) {
                return text.trim();
            }
        }
        return "";
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
}
