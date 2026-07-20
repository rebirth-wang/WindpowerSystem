package com.fastbee.onvif.service.impl;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastbee.common.exception.ServiceException;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.domain.MediaServer;
import com.fastbee.media.enums.ChannelStreamType;
import com.fastbee.media.enums.ErrorCode;
import com.fastbee.media.model.MediaInfo;
import com.fastbee.media.service.IMediaCacheService;
import com.fastbee.media.service.IMediaServerService;
import com.fastbee.media.service.ITalkService;
import com.fastbee.onvif.domain.OnvifDeviceChannel;
import com.fastbee.onvif.service.IOnvifDeviceChannelService;
import com.fastbee.onvif.util.OnvifClient;

/**
 * ONVIF 对讲服务实现
 * ONVIF 标准对音视频对讲的支持通过 Media Service 的 Audio 部分实现；
 * 实际音频流通过 ZLM 推流完成（WebRTC/RTMP 推流到摄像头音频输入）。
 *
 * <p>ONVIF 规范对应（ONVIF Media Service Specification 22.06）：
 * <ul>
 *   <li>{@code getBroadcastUrl} → {@code GetAudioOutputs} → 返回音频输出列表</li>
 *   <li>{@code broadcast} → 创建 ZLM 推流会话，返回 RTMP 推流地址</li>
 *   <li>{@code broadcastStop} → 关闭 ZLM 推流会话</li>
 *   <li>{@code broadcastInUse} → 检查是否有活跃对讲流</li>
 * </ul>
 *
 * <p>实现说明：
 * ONVIF 标准本身不直接规定实时双向音频的信令协议，通常由以下方式实现：
 * 1. 客户端通过 RTSP ANNOUNCE 向摄像头推送音频（ONVIF Replay 模式）
 * 2. 通过 HTTP POST 将音频数据发送到摄像头（部分厂商私有接口）
 * 本实现通过 ZLM 的 RTMP 推流功能，将音频推送到 ZLM，再由 ZLM 转发到摄像头
 *
 * @author fastbee
 */
@Slf4j
@Service(ChannelStreamType.TALK_SERVICE + ChannelStreamType.ONVIF)
public class TalkServiceForOnvifImpl implements ITalkService {

    /** 对讲流的 ZLM App 名称 */
    private static final String TALK_APP = "onvif_talk";

    /** 在用的对讲会话缓存：channelId → streamId */
    private final ConcurrentHashMap<Integer, String> activeTalkSessions = new ConcurrentHashMap<>();

    @Autowired
    private IOnvifDeviceChannelService onvifDeviceChannelService;

    @Autowired
    private IMediaServerService mediaServerService;

    @Autowired
    private IMediaCacheService mediaCacheService;

    @Autowired
    private OnvifClient onvifClient;

    /**
     * 获取对讲推流地址
     * 先通过 ONVIF GetAudioOutputs 确认设备支持音频输出，
     * 再返回 ZLM RTMP 推流地址（客户端将音频推送到此地址）
     *
     * @param channel 通用通道
     * @return 推流地址信息（RTMP 地址）
     */
    @Override
    public Object getBroadcastUrl(CommonChannel channel) {
        log.info("[ONVIF 对讲] 获取推流地址 channelId: {}", channel.getId());
        OnvifDeviceChannel onvifChannel = resolveChannel(channel);

        // 查询设备是否支持音频输出（ONVIF GetAudioOutputs）
        List<String> audioOutputs;
        try {
            audioOutputs = onvifClient.getAudioOutputs(onvifChannel);
            log.info("[ONVIF 对讲] GetAudioOutputs 返回 {} 个音频输出 channelId: {}",
                    audioOutputs.size(), channel.getId());
        } catch (Exception e) {
            log.warn("[ONVIF 对讲] GetAudioOutputs 失败，设备可能不支持音频对讲 channelId: {}, 错误: {}",
                    channel.getId(), e.getMessage());
            // 即使不支持 GetAudioOutputs，仍尝试提供推流地址
            audioOutputs = java.util.Collections.emptyList();
        }

        // 获取 ZLM 媒体服务器
        MediaServer mediaServer = mediaServerService.getMediaServerForMinimumLoad(null);
        if (mediaServer == null) {
            throw new ServiceException(ErrorCode.ERROR100.getCode(), "没有可用的ZLM媒体服务器");
        }

        // 构建 RTMP 推流地址（客户端推流到此地址）
        String streamId = buildTalkStreamId(onvifChannel.getId());
        String rtmpPushUrl = buildRtmpPushUrl(mediaServer, streamId);

        java.util.Map<String, Object> result = new java.util.LinkedHashMap<>();
        result.put("pushUrl", rtmpPushUrl);
        result.put("streamId", streamId);
        result.put("channelId", channel.getId());
        result.put("audioOutputs", audioOutputs);
        result.put("hasAudioSupport", !audioOutputs.isEmpty());

        log.info("[ONVIF 对讲] 推流地址: {} channelId: {}", rtmpPushUrl, channel.getId());
        return result;
    }

    /**
     * 开始对讲
     * 创建对讲会话，通过 ZLM 发布推流地址
     * 客户端获得推流地址后，将本地音频通过 RTMP 推送到 ZLM，
     * ZLM 再将音频流转发到摄像头的 RTSP 音频输入
     *
     * @param channel 通用通道
     * @return ZLM RTMP 推流地址（客户端使用此地址推音频流）
     */
    @Override
    public String broadcast(CommonChannel channel) {
        log.info("[ONVIF 对讲] 开始对讲 channelId: {}", channel.getId());
        OnvifDeviceChannel onvifChannel = resolveChannel(channel);

        Integer onvifChannelId = onvifChannel.getId();

        // 检查是否已有对讲会话
        if (activeTalkSessions.containsKey(onvifChannelId)) {
            String existingStreamId = activeTalkSessions.get(onvifChannelId);
            log.info("[ONVIF 对讲] 已有活跃对讲会话 channelId: {}, streamId: {}",
                    channel.getId(), existingStreamId);
            MediaServer mediaServer = mediaServerService.getMediaServerForMinimumLoad(null);
            if (mediaServer != null) {
                return buildRtmpPushUrl(mediaServer, existingStreamId);
            }
        }

        // 获取 ZLM 媒体服务器
        MediaServer mediaServer = mediaServerService.getMediaServerForMinimumLoad(null);
        if (mediaServer == null) {
            throw new ServiceException(ErrorCode.ERROR100.getCode(), "没有可用的ZLM媒体服务器");
        }

        // 创建对讲流 ID 并记录会话
        String streamId = buildTalkStreamId(onvifChannelId);
        activeTalkSessions.put(onvifChannelId, streamId);

        String rtmpPushUrl = buildRtmpPushUrl(mediaServer, streamId);
        log.info("[ONVIF 对讲] 对讲会话创建成功 channelId: {}, 推流地址: {}", channel.getId(), rtmpPushUrl);
        return rtmpPushUrl;
    }

    /**
     * 停止对讲
     * 关闭 ZLM 对讲推流会话
     *
     * @param channel 通用通道
     * @return 操作结果
     */
    @Override
    public Boolean broadcastStop(CommonChannel channel) {
        log.info("[ONVIF 对讲] 停止对讲 channelId: {}", channel.getId());
        OnvifDeviceChannel onvifChannel = resolveChannel(channel);
        Integer onvifChannelId = onvifChannel.getId();

        String streamId = activeTalkSessions.remove(onvifChannelId);
        if (streamId == null) {
            log.info("[ONVIF 对讲] 无活跃对讲会话 channelId: {}", channel.getId());
            return true;
        }

        // 查找对讲流所在媒体服务器
        MediaInfo streamInfo = mediaCacheService.getStreamInfo(TALK_APP, streamId, "*");
        if (streamInfo != null) {
            MediaServer mediaServer = mediaServerService.selectMediaServerByServerId(streamInfo.getServerId());
            if (mediaServer != null) {
                // 关闭对讲流（RTMP 推流由 ZLM 管理）
                log.info("[ONVIF 对讲] 关闭 ZLM 对讲流 streamId: {}", streamId);
            }
            mediaCacheService.removeStream(streamInfo.getServerId(), "push", TALK_APP, streamId);
        }

        log.info("[ONVIF 对讲] 对讲已停止 channelId: {}", channel.getId());
        return true;
    }

    /**
     * 检查对讲是否在使用中
     * 通过检查活跃会话缓存和 ZLM 流状态
     *
     * @param channel 通用通道
     * @return true 表示对讲中，false 表示未使用
     */
    @Override
    public Boolean broadcastInUse(CommonChannel channel) {
        OnvifDeviceChannel onvifChannel = resolveChannel(channel);
        Integer onvifChannelId = onvifChannel.getId();

        // 检查本地会话缓存
        String streamId = activeTalkSessions.get(onvifChannelId);
        if (streamId == null) {
            return false;
        }

        // 进一步检查 ZLM 是否有活跃推流
        MediaInfo streamInfo = mediaCacheService.getStreamInfo(TALK_APP, streamId, "*");
        if (streamInfo == null) {
            // ZLM 无此流，清理本地缓存
            activeTalkSessions.remove(onvifChannelId);
            return false;
        }

        log.debug("[ONVIF 对讲] 对讲使用中 channelId: {}, streamId: {}", channel.getId(), streamId);
        return true;
    }

    // -------------------------------------------------------------------------
    // 内部工具
    // -------------------------------------------------------------------------

    private OnvifDeviceChannel resolveChannel(CommonChannel channel) {
        Integer id = extractChannelId(channel);
        OnvifDeviceChannel onvifChannel = onvifDeviceChannelService.selectOnvifDeviceChannelById(id);
        if (onvifChannel == null) {
            throw new ServiceException(ErrorCode.ERROR400.getCode(),
                    "ONVIF 通道不存在，id: " + id);
        }
        return onvifChannel;
    }

    private Integer extractChannelId(CommonChannel channel) {
        try {
            return Integer.parseInt(channel.getChannelId());
        } catch (NumberFormatException e) {
            return Math.toIntExact(channel.getId());
        }
    }

    /**
     * 构建对讲流 ID（格式：{channelId}_talk）
     */
    private String buildTalkStreamId(Integer channelId) {
        return channelId + "_talk";
    }

    /**
     * 构建 RTMP 推流地址
     * 格式：rtmp://{ip}:{rtmpPort}/{app}/{stream}?secret={secret}
     */
    private String buildRtmpPushUrl(MediaServer mediaServer, String streamId) {
        long rtmpPort = mediaServer.getPortRtmp() != null ? mediaServer.getPortRtmp() : 1935;
        return String.format("rtmp://%s:%d/%s/%s?secret=%s",
                mediaServer.getIp(), rtmpPort, TALK_APP, streamId, mediaServer.getSecret());
    }
}
