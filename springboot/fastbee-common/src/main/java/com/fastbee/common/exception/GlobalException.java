//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.exception;

public class GlobalException extends RuntimeException {
    private static final long z = 1L;
    private String message;
    private String A;

    public GlobalException() {
    }

    public GlobalException(String message) {
        this.message = message;
    }

    public String getDetailMessage() {
        return this.A;
    }

    public GlobalException setDetailMessage(String detailMessage) {
        this.A = detailMessage;
        return this;
    }

    public String getMessage() {
        return this.message;
    }

    public GlobalException setMessage(String message) {
        this.message = message;
        return this;
    }
}
