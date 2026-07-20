package com.fastbee.iot.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.fastbee.iot.domain.Device;

/**
 * 设备Service接口
 *
 * @author kerwincui
 * @date 2021-12-16
 */
public interface IDeviceUpdateService extends IService<Device>
{

    /**
     * 修改设备
     *
     * @param device 设备
     * @return 结果
     */
    public int updateDevice(Device device);

    /**
     * 更新设备状态
     * @param device 设备
     * @return 结果
     */
    public int updateDeviceBySerialNumber(Device device);

    /**
     * 更新设备状态和定位
     * @param device 设备
     * @return 结果
     */
    public int updateDeviceStatusAndLocation(Device device, String ipAddress);

    /**
     * 更新设备状态
     * @param device 设备
     * @return 结果
     */
    public int updateDeviceStatus(Device device);

    void updateProductNameByProductId(Long productId, String productName);
}
