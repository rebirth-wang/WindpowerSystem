package com.fastbee.icc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 大华ICC平台连接配置
 *
 * <p>在 application.yml 中配置：
 * <pre>
 * fastbee:
 *   media:
 *     icc:
 *       enabled: true
 *       clientId: your_client_id
 *       clientSecret: your_client_secret
 *       username: your_username
 *       password: your_password
 *       host: icc_platform_ip
 *       httpsPort: 443
 *       httpPort: 80
 * </pre>
 *
 * @author fastbee
 */
@Data
@ConfigurationProperties(prefix = "fastbee.media.icc")
public class PlatformConfig {

    /** 是否启用大华ICC集成 */
    private boolean enabled = false;

    /** 凭证ID（自定义，配合申请凭证流程申请） */
    private String clientId = "CompanyName";

    /** 凭证密钥 */
    private String clientSecret = "";

    /** 平台登录用户名 */
    private String username = "";

    /** 平台登录密码 */
    private String password = "";

    /** 平台IP地址 */
    private String host = "127.0.0.1";

    /** HTTPS端口，默认443 */
    private String httpsPort = "443";

    /** HTTP端口，默认80（需运维中心开启http调试模式后才支持） */
    private String httpPort = "80";

    /** 连接超时（毫秒），-1代表不限制 */
    private Long connectionTimeout = -1L;

    /** 读取超时（毫秒），-1代表不限制 */
    private Long readTimeout = -1L;

    /** Token缓存有效期（秒），默认7200秒（2小时），需小于平台实际过期时间 */
    private long tokenExpireSeconds = 7200L;
}
