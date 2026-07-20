package com.fastbee.sip.service;

import com.alibaba.fastjson2.JSONObject;

import com.fastbee.media.domain.MediaServer;
import com.fastbee.media.model.StreamInfo;
import com.fastbee.media.model.VideoSessionInfo;

public interface IZmlHookService {
    JSONObject onHttpAccess(JSONObject json);
    JSONObject onPlay(JSONObject json);
    JSONObject onPublish(JSONObject json);
    JSONObject onStreamNoneReader(JSONObject json);
    JSONObject onStreamNotFound(JSONObject json);
    JSONObject onStreamChanged(JSONObject json);
    JSONObject onFlowReport(JSONObject json);
    JSONObject onRtpServerTimeout(JSONObject json);
    JSONObject onSendRtpStopped(JSONObject json);
    JSONObject onRecordMp4(JSONObject json);
    JSONObject onServerStarted(JSONObject json);
    JSONObject onServerKeepalive(JSONObject json);
    JSONObject onServerExited(JSONObject json);
    StreamInfo updateStream(VideoSessionInfo sinfo);
    StreamInfo buildStreamProxyUrl(MediaServer mediaInfo, String streamId) ;
    StreamInfo buildPushRtc(String app, String deviceId, String channelId);
}
