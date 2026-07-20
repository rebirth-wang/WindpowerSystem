package com.fastbee.media.service;

import java.util.List;

import com.fastbee.media.domain.MediaServer;
import com.fastbee.media.model.InviteMsgInfo;
import com.fastbee.media.model.SendRtpItem;
import com.fastbee.media.model.VideoSessionInfo;

public interface ISendRtpServerService {

    SendRtpItem createSendRtpInfo(MediaServer mediaServer, String ip, Integer port, String ssrc, String requesterId,
                                  String deviceId, String channelId, Boolean isTcp, Boolean rtcp);

    SendRtpItem createSendRtpInfo(MediaServer mediaServer, String ip, Integer port, String ssrc, String platformId,
                                  String app, String stream, String channelId, Boolean tcp, Boolean rtcp);

    void update(SendRtpItem sendRtpItem);

    SendRtpItem queryByChannelId(String channelId, String targetId);

    SendRtpItem queryByCallId(String callId);

    List<SendRtpItem> queryByStream(String stream);

    SendRtpItem queryByStream(String stream, String targetId);

    void delete(SendRtpItem sendRtpInfo);

    void deleteByCallId(String callId);

    void deleteByStream(String Stream, String targetId);

    void deleteByChannel(String channelId, String targetId);

    List<SendRtpItem> queryAll();

    void startSendRtp(MediaServer mediaInfo, InviteMsgInfo inviteInfo, SendRtpItem sendRtpItem);

    boolean isChannelSendingRTP(String channelId);

    List<SendRtpItem> queryByChannelId(String channelId);

    void deleteByStream(String stream);

    int getNextPort(MediaServer mediaServer);

    VideoSessionInfo createRTPServer(MediaServer mediaInfo, VideoSessionInfo videoSessionInfo);
}
