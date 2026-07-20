package com.fastbee.ai.service.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fastbee.ai.service.IAiDeviceMetadataService;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.cache.IDeviceCache;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.model.DeviceMetaData;

@Service
public class AiDeviceMetadataServiceImpl implements IAiDeviceMetadataService {

    @Resource
    private IDeviceCache deviceCache;

    @Override
    public DeviceMetaData getRequiredDeviceMetaData(String serialNumber) {
        if (StringUtils.isBlank(serialNumber)) {
            throw new ServiceException(message("ai.device.serial.number.required"));
        }
        DeviceMetaData deviceMetaData = getOptionalDeviceMetaData(serialNumber);
        if (deviceMetaData == null) {
            throw new ServiceException(message("ai.device.not.found.or.tenant.no.permission"));
        }
        return deviceMetaData;
    }

    @Override
    public DeviceMetaData getOptionalDeviceMetaData(String serialNumber) {
        if (StringUtils.isBlank(serialNumber)) {
            return null;
        }
        DeviceMetaData deviceMetaData = deviceCache.getDeviceMetaDataCache(serialNumber.trim());
        if (deviceMetaData == null || deviceMetaData.getDevice() == null || deviceMetaData.getDevice().getDeviceId() == null) {
            return null;
        }
        try {
            validateOperatePermission(deviceMetaData.getDevice());
        } catch (ServiceException ignored) {
            return null;
        }
        return deviceMetaData;
    }

    private void validateOperatePermission(Device device) {
        SecurityUtils.checkUserOperatePermission(device.getTenantId(), device.getCreateBy());
    }
}
