package com.fastbee.icc.model.video.ptzControl;

import lombok.Data;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-10 10:02
 * @Description: 获取云台预置点接口请求参数
 */
@Data
public class GetPresetPointsRequest {
    private Data data;

    @lombok.Data
    public static class Data{
        private String channelId;
    }
}
