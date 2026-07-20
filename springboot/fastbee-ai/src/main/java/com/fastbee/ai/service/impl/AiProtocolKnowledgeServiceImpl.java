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
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Service;

import com.fastbee.ai.domain.AiKnowledgeBase;
import com.fastbee.ai.domain.AiKnowledgeVersion;
import com.fastbee.ai.model.vo.AiProtocolKnowledgeContextVO;
import com.fastbee.ai.model.vo.AiProtocolKnowledgeItemVO;
import com.fastbee.ai.service.IAiKnowledgeBaseService;
import com.fastbee.ai.service.IAiKnowledgeVersionService;
import com.fastbee.ai.service.IAiProtocolKnowledgeService;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;

/**
 * 协议知识上下文服务实现。
 */
@Service
public class AiProtocolKnowledgeServiceImpl implements IAiProtocolKnowledgeService {

    private static final String KB_TYPE_PROTOCOL = "PROTOCOL_SPEC";
    private static final String KNOWLEDGE_STATUS_ENABLED = "0";
    private static final String RUNTIME_SOURCE_FILE_SNAPSHOT = "FILE_SNAPSHOT";
    private static final String RUNTIME_SOURCE_EMPTY = "EMPTY";
    private static final int MAX_PROMPT_ITEMS = 8;

    @Resource
    private IAiKnowledgeBaseService aiKnowledgeBaseService;

    @Resource
    private IAiKnowledgeVersionService aiKnowledgeVersionService;

    @Override
    public AiProtocolKnowledgeContextVO buildProtocolContext(String question) {
        AiProtocolKnowledgeContextVO context = new AiProtocolKnowledgeContextVO();
        context.setQuestion(question);

        AiKnowledgeBase knowledgeBase = loadActiveProtocolKnowledgeBase();
        if (knowledgeBase == null || knowledgeBase.getActiveVersionId() == null) {
            context.setRuntimeSource(RUNTIME_SOURCE_EMPTY);
            return context;
        }

        AiKnowledgeVersion activeVersion = aiKnowledgeVersionService.selectAiKnowledgeVersion(knowledgeBase.getActiveVersionId());
        if (activeVersion == null || StringUtils.isBlank(activeVersion.getSnapshotPath())) {
            context.setRuntimeSource(RUNTIME_SOURCE_EMPTY);
            return context;
        }

        List<AiProtocolKnowledgeItemVO> allItems = loadProtocolItems(activeVersion.getSnapshotPath());
        context.setRuntimeSource(RUNTIME_SOURCE_FILE_SNAPSHOT);
        context.setKnowledgeBaseId(knowledgeBase.getKnowledgeBaseId());
        context.setKbCode(knowledgeBase.getKbCode());
        context.setKbName(knowledgeBase.getKbName());
        context.setVersionId(activeVersion.getVersionId());
        context.setVersionNo(activeVersion.getVersionNo());
        context.setTotalItems(allItems.size());
        context.setTotalModules(resolveModuleCount(allItems));
        context.setModules(resolveModules(allItems));

        if (allItems.isEmpty()) {
            return context;
        }

        String normalizedQuestion = normalizeText(question);
        List<AiProtocolKnowledgeItemVO> scoredItems = allItems.stream()
                .peek(item -> item.setMatchScore(calculateScore(normalizedQuestion, item)))
                .sorted(Comparator.comparing(AiProtocolKnowledgeItemVO::getMatchScore, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(AiProtocolKnowledgeItemVO::getModuleName, Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(AiProtocolKnowledgeItemVO::getFieldName, Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(AiProtocolKnowledgeItemVO::getFieldCode, Comparator.nullsLast(String::compareToIgnoreCase)))
                .collect(Collectors.toCollection(ArrayList::new));

        List<AiProtocolKnowledgeItemVO> matchedItems = scoredItems.stream()
                .filter(item -> item.getMatchScore() != null && item.getMatchScore() > 0)
                .limit(MAX_PROMPT_ITEMS)
                .collect(Collectors.toCollection(ArrayList::new));

        if (matchedItems.isEmpty()) {
            matchedItems = scoredItems.stream()
                    .limit(Math.min(MAX_PROMPT_ITEMS, scoredItems.size()))
                    .collect(Collectors.toCollection(ArrayList::new));
        } else {
            context.setMatched(Boolean.TRUE);
        }

        context.setMatchedItems(matchedItems.size());
        context.setItems(matchedItems);
        context.setPromptLines(buildPromptLines(context, matchedItems));
        return context;
    }

    private AiKnowledgeBase loadActiveProtocolKnowledgeBase() {
        AiKnowledgeBase query = new AiKnowledgeBase();
        query.setKbType(KB_TYPE_PROTOCOL);
        query.setStatus(KNOWLEDGE_STATUS_ENABLED);
        List<AiKnowledgeBase> knowledgeBases = aiKnowledgeBaseService.listAiKnowledgeBase(query);
        if (knowledgeBases == null || knowledgeBases.isEmpty()) {
            return null;
        }
        return knowledgeBases.stream()
                .filter(Objects::nonNull)
                .filter(item -> StringUtils.equalsIgnoreCase(KB_TYPE_PROTOCOL, item.getKbType()))
                .filter(item -> KNOWLEDGE_STATUS_ENABLED.equals(item.getStatus()))
                .findFirst()
                .orElse(null);
    }

    private List<AiProtocolKnowledgeItemVO> loadProtocolItems(String snapshotPath) {
        JSONObject snapshot = readSnapshot(snapshotPath);
        JSONArray items = snapshot.getJSONArray("items");
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }
        List<AiProtocolKnowledgeItemVO> result = new ArrayList<>();
        for (int index = 0; index < items.size(); index++) {
            JSONObject item = items.getJSONObject(index);
            if (item == null) {
                continue;
            }
            AiProtocolKnowledgeItemVO protocolItem = new AiProtocolKnowledgeItemVO();
            protocolItem.setModuleName(trimToEmpty(item.getString("moduleName")));
            protocolItem.setFieldCode(trimToEmpty(item.getString("fieldCode")));
            protocolItem.setFieldName(trimToEmpty(item.getString("fieldName")));
            protocolItem.setDataType(trimToEmpty(item.getString("dataType")));
            protocolItem.setSampleValue(trimToEmpty(item.getString("sampleValue")));
            protocolItem.setValueMappings(trimToEmpty(item.getString("valueMappings")));
            protocolItem.setRemark(trimToEmpty(item.getString("remark")));
            protocolItem.setAliases(parseAliases(item.getString("aliases")));
            if (StringUtils.isBlank(protocolItem.getModuleName())
                    && StringUtils.isBlank(protocolItem.getFieldCode())
                    && StringUtils.isBlank(protocolItem.getFieldName())
                    && StringUtils.isBlank(protocolItem.getRemark())) {
                continue;
            }
            result.add(protocolItem);
        }
        return result;
    }

    private JSONObject readSnapshot(String snapshotPath) {
        try {
            Path path = Paths.get(snapshotPath.trim());
            if (!Files.exists(path)) {
                throw new ServiceException(message("ai.protocol.snapshot.not.exists", snapshotPath));
            }
            return JSON.parseObject(Files.readString(path, StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw ex instanceof ServiceException
                    ? (ServiceException) ex
                    : new ServiceException(message("ai.protocol.snapshot.read.failed", ex.getMessage()));
        }
    }

    private List<String> buildPromptLines(AiProtocolKnowledgeContextVO context, List<AiProtocolKnowledgeItemVO> items) {
        List<String> lines = new ArrayList<>();
        lines.add("当前已加载协议知识快照：知识库=" + defaultIfBlank(context.getKbName(), "-")
                + "，版本=" + defaultIfBlank(context.getVersionNo(), "-")
                + "，模块数=" + defaultIfBlank(String.valueOf(context.getTotalModules()), "0")
                + "，条目数=" + defaultIfBlank(String.valueOf(context.getTotalItems()), "0") + "。");
        for (AiProtocolKnowledgeItemVO item : items) {
            StringBuilder line = new StringBuilder("- 模块=").append(defaultIfBlank(item.getModuleName(), "未分类模块"));
            if (StringUtils.isNotBlank(item.getFieldName())) {
                line.append("；字段=").append(item.getFieldName());
            }
            if (StringUtils.isNotBlank(item.getFieldCode())) {
                line.append("；编码=").append(item.getFieldCode());
            }
            if (StringUtils.isNotBlank(item.getDataType())) {
                line.append("；类型=").append(item.getDataType());
            }
            if (StringUtils.isNotBlank(item.getSampleValue())) {
                line.append("；示例=").append(item.getSampleValue());
            }
            if (item.getAliases() != null && !item.getAliases().isEmpty()) {
                line.append("；别名=").append(String.join("、", item.getAliases()));
            }
            if (StringUtils.isNotBlank(item.getRemark())) {
                line.append("；说明=").append(item.getRemark());
            }
            lines.add(line.toString());
        }
        return lines;
    }

    private int calculateScore(String normalizedQuestion, AiProtocolKnowledgeItemVO item) {
        if (StringUtils.isBlank(normalizedQuestion) || item == null) {
            return 0;
        }
        int score = 0;
        score += calculateFieldScore(normalizedQuestion, item.getFieldName(), 45, 24);
        score += calculateFieldScore(normalizedQuestion, item.getFieldCode(), 36, 18);
        score += calculateFieldScore(normalizedQuestion, item.getModuleName(), 24, 12);
        score += calculateFieldScore(normalizedQuestion, item.getDataType(), 12, 6);
        score += calculateFieldScore(normalizedQuestion, item.getSampleValue(), 12, 6);
        score += calculateFieldScore(normalizedQuestion, item.getRemark(), 10, 4);
        if (item.getAliases() != null) {
            for (String alias : item.getAliases()) {
                score += calculateFieldScore(normalizedQuestion, alias, 28, 14);
            }
        }
        return score;
    }

    private int calculateFieldScore(String normalizedQuestion, String candidate, int exactScore, int containsScore) {
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

    private List<String> parseAliases(String rawAliases) {
        if (StringUtils.isBlank(rawAliases)) {
            return new ArrayList<>();
        }
        return Arrays.stream(rawAliases.split("[,，;；/|、\\s]+"))
                .map(this::trimToEmpty)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private int resolveModuleCount(List<AiProtocolKnowledgeItemVO> items) {
        return resolveModules(items).size();
    }

    private List<String> resolveModules(List<AiProtocolKnowledgeItemVO> items) {
        if (items == null || items.isEmpty()) {
            return new ArrayList<>();
        }
        Set<String> modules = new LinkedHashSet<>();
        for (AiProtocolKnowledgeItemVO item : items) {
            if (item != null && StringUtils.isNotBlank(item.getModuleName())) {
                modules.add(item.getModuleName().trim());
            }
        }
        return new ArrayList<>(modules);
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
                .replaceAll("\\s+", "");
    }

    private String trimToEmpty(String text) {
        return text == null ? "" : text.trim();
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value;
    }
}
