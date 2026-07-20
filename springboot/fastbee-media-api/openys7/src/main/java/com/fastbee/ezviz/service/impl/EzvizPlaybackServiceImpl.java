package com.fastbee.ezviz.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.fastbee.ezviz.client.EzvizHttpClient;
import com.fastbee.ezviz.model.EzvizBaseResponse;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.enums.ChannelStreamType;
import com.fastbee.media.model.RecordItem;
import com.fastbee.media.model.RecordList;
import com.fastbee.media.model.StreamInfo;
import com.fastbee.media.service.IPlaybackService;

/**
 * 萤石云 IPlaybackService 实现
 * <p>实现录像查询与云端录像回放流地址获取</p>
 *
 * <p>萤石云录像相关接口：
 * <ul>
 *   <li>POST /api/lapp/video/by/time - 按时间获取云存储录像地址</li>
 *   <li>POST /api/lapp/record/query  - 查询SD卡/NVR录像列表</li>
 * </ul>
 * </p>
 *
 * @author fastbee
 */
@Slf4j
@Service(ChannelStreamType.PLAYBACK_SERVICE + ChannelStreamType.EZVIZ)
public class EzvizPlaybackServiceImpl implements IPlaybackService {

    public static final String BEAN_NAME = ChannelStreamType.PLAYBACK_SERVICE + ChannelStreamType.EZVIZ;

    /** 按时间获取云存储回放地址 */
    private static final String API_PLAYBACK_BY_TIME   = "/api/lapp/video/by/time";
    /** 查询 SD卡/NVR 本地录像列表 */
    private static final String API_RECORD_QUERY       = "/api/lapp/record/query";
    /** 获取 SD卡 录像回放地址 */
    private static final String API_RECORD_PLAY        = "/api/lapp/record/by/time";

    private final EzvizHttpClient httpClient;

    public EzvizPlaybackServiceImpl(EzvizHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public StreamInfo playback(CommonChannel channel, String startTime, String stopTime) {
        String deviceSerial = channel.getDeviceId();
        int channelNo = parseChannelNo(channel.getChannelId(), deviceSerial);
        log.info("[萤石云] 获取录像回放流, deviceSerial={}, channelNo={}, start={}, stop={}", deviceSerial, channelNo, startTime, stopTime);

        Map<String, String> params = new HashMap<>();
        params.put("deviceSerial", deviceSerial);
        params.put("channelNo", String.valueOf(channelNo));
        params.put("startTime", toEzvizTimeStr(startTime));
        params.put("stopTime", toEzvizTimeStr(stopTime));
        params.put("protocol", "2"); // 2-HLS

        EzvizLiveAddressResponse resp = httpClient.postWithToken(API_PLAYBACK_BY_TIME, params, EzvizLiveAddressResponse.class);
        StreamInfo info = new StreamInfo();
        info.setDeviceId(deviceSerial);
        info.setChannelId(channel.getChannelId());
        if (resp.getData() != null) {
            JSONObject data = JSON.parseObject(JSON.toJSONString(resp.getData()));
            String url = data.getString("url");
            info.setHls(url);
            info.setPlayurl(url);
        }
        return info;
    }

    @Override
    public Boolean stopPlayback(CommonChannel channel, String stream) {
        // 萤石云 HLS 回放为点播模式，无需主动停止
        log.debug("[萤石云] stopPlayback, deviceId={}", channel.getDeviceId());
        return true;
    }

    @Override
    public Boolean playbackPause(CommonChannel channel, String stream) {
        log.debug("[萤石云] playbackPause 不支持，需客户端自行暂停 HLS 播放器");
        return true;
    }

    @Override
    public Boolean playbackResume(CommonChannel channel, String stream) {
        log.debug("[萤石云] playbackResume 不支持，需客户端自行恢复 HLS 播放器");
        return true;
    }

    @Override
    public Boolean playbackSeek(CommonChannel channel, String stream, Long seekTime) {
        log.debug("[萤石云] playbackSeek 通过重新请求回放地址实现跳转, seekTime={}", seekTime);
        return true;
    }

    @Override
    public Boolean playbackSpeed(CommonChannel channel, String stream, Double speed) {
        log.debug("[萤石云] playbackSpeed 不支持服务端倍速控制");
        return false;
    }

    @Override
    public RecordList queryRecord(CommonChannel channel, String startTime, String stopTime) {
        String deviceSerial = channel.getDeviceId();
        int channelNo = parseChannelNo(channel.getChannelId(), deviceSerial);
        log.info("[萤石云] 查询录像列表, deviceSerial={}, channelNo={}, start={}, stop={}", deviceSerial, channelNo, startTime, stopTime);

        Map<String, String> params = new HashMap<>();
        params.put("deviceSerial", deviceSerial);
        params.put("channelNo", String.valueOf(channelNo));
        params.put("startTime", toEzvizTimeStr(startTime));
        params.put("stopTime", toEzvizTimeStr(stopTime));
        params.put("recType", "0"); // 0-全部, 1-定时, 2-移动侦测, 3-告警

        EzvizRecordListResponse resp = httpClient.postWithToken(API_RECORD_QUERY, params, EzvizRecordListResponse.class);
        return convertToRecordList(resp);
    }

    @Override
    public StreamInfo download(CommonChannel channel, String startTime, String endTime, Integer downloadSpeed) {
        // 萤石云不支持直接下载，通过 HLS 回放地址提供下载
        log.warn("[萤石云] download 功能不支持，返回回放地址替代");
        return playback(channel, startTime, endTime);
    }

    /**
     * 将萤石云录像列表转换为 RecordList 格式
     * RecordItem.start/end 为秒级时间戳
     */
    private RecordList convertToRecordList(EzvizRecordListResponse resp) {
        RecordList recordList = new RecordList();
        if (resp.getData() == null) {
            return recordList;
        }
        JSONArray array;
        Object data = resp.getData();
        if (data instanceof List) {
            array = JSON.parseArray(JSON.toJSONString(data));
        } else {
            JSONObject dataObj = JSON.parseObject(JSON.toJSONString(data));
            array = dataObj.getJSONArray("recordInfos");
        }
        if (array == null) {
            return recordList;
        }
        for (int i = 0; i < array.size(); i++) {
            JSONObject item = array.getJSONObject(i);
            RecordItem ri = new RecordItem();
            // 萤石云返回格式为 "yyyy-MM-dd HH:mm:ss"，转换为秒级时间戳
            ri.setStart(parseTimeToSeconds(item.getString("startTime")));
            ri.setEnd(parseTimeToSeconds(item.getString("stopTime")));
            recordList.addItem(ri);
        }
        recordList.setSumNum(array.size());
        return recordList;
    }

    /**
     * 将 "yyyy-MM-dd HH:mm:ss" 格式时间字符串转换为秒级时间戳
     */
    private long parseTimeToSeconds(String timeStr) {
        if (timeStr == null || timeStr.isEmpty()) return 0L;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.parse(timeStr).getTime() / 1000;
        } catch (ParseException e) {
            log.warn("[萤石云] 时间格式解析失败: {}", timeStr);
            return 0L;
        }
    }

    /**
     * 将时间字符串转换为萤石云要求的格式（yyyy-MM-dd HH:mm:ss）
     * 若已是该格式则直接返回
     */
    private String toEzvizTimeStr(String timeStr) {
        if (timeStr == null) return "";
        // 若为时间戳（毫秒），转换为字符串
        if (timeStr.matches("\\d{13}")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(new Date(Long.parseLong(timeStr)));
        }
        return timeStr;
    }

    private int parseChannelNo(String channelId, String deviceSerial) {
        if (channelId == null || channelId.isEmpty()) return 1;
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

    static class EzvizRecordListResponse extends EzvizBaseResponse {
        private Object data;
        public Object getData() { return data; }
        public void setData(Object data) { this.data = data; }
    }

}
