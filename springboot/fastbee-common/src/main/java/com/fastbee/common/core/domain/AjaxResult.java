//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.core.domain;

import java.util.HashMap;

import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;

public class AjaxResult extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;
    public static final String CODE_TAG = "code";
    public static final String MSG_TAG = "msg";
    public static final String DATA_TAG = "data";

    public AjaxResult() {
    }

    public AjaxResult(int code, String msg) {
        super.put("code", code);
        super.put("msg", msg);
    }

    public AjaxResult(int code, String msg, Object data) {
        super.put("code", code);
        super.put("msg", msg);
        if (StringUtils.isNotNull(data)) {
            super.put("data", data);
        }

    }

    public AjaxResult(int code, String msg, Object data, int total) {
        super.put("code", code);
        super.put("msg", msg);
        if (StringUtils.isNotNull(data)) {
            super.put("data", data);
        }

        super.put("total", total);
    }

    public static AjaxResult success() {
        return success(MessageUtils.message("operate.success", new Object[0]));
    }

    public static AjaxResult success(Object data) {
        return success(MessageUtils.message("operate.success", new Object[0]), data);
    }

    public static AjaxResult success(Object data, int total) {
        return new AjaxResult(200, MessageUtils.message("operate.success", new Object[0]), data, total);
    }

    public static AjaxResult success(String msg) {
        return success(msg, (Object)null);
    }

    public static AjaxResult success(String msg, Object data) {
        return new AjaxResult(200, msg, data);
    }

    public static AjaxResult warn(String msg) {
        return warn(msg, (Object)null);
    }

    public static AjaxResult warn(String msg, Object data) {
        return new AjaxResult(601, msg, data);
    }

    public static AjaxResult error() {
        return error(MessageUtils.message("operate.fail", new Object[0]));
    }

    public static AjaxResult error(String msg) {
        return error(msg, (Object)null);
    }

    public static AjaxResult error(String msg, Object data) {
        return new AjaxResult(500, msg, data);
    }

    public static AjaxResult error(int code, String msg) {
        return new AjaxResult(code, msg, (Object)null);
    }

    public AjaxResult put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
