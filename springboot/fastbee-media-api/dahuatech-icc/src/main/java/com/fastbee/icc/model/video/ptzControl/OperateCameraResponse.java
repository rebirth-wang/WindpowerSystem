package com.fastbee.icc.model.video.ptzControl;

import com.dahuatech.icc.oauth.http.IccResponse;
import lombok.Data;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-09 16:19
 * @Description: 云台镜头控制请求结果
 */
@Data
public class OperateCameraResponse extends IccResponse {
    private String desc;
    private Data data;

    @lombok.Data
    public static class Data{
        private String result;
        private LockUser lockUser;
        @lombok.Data
        public static class LockUser{
            private String userName;
            private String userLevel;
            private String lowerDomain;
        }
    }

}
