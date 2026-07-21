//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.framework.manager;

import java.util.concurrent.Callable;

import org.springframework.cache.Cache;

import com.fastbee.common.utils.spring.SpringUtils;

public class CaffeineCacheDecorator implements Cache {
    private static final com.github.benmanes.caffeine.cache.Cache<Object, Object> CAFFEINE = (com.github.benmanes.caffeine.cache.Cache)SpringUtils.getBean("caffeine");
    private final Cache cache;

    public CaffeineCacheDecorator(Cache cache) {
        this.cache = cache;
    }

    public String getName() {
        return this.cache.getName();
    }

    public Object getNativeCache() {
        return this.cache.getNativeCache();
    }

    public String getUniqueKey(Object key) {
        return this.cache.getName() + ":" + key;
    }

    public Cache.ValueWrapper get(Object key) {
        Object o = CAFFEINE.get(this.getUniqueKey(key), (k) -> this.cache.get(key));
        return (Cache.ValueWrapper)o;
    }

    public <T> T get(Object key, Class<T> type) {
        Object o = CAFFEINE.get(this.getUniqueKey(key), (k) -> this.cache.get(key, type));
        return (T)o;
    }

    public void put(Object key, Object value) {
        CAFFEINE.invalidate(this.getUniqueKey(key));
        this.cache.put(key, value);
    }

    public Cache.ValueWrapper putIfAbsent(Object key, Object value) {
        CAFFEINE.invalidate(this.getUniqueKey(key));
        return this.cache.putIfAbsent(key, value);
    }

    public void evict(Object key) {
        this.evictIfPresent(key);
    }

    public boolean evictIfPresent(Object key) {
        boolean b = this.cache.evictIfPresent(key);
        if (b) {
            CAFFEINE.invalidate(this.getUniqueKey(key));
        }

        return b;
    }

    public void clear() {
        this.cache.clear();
    }

    public boolean invalidate() {
        return this.cache.invalidate();
    }

    public <T> T get(Object key, Callable<T> valueLoader) {
        Object o = CAFFEINE.get(this.getUniqueKey(key), (k) -> this.cache.get(key, valueLoader));
        return (T)o;
    }
}
