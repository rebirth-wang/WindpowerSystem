package com.fastbee.sip.server.impl;

import java.text.ParseException;
import java.util.List;

import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import javax.sip.header.CallIdHeader;
import javax.sip.message.Request;

import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fastbee.media.domain.MediaServer;
import com.fastbee.media.enums.SessionType;
import com.fastbee.media.model.VideoSessionInfo;
import com.fastbee.media.service.IMediaServerService;
import com.fastbee.media.service.impl.VideoSessionManager;
import com.fastbee.media.util.ZlmApiUtils;
import com.fastbee.media.util.ZlmRtpUtils;
import com.fastbee.sip.domain.SipConfig;
import com.fastbee.sip.domain.SipDevice;
import com.fastbee.sip.model.BroadcastItem;
import com.fastbee.sip.model.InviteInfo;
import com.fastbee.sip.server.*;
import com.fastbee.sip.server.cluster.SipSsrcManager;
import com.fastbee.sip.server.session.AudioBroadcastManager;
import com.fastbee.sip.server.session.InviteSessionManager;
import com.fastbee.sip.service.ISipConfigService;
import com.fastbee.sip.service.ISipDeviceService;
import com.fastbee.sip.service.ISipSendRtpService;
import com.fastbee.sip.util.SipUtil;

@Slf4j
@Component
public class SipCmdImpl implements ISipCmd {

    @Autowired
    private VideoSessionManager streamSession;

    @Autowired
    private ReqMsgHeaderBuilder headerBuilder;

    @Autowired
    private ISipConfigService sipConfigService;

    @Autowired
    private IMediaServerService mediaServerService;

    @Autowired
    private ISipDeviceService sipDeviceService;

    @Autowired
    private ZlmRtpUtils zlmRtpUtils;

    @Autowired
    private InviteSessionManager inviteSessionManager;

    @Autowired
    private AudioBroadcastManager audioBroadcastManager;

    @Autowired
    private SipLayer sipLayer;

    @Autowired
    private SIPSender sipSender;

    @Autowired
    private ZlmApiUtils zlmApiUtils;

    @Autowired
    private SipSsrcManager sipSsrcManager;

    @Autowired
    private ISipSendRtpService sendRtpService;


    @Override
    public VideoSessionInfo playStreamCmd(SipDevice device, String channelId, Boolean record) {
        try {
            SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(device.getDeviceSipId());
            if (sipConfig == null) {
                log.error("playStreamCmd sipConfig is null");
                return null;
            }
            MediaServer mediaInfo = mediaServerService.selectMediaServerBydeviceSipId(device.getDeviceSipId());
            if (mediaInfo == null) {
                log.error("playStreamCmd mediaInfo is null");
                return null;
            }
            VideoSessionInfo info = VideoSessionInfo.builder()
                    .mediaServerId(mediaInfo.getServerId())
                    .deviceId(device.getDeviceSipId())
                    .channelId(channelId)
                    .pushing(false)
                    .recording(false)
                    .streamMode(device.getStreamMode().toUpperCase())
                    .build();
            String fromTag;
            if (record != null && record) {
                info.setType(SessionType.PLAYRECORD);
                fromTag = "playrecord";
            } else {
                info.setType(SessionType.PLAY);
                fromTag = "play";
            }
            // 创建rtp服务器
            info = sendRtpService.createRTPServer(sipConfig, mediaInfo, device, info);
            // 创建Invite会话
            String content = buildRequestContent(sipConfig, mediaInfo, info);
            CallIdHeader callIdHeader = sipSender.getNewCallIdHeader(sipLayer.getLocalIp(device.getIp()),device.getTransport());
            Request request = headerBuilder.createInviteRequest(device, sipConfig, channelId, content, info.getSsrc(), fromTag, callIdHeader);
            // 发送消息
            sipSender.transmitRequest(sipLayer.getLocalIp(device.getIp()),request);

            log.info("playStreamCmd streamSession: {}", info);
            InviteInfo invite = InviteInfo.builder()
                    .ssrc(info.getSsrc())
                    .fromTag(fromTag)
                    .callId(callIdHeader.getCallId())
                    .port(info.getPort()).build();
            log.warn("playStreamCmd invite: {}", invite);
            inviteSessionManager.updateInviteInfo(info, invite);
            streamSession.put(info);
            return info;
        } catch (SipException | ParseException | InvalidArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    public VideoSessionInfo playScreenshotCmd(SipDevice device, String channelId, String fileName) {
        try {
            SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(device.getDeviceSipId());
            if (sipConfig == null) {
                log.error("playScreenshotCmd sipConfig is null");
                return null;
            }
            MediaServer mediaInfo = mediaServerService.selectMediaServerBydeviceSipId(device.getDeviceSipId());
            if (mediaInfo == null) {
                log.error("playScreenshotCmd mediaInfo is null");
                return null;
            }
            VideoSessionInfo info = VideoSessionInfo.builder()
                    .mediaServerId(mediaInfo.getServerId())
                    .deviceId(device.getDeviceSipId())
                    .channelId(channelId)
                    .type(SessionType.PLAY)
                    .pushing(false)
                    .recording(false)
                    .shotPath(fileName)
                    .streamMode(device.getStreamMode().toUpperCase())
                    .build();
            // 创建rtp服务器
            info = sendRtpService.createRTPServer(sipConfig, mediaInfo, device, info);
            // 创建Invite会话
            String content = buildRequestContent(sipConfig, mediaInfo, info);
            CallIdHeader callIdHeader = sipSender.getNewCallIdHeader(sipLayer.getLocalIp(device.getIp()),device.getTransport());
            Request request = headerBuilder.createInviteRequest(device, sipConfig, channelId, content, info.getSsrc(), "play", callIdHeader);
            // 发送消息
            sipSender.transmitRequest(sipLayer.getLocalIp(device.getIp()),request);

            log.info("playScreenshotCmd streamSession: {}", info);
            InviteInfo invite = InviteInfo.builder()
                    .ssrc(info.getSsrc())
                    .fromTag("play")
                    .callId(callIdHeader.getCallId())
                    .port(info.getPort()).build();
            log.warn("playScreenshotCmd invite: {}", invite);
            inviteSessionManager.updateInviteInfo(info, invite);
            streamSession.put(info);
            return info;
        } catch (SipException | ParseException | InvalidArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public VideoSessionInfo playbackStreamCmd(SipDevice device, String channelId, String startTime, String endTime) {
        try {
            SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(device.getDeviceSipId());
            if (sipConfig == null) {
                log.error("playbackStreamCmd sipConfig is null");
                return null;
            }
            MediaServer mediaInfo = mediaServerService.selectMediaServerBydeviceSipId(device.getDeviceSipId());
            if (mediaInfo == null) {
                log.error("playbackStreamCmd mediaInfo is null");
                return null;
            }
            VideoSessionInfo info = VideoSessionInfo.builder()
                    .mediaServerId(mediaInfo.getServerId())
                    .deviceId(device.getDeviceSipId())
                    .channelId(channelId)
                    .streamMode(device.getStreamMode().toUpperCase())
                    .type(SessionType.PLAYBACK)
                    .pushing(false)
                    .recording(false)
                    .startTime(startTime)
                    .endTime(endTime)
                    .build();
            //创建rtp服务器
            info = sendRtpService.createRTPServer(sipConfig, mediaInfo, device, info);
            //创建Invite会话
            String fromTag = "playback" + SipUtil.getNewFromTag();
            String viaTag = SipUtil.getNewViaTag();
            String content = buildRequestContent(sipConfig, mediaInfo, info);
            CallIdHeader callIdHeader = sipSender.getNewCallIdHeader(sipLayer.getLocalIp(device.getIp()),device.getTransport());
            Request request = headerBuilder.createPlaybackInviteRequest(device, sipConfig, channelId, content, viaTag, fromTag, callIdHeader);
            //发送消息
            sipSender.transmitRequest(sipLayer.getLocalIp(device.getIp()),request);
            log.info("playbackStreamCmd streamSession: {}", info);
            InviteInfo invite = InviteInfo.builder()
                    .ssrc(info.getSsrc())
                    .fromTag(fromTag)
                    .viaTag(viaTag)
                    .callId(callIdHeader.getCallId())
                    .port(info.getPort()).build();
            log.warn("playbackStreamCmd invite: {}", invite);
            inviteSessionManager.updateInviteInfo(info, invite);
            streamSession.put(info);
            return info;
        } catch (SipException | ParseException | InvalidArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public VideoSessionInfo downloadStreamCmd(SipDevice device, String channelId,
                                              String startTime, String endTime, int downloadSpeed) {
        try {
            SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(device.getDeviceSipId());
            if (sipConfig == null) {
                log.error("downloadStreamCmd sipConfig is null");
                return null;
            }
            MediaServer mediaInfo = mediaServerService.selectMediaServerBydeviceSipId(device.getDeviceSipId());
            if (mediaInfo == null) {
                log.error("downloadStreamCmd mediaInfo is null");
                return null;
            }
            VideoSessionInfo info = VideoSessionInfo.builder()
                    .mediaServerId(mediaInfo.getServerId())
                    .deviceId(device.getDeviceSipId())
                    .channelId(channelId)
                    .streamMode(device.getStreamMode().toUpperCase())
                    .type(SessionType.DOWNLOAD)
                    .pushing(false)
                    .recording(false)
                    .startTime(startTime)
                    .endTime(endTime)
                    .downloadSpeed(downloadSpeed)
                    .build();
            ;
            //创建rtp服务器
            info = sendRtpService.createRTPServer(sipConfig, mediaInfo, device, info);
            //创建Invite会话
            String fromTag = "download" + SipUtil.getNewFromTag();;
            String viaTag = SipUtil.getNewViaTag();
            String content = buildRequestContent(sipConfig, mediaInfo, info);
            CallIdHeader callIdHeader = sipSender.getNewCallIdHeader(sipLayer.getLocalIp(device.getIp()),device.getTransport());
            Request request = headerBuilder.createPlaybackInviteRequest(device, sipConfig, channelId, content, viaTag, fromTag, callIdHeader);
            //发送消息
            sipSender.transmitRequest(sipLayer.getLocalIp(device.getIp()),request);
            log.info("downloadStreamCmd streamSession: {}", info);
            InviteInfo invite = InviteInfo.builder()
                    .ssrc(info.getSsrc())
                    .fromTag(fromTag)
                    .viaTag(viaTag)
                    .callId(callIdHeader.getCallId())
                    .port(info.getPort()).build();
            log.warn("downloadStreamCmd invite: {}", invite);
            inviteSessionManager.updateInviteInfo(info, invite);
            streamSession.put(info);
            return info;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Boolean streamByeCmd(String deviceId, String channelId, String stream, String ssrc, Boolean check) {
        SipDevice dev = sipDeviceService.selectSipDeviceBySipId(deviceId);
        if (dev == null) {
            log.error("[发送BYE] device is null");
            return false;
        }
        return streamByeCmd(dev, channelId, stream, ssrc, check);
    }

    @Override
    public Boolean streamByeCmd(SipDevice device, String channelId, String stream, String ssrc, Boolean check) {
        SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(device.getDeviceSipId());
        if (sipConfig == null) {
            log.error("[发送BYE] sipConfig is null");
            return false;
        }
        MediaServer mediaInfo = mediaServerService.selectMediaServerBydeviceSipId(device.getDeviceSipId());
        if (mediaInfo == null) {
            log.error("[发送BYE] mediaInfo is null");
            return false;
        }
        List<VideoSessionInfo> SessionInfoList = streamSession.getSessionInfoForAll(device.getDeviceSipId(), channelId, stream, ssrc);
        if (SessionInfoList == null || SessionInfoList.isEmpty()) {
            log.warn("[发送BYE] 未找到事务信息,设备： device: {}, channel: {}", device.getDeviceSipId(), channelId);
            return false;
        }
        for (VideoSessionInfo info : SessionInfoList) {
            if(check) {
                JSONObject ret = zlmApiUtils.getMediaInfo(mediaInfo, "rtp", "rtmp", info.getStream());
                int code = ret.getInteger("code");
                if (code == 0) {
                    int readerCount = ret.getInteger("readerCount");
                    log.info("还有{}位用户正在观看该流！", readerCount);
                    if (readerCount > 0) {
                        log.info("流id:{} 不关闭！", info.getStream());
                        continue;
                    }
                } else {
                    log.info("流详细信息：{}，错误码：{}", ret, code);
                }
                streamByeCmd(device, sipConfig, mediaInfo, info);
            } else {
                streamByeCmd(device, sipConfig, mediaInfo, info);
            }
        }
        return true;
    }

    private void streamByeCmd(SipDevice device, SipConfig sipConfig, MediaServer mediaInfo, VideoSessionInfo info) {
        try {
            log.warn("[发送BYE] 设备： device: {}, channel: {}, stream: {}, ssrc: {}", info.getDeviceId(),
                    info.getChannelId(), info.getStream(), info.getSsrc());
            List<InviteInfo> list = inviteSessionManager.getInviteInfoAll(info.getType(), info.getDeviceId(), info.getChannelId(), info.getStream());
            if (list.isEmpty()) {
                log.warn("[发送BYE] 未找到invite信息,设备： Stream: {}", info.getStream());
            } else {
                for (InviteInfo invite : list) {
                    // 发送bye消息
                    Request request = headerBuilder.createByeRequest(device, sipConfig, info.getChannelId(), invite);
                    sipSender.transmitRequest(sipLayer.getLocalIp(device.getIp()), request);
                    // 释放 ssrc
                    if (info.getSsrc() != null) {
                        sipSsrcManager.releaseSsrc(info.getSsrc());
                    }
                    // 关闭rtp服务器
                    zlmRtpUtils.closeRTPServer(mediaInfo, info.getStream());
                    log.warn("closeRTPServer Port:{}", info.getPort());
                    if (info.getPushing()!=null && info.getPushing()) {
                        info.setPushing(false);
                    }
                    if (info.getRecording()!=null && info.getRecording()) {
                        info.setRecording(false);
                    }
                    streamSession.put(info);
                    // 删除会话缓存
                    if (info.getSsrc() != null) {
                        streamSession.remove(info.getDeviceId(), info.getChannelId(), info.getStream(), info.getSsrc());
                    }
                    // 删除invite缓存
                    inviteSessionManager.removeInviteInfo(info.getType(), info.getDeviceId(), info.getChannelId(), info.getStream());
                }
            }
        } catch (ParseException | SipException | InvalidArgumentException e) {
            e.printStackTrace();
        }
    }

    public void streamByeCmd(SipDevice device, String channelId, SessionType type, Boolean check) {
        SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(device.getDeviceSipId());
        if (sipConfig == null) {
            log.error("[发送BYE] sipConfig is null");
            return;
        }
        MediaServer mediaInfo = mediaServerService.selectMediaServerBydeviceSipId(device.getDeviceSipId());
        if (mediaInfo == null) {
            log.error("[发送BYE] mediaInfo is null");
            return;
        }
        List<InviteInfo> invites = inviteSessionManager.getInviteInfoList(type, device.getDeviceSipId(), channelId, null);
        if (invites != null && !invites.isEmpty()) {
            log.warn("device：{}，channel：{} 存在{}条未关闭的流！", device.getDeviceSipId(), channelId, invites.size());
            for (InviteInfo invite : invites) {
                if(check) {
                    JSONObject ret = zlmApiUtils.getMediaInfo(mediaInfo, "rtp", "rtmp", invite.getSsrc());
                    int code = ret.getInteger("code");
                    if (code == 0) {
                        int readerCount = ret.getInteger("readerCount");
                        log.info("还有{}位用户正在观看该流！", readerCount);
                        if (readerCount > 0) {
                            log.info("流id:{} 不关闭！", invite.getSsrc());
                            continue;
                        }
                    } else {
                        log.info("流详细信息：{}，错误码：{}", ret, code);
                    }
                    VideoSessionInfo info = streamSession.getSessionInfoBySSRC(invite.getSsrc());
                    streamByeCmd(device, sipConfig, mediaInfo, info);
                } else {
                    VideoSessionInfo info = streamSession.getSessionInfoBySSRC(invite.getSsrc());
                    streamByeCmd(device, sipConfig, mediaInfo, info);
                }
            }
        }
    }

    @Override
    public void streamByeCmdByInvite(SipDevice device, String channelId, InviteInfo invite) {
        SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(device.getDeviceSipId());
        if (sipConfig == null) {
            log.error("[发送BYE] sipConfig is null");
            return;
        }
        try {
            Request request = headerBuilder.createByeRequest(device, sipConfig, channelId, invite);
            sipSender.transmitRequest(sipLayer.getLocalIp(device.getIp()), request);
        } catch (ParseException | SipException | InvalidArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void audioBroadcastCmd(SipDevice device, String channelId) {
        SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(device.getDeviceSipId());
        if (sipConfig == null) {
            log.error("audioBroadcastCmd deviceSipId:{} not found", device.getDeviceSipId());
            return ;
        }
        String broadcastXml = "<?xml version=\"1.0\" encoding=\"GB2312\"?>\r\n" +
                "<Notify>\r\n" +
                "<CmdType>Broadcast</CmdType>\r\n" +
                "<SN>" + (int) ((Math.random() * 9 + 1) * 100000) + "</SN>\r\n" +
                "<SourceID>" + sipConfig.getServerSipid() + "</SourceID>\r\n" +
                "<TargetID>" + channelId + "</TargetID>\r\n" +
                "</Notify>\r\n";
        try {
            Request request = headerBuilder.createMessageRequest(device, sipConfig, broadcastXml.toString(), SipUtil.getNewFromTag(), sipSender.getNewCallIdHeader(sipLayer.getLocalIp(device.getIp()),device.getTransport()));
            sipSender.transmitRequest(sipLayer.getLocalIp(device.getIp()), request);
            // 更新广播状态
            BroadcastItem broadcastItem = BroadcastItem.builder()
                    .deviceId(device.getDeviceSipId())
                    .channelId(channelId)
                    .app("broadcast")
                    .stream(String.format("%s_%s_%s", SipUtil.PREFIX_TALK, device.getDeviceSipId(), channelId))
                    .build();
            audioBroadcastManager.update(broadcastItem);
        } catch (SipException | ParseException | InvalidArgumentException e) {
            log.error("audioBroadcastCmd error", e);
        }
    }


    private String buildRequestContent(SipConfig sipConfig, MediaServer mediaInfo, VideoSessionInfo info) {
        String streamMode = info.getStreamMode();
        StringBuilder content = new StringBuilder(200);
        content.append("v=0\r\n");
        switch (info.getType()) {
            case PLAY:
                content.append("o=").append(info.getChannelId()).append(" 0 0 IN IP4 ").append(mediaInfo.getIp()).append("\r\n");
                content.append("s=Play\r\n");
                content.append("c=IN IP4 ").append(mediaInfo.getIp()).append("\r\n");
                content.append("t=0 0\r\n");
                break;
            case PLAYRECORD:
                content.append("o=").append(info.getChannelId()).append(" 0 0 IN IP4 ").append(mediaInfo.getIp()).append("\r\n");
                content.append("s=Play\r\n");
                content.append("c=IN IP4 ").append(mediaInfo.getIp()).append("\r\n");
                content.append("t=0 0\r\n");
                break;
            case PLAYBACK:
                content.append("o=").append(info.getChannelId()).append(" 0 0 IN IP4 ").append(mediaInfo.getIp()).append("\r\n");
                content.append("s=Playback\r\n");
                content.append("u=").append(info.getChannelId()).append(":0\r\n");
                content.append("c=IN IP4 ").append(mediaInfo.getIp()).append("\r\n");
                content.append("t=").append(info.getStartTime()).append(" ").append(info.getEndTime()).append("\r\n");
                break;
            case DOWNLOAD:
                content.append("o=").append(info.getChannelId()).append(" 0 0 IN IP4 ").append(mediaInfo.getIp()).append("\r\n");
                content.append("s=Download\r\n");
                content.append("u=").append(info.getChannelId()).append(":0\r\n");
                content.append("c=IN IP4 ").append(mediaInfo.getIp()).append("\r\n");
                content.append("t=").append(info.getStartTime()).append(" ").append(info.getEndTime()).append("\r\n");
                break;
        }
        if (sipConfig.getSeniorSdp() != null && sipConfig.getSeniorSdp() == 1) {
            if ("TCP-PASSIVE".equals(streamMode)) {
                content.append("m=video ").append(info.getPort()).append(" TCP/RTP/AVP 96 126 125 99 34 98 97\r\n");
            } else if ("TCP-ACTIVE".equals(streamMode)) {
                content.append("m=video ").append(info.getPort()).append(" TCP/RTP/AVP 96 126 125 99 34 98 97\r\n");
            } else if ("UDP".equals(streamMode)) {
                content.append("m=video ").append(info.getPort()).append(" RTP/AVP 96 126 125 99 34 98 97\r\n");
            }
            content.append("a=recvonly\r\n");
            content.append("a=rtpmap:96 PS/90000\r\n");
            content.append("a=fmtp:126 profile-level-id=42e01e\r\n");
            content.append("a=rtpmap:126 H264/90000\r\n");
            content.append("a=rtpmap:125 H264S/90000\r\n");
            content.append("a=fmtp:125 profile-level-id=42e01e\r\n");
            content.append("a=rtpmap:99 MP4V-ES/90000\r\n");
            content.append("a=fmtp:99 profile-level-id=3\r\n");
            content.append("a=rtpmap:98 H264/90000\r\n");
            content.append("a=rtpmap:97 MPEG4/90000\r\n");
            if ("TCP-PASSIVE".equals(streamMode)) {
                // tcp被动模式
                content.append("a=setup:passive\r\n");
                content.append("a=connection:new\r\n");
            } else if ("TCP-ACTIVE".equals(streamMode)) {
                // tcp主动模式
                content.append("a=setup:active\r\n");
                content.append("a=connection:new\r\n");
            }
        } else {
            switch (streamMode) {
                case "TCP-PASSIVE":
                    content.append("m=video ").append(info.getPort()).append(" TCP/RTP/AVP 96 97 98 99\r\n");
                    break;
                case "TCP-ACTIVE":
                    content.append("m=video ").append(info.getPort()).append(" TCP/RTP/AVP 96 97 98 99\r\n");
                    break;
                case "UDP":
                    //content.append("m=video ").append(info.getPort()).append(" RTP/AVP 96 97 98 99\r\n");
                    content.append("m=video ").append(info.getPort()).append(" RTP/AVP 96 97 98\r\n");
                    break;
            }
            content.append("a=recvonly\r\n");
            content.append("a=rtpmap:96 PS/90000\r\n");
            content.append("a=rtpmap:97 MPEG4/90000\r\n");
            content.append("a=rtpmap:98 H264/90000\r\n");
            content.append("a=rtpmap:99 H265/90000\r\n");

            if ("TCP-PASSIVE".equals(streamMode)) {
                // tcp被动模式
                content.append("a=setup:passive\r\n");
                content.append("a=connection:new\r\n");
            } else if ("TCP-ACTIVE".equals(streamMode)) {
                // tcp主动模式
                content.append("a=setup:active\r\n");
                content.append("a=connection:new\r\n");
            }
        }
        if (info.getType() == SessionType.DOWNLOAD) {
            content.append("a=downloadspeed:").append(info.getDownloadSpeed()).append("\r\n");
        }
        // ssrc
        content.append("y=").append(info.getSsrc()).append("\r\n");
        return content.toString();
    }
}
