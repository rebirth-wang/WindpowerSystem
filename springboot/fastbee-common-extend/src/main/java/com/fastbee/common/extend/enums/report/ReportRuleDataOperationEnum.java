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
public enum ReportRuleDataOperationEnum {
    /**
     * 原值
     */
    AVERAGE(1, "平均值"),
    /**
     * 累计值
     */
    MAX(2, "最大值"),
    MIN(3, "最小值"),
    CUMULATIVE(4, "累计值"),
    DIFFERENCE(5, "差值"),
    RANGE(6, "极差值");

    private final Integer operation;
    private final String desc;

//    public static final List<String> listWechatPlatform = Arrays.asList(WECHAT_OPEN_WEB.sourceClient, WECHAT_OPEN_MOBILE.sourceClient, WECHAT_OPEN_MINI_PROGRAM.sourceClient);

    public static ReportRuleDataOperationEnum getByCode(Integer code) {
        for (ReportRuleDataOperationEnum reportDataTypeEnum : ReportRuleDataOperationEnum.values()) {
            if (reportDataTypeEnum.getOperation().equals(code)) {
                return reportDataTypeEnum;
            }
        }
        return null;
    }
}
