package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastbee.ai.config.properties.FastBeeAiProperties;
import com.fastbee.ai.domain.AiKnowledgeBase;
import com.fastbee.ai.domain.AiKnowledgeVersion;
import com.fastbee.ai.model.vo.AiSemanticContextVO;
import com.fastbee.ai.model.vo.AiSemanticFieldVO;
import com.fastbee.ai.model.vo.AiSemanticRuntimeBundleVO;
import com.fastbee.ai.model.vo.AiSemanticValueMappingVO;
import com.fastbee.ai.service.IAiKnowledgeBaseService;
import com.fastbee.ai.service.IAiKnowledgeVersionService;
import com.fastbee.ai.service.IAiSemanticNormalizationService;
import com.fastbee.ai.service.IAiSemanticRuntimeProvider;
import com.fastbee.ai.service.IAiSemanticRuntimeStore;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.entity.SysDictData;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.system.service.ISysDictTypeService;

/**
 * 问数语义归一服务。
 */
@Service
public class AiSemanticNormalizationServiceImpl implements IAiSemanticNormalizationService {

    private static final String KB_TYPE_NL2SQL = "NL2SQL_SEMANTIC";
    private static final Long GLOBAL_TENANT_ID = 0L;
    private static final String SOURCE_DICT = "DICT";
    private static final String ROUTE_AUTO = "AUTO";
    private static final String SOURCE_TEMPLATE = "TEMPLATE";
    private static final String SOURCE_TEMPLATE_DICT = "TEMPLATE_DICT";

    @Resource
    private IAiKnowledgeBaseService aiKnowledgeBaseService;

    @Resource
    private IAiKnowledgeVersionService aiKnowledgeVersionService;

    @Resource
    private ISysDictTypeService sysDictTypeService;

    @Resource
    private FastBeeAiProperties fastBeeAiProperties;

    @Autowired(required = false)
    private List<IAiSemanticRuntimeStore> aiSemanticRuntimeStores = Collections.emptyList();

    @Autowired(required = false)
    private List<IAiSemanticRuntimeProvider> aiSemanticRuntimeProviders = Collections.emptyList();

    @Override
    public AiSemanticContextVO buildNl2SqlContext(String question) {
        AiSemanticContextVO context = new AiSemanticContextVO();
        context.setQuestion(question);

        List<AiSemanticFieldVO> allFields = loadActiveNl2SqlSemanticsFromRuntimeStore();
        if (!allFields.isEmpty()) {
            context.setRuntimeSource(resolveRuntimeStoreType());
        } else {
            allFields = loadActiveNl2SqlSemanticsFromSnapshot();
            context.setRuntimeSource(allFields.isEmpty() ? "EMPTY" : "FILE_SNAPSHOT");
        }
        context.setTotalBindings(allFields.size());

        if (allFields.isEmpty()) {
            loadRuntimeProviderFields(context, question);
            return context;
        }

        String normalizedQuestion = normalizeText(question);
        List<AiSemanticFieldVO> matchedFields = allFields.stream()
                .peek(field -> field.setMatchScore(calculateScore(normalizedQuestion, field)))
                .sorted(Comparator.comparing(AiSemanticFieldVO::getMatchScore, Comparator.nullsLast(Comparator.reverseOrder()))
                        .thenComparing(AiSemanticFieldVO::getSemanticName, Comparator.nullsLast(String::compareToIgnoreCase))
                        .thenComparing(AiSemanticFieldVO::getTableColumnKey, Comparator.nullsLast(String::compareToIgnoreCase)))
                .collect(Collectors.toList());

        int limit = Math.max(6, fastBeeAiProperties.getNl2sql().getMaxSemanticPromptItems());
        List<AiSemanticFieldVO> selectedFields = matchedFields.stream()
                .filter(field -> field.getMatchScore() != null && field.getMatchScore() > 0)
                .limit(limit)
                .collect(Collectors.toCollection(ArrayList::new));
        if (selectedFields.isEmpty()) {
            selectedFields = matchedFields.stream()
                    .limit(Math.min(limit, matchedFields.size()))
                    .collect(Collectors.toCollection(ArrayList::new));
            context.setFallbackUsed(Boolean.TRUE);
        }

        context.setMatched(!selectedFields.isEmpty());
        context.setMatchedBindings(selectedFields.size());
        context.setFields(selectedFields);
        context.setPromptLines(selectedFields.stream().map(this::buildPromptLine).collect(Collectors.toList()));
        context.setKnowledgeBases(selectedFields.stream()
                .map(AiSemanticFieldVO::getKbCode)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList()));
        context.setBusinessObjects(selectedFields.stream()
                .map(AiSemanticFieldVO::getBusinessObjectName)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList()));
        context.setPrimaryTables(selectedFields.stream()
                .map(AiSemanticFieldVO::getPrimaryTable)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList()));
        context.setMetricRules(selectedFields.stream()
                .map(AiSemanticFieldVO::getMetricRuleName)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList()));
        context.setClarifyRules(selectedFields.stream()
                .map(AiSemanticFieldVO::getClarifyRule)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList()));
        context.setVersionNos(selectedFields.stream()
                .map(AiSemanticFieldVO::getVersionNo)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList()));

        loadRuntimeProviderFields(context, question);
        return context;
    }

    @Override
    public List<AiSemanticFieldVO> listSemanticFieldsByVersion(AiKnowledgeBase knowledgeBase, AiKnowledgeVersion version) {
        if (knowledgeBase == null || version == null) {
            return Collections.emptyList();
        }
        return loadSemanticFieldsFromSnapshot(knowledgeBase, version);
    }

    private List<AiSemanticFieldVO> loadActiveNl2SqlSemanticsFromRuntimeStore() {
        IAiSemanticRuntimeStore runtimeStore = resolveRuntimeStore();
        if (runtimeStore == null) {
            return Collections.emptyList();
        }
        try {
            return runtimeStore.listActiveNl2SqlBundles(GLOBAL_TENANT_ID).stream()
                    .filter(Objects::nonNull)
                    .map(AiSemanticRuntimeBundleVO::getFields)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    private List<AiSemanticFieldVO> loadActiveNl2SqlSemanticsFromSnapshot() {
        AiKnowledgeBase query = new AiKnowledgeBase();
        query.setKbType(KB_TYPE_NL2SQL);
        query.setStatus("0");
        List<AiKnowledgeBase> knowledgeBases = aiKnowledgeBaseService.listAiKnowledgeBase(query);
        if (knowledgeBases == null || knowledgeBases.isEmpty()) {
            return Collections.emptyList();
        }
        List<AiSemanticFieldVO> result = new ArrayList<>();
        for (AiKnowledgeBase knowledgeBase : knowledgeBases) {
            if (knowledgeBase == null || knowledgeBase.getActiveVersionId() == null) {
                continue;
            }
            AiKnowledgeVersion version = aiKnowledgeVersionService.selectAiKnowledgeVersion(knowledgeBase.getActiveVersionId());
            if (version == null || StringUtils.isBlank(version.getSnapshotPath())) {
                continue;
            }
            try {
                result.addAll(loadSemanticFieldsFromSnapshot(knowledgeBase, version));
            } catch (Exception ignored) {
                // 单个知识库快照异常时跳过，避免影响整条问数链路。
            }
        }
        return result;
    }

    private IAiSemanticRuntimeStore resolveRuntimeStore() {
        String storeType = resolveRuntimeStoreType();
        if (StringUtils.isBlank(storeType) || aiSemanticRuntimeStores == null || aiSemanticRuntimeStores.isEmpty()) {
            return null;
        }
        for (IAiSemanticRuntimeStore runtimeStore : aiSemanticRuntimeStores) {
            if (runtimeStore != null && runtimeStore.supports(storeType)) {
                return runtimeStore;
            }
        }
        return null;
    }

    private String resolveRuntimeStoreType() {
        return StringUtils.isBlank(fastBeeAiProperties.getNl2sql().getSemanticStoreType())
                ? "REDIS" : fastBeeAiProperties.getNl2sql().getSemanticStoreType().trim().toUpperCase(Locale.ROOT);
    }

    private List<AiSemanticFieldVO> loadSemanticFieldsFromSnapshot(AiKnowledgeBase knowledgeBase, AiKnowledgeVersion version) {
        JSONObject snapshot = readSnapshot(version.getSnapshotPath());
        JSONArray items = snapshot.getJSONArray("items");
        if (items == null || items.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, JSONObject> tableSemanticMap = buildTableSemanticMap(snapshot);
        Map<String, JSONObject> businessObjectMap = buildBusinessObjectMap(snapshot);
        List<JSONObject> metricRules = buildMetricRules(snapshot);
        List<JSONObject> relationPaths = buildRelationPaths(snapshot);
        List<AiSemanticFieldVO> fields = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            JSONObject item = items.getJSONObject(i);
            if (item == null) {
                continue;
            }
            AiSemanticFieldVO field = new AiSemanticFieldVO();
            field.setKnowledgeBaseId(knowledgeBase.getKnowledgeBaseId());
            field.setVersionId(version.getVersionId());
            field.setVersionNo(version.getVersionNo());
            field.setKbCode(knowledgeBase.getKbCode());
            field.setTableName(trimToEmpty(item.getString("tableName")));
            field.setTableComment(trimToEmpty(item.getString("tableComment")));
            field.setColumnName(trimToEmpty(item.getString("columnName")));
            field.setTableColumnKey(buildTableColumnKey(field.getTableName(), field.getColumnName()));
            field.setSemanticName(defaultIfBlank(item.getString("semanticName"), field.getColumnName()));
            field.setSemanticType(normalizeCode(item.getString("semanticType"), "DIMENSION"));
            field.setSourceType(normalizeCode(item.getString("sourceType"), "MANUAL"));
            field.setSourceCode(trimToEmpty(item.getString("sourceCode")));
            field.setDataSourceType(normalizeCode(item.getString("dataSourceType"), ROUTE_AUTO));
            field.setAliases(parseStringList(item.get("aliases")));
            field.setQueryHints(parseStringList(item.get("queryHints")));
            field.setRelationHints(parseStringList(item.get("relationHints")));
            field.setValueMappings(parseValueMappings(item.get("valueMappings")));
            field.setSemanticSource(SOURCE_TEMPLATE);
            enrichBySystemSource(field);
            enrichByEnterpriseSnapshot(field, tableSemanticMap, businessObjectMap, metricRules, relationPaths);
            if (StringUtils.isBlank(field.getSemanticName()) && StringUtils.isBlank(field.getTableColumnKey())) {
                continue;
            }
            fields.add(field);
        }
        return fields;
    }

    private Map<String, JSONObject> buildTableSemanticMap(JSONObject snapshot) {
        JSONArray tableSemantics = snapshot == null ? null : snapshot.getJSONArray("tableSemantics");
        if (tableSemantics == null || tableSemantics.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, JSONObject> result = new LinkedHashMap<>();
        for (int i = 0; i < tableSemantics.size(); i++) {
            JSONObject item = tableSemantics.getJSONObject(i);
            if (item == null || StringUtils.isBlank(item.getString("tableName"))) {
                continue;
            }
            result.put(normalizeTableName(item.getString("tableName")), item);
        }
        return result;
    }

    private Map<String, JSONObject> buildBusinessObjectMap(JSONObject snapshot) {
        JSONArray businessObjects = snapshot == null ? null : snapshot.getJSONArray("businessObjects");
        if (businessObjects == null || businessObjects.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, JSONObject> result = new LinkedHashMap<>();
        for (int i = 0; i < businessObjects.size(); i++) {
            JSONObject item = businessObjects.getJSONObject(i);
            if (item == null) {
                continue;
            }
            putBusinessObjectLookup(result, item.getString("businessObjectCode"), item);
            putBusinessObjectLookup(result, item.getString("businessObjectName"), item);
            putBusinessObjectLookup(result, item.getString("primaryTable"), item);
            for (String alias : parseStringList(item.get("aliases"))) {
                putBusinessObjectLookup(result, alias, item);
            }
        }
        return result;
    }

    private List<JSONObject> buildRelationPaths(JSONObject snapshot) {
        JSONArray relationPaths = snapshot == null ? null : snapshot.getJSONArray("relationPaths");
        if (relationPaths == null || relationPaths.isEmpty()) {
            return Collections.emptyList();
        }
        List<JSONObject> result = new ArrayList<>();
        for (int i = 0; i < relationPaths.size(); i++) {
            JSONObject item = relationPaths.getJSONObject(i);
            if (item != null) {
                result.add(item);
            }
        }
        return result;
    }

    private List<JSONObject> buildMetricRules(JSONObject snapshot) {
        JSONArray metricRules = snapshot == null ? null : snapshot.getJSONArray("metricRules");
        if (metricRules == null || metricRules.isEmpty()) {
            return Collections.emptyList();
        }
        List<JSONObject> result = new ArrayList<>();
        for (int i = 0; i < metricRules.size(); i++) {
            JSONObject item = metricRules.getJSONObject(i);
            if (item != null) {
                result.add(item);
            }
        }
        return result;
    }

    private void enrichByEnterpriseSnapshot(AiSemanticFieldVO field,
                                            Map<String, JSONObject> tableSemanticMap,
                                            Map<String, JSONObject> businessObjectMap,
                                            List<JSONObject> metricRules,
                                            List<JSONObject> relationPaths) {
        if (field == null) {
            return;
        }
        JSONObject tableSemantic = tableSemanticMap.get(normalizeTableName(field.getTableName()));
        if (tableSemantic != null) {
            field.setTableRole(trimToEmpty(tableSemantic.getString("tableRole")));
            field.setTableComment(defaultIfBlank(field.getTableComment(), tableSemantic.getString("tableComment")));
            field.setBusinessObjectName(defaultIfBlank(field.getBusinessObjectName(), tableSemantic.getString("businessObjectName")));
            field.setAliases(mergeStringList(field.getAliases(), buildTableBusinessAliases(field, tableSemantic)));
            field.setQueryHints(mergeStringList(field.getQueryHints(), buildTableSemanticHints(tableSemantic)));
        }
        JSONObject businessObject = enrichByBusinessObject(field, tableSemantic, businessObjectMap);
        enrichByMetricRule(field, businessObject, metricRules);
        field.setRelationHints(mergeStringList(field.getRelationHints(), buildRelationPathHints(field, relationPaths)));
    }

    private JSONObject enrichByBusinessObject(AiSemanticFieldVO field, JSONObject tableSemantic,
                                              Map<String, JSONObject> businessObjectMap) {
        if (field == null || businessObjectMap == null || businessObjectMap.isEmpty()) {
            return null;
        }
        JSONObject businessObject = resolveBusinessObject(field, tableSemantic, businessObjectMap);
        if (businessObject == null) {
            if ("主表".equals(trimToEmpty(field.getTableRole())) && StringUtils.isBlank(field.getPrimaryTable())) {
                field.setPrimaryTable(trimToEmpty(field.getTableName()));
            }
            return null;
        }
        field.setBusinessObjectCode(defaultIfBlank(field.getBusinessObjectCode(), businessObject.getString("businessObjectCode")));
        field.setBusinessObjectName(defaultIfBlank(field.getBusinessObjectName(), businessObject.getString("businessObjectName")));
        field.setPrimaryTable(defaultIfBlank(field.getPrimaryTable(), businessObject.getString("primaryTable")));
        field.setDefaultMetricRule(defaultIfBlank(field.getDefaultMetricRule(), businessObject.getString("defaultMetricRule")));
        field.setDefaultDataSource(defaultIfBlank(field.getDefaultDataSource(), businessObject.getString("defaultDataSource")));
        field.setClarifyRule(defaultIfBlank(field.getClarifyRule(), businessObject.getString("clarifyRule")));
        field.setAliases(mergeStringList(field.getAliases(), parseStringList(businessObject.get("aliases"))));
        field.setQueryHints(mergeStringList(field.getQueryHints(), buildBusinessObjectHints(businessObject)));
        return businessObject;
    }

    private JSONObject resolveBusinessObject(AiSemanticFieldVO field, JSONObject tableSemantic,
                                            Map<String, JSONObject> businessObjectMap) {
        List<String> lookupKeys = new ArrayList<>();
        if (tableSemantic != null) {
            lookupKeys.add(tableSemantic.getString("businessObjectCode"));
            lookupKeys.add(tableSemantic.getString("businessObjectName"));
            lookupKeys.add(tableSemantic.getString("tableName"));
        }
        lookupKeys.add(field.getBusinessObjectCode());
        lookupKeys.add(field.getBusinessObjectName());
        lookupKeys.add(field.getPrimaryTable());
        lookupKeys.add(field.getTableName());
        for (String lookupKey : lookupKeys) {
            String normalizedKey = normalizeBusinessObjectLookupKey(lookupKey);
            if (StringUtils.isBlank(normalizedKey)) {
                continue;
            }
            JSONObject businessObject = businessObjectMap.get(normalizedKey);
            if (businessObject != null) {
                return businessObject;
            }
        }
        return null;
    }

    private void putBusinessObjectLookup(Map<String, JSONObject> lookupMap, String key, JSONObject item) {
        String normalizedKey = normalizeBusinessObjectLookupKey(key);
        if (StringUtils.isNotBlank(normalizedKey)) {
            lookupMap.putIfAbsent(normalizedKey, item);
        }
    }

    private String normalizeBusinessObjectLookupKey(String value) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        return value.trim()
                .toLowerCase(Locale.ROOT)
                .replace("_", "")
                .replace("-", "")
                .replaceAll("\\s+", "");
    }

    private List<String> buildBusinessObjectHints(JSONObject businessObject) {
        List<String> hints = new ArrayList<>();
        String businessObjectName = trimToEmpty(businessObject.getString("businessObjectName"));
        String primaryTable = trimToEmpty(businessObject.getString("primaryTable"));
        String defaultMetricRule = trimToEmpty(businessObject.getString("defaultMetricRule"));
        String defaultDataSource = trimToEmpty(businessObject.getString("defaultDataSource"));
        String clarifyRule = trimToEmpty(businessObject.getString("clarifyRule"));
        if (StringUtils.isNotBlank(businessObjectName) || StringUtils.isNotBlank(primaryTable)) {
            hints.add("业务对象：" + defaultIfBlank(businessObjectName, "未定义")
                    + "；主事实表=" + defaultIfBlank(primaryTable, "未定义"));
        }
        if (StringUtils.isNotBlank(defaultMetricRule)) {
            hints.add("默认统计口径：" + defaultMetricRule);
        }
        if (StringUtils.isNotBlank(defaultDataSource)) {
            hints.add("默认数据源：" + defaultDataSource);
        }
        if (StringUtils.isNotBlank(clarifyRule)) {
            hints.add("澄清规则：" + clarifyRule);
        }
        return hints;
    }

    private void enrichByMetricRule(AiSemanticFieldVO field, JSONObject businessObject, List<JSONObject> metricRules) {
        if (field == null || metricRules == null || metricRules.isEmpty()) {
            return;
        }
        JSONObject metricRule = resolveMetricRule(field, businessObject, metricRules);
        if (metricRule == null) {
            return;
        }
        field.setMetricRuleCode(defaultIfBlank(field.getMetricRuleCode(), metricRule.getString("metricRuleCode")));
        field.setMetricRuleName(defaultIfBlank(field.getMetricRuleName(), metricRule.getString("metricRuleName")));
        field.setDefaultDataSource(defaultIfBlank(field.getDefaultDataSource(), metricRule.getString("defaultDataSource")));
        field.setQueryHints(mergeStringList(field.getQueryHints(), buildMetricRuleHints(metricRule)));
    }

    private JSONObject resolveMetricRule(AiSemanticFieldVO field, JSONObject businessObject, List<JSONObject> metricRules) {
        JSONObject bestMatch = null;
        int bestScore = 0;
        for (JSONObject metricRule : metricRules) {
            if (metricRule == null) {
                continue;
            }
            int score = 0;
            if (matchesMetricBusinessObject(metricRule, field, businessObject)) {
                score += 6;
            }
            if (matchesMetricPrimaryTable(metricRule, field)) {
                score += 4;
            }
            if (matchesMetricText(metricRule, field)) {
                score += 2;
            }
            if (score > bestScore) {
                bestScore = score;
                bestMatch = metricRule;
            }
        }
        return bestScore >= 6 ? bestMatch : null;
    }

    private boolean matchesMetricBusinessObject(JSONObject metricRule, AiSemanticFieldVO field, JSONObject businessObject) {
        String metricBusinessObject = trimToEmpty(metricRule.getString("businessObjectName"));
        String normalizedMetricBusinessObject = normalizeBusinessObjectLookupKey(metricBusinessObject);
        if (StringUtils.isBlank(normalizedMetricBusinessObject)) {
            return false;
        }
        List<String> candidates = new ArrayList<>();
        candidates.add(field == null ? null : field.getBusinessObjectCode());
        candidates.add(field == null ? null : field.getBusinessObjectName());
        if (businessObject != null) {
            candidates.add(businessObject.getString("businessObjectCode"));
            candidates.add(businessObject.getString("businessObjectName"));
        }
        for (String candidate : candidates) {
            if (StringUtils.equals(normalizedMetricBusinessObject, normalizeBusinessObjectLookupKey(candidate))) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesMetricPrimaryTable(JSONObject metricRule, AiSemanticFieldVO field) {
        if (field == null) {
            return false;
        }
        String metricPrimaryTable = normalizeTableName(metricRule.getString("primaryTable"));
        String fieldPrimaryTable = normalizeTableName(field.getPrimaryTable());
        String fieldTableName = normalizeTableName(field.getTableName());
        return StringUtils.isNotBlank(metricPrimaryTable)
                && (StringUtils.equals(metricPrimaryTable, fieldPrimaryTable)
                || StringUtils.equals(metricPrimaryTable, fieldTableName));
    }

    private boolean matchesMetricText(JSONObject metricRule, AiSemanticFieldVO field) {
        if (field == null) {
            return false;
        }
        String metricRuleName = normalizeText(metricRule.getString("metricRuleName"));
        if (StringUtils.isBlank(metricRuleName)) {
            return false;
        }
        return containsNormalizedMetricText(metricRuleName, field.getSemanticName())
                || containsNormalizedMetricText(metricRuleName, field.getColumnName())
                || containsNormalizedMetricText(metricRuleName, field.getMetricRuleName());
    }

    private boolean containsNormalizedMetricText(String metricRuleName, String candidateText) {
        String normalizedCandidate = normalizeText(candidateText);
        return StringUtils.isNotBlank(metricRuleName)
                && StringUtils.isNotBlank(normalizedCandidate)
                && metricRuleName.contains(normalizedCandidate);
    }

    private List<String> buildMetricRuleHints(JSONObject metricRule) {
        List<String> hints = new ArrayList<>();
        String metricRuleName = trimToEmpty(metricRule.getString("metricRuleName"));
        String aggregationType = trimToEmpty(metricRule.getString("aggregationType"));
        String distinctColumn = trimToEmpty(metricRule.getString("distinctColumn"));
        String stateRule = trimToEmpty(metricRule.getString("stateRule"));
        String timeRule = trimToEmpty(metricRule.getString("timeRule"));
        String defaultDataSource = trimToEmpty(metricRule.getString("defaultDataSource"));
        String applicableQuestion = trimToEmpty(metricRule.getString("applicableQuestion"));
        if (StringUtils.isNotBlank(metricRuleName) || StringUtils.isNotBlank(aggregationType)) {
            hints.add("指标口径：" + defaultIfBlank(metricRuleName, "未定义")
                    + "；聚合=" + defaultIfBlank(aggregationType, "未定义"));
        }
        if (StringUtils.isNotBlank(distinctColumn)) {
            hints.add("去重字段：" + distinctColumn);
        }
        if (StringUtils.isNotBlank(stateRule)) {
            hints.add("状态口径：" + stateRule);
        }
        if (StringUtils.isNotBlank(timeRule)) {
            hints.add("时间口径：" + timeRule);
        }
        if (StringUtils.isNotBlank(defaultDataSource)) {
            hints.add("指标默认数据源：" + defaultDataSource);
        }
        if (StringUtils.isNotBlank(applicableQuestion)) {
            hints.add("适用问法：" + applicableQuestion);
        }
        return hints;
    }

    private List<String> buildTableSemanticHints(JSONObject tableSemantic) {
        List<String> hints = new ArrayList<>();
        String tableRole = trimToEmpty(tableSemantic.getString("tableRole"));
        String businessObjectName = trimToEmpty(tableSemantic.getString("businessObjectName"));
        String primaryKeyColumn = trimToEmpty(tableSemantic.getString("primaryKeyColumn"));
        String applicableScene = trimToEmpty(tableSemantic.getString("applicableScene"));
        String forbiddenScene = trimToEmpty(tableSemantic.getString("forbiddenScene"));
        String defaultFilter = trimToEmpty(tableSemantic.getString("defaultFilter"));
        if (StringUtils.isNotBlank(tableRole) || StringUtils.isNotBlank(businessObjectName)) {
            hints.add("表级语义：表角色=" + defaultIfBlank(tableRole, "未定义")
                    + "，业务对象=" + defaultIfBlank(businessObjectName, "未定义"));
        }
        if (StringUtils.isNotBlank(primaryKeyColumn)) {
            hints.add("主事实表主键=" + primaryKeyColumn);
        }
        if (StringUtils.isNotBlank(applicableScene)) {
            hints.add("适用场景：" + applicableScene);
        }
        if (StringUtils.isNotBlank(forbiddenScene)) {
            hints.add("禁止场景：" + forbiddenScene);
        }
        if (StringUtils.isNotBlank(defaultFilter)) {
            hints.add("默认过滤：" + defaultFilter);
        }
        return hints;
    }

    private List<String> buildRelationPathHints(AiSemanticFieldVO field, List<JSONObject> relationPaths) {
        if (relationPaths == null || relationPaths.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> hints = new ArrayList<>();
        String tableName = normalizeTableName(field.getTableName());
        String columnName = normalizeColumnName(field.getColumnName());
        for (JSONObject relationPath : relationPaths) {
            String sourceTable = normalizeTableName(relationPath.getString("sourceTable"));
            String sourceColumn = normalizeColumnName(relationPath.getString("sourceColumn"));
            String targetTable = normalizeTableName(relationPath.getString("targetTable"));
            String targetColumn = normalizeColumnName(relationPath.getString("targetColumn"));
            boolean matchedSource = StringUtils.equals(tableName, sourceTable) && StringUtils.equals(columnName, sourceColumn);
            boolean matchedTarget = StringUtils.equals(tableName, targetTable) && StringUtils.equals(columnName, targetColumn);
            if (!matchedSource && !matchedTarget) {
                continue;
            }
            String relation = sourceTable + "." + sourceColumn + "=" + targetTable + "." + targetColumn;
            hints.add(relation);
            String applicableQuestion = trimToEmpty(relationPath.getString("applicableQuestion"));
            String forbiddenQuestion = trimToEmpty(relationPath.getString("forbiddenQuestion"));
            if (StringUtils.isNotBlank(applicableQuestion)) {
                hints.add("关联适用：" + applicableQuestion);
            }
            if (StringUtils.isNotBlank(forbiddenQuestion)) {
                hints.add("关联禁用：" + forbiddenQuestion);
            }
        }
        return hints;
    }

    private List<String> mergeStringList(List<String> first, List<String> second) {
        LinkedHashSet<String> result = new LinkedHashSet<>();
        if (first != null) {
            first.stream().filter(StringUtils::isNotBlank).map(String::trim).forEach(result::add);
        }
        if (second != null) {
            second.stream().filter(StringUtils::isNotBlank).map(String::trim).forEach(result::add);
        }
        return new ArrayList<>(result);
    }

    private JSONObject readSnapshot(String snapshotPath) {
        Path path = Paths.get(snapshotPath.trim());
        if (!Files.exists(path)) {
            throw new ServiceException(message("ai.semantic.snapshot.not.exists", snapshotPath));
        }
        try {
            return JSON.parseObject(Files.readString(path, StandardCharsets.UTF_8));
        } catch (IOException ex) {
            throw new ServiceException(message("ai.semantic.snapshot.read.failed", ex.getMessage()));
        }
    }

    private void enrichBySystemSource(AiSemanticFieldVO field) {
        if (SOURCE_DICT.equals(field.getSourceType()) && StringUtils.isNotBlank(field.getSourceCode())) {
            mergeDictMappings(field);
            field.setSemanticSource(SOURCE_TEMPLATE_DICT);
        }
    }

    private void mergeDictMappings(AiSemanticFieldVO field) {
        List<SysDictData> dictDataList = sysDictTypeService.selectDictDataByType(field.getSourceCode());
        if (dictDataList == null || dictDataList.isEmpty()) {
            return;
        }
        Map<String, AiSemanticValueMappingVO> mappingMap = new LinkedHashMap<>();
        for (AiSemanticValueMappingVO item : field.getValueMappings()) {
            if (item == null || (StringUtils.isBlank(item.getLabel()) && StringUtils.isBlank(item.getValue()))) {
                continue;
            }
            mappingMap.put(buildMappingKey(item.getLabel(), item.getValue()), item);
        }
        for (SysDictData dictData : dictDataList) {
            AiSemanticValueMappingVO mapping = new AiSemanticValueMappingVO();
            mapping.setLabel(resolveDictLabel(dictData));
            mapping.setValue(Objects.toString(dictData.getDictValue(), ""));
            mappingMap.putIfAbsent(buildMappingKey(mapping.getLabel(), mapping.getValue()), mapping);
        }
        field.setValueMappings(new ArrayList<>(mappingMap.values()));
    }

    private String resolveDictLabel(SysDictData dictData) {
        if (dictData == null) {
            return "";
        }
        if (StringUtils.isNotBlank(dictData.getDictLabel_zh_CN())) {
            return dictData.getDictLabel_zh_CN().trim();
        }
        return trimToEmpty(dictData.getDictLabel());
    }

    private Integer calculateScore(String normalizedQuestion, AiSemanticFieldVO field) {
        if (StringUtils.isBlank(normalizedQuestion) || field == null) {
            return 0;
        }
        int score = 0;
        score += scoreText(normalizedQuestion, field.getSemanticName(), 30);
        score += scoreText(normalizedQuestion, field.getBusinessObjectName(), 34);
        score += scoreText(normalizedQuestion, field.getTableComment(), 28);
        score += scoreText(normalizedQuestion, field.getTableName(), 10);
        score += scoreText(normalizedQuestion, field.getColumnName(), 12);
        score += scoreText(normalizedQuestion, field.getSourceCode(), 8);
        score += scoreText(normalizedQuestion, field.getBusinessObjectCode(), 8);
        score += scoreText(normalizedQuestion, field.getPrimaryTable(), 12);
        score += scoreText(normalizedQuestion, field.getTableColumnKey(), 16);
        score += scoreCollection(normalizedQuestion, field.getAliases(), 18);
        score += scoreCollection(normalizedQuestion, field.getQueryHints(), 14);
        score += scoreCollection(normalizedQuestion, field.getRelationHints(), 8);
        for (AiSemanticValueMappingVO item : field.getValueMappings()) {
            score += scoreText(normalizedQuestion, item.getLabel(), 16);
            score += scoreText(normalizedQuestion, item.getValue(), 4);
        }
        return score;
    }

    private int scoreCollection(String question, Collection<String> values, int weight) {
        if (values == null || values.isEmpty()) {
            return 0;
        }
        int score = 0;
        for (String item : values) {
            score += scoreText(question, item, weight);
        }
        return score;
    }

    private int scoreText(String question, String text, int weight) {
        String normalizedText = normalizeText(text);
        if (StringUtils.isBlank(question) || StringUtils.isBlank(normalizedText)) {
            return 0;
        }
        if (question.contains(normalizedText)) {
            return weight;
        }
        Set<String> keywords = splitKeywords(normalizedText);
        int matched = 0;
        for (String keyword : keywords) {
            if (keyword.length() >= 2 && question.contains(keyword)) {
                matched++;
            }
        }
        if (matched <= 0) {
            return 0;
        }
        return Math.max(2, matched * Math.max(2, weight / 4));
    }

    private Set<String> splitKeywords(String text) {
        return parseStringList(text).stream().collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private List<String> buildTableBusinessAliases(AiSemanticFieldVO field, JSONObject tableSemantic) {
        LinkedHashSet<String> aliases = new LinkedHashSet<>();
        if (tableSemantic != null) {
            addBusinessAlias(aliases, tableSemantic.getString("businessObjectName"));
            addBusinessAlias(aliases, tableSemantic.getString("tableComment"));
        }
        if (field != null) {
            addBusinessAlias(aliases, field.getBusinessObjectName());
            addBusinessAlias(aliases, field.getTableComment());
        }
        return new ArrayList<>(aliases);
    }

    private void addBusinessAlias(Set<String> aliases, String text) {
        String actualText = trimToEmpty(text);
        if (StringUtils.isBlank(actualText)) {
            return;
        }
        aliases.add(actualText);
        String stripped = stripBusinessObjectSuffix(actualText);
        if (StringUtils.isNotBlank(stripped)) {
            aliases.add(stripped);
        }
    }

    private String stripBusinessObjectSuffix(String text) {
        String actualText = trimToEmpty(text);
        if (StringUtils.isBlank(actualText)) {
            return "";
        }
        for (String suffix : List.of("信息表", "数据表", "记录表", "日志表", "明细表", "关联表", "配置表", "管理表", "主表", "表")) {
            if (actualText.endsWith(suffix) && actualText.length() > suffix.length() + 1) {
                return actualText.substring(0, actualText.length() - suffix.length()).trim();
            }
        }
        return actualText;
    }

    private String buildPromptLine(AiSemanticFieldVO field) {
        StringBuilder builder = new StringBuilder(256);
        builder.append(defaultIfBlank(field.getTableColumnKey(), field.getSemanticName()));
        builder.append("：语义=").append(defaultIfBlank(field.getSemanticName(), "未命名语义"));
        if (StringUtils.isNotBlank(field.getTableComment())) {
            builder.append("；表说明=").append(field.getTableComment());
        }
        if (StringUtils.isNotBlank(field.getBusinessObjectName())) {
            builder.append("；业务对象=").append(field.getBusinessObjectName());
        }
        if (StringUtils.isNotBlank(field.getPrimaryTable())) {
            builder.append("；主事实表=").append(field.getPrimaryTable());
        }
        if (StringUtils.isNotBlank(field.getTableRole())) {
            builder.append("；表角色=").append(field.getTableRole());
        }
        if (StringUtils.isNotBlank(field.getMetricRuleName())) {
            builder.append("；指标口径=").append(field.getMetricRuleName());
        }
        builder.append("；类型=").append(defaultIfBlank(field.getSemanticType(), "DIMENSION"));
        builder.append("；来源=").append(defaultIfBlank(field.getSourceType(), "MANUAL"));
        if (StringUtils.isNotBlank(field.getSourceCode())) {
            builder.append('(').append(field.getSourceCode()).append(')');
        }
        builder.append("；执行路由=").append(defaultIfBlank(field.getDataSourceType(), ROUTE_AUTO));
        if (!field.getValueMappings().isEmpty()) {
            builder.append("；值映射=");
            builder.append(field.getValueMappings().stream()
                    .limit(Math.max(4, fastBeeAiProperties.getNl2sql().getMaxSemanticValueMappings()))
                    .map(item -> defaultIfBlank(item.getLabel(), "-") + "=" + defaultIfBlank(item.getValue(), "-"))
                    .collect(Collectors.joining("、")));
        }
        if (!field.getAliases().isEmpty()) {
            builder.append("；同义词=").append(String.join("、", field.getAliases()));
        }
        if (!field.getQueryHints().isEmpty()) {
            builder.append("；提示=").append(String.join("；", field.getQueryHints()));
        }
        return builder.toString();
    }

    private void loadRuntimeProviderFields(AiSemanticContextVO context, String question) {
        if (context == null || aiSemanticRuntimeProviders == null || aiSemanticRuntimeProviders.isEmpty()) {
            return;
        }
        List<AiSemanticFieldVO> runtimeFields = new ArrayList<>();
        LinkedHashSet<String> runtimeProviders = new LinkedHashSet<>();
        for (IAiSemanticRuntimeProvider provider : aiSemanticRuntimeProviders) {
            if (provider == null) {
                continue;
            }
            List<AiSemanticFieldVO> fields;
            try {
                fields = provider.listSemanticFields(question);
            } catch (Exception ignored) {
                continue;
            }
            if (fields == null || fields.isEmpty()) {
                continue;
            }
            runtimeProviders.add(provider.getProviderCode());
            runtimeFields.addAll(fields);
        }
        if (runtimeFields.isEmpty()) {
            return;
        }
        runtimeFields.sort(Comparator.comparing(AiSemanticFieldVO::getMatchScore, Comparator.nullsLast(Comparator.reverseOrder()))
                .thenComparing(AiSemanticFieldVO::getSemanticName, Comparator.nullsLast(String::compareToIgnoreCase))
                .thenComparing(AiSemanticFieldVO::getSourceCode, Comparator.nullsLast(String::compareToIgnoreCase)));
        context.setRuntimeFields(runtimeFields);
        context.setRuntimeBindings(runtimeFields.size());
        context.setRuntimeProviders(new ArrayList<>(runtimeProviders));
    }

    private List<AiSemanticValueMappingVO> parseValueMappings(Object rawValue) {
        if (rawValue == null) {
            return new ArrayList<>();
        }
        if (rawValue instanceof JSONArray jsonArray) {
            return parseValueMappingsFromArray(jsonArray);
        }
        if (rawValue instanceof JSONObject jsonObject) {
            return parseValueMappingsFromObject(jsonObject);
        }
        String text = rawValue.toString().trim();
        if (StringUtils.isBlank(text)) {
            return new ArrayList<>();
        }
        if ((text.startsWith("{") && text.endsWith("}")) || (text.startsWith("[") && text.endsWith("]"))) {
            try {
                Object parsed = JSON.parse(text);
                if (parsed instanceof JSONArray jsonArray) {
                    return parseValueMappingsFromArray(jsonArray);
                }
                if (parsed instanceof JSONObject jsonObject) {
                    return parseValueMappingsFromObject(jsonObject);
                }
            } catch (Exception ignored) {
                // 忽略 JSON 解析异常，回退到普通文本切割。
            }
        }
        List<AiSemanticValueMappingVO> result = new ArrayList<>();
        for (String item : text.split("[\\r\\n;,；，]+")) {
            String actualItem = item == null ? "" : item.trim();
            if (actualItem.isEmpty()) {
                continue;
            }
            String[] pair = actualItem.split("[:=：]", 2);
            AiSemanticValueMappingVO mapping = new AiSemanticValueMappingVO();
            if (pair.length == 2) {
                mapping.setLabel(pair[0].trim());
                mapping.setValue(pair[1].trim());
            } else {
                mapping.setLabel(actualItem);
                mapping.setValue(actualItem);
            }
            result.add(mapping);
        }
        return deduplicateMappings(result);
    }

    private List<AiSemanticValueMappingVO> parseValueMappingsFromArray(JSONArray jsonArray) {
        List<AiSemanticValueMappingVO> result = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            Object item = jsonArray.get(i);
            if (item instanceof JSONObject jsonObject) {
                AiSemanticValueMappingVO mapping = new AiSemanticValueMappingVO();
                mapping.setLabel(defaultIfBlank(jsonObject.getString("label"), jsonObject.getString("text")));
                mapping.setValue(defaultIfBlank(jsonObject.getString("value"), jsonObject.getString("code")));
                result.add(mapping);
                continue;
            }
            if (item != null) {
                AiSemanticValueMappingVO mapping = new AiSemanticValueMappingVO();
                mapping.setLabel(item.toString());
                mapping.setValue(item.toString());
                result.add(mapping);
            }
        }
        return deduplicateMappings(result);
    }

    private List<AiSemanticValueMappingVO> parseValueMappingsFromObject(JSONObject jsonObject) {
        List<AiSemanticValueMappingVO> result = new ArrayList<>();
        for (String key : jsonObject.keySet()) {
            AiSemanticValueMappingVO mapping = new AiSemanticValueMappingVO();
            mapping.setLabel(key);
            mapping.setValue(Objects.toString(jsonObject.get(key), ""));
            result.add(mapping);
        }
        return deduplicateMappings(result);
    }

    private List<AiSemanticValueMappingVO> deduplicateMappings(List<AiSemanticValueMappingVO> mappings) {
        Map<String, AiSemanticValueMappingVO> mappingMap = new LinkedHashMap<>();
        for (AiSemanticValueMappingVO item : mappings) {
            if (item == null || (StringUtils.isBlank(item.getLabel()) && StringUtils.isBlank(item.getValue()))) {
                continue;
            }
            mappingMap.putIfAbsent(buildMappingKey(item.getLabel(), item.getValue()), item);
        }
        return new ArrayList<>(mappingMap.values());
    }

    private String buildMappingKey(String label, String value) {
        return defaultIfBlank(label, "") + "->" + defaultIfBlank(value, "");
    }

    private List<String> parseStringList(Object rawValue) {
        if (rawValue == null) {
            return new ArrayList<>();
        }
        if (rawValue instanceof JSONArray jsonArray) {
            List<String> result = new ArrayList<>();
            for (int i = 0; i < jsonArray.size(); i++) {
                String item = trimToEmpty(jsonArray.getString(i));
                if (StringUtils.isNotBlank(item)) {
                    result.add(item);
                }
            }
            return deduplicateStrings(result);
        }
        String text = rawValue.toString().trim();
        if (StringUtils.isBlank(text)) {
            return new ArrayList<>();
        }
        if (text.startsWith("[") && text.endsWith("]")) {
            try {
                JSONArray jsonArray = JSON.parseArray(text);
                return parseStringList(jsonArray);
            } catch (Exception ignored) {
                // 忽略 JSON 解析异常，继续走普通拆分。
            }
        }
        List<String> result = new ArrayList<>();
        for (String item : text.split("[,;，；\\r\\n]+")) {
            String actualItem = item == null ? "" : item.trim();
            if (StringUtils.isNotBlank(actualItem)) {
                result.add(actualItem);
            }
        }
        return deduplicateStrings(result);
    }

    private List<String> deduplicateStrings(List<String> values) {
        return values.stream()
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());
    }

    private String normalizeText(String value) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        return value.trim()
                .toLowerCase(Locale.ROOT)
                .replace('_', ' ')
                .replace('-', ' ')
                .replace('（', '(')
                .replace('）', ')')
                .replaceAll("\\s+", "");
    }

    private String normalizeCode(String value, String defaultValue) {
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return value.trim().replace('-', '_').toUpperCase(Locale.ROOT);
    }

    private String buildTableColumnKey(String tableName, String columnName) {
        String actualTable = StringUtils.isBlank(tableName) ? "*" : tableName.trim();
        String actualColumn = StringUtils.isBlank(columnName) ? "*" : columnName.trim();
        return actualTable + "." + actualColumn;
    }

    private String normalizeTableName(String tableName) {
        return trimToEmpty(tableName).toLowerCase(Locale.ROOT);
    }

    private String normalizeColumnName(String columnName) {
        return trimToEmpty(columnName).toLowerCase(Locale.ROOT);
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value.trim();
    }
}
