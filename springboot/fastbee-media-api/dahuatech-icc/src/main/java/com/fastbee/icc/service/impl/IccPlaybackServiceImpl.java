package com.fastbee.icc.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.fastbee.icc.client.IccHttpClient;
import com.fastbee.icc.exception.IccApiException;
import com.fastbee.icc.model.video.videoReplay.HlsPlaybackRequest;
import com.fastbee.icc.model.video.videoReplay.HlsPlaybackResponse;
import com.fastbee.icc.model.video.videoReplay.PlayBackByTimeResponse;
import com.fastbee.icc.model.video.videoReplay.PlaybackByTimeRequest;
import com.fastbee.icc.model.video.videoReplay.RegularVideoRecordRequest;
import com.fastbee.icc.model.video.videoReplay.RegularVideoRecordResponse;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.enums.ChannelStreamType;
import com.fastbee.media.model.RecordItem;
import com.fastbee.media.model.RecordList;
import com.fastbee.media.model.StreamInfo;
import com.fastbee.media.service.IPlaybackService;

/**
 * 大华ICC平台录像回放服务（IPlaybackService实现）
 *
 * <p>通过大华ICC录像服务API实现：
 * <ul>
 *   <li>查询录像列表：POST /evo-apigw/admin/API/SS/Record/QueryRecords</li>
 *   <li>RTSP录像回放（按时间）：POST /evo-apigw/admin/API/SS/Playback/StartPlaybackByTime</li>
 *   <li>HLS/RTMP录像回放：POST /evo-apigw/admin/API/video/stream/record</li>
 * </ul>
 * </p>
 *
 * <p>时间格式约定：ICC平台使用秒级时间戳（如 "1700000000"）</p>
 *
 * @author fastbee
 */
@Slf4j
@Service(ChannelStreamType.PLAYBACK_SERVICE + ChannelStreamType.ICC_DAHUA)
public class IccPlaybackServiceImpl implements IPlaybackService {

    /** Bean名称规则：playbackService + ChannelStreamType.ICC_DAHUA */
    public static final String BEAN_NAME = ChannelStreamType.PLAYBACK_SERVICE + ChannelStreamType.ICC_DAHUA;

    /** 查询录像列表接口 */
    private static final String API_QUERY_RECORDS = "/evo-apigw/admin/API/SS/Record/QueryRecords";
    /** RTSP录像回放（按时间）接口 */
    private static final String API_RTSP_PLAYBACK = "/evo-apigw/admin/API/SS/Playback/StartPlaybackByTime";
    /** HLS/RTMP录像回放接口 */
    private static final String API_HLS_PLAYBACK = "/evo-apigw/admin/API/video/stream/record";

    /** 录像来源：0-设备本地录像，1-中心录像，2-全部 */
    private static final String RECORD_SOURCE_ALL = "2";
    /** 主码流 */
    private static final String STREAM_TYPE_MAIN = "1";
    /** 播放类型：hls */
    private static final String PLAY_TYPE_HLS = "hls";

    private final IccHttpClient httpClient;

    public IccPlaybackServiceImpl(IccHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public StreamInfo playback(CommonChannel channel, String startTime, String stopTime) {
        String channelId = channel.getChannelId();
        log.info("[大华ICC] 获取录像回放流, channelId={}, start={}, stop={}", channelId, startTime, stopTime);

        String iccStartTime = toIccTimestamp(startTime);
        String iccStopTime = toIccTimestamp(stopTime);

        // 优先获取HLS回放流（前端兼容性更好）
        StreamInfo streamInfo = tryGetHlsPlayback(channel, iccStartTime, iccStopTime);
        if (streamInfo != null) {
            return streamInfo;
        }

        // 回退到RTSP回放
        return tryGetRtspPlayback(channel, iccStartTime, iccStopTime);
    }

    @Override
    public Boolean stopPlayback(CommonChannel channel, String stream) {
        // 大华ICC HLS回放为点播模式，无需主动停止
        log.debug("[大华ICC] stopPlayback, channelId={}", channel.getChannelId());
        return true;
    }

    @Override
    public Boolean playbackPause(CommonChannel channel, String stream) {
        log.debug("[大华ICC] playbackPause 不支持，需客户端自行暂停HLS播放器");
        return true;
    }

    @Override
    public Boolean playbackResume(CommonChannel channel, String stream) {
        log.debug("[大华ICC] playbackResume 不支持，需客户端自行恢复HLS播放器");
        return true;
    }

    @Override
    public Boolean playbackSeek(CommonChannel channel, String stream, Long seekTime) {
        log.debug("[大华ICC] playbackSeek 通过重新请求回放地址实现跳转, seekTime={}", seekTime);
        return true;
    }

    @Override
    public Boolean playbackSpeed(CommonChannel channel, String stream, Double speed) {
        log.debug("[大华ICC] playbackSpeed 不支持服务端倍速控制");
        return false;
    }

    @Override
    public RecordList queryRecord(CommonChannel channel, String startTime, String stopTime) {
        String channelId = channel.getChannelId();
        log.info("[大华ICC] 查询录像列表, channelId={}, start={}, stop={}", channelId, startTime, stopTime);

        RegularVideoRecordRequest request = new RegularVideoRecordRequest();
        RegularVideoRecordRequest.Data data = new RegularVideoRecordRequest.Data();
        data.setChannelId(channelId);
        data.setRecordSource(RECORD_SOURCE_ALL);
        data.setStreamType(STREAM_TYPE_MAIN);
        data.setStartTime(toIccTimestamp(startTime));
        data.setEndTime(toIccTimestamp(stopTime));
        request.setData(data);

        try {
            RegularVideoRecordResponse response = httpClient.post(API_QUERY_RECORDS, request, RegularVideoRecordResponse.class);
            if (!response.getCode().equals("1000")) {
                log.warn("[大华ICC] 查询录像列表失败: {}", response.getErrMsg());
                return new RecordList();
            }
            return convertToRecordList(channel.getDeviceId(), response);
        } catch (IccApiException e) {
            log.error("[大华ICC] 查询录像列表异常: {}", e.getMessage(), e);
            return new RecordList();
        }
    }

    @Override
    public StreamInfo download(CommonChannel channel, String startTime, String endTime, Integer downloadSpeed) {
        // 大华ICC不支持直接下载，通过HLS回放地址替代
        log.warn("[大华ICC] download功能使用HLS回放地址替代");
        return playback(channel, startTime, endTime);
    }

    /**
     * 获取HLS录像回放地址
     */
    private StreamInfo tryGetHlsPlayback(CommonChannel channel, String startTime, String stopTime) {
        try {
            HlsPlaybackRequest request = new HlsPlaybackRequest();
            HlsPlaybackRequest.Data data = new HlsPlaybackRequest.Data();
            data.setChannelId(channel.getChannelId());
            data.setStreamType(STREAM_TYPE_MAIN);
            data.setType(PLAY_TYPE_HLS);
            data.setBeginTime(startTime);
            data.setEndTime(stopTime);
            data.setRecordSource(RECORD_SOURCE_ALL);
            request.setData(data);

            HlsPlaybackResponse response = httpClient.post(API_HLS_PLAYBACK, request, HlsPlaybackResponse.class);
            if (response == null || !response.getCode().equals("1000")) {
                log.warn("[大华ICC] 获取HLS回放流失败, channelId={}", channel.getChannelId());
                return null;
            }

            String url = response.getData() != null ? response.getData().getUrl() : null;
            if (url == null || url.isEmpty()) {
                log.warn("[大华ICC] HLS回放流地址为空, channelId={}", channel.getChannelId());
                return null;
            }

            StreamInfo info = new StreamInfo();
            info.setDeviceId(channel.getDeviceId());
            info.setChannelId(channel.getChannelId());
            info.setHls(url);
            info.setPlayurl(url);
            info.setStartTime(startTime);
            info.setEndTime(stopTime);
            log.info("[大华ICC] 获取HLS回放流成功, channelId={}", channel.getChannelId());
            return info;
        } catch (IccApiException e) {
            log.warn("[大华ICC] 获取HLS回放流异常，将尝试RTSP: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取RTSP录像回放地址（按时间）
     */
    private StreamInfo tryGetRtspPlayback(CommonChannel channel, String startTime, String stopTime) {
        PlaybackByTimeRequest request = new PlaybackByTimeRequest();
        PlaybackByTimeRequest.Data data = new PlaybackByTimeRequest.Data();
        data.setChannelId(channel.getChannelId());
        data.setRecordSource(RECORD_SOURCE_ALL);
        data.setStreamType(STREAM_TYPE_MAIN);
        data.setStartTime(startTime);
        data.setEndTime(stopTime);
        request.setData(data);

        PlayBackByTimeResponse response = httpClient.post(API_RTSP_PLAYBACK, request, PlayBackByTimeResponse.class);
        if (response == null || !response.getCode().equals("1000")) {
            throw new IccApiException("[大华ICC] 获取RTSP回放流失败: "
                    + (response != null ? response.getErrMsg() : "响应为空"));
        }

        String rtspUrl = response.getData() != null ? response.getData().getUrl() : null;
        if (rtspUrl == null || rtspUrl.isEmpty()) {
            throw new IccApiException("[大华ICC] RTSP回放流地址为空, channelId=" + channel.getChannelId());
        }

        StreamInfo info = new StreamInfo();
        info.setDeviceId(channel.getDeviceId());
        info.setChannelId(channel.getChannelId());
        info.setRtsp(rtspUrl);
        info.setPlayurl(rtspUrl);
        info.setStartTime(startTime);
        info.setEndTime(stopTime);
        log.info("[大华ICC] 获取RTSP回放流成功, channelId={}", channel.getChannelId());
        return info;
    }

    /**
     * 将录像列表响应转换为RecordList
     */
    private RecordList convertToRecordList(String deviceId, RegularVideoRecordResponse response) {
        RecordList recordList = new RecordList();
        recordList.setDeviceID(deviceId);
        if (response.getData() == null || response.getData().getRecords() == null) {
            return recordList;
        }
        List<RegularVideoRecordResponse.Data.RecordInfo> records = response.getData().getRecords();
        for (RegularVideoRecordResponse.Data.RecordInfo record : records) {
            RecordItem item = new RecordItem();
            item.setStart(parseTimestamp(record.getStartTime()));
            item.setEnd(parseTimestamp(record.getEndTime()));
            recordList.addItem(item);
        }
        recordList.setSumNum(records.size());
        return recordList;
    }

    /**
     * 解析时间戳字符串为秒级long
     * 支持：秒级时间戳字符串、毫秒级时间戳字符串、"yyyy-MM-dd HH:mm:ss"格式
     */
    private long parseTimestamp(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) {
            return 0L;
        }
        // 纯数字
        if (timeStr.matches("\\d+")) {
            long ts = Long.parseLong(timeStr);
            // 毫秒级时间戳转秒级
            if (ts > 9999999999L) {
                return ts / 1000;
            }
            return ts;
        }
        // 日期字符串
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.parse(timeStr).getTime() / 1000;
        } catch (ParseException e) {
            log.warn("[大华ICC] 时间格式解析失败: {}", timeStr);
            return 0L;
        }
    }

    /**
     * 将时间字符串转换为ICC平台要求的秒级时间戳字符串
     * 支持：毫秒级时间戳（13位数字）、秒级时间戳（10位数字）、"yyyy-MM-dd HH:mm:ss"格式
     */
    private String toIccTimestamp(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) {
            return "";
        }
        if (timeStr.matches("\\d{13}")) {
            // 毫秒级转秒级
            return String.valueOf(Long.parseLong(timeStr) / 1000);
        }
        if (timeStr.matches("\\d{10}")) {
            return timeStr;
        }
        // 日期字符串转时间戳
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return String.valueOf(sdf.parse(timeStr).getTime() / 1000);
        } catch (ParseException e) {
            log.warn("[大华ICC] 时间格式转换失败: {}", timeStr);
            return timeStr;
        }
    }
}
