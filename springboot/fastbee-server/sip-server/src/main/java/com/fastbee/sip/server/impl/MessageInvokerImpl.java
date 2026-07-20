package com.fastbee.sip.server.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.sip.InvalidArgumentException;
import javax.sip.RequestEvent;
import javax.sip.SipException;
import javax.sip.message.Request;

import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.sip.domain.SipConfig;
import com.fastbee.sip.domain.SipDevice;
import com.fastbee.sip.server.*;
import com.fastbee.sip.server.msg.*;
import com.fastbee.sip.service.ISipConfigService;
import com.fastbee.sip.util.SipUtil;

@Slf4j
@Component
public class MessageInvokerImpl implements MessageInvoker {
    @Autowired
    private ISipConfigService sipConfigService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private ReqMsgHeaderBuilder headerBuilder;
    @Autowired
    private SipLayer sipLayer;
    @Autowired
    private SIPSender sipSender;

    public final static XmlMapper mapper = new XmlMapper();

    static Map<String, Function<String, SipMessage>> valueConverter = new ConcurrentHashMap<>();

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT, true);
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        mapper.configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS.mappedFeature(), true);
        valueConverter.put("DeviceInfo", xml -> jsonNodeToObject(xml, GB28181Device.class));
        valueConverter.put("Keepalive", xml -> jsonNodeToObject(xml, KeepaliveMessage.class));
        valueConverter.put("Catalog", xml -> jsonNodeToObject(xml, CatalogInfo.class));
        valueConverter.put("Alarm", xml -> jsonNodeToObject(xml, Alarm.class));
        valueConverter.put("ConfigDownload", xml -> jsonNodeToObject(xml, ConfigDownload.class));
        valueConverter.put("DeviceControl", xml -> jsonNodeToObject(xml, DeviceControl.class));

    }

    @SneakyThrows
    private static <T> T jsonNodeToObject(String xml, Class<T> tClass) {
        return mapper.readValue(xml.replace("\r\n", "").replace("\n", ""), tClass);
    }

    @SneakyThrows
    public SipMessage messageToObj(RequestEvent event) {
        try {
            Request request = event.getRequest();
            if ((!Request.MESSAGE.equals(request.getMethod())
                    && !Request.NOTIFY.equals(request.getMethod())
                    && !Request.SUBSCRIBE.equals(request.getMethod()))
                    || request.getRawContent() == null) {
                return null;
            }
            String content = new String(request.getRawContent(), "GB2312");
            JsonNode jsonNode = mapper.readTree(content);
            String cmdType = jsonNode.get("CmdType").asText();
            Function<String, SipMessage> converter = valueConverter.get(cmdType);
            if (null != converter) {
                return converter.apply(content);
            }
        } catch (Throwable error) {
            log.error("handle SIP message error \n{}", event.getRequest(), error);
        }
        return null;
    }

    @Override
    @SneakyThrows
    public void subscribe(@Nonnull SipDevice device, int startAlarmPriority, int endAlarmPriority, int expires, @Nonnull String alarmMethod, @Nullable Date startAlarmTime, @Nullable Date endAlarmTime) {
        String startAlarmTimeString = startAlarmTime == null ? "" : SipUtil.dateToISO8601(startAlarmTime);
        String endAlarmTimeString = endAlarmTime == null ? "" : SipUtil.dateToISO8601(endAlarmTime);
        SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(device.getDeviceSipId());
        if (sipConfig == null) {
            log.error("deviceInfoQuery deviceSipId:{} not found", device.getDeviceSipId());
            return ;
        }
        StringBuilder cmdXml = new StringBuilder(200);

        cmdXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\r\n");
        cmdXml.append("<Query>\r\n");
        cmdXml.append("<CmdType>Alarm</CmdType>\r\n");
        cmdXml.append("<SN>").append((int) ((Math.random() * 9 + 1) * 100000)).append("</SN>\r\n");
        cmdXml.append("<DeviceID>").append(device.getDeviceSipId()).append("</DeviceID>\r\n");
        if (!ObjectUtils.isEmpty(startAlarmPriority)) {
            cmdXml.append("<StartAlarmPriority>").append(startAlarmPriority).append("</StartAlarmPriority>\r\n");
        }
        if (!ObjectUtils.isEmpty(endAlarmPriority)) {
            cmdXml.append("<EndAlarmPriority>").append(endAlarmPriority).append("</EndAlarmPriority>\r\n");
        }
        if (!ObjectUtils.isEmpty(alarmMethod)) {
            cmdXml.append("<AlarmMethod>").append(alarmMethod).append("</AlarmMethod>\r\n");
        }
        if (!ObjectUtils.isEmpty(startAlarmTimeString)) {
            cmdXml.append("<StartAlarmTime>").append(startAlarmTimeString).append("</StartAlarmTime>\r\n");
        }
        if (!ObjectUtils.isEmpty(endAlarmTimeString)) {
            cmdXml.append("<EndAlarmTime>").append(endAlarmTimeString).append("</EndAlarmTime>\r\n");
        }
        cmdXml.append("</Query>\r\n");
        try {
            Request request = headerBuilder.createSubscribeRequest(device, sipConfig, cmdXml.toString(), null, expires,"presemce",sipSender.getNewCallIdHeader(sipLayer.getLocalIp(device.getIp()),device.getTransport()));
            sipSender.transmitRequest(sipLayer.getLocalIp(device.getIp()), request);
        } catch (SipException | ParseException | InvalidArgumentException e) {
            log.error("deviceInfoQuery error", e);
        }
    }

    @Override
    public Boolean deviceControl(SipDevice device, DeviceControl command) {
        SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(device.getDeviceSipId());
        if (sipConfig == null) {
            log.error("deviceInfoQuery deviceSipId:{} not found", device.getDeviceSipId());
            return false;
        }
        int sn = (int) ((Math.random() * 9 + 1) * 100000);
        try {
            Request request = headerBuilder.createMessageRequest(device, sipConfig, command.toXml(sn, "GB2312"), SipUtil.getNewFromTag(), sipSender.getNewCallIdHeader(sipLayer.getLocalIp(device.getIp()),device.getTransport()));
            sipSender.transmitRequest(sipLayer.getLocalIp(device.getIp()), request);
            return true;
        } catch (SipException | ParseException | InvalidArgumentException e) {
            log.error("deviceInfoQuery error", e);
        }
        return false;
    }

    public String frontEndCmdString(int cmdCode, int parameter1, int parameter2, int combineCode2) {
        StringBuilder builder = new StringBuilder("A50F01");
        String strTmp;
        strTmp = String.format("%02X", cmdCode);
        builder.append(strTmp, 0, 2);
        strTmp = String.format("%02X", parameter1);
        builder.append(strTmp, 0, 2);
        strTmp = String.format("%02X", parameter2);
        builder.append(strTmp, 0, 2);
        strTmp = String.format("%02X", combineCode2 << 4);
        builder.append(strTmp, 0, 2);
        //计算校验码
        int checkCode = (0XA5 + 0X0F + 0X01 + cmdCode + parameter1 + parameter2 + (combineCode2 << 4)) % 0X100;
        strTmp = String.format("%02X", checkCode);
        builder.append(strTmp, 0, 2);
        return builder.toString();
    }

    /**
     * 云台控制，支持方向与缩放控制
     *
     * @param device    控制设备
     * @param channelId 预览通道
     * @param leftRight 镜头左移右移 0:停止 1:左移 2:右移
     * @param upDown    镜头上移下移 0:停止 1:上移 2:下移
     * @param inOut     镜头放大缩小 0:停止 1:缩小 2:放大
     * @param moveSpeed 镜头移动速度
     * @param zoomSpeed 镜头缩放速度
     */
    @Override
    public void ptzCmd(SipDevice device, String channelId, int leftRight, int upDown, int inOut, int moveSpeed,
                       int zoomSpeed)  {
        SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(device.getDeviceSipId());
        if (sipConfig == null) {
            log.error("deviceInfoQuery deviceSipId:{} not found", device.getDeviceSipId());
            return;
        }
        String cmdStr = SipUtil.cmdString(leftRight, upDown, inOut, moveSpeed, zoomSpeed);
        StringBuilder ptzXml = new StringBuilder(200);
        ptzXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\r\n");
        ptzXml.append("<Control>\r\n");
        ptzXml.append("<CmdType>DeviceControl</CmdType>\r\n");
        ptzXml.append("<SN>" + (int) ((Math.random() * 9 + 1) * 100000) + "</SN>\r\n");
        ptzXml.append("<DeviceID>" + channelId + "</DeviceID>\r\n");
        ptzXml.append("<PTZCmd>" + cmdStr + "</PTZCmd>\r\n");
        ptzXml.append("<Info>\r\n");
        ptzXml.append("<ControlPriority>5</ControlPriority>\r\n");
        ptzXml.append("</Info>\r\n");
        ptzXml.append("</Control>\r\n");

        try {
            Request request = headerBuilder.createMessageRequest(device, sipConfig, ptzXml.toString(), SipUtil.getNewFromTag(), sipSender.getNewCallIdHeader(sipLayer.getLocalIp(device.getIp()),device.getTransport()));
            sipSender.transmitRequest(sipLayer.getLocalIp(device.getIp()), request);
        } catch (SipException | ParseException | InvalidArgumentException e) {
            log.error("deviceInfoQuery error", e);
        }
    }

    /**
     * 前端控制，包括PTZ指令、FI指令、预置位指令、巡航指令、扫描指令和辅助开关指令
     *
     * @param device       控制设备
     * @param channelId    预览通道
     * @param cmdCode      指令码
     * @param parameter1   数据1
     * @param parameter2   数据2
     * @param combineCode2 组合码2
     */
    @Override
    public void frontEndCmd(SipDevice device, String channelId, int cmdCode, int parameter1, int parameter2, int combineCode2) {
        SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(device.getDeviceSipId());
        if (sipConfig == null) {
            log.error("deviceInfoQuery deviceSipId:{} not found", device.getDeviceSipId());
            return;
        }
        String cmdStr = frontEndCmdString(cmdCode, parameter1, parameter2, combineCode2);
        StringBuffer ptzXml = new StringBuffer(200);
        ptzXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\r\n");
        ptzXml.append("<Control>\r\n");
        ptzXml.append("<CmdType>DeviceControl</CmdType>\r\n");
        ptzXml.append("<SN>" + (int) ((Math.random() * 9 + 1) * 100000) + "</SN>\r\n");
        ptzXml.append("<DeviceID>" + channelId + "</DeviceID>\r\n");
        ptzXml.append("<PTZCmd>" + cmdStr + "</PTZCmd>\r\n");
        ptzXml.append("<Info>\r\n");
        ptzXml.append("<ControlPriority>5</ControlPriority>\r\n");
        ptzXml.append("</Info>\r\n");
        ptzXml.append("</Control>\r\n");

        try {
            Request request = headerBuilder.createMessageRequest(device, sipConfig, ptzXml.toString(), SipUtil.getNewFromTag(), sipSender.getNewCallIdHeader(sipLayer.getLocalIp(device.getIp()),device.getTransport()));
            sipSender.transmitRequest(sipLayer.getLocalIp(device.getIp()), request);
        } catch (SipException | ParseException | InvalidArgumentException e) {
            log.error("deviceInfoQuery error", e);
        }

    }

    @Override
    public void presetQuery(SipDevice device, String channelId) {
        SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(device.getDeviceSipId());
        if (sipConfig == null) {
            log.error("deviceInfoQuery deviceSipId:{} not found", device.getDeviceSipId());
            return ;
        }
        String cmdType = "PresetQuery";
        int sn = (int) ((Math.random() * 9 + 1) * 100000);

        StringBuffer cmdXml = new StringBuffer(200);
        cmdXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\r\n");
        cmdXml.append("<Query>\r\n");
        cmdXml.append("<CmdType>" + cmdType + "</CmdType>\r\n");
        cmdXml.append("<SN>" + sn + "</SN>\r\n");
        if (ObjectUtils.isEmpty(channelId)) {
            cmdXml.append("<DeviceID>" + device.getDeviceId() + "</DeviceID>\r\n");
        } else {
            cmdXml.append("<DeviceID>" + channelId + "</DeviceID>\r\n");
        }
        cmdXml.append("</Query>\r\n");
        try {
            Request request = headerBuilder.createMessageRequest(device, sipConfig, cmdXml.toString(), SipUtil.getNewFromTag(), sipSender.getNewCallIdHeader(sipLayer.getLocalIp(device.getIp()),device.getTransport()));
            sipSender.transmitRequest(sipLayer.getLocalIp(device.getIp()), request);
        } catch (SipException | ParseException | InvalidArgumentException e) {
            log.error("deviceInfoQuery error", e);
        }

    }


    @Override
    public void deviceInfoQuery(SipDevice device) {
        SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(device.getDeviceSipId());
        if (sipConfig == null) {
            log.error("deviceInfoQuery deviceSipId:{} not found", device.getDeviceSipId());
            return ;
        }
        StringBuilder catalogXml = new StringBuilder(200);
        catalogXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\r\n");
        catalogXml.append("<Query>\r\n");
        catalogXml.append("<CmdType>DeviceInfo</CmdType>\r\n");
        catalogXml.append("<SN>").append((int) ((Math.random() * 9 + 1) * 100000)).append("</SN>\r\n");
        catalogXml.append("<DeviceID>").append(device.getDeviceSipId()).append("</DeviceID>\r\n");
        catalogXml.append("</Query>\r\n");
        try {
            Request request = headerBuilder.createMessageRequest(device, sipConfig, catalogXml.toString(), SipUtil.getNewFromTag(), sipSender.getNewCallIdHeader(sipLayer.getLocalIp(device.getIp()),device.getTransport()));
            sipSender.transmitRequest(sipLayer.getLocalIp(device.getIp()), request);
        } catch (SipException | ParseException | InvalidArgumentException e) {
            log.error("deviceInfoQuery error", e);
        }
    }

    @Override
    public void catalogQuery(SipDevice device) {
        SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(device.getDeviceSipId());
        if (sipConfig == null) {
            log.error("deviceInfoQuery deviceSipId:{} not found", device.getDeviceSipId());
            return ;
        }
        StringBuilder catalogXml = new StringBuilder(200);
        catalogXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\r\n");
        catalogXml.append("<Query>\r\n");
        catalogXml.append("<CmdType>Catalog</CmdType>\r\n");
        catalogXml.append("<SN>").append((int) ((Math.random() * 9 + 1) * 100000)).append("</SN>\r\n");
        catalogXml.append("<DeviceID>").append(device.getDeviceSipId()).append("</DeviceID>\r\n");
        catalogXml.append("</Query>\r\n");
        try {
            Request request = headerBuilder.createMessageRequest(device, sipConfig, catalogXml.toString(), SipUtil.getNewFromTag(), sipSender.getNewCallIdHeader(sipLayer.getLocalIp(device.getIp()),device.getTransport()));
            sipSender.transmitRequest(sipLayer.getLocalIp(device.getIp()), request);
        } catch (SipException | ParseException | InvalidArgumentException e) {
            log.error("deviceInfoQuery error", e);
        }
    }

    @Override
    public void recordInfoQuery(SipDevice device, String sn, String channelId, Date start, Date end) {
        String startTimeString = SipUtil.dateToISO8601(start);
        String endTimeString = SipUtil.dateToISO8601(end);
        SipConfig sipConfig = sipConfigService.selectSipConfigBydeviceSipId(device.getDeviceSipId());
        if (sipConfig == null) {
            log.error("deviceInfoQuery deviceSipId:{} not found", device.getDeviceSipId());
            return ;
        }
        StringBuilder catalogXml = new StringBuilder(200);
        catalogXml.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>\r\n");
        catalogXml.append("<Query>\r\n");
        catalogXml.append("<CmdType>RecordInfo</CmdType>\r\n");
        catalogXml.append("<SN>").append(sn).append("</SN>\r\n");
        catalogXml.append("<DeviceID>").append(channelId).append("</DeviceID>\r\n");
        catalogXml.append("<StartTime>").append(startTimeString).append("</StartTime>\r\n");
        catalogXml.append("<EndTime>").append(endTimeString).append("</EndTime>\r\n");
        catalogXml.append("<Secrecy>0</Secrecy>\r\n");
        catalogXml.append("<Type>all</Type>\r\n");
        catalogXml.append("</Query>\r\n");
        try {
            Request request = headerBuilder.createMessageRequest(device, sipConfig, catalogXml.toString(), SipUtil.getNewFromTag(), sipSender.getNewCallIdHeader(sipLayer.getLocalIp(device.getIp()),device.getTransport()));
            sipSender.transmitRequest(sipLayer.getLocalIp(device.getIp()), request);
        } catch (SipException | ParseException | InvalidArgumentException e) {
            log.error("deviceInfoQuery error", e);
        }
    }

    public <T> T getExecResult(String key, long timeout) {
        long time = 0;
        while (true) {
            try {
                T instance = redisCache.getCacheObject(key);
                if (null == instance) {
                    if (time >= timeout) {
                        log.error("key:{} get Response timeout", key);
                        return null;
                    }
                    time += 1000;
                    TimeUnit.MILLISECONDS.sleep(1000L);
                } else {
                    return instance;
                }
            } catch (Exception e) {
                log.error("", e);
                Thread.currentThread().interrupt();
                break;
            }
        }
        log.error("key:{} can't get Response", key);
        return null;
    }
}

