//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.framework.utils;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.redisson.api.ObjectListener;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBatch;
import org.redisson.api.RBucket;
import org.redisson.api.RBucketAsync;
import org.redisson.api.RKeys;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.redisson.api.RMapAsync;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RSet;
import org.redisson.api.RTopic;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;

import com.fastbee.common.utils.spring.SpringUtils;

public class RedisUtils {
    private static final RedissonClient CLIENT = (RedissonClient)SpringUtils.getBean(RedissonClient.class);

    public static long rateLimiter(String key, RateType rateType, int rate, int rateInterval) {
        RRateLimiter rateLimiter = CLIENT.getRateLimiter(key);
        rateLimiter.trySetRate(rateType, (long)rate, (long)rateInterval, RateIntervalUnit.SECONDS);
        return rateLimiter.tryAcquire() ? rateLimiter.availablePermits() : -1L;
    }

    public static RedissonClient getClient() {
        return CLIENT;
    }

    public static <T> void publish(String channelKey, T msg, Consumer<T> consumer) {
        RTopic topic = CLIENT.getTopic(channelKey);
        topic.publish(msg);
        consumer.accept(msg);
    }

    public static <T> void publish(String channelKey, T msg) {
        RTopic topic = CLIENT.getTopic(channelKey);
        topic.publish(msg);
    }

    public static <T> void subscribe(String channelKey, Class<T> clazz, Consumer<T> consumer) {
        RTopic topic = CLIENT.getTopic(channelKey);
        topic.addListener(clazz, (channel, msg) -> consumer.accept(msg));
    }

    public static <T> void setCacheObject(String key, T value) {
        setCacheObject(key, value, false);
    }

    public static <T> void setCacheObject(String key, T value, boolean isSaveTtl) {
        RBucket<T> bucket = CLIENT.getBucket(key);
        if (isSaveTtl) {
            try {
                bucket.setAndKeepTTL(value);
            } catch (Exception var7) {
                long timeToLive = bucket.remainTimeToLive();
                if (timeToLive == -1L) {
                    setCacheObject(key, value);
                } else {
                    setCacheObject(key, value, Duration.ofMillis(timeToLive));
                }
            }
        } else {
            bucket.set(value);
        }

    }

    public static <T> void setCacheObject(String key, T value, Duration duration) {
        RBatch batch = CLIENT.createBatch();
        RBucketAsync<T> bucket = batch.getBucket(key);
        bucket.setAsync(value);
        bucket.expireAsync(duration);
        batch.execute();
    }

    public static <T> boolean setObjectIfAbsent(String key, T value, Duration duration) {
        RBucket<T> bucket = CLIENT.getBucket(key);
        return bucket.setIfAbsent(value, duration);
    }

    public static <T> boolean setObjectIfExists(String key, T value, Duration duration) {
        RBucket<T> bucket = CLIENT.getBucket(key);
        return bucket.setIfExists(value, duration);
    }

    public static <T> void addObjectListener(String key, ObjectListener listener) {
        RBucket<T> result = CLIENT.getBucket(key);
        result.addListener(listener);
    }

    public static boolean expire(String key, long timeout) {
        return expire(key, Duration.ofSeconds(timeout));
    }

    public static boolean expire(String key, Duration duration) {
        RBucket rBucket = CLIENT.getBucket(key);
        return rBucket.expire(duration);
    }

    public static <T> T getCacheObject(String key) {
        RBucket<T> rBucket = CLIENT.getBucket(key);
        return (T)rBucket.get();
    }

    public static <T> long getTimeToLive(String key) {
        RBucket<T> rBucket = CLIENT.getBucket(key);
        return rBucket.remainTimeToLive();
    }

    public static boolean deleteObject(String key) {
        return CLIENT.getBucket(key).delete();
    }

    public static void deleteObject(Collection collection) {
        RBatch batch = CLIENT.createBatch();
        collection.forEach((t) -> batch.getBucket(t.toString()).deleteAsync());
        batch.execute();
    }

    public static boolean isExistsObject(String key) {
        return CLIENT.getBucket(key).isExists();
    }

    public static <T> boolean setCacheList(String key, List<T> dataList) {
        RList<T> rList = CLIENT.getList(key);
        return rList.addAll(dataList);
    }

    public static <T> boolean addCacheList(String key, T data) {
        RList<T> rList = CLIENT.getList(key);
        return rList.add(data);
    }

    public static <T> void addListListener(String key, ObjectListener listener) {
        RList<T> rList = CLIENT.getList(key);
        rList.addListener(listener);
    }

    public static <T> List<T> getCacheList(String key) {
        RList<T> rList = CLIENT.getList(key);
        return rList.readAll();
    }

    public static <T> List<T> getCacheListRange(String key, int form, int to) {
        RList<T> rList = CLIENT.getList(key);
        return rList.range(form, to);
    }

    public static <T> boolean setCacheSet(String key, Set<T> dataSet) {
        RSet<T> rSet = CLIENT.getSet(key);
        return rSet.addAll(dataSet);
    }

    public static <T> boolean addCacheSet(String key, T data) {
        RSet<T> rSet = CLIENT.getSet(key);
        return rSet.add(data);
    }

    public static <T> void addSetListener(String key, ObjectListener listener) {
        RSet<T> rSet = CLIENT.getSet(key);
        rSet.addListener(listener);
    }

    public static <T> Set<T> getCacheSet(String key) {
        RSet<T> rSet = CLIENT.getSet(key);
        return rSet.readAll();
    }

    public static <T> void setCacheMap(String key, Map<String, T> dataMap) {
        if (dataMap != null) {
            RMap<String, T> rMap = CLIENT.getMap(key);
            rMap.putAll(dataMap);
        }

    }

    public static <T> void addMapListener(String key, ObjectListener listener) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        rMap.addListener(listener);
    }

    public static <T> Map<String, T> getCacheMap(String key) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        return rMap.getAll(rMap.keySet());
    }

    public static <T> Set<String> getCacheMapKeySet(String key) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        return rMap.keySet();
    }

    public static <T> void setCacheMapValue(String key, String hKey, T value) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        rMap.put(hKey, value);
    }

    public static <T> T getCacheMapValue(String key, String hKey) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        return (T)rMap.get(hKey);
    }

    public static <T> T delCacheMapValue(String key, String hKey) {
        RMap<String, T> rMap = CLIENT.getMap(key);
        return (T)rMap.remove(hKey);
    }

    public static <T> void delMultiCacheMapValue(String key, Set<String> hKeys) {
        RBatch batch = CLIENT.createBatch();
        RMapAsync<String, T> rMap = batch.getMap(key);

        for(String hKey : hKeys) {
            rMap.removeAsync(hKey);
        }

        batch.execute();
    }

    public static <K, V> Map<K, V> getMultiCacheMapValue(String key, Set<K> hKeys) {
        RMap<K, V> rMap = CLIENT.getMap(key);
        return rMap.getAll(hKeys);
    }

    public static void setAtomicValue(String key, long value) {
        RAtomicLong atomic = CLIENT.getAtomicLong(key);
        atomic.set(value);
    }

    public static long getAtomicValue(String key) {
        RAtomicLong atomic = CLIENT.getAtomicLong(key);
        return atomic.get();
    }

    public static long incrAtomicValue(String key) {
        RAtomicLong atomic = CLIENT.getAtomicLong(key);
        return atomic.incrementAndGet();
    }

    public static long decrAtomicValue(String key) {
        RAtomicLong atomic = CLIENT.getAtomicLong(key);
        return atomic.decrementAndGet();
    }

    public static Collection<String> keys(String pattern) {
        Stream<String> stream = CLIENT.getKeys().getKeysStreamByPattern(pattern);
        return (Collection)stream.collect(Collectors.toList());
    }

    public static void deleteKeys(String pattern) {
        CLIENT.getKeys().deleteByPattern(pattern);
    }

    public static Boolean hasKey(String key) {
        RKeys rKeys = CLIENT.getKeys();
        return rKeys.countExists(new String[]{key}) > 0L;
    }

    private RedisUtils() {
    }
}
