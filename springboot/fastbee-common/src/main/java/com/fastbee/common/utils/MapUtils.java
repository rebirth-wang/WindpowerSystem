//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import com.fastbee.common.core.text.KeyValue;

public class MapUtils {
    public static <K, V> List<V> getList(Multimap<K, V> multimap, Collection<K> keys) {
        ArrayList var2 = new ArrayList();
        keys.forEach((var2x) -> {
            Collection var3 = multimap.get(var2x);
            if (!CollectionUtil.isEmpty(var3)) {
                var2.addAll(var3);
            }
        });
        return var2;
    }

    public static <K, V> void findAndThen(Map<K, V> map, K key, Consumer<V> consumer) {
        if (!CollUtil.isEmpty(map)) {
            Object var3 = map.get(key);
            if (var3 != null) {
                consumer.accept((V) var3);
            }
        }
    }

    public static <K, V> Map<K, V> convertMap(List<KeyValue<K, V>> keyValues) {
        LinkedHashMap var1 = Maps.newLinkedHashMapWithExpectedSize(keyValues.size());
        keyValues.forEach((var1x) -> var1.put(var1x.getKey(), var1x.getValue()));
        return var1;
    }
}
