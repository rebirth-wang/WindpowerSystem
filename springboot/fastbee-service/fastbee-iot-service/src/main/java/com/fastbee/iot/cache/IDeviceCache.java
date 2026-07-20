package com.fastbee.iot.cache;

import java.util.List;

import com.fastbee.common.enums.DeviceStatus;
import com.fastbee.common.extend.core.domin.mq.DeviceStatusBo;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.model.DeviceMetaData;

/**
 * 设备缓存
 * @author bill
 */
public interface IDeviceCache {

    /**
     * 更新设备状态
     * @param dto dto
     */
    public void updateDeviceStatusCache(DeviceStatusBo dto, Device device);

    /**
     * 获取设备在线总数
     * @return 设备在线总数
     */
    public long deviceOnlineTotal();


    /**
     * 批量更新redis缓存设备状态
     * @param serialNumbers 设备列表
     * @param status 状态
     */
    void updateBatchDeviceStatusCache(List<String> serialNumbers, DeviceStatus status);

    /**
     * 获取设备详情缓存
     * @param serialNumber
     * @return
     */
    DeviceMetaData getDeviceMetaDataCache(String serialNumber);

    /** 
     * @description: 删除设备详情缓存
     * @param: serialNumber
     * @return: void
     * @author zzy
     * @date: 2025-10-22 17:38
     */ 
    void deleteDeviceProtocolDetailCache(String serialNumber);



}
