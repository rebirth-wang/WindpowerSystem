package com.fastbee.icc.model.event.eventSubcribe;

import java.util.List;
import java.util.Map;

import com.dahuatech.icc.oauth.http.IccResponse;
import lombok.Data;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-19 11:49
 * @Description: 事件订阅列表查询返回参数
 */
@Data
public class SubscribeListResponse extends IccResponse {
    private Data data;
    @lombok.Data
    public static class Data{
        /** 订阅列表 */
        private Map<String, List<SubscribeInfo>> subscribeMap;
        @lombok.Data
        public static class SubscribeInfo{
            /** 类型列表 */
            private List<String> types;
            /** 设备和通道列表 */
            private List<String> nodeCodes;
            /** 组织列表 */
            private List<String> orgs;
            /** 报警等级列表 */
            private List<Integer> grades;
            /** 级联订阅 */
            private Integer domainSubscribe;
            /** 是否过预案才转发 */
            private Integer scheme;
            /** 事件类型 */
            private Integer eventType;
            /** 监听地址 */
            private String monitor;
            /** 监听类型,固定值为：url */
            private String monitorType;
        }
    }
}
