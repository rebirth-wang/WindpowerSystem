package com.fastbee.ezviz.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 萤石云 AccessToken 响应
 *
 * @author fastbee
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EzvizTokenResponse extends EzvizBaseResponse {

    /** 响应数据 */
    private TokenData data;

    @Data
    public static class TokenData {
        /** AccessToken 值 */
        private String accessToken;
        /** 有效期（毫秒） */
        private Long expireTime;
    }

}
