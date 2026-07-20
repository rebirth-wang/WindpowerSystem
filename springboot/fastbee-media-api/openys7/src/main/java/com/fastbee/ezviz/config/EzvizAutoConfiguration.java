package com.fastbee.ezviz.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.fastbee.ezviz.client.EzvizHttpClient;
import com.fastbee.ezviz.client.EzvizTokenHolder;
import com.fastbee.ezviz.service.EzvizDeviceService;
import com.fastbee.ezviz.service.impl.EzvizPlayServiceImpl;
import com.fastbee.ezviz.service.impl.EzvizPlaybackServiceImpl;
import com.fastbee.ezviz.service.impl.EzvizPtzServiceImpl;

/**
 * 萤石云集成自动配置类
 *
 * <p>在 application.yml 中配置：
 * <pre>
 * fastbee:
 *   media:
 *     ezviz:
 *       enabled: true
 *       appKey: your_app_key
 *       appSecret: your_app_secret
 *       apiBase: https://open.ys7.com
 * </pre>
 *
 * <p>本配置类会自动注册以下 Bean：
 * <ul>
 *   <li>{@link EzvizTokenHolder} - Token 持有与自动刷新</li>
 *   <li>{@link EzvizHttpClient}  - HTTP 客户端封装</li>
 *   <li>{@link EzvizDeviceService} - 设备管理服务</li>
 *   <li>{@link EzvizPlayServiceImpl}    - 实时预览流服务（Bean name: playService7）</li>
 *   <li>{@link EzvizPlaybackServiceImpl} - 录像回放服务（Bean name: playbackService7）</li>
 *   <li>{@link EzvizPtzServiceImpl}     - 云台控制服务（Bean name: ptzService7）</li>
 * </ul>
 *
 * @author fastbee
 */
@Configuration
@ConditionalOnProperty(prefix = "fastbee.media.ezviz", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(EzvizConfig.class)
public class EzvizAutoConfiguration {

    /**
     * 专用于萤石云调用的 RestTemplate
     */
    @Bean("ezvizRestTemplate")
    @ConditionalOnMissingBean(name = "ezvizRestTemplate")
    public RestTemplate ezvizRestTemplate(EzvizConfig config) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(config.getConnectTimeout());
        factory.setReadTimeout(config.getReadTimeout());
        return new RestTemplate(factory);
    }

    @Bean
    @ConditionalOnMissingBean
    public EzvizTokenHolder ezvizTokenHolder(EzvizConfig config, RestTemplate ezvizRestTemplate) {
        return new EzvizTokenHolder(config, ezvizRestTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public EzvizHttpClient ezvizHttpClient(EzvizConfig config, RestTemplate ezvizRestTemplate, EzvizTokenHolder ezvizTokenHolder) {
        return new EzvizHttpClient(config, ezvizRestTemplate, ezvizTokenHolder);
    }

    @Bean
    @ConditionalOnMissingBean
    public EzvizDeviceService ezvizDeviceService(EzvizHttpClient ezvizHttpClient) {
        return new EzvizDeviceService(ezvizHttpClient);
    }

    /**
     * 注册萤石云实时预览服务，Bean 名称为 "playService7"
     * 对应 ChannelStreamType.PLAY_SERVICE + ChannelStreamType.EZVIZ
     */
    @Bean(EzvizPlayServiceImpl.BEAN_NAME)
    @ConditionalOnMissingBean(name = EzvizPlayServiceImpl.BEAN_NAME)
    public EzvizPlayServiceImpl ezvizPlayService(EzvizHttpClient ezvizHttpClient, EzvizTokenHolder ezvizTokenHolder) {
        return new EzvizPlayServiceImpl(ezvizHttpClient, ezvizTokenHolder);
    }

    /**
     * 注册萤石云录像回放服务，Bean 名称为 "playbackService7"
     */
    @Bean(EzvizPlaybackServiceImpl.BEAN_NAME)
    @ConditionalOnMissingBean(name = EzvizPlaybackServiceImpl.BEAN_NAME)
    public EzvizPlaybackServiceImpl ezvizPlaybackService(EzvizHttpClient ezvizHttpClient) {
        return new EzvizPlaybackServiceImpl(ezvizHttpClient);
    }

    /**
     * 注册萤石云云台控制服务，Bean 名称为 "ptzService7"
     */
    @Bean(EzvizPtzServiceImpl.BEAN_NAME)
    @ConditionalOnMissingBean(name = EzvizPtzServiceImpl.BEAN_NAME)
    public EzvizPtzServiceImpl ezvizPtzService(EzvizHttpClient ezvizHttpClient) {
        return new EzvizPtzServiceImpl(ezvizHttpClient);
    }

}
