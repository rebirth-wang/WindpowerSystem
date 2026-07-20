package com.fastbee.sip.server.impl;

import java.text.ParseException;

import javax.sip.InvalidArgumentException;
import javax.sip.SipException;
import javax.sip.message.Request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fastbee.sip.domain.SipConfig;
import com.fastbee.sip.domain.SipDevice;
import com.fastbee.sip.server.IRtspCmd;
import com.fastbee.sip.server.ReqMsgHeaderBuilder;
import com.fastbee.sip.server.SIPSender;
import com.fastbee.sip.server.SipLayer;
import com.fastbee.sip.service.ISipConfigService;

/**
 * @author zhuangpengli
 */
@Slf4j
@Component
public class RtspCmdImpl implements IRtspCmd {

    @Autowired
    private ReqMsgHeaderBuilder headerBuilder;

    @Autowired
    private ISipConfigService sipConfigService;

    @Autowired
    private SIPSender sipSender;

    @Autowired
    private SipLayer sipLayer;

    @Override
    public void playPause(SipDevice device, String channelId, String streamId) {
        try {
            SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(device.getDeviceSipId());
            if (sipConfig == null) {
                log.error("[playPause] sipConfig is null");
                return ;
            }
            String content = "PAUSE RTSP/1.0\r\n" +
                    "CSeq: " + getInfoCseq() + "\r\n" +
                    "PauseTime: now\r\n";
            Request request = headerBuilder.createRtspRequest(device, sipConfig, channelId, streamId, content);
            //发送消息
            sipSender.transmitRequest(sipLayer.getLocalIp(device.getIp()),request);

        } catch (SipException | ParseException | InvalidArgumentException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void playReplay(SipDevice device, String channelId, String streamId) {
        try {
            SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(device.getDeviceSipId());
            if (sipConfig == null) {
                log.error("[playReplay] sipConfig is null");
                return ;
            }
            String content = "PLAY RTSP/1.0\r\n" +
                    "CSeq: " + getInfoCseq() + "\r\n" +
                    "Range: npt=now-\r\n";
            Request request = headerBuilder.createRtspRequest(device, sipConfig, channelId, streamId, content);
            //发送消息
            sipSender.transmitRequest(sipLayer.getLocalIp(device.getIp()),request);
        } catch (SipException | ParseException | InvalidArgumentException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void playBackSeek(SipDevice device, String channelId, String streamId, Long seektime) {
        try {
            SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(device.getDeviceSipId());
            if (sipConfig == null) {
                log.error("[playBackSeek] sipConfig is null");
                return ;
            }
            String content = "PLAY RTSP/1.0\r\n" +
                    "CSeq: " + getInfoCseq() + "\r\n" +
                    "Range: npt=" + Math.abs(seektime) + "-\r\n";
            Request request = headerBuilder.createRtspRequest(device, sipConfig, channelId, streamId, content);
            //发送消息
            sipSender.transmitRequest(sipLayer.getLocalIp(device.getIp()),request);
        } catch (SipException | ParseException | InvalidArgumentException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void playBackSpeed(SipDevice device, String channelId, String streamId, Double speed) {
        try {
            SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(device.getDeviceSipId());
            if (sipConfig == null) {
                log.error("[playBackSpeed] sipConfig is null");
                return ;
            }
            String content = "PLAY RTSP/1.0\r\n" +
                    "CSeq: " + getInfoCseq() + "\r\n" +
                    "Scale: " + String.format("%.6f", speed) + "\r\n";
            Request request = headerBuilder.createRtspRequest(device, sipConfig, channelId, streamId, content);
            //发送消息
            sipSender.transmitRequest(sipLayer.getLocalIp(device.getIp()),request);
        } catch (SipException | ParseException | InvalidArgumentException e) {
            e.printStackTrace();
        }

    }

    private int getInfoCseq() {
        return (int) ((Math.random() * 9 + 1) * Math.pow(10, 8));
    }
}
