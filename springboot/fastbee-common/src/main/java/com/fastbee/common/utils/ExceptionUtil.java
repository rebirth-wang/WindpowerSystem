//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class ExceptionUtil {
    public static String getExceptionMessage(Throwable e) {
        StringWriter var1 = new StringWriter();
        e.printStackTrace(new PrintWriter(var1, true));
        return var1.toString();
    }

    public static String getRootErrorMessage(Exception e) {
        Throwable var1 = ExceptionUtils.getRootCause(e);
        var1 = (Throwable)(var1 == null ? e : var1);
        if (var1 == null) {
            return "";
        } else {
            String var2 = var1.getMessage();
            return var2 == null ? "null" : StringUtils.defaultString(var2);
        }
    }
}
