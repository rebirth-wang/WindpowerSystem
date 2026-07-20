package com.fastbee.ai.service.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fastbee.ai.service.IAiDeviceMetadataService;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;
import com.fastbee.iot.service.IOrderControlService;
import com.fastbee.iot.service.IThingsModelService;

@ExtendWith(MockitoExtension.class)
class AiDeviceControlGovernanceServiceImplTest {

    @Mock
    private IAiDeviceMetadataService aiDeviceMetadataService;

    @Mock
    private IThingsModelService thingsModelService;

    @Mock
    private IOrderControlService orderControlService;

    @InjectMocks
    private AiDeviceControlGovernanceServiceImpl service;

    @Test
    void shouldValidateControlPermissionByOrderControlPolicy() {
        Device device = buildDevice();
        DeviceMetaData deviceMetaData = new DeviceMetaData();
        deviceMetaData.setDevice(device);

        ThingsModelValueItem thingModel = new ThingsModelValueItem();
        thingModel.setId("switch");
        thingModel.setModelId(101L);

        when(aiDeviceMetadataService.getRequiredDeviceMetaData("SN-001")).thenReturn(deviceMetaData);
        when(thingsModelService.getSingleThingModels(300L, "switch")).thenReturn(thingModel);
        when(orderControlService.judgeThingsModel(200L, 101L)).thenReturn(new AjaxResult(200, "ok"));

        Assertions.assertDoesNotThrow(() -> service.validateControlPermission("SN-001", "switch"));
        verify(orderControlService).judgeThingsModel(200L, 101L);
    }

    @Test
    void shouldThrowWhenThingModelPermissionDenied() {
        Device device = buildDevice();
        DeviceMetaData deviceMetaData = new DeviceMetaData();
        deviceMetaData.setDevice(device);

        ThingsModelValueItem thingModel = new ThingsModelValueItem();
        thingModel.setId("switch");
        thingModel.setModelId(101L);

        when(aiDeviceMetadataService.getRequiredDeviceMetaData("SN-001")).thenReturn(deviceMetaData);
        when(thingsModelService.getSingleThingModels(300L, "switch")).thenReturn(thingModel);
        when(orderControlService.judgeThingsModel(200L, 101L)).thenReturn(AjaxResult.error("no permission"));

        ServiceException exception = Assertions.assertThrows(ServiceException.class,
                () -> service.validateControlPermission("SN-001", "switch"));
        Assertions.assertTrue(exception.getMessage().contains("no permission"));
    }

    private Device buildDevice() {
        Device device = new Device();
        device.setDeviceId(200L);
        device.setProductId(300L);
        device.setSerialNumber("SN-001");
        device.setTenantId(400L);
        device.setCreateBy("owner");
        return device;
    }
}
