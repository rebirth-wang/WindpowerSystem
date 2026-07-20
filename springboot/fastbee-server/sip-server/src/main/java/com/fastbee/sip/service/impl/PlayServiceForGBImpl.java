package com.fastbee.sip.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastbee.common.extend.config.SysSipConfig;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.enums.ChannelStreamType;
import com.fastbee.media.enums.InviteType;
import com.fastbee.media.model.Platform;
import com.fastbee.media.model.StreamInfo;
import com.fastbee.media.service.IPlayService;
import com.fastbee.sip.domain.SipDevice;
import com.fastbee.sip.redisMsg.IRedisRpcPlayService;
import com.fastbee.sip.service.ISipDeviceService;
import com.fastbee.sip.service.ISipPlayService;
import com.fastbee.sip.util.SipUtil;

@Slf4j
@Service(ChannelStreamType.PLAY_SERVICE + ChannelStreamType.GB28181)
public class PlayServiceForGBImpl implements IPlayService {
    @Autowired
    private ISipDeviceService sipDeviceService;
    @Autowired
    private SysSipConfig sysSipConfig;
    @Autowired
    private IRedisRpcPlayService redisRpcPlayService;
    @Autowired
    private ISipPlayService sipPlayService;

    @Override
    public StreamInfo play(CommonChannel channel, Platform platform, Boolean record) {
        SipDevice device = sipDeviceService.selectSipDeviceBySipId(channel.getDeviceId());
        if (device == null) {
            log.error("play dev is null,deviceId:{},channelId:{}", channel.getDeviceId(), channel.getChannelId());
            return null;
        }
        // 判断设备是否属于当前平台, 如果不属于则发起自动调用
        if (!sysSipConfig.getServerId().equals(device.getServerId())) {
            return redisRpcPlayService.play(device.getServerId(), channel.getChannelId(), record, null);
        }
        return sipPlayService.play(device, channel.getChannelId(), record);
    }

    @Override
    public Boolean stopPlay(CommonChannel channel) {
        SipDevice device = sipDeviceService.selectSipDeviceBySipId(channel.getDeviceId());
        if (device == null) {
            log.error("play dev is null,deviceId:{},channelId:{}", channel.getDeviceId(), channel.getChannelId());
            return false;
        }
        String streamid = String.format("%s_%s_%s", SipUtil.PREFIX_PLAY, device.getDeviceId(), channel.getChannelId());
        // 判断设备是否属于当前平台, 如果不属于则发起自动调用
        if (!sysSipConfig.getServerId().equals(device.getServerId())) {
            return redisRpcPlayService.stop(device.getServerId(), InviteType.PLAY, channel.getChannelId(),streamid);
        }
        return sipPlayService.closeStream(device.getDeviceSipId(), channel.getChannelId(), streamid,false);
    }

    @Override
    public Boolean closeStream(CommonChannel channel, String stream, Boolean check) {
        SipDevice device = sipDeviceService.selectSipDeviceBySipId(channel.getDeviceId());
        if (device == null) {
            log.error("play dev is null,deviceId:{},channelId:{}", channel.getDeviceId(), channel.getChannelId());
            return false;
        }
        String streamid = String.format("%s_%s_%s", SipUtil.PREFIX_PLAY, device.getDeviceId(), channel.getChannelId());
        // 判断设备是否属于当前平台, 如果不属于则发起自动调用
        if (!sysSipConfig.getServerId().equals(device.getServerId())) {
            return redisRpcPlayService.stop(device.getServerId(), InviteType.PLAY, channel.getChannelId(),streamid);
        }
        return sipPlayService.closeStream(device.getDeviceSipId(), channel.getChannelId(), streamid,check);
    }
}
