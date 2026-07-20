package com.fastbee.ai.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.fastbee.ai.config.properties.FastBeeAiProperties;

/**
 * AI 模块基础配置。
 *
 * <p>当前阶段先完成配置装配与骨架初始化，后续具体模型 Bean
 * 仍由对应 starter 和业务配置共同决定。</p>
 */
@Configuration
@EnableConfigurationProperties(FastBeeAiProperties.class)
public class FastBeeAiConfiguration {
}
