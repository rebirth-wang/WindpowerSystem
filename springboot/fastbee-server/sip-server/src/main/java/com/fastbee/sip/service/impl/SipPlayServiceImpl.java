package com.fastbee.sip.service.impl;

import java.util.Comparator;
import java.util.List;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.core.redis.RedisKeyBuilder;
import com.fastbee.common.utils.DateUtils;
import com.fastbee.media.convert.CommonChannelConvert;
import com.fastbee.media.domain.MediaServer;
import com.fastbee.media.enums.SessionType;
import com.fastbee.media.model.*;
import com.fastbee.media.service.IMediaServerService;
import com.fastbee.media.service.impl.VideoSessionManager;
import com.fastbee.media.util.ZlmApiUtils;
import com.fastbee.sip.domain.SipDevice;
import com.fastbee.sip.server.IRtspCmd;
import com.fastbee.sip.server.ISipCmd;
import com.fastbee.sip.server.MessageInvoker;
import com.fastbee.sip.service.ISipDeviceService;
import com.fastbee.sip.service.ISipPlayService;
import com.fastbee.sip.service.IZmlHookService;
import com.fastbee.sip.util.SipUtil;

@Service
@Slf4j
public class SipPlayServiceImpl implements ISipPlayService {

    @Autowired
    private ISipCmd sipCmd;

    @Autowired
    private IRtspCmd rtspCmd;

    @Autowired
    private IZmlHookService zmlHookService;

    @Autowired
    private VideoSessionManager streamSession;

    @Autowired
    private ISipDeviceService sipDeviceService;

    @Autowired
    private IMediaServerService mediaServerService;

    @Autowired
    private ZlmApiUtils zlmApiUtils;

    @Autowired
    private MessageInvoker messageInvoker;

    @Autowired
    private RedisCache redisCache;

    @Override
    public StreamInfo play(String deviceId, String channelId, Boolean record) {
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        if (dev == null) {
            log.error("play dev is null,deviceId:{},channelId:{}", deviceId, channelId);
            return null;
        }
        return this.play(dev,channelId,record);
    }

    @Override
    public StreamInfo play(SipDevice dev, String channelId, Boolean record) {
        String deviceId = dev.getDeviceSipId();
        String streamid;
        if (record != null && record) {
            streamid = String.format("%s_%s_%s", SipUtil.PREFIX_PLAYRECORD,
                    deviceId,
                    channelId);
        } else {
            streamid = String.format("%s_%s_%s", SipUtil.PREFIX_PLAY, deviceId, channelId);
        }
        VideoSessionInfo sinfo = streamSession.getSessionInfo(deviceId, channelId, streamid, null);
        if (sinfo == null) {
            VideoSessionInfo info = sipCmd.playStreamCmd(dev, channelId, record);
            return zmlHookService.updateStream(info);
        } else {
            log.info("sinfo： {}", sinfo);
            MediaServer mediaInfo = mediaServerService.selectMediaServerBydeviceSipId(deviceId);
            if (mediaInfo == null) {
                log.error("mediaInfo is null,deviceId:{},channelId:{}", deviceId, channelId);
            }
            JSONObject rtpInfo = zlmApiUtils.getRtpInfo(mediaInfo, streamid);
            if (rtpInfo != null && rtpInfo.getInteger("code") == 0) {
                if (rtpInfo.getBoolean("exist")) {
                    //直播
                    if (sinfo.getPushing() && !record) {
                        return zmlHookService.updateStream(sinfo);
                    }
                    //直播录像
                    if (sinfo.getRecording() && record) {
                        return zmlHookService.updateStream(sinfo);
                    }
                } else {
                    //清理会话后 重新发起播放
                    sipCmd.streamByeCmd(dev, channelId, streamid, null, false);
                    VideoSessionInfo info = sipCmd.playStreamCmd(dev, channelId, record);
                    return zmlHookService.updateStream(info);
                }
            } else {
                log.error("获取流信息失败：{}", rtpInfo);
            }
            return null;
        }
    }

    @Override
    public StreamPlayUrl getUrl(String deviceId, String channelId) {
        String streamid = String.format("%s_%s_%s", SipUtil.PREFIX_PLAY, deviceId, channelId);
        VideoSessionInfo sinfo = streamSession.getSessionInfo(deviceId, channelId, streamid, null);
        if (sinfo != null) {
            log.info("sinfo： {}", sinfo);
            MediaServer mediaInfo = mediaServerService.selectMediaServerBydeviceSipId(deviceId);
            if (mediaInfo == null) {
                log.error("mediaInfo is null,deviceId:{},channelId:{}", deviceId, channelId);
            }
            JSONObject rtpInfo = zlmApiUtils.getRtpInfo(mediaInfo, streamid);
            if (rtpInfo != null && rtpInfo.getInteger("code") == 0) {
                if (rtpInfo.getBoolean("exist")) {
                    //直播
                    if (sinfo.getPushing()!=null && sinfo.getPushing()) {
                        return CommonChannelConvert.INSTANCE.convertStream(zmlHookService.updateStream(sinfo));
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Boolean pushing(String deviceId, String streamId) {
        MediaServer mediaInfo = mediaServerService.selectMediaServerBydeviceSipId(deviceId);
        if (mediaInfo == null) {
            log.error("mediaInfo is null,deviceId:{}", deviceId);
        }
        JSONObject rtpInfo = zlmApiUtils.getRtpInfo(mediaInfo, streamId);
        if (rtpInfo != null && rtpInfo.getInteger("code") == 0) {
            if (rtpInfo.getBoolean("exist")) {
                log.debug("流存在：{}", rtpInfo);
                return true;
            } else {
                log.error("流不存在：{}", rtpInfo);
                return false;
            }
        } else {
            log.error("获取流信息失败：{}", rtpInfo);
            return false;
        }
    }

    @Override
    public String screenshot(String deviceId, String channelId) {
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        if (dev == null) {
            log.error("play dev is null,deviceId:{},channelId:{}", deviceId, channelId);
            return null;
        }
        String streamid = String.format("%s_%s_%s", SipUtil.PREFIX_PLAY, deviceId, channelId);
        VideoSessionInfo sinfo = streamSession.getSessionInfo(deviceId, channelId, streamid, null);
        if (sinfo == null) {
            // 重新发起推流后截图 可以自定义文件格式
            String fileName = sinfo.getDeviceId() + "_" + sinfo.getChannelId() + "_" + DateUtils.getNowDate() + ".jpg";
            sipCmd.playScreenshotCmd(dev, channelId, fileName);
            // 返回截图文件名
            return fileName;
        } else {
            // 流存在，立刻截图
            log.info("sinfo： {}", sinfo);
            MediaServer mediaInfo = mediaServerService.selectMediaServerBydeviceSipId(deviceId);
            if (mediaInfo == null) {
                log.error("mediaInfo is null,deviceId:{},channelId:{}", deviceId, channelId);
            }
            JSONObject rtpInfo = zlmApiUtils.getRtpInfo(mediaInfo, streamid);
            if (rtpInfo != null && rtpInfo.getInteger("code") == 0) {
                if (sinfo.getPushing()!=null && sinfo.getPushing()) {
                    String streamUrl = String.format("rtsp://127.0.0.1:%s/%s/%s", mediaInfo.getPortRtsp(), "rtp", sinfo.getStream());
                    // 可以自定义文件格式
                    String fileName = sinfo.getDeviceId() + "_" + sinfo.getChannelId() + "_" + DateUtils.getNowDate() + ".jpg";
                    // 请求截图
                    log.info("[请求截图]: {}:{}", streamUrl, fileName);
                    zlmApiUtils.getSnap(mediaInfo, streamUrl, 15, 1, fileName);
                    return fileName;
                }
            }
            return "";
        }
    }
    @Override
    public Boolean closeStream(SipDevice dev, String channelId, String streamId, Boolean check) {
        return sipCmd.streamByeCmd(dev, channelId, streamId, null, true);
    }
    @Override
    public Boolean closeStream(String deviceId, String channelId, String streamId, Boolean check) {
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        return sipCmd.streamByeCmd(dev, channelId, streamId, null, true);
    }

    @Override
    public RecordList listDevRecord(SipDevice dev, String channelId, String start, String end) {
        if (dev != null) {
            String sn = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
            String recordkey = channelId + ":" + sn;
            messageInvoker.recordInfoQuery(dev, sn, channelId, SipUtil.timestampToDate(start), SipUtil.timestampToDate(end));
            String catchkey = RedisKeyBuilder.buildSipRecordinfoCacheKey(recordkey);
            return (RecordList) messageInvoker.getExecResult(catchkey, SipUtil.DEFAULT_EXEC_TIMEOUT);
        }
        return null;
    }

    @Override
    public RecordList listDevRecord(String deviceId, String channelId, String start, String end) {
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        if (dev != null) {
            String sn = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
            String recordkey = channelId + ":" + sn;
            messageInvoker.recordInfoQuery(dev, sn, channelId, SipUtil.timestampToDate(start), SipUtil.timestampToDate(end));
            String catchkey = RedisKeyBuilder.buildSipRecordinfoCacheKey(recordkey);
            return (RecordList) messageInvoker.getExecResult(catchkey, SipUtil.DEFAULT_EXEC_TIMEOUT);
        }
        return null;
    }

    @Override
    public List<RecordItem> listRecord(String deviceId, String channelId) {
        String recordkey = channelId + ":" + deviceId;
        String catchkey = RedisKeyBuilder.buildSipRecordinfoCacheKey(recordkey);
        List<RecordItem> items = redisCache.getCacheList(catchkey);
        if (items.size() > 1) {
            items.sort(Comparator.naturalOrder());
        }
        return items;
    }

    @Override
    public StreamInfo download(SipDevice dev, String channelId, String startTime, String endTime, Integer downloadSpeed) {
        VideoSessionInfo info = sipCmd.downloadStreamCmd(dev, channelId, startTime, endTime, downloadSpeed);
        return zmlHookService.updateStream(info);
    }

    @Override
    public StreamInfo download(String deviceId, String channelId, String startTime, String endTime, Integer downloadSpeed) {
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        VideoSessionInfo info = sipCmd.downloadStreamCmd(dev, channelId, startTime, endTime, downloadSpeed);
        return zmlHookService.updateStream(info);
    }

    @Override
    public StreamInfo playback(SipDevice dev, String channelId, String startTime, String endTime) {
        // 关闭无人拉流的录像推流
        sipCmd.streamByeCmd(dev, channelId, SessionType.PLAYBACK, true);
        VideoSessionInfo info = sipCmd.playbackStreamCmd(dev, channelId, startTime, endTime);
        return zmlHookService.updateStream(info);
    }

    @Override
    public StreamInfo playback(String deviceId, String channelId, String startTime, String endTime) {
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        // 关闭无人拉流的录像推流
        sipCmd.streamByeCmd(dev, channelId, SessionType.PLAYBACK, true);
        VideoSessionInfo info = sipCmd.playbackStreamCmd(dev, channelId, startTime, endTime);
        return zmlHookService.updateStream(info);
    }

    @Override
    public Boolean playbackPause(String deviceId, String channelId, String streamId) {
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        VideoSessionInfo sinfo = streamSession.getSessionInfo(deviceId, channelId, streamId, null);
        if (null == sinfo) {
            log.error("streamId不存在");
            return false;
        }
        rtspCmd.playPause(dev, channelId, streamId);
        return true;
    }

    @Override
    public Boolean playbackReplay(String deviceId, String channelId, String streamId) {
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        VideoSessionInfo sinfo = streamSession.getSessionInfo(deviceId, channelId, streamId, null);
        if (null == sinfo) {
            log.error("streamId不存在");
            return false;
        }
        rtspCmd.playReplay(dev, channelId, streamId);
        return true;
    }

    @Override
    public Boolean playbackSeek(String deviceId, String channelId, String streamId, Long seektime) {
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        VideoSessionInfo sinfo = streamSession.getSessionInfo(deviceId, channelId, streamId, null);
        if (null == sinfo) {
            log.error("streamId不存在");
            return false;
        }
        rtspCmd.playBackSeek(dev, channelId, streamId, seektime);
        return true;
    }

    @Override
    public Boolean playbackSpeed(String deviceId, String channelId, String streamId, Double speed) {
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        VideoSessionInfo sinfo = streamSession.getSessionInfo(deviceId, channelId, streamId, null);
        if (null == sinfo) {
            log.error("streamId不存在");
            return false;
        }
        rtspCmd.playBackSpeed(dev, channelId, streamId, speed);
        return true;
    }
}
