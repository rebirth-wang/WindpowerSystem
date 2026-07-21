//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.enums;

public enum FunctionReplyStatus {
    SUCCESS(200, "设备执行成功"),
    FAIl(201, "指令执行失败"),
    UNKNOWN(204, "设备超时未回复"),
    NORELY(203, "指令下发成功");

    int code;
    String message;

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    private FunctionReplyStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
