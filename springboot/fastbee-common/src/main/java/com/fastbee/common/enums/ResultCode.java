//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.enums;

public enum ResultCode implements IErrorCode {
    SUCCESS(200, "请求成功"),
    FAILED(500, "系统内部错误"),
    ACCEPTED(202, "请求已接收"),
    REDIRECT(303, "重定向"),
    UNAUTHORIZED(401, "暂未登录或token过期"),
    FORBIDDEN(403, "没有相关权限或授权过期"),
    NOT_FOUND(404, "资源未找到"),
    PARSE_MSG_EXCEPTION(4018, "解析协议异常"),
    TIMEOUT(502, "响应超时！"),
    FIRMWARE_VERSION_UNIQUE_ERROR(4022, "产品下已存在该版本固件"),
    FIRMWARE_SEQ_UNIQUE_ERROR(4023, "产品下已存在该升级序列号"),
    FIRMWARE_TASK_UNIQUE_ERROR(4024, "任务名已存在"),
    REPLY_TIMEOUT(4001, "超时未回执"),
    INVALID_USER_APP(4002, "用户信息不存在"),
    INVALID_MQTT_USER(1003, "内部mqtt服务用户异常"),
    DECODE_PROTOCOL_EXCEPTION(1000, "解析协议异常"),
    MQTT_TOPIC_INVALID(1001, "MQTT订阅topic格式非法");

    private int code;
    private String message;

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    private ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
