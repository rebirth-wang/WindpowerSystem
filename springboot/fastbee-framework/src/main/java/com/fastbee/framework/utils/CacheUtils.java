//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.framework.utils;

import java.util.Set;

import org.redisson.api.RMap;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import com.fastbee.common.utils.spring.SpringUtils;

public class CacheUtils {
    private static final CacheManager CACHE_MANAGER = (CacheManager)SpringUtils.getBean(CacheManager.class);

    public static Set<Object> keys(String cacheNames) {
        RMap<Object, Object> rmap = (RMap)CACHE_MANAGER.getCache(cacheNames).getNativeCache();
        return rmap.keySet();
    }

    public static <T> T get(String cacheNames, Object key) {
        Cache.ValueWrapper wrapper = CACHE_MANAGER.getCache(cacheNames).get(key);
        return (T)(wrapper != null ? wrapper.get() : null);
    }

    public static void put(String cacheNames, Object key, Object value) {
        CACHE_MANAGER.getCache(cacheNames).put(key, value);
    }

    public static void evict(String cacheNames, Object key) {
        CACHE_MANAGER.getCache(cacheNames).evict(key);
    }

    public static void clear(String cacheNames) {
        CACHE_MANAGER.getCache(cacheNames).clear();
    }

    private CacheUtils() {
    }
}
