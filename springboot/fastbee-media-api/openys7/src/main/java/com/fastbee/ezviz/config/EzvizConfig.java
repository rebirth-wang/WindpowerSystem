package com.fastbee.ezviz.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 萤石云开放平台配置
 *
 * @author fastbee
 */
@Data
@ConfigurationProperties(prefix = "fastbee.media.ezviz")
public class EzvizConfig {

    /** 是否启用萤石云集成 */
    private boolean enabled = false;

    /** 萤石云开放平台 AppKey */
    private String appKey;

    /** 萤石云开放平台 AppSecret */
    private String appSecret;

    /** API 基础地址，默认为国内正式环境 */
    private String apiBase = "https://open.ys7.com";

    /** AccessToken 有效期（秒），默认7天 */
    private long tokenExpireSeconds = 604800L;

    /** HTTP 请求连接超时（毫秒） */
    private int connectTimeout = 5000;

    /** HTTP 请求读取超时（毫秒） */
    private int readTimeout = 10000;

}
