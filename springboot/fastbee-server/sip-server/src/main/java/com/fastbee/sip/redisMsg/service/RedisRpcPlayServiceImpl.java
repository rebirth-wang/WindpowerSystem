package com.fastbee.sip.redisMsg.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.config.SysSipConfig;
import com.fastbee.common.extend.config.redis.RedisRpcConfig;
import com.fastbee.common.extend.config.redis.bean.RedisRpcRequest;
import com.fastbee.common.extend.config.redis.bean.RedisRpcResponse;
import com.fastbee.common.extend.core.media.ErrorCallback;
import com.fastbee.common.extend.enums.ErrorCode;
import com.fastbee.media.enums.InviteType;
import com.fastbee.media.model.Preset;
import com.fastbee.media.model.PtzInput;
import com.fastbee.media.model.RecordList;
import com.fastbee.media.model.StreamInfo;
import com.fastbee.sip.redisMsg.IRedisRpcPlayService;

@Slf4j
@Service
public class RedisRpcPlayServiceImpl implements IRedisRpcPlayService {

    @Autowired
    private RedisRpcConfig redisRpcConfig;

    @Autowired
    private SysSipConfig sysSipConfig;


    private RedisRpcRequest buildRequest(String uri, Object param) {
        RedisRpcRequest request = new RedisRpcRequest();
        request.setFromId(sysSipConfig.getServerId());
        request.setParam(param);
        request.setUri(uri);
        return request;
    }

    @Override
    public StreamInfo play(String serverId, String channelId, Boolean record, ErrorCallback<StreamInfo> callback) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("channelId", channelId);
        jsonObject.put("record", record);
        RedisRpcRequest request = buildRequest("channel/play", jsonObject);
        request.setToId(serverId);
        RedisRpcResponse response = redisRpcConfig.request(request, sysSipConfig.getPlayTimeout(), TimeUnit.MILLISECONDS);
        if (response == null) {
            if (callback != null) {
                callback.run(ErrorCode.ERROR100.getCode(), ErrorCode.ERROR100.getMsg(), null);
            }
        } else {
            if (response.getStatusCode() == ErrorCode.SUCCESS.getCode()) {
                StreamInfo stream = JSON.parseObject(response.getBody().toString(), StreamInfo.class);
                if (callback != null) {
                    callback.run(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMsg(), stream);
                }
                return stream;
            } else {
                if (callback != null) {
                    callback.run(response.getStatusCode(), response.getBody().toString(), null);
                }
            }
        }
        return null;
    }

    @Override
    public Boolean stop(String serverId, InviteType type, String channelId, String stream) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("channelId", channelId);
        jsonObject.put("stream", stream);
        jsonObject.put("inviteSessionType", type);
        RedisRpcRequest request = buildRequest("channel/stop", jsonObject.toJSONString());
        request.setToId(serverId);
        RedisRpcResponse response = redisRpcConfig.request(request, 50, TimeUnit.MICROSECONDS);
        if (response == null) {
            throw new ServiceException(ErrorCode.ERROR100.getCode(), ErrorCode.ERROR100.getMsg());
        } else {
            if (response.getStatusCode() != ErrorCode.SUCCESS.getCode()) {
                throw new ServiceException(ErrorCode.ERROR100.getCode(), ErrorCode.ERROR100.getMsg());
            }
            return true;
        }
    }

    @Override
    public Boolean closeStream(String serverId, String channelId, String streamId, Boolean check) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("channelId", channelId);
        jsonObject.put("streamId", streamId);
        jsonObject.put("check", check);
        RedisRpcRequest request = buildRequest("channel/closeStream", jsonObject.toJSONString());
        request.setToId(serverId);
        RedisRpcResponse response = redisRpcConfig.request(request, 50, TimeUnit.MICROSECONDS);
        if (response == null) {
            throw new ServiceException(ErrorCode.ERROR100.getCode(), ErrorCode.ERROR100.getMsg());
        } else {
            if (response.getStatusCode() != ErrorCode.SUCCESS.getCode()) {
                throw new ServiceException(ErrorCode.ERROR100.getCode(), ErrorCode.ERROR100.getMsg());
            }
            return true;
        }
    }

    @Override
    public RecordList queryRecordInfo(String serverId, String channelId, String startTime, String endTime, ErrorCallback<RecordList> callback) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("channelId", channelId);
        jsonObject.put("startTime", startTime);
        jsonObject.put("endTime", endTime);
        RedisRpcRequest request = buildRequest("channel/queryRecordInfo", jsonObject);
        request.setToId(serverId);
        RedisRpcResponse response = redisRpcConfig.request(request, sysSipConfig.getPlayTimeout(), TimeUnit.MILLISECONDS);
        if (response == null) {
            if (callback != null) {
                callback.run(ErrorCode.ERROR100.getCode(), ErrorCode.ERROR100.getMsg(), null);
            }
        } else {
            if (response.getStatusCode() == ErrorCode.SUCCESS.getCode()) {
                RecordList recordList = JSON.parseObject(response.getBody().toString(), RecordList.class);
                if (callback != null) {
                    callback.run(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMsg(), recordList);
                }
                return recordList;
            } else {
                if (callback != null) {
                    callback.run(response.getStatusCode(), response.getBody().toString(), null);
                }
            }
        }
        return null;
    }

    @Override
    public Boolean ptz(String serverId, String channelId, PtzInput input) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("channelId", channelId);
        jsonObject.put("ptz", input);
        RedisRpcRequest request = buildRequest("channel/ptz", jsonObject.toString());
        request.setToId(serverId);
        RedisRpcResponse response = redisRpcConfig.request(request, 5, TimeUnit.SECONDS);
        if (response == null) {
            log.info("[RPC ptz] 失败, streamId: {}", channelId);
            return false;
        } else {
            if (response.getStatusCode() != ErrorCode.SUCCESS.getCode()) {
                log.info("[RPC ptz] 失败, {},  streamId: {}", response.getBody(), channelId);
                return false;
            }
        }
        return true;
    }

    @Override
    public StreamInfo playback(String serverId, String channelId, String startTime, String endTime, ErrorCallback<StreamInfo> callback) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("channelId", channelId);
        jsonObject.put("startTime", startTime);
        jsonObject.put("endTime", endTime);
        RedisRpcRequest request = buildRequest("channel/playback", jsonObject.toString());
        request.setToId(serverId);
        RedisRpcResponse response = redisRpcConfig.request(request, sysSipConfig.getPlayTimeout(), TimeUnit.MILLISECONDS);
        if (response == null) {
            if (callback != null) {
                callback.run(ErrorCode.ERROR100.getCode(), ErrorCode.ERROR100.getMsg(), null);
            }
        } else {
            if (response.getStatusCode() == ErrorCode.SUCCESS.getCode()) {
                StreamInfo stream = JSON.parseObject(response.getBody().toString(), StreamInfo.class);
                if (callback != null) {
                    callback.run(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMsg(), stream);
                }
                return stream;
            } else {
                if (callback != null) {
                    callback.run(response.getStatusCode(), response.getBody().toString(), null);
                }
            }
        }
        return null;
    }

    @Override
    public Boolean playbackPause(String serverId, String channelId, String streamId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("streamId", streamId);
        jsonObject.put("channelId", channelId);
        RedisRpcRequest request = buildRequest("channel/playbackPause", jsonObject.toString());
        request.setToId(serverId);
        RedisRpcResponse response = redisRpcConfig.request(request, 5, TimeUnit.SECONDS);
        if (response == null) {
            log.info("[RPC 暂停回放] 失败, streamId: {}", streamId);
            return false;
        } else {
            if (response.getStatusCode() != ErrorCode.SUCCESS.getCode()) {
                log.info("[RPC 暂停回放] 失败, {},  streamId: {}", response.getBody(), streamId);
                return false;
            }
        }
        return true;
    }

    @Override
    public Boolean playbackResume(String serverId, String channelId, String streamId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("streamId", streamId);
        jsonObject.put("channelId", channelId);
        RedisRpcRequest request = buildRequest("channel/playbackResume", jsonObject.toString());
        request.setToId(serverId);
        RedisRpcResponse response = redisRpcConfig.request(request, 5, TimeUnit.SECONDS);
        if (response == null) {
            log.info("[RPC 恢复回放] 失败, streamId: {}", streamId);
            return false;
        } else {
            if (response.getStatusCode() != ErrorCode.SUCCESS.getCode()) {
                log.info("[RPC 恢复回放] 失败, {},  streamId: {}", response.getBody(), streamId);
                return false;
            }
        }
        return true;
    }

    @Override
    public Boolean playbackSeek(String serverId, String channelId, String streamId, Long seektime) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("channelId", channelId);
        jsonObject.put("streamId", streamId);
        jsonObject.put("seektime", seektime);
        RedisRpcRequest request = buildRequest("channel/playbackSeek", jsonObject.toString());
        request.setToId(serverId);
        RedisRpcResponse response = redisRpcConfig.request(request, 5, TimeUnit.SECONDS);
        if (response == null) {
            log.info("[RPC 回放Seek] 失败, streamId: {}", streamId);
            return false;
        } else {
            if (response.getStatusCode() != ErrorCode.SUCCESS.getCode()) {
                log.info("[RPC 回放Seek] 失败, {},  streamId: {}", response.getBody(), streamId);
                return false;
            }
        }
        return true;
    }

    @Override
    public Boolean playbackSpeed(String serverId, String channelId, String streamId, Double speed) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("channelId", channelId);
        jsonObject.put("streamId", streamId);
        jsonObject.put("speed", speed);
        RedisRpcRequest request = buildRequest("channel/playbackSpeed", jsonObject.toString());
        request.setToId(serverId);
        RedisRpcResponse response = redisRpcConfig.request(request, 5, TimeUnit.SECONDS);
        if (response == null) {
            log.info("[RPC 回放Speed] 失败, streamId: {}", streamId);
            return false;
        } else {
            if (response.getStatusCode() != ErrorCode.SUCCESS.getCode()) {
                log.info("[RPC 回放Speed] 失败, {},  streamId: {}", response.getBody(), streamId);
                return false;
            }
        }
        return true;
    }

    @Override
    public StreamInfo download(String serverId, String channelId, String startTime, String endTime, int downloadSpeed, ErrorCallback<StreamInfo> callback) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("channelId", channelId);
        jsonObject.put("startTime", startTime);
        jsonObject.put("endTime", endTime);
        jsonObject.put("downloadSpeed", downloadSpeed);
        RedisRpcRequest request = buildRequest("channel/download", jsonObject.toString());
        request.setToId(serverId);
        RedisRpcResponse response = redisRpcConfig.request(request, sysSipConfig.getPlayTimeout(), TimeUnit.MILLISECONDS);
        if (response == null) {
            if (callback != null) {
                callback.run(ErrorCode.ERROR100.getCode(), ErrorCode.ERROR100.getMsg(), null);
            }
        } else {
            if (response.getStatusCode() == ErrorCode.SUCCESS.getCode()) {
                StreamInfo stream = JSON.parseObject(response.getBody().toString(), StreamInfo.class);
                if (callback != null) {
                    callback.run(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMsg(), stream);
                }
                return stream;
            } else {
                if (callback != null) {
                    callback.run(response.getStatusCode(), response.getBody().toString(), null);
                }
            }
        }
        return null;
    }

    @Override
    public Boolean frontEndCommand(String serverId, String channelId, int cmdCode, int parameter1, int parameter2, int combindCode2) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("channelId", channelId);
        jsonObject.put("cmdCode", cmdCode);
        jsonObject.put("parameter1", parameter1);
        jsonObject.put("parameter2", parameter2);
        jsonObject.put("combindCode2", combindCode2);
        RedisRpcRequest request = buildRequest("channel/ptz/frontEndCommand", jsonObject.toString());
        request.setToId(serverId);
        RedisRpcResponse response = redisRpcConfig.request(request, sysSipConfig.getPlayTimeout(), TimeUnit.MILLISECONDS);
        if (response == null) {
            log.info("[RPC 前端命令] 失败, channelId: {}", channelId);
            return false;
        } else {
            if (response.getStatusCode() != ErrorCode.SUCCESS.getCode()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<Preset> queryPresetList(String serverId, String channelId, ErrorCallback<List<Preset>> callback) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("channelId", channelId);
        RedisRpcRequest request = buildRequest("channel/ptz/presetQuery", jsonObject.toString());
        request.setToId(serverId);
        RedisRpcResponse response = redisRpcConfig.request(request, sysSipConfig.getPlayTimeout(), TimeUnit.MILLISECONDS);
        if (response == null) {
            log.info("[RPC 前端命令] 失败, channelId: {}", channelId);
            return null;
        } else {
            if (response.getStatusCode() == ErrorCode.SUCCESS.getCode()) {
                List<Preset> presets = JSON.parseObject(response.getBody().toString(), new TypeReference<List<Preset>>() {});
                if (callback != null) {
                    callback.run(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMsg(), presets);
                }
                return presets;
            } else {
                if (callback != null) {
                    callback.run(response.getStatusCode(), response.getBody().toString(), null);
                }
            }
        }
        return null;
    }

    @Override
    public void playPush(String serverId, Integer id, ErrorCallback<StreamInfo> callback) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        RedisRpcRequest request = buildRequest("streamPush/play", jsonObject);
        request.setToId(serverId);
        RedisRpcResponse response = redisRpcConfig.request(request, sysSipConfig.getPlayTimeout(), TimeUnit.SECONDS);
        if (response == null) {
            if (callback != null) {
                callback.run(ErrorCode.ERROR100.getCode(), ErrorCode.ERROR100.getMsg(), null);
            }
        } else {
            if (response.getStatusCode() == ErrorCode.SUCCESS.getCode()) {
                StreamInfo stream = JSON.parseObject(response.getBody().toString(), StreamInfo.class);
                if (callback != null) {
                    callback.run(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMsg(), stream);
                }
            } else {
                if (callback != null) {
                    callback.run(response.getStatusCode(), response.getBody().toString(), null);
                }
            }
        }
    }

    @Override
    public void playProxy(String serverId, int id, ErrorCallback<StreamInfo> callback) {
        RedisRpcRequest request = buildRequest("streamProxy/play", id);
        request.setToId(serverId);
        RedisRpcResponse response = redisRpcConfig.request(request, sysSipConfig.getPlayTimeout(), TimeUnit.SECONDS);
        if (response == null) {
            if (callback != null) {
                callback.run(ErrorCode.ERROR100.getCode(), ErrorCode.ERROR100.getMsg(), null);
            }
        } else {
            if (response.getStatusCode() == ErrorCode.SUCCESS.getCode()) {
                StreamInfo stream = JSON.parseObject(response.getBody().toString(), StreamInfo.class);
                if (callback != null) {
                    callback.run(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMsg(), stream);
                }
            } else {
                if (callback != null) {
                    callback.run(response.getStatusCode(), response.getBody().toString(), null);
                }
            }
        }
    }

    @Override
    public void stopProxy(String serverId, int id) {
        RedisRpcRequest request = buildRequest("streamProxy/stop", id);
        RedisRpcResponse response = redisRpcConfig.request(request, sysSipConfig.getPlayTimeout(), TimeUnit.SECONDS);
        if (response != null && response.getStatusCode() == ErrorCode.SUCCESS.getCode()) {
            log.info("[rpc 拉流代理] 停止成功： id: {}", id);
        } else {
            log.info("[rpc 拉流代理] 停止失败 id: {}", id);
        }
    }

    @Override
    public String audioBroadcast(String serverId, String deviceId, String channelDeviceId, Boolean broadcastMode) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceId", deviceId);
        jsonObject.put("channelDeviceId", channelDeviceId);
        jsonObject.put("broadcastMode", broadcastMode);
        RedisRpcRequest request = buildRequest("devicePlay/audioBroadcast", jsonObject.toString());
        request.setToId(serverId);
        RedisRpcResponse response = redisRpcConfig.request(request, sysSipConfig.getPlayTimeout(), TimeUnit.SECONDS);
        if (response != null && response.getStatusCode() == ErrorCode.SUCCESS.getCode()) {
            return response.getBody().toString();
        }
        return "";
    }
}

