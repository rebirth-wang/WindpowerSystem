package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Service;

import com.fastbee.ai.domain.AiKnowledgeBase;
import com.fastbee.ai.domain.AiKnowledgeVersion;
import com.fastbee.ai.model.vo.AiKnowledgeVersionQualityCheckVO;
import com.fastbee.ai.model.vo.AiKnowledgeVersionQualityIssueVO;
import com.fastbee.ai.service.IAiNl2SqlQualityGateService;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;

/**
 * NL2SQL 发布质量门禁服务实现。
 *
 * <p>首版采用确定性静态门禁，发布时校验企业版问句样例、主事实表、
 * 运行时数据源和字典映射准备情况，避免发布后才在生产对话中暴露高风险问题。</p>
 */
@Service
public class AiNl2SqlQualityGateServiceImpl implements IAiNl2SqlQualityGateService {

    private static final String KB_TYPE_NL2SQL = "NL2SQL_SEMANTIC";
    private static final Pattern SQL_TABLE_PATTERN = Pattern.compile("(?i)\\b(?:from|join)\\s+([`\\w.]+)");
    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\b[a-zA-Z][a-zA-Z0-9_]*\\b");
    private static final Pattern ISSUE_ROW_QUESTION_PATTERN =
            Pattern.compile("^第\\s*(\\d+)\\s*行(?:\\s*P[0-2]\\s*问句)?“([^”]*)”");
    private static final Pattern ISSUE_ROW_PATTERN = Pattern.compile("^第\\s*(\\d+)\\s*行(?:\\s*P[0-2]\\s*问句)?");

    @Override
    public AiKnowledgeVersionQualityCheckVO checkBeforePublish(AiKnowledgeBase knowledgeBase, AiKnowledgeVersion version) {
        AiKnowledgeVersionQualityCheckVO result = initQualityCheckResult(knowledgeBase, version);
        if (!isNl2SqlKnowledgeBase(knowledgeBase)) {
            result.setPassed(Boolean.TRUE);
            result.setSummary("当前知识库类型无需执行 NL2SQL 质量预检");
            return result;
        }

        List<String> blockingIssues = new ArrayList<>();
        List<String> warningIssues = new ArrayList<>();
        JSONObject snapshot;
        try {
            snapshot = readSnapshot(version);
        } catch (ServiceException ex) {
            blockingIssues.add(trimGatePrefix(ex.getMessage()));
            finalizeQualityCheckResult(result, 0, 0, blockingIssues, warningIssues);
            return result;
        }

        JSONArray items = snapshot.getJSONArray("items");
        if (items == null || items.isEmpty()) {
            blockingIssues.add("当前版本未包含可发布的字段语义记录");
        }
        JSONArray questionExamples = snapshot.getJSONArray("questionExamples");
        if (questionExamples == null || questionExamples.isEmpty()) {
            blockingIssues.add("当前版本缺少“问句样例”Sheet，请下载企业版模板补齐黄金问句后重新上传并构建版本");
            finalizeQualityCheckResult(result, 0, 0, blockingIssues, warningIssues);
            return result;
        }

        GateContext context = new GateContext(snapshot);
        int p0Count = 0;
        int validQuestionCount = 0;
        for (int index = 0; index < questionExamples.size(); index++) {
            JSONObject example = questionExamples.getJSONObject(index);
            if (example == null) {
                continue;
            }
            String question = trimToEmpty(example.getString("question"));
            String riskLevel = normalizeRiskLevel(example.getString("riskLevel"));
            if (StringUtils.isBlank(question)) {
                if ("P0".equals(riskLevel)) {
                    blockingIssues.add("第 " + resolveRowNum(example, index) + " 行 P0 问句为空");
                } else {
                    warningIssues.add("第 " + resolveRowNum(example, index) + " 行 " + riskLevel + " 问句为空");
                }
                continue;
            }
            validQuestionCount++;
            if ("P0".equals(riskLevel)) {
                p0Count++;
                validateQuestionExample(example, index, context, blockingIssues);
            } else {
                validateAdvisoryQuestionExample(example, index, context, warningIssues);
            }
        }

        if (validQuestionCount == 0) {
            blockingIssues.add("问句样例中未配置任何有效问题");
        }
        if (p0Count == 0) {
            blockingIssues.add("问句样例中至少需要配置 1 条 P0 黄金问句，覆盖主事实表、字典映射或运行时指标等高风险场景");
        }
        finalizeQualityCheckResult(result, validQuestionCount, p0Count, blockingIssues, warningIssues);
        return result;
    }

    @Override
    public void validateBeforePublish(AiKnowledgeBase knowledgeBase, AiKnowledgeVersion version) {
        AiKnowledgeVersionQualityCheckVO result = checkBeforePublish(knowledgeBase, version);
        if (result == null || Boolean.TRUE.equals(result.getPassed())) {
            return;
        }
        List<AiKnowledgeVersionQualityIssueVO> blockingIssues = result.getIssues() == null
                ? Collections.emptyList()
                : result.getIssues().stream()
                .filter(item -> item != null && "ERROR".equalsIgnoreCase(item.getLevel()))
                .toList();
        if (!blockingIssues.isEmpty()) {
            throw new ServiceException(message("ai.nl2sql.quality.gate.blocking.failed",
                    blockingIssues.size(), String.join("；", limitIssueMessages(blockingIssues, 8))));
        }
    }

    private AiKnowledgeVersionQualityCheckVO initQualityCheckResult(AiKnowledgeBase knowledgeBase, AiKnowledgeVersion version) {
        AiKnowledgeVersionQualityCheckVO result = new AiKnowledgeVersionQualityCheckVO();
        result.setKnowledgeBaseId(knowledgeBase == null ? null : knowledgeBase.getKnowledgeBaseId());
        result.setVersionId(version == null ? null : version.getVersionId());
        result.setKbType(knowledgeBase == null ? null : knowledgeBase.getKbType());
        result.setVersionNo(version == null ? null : version.getVersionNo());
        result.setPassed(Boolean.FALSE);
        result.setCheckedQuestionCount(0);
        result.setP0QuestionCount(0);
        result.setBlockingIssueCount(0);
        result.setWarningIssueCount(0);
        result.setCheckedTime(new Date());
        return result;
    }

    private void finalizeQualityCheckResult(AiKnowledgeVersionQualityCheckVO result,
                                            int validQuestionCount,
                                            int p0Count,
                                            List<String> blockingIssues,
                                            List<String> warningIssues) {
        result.setCheckedQuestionCount(validQuestionCount);
        result.setP0QuestionCount(p0Count);
        result.setBlockingIssueCount(blockingIssues == null ? 0 : blockingIssues.size());
        result.setWarningIssueCount(warningIssues == null ? 0 : warningIssues.size());
        result.setIssues(convertQualityIssues(blockingIssues, warningIssues));
        result.setPassed(blockingIssues == null || blockingIssues.isEmpty());
        if (!Boolean.TRUE.equals(result.getPassed())) {
            result.setSummary("质量预检未通过，共 " + result.getBlockingIssueCount()
                    + " 个阻断项，另有 " + result.getWarningIssueCount() + " 个告警项");
            return;
        }
        if (result.getWarningIssueCount() != null && result.getWarningIssueCount() > 0) {
            result.setSummary("质量预检通过，但存在 " + result.getWarningIssueCount() + " 个告警项，建议发布前复核");
            return;
        }
        result.setSummary("质量预检通过，可进入版本发布");
    }

    private List<AiKnowledgeVersionQualityIssueVO> convertQualityIssues(List<String> blockingIssues,
                                                                        List<String> warningIssues) {
        List<AiKnowledgeVersionQualityIssueVO> result = new ArrayList<>();
        if (blockingIssues != null && !blockingIssues.isEmpty()) {
            for (String issue : blockingIssues) {
                result.add(buildQualityIssue(issue, "ERROR", "QUALITY_GATE_BLOCKING"));
            }
        }
        if (warningIssues != null && !warningIssues.isEmpty()) {
            for (String issue : warningIssues) {
                result.add(buildQualityIssue(issue, "WARNING", "QUALITY_GATE_WARNING"));
            }
        }
        return result;
    }

    private AiKnowledgeVersionQualityIssueVO buildQualityIssue(String issueMessage, String level, String issueCode) {
        AiKnowledgeVersionQualityIssueVO issue = new AiKnowledgeVersionQualityIssueVO();
        issue.setLevel(level);
        issue.setIssueCode(issueCode);
        String actualMessage = trimToEmpty(issueMessage);
        Matcher rowQuestionMatcher = ISSUE_ROW_QUESTION_PATTERN.matcher(actualMessage);
        if (rowQuestionMatcher.find()) {
            issue.setRowNum(Integer.parseInt(rowQuestionMatcher.group(1)));
            issue.setQuestion(trimToEmpty(rowQuestionMatcher.group(2)));
            issue.setMessage(trimIssueMessage(actualMessage.substring(rowQuestionMatcher.end())));
            return issue;
        }
        Matcher rowMatcher = ISSUE_ROW_PATTERN.matcher(actualMessage);
        if (rowMatcher.find()) {
            issue.setRowNum(Integer.parseInt(rowMatcher.group(1)));
            issue.setMessage(trimIssueMessage(actualMessage.substring(rowMatcher.end())));
            return issue;
        }
        issue.setMessage(actualMessage);
        return issue;
    }

    private String trimIssueMessage(String message) {
        String actualMessage = trimToEmpty(message);
        if (actualMessage.startsWith("，") || actualMessage.startsWith("：") || actualMessage.startsWith(":")) {
            return trimToEmpty(actualMessage.substring(1));
        }
        return actualMessage;
    }

    private String trimGatePrefix(String message) {
        String actualMessage = trimToEmpty(message);
        String prefix = "问数语义发布门禁未通过：";
        if (actualMessage.startsWith(prefix)) {
            return trimToEmpty(actualMessage.substring(prefix.length()));
        }
        return actualMessage;
    }

    private void validateQuestionExample(JSONObject example, int index, GateContext context, List<String> blockingIssues) {
        String riskLevel = normalizeRiskLevel(example.getString("riskLevel"));
        if (!"P0".equals(riskLevel)) {
            return;
        }
        String question = trimToEmpty(example.getString("question"));
        String expectedBusinessObject = trimToEmpty(example.getString("expectedBusinessObject"));
        String expectedSource = trimToEmpty(example.getString("expectedSource"));
        String allowedTablesText = trimToEmpty(example.getString("allowedTables"));
        String forbiddenTablesText = trimToEmpty(example.getString("forbiddenTables"));
        String expectedAction = trimToEmpty(example.getString("expectedAction"));
        String rowLabel = "第 " + resolveRowNum(example, index) + " 行 P0 问句“" + question + "”";

        if (StringUtils.isBlank(expectedBusinessObject)) {
            blockingIssues.add(rowLabel + "未配置期望业务对象");
        } else if (!context.containsBusinessObject(expectedBusinessObject)) {
            blockingIssues.add(rowLabel + "期望业务对象未在“业务对象”Sheet 中声明：" + expectedBusinessObject);
        }
        if (StringUtils.isBlank(expectedSource)) {
            blockingIssues.add(rowLabel + "未配置期望主表/数据源");
            return;
        }

        Set<String> expectedTables = extractTableNames(expectedSource + ";" + allowedTablesText, context);
        Set<String> forbiddenTables = extractTableNames(forbiddenTablesText, context);
        boolean runtimeExpected = isRuntimeExpected(expectedSource, allowedTablesText);
        if (runtimeExpected) {
            validateRuntimeQuestion(rowLabel, question, forbiddenTablesText, blockingIssues);
        } else {
            validateRelationDbQuestion(rowLabel, expectedTables, context, blockingIssues);
        }

        Set<String> intersection = new LinkedHashSet<>(expectedTables);
        intersection.retainAll(forbiddenTables);
        if (!intersection.isEmpty()) {
            blockingIssues.add(rowLabel + "的允许表和禁止表存在冲突：" + String.join("、", intersection));
        }

        List<JSONObject> matchedMetricRules = findMatchingMetricRules(expectedBusinessObject, expectedTables, context);
        validateBusinessObjectCountRule(rowLabel, question, expectedTables, runtimeExpected, blockingIssues);
        validateDictionaryRule(rowLabel, question, expectedAction, expectedTables, matchedMetricRules, context, blockingIssues);
        validateMetricRule(rowLabel, question, expectedAction, runtimeExpected, matchedMetricRules, blockingIssues);
    }

    private void validateAdvisoryQuestionExample(JSONObject example, int index, GateContext context, List<String> warningIssues) {
        String riskLevel = normalizeRiskLevel(example.getString("riskLevel"));
        String question = trimToEmpty(example.getString("question"));
        String expectedBusinessObject = trimToEmpty(example.getString("expectedBusinessObject"));
        String expectedSource = trimToEmpty(example.getString("expectedSource"));
        String allowedTablesText = trimToEmpty(example.getString("allowedTables"));
        String forbiddenTablesText = trimToEmpty(example.getString("forbiddenTables"));
        String expectedAction = trimToEmpty(example.getString("expectedAction"));
        String rowLabel = "第 " + resolveRowNum(example, index) + " 行 " + riskLevel + " 问句“" + question + "”";

        if (StringUtils.isBlank(expectedBusinessObject)) {
            warningIssues.add(rowLabel + "未配置期望业务对象，建议补齐以提升回归覆盖质量");
        } else if (!context.containsBusinessObject(expectedBusinessObject)) {
            warningIssues.add(rowLabel + "期望业务对象未在“业务对象”Sheet 中声明：" + expectedBusinessObject);
        }
        if (StringUtils.isBlank(expectedSource)) {
            warningIssues.add(rowLabel + "未配置期望主表/数据源，建议补齐以便后续回归定位口径问题");
            return;
        }

        Set<String> expectedTables = extractTableNames(expectedSource + ";" + allowedTablesText, context);
        Set<String> forbiddenTables = extractTableNames(forbiddenTablesText, context);
        boolean runtimeExpected = isRuntimeExpected(expectedSource, allowedTablesText);
        if (runtimeExpected) {
            validateRuntimeQuestion(rowLabel, question, forbiddenTablesText, warningIssues);
        } else {
            validateRelationDbQuestion(rowLabel, expectedTables, context, warningIssues);
        }

        Set<String> intersection = new LinkedHashSet<>(expectedTables);
        intersection.retainAll(forbiddenTables);
        if (!intersection.isEmpty()) {
            warningIssues.add(rowLabel + "的允许表和禁止表存在冲突：" + String.join("、", intersection));
        }

        List<JSONObject> matchedMetricRules = findMatchingMetricRules(expectedBusinessObject, expectedTables, context);
        validateBusinessObjectCountRule(rowLabel, question, expectedTables, runtimeExpected, warningIssues);
        validateDictionaryRule(rowLabel, question, expectedAction, expectedTables, matchedMetricRules, context, warningIssues);
        validateMetricRule(rowLabel, question, expectedAction, runtimeExpected, matchedMetricRules, warningIssues);
    }

    private void validateRuntimeQuestion(String rowLabel, String question, String forbiddenTablesText,
                                         List<String> blockingIssues) {
        if (!looksLikeRuntimeMetricQuestion(question)) {
            blockingIssues.add(rowLabel + "期望走运行时数据源，但问题文本缺少当前、实时、历史、趋势、总计等运行时语义");
        }
        if (StringUtils.isBlank(forbiddenTablesText)) {
            blockingIssues.add(rowLabel + "期望走运行时数据源，但未配置禁止表或禁止 SQL 示例，无法防止模型误走关系库字段");
        }
    }

    private void validateRelationDbQuestion(String rowLabel, Set<String> expectedTables, GateContext context,
                                            List<String> blockingIssues) {
        if (expectedTables.isEmpty()) {
            blockingIssues.add(rowLabel + "未配置可校验的关系库主表或允许表");
            return;
        }
        for (String table : expectedTables) {
            if (!context.tableNames.contains(table)) {
                blockingIssues.add(rowLabel + "期望表不存在于当前语义快照：" + table);
                continue;
            }
            if (!context.tableSemantics.containsKey(table)) {
                blockingIssues.add(rowLabel + "期望表未在“表级语义”Sheet 中声明：" + table);
            }
        }
    }

    private void validateBusinessObjectCountRule(String rowLabel, String question, Set<String> expectedTables,
                                                 boolean runtimeExpected, List<String> blockingIssues) {
        if (runtimeExpected || !hasCountIntent(question)) {
            return;
        }
        String normalizedQuestion = normalizeText(question);
        if (normalizedQuestion.contains("产品")
                && !containsAny(question, "每个产品", "按产品", "有设备的产品", "接入设备的产品")
                && !expectedTables.contains("iot_product")) {
            blockingIssues.add(rowLabel + "询问产品总量，但期望表未包含产品主事实表 iot_product");
        }
        if (normalizedQuestion.contains("设备") && !containsAny(question, "设备日志", "设备记录")
                && !expectedTables.contains("iot_device")) {
            blockingIssues.add(rowLabel + "询问设备总量，但期望表未包含设备主事实表 iot_device");
        }
        if (normalizedQuestion.contains("用户")
                && !containsAny(question, "设备用户", "授权用户", "分享用户", "绑定用户")
                && !expectedTables.contains("sys_user")) {
            blockingIssues.add(rowLabel + "询问用户总量，但期望表未包含系统用户主表 sys_user");
        }
        if (isAlertLogFactQuestion(normalizedQuestion)
                && !expectedTables.contains("iot_alert_log")) {
            blockingIssues.add(rowLabel + "询问告警记录、未处理/待处理告警或告警趋势，但期望表未包含告警记录表 iot_alert_log");
        }
        if (normalizedQuestion.contains("告警规则") && !expectedTables.contains("iot_alert")) {
            blockingIssues.add(rowLabel + "询问告警规则数量，但期望表未包含告警规则表 iot_alert");
        }
        if (normalizedQuestion.contains("工单") && !expectedTables.contains("iot_work_order")) {
            blockingIssues.add(rowLabel + "询问工单数量，但期望表未包含工单主表 iot_work_order");
        }
        if ((normalizedQuestion.contains("ai模型") || normalizedQuestion.contains("模型数量")) && !expectedTables.contains("ai_model")) {
            blockingIssues.add(rowLabel + "询问 AI 模型数量，但期望表未包含 AI 模型表 ai_model");
        }
        if (normalizedQuestion.contains("知识库") && !expectedTables.contains("ai_knowledge_base")) {
            blockingIssues.add(rowLabel + "询问知识库数量，但期望表未包含知识库主表 ai_knowledge_base");
        }
    }

    private void validateDictionaryRule(String rowLabel, String question, String expectedAction,
                                        Set<String> expectedTables,
                                        List<JSONObject> matchedMetricRules,
                                        GateContext context,
                                        List<String> blockingIssues) {
        if (!needsDictionaryMapping(question, expectedAction)) {
            return;
        }
        if (hasDictionaryMapping(expectedTables, context) || hasMetricStateRule(matchedMetricRules)) {
            return;
        }
        if (expectedTables.isEmpty()) {
            blockingIssues.add(rowLabel + "涉及状态/类型/启停口径，但既未配置可检查的期望表，也未在“指标口径”Sheet 中声明状态口径");
            return;
        }
        if (!hasDictionaryMapping(expectedTables, context)) {
            blockingIssues.add(rowLabel + "涉及状态/类型/启停口径，但期望表中未找到可用字典、枚举或值映射字段");
        }
    }

    private void validateMetricRule(String rowLabel, String question, String expectedAction,
                                    boolean runtimeExpected,
                                    List<JSONObject> matchedMetricRules,
                                    List<String> blockingIssues) {
        if (!needsMetricRule(question, expectedAction, runtimeExpected)) {
            return;
        }
        if (matchedMetricRules.isEmpty()) {
            blockingIssues.add(rowLabel + "涉及统计或运行时指标口径，但未在“指标口径”Sheet 中声明匹配的指标口径");
            return;
        }
        if (runtimeExpected && !hasRuntimeMetricRule(matchedMetricRules, question, expectedAction)) {
            blockingIssues.add(rowLabel + "期望走运行时数据源，但匹配到的指标口径未声明 Redis、TSDB 或 Hybrid 等运行时数据源");
        }
        if (needsDistinctMetricRule(question, expectedAction) && !hasDistinctMetricRule(matchedMetricRules)) {
            blockingIssues.add(rowLabel + "涉及去重统计口径，但匹配到的指标口径未声明去重字段或 DISTINCT 聚合方式");
        }
        if (needsTimeWindowMetricRule(question, expectedAction) && !hasTimeWindowMetricRule(matchedMetricRules)) {
            blockingIssues.add(rowLabel + "涉及历史、趋势、总计或时间窗口口径，但匹配到的指标口径未声明时间口径或时序数据源");
        }
        if (needsGroupMetricRule(question, expectedAction) && !hasGroupMetricRule(matchedMetricRules)) {
            blockingIssues.add(rowLabel + "涉及分组统计口径，但匹配到的指标口径未声明 GROUP BY、按维度或每个对象统计方式");
        }
        if (needsRatioMetricRule(question, expectedAction) && !hasRatioMetricRule(matchedMetricRules)) {
            blockingIssues.add(rowLabel + "涉及占比、比例或率类口径，但匹配到的指标口径未声明比例计算方式");
        }
        if (hasCountIntent(question) && !runtimeExpected && !hasCountMetricRule(matchedMetricRules)) {
            blockingIssues.add(rowLabel + "询问数量统计，但匹配到的指标口径未声明 COUNT、总量或计数类聚合方式");
        }
    }

    private JSONObject readSnapshot(AiKnowledgeVersion version) {
        if (version == null || StringUtils.isBlank(version.getSnapshotPath())) {
            throw new ServiceException(message("ai.nl2sql.quality.gate.snapshot.required"));
        }
        Path snapshotPath = Paths.get(version.getSnapshotPath().trim());
        if (!Files.exists(snapshotPath)) {
            throw new ServiceException(message("ai.nl2sql.quality.gate.snapshot.not.exists"));
        }
        try {
            return JSON.parseObject(Files.readString(snapshotPath, StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw new ServiceException(message("ai.nl2sql.quality.gate.snapshot.read.failed", ex.getMessage()));
        }
    }

    private Set<String> extractTableNames(String text, GateContext context) {
        Set<String> tableNames = new LinkedHashSet<>();
        if (StringUtils.isBlank(text)) {
            return tableNames;
        }
        Matcher sqlMatcher = SQL_TABLE_PATTERN.matcher(text);
        while (sqlMatcher.find()) {
            addTableName(tableNames, sqlMatcher.group(1), context);
        }
        Matcher tokenMatcher = TOKEN_PATTERN.matcher(text);
        while (tokenMatcher.find()) {
            addTableName(tableNames, tokenMatcher.group(), context);
        }
        return tableNames;
    }

    private void addTableName(Set<String> tableNames, String value, GateContext context) {
        String tableName = normalizeTableName(value);
        if (StringUtils.isBlank(tableName)) {
            return;
        }
        if (context.tableNames.contains(tableName) || looksLikePhysicalTable(tableName)) {
            tableNames.add(tableName);
        }
    }

    private boolean looksLikePhysicalTable(String value) {
        return value.startsWith("iot_")
                || value.startsWith("sys_")
                || value.startsWith("ai_")
                || value.startsWith("scene_")
                || value.startsWith("sip_")
                || value.startsWith("modbus_");
    }

    private boolean hasDictionaryMapping(Set<String> expectedTables, GateContext context) {
        for (int index = 0; index < context.items.size(); index++) {
            JSONObject item = context.items.getJSONObject(index);
            if (item == null) {
                continue;
            }
            String tableName = normalizeTableName(item.getString("tableName"));
            if (!expectedTables.contains(tableName)) {
                continue;
            }
            String columnName = normalizeColumnName(item.getString("columnName"));
            if (!isEnumLikeColumn(columnName)) {
                continue;
            }
            if (StringUtils.isNotBlank(item.getString("valueMappings"))
                    || StringUtils.isNotBlank(item.getString("sourceCode"))
                    || "DICT".equalsIgnoreCase(trimToEmpty(item.getString("sourceType")))
                    || "ENUM".equalsIgnoreCase(trimToEmpty(item.getString("sourceType")))) {
                return true;
            }
        }
        return false;
    }

    private List<JSONObject> findMatchingMetricRules(String expectedBusinessObject,
                                                     Set<String> expectedTables,
                                                     GateContext context) {
        List<JSONObject> result = new ArrayList<>();
        if (context == null || context.metricRules.isEmpty()) {
            return result;
        }
        String normalizedBusinessObject = normalizeLookupKey(expectedBusinessObject);
        for (JSONObject metricRule : context.metricRules) {
            if (metricRule == null) {
                continue;
            }
            int score = 0;
            String normalizedRuleBusinessObject = normalizeLookupKey(metricRule.getString("businessObjectName"));
            String primaryTable = normalizeTableName(metricRule.getString("primaryTable"));
            if (StringUtils.isNotBlank(normalizedBusinessObject)
                    && StringUtils.equals(normalizedBusinessObject, normalizedRuleBusinessObject)) {
                score += 8;
            }
            if (expectedTables != null && !expectedTables.isEmpty()
                    && StringUtils.isNotBlank(primaryTable)
                    && expectedTables.contains(primaryTable)) {
                score += 6;
            }
            if (score > 0) {
                metricRule.put("_matchScore", score);
                result.add(metricRule);
            }
        }
        result.sort((left, right) -> Integer.compare(right.getIntValue("_matchScore"), left.getIntValue("_matchScore")));
        return result;
    }

    private boolean hasMetricStateRule(List<JSONObject> matchedMetricRules) {
        if (matchedMetricRules == null || matchedMetricRules.isEmpty()) {
            return false;
        }
        for (JSONObject metricRule : matchedMetricRules) {
            if (metricRule != null && StringUtils.isNotBlank(trimToEmpty(metricRule.getString("stateRule")))) {
                return true;
            }
        }
        return false;
    }

    private boolean needsMetricRule(String question, String expectedAction, boolean runtimeExpected) {
        return runtimeExpected
                || hasCountIntent(question)
                || needsGroupMetricRule(question, expectedAction)
                || needsRatioMetricRule(question, expectedAction)
                || containsAny(expectedAction, "COUNT", "DISTINCT", "GROUP BY", "去重", "聚合", "统计", "总量", "总计", "实时值", "时间窗口");
    }

    private boolean hasRuntimeMetricRule(List<JSONObject> matchedMetricRules, String question, String expectedAction) {
        if (matchedMetricRules == null || matchedMetricRules.isEmpty()) {
            return false;
        }
        boolean historyIntent = looksLikeHistoryMetricIntent(question, expectedAction);
        boolean currentIntent = looksLikeCurrentMetricIntent(question, expectedAction);
        for (JSONObject metricRule : matchedMetricRules) {
            if (metricRule == null) {
                continue;
            }
            String dataSource = trimToEmpty(metricRule.getString("defaultDataSource")).toUpperCase(Locale.ROOT);
            String timeRule = trimToEmpty(metricRule.getString("timeRule"));
            if (historyIntent) {
                if (dataSource.contains("TSDB") || dataSource.contains("HYBRID")
                        || containsAny(timeRule, "历史", "趋势", "总计", "当天", "时间窗口")) {
                    return true;
                }
                continue;
            }
            if (currentIntent) {
                if (dataSource.contains("REDIS") || dataSource.contains("HYBRID") || dataSource.contains("RUNTIME")
                        || containsAny(timeRule, "当前", "实时", "现在", "此刻")) {
                    return true;
                }
                continue;
            }
            if (dataSource.contains("REDIS") || dataSource.contains("TSDB")
                    || dataSource.contains("HYBRID") || dataSource.contains("RUNTIME")) {
                return true;
            }
        }
        return false;
    }

    private boolean needsDistinctMetricRule(String question, String expectedAction) {
        return containsAny(question, "有设备的产品", "接入设备的产品", "去重")
                || containsAny(expectedAction, "DISTINCT", "去重");
    }

    private boolean hasDistinctMetricRule(List<JSONObject> matchedMetricRules) {
        if (matchedMetricRules == null || matchedMetricRules.isEmpty()) {
            return false;
        }
        for (JSONObject metricRule : matchedMetricRules) {
            if (metricRule == null) {
                continue;
            }
            String distinctColumn = trimToEmpty(metricRule.getString("distinctColumn"));
            String aggregationType = trimToEmpty(metricRule.getString("aggregationType")).toUpperCase(Locale.ROOT);
            if (StringUtils.isNotBlank(distinctColumn) || aggregationType.contains("DISTINCT")) {
                return true;
            }
        }
        return false;
    }

    private boolean needsTimeWindowMetricRule(String question, String expectedAction) {
        return looksLikeHistoryMetricIntent(question, expectedAction);
    }

    private boolean hasTimeWindowMetricRule(List<JSONObject> matchedMetricRules) {
        if (matchedMetricRules == null || matchedMetricRules.isEmpty()) {
            return false;
        }
        for (JSONObject metricRule : matchedMetricRules) {
            if (metricRule == null) {
                continue;
            }
            String dataSource = trimToEmpty(metricRule.getString("defaultDataSource")).toUpperCase(Locale.ROOT);
            String timeRule = trimToEmpty(metricRule.getString("timeRule"));
            if (dataSource.contains("TSDB") || dataSource.contains("HYBRID")
                    || containsAny(timeRule, "历史", "趋势", "总计", "当天", "时间窗口", "最近")) {
                return true;
            }
        }
        return false;
    }

    private boolean needsGroupMetricRule(String question, String expectedAction) {
        return containsAny(question, "每个", "各个", "按产品", "按设备", "按状态", "按日期", "按天", "按小时", "按月", "按区域", "按机构", "按用户",
                "分组", "排行", "排名", "最多", "最少", "Top", "top")
                || containsAny(expectedAction, "GROUP BY", "分组", "按维度", "每个", "排名", "排行");
    }

    private boolean hasGroupMetricRule(List<JSONObject> matchedMetricRules) {
        if (matchedMetricRules == null || matchedMetricRules.isEmpty()) {
            return false;
        }
        for (JSONObject metricRule : matchedMetricRules) {
            if (metricRule == null) {
                continue;
            }
            String metricRuleName = trimToEmpty(metricRule.getString("metricRuleName"));
            String aggregationType = trimToEmpty(metricRule.getString("aggregationType")).toUpperCase(Locale.ROOT);
            String applicableQuestion = trimToEmpty(metricRule.getString("applicableQuestion"));
            if (aggregationType.contains("GROUP")
                    || containsAny(metricRuleName, "每个", "各个", "按", "排行", "排名")
                    || containsAny(applicableQuestion, "每个", "各个", "按", "分组", "排行", "排名")) {
                return true;
            }
        }
        return false;
    }

    private boolean needsRatioMetricRule(String question, String expectedAction) {
        return containsAny(question, "占比", "比例", "比率", "百分比")
                || containsAny(expectedAction, "占比", "比例", "比率", "百分比", "RATIO", "COUNT_IF");
    }

    private boolean hasRatioMetricRule(List<JSONObject> matchedMetricRules) {
        if (matchedMetricRules == null || matchedMetricRules.isEmpty()) {
            return false;
        }
        for (JSONObject metricRule : matchedMetricRules) {
            if (metricRule == null) {
                continue;
            }
            String metricRuleName = trimToEmpty(metricRule.getString("metricRuleName"));
            String aggregationType = trimToEmpty(metricRule.getString("aggregationType")).toUpperCase(Locale.ROOT);
            String remark = trimToEmpty(metricRule.getString("remark"));
            if (aggregationType.contains("RATIO")
                    || aggregationType.contains("COUNT_IF")
                    || aggregationType.contains("/")
                    || containsAny(metricRuleName, "占比", "比例", "率")
                    || containsAny(remark, "占比", "比例", "分子", "分母")) {
                return true;
            }
        }
        return false;
    }

    private boolean hasCountMetricRule(List<JSONObject> matchedMetricRules) {
        if (matchedMetricRules == null || matchedMetricRules.isEmpty()) {
            return false;
        }
        for (JSONObject metricRule : matchedMetricRules) {
            if (metricRule == null) {
                continue;
            }
            String metricRuleName = trimToEmpty(metricRule.getString("metricRuleName"));
            String aggregationType = trimToEmpty(metricRule.getString("aggregationType")).toUpperCase(Locale.ROOT);
            if (aggregationType.contains("COUNT")
                    || containsAny(metricRuleName, "数量", "总量", "总数", "计数")) {
                return true;
            }
        }
        return false;
    }

    private boolean needsDictionaryMapping(String question, String expectedAction) {
        return containsAny(question, "在线", "离线", "启用", "停用", "开启", "关闭", "状态", "类型", "未处理", "已处理",
                "待处理", "处理中", "已完成", "已发布", "未发布", "成功", "失败", "异常", "正常", "禁用")
                || containsAny(expectedAction, "字典", "枚举", "状态", "类型", "映射");
    }

    private boolean looksLikeCurrentMetricIntent(String question, String expectedAction) {
        return containsAny(question, "当前", "实时", "现在", "此刻")
                || containsAny(expectedAction, "当前", "实时", "实时值");
    }

    private boolean looksLikeHistoryMetricIntent(String question, String expectedAction) {
        return containsAny(question, "历史", "趋势", "最近", "近", "过去", "今天", "昨天", "昨日", "当天", "本周", "本月", "小时", "总计", "累计")
                || containsAny(expectedAction, "历史", "趋势", "时间窗口", "总计", "聚合");
    }

    private boolean looksLikeRuntimeMetricQuestion(String question) {
        boolean timeIntent = containsAny(question, "当前", "实时", "现在", "此刻", "历史", "趋势", "最近", "今天", "昨天", "昨日", "当天", "总计");
        boolean metricIntent = containsAny(question, "温度", "湿度", "电压", "电流", "功率", "电量", "电能", "亮度",
                "光照", "压力", "流量", "液位", "转速", "档位", "开关", "状态", "灯光", "色值", "电池", "信号",
                "二氧化碳", "co2", "pm2.5", "pm10");
        return timeIntent && metricIntent;
    }

    private boolean isRuntimeExpected(String expectedSource, String allowedTablesText) {
        String actualText = (trimToEmpty(expectedSource) + ";" + trimToEmpty(allowedTablesText)).toUpperCase(Locale.ROOT);
        return actualText.contains("REDIS")
                || actualText.contains("TSDB")
                || actualText.contains("HYBRID")
                || actualText.contains("RUNTIME")
                || actualText.contains("运行时");
    }

    private boolean hasCountIntent(String question) {
        return containsAny(question, "数量", "总数", "统计", "多少个", "多少", "几个", "几条", "几笔", "有多少", "count");
    }

    private boolean isAlertLogFactQuestion(String normalizedQuestion) {
        if (StringUtils.isBlank(normalizedQuestion)) {
            return false;
        }
        return containsAny(normalizedQuestion,
                "未处理告警", "待处理告警", "未处置告警", "待处置告警",
                "未处理报警", "待处理报警", "未处置报警", "待处置报警",
                "告警记录", "报警记录", "告警日志", "报警日志", "告警趋势", "报警趋势");
    }

    private boolean isEnumLikeColumn(String columnName) {
        return "status".equals(columnName)
                || "type".equals(columnName)
                || "del_flag".equals(columnName)
                || columnName.startsWith("is_")
                || columnName.startsWith("enable")
                || columnName.endsWith("_status")
                || columnName.endsWith("_type")
                || columnName.endsWith("_flag");
    }

    private boolean containsAny(String text, String... keywords) {
        if (StringUtils.isBlank(text) || keywords == null) {
            return false;
        }
        for (String keyword : keywords) {
            if (StringUtils.isNotBlank(keyword) && text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private List<String> limitIssueMessages(List<AiKnowledgeVersionQualityIssueVO> issues, int limit) {
        if (issues == null || issues.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        int actualLimit = Math.min(issues.size(), limit);
        for (int index = 0; index < actualLimit; index++) {
            result.add(buildIssueSummary(issues.get(index)));
        }
        if (issues.size() > limit) {
            result.add("其余 " + (issues.size() - limit) + " 项已省略");
        }
        return result;
    }

    private String buildIssueSummary(AiKnowledgeVersionQualityIssueVO issue) {
        if (issue == null) {
            return "未知阻断项";
        }
        StringBuilder builder = new StringBuilder();
        if (issue.getRowNum() != null) {
            builder.append("第 ").append(issue.getRowNum()).append(" 行");
        }
        if (StringUtils.isNotBlank(issue.getQuestion())) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append("问句“").append(issue.getQuestion()).append("”");
        }
        if (builder.length() > 0 && StringUtils.isNotBlank(issue.getMessage())) {
            builder.append("：");
        }
        builder.append(StringUtils.isBlank(issue.getMessage()) ? "未知阻断项" : issue.getMessage());
        return builder.toString();
    }

    private int resolveRowNum(JSONObject item, int index) {
        Integer rowNum = item == null ? null : item.getInteger("rowNum");
        return rowNum == null || rowNum <= 0 ? index + 2 : rowNum;
    }

    private boolean isNl2SqlKnowledgeBase(AiKnowledgeBase knowledgeBase) {
        return knowledgeBase != null && KB_TYPE_NL2SQL.equalsIgnoreCase(trimToEmpty(knowledgeBase.getKbType()));
    }

    private String normalizeRiskLevel(String riskLevel) {
        if (StringUtils.isBlank(riskLevel)) {
            return "P1";
        }
        String actualRiskLevel = riskLevel.trim().toUpperCase(Locale.ROOT);
        if ("P0".equals(actualRiskLevel) || "P1".equals(actualRiskLevel) || "P2".equals(actualRiskLevel)) {
            return actualRiskLevel;
        }
        return "P1";
    }

    private String normalizeText(String text) {
        return StringUtils.isBlank(text) ? "" : text.toLowerCase(Locale.ROOT)
                .replace("`", "")
                .replace("_", "")
                .replaceAll("\\s+", "");
    }

    private String normalizeTableName(String table) {
        if (StringUtils.isBlank(table)) {
            return "";
        }
        String actualTable = table.trim().replace("`", "");
        int index = actualTable.lastIndexOf('.');
        if (index >= 0) {
            actualTable = actualTable.substring(index + 1);
        }
        return actualTable.toLowerCase(Locale.ROOT);
    }

    private String normalizeColumnName(String columnName) {
        return StringUtils.isBlank(columnName) ? "" : columnName.trim().toLowerCase(Locale.ROOT);
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeLookupKey(String value) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        return value.trim()
                .toLowerCase(Locale.ROOT)
                .replace("_", "")
                .replace("-", "")
                .replace("+", "")
                .replace("/", "")
                .replace("（", "")
                .replace("）", "")
                .replace("(", "")
                .replace(")", "")
                .replaceAll("\\s+", "");
    }

    private static final class GateContext {

        private final JSONArray items;
        private final List<JSONObject> metricRules = new ArrayList<>();
        private final Set<String> tableNames = new LinkedHashSet<>();
        private final Set<String> businessObjectKeys = new LinkedHashSet<>();
        private final Map<String, JSONObject> tableSemantics = new LinkedHashMap<>();

        private GateContext(JSONObject snapshot) {
            this.items = snapshot.getJSONArray("items") == null ? new JSONArray() : snapshot.getJSONArray("items");
            JSONArray tables = snapshot.getJSONArray("tables");
            if (tables != null) {
                for (int index = 0; index < tables.size(); index++) {
                    addTable(tables.getString(index));
                }
            }
            for (int index = 0; index < items.size(); index++) {
                JSONObject item = items.getJSONObject(index);
                if (item != null) {
                    addTable(item.getString("tableName"));
                }
            }
            JSONArray tableSemanticArray = snapshot.getJSONArray("tableSemantics");
            if (tableSemanticArray != null) {
                for (int index = 0; index < tableSemanticArray.size(); index++) {
                    JSONObject item = tableSemanticArray.getJSONObject(index);
                    if (item == null) {
                        continue;
                    }
                    String tableName = normalizeTableNameStatic(item.getString("tableName"));
                    if (StringUtils.isNotBlank(tableName)) {
                        tableSemantics.put(tableName, item);
                        tableNames.add(tableName);
                    }
                }
            }
            JSONArray metricRuleArray = snapshot.getJSONArray("metricRules");
            if (metricRuleArray != null) {
                for (int index = 0; index < metricRuleArray.size(); index++) {
                    JSONObject item = metricRuleArray.getJSONObject(index);
                    if (item != null) {
                        metricRules.add(JSON.parseObject(JSON.toJSONString(item)));
                    }
                }
            }
            JSONArray businessObjectArray = snapshot.getJSONArray("businessObjects");
            if (businessObjectArray != null) {
                for (int index = 0; index < businessObjectArray.size(); index++) {
                    JSONObject item = businessObjectArray.getJSONObject(index);
                    if (item == null) {
                        continue;
                    }
                    addBusinessObjectKey(item.getString("businessObjectCode"));
                    addBusinessObjectKey(item.getString("businessObjectName"));
                    addBusinessObjectKey(item.getString("primaryTable"));
                    for (String alias : splitBusinessObjectAliases(item.getString("aliases"))) {
                        addBusinessObjectKey(alias);
                    }
                }
            }
        }

        private void addTable(String tableName) {
            String normalizedTable = normalizeTableNameStatic(tableName);
            if (StringUtils.isNotBlank(normalizedTable)) {
                tableNames.add(normalizedTable);
            }
        }

        private void addBusinessObjectKey(String value) {
            String normalizedKey = normalizeLookupKey(value);
            if (StringUtils.isNotBlank(normalizedKey)) {
                businessObjectKeys.add(normalizedKey);
            }
        }

        private boolean containsBusinessObject(String value) {
            return businessObjectKeys.contains(normalizeLookupKey(value));
        }

        private static List<String> splitBusinessObjectAliases(String value) {
            List<String> result = new ArrayList<>();
            if (StringUtils.isBlank(value)) {
                return result;
            }
            for (String item : value.split("[,;，；\\r\\n]+")) {
                if (StringUtils.isNotBlank(item)) {
                    result.add(item.trim());
                }
            }
            return result;
        }

        private static String normalizeLookupKey(String value) {
            if (StringUtils.isBlank(value)) {
                return "";
            }
            return value.trim()
                    .toLowerCase(Locale.ROOT)
                    .replace("_", "")
                    .replace("-", "")
                    .replace("+", "")
                    .replace("/", "")
                    .replace("（", "")
                    .replace("）", "")
                    .replace("(", "")
                    .replace(")", "")
                    .replaceAll("\\s+", "");
        }

        private static String normalizeTableNameStatic(String table) {
            if (StringUtils.isBlank(table)) {
                return "";
            }
            String actualTable = table.trim().replace("`", "");
            int index = actualTable.lastIndexOf('.');
            if (index >= 0) {
                actualTable = actualTable.substring(index + 1);
            }
            return actualTable.toLowerCase(Locale.ROOT);
        }
    }
}
