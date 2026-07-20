package com.fastbee.icc.model.ipms.deviceManage;

import java.util.List;

import com.dahuatech.icc.oauth.http.IccResponse;
import lombok.Data;

/**
 * @className SluiceChannelResponse
 * @Author 355079
 * @Date 2024/12/10
 * @Description 道闸通道信息全量查询返回结果
 */
@Data
public class SluiceChannelResponse extends IccResponse {
    private List<Data> data;
    @lombok.Data
    public static class Data {
        private String channelName;
        private String channelId;
    }
}
