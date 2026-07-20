package com.fastbee.sip.handler.req;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

import javax.sdp.Media;
import javax.sdp.MediaDescription;
import javax.sdp.SessionDescription;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.header.CallIdHeader;
import javax.sip.header.FromHeader;
import javax.sip.message.Response;

import gov.nist.javax.sdp.TimeDescriptionImpl;
import gov.nist.javax.sdp.fields.TimeField;
import gov.nist.javax.sdp.fields.URIField;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fastbee.common.extend.exception.ServiceException;
import com.fastbee.common.extend.exception.enums.GlobalErrorCodeConstants;
import com.fastbee.media.domain.MediaServer;
import com.fastbee.media.enums.InviteType;
import com.fastbee.media.enums.SessionType;
import com.fastbee.media.model.InviteMsgInfo;
import com.fastbee.media.model.SendRtpItem;
import com.fastbee.media.model.VideoSessionInfo;
import com.fastbee.media.service.IMediaServerService;
import com.fastbee.media.service.ISendRtpServerService;
import com.fastbee.media.util.ZlmRtpUtils;
import com.fastbee.sip.domain.SipConfig;
import com.fastbee.sip.domain.SipDevice;
import com.fastbee.sip.enums.AudioBroadcastStatus;
import com.fastbee.sip.handler.IReqHandler;
import com.fastbee.sip.model.*;
import com.fastbee.sip.server.IGBListener;
import com.fastbee.sip.server.session.AudioBroadcastManager;
import com.fastbee.sip.server.session.InviteSessionManager;
import com.fastbee.sip.service.*;
import com.fastbee.sip.util.SipUtil;

@Slf4j
@Component
public class InviteReqHandler extends ReqAbstractHandler implements InitializingBean, IReqHandler {
    private static final String INVITE_BAD_REQUEST_MESSAGE = "invite process failed";

    @Autowired
    private IGBListener sipListener;

    @Autowired
    private ISipDeviceService sipDeviceService;

    @Autowired
    private IMediaServerService mediaServerService;

    @Autowired
    private ISendRtpServerService sendRtpService;

    @Autowired
    private ZlmRtpUtils zlmRtpUtils;

    @Autowired
    private ISipConfigService sipConfigService;

    @Autowired
    private AudioBroadcastManager audioBroadcastManager;

    @Autowired
    private InviteSessionManager inviteSessionManager;

    @Autowired
    private ISipTalkService talkService;

    @Override
    public void processMsg(RequestEvent evt) {
        log.info("接收到INVITE信息");
        SIPRequest request = (SIPRequest) evt.getRequest();
        try {
            InviteMsgInfo inviteInfo = decode(evt);
            inviteFromDeviceHandle(request, inviteInfo);
        } catch (ServiceException e) {
            handleInviteServiceException(request, e);
        } catch (Exception e) {
            handleInviteUnexpectedException(request, e);
        }

    }

    private void handleInviteServiceException(SIPRequest request, ServiceException e) {
        String message = defaultIfBlank(e.getMessage(), INVITE_BAD_REQUEST_MESSAGE);
        log.warn("[INVITE处理失败] callId={}, code={}, message={}", getCallId(request), e.getCode(), message);
        responseInviteError(request, Response.BAD_REQUEST, message);
    }

    private void handleInviteUnexpectedException(SIPRequest request, Exception e) {
        String message = defaultIfBlank(e.getMessage(), INVITE_BAD_REQUEST_MESSAGE);
        log.error("[INVITE处理异常] callId={}, message={}", getCallId(request), message, e);
        responseInviteError(request, Response.BAD_REQUEST, INVITE_BAD_REQUEST_MESSAGE);
    }

    private void responseInviteError(SIPRequest request, int statusCode, String message) {
        try {
            responseAck(request, statusCode, message);
        } catch (SipException | InvalidArgumentException | ParseException sendException) {
            log.error("[命令发送失败] invite response failed, callId={}, status={}, reason={}, error={}",
                    getCallId(request), statusCode, message, sendException.getMessage(), sendException);
        }
    }

    private String getCallId(SIPRequest request) {
        if (request == null || request.getCallIdHeader() == null) {
            return null;
        }
        return request.getCallIdHeader().getCallId();
    }

    private String defaultIfBlank(String value, String defaultValue) {
        return StringUtils.isBlank(value) ? defaultValue : value;
    }

    public void inviteFromDeviceHandle(SIPRequest request, InviteMsgInfo inviteInfo) throws InvalidArgumentException, ParseException, SipException {
        // 非上级平台请求，查询是否设备请求（通常为接收语音广播的设备）
        SipDevice device = sipDeviceService.selectSipDeviceBySipId(inviteInfo.getRequestId());
        if (device != null) {
            log.info("收到设备" + inviteInfo.getRequestId() + "的语音广播Invite请求");
            try {
                responseAck(request, Response.TRYING);
            } catch (SipException | InvalidArgumentException | ParseException e) {
                log.error("[命令发送失败] INVITE TRYING BAD_REQUEST: {}", e.getMessage());
            }
            log.info("inviteInfo:{}", inviteInfo);
            log.info("设备{}：{} 请求语音流，{} 地址：{}:{}，ssrc：{}, {}", inviteInfo.getRequestId(), inviteInfo.getSourceChannelId(), inviteInfo.getIp(), inviteInfo.getOriginIp(), inviteInfo.getPort(), inviteInfo.getSsrc(),
                    inviteInfo.isTcp() ? (inviteInfo.isTcpActive() ? "TCP主动" : "TCP被动") : "UDP");

            // 语音广播状态管理
            BroadcastItem broadcastItem = audioBroadcastManager.get(inviteInfo.getSourceChannelId());
            if (broadcastItem == null) {
                log.warn("来自设备的Invite请求非语音广播，已忽略，requesterId： {}/{}", inviteInfo.getRequestId(), inviteInfo.getSourceChannelId());
                try {
                    responseAck(request, Response.FORBIDDEN);
                } catch (SipException | InvalidArgumentException | ParseException e) {
                    log.error("[命令发送失败] 来自设备的Invite请求非语音广播 FORBIDDEN: {}", e.getMessage());
                }
                return;
            }

            // 选择流媒体服务器
            MediaServer mediaInfo = mediaServerService.selectMediaServerBydeviceSipId(device.getDeviceSipId());
            if (mediaInfo == null) {
                log.error("broadcastInviteOk mediaInfo is null");
                return;
            }
            log.info("broadcastInviteOk mediaInfo is {}", mediaInfo);
            // 缓存rtp发送相关信息
            SendRtpItem sendRtpItem = sendRtpService.createSendRtpInfo(mediaInfo,
                    inviteInfo.getIp(),
                    inviteInfo.getPort(),
                    inviteInfo.getSsrc(),
                    inviteInfo.getRequestId(),
                    device.getDeviceSipId(),
                    inviteInfo.getSourceChannelId(),
                    inviteInfo.isTcp(),
                    false);

            CallIdHeader callIdHeader = (CallIdHeader) request.getHeader(CallIdHeader.NAME);
            FromHeader fheader = (FromHeader) request.getHeader(FromHeader.NAME);
            sendRtpItem.setApp(broadcastItem.getApp());
            sendRtpItem.setStream(broadcastItem.getStream());
            sendRtpItem.setPlayType(InviteType.BROADCAST);
            sendRtpItem.setCallId(callIdHeader.getCallId());
            sendRtpItem.setStatus(1);
            sendRtpItem.setFromTag(fheader.getTag());
            sendRtpItem.setPt(8);
            sendRtpItem.setUsePs(false);
            sendRtpItem.setOnlyAudio(true);
            sendRtpItem.setTcpActive(inviteInfo.isTcpActive());
            sendRtpService.update(sendRtpItem);

            log.info("sendRtpService sendRtpItem is {}", sendRtpItem);

            // 判断发送的流是否就绪
            Boolean isReady = zlmRtpUtils.isStreamReady(mediaInfo, broadcastItem.getApp(), broadcastItem.getStream());
            if (isReady) {
                sendInviteOk(request, device, mediaInfo, sendRtpItem, inviteInfo);
            } else {
                log.warn("[语音通话]，未发现待推送的流,app={},stream={}", broadcastItem.getApp(), broadcastItem.getStream());
                try {
                    responseAck(request, Response.GONE);
                } catch (SipException | InvalidArgumentException | ParseException e) {
                    log.error("[命令发送失败] 语音通话 回复410失败， {}", e.getMessage());
                }
                talkService.broadcastStop(inviteInfo.getRequestId(), inviteInfo.getSourceChannelId());
            }
        } else {
            log.warn("来自无效设备/平台的请求");
            try {
                // 不支持的格式，发415
                responseAck(request, Response.BAD_REQUEST);
            } catch (SipException | InvalidArgumentException | ParseException e) {
                log.error("[命令发送失败] invite 来自无效设备/平台的请求， {}", e.getMessage());
            }
            talkService.broadcastStop(inviteInfo.getRequestId(), inviteInfo.getSourceChannelId());
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String method = "INVITE";
        sipListener.addRequestProcessor(method, this);
    }

    private InviteMsgInfo decode(RequestEvent evt) throws Exception {
        InviteMsgInfo inviteInfo = new InviteMsgInfo();
        SIPRequest request = (SIPRequest) evt.getRequest();
        String[] channelIdArrayFromSub = SipUtil.getChannelIdFromRequest(request);

        // 解析SDP消息, 使用jainsip 自带的sdp解析方式
        String contentString = new String(request.getRawContent());
        GbSdp gb28181Sdp = SipUtil.parseSDP(contentString);
        SessionDescription sdp = gb28181Sdp.getBaseSdb();
        String sessionName = sdp.getSessionName().getValue();
        String channelIdFromSdp = null;
        if (StringUtils.equalsIgnoreCase("Playback", sessionName)) {
            URIField uriField = (URIField) sdp.getURI();
            channelIdFromSdp = uriField.getURI().split(":")[0];
        }
        final String channelId = StringUtils.isNotBlank(channelIdFromSdp) ? channelIdFromSdp :
                (channelIdArrayFromSub != null ? channelIdArrayFromSub[0] : null);
        String requestId = SipUtil.getUserIdFromFromHeader(request);
        CallIdHeader callIdHeader = (CallIdHeader) request.getHeader(CallIdHeader.NAME);
        if (requestId == null || channelId == null) {
            log.warn("[解析INVITE消息] 无法从请求中获取到来源id，返回400错误");
            throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), "request decode fail");
        }
        log.info("[INVITE] 来源ID: {}, callId: {}, 来自：{}：{}",
                requestId, callIdHeader.getCallId(), request.getRemoteAddress(), request.getRemotePort());
        inviteInfo.setRequestId(requestId);
        inviteInfo.setTargetChannelId(channelId);
        if (channelIdArrayFromSub != null && channelIdArrayFromSub.length == 2) {
            inviteInfo.setSourceChannelId(channelIdArrayFromSub[1]);
        }
        inviteInfo.setSessionName(sessionName);
        inviteInfo.setSsrc(gb28181Sdp.getSsrc());
        inviteInfo.setCallId(callIdHeader.getCallId());

        // 如果是录像回放，则会存在录像的开始时间与结束时间
        Long startTime = null;
        Long stopTime = null;
        if (sdp.getTimeDescriptions(false) != null && !sdp.getTimeDescriptions(false).isEmpty()) {
            TimeDescriptionImpl timeDescription = (TimeDescriptionImpl) (sdp.getTimeDescriptions(false).get(0));
            TimeField startTimeFiled = (TimeField) timeDescription.getTime();
            startTime = startTimeFiled.getStartTime();
            stopTime = startTimeFiled.getStopTime();
        }
        // 获取支持的格式
        Vector<MediaDescription> mediaDescriptions = sdp.getMediaDescriptions(true);
        // 查看是否支持PS 负载96
        int port = -1;
        boolean mediaTransmissionTCP = false;
        Boolean tcpActive = null;
        for (MediaDescription mediaDescription : mediaDescriptions) {
            Media media = mediaDescription.getMedia();

            Vector<String> mediaFormats = media.getMediaFormats(false);
            if (mediaFormats.contains("96") || mediaFormats.contains("8")) {
                port = media.getMediaPort();
                String protocol = media.getProtocol();

                // 区分TCP发流还是udp， 当前默认udp
                if ("TCP/RTP/AVP".equalsIgnoreCase(protocol)) {
                    String setup = mediaDescription.getAttribute("setup");
                    if (setup != null) {
                        mediaTransmissionTCP = true;
                        if ("active".equalsIgnoreCase(setup)) {
                            tcpActive = true;
                        } else if ("passive".equalsIgnoreCase(setup)) {
                            tcpActive = false;
                        }
                    }
                }
                break;
            }
        }
        if (port == -1) {
            log.info("[解析INVITE消息] 不支持的媒体格式，返回415");
            throw new ServiceException(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), "unsupported media type");
        }
        inviteInfo.setTcp(mediaTransmissionTCP);
        inviteInfo.setTcpActive(tcpActive != null ? tcpActive : false);
        inviteInfo.setStartTime(timestampToString(startTime));
        inviteInfo.setStopTime(timestampToString(stopTime));

        // 获取其他相关信息
        String downloadSpeed = "1";
        if (!mediaDescriptions.isEmpty()) {
            MediaDescription mediaDescription = mediaDescriptions.get(0);
            downloadSpeed = mediaDescription.getAttribute("downloadspeed");
        }
        inviteInfo.setOriginIp(sdp.getOrigin().getAddress());
        inviteInfo.setIp(sdp.getConnection().getAddress());
        inviteInfo.setPort(port);
        inviteInfo.setDownloadSpeed(downloadSpeed);
        return inviteInfo;
    }

    SIPResponse sendInviteOk(SIPRequest request, SipDevice device, MediaServer mediaInfo, SendRtpItem sendRtpItem, InviteMsgInfo inviteInfo) throws InvalidArgumentException, ParseException, SipException {
        SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(device.getDeviceSipId());
        if (sipConfig == null) {
            log.error("sendInviteOk sipConfig is null");
            return null;
        }
        log.info("sendInviteOk sipConfig is {}", sipConfig);
        SIPResponse sipResponse = null;
        // 填充sdp
        StringBuilder content = new StringBuilder(200);
        content.append("v=0\r\n");
        content.append("o=").append(sipConfig.getServerSipid()).append(" ").append(inviteInfo.getSessionId()).append(" ").append(inviteInfo.getSessionVersion()).append(" IN IP4 ").append(mediaInfo.getIp()).append("\r\n");
        content.append("s=Play\r\n");
        content.append("c=IN IP4 ").append(mediaInfo.getIp()).append("\r\n");
        content.append("t=0 0\r\n");
        if (inviteInfo.isTcp()) {
            content.append("m=audio ").append(sendRtpItem.getLocalPort()).append(" TCP/RTP/AVP 8\r\n");
        } else {
            content.append("m=audio ").append(sendRtpItem.getLocalPort()).append(" RTP/AVP 8\r\n");
        }
        content.append("a=rtpmap:8 PCMA/8000/1\r\n");
        content.append("a=sendonly\r\n");
        if (sendRtpItem.isTcp()) {
            content.append("a=connection:new\r\n");
            if (!sendRtpItem.isTcpActive()) {
                content.append("a=setup:active\r\n");
            } else {
                content.append("a=setup:passive\r\n");
            }
        }
        content.append("y=").append(inviteInfo.getSsrc()).append("\r\n");
        content.append("f=v/////a/1/8/1\r\n");
        log.info("responseSdpAck content is {}", content);
        // 响应报文
        sipResponse = responseSdpAck(request, content.toString(),sipConfig);
        log.info("responseSdpAck sipResponse is {}", sipResponse);
        // 更新sendRtpItem状态
        sendRtpItem.setStatus(2);
        sendRtpService.update(sendRtpItem);

        // 更新语音广播状态
        BroadcastItem audioBroadcastCatch = audioBroadcastManager.get(sendRtpItem.getChannelId());
        audioBroadcastCatch.setStatus(AudioBroadcastStatus.Ok);
        audioBroadcastCatch.setSipTransactionInfoByRequest(sipResponse);
        audioBroadcastManager.update(audioBroadcastCatch);

        // 创建会话管理
        VideoSessionInfo info = VideoSessionInfo.builder()
                .mediaServerId(mediaInfo.getServerId())
                .deviceId(device.getDeviceSipId())
                .pushing(false)
                .recording(false)
                .channelId(sendRtpItem.getChannelId())
                .streamMode(device.getStreamMode().toUpperCase())
                .type(SessionType.BROADCAST)
                .build();
        InviteInfo invite = InviteInfo.builder()
                .ssrc(info.getSsrc())
                .fromTag(sipResponse.getFromTag())
                .viaTag(sipResponse.getFromTag())
                .callId(request.getCallIdHeader().getCallId())
                .port(info.getPort()).build();
        inviteSessionManager.updateInviteInfo(info,invite);
        // 开启发流，大华在收到200OK后就会开始建立连接
        log.info("startSendRtp isTcpActive {} isBroadcastPushAfterAck {}", sendRtpItem.isTcpActive(), device.isBroadcastPushAfterAck());
        if (sendRtpItem.isTcpActive() || !device.isBroadcastPushAfterAck()) {
            if (sendRtpItem.isTcpActive()) {
                log.info("[语音喊话] 监听端口等待设备连接后推流");
            } else {
                log.info("[语音喊话] 回复200OK后发现 BroadcastPushAfterAck为False，现在开始推流");
            }
            // 开始推流
            log.info("startSendRtp");
            sendRtpService.startSendRtp(mediaInfo, inviteInfo, sendRtpItem);
        }

        return sipResponse;
    }

    public String timestampToString(Long timestamp) {
        if (timestamp == null || timestamp <= 0) {
            return null;
        }

        // 将时间戳转换为本地日期时间
        LocalDateTime localDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(timestamp), // SDP中的时间通常是Unix时间戳（秒）
                ZoneId.systemDefault()
        );

        // 格式化日期时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return localDateTime.format(formatter);
    }

}
