package com.fastbee.media.service;

import com.fastbee.media.domain.MediaServer;
import com.fastbee.media.model.StreamInfo;

public interface IMediaService {
    StreamInfo getStreamInfoByAppAndStreamWithCheck(String app, String stream, String mediaServerId, String addr, boolean authority);

    StreamInfo getStreamInfoByAppAndStreamWithCheck(String app, String stream, String mediaServerId, boolean authority);

    StreamInfo getStreamInfoByAppAndStream(MediaServer mediaServerItem, String app, String stream, Object tracks, String callId);

    StreamInfo getStreamInfoByAppAndStream(MediaServer mediaInfo, String app, String stream, Object tracks, String addr, String callId);
}
