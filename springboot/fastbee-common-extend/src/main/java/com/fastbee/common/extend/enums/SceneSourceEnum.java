package com.fastbee.common.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 场景规则触发源枚举
 * 触发执行源: 1=设备触发/执行, 2=定时触发, 3=产品触发/执行, 4=告警执行, 5=告警恢复, 6=设备下线触发
 *
 * @author fastbee
 * @date 2024-01-01
 */
@Getter
@AllArgsConstructor
public enum SceneSourceEnum {

    /**
     * 设备触发/执行
     */
    DEVICE(1, "设备触发/执行"),

    /**
     * 定时触发
     */
    TIMER(2, "定时触发"),

    /**
     * 产品触发/执行
     */
    PRODUCT(3, "产品触发/执行"),

    /**
     * 告警执行
     */
    ALARM(4, "告警执行"),

    /**
     * 告警恢复
     */
    ALARM_RECOVER(5, "告警恢复"),

    /**
     * 设备下线触发
     */
    DEVICE_OFFLINE(6, "设备下线触发");

    private final Integer source;
    private final String description;

    public static SceneSourceEnum getBySource(Integer source) {
        if (source == null) return null;
        for (SceneSourceEnum e : values()) {
            if (e.source.equals(source)) return e;
        }
        return null;
    }
}
