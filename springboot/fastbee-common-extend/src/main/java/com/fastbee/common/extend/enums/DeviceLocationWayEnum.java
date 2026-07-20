package com.fastbee.common.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 设备定位方式枚举
 * @author fastbee
 * @date 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum DeviceLocationWayEnum {

    /**
     * IP自动定位
     */
    IP_AUTO(1, "IP自动定位"),
    
    /**
     * 设备定位
     */
    DEVICE(2, "设备定位"),
    
    /**
     * 自定义
     */
    CUSTOM(3, "自定义");

    private final Integer way;
    private final String description;

    /**
     * 根据定位方式编码获取枚举
     * @param way 定位方式编码
     * @return 设备定位方式枚举
     */
    public static DeviceLocationWayEnum getByWay(Integer way) {
        if (way == null) {
            return null;
        }
        for (DeviceLocationWayEnum locationWay : DeviceLocationWayEnum.values()) {
            if (locationWay.getWay().equals(way)) {
                return locationWay;
            }
        }
        return null;
    }
}
