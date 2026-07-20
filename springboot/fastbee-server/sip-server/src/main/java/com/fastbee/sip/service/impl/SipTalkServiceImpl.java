package com.fastbee.sip.service.impl;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastbee.media.domain.MediaServer;
import com.fastbee.media.model.SendRtpItem;
import com.fastbee.media.model.StreamInfo;
import com.fastbee.media.service.IMediaServerService;
import com.fastbee.media.service.ISendRtpServerService;
import com.fastbee.media.util.ZlmRtpUtils;
import com.fastbee.sip.domain.SipDevice;
import com.fastbee.sip.model.BroadcastItem;
import com.fastbee.sip.server.ISipCmd;
import com.fastbee.sip.server.session.AudioBroadcastManager;
import com.fastbee.sip.service.*;

@Slf4j
@Service
public class SipTalkServiceImpl implements ISipTalkService {
    @Autowired
    private ISipCmd sipCmd;

    @Autowired
    private ZlmRtpUtils zlmRtpUtils;

    @Autowired
    private ISipDeviceService sipDeviceService;

    @Autowired
    private IZmlHookService zmlHookService;

    @Autowired
    private ISendRtpServerService sendRtpService;

    @Autowired
    private IMediaServerService mediaServerService;

    @Autowired
    private AudioBroadcastManager audioBroadcastManager;

    @Override
    public StreamInfo getBroadcastUrl(String deviceId, String channelId) {
        return zmlHookService.buildPushRtc("broadcast", deviceId, channelId);
    }

    @Override
    public String broadcast(String deviceId, String channelId) {
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        if (dev == null) {
            log.error("broadcast dev is null,deviceId:{},channelId:{}", deviceId, channelId);
            return null;
        }
        sipCmd.audioBroadcastCmd(dev, channelId);
        return "";
    }

    @Override
    public void broadcastStop(String deviceId, String channelId) {
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        if (dev == null) {
            log.error("[发送BYE] device is null");
            return ;
        }

        log.info("[停止对讲] 设备：{}, 通道：{}", deviceId, channelId);
        List<BroadcastItem> audioBroadcastList = new ArrayList<>();
        audioBroadcastList.addAll(audioBroadcastManager.getByDeviceId(deviceId));
        MediaServer mediaInfo = mediaServerService.selectMediaServerBydeviceSipId(deviceId);
        if (mediaInfo == null) {
            log.error("broadcastInviteOk mediaInfo is null");
            return;
        }
        if (!audioBroadcastList.isEmpty()) {
            for (BroadcastItem audioBroadcast : audioBroadcastList) {
                if (audioBroadcast == null) {
                    continue;
                }
                SendRtpItem sendRtpInfo = sendRtpService.queryByChannelId(channelId, deviceId);
                if (sendRtpInfo != null) {
                    sendRtpService.delete(sendRtpInfo);
                    zlmRtpUtils.stopSendRtpStream(mediaInfo, sendRtpInfo.getApp(), sendRtpInfo.getStream(), null);
                    sipCmd.streamByeCmdByInvite(dev, channelId, audioBroadcast.getSipTransactionInfo().getInviteInfo());
                }
                audioBroadcastManager.del(channelId);
            }
        }
    }

    @Override
    public boolean broadcastInUse(String deviceId, String channelId) {
        if (audioBroadcastManager.exit(channelId)) {
            SendRtpItem sendRtpInfo = sendRtpService.queryByChannelId(channelId, deviceId);
            if (sendRtpInfo != null && sendRtpInfo.isOnlyAudio()) {
                // 查询流是否存在，不存在则认为是异常状态
                MediaServer mediaInfo = mediaServerService.selectMediaServerBydeviceSipId(deviceId);
                if (mediaInfo == null) {
                    log.error("broadcastInviteOk mediaInfo is null");
                    return false;
                }
                Boolean streamReady = zlmRtpUtils.isStreamReady(mediaInfo, sendRtpInfo.getApp(), sendRtpInfo.getStream());
                if (streamReady) {
                    log.warn("语音广播通道使用中： {}", channelId);
                    return true;
                }
            }
        }
        return false;
    }
}
