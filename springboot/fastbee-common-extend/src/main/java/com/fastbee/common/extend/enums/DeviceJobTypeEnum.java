package com.fastbee.common.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fastb
 * @version 1.0
 * @description: 通知渠道枚举
 * @date 2023-12-18 11:52
 */
@Getter
@AllArgsConstructor
public enum DeviceJobTypeEnum {

    /**
     * 设备定时
     */
    DEVICE_JOB(1, "周期计算"),
    /**
     * 设备告警
     */
    DEVICE_ALERT(2, "自定义时间段"),
    /**
     * 场景联动
     */
    SCENE(3, "场景联动定时触发"),
    /**
     * 场景管理-运算变量
     */
    SCENE_MODEL(4, "场景运算变量定时触发"),
    /**
     * ModbusJob
     */
    MODBUS_JOB(5, "ModbusJob"),
    /**
     * ModbusTcp
     */
    MODBUS_TCP(6, "ModbusTcp"),
    /**
     * 报表管理
     */
    REPORT(7, "报表定时任务触发"),
    /**
     * 规则引擎可视化定时触发
     */
    RULE_VIEW(8, "规则引擎可视化定时触发");

    private final Integer type;
    private final String desc;

    public static DeviceJobTypeEnum getByCode(Integer code) {
        for (DeviceJobTypeEnum cycleEnum : DeviceJobTypeEnum.values()) {
            if (cycleEnum.getType().equals(code)) {
                return cycleEnum;
            }
        }
        return null;
    }
}
