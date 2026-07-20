package com.fastbee.sip.service;

import com.fastbee.media.domain.MediaServer;
import com.fastbee.media.model.InviteMsgInfo;
import com.fastbee.media.model.SendRtpItem;
import com.fastbee.media.model.VideoSessionInfo;
import com.fastbee.sip.domain.SipConfig;
import com.fastbee.sip.domain.SipDevice;

/**
 * @author zhuangpengli
 */
public interface ISipSendRtpService {
    void startSendRtp(MediaServer mediaInfo, InviteMsgInfo inviteInfo, SendRtpItem sendRtpItem);
    VideoSessionInfo createRTPServer(SipConfig sipConfig, MediaServer mediaInfo, SipDevice device, VideoSessionInfo videoSessionInfo);
}
