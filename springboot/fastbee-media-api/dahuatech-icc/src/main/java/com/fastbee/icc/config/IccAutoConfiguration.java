package com.fastbee.icc.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fastbee.icc.client.IccHttpClient;
import com.fastbee.icc.client.IccTokenHolder;
import com.fastbee.icc.service.IccDeviceService;
import com.fastbee.icc.service.impl.IccPlayServiceImpl;
import com.fastbee.icc.service.impl.IccPlaybackServiceImpl;
import com.fastbee.icc.service.impl.IccPtzServiceImpl;

/**
 * 大华ICC平台集成自动配置类
 *
 * <p>在 application.yml 中配置启用：
 * <pre>
 * fastbee:
 *   media:
 *     icc:
 *       enabled: true
 *       clientId: your_client_id
 *       clientSecret: your_client_secret
 *       username: your_username
 *       password: your_password
 *       host: 192.168.1.100
 *       httpsPort: 443
 *       httpPort: 80
 *       tokenExpireSeconds: 7200
 * </pre>
 *
 * <p>本配置类会自动注册以下 Bean：
 * <ul>
 *   <li>{@link IccTokenHolder} - Token 持有与自动刷新</li>
 *   <li>{@link IccHttpClient}  - HTTP 客户端封装（基于ICC SDK）</li>
 *   <li>{@link IccDeviceService} - 设备管理服务</li>
 *   <li>{@link IccPlayServiceImpl}    - 实时预览流服务（Bean name: playService8）</li>
 *   <li>{@link IccPlaybackServiceImpl} - 录像回放服务（Bean name: playbackService8）</li>
 *   <li>{@link IccPtzServiceImpl}     - 云台控制服务（Bean name: ptzService8）</li>
 * </ul>
 *
 * <p>Bean路由说明：媒体调度器通过 dataType=8 自动路由到大华ICC服务Bean
 *
 * @author fastbee
 */
@Configuration
@ConditionalOnProperty(prefix = "fastbee.media.icc", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(PlatformConfig.class)
public class IccAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public IccTokenHolder iccTokenHolder(PlatformConfig config) {
        return new IccTokenHolder(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public IccHttpClient iccHttpClient(PlatformConfig config, IccTokenHolder iccTokenHolder) {
        return new IccHttpClient(config, iccTokenHolder);
    }

    @Bean
    @ConditionalOnMissingBean
    public IccDeviceService iccDeviceService(IccHttpClient iccHttpClient) {
        return new IccDeviceService(iccHttpClient);
    }

    /**
     * 注册大华ICC实时预览服务，Bean 名称为 "playService8"
     * 对应 ChannelStreamType.PLAY_SERVICE + ChannelStreamType.ICC_DAHUA
     */
    @Bean(IccPlayServiceImpl.BEAN_NAME)
    @ConditionalOnMissingBean(name = IccPlayServiceImpl.BEAN_NAME)
    public IccPlayServiceImpl iccPlayService(IccHttpClient iccHttpClient) {
        return new IccPlayServiceImpl(iccHttpClient);
    }

    /**
     * 注册大华ICC录像回放服务，Bean 名称为 "playbackService8"
     */
    @Bean(IccPlaybackServiceImpl.BEAN_NAME)
    @ConditionalOnMissingBean(name = IccPlaybackServiceImpl.BEAN_NAME)
    public IccPlaybackServiceImpl iccPlaybackService(IccHttpClient iccHttpClient) {
        return new IccPlaybackServiceImpl(iccHttpClient);
    }

    /**
     * 注册大华ICC云台控制服务，Bean 名称为 "ptzService8"
     */
    @Bean(IccPtzServiceImpl.BEAN_NAME)
    @ConditionalOnMissingBean(name = IccPtzServiceImpl.BEAN_NAME)
    public IccPtzServiceImpl iccPtzService(IccHttpClient iccHttpClient) {
        return new IccPtzServiceImpl(iccHttpClient);
    }
}
