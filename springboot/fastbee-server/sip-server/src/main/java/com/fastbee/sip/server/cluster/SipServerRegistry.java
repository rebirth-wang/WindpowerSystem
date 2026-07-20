package com.fastbee.sip.server.cluster;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fastbee.common.extend.config.SysSipConfig;

/**
 * SIP 服务器集群注册中心
 * 负责实例注册、心跳续约、活跃实例发现
 */
@Slf4j
@Component
public class SipServerRegistry {

    private static final String REGISTRY_KEY_PREFIX = "SIP:SERVER:REGISTRY:";
    private static final String ACTIVE_SET_KEY = "SIP:SERVER:ACTIVE";
    private static final long REGISTRY_TTL_SECONDS = 30;
    private static final long HEARTBEAT_INTERVAL_SECONDS = 10;

    @Autowired
    private SysSipConfig sysSipConfig;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    private ScheduledExecutorService scheduler;

    /**
     * 注册当前实例到集群，并启动心跳续约
     */
    public void register() {
        String serverId = sysSipConfig.getServerId();
        String registryKey = REGISTRY_KEY_PREFIX + serverId;

        Map<String, String> info = new HashMap<>();
        info.put("ip", sysSipConfig.getIp());
        info.put("port", String.valueOf(sysSipConfig.getPort()));
        info.put("startTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        info.put("status", "ONLINE");

        redisTemplate.opsForHash().putAll(registryKey, info);
        redisTemplate.expire(registryKey, REGISTRY_TTL_SECONDS, TimeUnit.SECONDS);
        redisTemplate.opsForSet().add(ACTIVE_SET_KEY, serverId);
        log.info("[SIP集群] 实例注册成功: {}", serverId);

        // 启动心跳续约
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "sip-cluster-heartbeat");
            t.setDaemon(true);
            return t;
        });
        scheduler.scheduleAtFixedRate(this::heartbeat,
                HEARTBEAT_INTERVAL_SECONDS, HEARTBEAT_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    private void heartbeat() {
        try {
            String serverId = sysSipConfig.getServerId();
            String registryKey = REGISTRY_KEY_PREFIX + serverId;
            
            // 使用原子操作刷新TTL，避免竞争条件
            Map<String, String> info = new HashMap<>();
            info.put("ip", sysSipConfig.getIp());
            info.put("port", String.valueOf(sysSipConfig.getPort()));
            info.put("startTime", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            info.put("status", "ONLINE");
            
            // 原子性地更新信息并设置TTL
            redisTemplate.opsForHash().putAll(registryKey, info);
            redisTemplate.expire(registryKey, REGISTRY_TTL_SECONDS, TimeUnit.SECONDS);
            redisTemplate.opsForSet().add(ACTIVE_SET_KEY, serverId);
        } catch (Exception e) {
            log.error("[SIP集群] 心跳续约失败", e);
        }
    }

    /**
     * 获取所有活跃的SIP服务器实例ID
     */
    public Set<String> getActiveServerIds() {
        Set<Object> members = redisTemplate.opsForSet().members(ACTIVE_SET_KEY);
        if (members == null || members.isEmpty()) {
            return Collections.emptySet();
        }
        Set<String> activeIds = new HashSet<>();
        for (Object member : members) {
            String sid = member.toString();
            String registryKey = REGISTRY_KEY_PREFIX + sid;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(registryKey))) {
                activeIds.add(sid);
            } else {
                // 注册key已过期，从active集合中移除
                redisTemplate.opsForSet().remove(ACTIVE_SET_KEY, sid);
            }
        }
        return activeIds;
    }

    /**
     * 判断指定serverId是否在线
     */
    public boolean isServerOnline(String serverId) {
        String registryKey = REGISTRY_KEY_PREFIX + serverId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(registryKey));
    }

    @PreDestroy
    public void unregister() {
        try {
            String serverId = sysSipConfig.getServerId();
            redisTemplate.delete(REGISTRY_KEY_PREFIX + serverId);
            redisTemplate.opsForSet().remove(ACTIVE_SET_KEY, serverId);
            if (scheduler != null) {
                scheduler.shutdown();
            }
            log.info("[SIP集群] 实例注销: {}", serverId);
        } catch (Exception e) {
            log.error("[SIP集群] 实例注销失败", e);
        }
    }
}
