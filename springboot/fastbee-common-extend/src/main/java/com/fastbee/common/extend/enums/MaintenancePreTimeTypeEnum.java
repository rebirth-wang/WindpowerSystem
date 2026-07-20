package com.fastbee.common.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 维保提前时间类型
 * @author zzy
 * @date 2024/9/5 16:04
 */
@Getter
@AllArgsConstructor
public enum MaintenancePreTimeTypeEnum {

    NOT_IN_ADVANCE(0,"不提前"),
    OND_DAY(1,"一天"),
    TWO_DAY(2,"两天"),
    THREE_DAY(3,"三天"),
    ONE_WEEK(4,"一周"),
    TWO_WEEK(5,"两周"),
    ONE_MONTH(6,"一个月");

    Integer type;
    String desc;

    public static MaintenancePreTimeTypeEnum getByType(Integer type) {
        for (MaintenancePreTimeTypeEnum maintenancePreTimeTypeEnum : values()) {
            if (maintenancePreTimeTypeEnum.getType().equals(type)) {
                return maintenancePreTimeTypeEnum;
            }
        }
        return null;
    }
}
