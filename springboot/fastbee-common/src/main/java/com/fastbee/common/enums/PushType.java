//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.enums;

public enum PushType {
    WECHAT_SERVER_PUSH("wechat_server_push", "微信小程序服务号推送");

    private String serviceCode;
    private String desc;

    public String getServiceCode() {
        return this.serviceCode;
    }

    public String getDesc() {
        return this.desc;
    }

    private PushType(String serviceCode, String desc) {
        this.serviceCode = serviceCode;
        this.desc = desc;
    }
}
