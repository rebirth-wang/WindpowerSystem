package com.fastbee.common.extend.enums.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 报表规则数据类型
 * @author fastb
 * @date 2024-06-05 10:42
 * @version 1.0
 */
@AllArgsConstructor
@Getter
public enum AggregateUnitsEnum {
    /**
     * 分钟
     */
    MINUTE(1, "分钟"),
    HOUR(2, "小时"),
    DAY(3, "天"),
//    WEEK(4, "周"),
//    MONTH(5, "月"),
    /**
     * 累计值
     */
    AGGREGATE_DATA(2, "聚合数据");

    private final Integer type;
    private final String desc;

    public static AggregateUnitsEnum getByCode(Integer code) {
        for (AggregateUnitsEnum reportDataTypeEnum : AggregateUnitsEnum.values()) {
            if (reportDataTypeEnum.getType().equals(code)) {
                return reportDataTypeEnum;
            }
        }
        return null;
    }
}
