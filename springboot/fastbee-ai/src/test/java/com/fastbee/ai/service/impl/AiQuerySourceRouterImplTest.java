package com.fastbee.ai.service.impl;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fastbee.ai.model.enums.AiQueryMode;
import com.fastbee.ai.model.vo.AiQueryRouteVO;
import com.fastbee.ai.model.vo.AiSemanticContextVO;
import com.fastbee.ai.model.vo.AiSemanticFieldVO;

class AiQuerySourceRouterImplTest {

    private final AiQuerySourceRouterImpl router = new AiQuerySourceRouterImpl();

    @Test
    void shouldRouteBySelectedRuntimeFieldInsteadOfFirstCandidate() {
        AiSemanticFieldVO redisField = buildField("co2", "二氧化碳", "REDIS_VALUE");
        AiSemanticFieldVO tsdbField = buildField("array_00_light_color", "灯光色值0", "TSDB_QUERY");

        AiSemanticContextVO context = new AiSemanticContextVO();
        context.setRuntimeFields(List.of(redisField, tsdbField));

        AiQueryRouteVO route = router.route(
                "查询智能开关产品10最近24小时灯光色值趋势\n已确认物模型：灯光色值0\nidentifier=array_00_light_color",
                context
        );

        Assertions.assertEquals(AiQueryMode.TSDB_QUERY.name(), route.getQueryMode());
        Assertions.assertTrue(route.getRouteReason().contains("TSDB"));
    }

    @Test
    void shouldFallbackToRdbForRelationalStatisticEvenWithRuntimeCandidates() {
        AiSemanticFieldVO runtimeField = buildField("co2", "二氧化碳", "REDIS_VALUE");
        runtimeField.setAliases(List.of("★智能开关产品10", "DEVICE88888888", "二氧化碳", "co2"));
        runtimeField.setQueryHints(List.of("device=★智能开关产品10", "serialNumber=DEVICE88888888", "product=★智能开关产品"));

        AiSemanticContextVO context = new AiSemanticContextVO();
        context.setRuntimeFields(List.of(runtimeField));

        AiQueryRouteVO route = router.route("统计每个产品下的离线设备数量", context);

        Assertions.assertEquals(AiQueryMode.RDB_SQL.name(), route.getQueryMode());
        Assertions.assertTrue(route.getRouteTags().contains("RUNTIME_SEMANTIC_IGNORED"));
    }

    @Test
    void shouldFallbackToRdbForGenericModuleStatisticEvenWithRuntimeCandidates() {
        AiSemanticFieldVO runtimeField = buildField("co2", "二氧化碳", "REDIS_VALUE");

        AiSemanticContextVO context = new AiSemanticContextVO();
        context.setRuntimeFields(List.of(runtimeField));

        AiQueryRouteVO route = router.route("统计每个厂商下的离线物联卡数量", context);

        Assertions.assertEquals(AiQueryMode.RDB_SQL.name(), route.getQueryMode());
        Assertions.assertTrue(route.getRouteReason().contains("关系库"));
    }

    @Test
    void shouldRouteCurrentRuntimeMetricWhenQuestionMatchesThingModel() {
        AiSemanticFieldVO runtimeField = buildField("temperature", "空气温度", "REDIS_VALUE");
        runtimeField.setAliases(List.of("空气温度", "温度"));
        runtimeField.setQueryHints(List.of("device=★智能开关产品10", "serialNumber=DEVICE88888888", "product=★智能开关产品"));

        AiSemanticContextVO context = new AiSemanticContextVO();
        context.setRuntimeFields(List.of(runtimeField));

        AiQueryRouteVO route = router.route("查询设备★智能开关产品10当前温度是多少", context);

        Assertions.assertEquals(AiQueryMode.REDIS_VALUE.name(), route.getQueryMode());
        Assertions.assertTrue(route.getRouteTags().contains("CURRENT_INTENT"));
    }

    @Test
    void shouldRouteImplicitCurrentValueQuestionToRedisWhenRuntimeMetricMatches() {
        AiSemanticFieldVO runtimeField = buildField("pm10", "PM10", "REDIS_VALUE");
        runtimeField.setAliases(List.of("PM10", "pm10"));
        runtimeField.setQueryHints(List.of("device=远程采集控制设备001", "serialNumber=REMOTE001", "product=远程采集控制设备"));

        AiSemanticContextVO context = new AiSemanticContextVO();
        context.setRuntimeFields(List.of(runtimeField));

        AiQueryRouteVO route = router.route("远程采集控制设备的PM10数值是多少", context);

        Assertions.assertEquals(AiQueryMode.REDIS_VALUE.name(), route.getQueryMode());
        Assertions.assertTrue(route.getRouteTags().contains("CURRENT_INTENT"));
        Assertions.assertTrue(route.getRouteReason().contains("Redis"));
    }

    private AiSemanticFieldVO buildField(String sourceCode, String semanticName, String dataSourceType) {
        AiSemanticFieldVO field = new AiSemanticFieldVO();
        field.setSourceCode(sourceCode);
        field.setSemanticName(semanticName);
        field.setDataSourceType(dataSourceType);
        return field;
    }
}
