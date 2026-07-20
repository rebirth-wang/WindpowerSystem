package com.fastbee.icc.util;

import java.nio.charset.StandardCharsets;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-03-07 15:40
 * @Description:
 */
@Slf4j
public class RestTemplateUtil {

    /**
     * 构造RestTemplate
     * @return RestTemplate实例
     */
    public static RestTemplate getRestTemplate() {
        try {
            // 创建信任所有证书的SSL Context
            SSLContextBuilder sslContextBuilder = SSLContextBuilder.create()
                    .loadTrustMaterial(TrustAllStrategy.INSTANCE);
            
            // 创建SSL Connection Socket Factory
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslContextBuilder.build(),
                    NoopHostnameVerifier.INSTANCE
            );
            
            // 创建HttpClient
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create()
                            .setSSLSocketFactory(sslsf)
                            .build())
                    .build();
            
            // 创建RequestFactory
            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
            // 超时设置
            factory.setConnectionRequestTimeout(5000);
            factory.setConnectTimeout(5000);
            factory.setReadTimeout(5000);
            
            // 创建RestTemplate
            RestTemplate restTemplate = new RestTemplate(factory);
            // 解决中文乱码问题
            restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
            
            return restTemplate;
        } catch (Exception e) {
            log.error("RestTemplateUtil,getRestTemplate,exception:{}", e.getMessage(), e);
            throw new RuntimeException("Failed to create RestTemplate", e);
        }
    }
}
