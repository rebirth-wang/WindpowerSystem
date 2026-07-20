//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.enums;

public enum DeviceDistributeTypeEnum {
    SELECT(1, "选择分配"),
    IMPORT(2, "导入分配"),
    USER(3, "用户分配");

    private Integer type;
    private String desc;

    public static String getDesc(Integer type) {
        for(DeviceDistributeTypeEnum var4 : values()) {
            if (var4.getType().equals(type)) {
                return var4.getDesc();
            }
        }

        return "";
    }

    public Integer getType() {
        return this.type;
    }

    public String getDesc() {
        return this.desc;
    }

    private DeviceDistributeTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
