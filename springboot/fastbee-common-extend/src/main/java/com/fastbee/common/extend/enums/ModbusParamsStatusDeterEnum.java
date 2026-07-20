package com.fastbee.common.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fastb
 * @version 1.0
 * @description: modbus子设备状态判断方式
 * @date 2023-12-18 11:52
 */
@Getter
@AllArgsConstructor
public enum ModbusParamsStatusDeterEnum {

    /**
     * 设备数据
     */
    DEVICE_DATA(1, "设备数据"),

    /**
     * 网关
     */
    GATEWAY(2, "网关");

    private final Integer status;
    private final String desc;

    public static ModbusParamsStatusDeterEnum getByCode(Integer code) {
        for (ModbusParamsStatusDeterEnum cycleEnum : ModbusParamsStatusDeterEnum.values()) {
            if (cycleEnum.getStatus().equals(code)) {
                return cycleEnum;
            }
        }
        return null;
    }
}
