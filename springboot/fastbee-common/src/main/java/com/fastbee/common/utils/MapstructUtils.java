//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils;

import java.util.List;
import java.util.Map;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import io.github.linpeilie.Converter;

import com.fastbee.common.utils.spring.SpringUtils;

public class MapstructUtils {
    private static final Converter aG = (Converter)SpringUtils.getBean(Converter.class);

    public static <T, V> V convert(T source, Class<V> desc) {
        if (ObjectUtil.isNull(source)) {
            return null;
        } else {
            return (V)(ObjectUtil.isNull(desc) ? null : aG.convert(source, desc));
        }
    }

    public static <T, V> V convert(T source, V desc) {
        if (ObjectUtil.isNull(source)) {
            return null;
        } else {
            return (V)(ObjectUtil.isNull(desc) ? null : aG.convert(source, desc));
        }
    }

    public static <T, V> List<V> convert(List<T> sourceList, Class<V> desc) {
        if (ObjectUtil.isNull(sourceList)) {
            return null;
        } else {
            return (List<V>)(CollUtil.isEmpty(sourceList) ? CollUtil.newArrayList(new Object[0]) : aG.convert(sourceList, desc));
        }
    }

    public static <T> T convert(Map<String, Object> map, Class<T> beanClass) {
        if (MapUtil.isEmpty(map)) {
            return null;
        } else {
            return (T)(ObjectUtil.isNull(beanClass) ? null : aG.convert(map, beanClass));
        }
    }

    private MapstructUtils() {
    }
}
