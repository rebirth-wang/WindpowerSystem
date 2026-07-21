//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.enums;

import java.util.Objects;

public enum DataEnum {
    DECIMAL("decimal", "十进制"),
    DOUBLE("double", "双精度"),
    ENUM("enum", "枚举"),
    BOOLEAN("bool", "布尔类型"),
    INTEGER("integer", "整形"),
    OBJECT("object", "对象"),
    STRING("string", "字符串"),
    ARRAY("array", "数组");

    String type;
    String msg;

    public static DataEnum convert(String type) {
        for(DataEnum var4 : values()) {
            if (Objects.equals(var4.type, type)) {
                return var4;
            }
        }

        return STRING;
    }

    public String getType() {
        return this.type;
    }

    public String getMsg() {
        return this.msg;
    }

    private DataEnum(String type, String msg) {
        this.type = type;
        this.msg = msg;
    }
}
