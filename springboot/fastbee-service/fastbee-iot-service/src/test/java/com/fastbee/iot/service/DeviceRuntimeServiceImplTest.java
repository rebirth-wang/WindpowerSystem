package com.fastbee.iot.service;

import static com.fastbee.framework.core.util.RandomUtils.randomLongId;
import static com.fastbee.framework.core.util.RandomUtils.randomString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.fastbee.common.enums.FunctionReplyStatus;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.cache.ITSLCache;
import com.fastbee.iot.cache.ITSLValueCache;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;
import com.fastbee.iot.model.ThingsModels.ValueItem;
import com.fastbee.iot.model.vo.FunctionLogVO;
import com.fastbee.iot.service.impl.DeviceRuntimeServiceImpl;

/**
 * {@link DeviceRuntimeServiceImpl} 单元测试
 *
 * @author fastbee
 */
@DisplayName("设备运行时 Service 单元测试")
public class DeviceRuntimeServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private DeviceRuntimeServiceImpl deviceRuntimeService;

    @Mock
    private IFunctionLogService logService;
    @Mock
    private IDeviceService deviceService;
    @Mock
    private ITSLCache itslCache;
    @Mock
    private ITSLValueCache itslValueCache;

    @Test
    @DisplayName("runtimeBySerialNumber - serialNumber 为空应返回空列表")
    void testRuntimeBySerialNumber_EmptySerial_ShouldReturnEmptyList() {
        assertTrue(deviceRuntimeService.runtimeBySerialNumber("").isEmpty());
        assertTrue(deviceRuntimeService.runtimeBySerialNumber(null).isEmpty());
    }

    @Test
    @DisplayName("runtimeBySerialNumber - 应将缓存值合并到物模型列表")
    void testRuntimeBySerialNumber_ShouldMergeValueItems() {
        String sn = randomString();
        Long productId = randomLongId();

        Device device = new Device();
        device.setSerialNumber(sn);
        device.setProductId(productId);
        when(deviceService.selectDeviceBySerialNumber(sn)).thenReturn(device);

        ThingsModelValueItem m1 = new ThingsModelValueItem().setId("temp");
        ThingsModelValueItem m2 = new ThingsModelValueItem().setId("humi");
        when(itslCache.getThingsModelList(productId)).thenReturn(List.of(m1, m2));

        ValueItem v1 = new ValueItem();
        v1.setId("temp");
        v1.setValue("12");
        v1.setShadow("s");
        v1.setTs(new Date());
        when(itslValueCache.getCacheDeviceStatus(productId, sn)).thenReturn(List.of(v1));

        List<ThingsModelValueItem> result = deviceRuntimeService.runtimeBySerialNumber(sn);

        assertEquals(2, result.size());
        ThingsModelValueItem temp = result.stream().filter(x -> "temp".equals(x.getId())).findFirst().orElseThrow();
        assertEquals("12", temp.getValue());
        assertEquals("s", temp.getShadow());
        assertNotNull(temp.getTs());
        ThingsModelValueItem humi = result.stream().filter(x -> "humi".equals(x.getId())).findFirst().orElseThrow();
        assertNull(humi.getValue());
    }

    @Test
    @DisplayName("runtimeReply - 超过 10s 未回复应将 NORELY 置为 FAIL")
    void testRuntimeReply_NoReplyOver10s_ShouldMarkFail() {
        String sn = "SN";
        FunctionLogVO row = new FunctionLogVO();
        row.setSerialNumber(sn);
        row.setResultCode(FunctionReplyStatus.NORELY.getCode());
        row.setResultMsg("pending");
        row.setCreateTime(new Date(System.currentTimeMillis() - 20_000));
        row.setReplyTime(null);
        Page<FunctionLogVO> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(row));
        when(logService.selectFunctionLogList(any(FunctionLogVO.class))).thenReturn(page);

        Page<FunctionLogVO> result = deviceRuntimeService.runtimeReply(sn);

        assertEquals(1, result.getRecords().size());
        FunctionLogVO out = result.getRecords().get(0);
        assertNotNull(out.getReplyTime());
        assertEquals(FunctionReplyStatus.FAIl.getCode(), out.getResultCode());
        assertEquals(FunctionReplyStatus.FAIl.getMessage(), out.getShowValue());
    }
}

