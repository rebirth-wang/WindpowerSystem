package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.fastbee.ai.model.enums.AiQueryMode;
import com.fastbee.ai.model.vo.AiQueryRouteVO;
import com.fastbee.ai.model.vo.AiSemanticContextVO;
import com.fastbee.ai.model.vo.AiSemanticFieldVO;
import com.fastbee.ai.service.IAiQuerySourceRouter;
import com.fastbee.ai.support.AiRuntimeFieldSelectionSupport;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 问数执行源路由器实现。
 */
@Service
public class AiQuerySourceRouterImpl implements IAiQuerySourceRouter {

    private static final String TARGET_RDB = "MANAGEMENT_STATISTIC";
    private static final String TARGET_RUNTIME_CURRENT = "DEVICE_RUNTIME_VALUE";
    private static final String TARGET_RUNTIME_HISTORY = "DEVICE_HISTORY_METRIC";
    private static final String TARGET_RUNTIME_MIXED = "DEVICE_RUNTIME_PIPELINE";

    @Override
    public AiQueryRouteVO route(String question, AiSemanticContextVO semanticContext) {
        AiQueryRouteVO route = new AiQueryRouteVO();
        List<String> tags = new ArrayList<>();
        String explicitIdentifier = AiRuntimeFieldSelectionSupport.extractExplicitIdentifier(question);
        boolean hasRuntimeSemantic = semanticContext != null
                && semanticContext.getRuntimeFields() != null
                && !semanticContext.getRuntimeFields().isEmpty();
        boolean historyIntent = containsAny(question, "历史", "最近", "近", "趋势", "曲线", "区间", "期间",
                "history", "recent", "trend", "curve", "period", "range");
        boolean aggregateIntent = containsAny(question, "统计", "总计", "总和", "平均", "最大", "最小", "峰值", "累计", "汇总",
                "statistics", "statistic", "count", "total", "sum", "average", "avg", "max", "min", "peak", "accumulate");
        boolean currentIntent = containsAny(question, "当前", "实时", "现在", "此刻", "最新",
                "current", "realtime", "now", "latest")
                || isImplicitCurrentValueQuestion(question, historyIntent, aggregateIntent);

        if (hasRuntimeSemantic || StringUtils.isNotBlank(explicitIdentifier)) {
            if (!hasRuntimeSemantic) {
                throw new ServiceException(message("ai.query.router.runtime.identifier.not.matched", explicitIdentifier));
            }
            if (!isRuntimeThingModelQuestion(question, semanticContext.getRuntimeFields(), explicitIdentifier)) {
                tags.add("RUNTIME_SEMANTIC_IGNORED");
                route.setQueryMode(AiQueryMode.RDB_SQL.name());
                route.setTargetType(TARGET_RDB);
                route.setRouteReason("虽然命中运行时物模型候选，但当前问题更像关系库统计或管理数据问数，避免被物模型候选带偏，转入关系库受控问数链路。");
                route.setRouteScore(82);
                route.setRouteTags(tags);
                return route;
            }
            tags.add("RUNTIME_SEMANTIC");
            AiSemanticFieldVO runtimeField = AiRuntimeFieldSelectionSupport.resolveRuntimeField(question,
                    semanticContext.getRuntimeFields(), "未命中可用的运行时物模型语义，无法规划设备运行时问数路由。", "设备运行时问数路由规划");
            String runtimeHint = normalizeHint(runtimeField.getDataSourceType());
            if (currentIntent) {
                tags.add("CURRENT_INTENT");
                route.setQueryMode(AiQueryMode.REDIS_VALUE.name());
                route.setTargetType(TARGET_RUNTIME_CURRENT);
                route.setRouteReason("命中运行时物模型语义，且问题属于当前/实时值问数，优先路由到 Redis 实时值链路。");
                route.setRouteScore("REDIS_VALUE".equals(runtimeHint) ? 96 : 88);
                if (StringUtils.isNotBlank(runtimeHint)) {
                    tags.add("RUNTIME_HINT=" + runtimeHint);
                }
                route.setRouteTags(tags);
                return route;
            }
            if (historyIntent || aggregateIntent) {
                tags.add(historyIntent ? "HISTORY_INTENT" : "AGGREGATE_INTENT");
                if ("TSDB_QUERY".equals(runtimeHint)) {
                    route.setQueryMode(AiQueryMode.TSDB_QUERY.name());
                    route.setTargetType(TARGET_RUNTIME_HISTORY);
                    route.setRouteReason("命中运行时物模型语义，且问题属于历史/统计问数，历史能力提示可直接走 TSDB。");
                    route.setRouteScore(94);
                } else {
                    route.setQueryMode(AiQueryMode.HYBRID_PIPELINE.name());
                    route.setTargetType(TARGET_RUNTIME_HISTORY);
                    route.setRouteReason("命中运行时物模型语义，但历史能力不足或执行源不稳定，需先做实体解析后再走多源混合链路。");
                    route.setRouteScore(86);
                }
                if (StringUtils.isNotBlank(runtimeHint)) {
                    tags.add("RUNTIME_HINT=" + runtimeHint);
                }
                route.setRouteTags(tags);
                return route;
            }
            route.setQueryMode(AiQueryMode.HYBRID_PIPELINE.name());
            route.setTargetType(TARGET_RUNTIME_MIXED);
            route.setRouteReason("命中运行时物模型语义，但问题未明确限定实时或历史，先按多源混合链路规划。");
            route.setRouteScore(78);
            if (StringUtils.isNotBlank(runtimeHint)) {
                tags.add("RUNTIME_HINT=" + runtimeHint);
            }
            route.setRouteTags(tags);
            return route;
        }

        route.setQueryMode(AiQueryMode.RDB_SQL.name());
        route.setTargetType(TARGET_RDB);
        route.setRouteReason("当前未命中运行时物模型语义，先按关系库受控问数链路处理。");
        route.setRouteScore(72);
        if (historyIntent || aggregateIntent) {
            tags.add(historyIntent ? "HISTORY_INTENT_FALLBACK" : "AGGREGATE_INTENT_FALLBACK");
        }
        route.setRouteTags(tags);
        return route;
    }

    private boolean isRuntimeThingModelQuestion(String question,
                                                List<AiSemanticFieldVO> runtimeFields,
                                                String explicitIdentifier) {
        if (StringUtils.isBlank(question) || runtimeFields == null || runtimeFields.isEmpty()) {
            return false;
        }
        if (StringUtils.isNotBlank(explicitIdentifier)) {
            return true;
        }
        if (looksLikeRelationalStatisticQuestion(question)) {
            return false;
        }
        boolean hasRuntimeIntent = containsAny(question, "当前", "实时", "现在", "此刻", "最新", "历史", "最近", "趋势",
                "曲线", "区间", "期间", "当天", "今天", "昨日", "昨天", "总计", "总和", "平均", "最大", "最小",
                "峰值", "累计", "是多少", "多少", "值",
                "current", "realtime", "now", "latest", "history", "recent", "trend", "today", "yesterday",
                "total", "sum", "average", "avg", "max", "min", "peak", "how many", "value");
        return hasRuntimeIntent && matchesRuntimeMetricExpression(question, runtimeFields);
    }

    private boolean looksLikeRelationalStatisticQuestion(String question) {
        return containsAny(question, "每个", "各", "按", "分组", "分布", "占比", "比例", "排行", "排名",
                "列表", "明细", "数量", "个数", "总数",
                "each", "per", "group", "distribution", "ratio", "rank", "ranking", "list", "detail", "count", "total");
    }

    private boolean isImplicitCurrentValueQuestion(String question, boolean historyIntent, boolean aggregateIntent) {
        if (historyIntent || aggregateIntent) {
            return false;
        }
        return containsAny(question, "数值是多少", "值是多少", "是多少", "多少",
                "what is the value", "current value", "how many", "value");
    }

    private boolean matchesRuntimeMetricExpression(String question, List<AiSemanticFieldVO> runtimeFields) {
        String normalizedQuestion = normalizeText(question);
        if (StringUtils.isBlank(normalizedQuestion) || runtimeFields == null || runtimeFields.isEmpty()) {
            return false;
        }
        for (AiSemanticFieldVO runtimeField : runtimeFields) {
            if (matchesRuntimeMetricExpression(normalizedQuestion, runtimeField)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesRuntimeMetricExpression(String normalizedQuestion, AiSemanticFieldVO runtimeField) {
        for (String metricValue : collectRuntimeMetricValues(runtimeField)) {
            String normalizedValue = normalizeText(metricValue);
            if (normalizedValue.length() < 2) {
                continue;
            }
            if (normalizedQuestion.contains(normalizedValue) || normalizedValue.contains(normalizedQuestion)) {
                return true;
            }
        }
        return false;
    }

    private List<String> collectRuntimeMetricValues(AiSemanticFieldVO runtimeField) {
        Set<String> values = new LinkedHashSet<>();
        if (runtimeField == null) {
            return new ArrayList<>();
        }
        Set<String> excludedValues = collectRuntimeEntityValues(runtimeField);
        addRuntimeMetricValue(values, excludedValues, runtimeField.getSemanticName());
        addRuntimeMetricValue(values, excludedValues, runtimeField.getSourceCode());
        if (runtimeField.getAliases() != null) {
            for (String alias : runtimeField.getAliases()) {
                addRuntimeMetricValue(values, excludedValues, alias);
            }
        }
        return new ArrayList<>(values);
    }

    private Set<String> collectRuntimeEntityValues(AiSemanticFieldVO runtimeField) {
        Set<String> excludedValues = new LinkedHashSet<>();
        addExcludedRuntimeValue(excludedValues, extractQueryHintValue(runtimeField, "device"));
        addExcludedRuntimeValue(excludedValues, extractQueryHintValue(runtimeField, "serialNumber"));
        addExcludedRuntimeValue(excludedValues, extractQueryHintValue(runtimeField, "product"));
        addExcludedRuntimeValue(excludedValues, extractQueryHintValue(runtimeField, "model-type"));
        addExcludedRuntimeValue(excludedValues, extractQueryHintValue(runtimeField, "unit"));
        return excludedValues;
    }

    private void addRuntimeMetricValue(Set<String> values, Set<String> excludedValues, String value) {
        if (values == null || StringUtils.isBlank(value)) {
            return;
        }
        String normalizedValue = normalizeText(value);
        if (normalizedValue.length() < 2 || excludedValues.contains(normalizedValue)) {
            return;
        }
        values.add(value.trim());
    }

    private void addExcludedRuntimeValue(Set<String> excludedValues, String value) {
        if (excludedValues == null || StringUtils.isBlank(value)) {
            return;
        }
        String normalizedValue = normalizeText(value);
        if (normalizedValue.length() >= 2) {
            excludedValues.add(normalizedValue);
        }
    }

    private String extractQueryHintValue(AiSemanticFieldVO runtimeField, String key) {
        if (runtimeField == null || runtimeField.getQueryHints() == null || StringUtils.isBlank(key)) {
            return null;
        }
        String prefix = key + "=";
        for (String hint : runtimeField.getQueryHints()) {
            if (StringUtils.isNotBlank(hint) && hint.startsWith(prefix)) {
                return hint.substring(prefix.length());
            }
        }
        return null;
    }

    private String normalizeText(String text) {
        if (StringUtils.isBlank(text)) {
            return "";
        }
        return text.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[\\s\\-_/（）()【】\\[\\]、，,；;:：。.!！?？\"'`]+", "");
    }

    private String normalizeHint(String routeHint) {
        if (StringUtils.isBlank(routeHint)) {
            return null;
        }
        return routeHint.trim().replace('-', '_').toUpperCase(Locale.ROOT);
    }

    private boolean containsAny(String question, String... keywords) {
        if (StringUtils.isBlank(question) || keywords == null) {
            return false;
        }
        String normalized = question.toLowerCase(Locale.ROOT);
        for (String keyword : keywords) {
            if (StringUtils.isNotBlank(keyword) && normalized.contains(keyword.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }
}
