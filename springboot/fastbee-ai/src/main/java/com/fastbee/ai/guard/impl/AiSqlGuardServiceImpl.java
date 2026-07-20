package com.fastbee.ai.guard.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fastbee.ai.config.properties.FastBeeAiProperties;
import com.fastbee.ai.guard.IAiSqlGuardService;
import com.fastbee.ai.model.vo.AiSqlGuardResultVO;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;

/**
 * AI SQL 守卫服务实现。
 */
@Service
public class AiSqlGuardServiceImpl implements IAiSqlGuardService {

    private static final Pattern COMMENT_PATTERN = Pattern.compile("(?s)(--|/\\*|\\*/|#)");
    private static final Pattern DANGEROUS_PATTERN = Pattern.compile(
            "(?i)\\b(insert|update|delete|drop|alter|truncate|create|grant|revoke|merge|replace|call|exec|execute|use|set|show|into|union)\\b");
    private static final Pattern TABLE_PATTERN = Pattern.compile("(?i)\\b(?:from|join)\\s+([`\\w.]+)");

    @Resource
    private FastBeeAiProperties properties;

    @Override
    public AiSqlGuardResultVO guardReadOnlySelect(String sqlText, Integer rowLimit) {
        if (!properties.getNl2sql().isEnabled()) {
            throw new ServiceException(message("ai.sql.guard.readonly.disabled"));
        }
        if (StringUtils.isBlank(sqlText)) {
            throw new ServiceException(message("ai.sql.guard.sql.required"));
        }

        String normalizedSql = normalizeSql(sqlText);
        validateSql(normalizedSql);

        List<String> tables = extractTables(normalizedSql);
        validateAllowedTables(tables);

        AiSqlGuardResultVO result = new AiSqlGuardResultVO();
        result.setOriginalSql(sqlText);
        result.setNormalizedSql(normalizedSql);
        result.setMaxRows(resolveMaxRows(rowLimit));
        result.setTables(tables);
        return result;
    }

    private String normalizeSql(String sqlText) {
        String normalizedSql = sqlText.trim();
        if (normalizedSql.startsWith("```")) {
            normalizedSql = normalizedSql.replaceAll("(?s)^```[a-zA-Z]*\\s*", "");
            normalizedSql = normalizedSql.replaceAll("(?s)\\s*```$", "");
        }
        normalizedSql = normalizedSql.trim().replace('\r', ' ').replace('\n', ' ');
        normalizedSql = normalizedSql.replaceAll("\\s+", " ");
        while (normalizedSql.endsWith(";")) {
            normalizedSql = normalizedSql.substring(0, normalizedSql.length() - 1).trim();
        }
        return normalizedSql;
    }

    private void validateSql(String normalizedSql) {
        if (StringUtils.isBlank(normalizedSql)) {
            throw new ServiceException(message("ai.sql.guard.sql.required"));
        }
        if (!normalizedSql.toLowerCase(Locale.ROOT).startsWith("select ")) {
            throw new ServiceException(message("ai.sql.guard.select.only"));
        }
        if (normalizedSql.contains(";")) {
            throw new ServiceException(message("ai.sql.guard.single.select.only"));
        }
        if (COMMENT_PATTERN.matcher(normalizedSql).find()) {
            throw new ServiceException(message("ai.sql.guard.comment.rejected"));
        }
        if (DANGEROUS_PATTERN.matcher(normalizedSql).find()) {
            throw new ServiceException(message("ai.sql.guard.dangerous.keyword.rejected"));
        }
    }

    private List<String> extractTables(String normalizedSql) {
        Set<String> tables = new LinkedHashSet<>();
        Matcher matcher = TABLE_PATTERN.matcher(normalizedSql);
        while (matcher.find()) {
            String tableName = matcher.group(1);
            if (StringUtils.isBlank(tableName) || tableName.startsWith("(")) {
                continue;
            }
            tables.add(tableName.replace("`", ""));
        }
        return new ArrayList<>(tables);
    }

    private void validateAllowedTables(List<String> tables) {
        List<String> allowedTables = properties.getNl2sql().getAllowedTables();
        if (allowedTables == null || allowedTables.isEmpty()) {
            return;
        }
        if (tables == null || tables.isEmpty()) {
            throw new ServiceException(message("ai.sql.guard.table.required"));
        }
        Set<String> allowedTableSet = new LinkedHashSet<>();
        for (String item : allowedTables) {
            if (StringUtils.isNotBlank(item)) {
                allowedTableSet.add(item.toLowerCase(Locale.ROOT));
            }
        }
        for (String table : tables) {
            if (!allowedTableSet.contains(table.toLowerCase(Locale.ROOT))) {
                throw new ServiceException(message("ai.sql.guard.table.unauthorized", table));
            }
        }
    }

    private int resolveMaxRows(Integer rowLimit) {
        int configuredMaxRows = Math.max(properties.getNl2sql().getMaxRows(), 1);
        if (rowLimit == null || rowLimit <= 0) {
            return configuredMaxRows;
        }
        return Math.min(rowLimit, configuredMaxRows);
    }
}
