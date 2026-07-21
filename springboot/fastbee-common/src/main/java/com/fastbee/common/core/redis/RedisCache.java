//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.common.core.redis;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.annotation.Resource;

import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.fastbee.common.utils.StringUtils;

@Component
public class RedisCache {
    private static final Logger j = LoggerFactory.getLogger(RedisCache.class);
    @Resource
    public RedisTemplate redisTemplate;
    @Resource
    private StringRedisTemplate k;

    public List<Object> values(String key) {
        return this.redisTemplate.opsForHash().values(key);
    }

    public <T> void setCacheObject(String key, T value) {
        this.redisTemplate.opsForValue().set(key, value);
    }

    public <T> void setCacheObject(String key, T value, Integer timeout, TimeUnit timeUnit) {
        this.redisTemplate.opsForValue().set(key, value, (long)timeout, timeUnit);
    }

    public boolean expire(String key, long timeout) {
        return this.expire(key, timeout, TimeUnit.SECONDS);
    }

    public boolean expire(String key, long timeout, TimeUnit unit) {
        return this.redisTemplate.expire(key, timeout, unit);
    }

    public long getExpire(String key) {
        return this.redisTemplate.getExpire(key);
    }

    public Boolean hasKey(String key) {
        return this.redisTemplate.hasKey(key);
    }

    public <T> T getCacheObject(String key) {
        ValueOperations var2 = this.redisTemplate.opsForValue();
        return (T)var2.get(key);
    }

    public boolean deleteObject(String key) {
        return this.redisTemplate.delete(key);
    }

    public boolean deleteObject(Collection collection) {
        return this.redisTemplate.delete(collection) > 0L;
    }

    public <T> long setCacheList(String key, List<T> dataList) {
        Long var3 = this.redisTemplate.opsForList().rightPushAll(key, dataList);
        return var3 == null ? 0L : var3;
    }

    public <T> List<T> getCacheList(String key) {
        return this.redisTemplate.opsForList().range(key, 0L, -1L);
    }

    public <T> BoundSetOperations<String, T> setCacheSet(String key, Set<T> dataSet) {
        BoundSetOperations var3 = this.redisTemplate.boundSetOps(key);
        Iterator var4 = dataSet.iterator();

        while(var4.hasNext()) {
            var3.add(new Object[]{var4.next()});
        }

        return var3;
    }

    public <T> Set<T> getCacheSet(String key) {
        return this.redisTemplate.opsForSet().members(key);
    }

    public <T> void setCacheMap(String key, Map<String, T> dataMap) {
        if (dataMap != null) {
            this.redisTemplate.opsForHash().putAll(key, dataMap);
        }

    }

    public <T> Map<String, T> getCacheMap(String key) {
        return this.redisTemplate.opsForHash().entries(key);
    }

    public <T> void setCacheMapValue(String key, String hKey, T value) {
        this.redisTemplate.opsForHash().put(key, hKey, value);
    }

    public <T> T getCacheMapValue(String key, String hKey) {
        HashOperations var3 = this.redisTemplate.opsForHash();
        return (T)var3.get(key, hKey);
    }

    public Long getCacheMapSize(String key) {
        return this.redisTemplate.opsForHash().size(key);
    }

    public <T> List<T> getMultiCacheMapValue(String key, Collection<Object> hKeys) {
        return this.redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    public boolean deleteCacheMapValue(String key, String hKey) {
        return this.redisTemplate.opsForHash().delete(key, new Object[]{hKey}) > 0L;
    }

    public Collection<String> keys(String pattern) {
        return this.redisTemplate.keys(pattern);
    }

    public boolean containsKey(String key) {
        return this.redisTemplate.hasKey(key);
    }

    public long incr(String key, long delta) {
        if (delta < 0L) {
            throw new RuntimeException("递增因子必须大于0");
        } else {
            return this.redisTemplate.opsForValue().increment(key, delta);
        }
    }

    public Long incr2(String key, long liveTime) {
        RedisAtomicLong var4 = new RedisAtomicLong(key, this.redisTemplate.getConnectionFactory());
        Long var5 = var4.getAndIncrement();
        if (var5 == 0L && liveTime > 0L) {
            var4.expire(liveTime, TimeUnit.HOURS);
        }

        return var5;
    }

    public long sAdd(String key, Object... values) {
        try {
            return this.redisTemplate.opsForSet().add(key, values);
        } catch (Exception var4) {
            var4.printStackTrace();
            return 0L;
        }
    }

    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long var5 = this.redisTemplate.opsForSet().add(key, values);
            if (time > 0L) {
                this.expire(key, time);
            }

            return var5;
        } catch (Exception var6) {
            var6.printStackTrace();
            return 0L;
        }
    }

    public long setRemove(String key, Object... values) {
        try {
            Long var3 = this.redisTemplate.opsForSet().remove(key, values);
            return var3;
        } catch (Exception var4) {
            var4.printStackTrace();
            return 0L;
        }
    }

    public boolean zSetAdd(String key, String value, double score) {
        try {
            Boolean var5 = this.k.opsForZSet().add(key, value, score);
            return BooleanUtils.isTrue(var5);
        } catch (Exception var6) {
            var6.printStackTrace();
            return false;
        }
    }

    public boolean zRem(String key, Object... values) {
        try {
            Long var3 = this.k.opsForZSet().remove(key, values);
            return var3 != null;
        } catch (Exception var4) {
            var4.printStackTrace();
            return false;
        }
    }

    public Long zRemBySocre(String key, double start, double end) {
        try {
            return this.k.opsForZSet().removeRangeByScore(key, start, end);
        } catch (Exception var7) {
            var7.printStackTrace();
            return null;
        }
    }

    public Long zRank(String key, String value) {
        try {
            return this.k.opsForZSet().rank(key, value);
        } catch (Exception var4) {
            var4.printStackTrace();
            return null;
        }
    }

    public Set<String> zRange(String key, int start, int end) {
        try {
            return this.k.opsForZSet().range(key, (long)start, (long)end);
        } catch (Exception var5) {
            var5.printStackTrace();
            return null;
        }
    }

    public Set<String> zRangeByScore(String key, double start, double end) {
        try {
            return this.k.opsForZSet().rangeByScore(key, start, end);
        } catch (Exception var7) {
            var7.printStackTrace();
            return null;
        }
    }

    public Set<String> zRange(String key, long start, long end) {
        try {
            return this.k.opsForZSet().range(key, start, end);
        } catch (Exception var7) {
            var7.printStackTrace();
            return null;
        }
    }

    public Long zSize(String key) {
        try {
            return this.k.opsForZSet().zCard(key);
        } catch (Exception var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public Set<String> getListKeyByPrefix(String prefix) {
        Set var2 = this.redisTemplate.keys(prefix.concat("*"));
        return var2;
    }

    public Cursor<Map.Entry<Object, Object>> hashScan(String key, ScanOptions options) {
        return this.redisTemplate.opsForHash().scan(key, options);
    }

    public Map hashEntity(String key) {
        return this.redisTemplate.boundHashOps(key).entries();
    }

    public void hashPutAll(String key, Map<String, String> maps) {
        this.redisTemplate.opsForHash().putAll(key, maps);
    }

    public void hashPutAllObj(String key, Map<String, Object> maps) {
        this.redisTemplate.opsForHash().putAll(key, maps);
    }






    public List<Object> scan(String query) {
        Set<String> keys = (Set)this.redisTemplate.execute((RedisCallback) (connection) -> {
            Set<String> keysTmp = new HashSet();
            ScanOptions scanOptions = ScanOptions.scanOptions().match("*" + query + "*").count(1000L).build();
            Cursor<byte[]> cursor = null;

            try {
                cursor = connection.scan(scanOptions);

                while(cursor.hasNext()) {
                    keysTmp.add(new String((byte[])cursor.next(), StandardCharsets.UTF_8));
                }
            } catch (Exception e) {
                throw new RuntimeException("Redis scan operation failed", e);
            } finally {
                if (cursor != null) {
                    try {
                        cursor.close();
                    } catch (Exception var12) {
                    }
                }

            }

            return keysTmp;
        });
        return new ArrayList(keys);
    }

    private boolean a(String var1, String var2, String var3) {
        boolean var4 = false;
        if ("".equals(var2)) {
            return var4;
        } else {
            switch (var1) {
                case "=":
                    var4 = var2.equals(var3);
                    break;
                case "!=":
                    var4 = !var2.equals(var3);
                    break;
                case ">":
                    if (this.a(var2) && this.a(var3)) {
                        var4 = Double.parseDouble(var2) > Double.parseDouble(var3);
                    }
                    break;
                case "<":
                    if (this.a(var2) && this.a(var3)) {
                        var4 = Double.parseDouble(var2) < Double.parseDouble(var3);
                    }
                    break;
                case ">=":
                    if (this.a(var2) && this.a(var3)) {
                        var4 = Double.parseDouble(var2) >= Double.parseDouble(var3);
                    }
                    break;
                case "<=":
                    if (this.a(var2) && this.a(var3)) {
                        var4 = Double.parseDouble(var2) <= Double.parseDouble(var3);
                    }
                    break;
                case "contain":
                    var4 = var2.contains(var3);
                    break;
                case "notcontain":
                    var4 = !var2.contains(var3);
            }

            return var4;
        }
    }

    private boolean a(String var1) {
        Pattern var2 = Pattern.compile("[0-9]*\\.?[0-9]+");
        Matcher var3 = var2.matcher(var1);
        return var3.matches();
    }

    public void publish(Object message, String channel) {
        try {
            this.redisTemplate.convertAndSend(channel, message);
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public <T> void setHashValue(String key, String hKey, T value) {
        this.redisTemplate.opsForHash().put(key, hKey, value);
    }

    public String getStrCacheObject(String key) {
        return (String)this.k.opsForValue().get(key);
    }

    public void deleteStrObject(String key) {
        this.k.delete(key);
    }

    public void deleteStrHash(String key) {
        this.k.opsForHash().getOperations().delete(key);
    }

    public void delHashValue(String key, String hkey) {
        HashOperations var3 = this.redisTemplate.opsForHash();
        var3.delete(key, new Object[]{hkey});
    }

    public <T> Object getStringHashValue(String key, String hKey) {
        return this.k.opsForHash().get(key, hKey);
    }

    public <T> void delStringHashValue(String key, String hKey) {
        this.k.opsForHash().delete(key, new Object[]{hKey});
    }

    @Transactional(
            isolation = Isolation.SERIALIZABLE,
            rollbackFor = {Exception.class}
    )
    public synchronized String getCacheModbusTcpId(String serialNumber) {
        String var2 = RedisKeyBuilder.buildModbusTcpCacheKey(serialNumber);
        String var3 = (String)this.k.opsForValue().get(var2);
        long var4 = 0L;
        if (StringUtils.isBlank(var3)) {
            this.k.opsForValue().set(var2, String.valueOf(0));
        } else {
            int var6 = Integer.parseInt(var3);
            if (var6 < 65535) {
                Long var7 = this.k.opsForValue().increment(var2);
                if (null != var7) {
                    var4 = var7;
                }
            } else {
                this.k.opsForValue().set(var2, String.valueOf(0));
            }
        }

        return String.valueOf(var4);
    }

    public synchronized void cacheModbusTcpData(String serialNumber, String id, String data) {
        String var4 = RedisKeyBuilder.buildModbusTcpRuntimeCacheKey(serialNumber);
        this.k.opsForHash().put(var4, id, data);
        this.k.expire(var4, 30L, TimeUnit.SECONDS);
    }
}
