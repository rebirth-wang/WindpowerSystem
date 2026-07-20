package com.fastbee.sip.service.impl;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.fastbee.common.constant.FastBeeConstant;
import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.core.redis.RedisKeyBuilder;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.media.model.RecordList;
import com.fastbee.media.model.ZlmMediaServer;
import com.fastbee.sip.service.ISipCacheService;

@Service
@Slf4j
public class SipCacheServiceImpl implements ISipCacheService {

    private static final String GB_DEVICE_STATUS_LATEST = "gb28181:device:status:latest:";
    private static final String GB_DEVICE_STATUS_HISTORY = "gb28181:device:status:history:";
    private static final String GB_STREAM_STATUS_LATEST = "gb28181:stream:status:latest:";
    private static final String GB_STREAM_STATUS_HISTORY = "gb28181:stream:status:history:";
    private static final long STATUS_RECORD_TTL_DAYS = 7L;
    private static final long STATUS_HISTORY_MAX_SIZE = 100L;

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
    public void recordDeviceStatus(String deviceId, String event, JSONObject status) {
        if (ObjectUtils.isEmpty(deviceId) || status == null) {
            return;
        }
        recordStatus(GB_DEVICE_STATUS_LATEST + deviceId, GB_DEVICE_STATUS_HISTORY + deviceId, event, status);
    }

    @Override
    public void recordStreamStatus(String streamId, String event, JSONObject status) {
        if (ObjectUtils.isEmpty(streamId) || status == null) {
            return;
        }
        recordStatus(GB_STREAM_STATUS_LATEST + streamId, GB_STREAM_STATUS_HISTORY + streamId, event, status);
    }

    private void recordStatus(String latestKey, String historyKey, String event, JSONObject status) {
        try {
            status.put("event", event);
            status.put("recordTime", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, new Date()));
            String statusJson = status.toJSONString();
            redisCache.redisTemplate.opsForValue().set(latestKey, statusJson, STATUS_RECORD_TTL_DAYS, TimeUnit.DAYS);
            redisCache.redisTemplate.opsForList().rightPush(historyKey, statusJson);
            redisCache.redisTemplate.opsForList().trim(historyKey, -STATUS_HISTORY_MAX_SIZE, -1);
            redisCache.redisTemplate.expire(historyKey, STATUS_RECORD_TTL_DAYS, TimeUnit.DAYS);
        } catch (Exception e) {
            log.warn("记录国标状态到Redis失败, latestKey:{}, event:{}", latestKey, event, e);
        }
    }
}
