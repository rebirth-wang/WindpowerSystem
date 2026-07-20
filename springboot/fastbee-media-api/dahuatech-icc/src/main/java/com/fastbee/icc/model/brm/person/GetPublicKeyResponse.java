package com.fastbee.icc.model.brm.person;

import com.dahuatech.icc.oauth.http.IccResponse;
import lombok.Data;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-03-19 17:07
 * @Description: 获取RSA加密公钥
 */
@Data
public class GetPublicKeyResponse extends IccResponse {
    /** 返回数据对象 */
    private KeyData data;
    @Data
    public static class KeyData{
        /** 私钥key值 */
        private String key;
        /** 公钥 */
        private String publicKey;

    }
}
