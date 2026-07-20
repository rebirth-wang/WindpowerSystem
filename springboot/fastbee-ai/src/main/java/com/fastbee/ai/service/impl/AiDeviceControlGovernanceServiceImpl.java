package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fastbee.ai.service.IAiDeviceControlGovernanceService;
import com.fastbee.ai.service.IAiDeviceMetadataService;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;
import com.fastbee.iot.service.IOrderControlService;
import com.fastbee.iot.service.IThingsModelService;

/**
 * AI 设备控制治理服务实现。
 */
@Service
public class AiDeviceControlGovernanceServiceImpl implements IAiDeviceControlGovernanceService {

    @Resource
    private IAiDeviceMetadataService aiDeviceMetadataService;

    @Resource
    private IThingsModelService thingsModelService;

    @Resource
    private IOrderControlService orderControlService;

    @Override
    public void validateControlPermission(String serialNumber, String identifier) {
        if (StringUtils.isBlank(serialNumber)) {
            throw new ServiceException(message("ai.device.serial.number.required"));
        }
        if (StringUtils.isBlank(identifier)) {
            throw new ServiceException(message("ai.device.identifier.required"));
        }

        DeviceMetaData deviceMetaData = aiDeviceMetadataService.getRequiredDeviceMetaData(serialNumber);
        Device device = requireDevice(deviceMetaData);

        ThingsModelValueItem thingModel = thingsModelService.getSingleThingModels(device.getProductId(), identifier.trim());
        if (thingModel == null || thingModel.getModelId() == null) {
            throw new ServiceException(message("ai.device.control.thing.model.not.found", identifier.trim()));
        }

        AjaxResult result = orderControlService.judgeThingsModel(device.getDeviceId(), thingModel.getModelId());
        Object code = result == null ? null : result.get("code");
        if (code != null && "200".equals(String.valueOf(code))) {
            return;
        }
        Object resultMessage = result == null ? null : result.get("msg");
        String errorMessage = resultMessage == null ? null : String.valueOf(resultMessage);
        throw new ServiceException(StringUtils.isNotBlank(errorMessage) ? errorMessage : message("ai.device.control.permission.denied"));
    }

    private Device requireDevice(DeviceMetaData deviceMetaData) {
        if (deviceMetaData == null || deviceMetaData.getDevice() == null || deviceMetaData.getDevice().getDeviceId() == null) {
            throw new ServiceException(message("ai.device.not.found.or.no.permission"));
        }
        return deviceMetaData.getDevice();
    }
}
