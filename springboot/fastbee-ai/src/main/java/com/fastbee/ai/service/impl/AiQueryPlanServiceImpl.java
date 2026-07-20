package com.fastbee.ai.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fastbee.ai.model.enums.AiQueryMode;
import com.fastbee.ai.model.vo.AiQueryPlanStepVO;
import com.fastbee.ai.model.vo.AiQueryPlanVO;
import com.fastbee.ai.model.vo.AiQueryRouteVO;
import com.fastbee.ai.model.vo.AiSemanticContextVO;
import com.fastbee.ai.model.vo.AiSemanticFieldVO;
import com.fastbee.ai.service.IAiQueryPlanService;
import com.fastbee.ai.service.IAiQuerySourceRouter;
import com.fastbee.ai.service.IAiSemanticNormalizationService;
import com.fastbee.ai.support.AiRuntimeFieldSelectionSupport;
import com.fastbee.common.utils.StringUtils;

/**
 * AI 多源问数执行计划服务实现。
 */
@Service
public class AiQueryPlanServiceImpl implements IAiQueryPlanService {

    @Resource
    private IAiSemanticNormalizationService aiSemanticNormalizationService;

    @Resource
    private IAiQuerySourceRouter aiQuerySourceRouter;

    @Override
    public AiQueryPlanVO buildPlan(String question) {
        AiSemanticContextVO context = aiSemanticNormalizationService.buildNl2SqlContext(question);
        AiQueryRouteVO route = aiQuerySourceRouter.route(question, context);

        AiQueryPlanVO plan = new AiQueryPlanVO();
        plan.setQuestion(question);
        plan.setRuntimeSource(context.getRuntimeSource());
        plan.setFallbackUsed(Boolean.TRUE.equals(context.getFallbackUsed()));
        plan.setMatchedBindings(defaultZero(context.getMatchedBindings()));
        plan.setRuntimeBindings(defaultZero(context.getRuntimeBindings()));
        plan.setRuntimeSemanticMatched(defaultZero(context.getRuntimeBindings()) > 0);
        plan.setRuntimeProviders(copyList(context.getRuntimeProviders()));
        plan.setCandidateTables(collectCandidateTables(context));
        plan.setKnowledgeBases(copyList(context.getKnowledgeBases()));
        plan.setVersionNos(copyList(context.getVersionNos()));
        plan.setMatchedSemantics(collectSemanticNames(context.getFields()));
        plan.setMatchedFieldKeys(collectFieldKeys(context.getFields()));
        plan.setRuntimeSemantics(collectSemanticNames(context.getRuntimeFields()));
        plan.setRuntimeFieldKeys(collectFieldKeys(context.getRuntimeFields()));
        plan.setCandidateIdentifiers(collectCandidateIdentifiers(context.getRuntimeFields()));
        plan.setPrimarySemantic(resolvePrimarySemantic(question, context));
        plan.setQueryMode(route.getQueryMode());
        plan.setTargetType(route.getTargetType());
        plan.setReason(route.getRouteReason());

        if (AiQueryMode.RDB_SQL.name().equals(route.getQueryMode())) {
            buildRdbPlan(plan);
            return plan;
        }

        plan.setRequiresRelationResolution(Boolean.TRUE);
        if (AiQueryMode.REDIS_VALUE.name().equals(route.getQueryMode())) {
            buildRedisPlan(plan, question, context);
            return plan;
        }
        if (AiQueryMode.TSDB_QUERY.name().equals(route.getQueryMode())) {
            buildTsdbPlan(plan, question, context, containsAggregateIntent(question));
            return plan;
        }
        buildHybridPlan(plan, question, context);
        return plan;
    }

    private void buildRdbPlan(AiQueryPlanVO plan) {
        plan.setExecutableNow(Boolean.TRUE);
        plan.setBlockedReason(null);
        plan.setSummary("已识别为关系库问数，建议继续走受控 NL2SQL 链路。");
        plan.getSteps().add(buildStep(1, "SEMANTIC_ASSEMBLE", "RDB_SQL",
                "从静态语义上下文中挑选候选表和字段。",
                "关系库候选表",
                List.of("问题文本", "语义库字段"),
                List.of("候选表", "候选字段")));
        plan.getSteps().add(buildStep(2, "SQL_GENERATE", "RDB_SQL",
                "生成受控只读 SQL。",
                "SQL 生成",
                List.of("候选表", "候选字段", "值映射"),
                List.of("结构化 SQL")));
        plan.getSteps().add(buildStep(3, "SQL_EXECUTE", "RDB_SQL",
                "通过 SQL 守卫、DataScope 和只读执行器执行查询。",
                "关系库执行",
                List.of("SQL", "行数限制"),
                List.of("结果集")));
        plan.getSteps().add(buildStep(4, "RESULT_FORMAT", "RDB_SQL",
                "将结果集格式化为问数结果。",
                "结果整理",
                List.of("结果集"),
                List.of("最终回答")));
    }

    private void buildRedisPlan(AiQueryPlanVO plan, String question, AiSemanticContextVO context) {
        plan.setExecutableNow(Boolean.TRUE);
        plan.setBlockedReason(null);
        plan.setSummary("已识别为设备实时值问数，建议优先走 Redis 实时值链路。");
        AiSemanticFieldVO runtimeField = firstRuntimeField(question, context);
        String semanticName = runtimeField == null ? "设备指标" : runtimeField.getSemanticName();
        String identifier = runtimeField == null ? null : runtimeField.getSourceCode();
        plan.getSteps().add(buildStep(1, "ENTITY_LOOKUP", "RDB_SQL",
                "先定位目标设备以及所属产品。",
                "设备和产品解析",
                List.of("设备编号/设备名称"),
                List.of("deviceId", "productId", "serialNumber")));
        plan.getSteps().add(buildStep(2, "TSL_RESOLVE", "THING_MODEL_RUNTIME",
                "根据运行时物模型语义解析指标对应的真实 identifier。",
                semanticName,
                buildInputs("产品ID", "指标名称", identifier),
                buildOutputs("identifier", "物模型类型", "单位")));
        plan.getSteps().add(buildStep(3, "REDIS_READ", "REDIS_VALUE",
                "读取 Redis 中该设备当前指标值。",
                "实时值读取",
                buildInputs("productId", "serialNumber", identifier),
                buildOutputs("当前值", "上报时间")));
        plan.getSteps().add(buildStep(4, "RESULT_FORMAT", "REDIS_VALUE",
                "将实时值和单位整理为最终回答。",
                "结果整理",
                List.of("当前值", "单位", "上报时间"),
                List.of("最终回答")));
    }

    private void buildTsdbPlan(AiQueryPlanVO plan, String question, AiSemanticContextVO context, boolean aggregateIntent) {
        plan.setExecutableNow(Boolean.TRUE);
        plan.setBlockedReason(null);
        plan.setSummary(aggregateIntent
                ? "已识别为设备历史统计问数，建议优先走 TSDB 聚合链路。"
                : "已识别为设备历史问数，建议优先走 TSDB 历史查询链路。");
        AiSemanticFieldVO runtimeField = firstRuntimeField(question, context);
        String identifier = runtimeField == null ? null : runtimeField.getSourceCode();
        String semanticName = runtimeField == null ? "设备指标" : runtimeField.getSemanticName();
        plan.getSteps().add(buildStep(1, "ENTITY_LOOKUP", "RDB_SQL",
                "先定位目标设备以及所属产品。",
                "设备和产品解析",
                List.of("设备编号/设备名称"),
                List.of("deviceId", "productId", "serialNumber")));
        plan.getSteps().add(buildStep(2, "TSL_RESOLVE", "THING_MODEL_RUNTIME",
                "根据物模型运行时语义解析历史指标对应的 identifier。",
                semanticName,
                buildInputs("产品ID", "指标名称", identifier),
                buildOutputs("identifier", "是否历史存储", "单位")));
        plan.getSteps().add(buildStep(3, "TSDB_QUERY", "TSDB_QUERY",
                aggregateIntent ? "调用时序查询服务做聚合统计。" : "调用时序查询服务读取历史数据。",
                "时序库查询",
                buildInputs("serialNumber", "identifier", "时间范围"),
                buildOutputs(aggregateIntent ? "聚合值" : "历史序列")));
        plan.getSteps().add(buildStep(4, "RESULT_FORMAT", "TSDB_QUERY",
                "将历史值、趋势或聚合结果整理为最终回答。",
                "结果整理",
                List.of("时序结果"),
                List.of("最终回答")));
    }

    private void buildHybridPlan(AiQueryPlanVO plan, String question, AiSemanticContextVO context) {
        plan.setExecutableNow(Boolean.TRUE);
        plan.setBlockedReason(null);
        plan.setSummary("已识别为需要多源联动的设备问数，建议走混合执行链路。");
        AiSemanticFieldVO runtimeField = firstRuntimeField(question, context);
        String identifier = runtimeField == null ? null : runtimeField.getSourceCode();
        String semanticName = runtimeField == null ? "设备指标" : runtimeField.getSemanticName();
        plan.getSteps().add(buildStep(1, "ENTITY_LOOKUP", "RDB_SQL",
                "先解析设备、产品和基础业务过滤条件。",
                "设备和产品解析",
                List.of("设备编号/设备名称", "基础过滤条件"),
                List.of("deviceId", "productId", "serialNumber")));
        plan.getSteps().add(buildStep(2, "TSL_RESOLVE", "THING_MODEL_RUNTIME",
                "根据产品物模型和语义名称解析真实 identifier。",
                semanticName,
                buildInputs("productId", "语义名称", identifier),
                buildOutputs("identifier", "推荐执行路由")));
        plan.getSteps().add(buildStep(3, "SOURCE_ROUTE", "HYBRID_PIPELINE",
                "根据问题意图、历史能力和实时值能力决定走 Redis、TSDB 还是关系库补偿链路。",
                "执行路由判断",
                List.of("问题文本", "identifier", "路由提示"),
                List.of("最终执行源")));
        plan.getSteps().add(buildStep(4, "SOURCE_EXECUTE", "HYBRID_PIPELINE",
                "按最终执行源组合读取实时值、历史值或关系库补偿数据。",
                "多源执行",
                List.of("最终执行源"),
                List.of("查询结果")));
        plan.getSteps().add(buildStep(5, "RESULT_FORMAT", "HYBRID_PIPELINE",
                "将多源结果汇总为最终回答。",
                "结果整理",
                List.of("查询结果"),
                List.of("最终回答")));
    }

    private AiQueryPlanStepVO buildStep(int stepNo, String stepType, String executorType, String description, String target,
                                        List<String> inputs, List<String> outputs) {
        AiQueryPlanStepVO step = new AiQueryPlanStepVO();
        step.setStepNo(stepNo);
        step.setStepType(stepType);
        step.setExecutorType(executorType);
        step.setDescription(description);
        step.setTarget(target);
        step.setInputs(copyList(inputs));
        step.setOutputs(copyList(outputs));
        return step;
    }

    private boolean containsAggregateIntent(String question) {
        return containsAny(question, "统计", "总计", "总和", "平均", "最大", "最小", "峰值", "累计", "汇总");
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

    private List<String> collectCandidateTables(AiSemanticContextVO context) {
        Set<String> tables = new LinkedHashSet<>();
        appendTables(tables, context.getFields());
        appendTables(tables, context.getRuntimeFields());
        return new ArrayList<>(tables);
    }

    private void appendTables(Set<String> tables, List<AiSemanticFieldVO> fields) {
        if (fields == null) {
            return;
        }
        for (AiSemanticFieldVO field : fields) {
            if (field != null && StringUtils.isNotBlank(field.getTableName())) {
                tables.add(field.getTableName());
            }
        }
    }

    private List<String> collectSemanticNames(List<AiSemanticFieldVO> fields) {
        Set<String> names = new LinkedHashSet<>();
        if (fields != null) {
            for (AiSemanticFieldVO field : fields) {
                if (field != null && StringUtils.isNotBlank(field.getSemanticName())) {
                    names.add(field.getSemanticName());
                }
            }
        }
        return new ArrayList<>(names);
    }

    private List<String> collectCandidateIdentifiers(List<AiSemanticFieldVO> fields) {
        Set<String> identifiers = new LinkedHashSet<>();
        if (fields != null) {
            for (AiSemanticFieldVO field : fields) {
                if (field != null && StringUtils.isNotBlank(field.getSourceCode())) {
                    identifiers.add(field.getSourceCode());
                }
            }
        }
        return new ArrayList<>(identifiers);
    }

    private List<String> collectFieldKeys(List<AiSemanticFieldVO> fields) {
        Set<String> fieldKeys = new LinkedHashSet<>();
        if (fields != null) {
            for (AiSemanticFieldVO field : fields) {
                if (field == null) {
                    continue;
                }
                if (StringUtils.isNotBlank(field.getTableColumnKey())) {
                    fieldKeys.add(field.getTableColumnKey());
                    continue;
                }
                if (StringUtils.isNotBlank(field.getTableName()) && StringUtils.isNotBlank(field.getColumnName())) {
                    fieldKeys.add(field.getTableName() + "." + field.getColumnName());
                }
            }
        }
        return new ArrayList<>(fieldKeys);
    }

    private AiSemanticFieldVO firstRuntimeField(String question, AiSemanticContextVO context) {
        if (context.getRuntimeFields() == null || context.getRuntimeFields().isEmpty()) {
            if (StringUtils.isNotBlank(AiRuntimeFieldSelectionSupport.extractExplicitIdentifier(question))) {
                return AiRuntimeFieldSelectionSupport.resolveRuntimeField(question, context.getRuntimeFields(),
                        "未命中可用的运行时物模型语义，无法构建设备运行时问数执行计划。", "设备运行时问数执行计划");
            }
            return null;
        }
        return AiRuntimeFieldSelectionSupport.resolveRuntimeField(question, context.getRuntimeFields(),
                "未命中可用的运行时物模型语义，无法构建设备运行时问数执行计划。", "设备运行时问数执行计划");
    }

    private String resolvePrimarySemantic(String question, AiSemanticContextVO context) {
        AiSemanticFieldVO runtimeField = firstRuntimeField(question, context);
        if (runtimeField != null && StringUtils.isNotBlank(runtimeField.getSemanticName())) {
            return runtimeField.getSemanticName();
        }
        if (context.getFields() == null || context.getFields().isEmpty()) {
            return null;
        }
        AiSemanticFieldVO field = context.getFields().get(0);
        return field == null ? null : field.getSemanticName();
    }

    private Integer defaultZero(Integer value) {
        return value == null ? 0 : value;
    }

    private List<String> buildInputs(String... values) {
        List<String> result = new ArrayList<>();
        if (values == null) {
            return result;
        }
        for (String value : values) {
            if (StringUtils.isNotBlank(value)) {
                result.add(value);
            }
        }
        return result;
    }

    private List<String> buildOutputs(String... values) {
        return buildInputs(values);
    }

    private List<String> copyList(List<String> source) {
        return source == null ? new ArrayList<>() : new ArrayList<>(source);
    }
}
