package com.fastbee.media.service.impl;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.enums.ChannelStreamType;
import com.fastbee.media.enums.InviteType;
import com.fastbee.media.model.*;
import com.fastbee.media.service.IComChannelPlayService;
import com.fastbee.media.service.IPlayService;
import com.fastbee.media.service.IPlaybackService;

@Slf4j
@Service
public class ComChannelPlayServiceImpl implements IComChannelPlayService {

    @Autowired
    private Map<String, IPlayService> sourcePlayServiceMap;

    @Autowired
    private Map<String, IPlaybackService> sourcePlaybackServiceMap;

    @Override
    public void startInvite(CommonChannel channel, InviteMsgInfo inviteInfo, Platform platform) {
        if (channel == null || inviteInfo == null || channel.getDataType() == null) {
            log.warn("[通用通道点播] 参数异常, channel: {}, inviteInfo: {}", channel != null, inviteInfo != null);
            throw new PlayException(500, "server internal error");
        }
        log.info("[点播通用通道] 类型：{}， 通道： {}({})", inviteInfo.getSessionName(), channel.getChannelName(), channel.getDeviceId());

        if ("Play".equalsIgnoreCase(inviteInfo.getSessionName())) {
            play(channel, platform, false);
        } else if ("Playback".equals(inviteInfo.getSessionName())) {
            // string 转long
            inviteInfo.getStartTime();
            playback(channel, inviteInfo.getStartTime(), inviteInfo.getStopTime());
        } else if ("Download".equals(inviteInfo.getSessionName())) {
            Integer downloadSpeed = Integer.parseInt(inviteInfo.getDownloadSpeed());
            // 国标通道
            download(channel, inviteInfo.getStartTime(), inviteInfo.getStopTime(), downloadSpeed);
        } else {
            // 不支持的点播方式
            log.error("[点播通用通道] 不支持的点播方式：{}， {}({})", inviteInfo.getSessionName(), channel.getChannelName(), channel.getDeviceId());
            throw new PlayException(400, "bad request");
        }
    }

    @Override
    public void stopInvite(InviteType type, CommonChannel channel, String stream) {
        switch (type) {
            case PLAY:
                stopPlay(channel);
                break;
            case PLAYBACK:
                stopPlayback(channel, stream);
                break;
            case DOWNLOAD:
                stopDownload(channel, stream);
                break;
            default:
                // 通道数据异常
                log.error("[点播通用通道] 类型编号： {} 不支持此类型请求", type);
                throw new PlayException(486, "channel not support");
        }
    }


    @Override
    public StreamInfo play(CommonChannel channel, Platform platform, Boolean record) {
        log.info("[通用通道] 播放， 类型： {}， 编号：{}", ChannelStreamType.getDateTypeDesc(channel.getDataType()), channel.getDeviceId());
        Integer dataType = channel.getDataType();
        IPlayService sourceChannelPlayService = sourcePlayServiceMap.get(ChannelStreamType.PLAY_SERVICE + dataType);
        if (sourceChannelPlayService == null) {
            // 通道数据异常
            log.error("[点播通用通道] 类型编号： {} 不支持实时流预览", ChannelStreamType.getDateTypeDesc(channel.getDataType()));
            throw new PlayException(486, "channel not support");
        }
        return sourceChannelPlayService.play(channel, platform, record);
    }

    @Override
    public StreamPlayUrl getUrl(String deviceId, String channelId) {
        return null;
    }

    @Override
    public StreamInfo playback(CommonChannel channel, String startTime, String stopTime) {
        log.info("[通用通道] 回放， 类型： {}， 编号：{}", ChannelStreamType.getDateTypeDesc(channel.getDataType()), channel.getDeviceId());
        Integer dataType = channel.getDataType();
        IPlaybackService playbackService = sourcePlaybackServiceMap.get(ChannelStreamType.PLAYBACK_SERVICE + dataType);
        if (playbackService == null) {
            // 通道数据异常
            log.error("[回放通用通道] 类型编号： {} 不支持回放", dataType);
            throw new PlayException(486, "channel not support");
        }
        return playbackService.playback(channel, startTime, stopTime);
    }

    @Override
    public void download(CommonChannel channel, String startTime, String stopTime, Integer downloadSpeed) {
        log.info("[通用通道] 录像下载， 类型： {}， 编号：{}", ChannelStreamType.getDateTypeDesc(channel.getDataType()), channel.getDeviceId());
        Integer dataType = channel.getDataType();
        IPlaybackService playbackService = sourcePlaybackServiceMap.get(ChannelStreamType.PLAYBACK_SERVICE + dataType);
        if (playbackService == null) {
            // 通道数据异常
            log.error("[录像下载通用通道] 类型编号： {} 不支持录像下载", dataType);
            throw new PlayException(486, "channel not support");
        }
        playbackService.download(channel, startTime, stopTime, downloadSpeed);
    }

    @Override
    public Boolean stopPlay(CommonChannel channel) {
        Integer dataType = channel.getDataType();
        IPlayService sourceChannelPlayService = sourcePlayServiceMap.get(ChannelStreamType.PLAY_SERVICE + dataType);
        if (sourceChannelPlayService == null) {
            // 通道数据异常
            log.error("[停止播放通用通道] 类型编号： {} 不支持停止实时流", dataType);
            throw new PlayException(486, "channel not support");
        }
        return sourceChannelPlayService.stopPlay(channel);
    }

    @Override
    public Boolean stopPlayback(CommonChannel channel, String stream) {
        log.info("[通用通道] 停止回放， 类型： {}， 编号：{}", ChannelStreamType.getDateTypeDesc(channel.getDataType()), channel.getDeviceId());
        Integer dataType = channel.getDataType();
        IPlaybackService playbackService = sourcePlaybackServiceMap.get(ChannelStreamType.PLAYBACK_SERVICE + dataType);
        if (playbackService == null) {
            // 通道数据异常
            log.error("[停止回放通用通道] 类型编号： {} 不支持回放", dataType);
            throw new PlayException(486, "channel not support");
        }
        return playbackService.stopPlayback(channel, stream);
    }

    @Override
    public Boolean closeStream(CommonChannel channel, String stream, Boolean check) {
        return null;
    }

    @Override
    public void stopDownload(CommonChannel channel, String stream) {
        log.info("[通用通道] 停止录像下载， 类型： {}， 编号：{} stream: {}", ChannelStreamType.getDateTypeDesc(channel.getDataType()), channel.getDeviceId(), stream);
        Integer dataType = channel.getDataType();
//        ISourceDownloadService downloadService = sourceDownloadServiceMap.get(ChannelStreamType.DOWNLOAD_SERVICE + dataType);
//        if (downloadService == null) {
//            // 通道数据异常
//            log.error("[点播通用通道] 类型编号： {} 不支持录像下载", dataType);
//            throw new PlayException(486, "channel not support");
//        }
//        downloadService.stopDownload(channel, stream);
    }

    @Override
    public void playbackPause(CommonChannel channel, String stream) {
        log.info("[通用通道] 回放暂停， 类型： {}， 编号：{} stream：{}", ChannelStreamType.getDateTypeDesc(channel.getDataType()), channel.getDeviceId(), stream);
        Integer dataType = channel.getDataType();
        IPlaybackService playbackService = sourcePlaybackServiceMap.get(ChannelStreamType.PLAYBACK_SERVICE + dataType);
        if (playbackService == null) {
            // 通道数据异常
            log.error("[通用通道] 类型编号： {} 不支持回放暂停", dataType);
            throw new PlayException(486, "channel not support");
        }
        playbackService.playbackPause(channel, stream);
    }

    @Override
    public void playbackResume(CommonChannel channel, String stream) {
        log.info("[通用通道] 回放暂停恢复， 类型： {}， 编号：{} stream：{}", ChannelStreamType.getDateTypeDesc(channel.getDataType()), channel.getDeviceId(), stream);
        Integer dataType = channel.getDataType();
        IPlaybackService playbackService = sourcePlaybackServiceMap.get(ChannelStreamType.PLAYBACK_SERVICE + dataType);
        if (playbackService == null) {
            // 通道数据异常
            log.error("[通用通道] 类型编号： {} 不支持回放暂停恢复", dataType);
            throw new PlayException(486, "channel not support");
        }
        playbackService.playbackResume(channel, stream);
    }

    @Override
    public void playbackSeek(CommonChannel channel, String stream, long seekTime) {
        log.info("[通用通道] 回放拖动播放， 类型： {}， 编号：{} stream：{}", ChannelStreamType.getDateTypeDesc(channel.getDataType()), channel.getDeviceId(), stream);
        Integer dataType = channel.getDataType();
        IPlaybackService playbackService = sourcePlaybackServiceMap.get(ChannelStreamType.PLAYBACK_SERVICE + dataType);
        if (playbackService == null) {
            // 通道数据异常
            log.error("[通用通道] 类型编号： {} 不支持回放暂停恢复", dataType);
            throw new PlayException(486, "channel not support");
        }
        playbackService.playbackSeek(channel, stream, seekTime);
    }

    @Override
    public void playbackSpeed(CommonChannel channel, String stream, Double speed) {
        log.info("[通用通道] 回放倍速播放， 类型： {}， 编号：{} stream：{}", ChannelStreamType.getDateTypeDesc(channel.getDataType()), channel.getDeviceId(), stream);
        Integer dataType = channel.getDataType();
        IPlaybackService playbackService = sourcePlaybackServiceMap.get(ChannelStreamType.PLAYBACK_SERVICE + dataType);
        if (playbackService == null) {
            // 通道数据异常
            log.error("[通用通道] 类型编号： {} 不支持回放暂停恢复", dataType);
            throw new PlayException(486, "channel not support");
        }
        playbackService.playbackSpeed(channel, stream, speed);
    }

    @Override
    public RecordList queryRecord(CommonChannel channel, String startTime, String endTime) {
        log.info("[通用通道] 录像查询， 类型： {}， 编号：{}", ChannelStreamType.getDateTypeDesc(channel.getDataType()), channel.getDeviceId());
        Integer dataType = channel.getDataType();
        IPlaybackService playbackService = sourcePlaybackServiceMap.get(ChannelStreamType.PLAYBACK_SERVICE + dataType);
        if (playbackService == null) {
            // 通道数据异常
            log.error("[通用通道] 类型编号： {} 不支持回放暂停恢复", dataType);
            throw new PlayException(486, "channel not support");
        }
        return playbackService.queryRecord(channel, startTime, endTime);
    }
}
