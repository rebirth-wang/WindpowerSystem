package com.fastbee.bridge.model;

import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * mqtt桥接配置表对象 mqtt_client
 *
 * @author gx_ma
 * @date 2024-06-03
 */
@Data
public class MqttClient
{
    /** 连接器名称 */
    @Excel(name = "连接器名称")
    private String name;

    /** MQTT服务地址（broker.emqx.io:1883） */
    @Excel(name = "MQTT服务地址", readConverterExp = "b=roker.emqx.io:1883")
    private String hostUrl;

    /** 用户名 */
    @Excel(name = "用户名")
    private String username;

    /** 密码 */
    @Excel(name = "密码")
    private String password;

    /** 客户端ID */
    @Excel(name = "客户端ID")
    private String clientId;

    /**
     * 超时时间
     */
    private int timeout = 30;
    /**
     * 保持连接数
     */
    private int keepalive = 60;

    // 0:default 0  3:MQTT_VERSION_3_1 3  4:MQTT_VERSION_3_1_1
    private int version = 0;

    private boolean automaticReconnect = true;
    /**是否清除session*/
    private boolean clearSession = true;;
    /**是否共享订阅*/
    private boolean isShared;
    /**分组共享订阅*/
    private boolean isSharedGroup;
}
