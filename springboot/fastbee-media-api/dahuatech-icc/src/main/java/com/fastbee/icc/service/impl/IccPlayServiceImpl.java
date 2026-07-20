package com.fastbee.icc.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.fastbee.icc.client.IccHttpClient;
import com.fastbee.icc.exception.IccApiException;
import com.fastbee.icc.model.video.realTimePreview.HlsUrlRequest;
import com.fastbee.icc.model.video.realTimePreview.HlsUrlResponse;
import com.fastbee.icc.model.video.realTimePreview.RtspUrlRequest;
import com.fastbee.icc.model.video.realTimePreview.RtspUrlResponse;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.enums.ChannelStreamType;
import com.fastbee.media.model.Platform;
import com.fastbee.media.model.StreamInfo;
import com.fastbee.media.service.IPlayService;

/**
 * 大华ICC平台实时预览服务（IPlayService实现）
 *
 * <p>通过大华ICC视频服务API获取实时预览流地址，支持：
 * <ul>
 *   <li>RTSP流：POST /evo-apigw/admin/API/MTS/Video/StartVideo</li>
 *   <li>HLS/FLV/RTMP流：POST /evo-apigw/admin/API/video/stream/realtime</li>
 * </ul>
 * </p>
 *
 * <p>通道ID格式：大华ICC通道ID格式为 "deviceCode$channelSeq"，例如 "1000000$1"</p>
 *
 * @author fastbee
 */
@Slf4j
@Service(ChannelStreamType.PLAY_SERVICE + ChannelStreamType.ICC_DAHUA)
public class IccPlayServiceImpl implements IPlayService {

    /** Bean名称规则：playService + ChannelStreamType.ICC_DAHUA */
    public static final String BEAN_NAME = ChannelStreamType.PLAY_SERVICE + ChannelStreamType.ICC_DAHUA;

    /** 获取RTSP流地址接口 */
    private static final String API_RTSP_URL = "/evo-apigw/admin/API/MTS/Video/StartVideo";
    /** 获取HLS/FLV/RTMP流地址接口 */
    private static final String API_HLS_URL = "/evo-apigw/admin/API/video/stream/realtime";

    /** 流类型：1-主码流，2-辅码流 */
    private static final String STREAM_TYPE_MAIN = "1";
    /** 播放类型：hls */
    private static final String PLAY_TYPE_HLS = "hls";

    private final IccHttpClient httpClient;

    public IccPlayServiceImpl(IccHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public StreamInfo play(CommonChannel channel, Platform platform, Boolean record) {
        String channelId = channel.getChannelId();
        log.info("[大华ICC] 获取实时预览流, channelId={}", channelId);

        // 优先尝试获取HLS流（前端兼容性更好）
        StreamInfo streamInfo = tryGetHlsStream(channel);
        if (streamInfo != null) {
            return streamInfo;
        }

        // 回退到RTSP流
        return tryGetRtspStream(channel);
    }

    @Override
    public Boolean stopPlay(CommonChannel channel) {
        // 大华ICC实时流无需主动停止（服务端超时自动关闭）
        log.debug("[大华ICC] stopPlay - 大华ICC无需主动停止直播流, channelId={}", channel.getChannelId());
        return true;
    }

    @Override
    public Boolean closeStream(CommonChannel channel, String stream, Boolean check) {
        log.debug("[大华ICC] closeStream - 大华ICC无需主动关闭流, channelId={}", channel.getChannelId());
        return true;
    }

    /**
     * 获取HLS/FLV/RTMP流地址
     */
    private StreamInfo tryGetHlsStream(CommonChannel channel) {
        try {
            HlsUrlRequest request = new HlsUrlRequest();
            HlsUrlRequest.Data data = new HlsUrlRequest.Data();
            data.setChannelId(channel.getChannelId());
            data.setStreamType(STREAM_TYPE_MAIN);
            data.setType(PLAY_TYPE_HLS);
            request.setData(data);

            HlsUrlResponse response = httpClient.post(API_HLS_URL, request, HlsUrlResponse.class);
            if (response == null) {
                log.warn("[大华ICC] 获取HLS流地址返回空, channelId={}", channel.getChannelId());
                return null;
            }
            if (!response.getCode().equals("1000")) {
                log.warn("[大华ICC] 获取HLS流地址失败: code={}, msg={}", response.getCode(), response.getErrMsg());
                return null;
            }

            String hlsUrl = response.getData() != null ? response.getData().getUrl() : null;
            if (hlsUrl == null || hlsUrl.isEmpty()) {
                log.warn("[大华ICC] HLS流地址为空, channelId={}", channel.getChannelId());
                return null;
            }

            StreamInfo info = new StreamInfo();
            info.setDeviceId(channel.getDeviceId());
            info.setChannelId(channel.getChannelId());
            info.setHls(hlsUrl);
            info.setPlayurl(hlsUrl);
            log.info("[大华ICC] 获取HLS流成功, channelId={}, url={}", channel.getChannelId(), hlsUrl);
            return info;
        } catch (IccApiException e) {
            log.warn("[大华ICC] 获取HLS流异常，将尝试RTSP: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取RTSP流地址
     */
    private StreamInfo tryGetRtspStream(CommonChannel channel) {
        RtspUrlRequest request = new RtspUrlRequest();
        RtspUrlRequest.Data data = new RtspUrlRequest.Data();
        data.setChannelId(channel.getChannelId());
        data.setStreamType(STREAM_TYPE_MAIN);
        data.setDataType("1"); // 实时预览
        request.setData(data);

        RtspUrlResponse response = httpClient.post(API_RTSP_URL, request, RtspUrlResponse.class);
        if (response == null) {
            throw new IccApiException("[大华ICC] 获取RTSP流地址返回空, channelId=" + channel.getChannelId());
        }
        if (!response.getCode().equals("1000")) {
            throw new IccApiException("[大华ICC] 获取RTSP流地址失败: " + response.getErrMsg(), response.getCode());
        }

        String rtspUrl = response.getData() != null ? response.getData().getUrl() : null;
        if (rtspUrl == null || rtspUrl.isEmpty()) {
            throw new IccApiException("[大华ICC] RTSP流地址为空, channelId=" + channel.getChannelId());
        }

        StreamInfo info = new StreamInfo();
        info.setDeviceId(channel.getDeviceId());
        info.setChannelId(channel.getChannelId());
        info.setRtsp(rtspUrl);
        info.setPlayurl(rtspUrl);
        log.info("[大华ICC] 获取RTSP流成功, channelId={}", channel.getChannelId());
        return info;
    }
}
