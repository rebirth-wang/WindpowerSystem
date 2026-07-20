package com.fastbee.media.service.impl;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.stereotype.Service;

import com.fastbee.common.exception.ServiceException;
import com.fastbee.media.constant.MediaConstant;
import com.fastbee.media.domain.MediaServer;
import com.fastbee.media.model.InviteMsgInfo;
import com.fastbee.media.model.SendRtpItem;
import com.fastbee.media.model.VideoSessionInfo;
import com.fastbee.media.service.ISendRtpServerService;
import com.fastbee.media.util.JsonUtil;
import com.fastbee.media.util.ZlmRtpUtils;

@Service
@Slf4j
public class SendRtpServerServiceImpl implements ISendRtpServerService {
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    private ZlmRtpUtils zlmRtpUtils;

    @Override
    public SendRtpItem createSendRtpInfo(MediaServer mediaServer, String ip, Integer port, String ssrc, String requesterId,
                                         String deviceId, String channelId, Boolean isTcp, Boolean rtcp) {
        int localPort = getNextPort(mediaServer);
        if (localPort <= 0) {
            return null;
        }
        return SendRtpItem.getInstance(localPort, mediaServer, ip, port, ssrc, deviceId, null, channelId,
                isTcp, rtcp, mediaServer.getServerId());
    }

    @Override
    public SendRtpItem createSendRtpInfo(MediaServer mediaServer, String ip, Integer port, String ssrc, String platformId,
                                         String app, String stream, String channelId, Boolean tcp, Boolean rtcp){

        int localPort = getNextPort(mediaServer);
        if (localPort <= 0) {
            throw new ServiceException(500, "server internal error");
        }
        SendRtpItem sendRtpInfo = SendRtpItem.getInstance(localPort, mediaServer, ip, port, ssrc, null, platformId, channelId,
                tcp, rtcp, mediaServer.getServerId());
        if (sendRtpInfo == null) {
            return null;
        }
        sendRtpInfo.setApp(app);
        sendRtpInfo.setStream(stream);
        return sendRtpInfo;
    }

    @Override
    public void update(SendRtpItem sendRtpItem) {
        redisTemplate.opsForHash().put(MediaConstant.REDIS.SEND_RTP_INFO_CALLID, sendRtpItem.getCallId(), sendRtpItem);
        redisTemplate.opsForHash().put(MediaConstant.REDIS.SEND_RTP_INFO_STREAM + sendRtpItem.getStream(), sendRtpItem.getTargetId(), sendRtpItem);
        redisTemplate.opsForHash().put(MediaConstant.REDIS.SEND_RTP_INFO_CHANNEL + sendRtpItem.getChannelId(), sendRtpItem.getTargetId(), sendRtpItem);
    }

    @Override
    public SendRtpItem queryByChannelId(String channelId, String targetId) {
        String key = MediaConstant.REDIS.SEND_RTP_INFO_CHANNEL + channelId;
        return JsonUtil.redisHashJsonToObject(redisTemplate, key, targetId, SendRtpItem.class);
    }

    @Override
    public SendRtpItem queryByCallId(String callId) {
        String key = MediaConstant.REDIS.SEND_RTP_INFO_CALLID;
        return (SendRtpItem)redisTemplate.opsForHash().get(key, callId);
    }

    @Override
    public SendRtpItem queryByStream(String stream, String targetId) {
        String key = MediaConstant.REDIS.SEND_RTP_INFO_STREAM + stream;
        return JsonUtil.redisHashJsonToObject(redisTemplate, key, targetId, SendRtpItem.class);
    }

    @Override
    public List<SendRtpItem> queryByStream(String stream) {
        String key = MediaConstant.REDIS.SEND_RTP_INFO_STREAM + stream;
        List<Object> values = redisTemplate.opsForHash().values(key);
        List<SendRtpItem> result= new ArrayList<>();
        for (Object o : values) {
            result.add((SendRtpItem) o);
        }
        return result;
    }

    /**
     * 删除RTP推送信息缓存
     */
    @Override
    public void delete(SendRtpItem sendRtpInfo) {
        if (sendRtpInfo == null) {
            return;
        }
        redisTemplate.opsForHash().delete(MediaConstant.REDIS.SEND_RTP_INFO_CALLID, sendRtpInfo.getCallId());
        redisTemplate.opsForHash().delete(MediaConstant.REDIS.SEND_RTP_INFO_STREAM + sendRtpInfo.getStream(), sendRtpInfo.getTargetId());
        redisTemplate.opsForHash().delete(MediaConstant.REDIS.SEND_RTP_INFO_CHANNEL + sendRtpInfo.getChannelId(), sendRtpInfo.getTargetId());
    }
    @Override
    public void deleteByCallId(String callId) {
        SendRtpItem sendRtpInfo = queryByCallId(callId);
        if (sendRtpInfo == null) {
            return;
        }
        delete(sendRtpInfo);
    }
    @Override
    public void deleteByStream(String stream, String targetId) {
        SendRtpItem sendRtpInfo = queryByStream(stream, targetId);
        if (sendRtpInfo == null) {
            return;
        }
        delete(sendRtpInfo);
    }

    @Override
    public void deleteByStream(String stream) {
        List<SendRtpItem> sendRtpInfos = queryByStream(stream);
        for (SendRtpItem sendRtpInfo : sendRtpInfos) {
            delete(sendRtpInfo);
        }
    }

    @Override
    public void deleteByChannel(String channelId, String targetId) {
        SendRtpItem sendRtpInfo = queryByChannelId(channelId, targetId);
        if (sendRtpInfo == null) {
            return;
        }
        delete(sendRtpInfo);
    }

    @Override
    public List<SendRtpItem> queryByChannelId(String channelId) {
        String key = MediaConstant.REDIS.SEND_RTP_INFO_CHANNEL + channelId;
        List<Object> values = redisTemplate.opsForHash().values(key);
        List<SendRtpItem> result= new ArrayList<>();
        for (Object o : values) {
            result.add((SendRtpItem) o);
        }
        return result;
    }

    @Override
    public List<SendRtpItem> queryAll() {
        String key = MediaConstant.REDIS.SEND_RTP_INFO_CALLID;
        List<Object> values = redisTemplate.opsForHash().values(key);
        List<SendRtpItem> result= new ArrayList<>();
        for (Object o : values) {
            result.add((SendRtpItem) o);
        }
        return result;
    }

    @Override
    public void startSendRtp(MediaServer mediaInfo, InviteMsgInfo inviteInfo, SendRtpItem sendRtpItem) {
        Map<String, Object> params = new HashMap<>(16);
        params.put("vhost", "__defaultVhost__");
        params.put("app", sendRtpItem.getApp());
        params.put("stream", sendRtpItem.getStream());
        params.put("ssrc", inviteInfo.getSsrc());
        params.put("src_port", sendRtpItem.getLocalPort());
        params.put("pt", sendRtpItem.getPt());
        params.put("use_ps", sendRtpItem.isUsePs() ? "1" : "0");
        params.put("only_audio", sendRtpItem.isOnlyAudio() ? "1" : "0");
        if (!sendRtpItem.isTcp()) {
            // udp模式下开启rtcp保活
            params.put("udp_rtcp_timeout", sendRtpItem.isRtcp() ? "500" : "0");
        }
        params.put("is_udp", sendRtpItem.isTcp() ? "0" : "1");
        if (sendRtpItem.isTcpActive()) {
            // params.put("recv_stream_id", sendRtpItem.getReceiveStream());
            if (sendRtpItem.getTimeout()  != null) {
                params.put("close_delay_ms", sendRtpItem.getTimeout());
            }
            zlmRtpUtils.startSendRtpPassive(mediaInfo, params);
        } else {
            params.put("dst_url", inviteInfo.getIp());
            params.put("dst_port", inviteInfo.getPort());
            zlmRtpUtils.startSendRtpStream(mediaInfo, params);
        }
    }

    /**
     * 查询某个通道是否存在上级点播（RTP推送）
     */
    @Override
    public boolean isChannelSendingRTP(String channelId) {
        List<SendRtpItem> sendRtpInfoList = queryByChannelId(channelId);
        return !sendRtpInfoList.isEmpty();
    }


    private Set<Integer> getAllSendRtpPort() {
        String key = MediaConstant.REDIS.SEND_RTP_INFO_CALLID;
        List<Object> values = redisTemplate.opsForHash().values(key);
        Set<Integer> result = new HashSet<>();
        for (Object value : values) {
            SendRtpItem sendRtpInfo = (SendRtpItem) value;
            result.add(sendRtpInfo.getPort());
        }
        return result;
    }


    @Override
    public synchronized int getNextPort(MediaServer mediaServer) {
        if (mediaServer == null) {
            log.warn("[发送端口管理] 参数错误，mediaServer为NULL");
            return -1;
        }
        String sendIndexKey = MediaConstant.REDIS.SEND_RTP_PORT + ":" +  mediaServer.getId();
        Set<Integer> sendRtpSet = getAllSendRtpPort();
        String sendRtpPortRange = mediaServer.getRtpPortRange();
        int startPort;
        int endPort;
        if (sendRtpPortRange != null) {
            String[] portArray = sendRtpPortRange.split(",");
            if (portArray.length != 2 || !NumberUtils.isParsable(portArray[0]) || !NumberUtils.isParsable(portArray[1])) {
                log.warn("{}发送端口配置格式错误，自动使用30000-30100作为端口范围", mediaServer.getId());
                startPort = 30000;
                endPort = 30100;
            }else {
                if ( Integer.parseInt(portArray[1]) - Integer.parseInt(portArray[0]) < 1) {
                    log.warn("{}发送端口配置错误,结束端口至少比开始端口大一，自动使用30000-30100作为端口范围", mediaServer.getId());
                    startPort = 30000;
                    endPort = 30100;
                }else {
                    startPort = Integer.parseInt(portArray[0]);
                    endPort = Integer.parseInt(portArray[1]);
                }
            }
        }else {
            log.warn("{}未设置发送端口默认值，自动使用30000-30100作为端口范围", mediaServer.getId());
            startPort = 30000;
            endPort = 30100;
        }
        if (redisTemplate == null || redisTemplate.getConnectionFactory() == null) {
            log.warn("{}获取redis连接信息失败", mediaServer.getId());
            return -1;
        }
        RedisAtomicInteger redisAtomicInteger = new RedisAtomicInteger(sendIndexKey , redisTemplate.getConnectionFactory());
        if (redisAtomicInteger.get() < startPort) {
            redisAtomicInteger.set(startPort);
            return startPort;
        }else {
            for (int i = 0; i < endPort - startPort; i++) {
                int port = redisAtomicInteger.getAndIncrement();
                if (port > endPort) {
                    redisAtomicInteger.set(startPort);
                    if (sendRtpSet.contains(startPort)) {
                        continue;
                    }else {
                        return startPort;
                    }
                }
                if (!sendRtpSet.contains(port)) {
                    return port;
                }
            }
        }
        log.warn("{}获取发送端口失败, 无可用端口", mediaServer.getId());
        return -1;
    }

    @Override
    public VideoSessionInfo createRTPServer(MediaServer mediaInfo, VideoSessionInfo videoSessionInfo) {
        // 默认使用udp模式
        int tcpMode = 1;
        int mediaPort = zlmRtpUtils.createRTPServer(mediaInfo, videoSessionInfo.getStream(), videoSessionInfo.getSsrc(), false, false, tcpMode);
        videoSessionInfo.setPort(mediaPort);
        return videoSessionInfo;
    }
}
