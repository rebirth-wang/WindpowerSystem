package com.fastbee.common.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 设备维保类型枚举
 * @author zzy
 * @date 2024/9/5 16:04
 */
@Getter
@AllArgsConstructor
public enum MaintenanceTypeEnum {

    WITHIN_WARRANTY(1,"保内维保"),
    RELEASE_ON_BAIL(2,"保外维保");

    Integer type;
    String desc;
}
