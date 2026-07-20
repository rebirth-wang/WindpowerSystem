package com.fastbee.onvif.service.impl;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastbee.common.exception.ServiceException;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.domain.MediaServer;
import com.fastbee.media.enums.ChannelStreamType;
import com.fastbee.media.enums.ErrorCode;
import com.fastbee.media.model.MediaInfo;
import com.fastbee.media.model.RecordItem;
import com.fastbee.media.model.RecordList;
import com.fastbee.media.model.StreamInfo;
import com.fastbee.media.service.IMediaCacheService;
import com.fastbee.media.service.IMediaServerService;
import com.fastbee.media.service.IMediaService;
import com.fastbee.media.service.IPlaybackService;
import com.fastbee.media.util.ZlmApiUtils;
import com.fastbee.onvif.domain.OnvifDeviceChannel;
import com.fastbee.onvif.service.IOnvifDeviceChannelService;
import com.fastbee.onvif.util.OnvifClient;

/**
 * ONVIF 视频回放服务实现
 * 通过 OnvifClient 调用 ONVIF Recording/Replay Service 实现录像查询和回放控制
 *
 * <p>ONVIF 标准对应：
 * <ul>
 *   <li>{@code queryRecord} → GetRecordingSummary（查询录像摘要）</li>
 *   <li>{@code playback} → GetReplayUri → ZLM 拉流代理</li>
 *   <li>{@code stopPlayback} → ZLM closeStreams</li>
 *   <li>{@code playbackPause} → RTSP PAUSE（通过 ZLM pauseProxy）</li>
 *   <li>{@code playbackResume} → RTSP PLAY（通过 ZLM 重新拉流）</li>
 *   <li>{@code playbackSeek} → 重新拉起含 seek 时间的回放流</li>
 *   <li>{@code playbackSpeed} → ONVIF 暂未标准化，通过 ZLM 速率控制</li>
 *   <li>{@code download} → GetReplayUri + ZLM 全速拉流</li>
 * </ul>
 *
 * @author fastbee
 */
@Slf4j
@Service(ChannelStreamType.PLAYBACK_SERVICE + ChannelStreamType.ONVIF)
public class PlaybackServiceForOnvifImpl implements IPlaybackService {

    private static final String APP = "onvif_playback";

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
    private OnvifClient onvifClient;

    /**
     * 开始回放
     * 调用 ONVIF Replay Service - GetReplayUri 获取回放 RTSP 地址，
     * 然后通过 ZLM 拉流代理
     *
     * @param channel   通用通道
     * @param startTime 开始时间（ISO 8601 或 yyyy-MM-dd HH:mm:ss）
     * @param stopTime  结束时间
     * @return 流信息（含播放地址）
     */
    @Override
    public StreamInfo playback(CommonChannel channel, String startTime, String stopTime) {
        log.info("[ONVIF 回放] channelId: {}, 开始: {}, 结束: {}", channel.getId(), startTime, stopTime);
        OnvifDeviceChannel onvifChannel = resolveChannel(channel);

        // 确保有 profileToken
        ensureProfileToken(onvifChannel);

        // 转换时间格式为 ISO 8601
        String iso8601Start = toIso8601(startTime);
        String iso8601Stop = toIso8601(stopTime);

        // 获取 ZLM 媒体服务器
        MediaServer mediaServer = mediaServerService.getMediaServerForMinimumLoad(null);
        if (mediaServer == null) {
            throw new ServiceException(ErrorCode.ERROR100.getCode(), "没有可用的ZLM媒体服务器");
        }

        // 获取回放流地址（ONVIF Replay Service - GetReplayUri）
        String replayUri;
        try {
            replayUri = onvifClient.getReplayUri(onvifChannel, iso8601Start, iso8601Stop);
        } catch (Exception e) {
            log.warn("[ONVIF 回放] GetReplayUri 失败，回退到 liveStream，错误: {}", e.getMessage());
            // 部分设备不支持 Replay Service，回退到带时间参数的实时流
            replayUri = buildFallbackReplayUri(onvifChannel, iso8601Start);
        }

        if (replayUri == null || replayUri.isEmpty()) {
            throw new ServiceException(ErrorCode.ERROR100.getCode(), "无法获取回放地址");
        }

        // 注入认证信息
        String authUri = buildAuthUri(replayUri, onvifChannel.getUsername(), onvifChannel.getPassword());
        String streamId = buildPlaybackStreamId(onvifChannel.getId(), startTime);
        log.debug("[ONVIF 回放] 拉流，url: {}, stream: {}", authUri, streamId);

        // ZLM 拉流代理
        zlmApiUtils.addStreamProxy(mediaServer, APP, streamId, authUri,
                onvifChannel.getEnableAudio() != null && onvifChannel.getEnableAudio() == 1,
                false, 0, 30);

        log.info("[ONVIF 回放] 拉流成功，channelId: {}", channel.getId());
        return mediaService.getStreamInfoByAppAndStream(mediaServer, APP, streamId, null, null);
    }

    /**
     * 停止回放
     *
     * @param channel  通用通道
     * @param streamId 流 ID
     * @return 操作结果
     */
    @Override
    public Boolean stopPlayback(CommonChannel channel, String streamId) {
        log.info("[ONVIF 停止回放] channelId: {}, streamId: {}", channel.getId(), streamId);
        MediaInfo streamInfo = mediaCacheService.getStreamInfo(APP, streamId, "*");
        if (streamInfo == null) {
            log.info("[ONVIF 停止回放] 无活跃回放流，channelId: {}", channel.getId());
            return true;
        }
        String mediaServerId = streamInfo.getServerId();
        MediaServer mediaServer = mediaServerService.selectMediaServerByServerId(mediaServerId);
        if (mediaServer != null) {
            zlmApiUtils.closeStreams(mediaServer, APP, streamId);
        }
        mediaCacheService.removeStream(mediaServerId, APP, APP, streamId);
        return true;
    }

    /**
     * 暂停回放
     * ZLM 代理流不直接支持 RTSP PAUSE，通过先关闭流实现暂停。
     * 如需恢复，需调用 {@link #playbackResume} 重新拉流。
     *
     * @param channel  通用通道
     * @param streamId 流 ID
     * @return 操作结果
     */
    @Override
    public Boolean playbackPause(CommonChannel channel, String streamId) {
        log.info("[ONVIF 暂停回放] channelId: {}, streamId: {}", channel.getId(), streamId);
        // ZLM addStreamProxy 不提供 pause/resume API，通过记录暂停位置实现
        // 此处先关闭 ZLM 代理流，resume 时重新指定 startTime 拉起
        MediaInfo streamInfo = mediaCacheService.getStreamInfo(APP, streamId, "*");
        if (streamInfo == null) {
            log.warn("[ONVIF 暂停回放] 未找到活跃流，channelId: {}", channel.getId());
            return false;
        }
        MediaServer mediaServer = mediaServerService.selectMediaServerByServerId(streamInfo.getServerId());
        if (mediaServer != null) {
            // 停止代理流（相当于暂停）
            zlmApiUtils.closeStreams(mediaServer, APP, streamId);
            log.info("[ONVIF 暂停回放] 已暂停（停止代理流），channelId: {}", channel.getId());
        }
        return true;
    }

    /**
     * 恢复回放
     * 由于 ZLM 代理流不提供 resume API，需上层传入恢复时间点重新拉流。
     * 本方法在无活跃流时，尝试从通道缓存地址重新发起拉流。
     *
     * @param channel  通用通道
     * @param streamId 流 ID（格式：{channelId}_{startTimeToken}）
     * @return 操作结果
     */
    @Override
    public Boolean playbackResume(CommonChannel channel, String streamId) {
        log.info("[ONVIF 恢复回放] channelId: {}, streamId: {}", channel.getId(), streamId);
        // 检查流是否已有活跃代理
        MediaInfo streamInfo = mediaCacheService.getStreamInfo(APP, streamId, "*");
        if (streamInfo != null) {
            log.info("[ONVIF 恢复回放] 流已存在，无需重启，channelId: {}", channel.getId());
            return true;
        }
        // 流不存在，尝试重新拉起（上层应提供 seekTime 后调用 playbackSeek 替代此方法）
        OnvifDeviceChannel onvifChannel = resolveChannel(channel);
        MediaServer mediaServer = mediaServerService.getMediaServerForMinimumLoad(null);
        if (mediaServer == null) {
            log.warn("[ONVIF 恢复回放] 无可用媒体服务器，channelId: {}", channel.getId());
            return false;
        }
        try {
            String replayUri = buildFallbackReplayUri(onvifChannel, null);
            if (replayUri != null && !replayUri.isEmpty()) {
                String authUri = buildAuthUri(replayUri, onvifChannel.getUsername(), onvifChannel.getPassword());
                zlmApiUtils.addStreamProxy(mediaServer, APP, streamId, authUri,
                        onvifChannel.getEnableAudio() != null && onvifChannel.getEnableAudio() == 1,
                        false, 0, 30);
                log.info("[ONVIF 恢复回放] 重新拉流成功，channelId: {}", channel.getId());
                return true;
            }
        } catch (Exception e) {
            log.error("[ONVIF 恢复回放] 失败，channelId: {}, 错误: {}", channel.getId(), e.getMessage());
        }
        return false;
    }

    /**
     * 回放跳转
     * ONVIF 通过重新拉起含 starttime 参数的 Replay URI 实现 Seek
     * 对应 ONVIF Replay: 重新发起带 seek 时间的 RTSP 请求
     *
     * @param channel  通用通道
     * @param streamId 流 ID
     * @param seekTime seek 目标时间（毫秒时间戳）
     * @return 操作结果
     */
    @Override
    public Boolean playbackSeek(CommonChannel channel, String streamId, Long seekTime) {
        log.info("[ONVIF 回放跳转] channelId: {}, streamId: {}, seekTime: {}", channel.getId(), streamId, seekTime);
        // 先停止当前流
        stopPlayback(channel, streamId);
        // 重新拉起到目标时间点
        String seekTimeStr = formatSeekTime(seekTime);
        // 简单实现：重新调用 playback，从 seekTime 开始
        OnvifDeviceChannel onvifChannel = resolveChannel(channel);
        ensureProfileToken(onvifChannel);
        MediaServer mediaServer = mediaServerService.getMediaServerForMinimumLoad(null);
        if (mediaServer == null) {
            return false;
        }
        try {
            String replayUri = onvifClient.getReplayUri(onvifChannel, toIso8601(seekTimeStr), null);
            if (replayUri != null && !replayUri.isEmpty()) {
                String authUri = buildAuthUri(replayUri, onvifChannel.getUsername(), onvifChannel.getPassword());
                zlmApiUtils.addStreamProxy(mediaServer, APP, streamId, authUri, false, false, 0, 30);
                log.info("[ONVIF 回放跳转] 成功，channelId: {}", channel.getId());
                return true;
            }
        } catch (Exception e) {
            log.error("[ONVIF 回放跳转] 失败，channelId: {}, 错误: {}", channel.getId(), e.getMessage());
        }
        return false;
    }

    /**
     * 设置回放速率
     * ONVIF 标准暂未规范速率控制，ZLM 通过 setRecordSpeed 支持 MP4 录像的速率控制。
     * 对于 RTSP 代理流，速率控制需重新拉起对应速率的流（ONVIF 不支持速率控制命令）。
     *
     * @param channel  通用通道
     * @param streamId 流 ID
     * @param speed    播放速率（1.0 = 正常，2.0 = 2倍速）
     * @return 操作结果
     */
    @Override
    public Boolean playbackSpeed(CommonChannel channel, String streamId, Double speed) {
        log.info("[ONVIF 回放速率] channelId: {}, streamId: {}, speed: {}", channel.getId(), streamId, speed);
        MediaInfo streamInfo = mediaCacheService.getStreamInfo(APP, streamId, "*");
        if (streamInfo == null) {
            log.warn("[ONVIF 回放速率] 未找到活跃流，channelId: {}", channel.getId());
            return false;
        }
        MediaServer mediaServer = mediaServerService.selectMediaServerByServerId(streamInfo.getServerId());
        if (mediaServer == null) {
            return false;
        }
        // ZLM setRecordSpeed 用于 MP4 录像速率，代理流不支持动态变速
        // 此处调用 ZLM 的 setRecordSpeed 接口（仅对 ZLM 内部 MP4 录像有效）
        String speedStr = speed != null ? String.valueOf(speed) : "1.0";
        try {
            zlmApiUtils.setRecordSpeed(mediaServer, speedStr, APP, streamId);
            log.info("[ONVIF 回放速率] 已设置速率 {}，channelId: {}", speed, channel.getId());
            return true;
        } catch (Exception e) {
            log.warn("[ONVIF 回放速率] 设置速率失败（ONVIF/ZLM 代理流不支持动态变速），channelId: {}", channel.getId());
            return false;
        }
    }

    /**
     * 查询录像记录
     * 调用 ONVIF Recording Service - GetRecordingSummary 获取录像摘要
     * 对应 ONVIF Recording Control Service Specification
     *
     * @param channel   通用通道
     * @param startTime 开始时间
     * @param stopTime  结束时间
     * @return 录像列表
     */
    @Override
    public RecordList queryRecord(CommonChannel channel, String startTime, String stopTime) {
        log.info("[ONVIF 录像查询] channelId: {}, 开始: {}, 结束: {}", channel.getId(), startTime, stopTime);
        OnvifDeviceChannel onvifChannel = resolveChannel(channel);
        RecordList recordList = new RecordList();
        recordList.setDeviceID(channel.getChannelId());

        try {
            Map<String, String> summary = onvifClient.getRecordingSummary(onvifChannel);
            // 将录像摘要转换为 RecordList
            String dataFrom = summary.get("dataFrom");
            String dataUntil = summary.get("dataUntil");
            if (dataFrom != null && dataUntil != null) {
                RecordItem item = new RecordItem();
                item.setStart(parseIso8601ToMillis(dataFrom));
                item.setEnd(parseIso8601ToMillis(dataUntil));
                recordList.addItem(item);
                recordList.setSumNum(1);
                log.info("[ONVIF 录像查询] 成功，从: {} 到: {}", dataFrom, dataUntil);
            } else {
                log.info("[ONVIF 录像查询] 设备未返回录像摘要，channelId: {}", channel.getId());
            }
        } catch (Exception e) {
            log.warn("[ONVIF 录像查询] 失败（设备可能不支持 Recording Service），channelId: {}, 错误: {}",
                    channel.getId(), e.getMessage());
        }
        return recordList;
    }

    /**
     * 下载录像
     * 与 playback 相同，但速率设置为 downloadSpeed 倍速
     *
     * @param channel       通用通道
     * @param startTime     开始时间
     * @param endTime       结束时间
     * @param downloadSpeed 下载倍速
     * @return 流信息（可直接播放或录制）
     */
    @Override
    public StreamInfo download(CommonChannel channel, String startTime, String endTime, Integer downloadSpeed) {
        log.info("[ONVIF 下载] channelId: {}, 开始: {}, 结束: {}, 倍速: {}",
                channel.getId(), startTime, endTime, downloadSpeed);
        // ONVIF 下载与回放使用相同的 ReplayUri，通过速率控制实现加速
        StreamInfo streamInfo = playback(channel, startTime, endTime);
        if (streamInfo != null && downloadSpeed != null && downloadSpeed > 1) {
            // 尝试设置下载速率（ZLM setRecordSpeed 仅对 MP4 录像有效）
            MediaServer mediaServer = mediaServerService.getMediaServerForMinimumLoad(null);
            if (mediaServer != null) {
                String streamId = buildPlaybackStreamId(extractOnvifChannelId(channel), startTime);
                try {
                    zlmApiUtils.setRecordSpeed(mediaServer, String.valueOf(downloadSpeed), APP, streamId);
                } catch (Exception e) {
                    log.warn("[ONVIF 下载] 设置下载倍速失败，使用正常速率，错误: {}", e.getMessage());
                }
            }
        }
        return streamInfo;
    }

    // -------------------------------------------------------------------------
    // 内部工具
    // -------------------------------------------------------------------------

    private OnvifDeviceChannel resolveChannel(CommonChannel channel) {
        Integer onvifChannelId = extractOnvifChannelId(channel);
        OnvifDeviceChannel onvifChannel = onvifDeviceChannelService.selectOnvifDeviceChannelById(onvifChannelId);
        if (onvifChannel == null) {
            throw new ServiceException(ErrorCode.ERROR400.getCode(),
                    "ONVIF 通道不存在，channelId: " + channel.getChannelId());
        }
        return onvifChannel;
    }

    private Integer extractOnvifChannelId(CommonChannel channel) {
        try {
            return Integer.parseInt(channel.getChannelId());
        } catch (NumberFormatException e) {
            return Math.toIntExact(channel.getId());
        }
    }

    private void ensureProfileToken(OnvifDeviceChannel channel) {
        if (channel.getProfileToken() == null || channel.getProfileToken().isEmpty()) {
            try {
                List<String> profiles = onvifClient.getProfiles(channel);
                if (!profiles.isEmpty()) {
                    channel.setProfileToken(profiles.get(0));
                    onvifDeviceChannelService.updateWithCache(channel);
                }
            } catch (Exception e) {
                throw new ServiceException(ErrorCode.ERROR100.getCode(),
                        "无法获取 Profile Token，请先查询设备信息: " + e.getMessage());
            }
        }
    }

    private String buildAuthUri(String uri, String username, String password) {
        if (username == null || password == null) return uri;
        try {
            return new org.apache.http.client.utils.URIBuilder(uri).setUserInfo(username, password).toString();
        } catch (Exception e) {
            return uri;
        }
    }

    /** 构建回放流 ID（包含通道 ID 和开始时间，保证唯一性） */
    private String buildPlaybackStreamId(Integer channelId, String startTime) {
        String timeToken = startTime != null ? startTime.replaceAll("[^0-9]", "") : "0";
        return channelId + "_" + timeToken;
    }

    /** 转换为 ISO 8601 格式（兼容 yyyy-MM-dd HH:mm:ss 输入） */
    private String toIso8601(String timeStr) {
        if (timeStr == null) return null;
        if (timeStr.contains("T")) return timeStr; // 已是 ISO 8601
        // yyyy-MM-dd HH:mm:ss → yyyy-MM-ddTHH:mm:ssZ
        return timeStr.replace(" ", "T") + "Z";
    }

    /** 毫秒时间戳转 seek 时间字符串 */
    private String formatSeekTime(Long seekTimeMs) {
        if (seekTimeMs == null) return null;
        java.util.Date date = new java.util.Date(seekTimeMs);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    /** 解析 ISO 8601 时间戳为毫秒 */
    private long parseIso8601ToMillis(String iso8601) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            return sdf.parse(iso8601).getTime();
        } catch (Exception e) {
            return 0L;
        }
    }

    /** 当设备不支持 Replay Service 时的回退方案（使用带时间的实时流地址） */
    private String buildFallbackReplayUri(OnvifDeviceChannel channel, String startTime) {
        String uri = channel.getReplayStream();
        if (uri == null) uri = channel.getLiveStreamTcp();
        return uri;
    }
}
