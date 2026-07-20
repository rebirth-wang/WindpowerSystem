package com.fastbee.isup.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.isup.sdk.isapi.ISAPIService;

/**
 * 云台控制接口
 */
@Slf4j
@Profile("isup")
@RestController
@RequestMapping("/api/devices/{deviceId}/ptz")
@RequiredArgsConstructor
public class IsupPtzController {

    private final ISAPIService isapiService;

    /**
     * 控制云台运动
     *
     * @param deviceId 设备ID
     * @param pan      水平角度
     * @param tilt     垂直角度
     * @param duration 持续时间(毫秒)
     */
    @PostMapping
    public AjaxResult control(
            @PathVariable String deviceId,
            @RequestParam(defaultValue = "1") int channelId,
            @RequestParam(defaultValue = "60") int pan,
            @RequestParam(defaultValue = "0") int tilt,
            @RequestParam(defaultValue = "1000") int duration) {

        log.debug("云台控制 - deviceId: {}, pan: {}, tilt: {}, duration: {}",
                deviceId, pan, tilt, duration);

        isapiService.controlPtz(deviceId, channelId, pan, tilt, duration);
        return AjaxResult.success("云台控制指令已发送");
    }
}
