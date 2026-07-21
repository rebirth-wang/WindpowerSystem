//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.exception;

public class ErrorCode {
    private final Integer x;
    private final String y;

    public ErrorCode(Integer code, String message) {
        this.x = code;
        this.y = message;
    }

    public Integer getCode() {
        return this.x;
    }

    public String getMsg() {
        return this.y;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ErrorCode)) {
            return false;
        } else {
            ErrorCode var2 = (ErrorCode)o;
            if (!var2.canEqual(this)) {
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

                String var5 = this.getMsg();
                String var6 = var2.getMsg();
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
        return other instanceof ErrorCode;
    }

    public int hashCode() {
        boolean var1 = true;
        int var2 = 1;
        Integer var3 = this.getCode();
        var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
        String var4 = this.getMsg();
        var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
        return var2;
    }

    public String toString() {
        return "ErrorCode(code=" + this.getCode() + ", msg=" + this.getMsg() + ")";
    }
}
