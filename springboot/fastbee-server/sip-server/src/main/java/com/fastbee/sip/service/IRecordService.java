package com.fastbee.sip.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

public interface IRecordService {
    JSONObject listServerRecord(String recordApi, Integer pageNum, Integer pageSize);
    JSONArray listServerRecordByDate(String recordApi, Integer year, Integer month, String app, String stream);
    JSONObject listServerRecordByStream(String recordApi, Integer pageNum, Integer pageSize, String app);
    JSONObject listServerRecordByApp(String recordApi, Integer pageNum, Integer pageSize);
    JSONObject listServerRecordByFile(String recordApi, Integer pageNum, Integer pageSize, String app, String stream, String startTime, String endTime);
    JSONObject listServerRecordByDevice(Integer pageNum, Integer pageSize, String deviceId, String channelId, String startTime, String endTime);
    JSONObject listServerRecordByChanelId(String serverId, String channelId, String startTime, String endTime, Integer pageNum, Integer pageSize);
    boolean startRecord(String stream);
    boolean stopRecord(String stream);
    boolean isRecording(String stream);
    JSONObject getMp4RecordFile(String stream,String period);
    JSONObject upload(String recordApi, String file);
}
