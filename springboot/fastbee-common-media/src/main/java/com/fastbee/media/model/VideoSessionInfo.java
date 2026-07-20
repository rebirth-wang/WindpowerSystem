package com.fastbee.media.model;

import lombok.Builder;
import lombok.Data;

import com.fastbee.media.enums.SessionType;

@Data
@Builder
public class VideoSessionInfo {
    //流基本信息
    private Integer sessionId;
    private Integer previewHandle;
    private String mediaServerId;
    private String deviceId;
    private String channelId;
    private String stream;
    private String app;
    private String ssrc;
    private String mediaServerIp;
    private Integer port;
    private String streamMode;
    private SessionType type;
    //流状态
    private Boolean pushing = false;
    private Boolean recording = false;
    private Integer onPlaytime = 0;
    private Integer player;
    private Boolean videoRecord;
    private Boolean enableFmp4;
    private Boolean enableHls;
    private Boolean enableRtmp;
    private Boolean enableRtsp;

    //录像信息
    String startTime;
    String endTime;
    Integer downloadSpeed;

    //截图信息
    String shotTime;
    String shotPath;
}
