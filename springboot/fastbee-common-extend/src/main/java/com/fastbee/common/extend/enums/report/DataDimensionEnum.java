package com.fastbee.common.extend.enums.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据维度枚举
 * @author fastb
 * @date 2024-06-05 10:42
 * @version 1.0
 */
@AllArgsConstructor
@Getter
public enum DataDimensionEnum {
    /**
     * 设备
     */
    DEVICE(1, "设备"),
    /**
     * 场景
     */
    SCENE(2, "场景");

    private final int type;
    private final String desc;

    public static DataDimensionEnum getByCode(int code) {
        for (DataDimensionEnum reportDataTypeEnum : DataDimensionEnum.values()) {
            if (reportDataTypeEnum.getType() == code) {
                return reportDataTypeEnum;
            }
        }
        return null;
    }
}
