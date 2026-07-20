//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.framework.security.context;

import org.springframework.web.context.request.RequestContextHolder;

import com.fastbee.common.core.text.Convert;

public class PermissionContextHolder {
    private static final String PERMISSION_CONTEXT_ATTRIBUTES = "PERMISSION_CONTEXT";

    public static void setContext(String permission) {
        RequestContextHolder.currentRequestAttributes().setAttribute("PERMISSION_CONTEXT", permission, 0);
    }

    public static String getContext() {
        return Convert.toStr(RequestContextHolder.currentRequestAttributes().getAttribute("PERMISSION_CONTEXT", 0));
    }
}
