//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.exception;

public final class ServerException extends RuntimeException {
    private Integer code;
    private String message;

    public ServerException() {
    }

    public ServerException(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMsg();
    }

    public ServerException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return this.code;
    }

    public ServerException setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return this.message;
    }

    public ServerException setMessage(String message) {
        this.message = message;
        return this;
    }

    public String toString() {
        return "ServerException(code=" + this.getCode() + ", message=" + this.getMessage() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ServerException)) {
            return false;
        } else {
            ServerException var2 = (ServerException)o;
            if (!var2.canEqual(this)) {
                return false;
            } else if (!super.equals(o)) {
                return false;
            } else {
                Integer var3 = this.getCode();
                Integer var4 = var2.getCode();
                if (var3 == null) {
                    if (var4 != null) {
                        return false;
                    }
                } else if (!var3.equals(var4)) {
                    return false;
                }

                String var5 = this.getMessage();
                String var6 = var2.getMessage();
                if (var5 == null) {
                    if (var6 != null) {
                        return false;
                    }
                } else if (!var5.equals(var6)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof ServerException;
    }

    public int hashCode() {
        boolean var1 = true;
        int var2 = super.hashCode();
        Integer var3 = this.getCode();
        var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
        String var4 = this.getMessage();
        var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
        return var2;
    }
}
