package com.fastbee.media.model;

import java.io.Serializable;
import java.util.Objects;

import lombok.Data;

import com.fastbee.media.domain.MediaServer;

@Data
public class StreamInfo implements Serializable, Cloneable {
    private String app;
    private String streamId;
    private String deviceId;
    private String channelId;
    private String ip;
    private String playurl;
    private String ssrc;

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

    private MediaServer mediaServer;
    private MediaInfo mediaInfo;

    private String startTime;
    private String endTime;
    private Double duration;
    private double progress;
    private DownloadFileInfo downLoadFilePath;
    private String callId;
    private boolean pause;
    private int originType;
    private String originTypeStr;
    private StreamInfo transcodeStream;

    private String serverId;
    private String key;
    private Object tracks;

    public StreamInfo() {
    }

    public StreamInfo(String deviceId, String channelId, String streamId, String ssrc) {
        this.deviceId = deviceId;
        this.channelId = channelId;
        this.streamId = streamId;
        this.ssrc = ssrc;
    }

    public StreamInfo(MediaServer mediaInfo, String app, String stream) {
        this.mediaServer = mediaInfo;
        this.app = app;
        this.streamId = stream;
        setRtmp(mediaInfo.getIp(), mediaInfo.getPortRtmp(), mediaInfo.getPortRtmp(), app, stream, "");
        setRtsp(mediaInfo.getIp(), mediaInfo.getPortRtsp(), mediaInfo.getPortRtsp(), app, stream, "");
        setFlv(mediaInfo.getIp(), mediaInfo.getPortHttp(), mediaInfo.getPortHttps(), app, stream, "");
        setFmp4(mediaInfo.getIp(), mediaInfo.getPortHttp(), mediaInfo.getPortHttps(), app, stream, "");
        setHls(mediaInfo.getIp(), mediaInfo.getPortHttp(), mediaInfo.getPortHttps(), app, stream, "");
        setTs(mediaInfo.getIp(), mediaInfo.getPortHttp(), mediaInfo.getPortHttps(), app, stream, "");
        setRtc(mediaInfo.getIp(), mediaInfo.getPortHttp(), mediaInfo.getPortHttps(), app, stream, "");
        if (Objects.equals(mediaInfo.getProtocol(), "http")) {
            this.playurl = this.flv.toString();
        } else if (Objects.equals(mediaInfo.getProtocol(), "https")) {
            this.playurl = this.https_flv.toString();
        }
    }

    public StreamInfo build(MediaServer mediaInfo, String app, String stream) {
        this.mediaServer = mediaInfo;
        this.app = app;
        this.streamId = stream;
        setRtmp(mediaInfo.getIp(), mediaInfo.getPortRtmp(), mediaInfo.getPortRtmp(), app, stream, "");
        setRtsp(mediaInfo.getIp(), mediaInfo.getPortRtsp(), mediaInfo.getPortRtsp(), app, stream, "");
        setFlv(mediaInfo.getIp(), mediaInfo.getPortHttp(), mediaInfo.getPortHttps(), app, stream, "");
        setFmp4(mediaInfo.getIp(), mediaInfo.getPortHttp(), mediaInfo.getPortHttps(), app, stream, "");
        setHls(mediaInfo.getIp(), mediaInfo.getPortHttp(), mediaInfo.getPortHttps(), app, stream, "");
        setTs(mediaInfo.getIp(), mediaInfo.getPortHttp(), mediaInfo.getPortHttps(), app, stream, "");
        setRtc(mediaInfo.getIp(), mediaInfo.getPortHttp(), mediaInfo.getPortHttps(), app, stream, "");
        if (Objects.equals(mediaInfo.getProtocol(), "http")) {
            this.playurl = this.flv.toString();
        } else if (Objects.equals(mediaInfo.getProtocol(), "https")) {
            this.playurl = this.https_flv.toString();
        }
        return this;
    }

    public void setRtmp(String host, Long port, Long sslPort, String app, String stream, String callIdParam) {
        String file = String.format("%s/%s%s", app, stream, callIdParam);
        if (port != null && port > 0) {
            this.rtmp = String.format("rtmp://%s:%d/%s", host, port, file);
        }
        if (sslPort != null && sslPort > 0) {
            this.rtmps = String.format("rtmps://%s:%d/%s", host, sslPort, file);
        }
    }

    public void setRtsp(String host, Long port, Long sslPort, String app, String stream, String callIdParam) {
        String file = String.format("%s/%s%s", app, stream, callIdParam);
        if (port != null && port > 0) {
            this.rtsp = String.format("rtsp://%s:%d/%s", host, port, file);
        }
        if (sslPort != null && sslPort > 0) {
            this.rtsps = String.format("rtsps://%s:%d/%s", host, sslPort, file);
        }
    }

    public void setFlv(String host, Long port, Long sslPort, String app, String stream, String callIdParam) {
        String file = String.format("%s/%s.live.flv%s", app, stream, callIdParam);
        if (port > 0) {
            this.flv = String.format("http://%s:%d/%s", host, port, file);
        }
        this.ws_flv = String.format("ws://%s:%d/%s", host, port, file);
        if (sslPort > 0) {
            this.https_flv = String.format("https://%s:%d/%s", host, sslPort, file);
            this.wss_flv = String.format("wss://%s:%d/%s", host, sslPort, file);
        }
    }

    public void setFmp4(String host, Long port, Long sslPort, String app, String stream, String callIdParam) {
        String file = String.format("%s/%s.live.mp4%s", app, stream, callIdParam);
        if (port > 0) {
            this.fmp4 = String.format("http://%s:%d/%s", host, port, file);
            this.ws_fmp4 = String.format("ws://%s:%d/%s", host, port, file);
        }
        if (sslPort > 0) {
            this.https_fmp4 = String.format("https://%s:%d/%s", host, sslPort, file);
            this.wss_fmp4 = String.format("wss://%s:%d/%s", host, sslPort, file);
        }
    }

    public void setHls(String host, Long port, Long sslPort, String app, String stream, String callIdParam) {
        String file = String.format("%s/%s/hls.m3u8%s", app, stream, callIdParam);
        if (port > 0) {
            this.hls = String.format("http://%s:%d/%s", host, port, file);
            this.ws_hls = String.format("ws://%s:%d/%s", host, port, file);
        }
        if (sslPort > 0) {
            this.https_hls = String.format("https://%s:%d/%s", host, sslPort, file);
            this.wss_hls = String.format("wss://%s:%d/%s", host, sslPort, file);
        }
    }

    public void setTs(String host, Long port, Long sslPort, String app, String stream, String callIdParam) {
        String file = String.format("%s/%s.live.ts%s", app, stream, callIdParam);
        if (port > 0) {
            this.ts = String.format("http://%s:%d/%s", host, port, file);
            this.ws_ts = String.format("ws://%s:%d/%s", host, port, file);
        }
        if (sslPort > 0) {
            this.https_ts = String.format("https://%s:%d/%s", host, sslPort, file);
            this.wss_ts = String.format("wss://%s:%d/%s", host, sslPort, file);
        }
    }

    public void setRtc(String host, Long port, Long sslPort, String app, String stream, String callIdParam) {
        if (callIdParam != null) {
            callIdParam = Objects.equals(callIdParam, "") ? callIdParam : callIdParam.replace("?", "&");
        }
        String file = String.format("index/api/webrtc?app=%s&stream=%s&type=play%s", app, stream, callIdParam);
        if (port > 0) {
            this.rtc = String.format("http://%s:%d/%s", host, port, file);
        }
        if (sslPort > 0) {
            this.rtcs = String.format("https://%s:%d/%s", host, sslPort, file);
        }
    }

}
