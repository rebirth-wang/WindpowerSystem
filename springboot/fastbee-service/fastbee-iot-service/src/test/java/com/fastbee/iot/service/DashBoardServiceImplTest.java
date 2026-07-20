package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.core.redis.RedisKeyBuilder;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.mapper.DeviceMapper;
import com.fastbee.iot.model.HistoryBo;
import com.fastbee.iot.model.dashBoard.DeviceMatchBo;
import com.fastbee.iot.service.impl.DashBoardServiceImpl;
import com.fastbee.iot.tsdb.service.ILogService;

@DisplayName("大屏 Service 单元测试")
class DashBoardServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private DashBoardServiceImpl dashBoardService;

    @Mock
    private RedisCache redisCache;
    @Mock
    private DeviceMapper deviceMapper;
    @Mock
    private ILogService logService;

    @BeforeEach
    void setUp() {
        SecurityUtilsTestHelper.prepareSecurityUtils();
    }

    @Test
    @DisplayName("dashCombine - 设备不存在时应抛异常")
    void testDashCombine_DeviceNotFound_ShouldThrowException() {
        DeviceMatchBo bo = new DeviceMatchBo();
        bo.setDeviceId(1L);
        bo.setType(5);
        bo.setIdentifier(new String[]{"temp"});
        when(deviceMapper.selectById(1L)).thenReturn(null);

        assertThrows(ServiceException.class, () -> dashBoardService.dashCombine(bo));
    }

    @Test
    @DisplayName("dashCombine - 实时查询时应从缓存取值")
    void testDashCombine_RealTime_ShouldReadFromCache() {
        Device device = new Device();
        device.setDeviceId(1L);
        device.setProductId(10L);
        device.setSerialNumber("SN001");
        DeviceMatchBo bo = new DeviceMatchBo();
        bo.setDeviceId(1L);
        bo.setType(5);
        bo.setIdentifier(new String[]{"temp"});
        when(deviceMapper.selectById(1L)).thenReturn(device);
        when(redisCache.getCacheMapValue(RedisKeyBuilder.buildTSLVCacheKey(10L, "SN001"), "temp"))
                .thenReturn("{\"value\":\"36.5\"}");

        AjaxResult result = dashBoardService.dashCombine(bo);

        assertEquals(200, result.get(AjaxResult.CODE_TAG));
        List<Map<String, Object>> data = (List<Map<String, Object>>) result.get(AjaxResult.DATA_TAG);
        assertEquals("temp", data.get(0).get("identifier"));
        assertEquals("36.5", data.get(0).get("value"));
    }

    @Test
    @DisplayName("dashCombine - 多属性历史查询时应合并 NameValue 列表")
    void testDashCombine_MultiAttributeHistory_ShouldMergeNameValue() {
        Device device = new Device();
        device.setDeviceId(1L);
        device.setSerialNumber("SN001");
        DeviceMatchBo bo = new DeviceMatchBo();
        bo.setDeviceId(1L);
        bo.setType(2);
        bo.setIdentifier(new String[]{"temp", "hum"});
        when(deviceMapper.selectById(1L)).thenReturn(device);
        HistoryBo temp = new HistoryBo();
        temp.setIdentify("temp");
        temp.setValue("36.5");
        temp.setTime(new Date());
        HistoryBo hum = new HistoryBo();
        hum.setIdentify("hum");
        hum.setValue("80");
        hum.setTime(new Date());
        when(logService.selectHistorySingleBo(any())).thenReturn(List.of(temp), List.of(hum));

        AjaxResult result = dashBoardService.dashCombine(bo);

        assertEquals(200, result.get(AjaxResult.CODE_TAG));
        List<?> data = (List<?>) result.get(AjaxResult.DATA_TAG);
        assertEquals(2, data.size());
    }
}
