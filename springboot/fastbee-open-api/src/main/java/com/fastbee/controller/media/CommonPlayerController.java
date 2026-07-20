package com.fastbee.controller.media;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.model.StreamPlayUrl;
import com.fastbee.media.service.IComChannelPlayService;
import com.fastbee.media.service.ICommonChannelService;
import com.fastbee.sip.service.IStreamProxyService;

@Slf4j
@RestController
@RequestMapping("/common/player")
public class CommonPlayerController extends BaseController {
    @Autowired
    private ICommonChannelService channelService;

    @Autowired
    private IComChannelPlayService channelPlayService;

    @Autowired
    private IStreamProxyService streamProxyService;


    @ApiOperation("直播播放")
    @PreAuthorize("@ss.hasPermi('iot:player:play')")
    @GetMapping("/play")
    public AjaxResult play(String deviceId, String channelId, Boolean record) {
        CommonChannel channel = channelService.selectCommonChannelByDeviceChannelId(deviceId, channelId);
        return AjaxResult.success(MessageUtils.message("success"), channelPlayService.play(channel, null, record));
    }

    @ApiOperation("直播播放停止")
    @PreAuthorize("@ss.hasPermi('iot:player:play')")
    @GetMapping("/play/stop")
    public AjaxResult stopPlay(String deviceId, String channelId) {
        CommonChannel channel = channelService.selectCommonChannelByDeviceChannelId(deviceId, channelId);
        return AjaxResult.success(channelPlayService.stopPlay(channel));
    }

    @ApiOperation("获取直播地址")
    @GetMapping("/getUrl/{deviceId}/{channelId}")
    public AjaxResult getUrl(@PathVariable String deviceId, @PathVariable String channelId) {
        StreamPlayUrl stream = channelPlayService.getUrl(deviceId, channelId);
        if (stream == null) {
            return AjaxResult.error(MessageUtils.message("fail"));
        } else {
            return AjaxResult.success(MessageUtils.message("success"), stream);
        }
    }

    @ApiOperation("设备录像查询")
    @PreAuthorize("@ss.hasPermi('iot:player:playback')")
    @GetMapping("/playback/query")
    public AjaxResult queryRecord(String deviceId, String channelId, String startTime, String endTime) {
        CommonChannel channel = channelService.selectCommonChannelByDeviceChannelId(deviceId, channelId);
        Assert.notNull(channel, "通道不存在");
        return AjaxResult.success(MessageUtils.message("success"), channelPlayService.queryRecord(channel, startTime, endTime));
    }

    @ApiOperation("设备录像回放播放")
    @PreAuthorize("@ss.hasPermi('iot:player:playback')")
    @GetMapping("/playback")
    public AjaxResult playback(String deviceId, String channelId, String startTime, String endTime) {
        CommonChannel channel = channelService.selectCommonChannelByDeviceChannelId(deviceId, channelId);
        return AjaxResult.success(MessageUtils.message("success"), channelPlayService.playback(channel, startTime, endTime));
    }

    @ApiOperation("设备录像回放停止")
    @PreAuthorize("@ss.hasPermi('iot:player:playback')")
    @GetMapping("/playback/stop")
    public AjaxResult stopPlayback(String deviceId, String channelId, String stream) {
        Assert.notNull(channelId, "参数异常");
        CommonChannel channel = channelService.selectCommonChannelByDeviceChannelId(deviceId, channelId);
        Assert.notNull(channel, "通道不存在");
        return AjaxResult.success(channelPlayService.stopPlayback(channel, stream));
    }

    @ApiOperation("停止推流（有播放者时，不会关流，会影响其他人观看）")
    @GetMapping("/closeStream/{deviceId}/{channelId}/{streamId}")
    public AjaxResult closeStream(@PathVariable String deviceId, @PathVariable String channelId, @PathVariable String streamId) {
        CommonChannel channel = channelService.selectCommonChannelByDeviceChannelId(deviceId, channelId);
        return AjaxResult.success("success!", channelPlayService.closeStream(channel, streamId,true));
    }

    @ApiOperation("强制停止推流")
    @GetMapping("/closeStreamDirect/{deviceId}/{channelId}/{streamId}")
    public AjaxResult closeStreamDirect(@PathVariable String deviceId, @PathVariable String channelId, @PathVariable String streamId) {
        CommonChannel channel = channelService.selectCommonChannelByDeviceChannelId(deviceId, channelId);
        return AjaxResult.success("success!", channelPlayService.closeStream(channel, streamId, false));
    }

    @ApiOperation("回放暂停")
    @PreAuthorize("@ss.hasPermi('iot:player:playback')")
    @GetMapping("/playback/pause")
    public AjaxResult pausePlayback(String deviceId, String channelId, String stream) {
        CommonChannel channel = channelService.selectCommonChannelByDeviceChannelId(deviceId, channelId);
        channelPlayService.playbackPause(channel, stream);
        return AjaxResult.success();
    }

    @ApiOperation("回放恢复")
    @PreAuthorize("@ss.hasPermi('iot:player:playback')")
    @GetMapping("/playback/resume")
    public AjaxResult resumePlayback(String deviceId, String channelId, String stream) {
        CommonChannel channel = channelService.selectCommonChannelByDeviceChannelId(deviceId, channelId);
        channelPlayService.playbackResume(channel, stream);
        return AjaxResult.success();
    }

    @ApiOperation("录像回放定位")
    @PreAuthorize("@ss.hasPermi('iot:player:playback')")
    @GetMapping("/playback/seek")
    public AjaxResult seekPlayback(String deviceId, String channelId, String stream, Long seekTime) {
        CommonChannel channel = channelService.selectCommonChannelByDeviceChannelId(deviceId, channelId);
        channelPlayService.playbackSeek(channel, stream, seekTime);
        return AjaxResult.success();
    }

    @ApiOperation("录像倍速播放")
    @PreAuthorize("@ss.hasPermi('iot:player:playback')")
    @GetMapping("/playback/speed")
    public AjaxResult speedPlayback(String deviceId, String channelId, String stream, Double speed) {
        CommonChannel channel = channelService.selectCommonChannelByDeviceChannelId(deviceId, channelId);
        channelPlayService.playbackSpeed(channel, stream, speed);
        return AjaxResult.success();
    }


    /**
     * 启用流代理
     */
    @ApiOperation("启用流代理")
    @PreAuthorize("@ss.hasPermi('iot:video:list')")
    @GetMapping("/proxy/start")
    public AjaxResult proxyStart(String deviceId, String channelId) {
        return AjaxResult.success(streamProxyService.start(deviceId, channelId,null));
    }

    /**
     * 停用流代理
     */
    @ApiOperation("停用流代理")
    @PreAuthorize("@ss.hasPermi('iot:video:list')")
    @GetMapping("/proxy/stop")
    public AjaxResult proxyStop(String deviceId, String channelId) {
        streamProxyService.stop(deviceId, channelId);
        return AjaxResult.success();
    }

    /**
     * 流代理是否在推流
     */
    @ApiOperation("流代理是否在推流")
    @PreAuthorize("@ss.hasPermi('iot:video:list')")
    @GetMapping("/proxy/pulling")
    public AjaxResult pulling(String deviceId, String channelId) {
        return AjaxResult.success(streamProxyService.pulling(deviceId, channelId));
    }

}
