package com.fastbee.media.model;

import lombok.Data;

@Data
public class StreamPlayUrl {
    private String flv;
    private String https_flv;
    private String ws_flv;
    private String wss_flv;
    private String fmp4;
    private String https_fmp4;
    private String ws_fmp4;
    private String wss_fmp4;
    private String hls;
    private String https_hls;
    private String ws_hls;
    private String wss_hls;
    private String rtmp;
    private String rtsp;
    private String rtc;
    private String rtcs;
}
