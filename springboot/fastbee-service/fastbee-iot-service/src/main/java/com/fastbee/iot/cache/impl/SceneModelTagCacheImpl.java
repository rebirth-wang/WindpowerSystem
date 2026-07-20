package com.fastbee.iot.cache.impl;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Service;

import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.core.redis.RedisKeyBuilder;
import com.fastbee.iot.cache.SceneModelTagCache;
import com.fastbee.iot.model.scenemodel.SceneModelTagCacheVO;

/**
 * 物模型缓存和物模型值缓存
 * @author gsb
 * @date 2024/3/22 16:37
 */
@Service
public class SceneModelTagCacheImpl implements SceneModelTagCache {

    @Resource
    private RedisCache redisCache;


    @Override
    public void addSceneModelTagValue(Long sceneModelId, SceneModelTagCacheVO sceneModelTagCacheVO) {
        String key = RedisKeyBuilder.buildSceneModelTagCacheKey(sceneModelId);
        redisCache.setHashValue(key, sceneModelTagCacheVO.getId(), JSON.toJSONString(sceneModelTagCacheVO));
    }

    @Override
    public SceneModelTagCacheVO getSceneModelTagValue(Long sceneModelId, Long sceneModelTagId) {
        String key = RedisKeyBuilder.buildSceneModelTagCacheKey(sceneModelId);
        return JSONObject.parseObject(redisCache.getCacheMapValue(key, sceneModelTagId.toString()), SceneModelTagCacheVO.class);
    }
}
