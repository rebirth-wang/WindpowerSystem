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
import com.fastbee.media.model.Platform;
import com.fastbee.media.model.StreamInfo;
import com.fastbee.media.service.IPlayService;

@Slf4j
@Profile("isup")
@Service(ChannelStreamType.PLAY_SERVICE + ChannelStreamType.ISUP)
public class PlayServiceForIsupImpl implements IPlayService {
    @Resource
    private DeviceCacheService deviceCacheService;
    @Resource
    private IMediaStreamService mediaStreamService;

    @Override
    public StreamInfo play(CommonChannel channel, Platform platform, Boolean record) {
        String deviceId = channel.getDeviceId();
        Integer channelId = Integer.valueOf(channel.getChannelId());
        Optional<IsupDevInfo> deviceOpt = deviceCacheService.getByDeviceId(deviceId);
        if (!deviceOpt.isPresent()) {
            return null;
        }
        IsupDevInfo device = deviceOpt.get();
        // 查找指定的通道
        IsupDevInfo.Channel Isuchannel = device.getChannels().stream()
                .filter(ch -> ch.getChannelId().equals(channelId))
                .findFirst()
                .orElse(null);
        if (Isuchannel == null) {
            return null;
        }
        return mediaStreamService.preview(device, channelId);
    }

    @Override
    public Boolean stopPlay(CommonChannel channel) {
        String deviceId = channel.getDeviceId();
        Integer channelId = Integer.valueOf(channel.getChannelId());
        Optional<IsupDevInfo> deviceOpt = deviceCacheService.getByDeviceId(deviceId);
        AtomicReference<Boolean> ret = new AtomicReference<>(false);
        deviceOpt.ifPresent(device -> {
            log.info("停止预览 - deviceId: {}, channelId: {}", deviceId, channelId);
            ret.set(mediaStreamService.stopPreview(device, channelId));
        });
        return ret.get();
    }

    @Override
    public Boolean closeStream(CommonChannel channel, String stream, Boolean check) {
        return null;
    }
}
