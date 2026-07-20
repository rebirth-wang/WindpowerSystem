package com.fastbee.sip.service.impl;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastbee.media.domain.MediaServer;
import com.fastbee.media.model.InviteMsgInfo;
import com.fastbee.media.model.SendRtpItem;
import com.fastbee.media.model.VideoSessionInfo;
import com.fastbee.media.util.ZlmRtpUtils;
import com.fastbee.sip.domain.SipConfig;
import com.fastbee.sip.domain.SipDevice;
import com.fastbee.sip.server.cluster.SipSsrcManager;
import com.fastbee.sip.service.ISipSendRtpService;
import com.fastbee.sip.util.SipUtil;

/**
 * @author zhuangpengli
 */
@Slf4j
@Service
public class SendRtpServiceInpl implements ISipSendRtpService {
    @Autowired
    private ZlmRtpUtils zlmRtpUtils;
    @Autowired
    private SipSsrcManager sipSsrcManager;
    @Override
    public void startSendRtp(MediaServer mediaInfo, InviteMsgInfo inviteInfo, SendRtpItem sendRtpItem) {
        Map<String, Object> params = new HashMap<>(16);
        params.put("vhost", "__defaultVhost__");
        params.put("app", sendRtpItem.getApp());
        params.put("stream", sendRtpItem.getStream());
        params.put("ssrc", inviteInfo.getSsrc());
        params.put("src_port", sendRtpItem.getLocalPort());
        params.put("pt", sendRtpItem.getPt());
        params.put("use_ps", sendRtpItem.isUsePs() ? "1" : "0");
        params.put("only_audio", sendRtpItem.isOnlyAudio() ? "1" : "0");
        if (!sendRtpItem.isTcp()) {
            // udp模式下开启rtcp保活
            params.put("udp_rtcp_timeout", sendRtpItem.isRtcp() ? "500" : "0");
        }
        params.put("is_udp", sendRtpItem.isTcp() ? "0" : "1");
        if (sendRtpItem.isTcpActive()) {
            // params.put("recv_stream_id", sendRtpItem.getReceiveStream());
            if (sendRtpItem.getTimeout()  != null) {
                params.put("close_delay_ms", sendRtpItem.getTimeout());
            }
            zlmRtpUtils.startSendRtpPassive(mediaInfo, params);
        } else {
            params.put("dst_url", inviteInfo.getIp());
            params.put("dst_port", inviteInfo.getPort());
            zlmRtpUtils.startSendRtpStream(mediaInfo, params);
        }
    }

    @Override
    public VideoSessionInfo createRTPServer(SipConfig sipConfig, MediaServer mediaInfo, SipDevice device, VideoSessionInfo videoSessionInfo) {
        switch (videoSessionInfo.getType()) {
            case PLAY:
                videoSessionInfo.setSsrc(sipSsrcManager.getPlaySsrc(sipConfig.getDomainAlias()));
                videoSessionInfo.setStream(String.format("%s_%s_%s", SipUtil.PREFIX_PLAY, device.getDeviceSipId(), videoSessionInfo.getChannelId()));
                break;
            case PLAYRECORD:
                videoSessionInfo.setSsrc(sipSsrcManager.getPlaySsrc(sipConfig.getDomainAlias()));
                videoSessionInfo.setStream(String.format("%s_%s_%s", SipUtil.PREFIX_PLAYRECORD, device.getDeviceSipId(), videoSessionInfo.getChannelId()));
                break;
            case PLAYBACK:
                videoSessionInfo.setSsrc(sipSsrcManager.getPlayBackSsrc(sipConfig.getDomainAlias()));
                videoSessionInfo.setStream(videoSessionInfo.getSsrc());
                break;
            case DOWNLOAD:
                videoSessionInfo.setSsrc(sipSsrcManager.getPlayBackSsrc(sipConfig.getDomainAlias()));
                videoSessionInfo.setStream(videoSessionInfo.getSsrc());
                break;
        }
        int tcpMode;
        if ("TCP-PASSIVE".equalsIgnoreCase(device.getStreamMode())) {
            tcpMode = 1;
        } else if ("TCP-ACTIVE".equalsIgnoreCase(device.getStreamMode())) {
            tcpMode = 2;
        } else {
            tcpMode = 0;
        }
        int mediaPort = zlmRtpUtils.createRTPServer(mediaInfo, videoSessionInfo.getStream(), videoSessionInfo.getSsrc(), false, false, tcpMode);
        videoSessionInfo.setPort(mediaPort);
        return videoSessionInfo;
    }
}
