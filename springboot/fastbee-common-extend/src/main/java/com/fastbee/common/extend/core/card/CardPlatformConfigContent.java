package com.fastbee.common.extend.core.card;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author zzy
 * @description: 物联网卡平台配置信息
 * @date 2025-11-11 17:41
 */
@Data
public class CardPlatformConfigContent {


    /**
     * API 基础 URL 移动、电信、联通
     */
    private String apiBaseUrl;

    private String apiKey;

    private String apiSecret;

    /**
     * 应用密钥 联通
     */
    private String appSecret;


    /**
     * API 版本 移动 比如 v5
     */
    private String apiVersion;

    /**
     * API 版本 联通
     */
    private String version;

    /**
     * 移动
     */
    private String appid;

    /**
     * 移动 密码
     */
    private String password;

    /**
     * 移动 trans id
     */
    @JsonProperty("transid")
    private String transId;

    /**
     * 开放标识 联通
     */
    private String openId;


    /**
     * app key 电信
     */
    private String appKey;

    /**
     * 秘密密钥 电信
     */
    private String secretKey;

    /**
     * 账户
     */
    private String account;

}
