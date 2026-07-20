package com.fastbee.common.extend.core.card;

import java.util.List;

import lombok.Data;

/**
 * @author zzy
 * @description: 中国移动获取token返回结果类
 * @date 2025-11-19 11:17
 */
@Data
public class ChinaMobileTokenResult {

    private String status;

    private String message;

    private List<TokenResult> result;

    @Data
    public static class TokenResult {

        private String token;
        /**
         * 秒
         */
        private String ttl;
    }
}
