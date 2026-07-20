package com.fastbee.media.model;

import lombok.Data;

@Data
public class StreamContent {
    private String app;
    private String stream;
    private String ip;
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
    private String ts;
    private String https_ts;
    private String ws_ts;
    private String wss_ts;
    private String rtmp;
    private String rtmps;
    private String rtsp;
    private String rtsps;
    private String rtc;
    private String rtcs;
    private String mediaServerId;
    private MediaInfo mediaInfo;
    private String startTime;
    private String endTime;
    private Double duration;
    private DownloadFileInfo downLoadFilePath;
    private StreamContent transcodeStream;
    private double progress;
    private String key;
    private String serverId;

    public StreamContent(StreamInfo streamInfo) {
        if (streamInfo == null) {
            return;
        }
        this.app = streamInfo.getApp();
        this.stream = streamInfo.getStreamId();
        if (streamInfo.getFlv() != null) {
            this.flv = streamInfo.getFlv();
        }
        if (streamInfo.getHttps_flv() != null) {
            this.https_flv = streamInfo.getHttps_flv();
        }
        if (streamInfo.getWs_flv() != null) {
            this.ws_flv = streamInfo.getWs_flv();
        }
        if (streamInfo.getWss_flv() != null) {
            this.wss_flv = streamInfo.getWss_flv();
        }
        if (streamInfo.getFmp4() != null) {
            this.fmp4 = streamInfo.getFmp4();
        }
        if (streamInfo.getHttps_fmp4() != null) {
            this.https_fmp4 = streamInfo.getHttps_fmp4();
        }
        if (streamInfo.getWs_fmp4() != null) {
            this.ws_fmp4 = streamInfo.getWs_fmp4();
        }
        if (streamInfo.getWss_fmp4() != null) {
            this.wss_fmp4 = streamInfo.getWss_fmp4();
        }
        if (streamInfo.getHls() != null) {
            this.hls = streamInfo.getHls();
        }
        if (streamInfo.getHttps_hls() != null) {
            this.https_hls = streamInfo.getHttps_hls();
        }
        if (streamInfo.getWs_hls() != null) {
            this.ws_hls = streamInfo.getWs_hls();
        }
        if (streamInfo.getWss_hls() != null) {
            this.wss_hls = streamInfo.getWss_hls();
        }
        if (streamInfo.getTs() != null) {
            this.ts = streamInfo.getTs();
        }
        if (streamInfo.getHttps_ts() != null) {
            this.https_ts = streamInfo.getHttps_ts();
        }
        if (streamInfo.getWs_ts() != null) {
            this.ws_ts = streamInfo.getWs_ts();
        }
        if (streamInfo.getRtmp() != null) {
            this.rtmp = streamInfo.getRtmp();
        }
        if (streamInfo.getRtmps() != null) {
            this.rtmps = streamInfo.getRtmps();
        }
        if (streamInfo.getRtsp() != null) {
            this.rtsp = streamInfo.getRtsp();
        }
        if (streamInfo.getRtsps() != null) {
            this.rtsps = streamInfo.getRtsps();
        }
        if (streamInfo.getRtc() != null) {
            this.rtc = streamInfo.getRtc();
        }
        if (streamInfo.getRtcs() != null) {
            this.rtcs = streamInfo.getRtcs();
        }
        if (streamInfo.getMediaServer() != null) {
            this.mediaServerId = streamInfo.getMediaServer().getServerId();
        }

        this.mediaInfo = streamInfo.getMediaInfo();
        this.startTime = streamInfo.getStartTime();
        this.endTime = streamInfo.getEndTime();
        this.progress = streamInfo.getProgress();
        this.duration = streamInfo.getDuration();
        this.key = streamInfo.getKey();
        this.serverId = streamInfo.getServerId();

        if (streamInfo.getDownLoadFilePath() != null) {
            this.downLoadFilePath = streamInfo.getDownLoadFilePath();
        }
        if (streamInfo.getTranscodeStream() != null) {
            this.transcodeStream = new StreamContent(streamInfo.getTranscodeStream());
        }
    }

}
