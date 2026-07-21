//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.enums;

public enum OTAUpgrade {
    AWAIT(0, "等待升级", "等待升级"),
    SEND(1, "发送中", "发送中"),
    REPLY(2, "升级中", "升级中"),
    SUCCESS(3, "成功", "升级成功"),
    FAILED(4, "失败", "升级失败"),
    FAILED_PULL(4, "失败", "拉取固件包失败"),
    FAILED_PACKAGE_SIZE(4, "失败", "获取固件包分包大小失败"),
    FAILED_PUSH(4, "失败", "推送固件包失败"),
    STOP(5, "停止", "设备重复升级停止推送"),
    STOP_OFFLINE(5, "停止", "设备离线停止推送"),
    UNKNOWN(404, "未知", "未知错误码");

    Integer status;
    String subMsg;
    String des;

    public static OTAUpgrade parse(Integer code) {
        for(OTAUpgrade var4 : values()) {
            if (var4.status.equals(code)) {
                return var4;
            }
        }

        return UNKNOWN;
    }

    private OTAUpgrade(Integer status, String subMsg, String des) {
        this.status = status;
        this.subMsg = subMsg;
        this.des = des;
    }

    public Integer getStatus() {
        return this.status;
    }

    public String getSubMsg() {
        return this.subMsg;
    }

    public String getDes() {
        return this.des;
    }
}
