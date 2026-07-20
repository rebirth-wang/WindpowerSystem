package com.fastbee.sip.server;

import com.fastbee.media.enums.SessionType;
import com.fastbee.media.model.VideoSessionInfo;
import com.fastbee.sip.domain.SipDevice;
import com.fastbee.sip.model.InviteInfo;

public interface ISipCmd {
    VideoSessionInfo playStreamCmd(SipDevice device, String channelId, Boolean record);
    VideoSessionInfo playScreenshotCmd(SipDevice device, String channelId, String fileName);
    VideoSessionInfo playbackStreamCmd(SipDevice device, String channelId, String startTime, String endTime);
    VideoSessionInfo downloadStreamCmd(SipDevice device, String channelId, String startTime, String endTime, int downloadSpeed);
    Boolean streamByeCmd(SipDevice device, String channelId, String stream, String ssrc, Boolean check);
    Boolean streamByeCmd(String deviceId, String channelId, String stream, String ssrc, Boolean check);
    void streamByeCmd(SipDevice device, String channelId, SessionType type, Boolean check);
    void streamByeCmdByInvite(SipDevice device, String channelId, InviteInfo invite);
    void audioBroadcastCmd(SipDevice device, String channelId);
}
