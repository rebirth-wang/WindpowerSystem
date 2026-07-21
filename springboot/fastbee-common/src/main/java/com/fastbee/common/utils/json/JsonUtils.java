//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.json;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JsonUtils {
    private static final Logger bZ = LoggerFactory.getLogger(JsonUtils.class);
    private static ObjectMapper ca = new ObjectMapper();

    public static void init(ObjectMapper objectMapper) {
        ca = objectMapper;
    }

    public static String toJsonString(Object object) throws JsonProcessingException {
        try {
            return ca.writeValueAsString(object);
        } catch (Throwable var2) {
            throw var2;
        }
    }

    public static byte[] toJsonByte(Object object) throws JsonProcessingException {
        try {
            return ca.writeValueAsBytes(object);
        } catch (Throwable var2) {
            throw var2;
        }
    }

    public static String toJsonPrettyString(Object object) throws JsonProcessingException {
        try {
            return ca.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (Throwable var2) {
            throw var2;
        }
    }

    public static <T> T parseObject(String text, Class<T> clazz) {
        if (StrUtil.isEmpty(text)) {
            return null;
        } else {
            try {
                return (T)ca.readValue(text, clazz);
            } catch (IOException var3) {
                bZ.error("json parse err,json:{}", text, var3);
                throw new RuntimeException(var3);
            }
        }
    }

    public static <T> T parseObject(String text, Type type) {
        if (StrUtil.isEmpty(text)) {
            return null;
        } else {
            try {
                return (T)ca.readValue(text, ca.getTypeFactory().constructType(type));
            } catch (IOException var3) {
                bZ.error("json parse err,json:{}", text, var3);
                throw new RuntimeException(var3);
            }
        }
    }

    public static <T> T parseObject2(String text, Class<T> clazz) {
        return (T)(StrUtil.isEmpty(text) ? null : JSONUtil.toBean(text, clazz));
    }

    public static <T> T parseObject(byte[] bytes, Class<T> clazz) {
        if (ArrayUtil.isEmpty(bytes)) {
            return null;
        } else {
            try {
                return (T)ca.readValue(bytes, clazz);
            } catch (IOException var3) {
                bZ.error("json parse err,json:{}", bytes, var3);
                throw new RuntimeException(var3);
            }
        }
    }

    public static <T> T parseObject(String text, TypeReference<T> typeReference) {
        try {
            return (T)ca.readValue(text, typeReference);
        } catch (IOException var3) {
            bZ.error("json parse err,json:{}", text, var3);
            throw new RuntimeException(var3);
        }
    }

    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        if (StrUtil.isEmpty(text)) {
            return new ArrayList();
        } else {
            try {
                return (List)ca.readValue(text, ca.getTypeFactory().constructCollectionType(List.class, clazz));
            } catch (IOException var3) {
                bZ.error("json parse err,json:{}", text, var3);
                throw new RuntimeException(var3);
            }
        }
    }

    public static JsonNode parseTree(String text) {
        try {
            return ca.readTree(text);
        } catch (IOException var2) {
            bZ.error("json parse err,json:{}", text, var2);
            throw new RuntimeException(var2);
        }
    }

    public static JsonNode parseTree(byte[] text) {
        try {
            return ca.readTree(text);
        } catch (IOException var2) {
            bZ.error("json parse err,json:{}", text, var2);
            throw new RuntimeException(var2);
        }
    }

    public static boolean isJson(String text) {
        return JSONUtil.isTypeJSON(text);
    }

    private JsonUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static {
        ca.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        ca.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ca.registerModules(new Module[]{new JavaTimeModule()});
    }
}
