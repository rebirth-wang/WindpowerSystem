package com.fastbee.common.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fastb
 * @version 1.0
 * @description: modbus云端轮询类型枚举
 * @date 2023-12-18 11:52
 */
@Getter
@AllArgsConstructor
public enum ModbusParamsPollTypeEnum {

    /**
     * 云端轮询
     */
    CLOUD_POLL(0, "云端轮询"),

    /**
     * 边缘采集
     */
    EDGE_COLLECTION(1, "边缘采集");

    private final Integer type;
    private final String desc;

    public static ModbusParamsPollTypeEnum getByCode(Integer code) {
        for (ModbusParamsPollTypeEnum cycleEnum : ModbusParamsPollTypeEnum.values()) {
            if (cycleEnum.getType().equals(code)) {
                return cycleEnum;
            }
        }
        return null;
    }
}
