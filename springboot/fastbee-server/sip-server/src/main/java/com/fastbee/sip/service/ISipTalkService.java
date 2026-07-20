package com.fastbee.sip.service;

import com.fastbee.media.model.StreamInfo;

public interface ISipTalkService {
    StreamInfo getBroadcastUrl(String deviceId, String channelId);
    String broadcast(String deviceId, String channelId);
    void broadcastStop(String deviceId, String channelId);
    boolean broadcastInUse(String deviceId, String channelId);
}
