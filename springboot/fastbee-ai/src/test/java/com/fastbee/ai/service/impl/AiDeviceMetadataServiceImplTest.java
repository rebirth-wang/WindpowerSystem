package com.fastbee.ai.service.impl;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.iot.cache.IDeviceCache;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.model.DeviceMetaData;

/**
 * AI 设备编号直查权限校验测试。
 */
@ExtendWith(MockitoExtension.class)
class AiDeviceMetadataServiceImplTest {

    @Mock
    private IDeviceCache deviceCache;

    @InjectMocks
    private AiDeviceMetadataServiceImpl service;

    @Test
    void shouldAllowMetadataWhenOperatePermissionPassed() {
        String serialNumber = "SN001";
        DeviceMetaData metaData = buildMetaData(serialNumber, 100L, "owner");
        when(deviceCache.getDeviceMetaDataCache(serialNumber)).thenReturn(metaData);

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            DeviceMetaData result = service.getOptionalDeviceMetaData(serialNumber);

            Assertions.assertSame(metaData, result);
            mockedSecurityUtils.verify(() -> SecurityUtils.checkUserOperatePermission(100L, "owner"));
        }
    }

    @Test
    void shouldRejectMetadataWhenOperatePermissionDenied() {
        String serialNumber = "SN001";
        when(deviceCache.getDeviceMetaDataCache(serialNumber)).thenReturn(buildMetaData(serialNumber, 100L, "owner"));

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(() -> SecurityUtils.checkUserOperatePermission(100L, "owner"))
                    .thenThrow(new ServiceException("no permission"));

            DeviceMetaData result = service.getOptionalDeviceMetaData(serialNumber);

            Assertions.assertNull(result);
        }
    }

    private DeviceMetaData buildMetaData(String serialNumber, Long tenantId, String createBy) {
        Device device = new Device();
        device.setDeviceId(1L);
        device.setSerialNumber(serialNumber);
        device.setTenantId(tenantId);
        device.setCreateBy(createBy);
        DeviceMetaData metaData = new DeviceMetaData();
        metaData.setDevice(device);
        return metaData;
    }
}
