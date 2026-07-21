//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.enums;

public enum StatusEnum {
    SUCCESS(1, "成功"),
    FAIL(0, "失败");

    private final Integer status;
    private final String name;

    public Integer getStatus() {
        return this.status;
    }

    public String getName() {
        return this.name;
    }

    private StatusEnum(Integer status, String name) {
        this.status = status;
        this.name = name;
    }
}
