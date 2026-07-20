//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.exception;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastbee.common.enums.GlobalErrorCodeConstants;

public class ServiceExceptionUtil {
    private static final Logger C = LoggerFactory.getLogger(ServiceExceptionUtil.class);
    private static final ConcurrentMap<Integer, String> D = new ConcurrentHashMap();

    public static void putAll(Map<Integer, String> messages) {
        D.putAll(messages);
    }

    public static void put(Integer code, String message) {
        D.put(code, message);
    }

    public static void delete(Integer code, String message) {
        D.remove(code, message);
    }

    public static ServiceException exception(ErrorCode errorCode) {
        String var1 = (String)D.getOrDefault(errorCode.getCode(), errorCode.getMsg());
        return exception0(errorCode.getCode(), var1);
    }

    public static ServiceException exception(ErrorCode errorCode, Object... params) {
        String var2 = (String)D.getOrDefault(errorCode.getCode(), errorCode.getMsg());
        return exception0(errorCode.getCode(), var2, params);
    }

    public static ServiceException exception(Integer code) {
        return exception0(code, (String)D.get(code));
    }

    public static ServiceException exception(Integer code, Object... params) {
        return exception0(code, (String)D.get(code), params);
    }

    public static ServiceException exception0(Integer code, String messagePattern, Object... params) {
        String var3 = doFormat(code, messagePattern, params);
        return new ServiceException(code, var3);
    }

    public static ServiceException invalidParamException(String messagePattern, Object... params) {
        return exception0(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), messagePattern, params);
    }

    @VisibleForTesting
    public static String doFormat(int code, String messagePattern, Object... params) {
        StringBuilder var3 = new StringBuilder(messagePattern.length() + 50);
        int var4 = 0;

        for(int var6 = 0; var6 < params.length; ++var6) {
            int var5 = messagePattern.indexOf("{}", var4);
            if (var5 == -1) {
                C.error("[doFormat][参数过多：错误码({})|错误内容({})|参数({})", new Object[]{code, messagePattern, params});
                if (var4 == 0) {
                    return messagePattern;
                }

                var3.append(messagePattern.substring(var4));
                return var3.toString();
            }

            var3.append(messagePattern, var4, var5);
            var3.append(params[var6]);
            var4 = var5 + 2;
        }

        if (messagePattern.indexOf("{}", var4) != -1) {
            C.error("[doFormat][参数过少：错误码({})|错误内容({})|参数({})", new Object[]{code, messagePattern, params});
        }

        var3.append(messagePattern.substring(var4));
        return var3.toString();
    }
}
