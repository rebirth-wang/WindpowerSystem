package com.fastbee.isup.service.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fastbee.isup.config.HikIsupProperties;
import com.fastbee.isup.model.IsupDevInfo;
import com.fastbee.isup.sdk.StreamManager;
import com.fastbee.isup.sdk.enums.SEARCH_TYPE;
import com.fastbee.isup.sdk.service.HCISUPCMS;
import com.fastbee.isup.sdk.service.IHikISUPStream;
import com.fastbee.isup.sdk.structure.*;
import com.fastbee.isup.service.IMediaStreamService;
import com.fastbee.media.constant.MediaConstant;
import com.fastbee.media.domain.MediaServer;
import com.fastbee.media.enums.SessionType;
import com.fastbee.media.model.RecordItem;
import com.fastbee.media.model.RecordList;
import com.fastbee.media.model.StreamInfo;
import com.fastbee.media.model.VideoSessionInfo;
import com.fastbee.media.service.IMediaServerService;
import com.fastbee.media.service.ISendRtpServerService;
import com.fastbee.media.service.impl.VideoSessionManager;
import com.fastbee.media.util.SsrcUtil;
import com.fastbee.media.util.ZlmApiUtils;

@Slf4j
@Profile("isup")
@Service("mediaStreamService")
@RequiredArgsConstructor
public class MediaStreamServiceImpl implements IMediaStreamService {
    private final HikIsupProperties hikIsupProperties;
    private final IHikISUPStream hikISUPStream;
    private final HCISUPCMS hcIsupCms;
    @Autowired
    private VideoSessionManager streamSession;

    @Autowired
    private ISendRtpServerService sendRtpServerService;

    @Autowired
    private IMediaServerService mediaServerService;

    @Autowired
    private ZlmApiUtils zlmApiUtils;

    @Override
    public StreamInfo preview(IsupDevInfo device, Integer channelId) {
        if (channelId == null) return null;
        // 防重复：如果该设备已在预览或已有RTP服务，直接返回
        VideoSessionInfo sinfo = streamSession.getSessionInfo(device.getDeviceId(), channelId.toString(), "*", SessionType.PLAY.name());
        if (sinfo == null) {
            // 创建异步控制器
            return RealPlay(device, channelId);
        } else {
            // 会话存在 不用重新拉流
            log.info("sinfo： {}", sinfo);
            MediaServer mediaInfo = mediaServerService.selectMediaServerByServerId(sinfo.getMediaServerId());
            if (mediaInfo == null) {
                log.error("mediaInfo is null, MediaServerId:{}", sinfo.getMediaServerId());
            }
            JSONObject rtpInfo = zlmApiUtils.getRtpInfo(mediaInfo, sinfo.getStream());
            if (rtpInfo != null && rtpInfo.getInteger("code") == 0) {
                if (rtpInfo.getBoolean("exist")) {
                    // 直接播放
                    StreamInfo streamInfo = new StreamInfo();
                    streamInfo.setDeviceId(device.getDeviceId());
                    streamInfo.setChannelId(channelId.toString());
                    return streamInfo.build(mediaInfo, "rtp", sinfo.getStream());
                } else {
                    // 清理会话后 重新发起播放
                    stopPreview(device, channelId);
                    return RealPlay(device, channelId);
                }
            } else {
                log.error("获取流信息失败：{}", rtpInfo);
            }
        }
        return null;
    }

    public Boolean stopPreview(IsupDevInfo device, Integer channelId) {
        Integer loginId = device.getLoginId();
        if (channelId == null) {
            log.error("通道号不能为空，无法停止预览");
            return false;
        }
        VideoSessionInfo info = streamSession.getSessionInfo(device.getDeviceId(), channelId.toString(), "*", SessionType.PLAY.name());
        if (info != null) {
            MediaServer mediaInfo = mediaServerService.selectMediaServerBydeviceSipId(info.getDeviceId());
            if (mediaInfo == null) {
                log.error("[发送BYE] mediaInfo is null");
                return false;
            }
            JSONObject ret = zlmApiUtils.getMediaInfo(mediaInfo, "rtp", "rtmp", info.getStream());
            int code = ret.getInteger("code");
            if (code == 0) {
                int readerCount = ret.getInteger("readerCount");
                log.info("还有{}位用户正在观看该流！", readerCount);
                if (readerCount > 0) {
                    log.info("流id:{} 不关闭！", info.getStream());
                    return false;
                }
            } else {
                log.info("流详细信息：{}，错误码：{}", ret, code);
            }
            Integer sessionId = info.getSessionId();
            Integer previewHandleId = info.getPreviewHandle();
            if (previewHandleId != null && !hikISUPStream.NET_ESTREAM_StopPreview(previewHandleId)) {
                log.error("NET_ESTREAM_StopPreview failed,err = {}", hikISUPStream.NET_ESTREAM_GetLastError());
            }
            if (!hcIsupCms.NET_ECMS_StopGetRealStream(loginId, sessionId)) {
                log.error("NET_ECMS_StopGetRealStream failed,err = {}", hcIsupCms.NET_ECMS_GetLastError());
            }
            streamSession.remove(device.getDeviceId(), channelId.toString(), info.getSessionId().toString(), SessionType.PLAY.name());
            log.info("CMS已发送停止预览请求");
        }
        return true;
    }

    @Override
    public RecordList getRecored(IsupDevInfo device, Integer channelId, String startTime, String endTime) {
        NET_EHOME_REC_FILE_COND RecFileCond = new NET_EHOME_REC_FILE_COND();
        //录像查询条件
        RecFileCond.dwChannel = channelId; //通道号
        RecFileCond.dwRecType = 0xff; //录像类型 0xff:全部录像
        RecFileCond.dwStartIndex = 0; //查询的起始位置，从0开始。
        RecFileCond.dwMaxFileCountPer = 100; //单次搜索可查询的最大文件数，由实际网络环境决定。建议最大文件数设为8。
        RecFileCond.byLocalOrUTC = 0;
        DateTimeFormatter formatter;
        if (startTime.contains("T")) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        } else {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        }
        LocalDateTime start = LocalDateTime.parse(startTime, formatter);
        LocalDateTime end = LocalDateTime.parse(endTime, formatter);
        RecFileCond.struStartTime.wYear = (short) start.getYear();
        RecFileCond.struStartTime.byMonth = (byte) start.getMonthValue();
        RecFileCond.struStartTime.byDay = (byte) start.getDayOfMonth();
        RecFileCond.struStartTime.byHour = (byte) start.getHour();
        RecFileCond.struStartTime.byMinute = (byte) start.getMinute();
        RecFileCond.struStartTime.bySecond = (byte) start.getSecond();
        RecFileCond.struStopTime.wYear = (short) end.getYear();
        RecFileCond.struStopTime.byMonth = (byte) end.getMonthValue();
        RecFileCond.struStopTime.byDay = (byte) end.getDayOfMonth();
        RecFileCond.struStopTime.byHour = (byte) end.getHour();
        RecFileCond.struStopTime.byMinute = (byte) end.getMinute();
        RecFileCond.struStopTime.bySecond = (byte) end.getSecond();
        RecFileCond.write();
        int FindFileHandle = hcIsupCms.NET_ECMS_StartFindFile_V11(device.getLoginId(), SEARCH_TYPE.ENUM_SEARCH_RECORD_FILE.getCode(), RecFileCond.getPointer(), RecFileCond.size());
        if (FindFileHandle <= -1) {
            log.error("NET_ECMS_StartFindFile_V11 error code:" + hcIsupCms.NET_ECMS_GetLastError());
            return null;
        }
        RecordList recordList = new RecordList();
        recordList.setDeviceID(device.getDeviceId());
        Integer sum = 0;
        while (true) {
            NET_EHOME_REC_FILE struFindData = new NET_EHOME_REC_FILE();
            long State = hcIsupCms.NET_ECMS_FindNextFile_V11(FindFileHandle, struFindData.getPointer(), struFindData.size());
            if (State <= -1) {
                log.error("查找失败，错误码为" + hcIsupCms.NET_ECMS_GetLastError());
                return null;
            } else if (State == 1000) {
                //获取文件信息成功
                struFindData.read();
                String strFileName = new String(struFindData.sFileName, StandardCharsets.UTF_8).trim();
                log.info("文件名称：" + strFileName);
                log.info("文件大小:" + struFindData.dwFileSize);
                log.info("获取文件成功");
                RecordItem recordItem = new RecordItem();
                // 转换时间格式
                recordItem.setStart(struFindData.getStartTimestamp());
                recordItem.setEnd(struFindData.getStopTimestamp());
                //搜索到的文件信息
                System.out.println("Filename[" + strFileName + "], Filesize[" + struFindData.dwFileSize + "], StarTime[" + struFindData.getStartTimestamp() + "], StopTime[" + struFindData.getStopTimestamp() + "]");
                recordList.getRecordItems().add(recordItem);
                sum++;
                continue;
            } else if (State == 1001) {
                //未查找到文件
                log.error("未查找到文件");
                break;
            } else if (State == 1002) {
                //正在查找请等待
                log.error("正在查找，请等待");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            } else if (State == 1003) {
                //没有更多的文件，查找结束
                log.error("没有更多的文件，查找结束");
                break;
            } else if (State == 1004) {
                //查找文件时异常
                log.error("检索异常");
                break;
            } else if (State == 1005) {
                //查找文件超时
                log.error("设备不支持改操作");
                break;
            }
        }
        recordList.setSumNum(sum);
        if (!hcIsupCms.NET_ECMS_StopFindFile(FindFileHandle)) {
            log.error("NET_ECMS_StopFindFile error code:" + hcIsupCms.NET_ECMS_GetLastError());
        }
        log.info("NET_ECMS_StopFindFile suss");
        return recordList;
    }

    /**
     * 开启预览
     * sessionID 会话id
     */
    public StreamInfo RealPlay(IsupDevInfo device, Integer channelId) {
        NET_EHOME_PREVIEWINFO_IN_V11 struPreviewInV11 = new NET_EHOME_PREVIEWINFO_IN_V11();
        struPreviewInV11.iChannel = channelId; // 通道号
        struPreviewInV11.dwLinkMode = 0; // 0- TCP方式，1- UDP方式
        struPreviewInV11.dwStreamType = 0; // 码流类型：0- 主码流，1- 子码流, 2- 第三码流
        log.info("ip: {}, port: {}", hikIsupProperties.getExtendIp(), hikIsupProperties.getSmsPort());
        struPreviewInV11.struStreamSever.szIP = hikIsupProperties.getExtendIp().getBytes(); //流媒体服务器IP地址,公网地址
        struPreviewInV11.struStreamSever.wPort = Short.parseShort(hikIsupProperties.getSmsPort()); //流媒体服务器端口，需要跟服务器启动监听端口一致
        struPreviewInV11.write();
        //预览请求
        NET_EHOME_PREVIEWINFO_OUT struPreviewOut = new NET_EHOME_PREVIEWINFO_OUT();
        boolean getRS = hcIsupCms.NET_ECMS_StartGetRealStreamV11(device.getLoginId(), struPreviewInV11, struPreviewOut);
        log.info("NET_ECMS_StartGetRealStream 预览请求: {}", getRS);
        if (!hcIsupCms.NET_ECMS_StartGetRealStreamV11(device.getLoginId(), struPreviewInV11, struPreviewOut)) {
            log.error("NET_ECMS_StartGetRealStream failed, error code: {}", hcIsupCms.NET_ECMS_GetLastError());
        } else {
            struPreviewOut.read();
            log.info("NET_ECMS_StartGetRealStream succeed, sessionID: {}", struPreviewOut.lSessionID);
            StreamManager.userIDandSessionMap.put(device.getLoginId() * 100 + channelId, struPreviewOut.lSessionID);
            NET_EHOME_PUSHSTREAM_IN struPushInfoIn = new NET_EHOME_PUSHSTREAM_IN();
            struPushInfoIn.read();
            struPushInfoIn.dwSize = struPushInfoIn.size();
            struPushInfoIn.lSessionID = struPreviewOut.lSessionID;
            struPushInfoIn.write();
            NET_EHOME_PUSHSTREAM_OUT struPushInfoOut = new NET_EHOME_PUSHSTREAM_OUT();
            struPushInfoOut.read();
            struPushInfoOut.dwSize = struPushInfoOut.size();
            struPushInfoOut.write();
            // 动态分配RTP端口
            String deviceId = device.getDeviceId();
            String streamKey = String.format("%s_%s_%d_%d", MediaConstant.PREFIX.PREFIX_ISUP_PLAY, deviceId, channelId, struPreviewOut.lSessionID);
            MediaServer mediaInfo = mediaServerService.selectMediaServerBydeviceSipId(deviceId);
            VideoSessionInfo info = VideoSessionInfo.builder()
                    .mediaServerId(mediaInfo.getServerId())
                    .mediaServerIp(mediaInfo.getIp())
                    .type(SessionType.PLAY)
                    .deviceId(device.getDeviceId())
                    .channelId(channelId.toString())
                    .stream(streamKey)
                    .pushing(false)
                    .recording(false)
                    .ssrc(SsrcUtil.getPlaySsrc("3406666600"))
                    .sessionId(struPreviewOut.lSessionID)
                    .build();
            // 先创建RTP服务
            sendRtpServerService.createRTPServer(mediaInfo, info);
            streamSession.putBySessionId(info);
            if (!hcIsupCms.NET_ECMS_StartPushRealStream(device.getLoginId(), struPushInfoIn, struPushInfoOut)) {
                log.error("NET_ECMS_StartPushRealStream failed, error code: {}", hcIsupCms.NET_ECMS_GetLastError());
            }
            StreamInfo streamInfo = new StreamInfo();
            streamInfo.setDeviceId(device.getDeviceId());
            streamInfo.setChannelId(channelId.toString());
            return streamInfo.build(mediaInfo, "rtp", streamKey);
        }
        return null;
    }


    @Async
    @Override
    public StreamInfo playbackByTime(IsupDevInfo device, Integer channelId, String startTime, String endTime) {
        if (channelId == null) return null;

        NET_EHOME_PLAYBACK_INFO_IN m_struPlayBackInfoIn = new NET_EHOME_PLAYBACK_INFO_IN();
        m_struPlayBackInfoIn.read();
        m_struPlayBackInfoIn.dwSize = m_struPlayBackInfoIn.size();
        m_struPlayBackInfoIn.dwChannel = channelId; //通道号
        m_struPlayBackInfoIn.byPlayBackMode = 1;//0- 按文件名回放，1- 按时间回放
        m_struPlayBackInfoIn.unionPlayBackMode.setType(NET_EHOME_PLAYBACKBYTIME.class);
        // 解析时间字符串（支持 "2025-11-12 11:03:00" 或 "2025-11-12T11:03:00"）
        DateTimeFormatter formatter;
        if (startTime.contains("T")) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        } else {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        }
        LocalDateTime start = LocalDateTime.parse(startTime, formatter);
        LocalDateTime end = LocalDateTime.parse(endTime, formatter);
        // 填充开始时间
        m_struPlayBackInfoIn.unionPlayBackMode.struPlayBackbyTime.struStartTime.wYear = (short) start.getYear();
        m_struPlayBackInfoIn.unionPlayBackMode.struPlayBackbyTime.struStartTime.byMonth = (byte) start.getMonthValue();
        m_struPlayBackInfoIn.unionPlayBackMode.struPlayBackbyTime.struStartTime.byDay = (byte) start.getDayOfMonth();
        m_struPlayBackInfoIn.unionPlayBackMode.struPlayBackbyTime.struStartTime.byHour = (byte) start.getHour();
        m_struPlayBackInfoIn.unionPlayBackMode.struPlayBackbyTime.struStartTime.byMinute = (byte) start.getMinute();
        m_struPlayBackInfoIn.unionPlayBackMode.struPlayBackbyTime.struStartTime.bySecond = (byte) start.getSecond();
        // 填充结束时间
        m_struPlayBackInfoIn.unionPlayBackMode.struPlayBackbyTime.struStopTime.wYear = (short) end.getYear();
        m_struPlayBackInfoIn.unionPlayBackMode.struPlayBackbyTime.struStopTime.byMonth = (byte) end.getMonthValue();
        m_struPlayBackInfoIn.unionPlayBackMode.struPlayBackbyTime.struStopTime.byDay = (byte) end.getDayOfMonth();
        m_struPlayBackInfoIn.unionPlayBackMode.struPlayBackbyTime.struStopTime.byHour = (byte) end.getHour();
        m_struPlayBackInfoIn.unionPlayBackMode.struPlayBackbyTime.struStopTime.byMinute = (byte) end.getMinute();
        m_struPlayBackInfoIn.unionPlayBackMode.struPlayBackbyTime.struStopTime.bySecond = (byte) end.getSecond();

        System.arraycopy(hikIsupProperties.getExtendIp().getBytes(), 0, m_struPlayBackInfoIn.struStreamSever.szIP,
                0, hikIsupProperties.getExtendIp().length());
        m_struPlayBackInfoIn.struStreamSever.wPort = Short.parseShort(hikIsupProperties.getSmsBackPort());
        m_struPlayBackInfoIn.write();
        NET_EHOME_PLAYBACK_INFO_OUT m_struPlayBackInfoOut = new NET_EHOME_PLAYBACK_INFO_OUT();
        m_struPlayBackInfoOut.write();
        if (!hcIsupCms.NET_ECMS_StartPlayBack(device.getLoginId(), m_struPlayBackInfoIn, m_struPlayBackInfoOut)) {
            System.out.println("NET_ECMS_StartPlayBack failed, error code:" + hcIsupCms.NET_ECMS_GetLastError());
            return null;
        } else {
            m_struPlayBackInfoOut.read();
            System.out.println("NET_ECMS_StartPlayBack succeed, lSessionID:" + m_struPlayBackInfoOut.lSessionID);
        }
        // 会话id
        int lSessionID = m_struPlayBackInfoOut.lSessionID;
        NET_EHOME_PUSHPLAYBACK_IN m_struPushPlayBackIn = new NET_EHOME_PUSHPLAYBACK_IN();
        m_struPushPlayBackIn.read();
        m_struPushPlayBackIn.dwSize = m_struPushPlayBackIn.size();
        m_struPushPlayBackIn.lSessionID = lSessionID;
        m_struPushPlayBackIn.write();
        NET_EHOME_PUSHPLAYBACK_OUT m_struPushPlayBackOut = new NET_EHOME_PUSHPLAYBACK_OUT();
        m_struPushPlayBackOut.read();
        m_struPushPlayBackOut.dwSize = m_struPushPlayBackOut.size();
        m_struPushPlayBackOut.write();
        // 动态分配RTP端口
        String deviceId = device.getDeviceId();
        String streamKey = String.format("%s_%s_%d_%d", MediaConstant.PREFIX.PREFIX_ISUP_PLAYBACK, deviceId, channelId, lSessionID);
        MediaServer mediaInfo = mediaServerService.selectMediaServerBydeviceSipId(deviceId);
        VideoSessionInfo info = VideoSessionInfo.builder()
                .mediaServerId(mediaInfo.getServerId())
                .mediaServerIp(mediaInfo.getIp())
                .type(SessionType.PLAYBACK)
                .pushing(false)
                .recording(false)
                .deviceId(device.getDeviceId())
                .channelId(channelId.toString())
                .stream(streamKey)
                .ssrc(SsrcUtil.getPlayBackSsrc("3406666600"))
                .sessionId(lSessionID)
                .build();
        // 先创建RTP服务
        sendRtpServerService.createRTPServer(mediaInfo, info);
        streamSession.putBySessionId(info);
        // 开始推流
        if (!hcIsupCms.NET_ECMS_StartPushPlayBack(device.getLoginId(), m_struPushPlayBackIn, m_struPushPlayBackOut)) {
            System.out.println("NET_ECMS_StartPushPlayBack failed, error code:" + hcIsupCms.NET_ECMS_GetLastError());
        }
        StreamInfo streamInfo = new StreamInfo();
        streamInfo.setDeviceId(device.getDeviceId());
        streamInfo.setChannelId(channelId.toString());
        return streamInfo.build(mediaInfo, "rtp", streamKey);
    }

    @Override
    public Boolean stopPlayBackByTime(IsupDevInfo device, Integer channelId, String streamKey) {
        Integer loginId = device.getLoginId();
        if (channelId == null) {
            log.error("通道号不能为空，无法停止预览");
            return false;
        }
        VideoSessionInfo info = streamSession.getSessionInfo(device.getDeviceId(), channelId.toString(), streamKey, SessionType.PLAYBACK.name());
        if (info != null) {
            Integer sessionId = info.getSessionId();
            Integer previewHandleId = info.getPreviewHandle();
            if (previewHandleId != null && !hikISUPStream.NET_ESTREAM_StopPlayBack(previewHandleId)) {
                log.error("NET_ESTREAM_StopPlayBack failed,err = {}", hikISUPStream.NET_ESTREAM_GetLastError());
            }
            if (!hcIsupCms.NET_ECMS_StopPlayBack(loginId, sessionId)) {
                log.error("NET_ECMS_StopPlayBack failed,err = {}", hcIsupCms.NET_ECMS_GetLastError());
            }
            streamSession.remove(device.getDeviceId(), channelId.toString(), info.getSessionId().toString(), SessionType.PLAYBACK.name());
            log.info("CMS已发送停止回放请求");
        }
        return true;
    }

    @Override
    public void voiceTrans(Integer loginId, String fileFullPath) {
        log.info("voiceTrans fileFullPath:{}", fileFullPath);
        //获取设备通道对讲信息，包括编码格式，起始对讲通道号等，跟nvr对讲前先获取
        NET_EHOME_DEVICE_INFO res = getDeviceInfo(loginId);
        int dwVoiceChan = res.byStartDTalkChan + 3;
        byte dwAudioEncType = (byte) res.dwAudioEncType;
        int voiceTalkSessionId = StartVoiceTrans(loginId, dwVoiceChan, dwAudioEncType, fileFullPath);
        StopVoiceTrans(loginId, voiceTalkSessionId, StreamManager.lVoiceLinkHandle);
    }

    private NET_EHOME_DEVICE_INFO getDeviceInfo(Integer loginId) {
        boolean bRet;
        NET_EHOME_DEVICE_INFO ehomeDeviceInfo = new NET_EHOME_DEVICE_INFO();
        ehomeDeviceInfo.read();
        ehomeDeviceInfo.dwSize = ehomeDeviceInfo.size();
        ehomeDeviceInfo.write();

        NET_EHOME_CONFIG strEhomeCfd = new NET_EHOME_CONFIG();
        strEhomeCfd.pCondBuf = null;
        strEhomeCfd.dwCondSize = 0;
        strEhomeCfd.pOutBuf = ehomeDeviceInfo.getPointer();
        strEhomeCfd.dwOutSize = ehomeDeviceInfo.size();
        strEhomeCfd.pInBuf = null;
        strEhomeCfd.dwInSize = 0;
        strEhomeCfd.write();

        bRet = hcIsupCms.NET_ECMS_GetDevConfig(loginId, 1, strEhomeCfd.getPointer(), strEhomeCfd.size());
        if (!bRet) {
            int dwErr = hcIsupCms.NET_ECMS_GetLastError();
            System.out.println("获取报警输入参数失败，Error:" + dwErr);
        } else {
            //  读取返回的数据
            ehomeDeviceInfo.read();
            System.out.println("语音对讲的音频格式:" + ehomeDeviceInfo.dwAudioEncType);
            System.out.println("起始数字对讲通道号:" + ehomeDeviceInfo.byStartDTalkChan);
        }
        return ehomeDeviceInfo;
    }

    /**
     * 开启语音转发
     * 说明：byEncodingType为获取设备通道对讲信息中返回的编码格式，对应结构体NET_EHOME_DEVICE_INFO.dwAudioEncType;  // 语音对讲的音频格式：0-G.722，1-G.711U，2-G.711A，3-G.726，4-AAC，5-MP2L2。
     * NET_EHOME_DEVICE_INFO.dwAudioEncType为1表示g711u，对应NET_EHOME_TALK_ENCODING_TYPE中为2表示g711u
     * <p>
     * typedef enum tagNET_EHOME_TALK_ENCODING_TYPE{
     * ENUM_ENCODING_START = 0,
     * ENUM_ENCODING_G722_1, = 1
     * ENUM_ENCODING_G711_MU, = 2
     * ENUM_ENCODING_G711_A, = 3
     * ENUM_ENCODING_G723, = 4
     * ENUM_ENCODING_MP1L2, = 5
     * ENUM_ENCODING_MP2L2, = 6
     * ENUM_ENCODING_G726, = 7
     * ENUM_ENCODING_AAC, = 8
     * ENUM_ENCODING_RAW = 100
     * }NET_EHOME_TALK_ENCODING_TYPE;
     */
    private int StartVoiceTrans(Integer loginId, int dwVoiceChan, byte byEncodingType, String filePath) {
        int voiceTalkSessionId = -1;
//        byEncodingType = (byte) (byEncodingType + 1); //这里是将NET_EHOME_DEVICE_INFO.dwAudioEncType跟byEncodingType值对齐，区别见结构体NET_EHOME_DEVICE_INFO和NET_EHOME_TALK_ENCODING_TYPE的定义
        // 语音对讲开启请求的输入参数
        NET_EHOME_VOICE_TALK_IN net_ehome_voice_talk_in = new NET_EHOME_VOICE_TALK_IN();
        net_ehome_voice_talk_in.struStreamSever.szIP = hikIsupProperties.getExtendIp().getBytes();
        net_ehome_voice_talk_in.struStreamSever.wPort = Short.parseShort(hikIsupProperties.getVoiceSmsPort());
        net_ehome_voice_talk_in.dwVoiceChan = dwVoiceChan; //语音通道号,NVR设备起始通道号为3，dwVoiceChan传6对应nvr的通道4
        net_ehome_voice_talk_in.byEncodingType[0] = byEncodingType;  //跟NVR通道对讲必须带上此参数，ENUM_ENCODING_G722_1 = 1；ENUM_ENCODING_G711_MU = 2 ；ENUM_ENCODING_G711_A = 3
//        net_ehome_voice_talk_in.byAudioSamplingRate = 5;
        net_ehome_voice_talk_in.write();
        // 语音对讲开启请求的输出参数
        NET_EHOME_VOICE_TALK_OUT net_ehome_voice_talk_out = new NET_EHOME_VOICE_TALK_OUT();
        // 将语音对讲开启请求从CMS 发送给设备发送SMS 的地址和端口号给设备，设备自动为CMS 分配一个会话ID。
        if (!hcIsupCms.NET_ECMS_StartVoiceWithStmServer(loginId, net_ehome_voice_talk_in, net_ehome_voice_talk_out)) {
            System.out.println("NET_ECMS_StartVoiceWithStmServer failed, error code:" + hcIsupCms.NET_ECMS_GetLastError());
            return voiceTalkSessionId;
        } else {
            net_ehome_voice_talk_out.read();
            System.out.println("NET_ECMS_StartVoiceWithStmServer suss sessionID=" + net_ehome_voice_talk_out.lSessionID);
        }

        // 语音传输请求的输入参数
        NET_EHOME_PUSHVOICE_IN struPushVoiceIn = new NET_EHOME_PUSHVOICE_IN();
        struPushVoiceIn.dwSize = struPushVoiceIn.size();
        struPushVoiceIn.lSessionID = net_ehome_voice_talk_out.lSessionID;
        voiceTalkSessionId = net_ehome_voice_talk_out.lSessionID;
        struPushVoiceIn.write();
        // 语音传输请求的输出参数
        NET_EHOME_PUSHVOICE_OUT struPushVoiceOut = new NET_EHOME_PUSHVOICE_OUT();
        struPushVoiceOut.dwSize = struPushVoiceOut.size();
        struPushVoiceOut.write();
        // 将语音传输请求从CMS 发送给设备。设备自动连接SMS 并开始发送音频数据给SMS
        if (!hcIsupCms.NET_ECMS_StartPushVoiceStream(loginId, struPushVoiceIn, struPushVoiceOut)) {
            System.out.println("NET_ECMS_StartPushVoiceStream failed, error code:" + hcIsupCms.NET_ECMS_GetLastError());
            return voiceTalkSessionId;
        }
        System.out.println("NET_ECMS_StartPushVoiceStream success!\n");
        //发送音频数据
        FileInputStream voiceInputStream = null;
        int dataLength = 0;
        try {
            //创建从文件读取数据的FileInputStream流
            voiceInputStream = new FileInputStream(filePath);
            //返回文件的总字节数
            dataLength = voiceInputStream.available();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        if (dataLength < 0) {
            System.out.println("input file dataSize < 0");
            throw new RuntimeException("输入的文件");
//            return false;
        }
        BYTE_ARRAY ptrVoiceByte = new BYTE_ARRAY(dataLength);
        try {
            if (voiceInputStream != null) {
                voiceInputStream.read(ptrVoiceByte.byValue);
            }
        } catch (IOException e2) {
            log.error(e2.getMessage());
        }
        ptrVoiceByte.write();
        int iSendData = 160;//G722编码每20毫秒发送80字节，G711编码每20毫秒发送160字节
        for (int i = 0; i < dataLength / iSendData; i++) {
            BYTE_ARRAY ptrG711Send = new BYTE_ARRAY(iSendData);
            System.arraycopy(ptrVoiceByte.byValue, i * iSendData, ptrG711Send.byValue, 0, iSendData);
            ptrG711Send.write();
            NET_EHOME_VOICETALK_DATA struVoicTalkData = new NET_EHOME_VOICETALK_DATA();
            struVoicTalkData.pData = ptrG711Send.getPointer();
            struVoicTalkData.dwDataLen = iSendData;
            struVoicTalkData.write();
            // 将音频数据发送给设备
            if (hikISUPStream.NET_ESTREAM_SendVoiceTalkData(StreamManager.lVoiceLinkHandle, struVoicTalkData) <= -1) {
                System.out.println("NET_ESTREAM_SendVoiceTalkData failed, error code:" + hikISUPStream.NET_ESTREAM_GetLastError());
            }
            //需要实时速率发送数据
            try {
                Thread.sleep(19);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                log.error(e.getMessage());
            }
        }
        return voiceTalkSessionId;
    }

    /**
     * 停止语音对讲
     */
    private void StopVoiceTrans(int loginId, int voiceTalkSessionId, int lVoiceLinkHandle) {
        //SMS 停止语音对讲
        if (lVoiceLinkHandle >= 0) {
            if (!hikISUPStream.NET_ESTREAM_StopVoiceTalk(lVoiceLinkHandle)) {
                System.out.println("NET_ESTREAM_StopVoiceTalk failed, error code:" + hikISUPStream.NET_ESTREAM_GetLastError());
                return;
            }
        }
        //释放语音对讲请求资源
        if (!hcIsupCms.NET_ECMS_StopVoiceTalkWithStmServer(loginId, voiceTalkSessionId)) {
            System.out.println("NET_ECMS_StopVoiceTalkWithStmServer failed, error code:" + hcIsupCms.NET_ECMS_GetLastError());
            return;
        }
        log.info("NET_ESTREAM_StopVoiceTalk success!");
    }
}
