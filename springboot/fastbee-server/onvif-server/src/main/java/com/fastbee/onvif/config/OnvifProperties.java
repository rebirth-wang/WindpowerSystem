package com.fastbee.onvif.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ONVIF 服务器配置属性
 * 从 application.yml 的 onvif 前缀读取
 *
 * <pre>
 * onvif:
 *   server-id: fastbee        # WebSocket 鉴权 Server ID
 *   heartbeat-interval: 30    # 心跳检测间隔（秒），0 表示禁用
 *   heartbeat-timeout: 90     # 心跳超时时间（秒），超时后断开连接
 * </pre>
 *
 * @author fastbee
 */
@Data
@Component
@ConfigurationProperties(prefix = "onvif")
public class OnvifProperties {

    /** WebSocket 鉴权服务器 ID，客户端连接时需携带此 ID */
    private String serverId = "fastbee";

    /** 心跳检测间隔（秒），默认 30 秒，0 表示禁用心跳 */
    private int heartbeatInterval = 30;

    /** 心跳超时时间（秒），超过此时间未收到响应则断开连接，默认 90 秒 */
    private int heartbeatTimeout = 90;

    /** Enable local WS-Discovery scan when the application starts. */
    private boolean scanOnStartup = true;

    /** Enable scheduled local WS-Discovery scan. */
    private boolean scheduledScanEnabled = true;

    /** WS-Discovery receive timeout in milliseconds. */
    private int discoveryTimeout = 10000;

    /** Sync discovered ONVIF channels into iot_device and common_channel. */
    private boolean syncToIotEnabled = true;

    /** Optional default ONVIF username used to enrich discovered devices. */
    private String defaultUsername;

    /** Optional default ONVIF password used to enrich discovered devices. */
    private String defaultPassword;
}
