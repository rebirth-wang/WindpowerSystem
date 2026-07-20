package com.fastbee.ai.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.fastbee.ai.model.enums.AiQueryMode;
import com.fastbee.ai.model.vo.AiHybridQueryResultVO;
import com.fastbee.ai.model.vo.AiQueryGovernanceSnapshotVO;
import com.fastbee.ai.model.vo.AiQueryRouteVO;
import com.fastbee.ai.model.vo.AiSemanticContextVO;
import com.fastbee.ai.model.vo.AiSemanticFieldVO;
import com.fastbee.ai.model.vo.AiTsdbQueryResultVO;
import com.fastbee.ai.service.IAiDeviceResolveService;
import com.fastbee.ai.service.IAiQueryGovernanceService;
import com.fastbee.ai.service.IAiQuerySourceRouter;
import com.fastbee.ai.service.IAiSemanticNormalizationService;
import com.fastbee.iot.cache.ITSLValueCache;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.HistoryModel;
import com.fastbee.iot.model.ThingsModelItem.Datatype;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;
import com.fastbee.iot.model.vo.FunctionLogVO;
import com.fastbee.iot.service.IFunctionLogService;
import com.fastbee.iot.service.IThingsModelService;
import com.fastbee.iot.tsdb.service.ILogService;

class AiThingModelFunctionLogQueryServiceTest {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String FUNCTION_IDENTIFIER = "array_01_light_color";

    @Test
    void shouldQueryFunctionHistoryFromFunctionLogInTsdbService() throws Exception {
        AiTsdbQueryServiceImpl service = new AiTsdbQueryServiceImpl();

        IAiSemanticNormalizationService normalizationService = mock(IAiSemanticNormalizationService.class);
        IAiQuerySourceRouter querySourceRouter = mock(IAiQuerySourceRouter.class);
        IAiDeviceResolveService deviceResolveService = mock(IAiDeviceResolveService.class);
        IAiQueryGovernanceService governanceService = mock(IAiQueryGovernanceService.class);
        IThingsModelService thingsModelService = mock(IThingsModelService.class);
        ILogService logService = mock(ILogService.class);
        IFunctionLogService functionLogService = mock(IFunctionLogService.class);

        inject(service, "aiSemanticNormalizationService", normalizationService);
        inject(service, "aiQuerySourceRouter", querySourceRouter);
        inject(service, "aiDeviceResolveService", deviceResolveService);
        inject(service, "aiQueryGovernanceService", governanceService);
        inject(service, "thingsModelService", thingsModelService);
        inject(service, "logService", logService);
        inject(service, "functionLogService", functionLogService);

        String question = "查询SN001最近1小时灯光色值1历史数据";
        AiSemanticFieldVO field = buildFunctionField();
        AiSemanticContextVO context = new AiSemanticContextVO();
        context.setQuestion(question);
        context.setRuntimeFields(List.of(field));
        when(normalizationService.buildNl2SqlContext(question)).thenReturn(context);
        when(querySourceRouter.route(eq(question), any())).thenReturn(buildRoute(AiQueryMode.TSDB_QUERY.name()));
        when(deviceResolveService.resolveRequiredDeviceMetaData(question)).thenReturn(buildDeviceMetaData());
        when(governanceService.validateMultiSourceAccess(any(), eq(FUNCTION_IDENTIFIER), eq(AiQueryMode.TSDB_QUERY.name())))
                .thenReturn(buildGovernanceSnapshot());
        when(thingsModelService.getSingleThingModels(eq(100L), eq(FUNCTION_IDENTIFIER))).thenReturn(buildValueItem());
        when(functionLogService.listHistory(any(FunctionLogVO.class))).thenReturn(List.of(
                buildHistoryModel("2024-01-01 10:00:00", "20", FUNCTION_IDENTIFIER),
                buildHistoryModel("2024-01-01 09:00:00", "18", FUNCTION_IDENTIFIER)
        ));

        AiTsdbQueryResultVO result = service.query(question);

        assertEquals(AiQueryMode.TSDB_QUERY.name(), result.getQueryMode());
        assertEquals(2, result.getHistoryPoints().size());
        assertTrue(result.getMessages().stream().anyMatch(message -> message.contains("FunctionLog")));
        assertTrue(result.getHistoryPoints().get(0).getTime().compareTo(result.getHistoryPoints().get(1).getTime()) <= 0);

        ArgumentCaptor<FunctionLogVO> captor = ArgumentCaptor.forClass(FunctionLogVO.class);
        verify(functionLogService).listHistory(captor.capture());
        FunctionLogVO query = captor.getValue();
        assertEquals("SN001", query.getSerialNumber());
        assertEquals(List.of(FUNCTION_IDENTIFIER), query.getIdentifyList());
        assertNotNull(query.getBeginTime());
        assertNotNull(query.getEndTime());
        assertOneHourWindow(query);
        verify(logService, never()).selectHistorySingleBo(any(com.fastbee.iot.domain.DeviceLog.class));
    }

    @Test
    void shouldQueryFunctionHistoryFromFunctionLogInHybridService() throws Exception {
        AiHybridQueryServiceImpl service = new AiHybridQueryServiceImpl();

        IAiSemanticNormalizationService normalizationService = mock(IAiSemanticNormalizationService.class);
        IAiQuerySourceRouter querySourceRouter = mock(IAiQuerySourceRouter.class);
        IAiDeviceResolveService deviceResolveService = mock(IAiDeviceResolveService.class);
        IAiQueryGovernanceService governanceService = mock(IAiQueryGovernanceService.class);
        ITSLValueCache itslValueCache = mock(ITSLValueCache.class);
        IThingsModelService thingsModelService = mock(IThingsModelService.class);
        ILogService logService = mock(ILogService.class);
        IFunctionLogService functionLogService = mock(IFunctionLogService.class);

        inject(service, "aiSemanticNormalizationService", normalizationService);
        inject(service, "aiQuerySourceRouter", querySourceRouter);
        inject(service, "aiDeviceResolveService", deviceResolveService);
        inject(service, "aiQueryGovernanceService", governanceService);
        inject(service, "itslValueCache", itslValueCache);
        inject(service, "thingsModelService", thingsModelService);
        inject(service, "logService", logService);
        inject(service, "functionLogService", functionLogService);

        String question = "查询SN001最近1小时灯光色值1历史数据";
        AiSemanticFieldVO field = buildFunctionField();
        AiSemanticContextVO context = new AiSemanticContextVO();
        context.setQuestion(question);
        context.setRuntimeFields(List.of(field));
        when(normalizationService.buildNl2SqlContext(question)).thenReturn(context);
        when(querySourceRouter.route(eq(question), any())).thenReturn(buildRoute(AiQueryMode.HYBRID_PIPELINE.name()));
        when(deviceResolveService.resolveRequiredDeviceMetaData(question)).thenReturn(buildDeviceMetaData());
        when(governanceService.validateMultiSourceAccess(any(), eq(FUNCTION_IDENTIFIER), eq(AiQueryMode.HYBRID_PIPELINE.name())))
                .thenReturn(buildGovernanceSnapshot());
        when(thingsModelService.getSingleThingModels(eq(100L), eq(FUNCTION_IDENTIFIER))).thenReturn(buildValueItem());
        when(functionLogService.listHistory(any(FunctionLogVO.class))).thenReturn(List.of(
                buildHistoryModel("2024-01-01 10:00:00", "20", FUNCTION_IDENTIFIER),
                buildHistoryModel("2024-01-01 09:00:00", "18", FUNCTION_IDENTIFIER)
        ));

        AiHybridQueryResultVO result = service.query(question);

        assertEquals(AiQueryMode.HYBRID_PIPELINE.name(), result.getQueryMode());
        assertEquals(2, result.getHistoryPoints().size());
        assertTrue(result.getMessages().stream().anyMatch(message -> message.contains("FunctionLog")));
        assertTrue(result.getHistoryPoints().get(0).getTime().compareTo(result.getHistoryPoints().get(1).getTime()) <= 0);

        ArgumentCaptor<FunctionLogVO> captor = ArgumentCaptor.forClass(FunctionLogVO.class);
        verify(functionLogService).listHistory(captor.capture());
        FunctionLogVO query = captor.getValue();
        assertEquals("SN001", query.getSerialNumber());
        assertEquals(List.of(FUNCTION_IDENTIFIER), query.getIdentifyList());
        assertNotNull(query.getBeginTime());
        assertNotNull(query.getEndTime());
        assertOneHourWindow(query);
        verify(logService, never()).selectHistorySingleBo(any(com.fastbee.iot.domain.DeviceLog.class));
    }

    @Test
    void shouldFallbackFunctionCurrentValueFromFunctionLogInHybridService() throws Exception {
        AiHybridQueryServiceImpl service = new AiHybridQueryServiceImpl();

        IAiSemanticNormalizationService normalizationService = mock(IAiSemanticNormalizationService.class);
        IAiQuerySourceRouter querySourceRouter = mock(IAiQuerySourceRouter.class);
        IAiDeviceResolveService deviceResolveService = mock(IAiDeviceResolveService.class);
        IAiQueryGovernanceService governanceService = mock(IAiQueryGovernanceService.class);
        ITSLValueCache itslValueCache = mock(ITSLValueCache.class);
        IThingsModelService thingsModelService = mock(IThingsModelService.class);
        ILogService logService = mock(ILogService.class);
        IFunctionLogService functionLogService = mock(IFunctionLogService.class);

        inject(service, "aiSemanticNormalizationService", normalizationService);
        inject(service, "aiQuerySourceRouter", querySourceRouter);
        inject(service, "aiDeviceResolveService", deviceResolveService);
        inject(service, "aiQueryGovernanceService", governanceService);
        inject(service, "itslValueCache", itslValueCache);
        inject(service, "thingsModelService", thingsModelService);
        inject(service, "logService", logService);
        inject(service, "functionLogService", functionLogService);

        String question = "查询SN001当前灯光色值1是多少";
        AiSemanticFieldVO field = buildFunctionField();
        AiSemanticContextVO context = new AiSemanticContextVO();
        context.setQuestion(question);
        context.setRuntimeFields(List.of(field));
        when(normalizationService.buildNl2SqlContext(question)).thenReturn(context);
        when(querySourceRouter.route(eq(question), any())).thenReturn(buildRoute(AiQueryMode.REDIS_VALUE.name()));
        when(deviceResolveService.resolveRequiredDeviceMetaData(question)).thenReturn(buildDeviceMetaData());
        when(governanceService.validateMultiSourceAccess(any(), eq(FUNCTION_IDENTIFIER), eq(AiQueryMode.HYBRID_PIPELINE.name())))
                .thenReturn(buildGovernanceSnapshot());
        when(thingsModelService.getSingleThingModels(eq(100L), eq(FUNCTION_IDENTIFIER))).thenReturn(buildValueItem());
        when(itslValueCache.getCacheIdentifier(eq(100L), eq("SN001"), eq(FUNCTION_IDENTIFIER))).thenReturn(null);
        when(functionLogService.listHistory(any(FunctionLogVO.class))).thenReturn(List.of(
                buildHistoryModel("2024-01-01 10:00:00", "20", FUNCTION_IDENTIFIER)
        ));

        AiHybridQueryResultVO result = service.query(question);

        assertEquals("20", result.getCurrentValue());
        assertTrue(result.getMessages().stream().anyMatch(message -> message.contains("FunctionLog")));
        verify(functionLogService).listHistory(any(FunctionLogVO.class));
        verify(logService, never()).selectLastReport(any(com.fastbee.iot.domain.DeviceLog.class));
    }

    @Test
    void shouldRecognizeFunctionModelByThingModelTypeWhenHintMissingInTsdbService() throws Exception {
        AiTsdbQueryServiceImpl service = new AiTsdbQueryServiceImpl();

        IAiSemanticNormalizationService normalizationService = mock(IAiSemanticNormalizationService.class);
        IAiQuerySourceRouter querySourceRouter = mock(IAiQuerySourceRouter.class);
        IAiDeviceResolveService deviceResolveService = mock(IAiDeviceResolveService.class);
        IAiQueryGovernanceService governanceService = mock(IAiQueryGovernanceService.class);
        IThingsModelService thingsModelService = mock(IThingsModelService.class);
        ILogService logService = mock(ILogService.class);
        IFunctionLogService functionLogService = mock(IFunctionLogService.class);

        inject(service, "aiSemanticNormalizationService", normalizationService);
        inject(service, "aiQuerySourceRouter", querySourceRouter);
        inject(service, "aiDeviceResolveService", deviceResolveService);
        inject(service, "aiQueryGovernanceService", governanceService);
        inject(service, "thingsModelService", thingsModelService);
        inject(service, "logService", logService);
        inject(service, "functionLogService", functionLogService);

        String question = "查询SN001最近1小时灯光色值1趋势";
        AiSemanticFieldVO field = buildFunctionFieldWithoutModelTypeHint();
        AiSemanticContextVO context = new AiSemanticContextVO();
        context.setQuestion(question);
        context.setRuntimeFields(List.of(field));
        when(normalizationService.buildNl2SqlContext(question)).thenReturn(context);
        when(querySourceRouter.route(eq(question), any())).thenReturn(buildRoute(AiQueryMode.TSDB_QUERY.name()));
        when(deviceResolveService.resolveRequiredDeviceMetaData(question)).thenReturn(buildDeviceMetaData());
        when(governanceService.validateMultiSourceAccess(any(), eq(FUNCTION_IDENTIFIER), eq(AiQueryMode.TSDB_QUERY.name())))
                .thenReturn(buildGovernanceSnapshot());
        when(thingsModelService.getSingleThingModels(eq(100L), eq(FUNCTION_IDENTIFIER))).thenReturn(buildValueItem());
        when(functionLogService.listHistory(any(FunctionLogVO.class))).thenReturn(List.of(
                buildHistoryModel("2024-01-01 10:00:00", "20", FUNCTION_IDENTIFIER)
        ));

        AiTsdbQueryResultVO result = service.query(question);

        assertEquals(1, result.getHistoryPoints().size());
        verify(functionLogService).listHistory(any(FunctionLogVO.class));
        verify(logService, never()).selectHistorySingleBo(any(com.fastbee.iot.domain.DeviceLog.class));
    }

    private void inject(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    private AiSemanticFieldVO buildFunctionField() {
        AiSemanticFieldVO field = new AiSemanticFieldVO();
        field.setSourceCode(FUNCTION_IDENTIFIER);
        field.setSemanticName("灯光色值1");
        field.setDataSourceType("TSDB_QUERY");
        field.setQueryHints(List.of("model-type=功能", "history-route=TSDB_QUERY", "current-route=REDIS_VALUE"));
        return field;
    }

    private AiSemanticFieldVO buildFunctionFieldWithoutModelTypeHint() {
        AiSemanticFieldVO field = new AiSemanticFieldVO();
        field.setSourceCode(FUNCTION_IDENTIFIER);
        field.setSemanticName("灯光色值1");
        field.setDataSourceType("TSDB_QUERY");
        field.setQueryHints(List.of("history-route=TSDB_QUERY", "current-route=REDIS_VALUE"));
        return field;
    }

    private void assertOneHourWindow(FunctionLogVO query) {
        long durationMillis = query.getEndTime().getTime() - query.getBeginTime().getTime();
        assertTrue(durationMillis >= 59L * 60L * 1000L && durationMillis <= 61L * 60L * 1000L);
    }

    private AiQueryRouteVO buildRoute(String queryMode) {
        AiQueryRouteVO route = new AiQueryRouteVO();
        route.setQueryMode(queryMode);
        route.setRouteReason("命中运行时物模型语义。");
        return route;
    }

    private DeviceMetaData buildDeviceMetaData() {
        Device device = new Device();
        device.setDeviceId(1L);
        device.setDeviceName("设备A");
        device.setSerialNumber("SN001");
        device.setProductId(100L);
        device.setTenantId(11L);

        DeviceMetaData deviceMetaData = new DeviceMetaData();
        deviceMetaData.setDevice(device);
        return deviceMetaData;
    }

    private AiQueryGovernanceSnapshotVO buildGovernanceSnapshot() {
        AiQueryGovernanceSnapshotVO snapshot = new AiQueryGovernanceSnapshotVO();
        snapshot.setDeviceId(1L);
        snapshot.setAccessValidated(Boolean.TRUE);
        snapshot.setProductName("产品A");
        snapshot.setPermissionChecks(List.of("权限校验通过"));
        return snapshot;
    }

    private ThingsModelValueItem buildValueItem() {
        ThingsModelValueItem valueItem = new ThingsModelValueItem();
        valueItem.setType(2);
        Datatype datatype = new Datatype();
        datatype.setUnit("℃");
        valueItem.setDatatype(datatype);
        return valueItem;
    }

    private HistoryModel buildHistoryModel(String time, String value, String identify) {
        HistoryModel historyModel = new HistoryModel();
        historyModel.setTime(Date.from(LocalDateTime.parse(time, TIME_FORMATTER).atZone(ZoneId.systemDefault()).toInstant()));
        historyModel.setValue(value);
        historyModel.setIdentify(identify);
        return historyModel;
    }
}
