//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.fastbee.framework.config;

import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fastbee.framework.manager.SpringCacheManager;

@Configuration
@EnableCaching
@ConditionalOnProperty(
        name = {"spring.cache.enable"},
        havingValue = "true"
)
public class CacheConfig {
    @Value("${spring.cache.ttl}")
    private Long ttl;

    @Bean
    public Cache<Object, Object> caffeine() {
        return Caffeine.newBuilder().expireAfterWrite(60L, TimeUnit.SECONDS).initialCapacity(100).maximumSize(10000L).build();
    }

    @Bean
    public CacheManager cacheManager() {
        return new SpringCacheManager(this.ttl);
    }
}
