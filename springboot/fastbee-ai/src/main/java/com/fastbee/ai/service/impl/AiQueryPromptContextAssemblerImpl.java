package com.fastbee.ai.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fastbee.ai.model.vo.AiSemanticContextVO;
import com.fastbee.ai.model.vo.AiSemanticFieldVO;
import com.fastbee.ai.model.vo.AiSemanticValueMappingVO;
import com.fastbee.ai.service.IAiQueryPromptContextAssembler;
import com.fastbee.common.utils.StringUtils;

/**
 * 问数提示词上下文装配器实现。
 */
@Service
public class AiQueryPromptContextAssemblerImpl implements IAiQueryPromptContextAssembler {

    private static final int MAX_ALIASES = 6;
    private static final int MAX_HINTS = 8;
    private static final int MAX_RELATIONS = 6;
    private static final int MAX_MAPPINGS = 6;

    @Override
    public List<String> assembleNl2SqlPromptLines(AiSemanticContextVO semanticContext) {
        if (semanticContext == null) {
            return new ArrayList<>();
        }

        LinkedHashSet<String> lines = new LinkedHashSet<>();
        if (semanticContext.getPromptLines() != null) {
            semanticContext.getPromptLines().stream()
                    .filter(StringUtils::isNotBlank)
                    .map(String::trim)
                    .forEach(lines::add);
        }

        if (semanticContext.getBusinessObjects() != null && !semanticContext.getBusinessObjects().isEmpty()) {
            lines.add("业务对象候选：" + String.join("、", semanticContext.getBusinessObjects()));
        }
        if (semanticContext.getPrimaryTables() != null && !semanticContext.getPrimaryTables().isEmpty()) {
            lines.add("主事实表候选：" + String.join("、", semanticContext.getPrimaryTables()));
        }
        if (semanticContext.getMetricRules() != null && !semanticContext.getMetricRules().isEmpty()) {
            lines.add("指标口径候选：" + String.join("、", semanticContext.getMetricRules()));
        }
        if (semanticContext.getClarifyRules() != null && !semanticContext.getClarifyRules().isEmpty()) {
            semanticContext.getClarifyRules().stream()
                    .filter(StringUtils::isNotBlank)
                    .limit(3)
                    .forEach(rule -> lines.add("澄清规则：" + rule.trim()));
        }

        if (lines.isEmpty() && semanticContext.getFields() != null) {
            semanticContext.getFields().stream()
                    .filter(field -> field != null && StringUtils.isNotBlank(field.getSemanticName()))
                    .map(this::buildFieldLine)
                    .forEach(lines::add);
        }

        List<String> relationLines = collectRelationLines(semanticContext.getFields());
        if (!relationLines.isEmpty()) {
            lines.addAll(relationLines);
        }

        List<String> tableLines = collectCandidateTableLines(semanticContext.getFields());
        if (!tableLines.isEmpty()) {
            lines.addAll(tableLines);
        }
        return new ArrayList<>(lines);
    }

    private String buildFieldLine(AiSemanticFieldVO field) {
        StringBuilder builder = new StringBuilder();
        builder.append(defaultIfBlank(field.getTableColumnKey(), buildTableColumnKey(field)));
        builder.append("：语义=").append(defaultIfBlank(field.getSemanticName(), "未命名语义"));
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
            builder.append('(').append(field.getSourceCode().trim()).append(')');
        }
        if (!field.getValueMappings().isEmpty()) {
            builder.append("；值映射=").append(field.getValueMappings().stream()
                    .filter(mapping -> mapping != null && (StringUtils.isNotBlank(mapping.getLabel()) || StringUtils.isNotBlank(mapping.getValue())))
                    .limit(MAX_MAPPINGS)
                    .map(this::buildMappingText)
                    .collect(Collectors.joining("、")));
        }
        List<String> aliases = limitStrings(field.getAliases(), MAX_ALIASES);
        if (!aliases.isEmpty()) {
            builder.append("；同义词=").append(String.join("、", aliases));
        }
        List<String> hints = limitStrings(field.getQueryHints(), MAX_HINTS);
        if (!hints.isEmpty()) {
            builder.append("；提示=").append(String.join("；", hints));
        }
        return builder.toString();
    }

    private List<String> collectRelationLines(List<AiSemanticFieldVO> fields) {
        LinkedHashSet<String> relations = new LinkedHashSet<>();
        if (fields == null) {
            return new ArrayList<>();
        }
        for (AiSemanticFieldVO field : fields) {
            if (field == null || field.getRelationHints() == null) {
                continue;
            }
            for (String relationHint : field.getRelationHints()) {
                if (StringUtils.isNotBlank(relationHint)) {
                    relations.add("关联提示：" + relationHint.trim());
                }
                if (relations.size() >= MAX_RELATIONS) {
                    return new ArrayList<>(relations);
                }
            }
        }
        return new ArrayList<>(relations);
    }

    private List<String> collectCandidateTableLines(List<AiSemanticFieldVO> fields) {
        Set<String> tableNames = new LinkedHashSet<>();
        if (fields == null) {
            return new ArrayList<>();
        }
        for (AiSemanticFieldVO field : fields) {
            if (field != null && StringUtils.isNotBlank(field.getTableName())) {
                tableNames.add(field.getTableName().trim());
            }
        }
        if (tableNames.isEmpty()) {
            return new ArrayList<>();
        }
        return List.of("候选表：" + String.join("、", tableNames));
    }

    private String buildTableColumnKey(AiSemanticFieldVO field) {
        String tableName = field == null ? null : field.getTableName();
        String columnName = field == null ? null : field.getColumnName();
        if (StringUtils.isBlank(tableName) && StringUtils.isBlank(columnName)) {
            return "unknown";
        }
        if (StringUtils.isBlank(tableName)) {
            return columnName.trim();
        }
        if (StringUtils.isBlank(columnName)) {
            return tableName.trim();
        }
        return tableName.trim() + "." + columnName.trim();
    }

    private List<String> limitStrings(List<String> source, int maxSize) {
        if (source == null || source.isEmpty()) {
            return new ArrayList<>();
        }
        return source.stream()
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .distinct()
                .limit(maxSize)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private String buildMappingText(AiSemanticValueMappingVO mapping) {
        String label = mapping == null ? "" : defaultIfBlank(mapping.getLabel(), "-");
        String value = mapping == null ? "" : defaultIfBlank(mapping.getValue(), "-");
        return label + "=" + value;
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value.trim();
    }
}
