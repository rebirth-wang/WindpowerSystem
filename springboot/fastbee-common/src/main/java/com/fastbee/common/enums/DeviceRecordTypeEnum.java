//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.enums;

public enum DeviceRecordTypeEnum {
    IMPORT(1, "导入记录"),
    RECOVERY(2, "回收记录"),
    ASSIGNMENT(3, "分配记录"),
    ASSIGNMENT_DETAIL(4, "分配详细记录");

    private Integer type;
    private String desc;

    public Integer getType() {
        return this.type;
    }

    public String getDesc() {
        return this.desc;
    }

    private DeviceRecordTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
