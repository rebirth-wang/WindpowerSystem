package com.fastbee.icc.model.event.eventQuery;

import com.dahuatech.icc.oauth.http.IccResponse;
import lombok.Data;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-06-11 13:54
 * @Description: alarm事件统计返回结果
 */
@Data
public class AlarmRecordCountResponse extends IccResponse {
    private TotalInfo data;
    @Data
    public static class TotalInfo{
        private Integer value;
    }
}
