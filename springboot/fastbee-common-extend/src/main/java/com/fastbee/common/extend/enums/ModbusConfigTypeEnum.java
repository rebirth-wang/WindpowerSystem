package com.fastbee.common.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Modbus配置寄存器类型枚举
 * 1=IO类型(线圈/离散输入), 2=寄存器类型(保持寄存器/输入寄存器)
 *
 * @author fastbee
 * @date 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum ModbusConfigTypeEnum {

    /**
     * IO类型 - 线圈/离散输入 (Coil / Discrete Input)
     */
    IO(1, "IO类型"),

    /**
     * 寄存器类型 - 保持寄存器/输入寄存器 (Holding/Input Register)
     */
    REGISTER(2, "寄存器类型");

    private final Integer type;
    private final String description;

    public static ModbusConfigTypeEnum getByType(Integer type) {
        if (type == null) return null;
        for (ModbusConfigTypeEnum e : values()) {
            if (e.type.equals(type)) return e;
        }
        return null;
    }
}
