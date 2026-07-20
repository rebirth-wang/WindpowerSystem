package com.fastbee.onvif.service;

import java.util.Map;

/**
 * ONVIF 服务接口
 * 封装主要 ONVIF 操作，通过 WebSocket 或直接 SOAP 调用实现
 *
 * @author fastbee
 */
public interface IOnvifService {
    /**
     * 设备发现
     * 对应 WS-Discovery Probe 消息
     *
     * @param deviceId 设备 ID
     * @param clear    是否清除历史数据
     */
    void discovery(Integer deviceId, boolean clear);

    /**
     * 查询通道信息
     * 对应 ONVIF GetDeviceInformation + GetCapabilities + GetProfiles
     *
     * @param deviceId  设备 ID
     * @param channelId 通道 ID
     * @param username  认证用户名
     * @param password  认证密码
     */
    void queryChannelInfo(Integer deviceId, Integer channelId, String username, String password);

    /**
     * 获取设备详细信息（直接 SOAP 调用）
     * 对应 ONVIF GetDeviceInformation + GetCapabilities + GetProfiles
     * 并将结果更新到 OnvifDeviceChannel
     *
     * @param channelId 通道 ID
     * @return 设备信息 Map
     */
    Map<String, Object> getDeviceInfo(Integer channelId);

    /**
     * 开始播放
     * 对应 ONVIF GetStreamUri + ZLM 拉流代理
     *
     * @param channelId 通道 ID
     */
    void play(int channelId);

    /**
     * 停止播放
     *
     * @param channelId 通道 ID
     */
    void stop(int channelId);

    /**
     * 开始 PTZ 运动
     * 对应 ONVIF ContinuousMove
     *
     * @param id     通道 ID
     * @param xSpeed 水平速度（-100 ~ 100）
     * @param ySpeed 垂直速度（-100 ~ 100）
     * @param zSpeed 缩放速度（-100 ~ 100）
     */
    void ptzStart(int id, int xSpeed, int ySpeed, int zSpeed);

    /**
     * 停止 PTZ 运动
     * 对应 ONVIF PTZ Stop
     *
     * @param id 通道 ID
     */
    void ptzStop(int id);

    /**
     * 获取快照 URI
     * 对应 ONVIF GetSnapshotUri
     *
     * @param channelId 通道 ID
     * @return 快照 HTTP URI
     */
    String getSnapshotUri(Integer channelId);

    /**
     * 初始化设备所有设备状态（启动时重置为离线）
     */
    void initDeviceStatus();
}
