package com.fastbee.sip.redisMsg.service;

import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.extend.config.SysSipConfig;
import com.fastbee.common.extend.config.redis.RedisRpcConfig;
import com.fastbee.common.extend.config.redis.bean.RedisRpcRequest;
import com.fastbee.common.extend.config.redis.bean.RedisRpcResponse;
import com.fastbee.media.model.SendRtpItem;
import com.fastbee.sip.domain.SipDevice;
import com.fastbee.sip.redisMsg.IRedisRpcService;

@Slf4j
@Service
public class RedisRpcServiceImpl implements IRedisRpcService {
    @Autowired
    private RedisRpcConfig redisRpcConfig;

    @Autowired
    private SysSipConfig sysSipConfig;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    private RedisRpcRequest buildRequest(String uri, Object param) {
        RedisRpcRequest request = new RedisRpcRequest();
        request.setFromId(sysSipConfig.getServerId());
        request.setParam(param);
        request.setUri(uri);
        return request;
    }

    @Override
    public void rtpSendStopped(String callId) {
        SendRtpItem sendRtpItem = (SendRtpItem)redisTemplate.opsForValue().get(callId);
        if (sendRtpItem == null) {
            log.info("[停止sip服务器监听流上线] 未找到redis中的发流信息， key：{}", callId);
            return;
        }
        RedisRpcRequest request = buildRequest("streamPush/rtpSendStopped", callId);
        request.setToId(sendRtpItem.getMediaServerId());
        redisRpcConfig.request(request, 10, TimeUnit.MILLISECONDS);
    }

    @Override
    public void removeCallback(long key) {
        redisRpcConfig.removeCallback(key);
    }

    @Override
    public void subscribeCatalog(int id, int cycle) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("cycle", cycle);
        RedisRpcRequest request = buildRequest("device/subscribeCatalog", jsonObject);
        redisRpcConfig.request(request, 10, TimeUnit.MILLISECONDS);
    }

    @Override
    public void subscribeMobilePosition(int id, int cycle, int interval) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("cycle", cycle);
        jsonObject.put("interval", cycle);
        RedisRpcRequest request = buildRequest("device/subscribeMobilePosition", jsonObject);
        redisRpcConfig.request(request, 10, TimeUnit.MILLISECONDS);
    }

    @Override
    public AjaxResult devicesSync(String serverId, String deviceId) {
        RedisRpcRequest request = buildRequest("device/devicesSync", deviceId);
        request.setToId(serverId);
        RedisRpcResponse response = redisRpcConfig.request(request, 100, TimeUnit.MILLISECONDS);
        return JSON.parseObject(response.getBody().toString(), AjaxResult.class);
    }

    @Override
    public AjaxResult deviceConfigQuery(String serverId, SipDevice device, String channelId, String configType) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("device", device.getDeviceId());
        jsonObject.put("channelId", channelId);
        jsonObject.put("configType", configType);
        RedisRpcRequest request = buildRequest("device/deviceConfigQuery", jsonObject);
        request.setToId(serverId);
        RedisRpcResponse response = redisRpcConfig.request(request, 50, TimeUnit.MILLISECONDS);
        return JSON.parseObject(response.getBody().toString(), AjaxResult.class);
    }

    @Override
    public AjaxResult deviceStatus(String serverId, SipDevice device) {
        RedisRpcRequest request = buildRequest("device/deviceStatus", device.getDeviceId());
        request.setToId(serverId);
        RedisRpcResponse response = redisRpcConfig.request(request, 50, TimeUnit.MILLISECONDS);
        return JSON.parseObject(response.getBody().toString(), AjaxResult.class);
    }

    @Override
    public AjaxResult deviceInfo(String serverId, SipDevice device) {
        RedisRpcRequest request = buildRequest("device/info", device.getDeviceId());
        request.setToId(serverId);
        RedisRpcResponse response = redisRpcConfig.request(request, 50, TimeUnit.MILLISECONDS);
        return JSON.parseObject(response.getBody().toString(), AjaxResult.class);
    }

    @Override
    public AjaxResult queryPreset(String serverId, SipDevice device, String channelId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("device", device.getDeviceId());
        jsonObject.put("channelId", channelId);
        RedisRpcRequest request = buildRequest("device/queryPreset", jsonObject);
        request.setToId(serverId);
        RedisRpcResponse response = redisRpcConfig.request(request, 60000, TimeUnit.MILLISECONDS);
        return JSON.parseObject(response.getBody().toString(), AjaxResult.class);
    }

    @Override
    public AjaxResult alarm(String serverId, SipDevice device, String startPriority, String endPriority,
                                   String alarmMethod, String alarmType, String startTime, String endTime) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("device", device.getDeviceId());
        jsonObject.put("startPriority", startPriority);
        jsonObject.put("endPriority", endPriority);
        jsonObject.put("alarmMethod", alarmMethod);
        jsonObject.put("alarmType", alarmType);
        jsonObject.put("startTime", startTime);
        jsonObject.put("endTime", endTime);
        RedisRpcRequest request = buildRequest("device/alarm", jsonObject);
        request.setToId(serverId);
        RedisRpcResponse response = redisRpcConfig.request(request, 50, TimeUnit.MILLISECONDS);
        return JSON.parseObject(response.getBody().toString(), AjaxResult.class);
    }
}
