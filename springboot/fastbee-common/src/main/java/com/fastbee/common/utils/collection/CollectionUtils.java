//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.utils.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;

public class CollectionUtils {
    public static String[] copy(String[] source) {
        if (isEmpty(source)) {
            return null;
        } else {
            int var1 = source.length;
            String[] var2 = new String[var1];

            for(int var3 = 0; var3 < var1; ++var3) {
                var2[var3] = source[var3];
            }

            return var2;
        }
    }

    public static String concat(String[] source, String split) {
        if (isEmpty(source)) {
            return null;
        } else {
            String var2 = "";

            for(int var3 = 0; var3 < source.length; ++var3) {
                var2 = var2.concat(source[var3]);
                if (var3 != source.length - 1) {
                    var2 = var2.concat(split);
                }
            }

            return var2;
        }
    }

    public static boolean isEmpty(String[] source) {
        if (null == source) {
            return true;
        } else {
            return 0 == source.length;
        }
    }

    public static boolean containsAny(Object source, Object... targets) {
        return Arrays.asList(targets).contains(source);
    }

    public static boolean isAnyEmpty(Collection<?>... collections) {
        return Arrays.stream(collections).anyMatch(CollUtil::isEmpty);
    }

    public static <T> List<T> filterList(Collection<T> from, Predicate<T> predicate) {
        return (List<T>)(CollUtil.isEmpty(from) ? new ArrayList() : (List)from.stream().filter(predicate).collect(Collectors.toList()));
    }

    public static <T, R> List<T> distinct(Collection<T> from, Function<T, R> keyMapper) {
        return (List<T>)(CollUtil.isEmpty(from) ? new ArrayList() : distinct(from, keyMapper, (var0, var1) -> var0));
    }

    public static <T, R> List<T> distinct(Collection<T> from, Function<T, R> keyMapper, BinaryOperator<T> cover) {
        return CollUtil.isEmpty(from) ? new ArrayList() : new ArrayList(convertMap(from, keyMapper, Function.identity(), cover).values());
    }

    public static <T, U> List<U> convertList(Collection<T> from, Function<T, U> func) {
        return (List<U>)(CollUtil.isEmpty(from) ? new ArrayList() : (List)from.stream().map(func).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    public static <T, U> List<U> convertList(Collection<T> from, Function<T, U> func, Predicate<T> filter) {
        return (List<U>)(CollUtil.isEmpty(from) ? new ArrayList() : (List)from.stream().filter(filter).map(func).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    public static <T, U> Set<U> convertSet(Collection<T> from, Function<T, U> func) {
        return (Set<U>)(CollUtil.isEmpty(from) ? new HashSet() : (Set)from.stream().map(func).filter(Objects::nonNull).collect(Collectors.toSet()));
    }

    public static <T, U> Set<U> convertSet(Collection<T> from, Function<T, U> func, Predicate<T> filter) {
        return (Set<U>)(CollUtil.isEmpty(from) ? new HashSet() : (Set)from.stream().filter(filter).map(func).filter(Objects::nonNull).collect(Collectors.toSet()));
    }

    public static <T, K> Map<K, T> convertMap(Collection<T> from, Function<T, K> keyFunc) {
        return (Map<K, T>)(CollUtil.isEmpty(from) ? new HashMap() : convertMap(from, keyFunc, Function.identity()));
    }

    public static <T, K> Map<K, T> convertMap(Collection<T> from, Function<T, K> keyFunc, Supplier<? extends Map<K, T>> supplier) {
        return CollUtil.isEmpty(from) ? (Map)supplier.get() : convertMap(from, keyFunc, Function.identity(), supplier);
    }

    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        return (Map<K, V>)(CollUtil.isEmpty(from) ? new HashMap() : convertMap(from, keyFunc, valueFunc, (BinaryOperator)((var0, var1) -> var0)));
    }

    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction) {
        return (Map<K, V>)(CollUtil.isEmpty(from) ? new HashMap() : convertMap(from, keyFunc, valueFunc, mergeFunction, HashMap::new));
    }

    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, Supplier<? extends Map<K, V>> supplier) {
        return CollUtil.isEmpty(from) ? (Map)supplier.get() : convertMap(from, keyFunc, valueFunc, (var0, var1) -> var0, supplier);
    }

    public static <T, K, V> Map<K, V> convertMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc, BinaryOperator<V> mergeFunction, Supplier<? extends Map<K, V>> supplier) {
        return (Map<K, V>)(CollUtil.isEmpty(from) ? new HashMap() : (Map)from.stream().collect(Collectors.toMap(keyFunc, valueFunc, mergeFunction, supplier)));
    }

    public static <T, K> Map<K, List<T>> convertMultiMap(Collection<T> from, Function<T, K> keyFunc) {
        return (Map<K, List<T>>)(CollUtil.isEmpty(from) ? new HashMap() : (Map)from.stream().collect(Collectors.groupingBy(keyFunc, Collectors.mapping((var0) -> var0, Collectors.toList()))));
    }

    public static <T, K, V> Map<K, List<V>> convertMultiMap(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        return (Map<K, List<V>>)(CollUtil.isEmpty(from) ? new HashMap() : (Map)from.stream().collect(Collectors.groupingBy(keyFunc, Collectors.mapping(valueFunc, Collectors.toList()))));
    }

    public static <T, K, V> Map<K, Set<V>> convertMultiMap2(Collection<T> from, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        return (Map<K, Set<V>>)(CollUtil.isEmpty(from) ? new HashMap() : (Map)from.stream().collect(Collectors.groupingBy(keyFunc, Collectors.mapping(valueFunc, Collectors.toSet()))));
    }

    public static boolean containsAny(Collection<?> source, Collection<?> candidates) {
        return org.springframework.util.CollectionUtils.containsAny(source, candidates);
    }

    public static <T> T getFirst(List<T> from) {
        return (T)(!CollectionUtil.isEmpty(from) ? from.get(0) : null);
    }





    public static <T, V extends Comparable<? super V>> V getSumValue(List<T> from, Function<T, V> valueFunc, BinaryOperator<V> accumulator) {
        if (CollUtil.isEmpty(from)) {
            return null;
        } else {
            assert from.size() > 0;

            return (V)(from.stream().map(valueFunc).reduce(accumulator).get());
        }
    }

    public static <T> void addIfNotNull(Collection<T> coll, T item) {
        if (item != null) {
            coll.add(item);
        }
    }

    public static <T> Collection<T> singleton(T deptId) {
        return (Collection<T>)(deptId == null ? Collections.emptyList() : Collections.singleton(deptId));
    }

    public static List startPage(List list, Integer pageNum, Integer pageSize) {
        if (list == null) {
            return null;
        } else if (list.size() == 0) {
            return null;
        } else {
            Integer var3 = list.size();
            Integer var4 = 0;
            if (var3 % pageSize == 0) {
                var4 = var3 / pageSize;
            } else {
                var4 = var3 / pageSize + 1;
            }

            int var5 = 0;
            int var6 = 0;
            if (!pageNum.equals(var4)) {
                var5 = (pageNum - 1) * pageSize;
                var6 = var5 + pageSize;
            } else {
                var5 = (pageNum - 1) * pageSize;
                var6 = var3;
            }

            List var7 = list.subList(var5, var6);
            return var7;
        }
    }
}
