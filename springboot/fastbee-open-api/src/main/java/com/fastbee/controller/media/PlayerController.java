package com.fastbee.controller.media;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.media.model.StreamPlayUrl;
import com.fastbee.sip.service.ISipPlayService;

@Slf4j
@RestController
@RequestMapping("/sip/player")
public class PlayerController extends BaseController {
    @Autowired
    private ISipPlayService playService;

    @GetMapping("/getBigScreenUrl/{deviceId}/{channelId}")
    public AjaxResult getBigScreenUrl(@PathVariable String deviceId, @PathVariable String channelId) {
        return AjaxResult.success(MessageUtils.message("success"), playService.play(deviceId, channelId, false).getHttps_fmp4());
    }

    @ApiOperation("直播播放")
    @GetMapping("/play/{deviceId}/{channelId}")
    public AjaxResult play(@PathVariable String deviceId, @PathVariable String channelId) {
        return AjaxResult.success(MessageUtils.message("success"), playService.play(deviceId, channelId, false));
    }

    @ApiOperation("获取直播地址")
    @GetMapping("/getUrl/{deviceId}/{channelId}")
    public AjaxResult getUrl(@PathVariable String deviceId, @PathVariable String channelId) {
        StreamPlayUrl stream = playService.getUrl(deviceId, channelId);
        if (stream == null) {
            return AjaxResult.error(MessageUtils.message("fail"));
        } else {
            return AjaxResult.success(MessageUtils.message("success"), stream);
        }
    }

    @ApiOperation("流是否在推流")
    @GetMapping("/pushing/{deviceId}/{streamId}")
    public AjaxResult pushing(@PathVariable String deviceId, @PathVariable String streamId) {
        return AjaxResult.success(MessageUtils.message("success"), playService.pushing(deviceId, streamId));
    }

    @ApiOperation("通道截图 (延迟一会后，需closeStream停止推流)")
    @GetMapping("/screenshot/{deviceId}/{channelId}")
    public AjaxResult screenshot(@PathVariable String deviceId, @PathVariable String channelId) {
        return AjaxResult.success(MessageUtils.message("success"), playService.screenshot(deviceId, channelId));
    }

    @ApiOperation("回放播放")
    @GetMapping("/playback/{deviceId}/{channelId}")
    public AjaxResult playback(@PathVariable String deviceId,
                               @PathVariable String channelId, String start, String end) {
        return AjaxResult.success(MessageUtils.message("success"), playService.playback(deviceId, channelId, start, end));
    }

    @ApiOperation("停止推流（有播放者时，不会关流，会影响其他人观看）")
    @GetMapping("/closeStream/{deviceId}/{channelId}/{streamId}")
    public AjaxResult closeStream(@PathVariable String deviceId, @PathVariable String channelId, @PathVariable String streamId) {
        return AjaxResult.success("success!", playService.closeStream(deviceId, channelId, streamId,true));
    }

    @ApiOperation("强制停止推流")
    @GetMapping("/closeStreamDirect/{deviceId}/{channelId}/{streamId}")
    public AjaxResult closeStreamDirect(@PathVariable String deviceId, @PathVariable String channelId, @PathVariable String streamId) {
        return AjaxResult.success("success!", playService.closeStream(deviceId, channelId, streamId,false));
    }

    @ApiOperation("回放暂停")
    @GetMapping("/playbackPause/{deviceId}/{channelId}/{streamId}")
    public AjaxResult playbackPause(@PathVariable String deviceId, @PathVariable String channelId, @PathVariable String streamId) {
        return AjaxResult.success(MessageUtils.message("success"), playService.playbackPause(deviceId, channelId, streamId));
    }

    @ApiOperation("回放恢复")
    @GetMapping("/playbackReplay/{deviceId}/{channelId}/{streamId}")
    public AjaxResult playbackReplay(@PathVariable String deviceId, @PathVariable String channelId, @PathVariable String streamId) {
        return AjaxResult.success(MessageUtils.message("success"), playService.playbackReplay(deviceId, channelId, streamId));
    }

    @ApiOperation("录像回放定位")
    @GetMapping("/playbackSeek/{deviceId}/{channelId}/{streamId}")
    public AjaxResult playbackSeek(@PathVariable String deviceId, @PathVariable String channelId, @PathVariable String streamId, long seek) {
        return AjaxResult.success(MessageUtils.message("success"), playService.playbackSeek(deviceId, channelId, streamId, seek));
    }

    @ApiOperation("录像倍速播放")
    @GetMapping("/playbackSpeed/{deviceId}/{channelId}/{streamId}")
    public AjaxResult playbackSpeed(@PathVariable String deviceId, @PathVariable String channelId, @PathVariable String streamId, Double speed) {
        return AjaxResult.success(MessageUtils.message("success"), playService.playbackSpeed(deviceId, channelId, streamId, speed));
    }
}
