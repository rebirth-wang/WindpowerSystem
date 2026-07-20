package com.fastbee.icc.exception;

import lombok.Getter;

/**
 * 大华ICC业务异常
 *
 * @author fastbee
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String code;
    private final String msg;

    public BusinessException() {
        super("大华ICC业务异常");
        this.code = "UNKNOWN";
        this.msg = "大华ICC业务异常";
    }

    public BusinessException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public BusinessException(String msg) {
        super(msg);
        this.code = "UNKNOWN";
        this.msg = msg;
    }

    public BusinessException(String code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "BusinessException{code='" + code + "', msg='" + msg + "'}";
    }
}
