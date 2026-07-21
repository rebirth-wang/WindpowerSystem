//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.core.domain;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.util.Assert;

import com.fastbee.common.enums.GlobalErrorCodeConstants;
import com.fastbee.common.exception.ErrorCode;
import com.fastbee.common.exception.ServiceException;

public class CommonResult<T> implements Serializable {
    private Integer code;
    private T data;
    private String msg;

    public static <T> CommonResult<T> error(CommonResult<?> result) {
        return error(result.getCode(), result.getMsg());
    }

    public static <T> CommonResult<T> error(Integer code, String message) {
        Assert.isTrue(!GlobalErrorCodeConstants.SUCCESS.getCode().equals(code), "code 必须是错误的！");
        CommonResult var2 = new CommonResult();
        var2.code = code;
        var2.msg = message;
        return var2;
    }

    public static <T> CommonResult<T> error(ErrorCode errorCode) {
        return error(errorCode.getCode(), errorCode.getMsg());
    }

    public static <T> CommonResult<T> success(T data) {
        CommonResult var1 = new CommonResult();
        var1.code = GlobalErrorCodeConstants.SUCCESS.getCode();
        var1.data = data;
        var1.msg = "";
        return var1;
    }

    public static boolean isSuccess(Integer code) {
        return Objects.equals(code, GlobalErrorCodeConstants.SUCCESS.getCode());
    }

    @JsonIgnore
    public boolean isSuccess() {
        return isSuccess(this.code);
    }

    @JsonIgnore
    public boolean isError() {
        return !this.isSuccess();
    }

    public void checkError() throws ServiceException {
        if (!this.isSuccess()) {
            throw new ServiceException(this.code, this.msg);
        }
    }

    @JsonIgnore
    public T getCheckedData() {
        this.checkError();
        return this.data;
    }

    public static <T> CommonResult<T> error(ServiceException serviceException) {
        return error(serviceException.getCode(), serviceException.getMessage());
    }

    public Integer getCode() {
        return this.code;
    }

    public T getData() {
        return this.data;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof CommonResult)) {
            return false;
        } else {
            CommonResult var2 = (CommonResult)o;
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

                Object var5 = this.getData();
                Object var6 = var2.getData();
                if (var5 == null) {
                    if (var6 != null) {
                        return false;
                    }
                } else if (!var5.equals(var6)) {
                    return false;
                }

                String var7 = this.getMsg();
                String var8 = var2.getMsg();
                if (var7 == null) {
                    if (var8 != null) {
                        return false;
                    }
                } else if (!var7.equals(var8)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof CommonResult;
    }

    public int hashCode() {
        boolean var1 = true;
        int var2 = 1;
        Integer var3 = this.getCode();
        var2 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
        Object var4 = this.getData();
        var2 = var2 * 59 + (var4 == null ? 43 : var4.hashCode());
        String var5 = this.getMsg();
        var2 = var2 * 59 + (var5 == null ? 43 : var5.hashCode());
        return var2;
    }

    public String toString() {
        return "CommonResult(code=" + this.getCode() + ", data=" + this.getData() + ", msg=" + this.getMsg() + ")";
    }
}
