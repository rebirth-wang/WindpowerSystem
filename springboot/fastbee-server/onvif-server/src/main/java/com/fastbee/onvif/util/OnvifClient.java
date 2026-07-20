package com.fastbee.onvif.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.fastbee.common.exception.ServiceException;
import com.fastbee.media.enums.ErrorCode;
import com.fastbee.onvif.domain.OnvifDeviceChannel;

/**
 * ONVIF SOAP 协议客户端
 * 封装 WS-Security UsernameToken 认证、SOAP 信封构建、HTTP 调用、响应解析
 *
 * <p>ONVIF 标准参考：
 * <ul>
 *   <li>ONVIF Core Specification 22.06</li>
 *   <li>ONVIF Media Service WSDL</li>
 *   <li>ONVIF PTZ Service WSDL</li>
 *   <li>ONVIF Replay Control WSDL</li>
 * </ul>
 *
 * @author fastbee
 */
@Slf4j
@Component
public class OnvifClient {

    /** HTTP 连接超时（毫秒） */
    private static final int CONNECT_TIMEOUT = 5000;
    /** HTTP 读取超时（毫秒） */
    private static final int SOCKET_TIMEOUT = 10000;
    /** 最大重试次数 */
    private static final int MAX_RETRY = 3;

    /** ONVIF 默认 Device Service 路径 */
    private static final String DEVICE_SERVICE_PATH = "/onvif/device_service";

    // -------------------------------------------------------------------------
    // 公共 API
    // -------------------------------------------------------------------------

    /**
     * 获取设备信息（GetDeviceInformation）
     * ONVIF Device Service - GetDeviceInformation
     *
     * @param channel 通道（含 ip/port/username/password）
     * @return 设备信息 Map（Manufacturer/Model/FirmwareVersion/SerialNumber/HardwareId）
     */
    public Map<String, String> getDeviceInformation(OnvifDeviceChannel channel) {
        String serviceUrl = buildServiceUrl(channel, DEVICE_SERVICE_PATH);
        String body = buildSoapEnvelope(channel,
                "<tds:GetDeviceInformation xmlns:tds=\"http://www.onvif.org/ver10/device/wsdl\"/>");
        Document doc = sendSoapRequest(serviceUrl, body, channel);
        Map<String, String> info = new LinkedHashMap<>();
        info.put("manufacturer", extractText(doc, "Manufacturer"));
        info.put("model", extractText(doc, "Model"));
        info.put("firmwareVersion", extractText(doc, "FirmwareVersion"));
        info.put("serialNumber", extractText(doc, "SerialNumber"));
        info.put("hardwareId", extractText(doc, "HardwareId"));
        return info;
    }

    /**
     * 获取设备能力（GetCapabilities）
     * ONVIF Device Service - GetCapabilities
     * 返回各 Service 的 XAddr（URL），包括 Media/PTZ/Imaging 等
     *
     * @param channel 通道
     * @return 能力 Map（mediaServiceUrl / ptzServiceUrl / imagingServiceUrl）
     */
    public Map<String, String> getCapabilities(OnvifDeviceChannel channel) {
        String serviceUrl = buildServiceUrl(channel, DEVICE_SERVICE_PATH);
        String body = buildSoapEnvelope(channel,
                "<tds:GetCapabilities xmlns:tds=\"http://www.onvif.org/ver10/device/wsdl\">" +
                "<tds:Category>All</tds:Category></tds:GetCapabilities>");
        Document doc = sendSoapRequest(serviceUrl, body, channel);
        Map<String, String> caps = new LinkedHashMap<>();
        // Media Service
        NodeList mediaXAddr = doc.getElementsByTagNameNS("*", "XAddr");
        // 解析 Media/PTZ/Imaging XAddr
        NodeList mediaNodes = doc.getElementsByTagNameNS("http://www.onvif.org/ver10/schema", "Media");
        if (mediaNodes.getLength() > 0) {
            Element mediaElem = (Element) mediaNodes.item(0);
            caps.put("mediaServiceUrl", getChildText(mediaElem, "XAddr"));
        }
        NodeList ptzNodes = doc.getElementsByTagNameNS("http://www.onvif.org/ver10/schema", "PTZ");
        if (ptzNodes.getLength() > 0) {
            Element ptzElem = (Element) ptzNodes.item(0);
            caps.put("ptzServiceUrl", getChildText(ptzElem, "XAddr"));
        }
        NodeList imagingNodes = doc.getElementsByTagNameNS("http://www.onvif.org/ver10/schema", "Imaging");
        if (imagingNodes.getLength() > 0) {
            Element imgElem = (Element) imagingNodes.item(0);
            caps.put("imagingServiceUrl", getChildText(imgElem, "XAddr"));
        }
        // 若 XAddr 为相对路径，则补全 host
        caps.replaceAll((k, v) -> normalizeServiceUrl(channel, v));
        return caps;
    }

    /**
     * 获取媒体配置文件列表（GetProfiles）
     * ONVIF Media Service - GetProfiles
     *
     * @param channel 通道（需含 mediaServiceUrl）
     * @return Profile Token 列表（取第一个作为默认 token）
     */
    public List<String> getProfiles(OnvifDeviceChannel channel) {
        String serviceUrl = resolveMediaServiceUrl(channel);
        String body = buildSoapEnvelope(channel,
                "<trt:GetProfiles xmlns:trt=\"http://www.onvif.org/ver10/media/wsdl\"/>");
        Document doc = sendSoapRequest(serviceUrl, body, channel);
        List<String> tokens = new ArrayList<>();
        NodeList profiles = doc.getElementsByTagNameNS("*", "Profiles");
        for (int i = 0; i < profiles.getLength(); i++) {
            Element profile = (Element) profiles.item(i);
            String token = profile.getAttribute("token");
            if (token != null && !token.isEmpty()) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    /**
     * 获取实时流地址（GetStreamUri）
     * ONVIF Media Service - GetStreamUri
     * 遵循 ONVIF Media Service Specification Section 5.3
     *
     * @param channel      通道（需含 profileToken / mediaServiceUrl）
     * @param transportType 传输类型：RtspUnicast / RtspMulticast（默认 RtspUnicast）
     * @return RTSP URI（未含认证信息，认证信息需在调用方注入）
     */
    public String getStreamUri(OnvifDeviceChannel channel, String transportType) {
        String serviceUrl = resolveMediaServiceUrl(channel);
        String profileToken = resolveProfileToken(channel);
        String transport = (transportType != null) ? transportType : "RtspUnicast";
        String bodyXml =
                "<trt:GetStreamUri xmlns:trt=\"http://www.onvif.org/ver10/media/wsdl\">" +
                "<trt:StreamSetup>" +
                "<tt:Stream xmlns:tt=\"http://www.onvif.org/ver10/schema\">RTP-Unicast</tt:Stream>" +
                "<tt:Transport xmlns:tt=\"http://www.onvif.org/ver10/schema\">" +
                "<tt:Protocol>" + transport.replace("Rtsp", "RTSP").replace("Unicast", "").replace("Multicast", "") + "</tt:Protocol>" +
                "</tt:Transport>" +
                "</trt:StreamSetup>" +
                "<trt:ProfileToken>" + escapeXml(profileToken) + "</trt:ProfileToken>" +
                "</trt:GetStreamUri>";
        Document doc = sendSoapRequest(serviceUrl, bodyXml, channel);
        return extractText(doc, "Uri");
    }

    /**
     * 获取快照 URI（GetSnapshotUri）
     * ONVIF Media Service - GetSnapshotUri
     *
     * @param channel 通道
     * @return 快照 HTTP URI
     */
    public String getSnapshotUri(OnvifDeviceChannel channel) {
        String serviceUrl = resolveMediaServiceUrl(channel);
        String profileToken = resolveProfileToken(channel);
        String bodyXml =
                "<trt:GetSnapshotUri xmlns:trt=\"http://www.onvif.org/ver10/media/wsdl\">" +
                "<trt:ProfileToken>" + escapeXml(profileToken) + "</trt:ProfileToken>" +
                "</trt:GetSnapshotUri>";
        Document doc = sendSoapRequest(serviceUrl, bodyXml, channel);
        return extractText(doc, "Uri");
    }

    /**
     * 获取回放流地址（GetReplayUri）
     * ONVIF Replay Control Service - GetReplayUri
     * 遵循 ONVIF Replay Control Service Specification
     *
     * @param channel     通道
     * @param startTime   开始时间（ISO 8601，如 2024-01-01T00:00:00Z）
     * @param endTime     结束时间（ISO 8601）
     * @return 回放 RTSP URI
     */
    public String getReplayUri(OnvifDeviceChannel channel, String startTime, String endTime) {
        String replayServiceUrl = buildServiceUrl(channel, "/onvif/replay_service");
        String profileToken = resolveProfileToken(channel);
        String bodyXml =
                "<trp:GetReplayUri xmlns:trp=\"http://www.onvif.org/ver10/replay/wsdl\">" +
                "<trp:StreamSetup>" +
                "<tt:Stream xmlns:tt=\"http://www.onvif.org/ver10/schema\">RTP-Unicast</tt:Stream>" +
                "<tt:Transport xmlns:tt=\"http://www.onvif.org/ver10/schema\">" +
                "<tt:Protocol>RTSP</tt:Protocol>" +
                "</tt:Transport>" +
                "</trp:StreamSetup>" +
                "<trp:RecordingToken>" + escapeXml(profileToken) + "</trp:RecordingToken>" +
                "</trp:GetReplayUri>";
        Document doc = sendSoapRequest(replayServiceUrl, bodyXml, channel);
        return extractText(doc, "Uri");
    }

    /**
     * 查询录像摘要（GetRecordingSummary）
     * ONVIF Recording Service - GetRecordingSummary
     *
     * @param channel 通道
     * @return 录像信息 Map（DataFrom/DataUntil/NumberRecordings）
     */
    public Map<String, String> getRecordingSummary(OnvifDeviceChannel channel) {
        String recordingServiceUrl = buildServiceUrl(channel, "/onvif/recording_service");
        String body = buildSoapEnvelope(channel,
                "<trc:GetRecordingSummary xmlns:trc=\"http://www.onvif.org/ver10/recording/wsdl\"/>");
        Document doc = sendSoapRequest(recordingServiceUrl, body, channel);
        Map<String, String> summary = new LinkedHashMap<>();
        summary.put("dataFrom", extractText(doc, "DataFrom"));
        summary.put("dataUntil", extractText(doc, "DataUntil"));
        summary.put("numberRecordings", extractText(doc, "NumberRecordings"));
        return summary;
    }

    /**
     * PTZ 连续移动（ContinuousMove）
     * ONVIF PTZ Service - ContinuousMove
     * 速度范围：ONVIF 标准 -1.0 ~ 1.0
     *
     * @param channel 通道
     * @param panX    水平速度（-1.0 ~ 1.0）
     * @param tiltY   垂直速度（-1.0 ~ 1.0）
     * @param zoom    缩放速度（-1.0 ~ 1.0）
     */
    public void continuousMove(OnvifDeviceChannel channel, double panX, double tiltY, double zoom) {
        String ptzServiceUrl = resolvePtzServiceUrl(channel);
        String profileToken = resolveProfileToken(channel);
        String bodyXml =
                "<tptz:ContinuousMove xmlns:tptz=\"http://www.onvif.org/ver20/ptz/wsdl\">" +
                "<tptz:ProfileToken>" + escapeXml(profileToken) + "</tptz:ProfileToken>" +
                "<tptz:Velocity>" +
                "<tt:PanTilt xmlns:tt=\"http://www.onvif.org/ver10/schema\" x=\"" + panX + "\" y=\"" + tiltY + "\"/>" +
                "<tt:Zoom xmlns:tt=\"http://www.onvif.org/ver10/schema\" x=\"" + zoom + "\"/>" +
                "</tptz:Velocity>" +
                "</tptz:ContinuousMove>";
        sendSoapRequest(ptzServiceUrl, bodyXml, channel);
    }

    /**
     * PTZ 停止（Stop）
     * ONVIF PTZ Service - Stop
     *
     * @param channel    通道
     * @param panTilt    是否停止云台移动
     * @param zoom       是否停止变焦
     */
    public void ptzStop(OnvifDeviceChannel channel, boolean panTilt, boolean zoom) {
        String ptzServiceUrl = resolvePtzServiceUrl(channel);
        String profileToken = resolveProfileToken(channel);
        String bodyXml =
                "<tptz:Stop xmlns:tptz=\"http://www.onvif.org/ver20/ptz/wsdl\">" +
                "<tptz:ProfileToken>" + escapeXml(profileToken) + "</tptz:ProfileToken>" +
                "<tptz:PanTilt>" + panTilt + "</tptz:PanTilt>" +
                "<tptz:Zoom>" + zoom + "</tptz:Zoom>" +
                "</tptz:Stop>";
        sendSoapRequest(ptzServiceUrl, bodyXml, channel);
    }

    /**
     * PTZ 绝对移动（AbsoluteMove）
     * ONVIF PTZ Service - AbsoluteMove
     *
     * @param channel 通道
     * @param panX    目标水平位置（-1.0 ~ 1.0）
     * @param tiltY   目标垂直位置（-1.0 ~ 1.0）
     * @param zoom    目标变焦位置（0.0 ~ 1.0）
     */
    public void absoluteMove(OnvifDeviceChannel channel, double panX, double tiltY, double zoom) {
        String ptzServiceUrl = resolvePtzServiceUrl(channel);
        String profileToken = resolveProfileToken(channel);
        String bodyXml =
                "<tptz:AbsoluteMove xmlns:tptz=\"http://www.onvif.org/ver20/ptz/wsdl\">" +
                "<tptz:ProfileToken>" + escapeXml(profileToken) + "</tptz:ProfileToken>" +
                "<tptz:Position>" +
                "<tt:PanTilt xmlns:tt=\"http://www.onvif.org/ver10/schema\" x=\"" + panX + "\" y=\"" + tiltY + "\"/>" +
                "<tt:Zoom xmlns:tt=\"http://www.onvif.org/ver10/schema\" x=\"" + zoom + "\"/>" +
                "</tptz:Position>" +
                "</tptz:AbsoluteMove>";
        sendSoapRequest(ptzServiceUrl, bodyXml, channel);
    }

    /**
     * 跳转预置位（GotoPreset）
     * ONVIF PTZ Service - GotoPreset
     *
     * @param channel    通道
     * @param presetToken 预置位 Token
     */
    public void gotoPreset(OnvifDeviceChannel channel, String presetToken) {
        String ptzServiceUrl = resolvePtzServiceUrl(channel);
        String profileToken = resolveProfileToken(channel);
        String bodyXml =
                "<tptz:GotoPreset xmlns:tptz=\"http://www.onvif.org/ver20/ptz/wsdl\">" +
                "<tptz:ProfileToken>" + escapeXml(profileToken) + "</tptz:ProfileToken>" +
                "<tptz:PresetToken>" + escapeXml(presetToken) + "</tptz:PresetToken>" +
                "</tptz:GotoPreset>";
        sendSoapRequest(ptzServiceUrl, bodyXml, channel);
    }

    /**
     * 设置预置位（SetPreset）
     * ONVIF PTZ Service - SetPreset
     *
     * @param channel     通道
     * @param presetName  预置位名称
     * @return 预置位 Token
     */
    public String setPreset(OnvifDeviceChannel channel, String presetName) {
        String ptzServiceUrl = resolvePtzServiceUrl(channel);
        String profileToken = resolveProfileToken(channel);
        String bodyXml =
                "<tptz:SetPreset xmlns:tptz=\"http://www.onvif.org/ver20/ptz/wsdl\">" +
                "<tptz:ProfileToken>" + escapeXml(profileToken) + "</tptz:ProfileToken>" +
                "<tptz:PresetName>" + escapeXml(presetName) + "</tptz:PresetName>" +
                "</tptz:SetPreset>";
        Document doc = sendSoapRequest(ptzServiceUrl, bodyXml, channel);
        return extractText(doc, "PresetToken");
    }

    /**
     * 删除预置位（RemovePreset）
     * ONVIF PTZ Service - RemovePreset
     *
     * @param channel     通道
     * @param presetToken 预置位 Token
     */
    public void removePreset(OnvifDeviceChannel channel, String presetToken) {
        String ptzServiceUrl = resolvePtzServiceUrl(channel);
        String profileToken = resolveProfileToken(channel);
        String bodyXml =
                "<tptz:RemovePreset xmlns:tptz=\"http://www.onvif.org/ver20/ptz/wsdl\">" +
                "<tptz:ProfileToken>" + escapeXml(profileToken) + "</tptz:ProfileToken>" +
                "<tptz:PresetToken>" + escapeXml(presetToken) + "</tptz:PresetToken>" +
                "</tptz:RemovePreset>";
        sendSoapRequest(ptzServiceUrl, bodyXml, channel);
    }

    /**
     * 获取预置位列表（GetPresets）
     * ONVIF PTZ Service - GetPresets
     *
     * @param channel 通道
     * @return 预置位列表 [{token, name}]
     */
    public List<Map<String, String>> getPresets(OnvifDeviceChannel channel) {
        String ptzServiceUrl = resolvePtzServiceUrl(channel);
        String profileToken = resolveProfileToken(channel);
        String bodyXml =
                "<tptz:GetPresets xmlns:tptz=\"http://www.onvif.org/ver20/ptz/wsdl\">" +
                "<tptz:ProfileToken>" + escapeXml(profileToken) + "</tptz:ProfileToken>" +
                "</tptz:GetPresets>";
        Document doc = sendSoapRequest(ptzServiceUrl, bodyXml, channel);
        List<Map<String, String>> presets = new ArrayList<>();
        NodeList presetNodes = doc.getElementsByTagNameNS("*", "Preset");
        for (int i = 0; i < presetNodes.getLength(); i++) {
            Element preset = (Element) presetNodes.item(i);
            Map<String, String> p = new LinkedHashMap<>();
            p.put("token", preset.getAttribute("token"));
            p.put("name", getChildText(preset, "Name"));
            presets.add(p);
        }
        return presets;
    }

    /**
     * 跳转到 Home 位置（GotoHomePosition）
     * ONVIF PTZ Service - GotoHomePosition
     *
     * @param channel 通道
     */
    public void gotoHomePosition(OnvifDeviceChannel channel) {
        String ptzServiceUrl = resolvePtzServiceUrl(channel);
        String profileToken = resolveProfileToken(channel);
        String bodyXml =
                "<tptz:GotoHomePosition xmlns:tptz=\"http://www.onvif.org/ver20/ptz/wsdl\">" +
                "<tptz:ProfileToken>" + escapeXml(profileToken) + "</tptz:ProfileToken>" +
                "</tptz:GotoHomePosition>";
        sendSoapRequest(ptzServiceUrl, bodyXml, channel);
    }

    /**
     * 发送辅助命令（SendAuxiliaryCommand）
     * ONVIF PTZ Service - SendAuxiliaryCommand
     * 用于雨刷(tt:Wiper|On)等辅助功能
     *
     * @param channel   通道
     * @param command   命令字符串（如 "tt:Wiper|On" / "tt:Wiper|Off"）
     */
    public void sendAuxiliaryCommand(OnvifDeviceChannel channel, String command) {
        String ptzServiceUrl = resolvePtzServiceUrl(channel);
        String profileToken = resolveProfileToken(channel);
        String bodyXml =
                "<tptz:SendAuxiliaryCommand xmlns:tptz=\"http://www.onvif.org/ver20/ptz/wsdl\">" +
                "<tptz:ProfileToken>" + escapeXml(profileToken) + "</tptz:ProfileToken>" +
                "<tptz:AuxiliaryData>" + escapeXml(command) + "</tptz:AuxiliaryData>" +
                "</tptz:SendAuxiliaryCommand>";
        sendSoapRequest(ptzServiceUrl, bodyXml, channel);
    }

    /**
     * 获取音频输出信息（GetAudioOutputs）
     * ONVIF Media Service - GetAudioOutputs
     * 用于对讲功能初始化
     *
     * @param channel 通道
     * @return 音频输出 token 列表
     */
    public List<String> getAudioOutputs(OnvifDeviceChannel channel) {
        String serviceUrl = resolveMediaServiceUrl(channel);
        String bodyXml =
                "<trt:GetAudioOutputs xmlns:trt=\"http://www.onvif.org/ver10/media/wsdl\"/>";
        Document doc = sendSoapRequest(serviceUrl, bodyXml, channel);
        List<String> tokens = new ArrayList<>();
        NodeList outputs = doc.getElementsByTagNameNS("*", "AudioOutputs");
        for (int i = 0; i < outputs.getLength(); i++) {
            Element output = (Element) outputs.item(i);
            String token = output.getAttribute("token");
            if (token != null && !token.isEmpty()) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    // -------------------------------------------------------------------------
    // SOAP 核心方法
    // -------------------------------------------------------------------------

    /**
     * 构建 SOAP 信封（含 WS-Security UsernameToken 认证头）
     * 遵循 ONVIF Core Specification Section 5.12.1 WS-UsernameToken Profile
     *
     * @param channel 通道（提供认证信息）
     * @param bodyContent SOAP Body 内容
     * @return 完整 SOAP XML 字符串
     */
    public String buildSoapEnvelope(OnvifDeviceChannel channel, String bodyContent) {
        String username = channel.getUsername();
        String password = channel.getPassword();
        if (username == null || password == null) {
            // 无认证信息，构建不含 Security 头的 SOAP 信封
            return "<s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\">" +
                    "<s:Body>" + bodyContent + "</s:Body></s:Envelope>";
        }

        // WS-Security UsernameToken（Digest 模式）
        // 参见 ONVIF Core Specification Section 5.12.1
        String nonce = generateNonce();
        String created = getUtcTimestamp();
        String digest = generatePasswordDigest(nonce, created, password);
        String nonceBase64 = Base64.getEncoder().encodeToString(nonce.getBytes(StandardCharsets.UTF_8));

        return "<s:Envelope xmlns:s=\"http://www.w3.org/2003/05/soap-envelope\">" +
                "<s:Header>" +
                "<Security xmlns=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\" " +
                "xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">" +
                "<UsernameToken>" +
                "<Username>" + escapeXml(username) + "</Username>" +
                "<Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest\">" +
                digest + "</Password>" +
                "<Nonce EncodingType=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary\">" +
                nonceBase64 + "</Nonce>" +
                "<wsu:Created>" + created + "</wsu:Created>" +
                "</UsernameToken>" +
                "</Security>" +
                "</s:Header>" +
                "<s:Body>" + bodyContent + "</s:Body>" +
                "</s:Envelope>";
    }

    /**
     * 发送 SOAP 请求并解析响应
     * 包含重试机制（最多 MAX_RETRY 次）
     *
     * @param url  ONVIF Service URL
     * @param body SOAP 请求体（完整 XML 字符串）
     * @param channel 通道信息（用于错误日志）
     * @return 解析后的 Document
     * @throws ServiceException SOAP Fault 或网络错误时抛出
     */
    public Document sendSoapRequest(String url, String body, OnvifDeviceChannel channel) {
        log.debug("[OnvifClient] SOAP 请求 URL: {}, 通道: {}:{}", url,
                channel != null ? channel.getIp() : "unknown",
                channel != null ? channel.getPort() : 0);

        Exception lastException = null;
        for (int retry = 0; retry < MAX_RETRY; retry++) {
            try {
                return doSendSoapRequest(url, body);
            } catch (OnvifAuthException e) {
                // 认证失败不重试
                throw new ServiceException(ErrorCode.ERROR100.getCode(), "ONVIF 认证失败: " + e.getMessage());
            } catch (OnvifUnsupportedException e) {
                // 不支持的操作不重试
                throw new ServiceException(ErrorCode.ERROR100.getCode(), "ONVIF 设备不支持该操作: " + e.getMessage());
            } catch (Exception e) {
                lastException = e;
                log.warn("[OnvifClient] 第 {} 次请求失败，URL: {}, 错误: {}", retry + 1, url, e.getMessage());
                if (retry < MAX_RETRY - 1) {
                    try {
                        Thread.sleep(500L * (retry + 1));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        throw new ServiceException(ErrorCode.ERROR100.getCode(),
                "ONVIF 设备不可达（" + url + "）: " + (lastException != null ? lastException.getMessage() : "未知错误"));
    }

    // -------------------------------------------------------------------------
    // 内部实现
    // -------------------------------------------------------------------------

    private Document doSendSoapRequest(String url, String body) throws Exception {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .build();
        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig).build()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/soap+xml; charset=utf-8");
            httpPost.setHeader("SOAPAction", "\"\"");
            httpPost.setEntity(new StringEntity(body, StandardCharsets.UTF_8));

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                HttpEntity entity = response.getEntity();
                String responseBody = (entity != null) ? EntityUtils.toString(entity, StandardCharsets.UTF_8) : "";
                log.debug("[OnvifClient] SOAP 响应 StatusCode: {}", statusCode);

                if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                    throw new OnvifAuthException("HTTP 401 Unauthorized");
                }

                Document doc = parseXml(responseBody);

                // 检查 SOAP Fault
                NodeList faultNodes = doc.getElementsByTagNameNS("*", "Fault");
                if (faultNodes.getLength() > 0) {
                    Element fault = (Element) faultNodes.item(0);
                    String faultCode = extractText(fault, "Value");
                    String faultReason = extractText(fault, "Text");
                    if (faultReason == null || faultReason.isEmpty()) {
                        faultReason = extractText(fault, "faultstring");
                    }
                    // 解析具体错误类型
                    if (faultReason != null && faultReason.toLowerCase().contains("not authorized")) {
                        throw new OnvifAuthException(faultReason);
                    }
                    if (faultCode != null && faultCode.contains("ActionNotSupported")) {
                        throw new OnvifUnsupportedException(faultReason);
                    }
                    throw new RuntimeException("SOAP Fault: [" + faultCode + "] " + faultReason);
                }
                return doc;
            }
        }
    }

    /** 生成 WS-Security Digest 密码 */
    private String generatePasswordDigest(String nonce, String created, String password) {
        try {
            byte[] nonceBytes = nonce.getBytes(StandardCharsets.UTF_8);
            byte[] createdBytes = created.getBytes(StandardCharsets.UTF_8);
            byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
            byte[] combined = new byte[nonceBytes.length + createdBytes.length + passwordBytes.length];
            System.arraycopy(nonceBytes, 0, combined, 0, nonceBytes.length);
            System.arraycopy(createdBytes, 0, combined, nonceBytes.length, createdBytes.length);
            System.arraycopy(passwordBytes, 0, combined, nonceBytes.length + createdBytes.length, passwordBytes.length);
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(combined);
            return Base64.getEncoder().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 不可用", e);
        }
    }

    /** 生成随机 Nonce */
    private String generateNonce() {
        byte[] nonce = new byte[16];
        new SecureRandom().nextBytes(nonce);
        return Base64.getEncoder().encodeToString(nonce);
    }

    /** 生成 UTC 时间戳（ISO 8601） */
    private String getUtcTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    /** 解析 XML 字符串为 Document */
    private Document parseXml(String xml) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        // 防止 XXE 攻击
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        try (InputStream is = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))) {
            return builder.parse(is);
        }
    }

    /** 提取指定标签名的文本内容（取第一个匹配） */
    public String extractText(Document doc, String tagName) {
        NodeList nodes = doc.getElementsByTagNameNS("*", tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return null;
    }

    /** 提取 Element 子节点文本 */
    private String extractText(Element element, String tagName) {
        NodeList nodes = element.getElementsByTagNameNS("*", tagName);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        }
        return null;
    }

    /** 获取 Element 直接子节点文本 */
    private String getChildText(Element parent, String childTagName) {
        NodeList children = parent.getElementsByTagNameNS("*", childTagName);
        if (children.getLength() > 0) {
            return children.item(0).getTextContent();
        }
        return null;
    }

    /** 构建 Service URL（ip:port + path） */
    public String buildServiceUrl(OnvifDeviceChannel channel, String path) {
        return "http://" + channel.getIp() + ":" + channel.getPort() + path;
    }

    /** 规范化 Service URL（处理相对路径） */
    private String normalizeServiceUrl(OnvifDeviceChannel channel, String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        if (url.startsWith("http")) {
            return url;
        }
        return "http://" + channel.getIp() + ":" + channel.getPort() + (url.startsWith("/") ? "" : "/") + url;
    }

    /** 解析 Media Service URL */
    private String resolveMediaServiceUrl(OnvifDeviceChannel channel) {
        if (channel.getMediaServiceUrl() != null && !channel.getMediaServiceUrl().isEmpty()) {
            return channel.getMediaServiceUrl();
        }
        return buildServiceUrl(channel, "/onvif/media_service");
    }

    /** 解析 PTZ Service URL */
    private String resolvePtzServiceUrl(OnvifDeviceChannel channel) {
        if (channel.getPtzServiceUrl() != null && !channel.getPtzServiceUrl().isEmpty()) {
            return channel.getPtzServiceUrl();
        }
        return buildServiceUrl(channel, "/onvif/ptz_service");
    }

    /** 解析 Profile Token */
    private String resolveProfileToken(OnvifDeviceChannel channel) {
        if (channel.getProfileToken() != null && !channel.getProfileToken().isEmpty()) {
            return channel.getProfileToken();
        }
        throw new ServiceException(ErrorCode.ERROR100.getCode(),
                "通道 [" + channel.getId() + "] 尚未获取 Profile Token，请先调用设备信息查询");
    }

    /** XML 特殊字符转义 */
    public String escapeXml(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&apos;");
    }

    // -------------------------------------------------------------------------
    // 内部异常类
    // -------------------------------------------------------------------------

    /** ONVIF 认证失败异常 */
    public static class OnvifAuthException extends RuntimeException {
        public OnvifAuthException(String message) {
            super(message);
        }
    }

    /** ONVIF 不支持的操作异常 */
    public static class OnvifUnsupportedException extends RuntimeException {
        public OnvifUnsupportedException(String message) {
            super(message);
        }
    }
}
