package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import com.fastbee.ai.config.properties.FastBeeAiProperties;
import com.fastbee.ai.constant.AiPromptConstant;
import com.fastbee.ai.model.vo.AiModelRouteVO;
import com.fastbee.ai.model.vo.AiNl2SqlStructuredResultVO;
import com.fastbee.ai.model.vo.AiSemanticContextVO;
import com.fastbee.ai.service.AiChatModelFactoryService;
import com.fastbee.ai.service.IAiDeviceResolveService;
import com.fastbee.ai.service.IAiNl2SqlGenerateService;
import com.fastbee.ai.service.IAiQueryPromptContextAssembler;
import com.fastbee.ai.service.IAiSemanticNormalizationService;
import com.fastbee.ai.support.AiNl2SqlStructuredParser;
import com.fastbee.ai.support.AiReplyLanguageSupport;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.mybatis.enums.DataBaseType;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.cache.ITSLCache;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;

/**
 * AI 智能问数结构化生成服务实现。
 */
@Service
public class AiNl2SqlGenerateServiceImpl implements IAiNl2SqlGenerateService {

    private static final Pattern SQL_TABLE_PATTERN = Pattern.compile("(?i)\\b(?:from|join)\\s+([`\\w.]+)");

    @Resource
    private FastBeeAiProperties properties;

    @Resource
    private AiChatModelFactoryService aiChatModelFactoryService;

    @Resource
    private AiNl2SqlStructuredParser aiNl2SqlStructuredParser;

    @Resource
    private IAiSemanticNormalizationService aiSemanticNormalizationService;

    @Resource
    private IAiQueryPromptContextAssembler aiQueryPromptContextAssembler;

    @Resource
    private IAiDeviceResolveService aiDeviceResolveService;

    @Resource
    private ITSLCache itslCache;

    @Override
    public AiNl2SqlStructuredResultVO generateStructured(String question, AiModelRouteVO route) {
        if (StringUtils.isBlank(question)) {
            throw new ServiceException(message("ai.nl2sql.question.required"));
        }
        if (route == null) {
            throw new ServiceException(message("ai.nl2sql.model.route.required"));
        }

        AiSemanticContextVO semanticContext = aiSemanticNormalizationService.buildNl2SqlContext(question);
        AiNl2SqlStructuredResultVO deterministicResult = tryBuildDeterministicStructuredResult(question, semanticContext);
        if (deterministicResult != null) {
            validateBusinessSemantic(question, semanticContext, deterministicResult);
            return deterministicResult;
        }
        ChatModel chatModel = aiChatModelFactoryService.resolveChatModel(route);
        String prompt = buildPrompt(question, semanticContext);
        String modelResponse = chatModel.call(prompt);
        AiNl2SqlStructuredResultVO result = aiNl2SqlStructuredParser.parse(question, modelResponse);
        if (StringUtils.isBlank(result.getSql())) {
            throw new ServiceException(message("ai.nl2sql.structured.sql.required"));
        }
        validateBusinessSemantic(question, semanticContext, result);
        if (StringUtils.isBlank(result.getSummary())) {
            result.setSummary(message("ai.nl2sql.summary.generated"));
        }
        if (result.getConfidence() == null) {
            result.setConfidence(Boolean.TRUE.equals(result.getStructuredOutput()) ? 0.85D : 0.60D);
        }
        return result;
    }

    private AiNl2SqlStructuredResultVO tryBuildDeterministicStructuredResult(String question, AiSemanticContextVO semanticContext) {
        String normalizedQuestion = normalizeText(question);
        if (isAlertTrendQuestion(normalizedQuestion)) {
            int recentDays = resolveRecentDays(question);
            return buildAlertTrendStructuredResult(question, recentDays, resolveCurrentDatabaseType());
        }
        if (isAlertProcessCountQuestion(normalizedQuestion)) {
            return buildAlertProcessCountStructuredResult(question);
        }
        return null;
    }

    private String buildPrompt(String question, AiSemanticContextVO semanticContext) {
        StringBuilder builder = new StringBuilder(AiPromptConstant.NL2SQL_PROMPT_TEMPLATE);
        if (semanticContext != null) {
            if (semanticContext.getMatched() != null && semanticContext.getMatched()) {
                builder.append("本次问题已命中语义条数：").append(semanticContext.getMatchedBindings()).append('\n');
            }
            if (semanticContext.getFallbackUsed() != null && semanticContext.getFallbackUsed()) {
                builder.append("本次语义命中较弱，请优先参考候选字段定义。").append('\n');
            }
            if (semanticContext.getKnowledgeBases() != null && !semanticContext.getKnowledgeBases().isEmpty()) {
                builder.append("命中的知识库：").append(String.join("、", semanticContext.getKnowledgeBases())).append('\n');
            }
            if (semanticContext.getBusinessObjects() != null && !semanticContext.getBusinessObjects().isEmpty()) {
                builder.append("命中的业务对象：").append(String.join("、", semanticContext.getBusinessObjects())).append('\n');
            }
            if (semanticContext.getPrimaryTables() != null && !semanticContext.getPrimaryTables().isEmpty()) {
                builder.append("主事实表候选：").append(String.join("、", semanticContext.getPrimaryTables())).append('\n');
            }
            if (semanticContext.getMetricRules() != null && !semanticContext.getMetricRules().isEmpty()) {
                builder.append("指标口径候选：").append(String.join("、", semanticContext.getMetricRules())).append('\n');
            }
            if (semanticContext.getVersionNos() != null && !semanticContext.getVersionNos().isEmpty()) {
                builder.append("命中的版本：").append(String.join("、", semanticContext.getVersionNos())).append('\n');
            }
        }
        String runtimeMetricError = resolveRuntimeMetricFallbackError(question, semanticContext);
        if (StringUtils.isNotBlank(runtimeMetricError)) {
            throw new ServiceException(runtimeMetricError);
        }
        List<String> promptLines = aiQueryPromptContextAssembler.assembleNl2SqlPromptLines(semanticContext);
        if (promptLines.isEmpty()) {
            throw new ServiceException(message("ai.nl2sql.semantic.not.matched"));
        }
        for (String line : promptLines) {
            builder.append("- ").append(line).append('\n');
        }
        if (StringUtils.isNotBlank(properties.getNl2sql().getSchemaPrompt())) {
            builder.append("补充业务知识：").append('\n');
            builder.append(properties.getNl2sql().getSchemaPrompt()).append('\n');
        }
        builder.append(AiReplyLanguageSupport.buildModelInstruction(question, null, AiReplyLanguageSupport.currentLocale())).append('\n');
        builder.append("用户问题：").append(question);
        return builder.toString();
    }

    private void validateBusinessSemantic(String question, AiSemanticContextVO semanticContext,
                                          AiNl2SqlStructuredResultVO result) {
        if (result == null || StringUtils.isBlank(result.getSql())) {
            return;
        }
        String normalizedQuestion = normalizeText(question);
        String normalizedSql = normalizeSql(result.getSql());
        Set<String> tables = resolveSqlTables(result);

        validateProductCount(question, normalizedQuestion, normalizedSql, tables);
        validateDeviceCount(question, normalizedQuestion, tables);
        validateUserCount(question, normalizedQuestion, normalizedSql, tables);
        validateAlertSemantic(normalizedQuestion, tables);
        validatePrimaryTableByBusinessObject(question, normalizedQuestion, tables, semanticContext);
        validateRuntimeMetricSql(question, normalizedQuestion, normalizedSql, semanticContext);
    }

    private void validateProductCount(String question, String normalizedQuestion, String normalizedSql, Set<String> tables) {
        if (!hasCountIntent(normalizedQuestion) || !normalizedQuestion.contains("产品")) {
            return;
        }
        if (normalizedQuestion.contains("设备") && !containsAny(question, "有设备的产品", "接入设备的产品")) {
            return;
        }
        if (containsAny(question, "每个产品", "按产品", "产品下设备", "产品的设备", "有设备的产品", "接入设备的产品")) {
            return;
        }
        if (tables.contains("iot_product")) {
            return;
        }
        if (tables.contains("iot_device") && containsAny(normalizedSql, "count(distinctproduct_id)", "count(distinct`product_id`)")) {
                throw new ServiceException(message("ai.nl2sql.semantic.guard.product.total.device.rejected"));
        }
        if (!tables.isEmpty()) {
                throw new ServiceException(message("ai.nl2sql.semantic.guard.product.total.primary.required"));
        }
    }

    private void validateDeviceCount(String question, String normalizedQuestion, Set<String> tables) {
        if (!hasCountIntent(normalizedQuestion) || !normalizedQuestion.contains("设备")) {
            return;
        }
        if (containsAny(question, "设备日志", "设备记录", "设备用户", "设备告警", "设备工单", "设备分享", "设备授权")) {
            return;
        }
        if (tables.contains("iot_device")) {
            return;
        }
        if (containsAnyTable(tables, "_log", "_record", "_user", "_share", "_authorize")) {
                throw new ServiceException(message("ai.nl2sql.semantic.guard.device.total.primary.required"));
        }
    }

    private void validateUserCount(String question, String normalizedQuestion, String normalizedSql, Set<String> tables) {
        if (!hasCountIntent(normalizedQuestion) || !normalizedQuestion.contains("用户")) {
            return;
        }
        if (containsAny(question, "设备用户", "授权用户", "分享用户", "绑定用户", "关联用户")) {
            return;
        }
        if (tables.contains("sys_user")) {
            return;
        }
        if (containsAnyTable(tables, "_user") || normalizedSql.contains("count(distinctuser_id)")) {
                throw new ServiceException(message("ai.nl2sql.semantic.guard.user.total.primary.required"));
        }
    }

    private void validateAlertSemantic(String normalizedQuestion, Set<String> tables) {
        if (StringUtils.isBlank(normalizedQuestion) || tables == null || tables.isEmpty()) {
            return;
        }
        if (hasCountIntent(normalizedQuestion) && containsAny(normalizedQuestion, "告警规则", "报警规则")) {
            if (tables.contains("iot_alert")) {
                return;
            }
                throw new ServiceException(message("ai.nl2sql.semantic.guard.alert.rule.primary.required"));
        }
        if (!isAlertLogFactQuestion(normalizedQuestion)) {
            return;
        }
        if (tables.contains("iot_alert_log")) {
            return;
        }
        throw new ServiceException(message("ai.nl2sql.semantic.guard.alert.log.primary.required"));
    }

    private AiNl2SqlStructuredResultVO buildAlertProcessCountStructuredResult(String question) {
        String sql = "SELECT COUNT(*) AS pending_alert_count FROM iot_alert_log WHERE status = 2";
        JSONObject payload = new JSONObject();
        payload.put("sql", sql);
        payload.put("summary", message("ai.nl2sql.summary.alert.process.count"));
        payload.put("confidence", 0.99D);
        payload.put("tables", List.of("iot_alert_log"));

        AiNl2SqlStructuredResultVO result = new AiNl2SqlStructuredResultVO();
        result.setQuestion(question);
        result.setSql(sql);
        result.setSummary(payload.getString("summary"));
        result.setConfidence(0.99D);
        result.setTables(new ArrayList<>(List.of("iot_alert_log")));
        result.setStructuredPayload(payload.toJSONString());
        result.setStructuredOutput(Boolean.TRUE);
        result.setParseStatus("SUCCESS");
        result.setModelResponse(null);
        return result;
    }

    private AiNl2SqlStructuredResultVO buildAlertTrendStructuredResult(String question, int recentDays, DataBaseType dataBaseType) {
        int actualDays = Math.max(recentDays, 1);
        String sql = buildAlertTrendSql(actualDays, dataBaseType);
        JSONObject payload = new JSONObject();
        payload.put("sql", sql);
        payload.put("summary", message("ai.nl2sql.summary.alert.trend", actualDays));
        payload.put("confidence", 0.99D);
        payload.put("tables", List.of("iot_alert_log"));

        AiNl2SqlStructuredResultVO result = new AiNl2SqlStructuredResultVO();
        result.setQuestion(question);
        result.setSql(sql);
        result.setSummary(payload.getString("summary"));
        result.setConfidence(0.99D);
        result.setTables(new ArrayList<>(List.of("iot_alert_log")));
        result.setStructuredPayload(payload.toJSONString());
        result.setStructuredOutput(Boolean.TRUE);
        result.setParseStatus("SUCCESS");
        result.setModelResponse(null);
        return result;
    }

    private String buildAlertTrendSql(int recentDays, DataBaseType dataBaseType) {
        int offsetDays = Math.max(recentDays - 1, 0);
        DataBaseType actualType = dataBaseType == null ? DataBaseType.MY_SQL : dataBaseType;
        return switch (actualType) {
            case ORACLE -> "SELECT TRUNC(create_time) AS stat_date, COUNT(*) AS alert_count "
                    + "FROM iot_alert_log WHERE create_time >= TRUNC(CURRENT_DATE) - " + offsetDays
                    + " GROUP BY TRUNC(create_time) ORDER BY stat_date ASC";
            case POSTGRE_SQL -> "SELECT DATE(create_time) AS stat_date, COUNT(*) AS alert_count "
                    + "FROM iot_alert_log WHERE create_time >= CURRENT_DATE - INTERVAL '" + offsetDays
                    + " day' GROUP BY DATE(create_time) ORDER BY stat_date ASC";
            case SQL_SERVER -> "SELECT CAST(create_time AS DATE) AS stat_date, COUNT(*) AS alert_count "
                    + "FROM iot_alert_log WHERE create_time >= DATEADD(DAY, -" + offsetDays
                    + ", CAST(GETDATE() AS DATE)) GROUP BY CAST(create_time AS DATE) ORDER BY stat_date ASC";
            default -> "SELECT DATE(create_time) AS stat_date, COUNT(*) AS alert_count "
                    + "FROM iot_alert_log WHERE create_time >= DATE_SUB(CURRENT_DATE, INTERVAL " + offsetDays
                    + " DAY) GROUP BY DATE(create_time) ORDER BY stat_date ASC";
        };
    }

    private DataBaseType resolveCurrentDatabaseType() {
        try {
            DataBaseType dataBaseType = DataScopeAspect.getDataBaseType(DataScopeAspect.DEFAULT_DATASOURCE_NAME);
            return dataBaseType == null ? DataBaseType.MY_SQL : dataBaseType;
        } catch (Exception ignored) {
            return DataBaseType.MY_SQL;
        }
    }

    private boolean isAlertTrendQuestion(String normalizedQuestion) {
        if (StringUtils.isBlank(normalizedQuestion)) {
            return false;
        }
        if (containsAny(normalizedQuestion, "告警趋势", "报警趋势", "告警变化", "报警变化")) {
            return true;
        }
        return containsAny(normalizedQuestion, "按天统计告警", "按天统计报警", "每日告警", "每天告警", "每日报警", "每天报警");
    }

    private boolean isAlertProcessCountQuestion(String normalizedQuestion) {
        if (StringUtils.isBlank(normalizedQuestion) || !hasCountIntent(normalizedQuestion)) {
            return false;
        }
        if (!containsAny(normalizedQuestion, "告警", "报警")) {
            return false;
        }
        if (containsAny(normalizedQuestion, "告警规则", "报警规则")) {
            return false;
        }
        return containsAny(normalizedQuestion, "未处理", "待处理", "未处置", "待处置", "待办");
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

    private int resolveRecentDays(String question) {
        String normalizedQuestion = normalizeText(question);
        Matcher digitMatcher = Pattern.compile("(\\d+)天").matcher(normalizedQuestion);
        if (digitMatcher.find()) {
            try {
                return Integer.parseInt(digitMatcher.group(1));
            } catch (NumberFormatException ignored) {
                // 忽略数字解析异常，继续使用中文数字或默认值。
            }
        }
        if (normalizedQuestion.contains("今天") || normalizedQuestion.contains("今日")) {
            return 1;
        }
        if (normalizedQuestion.contains("昨天") || normalizedQuestion.contains("昨日")) {
            return 1;
        }
        if (normalizedQuestion.contains("近七天") || normalizedQuestion.contains("最近七天")) {
            return 7;
        }
        if (normalizedQuestion.contains("近三天") || normalizedQuestion.contains("最近三天")) {
            return 3;
        }
        if (normalizedQuestion.contains("近三十天") || normalizedQuestion.contains("最近三十天")) {
            return 30;
        }
        return 7;
    }

    private void validatePrimaryTableByBusinessObject(String question, String normalizedQuestion, Set<String> tables,
                                                      AiSemanticContextVO semanticContext) {
        if (!hasCountIntent(normalizedQuestion) || semanticContext == null || tables == null || tables.isEmpty()) {
            return;
        }
        BusinessSemanticCandidate candidate = resolveDominantBusinessSemantic(semanticContext);
        if (candidate == null || StringUtils.isBlank(candidate.primaryTable)) {
            return;
        }
        if (StringUtils.isBlank(candidate.businessObjectName) && StringUtils.isBlank(candidate.defaultMetricRule)) {
            return;
        }
        if (tables.contains(candidate.primaryTable)) {
            return;
        }
        if (looksLikeRuntimePrimaryCandidate(candidate)) {
            return;
        }
        if (isExplicitNonPrimaryCountQuestion(question, normalizedQuestion)) {
            return;
        }
        throw new ServiceException(message("ai.nl2sql.semantic.guard.business.primary.required",
                defaultIfBlank(candidate.businessObjectName, candidate.primaryTable), candidate.primaryTable));
    }

    private void validateRuntimeMetricSql(String question, String normalizedQuestion, String normalizedSql,
                                          AiSemanticContextVO semanticContext) {
        if (!looksLikeRuntimeMetricQuestion(question, semanticContext)) {
            return;
        }
        if (!containsAny(normalizedSql, "temperature", "humidity", "power", "electricity", "voltage", "current",
                "pm10", "pm25", "pm2.5", "co2", "thingmodelvalue", "thingsmodelvalue", "things_model_value")) {
            return;
        }
        throw new ServiceException(message("ai.nl2sql.semantic.guard.runtime.metric.route.required"));
    }

    private Set<String> resolveSqlTables(AiNl2SqlStructuredResultVO result) {
        Set<String> tables = new LinkedHashSet<>();
        if (result.getTables() != null) {
            for (String table : result.getTables()) {
                String normalizedTable = normalizeTableName(table);
                if (StringUtils.isNotBlank(normalizedTable)) {
                    tables.add(normalizedTable);
                }
            }
        }
        Matcher matcher = SQL_TABLE_PATTERN.matcher(result.getSql());
        while (matcher.find()) {
            String normalizedTable = normalizeTableName(matcher.group(1));
            if (StringUtils.isNotBlank(normalizedTable)) {
                tables.add(normalizedTable);
            }
        }
        return tables;
    }

    private boolean hasCountIntent(String normalizedQuestion) {
        return containsAny(normalizedQuestion, "数量", "总数", "统计", "多少个", "多少", "几个", "几条", "几笔", "有多少", "count");
    }

    private BusinessSemanticCandidate resolveDominantBusinessSemantic(AiSemanticContextVO semanticContext) {
        if (semanticContext == null || semanticContext.getFields() == null || semanticContext.getFields().isEmpty()) {
            return null;
        }
        Map<String, BusinessSemanticCandidate> candidateMap = new LinkedHashMap<>();
        for (com.fastbee.ai.model.vo.AiSemanticFieldVO field : semanticContext.getFields()) {
            if (field == null || StringUtils.isBlank(field.getPrimaryTable())) {
                continue;
            }
            String key = trimToEmpty(field.getPrimaryTable()) + "|" + trimToEmpty(field.getBusinessObjectName());
            BusinessSemanticCandidate candidate = candidateMap.computeIfAbsent(key, ignored -> new BusinessSemanticCandidate());
            candidate.primaryTable = trimToEmpty(field.getPrimaryTable());
            candidate.businessObjectName = defaultIfBlank(candidate.businessObjectName, field.getBusinessObjectName());
            candidate.defaultMetricRule = defaultIfBlank(candidate.defaultMetricRule, field.getDefaultMetricRule());
            candidate.defaultDataSource = defaultIfBlank(candidate.defaultDataSource, field.getDefaultDataSource());
            int weight = field.getMatchScore() == null || field.getMatchScore() <= 0 ? 1 : field.getMatchScore();
            candidate.score += weight;
            candidate.fieldCount++;
        }
        if (candidateMap.isEmpty()) {
            return null;
        }
        List<BusinessSemanticCandidate> candidates = new ArrayList<>(candidateMap.values());
        candidates.sort((left, right) -> {
            int scoreCompare = Integer.compare(right.score, left.score);
            if (scoreCompare != 0) {
                return scoreCompare;
            }
            return Integer.compare(right.fieldCount, left.fieldCount);
        });
        if (candidates.size() == 1) {
            return candidates.get(0);
        }
        BusinessSemanticCandidate top = candidates.get(0);
        BusinessSemanticCandidate second = candidates.get(1);
        if (!StringUtils.equalsIgnoreCase(top.primaryTable, second.primaryTable)
                && top.score <= second.score + 6) {
            return null;
        }
        return top;
    }

    private boolean containsAnyTable(Set<String> tables, String... fragments) {
        if (tables == null || tables.isEmpty() || fragments == null) {
            return false;
        }
        for (String table : tables) {
            for (String fragment : fragments) {
                if (StringUtils.isNotBlank(fragment) && table.contains(fragment)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String normalizeSql(String sql) {
        return StringUtils.isBlank(sql) ? "" : sql.toLowerCase(Locale.ROOT)
                .replace("`", "")
                .replaceAll("\\s+", "");
    }

    private String normalizeText(String text) {
        return StringUtils.isBlank(text) ? "" : text.toLowerCase(Locale.ROOT)
                .replace("`", "")
                .replace("_", "")
                .replaceAll("\\s+", "");
    }

    private boolean isExplicitNonPrimaryCountQuestion(String question, String normalizedQuestion) {
        if (containsAny(question, "有设备的产品", "接入设备的产品", "设备日志", "设备记录", "授权用户", "分享用户", "绑定用户")) {
            return true;
        }
        return containsAny(normalizedQuestion, "每个", "各", "按", "分组", "关联", "绑定", "授权", "分享", "日志", "记录", "明细");
    }

    private boolean looksLikeRuntimePrimaryCandidate(BusinessSemanticCandidate candidate) {
        if (candidate == null) {
            return false;
        }
        String defaultDataSource = trimToEmpty(candidate.defaultDataSource).toUpperCase(Locale.ROOT);
        return defaultDataSource.contains("REDIS")
                || defaultDataSource.contains("TSDB")
                || defaultDataSource.contains("HYBRID")
                || defaultDataSource.contains("RUNTIME");
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

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private String defaultIfBlank(String currentValue, String candidateValue) {
        return StringUtils.isBlank(currentValue) ? trimToEmpty(candidateValue) : currentValue;
    }

    private boolean looksLikeRuntimeMetricQuestion(String question, AiSemanticContextVO semanticContext) {
        if (StringUtils.isBlank(question)) {
            return false;
        }
        String normalizedQuestion = normalizeText(question);
        if (semanticContext != null
                && semanticContext.getRuntimeFields() != null
                && !semanticContext.getRuntimeFields().isEmpty()) {
            return false;
        }
        if (containsAny(normalizedQuestion, "数量", "个数", "总数", "多少个", "几条", "几笔")) {
            return false;
        }
        boolean timeIntent = containsAny(normalizedQuestion, "当前", "实时", "现在", "此刻", "历史", "趋势", "最近", "今天", "昨天", "昨日",
                "数值是多少", "值是多少", "是多少", "多少", "数值", "读数");
        boolean metricIntent = containsAny(normalizedQuestion, "温度", "湿度", "电压", "电流", "功率", "电量", "电能", "亮度",
                "光照", "压力", "流量", "液位", "转速", "档位", "开关", "状态", "二氧化碳", "co2", "pm2.5", "pm10");
        return timeIntent && metricIntent;
    }

    private String resolveRuntimeMetricFallbackError(String question, AiSemanticContextVO semanticContext) {
        if (!looksLikeRuntimeMetricQuestion(question, semanticContext)) {
            return null;
        }
        DeviceMetaData deviceMetaData = aiDeviceResolveService.resolveOptionalDeviceMetaData(question);
        if (deviceMetaData == null || deviceMetaData.getDevice() == null) {
            return "当前问题更像设备运行时指标问数，但还未成功解析到设备或物模型，请先明确设备名称/设备编号，或在会话中先完成设备与物模型选择。";
        }
        if (deviceMetaData.getProduct() == null || deviceMetaData.getProduct().getProductId() == null) {
            return "已定位设备 " + deviceMetaData.getDevice().getDeviceName() + "，但未能解析到所属产品，暂无法执行运行时指标问数。";
        }
        List<ThingsModelValueItem> thingsModelList = loadThingsModels(deviceMetaData.getProduct().getProductId());
        if (thingsModelList.isEmpty()) {
            return "已定位设备 " + deviceMetaData.getDevice().getDeviceName() + "，但该设备所属产品未配置可用物模型，暂无法执行运行时指标问数。";
        }
        return "已定位设备 " + deviceMetaData.getDevice().getDeviceName() + "，但当前问句未命中可用物模型，请补充更明确的指标名称或先完成物模型选择。";
    }

    private List<ThingsModelValueItem> loadThingsModels(Long productId) {
        if (productId == null) {
            return List.of();
        }
        try {
            List<ThingsModelValueItem> items = itslCache.getThingsModelList(productId);
            return items == null ? List.of() : items;
        } catch (Exception ignored) {
            return List.of();
        }
    }

    private boolean containsAny(String question, String... keywords) {
        if (StringUtils.isBlank(question) || keywords == null) {
            return false;
        }
        for (String keyword : keywords) {
            if (StringUtils.isNotBlank(keyword) && question.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private static final class BusinessSemanticCandidate {

        private String businessObjectName;
        private String primaryTable;
        private String defaultMetricRule;
        private String defaultDataSource;
        private int score;
        private int fieldCount;
    }

}
