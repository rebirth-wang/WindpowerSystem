package com.fastbee.common.extend.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductExtParamTypeEnum {
    TEXT(1, "text", "文字"),
    NUMBER(2, "number", "数值"),
    SWITCH(3, "switch", "开关"),
    SELECT(4, "select", "选项"),
    DATE(5, "date", "日期"),
    TIME(6, "time", "时间");

    private final Integer code;
    private final String value;
    private final String desc;

    public static ProductExtParamTypeEnum transfer(Integer code) {
        for (ProductExtParamTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return TEXT;
    }

    public static ProductExtParamTypeEnum transferByValue(String value) {
        if (value == null) {
            return TEXT;
        }
        for (ProductExtParamTypeEnum type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return TEXT;
    }
}
