package com.fastbee.onvif;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.websocket.Session;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fastbee.onvif.config.OnvifProperties;

/**
 * ONVIF WebSocket Session 管理器
 * 维护 deviceId → Session 的映射，提供会话注册、注销及心跳检测功能
 *
 * <p>心跳机制：
 * <ul>
 *   <li>定时向所有活跃 Session 发送 PING 帧</li>
 *   <li>记录最后活跃时间，超过 {@code heartbeatTimeout} 秒则主动断开</li>
 * </ul>
 *
 * @author fastbee
 */
@Slf4j
@Component
public class WebsocketSessionManger {

    /** deviceId → Session 映射 */
    private final Map<Integer, Session> sessionMap = new ConcurrentHashMap<>();

    /** deviceId → 最后活跃时间（毫秒时间戳） */
    private final Map<Integer, Long> lastActiveTimeMap = new ConcurrentHashMap<>();

    @Autowired
    private OnvifProperties onvifProperties;

    /** 心跳检测线程池 */
    private ScheduledExecutorService heartbeatExecutor;

    // -------------------------------------------------------------------------
    // 生命周期
    // -------------------------------------------------------------------------

    /**
     * 启动心跳检测任务
     * 仅在 heartbeatInterval > 0 时启动
     */
    @PostConstruct
    public void startHeartbeat() {
        int interval = onvifProperties.getHeartbeatInterval();
        if (interval <= 0) {
            log.info("[ONVIF Session] 心跳检测已禁用");
            return;
        }
        heartbeatExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "onvif-heartbeat");
            t.setDaemon(true);
            return t;
        });
        heartbeatExecutor.scheduleAtFixedRate(this::checkHeartbeat, interval, interval, TimeUnit.SECONDS);
        log.info("[ONVIF Session] 心跳检测已启动，间隔: {}s，超时: {}s", interval, onvifProperties.getHeartbeatTimeout());
    }

    /**
     * 关闭心跳检测线程池
     */
    @PreDestroy
    public void stopHeartbeat() {
        if (heartbeatExecutor != null && !heartbeatExecutor.isShutdown()) {
            heartbeatExecutor.shutdown();
            log.info("[ONVIF Session] 心跳检测已停止");
        }
    }

    // -------------------------------------------------------------------------
    // Session 管理
    // -------------------------------------------------------------------------

    /**
     * 注册 Session
     *
     * @param id      设备 ID
     * @param session WebSocket Session
     */
    public void addSession(Integer id, Session session) {
        sessionMap.put(id, session);
        lastActiveTimeMap.put(id, System.currentTimeMillis());
        log.debug("[ONVIF Session] 注册 Session, deviceId: {}", id);
    }

    /**
     * 注销并关闭 Session
     *
     * @param id 设备 ID
     */
    public void removeSession(Integer id) {
        Session session = sessionMap.remove(id);
        lastActiveTimeMap.remove(id);
        if (session != null) {
            try {
                if (session.isOpen()) {
                    session.close();
                }
            } catch (IOException e) {
                log.warn("[ONVIF Session] 关闭 Session 失败，deviceId: {}", id, e);
            }
        }
    }

    /**
     * 仅从映射中移除 Session 引用（不主动关闭连接，用于连接已断开的情况）
     *
     * @param id 设备 ID
     */
    public void removeSessionCatch(Integer id) {
        sessionMap.remove(id);
        lastActiveTimeMap.remove(id);
    }

    /**
     * 获取 Session
     *
     * @param id 设备 ID
     * @return Session，不存在则返回 null
     */
    public Session getSession(Integer id) {
        return sessionMap.get(id);
    }

    /**
     * 更新设备最后活跃时间（收到任意消息时调用）
     *
     * @param id 设备 ID
     */
    public void updateLastActiveTime(Integer id) {
        lastActiveTimeMap.put(id, System.currentTimeMillis());
    }

    /**
     * 获取当前连接数
     *
     * @return 活跃连接数
     */
    public int getSessionCount() {
        return sessionMap.size();
    }

    // -------------------------------------------------------------------------
    // 心跳检测
    // -------------------------------------------------------------------------

    /**
     * 心跳检测任务：
     * 1. 向所有活跃 Session 发送 PING 帧
     * 2. 检查超时 Session 并主动断开
     */
    private void checkHeartbeat() {
        long now = System.currentTimeMillis();
        long timeoutMs = (long) onvifProperties.getHeartbeatTimeout() * 1000;
        log.debug("[ONVIF 心跳] 检测开始，当前连接数: {}", sessionMap.size());

        for (Map.Entry<Integer, Session> entry : sessionMap.entrySet()) {
            Integer deviceId = entry.getKey();
            Session session = entry.getValue();

            // 检查 Session 是否已关闭
            if (!session.isOpen()) {
                log.warn("[ONVIF 心跳] Session 已关闭，移除 deviceId: {}", deviceId);
                removeSessionCatch(deviceId);
                continue;
            }

            // 检查超时
            Long lastActive = lastActiveTimeMap.get(deviceId);
            if (lastActive != null && (now - lastActive) > timeoutMs) {
                log.warn("[ONVIF 心跳] 设备心跳超时，断开连接，deviceId: {}，最后活跃: {}ms 前",
                        deviceId, now - lastActive);
                removeSession(deviceId);
                continue;
            }

            // 发送 PING
            try {
                session.getBasicRemote().sendPing(null);
                log.debug("[ONVIF 心跳] PING 发送成功，deviceId: {}", deviceId);
            } catch (IOException e) {
                log.warn("[ONVIF 心跳] PING 发送失败，deviceId: {}，错误: {}", deviceId, e.getMessage());
                removeSession(deviceId);
            }
        }
    }
}
