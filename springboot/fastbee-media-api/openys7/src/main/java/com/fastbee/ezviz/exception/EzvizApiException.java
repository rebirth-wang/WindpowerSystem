package com.fastbee.ezviz.exception;

/**
 * 萤石云 API 调用异常
 *
 * @author fastbee
 */
public class EzvizApiException extends RuntimeException {

    private String errorCode;

    public EzvizApiException(String message) {
        super(message);
    }

    public EzvizApiException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public EzvizApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getErrorCode() {
        return errorCode;
    }
}
