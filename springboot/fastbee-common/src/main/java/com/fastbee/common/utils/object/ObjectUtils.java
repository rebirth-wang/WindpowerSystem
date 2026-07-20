//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.object;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.function.Consumer;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;

public class ObjectUtils {
    public static <T> T cloneIgnoreId(T object, Consumer<T> consumer) {
        Object var2 = ObjectUtil.clone(object);
        Field var3 = ReflectUtil.getField(object.getClass(), "id");
        if (var3 != null) {
            ReflectUtil.setFieldValue(var2, var3, (Object)null);
        }

        if (var2 != null) {
            consumer.accept((T) var2);
        }

        return (T)var2;
    }

    public static <T extends Comparable<T>> T max(T obj1, T obj2) {
        if (obj1 == null) {
            return obj2;
        } else if (obj2 == null) {
            return obj1;
        } else {
            return (T)(obj1.compareTo(obj2) > 0 ? obj1 : obj2);
        }
    }

    @SafeVarargs
    public static <T> T defaultIfNull(T... array) {
        for(Object var4 : array) {
            if (var4 != null) {
                return (T)var4;
            }
        }

        return null;
    }

    @SafeVarargs
    public static <T> boolean equalsAny(T obj, T... array) {
        return Arrays.asList(array).contains(obj);
    }
}
