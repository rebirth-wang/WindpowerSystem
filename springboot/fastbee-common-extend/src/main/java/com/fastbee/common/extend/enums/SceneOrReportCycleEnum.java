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
public enum SceneOrReportCycleEnum {

    /**
     * 周期计算
     */
    CYCLE(1, "周期计算"),
    /**
     * 自定义时间段
     */
    CUSTOM_TIME_PERIODS(2, "自定义时间段"),
    /**
     * 固定时间
     */
    FIXED_TIME(3, "固定时间");

    private final Integer type;
    private final String desc;

    public static SceneOrReportCycleEnum getByCode(Integer code) {
        for (SceneOrReportCycleEnum cycleEnum : SceneOrReportCycleEnum.values()) {
            if (cycleEnum.getType().equals(code)) {
                return cycleEnum;
            }
        }
        return null;
    }
}
