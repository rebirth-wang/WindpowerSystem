package com.fastbee.common.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Modbus轮询任务类型枚举
 * 1=单个变量轮询, 2=全量轮询
 *
 * @author fastbee
 * @date 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum ModbusJobTypeEnum {

    /**
     * 单个变量获取
     */
    SINGLE(1, "单个变量获取"),

    /**
     * 全量批量获取
     */
    BATCH(2, "全量批量获取");

    private final Integer type;
    private final String description;

    public static ModbusJobTypeEnum getByType(Integer type) {
        if (type == null) return null;
        for (ModbusJobTypeEnum e : values()) {
            if (e.type.equals(type)) return e;
        }
        return null;
    }
}
