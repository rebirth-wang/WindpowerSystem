package com.fastbee.mqttclient;

import javax.net.ssl.SSLContext;

import jakarta.annotation.PreDestroy;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.pool.PoolConcurrencyPolicy;
import org.apache.hc.core5.pool.PoolReusePolicy;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    private CloseableHttpClient httpClient;

    @Bean
    public RestTemplate restTemplate(
            @Value("${rest-template.max-connections}") int maxConn,
            @Value("${rest-template.max-per-route}") int maxPerRoute,
            @Value("${rest-template.connection-timeout}") int connTimeout,
            @Value("${rest-template.read-timeout}") int readTimeout) {

        // 参数校验
        if (maxConn <= 0 || maxPerRoute <= 0 || connTimeout < 0 || readTimeout < 0) {
            throw new IllegalArgumentException("Invalid configuration values provided.");
        }

        try {
            SSLContext sslContext = SSLContexts.createSystemDefault();

            SocketConfig socketConfig = SocketConfig.custom()
                    .setSoTimeout(Timeout.ofMilliseconds(readTimeout))
                    .build();
            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                    .setSocketTimeout(Timeout.ofMilliseconds(readTimeout))
                    .setConnectTimeout(Timeout.ofMilliseconds(connTimeout))
                    .setTimeToLive(TimeValue.ofMinutes(10))
                    .build();

            PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                    .setDefaultSocketConfig(socketConfig)
                    .setPoolConcurrencyPolicy(PoolConcurrencyPolicy.STRICT)
                    .setConnPoolPolicy(PoolReusePolicy.LIFO)
                    .setDefaultConnectionConfig(connectionConfig)
                    .setMaxConnTotal(maxConn)
                    .setMaxConnPerRoute(maxPerRoute)
                    .build();

            httpClient = HttpClients.custom()
                    .setConnectionManager(connectionManager)
                    .build();

            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
            factory.setHttpClient(httpClient);
            factory.setReadTimeout(readTimeout);

            return new RestTemplate(factory); // 正确传入 factory

        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize RestTemplate", e);
        }
    }

    @PreDestroy
    public void destroy() {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (Exception ignored) {
                // 日志可选：logger.warn("Error closing HttpClient", ignored);
            }
        }
    }
}
