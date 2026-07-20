package com.fastbee.framework.config;

import java.io.IOException;
import java.net.ServerSocket;

import com.github.fppt.jedismock.RedisServer;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * Redis 测试 Configuration，主要实现内嵌 Redis 的启动
 *
 */
@Configuration(proxyBeanMethods = false)
@Lazy(false) // 禁止延迟加载
@EnableConfigurationProperties(RedisProperties.class)
public class RedisTestConfiguration {

    /**
     * 创建模拟的 Redis Server 服务器
     */
    @Bean
    public RedisServer redisServer(RedisProperties properties) throws IOException {
        // 查找一个可用端口
        int port = findAvailablePort();
        
        RedisServer redisServer = new RedisServer(port);
        // 一次执行多个单元测试时，貌似创建多个 spring 容器，导致不进行 stop。这样，就导致端口被占用，无法启动。。。
        try {
            redisServer.start();
        } catch (Exception ignore) {}
        
        // 更新 RedisProperties 配置，让 Spring 连接到嵌入式 Redis
        properties.setHost("127.0.0.1");
        properties.setPort(port);
        properties.setPassword(null); // 嵌入式 Redis 不需要密码
        
        return redisServer;
    }
    
    /**
     * 查找一个可用的端口
     */
    private int findAvailablePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        } catch (IOException e) {
            // 如果无法获取可用端口，返回默认端口
            return 0;
        }
    }

}
