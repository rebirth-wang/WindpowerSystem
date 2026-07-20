package com.fastbee.media.service;

import java.util.List;
import java.util.Set;

import com.fastbee.media.domain.MediaServer;
import com.fastbee.media.model.MediaInfo;
import com.fastbee.media.model.RecordList;
import com.fastbee.media.model.ZlmMediaServer;

public interface IMediaCacheService {
    Long getCSEQ(String serverSipId);

    void updateMediaInfo(ZlmMediaServer mediaServerConfig);

    void setRecordList(String key, RecordList recordList);

    void addStream(MediaServer mediaServerItem, String type, String app, String streamId, MediaInfo item);
    /**
     * 移除流信息从redis
     * @param mediaServerId
     * @param app
     * @param streamId
     */
    void removeStream(String mediaServerId, String type, String app, String streamId);


    /**
     * 移除流信息从redis
     * @param mediaServerId
     */
    void removeStream(String mediaServerId, String type);

    List<MediaInfo> getStreams(String mediaServerId, String pull);
    MediaInfo getStreamInfo(String app, String streamId, String mediaServerId);
    MediaInfo getProxyStream(String app, String streamId);

    void addPushListItem(String app, String stream, MediaInfo param);

    MediaInfo getPushListItem(String app, String stream);

    void addCount(String mediaServerId);

    void removeCount(String mediaServerId);

    Set<Object> getMediaServerLoadCount();
}
