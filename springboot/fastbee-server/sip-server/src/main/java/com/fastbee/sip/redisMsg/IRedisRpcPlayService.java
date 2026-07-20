package com.fastbee.sip.redisMsg;

import java.util.List;

import com.fastbee.common.extend.core.media.ErrorCallback;
import com.fastbee.media.enums.InviteType;
import com.fastbee.media.model.Preset;
import com.fastbee.media.model.PtzInput;
import com.fastbee.media.model.RecordList;
import com.fastbee.media.model.StreamInfo;

public interface IRedisRpcPlayService {

    StreamInfo play(String serverId, String channelId, Boolean record, ErrorCallback<StreamInfo> callback);

    Boolean stop(String serverId, InviteType type, String channelId, String stream);

    Boolean closeStream(String deviceId, String channelId, String streamId, Boolean check);

    StreamInfo playback(String serverId, String channelId, String startTime, String endTime, ErrorCallback<StreamInfo> callback);

    Boolean playbackPause(String serverId, String channelId, String streamId);

    Boolean playbackResume(String serverId, String channelId, String streamId);

    Boolean playbackSeek(String serverId, String channelId, String streamId, Long seektime);

    Boolean playbackSpeed(String serverId, String channelId, String streamId, Double speed);

    StreamInfo download(String serverId, String channelId, String startTime, String endTime, int downloadSpeed, ErrorCallback<StreamInfo> callback);

    RecordList queryRecordInfo(String serverId, String channelId, String startTime, String endTime, ErrorCallback<RecordList> callback);

    Boolean ptz(String serverId, String channelId, PtzInput imput);

    Boolean frontEndCommand(String serverId, String channelId, int cmdCode, int parameter1, int parameter2, int combindCode2);

    List<Preset> queryPresetList(String serverId, String channelId, ErrorCallback<List<Preset>> callback);

    void playPush(String serverId, Integer id, ErrorCallback<StreamInfo> callback);

    void playProxy(String serverId, int id, ErrorCallback<StreamInfo> callback);

    void stopProxy(String serverId, int id);

//    DownloadFileInfo getRecordPlayUrl(String serverId, Integer recordId);

    String audioBroadcast(String serverId, String deviceId, String channelDeviceId, Boolean broadcastMode);
}
