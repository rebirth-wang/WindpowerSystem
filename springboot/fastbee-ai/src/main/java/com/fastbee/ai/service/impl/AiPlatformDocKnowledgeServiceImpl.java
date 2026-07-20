package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Service;

import com.fastbee.ai.config.properties.FastBeeAiProperties;
import com.fastbee.ai.domain.AiKnowledgeBase;
import com.fastbee.ai.domain.AiKnowledgeVersion;
import com.fastbee.ai.model.vo.AiPlatformDocKnowledgeChunkVO;
import com.fastbee.ai.model.vo.AiPlatformDocKnowledgeContextVO;
import com.fastbee.ai.service.IAiKnowledgeBaseService;
import com.fastbee.ai.service.IAiKnowledgeVersionService;
import com.fastbee.ai.service.IAiPlatformDocKnowledgeService;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;

/**
 * 平台文档知识上下文服务实现。
 */
@Service
public class AiPlatformDocKnowledgeServiceImpl implements IAiPlatformDocKnowledgeService {

    private static final String KB_TYPE_PLATFORM_DOC = "PLATFORM_DOC";
    private static final String KNOWLEDGE_STATUS_ENABLED = "0";
    private static final String RUNTIME_SOURCE_VERSION_SNAPSHOT_CHUNK = "VERSION_SNAPSHOT_CHUNK";
    private static final String RUNTIME_SOURCE_EMPTY = "EMPTY";
    private static final int MAX_PROMPT_CHUNKS = 6;
    private static final int MIN_MATCH_SCORE = 8;
    private static final int MIN_EFFECTIVE_MATCH_SCORE = 16;
    private static final int MAX_PREVIEW_LENGTH = 220;
    private static final Pattern ASCII_WORD_PATTERN = Pattern.compile("[A-Za-z0-9]+");
    private static final Pattern ASCII_ONLY_PATTERN = Pattern.compile("^[a-z0-9]+$");
    private static final Set<String> QUERY_NOISE_TOKENS = Set.of(
            "请问", "帮我", "一下", "如何", "怎么", "怎样", "哪些", "什么", "是否",
            "系统", "平台", "页面", "功能", "操作", "步骤", "使用", "查看", "看下", "查下"
    );
    private static final Set<String> GUIDE_QUERY_HINTS = Set.of("怎么", "如何", "步骤", "流程", "操作", "新增", "创建", "配置");
    private static final Set<String> GUIDE_ACTION_TOKENS = Set.of(
            "怎么", "如何", "怎样", "步骤", "流程", "操作", "新增", "创建", "添加", "配置",
            "上传", "发布", "下载", "导入", "导出", "查看", "打开", "点击", "按钮", "在哪", "哪里"
    );
    private static final Set<String> MENU_QUERY_HINTS = Set.of("在哪", "哪里", "入口", "菜单", "页面", "路径");
    private static final Set<String> NOTICE_QUERY_HINTS = Set.of("注意", "失败", "报错", "异常", "限制", "为什么", "原因");
    private static final Set<String> PERMISSION_QUERY_HINTS = Set.of("权限", "角色", "租户", "机构", "管理员", "看不到", "无权限");
    private static final Set<String> CONFIG_QUERY_HINTS = Set.of("配置", "参数", "规则", "设置", "字段");
    private static final Set<String> DEVELOPMENT_DOC_HINTS = Set.of("dev", "开发", "源码", "接口", "api", "二开");

    @Resource
    private IAiKnowledgeBaseService aiKnowledgeBaseService;

    @Resource
    private IAiKnowledgeVersionService aiKnowledgeVersionService;

    @Resource
    private FastBeeAiProperties fastBeeAiProperties;

    @Override
    public AiPlatformDocKnowledgeContextVO buildPlatformContext(String question) {
        AiPlatformDocKnowledgeContextVO context = new AiPlatformDocKnowledgeContextVO();
        context.setQuestion(question);

        AiKnowledgeBase knowledgeBase = loadActivePlatformKnowledgeBase();
        if (knowledgeBase == null || knowledgeBase.getActiveVersionId() == null) {
            context.setRuntimeSource(RUNTIME_SOURCE_EMPTY);
            return context;
        }

        AiKnowledgeVersion activeVersion = aiKnowledgeVersionService.selectAiKnowledgeVersion(knowledgeBase.getActiveVersionId());
        if (activeVersion == null || StringUtils.isBlank(activeVersion.getSnapshotPath())) {
            context.setRuntimeSource(RUNTIME_SOURCE_EMPTY);
            return context;
        }

        PlatformSnapshotData snapshotData = loadPlatformSnapshotData(activeVersion.getSnapshotPath());
        context.setRuntimeSource(RUNTIME_SOURCE_VERSION_SNAPSHOT_CHUNK);
        context.setKnowledgeBaseId(knowledgeBase.getKnowledgeBaseId());
        context.setKbCode(knowledgeBase.getKbCode());
        context.setKbName(knowledgeBase.getKbName());
        context.setVersionId(activeVersion.getVersionId());
        context.setVersionNo(activeVersion.getVersionNo());
        context.setTotalItems(snapshotData.rowCount());
        context.setTotalChunks(snapshotData.chunks().size());
        if (snapshotData.chunks().isEmpty()) {
            return context;
        }

        String normalizedQuestion = normalizeText(question);
        List<String> keywords = extractKeywords(question);
        List<AiPlatformDocKnowledgeChunkVO> scoredChunks = snapshotData.chunks().stream()
                .peek(chunk -> chunk.setMatchScore(calculateScore(normalizedQuestion, keywords, chunk)))
                .sorted(Comparator.comparing(AiPlatformDocKnowledgeChunkVO::getMatchScore, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(AiPlatformDocKnowledgeChunkVO::getSectionName, Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(AiPlatformDocKnowledgeChunkVO::getTitle, Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(AiPlatformDocKnowledgeChunkVO::getChunkCode, Comparator.nullsLast(String::compareToIgnoreCase)))
                .collect(Collectors.toCollection(ArrayList::new));

        List<AiPlatformDocKnowledgeChunkVO> matchedChunks = scoredChunks.stream()
                .filter(chunk -> chunk.getMatchScore() != null && chunk.getMatchScore() >= MIN_MATCH_SCORE)
                .limit(MAX_PROMPT_CHUNKS)
                .collect(Collectors.toCollection(ArrayList::new));
        matchedChunks = prioritizeOperationManualChunks(normalizedQuestion, keywords, scoredChunks, matchedChunks);
        matchedChunks = filterEffectiveMatchedChunks(keywords, matchedChunks);

        if (matchedChunks.isEmpty()) {
            return context;
        }

        context.setMatched(Boolean.TRUE);
        context.setMatchedChunks(matchedChunks.size());
        context.setChunks(matchedChunks);
        context.setPromptLines(buildPromptLines(context, matchedChunks));
        return context;
    }

    private AiKnowledgeBase loadActivePlatformKnowledgeBase() {
        AiKnowledgeBase query = new AiKnowledgeBase();
        query.setKbType(KB_TYPE_PLATFORM_DOC);
        query.setStatus(KNOWLEDGE_STATUS_ENABLED);
        List<AiKnowledgeBase> knowledgeBases = aiKnowledgeBaseService.listAiKnowledgeBase(query);
        if (knowledgeBases == null || knowledgeBases.isEmpty()) {
            return null;
        }
        return knowledgeBases.stream()
                .filter(Objects::nonNull)
                .filter(item -> StringUtils.equalsIgnoreCase(KB_TYPE_PLATFORM_DOC, item.getKbType()))
                .filter(item -> KNOWLEDGE_STATUS_ENABLED.equals(item.getStatus()))
                .findFirst()
                .orElse(null);
    }

    private PlatformSnapshotData loadPlatformSnapshotData(String snapshotPath) {
        JSONObject snapshot = readSnapshot(snapshotPath);
        JSONArray items = snapshot.getJSONArray("items");
        if (items == null || items.isEmpty()) {
            return new PlatformSnapshotData(snapshot.getIntValue("rowCount"), Collections.emptyList());
        }
        List<AiPlatformDocKnowledgeChunkVO> chunks = new ArrayList<>();
        for (int index = 0; index < items.size(); index++) {
            JSONObject item = items.getJSONObject(index);
            if (item == null) {
                continue;
            }
            chunks.addAll(buildChunks(item, index));
        }
        int rowCount = snapshot.getIntValue("rowCount");
        return new PlatformSnapshotData(rowCount <= 0 ? items.size() : rowCount, chunks);
    }

    private List<AiPlatformDocKnowledgeChunkVO> buildChunks(JSONObject item, int itemIndex) {
        String sectionName = trimToEmpty(item.getString("sectionName"));
        String sectionLevel1 = trimToEmpty(item.getString("sectionLevel1"));
        String sectionPath = trimToEmpty(item.getString("sectionPath"));
        String pageTitle = trimToEmpty(item.getString("pageTitle"));
        String title = trimToEmpty(item.getString("title"));
        String headingPath = trimToEmpty(item.getString("headingPath"));
        String knowledgeType = trimToEmpty(item.getString("knowledgeType"));
        String menuPath = trimToEmpty(item.getString("menuPath"));
        String targetRole = trimToEmpty(item.getString("targetRole"));
        String preconditions = trimToEmpty(item.getString("preconditions"));
        String actionSteps = trimToEmpty(item.getString("actionSteps"));
        String resultDesc = trimToEmpty(item.getString("resultDesc"));
        String cautions = trimToEmpty(item.getString("cautions"));
        String content = normalizeContent(item.getString("content"));
        String remark = trimToEmpty(item.getString("remark"));
        String relatedDocs = trimToEmpty(item.getString("relatedDocs"));
        String sourceFile = trimToEmpty(item.getString("sourceFile"));
        String sourceUrl = trimToEmpty(item.getString("sourceUrl"));
        String sourceType = trimToEmpty(item.getString("sourceType"));
        List<String> tags = parseTags(item.getString("tags"));
        List<String> aliases = parseTags(item.getString("aliases"));
        if (StringUtils.isBlank(sectionName)
                && StringUtils.isBlank(title)
                && StringUtils.isBlank(headingPath)
                && StringUtils.isBlank(menuPath)
                && StringUtils.isBlank(content)
                && tags.isEmpty()
                && aliases.isEmpty()
                && StringUtils.isBlank(remark)) {
            return Collections.emptyList();
        }
        List<String> contentChunks = splitContent(content);
        if (contentChunks.isEmpty()) {
            contentChunks = Collections.singletonList(buildFallbackContent(sectionName, title, tags, aliases, remark));
        }

        List<AiPlatformDocKnowledgeChunkVO> result = new ArrayList<>();
        for (int index = 0; index < contentChunks.size(); index++) {
            String chunkContent = trimToEmpty(contentChunks.get(index));
            if (StringUtils.isBlank(chunkContent)) {
                continue;
            }
            AiPlatformDocKnowledgeChunkVO chunk = new AiPlatformDocKnowledgeChunkVO();
            chunk.setSectionName(sectionName);
            chunk.setSectionLevel1(sectionLevel1);
            chunk.setSectionPath(sectionPath);
            chunk.setPageTitle(pageTitle);
            chunk.setTitle(title);
            chunk.setHeadingPath(headingPath);
            chunk.setKnowledgeType(knowledgeType);
            chunk.setMenuPath(menuPath);
            chunk.setTargetRole(targetRole);
            chunk.setPreconditions(preconditions);
            chunk.setActionSteps(actionSteps);
            chunk.setResultDesc(resultDesc);
            chunk.setCautions(cautions);
            chunk.setChunkIndex(index + 1);
            chunk.setChunkCode("PDOC_" + String.format("%04d", itemIndex + 1) + "_" + String.format("%02d", index + 1));
            chunk.setTags(new ArrayList<>(tags));
            chunk.setAliases(new ArrayList<>(aliases));
            chunk.setContent(chunkContent);
            chunk.setContentPreview(buildPreview(chunkContent));
            chunk.setRemark(remark);
            chunk.setRelatedDocs(relatedDocs);
            chunk.setSourceFile(sourceFile);
            chunk.setSourceUrl(sourceUrl);
            chunk.setSourceType(sourceType);
            result.add(chunk);
        }
        return result;
    }

    private JSONObject readSnapshot(String snapshotPath) {
        try {
            Path path = Paths.get(snapshotPath.trim());
            if (!Files.exists(path)) {
                throw new ServiceException(message("ai.platform.doc.snapshot.not.exists", snapshotPath));
            }
            return JSON.parseObject(Files.readString(path, StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw ex instanceof ServiceException
                    ? (ServiceException) ex
                    : new ServiceException(message("ai.platform.doc.snapshot.read.failed", ex.getMessage()));
        }
    }

    private List<String> buildPromptLines(AiPlatformDocKnowledgeContextVO context,
                                          List<AiPlatformDocKnowledgeChunkVO> chunks) {
        List<String> lines = new ArrayList<>();
        lines.add("当前已加载平台文档知识快照：知识库=" + defaultIfBlank(context.getKbName(), "-")
                + "，版本=" + defaultIfBlank(context.getVersionNo(), "-")
                + "，条目数=" + defaultIfBlank(String.valueOf(context.getTotalItems()), "0")
                + "，切块数=" + defaultIfBlank(String.valueOf(context.getTotalChunks()), "0") + "。");
        for (AiPlatformDocKnowledgeChunkVO chunk : chunks) {
            StringBuilder line = new StringBuilder("- 章节=").append(defaultIfBlank(chunk.getSectionName(), "未分类"));
            if (StringUtils.isNotBlank(chunk.getPageTitle())) {
                line.append("；页面=").append(chunk.getPageTitle());
            }
            if (StringUtils.isNotBlank(chunk.getHeadingPath())) {
                line.append("；标题路径=").append(chunk.getHeadingPath());
            } else if (StringUtils.isNotBlank(chunk.getTitle())) {
                line.append("；标题=").append(chunk.getTitle());
            }
            if (StringUtils.isNotBlank(chunk.getMenuPath())) {
                line.append("；菜单路径=").append(chunk.getMenuPath());
            }
            if (StringUtils.isNotBlank(chunk.getKnowledgeType())) {
                line.append("；知识类型=").append(chunk.getKnowledgeType());
            }
            if (isOperationManualChunk(chunk)) {
                line.append("；来源定位=操作手册");
            } else if (isDevelopmentDocChunk(chunk)) {
                line.append("；来源定位=开发文档");
            }
            if (StringUtils.isNotBlank(chunk.getTargetRole())) {
                line.append("；适用角色=").append(shortenText(chunk.getTargetRole(), 48));
            }
            if (StringUtils.isNotBlank(chunk.getPreconditions())) {
                line.append("；前置条件=").append(shortenText(chunk.getPreconditions(), 120));
            }
            if (StringUtils.isNotBlank(chunk.getActionSteps())) {
                line.append("；操作步骤=").append(shortenText(chunk.getActionSteps(), 220));
            }
            if (StringUtils.isNotBlank(chunk.getCautions())) {
                line.append("；注意事项=").append(shortenText(chunk.getCautions(), 160));
            }
            if (chunk.getTags() != null && !chunk.getTags().isEmpty()) {
                line.append("；标签=").append(String.join("、", chunk.getTags()));
            }
            if (chunk.getAliases() != null && !chunk.getAliases().isEmpty()) {
                line.append("；同义词=").append(String.join("、", chunk.getAliases()));
            }
            if (StringUtils.isNotBlank(chunk.getContentPreview())) {
                line.append("；内容摘要=").append(chunk.getContentPreview());
            }
            if (StringUtils.isNotBlank(chunk.getRemark())) {
                line.append("；备注=").append(chunk.getRemark());
            }
            if (StringUtils.isNotBlank(chunk.getSourceUrl())) {
                line.append("；来源链接=").append(chunk.getSourceUrl());
            }
            if (StringUtils.isNotBlank(chunk.getSourceFile())) {
                line.append("；来源文件=").append(chunk.getSourceFile());
            }
            lines.add(line.toString());
        }
        return lines;
    }

    private List<String> splitContent(String content) {
        if (StringUtils.isBlank(content)) {
            return Collections.emptyList();
        }
        int chunkSize = Math.max(200, fastBeeAiProperties.getRag().getChunkSize());
        int chunkOverlap = Math.max(0, Math.min(fastBeeAiProperties.getRag().getChunkOverlap(), chunkSize / 2));
        List<String> result = new ArrayList<>();
        int start = 0;
        while (start < content.length()) {
            int rawEnd = Math.min(start + chunkSize, content.length());
            int actualEnd = adjustChunkEnd(content, start, rawEnd, chunkSize);
            String chunk = content.substring(start, actualEnd).trim();
            if (StringUtils.isNotBlank(chunk)) {
                result.add(chunk);
            }
            if (actualEnd >= content.length()) {
                break;
            }
            start = Math.max(actualEnd - chunkOverlap, start + 1);
        }
        return result;
    }

    private int adjustChunkEnd(String content, int start, int rawEnd, int chunkSize) {
        if (rawEnd >= content.length()) {
            return rawEnd;
        }
        int minEnd = Math.min(content.length(), start + Math.max(120, chunkSize / 2));
        int newlinePos = content.lastIndexOf('\n', rawEnd);
        if (newlinePos >= minEnd) {
            return newlinePos + 1;
        }
        int punctuationPos = Math.max(content.lastIndexOf('。', rawEnd), content.lastIndexOf('.', rawEnd));
        if (punctuationPos >= minEnd) {
            return punctuationPos + 1;
        }
        int semicolonPos = Math.max(content.lastIndexOf('；', rawEnd), content.lastIndexOf(';', rawEnd));
        if (semicolonPos >= minEnd) {
            return semicolonPos + 1;
        }
        return rawEnd;
    }

    private int calculateScore(String normalizedQuestion, List<String> keywords, AiPlatformDocKnowledgeChunkVO chunk) {
        if (chunk == null) {
            return 0;
        }
        int score = 0;
        score += calculateFieldScore(normalizedQuestion, chunk.getHeadingPath(), 52, 32);
        score += calculateFieldScore(normalizedQuestion, chunk.getMenuPath(), 48, 30);
        score += calculateFieldScore(normalizedQuestion, chunk.getPageTitle(), 34, 22);
        score += calculateFieldScore(normalizedQuestion, chunk.getSectionPath(), 30, 18);
        score += calculateFieldScore(normalizedQuestion, chunk.getTitle(), 42, 24);
        score += calculateFieldScore(normalizedQuestion, chunk.getSectionName(), 28, 16);
        score += calculateFieldScore(normalizedQuestion, chunk.getTargetRole(), 26, 14);
        score += calculateFieldScore(normalizedQuestion, chunk.getPreconditions(), 28, 12);
        score += calculateFieldScore(normalizedQuestion, chunk.getActionSteps(), 32, 14);
        score += calculateFieldScore(normalizedQuestion, chunk.getResultDesc(), 22, 10);
        score += calculateFieldScore(normalizedQuestion, chunk.getCautions(), 30, 14);
        score += calculateFieldScore(normalizedQuestion, chunk.getRemark(), 16, 8);
        if (chunk.getTags() != null) {
            for (String tag : chunk.getTags()) {
                score += calculateFieldScore(normalizedQuestion, tag, 30, 18);
            }
        }
        if (chunk.getAliases() != null) {
            for (String alias : chunk.getAliases()) {
                score += calculateFieldScore(normalizedQuestion, alias, 28, 16);
            }
        }
        score += calculateFieldScore(normalizedQuestion, chunk.getContent(), 16, 6);
        score += calculateKeywordScore(keywords, chunk);
        score += calculateScenarioBoost(normalizedQuestion, chunk);
        return score;
    }

    private int calculateFieldScore(String normalizedQuestion, String candidate, int exactScore, int containsScore) {
        if (StringUtils.isBlank(normalizedQuestion)) {
            return 0;
        }
        String normalizedCandidate = normalizeText(candidate);
        if (StringUtils.isBlank(normalizedCandidate)) {
            return 0;
        }
        if (normalizedQuestion.equals(normalizedCandidate)) {
            return exactScore;
        }
        if (normalizedQuestion.contains(normalizedCandidate) || normalizedCandidate.contains(normalizedQuestion)) {
            return containsScore;
        }
        return 0;
    }

    private int calculateKeywordScore(List<String> keywords, AiPlatformDocKnowledgeChunkVO chunk) {
        if (keywords == null || keywords.isEmpty() || chunk == null) {
            return 0;
        }
        int score = 0;
        String normalizedPageTitle = normalizeText(chunk.getPageTitle());
        String normalizedHeadingPath = normalizeText(chunk.getHeadingPath());
        String normalizedSectionPath = normalizeText(chunk.getSectionPath());
        String normalizedMenuPath = normalizeText(chunk.getMenuPath());
        String normalizedTargetRole = normalizeText(chunk.getTargetRole());
        String normalizedPreconditions = normalizeText(chunk.getPreconditions());
        String normalizedActionSteps = normalizeText(chunk.getActionSteps());
        String normalizedResultDesc = normalizeText(chunk.getResultDesc());
        String normalizedCautions = normalizeText(chunk.getCautions());
        String normalizedTitle = normalizeText(chunk.getTitle());
        String normalizedSection = normalizeText(chunk.getSectionName());
        String normalizedRemark = normalizeText(chunk.getRemark());
        String normalizedContent = normalizeText(chunk.getContent());
        List<String> normalizedTags = normalizeList(chunk.getTags());
        List<String> normalizedAliases = normalizeList(chunk.getAliases());
        for (String keyword : keywords) {
            if (StringUtils.isBlank(keyword)) {
                continue;
            }
            if (containsToken(normalizedTitle, keyword)) {
                score += 8;
            }
            if (containsToken(normalizedHeadingPath, keyword)) {
                score += 10;
            }
            if (containsToken(normalizedMenuPath, keyword)) {
                score += 10;
            }
            if (containsToken(normalizedPageTitle, keyword)) {
                score += 7;
            }
            if (containsToken(normalizedSection, keyword)) {
                score += 5;
            }
            if (containsToken(normalizedSectionPath, keyword)) {
                score += 6;
            }
            if (containsAny(normalizedTags, keyword)) {
                score += 6;
            }
            if (containsAny(normalizedAliases, keyword)) {
                score += 5;
            }
            if (containsToken(normalizedTargetRole, keyword)) {
                score += 6;
            }
            if (containsToken(normalizedPreconditions, keyword)) {
                score += 6;
            }
            if (containsToken(normalizedActionSteps, keyword)) {
                score += 7;
            }
            if (containsToken(normalizedResultDesc, keyword)) {
                score += 4;
            }
            if (containsToken(normalizedCautions, keyword)) {
                score += 7;
            }
            if (containsToken(normalizedRemark, keyword)) {
                score += 3;
            }
            if (containsToken(normalizedContent, keyword)) {
                score += 2;
            }
        }
        return score;
    }

    private int calculateScenarioBoost(String normalizedQuestion, AiPlatformDocKnowledgeChunkVO chunk) {
        if (StringUtils.isBlank(normalizedQuestion) || chunk == null) {
            return 0;
        }
        int score = 0;
        boolean developmentQuestion = isDevelopmentQuestion(normalizedQuestion);
        if (matchesQuestionIntent(normalizedQuestion, GUIDE_QUERY_HINTS)) {
            score += knowledgeTypeBoost(chunk.getKnowledgeType(), "STEP", 18);
            score += knowledgeTypeBoost(chunk.getKnowledgeType(), "GUIDE", 8);
            score += keywordFieldBoost(normalizedQuestion, chunk.getActionSteps(), 10);
            if (StringUtils.isNotBlank(chunk.getActionSteps())) {
                score += 18;
            }
            if (!developmentQuestion && isOperationManualChunk(chunk)) {
                score += StringUtils.isNotBlank(chunk.getActionSteps()) ? 42 : 24;
            }
            if (!developmentQuestion && isDevelopmentDocChunk(chunk) && StringUtils.isBlank(chunk.getActionSteps())) {
                score -= 20;
            }
            if (StringUtils.isBlank(chunk.getActionSteps())
                    && !"STEP".equalsIgnoreCase(trimToEmpty(chunk.getKnowledgeType()))) {
                score -= 8;
            }
        }
        if (developmentQuestion) {
            if (isDevelopmentDocChunk(chunk)) {
                score += 44;
            }
            if (isOperationManualChunk(chunk)) {
                score -= 12;
            }
        }
        if (matchesQuestionIntent(normalizedQuestion, MENU_QUERY_HINTS)) {
            score += keywordFieldBoost(normalizedQuestion, chunk.getMenuPath(), 18);
            score += keywordFieldBoost(normalizedQuestion, chunk.getSectionPath(), 10);
        }
        if (matchesQuestionIntent(normalizedQuestion, NOTICE_QUERY_HINTS)) {
            score += knowledgeTypeBoost(chunk.getKnowledgeType(), "NOTICE", 16);
            score += keywordFieldBoost(normalizedQuestion, chunk.getCautions(), 12);
            score += keywordFieldBoost(normalizedQuestion, chunk.getPreconditions(), 8);
        }
        if (matchesQuestionIntent(normalizedQuestion, PERMISSION_QUERY_HINTS)) {
            score += knowledgeTypeBoost(chunk.getKnowledgeType(), "PERMISSION", 18);
            score += keywordFieldBoost(normalizedQuestion, chunk.getTargetRole(), 12);
            score += keywordFieldBoost(normalizedQuestion, chunk.getPreconditions(), 8);
        }
        if (matchesQuestionIntent(normalizedQuestion, CONFIG_QUERY_HINTS)) {
            score += knowledgeTypeBoost(chunk.getKnowledgeType(), "CONFIG", 16);
            score += keywordFieldBoost(normalizedQuestion, chunk.getActionSteps(), 8);
            score += keywordFieldBoost(normalizedQuestion, chunk.getContent(), 6);
        }
        return score;
    }

    private List<AiPlatformDocKnowledgeChunkVO> prioritizeOperationManualChunks(String normalizedQuestion,
                                                                               List<String> keywords,
                                                                               List<AiPlatformDocKnowledgeChunkVO> scoredChunks,
                                                                               List<AiPlatformDocKnowledgeChunkVO> matchedChunks) {
        if (!matchesQuestionIntent(normalizedQuestion, GUIDE_QUERY_HINTS)
                || isDevelopmentQuestion(normalizedQuestion)
                || scoredChunks == null || scoredChunks.isEmpty()
                || matchedChunks == null || matchedChunks.isEmpty()) {
            return matchedChunks;
        }
        AiPlatformDocKnowledgeChunkVO manualChunk = scoredChunks.stream()
                .filter(chunk -> chunk.getMatchScore() != null && chunk.getMatchScore() >= MIN_MATCH_SCORE)
                .filter(this::isOperationManualChunk)
                .filter(this::hasOperationGuidance)
                .filter(chunk -> matchesGuideSubject(normalizedQuestion, keywords, chunk))
                .max(Comparator.comparingInt(chunk -> calculateOperationManualPriority(normalizedQuestion, chunk)))
                .orElse(null);
        if (manualChunk == null) {
            return matchedChunks;
        }
        List<AiPlatformDocKnowledgeChunkVO> prioritizedChunks = new ArrayList<>();
        prioritizedChunks.add(manualChunk);
        for (AiPlatformDocKnowledgeChunkVO chunk : matchedChunks) {
            if (prioritizedChunks.size() >= MAX_PROMPT_CHUNKS) {
                break;
            }
            if (!sameChunk(manualChunk, chunk)) {
                prioritizedChunks.add(chunk);
            }
        }
        return prioritizedChunks;
    }

    private List<AiPlatformDocKnowledgeChunkVO> filterEffectiveMatchedChunks(List<String> keywords,
                                                                             List<AiPlatformDocKnowledgeChunkVO> matchedChunks) {
        if (matchedChunks == null || matchedChunks.isEmpty()) {
            return matchedChunks;
        }
        return matchedChunks.stream()
                .filter(chunk -> chunk.getMatchScore() != null && (chunk.getMatchScore() >= MIN_EFFECTIVE_MATCH_SCORE
                        || hasDirectKeywordHit(keywords, chunk)))
                .limit(MAX_PROMPT_CHUNKS)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private boolean hasDirectKeywordHit(List<String> keywords, AiPlatformDocKnowledgeChunkVO chunk) {
        if (keywords == null || keywords.isEmpty() || chunk == null) {
            return false;
        }
        String searchableText = buildPlatformChunkSearchText(chunk);
        if (StringUtils.isBlank(searchableText)) {
            return false;
        }
        for (String keyword : keywords) {
            String normalizedKeyword = normalizeText(keyword);
            if (isEffectiveKeyword(normalizedKeyword) && searchableText.contains(normalizedKeyword)) {
                return true;
            }
        }
        return false;
    }

    private String buildPlatformChunkSearchText(AiPlatformDocKnowledgeChunkVO chunk) {
        return normalizeText(defaultIfBlank(chunk.getPageTitle(), "")
                + defaultIfBlank(chunk.getTitle(), "")
                + defaultIfBlank(chunk.getHeadingPath(), "")
                + defaultIfBlank(chunk.getMenuPath(), "")
                + defaultIfBlank(chunk.getSectionName(), "")
                + defaultIfBlank(chunk.getSectionPath(), "")
                + defaultIfBlank(chunk.getTargetRole(), "")
                + defaultIfBlank(chunk.getPreconditions(), "")
                + defaultIfBlank(chunk.getActionSteps(), "")
                + defaultIfBlank(chunk.getResultDesc(), "")
                + defaultIfBlank(chunk.getCautions(), "")
                + defaultIfBlank(chunk.getRemark(), "")
                + defaultIfBlank(chunk.getRelatedDocs(), "")
                + defaultIfBlank(chunk.getSourceFile(), "")
                + defaultIfBlank(chunk.getSourceUrl(), "")
                + defaultIfBlank(chunk.getContent(), "")
                + joinValues(chunk.getTags())
                + joinValues(chunk.getAliases()));
    }

    private int calculateOperationManualPriority(String normalizedQuestion, AiPlatformDocKnowledgeChunkVO chunk) {
        if (chunk == null) {
            return 0;
        }
        int priority = chunk.getMatchScore() == null ? 0 : chunk.getMatchScore();
        String searchableText = buildOperationChunkSearchText(chunk);
        String knowledgeType = trimToEmpty(chunk.getKnowledgeType());
        if ("STEP".equalsIgnoreCase(knowledgeType)) {
            priority += 24;
        } else if ("GUIDE".equalsIgnoreCase(knowledgeType)) {
            priority += 8;
        }
        if (StringUtils.isNotBlank(chunk.getActionSteps())) {
            priority += 28;
        }
        String guideSubject = extractGuideSubject(normalizedQuestion);
        if (StringUtils.isNotBlank(guideSubject) && containsToken(searchableText, guideSubject)) {
            priority += 30;
        }
        if (containsCreateIntent(normalizedQuestion)) {
            boolean createStepMatched = containsAnyToken(searchableText, "新增", "创建", "新建", "添加", "+新增", "新增产品", "创建产品");
            if (createStepMatched) {
                priority += 90;
            }
            if (containsAnyToken(searchableText, "点击", "按钮", "确定")) {
                priority += 20;
            }
            if (containsToken(searchableText, normalizeText("发布")) && !createStepMatched) {
                priority -= 35;
            }
            if (containsToken(searchableText, normalizeText("配置项")) && !createStepMatched) {
                priority -= 20;
            }
        }
        return priority;
    }

    private String buildOperationChunkSearchText(AiPlatformDocKnowledgeChunkVO chunk) {
        return normalizeText(defaultIfBlank(chunk.getPageTitle(), "")
                + defaultIfBlank(chunk.getTitle(), "")
                + defaultIfBlank(chunk.getHeadingPath(), "")
                + defaultIfBlank(chunk.getMenuPath(), "")
                + defaultIfBlank(chunk.getSectionPath(), "")
                + defaultIfBlank(chunk.getActionSteps(), "")
                + defaultIfBlank(chunk.getCautions(), "")
                + defaultIfBlank(chunk.getContent(), "")
                + joinValues(chunk.getTags())
                + joinValues(chunk.getAliases()));
    }

    private boolean containsCreateIntent(String normalizedQuestion) {
        return containsAnyToken(normalizedQuestion, "创建", "新增", "新建", "添加");
    }

    private boolean containsAnyToken(String content, String... keywords) {
        if (StringUtils.isBlank(content) || keywords == null || keywords.length == 0) {
            return false;
        }
        for (String keyword : keywords) {
            if (containsToken(content, normalizeText(keyword))) {
                return true;
            }
        }
        return false;
    }

    private boolean isOperationManualChunk(AiPlatformDocKnowledgeChunkVO chunk) {
        if (chunk == null) {
            return false;
        }
        String sourceFile = normalizeText(chunk.getSourceFile());
        String sectionLevel1 = normalizeText(chunk.getSectionLevel1());
        String sectionPath = normalizeText(chunk.getSectionPath());
        return containsToken(sourceFile, "manual")
                || containsToken(sectionLevel1, "操作手册")
                || containsToken(sectionPath, "操作手册");
    }

    private boolean isDevelopmentDocChunk(AiPlatformDocKnowledgeChunkVO chunk) {
        if (chunk == null) {
            return false;
        }
        String sourceFile = normalizeText(chunk.getSourceFile());
        String sectionLevel1 = normalizeText(chunk.getSectionLevel1());
        String sectionPath = normalizeText(chunk.getSectionPath());
        for (String hint : DEVELOPMENT_DOC_HINTS) {
            String normalizedHint = normalizeText(hint);
            if (containsToken(sourceFile, normalizedHint)
                    || containsToken(sectionLevel1, normalizedHint)
                    || containsToken(sectionPath, normalizedHint)) {
                return true;
            }
        }
        return false;
    }

    private boolean isDevelopmentQuestion(String normalizedQuestion) {
        return matchesQuestionIntent(normalizedQuestion, DEVELOPMENT_DOC_HINTS);
    }

    private boolean hasOperationGuidance(AiPlatformDocKnowledgeChunkVO chunk) {
        if (chunk == null) {
            return false;
        }
        if (StringUtils.isNotBlank(chunk.getActionSteps())) {
            return true;
        }
        String knowledgeType = trimToEmpty(chunk.getKnowledgeType());
        String headingPath = normalizeText(chunk.getHeadingPath());
        String title = normalizeText(chunk.getTitle());
        return "STEP".equalsIgnoreCase(knowledgeType)
                || "GUIDE".equalsIgnoreCase(knowledgeType)
                || matchesQuestionIntent(headingPath, GUIDE_ACTION_TOKENS)
                || matchesQuestionIntent(title, GUIDE_ACTION_TOKENS);
    }

    private boolean matchesGuideSubject(String normalizedQuestion, List<String> keywords, AiPlatformDocKnowledgeChunkVO chunk) {
        if (chunk == null) {
            return false;
        }
        String combined = normalizeText(defaultIfBlank(chunk.getPageTitle(), "")
                + defaultIfBlank(chunk.getTitle(), "")
                + defaultIfBlank(chunk.getHeadingPath(), "")
                + defaultIfBlank(chunk.getMenuPath(), "")
                + defaultIfBlank(chunk.getSectionPath(), "")
                + joinValues(chunk.getTags())
                + joinValues(chunk.getAliases()));
        String guideSubject = extractGuideSubject(normalizedQuestion);
        if (StringUtils.isNotBlank(guideSubject) && guideSubject.length() <= 6) {
            return containsToken(combined, guideSubject);
        }
        if (keywords != null) {
            for (String keyword : keywords) {
                if (containsToken(combined, keyword)) {
                    return true;
                }
            }
        }
        return true;
    }

    private String extractGuideSubject(String normalizedQuestion) {
        if (StringUtils.isBlank(normalizedQuestion)) {
            return "";
        }
        String subject = normalizedQuestion;
        for (String token : GUIDE_ACTION_TOKENS) {
            subject = subject.replace(normalizeText(token), "");
        }
        for (String token : QUERY_NOISE_TOKENS) {
            subject = subject.replace(normalizeText(token), "");
        }
        return subject.replace("并", "")
                .replace("和", "")
                .replace("或", "")
                .replace("的", "")
                .replace("具体", "")
                .trim();
    }

    private boolean sameChunk(AiPlatformDocKnowledgeChunkVO left, AiPlatformDocKnowledgeChunkVO right) {
        if (left == null || right == null) {
            return false;
        }
        return Objects.equals(left.getChunkCode(), right.getChunkCode());
    }

    private int knowledgeTypeBoost(String knowledgeType, String expectedType, int boost) {
        return StringUtils.equalsIgnoreCase(trimToEmpty(knowledgeType), expectedType) ? boost : 0;
    }

    private int keywordFieldBoost(String normalizedQuestion, String fieldValue, int boost) {
        return containsToken(normalizeText(fieldValue), normalizedQuestion)
                || containsToken(normalizedQuestion, normalizeText(fieldValue))
                ? boost : 0;
    }

    private boolean matchesQuestionIntent(String normalizedQuestion, Set<String> hintKeywords) {
        if (StringUtils.isBlank(normalizedQuestion) || hintKeywords == null || hintKeywords.isEmpty()) {
            return false;
        }
        for (String hintKeyword : hintKeywords) {
            if (normalizedQuestion.contains(normalizeText(hintKeyword))) {
                return true;
            }
        }
        return false;
    }

    private List<String> extractKeywords(String question) {
        if (StringUtils.isBlank(question)) {
            return Collections.emptyList();
        }
        Set<String> keywords = new LinkedHashSet<>();
        String normalizedQuestion = question.replace('\r', ' ')
                .replace('\n', ' ')
                .trim();
        addAsciiPhraseKeywords(normalizedQuestion, keywords);
        Arrays.stream(normalizedQuestion.split("[,，;；/|、\\s]+"))
                .map(this::trimToEmpty)
                .filter(StringUtils::isNotBlank)
                .map(this::normalizeText)
                .filter(StringUtils::isNotBlank)
                .filter(token -> token.length() >= 2)
                .filter(token -> !QUERY_NOISE_TOKENS.contains(token))
                .filter(this::isEffectiveKeyword)
                .forEach(keywords::add);
        return new ArrayList<>(keywords);
    }

    private void addAsciiPhraseKeywords(String question, Set<String> keywords) {
        if (StringUtils.isBlank(question) || keywords == null) {
            return;
        }
        Matcher matcher = ASCII_WORD_PATTERN.matcher(question);
        List<String> asciiTokens = new ArrayList<>();
        while (matcher.find()) {
            String token = normalizeText(matcher.group());
            if (StringUtils.isNotBlank(token)) {
                asciiTokens.add(token);
            }
        }
        if (asciiTokens.isEmpty()) {
            return;
        }
        for (String token : asciiTokens) {
            if (isEffectiveKeyword(token)) {
                keywords.add(token);
            }
        }
        for (int index = 0; index < asciiTokens.size() - 1; index++) {
            String combined = asciiTokens.get(index) + asciiTokens.get(index + 1);
            if (isEffectiveKeyword(combined)) {
                keywords.add(combined);
            }
        }
        if (asciiTokens.size() > 2) {
            String combinedAll = String.join("", asciiTokens);
            if (isEffectiveKeyword(combinedAll)) {
                keywords.add(combinedAll);
            }
        }
    }

    private boolean isEffectiveKeyword(String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return false;
        }
        String normalizedKeyword = normalizeText(keyword);
        if (StringUtils.isBlank(normalizedKeyword) || QUERY_NOISE_TOKENS.contains(normalizedKeyword)) {
            return false;
        }
        if (ASCII_ONLY_PATTERN.matcher(normalizedKeyword).matches()) {
            return normalizedKeyword.length() >= 3;
        }
        return normalizedKeyword.length() >= 2;
    }

    private List<String> parseTags(String rawText) {
        if (StringUtils.isBlank(rawText)) {
            return new ArrayList<>();
        }
        return Arrays.stream(rawText.split("[,，;；/|、\\s]+"))
                .map(this::trimToEmpty)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private List<String> normalizeList(List<String> values) {
        if (values == null || values.isEmpty()) {
            return Collections.emptyList();
        }
        return values.stream()
                .map(this::normalizeText)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private String joinValues(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        return String.join("", values);
    }

    private boolean containsAny(List<String> candidates, String keyword) {
        if (candidates == null || candidates.isEmpty() || StringUtils.isBlank(keyword)) {
            return false;
        }
        for (String candidate : candidates) {
            if (containsToken(candidate, keyword)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsToken(String content, String keyword) {
        return StringUtils.isNotBlank(content)
                && StringUtils.isNotBlank(keyword)
                && (content.contains(keyword) || keyword.contains(content));
    }

    private String buildFallbackContent(String sectionName,
                                        String title,
                                        List<String> tags,
                                        List<String> aliases,
                                        String remark) {
        StringBuilder builder = new StringBuilder();
        appendPart(builder, "章节", sectionName);
        appendPart(builder, "标题", title);
        if (tags != null && !tags.isEmpty()) {
            appendPart(builder, "标签", String.join("、", tags));
        }
        if (aliases != null && !aliases.isEmpty()) {
            appendPart(builder, "同义词", String.join("、", aliases));
        }
        appendPart(builder, "备注", remark);
        return builder.toString().trim();
    }

    private String shortenText(String value, int maxLength) {
        String actualValue = trimToEmpty(value).replace(';', '；');
        if (StringUtils.isBlank(actualValue) || actualValue.length() <= maxLength) {
            return actualValue;
        }
        return actualValue.substring(0, maxLength) + "...";
    }

    private void appendPart(StringBuilder builder, String label, String value) {
        if (StringUtils.isBlank(value)) {
            return;
        }
        if (builder.length() > 0) {
            builder.append("；");
        }
        builder.append(label).append("：").append(value.trim());
    }

    private String buildPreview(String content) {
        if (StringUtils.isBlank(content)) {
            return "";
        }
        String normalized = content.replace("\r\n", " ")
                .replace('\r', ' ')
                .replace('\n', ' ')
                .trim();
        if (normalized.length() <= MAX_PREVIEW_LENGTH) {
            return normalized;
        }
        return normalized.substring(0, MAX_PREVIEW_LENGTH) + "...";
    }

    private String normalizeContent(String content) {
        if (content == null) {
            return "";
        }
        return content.replace("\r\n", "\n")
                .replace('\r', '\n')
                .replace("\u0000", "")
                .trim();
    }

    private String normalizeText(String text) {
        if (StringUtils.isBlank(text)) {
            return "";
        }
        return text.toLowerCase(Locale.ROOT)
                .replace("：", "")
                .replace(":", "")
                .replace("，", "")
                .replace(",", "")
                .replace("。", "")
                .replace(".", "")
                .replace("；", "")
                .replace(";", "")
                .replace("（", "")
                .replace("）", "")
                .replace("(", "")
                .replace(")", "")
                .replace("_", "")
                .replace("-", "")
                .replace("“", "")
                .replace("”", "")
                .replace("\"", "")
                .replaceAll("\\s+", "");
    }

    private String trimToEmpty(String text) {
        return text == null ? "" : text.trim();
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value;
    }

    private static final class PlatformSnapshotData {

        private final int rowCount;
        private final List<AiPlatformDocKnowledgeChunkVO> chunks;

        private PlatformSnapshotData(int rowCount, List<AiPlatformDocKnowledgeChunkVO> chunks) {
            this.rowCount = rowCount;
            this.chunks = chunks;
        }

        private int rowCount() {
            return rowCount;
        }

        private List<AiPlatformDocKnowledgeChunkVO> chunks() {
            return chunks;
        }
    }
}
