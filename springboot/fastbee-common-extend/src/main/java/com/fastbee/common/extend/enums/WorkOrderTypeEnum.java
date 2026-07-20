package com.fastbee.common.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 工单类型枚举
 * @author fastb
 * @date 2024-05-22 10:01
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum WorkOrderTypeEnum {

    ROUTINE_MAINTENANCE(1, "常规维护"),
    DEVICE_FAULT(2, "设备故障"),
    DEVICE_INSTALL(3, "设备安装"),
    DEVICE_ALERT(4, "设备告警"),
    USER_FEEDBACK(5, "用户反馈");

    private final Integer type;

    private final String description;

    public static WorkOrderTypeEnum getEnumByType(Integer type) {
        for (WorkOrderTypeEnum workOrderTypeEnum : WorkOrderTypeEnum.values()) {
            if (workOrderTypeEnum.getType().equals(type)) {
                return workOrderTypeEnum;
            }
        }
        return null;
    }

}
