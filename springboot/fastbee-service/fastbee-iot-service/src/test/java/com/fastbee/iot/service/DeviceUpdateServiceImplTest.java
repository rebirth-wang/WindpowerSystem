package com.fastbee.iot.service;

import static com.fastbee.framework.core.util.RandomUtils.randomLongId;
import static com.fastbee.framework.core.util.RandomUtils.randomString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.cache.IDeviceCache;
import com.fastbee.iot.cache.ITSLValueCache;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.mapper.DeviceMapper;
import com.fastbee.iot.service.impl.DeviceUpdateServiceImpl;
import com.fastbee.iot.tsdb.service.ILogService;

/**
 * {@link DeviceUpdateServiceImpl} 单元测试
 *
 * <p>仅覆盖不依赖外部 HTTP / IP 定位的逻辑分支。</p>
 *
 * @author fastbee
 */
@DisplayName("设备更新 Service 单元测试")
public class DeviceUpdateServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private DeviceUpdateServiceImpl deviceUpdateService;

    @Mock
    private DeviceMapper deviceMapper;
    @Mock
    private IDeviceCache deviceCache;
    @Mock
    private ITSLValueCache itslValueCache;
    @Mock
    private ILogService logService;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), Device.class);
        ReflectionTestUtils.setField(deviceUpdateService, "baseMapper", deviceMapper);
    }

    @Test
    @DisplayName("updateDevice - 未激活(status=1)时应刷新物模型缓存并更新")
    void testUpdateDevice_Inactive_ShouldRefreshStatusCache() {
        Device device = new Device();
        device.setDeviceId(randomLongId());
        device.setSerialNumber(randomString());
        device.setStatus(1);
        device.setProductId(randomLongId());
        when(deviceMapper.updateById(device)).thenReturn(1);

        int rows = deviceUpdateService.updateDevice(device);

        assertEquals(1, rows);
        assertNotNull(device.getUpdateTime());
        verify(deviceCache).deleteDeviceProtocolDetailCache(device.getSerialNumber());
        verify(itslValueCache).addCacheDeviceStatus(device.getProductId(), device.getSerialNumber());
        verify(deviceMapper).updateById(device);
    }

    @Test
    @DisplayName("updateDevice - 已激活(非1)时应清空产品信息并更新")
    void testUpdateDevice_Activated_ShouldClearProductFields() {
        Device device = new Device();
        device.setDeviceId(randomLongId());
        device.setSerialNumber(randomString());
        device.setStatus(2);
        device.setProductId(randomLongId());
        device.setProductName("p");
        when(deviceMapper.updateById(device)).thenReturn(1);

        int rows = deviceUpdateService.updateDevice(device);

        assertEquals(1, rows);
        assertNull(device.getProductId());
        assertNull(device.getProductName());
        verify(deviceMapper).updateById(device);
        verifyNoInteractions(itslValueCache);
    }

    @Test
    @DisplayName("updateDeviceBySerialNumber - 应按 serialNumber 更新并清理协议缓存")
    void testUpdateDeviceBySerialNumber_ShouldUpdateByWrapper() {
        Device device = new Device();
        device.setSerialNumber("SN");
        when(deviceMapper.update(eq(device), any())).thenReturn(1);

        int rows = deviceUpdateService.updateDeviceBySerialNumber(device);

        assertEquals(1, rows);
        verify(deviceCache).deleteDeviceProtocolDetailCache("SN");
        verify(deviceMapper).update(eq(device), any());
    }

    @Test
    @DisplayName("updateProductNameByProductId - 应更新设备产品名称")
    void testUpdateProductNameByProductId_ShouldUpdate() {
        when(deviceMapper.update(any(), any(LambdaUpdateWrapper.class))).thenReturn(1);

        deviceUpdateService.updateProductNameByProductId(1L, "newName");

        verify(deviceMapper).update(any(), any(LambdaUpdateWrapper.class));
    }

    @Test
    @DisplayName("updateDeviceStatus - ONLINE 首次上线应设置 activeTime 并更新状态")
    void testUpdateDeviceStatus_Online_ShouldSetActiveTime() {
        Device device = new Device();
        device.setSerialNumber("SN");
        device.setStatus(3); // ONLINE type in this project
        device.setActiveTime(null);
        when(deviceMapper.updateDeviceStatus(device)).thenReturn(1);

        int rows = deviceUpdateService.updateDeviceStatus(device);

        assertEquals(1, rows);
        assertNotNull(device.getActiveTime());
        assertNotNull(device.getUpdateTime());
        verify(deviceCache).deleteDeviceProtocolDetailCache("SN");
        verify(deviceMapper).updateDeviceStatus(device);
    }
}

