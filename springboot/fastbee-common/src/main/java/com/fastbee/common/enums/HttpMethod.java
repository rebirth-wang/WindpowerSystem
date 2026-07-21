//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.enums;

import java.util.HashMap;
import java.util.Map;

import org.springframework.lang.Nullable;

public enum HttpMethod {
    GET,
    HEAD,
    POST,
    PUT,
    PATCH,
    DELETE,
    OPTIONS,
    TRACE;

    private static final Map<String, HttpMethod> mappings = new HashMap(16);

    @Nullable
    public static HttpMethod resolve(@Nullable String method) {
        return method != null ? (HttpMethod)mappings.get(method) : null;
    }

    public boolean matches(String method) {
        return this == resolve(method);
    }

    static {
        for(HttpMethod var3 : values()) {
            mappings.put(var3.name(), var3);
        }

    }
}
