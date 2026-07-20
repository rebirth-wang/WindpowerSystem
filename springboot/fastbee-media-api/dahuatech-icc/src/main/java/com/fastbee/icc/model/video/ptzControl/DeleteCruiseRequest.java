package com.fastbee.icc.model.video.ptzControl;

import lombok.Data;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-09 15:09
 * @Description: 删除巡航线请求参数
 */
@Data
public class DeleteCruiseRequest {
    private Data data;

    @lombok.Data
    public static  class Data{
        /** 通道编码 */
        private String channelId;
    }
}
