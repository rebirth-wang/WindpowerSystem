package com.fastbee.onvif.service.impl;

import java.net.URISyntaxException;
import java.util.*;

import jakarta.websocket.RemoteEndpoint;
import jakarta.websocket.Session;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.media.domain.MediaServer;
import com.fastbee.media.enums.ErrorCode;
import com.fastbee.media.model.MediaInfo;
import com.fastbee.media.model.StreamContent;
import com.fastbee.media.model.StreamInfo;
import com.fastbee.media.service.IMediaCacheService;
import com.fastbee.media.service.IMediaServerService;
import com.fastbee.media.service.IMediaService;
import com.fastbee.media.util.ZlmApiUtils;
import com.fastbee.media.util.ZlmRtpUtils;
import com.fastbee.onvif.WebsocketSessionManger;
import com.fastbee.onvif.bean.*;
import com.fastbee.onvif.callback.RequestMessage;
import com.fastbee.onvif.domain.OnvifDevice;
import com.fastbee.onvif.domain.OnvifDeviceChannel;
import com.fastbee.onvif.service.IOnvifDeviceChannelService;
import com.fastbee.onvif.service.IOnvifDeviceService;
import com.fastbee.onvif.service.IOnvifService;
import com.fastbee.onvif.util.OnvifClient;

/**
 * ONVIF 服务实现
 * 封装设备发现、通道信息查询、播放控制、PTZ、快照等主要功能
 * 部分操作通过 WebSocket 下发到 ONVIF 节点代理执行，
 * 部分操作（设备信息、快照）通过 OnvifClient 直接 SOAP 调用
 *
 * @author fastbee
 */
@Slf4j
@Service
public class OnvifServiceImpl implements IOnvifService {

    @Autowired
    private WebsocketSessionManger websocketSessionManger;

    @Autowired
    private IOnvifDeviceChannelService onvifDeviceChannelService;

    @Autowired
    private IOnvifDeviceService onvifDeviceService;

    @Autowired
    private IMediaCacheService mediaCacheService;

    @Autowired
    private IMediaServerService mediaServerService;

    @Autowired
    private IMediaService mediaService;

    @Autowired
    private ZlmApiUtils zlmApiUtils;

    @Autowired
    private ZlmRtpUtils zlmRtpUtils;

    @Autowired
    private OnvifClient onvifClient;

    // -------------------------------------------------------------------------
    // 设备发现
    // -------------------------------------------------------------------------

    /**
     * 触发设备发现
     * 通过 WebSocket 向 ONVIF 节点代理发送 DISCOVERY 消息
     * 节点代理执行 WS-Discovery Probe（UDP 组播 239.255.255.250:3702）
     *
     * @param deviceId 设备 ID
     * @param clear    是否清空历史通道记录
     */
    @Override
    public void discovery(Integer deviceId, boolean clear) {
        log.info("[ONVIF 请求扫描设备] id: {}, 是否清空：{}", deviceId, clear);
        if (clear) {
            onvifDeviceChannelService.clearByDeviceId(deviceId);
        }
        RemoteEndpoint.Async asyncRemote = getRemoteEndpoint(deviceId);
        WebsocketMessage message = new WebsocketMessage();
        message.setMessageType(WebsocketMessageType.DISCOVERY);
        asyncRemote.sendText(JSON.toJSONString(message));
    }

    // -------------------------------------------------------------------------
    // 设备信息查询
    // -------------------------------------------------------------------------

    /**
     * 查询通道信息（通过 WebSocket 下发到节点代理）
     * 节点代理调用 ONVIF GetDeviceInformation，结果通过 WebSocket 回调
     * 对应 ONVIF Device Service - GetDeviceInformation
     *
     * @param deviceId  设备 ID
     * @param channelId 通道 ID
     * @param username  用户名
     * @param password  密码
     */
    @Override
    public void queryChannelInfo(Integer deviceId, Integer channelId, String username, String password) {
        log.info("[ONVIF 查询设备信息] deviceId：{}，id: {}, username: {}", deviceId, channelId, username);
        OnvifDeviceChannel onvifDeviceChannel = onvifDeviceChannelService.selectOnvifDeviceChannelById(channelId);
        if (onvifDeviceChannel == null) {
            log.warn("[ONVIF 查询设备信息] 参数不全，id: {}", channelId);
            throw new ServiceException(ErrorCode.ERROR400.getCode(), "未找到通道：" + channelId);
        }
        if (username == null) {
            username = onvifDeviceChannel.getUsername();
        }
        if (password == null) {
            password = onvifDeviceChannel.getPassword();
        }
        if (username == null || password == null) {
            log.warn("[ONVIF 查询设备信息] 用户名或密码为空，id: {}", channelId);
            throw new ServiceException(ErrorCode.ERROR100.getCode(), "用户名或密码为空");
        }
        if (!username.equals(onvifDeviceChannel.getUsername()) || !password.equals(onvifDeviceChannel.getPassword())) {
            onvifDeviceChannel.setUsername(username);
            onvifDeviceChannel.setPassword(password);
            onvifDeviceChannelService.updateWithCache(onvifDeviceChannel);
        }
        RemoteEndpoint.Async asyncRemote = getRemoteEndpoint(deviceId);
        WebsocketMessage message = new WebsocketMessage();
        message.setMessageType(WebsocketMessageType.CAMERA_INFO);
        message.setChannelId(channelId);
        JSONObject data = new JSONObject();
        data.put("channelId", onvifDeviceChannel.getId());
        data.put("ip", onvifDeviceChannel.getIp());
        data.put("port", onvifDeviceChannel.getPort());
        data.put("username", username);
        data.put("password", password);
        message.setData(data);
        asyncRemote.sendText(JSON.toJSONString(message));
    }

    /**
     * 直接通过 SOAP 获取设备详细信息并持久化
     * 调用顺序：GetDeviceInformation → GetCapabilities → GetProfiles
     * 获取的 profileToken/mediaServiceUrl/ptzServiceUrl 更新到 OnvifDeviceChannel
     *
     * @param channelId 通道 ID
     * @return 设备信息 Map
     */
    @Override
    public Map<String, Object> getDeviceInfo(Integer channelId) {
        log.info("[ONVIF GetDeviceInfo] 通道ID：{}", channelId);
        OnvifDeviceChannel channel = onvifDeviceChannelService.selectOnvifDeviceChannelById(channelId);
        if (channel == null) {
            throw new ServiceException(ErrorCode.ERROR400.getCode(), "通道不存在：" + channelId);
        }
        validateChannelCredentials(channel);

        Map<String, Object> result = new LinkedHashMap<>();

        // Step 1: GetDeviceInformation
        Map<String, String> deviceInfo = onvifClient.getDeviceInformation(channel);
        result.putAll(deviceInfo);
        channel.setManufacture(deviceInfo.get("manufacturer"));
        channel.setModel(deviceInfo.get("model"));
        channel.setFirmwareVersion(deviceInfo.get("firmwareVersion"));
        channel.setSerialNumber(deviceInfo.get("serialNumber"));
        channel.setHardwareId(deviceInfo.get("hardwareId"));
        log.debug("[ONVIF GetDeviceInfo] 设备信息: {}", deviceInfo);

        // Step 2: GetCapabilities（获取各服务地址）
        try {
            Map<String, String> caps = onvifClient.getCapabilities(channel);
            if (caps.get("mediaServiceUrl") != null) {
                channel.setMediaServiceUrl(caps.get("mediaServiceUrl"));
                result.put("mediaServiceUrl", caps.get("mediaServiceUrl"));
            }
            if (caps.get("ptzServiceUrl") != null) {
                channel.setPtzServiceUrl(caps.get("ptzServiceUrl"));
                result.put("ptzServiceUrl", caps.get("ptzServiceUrl"));
                channel.setSupportPtz(1);
            }
            log.debug("[ONVIF GetDeviceInfo] 能力信息: {}", caps);
        } catch (Exception e) {
            log.warn("[ONVIF GetDeviceInfo] 获取能力失败，通道: {}, 错误: {}", channelId, e.getMessage());
        }

        // Step 3: GetProfiles（获取 Profile Token）
        try {
            List<String> profiles = onvifClient.getProfiles(channel);
            if (!profiles.isEmpty()) {
                channel.setProfileToken(profiles.get(0));
                result.put("profileToken", profiles.get(0));
                result.put("allProfiles", profiles);
                log.debug("[ONVIF GetDeviceInfo] Profile Token: {}", profiles.get(0));
            }
        } catch (Exception e) {
            log.warn("[ONVIF GetDeviceInfo] 获取 Profiles 失败，通道: {}, 错误: {}", channelId, e.getMessage());
        }

        // 持久化更新
        onvifDeviceChannelService.updateWithCache(channel);
        log.info("[ONVIF GetDeviceInfo] 完成，通道ID: {}", channelId);
        return result;
    }

    // -------------------------------------------------------------------------
    // 视频流播放
    // -------------------------------------------------------------------------

    /**
     * 开始播放通道视频流
     * 优先使用已缓存的流，否则通过 OnvifClient 获取 RTSP 地址后由 ZLM 拉流
     * 对应 ONVIF Media Service - GetStreamUri
     *
     * @param channelId 通道 ID
     */
    @Override
    public void play(int channelId) {
        Boolean streamReady;
        log.info("[播放ONVIF] 通道ID：{}", channelId);
        OnvifDeviceChannel channel = onvifDeviceChannelService.selectOnvifDeviceChannelById(channelId);
        if (channel == null) {
            throw new ServiceException(ErrorCode.ERROR400.getCode(), "通道不存在");
        }
        RequestMessage requestMessage = new RequestMessage();
        String key = ConstantHolder.PLAY_ONVIF_CHANNEL + channelId;
        requestMessage.setKey(key);
        String app = "onvif";
        String stream = channel.getId() + "";
        // 检查流是否已存在
        MediaInfo streamInfo = mediaCacheService.getStreamInfo(app, stream, "*");
        if (streamInfo != null) {
            String mediaServerId = streamInfo.getMediaServer().getServerId();
            MediaServer mediaServerItem = mediaServerService.selectMediaServerByServerId(mediaServerId);
            if (mediaServerItem == null
                    || (streamReady = zlmRtpUtils.isStreamReady(mediaServerItem, app, stream)) == null
                    || !streamReady) {
                mediaCacheService.removeStream(mediaServerId, app, app, stream);
            } else {
                log.info("[播放ONVIF] 已拉起，直接返回，通道ID：{}", channelId);
                StreamInfo resultStreamInfo = mediaService.getStreamInfoByAppAndStream(mediaServerItem, app, stream, null, null);
                requestMessage.setData(new StreamContent(resultStreamInfo));
                return;
            }
        }
        // 获取可用 ZLM 媒体服务器
        MediaServer mediaServerItem2 = this.mediaServerService.getMediaServerForMinimumLoad(null);
        if (mediaServerItem2 == null) {
            log.warn("[播放ONVIF] 失败，没有可用的ZLM，通道ID：{}", channelId);
            throw new ServiceException(ErrorCode.ERROR100.getCode(), "没有可用的ZLM");
        }
        // 优先尝试从设备实时获取流地址（若 profileToken 已获取）
        String rtspUri = resolveStreamUri(channel);
        if (rtspUri == null) {
            throw new ServiceException(ErrorCode.ERROR100.getCode(), "没有可用的播放地址，请先查询设备信息");
        }
        int rtpType = determineRtpType(channel);
        log.debug("[播放ONVIF] 拉流地址: {}, 通道ID: {}", rtspUri, channelId);
        JSONObject jsonObject = zlmApiUtils.addStreamProxy(mediaServerItem2, app, stream, rtspUri,
                channel.getEnableAudio() == 1, channel.getEnableMp4() == 1, rtpType, 15);
        if (jsonObject == null) {
            log.warn("[播放ONVIF] 失败，结果为空，通道ID：{}", channelId);
            throw new ServiceException(ErrorCode.ERROR100.getCode(), "拉流失败，结果为空");
        }
        log.info("[播放ONVIF] 拉流成功，通道ID：{}", channelId);
    }

    /**
     * 停止播放
     *
     * @param channelId 通道 ID
     */
    @Override
    public void stop(int channelId) {
        MediaServer mediaServerItem;
        log.info("[停止ONVIF] 通道ID：{}", channelId);
        OnvifDeviceChannel channel = onvifDeviceChannelService.selectOnvifDeviceChannelById(channelId);
        if (channel == null) {
            throw new ServiceException(ErrorCode.ERROR400.getCode(), "通道不存在");
        }
        String stream = channel.getId() + "";
        MediaInfo streamInfo = mediaCacheService.getStreamInfo("onvif", stream, "*");
        if (streamInfo != null) {
            String mediaServerId = streamInfo.getServerId();
            if (mediaServerId != null
                    && (mediaServerItem = this.mediaServerService.selectMediaServerByServerId(mediaServerId)) != null) {
                zlmApiUtils.closeStreams(mediaServerItem, "onvif", stream);
            }
            mediaCacheService.removeStream(streamInfo.getServerId(), "onvif", "onvif", stream);
        }
    }

    // -------------------------------------------------------------------------
    // PTZ 云台控制
    // -------------------------------------------------------------------------

    /**
     * 开始 PTZ 连续移动
     * 通过 WebSocket 下发 PTZ_START 消息到 ONVIF 节点代理
     * 对应 ONVIF PTZ Service - ContinuousMove
     *
     * @param id     通道 ID
     * @param xSpeed 水平速度（-100 ~ 100，正值向右，负值向左）
     * @param ySpeed 垂直速度（-100 ~ 100，正值向上，负值向下）
     * @param zSpeed 缩放速度（-100 ~ 100，正值放大，负值缩小）
     */
    @Override
    public void ptzStart(int id, int xSpeed, int ySpeed, int zSpeed) {
        log.info("[ONVIF 云台控制] 通道id: {}, x:{}, y:{}, z:{}", id, xSpeed, ySpeed, zSpeed);
        OnvifDeviceChannel channel = onvifDeviceChannelService.selectOnvifDeviceChannelById(id);
        if (channel == null) {
            throw new ServiceException(ErrorCode.ERROR400.getCode(), "通道不存在");
        }
        RemoteEndpoint.Async asyncRemote = getRemoteEndpoint(channel.getDeviceId());
        WebsocketMessage message = new WebsocketMessage();
        message.setMessageType(WebsocketMessageType.PTZ_START);
        message.setChannelId(id);
        JSONObject data = new JSONObject();
        data.put("channelId", channel.getId());
        data.put("ip", channel.getIp());
        data.put("port", channel.getPort());
        data.put("username", channel.getUsername());
        data.put("password", channel.getPassword());
        data.put("xSpeed", xSpeed);
        data.put("ySpeed", ySpeed);
        data.put("zSpeed", zSpeed);
        message.setData(data);
        asyncRemote.sendText(JSON.toJSONString(message));
    }

    /**
     * 停止 PTZ 移动
     * 通过 WebSocket 下发 PTZ_STOP 消息到 ONVIF 节点代理
     * 对应 ONVIF PTZ Service - Stop
     *
     * @param id 通道 ID
     */
    @Override
    public void ptzStop(int id) {
        log.info("[ONVIF 云台控制] 停止 通道id: {}", id);
        OnvifDeviceChannel channel = onvifDeviceChannelService.selectOnvifDeviceChannelById(id);
        if (channel == null) {
            throw new ServiceException(ErrorCode.ERROR400.getCode(), "通道不存在");
        }
        RemoteEndpoint.Async asyncRemote = getRemoteEndpoint(channel.getDeviceId());
        WebsocketMessage message = new WebsocketMessage();
        message.setMessageType(WebsocketMessageType.PTZ_STOP);
        message.setChannelId(id);
        JSONObject data = new JSONObject();
        data.put("channelId", channel.getId());
        data.put("ip", channel.getIp());
        data.put("port", channel.getPort());
        data.put("username", channel.getUsername());
        data.put("password", channel.getPassword());
        message.setData(data);
        asyncRemote.sendText(JSON.toJSONString(message));
    }

    // -------------------------------------------------------------------------
    // 快照
    // -------------------------------------------------------------------------

    /**
     * 获取快照 URI
     * 直接通过 OnvifClient 调用 ONVIF Media Service - GetSnapshotUri
     *
     * @param channelId 通道 ID
     * @return 快照 HTTP URI
     */
    @Override
    public String getSnapshotUri(Integer channelId) {
        log.info("[ONVIF 快照] 获取快照URI, 通道ID：{}", channelId);
        OnvifDeviceChannel channel = onvifDeviceChannelService.selectOnvifDeviceChannelById(channelId);
        if (channel == null) {
            throw new ServiceException(ErrorCode.ERROR400.getCode(), "通道不存在：" + channelId);
        }
        validateChannelCredentials(channel);
        // 若还没有 profileToken，先获取 profiles
        if (channel.getProfileToken() == null || channel.getProfileToken().isEmpty()) {
            log.info("[ONVIF 快照] 未找到 profileToken，尝试自动获取，通道ID: {}", channelId);
            try {
                List<String> profiles = onvifClient.getProfiles(channel);
                if (!profiles.isEmpty()) {
                    channel.setProfileToken(profiles.get(0));
                    onvifDeviceChannelService.updateWithCache(channel);
                }
            } catch (Exception e) {
                throw new ServiceException(ErrorCode.ERROR100.getCode(),
                        "获取 Profile Token 失败，请先查询设备信息: " + e.getMessage());
            }
        }
        String snapshotUri = onvifClient.getSnapshotUri(channel);
        if (snapshotUri == null || snapshotUri.isEmpty()) {
            throw new ServiceException(ErrorCode.ERROR100.getCode(), "获取快照 URI 失败");
        }
        // 注入认证信息（如设备不支持 Basic Auth 则需其他方式）
        String authenticatedUri = buildAuthenticatedUri(snapshotUri, channel.getUsername(), channel.getPassword());
        // 更新并缓存快照地址
        channel.setSnapshotUri(snapshotUri);
        onvifDeviceChannelService.updateWithCache(channel);
        log.info("[ONVIF 快照] 获取成功, 通道ID: {}, URI: {}", channelId, snapshotUri);
        return authenticatedUri;
    }

    // -------------------------------------------------------------------------
    // 设备状态初始化
    // -------------------------------------------------------------------------

    /**
     * 系统启动时将所有 ONVIF 设备状态重置为离线（0）
     * 待设备重新连接 WebSocket 后更新为在线（1）
     */
    @Override
    public void initDeviceStatus() {
        log.info("[ONVIF] 初始化设备状态，将所有设备标记为离线");
        OnvifDevice queryParam = new OnvifDevice();
        List<com.fastbee.onvif.domain.vo.OnvifDeviceVO> deviceList =
                onvifDeviceService.listOnvifDeviceVO(queryParam);
        if (deviceList == null || deviceList.isEmpty()) {
            log.info("[ONVIF] 无设备记录，跳过初始化");
            return;
        }
        for (com.fastbee.onvif.domain.vo.OnvifDeviceVO vo : deviceList) {
            OnvifDevice device = new OnvifDevice();
            device.setId(vo.getId());
            device.setStatus(0L);
            device.setUpdateTime(DateUtils.getNowDate());
            onvifDeviceService.updateWithCache(device);
        }
        log.info("[ONVIF] 设备状态初始化完成，共 {} 台设备标记为离线", deviceList.size());
    }

    // -------------------------------------------------------------------------
    // 内部工具方法
    // -------------------------------------------------------------------------

    /**
     * 获取 WebSocket 远程端点（带详细错误提示）
     */
    private RemoteEndpoint.Async getRemoteEndpoint(Integer deviceId) {
        Session session = this.websocketSessionManger.getSession(deviceId);
        if (session == null) {
            log.warn("[ONVIF] 失败，id: {}, 设备尚未连接", deviceId);
            throw new ServiceException(ErrorCode.ERROR100.getCode(), "设备尚未连接");
        }
        if (!session.isOpen()) {
            log.warn("[ONVIF] 失败，id: {}, 设备连接已关闭", deviceId);
            throw new ServiceException(ErrorCode.ERROR100.getCode(), "设备连接已关闭");
        }
        RemoteEndpoint.Async asyncRemote = session.getAsyncRemote();
        if (asyncRemote == null) {
            log.warn("[ONVIF] 消息发送失败，id: {}", deviceId);
            throw new ServiceException(ErrorCode.ERROR100.getCode(), "消息发送失败");
        }
        return asyncRemote;
    }

    /**
     * 解析 RTSP 流地址（优先通过 OnvifClient 获取，其次使用缓存地址）
     */
    private String resolveStreamUri(OnvifDeviceChannel channel) {
        // 优先通过 OnvifClient 动态获取（需要 profileToken）
        if (channel.getProfileToken() != null && !channel.getProfileToken().isEmpty()) {
            try {
                String rtspUri = onvifClient.getStreamUri(channel, "RtspUnicast");
                if (rtspUri != null && !rtspUri.isEmpty()) {
                    // 持久化到 liveStreamTcp
                    channel.setLiveStreamTcp(rtspUri);
                    onvifDeviceChannelService.updateWithCache(channel);
                    return buildAuthenticatedUri(rtspUri, channel.getUsername(), channel.getPassword());
                }
            } catch (Exception e) {
                log.warn("[ONVIF] GetStreamUri 失败，回退到缓存地址，通道: {}, 错误: {}",
                        channel.getId(), e.getMessage());
            }
        }
        // 回退使用缓存地址
        String uriStr = channel.getLiveStreamTcp();
        if (uriStr == null) {
            if (channel.getLiveStreamUdp() != null) {
                uriStr = channel.getLiveStreamUdp();
            } else if (channel.getLiveStreamMulticast() != null) {
                uriStr = channel.getLiveStreamMulticast();
            }
        }
        if (uriStr == null) {
            return null;
        }
        return buildAuthenticatedUri(uriStr, channel.getUsername(), channel.getPassword());
    }

    /** 确定 RTP 传输类型（0=TCP, 1=UDP, 2=Multicast） */
    private int determineRtpType(OnvifDeviceChannel channel) {
        if (channel.getLiveStreamTcp() != null) {
            return 0;
        } else if (channel.getLiveStreamUdp() != null) {
            return 1;
        } else {
            return 2;
        }
    }

    /** 在 URI 中注入用户名密码（RFC 3986 格式）*/
    private String buildAuthenticatedUri(String uriStr, String username, String password) {
        if (username == null || password == null) {
            return uriStr;
        }
        try {
            return new URIBuilder(uriStr).setUserInfo(username, password).toString();
        } catch (URISyntaxException e) {
            log.error("[ONVIF] 解析URI({})异常", uriStr, e);
            return uriStr;
        }
    }

    /** 校验通道认证信息 */
    private void validateChannelCredentials(OnvifDeviceChannel channel) {
        if (channel.getIp() == null || channel.getIp().isEmpty()) {
            throw new ServiceException(ErrorCode.ERROR400.getCode(),
                    "通道 [" + channel.getId() + "] IP 为空");
        }
        if (channel.getPort() == null || channel.getPort() == 0) {
            throw new ServiceException(ErrorCode.ERROR400.getCode(),
                    "通道 [" + channel.getId() + "] 端口为空");
        }
        if (channel.getUsername() == null || channel.getPassword() == null) {
            throw new ServiceException(ErrorCode.ERROR100.getCode(),
                    "通道 [" + channel.getId() + "] 用户名或密码为空，请先配置认证信息");
        }
    }
}
