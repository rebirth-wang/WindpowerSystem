package com.fastbee.icc.exception;

import lombok.Getter;

/**
 * 大华ICC API调用异常
 * <p>用于封装ICC平台API调用时的业务错误及网络错误</p>
 *
 * @author fastbee
 */
@Getter
public class IccApiException extends RuntimeException {

    /** ICC平台返回的业务错误码 */
    private final String errorCode;

    public IccApiException(String message) {
        super(message);
        this.errorCode = null;
    }

    public IccApiException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public IccApiException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
    }

    public IccApiException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "IccApiException{errorCode='" + errorCode + "', message='" + getMessage() + "'}";
    }
}
