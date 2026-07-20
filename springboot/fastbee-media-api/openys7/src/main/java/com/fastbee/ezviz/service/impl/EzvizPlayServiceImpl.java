package com.fastbee.ezviz.service.impl;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.fastbee.ezviz.client.EzvizHttpClient;
import com.fastbee.ezviz.client.EzvizTokenHolder;
import com.fastbee.ezviz.model.EzvizBaseResponse;
import com.fastbee.ezviz.model.EzvizLiveUrl;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.enums.ChannelStreamType;
import com.fastbee.media.model.Platform;
import com.fastbee.media.model.StreamInfo;
import com.fastbee.media.service.IPlayService;

/**
 * 萤石云 IPlayService 实现
 * <p>通过萤石云开放平台 API 获取实时预览流地址</p>
 *
 * <p>萤石云直播流接口：POST /api/lapp/live/address/get
 * 参数：deviceSerial（设备序列号）、channelNo（通道号）、protocol（协议类型）
 * 协议类型：1-ezopen, 2-hls, 3-rtmp, 4-flv</p>
 *
 * <p>注意：萤石云直播流地址有效期较短（约30分钟），需在播放前实时获取</p>
 *
 * @author fastbee
 */
@Slf4j
@Service(ChannelStreamType.PLAY_SERVICE + ChannelStreamType.EZVIZ)
public class EzvizPlayServiceImpl implements IPlayService {

    /** Bean 命名规则：playService + ChannelStreamType.EZVIZ */
    public static final String BEAN_NAME = ChannelStreamType.PLAY_SERVICE + ChannelStreamType.EZVIZ;

    /** 获取直播流地址接口 */
    private static final String API_LIVE_ADDRESS = "/api/lapp/live/address/get";

    private final EzvizHttpClient httpClient;
    private final EzvizTokenHolder tokenHolder;

    public EzvizPlayServiceImpl(EzvizHttpClient httpClient, EzvizTokenHolder tokenHolder) {
        this.httpClient = httpClient;
        this.tokenHolder = tokenHolder;
    }

    @Override
    public StreamInfo play(CommonChannel channel, Platform platform, Boolean record) {
        String deviceSerial = channel.getDeviceId();
        // channelId 格式为 "deviceSerial_channelNo"，取通道号
        int channelNo = parseChannelNo(channel.getChannelId(), deviceSerial);
        log.info("[萤石云] 获取直播流, deviceSerial={}, channelNo={}", deviceSerial, channelNo);

        // 请求所有协议类型，优先返回 HLS
        EzvizLiveUrl liveUrl = getLiveUrl(deviceSerial, channelNo, 2);
        StreamInfo streamInfo = buildStreamInfo(channel, liveUrl);
        log.info("[萤石云] 直播流获取成功, hls={}", liveUrl.getHls());
        return streamInfo;
    }

    @Override
    public Boolean stopPlay(CommonChannel channel) {
        // 萤石云直播流为按需拉取，无需主动停止
        log.debug("[萤石云] stopPlay - 萤石云无需停止直播流请求, deviceId={}", channel.getDeviceId());
        return true;
    }

    @Override
    public Boolean closeStream(CommonChannel channel, String stream, Boolean check) {
        log.debug("[萤石云] closeStream - 萤石云无需主动关闭流, deviceId={}", channel.getDeviceId());
        return true;
    }

    /**
     * 获取萤石云直播流地址
     *
     * @param deviceSerial 设备序列号
     * @param channelNo    通道号（从1开始）
     * @param protocol     协议类型：1-ezopen, 2-hls, 3-rtmp, 4-flv
     */
    public EzvizLiveUrl getLiveUrl(String deviceSerial, int channelNo, int protocol) {
        Map<String, String> params = new HashMap<>();
        params.put("deviceSerial", deviceSerial);
        params.put("channelNo", String.valueOf(channelNo));
        params.put("protocol", String.valueOf(protocol));
        params.put("code", "1");        // 流类型：1-高清，0-普通
        params.put("expireTime", "");   // 为空时使用默认有效期

        EzvizLiveAddressResponse resp = httpClient.postWithToken(API_LIVE_ADDRESS, params, EzvizLiveAddressResponse.class);
        EzvizLiveUrl url = new EzvizLiveUrl();
        if (resp.getData() != null) {
            JSONObject data = com.alibaba.fastjson2.JSON.parseObject(
                    com.alibaba.fastjson2.JSON.toJSONString(resp.getData()));
            url.setHls(data.getString("url"));
            url.setExpireTime(data.getLong("expireTime"));
        }
        return url;
    }

    /**
     * 将萤石云直播地址组装为 StreamInfo
     */
    private StreamInfo buildStreamInfo(CommonChannel channel, EzvizLiveUrl liveUrl) {
        StreamInfo info = new StreamInfo();
        info.setDeviceId(channel.getDeviceId());
        info.setChannelId(channel.getChannelId());
        info.setHls(liveUrl.getHls());
        info.setPlayurl(liveUrl.getHls());
        return info;
    }

    /**
     * 解析 channelId 中的通道号
     * 规则：channelId 为 "deviceSerial_1" 或 "1" 格式
     */
    private int parseChannelNo(String channelId, String deviceSerial) {
        if (channelId == null || channelId.isEmpty()) {
            return 1;
        }
        String noStr = channelId.startsWith(deviceSerial + "_")
                ? channelId.substring(deviceSerial.length() + 1)
                : channelId;
        try {
            return Integer.parseInt(noStr);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    // ---------- 内部响应类型 ----------

    static class EzvizLiveAddressResponse extends EzvizBaseResponse {
        private Object data;
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
    }

}
