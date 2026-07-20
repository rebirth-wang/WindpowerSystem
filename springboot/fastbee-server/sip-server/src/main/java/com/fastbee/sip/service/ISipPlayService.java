package com.fastbee.sip.service;

import java.util.List;

import com.fastbee.media.model.*;
import com.fastbee.sip.domain.SipDevice;

public interface ISipPlayService {

    StreamInfo play(SipDevice dev, String channelId, Boolean record);
    StreamInfo play(String deviceId, String channelId, Boolean record);

    StreamPlayUrl getUrl(String deviceId, String channelId);

    Boolean pushing(String deviceId, String streamId);

    String screenshot(String deviceId, String channelId);
    RecordList listDevRecord(SipDevice dev, String channelId, String startTime, String endTime);
    RecordList listDevRecord(String deviceId, String channelId, String startTime, String endTime);
    List<RecordItem> listRecord(String deviceId, String channelId);
    StreamInfo download(SipDevice dev, String channelId,
                        String startTime, String endTime, Integer downloadSpeed);
    StreamInfo download(String deviceId, String channelId,
                    String startTime, String endTime, Integer downloadSpeed);
    StreamInfo playback(SipDevice dev, String channelId, String startTime, String endTime);
    StreamInfo playback(String deviceId, String channelId, String startTime, String endTime);
    Boolean closeStream(SipDevice dev, String channelId, String streamId, Boolean check);
    Boolean closeStream(String deviceId, String channelId, String streamId, Boolean check);

    Boolean playbackPause(String deviceId, String channelId, String streamId);

    Boolean playbackReplay(String deviceId, String channelId, String streamId);

    Boolean playbackSeek(String deviceId, String channelId, String streamId, Long seektime);

    Boolean playbackSpeed(String deviceId, String channelId, String streamId, Double speed);
}
