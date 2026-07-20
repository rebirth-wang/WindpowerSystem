package com.fastbee.common.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 物联网卡运营商枚举
 * @author fastb
 * @date 2024-05-22 10:01
 * @version 1.0
 */
@Getter
@AllArgsConstructor
public enum CardOperatorEnum {

    CHINA_MOBILE("CMCC", "中国移动"),
    CHINA_TELECOM("CTCC", "中国电信"),
    CHINA_UNICOM("CUCC", "中国联通");

    private final String operator;

    private final String description;

    public static CardOperatorEnum getEnumByoperator(Integer operator) {
        for (CardOperatorEnum cardOperatorEnum : CardOperatorEnum.values()) {
            if (cardOperatorEnum.getOperator().equals(operator)) {
                return cardOperatorEnum;
            }
        }
        return null;
    }

    public static CardOperatorEnum getByIccid(String iccid) {
        if (iccid.startsWith("898601") || iccid.startsWith("898602") || iccid.startsWith("898603") || iccid.startsWith("898604")) {
            return CHINA_MOBILE;
        } else if (iccid.startsWith("898606") || iccid.startsWith("898607")) {
            return CHINA_TELECOM;
        } else if (iccid.startsWith("898609") || iccid.startsWith("898611")) {
            return CHINA_UNICOM;
        }
        return null;
    }

    public static String getOperatorByName(String operatorName) {
        for (CardOperatorEnum cardOperatorEnum : CardOperatorEnum.values()) {
            if (cardOperatorEnum.getDescription().equals(operatorName)) {
                return cardOperatorEnum.operator;
            }
        }
        return null;
    }
}
