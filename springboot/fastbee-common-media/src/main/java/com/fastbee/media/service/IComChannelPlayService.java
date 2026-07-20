package com.fastbee.media.service;

import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.enums.InviteType;
import com.fastbee.media.model.*;

public interface IComChannelPlayService {

    void startInvite(CommonChannel channel, InviteMsgInfo inviteInfo, Platform platform);

    void stopInvite(InviteType type, CommonChannel channel, String stream);

    StreamInfo playback(CommonChannel channel, String startTime, String stopTimek);

    void download(CommonChannel channel, String startTime, String stopTime, Integer downloadSpeed);

    Boolean stopPlay(CommonChannel channel);

    StreamInfo play(CommonChannel channel, Platform platform, Boolean record);
    StreamPlayUrl getUrl(String deviceId, String channelId);

    Boolean stopPlayback(CommonChannel channel, String stream);

    Boolean closeStream(CommonChannel channel, String stream, Boolean check);

    void stopDownload(CommonChannel channel, String stream);

    void playbackPause(CommonChannel channel, String streamId);

    void playbackResume(CommonChannel channel, String streamId);

    void playbackSeek(CommonChannel channel, String stream, long seekTime);

    void playbackSpeed(CommonChannel channel, String stream, Double speed);

    RecordList queryRecord(CommonChannel channel, String startTime, String endTime);
}
