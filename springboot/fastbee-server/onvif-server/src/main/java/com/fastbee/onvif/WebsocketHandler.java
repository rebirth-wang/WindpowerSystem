package com.fastbee.onvif;

import java.io.IOException;

import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fastbee.common.utils.DateUtils;
import com.fastbee.onvif.config.OnvifProperties;
import com.fastbee.onvif.domain.OnvifDevice;
import com.fastbee.onvif.service.IOnvifDeviceService;

/**
 * ONVIF WebSocket 端点处理器
 * 接收来自 ONVIF 节点代理的 WebSocket 连接，进行鉴权和消息路由
 *
 * <p>WebSocket 路径：/onvif/{serverId}/{id}
 * <ul>
 *   <li>{serverId}：服务器鉴权 ID，需与配置项 {@code onvif.server-id} 一致</li>
 *   <li>{id}：ONVIF 代理节点设备 ID（对应 iot_device.device_id）</li>
 * </ul>
 *
 * @author fastbee
 */
@Slf4j
@ServerEndpoint("/onvif/{serverId}/{id}")
@Component
public class WebsocketHandler {

    @Autowired
    private WebsocketMessageHandler messageHandler;

    @Autowired
    private WebsocketSessionManger sessionManger;

    @Autowired
    private IOnvifDeviceService onvifDeviceService;

    @Autowired
    private OnvifProperties onvifProperties;

    /**
     * WebSocket 连接建立
     * 校验 serverId 鉴权，通过后注册 Session 并更新设备在线状态
     *
     * @param serverId 客户端携带的服务器 ID（用于鉴权）
     * @param id       设备 ID，对应 iot_device.device_id
     * @param session  WebSocket Session
     */
    @OnOpen
    public void onOpen(@PathParam("serverId") String serverId, @PathParam("id") Integer id, Session session) {
        log.info("[ONVIF 节点] 收到新连接: id: {}, serverId: {}", id, serverId);
        // 从配置读取合法的 serverId（替换原先硬编码的 "fastbee"）
        String expectedServerId = onvifProperties.getServerId();
        if (!expectedServerId.equals(serverId)) {
            log.warn("[ONVIF 节点] 鉴权失败: id: {}, 期望 serverId: {}, 实际: {}", id, expectedServerId, serverId);
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.NOT_CONSISTENT, "鉴权失败"));
            } catch (IOException e) {
                log.error("[ONVIF 节点] 关闭鉴权失败的连接时出错，id: {}", id, e);
            }
            return;
        }
        sessionManger.addSession(id, session);
        // 查询 iot_device 设备记录，并把 ONVIF 节点在线状态写入 iot_device.status
        OnvifDevice onvifDevice = onvifDeviceService.queryByIdWithCache(id);
        if (onvifDevice == null) {
            log.warn("[ONVIF 节点] 未找到 iot_device 设备记录，拒绝连接，deviceId: {}", id);
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "iot_device 不存在"));
            } catch (IOException e) {
                log.error("[ONVIF 节点] 关闭未绑定设备连接时出错，id: {}", id, e);
            }
            return;
        }
        onvifDevice.setStatus(1L);
        onvifDevice.setLastHeartbeat(DateUtils.getNowDate());
        onvifDevice.setUpdateTime(DateUtils.getNowDate());
        onvifDeviceService.updateWithCache(onvifDevice);
        log.info("[ONVIF 节点] 设备上线，id: {}", id);
    }

    /**
     * 收到消息
     * 更新最后活跃时间，路由到 WebsocketMessageHandler 处理
     *
     * @param id  设备 ID
     * @param msg 消息内容（JSON 格式）
     */
    @OnMessage
    public void onMessage(@PathParam("id") Integer id, String msg) {
        log.debug("[ONVIF 节点] 消息: id: {}, 内容：{}", id, msg);
        // 收到任意消息时更新活跃时间（维持心跳）
        sessionManger.updateLastActiveTime(id);
        messageHandler.handMessage(id, msg);
    }

    /**
     * 连接发生错误
     *
     * @param id    设备 ID
     * @param session WebSocket Session
     * @param error 错误信息
     */
    @OnError
    public void onError(@PathParam("id") Integer id, Session session, Throwable error) {
        log.error("[ONVIF 节点] 收到错误: id: {}", id, error);
        sessionManger.removeSession(id);
        updateDeviceOffline(id);
    }

    /**
     * 连接关闭
     *
     * @param session WebSocket Session
     * @param id      设备 ID
     */
    @OnClose
    public void onClose(Session session, @PathParam("id") Integer id) {
        log.info("[ONVIF 节点] 连接关闭: id: {}", id);
        sessionManger.removeSessionCatch(id);
        updateDeviceOffline(id);
    }

    /**
     * 更新设备离线状态
     *
     * @param id 设备 ID
     */
    private void updateDeviceOffline(Integer id) {
        try {
            OnvifDevice device = onvifDeviceService.queryByIdWithCache(id);
            if (device != null) {
                device.setStatus(0L);
                device.setUpdateTime(DateUtils.getNowDate());
                onvifDeviceService.updateWithCache(device);
                log.info("[ONVIF 节点] 设备离线，id: {}", id);
            }
        } catch (Exception e) {
            log.warn("[ONVIF 节点] 更新设备离线状态失败，id: {}", id, e);
        }
    }
}
