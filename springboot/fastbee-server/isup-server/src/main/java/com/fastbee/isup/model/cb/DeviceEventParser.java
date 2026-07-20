package com.fastbee.isup.model.cb;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DeviceEventParser {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static DeviceEventBase parse(String json) throws Exception {
        JsonNode root = mapper.readTree(json);
        String eventType = root.path("eventType").asText();

        switch (eventType) {
            case "faceCapture":
                return JSON.parseObject(json,FaceCaptureEvent.class);
            case "GPSUpload":
                return JSON.parseObject(json,GPSUploadEvent.class);
            case "alarmResult":
                return JSON.parseObject(json,AlarmResultEvent.class);
            default:
                // 未知类型默认解析为基础类
                return JSON.parseObject(json,DeviceEventBase.class);
        }
    }
}

