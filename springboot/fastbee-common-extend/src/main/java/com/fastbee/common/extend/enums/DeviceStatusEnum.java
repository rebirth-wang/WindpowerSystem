package com.fastbee.common.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 设备状态枚举
 * @author fastbee
 * @date 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum DeviceStatusEnum {

    /**
     * 未激活
     */
    INACTIVE(1, "未激活"),
    
    /**
     * 禁用
     */
    FORBIDDEN(2, "禁用"),
    
    /**
     * 在线
     */
    ONLINE(3, "在线"),
    
    /**
     * 离线
     */
    OFFLINE(4, "离线");

    private final Integer status;
    private final String description;

    /**
     * 根据状态码获取枚举
     * @param status 状态码
     * @return 设备状态枚举
     */
    public static DeviceStatusEnum getByStatus(Integer status) {
        if (status == null) {
            return null;
        }
        for (DeviceStatusEnum deviceStatus : DeviceStatusEnum.values()) {
            if (deviceStatus.getStatus().equals(status)) {
                return deviceStatus;
            }
        }
        return null;
    }
}
