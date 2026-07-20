package com.fastbee.icc.code;

/**
 * 大华ICC自定义错误码
 *
 * @author fastbee
 */
public enum CustomErrorCode {

    SUCCESS("1000", "成功"),
    TOKEN_EXPIRED("1001", "Token已过期"),
    TOKEN_INVALID("1002", "Token无效"),
    AUTH_FAILED("2001", "鉴权失败"),
    DEVICE_NOT_FOUND("3001", "设备不存在"),
    CHANNEL_NOT_FOUND("3002", "通道不存在"),
    CHANNEL_OFFLINE("3003", "通道离线"),
    STREAM_START_FAILED("4001", "流媒体启动失败"),
    STREAM_NOT_FOUND("4002", "流媒体不存在"),
    PTZ_CONTROL_FAILED("5001", "云台控制失败"),
    RECORD_NOT_FOUND("6001", "录像不存在"),
    PLAYBACK_FAILED("6002", "录像回放失败"),
    NETWORK_ERROR("9001", "网络错误"),
    UNKNOWN_ERROR("9999", "未知错误"),
    ;

    private final String code;
    private final String msg;

    CustomErrorCode(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    /**
     * 根据错误码查找枚举
     *
     * @param code 错误码
     * @return 对应枚举，未找到返回UNKNOWN_ERROR
     */
    public static CustomErrorCode fromCode(String code) {
        if (code == null) {
            return UNKNOWN_ERROR;
        }
        for (CustomErrorCode e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        return UNKNOWN_ERROR;
    }
}
