package com.fastbee.common.extend.core.domin.notify.config;

import lombok.Data;

/**
 * @author fastb
 * @version 1.0
 * @description: 钉钉渠道应用配置类
 * @date 2024-01-12 17:50
 */
@Data
public class DingTalkConfigParams {

    private String appKey;

    private String appSecret;

    private String agentId;

    private String webHook;
}
