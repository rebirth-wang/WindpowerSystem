package com.fastbee.media.service;

import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.model.RecordList;
import com.fastbee.media.model.StreamInfo;

/**
 * 资源能力接入-录像回放
 */
public interface IPlaybackService {
    StreamInfo playback(CommonChannel channel, String startTime, String stopTime);

    Boolean stopPlayback(CommonChannel channel, String stream);

    Boolean playbackPause(CommonChannel channel, String stream);

    Boolean playbackResume(CommonChannel channel, String stream);

    Boolean playbackSeek(CommonChannel channel, String stream, Long seekTime);

    Boolean playbackSpeed(CommonChannel channel, String stream, Double speed);

    RecordList queryRecord(CommonChannel channel, String startTime, String stopTime);

    StreamInfo download(CommonChannel channel, String startTime, String endTime, Integer downloadSpeed);
}
