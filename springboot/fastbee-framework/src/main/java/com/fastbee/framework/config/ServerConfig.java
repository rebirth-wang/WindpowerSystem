//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.framework.config;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.fastbee.common.utils.ServletUtils;

@Component
public class ServerConfig {
    public String getUrl() {
        HttpServletRequest request = ServletUtils.getRequest();
        return getDomain(request);
    }

    public static String getDomain(HttpServletRequest request) {
        String scheme = request.getHeader("X-Forwarded-Proto");
        if (scheme == null || scheme.isEmpty()) {
            scheme = request.getScheme();
        }

        StringBuffer url = request.getRequestURL();
        String contextPath = request.getServletContext().getContextPath();
        String baseUrl = url.delete(url.length() - request.getRequestURI().length(), url.length()).append(contextPath).toString();
        if ("https".equalsIgnoreCase(scheme)) {
            baseUrl = baseUrl + "/prod-api";
        }

        return scheme + "://" + baseUrl.replaceFirst("^http[s]?://", "");
    }
}
