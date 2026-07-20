package com.fastbee.ai.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastbee.ai.config.properties.FastBeeAiProperties;
import com.fastbee.ai.model.vo.AiSemanticFieldVO;
import com.fastbee.ai.model.vo.AiSemanticRuntimeBundleVO;
import com.fastbee.ai.service.IAiNl2SqlScopeSemanticResolver;
import com.fastbee.ai.service.IAiSemanticRuntimeStore;
import com.fastbee.common.utils.StringUtils;

/**
 * 基于问数语义库的数据范围解析器。
 */
@Service
public class AiNl2SqlScopeSemanticResolverImpl implements IAiNl2SqlScopeSemanticResolver {

    private static final Long GLOBAL_TENANT_ID = 0L;
    private static final String DEFAULT_STORE_TYPE = "REDIS";
    private static final String QUERY_HINT_AUTO_DATA_SCOPE = "auto-data-scope";
    private static final String COLUMN_TENANT_ID = "tenant_id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_DEL_FLAG = "del_flag";

    @Resource
    private FastBeeAiProperties fastBeeAiProperties;

    @Autowired(required = false)
    private List<IAiSemanticRuntimeStore> aiSemanticRuntimeStores = Collections.emptyList();

    @Override
    public Map<String, SemanticScopeMode> resolveScopeModes(List<String> tableNames) {
        Set<String> targetTables = normalizeTables(tableNames);
        if (targetTables.isEmpty()) {
            return Collections.emptyMap();
        }
        IAiSemanticRuntimeStore runtimeStore = resolveRuntimeStore();
        if (runtimeStore == null) {
            return Collections.emptyMap();
        }
        try {
            return resolveScopeModesFromFields(targetTables, loadRuntimeFields(runtimeStore));
        } catch (Exception ignored) {
            return Collections.emptyMap();
        }
    }

    @Override
    public Set<String> resolveLogicDeleteTables(List<String> tableNames) {
        Set<String> targetTables = normalizeTables(tableNames);
        if (targetTables.isEmpty()) {
            return Collections.emptySet();
        }
        IAiSemanticRuntimeStore runtimeStore = resolveRuntimeStore();
        if (runtimeStore == null) {
            return Collections.emptySet();
        }
        try {
            return resolveLogicDeleteTablesFromFields(targetTables, loadRuntimeFields(runtimeStore));
        } catch (Exception ignored) {
            return Collections.emptySet();
        }
    }

    private Set<String> normalizeTables(List<String> tableNames) {
        if (tableNames == null || tableNames.isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> result = new LinkedHashSet<>();
        for (String tableName : tableNames) {
            String normalized = resolveSimpleTableName(tableName);
            if (StringUtils.isNotBlank(normalized)) {
                result.add(normalized);
            }
        }
        return result;
    }

    private List<AiSemanticFieldVO> loadRuntimeFields(IAiSemanticRuntimeStore runtimeStore) {
        List<AiSemanticRuntimeBundleVO> bundles = runtimeStore.listActiveNl2SqlBundles(GLOBAL_TENANT_ID);
        if (bundles == null || bundles.isEmpty()) {
            return Collections.emptyList();
        }
        return bundles.stream()
                .filter(Objects::nonNull)
                .map(AiSemanticRuntimeBundleVO::getFields)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .toList();
    }

    private Map<String, SemanticScopeMode> resolveScopeModesFromFields(Set<String> targetTables, List<AiSemanticFieldVO> fields) {
        if (fields == null || fields.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, SemanticScopeMode> result = new HashMap<>();
        for (AiSemanticFieldVO field : fields) {
            String tableName = resolveSimpleTableName(field.getTableName());
            if (!targetTables.contains(tableName) || !hasAutoDataScopeHint(field)) {
                continue;
            }
            SemanticScopeMode scopeMode = resolveScopeModeByColumn(field.getColumnName());
            if (scopeMode == SemanticScopeMode.UNKNOWN) {
                continue;
            }
            putScopeMode(result, tableName, scopeMode);
        }
        return result;
    }

    private Set<String> resolveLogicDeleteTablesFromFields(Set<String> targetTables, List<AiSemanticFieldVO> fields) {
        if (fields == null || fields.isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> result = new LinkedHashSet<>();
        for (AiSemanticFieldVO field : fields) {
            String tableName = resolveSimpleTableName(field.getTableName());
            if (targetTables.contains(tableName) && COLUMN_DEL_FLAG.equals(normalizeColumnName(field.getColumnName()))) {
                result.add(tableName);
            }
        }
        return result;
    }

    private void putScopeMode(Map<String, SemanticScopeMode> result, String tableName, SemanticScopeMode scopeMode) {
        SemanticScopeMode current = result.get(tableName);
        if (current == null || scopeMode == SemanticScopeMode.TENANT) {
            result.put(tableName, scopeMode);
        }
    }

    private boolean hasAutoDataScopeHint(AiSemanticFieldVO field) {
        if (field == null || field.getQueryHints() == null || field.getQueryHints().isEmpty()) {
            return false;
        }
        for (String queryHint : field.getQueryHints()) {
            if (QUERY_HINT_AUTO_DATA_SCOPE.equalsIgnoreCase(trimToEmpty(queryHint))) {
                return true;
            }
        }
        return false;
    }

    private SemanticScopeMode resolveScopeModeByColumn(String columnName) {
        String normalized = normalizeColumnName(columnName);
        if (COLUMN_TENANT_ID.equals(normalized)) {
            return SemanticScopeMode.TENANT;
        }
        if (COLUMN_USER_ID.equals(normalized)) {
            return SemanticScopeMode.USER;
        }
        return SemanticScopeMode.UNKNOWN;
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
        if (fastBeeAiProperties == null || fastBeeAiProperties.getNl2sql() == null
                || StringUtils.isBlank(fastBeeAiProperties.getNl2sql().getSemanticStoreType())) {
            return DEFAULT_STORE_TYPE;
        }
        return fastBeeAiProperties.getNl2sql().getSemanticStoreType().trim().toUpperCase(Locale.ROOT);
    }

    private String resolveSimpleTableName(String tableName) {
        if (StringUtils.isBlank(tableName)) {
            return "";
        }
        String normalized = tableName.trim().replace("`", "");
        int index = normalized.lastIndexOf('.');
        return (index >= 0 ? normalized.substring(index + 1) : normalized).toLowerCase(Locale.ROOT);
    }

    private String normalizeColumnName(String columnName) {
        return trimToEmpty(columnName).replace("`", "").toLowerCase(Locale.ROOT);
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}
