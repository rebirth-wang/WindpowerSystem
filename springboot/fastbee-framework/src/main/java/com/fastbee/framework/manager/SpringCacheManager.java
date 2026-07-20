//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.framework.manager;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonCache;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.transaction.TransactionAwareCacheDecorator;
import org.springframework.util.StringUtils;

import com.fastbee.framework.utils.RedisUtils;

public class SpringCacheManager implements CacheManager {
    private boolean dynamic = true;
    private boolean allowNullValues = true;
    private boolean transactionAware = true;
    private Long ttl;
    Map<String, CacheConfig> configMap = new ConcurrentHashMap();
    ConcurrentMap<String, Cache> instanceMap = new ConcurrentHashMap();

    public SpringCacheManager() {
    }

    public SpringCacheManager(Long ttl) {
        this.ttl = ttl;
    }

    public void setAllowNullValues(boolean allowNullValues) {
        this.allowNullValues = allowNullValues;
    }

    public void setTransactionAware(boolean transactionAware) {
        this.transactionAware = transactionAware;
    }

    public void setCacheNames(Collection<String> names) {
        if (names != null) {
            for(String name : names) {
                this.getCache(name);
            }

            this.dynamic = false;
        } else {
            this.dynamic = true;
        }

    }

    public void setConfig(Map<String,CacheConfig> config) {
        this.configMap = config;
    }

    protected CacheConfig createDefaultConfig() {
        return new CacheConfig();
    }

    public Cache getCache(String name) {
        String[] array = StringUtils.delimitedListToStringArray(name, "#");
        name = array[0];
        Cache cache = (Cache)this.instanceMap.get(name);
        if (cache != null) {
            return cache;
        } else if (!this.dynamic) {
            return cache;
        } else {
            CacheConfig config = (CacheConfig)this.configMap.get(name);
            if (config == null) {
                config = this.createDefaultConfig();
                this.configMap.put(name, config);
            }

            if (this.ttl > 0L) {
                config.setTTL(this.ttl * 1000L);
            }

            if (array.length > 1) {
                config.setTTL(DurationStyle.detectAndParse(array[1]).toMillis());
            }

            if (array.length > 2) {
                config.setMaxIdleTime(DurationStyle.detectAndParse(array[2]).toMillis());
            }

            if (array.length > 3) {
                config.setMaxSize(Integer.parseInt(array[3]));
            }

            return config.getMaxIdleTime() == 0L && config.getTTL() == 0L && config.getMaxSize() == 0 ? this.createMap(name, config) : this.createMapCache(name, config);
        }
    }

    private Cache createMap(String name, CacheConfig config) {
        RMap<Object, Object> map = RedisUtils.getClient().getMap(name);
        Cache cache = new CaffeineCacheDecorator(new RedissonCache(map, this.allowNullValues));
        if (this.transactionAware) {
            cache = new TransactionAwareCacheDecorator(cache);
        }

        Cache oldCache = (Cache)this.instanceMap.putIfAbsent(name, cache);
        if (oldCache != null) {
            cache = oldCache;
        }

        return cache;
    }

    private Cache createMapCache(String name, CacheConfig config) {
        RMapCache<Object, Object> map = RedisUtils.getClient().getMapCache(name);
        Cache cache = new CaffeineCacheDecorator(new RedissonCache(map, config, this.allowNullValues));
        if (this.transactionAware) {
            cache = new TransactionAwareCacheDecorator(cache);
        }

        Cache oldCache = (Cache)this.instanceMap.putIfAbsent(name, cache);
        if (oldCache != null) {
            cache = oldCache;
        } else {
            map.setMaxSize(config.getMaxSize());
        }

        return cache;
    }

    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(this.configMap.keySet());
    }
}
