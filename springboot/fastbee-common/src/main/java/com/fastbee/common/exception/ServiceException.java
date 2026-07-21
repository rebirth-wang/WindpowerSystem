//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.exception;

public final class ServiceException extends RuntimeException {
    private static final long B = 1L;
    private Integer code;
    private String message;
    private String A;

    public ServiceException() {
    }

    public ServiceException(String message) {
        this.message = message;
    }

    public ServiceException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public ServiceException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getDetailMessage() {
        return this.A;
    }

    public String getMessage() {
        return this.message;
    }

    public Integer getCode() {
        return this.code;
    }

    public ServiceException setMessage(String message) {
        this.message = message;
        return this;
    }

    public ServiceException setDetailMessage(String detailMessage) {
        this.A = detailMessage;
        return this;
    }
}
