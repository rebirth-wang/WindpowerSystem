//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.core.domain;

import java.io.Serializable;

public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int SUCCESS = 200;
    public static final int FAIL = 500;
    private int code;
    private String msg;
    private T data;

    public static <T> R<T> ok() {
        return (R<T>) restResult((Object)null, 200, "操作成功");
    }

    public static <T> R<T> ok(T data) {
        return restResult(data, 200, "操作成功");
    }

    public static <T> R<T> ok(T data, String msg) {
        return restResult(data, 200, msg);
    }

    public static <T> R<T> fail() {
        return (R<T>) restResult((Object)null, 500, "操作失败");
    }

    public static <T> R<T> fail(String msg) {
        return (R<T>) restResult((Object)null, 500, msg);
    }

    public static <T> R<T> fail(T data) {
        return restResult(data, 500, "操作失败");
    }

    public static <T> R<T> fail(T data, String msg) {
        return restResult(data, 500, msg);
    }

    public static <T> R<T> fail(int code, String msg) {
        return (R<T>) restResult((Object)null, code, msg);
    }

    private static <T> R<T> restResult(T data, int code, String msg) {
        R var3 = new R();
        var3.setCode(code);
        var3.setData(data);
        var3.setMsg(msg);
        return var3;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> Boolean isError(R<T> ret) {
        return !isSuccess(ret);
    }

    public static <T> Boolean isSuccess(R<T> ret) {
        return 200 == ret.getCode();
    }
}
