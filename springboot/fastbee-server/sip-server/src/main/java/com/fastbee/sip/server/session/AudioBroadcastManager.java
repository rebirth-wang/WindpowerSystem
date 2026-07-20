package com.fastbee.sip.server.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.sip.model.BroadcastItem;

/**
 * 语音广播会话管理器（Redis 版）
 * <p>
 * Redis Key 设计:
 *   SIP:BROADCAST:CHANNEL:{channelId} -> String(JSON BroadcastItem)  TTL=600s
 *   SIP:BROADCAST:DEVICE:{deviceId}   -> Set<channelId>              TTL=600s
 */
@Slf4j
@Component
public class AudioBroadcastManager {

    private static final String CHANNEL_KEY_PREFIX = "SIP:BROADCAST:CHANNEL:";
    private static final String DEVICE_KEY_PREFIX  = "SIP:BROADCAST:DEVICE:";
    private static final long   TTL_SECONDS        = 600;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 新增或更新广播会话（统一以 channelId 为主 key）
     */
    public void update(BroadcastItem item) {
        String channelKey = CHANNEL_KEY_PREFIX + item.getChannelId();
        String deviceKey  = DEVICE_KEY_PREFIX  + item.getDeviceId();
        redisCache.setCacheObject(channelKey, item, (int) TTL_SECONDS, TimeUnit.SECONDS);
        redisTemplate.opsForSet().add(deviceKey, item.getChannelId());
        redisTemplate.expire(deviceKey, TTL_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * 删除广播会话
     */
    public void del(String channelId) {
        String channelKey = CHANNEL_KEY_PREFIX + channelId;
        BroadcastItem item = get(channelId);
        if (item != null) {
            String deviceKey = DEVICE_KEY_PREFIX + item.getDeviceId();
            redisTemplate.opsForSet().remove(deviceKey, channelId);
        }
        redisCache.deleteObject(channelKey);
    }

    /**
     * 获取所有广播会话
     */
    public List<BroadcastItem> getAll() {
        Collection<String> keys = redisCache.keys(CHANNEL_KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return Collections.emptyList();
        }
        List<BroadcastItem> result = new ArrayList<>();
        for (String key : keys) {
            BroadcastItem item = redisCache.getCacheObject(key);
            if (item != null) {
                result.add(item);
            }
        }
        return result;
    }

    /**
     * 判断广播会话是否存在
     */
    public boolean exit(String channelId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(CHANNEL_KEY_PREFIX + channelId));
    }

    /**
     * 根据 channelId 获取广播会话
     */
    public BroadcastItem get(String channelId) {
        return redisCache.getCacheObject(CHANNEL_KEY_PREFIX + channelId);
    }

    /**
     * 根据 deviceId 获取该设备所有广播会话
     */
    public List<BroadcastItem> getByDeviceId(String deviceId) {
        String deviceKey = DEVICE_KEY_PREFIX + deviceId;
        Set<Object> channelIds = redisTemplate.opsForSet().members(deviceKey);
        if (channelIds == null || channelIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<BroadcastItem> result = new ArrayList<>();
        for (Object cid : channelIds) {
            BroadcastItem item = get(cid.toString());
            if (item != null) {
                result.add(item);
            }
        }
        return result;
    }
}
