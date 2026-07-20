package com.fastbee.controller.media;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.sip.enums.Direct;
import com.fastbee.sip.model.PtzDirectionInput;
import com.fastbee.sip.model.PtzscaleInput;
import com.fastbee.sip.service.ISipPtzCmdService;

@Slf4j
@RestController
@RequestMapping("/sip/ptz")
public class PtzController {
    @Autowired
    private ISipPtzCmdService ptzCmdService;

    @ApiOperation("云台方向控制")
    @PreAuthorize("@ss.hasPermi('iot:video:list')")
    @PostMapping("/direction/{deviceId}/{channelId}")
    public AjaxResult direction(@PathVariable String deviceId, @PathVariable String channelId, @RequestBody PtzDirectionInput ptzDirectionInput) {
        Direct direct = null;
        int leftRight = ptzDirectionInput.getLeftRight();
        int upDown = ptzDirectionInput.getUpDown();
        if (leftRight == 1) {
            direct = Direct.RIGHT;
        } else if (leftRight == 2) {
            direct = Direct.LEFT;
        } else if (upDown == 1) {
            direct = Direct.UP;
        } else if (upDown == 2) {
            direct = Direct.DOWN;
        } else {
            direct = Direct.STOP;
        }
        Integer speed = ptzDirectionInput.getMoveSpeed();
        return AjaxResult.success(MessageUtils.message("success"), ptzCmdService.directPtzCmd(deviceId, channelId, direct, speed));
    }

    @ApiOperation("云台缩放控制")
    @PreAuthorize("@ss.hasPermi('iot:video:list')")
    @PostMapping("/scale/{deviceId}/{channelId}")
    public AjaxResult scale(@PathVariable String deviceId, @PathVariable String channelId, @RequestBody PtzscaleInput ptzscaleInput) {
        Direct direct;
        if (ptzscaleInput.getInOut() == 1) {
            direct = Direct.ZOOM_IN;
        } else if (ptzscaleInput.getInOut() == 2) {
            direct = Direct.ZOOM_OUT;
        } else {
            direct = Direct.STOP;
        }
        Integer speed = ptzscaleInput.getScaleSpeed();
        return AjaxResult.success(MessageUtils.message("success"), ptzCmdService.directPtzCmd(deviceId, channelId, direct, speed));
    }

    @ApiOperation("云台指令控制")
    @PostMapping("/cmd/{deviceId}/{channelId}/{cmd}")
    @PreAuthorize("@ss.hasPermi('iot:video:list')")
    public AjaxResult ptzCmd(@PathVariable String deviceId, @PathVariable String channelId, @PathVariable String cmd) {
        return AjaxResult.success(MessageUtils.message("success"), ptzCmdService.ptzCmd(deviceId, channelId, cmd));
    }

}
