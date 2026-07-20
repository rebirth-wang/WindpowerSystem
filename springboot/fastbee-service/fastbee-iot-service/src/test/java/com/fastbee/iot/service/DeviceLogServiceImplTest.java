package com.fastbee.iot.service;

import static com.fastbee.framework.core.util.RandomUtils.randomLongId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.DeviceLog;
import com.fastbee.iot.mapper.DeviceLogMapper;
import com.fastbee.iot.mapper.DeviceMapper;
import com.fastbee.iot.model.DeviceReport;
import com.fastbee.iot.model.HistoryModel;
import com.fastbee.iot.model.MonitorModel;
import com.fastbee.iot.model.param.DataCenterParam;
import com.fastbee.iot.model.vo.ThingsModelLogCountVO;
import com.fastbee.iot.model.vo.ThingsModelVO;
import com.fastbee.iot.service.impl.DeviceLogServiceImpl;
import com.fastbee.iot.tsdb.service.ILogService;

/**
 * {@link DeviceLogServiceImpl} 单元测试
 *
 * <p>跳过依赖 SecurityUtils 的 insertDeviceLog 方法。</p>
 *
 * @author fastbee
 */
@DisplayName("设备日志 Service 单元测试")
public class DeviceLogServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private DeviceLogServiceImpl deviceLogService;

    @Mock
    private DeviceLogMapper deviceLogMapper;
    @Mock
    private ILogService logService;
    @Mock
    private IThingsModelService thingsModelService;
    @Mock
    private DeviceMapper deviceMapper;

    @Test
    @DisplayName("selectDeviceLogByLogId - 应委托 Mapper")
    void testSelectDeviceLogByLogId_ShouldDelegate() {
        Long id = randomLongId();
        DeviceLog row = new DeviceLog();
        when(deviceLogMapper.selectDeviceLogByLogId(id)).thenReturn(row);

        assertSame(row, deviceLogService.selectDeviceLogByLogId(id));
        verify(deviceLogMapper).selectDeviceLogByLogId(id);
    }

    @Test
    @DisplayName("selectDeviceLogList - isMonitor 为空时应默认 0 并委托 TSDB")
    void testSelectDeviceLogList_DefaultIsMonitor_ShouldDelegate() {
        DeviceLog query = new DeviceLog();
        query.setIsMonitor(null);
        Page<DeviceLog> page = new Page<>();
        page.setTotal(0);
        when(logService.selectDeviceLogList(any(DeviceLog.class))).thenReturn(page);

        Page<DeviceLog> result = deviceLogService.selectDeviceLogList(query);

        assertNotNull(result);
        ArgumentCaptor<DeviceLog> captor = ArgumentCaptor.forClass(DeviceLog.class);
        verify(logService).selectDeviceLogList(captor.capture());
        assertEquals((byte) 0, captor.getValue().getIsMonitor());
    }

    @Test
    @DisplayName("selectMonitorList - 应委托 TSDB")
    void testSelectMonitorList_ShouldDelegate() {
        DeviceLog query = new DeviceLog();
        when(logService.selectMonitorList(query)).thenReturn(List.of(new MonitorModel()));

        List<MonitorModel> result = deviceLogService.selectMonitorList(query);

        assertEquals(1, result.size());
        verify(logService).selectMonitorList(query);
    }

    @Test
    @DisplayName("updateDeviceLog / deleteDeviceLog - 应委托 Mapper")
    void testUpdateAndDelete_ShouldDelegate() {
        DeviceLog log = new DeviceLog();
        when(deviceLogMapper.updateDeviceLog(log)).thenReturn(1);
        when(deviceLogMapper.deleteDeviceLogByLogId(1L)).thenReturn(1);

        assertEquals(1, deviceLogService.updateDeviceLog(log));
        assertEquals(1, deviceLogService.deleteDeviceLogByLogId(1L));
        verify(deviceLogMapper).updateDeviceLog(log);
        verify(deviceLogMapper).deleteDeviceLogByLogId(1L);
    }

    @Test
    @DisplayName("selectHistoryList / listHistory - 应委托 TSDB")
    void testHistoryDelegates_ShouldDelegate() {
        DeviceLog query = new DeviceLog();
        when(logService.selectHistoryList(query)).thenReturn(Map.of());
        when(logService.listHistory(query)).thenReturn(List.of(new HistoryModel()));

        assertNotNull(deviceLogService.selectHistoryList(query));
        assertEquals(1, deviceLogService.listHistory(query).size());
        verify(logService).selectHistoryList(query);
        verify(logService).listHistory(query);
    }

    @Test
    @DisplayName("listhistoryGroupByCreateTime - 应转换为 JSONObject 列表")
    void testListhistoryGroupByCreateTime_ShouldTransform() {
        DeviceLog query = new DeviceLog();
        HistoryModel h = new HistoryModel();
        h.setIdentify("a,b");
        h.setValue("1,2");
        h.setTime(new Date());
        Page<HistoryModel> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(h));
        when(logService.listhistoryGroupByCreateTime(query)).thenReturn(page);

        List<JSONObject> result = deviceLogService.listhistoryGroupByCreateTime(query);

        assertEquals(1, result.size());
        JSONObject obj = result.get(0);
        assertTrue(obj.containsKey("time"));
        assertEquals("a", obj.getString("name1"));
        assertEquals("1", obj.getString("value1"));
        assertEquals("b", obj.getString("name2"));
        assertEquals("2", obj.getString("value2"));
    }

    @Test
    @DisplayName("countThingsModelInvoke - 应委托 TSDB")
    void testCountThingsModelInvoke_ShouldDelegate() {
        DataCenterParam param = new DataCenterParam();
        when(logService.countThingsModelInvoke(param)).thenReturn(List.of(new ThingsModelLogCountVO()));

        List<ThingsModelLogCountVO> result = deviceLogService.countThingsModelInvoke(param);

        assertEquals(1, result.size());
        verify(logService).countThingsModelInvoke(param);
    }

    @Test
    @DisplayName("selectDeviceReportData - 应组装上报/未上报列表")
    void testSelectDeviceReportData_ShouldBuildReport() {
        Date begin = new Date(System.currentTimeMillis() - 1000);
        Date end = new Date();

        Device device = new Device();
        device.setSerialNumber("SN");
        device.setProductId(1L);
        when(deviceMapper.selectList(any())).thenReturn(List.of(device));

        ThingsModelVO tm = new ThingsModelVO();
        tm.setIdentifier("temp");
        when(thingsModelService.selectThingsModelList(any(ThingsModelVO.class))).thenReturn(List.of(tm));

        DeviceLog last = new DeviceLog();
        last.setIdentify("temp");
        when(logService.selectLastReport(any(DeviceLog.class))).thenReturn(last);

        List<DeviceReport> result = deviceLogService.selectDeviceReportData(begin, end);

        assertEquals(1, result.size());
        DeviceReport report = result.get(0);
        assertEquals("SN", report.getSerialNumber());
        assertEquals(1, report.getReportList().size());
        assertTrue(report.getUnReportList().isEmpty());
    }
}

