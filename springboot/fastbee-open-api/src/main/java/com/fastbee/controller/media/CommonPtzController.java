package com.fastbee.controller.media;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.model.*;
import com.fastbee.media.service.IComChannelPtzService;
import com.fastbee.media.service.ICommonChannelService;

@Slf4j
@RestController
@RequestMapping("/common/ptz")
public class CommonPtzController {

    @Autowired
    private ICommonChannelService channelService;

    @Autowired
    private IComChannelPtzService comChannelPtzService;

    @ApiOperation("云台方向控制")
    @PreAuthorize("@ss.hasPermi('iot:video:list')")
    @PostMapping("/ptz/{deviceId}/{channelId}")
    public AjaxResult ptz(@PathVariable String deviceId, @PathVariable String channelId, @RequestBody PtzInput input) {
        CommonChannel channel = resolveChannel(deviceId, channelId);
        return AjaxResult.success("success!", comChannelPtzService.ptz(channel, input));
    }

    @ApiOperation("前端控制码云台控制")
    @PreAuthorize("@ss.hasPermi('iot:video:list')")
    @PostMapping("/front-end/{deviceId}/{channelId}")
    public AjaxResult frontEndCommand(@PathVariable String deviceId,
                                      @PathVariable String channelId,
                                      @RequestParam Integer cmdCode,
                                      @RequestParam(required = false) Integer parameter1,
                                      @RequestParam(required = false) Integer parameter2,
                                      @RequestParam(required = false) Integer combindCode) {
        CommonChannel channel = resolveChannel(deviceId, channelId);
        return AjaxResult.success("success!",
                comChannelPtzService.frontEndCommand(channel, cmdCode, parameter1, parameter2, combindCode));
    }

    @ApiOperation("前端控制结构云台控制")
    @PreAuthorize("@ss.hasPermi('iot:video:list')")
    @PostMapping("/fec/{deviceId}/{channelId}")
    public AjaxResult ptzByCode(@PathVariable String deviceId,
                                @PathVariable String channelId,
                                @RequestBody FrontEndControlCodeForPTZ controlCode) {
        CommonChannel channel = resolveChannel(deviceId, channelId);
        return AjaxResult.success("success!", comChannelPtzService.ptz(channel, controlCode));
    }

    @ApiOperation("预置点控制")
    @PreAuthorize("@ss.hasPermi('iot:video:list')")
    @PostMapping("/preset/{deviceId}/{channelId}")
    public AjaxResult preset(@PathVariable String deviceId,
                             @PathVariable String channelId,
                             @RequestBody FrontEndControlCodeForPreset controlCode) {
        CommonChannel channel = resolveChannel(deviceId, channelId);
        return AjaxResult.success("success!", comChannelPtzService.preset(channel, controlCode));
    }

    @ApiOperation("查询预置点")
    @PreAuthorize("@ss.hasPermi('iot:video:list')")
    @GetMapping("/preset/{deviceId}/{channelId}")
    public AjaxResult queryPresetList(@PathVariable String deviceId, @PathVariable String channelId) {
        CommonChannel channel = resolveChannel(deviceId, channelId);
        return AjaxResult.success("success!", comChannelPtzService.queryPresetList(channel));
    }

    @ApiOperation("焦距/光圈控制")
    @PreAuthorize("@ss.hasPermi('iot:video:list')")
    @PostMapping("/fi/{deviceId}/{channelId}")
    public AjaxResult fi(@PathVariable String deviceId,
                         @PathVariable String channelId,
                         @RequestBody FrontEndControlCodeForFI controlCode) {
        CommonChannel channel = resolveChannel(deviceId, channelId);
        return AjaxResult.success("success!", comChannelPtzService.fi(channel, controlCode));
    }

    @ApiOperation("巡航控制")
    @PreAuthorize("@ss.hasPermi('iot:video:list')")
    @PostMapping("/tour/{deviceId}/{channelId}")
    public AjaxResult tour(@PathVariable String deviceId,
                           @PathVariable String channelId,
                           @RequestBody FrontEndControlCodeForTour controlCode) {
        CommonChannel channel = resolveChannel(deviceId, channelId);
        return AjaxResult.success("success!", comChannelPtzService.tour(channel, controlCode));
    }

    @ApiOperation("扫描控制")
    @PreAuthorize("@ss.hasPermi('iot:video:list')")
    @PostMapping("/scan/{deviceId}/{channelId}")
    public AjaxResult scan(@PathVariable String deviceId,
                           @PathVariable String channelId,
                           @RequestBody FrontEndControlCodeForScan controlCode) {
        CommonChannel channel = resolveChannel(deviceId, channelId);
        return AjaxResult.success("success!", comChannelPtzService.scan(channel, controlCode));
    }

    @ApiOperation("辅助功能控制")
    @PreAuthorize("@ss.hasPermi('iot:video:list')")
    @PostMapping("/auxiliary/{deviceId}/{channelId}")
    public AjaxResult auxiliary(@PathVariable String deviceId,
                                @PathVariable String channelId,
                                @RequestBody FrontEndControlCodeForAuxiliary controlCode) {
        CommonChannel channel = resolveChannel(deviceId, channelId);
        return AjaxResult.success("success!", comChannelPtzService.auxiliary(channel, controlCode));
    }

    @ApiOperation("雨刷控制")
    @PreAuthorize("@ss.hasPermi('iot:video:list')")
    @PostMapping("/wiper/{deviceId}/{channelId}")
    public AjaxResult wiper(@PathVariable String deviceId,
                            @PathVariable String channelId,
                            @RequestBody FrontEndControlCodeForWiper controlCode) {
        CommonChannel channel = resolveChannel(deviceId, channelId);
        return AjaxResult.success("success!", comChannelPtzService.wiper(channel, controlCode));
    }

    private CommonChannel resolveChannel(String deviceId, String channelId) {
        return channelService.selectCommonChannelByDeviceChannelId(deviceId, channelId);
    }
}
