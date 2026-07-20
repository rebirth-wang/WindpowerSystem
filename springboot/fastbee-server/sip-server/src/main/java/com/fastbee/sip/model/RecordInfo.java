package com.fastbee.sip.model;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

@Data
public class RecordInfo {
    private String app;
    private String stream;
    private String fileName;
    private String filePath;
    private long fileSize;
    private String folder;
    private String url;
    /**
     * 单位毫秒
     */
    private long startTime;
    /**
     * 单位毫秒
     */
    private double timeLen;
    private String params;

    public static RecordInfo getInstance(JSONObject hookParam) {
        RecordInfo recordInfo = new RecordInfo();
        recordInfo.setApp(hookParam.getString("app"));
        recordInfo.setStream(hookParam.getString("stream"));
        recordInfo.setFileName(hookParam.getString("file_name"));
        recordInfo.setUrl(hookParam.getString("url"));
        recordInfo.setFolder(hookParam.getString("folder"));
        recordInfo.setFilePath(hookParam.getString("file_path"));
        recordInfo.setFileSize(hookParam.getLong("file_size"));
        recordInfo.setStartTime(hookParam.getLong("start_time") * 1000);
        recordInfo.setTimeLen(hookParam.getDouble("time_len") * 1000);
        return recordInfo;
    }

    @Override
    public String toString() {
        return "RecordInfo{" +
                "文件名称='" + fileName + '\'' +
                ", 文件路径='" + filePath + '\'' +
                ", 文件大小=" + fileSize +
                ", 开始时间=" + startTime +
                ", 时长=" + timeLen +
                ", params=" + params +
                '}';
    }
}
