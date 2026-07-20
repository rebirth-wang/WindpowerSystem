package com.fastbee.common.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 设备维保状态枚举
 * @author zzy
 * @date 2024/9/5 16:04
 */
@Getter
@AllArgsConstructor
public enum MaintenanceStatusEnum {

    NO_STARTED(0,"未开始"),
    IN_PROGRESS(1,"维保中"),
    STOPPED(2,"已停止");

    Integer status;
    String desc;
}
