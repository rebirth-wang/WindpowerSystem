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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.fastbee.ai.constant.AiPromptConstant;
import com.fastbee.ai.model.vo.AiCodebaseGuideContextVO;
import com.fastbee.ai.model.vo.AiModelRouteVO;
import com.fastbee.ai.model.vo.AiPlatformDocKnowledgeContextVO;
import com.fastbee.ai.model.vo.AiRequirementEvaluationResultVO;
import com.fastbee.ai.service.AiChatModelFactoryService;
import com.fastbee.ai.service.AiModelRoutingService;
import com.fastbee.ai.service.IAiCodebaseGuideKnowledgeService;
import com.fastbee.ai.service.IAiPlatformDocKnowledgeService;
import com.fastbee.ai.service.IAiRequirementEvaluationService;
import com.fastbee.ai.support.AiCodebaseAnswerSanitizer;
import com.fastbee.ai.support.AiReplyLanguageSupport;
import com.fastbee.ai.support.AiRequirementEvaluationReportSupport;
import com.fastbee.common.config.RuoYiConfig;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.file.FileUtils;

/**
 * AI 需求评估服务实现。
 */
@Service
public class AiRequirementEvaluationServiceImpl implements IAiRequirementEvaluationService {

    private static final Logger log = LoggerFactory.getLogger(AiRequirementEvaluationServiceImpl.class);

    private static final int MAX_PROMPT_TEXT_LENGTH = 30000;
    private static final int MAX_SEARCH_TEXT_LENGTH = 1800;
    private static final int MAX_KNOWLEDGE_LINES = 18;
    private static final int MAX_REQUIREMENT_ITEMS = 12;
    private static final Pattern ARTIFACT_CODE_PATTERN = Pattern.compile("^\\d{8}_[A-Za-z0-9]{32}$");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Resource
    private AiModelRoutingService aiModelRoutingService;

    @Resource
    private AiChatModelFactoryService aiChatModelFactoryService;

    @Resource
    private IAiPlatformDocKnowledgeService aiPlatformDocKnowledgeService;

    @Resource
    private IAiCodebaseGuideKnowledgeService aiCodebaseGuideKnowledgeService;

    @Resource
    private AiRequirementEvaluationReportSupport aiRequirementEvaluationReportSupport;

    @Override
    public AiRequirementEvaluationResultVO evaluateFromText(String sourceFileName,
                                                            String contentType,
                                                            String contentText,
                                                            byte[] sourceFileBytes,
                                                            String requirement,
                                                            AiModelRouteVO route) {
        if (StringUtils.isBlank(contentText)) {
            throw new ServiceException(message("ai.requirement.evaluation.text.required"));
        }
        AiModelRouteVO actualRoute = route == null ? aiModelRoutingService.resolveDefaultRoute() : route;
        if (actualRoute == null) {
            throw new ServiceException(message("ai.requirement.evaluation.model.route.required"));
        }
        String actualRequirement = defaultIfBlank(requirement, "请结合平台能力评估这份需求文件，输出匹配结果、差距建议和待确认问题");
        String searchQuestion = buildKnowledgeSearchQuestion(sourceFileName, contentText, actualRequirement);
        AiPlatformDocKnowledgeContextVO platformContext = resolvePlatformContext(searchQuestion);
        AiCodebaseGuideContextVO codebaseContext = shouldUseCodebaseContext(searchQuestion)
                ? resolveCodebaseContext(searchQuestion)
                : null;

        ChatModel chatModel = aiChatModelFactoryService.resolveChatModel(actualRoute);
        String prompt = AiPromptConstant.REQUIREMENT_EVALUATION_PROMPT_TEMPLATE.formatted(
                actualRequirement,
                defaultIfBlank(sourceFileName, AiPromptConstant.REQUIREMENT_FILE_FALLBACK_NAME),
                defaultIfBlank(contentType, "-"),
                buildContextLines(platformContext == null ? null : platformContext.getPromptLines(), "未命中可直接引用的平台文档知识"),
                buildContextLines(codebaseContext == null ? null : codebaseContext.getPromptLines(), "本轮未启用或未命中源码导航安全摘要"),
                abbreviate(contentText, MAX_PROMPT_TEXT_LENGTH)
        );
        prompt = AiReplyLanguageSupport.appendModelInstruction(prompt, requirement, null, AiReplyLanguageSupport.currentLocale());
        String modelResponse = chatModel.call(prompt);
        AiRequirementEvaluationResultVO result = parseModelResult(modelResponse, sourceFileName, actualRoute);
        normalizeResult(result, sourceFileName, actualRoute, platformContext, codebaseContext);
        saveReportArtifact(result, sourceFileName, contentType, sourceFileBytes);
        return result;
    }

    @Override
    public void downloadReport(String artifactCode, HttpServletResponse response) {
        if (StringUtils.isBlank(artifactCode) || !ARTIFACT_CODE_PATTERN.matcher(artifactCode).matches()) {
            throw new ServiceException(message("ai.requirement.evaluation.artifact.code.invalid"));
        }
        String datePart = artifactCode.substring(0, 8);
        Path root = Paths.get(RuoYiConfig.getProfile(), "ai", "requirement-evaluation", "generated")
                .toAbsolutePath().normalize();
        Path metadataPath = root.resolve(datePart).resolve(artifactCode + ".json").normalize();
        if (!metadataPath.startsWith(root)) {
            throw new ServiceException(message("ai.requirement.evaluation.artifact.path.invalid"));
        }
        JSONObject metadata = readArtifactMetadata(metadataPath);
        String artifactFileName = metadata.getString("artifactFileName");
        if (StringUtils.isBlank(artifactFileName)) {
            artifactFileName = resolveFallbackArtifactFileName(root.resolve(datePart), artifactCode);
        }
        Path filePath = root.resolve(datePart).resolve(artifactFileName).normalize();
        if (!filePath.startsWith(root)) {
            throw new ServiceException(message("ai.requirement.evaluation.artifact.path.invalid"));
        }
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            throw new ServiceException(message("ai.requirement.evaluation.artifact.not.exists"));
        }
        String downloadName = defaultIfBlank(metadata.getString("artifactName"), filePath.getFileName().toString());
        try {
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            FileUtils.setAttachmentResponseHeader(response, downloadName);
            FileUtils.writeBytes(filePath.toString(), response.getOutputStream());
        } catch (IOException ex) {
            throw new ServiceException(message("ai.requirement.evaluation.artifact.download.failed", ex.getMessage()));
        }
    }

    private void saveReportArtifact(AiRequirementEvaluationResultVO result,
                                    String sourceFileName,
                                    String contentType,
                                    byte[] sourceFileBytes) {
        if (result == null) {
            return;
        }
        try {
            AiRequirementEvaluationReportSupport.GeneratedEvaluationArtifact artifact =
                    aiRequirementEvaluationReportSupport.buildEvaluationArtifact(sourceFileName, contentType, sourceFileBytes, result);
            if (artifact == null || artifact.bytes() == null || artifact.bytes().length == 0) {
                return;
            }
            String datePart = LocalDate.now().format(DATE_FORMATTER);
            String artifactCode = datePart + "_" + UUID.randomUUID().toString().replace("-", "");
            String extension = defaultIfBlank(artifact.fileExtension(), ".docx");
            String artifactFileName = artifactCode + extension;
            Path targetDir = Paths.get(RuoYiConfig.getProfile(), "ai", "requirement-evaluation", "generated", datePart)
                    .toAbsolutePath().normalize();
            Files.createDirectories(targetDir);
            Path artifactPath = targetDir.resolve(artifactFileName).normalize();
            Path metadataPath = targetDir.resolve(artifactCode + ".json").normalize();
            Files.write(artifactPath, artifact.bytes());

            JSONObject metadata = new JSONObject();
            metadata.put("artifactCode", artifactCode);
            metadata.put("artifactName", artifact.artifactName());
            metadata.put("artifactType", artifact.artifactType());
            metadata.put("artifactFileName", artifactFileName);
            metadata.put("sourceFileName", result.getSourceFileName());
            metadata.put("generatedAt", LocalDateTime.now().format(DATE_TIME_FORMATTER));
            metadata.put("summary", result.getSummary());
            metadata.put("matchLevel", result.getMatchLevel());
            metadata.put("modelCode", result.getModelCode());
            metadata.put("providerCode", result.getProviderCode());
            metadata.put("requirementItemCount", result.getRequirementItems() == null ? 0 : result.getRequirementItems().size());
            Files.writeString(metadataPath, JSON.toJSONString(metadata), StandardCharsets.UTF_8);

            result.setArtifactCode(artifactCode);
            result.setArtifactName(artifact.artifactName());
            result.setArtifactSize((long) artifact.bytes().length);
            result.setArtifactType(artifact.artifactType());
            result.setArtifactRelativePath(Paths.get("ai", "requirement-evaluation", "generated", datePart, artifactFileName)
                    .toString().replace("\\", "/"));
            appendArtifactKeyPoint(result);
        } catch (Exception ex) {
            log.warn("生成需求评估结果文件失败，sourceFileName={}", sourceFileName, ex);
        }
    }

    private void appendArtifactKeyPoint(AiRequirementEvaluationResultVO result) {
        if (result == null || StringUtils.isBlank(result.getArtifactName())) {
            return;
        }
        List<String> keyPoints = result.getKeyPoints() == null ? new ArrayList<>() : new ArrayList<>(result.getKeyPoints());
        String artifactPoint = "下载文件：" + result.getArtifactName();
        if (!keyPoints.contains(artifactPoint)) {
            keyPoints.add(artifactPoint);
        }
        result.setKeyPoints(keyPoints);
    }

    private JSONObject readArtifactMetadata(Path metadataPath) {
        if (metadataPath == null || !Files.exists(metadataPath) || !Files.isRegularFile(metadataPath)) {
            return new JSONObject();
        }
        try {
            return JSON.parseObject(Files.readString(metadataPath, StandardCharsets.UTF_8));
        } catch (Exception ex) {
            return new JSONObject();
        }
    }

    private String resolveFallbackArtifactFileName(Path targetDir, String artifactCode) {
        String[] extensions = {".xlsx", ".xls", ".docx"};
        for (String extension : extensions) {
            Path path = targetDir.resolve(artifactCode + extension).normalize();
            if (Files.exists(path) && Files.isRegularFile(path)) {
                return path.getFileName().toString();
            }
        }
        return artifactCode + ".docx";
    }

    private AiRequirementEvaluationResultVO parseModelResult(String modelResponse,
                                                            String sourceFileName,
                                                            AiModelRouteVO route) {
        String candidate = extractJsonCandidate(modelResponse);
        if (StringUtils.isBlank(candidate)) {
            return buildFallbackResult(modelResponse, sourceFileName, route);
        }
        try {
            JSONObject parsed = JSON.parseObject(candidate);
            JSONObject result = unwrapResultObject(parsed);
            if (result == null) {
                return buildFallbackResult(modelResponse, sourceFileName, route);
            }
            return buildResultFromJson(result, sourceFileName, route);
        } catch (Exception ex) {
            return buildFallbackResult(modelResponse, sourceFileName, route);
        }
    }

    private AiRequirementEvaluationResultVO buildResultFromJson(JSONObject parsed,
                                                                String sourceFileName,
                                                                AiModelRouteVO route) {
        AiRequirementEvaluationResultVO result = new AiRequirementEvaluationResultVO();
        JSONObject aiMeta = parsed.getJSONObject("aiMeta");
        result.setStatus(defaultIfBlank(parsed.getString("status"), "COMPLETED"));
        result.setOverallConclusion(sanitizeText(getFirstNonBlankString(parsed, "overallConclusion", "conclusion")));
        result.setMatchLevel(normalizeMatchLevel(parsed.getString("matchLevel")));
        result.setSummary(sanitizeText(getFirstNonBlankString(parsed, "summary", "overallConclusion", "conclusion")));
        result.setSourceFileName(defaultIfBlank(sourceFileName, AiPromptConstant.REQUIREMENT_FILE_FALLBACK_NAME));
        result.setConfidence(readDouble(aiMeta == null ? parsed.get("confidence") : aiMeta.get("confidence")));
        result.setModelCode(route == null ? "" : route.getModelCode());
        result.setProviderCode(route == null ? "" : route.getProviderCode());
        result.setDisclaimer(AiPromptConstant.REQUIREMENT_EVALUATION_DISCLAIMER);
        result.setRequirementItems(buildRequirementItems(parsed.getJSONArray("requirementItems")));
        result.setModuleImpacts(buildModuleImpacts(parsed.getJSONArray("moduleImpacts")));
        result.setRisks(toStringList(parsed.getJSONArray("risks")));
        result.setPendingQuestions(toStringList(parsed.getJSONArray("pendingQuestions")));
        result.setNextSteps(toStringList(parsed.getJSONArray("nextSteps")));
        result.setReferences(toStringList(parsed.getJSONArray("references")));
        return result;
    }

    private AiRequirementEvaluationResultVO buildFallbackResult(String modelResponse,
                                                               String sourceFileName,
                                                               AiModelRouteVO route) {
        AiRequirementEvaluationResultVO result = new AiRequirementEvaluationResultVO();
        String summary = sanitizeText(defaultIfBlank(modelResponse, "模型未返回结构化需求评估结果，请补充更完整的需求文件后重试。"));
        result.setStatus("NEED_REVIEW");
        result.setOverallConclusion("已完成初步阅读，但模型未返回标准结构化评估结果，需要人工复核。");
        result.setMatchLevel("UNKNOWN");
        result.setSummary(summary);
        result.setSourceFileName(defaultIfBlank(sourceFileName, AiPromptConstant.REQUIREMENT_FILE_FALLBACK_NAME));
        result.setModelCode(route == null ? "" : route.getModelCode());
        result.setProviderCode(route == null ? "" : route.getProviderCode());
        result.setDisclaimer(AiPromptConstant.REQUIREMENT_EVALUATION_DISCLAIMER);
        result.setRisks(List.of("模型未返回标准 JSON 结构，评估结论需要人工复核。"));
        result.setPendingQuestions(List.of("请确认需求文件是否包含完整功能范围、角色权限、设备接入方式和验收口径。"));
        result.setNextSteps(List.of("补充完整需求清单后重新评估", "由我方产品、技术或项目团队做正式评估确认"));
        return result;
    }

    private void normalizeResult(AiRequirementEvaluationResultVO result,
                                 String sourceFileName,
                                 AiModelRouteVO route,
                                 AiPlatformDocKnowledgeContextVO platformContext,
                                 AiCodebaseGuideContextVO codebaseContext) {
        if (result == null) {
            return;
        }
        result.setStatus(defaultIfBlank(result.getStatus(), "COMPLETED"));
        result.setSourceFileName(defaultIfBlank(result.getSourceFileName(),
                defaultIfBlank(sourceFileName, AiPromptConstant.REQUIREMENT_FILE_FALLBACK_NAME)));
        result.setMatchLevel(normalizeMatchLevel(result.getMatchLevel()));
        result.setSummary(sanitizeText(defaultIfBlank(result.getSummary(), result.getOverallConclusion())));
        result.setOverallConclusion(sanitizeText(defaultIfBlank(result.getOverallConclusion(), result.getSummary())));
        result.setModelCode(defaultIfBlank(result.getModelCode(), route == null ? "" : route.getModelCode()));
        result.setProviderCode(defaultIfBlank(result.getProviderCode(), route == null ? "" : route.getProviderCode()));
        result.setDisclaimer(AiPromptConstant.REQUIREMENT_EVALUATION_DISCLAIMER);
        if (result.getNextSteps() == null || result.getNextSteps().isEmpty()) {
            result.setNextSteps(List.of("补充业务范围、角色权限、设备接入和验收标准", "由我方产品、技术或项目团队做正式评估确认"));
        }
        result.setKeyPoints(buildKeyPoints(result, platformContext, codebaseContext));
    }

    private List<String> buildKeyPoints(AiRequirementEvaluationResultVO result,
                                        AiPlatformDocKnowledgeContextVO platformContext,
                                        AiCodebaseGuideContextVO codebaseContext) {
        List<String> keyPoints = new ArrayList<>();
        keyPoints.add("源文件：" + result.getSourceFileName());
        keyPoints.add("匹配等级：" + resolveMatchLevelLabel(result.getMatchLevel()));
        keyPoints.add("需求点数量：" + (result.getRequirementItems() == null ? 0 : result.getRequirementItems().size()));
        keyPoints.add("需要二开项：" + countRequirementItems(result, "需要二开"));
        if (platformContext != null && Boolean.TRUE.equals(platformContext.getMatched())) {
            keyPoints.add("平台知识命中：" + defaultIfBlank(platformContext.getKbName(), platformContext.getKbCode()));
        }
        if (codebaseContext != null && Boolean.TRUE.equals(codebaseContext.getMatched())) {
            keyPoints.add("源码摘要命中：" + defaultIfBlank(codebaseContext.getKbName(), codebaseContext.getKbCode()));
        }
        if (result.getConfidence() != null) {
            keyPoints.add("AI 置信度：" + Math.round(result.getConfidence() * 100) + "%");
        }
        return keyPoints;
    }

    private int countRequirementItems(AiRequirementEvaluationResultVO result, String matchType) {
        if (result == null || result.getRequirementItems() == null) {
            return 0;
        }
        int count = 0;
        for (LinkedHashMap<String, Object> item : result.getRequirementItems()) {
            Object value = item == null ? null : item.get("匹配结论");
            if (value != null && String.valueOf(value).contains(matchType)) {
                count++;
            }
        }
        return count;
    }

    private List<LinkedHashMap<String, Object>> buildRequirementItems(JSONArray array) {
        List<LinkedHashMap<String, Object>> rows = new ArrayList<>();
        JSONArray actualArray = array == null ? new JSONArray() : array;
        int limit = Math.min(actualArray.size(), MAX_REQUIREMENT_ITEMS);
        for (int index = 0; index < limit; index++) {
            JSONObject item = actualArray.getJSONObject(index);
            if (item == null) {
                continue;
            }
            LinkedHashMap<String, Object> row = new LinkedHashMap<>();
            row.put("需求点", sanitizeText(getFirstNonBlankString(item, "requirement", "name", "title")));
            row.put("匹配结论", sanitizeText(normalizeMatchType(getFirstNonBlankString(item, "matchType", "matchResult", "type"))));
            row.put("平台能力/依据", sanitizeText(getFirstNonBlankString(item, "platformCapability", "evidence", "capability")));
            row.put("建议动作", sanitizeText(getFirstNonBlankString(item, "suggestion", "nextAction", "action")));
            row.put("复杂度", sanitizeText(getFirstNonBlankString(item, "complexity", "difficulty")));
            row.put("涉及模块", sanitizeText(joinJsonArray(item.getJSONArray("relatedModules"))));
            rows.add(row);
        }
        return rows;
    }

    private List<LinkedHashMap<String, Object>> buildModuleImpacts(JSONArray array) {
        List<LinkedHashMap<String, Object>> rows = new ArrayList<>();
        JSONArray actualArray = array == null ? new JSONArray() : array;
        for (int index = 0; index < actualArray.size(); index++) {
            JSONObject item = actualArray.getJSONObject(index);
            if (item == null) {
                continue;
            }
            LinkedHashMap<String, Object> row = new LinkedHashMap<>();
            row.put("模块", sanitizeText(getFirstNonBlankString(item, "module", "moduleName")));
            row.put("影响范围", sanitizeText(getFirstNonBlankString(item, "impact", "scope")));
            row.put("二开提示", sanitizeText(getFirstNonBlankString(item, "developmentHint", "devHint", "suggestion")));
            rows.add(row);
        }
        return rows;
    }

    private AiPlatformDocKnowledgeContextVO resolvePlatformContext(String question) {
        try {
            return aiPlatformDocKnowledgeService == null ? null : aiPlatformDocKnowledgeService.buildPlatformContext(question);
        } catch (Exception ex) {
            return null;
        }
    }

    private AiCodebaseGuideContextVO resolveCodebaseContext(String question) {
        try {
            return aiCodebaseGuideKnowledgeService == null ? null : aiCodebaseGuideKnowledgeService.buildCodebaseContext(question);
        } catch (Exception ex) {
            return null;
        }
    }

    private boolean shouldUseCodebaseContext(String text) {
        if (StringUtils.isBlank(text)) {
            return false;
        }
        String normalized = text.toLowerCase(Locale.ROOT);
        return containsAny(normalized,
                "二开", "二次开发", "源码", "代码", "接口", "api", "controller", "service", "mapper",
                "前端", "后端", "页面", "新增字段", "加字段", "改造", "扩展", "定制", "自定义", "开发");
    }

    private String buildKnowledgeSearchQuestion(String sourceFileName, String contentText, String requirement) {
        StringBuilder builder = new StringBuilder(defaultIfBlank(requirement, ""));
        if (StringUtils.isNotBlank(sourceFileName)) {
            builder.append('\n').append(sourceFileName);
        }
        builder.append('\n').append(abbreviate(contentText, MAX_SEARCH_TEXT_LENGTH));
        return builder.toString();
    }

    private String buildContextLines(List<String> lines, String emptyText) {
        if (lines == null || lines.isEmpty()) {
            return emptyText;
        }
        List<String> actualLines = new ArrayList<>();
        for (String line : lines) {
            if (StringUtils.isBlank(line)) {
                continue;
            }
            actualLines.add(line.trim());
            if (actualLines.size() >= MAX_KNOWLEDGE_LINES) {
                break;
            }
        }
        return actualLines.isEmpty() ? emptyText : String.join("\n", actualLines);
    }

    private JSONObject unwrapResultObject(JSONObject parsed) {
        if (parsed == null) {
            return null;
        }
        JSONObject requirementEvaluation = parsed.getJSONObject("requirementEvaluation");
        if (requirementEvaluation != null) {
            return requirementEvaluation;
        }
        JSONObject result = parsed.getJSONObject("result");
        return result == null ? parsed : result;
    }

    private String extractJsonCandidate(String modelResponse) {
        if (StringUtils.isBlank(modelResponse)) {
            return null;
        }
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

    private List<String> toStringList(JSONArray array) {
        List<String> list = new ArrayList<>();
        if (array == null || array.isEmpty()) {
            return list;
        }
        for (Object item : array) {
            if (item == null) {
                continue;
            }
            String text;
            if (item instanceof JSONObject object) {
                text = getFirstNonBlankString(object, "message", "description", "reason", "issue", "question", "step", "title");
            } else {
                text = String.valueOf(item);
            }
            if (StringUtils.isNotBlank(text)) {
                list.add(sanitizeText(text));
            }
        }
        return list;
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

    private String joinJsonArray(JSONArray array) {
        if (array == null || array.isEmpty()) {
            return "";
        }
        List<String> values = new ArrayList<>();
        for (Object item : array) {
            if (item != null && StringUtils.isNotBlank(String.valueOf(item))) {
                values.add(String.valueOf(item).trim());
            }
        }
        return String.join("、", values);
    }

    private String sanitizeText(String value) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        return AiCodebaseAnswerSanitizer.sanitize(value.trim(), true).getContent();
    }

    private String normalizeMatchType(String value) {
        if (StringUtils.isBlank(value)) {
            return "暂无法判断";
        }
        String text = value.trim();
        if (text.contains("已有") || text.contains("标准") || text.equalsIgnoreCase("BUILT_IN")) {
            return "平台已有能力";
        }
        if (text.contains("配置") || text.equalsIgnoreCase("CONFIGURABLE")) {
            return "配置可实现";
        }
        if (text.contains("二开") || text.contains("开发") || text.contains("定制") || text.equalsIgnoreCase("CUSTOM_DEVELOPMENT")) {
            return "需要二开";
        }
        if (text.contains("无法") || text.contains("待确认") || text.equalsIgnoreCase("UNKNOWN")) {
            return "暂无法判断";
        }
        return text;
    }

    private String normalizeMatchLevel(String value) {
        if (StringUtils.isBlank(value)) {
            return "UNKNOWN";
        }
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (normalized.contains("高") || "HIGH".equals(normalized)) {
            return "HIGH";
        }
        if (normalized.contains("中") || "MEDIUM".equals(normalized) || "MID".equals(normalized)) {
            return "MEDIUM";
        }
        if (normalized.contains("低") || "LOW".equals(normalized)) {
            return "LOW";
        }
        return "UNKNOWN";
    }

    private String resolveMatchLevelLabel(String value) {
        return switch (normalizeMatchLevel(value)) {
            case "HIGH" -> "高";
            case "MEDIUM" -> "中";
            case "LOW" -> "低";
            default -> "待确认";
        };
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

    private boolean containsAny(String content, String... keywords) {
        if (StringUtils.isBlank(content) || keywords == null) {
            return false;
        }
        for (String keyword : keywords) {
            if (StringUtils.isNotBlank(keyword) && content.contains(keyword.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
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
