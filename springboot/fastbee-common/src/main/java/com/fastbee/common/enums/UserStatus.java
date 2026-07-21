//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.enums;

public enum UserStatus {
    OK(0, "正常"),
    DISABLE(1, "停用"),
    DELETED(2, "删除");

    private final Integer code;
    private final String info;

    private UserStatus(Integer code, String info) {
        this.code = code;
        this.info = info;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getInfo() {
        return this.info;
    }
}
