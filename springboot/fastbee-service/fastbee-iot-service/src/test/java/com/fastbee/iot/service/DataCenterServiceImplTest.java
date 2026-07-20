package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.cache.IDeviceCache;
import com.fastbee.iot.cache.ITSLCache;
import com.fastbee.iot.model.DeviceHistoryParam;
import com.fastbee.iot.model.HistoryModel;
import com.fastbee.iot.model.scenemodel.SceneHistoryParam;
import com.fastbee.iot.model.vo.DataCenterExportVO;
import com.fastbee.iot.model.vo.FunctionLogVO;
import com.fastbee.iot.model.vo.ThingsModelLogCountVO;
import com.fastbee.iot.service.impl.DataCenterServiceImpl;
import com.fastbee.iot.tsdb.service.ILogService;

@DisplayName("数据中心 Service 单元测试")
class DataCenterServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    @Spy
    private DataCenterServiceImpl dataCenterService;

    @Mock
    private IDeviceLogService deviceLogService;
    @Mock
    private IFunctionLogService functionLogService;
    @Mock
    private ILogService logService;
    @Mock
    private IDeviceService deviceService;
    @Mock
    private ISceneModelDataService sceneModelDataService;
    @Mock
    private IThingsModelService thingsModelService;
    @Mock
    private IAlertLogService alertLogService;
    @Mock
    private ITSLCache itslCache;
    @Mock
    private IDeviceCache deviceCache;

    @Test
    @DisplayName("deviceHistory - 无可查询标识时应返回空列表")
    void testDeviceHistory_NoIdentifiers_ShouldReturnEmptyList() {
        DeviceHistoryParam param = new DeviceHistoryParam();
        param.setDeviceId(1L);
        when(deviceService.listThingsModel(1L)).thenReturn(Collections.emptyList());

        List<com.alibaba.fastjson2.JSONObject> result = dataCenterService.deviceHistory(param);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("queryDeviceHistory - 属性/功能/事件历史应合并返回")
    void testQueryDeviceHistory_ShouldMergeHistories() {
        DeviceHistoryParam param = new DeviceHistoryParam();
        param.setSerialNumber("SN001");
        param.setBeginTime("2024-01-01 00:00:00");
        param.setEndTime("2024-01-01 23:59:59");
        DeviceHistoryParam.IdentifierVO p = new DeviceHistoryParam.IdentifierVO();
        p.setIdentifier("temp");
        p.setType(1);
        DeviceHistoryParam.IdentifierVO f = new DeviceHistoryParam.IdentifierVO();
        f.setIdentifier("switch");
        f.setType(2);
        DeviceHistoryParam.IdentifierVO e = new DeviceHistoryParam.IdentifierVO();
        e.setIdentifier("alert");
        e.setType(3);
        param.setIdentifierList(List.of(p, f, e));
        when(deviceLogService.listHistory(any())).thenReturn(List.of(new HistoryModel()));
        when(functionLogService.listHistory(any(FunctionLogVO.class))).thenReturn(List.of(new HistoryModel()));
        when(logService.listHistory(any())).thenReturn(List.of(new HistoryModel()));

        List<HistoryModel> result = dataCenterService.queryDeviceHistory(param);

        assertEquals(3, result.size());
    }

    @Test
    @DisplayName("sceneExport - 无数据时应返回空列表")
    void testSceneExport_NoData_ShouldReturnEmptyList() {
        SceneHistoryParam param = new SceneHistoryParam();
        doReturn(Collections.emptyList()).when(dataCenterService).sceneHistory(param);

        List<DataCenterExportVO> result = dataCenterService.sceneExport(param);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getDeviceHistoryDataForExport - 设备元数据不存在时应返回空列表")
    void testGetDeviceHistoryDataForExport_NoMeta_ShouldReturnEmptyList() {
        DeviceHistoryParam param = new DeviceHistoryParam();
        param.setSerialNumber("SN001");
        when(deviceCache.getDeviceMetaDataCache("SN001")).thenReturn(null);

        List<HistoryModel> result = dataCenterService.getDeviceHistoryDataForExport(param);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("countThingsModelInvoke - 应合并两类统计")
    void testCountThingsModelInvoke_ShouldMergeLists() {
        when(deviceLogService.countThingsModelInvoke(any())).thenReturn(List.of(new ThingsModelLogCountVO()));
        when(functionLogService.countThingsModelInvoke(any())).thenReturn(List.of(new ThingsModelLogCountVO()));

        List<ThingsModelLogCountVO> result = dataCenterService.countThingsModelInvoke(new com.fastbee.iot.model.param.DataCenterParam());

        assertEquals(2, result.size());
    }
}
