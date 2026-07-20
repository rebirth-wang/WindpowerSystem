package com.fastbee.media.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastbee.common.constant.FastBeeConstant;
import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.core.redis.RedisKeyBuilder;
import com.fastbee.media.constant.MediaConstant;
import com.fastbee.media.domain.MediaServer;
import com.fastbee.media.model.MediaInfo;
import com.fastbee.media.model.RecordList;
import com.fastbee.media.model.ZlmMediaServer;
import com.fastbee.media.service.IMediaCacheService;
import com.fastbee.media.util.MediaRedisKeyBuilder;

@Service
@Slf4j
public class MediaCacheServiceImpl implements IMediaCacheService {

    @Autowired
    private RedisCache redisCache;

    @Override
    public Long getCSEQ(String serverSipId) {
        String key = RedisKeyBuilder.buildSipCSEQCacheKey(serverSipId);
        long result = redisCache.incr(key, 1L);
        if (result > Integer.MAX_VALUE) {
            redisCache.setCacheObject(key, 1);
            result = 1;
        }
        return result;
    }

    @Override
    public void updateMediaInfo(ZlmMediaServer mediaServerConfig) {
        redisCache.setCacheObject(FastBeeConstant.REDIS.DEFAULT_MEDIA_CONFIG, mediaServerConfig);
    }

    @Override
    public void setRecordList(String key, RecordList recordList) {
        String catchkey = RedisKeyBuilder.buildSipRecordinfoCacheKey(key);
        redisCache.setCacheObject(catchkey, recordList);
    }

    @Override
    public void addStream(MediaServer mediaServerItem, String type, String app, String streamId, MediaInfo mediaInfo) {
        String catchkey = MediaRedisKeyBuilder.buildMediaInfoCacheKey(mediaServerItem.getServerId(), type, app, streamId);
        redisCache.setCacheObject(catchkey, JSON.toJSONString(mediaInfo));
    }

    @Override
    public void removeStream(String mediaServerId, String type, String app, String streamId) {
        redisCache.deleteObject(MediaRedisKeyBuilder.buildMediaInfoCacheKey(mediaServerId, type, app, streamId));
    }

    @Override
    public void removeStream(String mediaServerId, String type) {
        String key = MediaConstant.REDIS.SERVER_STREAMINFO + "_" + type.toUpperCase() + "_*_*_" + mediaServerId;
        List<Object> streams = redisCache.scan(key);
        for (Object stream : streams) {
            redisCache.deleteObject((String) stream);
        }
    }

    @Override
    public List<MediaInfo> getStreams(String mediaServerId, String pull) {
        List<MediaInfo> result = new ArrayList<>();
        String key = MediaConstant.REDIS.SERVER_STREAMINFO + "_" + pull.toUpperCase() + "_*_*_" + mediaServerId;
        List<Object> streams = redisCache.scan(key);
        for (Object stream : streams) {
            String mediaInfoJson = redisCache.getCacheObject((String) stream);
            MediaInfo mediaInfo = JSON.parseObject(mediaInfoJson, MediaInfo.class);
            result.add(mediaInfo);
        }
        return result;
    }

    @Override
    public MediaInfo getStreamInfo(String app, String streamId, String mediaServerId) {
        String scanKey = MediaConstant.REDIS.SERVER_STREAMINFO + "_*_" + app + "_" + streamId + "_" + mediaServerId;
        MediaInfo result = null;
        List<Object> keys = redisCache.scan(scanKey);
        if (!keys.isEmpty()) {
            String key = (String) keys.get(0);
            String mediaInfoJson = redisCache.getCacheObject((String) key);
            result = JSON.parseObject(mediaInfoJson, MediaInfo.class);
        }

        return result;
    }

    @Override
    public MediaInfo getProxyStream(String app, String streamId) {
        String scanKey = MediaConstant.REDIS.SERVER_STREAMINFO + "_PULL_" + app + "_" + streamId + "_*";
        MediaInfo result = null;
        List<Object> keys = redisCache.scan(scanKey);
        if (!keys.isEmpty()) {
            String key = (String) keys.get(0);
            String mediaInfoJson = redisCache.getCacheObject((String) key);
            result = JSON.parseObject(mediaInfoJson, MediaInfo.class);
        }
        return result;
    }

    @Override
    public void addPushListItem(String app, String stream, MediaInfo param) {
        String catchkey = MediaRedisKeyBuilder.buildPushMediaInfoCacheKey(app, stream);
        redisCache.setCacheObject(catchkey, JSON.toJSONString(param));
    }

    @Override
    public MediaInfo getPushListItem(String app, String stream) {
        String key = MediaRedisKeyBuilder.buildPushMediaInfoCacheKey(app, stream);
        return redisCache.getCacheObject(key);
    }

    @Override
    public void addCount(String mediaServerId) {
        if (mediaServerId == null) {
            return;
        }
        redisCache.redisTemplate.opsForZSet().incrementScore(MediaConstant.REDIS.MEDIA_SERVERS_ONLINE, mediaServerId, 1.0d);
    }

    @Override
    public void removeCount(String mediaServerId) {
        if (mediaServerId == null) {
            return;
        }
        redisCache.redisTemplate.opsForZSet().incrementScore(MediaConstant.REDIS.MEDIA_SERVERS_ONLINE, mediaServerId, -1.0d);
    }

    @Override
    public Set<Object> getMediaServerLoadCount() {
        Long size = redisCache.redisTemplate.opsForZSet().zCard(MediaConstant.REDIS.MEDIA_SERVERS_ONLINE);
        if (size == null || size.longValue() == 0) {
            log.info("获取负载最低的节点时无在线节点");
            return null;
        }
        Set<Object> objects = redisCache.redisTemplate.opsForZSet().range(MediaConstant.REDIS.MEDIA_SERVERS_ONLINE, 0L, -1L);
        return objects;
    }

}
