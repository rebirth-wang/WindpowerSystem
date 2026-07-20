//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.enums;

public enum VerifyTypeEnum {
    PASSWORD(1, "账号密码验证"),
    SMS(2, "短信验证");

    private Integer verifyType;
    private String desc;

    public Integer getVerifyType() {
        return this.verifyType;
    }

    public String getDesc() {
        return this.desc;
    }

    private VerifyTypeEnum() {
    }

    private VerifyTypeEnum(Integer verifyType, String desc) {
        this.verifyType = verifyType;
        this.desc = desc;
    }
}
