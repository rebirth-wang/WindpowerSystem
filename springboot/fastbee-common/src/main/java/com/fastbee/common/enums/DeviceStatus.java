//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.enums;

public enum DeviceStatus {
    UNACTIVATED(1, "NOTACTIVE", "未激活"),
    FORBIDDEN(2, "DISABLE", "禁用"),
    ONLINE(3, "ONLINE", "在线"),
    OFFLINE(4, "OFFLINE", "离线");

    private int type;
    private String code;
    private String description;

    public static DeviceStatus convert(int type) {
        for(DeviceStatus var4 : values()) {
            if (var4.type == type) {
                return var4;
            }
        }

        return null;
    }

    public static DeviceStatus convert(String code) {
        for(DeviceStatus var4 : values()) {
            if (var4.code.equals(code)) {
                return var4;
            }
        }

        return null;
    }

    public int getType() {
        return this.type;
    }

    public String getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

    private DeviceStatus(int type, String code, String description) {
        this.type = type;
        this.code = code;
        this.description = description;
    }
}
