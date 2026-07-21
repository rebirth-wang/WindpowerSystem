//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.enums;

public enum ExceptionCode {
    SUCCESS(200, "成功"),
    TIMEOUT(400, "超时"),
    OFFLINE(404, "设备断线"),
    FAIL(500, "失败");

    public int code;
    public String desc;

    public int getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    private ExceptionCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
