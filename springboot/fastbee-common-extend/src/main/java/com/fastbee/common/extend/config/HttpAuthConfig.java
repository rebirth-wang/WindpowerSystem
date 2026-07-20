package com.fastbee.common.extend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "server.http.auth")
@Data
public class HttpAuthConfig {
    private String type;
    private String username;
    private String password;
}
