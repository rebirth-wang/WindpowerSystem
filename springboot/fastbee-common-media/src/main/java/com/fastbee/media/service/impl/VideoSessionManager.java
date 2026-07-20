package com.fastbee.media.service.impl;

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.core.redis.RedisKeyBuilder;
import com.fastbee.media.enums.SessionType;
import com.fastbee.media.model.VideoSessionInfo;
import com.fastbee.media.util.MediaRedisKeyBuilder;

@Slf4j
@Component
public class VideoSessionManager {
    @Autowired
    private RedisCache redisCache;

    public void put(VideoSessionInfo info) {
        String ssrc = info.getSsrc();
        if (info.getType() == SessionType.PLAY || info.getType() == SessionType.PLAYBACK) {
            ssrc = info.getType().name();
        }
        String key = MediaRedisKeyBuilder.buildVideoStreamCacheKey(info.getDeviceId(), info.getChannelId(), info.getStream(), ssrc);
        redisCache.setCacheObject(key, info);
    }

    public void putBySessionId(VideoSessionInfo info) {
        String ssrc = info.getSsrc();
        if (info.getType() == SessionType.PLAY || info.getType() == SessionType.PLAYBACK) {
            ssrc = info.getType().name();
        }
        String key = MediaRedisKeyBuilder.buildVideoStreamCacheKey(info.getDeviceId(), info.getChannelId(), info.getSessionId().toString(), ssrc);
        redisCache.setCacheObject(key, info);
    }

    public VideoSessionInfo getSessionInfo(String deviceId, String channelId, String stream, String callId) {
        if (ObjectUtils.isEmpty(deviceId)) {
            deviceId = "*";
        }
        if (ObjectUtils.isEmpty(channelId)) {
            channelId = "*";
        }
        if (ObjectUtils.isEmpty(stream)) {
            stream = "*";
        }
        if (ObjectUtils.isEmpty(callId)) {
            callId = "*";
        }
        String key = MediaRedisKeyBuilder.buildVideoStreamCacheKey(deviceId, channelId, stream, callId);
        List<Object> scanResult = redisCache.scan(key);
        if (scanResult.isEmpty()) {
            return null;
        }
        return (VideoSessionInfo) redisCache.getCacheObject((String) scanResult.get(0));
    }

    public VideoSessionInfo getSessionInfoByCallId(String callId) {
        if (ObjectUtils.isEmpty(callId)) {
            return null;
        }
        String key = MediaRedisKeyBuilder.buildVideoStreamCacheKey("*", "*", "*", callId);
        List<Object> scanResult = redisCache.scan(key);
        if (!scanResult.isEmpty()) {
            return (VideoSessionInfo) redisCache.getCacheObject((String) scanResult.get(0));
        } else {
            return null;
        }
    }

    public VideoSessionInfo getSessionInfoBySSRC(String SSRC) {
        if (ObjectUtils.isEmpty(SSRC)) {
            return null;
        }
        String key = MediaRedisKeyBuilder.buildVideoStreamCacheKey("*", "*", SSRC, "*");
        List<Object> scanResult = redisCache.scan(key);
        if (!scanResult.isEmpty()) {
            if (redisCache.containsKey((String)scanResult.get(0))) {
                return (VideoSessionInfo) redisCache.getCacheObject((String)scanResult.get(0));
            }
        }
        return null;
    }

    public List<VideoSessionInfo> getSessionInfoForAll(String deviceId, String channelId, String stream, String callId) {
        if (ObjectUtils.isEmpty(deviceId)) {
            deviceId = "*";
        }
        if (ObjectUtils.isEmpty(channelId)) {
            channelId = "*";
        }
        if (ObjectUtils.isEmpty(stream)) {
            stream = "*";
        }
        if (ObjectUtils.isEmpty(callId)) {
            callId = "*";
        }
        String key = MediaRedisKeyBuilder.buildVideoStreamCacheKey(deviceId, channelId, stream, callId);
        List<Object> scanResult = redisCache.scan(key);
        if (scanResult.isEmpty()) {
            return emptyList();
        }
        List<VideoSessionInfo> result = new ArrayList<>();
        for (Object keyObj : scanResult) {
            result.add((VideoSessionInfo) redisCache.getCacheObject((String) keyObj));
        }
        return result;
    }

    public String getMediaServerId(String deviceId, String channelId, String stream) {
        VideoSessionInfo ssrcTransaction = getSessionInfo(deviceId, channelId, null, stream);
        if (ssrcTransaction == null) {
            return null;
        }
        return ssrcTransaction.getMediaServerId();
    }

    public String getSSRC(String deviceId, String channelId, String stream) {
        VideoSessionInfo ssrcTransaction = getSessionInfo(deviceId, channelId, null, stream);
        if (ssrcTransaction == null) {
            return null;
        }
        return ssrcTransaction.getSsrc();
    }

    public void remove(String deviceId, String channelId, String stream, String callId) {
        String key = MediaRedisKeyBuilder.buildVideoStreamCacheKey(deviceId, channelId, stream, callId);
        redisCache.deleteObject(key);
    }

    public void remove(String deviceId, String channelId, String stream) {
        List<VideoSessionInfo> sinfoList = getSessionInfoForAll(deviceId, channelId, stream, null);
        if (sinfoList == null || sinfoList.isEmpty()) {
            return;
        }
        for (VideoSessionInfo sinfo : sinfoList) {
            String key = MediaRedisKeyBuilder.buildVideoStreamCacheKey(deviceId, channelId, stream, sinfo.getSsrc());
            if (sinfo.getType() != SessionType.PLAY) {
                redisCache.deleteObject(key);
            }
        }
    }

    public void removeByCallId(String deviceId, String channelId, String callId) {
        VideoSessionInfo sinfo = getSessionInfo(deviceId, channelId, null, callId);
        if (sinfo == null) {
            return;
        }
        String key = MediaRedisKeyBuilder.buildVideoStreamCacheKey(deviceId, channelId, sinfo.getStream(), sinfo.getSsrc());
        if (sinfo.getType() != SessionType.PLAY) {
            redisCache.deleteObject(key);
        }
    }

    public List<VideoSessionInfo> getAllSsrc() {
        String allkey = RedisKeyBuilder.buildStreamCacheKey("*", "*", "*", "*");
        List<Object> scanResult = redisCache.scan(allkey);
        if (scanResult.isEmpty()) {
            return null;
        }
        List<VideoSessionInfo> result = new ArrayList<>();
        for (Object ssrcTransactionKey : scanResult) {
            String key = (String) ssrcTransactionKey;
            result.add((VideoSessionInfo) redisCache.getCacheObject((String) key));
        }
        return result;
    }
}
