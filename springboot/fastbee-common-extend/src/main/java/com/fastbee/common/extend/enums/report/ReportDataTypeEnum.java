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
public enum ReportDataTypeEnum {
    /**
     * 原值
     */
    HISTORY_DATA(1, "历史数据"),
    /**
     * 累计值
     */
    AGGREGATE_DATA(2, "聚合数据");

    private final Integer type;
    private final String desc;

    public static ReportDataTypeEnum getByCode(Integer code) {
        for (ReportDataTypeEnum reportDataTypeEnum : ReportDataTypeEnum.values()) {
            if (reportDataTypeEnum.getType().equals(code)) {
                return reportDataTypeEnum;
            }
        }
        return null;
    }
}
