package com.fastbee.isup.controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.isup.model.IsupDevInfo;
import com.fastbee.isup.model.vo.PlayURL;
import com.fastbee.isup.sdk.StreamManager;
import com.fastbee.isup.service.DeviceCacheService;
import com.fastbee.isup.service.IMediaStreamService;

/**
 * 流媒体控制接口（预览、回放）
 */
@Slf4j
@Profile("isup")
@RestController
@RequestMapping("/api/devices/{deviceId}")
@RequiredArgsConstructor
public class StreamController {
    private final IMediaStreamService mediaStreamService;
    private final DeviceCacheService deviceCacheService;

    /**
     * 开始实时预览
     */
    @PostMapping("/preview")
    public AjaxResult startPreview(
            @PathVariable String deviceId,
            @RequestParam(required = false, defaultValue = "1") Integer channelId,
            @RequestParam(required = false, defaultValue = "flv") String type) {
        Optional<IsupDevInfo> deviceOpt = deviceCacheService.getByDeviceId(deviceId);
        if (!deviceOpt.isPresent()) {
            return AjaxResult.error("设备不存在，无法预览");
        }
        IsupDevInfo device = deviceOpt.get();
        // 查找指定的通道
        IsupDevInfo.Channel channel = device.getChannels().stream()
                .filter(ch -> ch.getChannelId().equals(channelId))
                .findFirst()
                .orElse(null);
        if (channel == null) {
            return AjaxResult.error("通道不存在: " + channelId);
        }
        String streamKey = deviceId + "_" + channelId;
        // 防重复：如果该通道已有RTP服务，直接返回播放地址
        if (StreamManager.deviceRTP.containsKey(streamKey)) {
            return AjaxResult.success("通道已在预览中，忽略重复开启: " + streamKey,
                    buildHttpFlvStreamUrl("live", streamKey, type));
        }
        mediaStreamService.preview(device, channelId);
        return AjaxResult.success("开启预览成功", buildHttpFlvStreamUrl("live", streamKey, type));
    }

    /**
     * 停止实时预览
     */
    @DeleteMapping("/preview")
    public AjaxResult stopPreview(
            @PathVariable String deviceId,
            @RequestParam(required = false, defaultValue = "1") Integer channelId) {
        Optional<IsupDevInfo> deviceOpt = deviceCacheService.getByDeviceId(deviceId);
        deviceOpt.ifPresent(device -> {
            log.info("停止预览 - deviceId: {}, channelId: {}", deviceId, channelId);
            mediaStreamService.stopPreview(device, channelId);
        });
        return AjaxResult.success(true);
    }

    /**
     * 开始回放
     */
    @PostMapping("/playback")
    public AjaxResult startPlayback(
            @PathVariable String deviceId,
            @RequestParam(required = false, defaultValue = "1") Integer channelId,
            @RequestParam(required = false, defaultValue = "flv") String type,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        if (deviceId == null || deviceId.isEmpty()) {
            return AjaxResult.error("设备ID不能为空");
        }
        // URL 解码
        try {
            if (startTime != null) {
                startTime = URLDecoder.decode(startTime, StandardCharsets.UTF_8.name());
            }
            if (endTime != null) {
                endTime = URLDecoder.decode(endTime, StandardCharsets.UTF_8.name());
            }
        } catch (Exception e) {
            log.warn("时间参数解码失败: startTime={}, endTime={}", startTime, endTime, e);
        }
        Optional<IsupDevInfo> deviceOpt = deviceCacheService.getByDeviceId(deviceId);
        if (!deviceOpt.isPresent()) {
            log.warn("回放失败: 设备不存在, deviceId={}", deviceId);
            return AjaxResult.error("回放失败: 设备不存在");
        }
        IsupDevInfo device = deviceOpt.get();
        // 查找指定的通道
        IsupDevInfo.Channel channel = device.getChannels().stream()
                .filter(ch -> ch.getChannelId().equals(channelId))
                .findFirst()
                .orElse(null);
        if (channel == null) {
            return AjaxResult.error("通道不存在: " + channelId);
        }
        String streamKey = deviceId + "_" + channelId;
        // 防重复：如果该通道已有RTP服务，直接返回播放地址
        if (StreamManager.playbackDeviceRTP.containsKey(streamKey)) {
            log.info("通道已在预览中，忽略重复开启: {}", streamKey);
            PlayURL playURL = new PlayURL();
            playURL.setHttpFlv(buildHttpFlvStreamUrl("playback", streamKey, type));
            return AjaxResult.success(playURL);
        }
        mediaStreamService.playbackByTime(device, channelId, startTime, endTime);
        PlayURL playURL = new PlayURL();
        playURL.setHttpFlv(buildHttpFlvStreamUrl("playback", streamKey, type));
        return AjaxResult.success(playURL);
    }

    /**
     * 停止回放
     */
    @DeleteMapping("/playback")
    public AjaxResult stopPlayback(@PathVariable String deviceId, @RequestParam(required = false, defaultValue = "1") Integer channelId, @PathVariable String streamKey) {
        Optional<IsupDevInfo> deviceOpt = deviceCacheService.getByDeviceId(deviceId);
        if (deviceOpt.isPresent()) {
            IsupDevInfo device = deviceOpt.get();
            log.debug("停止回放 - deviceId: {}", deviceId);
            mediaStreamService.stopPlayBackByTime(device, channelId, streamKey);
        }
        return AjaxResult.success();
    }

    private String buildHttpFlvStreamUrl(String prefix, String streamKey, String type) {
        String url = "http://";
        if ("flv".equals(type)) {
            return url + ".live.flv";
        }
        return url + "/hls.m3u8";
    }
}
