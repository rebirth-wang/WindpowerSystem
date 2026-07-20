package com.fastbee.sip.server;

import com.fastbee.sip.domain.SipDevice;

/**
 * @author zhuangpengli
 */
public interface IRtspCmd {
    void playPause(SipDevice device, String channelId, String streamId);
    void playReplay(SipDevice device, String channelId, String streamId);
    void playBackSeek(SipDevice device, String channelId, String streamId, Long seektime);
    void playBackSpeed(SipDevice device, String channelId, String streamId, Double speed);
}
