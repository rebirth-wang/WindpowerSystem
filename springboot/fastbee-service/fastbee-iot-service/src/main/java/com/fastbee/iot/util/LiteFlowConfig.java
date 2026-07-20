package com.fastbee.iot.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Author gx_ma
 * @Date: 2025/01/17/ 15:52
 * @description
 */
@Data
@Component
@ConfigurationProperties(prefix = "liteflow.rule-source-ext-data-map")
public class LiteFlowConfig {
    private String applicationName;
}
