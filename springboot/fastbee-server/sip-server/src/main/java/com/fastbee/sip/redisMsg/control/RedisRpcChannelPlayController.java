package com.fastbee.sip.redisMsg.control;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fastbee.common.extend.config.SysSipConfig;
import com.fastbee.common.extend.config.redis.RedisRpcConfig;
import com.fastbee.common.extend.config.redis.bean.*;
import com.fastbee.common.extend.enums.ErrorCode;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.enums.InviteType;
import com.fastbee.media.model.RecordList;
import com.fastbee.media.model.StreamInfo;
import com.fastbee.media.service.ICommonChannelService;
import com.fastbee.sip.enums.Direct;
import com.fastbee.sip.service.ISipPlayService;
import com.fastbee.sip.service.ISipPtzCmdService;

@Component
@Slf4j
@RedisRpcController("channel")
public class  RedisRpcChannelPlayController extends RpcController {
    @Autowired
    private SysSipConfig sysSipConfig;

    @Autowired
    private ICommonChannelService commonChannelService;

    @Autowired
    private ISipPlayService playService;

    @Autowired
    private ISipPtzCmdService ptzCmdService;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;


    private void sendResponse(RedisRpcResponse response) {
        log.info("[redis-rpc] >> {}", response);
        response.setToId(sysSipConfig.getServerId());
        RedisRpcMessage message = new RedisRpcMessage();
        message.setResponse(response);
        redisTemplate.convertAndSend(RedisRpcConfig.REDIS_REQUEST_CHANNEL_KEY, message);
    }

    /**
     * 点播国标设备
     */
    @RedisRpcMapping("play")
    public RedisRpcResponse playChannel(RedisRpcRequest request) {
        JSONObject paramJson = JSONObject.parseObject(request.getParam().toString());
        String channelId = paramJson.getString("channelId");
        Boolean record = paramJson.getBoolean("record");

        RedisRpcResponse response = request.getResponse();
        if (StringUtils.isBlank(channelId)) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }
        // 获取对应的设备和通道信息
        CommonChannel channel = commonChannelService.selectCommonChannelByChannelId(channelId);
        if (channel == null) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }
        StreamInfo stream = playService.play(channel.getDeviceId(), channel.getChannelId(), record);
        response.setStatusCode(ErrorCode.SUCCESS.getCode());
        response.setBody(stream);
        // 手动发送结果
        sendResponse(response);
        return response;
    }

    /**
     * 点播国标设备
     */
    @RedisRpcMapping("queryRecordInfo")
    public RedisRpcResponse queryRecordInfo(RedisRpcRequest request) {
        JSONObject paramJson = JSONObject.parseObject(request.getParam().toString());
        String channelId = paramJson.getString("channelId");
        String startTime = paramJson.getString("startTime");
        String endTime = paramJson.getString("endTime");
        RedisRpcResponse response = request.getResponse();

        // 获取对应的设备和通道信息
        CommonChannel channel = commonChannelService.selectCommonChannelByChannelId(channelId);
        if (StringUtils.isBlank(channelId)) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }
        RecordList recordList = playService.listDevRecord(channel.getDeviceId(), channel.getChannelId(), startTime, endTime);
        response.setStatusCode(ErrorCode.SUCCESS.getCode());
        response.setBody(recordList);
        // 手动发送结果
        sendResponse(response);
        return response;
    }

    /**
     * 录像回放国标设备
     */
    @RedisRpcMapping("playback")
    public RedisRpcResponse playbackChannel(RedisRpcRequest request) {
        JSONObject paramJson = JSONObject.parseObject(request.getParam().toString());
        String channelId = paramJson.getString("channelId");
        String startTime = paramJson.getString("startTime");
        String endTime = paramJson.getString("endTime");
        RedisRpcResponse response = request.getResponse();

        if (StringUtils.isBlank(channelId)) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }
        // 获取对应的设备和通道信息
        CommonChannel channel = commonChannelService.selectCommonChannelByChannelId(channelId);
        if (channel == null) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }
        StreamInfo stream = playService.playback(channel.getDeviceId(), channel.getChannelId(), startTime, endTime);
        response.setStatusCode(ErrorCode.SUCCESS.getCode());
        response.setBody(stream);
        // 手动发送结果
        sendResponse(response);
        return response;

    }

    /**
     * 停止点播国标设备
     */
    @RedisRpcMapping("stop")
    public RedisRpcResponse stop(RedisRpcRequest request) {
        JSONObject jsonObject = JSONObject.parseObject(request.getParam().toString());
        RedisRpcResponse response = request.getResponse();
        String channelId = jsonObject.getString("channelId");
        String stream = jsonObject.getString("stream");
        InviteType type = jsonObject.getObject("inviteSessionType", InviteType.class);
        if (StringUtils.isBlank(channelId) || StringUtils.isBlank(stream)) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }

        CommonChannel channel = commonChannelService.selectCommonChannelByChannelId(channelId);
        if (channel == null) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }
        playService.closeStream(channel.getDeviceId(), channel.getChannelId(), stream, true);
        response.setStatusCode(ErrorCode.SUCCESS.getCode());
        // 手动发送结果
        sendResponse(response);
        return response;
    }

    @RedisRpcMapping("closeStream")
    public RedisRpcResponse closeStream(RedisRpcRequest request) {
        JSONObject jsonObject = JSONObject.parseObject(request.getParam().toString());
        RedisRpcResponse response = request.getResponse();
        String channelId = jsonObject.getString("channelId");
        String streamId = jsonObject.getString("streamId");
        Boolean check = jsonObject.getBoolean("check");

        if (StringUtils.isBlank(channelId) || StringUtils.isBlank(streamId)) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }

        CommonChannel channel = commonChannelService.selectCommonChannelByChannelId(channelId);
        if (channel == null) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }
        playService.closeStream(channel.getDeviceId(), channel.getChannelId(), streamId, check);
        response.setStatusCode(ErrorCode.SUCCESS.getCode());
        // 手动发送结果
        sendResponse(response);
        return response;
    }

    /**
     * 暂停录像回放
     */
    @RedisRpcMapping("playbackPause")
    public RedisRpcResponse playbackPause(RedisRpcRequest request) {
        JSONObject paramJson = JSONObject.parseObject(request.getParam().toString());
        String channelId = paramJson.getString("channelId");
        String streamId = paramJson.getString("streamId");
        RedisRpcResponse response = request.getResponse();
        if (streamId == null) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }
        CommonChannel channel = commonChannelService.selectCommonChannelByChannelId(channelId);
        if (channel == null) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }
        playService.playbackPause(channel.getDeviceId(), channelId, streamId);
        response.setStatusCode(ErrorCode.SUCCESS.getCode());
        return response;
    }

    /**
     * 恢复录像回放
     */
    @RedisRpcMapping("playbackResume")
    public RedisRpcResponse playbackResume(RedisRpcRequest request) {
        JSONObject paramJson = JSONObject.parseObject(request.getParam().toString());
        String channelId = paramJson.getString("channelId");
        String streamId = paramJson.getString("streamId");
        RedisRpcResponse response = request.getResponse();

        if (streamId == null) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }
        CommonChannel channel = commonChannelService.selectCommonChannelByChannelId(channelId);
        if (channel == null) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }
        playService.playbackReplay(channel.getDeviceId(), channelId, streamId);
        response.setStatusCode(ErrorCode.SUCCESS.getCode());
        return response;
    }

    @RedisRpcMapping("playbackSeek")
    public RedisRpcResponse playbackSeek(RedisRpcRequest request) {
        JSONObject paramJson = JSONObject.parseObject(request.getParam().toString());
        String channelId = paramJson.getString("channelId");
        String streamId = paramJson.getString("streamId");
        Long seektime = paramJson.getLong("seektime");
        RedisRpcResponse response = request.getResponse();

        if (streamId == null) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }
        CommonChannel channel = commonChannelService.selectCommonChannelByChannelId(channelId);
        if (channel == null) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }
        playService.playbackSeek(channel.getDeviceId(), channelId, streamId, seektime);
        response.setStatusCode(ErrorCode.SUCCESS.getCode());
        return response;
    }

    @RedisRpcMapping("playbackSpeed")
    public RedisRpcResponse playbackSpeed(RedisRpcRequest request) {
        JSONObject paramJson = JSONObject.parseObject(request.getParam().toString());
        String channelId = paramJson.getString("channelId");
        String streamId = paramJson.getString("streamId");
        Double speed = paramJson.getDouble("speed");
        RedisRpcResponse response = request.getResponse();

        if (streamId == null) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }
        CommonChannel channel = commonChannelService.selectCommonChannelByChannelId(channelId);
        if (channel == null) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }
        playService.playbackSpeed(channel.getDeviceId(), channelId, streamId, speed);
        response.setStatusCode(ErrorCode.SUCCESS.getCode());
        return response;
    }

    @RedisRpcMapping("ptz")
    public RedisRpcResponse ptz(RedisRpcRequest request) {
        JSONObject paramJson = JSONObject.parseObject(request.getParam().toString());
        String channelId = paramJson.getString("channelId");
        JSONObject input = paramJson.getJSONObject("input");
        RedisRpcResponse response = request.getResponse();
        if (channelId == null) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }
        CommonChannel channel = commonChannelService.selectCommonChannelByChannelId(channelId);
        if (channel == null) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }
        Direct direct;
        Integer leftRight = input.getInteger("leftRight");
        Integer upDown = input.getInteger("upDown");
        Integer inOut = input.getInteger("inOut");
        Integer moveSpeed = input.getInteger("moveSpeed");
        if (leftRight == 1) {
            direct = Direct.RIGHT;
        } else if (leftRight == 2) {
            direct = Direct.LEFT;
        } else if (upDown == 1) {
            direct = Direct.UP;
        } else if (upDown == 2) {
            direct = Direct.DOWN;
        } else if (inOut == 1) {
            direct = Direct.ZOOM_IN;
        } else if (inOut == 2) {
            direct = Direct.ZOOM_OUT;
        } else {
            direct = Direct.STOP;
        }

        ptzCmdService.directPtzCmd(channel.getDeviceId(), channelId, direct, moveSpeed);
        response.setStatusCode(ErrorCode.SUCCESS.getCode());
        return response;
    }


    /**
     * 录像回放国标设备
     */
    @RedisRpcMapping("download")
    public RedisRpcResponse downloadChannel(RedisRpcRequest request) {
        JSONObject paramJson = JSONObject.parseObject(request.getParam().toString());
        String channelId = paramJson.getString("channelId");
        String startTime = paramJson.getString("startTime");
        String endTime = paramJson.getString("endTime");
        int downloadSpeed = paramJson.getIntValue("downloadSpeed");
        RedisRpcResponse response = request.getResponse();
        CommonChannel channel = commonChannelService.selectCommonChannelByChannelId(channelId);
        if (channel == null) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }
        StreamInfo stream = playService.download(channel.getDeviceId(), channel.getChannelId(), startTime, endTime, downloadSpeed);
        response.setStatusCode(ErrorCode.SUCCESS.getCode());
        response.setBody(stream);
        // 手动发送结果
        sendResponse(response);
        return response;
    }

    /**
     * 云台控制
     */
    @RedisRpcMapping("ptz/frontEndCommand")
    public RedisRpcResponse frontEndCommand(RedisRpcRequest request) {
        JSONObject paramJson = JSONObject.parseObject(request.getParam().toString());
        String channelId = paramJson.getString("channelId");
        int cmdCode = paramJson.getIntValue("cmdCode");
        int parameter1 = paramJson.getIntValue("parameter1");
        int parameter2 = paramJson.getIntValue("parameter2");
        int combindCode2 = paramJson.getIntValue("combindCode2");

        RedisRpcResponse response = request.getResponse();

        if (StringUtils.isBlank(channelId) || cmdCode < 0 || parameter1 < 0 || parameter2 < 0 || combindCode2 < 0) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }

        CommonChannel channel = commonChannelService.selectCommonChannelByChannelId(channelId);
        if (channel == null) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }
        ptzCmdService.frontEndCommand(channel.getDeviceId(), channel.getChannelId(), cmdCode, parameter1, parameter2, combindCode2);
        response.setStatusCode(ErrorCode.SUCCESS.getCode());
        return response;
    }


    @RedisRpcMapping("ptz/presetQuery")
    public RedisRpcResponse queryPreset(RedisRpcRequest request) {
        JSONObject paramJson = JSONObject.parseObject(request.getParam().toString());
        //String deviceId = paramJson.getString("deviceId");
        String channelId = paramJson.getString("channelId");
        RedisRpcResponse response = request.getResponse();
        if (channelId == null) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }

        CommonChannel channel = commonChannelService.selectCommonChannelByChannelId(channelId);
        if (channel == null) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }
        ptzCmdService.presetQuery(channel.getDeviceId(), channelId);
        response.setStatusCode(ErrorCode.SUCCESS.getCode());
        return response;
    }
}
