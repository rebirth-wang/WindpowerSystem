//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.framework.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.CompositeCodec;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.SingleServerConfig;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fastbee.framework.config.properties.RedissonProperties;
import com.fastbee.framework.handler.KeyPrefixHandler;

@Configuration
@EnableConfigurationProperties({RedissonProperties.class})
public class RedissonConfig {
    private static final Logger log = LoggerFactory.getLogger(RedissonConfig.class);
    @Autowired
    private RedissonProperties redissonProperties;

    @Bean
    public RedissonAutoConfigurationCustomizer redissonCustomizer() {
        return (config) -> {
            JavaTimeModule javaTimeModule = new JavaTimeModule();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
            javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
            ObjectMapper om = new ObjectMapper();
            om.registerModule(javaTimeModule);
            om.setTimeZone(TimeZone.getDefault());
            om.setVisibility(PropertyAccessor.ALL, Visibility.ANY);
            om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, DefaultTyping.NON_FINAL);
            TypedJsonJacksonCodec jsonCodec = new TypedJsonJacksonCodec(Object.class, om);
            CompositeCodec codec = new CompositeCodec(StringCodec.INSTANCE, jsonCodec, jsonCodec);
            config.setThreads(this.redissonProperties.getThreads()).setNettyThreads(this.redissonProperties.getNettyThreads()).setUseScriptCache(true).setCodec(codec);
            RedissonProperties.SingleServerConfig singleServerConfig = this.redissonProperties.getSingleServerConfig();
            if (ObjectUtil.isNotNull(singleServerConfig)) {
                ((SingleServerConfig)((SingleServerConfig)((SingleServerConfig)((SingleServerConfig)config.useSingleServer().setNameMapper(new KeyPrefixHandler(this.redissonProperties.getKeyPrefix()))).setTimeout(singleServerConfig.getTimeout())).setClientName(singleServerConfig.getClientName())).setIdleConnectionTimeout(singleServerConfig.getIdleConnectionTimeout())).setSubscriptionConnectionPoolSize(singleServerConfig.getSubscriptionConnectionPoolSize()).setConnectionMinimumIdleSize(singleServerConfig.getConnectionMinimumIdleSize()).setConnectionPoolSize(singleServerConfig.getConnectionPoolSize());
            }

            RedissonProperties.ClusterServersConfig clusterServersConfig = this.redissonProperties.getClusterServersConfig();
            if (ObjectUtil.isNotNull(clusterServersConfig)) {
                ((ClusterServersConfig)((ClusterServersConfig)((ClusterServersConfig)((ClusterServersConfig)((ClusterServersConfig)((ClusterServersConfig)((ClusterServersConfig)((ClusterServersConfig)((ClusterServersConfig)((ClusterServersConfig)config.useClusterServers().setNameMapper(new KeyPrefixHandler(this.redissonProperties.getKeyPrefix()))).setTimeout(clusterServersConfig.getTimeout())).setClientName(clusterServersConfig.getClientName())).setIdleConnectionTimeout(clusterServersConfig.getIdleConnectionTimeout())).setSubscriptionConnectionPoolSize(clusterServersConfig.getSubscriptionConnectionPoolSize())).setMasterConnectionMinimumIdleSize(clusterServersConfig.getMasterConnectionMinimumIdleSize())).setMasterConnectionPoolSize(clusterServersConfig.getMasterConnectionPoolSize())).setSlaveConnectionMinimumIdleSize(clusterServersConfig.getSlaveConnectionMinimumIdleSize())).setSlaveConnectionPoolSize(clusterServersConfig.getSlaveConnectionPoolSize())).setReadMode(clusterServersConfig.getReadMode())).setSubscriptionMode(clusterServersConfig.getSubscriptionMode());
            }

            log.info("初始化 redis 配置");
        };
    }
}
