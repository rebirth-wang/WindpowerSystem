package com.fastbee.sip.service;

import com.alibaba.fastjson2.JSONObject;

import com.fastbee.media.model.RecordList;
import com.fastbee.media.model.ZlmMediaServer;

public interface ISipCacheService {
    Long getCSEQ(String serverSipId);

    void updateMediaInfo(ZlmMediaServer mediaServerConfig);

    void setRecordList(String key, RecordList recordList);

    void recordDeviceStatus(String deviceId, String event, JSONObject status);

    void recordStreamStatus(String streamId, String event, JSONObject status);
}
