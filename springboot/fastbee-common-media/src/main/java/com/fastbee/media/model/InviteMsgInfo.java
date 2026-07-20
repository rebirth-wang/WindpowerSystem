package com.fastbee.media.model;

import lombok.Data;

@Data
public class InviteMsgInfo {
    private String requestId;
    private String targetChannelId;
    private String sourceChannelId;
    // play playback
    private String sessionName;
    private String sessionId;
    private String sessionVersion;
    private String ssrc;
    private boolean tcp;
    private boolean tcpActive;
    private String callId;
    private String startTime;
    private String stopTime;
    private String downloadSpeed;
    private String originIp;
    private String ip;
    private int port;
}
