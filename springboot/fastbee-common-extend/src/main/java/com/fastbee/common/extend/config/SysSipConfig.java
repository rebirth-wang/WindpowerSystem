package com.fastbee.common.extend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "sip")
public class SysSipConfig {
    String serverId;
    boolean enabled;
    String ip;
    Integer port;
    String domain;
    String id;
    String password;
    boolean log;
    String zlmRecordPath;
    String mp4MaxSecond;
    Integer playTimeout;
}
