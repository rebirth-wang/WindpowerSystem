//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.exception.base;

import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;

public class BaseException extends RuntimeException {
    private static final long F = 1L;
    private String G;
    private String code;
    private Object[] H;
    private String I;

    public BaseException(String module, String code, Object[] args, String defaultMessage) {
        this.G = module;
        this.code = code;
        this.H = args;
        this.I = defaultMessage;
    }

    public BaseException(String module, String code, Object[] args) {
        this(module, code, args, (String)null);
    }

    public BaseException(String module, String defaultMessage) {
        this(module, (String)null, (Object[])null, defaultMessage);
    }

    public BaseException(String code, Object[] args) {
        this((String)null, code, args, (String)null);
    }

    public BaseException(String defaultMessage) {
        this((String)null, (String)null, (Object[])null, defaultMessage);
    }

    public String getMessage() {
        String var1 = null;
        if (!StringUtils.isEmpty(this.code)) {
            var1 = MessageUtils.message(this.code, this.H);
        }

        if (var1 == null) {
            var1 = this.I;
        }

        return var1;
    }

    public String getModule() {
        return this.G;
    }

    public String getCode() {
        return this.code;
    }

    public Object[] getArgs() {
        return this.H;
    }

    public String getDefaultMessage() {
        return this.I;
    }
}
