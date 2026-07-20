package com.fastbee.common.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 维保时间单位
 * @author zzy
 * @date 2024/9/5 16:04
 */
@Getter
@AllArgsConstructor
public enum MaintenanceUnitEnum {

    DAY(1,"天"),
    WEEK(2,"周"),
    MONTH(3,"月"),
    YEAR(4,"年");

    Integer unit;
    String desc;

    public static Integer changeDays(Integer unit, Integer value) {
        switch (unit) {
            case 1:
                return value;
            case 2:
                return 7 * value;
            case 3:
                return 30 * value;
            case 4:
                return 365 * value;
        }
        return 0;
    }

    public static MaintenanceUnitEnum getEnum(Integer unit) {
        for (MaintenanceUnitEnum e : MaintenanceUnitEnum.values()) {
            if (e.unit.equals(unit)) {
                return e;
            }
        }
        return null;
    }
}
