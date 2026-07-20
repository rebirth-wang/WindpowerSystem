package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fastbee.ai.config.properties.FastBeeAiProperties;
import com.fastbee.ai.guard.IAiSqlGuardService;
import com.fastbee.ai.model.dto.AiNl2SqlQueryRequest;
import com.fastbee.ai.model.vo.AiNl2SqlQueryResultVO;
import com.fastbee.ai.model.vo.AiSqlGuardResultVO;
import com.fastbee.ai.service.IAiNl2SqlDataScopeService;
import com.fastbee.ai.service.IAiNl2SqlQueryService;
import com.fastbee.ai.service.IAiNl2SqlScopeSemanticResolver;
import com.fastbee.ai.service.IAiNl2SqlScopeSemanticResolver.SemanticScopeMode;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.aspectj.DataScopeAspect;
import com.fastbee.common.mybatis.enums.DataBaseType;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 只读 SQL 查询服务实现。
 */
@Service
public class AiNl2SqlQueryServiceImpl implements IAiNl2SqlQueryService {

    private static final Set<String> USER_SCOPE_TABLES = new HashSet<>(Arrays.asList(
            "iot_alert_log",
            "iot_function_log"
    ));

    private static final Set<String> TENANT_SCOPE_TABLES = new HashSet<>(Arrays.asList(
            "ai_chat_session",
            "ai_knowledge_base",
            "ai_model",
            "ai_protocol_adaptation_task",
            "ai_provider",
            "iot_device",
            "iot_device_log",
            "iot_device_maintenance",
            "iot_product",
            "iot_scene",
            "iot_scene_model",
            "iot_work_order"
    ));
    private static final Pattern TABLE_ALIAS_PATTERN =
            Pattern.compile("(?i)\\b(?:from|join)\\s+([`\\w.]+)(?:\\s+(?:as\\s+)?([`\\w]+))?");
    private static final Set<String> SQL_ALIAS_STOP_WORDS = Set.of(
            "where", "join", "left", "right", "inner", "outer", "cross", "full",
            "on", "group", "order", "having", "limit", "union"
    );
    private static final Pattern AGGREGATE_FUNCTION_PATTERN =
            Pattern.compile("(?i)\\b(count|sum|avg|min|max)\\s*\\(");
    private static final String COLUMN_TENANT_ID = "tenant_id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_CREATE_BY = "create_by";
    private static final String COLUMN_DEL_FLAG = "del_flag";

    @Resource
    private DataSource dataSource;

    @Resource
    private FastBeeAiProperties properties;

    @Resource
    private IAiSqlGuardService aiSqlGuardService;

    @Resource
    private IAiNl2SqlDataScopeService aiNl2SqlDataScopeService;

    @Resource
    private IAiNl2SqlScopeSemanticResolver aiNl2SqlScopeSemanticResolver;

    private final Map<String, Boolean> logicDeleteColumnCache = new ConcurrentHashMap<>();
    private final Map<String, ScopeMode> dataScopeColumnCache = new ConcurrentHashMap<>();
    private final Map<String, String> databaseTableNameCache = new ConcurrentHashMap<>();
    private volatile boolean databaseTableNameCacheLoaded;

    @Override
    public AiNl2SqlQueryResultVO executeReadOnlyQuery(AiNl2SqlQueryRequest request) {
        if (request == null) {
            throw new ServiceException(message("ai.nl2sql.query.request.required"));
        }

        AiSqlGuardResultVO guardResult = aiSqlGuardService.guardReadOnlySelect(request.getSqlText(), request.getRowLimit());
        String correctedSql = correctTableNamesIfNecessary(guardResult.getNormalizedSql(), guardResult.getTables());
        correctedSql = correctDanglingColumnQualifiersIfNecessary(correctedSql, guardResult.getTables());
        if (!correctedSql.equals(guardResult.getNormalizedSql())) {
            guardResult = aiSqlGuardService.guardReadOnlySelect(correctedSql, request.getRowLimit());
            guardResult.setOriginalSql(request.getSqlText());
        }
        String logicDeleteSql = resolveLogicDeleteSql(guardResult.getTables(), guardResult.getNormalizedSql());
        String dataScopeSql = resolveScopedDataScope(request, guardResult.getTables(), guardResult.getNormalizedSql());
        String scopedSql = injectWhereCondition(guardResult.getNormalizedSql(), logicDeleteSql);
        scopedSql = injectWhereCondition(scopedSql, dataScopeSql);
        String executedSql = buildExecutedSql(scopedSql, guardResult.getMaxRows());

        AiNl2SqlQueryResultVO result = new AiNl2SqlQueryResultVO();
        result.setOriginalSql(guardResult.getOriginalSql());
        result.setNormalizedSql(guardResult.getNormalizedSql());
        result.setExecutedSql(executedSql);
        result.setTables(guardResult.getTables());
        result.setMaxRows(guardResult.getMaxRows());
        result.setDataScopeApplied(StringUtils.isNotBlank(dataScopeSql));

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = executeQuery(statement, executedSql)) {
            fillResult(result, resultSet);
            return result;
        } catch (SQLException ex) {
            throw wrapSqlException(ex);
        }
    }

    private String resolveScopedDataScope(AiNl2SqlQueryRequest request, List<String> tables, String normalizedSql) {
        ScopeMode scopeMode = resolveScopeMode(tables);
        if (scopeMode == ScopeMode.NONE) {
            return "";
        }

        AiNl2SqlQueryRequest scopedRequest = cloneRequest(request);
        if (scopeMode == ScopeMode.USER) {
            aiNl2SqlDataScopeService.applyUserScope(scopedRequest);
        } else {
            aiNl2SqlDataScopeService.applyTenantScope(scopedRequest);
        }
        return qualifyDataScopeSql(resolveDataScopeSql(scopedRequest), tables, normalizedSql, scopeMode);
    }

    private ResultSet executeQuery(Statement statement, String executedSql) throws SQLException {
        int timeoutMs = Math.max(properties.getNl2sql().getTimeoutMs(), 1000);
        statement.setQueryTimeout((timeoutMs + 999) / 1000);
        return statement.executeQuery(executedSql);
    }

    private void fillResult(AiNl2SqlQueryResultVO result, ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            result.getColumns().add(metaData.getColumnLabel(i));
        }
        while (resultSet.next()) {
            LinkedHashMap<String, Object> row = new LinkedHashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                row.put(metaData.getColumnLabel(i), resultSet.getObject(i));
            }
            result.getRows().add(row);
        }
        result.setRowCount(result.getRows().size());
    }

    private String resolveDataScopeSql(AiNl2SqlQueryRequest request) {
        Map<String, Object> params = request.getParams();
        if (params == null) {
            return "";
        }
        Object dataScope = params.get(DataScopeAspect.DATA_SCOPE);
        return dataScope == null ? "" : String.valueOf(dataScope).trim();
    }

    private String qualifyDataScopeSql(String dataScopeSql, List<String> tables, String normalizedSql, ScopeMode scopeMode) {
        if (StringUtils.isBlank(dataScopeSql) || tables == null || tables.isEmpty()
                || StringUtils.isBlank(normalizedSql) || scopeMode == ScopeMode.NONE) {
            return dataScopeSql;
        }
        String scopeColumn = scopeMode == ScopeMode.USER ? COLUMN_USER_ID : COLUMN_TENANT_ID;
        Map<String, String> tableAliases = resolveTableAliases(normalizedSql);
        Map<String, SemanticScopeMode> semanticScopeModes = resolveSemanticScopeModes(tables);
        Set<String> conditions = new LinkedHashSet<>();
        boolean qualified = false;
        for (String table : tables) {
            String simpleTableName = resolveSimpleTableName(table);
            if (StringUtils.isBlank(simpleTableName)) {
                continue;
            }
            ScopeMode tableScopeMode = resolveConfiguredScopeMode(simpleTableName, semanticScopeModes);
            if (tableScopeMode != scopeMode) {
                continue;
            }
            String qualifier = tableAliases.getOrDefault(simpleTableName, simpleTableName);
            String condition = qualifyDataScopeCondition(dataScopeSql, qualifier, scopeColumn);
            if (!condition.equals(dataScopeSql)) {
                qualified = true;
                conditions.add("(" + condition + ")");
            }
        }
        if (qualified && !conditions.isEmpty()) {
            return String.join(" AND ", conditions);
        }
        return dataScopeSql;
    }

    private String qualifyDataScopeCondition(String condition, String qualifier, String scopeColumn) {
        if (StringUtils.isBlank(condition) || StringUtils.isBlank(qualifier)) {
            return condition;
        }
        String qualified = replaceUnqualifiedColumn(condition, scopeColumn, qualifier);
        return replaceUnqualifiedColumn(qualified, COLUMN_CREATE_BY, qualifier);
    }

    private String replaceUnqualifiedColumn(String condition, String columnName, String qualifier) {
        Pattern pattern = Pattern.compile("(?i)(?<![.`\\w])`?" + Pattern.quote(columnName) + "`?(?![`\\w])");
        String replacement = normalizeSqlIdentifier(qualifier) + "." + columnName;
        return pattern.matcher(condition).replaceAll(Matcher.quoteReplacement(replacement));
    }

    private String resolveLogicDeleteSql(List<String> tables, String normalizedSql) {
        if (tables == null || tables.isEmpty() || StringUtils.isBlank(normalizedSql)
                || containsLogicDeletePredicate(normalizedSql)) {
            return "";
        }
        Set<String> logicDeleteTables = resolveSemanticLogicDeleteTables(tables);
        if (logicDeleteTables.isEmpty()) {
            return "";
        }
        Map<String, String> tableAliases = resolveTableAliases(normalizedSql);
        List<String> conditions = new ArrayList<>();
        for (String table : logicDeleteTables) {
            String simpleTableName = resolveSimpleTableName(table);
            if (StringUtils.isBlank(simpleTableName)) {
                continue;
            }
            String qualifier = tableAliases.getOrDefault(simpleTableName, simpleTableName);
            conditions.add(qualifier + ".del_flag = 0");
        }
        return String.join(" AND ", new LinkedHashSet<>(conditions));
    }

    private Set<String> resolveSemanticLogicDeleteTables(List<String> tables) {
        Set<String> result = new LinkedHashSet<>();
        if (aiNl2SqlScopeSemanticResolver == null) {
            return resolveLogicDeleteTablesFromMetaData(tables);
        }
        try {
            Set<String> logicDeleteTables = aiNl2SqlScopeSemanticResolver.resolveLogicDeleteTables(tables);
            if (logicDeleteTables != null) {
                result.addAll(logicDeleteTables);
            }
        } catch (Exception ignored) {
            // 运行时语义库不可用时继续走数据库元数据兜底。
        }
        result.addAll(resolveLogicDeleteTablesFromMetaData(tables));
        return result;
    }

    private Set<String> resolveLogicDeleteTablesFromMetaData(List<String> tables) {
        if (tables == null || tables.isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> result = new LinkedHashSet<>();
        for (String table : tables) {
            String simpleTableName = resolveSimpleTableName(table);
            if (StringUtils.isNotBlank(simpleTableName) && hasLogicDeleteColumn(simpleTableName)) {
                result.add(simpleTableName);
            }
        }
        return result;
    }

    private boolean hasLogicDeleteColumn(String tableName) {
        String simpleTableName = resolveSimpleTableName(tableName);
        if (StringUtils.isBlank(simpleTableName)) {
            return false;
        }
        return logicDeleteColumnCache.computeIfAbsent(simpleTableName, this::queryLogicDeleteColumn);
    }

    private boolean queryLogicDeleteColumn(String tableName) {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            return hasColumnIgnoreCase(metaData, connection, tableName, COLUMN_DEL_FLAG);
        } catch (SQLException ignored) {
            return false;
        }
    }

    private boolean hasColumnIgnoreCase(DatabaseMetaData metaData, Connection connection, String tableName, String columnName)
            throws SQLException {
        String catalog = connection.getCatalog();
        String schema = resolveConnectionSchema(connection);
        return hasColumn(metaData, catalog, schema, tableName, columnName)
                || hasColumn(metaData, catalog, null, tableName, columnName)
                || hasColumn(metaData, null, schema, tableName, columnName)
                || hasColumn(metaData, null, null, tableName, columnName)
                || hasColumn(metaData, catalog, schema, tableName.toUpperCase(Locale.ROOT), columnName.toUpperCase(Locale.ROOT))
                || hasColumn(metaData, catalog, null, tableName.toUpperCase(Locale.ROOT), columnName.toUpperCase(Locale.ROOT))
                || hasColumn(metaData, null, schema, tableName.toUpperCase(Locale.ROOT), columnName.toUpperCase(Locale.ROOT))
                || hasColumn(metaData, null, null, tableName.toUpperCase(Locale.ROOT), columnName.toUpperCase(Locale.ROOT));
    }

    private String resolveConnectionSchema(Connection connection) {
        try {
            return connection.getSchema();
        } catch (Exception ignored) {
            return null;
        }
    }

    private boolean hasColumn(DatabaseMetaData metaData, String catalog, String schema, String tableName, String columnName)
            throws SQLException {
        try (ResultSet columns = metaData.getColumns(catalog, schema, tableName, columnName)) {
            return columns.next();
        }
    }

    private boolean containsLogicDeletePredicate(String normalizedSql) {
        return normalizedSql.toLowerCase(Locale.ROOT).contains("del_flag");
    }

    private Map<String, String> resolveTableAliases(String sql) {
        if (StringUtils.isBlank(sql)) {
            return Collections.emptyMap();
        }
        Map<String, String> result = new LinkedHashMap<>();
        Matcher matcher = TABLE_ALIAS_PATTERN.matcher(sql);
        while (matcher.find()) {
            String tableName = resolveSimpleTableName(matcher.group(1));
            if (StringUtils.isBlank(tableName)) {
                continue;
            }
            String alias = normalizeSqlIdentifier(matcher.group(2));
            if (StringUtils.isBlank(alias) || SQL_ALIAS_STOP_WORDS.contains(alias.toLowerCase(Locale.ROOT))) {
                alias = tableName;
            }
            result.putIfAbsent(tableName, alias);
        }
        return result;
    }

    private String normalizeSqlIdentifier(String identifier) {
        return identifier == null ? "" : identifier.trim().replace("`", "");
    }

    private AiNl2SqlQueryRequest cloneRequest(AiNl2SqlQueryRequest request) {
        AiNl2SqlQueryRequest clone = new AiNl2SqlQueryRequest();
        clone.setSqlText(request.getSqlText());
        clone.setRowLimit(request.getRowLimit());
        clone.setPageNum(request.getPageNum());
        clone.setPageSize(request.getPageSize());
        if (request.getParams() != null && !request.getParams().isEmpty()) {
            clone.getParams().putAll(request.getParams());
        }
        return clone;
    }

    private ScopeMode resolveScopeMode(List<String> tables) {
        if (tables == null || tables.isEmpty()) {
            throw new ServiceException(message("ai.nl2sql.query.scope.table.required"));
        }

        boolean hasUserScope = false;
        boolean hasTenantScope = false;
        Map<String, SemanticScopeMode> semanticScopeModes = resolveSemanticScopeModes(tables);
        for (String table : tables) {
            String simpleTableName = resolveSimpleTableName(table);
            ScopeMode configuredScopeMode = resolveConfiguredScopeMode(simpleTableName, semanticScopeModes);
            if (configuredScopeMode == ScopeMode.USER) {
                hasUserScope = true;
                continue;
            }
            if (configuredScopeMode == ScopeMode.TENANT) {
                hasTenantScope = true;
                continue;
            }
            throw new ServiceException(message("ai.nl2sql.query.scope.mapping.required", simpleTableName));
        }
        if (hasUserScope && hasTenantScope) {
            throw new ServiceException(message("ai.nl2sql.query.scope.mixed.unsupported"));
        }
        if (hasUserScope) {
            return ScopeMode.USER;
        }
        if (hasTenantScope) {
            return ScopeMode.TENANT;
        }
        return ScopeMode.NONE;
    }

    private Map<String, SemanticScopeMode> resolveSemanticScopeModes(List<String> tables) {
        if (aiNl2SqlScopeSemanticResolver == null) {
            return Collections.emptyMap();
        }
        try {
            Map<String, SemanticScopeMode> scopeModes = aiNl2SqlScopeSemanticResolver.resolveScopeModes(tables);
            return scopeModes == null ? Collections.emptyMap() : scopeModes;
        } catch (Exception ignored) {
            return Collections.emptyMap();
        }
    }

    private ScopeMode resolveConfiguredScopeMode(String simpleTableName, Map<String, SemanticScopeMode> semanticScopeModes) {
        SemanticScopeMode semanticScopeMode = semanticScopeModes.get(simpleTableName);
        if (semanticScopeMode == SemanticScopeMode.USER) {
            return ScopeMode.USER;
        }
        if (semanticScopeMode == SemanticScopeMode.TENANT) {
            return ScopeMode.TENANT;
        }
        if (USER_SCOPE_TABLES.contains(simpleTableName)) {
            return ScopeMode.USER;
        }
        if (TENANT_SCOPE_TABLES.contains(simpleTableName)) {
            return ScopeMode.TENANT;
        }
        return resolveScopeModeFromMetaData(simpleTableName);
    }

    private ScopeMode resolveScopeModeFromMetaData(String tableName) {
        String simpleTableName = resolveSimpleTableName(tableName);
        if (StringUtils.isBlank(simpleTableName)) {
            return ScopeMode.NONE;
        }
        return dataScopeColumnCache.computeIfAbsent(simpleTableName, this::queryDataScopeMode);
    }

    private ScopeMode queryDataScopeMode(String tableName) {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            if (hasColumnIgnoreCase(metaData, connection, tableName, COLUMN_TENANT_ID)) {
                return ScopeMode.TENANT;
            }
            if (hasColumnIgnoreCase(metaData, connection, tableName, COLUMN_USER_ID)) {
                return ScopeMode.USER;
            }
            return ScopeMode.NONE;
        } catch (SQLException ignored) {
            return ScopeMode.NONE;
        }
    }

    private String resolveSimpleTableName(String table) {
        if (StringUtils.isBlank(table)) {
            return "";
        }
        String normalized = table.trim().replace("`", "");
        int index = normalized.lastIndexOf('.');
        return (index >= 0 ? normalized.substring(index + 1) : normalized).toLowerCase(Locale.ROOT);
    }

    private String injectWhereCondition(String normalizedSql, String whereCondition) {
        if (StringUtils.isBlank(whereCondition)) {
            return normalizedSql;
        }
        int insertIndex = findTopLevelInsertIndex(normalizedSql);
        int whereIndex = findTopLevelKeywordIndex(normalizedSql, " where ");
        StringBuilder builder = new StringBuilder(normalizedSql.length() + whereCondition.length() + 32);
        if (whereIndex >= 0 && whereIndex < insertIndex) {
            builder.append(normalizedSql, 0, insertIndex)
                    .append(" AND (")
                    .append(whereCondition)
                    .append(") ")
                    .append(normalizedSql.substring(insertIndex).trim());
            return builder.toString().trim();
        }
        builder.append(normalizedSql, 0, insertIndex)
                .append(" WHERE (")
                .append(whereCondition)
                .append(") ");
        if (insertIndex < normalizedSql.length()) {
            builder.append(normalizedSql.substring(insertIndex).trim());
        }
        return builder.toString().trim();
    }

    private String buildExecutedSql(String scopedSql, Integer maxRows) {
        if (shouldSkipExecutionLimit(scopedSql)) {
            return scopedSql;
        }
        DataBaseType dataBaseType = DataScopeAspect.getDataBaseType(DataScopeAspect.DEFAULT_DATASOURCE_NAME);
        switch (dataBaseType) {
            case SQL_SERVER:
                return "SELECT TOP " + maxRows + " * FROM (" + scopedSql + ") ai_nl2sql_view";
            case ORACLE:
                return "SELECT * FROM (" + scopedSql + ") ai_nl2sql_view WHERE ROWNUM <= " + maxRows;
            default:
                return "SELECT * FROM (" + scopedSql + ") ai_nl2sql_view LIMIT " + maxRows;
        }
    }

    /**
     * 聚合统计类 SQL 的结果规模由统计维度决定，不再追加执行层 LIMIT，避免趋势、分组统计和数量查询被二次截断。
     */
    private boolean shouldSkipExecutionLimit(String scopedSql) {
        if (StringUtils.isBlank(scopedSql)) {
            return false;
        }
        if (findTopLevelKeywordIndex(scopedSql, " group by ") >= 0) {
            return true;
        }
        String selectList = resolveTopLevelSelectList(scopedSql);
        return containsAggregateFunction(selectList);
    }

    private String resolveTopLevelSelectList(String sql) {
        String trimmedSql = sql == null ? "" : sql.trim();
        if (!trimmedSql.toLowerCase(Locale.ROOT).startsWith("select ")) {
            return "";
        }
        int fromIndex = findTopLevelKeywordIndex(trimmedSql, " from ");
        if (fromIndex <= 0) {
            return "";
        }
        return trimmedSql.substring("select".length(), fromIndex);
    }

    private boolean containsAggregateFunction(String selectList) {
        if (StringUtils.isBlank(selectList)) {
            return false;
        }
        Matcher matcher = AGGREGATE_FUNCTION_PATTERN.matcher(selectList);
        while (matcher.find()) {
            int openIndex = matcher.end() - 1;
            int closeIndex = findMatchingRightParenthesis(selectList, openIndex);
            if (closeIndex < 0) {
                return true;
            }
            String suffix = selectList.substring(closeIndex + 1).trim().toLowerCase(Locale.ROOT);
            if (!suffix.startsWith("over")) {
                return true;
            }
        }
        return false;
    }

    private int findMatchingRightParenthesis(String text, int openIndex) {
        int depth = 0;
        boolean singleQuote = false;
        boolean doubleQuote = false;
        for (int i = openIndex; i < text.length(); i++) {
            char current = text.charAt(i);
            if (current == '\'' && !doubleQuote) {
                singleQuote = !singleQuote;
            } else if (current == '"' && !singleQuote) {
                doubleQuote = !doubleQuote;
            } else if (!singleQuote && !doubleQuote) {
                if (current == '(') {
                    depth++;
                } else if (current == ')') {
                    depth--;
                    if (depth == 0) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    private int findTopLevelInsertIndex(String sql) {
        int insertIndex = sql.length();
        insertIndex = Math.min(insertIndex, normalizeIndex(findTopLevelKeywordIndex(sql, " group by ")));
        insertIndex = Math.min(insertIndex, normalizeIndex(findTopLevelKeywordIndex(sql, " order by ")));
        insertIndex = Math.min(insertIndex, normalizeIndex(findTopLevelKeywordIndex(sql, " limit ")));
        return insertIndex;
    }

    private int normalizeIndex(int index) {
        return index < 0 ? Integer.MAX_VALUE : index;
    }

    private int findTopLevelKeywordIndex(String sql, String keyword) {
        if (StringUtils.isBlank(sql) || StringUtils.isBlank(keyword)) {
            return -1;
        }
        String lowerSql = sql.toLowerCase(Locale.ROOT);
        String[] keywordWords = StringUtils.split(keyword.toLowerCase(Locale.ROOT));
        if (keywordWords == null || keywordWords.length == 0) {
            return -1;
        }
        int depth = 0;
        boolean singleQuote = false;
        boolean doubleQuote = false;
        for (int i = 0; i < lowerSql.length(); i++) {
            char current = lowerSql.charAt(i);
            if (current == '\'' && !doubleQuote) {
                singleQuote = !singleQuote;
            } else if (current == '"' && !singleQuote) {
                doubleQuote = !doubleQuote;
            } else if (!singleQuote && !doubleQuote) {
                if (current == '(') {
                    depth++;
                } else if (current == ')' && depth > 0) {
                    depth--;
                }
            }
            if (singleQuote || doubleQuote || depth > 0) {
                continue;
            }
            if (matchesKeywordWords(lowerSql, i, keywordWords)) {
                return i;
            }
        }
        return -1;
    }

    private boolean matchesKeywordWords(String lowerSql, int start, String[] keywordWords) {
        if (start > 0 && isSqlIdentifierPart(lowerSql.charAt(start - 1))) {
            return false;
        }
        int index = start;
        for (int i = 0; i < keywordWords.length; i++) {
            String word = keywordWords[i];
            if (!lowerSql.startsWith(word, index)) {
                return false;
            }
            index += word.length();
            if (i < keywordWords.length - 1) {
                if (index >= lowerSql.length() || !Character.isWhitespace(lowerSql.charAt(index))) {
                    return false;
                }
                while (index < lowerSql.length() && Character.isWhitespace(lowerSql.charAt(index))) {
                    index++;
                }
            }
        }
        return index >= lowerSql.length() || !isSqlIdentifierPart(lowerSql.charAt(index));
    }

    private boolean isSqlIdentifierPart(char current) {
        return Character.isLetterOrDigit(current) || current == '_' || current == '$';
    }

    private ServiceException wrapSqlException(SQLException ex) {
        String sqlMessage = ex.getMessage();
        if (StringUtils.isNotBlank(sqlMessage) && sqlMessage.toLowerCase(Locale.ROOT).contains("timeout")) {
            return new ServiceException(message("ai.nl2sql.query.timeout"));
        }
        return new ServiceException(StringUtils.isNotBlank(sqlMessage)
                ? message("ai.nl2sql.query.failed.detail", sqlMessage)
                : message("ai.nl2sql.query.failed"));
    }

    private String correctTableNamesIfNecessary(String normalizedSql, List<String> tables) {
        if (StringUtils.isBlank(normalizedSql) || tables == null || tables.isEmpty()) {
            return normalizedSql;
        }
        Map<String, String> corrections = new LinkedHashMap<>();
        for (String table : tables) {
            String simpleTableName = resolveSimpleTableName(table);
            if (StringUtils.isBlank(simpleTableName)) {
                continue;
            }
            String actualTableName = resolveActualTableName(simpleTableName);
            if (StringUtils.isNotBlank(actualTableName) && !simpleTableName.equals(actualTableName)) {
                corrections.put(simpleTableName, actualTableName);
            }
        }
        if (corrections.isEmpty()) {
            return normalizedSql;
        }
        Matcher matcher = TABLE_ALIAS_PATTERN.matcher(normalizedSql);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String tableSegment = matcher.group(1);
            String simpleTableName = resolveSimpleTableName(tableSegment);
            String actualTableName = corrections.get(simpleTableName);
            if (StringUtils.isBlank(actualTableName)) {
                continue;
            }
            String correctedSegment = replaceTableSegment(tableSegment, actualTableName);
            String fullMatch = matcher.group(0);
            int relativeStart = matcher.start(1) - matcher.start();
            int relativeEnd = matcher.end(1) - matcher.start();
            String replacement = fullMatch.substring(0, relativeStart)
                    + correctedSegment
                    + fullMatch.substring(relativeEnd);
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    private String correctDanglingColumnQualifiersIfNecessary(String normalizedSql, List<String> tables) {
        if (StringUtils.isBlank(normalizedSql) || tables == null || tables.size() != 1) {
            return normalizedSql;
        }
        String simpleTableName = resolveSimpleTableName(tables.get(0));
        if (StringUtils.isBlank(simpleTableName)) {
            return normalizedSql;
        }
        Map<String, String> tableAliases = resolveTableAliases(normalizedSql);
        String actualQualifier = tableAliases.getOrDefault(simpleTableName, simpleTableName);
        Set<String> knownQualifiers = new LinkedHashSet<>();
        knownQualifiers.add(simpleTableName.toLowerCase(Locale.ROOT));
        knownQualifiers.add(actualQualifier.toLowerCase(Locale.ROOT));
        Pattern qualifierPattern = Pattern.compile("(?i)(?<![`\\w])([a-z][a-z0-9_]{0,1})\\.");
        Matcher matcher = qualifierPattern.matcher(normalizedSql);
        StringBuffer buffer = new StringBuffer();
        boolean changed = false;
        while (matcher.find()) {
            String qualifier = normalizeSqlIdentifier(matcher.group(1));
            if (knownQualifiers.contains(qualifier.toLowerCase(Locale.ROOT))) {
                continue;
            }
            changed = true;
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(actualQualifier + "."));
        }
        matcher.appendTail(buffer);
        return changed ? buffer.toString() : normalizedSql;
    }

    private String resolveActualTableName(String tableName) {
        String simpleTableName = resolveSimpleTableName(tableName);
        if (StringUtils.isBlank(simpleTableName)) {
            return "";
        }
        loadDatabaseTableNameCacheIfNecessary();
        String actualTableName = databaseTableNameCache.get(normalizeTableNameForMatch(simpleTableName));
        return StringUtils.isBlank(actualTableName) ? simpleTableName : actualTableName;
    }

    private void loadDatabaseTableNameCacheIfNecessary() {
        if (databaseTableNameCacheLoaded) {
            return;
        }
        synchronized (databaseTableNameCache) {
            if (databaseTableNameCacheLoaded) {
                return;
            }
            try (Connection connection = dataSource.getConnection()) {
                DatabaseMetaData metaData = connection.getMetaData();
                String catalog = connection.getCatalog();
                String schema = resolveConnectionSchema(connection);
                collectDatabaseTableNames(metaData, catalog, schema);
                collectDatabaseTableNames(metaData, catalog, null);
                collectDatabaseTableNames(metaData, null, schema);
                collectDatabaseTableNames(metaData, null, null);
            } catch (SQLException ignored) {
                // 数据库元数据不可用时不做表名纠偏，继续让后续执行层给出明确错误。
            } finally {
                databaseTableNameCacheLoaded = true;
            }
        }
    }

    private void collectDatabaseTableNames(DatabaseMetaData metaData, String catalog, String schema) throws SQLException {
        String[] types = {"TABLE", "VIEW"};
        try (ResultSet tables = metaData.getTables(catalog, schema, "%", types)) {
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                if (StringUtils.isBlank(tableName)) {
                    continue;
                }
                String simpleTableName = resolveSimpleTableName(tableName);
                String cacheKey = normalizeTableNameForMatch(simpleTableName);
                if (StringUtils.isBlank(cacheKey)) {
                    continue;
                }
                String existing = databaseTableNameCache.putIfAbsent(cacheKey, simpleTableName);
                if (StringUtils.isNotBlank(existing) && !existing.equals(simpleTableName)) {
                    databaseTableNameCache.put(cacheKey, "");
                }
            }
        }
    }

    private String normalizeTableNameForMatch(String tableName) {
        return resolveSimpleTableName(tableName).replace("_", "");
    }

    private String replaceTableSegment(String tableSegment, String actualTableName) {
        if (StringUtils.isBlank(tableSegment)) {
            return actualTableName;
        }
        String normalizedSegment = tableSegment.trim();
        boolean quoted = normalizedSegment.startsWith("`") && normalizedSegment.endsWith("`");
        String cleanSegment = normalizedSegment.replace("`", "");
        int index = cleanSegment.lastIndexOf('.');
        String prefix = index >= 0 ? cleanSegment.substring(0, index + 1) : "";
        String corrected = prefix + actualTableName;
        return quoted ? "`" + corrected + "`" : corrected;
    }

    private enum ScopeMode {
        NONE,
        USER,
        TENANT
    }
}
