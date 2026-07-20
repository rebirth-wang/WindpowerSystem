//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fastbee.common.core.text.Convert;

public class ServletUtils {
    public static String getParameter(String name) {
        return getRequest().getParameter(name);
    }

    public static String getParameter(String name, String defaultValue) {
        return Convert.toStr(getRequest().getParameter(name), defaultValue);
    }

    public static Integer getParameterToInt(String name) {
        return Convert.toInt(getRequest().getParameter(name));
    }

    public static Integer getParameterToInt(String name, Integer defaultValue) {
        return Convert.toInt(getRequest().getParameter(name), defaultValue);
    }

    public static Boolean getParameterToBool(String name) {
        return Convert.toBool(getRequest().getParameter(name));
    }

    public static Boolean getParameterToBool(String name, Boolean defaultValue) {
        return Convert.toBool(getRequest().getParameter(name), defaultValue);
    }

    public static Map<String, String[]> getParams(ServletRequest request) {
        Map var1 = request.getParameterMap();
        return Collections.unmodifiableMap(var1);
    }

    public static Map<String, String> getParamMap(ServletRequest request) {
        HashMap var1 = new HashMap();

        for(Map.Entry var3 : getParams(request).entrySet()) {
            var1.put(var3.getKey(), StringUtils.join(var3.getValue(), ","));
        }

        return var1;
    }

    public static HttpServletRequest getRequest() {
        return getRequestAttributes().getRequest();
    }

    public static HttpServletResponse getResponse() {
        return getRequestAttributes().getResponse();
    }

    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    public static ServletRequestAttributes getRequestAttributes() {
        RequestAttributes var0 = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes)var0;
    }

    public static void renderString(HttpServletResponse response, String string) {
        try {
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
        } catch (IOException var3) {
            var3.printStackTrace();
        }

    }

    public static boolean isAjaxRequest(HttpServletRequest request) {
        String var1 = request.getHeader("accept");
        if (var1 != null && var1.contains("application/json")) {
            return true;
        } else {
            String var2 = request.getHeader("X-Requested-With");
            if (var2 != null && var2.contains("XMLHttpRequest")) {
                return true;
            } else {
                String var3 = request.getRequestURI();
                if (StringUtils.inStringIgnoreCase(var3, new String[]{".json", ".xml"})) {
                    return true;
                } else {
                    String var4 = request.getParameter("__ajax");
                    return StringUtils.inStringIgnoreCase(var4, new String[]{"json", "xml"});
                }
            }
        }
    }

    public static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException var2) {
            return "";
        }
    }

    public static String urlDecode(String str) {
        try {
            return URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException var2) {
            return "";
        }
    }


}
