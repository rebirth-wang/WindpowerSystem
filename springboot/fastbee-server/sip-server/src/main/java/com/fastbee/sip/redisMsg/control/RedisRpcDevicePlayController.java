package com.fastbee.sip.redisMsg.control;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fastbee.common.extend.config.SysSipConfig;
import com.fastbee.common.extend.config.redis.RedisRpcConfig;
import com.fastbee.common.extend.config.redis.bean.*;
import com.fastbee.common.extend.enums.ErrorCode;
import com.fastbee.sip.domain.SipDevice;
import com.fastbee.sip.service.ISipDeviceService;
import com.fastbee.sip.service.ISipTalkService;

@Component
@Slf4j
@RedisRpcController("devicePlay")
public class RedisRpcDevicePlayController extends RpcController {

    @Autowired
    private SysSipConfig sysSipConfig;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private ISipDeviceService deviceService;

    @Autowired
    private ISipTalkService talkService;

    private void sendResponse(RedisRpcResponse response){
        log.info("[redis-rpc] >> {}", response);
        response.setToId(sysSipConfig.getServerId());
        RedisRpcMessage message = new RedisRpcMessage();
        message.setResponse(response);
        redisTemplate.convertAndSend(RedisRpcConfig.REDIS_REQUEST_CHANNEL_KEY, message);
    }

    /**
     * 获取通道同步状态
     */
    @RedisRpcMapping("audioBroadcast")
    public RedisRpcResponse audioBroadcast(RedisRpcRequest request) {
        JSONObject paramJson = JSON.parseObject(request.getParam().toString());
        String deviceId = paramJson.getString("deviceId");
        String channelId = paramJson.getString("channelId");
        Boolean broadcastMode = paramJson.getBoolean("broadcastMode");

        SipDevice device = deviceService.selectSipDeviceBySipId(deviceId);
        RedisRpcResponse response = request.getResponse();
        if (device == null || !sysSipConfig.getServerId().equals(device.getServerId())) {
            response.setStatusCode(ErrorCode.ERROR400.getCode());
            response.setBody("param error");
            return response;
        }
        String ret = talkService.broadcast(deviceId, channelId);
        response.setStatusCode(ErrorCode.SUCCESS.getCode());
        response.setBody(ret);
        // 手动发送结果
        sendResponse(response);
        return response;
    }

}
