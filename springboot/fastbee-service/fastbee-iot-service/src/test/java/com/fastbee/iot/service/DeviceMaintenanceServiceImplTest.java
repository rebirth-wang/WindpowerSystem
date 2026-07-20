package com.fastbee.iot.service;

import static com.fastbee.framework.core.util.RandomUtils.randomLongId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.common.extend.enums.MaintenanceUnitEnum;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.DeviceMaintenance;
import com.fastbee.iot.mapper.DeviceMaintenanceMapper;
import com.fastbee.iot.service.impl.DeviceMaintenanceServiceImpl;

/**
 * {@link DeviceMaintenanceServiceImpl} 单元测试
 *
 * <p>仅覆盖不依赖 SecurityUtils 登录态的方法/分支。</p>
 *
 * @author fastbee
 */
@DisplayName("设备维保 Service 单元测试")
public class DeviceMaintenanceServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    @Spy
    private DeviceMaintenanceServiceImpl deviceMaintenanceService;

    @Mock
    private DeviceMaintenanceMapper deviceMaintenanceMapper;
    @Mock
    private IWorkOrderService workOrderService;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), DeviceMaintenance.class);
        ReflectionTestUtils.setField(deviceMaintenanceService, "baseMapper", deviceMaintenanceMapper);
    }

    @Test
    @DisplayName("updateStatusByIds - 应批量更新状态")
    void testUpdateStatusByIds_ShouldUpdate() {
        when(deviceMaintenanceMapper.update(any(), any(LambdaUpdateWrapper.class))).thenReturn(2);

        deviceMaintenanceService.updateStatusByIds(List.of(1L, 2L), 3);

        verify(deviceMaintenanceMapper).update(any(), any(LambdaUpdateWrapper.class));
    }

    @Test
    @DisplayName("deleteWithCacheByIds - 删除成功应调用工单清理")
    void testDeleteWithCacheByIds_DeleteSuccess_ShouldRemoveWorkOrders() {
        Long[] ids = {randomLongId(), randomLongId()};
        doReturn(true).when(deviceMaintenanceService).removeByIds(anyList());

        Boolean result = deviceMaintenanceService.deleteWithCacheByIds(ids, true);

        assertTrue(result);
        verify(workOrderService).removeDeviceMaintenanceWorkOrder(anyList());
    }

    @Test
    @DisplayName("deleteWithCacheByIds - 删除失败不应调用工单清理")
    void testDeleteWithCacheByIds_DeleteFail_ShouldNotRemoveWorkOrders() {
        Long[] ids = {randomLongId()};
        doReturn(false).when(deviceMaintenanceService).removeByIds(anyList());

        Boolean result = deviceMaintenanceService.deleteWithCacheByIds(ids, true);

        assertFalse(result);
        verify(workOrderService, never()).removeDeviceMaintenanceWorkOrder(anyList());
    }

    @Test
    @DisplayName("calculateNextMaintenanceTime - DAY 单位应加天数")
    void testCalculateNextMaintenanceTime_Day_ShouldPlusDays() {
        DeviceMaintenance m = new DeviceMaintenance();
        m.setMaintenancePeriodUnit(MaintenanceUnitEnum.DAY.getUnit());
        m.setMaintenancePeriod(3);
        Date now = new Date();

        Date next = deviceMaintenanceService.calculateNextMaintenanceTime(m, now);

        assertNotNull(next);
        assertTrue(next.after(now));
    }

    @Test
    @DisplayName("calculateNextMaintenanceTime - WEEK 单位应加周数")
    void testCalculateNextMaintenanceTime_Week_ShouldPlusWeeks() {
        DeviceMaintenance m = new DeviceMaintenance();
        m.setMaintenancePeriodUnit(MaintenanceUnitEnum.WEEK.getUnit());
        m.setMaintenancePeriod(1);
        Date now = new Date();

        Date next = deviceMaintenanceService.calculateNextMaintenanceTime(m, now);

        assertNotNull(next);
        assertTrue(next.after(now));
    }
}

