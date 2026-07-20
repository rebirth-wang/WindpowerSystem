package com.fastbee.icc.model.brm.person;

import com.dahuatech.icc.oauth.http.IccResponse;
import lombok.Data;

/**
 * program:java-sdk-demo
 * @Author: 355079
 * @Date:2024-03-12 19:16
 * @Description: 生成人员id返回结果
 */
@Data
public class PersonGenerateIdResponse extends IccResponse {

    private GenIdData data;

    @Data
    public static class GenIdData {
        /** 人员id */
        private Long id;

    }
}
