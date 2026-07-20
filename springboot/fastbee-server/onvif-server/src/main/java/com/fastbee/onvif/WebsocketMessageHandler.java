package com.fastbee.onvif;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fastbee.common.utils.DateUtils;
import com.fastbee.onvif.bean.ConstantHolder;
import com.fastbee.onvif.bean.WebsocketMessage;
import com.fastbee.onvif.callback.DeferredResultHolder;
import com.fastbee.onvif.callback.RequestMessage;
import com.fastbee.onvif.domain.OnvifDevice;
import com.fastbee.onvif.domain.OnvifDeviceChannel;
import com.fastbee.onvif.service.IOnvifDeviceChannelService;
import com.fastbee.onvif.service.IOnvifDeviceService;

/**
 * ONVIF WebSocket 消息处理器
 * 处理来自 ONVIF 节点代理的各类回调消息，包括设备发现结果、摄像头信息、PTZ 响应等
 *
 * <p>消息协议参考 {@link com.fastbee.onvif.bean.WebsocketMessageType}
 *
 * @author fastbee
 */
@Slf4j
@Component
public class WebsocketMessageHandler {

    @Autowired
    private DeferredResultHolder resultHolder;

    @Autowired
    private IOnvifDeviceService onvifDeviceService;

    @Autowired
    private IOnvifDeviceChannelService onvifDeviceChannelService;

    /**
     * 分发处理 WebSocket 消息
     *
     * @param deviceId 设备 ID
     * @param message  JSON 消息字符串
     */
    public void handMessage(int deviceId, String message) {
        WebsocketMessage wsMsg;
        try {
            wsMsg = JSON.parseObject(message, WebsocketMessage.class);
        } catch (Exception e) {
            log.error("[ONVIF 消息处理] 消息解析失败，deviceId: {}, 消息: {}", deviceId, message, e);
            return;
        }
        if (wsMsg == null || wsMsg.getMessageType() == null) {
            log.warn("[ONVIF 消息处理] 消息类型为空，deviceId: {}", deviceId);
            return;
        }
        JSONObject data = wsMsg.getData();
        log.debug("[ONVIF 消息处理] 类型: {}, deviceId: {}, success: {}",
                wsMsg.getMessageType(), deviceId, wsMsg.isSuccess());

        switch (wsMsg.getMessageType()) {
            case INFO:
                handleInfo(deviceId, wsMsg, data);
                break;
            case DISCOVERY:
                handleDiscovery(deviceId, wsMsg, data);
                break;
            case CAMERA_INFO:
                handleCameraInfo(deviceId, wsMsg, data);
                break;
            case PTZ_START:
            case PTZ_STOP:
                handlePtzResponse(deviceId, wsMsg);
                break;
            case SNAPSHOT:
                handleSnapshot(deviceId, wsMsg, data);
                break;
            case TALK_START:
                handleTalkStart(deviceId, wsMsg, data);
                break;
            case TALK_STOP:
                handleTalkStop(deviceId, wsMsg);
                break;
            case PLAYBACK_QUERY:
                handlePlaybackQuery(deviceId, wsMsg, data);
                break;
            case PLAYBACK_START:
                handlePlaybackStart(deviceId, wsMsg, data);
                break;
            case PLAYBACK_STOP:
                handlePlaybackStop(deviceId, wsMsg);
                break;
            default:
                log.warn("[ONVIF 消息处理] 未知消息类型: {}, deviceId: {}", wsMsg.getMessageType(), deviceId);
        }
    }

    // -------------------------------------------------------------------------
    // 消息处理方法
    // -------------------------------------------------------------------------

    /**
     * 处理节点信息回报（INFO）
     * 节点代理启动后主动上报自身基本信息
     */
    private void handleInfo(int deviceId, WebsocketMessage wsMsg, JSONObject data) {
        if (!wsMsg.isSuccess()) {
            log.warn("[ONVIF 消息处理] INFO 失败，deviceId: {}, 错误: {}", deviceId, wsMsg.getMsg());
            return;
        }
        if (data == null) {
            log.warn("[ONVIF 消息处理] INFO 数据为空，deviceId: {}", deviceId);
            return;
        }
        OnvifDevice onvifDevice = JSON.to(OnvifDevice.class, data);
        String name = data.getString("name");
        Boolean directConnection = data.getBoolean("directConnection");
        log.info("[ONVIF 消息处理] 收到节点信息：id: {}, 名称：{}，直连：{}", deviceId, name, directConnection);
        onvifDevice.setId(deviceId);
        onvifDevice.setStatus(1L);
        onvifDevice.setLastHeartbeat(DateUtils.getNowDate());
        onvifDeviceService.updateWithCache(onvifDevice);
    }

    /**
     * 处理设备发现结果（DISCOVERY）
     * 节点代理通过 WS-Discovery 发现摄像头后回调
     */
    private void handleDiscovery(int deviceId, WebsocketMessage wsMsg, JSONObject data) {
        if (!wsMsg.isSuccess()) {
            log.warn("[ONVIF 消息处理] DISCOVERY 失败，deviceId: {}, 错误: {}", deviceId, wsMsg.getMsg());
            return;
        }
        if (data == null) {
            log.warn("[ONVIF 消息处理] DISCOVERY 数据为空，deviceId: {}", deviceId);
            return;
        }
        OnvifDeviceChannel onvifDeviceChannel = JSON.to(OnvifDeviceChannel.class, data);
        log.info("[ONVIF 消息处理] 发现摄像头：id: {}, IP:{}, 端口：{}",
                deviceId, onvifDeviceChannel.getIp(), onvifDeviceChannel.getPort());
        onvifDeviceChannel.setDeviceId(deviceId);
        OnvifDeviceChannel existChannel = onvifDeviceChannelService.getChannelByIpAndPort(
                onvifDeviceChannel.getIp(), onvifDeviceChannel.getPort());
        if (existChannel == null) {
            onvifDeviceChannelService.insertWithCache(onvifDeviceChannel);
        } else {
            onvifDeviceChannel.setId(existChannel.getId());
            onvifDeviceChannelService.updateWithCache(onvifDeviceChannel);
        }
    }

    /**
     * 处理摄像头详情回调（CAMERA_INFO）
     * 节点代理调用 ONVIF GetDeviceInformation 后的结果回调
     */
    private void handleCameraInfo(int deviceId, WebsocketMessage wsMsg, JSONObject data) {
        log.info("[ONVIF 消息处理] 摄像头详情：设备：{}, 通道: {}, 详情：{}",
                deviceId, wsMsg.getChannelId(), data);
        RequestMessage requestMessage = new RequestMessage();
        String key = ConstantHolder.QUERY_CHANNEL_INFO + deviceId + wsMsg.getChannelId();
        requestMessage.setKey(key);
        if (!wsMsg.isSuccess()) {
            log.warn("[ONVIF 消息处理] 摄像头详情获取失败，deviceId: {}, channelId: {}, 错误: {}",
                    deviceId, wsMsg.getChannelId(), wsMsg.getMsg());
            requestMessage.setData(wsMsg.getMsg());
            this.resultHolder.invokeAllResult(requestMessage);
            return;
        }
        if (data == null) {
            log.warn("[ONVIF 消息处理] CAMERA_INFO 数据为空，deviceId: {}", deviceId);
            return;
        }
        OnvifDeviceChannel onvifChannel = JSON.to(OnvifDeviceChannel.class, data);
        if (onvifChannel.getIp() == null || onvifChannel.getPort() == 0) {
            log.warn("[ONVIF 消息处理] CAMERA_INFO 内容中缺少 IP 以及端口，deviceId: {}", deviceId);
        } else {
            onvifChannel.setDeviceId(deviceId);
            onvifDeviceChannelService.updateByIpAndPort(onvifChannel);
            this.resultHolder.invokeAllResult(requestMessage);
        }
    }

    /**
     * 处理 PTZ 控制响应（PTZ_START / PTZ_STOP）
     */
    private void handlePtzResponse(int deviceId, WebsocketMessage wsMsg) {
        if (!wsMsg.isSuccess()) {
            log.warn("[ONVIF 消息处理] PTZ 操作失败，deviceId: {}, 错误: {}", deviceId, wsMsg.getMsg());
        } else {
            log.debug("[ONVIF 消息处理] PTZ 操作成功，deviceId: {}, 类型: {}", deviceId, wsMsg.getMessageType());
        }
    }

    /**
     * 处理快照回调（SNAPSHOT）
     * 节点代理获取快照 URI 后的结果回调
     */
    private void handleSnapshot(int deviceId, WebsocketMessage wsMsg, JSONObject data) {
        log.info("[ONVIF 消息处理] 快照回调，deviceId: {}, channelId: {}", deviceId, wsMsg.getChannelId());
        RequestMessage requestMessage = new RequestMessage();
        String key = ConstantHolder.SNAPSHOT_ONVIF_CHANNEL + wsMsg.getChannelId();
        requestMessage.setKey(key);
        if (!wsMsg.isSuccess()) {
            log.warn("[ONVIF 消息处理] 快照获取失败，deviceId: {}, 错误: {}", deviceId, wsMsg.getMsg());
            requestMessage.setData(null);
        } else if (data != null) {
            requestMessage.setData(data.getString("uri"));
        }
        this.resultHolder.invokeAllResult(requestMessage);
    }

    /**
     * 处理对讲开始回调（TALK_START）
     * 节点代理建立音频通道后回调
     */
    private void handleTalkStart(int deviceId, WebsocketMessage wsMsg, JSONObject data) {
        log.info("[ONVIF 消息处理] 对讲开始，deviceId: {}, channelId: {}", deviceId, wsMsg.getChannelId());
        RequestMessage requestMessage = new RequestMessage();
        String key = ConstantHolder.TALK_ONVIF_CHANNEL + wsMsg.getChannelId();
        requestMessage.setKey(key);
        if (!wsMsg.isSuccess()) {
            log.warn("[ONVIF 消息处理] 对讲开始失败，deviceId: {}, 错误: {}", deviceId, wsMsg.getMsg());
            requestMessage.setData(null);
        } else {
            requestMessage.setData(data);
        }
        this.resultHolder.invokeAllResult(requestMessage);
    }

    /**
     * 处理对讲停止回调（TALK_STOP）
     */
    private void handleTalkStop(int deviceId, WebsocketMessage wsMsg) {
        log.info("[ONVIF 消息处理] 对讲停止，deviceId: {}, channelId: {}", deviceId, wsMsg.getChannelId());
        if (!wsMsg.isSuccess()) {
            log.warn("[ONVIF 消息处理] 对讲停止失败，deviceId: {}, 错误: {}", deviceId, wsMsg.getMsg());
        }
    }

    /**
     * 处理回放查询结果（PLAYBACK_QUERY）
     * 节点代理查询录像列表后回调
     */
    private void handlePlaybackQuery(int deviceId, WebsocketMessage wsMsg, JSONObject data) {
        log.info("[ONVIF 消息处理] 回放查询结果，deviceId: {}, channelId: {}", deviceId, wsMsg.getChannelId());
        RequestMessage requestMessage = new RequestMessage();
        String key = ConstantHolder.PLAYBACK_ONVIF_CHANNEL + "QUERY" + wsMsg.getChannelId();
        requestMessage.setKey(key);
        if (!wsMsg.isSuccess()) {
            log.warn("[ONVIF 消息处理] 回放查询失败，deviceId: {}, 错误: {}", deviceId, wsMsg.getMsg());
            requestMessage.setData(null);
        } else {
            requestMessage.setData(data);
        }
        this.resultHolder.invokeAllResult(requestMessage);
    }

    /**
     * 处理回放开始结果（PLAYBACK_START）
     */
    private void handlePlaybackStart(int deviceId, WebsocketMessage wsMsg, JSONObject data) {
        log.info("[ONVIF 消息处理] 回放开始，deviceId: {}, channelId: {}", deviceId, wsMsg.getChannelId());
        RequestMessage requestMessage = new RequestMessage();
        String key = ConstantHolder.PLAYBACK_ONVIF_CHANNEL + wsMsg.getChannelId();
        requestMessage.setKey(key);
        if (!wsMsg.isSuccess()) {
            log.warn("[ONVIF 消息处理] 回放开始失败，deviceId: {}, 错误: {}", deviceId, wsMsg.getMsg());
            requestMessage.setData(null);
        } else {
            requestMessage.setData(data);
        }
        this.resultHolder.invokeAllResult(requestMessage);
    }

    /**
     * 处理回放停止结果（PLAYBACK_STOP）
     */
    private void handlePlaybackStop(int deviceId, WebsocketMessage wsMsg) {
        log.info("[ONVIF 消息处理] 回放停止，deviceId: {}, channelId: {}", deviceId, wsMsg.getChannelId());
        if (!wsMsg.isSuccess()) {
            log.warn("[ONVIF 消息处理] 回放停止失败，deviceId: {}, 错误: {}", deviceId, wsMsg.getMsg());
        }
    }
}
