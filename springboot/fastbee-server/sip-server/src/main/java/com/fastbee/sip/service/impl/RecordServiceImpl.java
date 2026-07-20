package com.fastbee.sip.service.impl;

import static com.fastbee.common.extend.utils.SecurityUtils.getLoginUser;

import java.util.Objects;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.media.domain.CommonChannel;
import com.fastbee.media.domain.MediaServer;
import com.fastbee.media.service.ICommonChannelService;
import com.fastbee.media.service.IMediaServerService;
import com.fastbee.media.util.RecordApiUtils;
import com.fastbee.media.util.ZlmApiUtils;
import com.fastbee.oss.domain.OssDetail;
import com.fastbee.oss.service.IOssDetailService;
import com.fastbee.sip.server.ISipCmd;
import com.fastbee.sip.service.*;
import com.fastbee.sip.util.SipUtil;

@Slf4j
@Service
public class RecordServiceImpl implements IRecordService {

    @Autowired
    private ICommonChannelService commonChannelService;

    @Autowired
    private IMediaServerService mediaServerService;

    @Autowired
    private ZlmApiUtils zlmApiUtils;

    @Autowired
    private RecordApiUtils recordApiUtils;

    @Autowired
    private ISipCmd sipCmd;

    @Autowired
    private IOssDetailService ossDetailService;


    @Override
    public JSONObject listServerRecord(String recordApi, Integer pageNum, Integer pageSize) {
        return recordApiUtils.getRecordlist(recordApi, pageNum, pageSize, null).getJSONObject("data");
    }

    @Override
    public JSONArray listServerRecordByDate(String recordApi, Integer year, Integer month, String app, String stream) {
        return recordApiUtils.getRecordDatelist(recordApi, year, month, app, stream, null).getJSONArray("data");
    }

    @Override
    public JSONObject listServerRecordByStream(String recordApi, Integer pageNum, Integer pageSize, String app) {
        return recordApiUtils.getRecordStreamlist(recordApi, pageNum, pageSize, app, null).getJSONObject("data");
    }

    @Override
    public JSONObject listServerRecordByApp(String recordApi, Integer pageNum, Integer pageSize) {
        return recordApiUtils.getRecordApplist(recordApi, pageNum, pageSize, null).getJSONObject("data");
    }

    @Override
    public JSONObject listServerRecordByFile(String recordApi, Integer pageNum, Integer pageSize, String app, String stream, String startTime, String endTime) {
        return recordApiUtils.getRecordFilelist(recordApi, pageNum, pageSize, app, stream, startTime, endTime, null).getJSONObject("data");
    }

    @Override
    public JSONObject listServerRecordByDevice(Integer pageNum, Integer pageSize, String deviceId, String channelId, String startTime, String endTime) {
        String playrsid = String.format("%s_%s_%s", SipUtil.PREFIX_PLAYRECORD, deviceId, channelId);
        MediaServer mediaServer = mediaServerService.selectMediaServerBydeviceSipId(deviceId);
        String recordApi = "";
        if (mediaServer != null && Objects.equals(mediaServer.getProtocol(), "http")) {
            recordApi = "http://" + mediaServer.getIp() + ":" + mediaServer.getRecordPort();
        } else if (mediaServer != null && Objects.equals(mediaServer.getProtocol(), "https")) {
            recordApi = "https://" + mediaServer.getDomainAlias() + ":" + mediaServer.getRecordPort();
        }
        JSONObject obj = recordApiUtils.getRecordFilelist(recordApi, pageNum, pageSize, "rtp",
                playrsid, startTime, endTime, null);
        if (obj != null) {
            obj = obj.getJSONObject("data");
            obj.put("recordApi", recordApi);
            log.info("obj:{}", obj);
        }
        return obj;
    }

    @Override
    public JSONObject listServerRecordByChanelId(String serverId, String channelId, String startTime, String endTime, Integer pageNum, Integer pageSize) {
        // 参数验证
        if (serverId == null || channelId == null || pageNum == null || pageSize == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        if (pageNum <= 0 || pageSize <= 0) {
            throw new IllegalArgumentException("页码和页面大小必须大于0");
        }

        MediaServer mediaServer = mediaServerService.selectMediaServerByServerId(serverId);
        if (mediaServer == null) {
            throw new IllegalArgumentException("未找到对应的媒体服务器");
        }

        String recordApi = "";
        if (Objects.equals(mediaServer.getProtocol(), "http")) {
            recordApi = "http://" + mediaServer.getIp() + ":" + mediaServer.getRecordPort();
        } else if (Objects.equals(mediaServer.getProtocol(), "https")) {
            recordApi = "https://" + mediaServer.getDomainAlias() + ":" + mediaServer.getRecordPort();
        } else {
            throw new IllegalArgumentException("不支持的协议类型: " + mediaServer.getProtocol());
        }

        CommonChannel sipDeviceChannel = commonChannelService
                .lambdaQuery().eq(CommonChannel::getChannelId, channelId).one();
        if (sipDeviceChannel == null) {
            throw new IllegalArgumentException("未找到对应的设备通道");
        }

        String stream = String.format("%s_%s_%s", SipUtil.PREFIX_PLAYRECORD,
                sipDeviceChannel.getDeviceId(),
                channelId);

        try {
            return recordApiUtils.getRecordFilelist(recordApi, pageNum, pageSize, "rtp", stream, startTime, endTime, null).getJSONObject("data");
        } catch (Exception e) {
            throw new RuntimeException("获取录像文件列表失败", e);
        }
    }

    @Override
    public boolean startRecord(String stream) {
        SysUser user = getLoginUser().getUser();
        //缓存zlm服务器配置
        MediaServer media = mediaServerService.selectMediaServerBytenantId(user.getDept().getDeptUserId());
        if (media != null) {
            return zlmApiUtils.startRecord(media, "1", "live", stream).getBoolean("result");
        }
        return false;
    }

    @Override
    public boolean stopRecord(String stream) {
        SysUser user = getLoginUser().getUser();
        MediaServer media = mediaServerService.selectMediaServerBytenantId(user.getDept().getDeptUserId());
        ;
        if (media != null) {
            return zlmApiUtils.stopRecord(media, "1", "live", stream).getBoolean("result");
        }
        return false;
    }

    @Override
    public boolean isRecording(String stream) {
        SysUser user = getLoginUser().getUser();
        MediaServer media = mediaServerService.selectMediaServerBytenantId(user.getDept().getDeptUserId());
        ;
        if (media != null) {
            return zlmApiUtils.isRecording(media, "1", "live", stream).getBoolean("status");
        }
        return false;
    }

    @Override
    public JSONObject getMp4RecordFile(String stream, String period) {
        SysUser user = getLoginUser().getUser();
        MediaServer media = mediaServerService.selectMediaServerBytenantId(user.getDept().getDeptUserId());
        if (media != null) {
            return zlmApiUtils.getMp4RecordFile(media, period, "live", stream).getJSONObject("data");
        }
        return null;
    }

    @Override
    public JSONObject upload(String recordApi, String file) {
        JSONObject obj = recordApiUtils.uploadOss(recordApi, file, null).getJSONObject("data");
        if (obj != null) {
            JSONObject ossObj = obj.getJSONObject("data");
            if (ossObj != null) {
                OssDetail ossDetail = OssDetail.builder()
                        .fileName(ossObj.getString("fileName"))
                        .fileSuffix(ossObj.getString("fileSuffix"))
                        .url(ossObj.getString("url"))
                        .originalName(ossObj.getString("originalName"))
                        .service(ossObj.getString("service"))
                        .build();
                ossDetailService.insertOssDetail(ossDetail);
            }
        }
        return obj;
    }
}
