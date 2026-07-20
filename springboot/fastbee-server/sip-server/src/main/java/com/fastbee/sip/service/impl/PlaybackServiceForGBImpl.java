package com.fastbee.sip.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastbee.common.extend.config.SysSipConfig;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.enums.ChannelStreamType;
import com.fastbee.media.model.RecordList;
import com.fastbee.media.model.StreamInfo;
import com.fastbee.media.service.IPlaybackService;
import com.fastbee.sip.domain.SipDevice;
import com.fastbee.sip.redisMsg.IRedisRpcPlayService;
import com.fastbee.sip.service.ISipDeviceService;
import com.fastbee.sip.service.ISipPlayService;

@Slf4j
@Service(ChannelStreamType.PLAYBACK_SERVICE + ChannelStreamType.GB28181)
public class PlaybackServiceForGBImpl implements IPlaybackService {
    @Autowired
    private ISipDeviceService sipDeviceService;
    @Autowired
    private SysSipConfig sysSipConfig;
    @Autowired
    private IRedisRpcPlayService redisRpcPlayService;
    @Autowired
    private ISipPlayService sipPlayService;

    @Override
    public StreamInfo playback(CommonChannel channel, String startTime, String stopTime) {
        SipDevice device = sipDeviceService.selectSipDeviceBySipId(channel.getDeviceId());
        if (device == null) {
            log.error("playback dev is null,deviceId:{},channelId:{}", channel.getDeviceId(), channel.getChannelId());
            return null;
        }
        // 判断设备是否属于当前平台, 如果不属于则发起自动调用
        if (!sysSipConfig.getServerId().equals(device.getServerId())) {
            return redisRpcPlayService.playback(device.getServerId(), channel.getChannelId(), startTime, stopTime, null);
        }
        return sipPlayService.playback(device, channel.getChannelId(), startTime, stopTime);
    }

    @Override
    public Boolean stopPlayback(CommonChannel channel, String stream) {
        SipDevice device = sipDeviceService.selectSipDeviceBySipId(channel.getDeviceId());
        if (device == null) {
            log.error("stopPlayback dev is null,deviceId:{},channelId:{}", channel.getDeviceId(), channel.getChannelId());
            return null;
        }
        // 判断设备是否属于当前平台, 如果不属于则发起自动调用
        if (!sysSipConfig.getServerId().equals(device.getServerId())) {
            return redisRpcPlayService.closeStream(device.getServerId(), channel.getChannelId(), stream, true);
        }
        return sipPlayService.closeStream(device, channel.getChannelId(), stream, true);
    }

    @Override
    public Boolean playbackPause(CommonChannel channel, String stream) {
        SipDevice device = sipDeviceService.selectSipDeviceBySipId(channel.getDeviceId());
        if (device == null) {
            log.error("playbackPause dev is null,deviceId:{},channelId:{}", channel.getDeviceId(), channel.getChannelId());
            return null;
        }
        // 判断设备是否属于当前平台, 如果不属于则发起自动调用
        if (!sysSipConfig.getServerId().equals(device.getServerId())) {
            return redisRpcPlayService.playbackPause(device.getServerId(), channel.getChannelId(), stream);
        }
        return sipPlayService.playbackPause(device.getDeviceSipId(), channel.getChannelId(), stream);
    }

    @Override
    public Boolean playbackResume(CommonChannel channel, String stream) {
        SipDevice device = sipDeviceService.selectSipDeviceBySipId(channel.getDeviceId());
        if (device == null) {
            log.error("playbackResume dev is null,deviceId:{},channelId:{}", channel.getDeviceId(), channel.getChannelId());
            return null;
        }
        // 判断设备是否属于当前平台, 如果不属于则发起自动调用
        if (!sysSipConfig.getServerId().equals(device.getServerId())) {
            return redisRpcPlayService.playbackResume(device.getServerId(), channel.getChannelId(), stream);
        }
        return sipPlayService.playbackReplay(device.getDeviceSipId(), channel.getChannelId(), stream);
    }

    @Override
    public Boolean playbackSeek(CommonChannel channel, String stream, Long seekTime) {
        SipDevice device = sipDeviceService.selectSipDeviceBySipId(channel.getDeviceId());
        if (device == null) {
            log.error("playbackSeek dev is null,deviceId:{},channelId:{}", channel.getDeviceId(), channel.getChannelId());
            return null;
        }
        // 判断设备是否属于当前平台, 如果不属于则发起自动调用
        if (!sysSipConfig.getServerId().equals(device.getServerId())) {
            return redisRpcPlayService.playbackSeek(device.getServerId(), channel.getChannelId(), stream, seekTime);
        }
        return sipPlayService.playbackSeek(device.getDeviceSipId(), channel.getChannelId(), stream, seekTime);
    }

    @Override
    public Boolean playbackSpeed(CommonChannel channel, String stream, Double speed) {
        SipDevice device = sipDeviceService.selectSipDeviceBySipId(channel.getDeviceId());
        if (device == null) {
            log.error("playbackSpeed dev is null,deviceId:{},channelId:{}", channel.getDeviceId(), channel.getChannelId());
            return null;
        }
        // 判断设备是否属于当前平台, 如果不属于则发起自动调用
        if (!sysSipConfig.getServerId().equals(device.getServerId())) {
            return redisRpcPlayService.playbackSpeed(device.getServerId(), channel.getChannelId(), stream, speed);
        }
        return sipPlayService.playbackSpeed(device.getDeviceSipId(), channel.getChannelId(), stream, speed);
    }

    @Override
    public RecordList queryRecord(CommonChannel channel, String startTime, String stopTime) {
        SipDevice device = sipDeviceService.selectSipDeviceBySipId(channel.getDeviceId());
        if (device == null) {
            log.error("queryRecord dev is null,deviceId:{},channelId:{}", channel.getDeviceId(), channel.getChannelId());
            return null;
        }
        // 判断设备是否属于当前平台, 如果不属于则发起自动调用
        if (!sysSipConfig.getServerId().equals(device.getServerId())) {
            return redisRpcPlayService.queryRecordInfo(device.getServerId(), channel.getChannelId(), startTime, stopTime, null);
        }
        return sipPlayService.listDevRecord(device, channel.getChannelId(), startTime, stopTime);
    }

    @Override
    public StreamInfo download(CommonChannel channel, String startTime, String endTime, Integer downloadSpeed) {
        SipDevice device = sipDeviceService.selectSipDeviceBySipId(channel.getDeviceId());
        if (device == null) {
            log.error("download dev is null,deviceId:{},channelId:{}", channel.getDeviceId(), channel.getChannelId());
            return null;
        }
        // 判断设备是否属于当前平台, 如果不属于则发起自动调用
        if (!sysSipConfig.getServerId().equals(device.getServerId())) {
            return redisRpcPlayService.download(device.getServerId(), channel.getChannelId(), startTime, endTime, downloadSpeed, null);
        }
        return sipPlayService.download(device, channel.getChannelId(), startTime, endTime, downloadSpeed);
    }
}
