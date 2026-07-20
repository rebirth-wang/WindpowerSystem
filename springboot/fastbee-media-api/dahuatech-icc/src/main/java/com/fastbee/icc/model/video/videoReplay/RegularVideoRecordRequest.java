package com.fastbee.icc.model.video.videoReplay;

import lombok.Data;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-07 10:25
 * @Description: 查询普通录像信息列表请求参数
 */
@Data
public class RegularVideoRecordRequest {

    private Data data;

    @lombok.Data
    public static class Data{
        private String channelId;
        private String recordSource;
        private String startTime;
        private String endTime;
        private String streamType;
        private String recordType;
    }

}
