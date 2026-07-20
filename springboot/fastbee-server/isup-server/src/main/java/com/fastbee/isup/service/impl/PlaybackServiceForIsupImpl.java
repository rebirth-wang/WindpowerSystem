package com.fastbee.isup.service.impl;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.fastbee.isup.model.IsupDevInfo;
import com.fastbee.isup.service.DeviceCacheService;
import com.fastbee.isup.service.IMediaStreamService;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.enums.ChannelStreamType;
import com.fastbee.media.model.RecordList;
import com.fastbee.media.model.StreamInfo;
import com.fastbee.media.service.IPlaybackService;

@Slf4j
@Profile("isup")
@Service(ChannelStreamType.PLAYBACK_SERVICE + ChannelStreamType.ISUP)
public class PlaybackServiceForIsupImpl implements IPlaybackService {
    @Resource
    private DeviceCacheService deviceCacheService;
    @Resource
    private IMediaStreamService mediaStreamService;

    @Override
    public StreamInfo playback(CommonChannel channel, String startTime, String stopTime) {
        String deviceId = channel.getDeviceId();
        Integer channelId = Integer.valueOf(channel.getChannelId());
        Optional<IsupDevInfo> deviceOpt = deviceCacheService.getByDeviceId(deviceId);
        if (!deviceOpt.isPresent()) {
            return null;
        }
        IsupDevInfo device = deviceOpt.get();
        return mediaStreamService.playbackByTime(device, channelId, startTime, stopTime);
    }

    @Override
    public Boolean stopPlayback(CommonChannel channel, String stream) {
        String deviceId = channel.getDeviceId();
        Integer channelId = Integer.valueOf(channel.getChannelId());
        Optional<IsupDevInfo> deviceOpt = deviceCacheService.getByDeviceId(deviceId);
        AtomicReference<Boolean> ret = new AtomicReference<>(false);
        deviceOpt.ifPresent(device -> {
            log.info("停止预览 - deviceId: {}, channelId: {}", deviceId, channelId);
            ret.set(mediaStreamService.stopPlayBackByTime(device, channelId, stream));
        });
        return ret.get();
    }

    @Override
    public Boolean playbackPause(CommonChannel channel, String stream) {
        return true;
    }

    @Override
    public Boolean playbackResume(CommonChannel channel, String stream) {
        return true;
    }

    @Override
    public Boolean playbackSeek(CommonChannel channel, String stream, Long seekTime) {
        return null;
    }

    @Override
    public Boolean playbackSpeed(CommonChannel channel, String stream, Double speed) {
        return true;
    }

    @Override
    public RecordList queryRecord(CommonChannel channel, String startTime, String endTime) {
        String deviceId = channel.getDeviceId();
        Integer channelId = Integer.valueOf(channel.getChannelId());
        Optional<IsupDevInfo> deviceOpt = deviceCacheService.getByDeviceId(deviceId);
        if (!deviceOpt.isPresent()) {
            return null;
        }
        IsupDevInfo device = deviceOpt.get();
        return mediaStreamService.getRecored(device, channelId, startTime, endTime);
    }

    @Override
    public StreamInfo download(CommonChannel channel, String startTime, String endTime, Integer downloadSpeed) {
        return null;
    }
}
