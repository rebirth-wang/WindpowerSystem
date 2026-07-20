package com.fastbee.sip.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;

import jakarta.validation.constraints.NotNull;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.core.redis.RedisKeyBuilder;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.domain.MediaServer;
import com.fastbee.media.model.StreamInfo;
import com.fastbee.media.model.StreamProxy;
import com.fastbee.media.service.ICommonChannelService;
import com.fastbee.media.service.IMediaServerService;
import com.fastbee.media.util.ZlmApiUtils;
import com.fastbee.sip.service.IStreamProxyService;
import com.fastbee.sip.service.IZmlHookService;

/**
 * 视频代理业务
 */
@Slf4j
@Service
public class StreamProxyServiceImpl implements IStreamProxyService {

    @Autowired
    private IMediaServerService mediaServerService;

    @Autowired
    private ZlmApiUtils zlmApiUtils;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ICommonChannelService commonChannelService;

    @Autowired
    private IZmlHookService zmlHookService;

    @Override
    public StreamInfo start(String deviceId, String channelId, Boolean record) {
        log.info("[拉流代理]， 开始拉流，ID：{}", channelId);
        CommonChannel channel = commonChannelService.
                lambdaQuery()
                .eq(CommonChannel::getDeviceId, deviceId)
                .eq(CommonChannel::getChannelId, channelId)
                .one();
        String stream = "proxy_" + deviceId + "_" + channelId;
        // 从缓存获取代理信息
        String rediskey = RedisKeyBuilder.buildProxyCacheKey("default", stream);
        StreamProxy streamProxy = redisCache.getCacheObject(rediskey);
        if (streamProxy == null) {
            streamProxy = new StreamProxy();
            streamProxy.setSrcUrl(channel.getProxyUrl());
            //设置默认配置
            streamProxy.setType("default");
            streamProxy.setApp("proxy");
            streamProxy.setStream(stream);
            streamProxy.setTimeout(30);
            // 拉流方式，0：tcp，1：udp，2：组播
            streamProxy.setRtspType(0);
            streamProxy.setEnable(true);
            streamProxy.setEnableAudio(true);
            streamProxy.setEnableMp4(false);
            streamProxy.setEnableRemoveNoneReader(true);
            streamProxy.setEnableDisableNoneReader(false);
        } else {
            // 使用通道的地址发起流代理
            if (channel.getProxyUrl() != null) {
                streamProxy.setSrcUrl(channel.getProxyUrl());
            }
        }
        log.info("[拉流代理] 类型： {}， app：{}, stream: {}, 流地址： {}", streamProxy.getType(), streamProxy.getApp(), streamProxy.getStream(), streamProxy.getSrcUrl());
        if (record != null) {
            streamProxy.setEnableMp4(record);
        }

        return startProxy(streamProxy);
    }

    @Override
    public StreamInfo startProxy(@NotNull StreamProxy streamProxy) {
        MediaServer mediaServer;
        String mediaServerId = streamProxy.getRelatesMediaServerId();
        if (mediaServerId == null) {
            // 获取当前租户的媒体服务器
            SysUser user = getLoginUser().getUser();
            mediaServer = mediaServerService.selectMediaServerBytenantId(user.getDept().getDeptUserId());
        } else {
            mediaServer = mediaServerService.selectMediaServerByServerId(mediaServerId);
        }

        if (mediaServer != null) {
            JSONObject streamInfo = zlmApiUtils.getMediaInfo(mediaServer, streamProxy.getApp(), streamProxy.getStream());
            if (streamInfo.getInteger("code") == 0) {
                log.info("[拉流代理]，streamInfo: {}", streamInfo);
                log.info("[拉流代理]， 媒体流已存在，app：{}, stream: {}", streamProxy.getApp(), streamProxy.getStream());
                return zmlHookService.buildStreamProxyUrl(mediaServer, streamProxy.getStream());
            }
        } else {
            log.error("[拉流代理]， 媒体服务器不存在");
            return null;
        }

        String key = zlmApiUtils.startProxy(mediaServer, streamProxy);
        streamProxy.setStreamKey(key);
        streamProxy.setMediaServerId(mediaServer.getServerId());
        streamProxy.setPulling(true);
        // 保存代理信息
        String rediskey = RedisKeyBuilder.buildProxyCacheKey(streamProxy.getType(), streamProxy.getStream());
        redisCache.setCacheObject(rediskey, streamProxy);
        return zmlHookService.buildStreamProxyUrl(mediaServer, streamProxy.getStream());
    }

    @Override
    public void stop(String deviceId, String channelId) {
        // 从缓存获取代理信息
        String stream = "proxy_" + deviceId + "_" + channelId;
        String rediskey = RedisKeyBuilder.buildProxyCacheKey("default", stream);
        StreamProxy streamProxy = redisCache.getCacheObject(rediskey);
        if (streamProxy == null) {
            log.error("[拉流代理]， 代理信息不存在，ID");
            return;
        }
        stopProxy(streamProxy);
    }

    @Override
    public void stopProxy(StreamProxy streamProxy) {
        String mediaServerId = streamProxy.getMediaServerId();
        Assert.notNull(mediaServerId, "代理节点不存在");
        MediaServer mediaServer = mediaServerService.selectMediaServerByServerId(mediaServerId);
        if (mediaServer == null) {
            log.error("[拉流代理]， 媒体服务器不存在，mediaServerId：{}", mediaServerId);
            return;
        }
        if (ObjectUtils.isEmpty(streamProxy.getStreamKey())) {
            zlmApiUtils.closeStreams(mediaServer, streamProxy.getApp(), streamProxy.getStream());
        } else {
            zlmApiUtils.delStreamProxy(mediaServer, streamProxy.getStreamKey());
            streamProxy.setStreamKey("");
        }
        // 删除缓存
        String rediskey = RedisKeyBuilder.buildProxyCacheKey(streamProxy.getType(), streamProxy.getStream());
        streamProxy.setPulling(false);
        redisCache.setCacheObject(rediskey, streamProxy);
    }

    @Override
    public Boolean pulling(String deviceId, String channelId) {
        String stream = "proxy_" + deviceId + "_" + channelId;
        String rediskey = RedisKeyBuilder.buildProxyCacheKey("default", stream);
        StreamProxy streamProxy = redisCache.getCacheObject(rediskey);
        if (streamProxy == null) {
            log.error("[拉流代理]， 代理信息不存在，ID");
            return false;
        }
        MediaServer mediaServer;
        String mediaServerId = streamProxy.getRelatesMediaServerId();
        if (mediaServerId == null) {
            // 获取当前租户的媒体服务器
            SysUser user = getLoginUser().getUser();
            mediaServer = mediaServerService.selectMediaServerBytenantId(user.getDept().getDeptUserId());
        } else {
            mediaServer = mediaServerService.selectMediaServerByServerId(mediaServerId);
        }
        if (mediaServer != null) {
            JSONObject streamInfo = zlmApiUtils.getMediaInfo(mediaServer, streamProxy.getApp(), streamProxy.getStream());
            if (streamInfo.getInteger("code") == 0) {
                log.info("[拉流代理]，streamInfo: {}", streamInfo);
                log.info("[拉流代理]， 媒体流已存在，app：{}, stream: {}", streamProxy.getApp(), streamProxy.getStream());
                return true;
            }
        }
        return false;
    }
}
