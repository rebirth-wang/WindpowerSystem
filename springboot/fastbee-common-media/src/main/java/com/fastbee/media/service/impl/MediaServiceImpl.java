package com.fastbee.media.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.fastbee.media.domain.MediaServer;
import com.fastbee.media.model.StreamInfo;
import com.fastbee.media.service.IMediaServerService;
import com.fastbee.media.service.IMediaService;
import com.fastbee.media.util.ZlmApiUtils;

@Service
public class MediaServiceImpl implements IMediaService {

    @Autowired
    private IMediaServerService mediaServerService;

    @Autowired
    private ZlmApiUtils zlmApiUtils;

    @Override
    public StreamInfo getStreamInfoByAppAndStream(MediaServer mediaInfo, String app, String stream, Object tracks, String callId) {
        return getStreamInfoByAppAndStream(mediaInfo, app, stream, tracks, null, callId);
    }

    @Override
    public StreamInfo getStreamInfoByAppAndStreamWithCheck(String app, String stream, String mediaServerId, String addr, boolean authority) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
        StreamInfo streamInfo = null;
        MediaServer mediaInfo = this.mediaServerService.selectMediaServerByServerId(mediaServerId);
        if (mediaInfo == null) {
            return null;
        }
        String calld = null;
        JSONObject mediaList = this.zlmApiUtils.getMediaList(mediaInfo, app, stream);
        if (mediaList != null && mediaList.getInteger("code") == 0) {
            JSONArray data = mediaList.getJSONArray("data");
            if (data == null) {
                return null;
            }
            JSONObject mediaJSON = (JSONObject) JSON.parseObject(JSON.toJSONString(data.get(0)), JSONObject.class);
            JSONArray tracks = mediaJSON.getJSONArray("tracks");
            streamInfo = authority ? getStreamInfoByAppAndStream(mediaInfo, app, stream, tracks, addr, calld) : getStreamInfoByAppAndStream(mediaInfo, app, stream, tracks, addr, null);
        }
        return streamInfo;
    }

    @Override
    public StreamInfo getStreamInfoByAppAndStreamWithCheck(String app, String stream, String mediaServerId, boolean authority) {
        return getStreamInfoByAppAndStreamWithCheck(app, stream, mediaServerId, null, authority);
    }

    @Override
    public StreamInfo getStreamInfoByAppAndStream(MediaServer mediaInfo, String app, String stream, Object tracks, String addr, String callId) {
        StreamInfo streamInfoResult = new StreamInfo();
        streamInfoResult.setStreamId(stream);
        streamInfoResult.setApp(app);
        if (addr == null) {
            addr = mediaInfo.getIp();
        }
        streamInfoResult.setIp(addr);
        streamInfoResult.setServerId(mediaInfo.getServerId());
        String callIdParam = ObjectUtils.isEmpty(callId) ? "" : "?callId=" + callId;
        streamInfoResult.setRtmp(addr, mediaInfo.getPortRtmp(), null, app, stream, callIdParam);
        streamInfoResult.setRtsp(addr, mediaInfo.getPortRtsp(),null, app, stream, callIdParam);
        streamInfoResult.setFlv(addr, mediaInfo.getPortHttp(), mediaInfo.getPortHttps(), app, stream, callIdParam);
        streamInfoResult.setFmp4(addr, mediaInfo.getPortHttp(), mediaInfo.getPortHttps(), app, stream, callIdParam);
        streamInfoResult.setHls(addr, mediaInfo.getPortHttp(), mediaInfo.getPortHttps(), app, stream, callIdParam);
        streamInfoResult.setTs(addr, mediaInfo.getPortHttp(), mediaInfo.getPortHttps(), app, stream, callIdParam);
        streamInfoResult.setRtc(addr, mediaInfo.getPortHttp(), mediaInfo.getPortHttps(), app, stream, callIdParam);
        streamInfoResult.setTracks(tracks);
        return streamInfoResult;
    }
}
