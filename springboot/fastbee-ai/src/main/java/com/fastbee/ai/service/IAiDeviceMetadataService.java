package com.fastbee.ai.service;

import com.fastbee.iot.model.DeviceMetaData;

/**
 * AI 设备元数据服务。
 */
public interface IAiDeviceMetadataService {

    /**
     * 根据设备编号获取设备元数据，并完成租户校验。
     *
     * @param serialNumber 设备编号
     * @return 设备元数据
     */
    DeviceMetaData getRequiredDeviceMetaData(String serialNumber);

    DeviceMetaData getOptionalDeviceMetaData(String serialNumber);
}
