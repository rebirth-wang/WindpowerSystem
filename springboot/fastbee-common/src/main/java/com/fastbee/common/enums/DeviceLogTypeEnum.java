//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.enums;

public enum DeviceLogTypeEnum {
    ATTRIBUTE_REPORT((byte)1, "属性上报"),
    INVOKE_FUNCTION((byte)2, "调用功能"),
    EVENT_REPORT((byte)3, "事件上报"),
    DEVICE_UPDATE((byte)4, "设备升级"),
    DEVICE_ONLINE((byte)5, "设备上线"),
    DEVICE_OFFLINE((byte)6, "设备离线"),
    SCENE_VARIABLE_REPORT((byte)7, "场景录入、运算变量上报下发");

    private final Byte type;
    private final String desc;

    public Byte getType() {
        return this.type;
    }

    public String getDesc() {
        return this.desc;
    }

    private DeviceLogTypeEnum(Byte type, String desc) {
        this.type = type;
        this.desc = desc;
    }
}
