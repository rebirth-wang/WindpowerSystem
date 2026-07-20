package com.fastbee.onvif.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastbee.common.exception.ServiceException;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.domain.MediaServer;
import com.fastbee.media.enums.ChannelStreamType;
import com.fastbee.media.enums.ErrorCode;
import com.fastbee.media.model.MediaInfo;
import com.fastbee.media.model.Platform;
import com.fastbee.media.model.StreamInfo;
import com.fastbee.media.service.IMediaCacheService;
import com.fastbee.media.service.IMediaServerService;
import com.fastbee.media.service.IMediaService;
import com.fastbee.media.service.IPlayService;
import com.fastbee.media.util.ZlmApiUtils;
import com.fastbee.media.util.ZlmRtpUtils;
import com.fastbee.onvif.domain.OnvifDeviceChannel;
import com.fastbee.onvif.service.IOnvifDeviceChannelService;
import com.fastbee.onvif.util.OnvifClient;

/**
 * ONVIF 视频流播放服务实现
 * 通过 OnvifClient 调用 ONVIF Media Service - GetStreamUri 获取 RTSP 地址，
 * 然后通过 ZLM 媒体服务器拉流代理，实现视频流播放
 *
 * <p>ONVIF 标准对应：
 * <ul>
 *   <li>{@code play} → GetStreamUri(RTP-Unicast/RTSP) → ZLM addStreamProxy</li>
 *   <li>{@code stopPlay} → ZLM closeStreams</li>
 *   <li>{@code closeStream} → 条件性关闭 ZLM 流</li>
 * </ul>
 *
 * @author fastbee
 */
@Slf4j
@Service(ChannelStreamType.PLAY_SERVICE + ChannelStreamType.ONVIF)
public class PlayServiceForOnvifImpl implements IPlayService {

    private static final String APP = "onvif";

    @Autowired
    private IOnvifDeviceChannelService onvifDeviceChannelService;

    @Autowired
    private IMediaCacheService mediaCacheService;

    @Autowired
    private IMediaServerService mediaServerService;

    @Autowired
    private IMediaService mediaService;

    @Autowired
    private ZlmApiUtils zlmApiUtils;

    @Autowired
    private ZlmRtpUtils zlmRtpUtils;

    @Autowired
    private OnvifClient onvifClient;

    /**
     * 开始播放 ONVIF 通道视频流
     * 流程：
     * 1. 检查是否已有活跃流 → 有则直接返回流信息
     * 2. 通过 OnvifClient.getStreamUri 获取 RTSP 地址（需要 profileToken）
     * 3. 若获取失败则回退使用数据库中缓存的 liveStreamTcp/Udp/Multicast
     * 4. 通过 ZLM addStreamProxy 拉起代理流
     *
     * @param channel  通用通道（channelId 对应 OnvifDeviceChannel.id）
     * @param platform 平台信息（ONVIF 不使用，可为 null）
     * @param record   是否录制
     * @return 流信息（含播放地址）
     */
    @Override
    public StreamInfo play(CommonChannel channel, Platform platform, Boolean record) {
        log.info("[ONVIF 播放] channelId: {}", channel.getId());
        Boolean streamReady;
        OnvifDeviceChannel onvifChannel = resolveChannel(channel);
        String stream = onvifChannel.getId() + "";

        // Step 1: 检查已有活跃流
        MediaInfo streamInfo = mediaCacheService.getStreamInfo(APP, stream, "*");
        if (streamInfo != null) {
            String mediaServerId = streamInfo.getMediaServer().getServerId();
            MediaServer mediaServerItem = mediaServerService.selectMediaServerByServerId(mediaServerId);
            if (mediaServerItem != null
                    && (streamReady = zlmRtpUtils.isStreamReady(mediaServerItem, APP, stream)) != null
                    && streamReady) {
                log.info("[ONVIF 播放] 流已存在，直接返回，channelId: {}", channel.getId());
                return mediaService.getStreamInfoByAppAndStream(mediaServerItem, APP, stream, null, null);
            }
            // 流已不可用，清理缓存
            mediaCacheService.removeStream(mediaServerId, APP, APP, stream);
        }

        // Step 2: 获取可用 ZLM
        MediaServer mediaServer = mediaServerService.getMediaServerForMinimumLoad(null);
        if (mediaServer == null) {
            throw new ServiceException(ErrorCode.ERROR100.getCode(), "没有可用的ZLM媒体服务器");
        }

        // Step 3: 获取 RTSP 流地址
        String rtspUri = resolveRtspUri(onvifChannel);
        if (rtspUri == null || rtspUri.isEmpty()) {
            throw new ServiceException(ErrorCode.ERROR100.getCode(),
                    "无法获取播放地址，请先查询设备信息以获取 profileToken");
        }

        // Step 4: ZLM 拉流代理
        boolean enableAudio = onvifChannel.getEnableAudio() != null && onvifChannel.getEnableAudio() == 1;
        boolean enableMp4 = Boolean.TRUE.equals(record)
                || (onvifChannel.getEnableMp4() != null && onvifChannel.getEnableMp4() == 1);
        int rtpType = getRtpType(onvifChannel);
        log.debug("[ONVIF 播放] 拉流，url: {}, app: {}, stream: {}, audio: {}", rtspUri, APP, stream, enableAudio);

        zlmApiUtils.addStreamProxy(mediaServer, APP, stream, rtspUri, enableAudio, enableMp4, rtpType, 15);
        log.info("[ONVIF 播放] 拉流成功，channelId: {}", channel.getId());

        // 返回流信息（拉流是异步的，先返回预期的流信息）
        return mediaService.getStreamInfoByAppAndStream(mediaServer, APP, stream, null, null);
    }

    /**
     * 停止播放 ONVIF 通道视频流
     * 关闭 ZLM 代理流并清理缓存
     *
     * @param channel 通用通道
     * @return 操作结果
     */
    @Override
    public Boolean stopPlay(CommonChannel channel) {
        log.info("[ONVIF 停播] channelId: {}", channel.getId());
        OnvifDeviceChannel onvifChannel = resolveChannel(channel);
        String stream = onvifChannel.getId() + "";
        MediaInfo streamInfo = mediaCacheService.getStreamInfo(APP, stream, "*");
        if (streamInfo == null) {
            log.info("[ONVIF 停播] 无活跃流，channelId: {}", channel.getId());
            return true;
        }
        String mediaServerId = streamInfo.getServerId();
        MediaServer mediaServer = mediaServerService.selectMediaServerByServerId(mediaServerId);
        if (mediaServer != null) {
            zlmApiUtils.closeStreams(mediaServer, APP, stream);
        }
        mediaCacheService.removeStream(mediaServerId, APP, APP, stream);
        log.info("[ONVIF 停播] 成功，channelId: {}", channel.getId());
        return true;
    }

    /**
     * 关闭指定流（条件性）
     * 当 check=true 时仅在无人观看时关闭
     *
     * @param channel 通用通道
     * @param stream  流 ID
     * @param check   是否检查无人观看
     * @return 操作结果
     */
    @Override
    public Boolean closeStream(CommonChannel channel, String stream, Boolean check) {
        log.info("[ONVIF 关流] channelId: {}, stream: {}, check: {}", channel.getId(), stream, check);
        MediaInfo streamInfo = mediaCacheService.getStreamInfo(APP, stream, "*");
        if (streamInfo == null) {
            return true;
        }
        // check=true 时验证是否还有观看者（通过 ZLM 统计）
        if (Boolean.TRUE.equals(check)) {
            String mediaServerId = streamInfo.getServerId();
            MediaServer mediaServer = mediaServerService.selectMediaServerByServerId(mediaServerId);
            if (mediaServer != null) {
                Boolean ready = zlmRtpUtils.isStreamReady(mediaServer, APP, stream);
                if (Boolean.FALSE.equals(ready)) {
                    // 流已无观看者，关闭
                    zlmApiUtils.closeStreams(mediaServer, APP, stream);
                    mediaCacheService.removeStream(mediaServerId, APP, APP, stream);
                    return true;
                }
                log.info("[ONVIF 关流] 流仍有观看者，保持，channelId: {}", channel.getId());
                return false;
            }
        }
        // check=false 强制关闭
        String mediaServerId = streamInfo.getServerId();
        MediaServer mediaServer = mediaServerService.selectMediaServerByServerId(mediaServerId);
        if (mediaServer != null) {
            zlmApiUtils.closeStreams(mediaServer, APP, stream);
        }
        mediaCacheService.removeStream(mediaServerId, APP, APP, stream);
        return true;
    }

    // -------------------------------------------------------------------------
    // 内部工具
    // -------------------------------------------------------------------------

    /**
     * 通过 CommonChannel 查找对应的 OnvifDeviceChannel
     * CommonChannel.channelId 对应 OnvifDeviceChannel.id
     */
    private OnvifDeviceChannel resolveChannel(CommonChannel channel) {
        Integer onvifChannelId = extractOnvifChannelId(channel);
        OnvifDeviceChannel onvifChannel = onvifDeviceChannelService.selectOnvifDeviceChannelById(onvifChannelId);
        if (onvifChannel == null) {
            throw new ServiceException(ErrorCode.ERROR400.getCode(),
                    "ONVIF 通道不存在，channelId: " + channel.getChannelId());
        }
        return onvifChannel;
    }

    /** 从 CommonChannel 提取 ONVIF 通道 ID */
    private Integer extractOnvifChannelId(CommonChannel channel) {
        try {
            return Integer.parseInt(channel.getChannelId());
        } catch (NumberFormatException e) {
            return Math.toIntExact(channel.getId());
        }
    }

    /**
     * 获取 RTSP 流地址
     * 优先通过 OnvifClient 动态获取，回退使用数据库缓存地址
     */
    private String resolveRtspUri(OnvifDeviceChannel channel) {
        // 优先动态获取（需 profileToken）
        if (channel.getProfileToken() != null && !channel.getProfileToken().isEmpty()
                && channel.getUsername() != null && channel.getPassword() != null) {
            try {
                String uri = onvifClient.getStreamUri(channel, "RtspUnicast");
                if (uri != null && !uri.isEmpty()) {
                    // 注入认证信息并持久化
                    String authUri = buildAuthUri(uri, channel.getUsername(), channel.getPassword());
                    channel.setLiveStreamTcp(uri);
                    onvifDeviceChannelService.updateWithCache(channel);
                    return authUri;
                }
            } catch (Exception e) {
                log.warn("[ONVIF 播放] GetStreamUri 失败，回退到缓存，通道: {}, 错误: {}",
                        channel.getId(), e.getMessage());
            }
        }
        // 回退：使用缓存地址
        String uri = channel.getLiveStreamTcp();
        if (uri == null) {
            uri = channel.getLiveStreamUdp();
        }
        if (uri == null) {
            uri = channel.getLiveStreamMulticast();
        }
        if (uri == null) {
            return null;
        }
        return buildAuthUri(uri, channel.getUsername(), channel.getPassword());
    }

    /** 在 URI 中注入认证信息 */
    private String buildAuthUri(String uri, String username, String password) {
        if (username == null || password == null) {
            return uri;
        }
        try {
            return new org.apache.http.client.utils.URIBuilder(uri)
                    .setUserInfo(username, password).toString();
        } catch (Exception e) {
            log.warn("[ONVIF] URI 认证注入失败: {}", uri);
            return uri;
        }
    }

    /** 确定 RTP 传输类型 */
    private int getRtpType(OnvifDeviceChannel channel) {
        if (channel.getLiveStreamTcp() != null) return 0;
        if (channel.getLiveStreamUdp() != null) return 1;
        return 2;
    }
}
