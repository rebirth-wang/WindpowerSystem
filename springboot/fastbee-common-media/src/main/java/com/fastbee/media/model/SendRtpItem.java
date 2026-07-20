package com.fastbee.media.model;

import lombok.Data;

import com.fastbee.media.domain.MediaServer;
import com.fastbee.media.enums.ChannelStreamType;
import com.fastbee.media.enums.InviteType;

@Data
public class SendRtpItem {
    private String ip;
    private int port;
    private String ssrc;
    /**
     * 目标平台或设备的编号
     */
    private String targetId;

    /**
     * 目标平台或设备的名称
     */
    private String targetName;

    /**
     * 是否是发送给上级平台
     */
    private boolean sendToPlatform;
    private String platformId;
    private String deviceId;
    private String app;
    private String channelId;
    /**
     * 推流状态
     * 0 等待设备推流上来
     * 1 等待上级平台回复ack
     * 2 推流中
     */
    private int status = 0;
    private String stream;
    private boolean tcp;
    private boolean tcpActive;
    private String localIp;
    private int localPort;
    private String mediaServerId;
    private Long serverId;
    private String callId;
    private String fromTag;
    private String viaTag;
    private String toTag;
    private int pt = 96;
    private boolean usePs = true;
    private boolean onlyAudio = false;
    private boolean rtcp = false;
    private InviteType playType;
    private Integer timeout;
    /**
     * 发流的同时收流
     */
    private String receiveStream;

    /**
     * 上级的点播类型
     */
    private String sessionName;


    public static SendRtpItem getInstance(String app, String stream, String ssrc, String dstIp, Integer dstPort, boolean tcp, int sendLocalPort, Integer pt) {
        SendRtpItem sendRtpItem = new SendRtpItem();
        sendRtpItem.setApp(app);
        sendRtpItem.setStream(stream);
        sendRtpItem.setSsrc(ssrc);
        sendRtpItem.setTcp(tcp);
        sendRtpItem.setLocalPort(sendLocalPort);
        sendRtpItem.setIp(dstIp);
        sendRtpItem.setPort(dstPort);
        if (pt != null) {
            sendRtpItem.setPt(pt);
        }

        return sendRtpItem;
    }

    public static SendRtpItem getInstance(Integer localPort, MediaServer mediaServer, String ip, Integer port, String ssrc,
                                          String deviceId, String platformId, String channelId, Boolean isTcp, Boolean rtcp,
                                          String serverId) {
        if (localPort == 0) {
            return null;
        }
        SendRtpItem sendRtpItem = new SendRtpItem();
        sendRtpItem.setIp(ip);
        if(port != null) {
            sendRtpItem.setPort(port);
        }

        sendRtpItem.setSsrc(ssrc);
        if (deviceId != null) {
            sendRtpItem.setTargetId(deviceId);
            sendRtpItem.setSendToPlatform(false);
        }else {
            sendRtpItem.setTargetId(platformId);
            sendRtpItem.setSendToPlatform(true);
        }
        sendRtpItem.setChannelId(channelId);
        sendRtpItem.setTcp(isTcp);
        sendRtpItem.setRtcp(rtcp);
        sendRtpItem.setApp("rtp");
        sendRtpItem.setLocalPort(localPort);
        sendRtpItem.setMediaServerId(serverId);
        sendRtpItem.setServerId(mediaServer.getId());
        return sendRtpItem;
    }

    @Override
    public String toString() {
        return "SendRtpItem{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", ssrc='" + ssrc + '\'' +
                ", targetId='" + targetId + '\'' +
                ", app='" + app + '\'' +
                ", channelId='" + channelId + '\'' +
                ", status=" + status +
                ", stream='" + stream + '\'' +
                ", tcp=" + tcp +
                ", tcpActive=" + tcpActive +
                ", localIp='" + localIp + '\'' +
                ", localPort=" + localPort +
                ", mediaServerId='" + mediaServerId + '\'' +
                ", serverId='" + serverId + '\'' +
                ", CallId='" + callId + '\'' +
                ", fromTag='" + fromTag + '\'' +
                ", toTag='" + toTag + '\'' +
                ", pt=" + pt +
                ", usePs=" + usePs +
                ", onlyAudio=" + onlyAudio +
                ", rtcp=" + rtcp +
                ", playType=" + playType +
                ", receiveStream='" + receiveStream + '\'' +
                ", sessionName='" + sessionName + '\'' +
                '}';
    }


    public void setPlayTypeByChannelDataType(Integer dataType, String sessionName) {
        if (dataType == ChannelStreamType.STREAM_PUSH) {
            this.setPlayType(InviteType.PUSH);
        }else if (dataType == ChannelStreamType.STREAM_PROXY){
            this.setPlayType(InviteType.PROXY);
        }else {
            this.setPlayType("Play".equalsIgnoreCase(sessionName) ? InviteType.PLAY : InviteType.PLAYBACK);
        }
    }
}
