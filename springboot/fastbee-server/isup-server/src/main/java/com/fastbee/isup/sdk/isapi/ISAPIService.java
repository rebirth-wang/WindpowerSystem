package com.fastbee.isup.sdk.isapi;

import java.util.Optional;

import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.fastbee.common.utils.StringUtils;
import com.fastbee.isup.model.ChanStatusObj;
import com.fastbee.isup.model.IsupDevInfo;
import com.fastbee.isup.model.xml.*;
import com.fastbee.isup.sdk.service.impl.CmsUtil;
import com.fastbee.isup.service.DeviceCacheService;
import com.fastbee.isup.util.XmlUtil;

@Slf4j
@Profile("isup")
@Service
@RequiredArgsConstructor
public class ISAPIService {
    private final CmsUtil cmsUtil;
    private final DeviceCacheService deviceCacheService;

    public String getSyscapabilities(int lUserID) {
        String SystemcapabilitiesURL = "GET /ISAPI/System/capabilities";
        String contextXML = cmsUtil.passThrough(lUserID, SystemcapabilitiesURL, "");
        //log.info("getSyscapabilities: {}", contextXML);
        return contextXML;
    }

    /**
     * 获取设备信息（型号、版本、序列号等）
     *
     * @param lUserID
     */
    public DeviceInfo getDevInfo(int lUserID) {
        String getDevInfoURL = "GET /ISAPI/System/deviceInfo";
        String contextXML = cmsUtil.passThrough(lUserID, getDevInfoURL, "");
        //log.info("GetDevInfo: {}", contextXML);
        return XmlUtil.fromXml(contextXML, DeviceInfo.class);
    }

    public ResponseStatus reboot(int lUserID) {
        String rebootURL = "PUT /ISAPI/System/reboot";
        String contextXML = cmsUtil.passThrough(lUserID, rebootURL, "");
        log.info("ResponseStatus: {}", contextXML);
        return XmlUtil.fromXml(contextXML, ResponseStatus.class);
    }

    // type  smart faceContrast
    public ResponseStatus PutVCAResource(int lUserID, Integer channelId, String type) {
        String PutVCAResourceURL = "PUT /ISAPI/System/Video/inputs/channels/" + channelId + "/VCAResource";
        String Xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<VCAResource xmlns=\"http://www.hikvision.com/ver20/XMLSchema\" version=\"2.0\">" +
                "<type>" + type + "</type>\n" +
                "</VCAResource>\n";
        String contextXML = cmsUtil.passThrough(lUserID, PutVCAResourceURL, Xml);
        log.info("PutVCAResource: {}", contextXML);
        return XmlUtil.fromXml(contextXML, ResponseStatus.class);
    }


    public ResponseStatus VCAResourceTest(int lUserID, Integer channelId, String type) {
        String VCAResourceTest = "POST /ISAPI/System/Video/inputs/channels/" + channelId + "/VCAResource/test";
        String Xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<VCAResource xmlns=\"http://www.hikvision.com/ver20/XMLSchema\" version=\"2.0\">" +
                "<type>" + type + "</type>\n" +
                "</VCAResource>\n";
        String contextXML = cmsUtil.passThrough(lUserID, VCAResourceTest, Xml);
        log.info("VCAResourceTest: {}", contextXML);
        return XmlUtil.fromXml(contextXML, ResponseStatus.class);
    }


    public DeviceInfo getVCAResource(int lUserID, Integer channelId) {
        String getVCAResourceURL = "GET /ISAPI/System/Video/inputs/channels/" + channelId + "/VCAResource";
        //String getVCAResourceURL = "GET /ISAPI/System/Video/inputs/channels/" + channelId + "/VCAResource/capabilities";
        String contextXML = cmsUtil.passThrough(lUserID, getVCAResourceURL, "");
        log.info("GetVCAResourceURL: {}", contextXML);
        return XmlUtil.fromXml(contextXML, DeviceInfo.class);
    }

    /**
     * 获取所有数字通道状态
     *
     * @param lUserID
     */
    public InputProxyChannelStatusList getAllDigitalChannelStatus(int lUserID) {
        String GetAllDigitalChannelStatusURL = "GET /ISAPI/ContentMgmt/InputProxy/channels/status";
        String contextXML = cmsUtil.passThrough(lUserID, GetAllDigitalChannelStatusURL, "");
        log.info("GetAllDigitalChannelStatusURL: {}", contextXML);
        return XmlUtil.fromXml(contextXML, InputProxyChannelStatusList.class);
    }


    public ChanStatusObj getWorkingstatus(int lUserID) {
        String GetWorkingstatusURL = "GET /ISAPI/System/workingstatus?format=json";
        String contextJson = cmsUtil.passThrough(lUserID, GetWorkingstatusURL, "");
        // 字符串转json
        //log.info("GetWorkingstatusURL: {}", contextJson);
        return JSON.parseObject(contextJson, ChanStatusObj.class);
    }

    /**
     * 控制云台移动
     *
     * @param deviceId   设备ID
     * @param panSpeed   水平速度（-100~100，正为右，负为左）
     * @param tiltSpeed  垂直速度（-100~100，正为上，负为下）
     * @param durationMs 持续时间（毫秒）
     */
    public void controlPtz(String deviceId, Integer channelId, int panSpeed, int tiltSpeed, int durationMs) {
        Optional<IsupDevInfo> oneOpt = deviceCacheService.getByDeviceId(deviceId);
        if (oneOpt.isPresent()) {
            IsupDevInfo device = oneOpt.get();
            int userId = device.getLoginId();
            try {
                // 启动云台移动
                log.info("开始云台移动: userId={}, channel={}, pan={}, tilt={}", userId, channelId, panSpeed, tiltSpeed);
                this.doControlPtz(userId, channelId, panSpeed, tiltSpeed);
                if (panSpeed == 0 && tiltSpeed == 0) return;
                // 等待指定时间后停止
                Thread.sleep(Math.min(durationMs, 5000));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("云台控制线程被中断", e);
            } catch (Exception e) {
                log.error("云台控制异常: userId={}, channel={}, 错误={}", userId, channelId, e.getMessage(), e);
                throw new RuntimeException("云台控制失败：" + e.getMessage(), e);
            } finally {
                this.doControlPtz(userId, channelId, 0, 0);
                log.info("停止云台移动: userId={}, channel={}", userId, channelId);
            }
        }
    }

    private void doControlPtz(int userId, int channelId, int pan, int tilt) {
        String url = "PUT /ISAPI/PTZCtrl/channels/" + channelId + "/continuous";
        String startXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<PTZData>\n" +
                "    <pan>" + pan + "</pan>\n" +
                "    <tilt>" + tilt + "</tilt>\n" +
                "</PTZData>\n";
        cmsUtil.passThrough(userId, url, startXml);
    }


    /**
     * 远程抓图
     * URL中的<ID>，格式A0B，A表示实际通道号，B传1表示主码流，传2表示子码流。
     * 示例：GET /ISAPI/Streaming/channels/101/picture/async?format=json&imageType=JPEG&URLType=cloudURL表示获取通道1的主码流分辨率的抓图。
     *
     * @param lUserID
     */
    public void getPicByCloud(int lUserID) {
        String getPicURL = "GET /ISAPI/Streaming/channels/101/picture/async?format=json&imageType=JPEG&URLType=cloudURL";
        cmsUtil.passThrough(lUserID, getPicURL, "");
    }


    public String getFDcapabilities(int lUserID) {
        String FDcapabilitiesURL = "GET /ISAPI/Intelligent/FDLib/capabilities";
        String contextXML = cmsUtil.passThrough(lUserID, FDcapabilitiesURL, "");
        log.info("getFDcapabilities: {}", contextXML);
        return contextXML;
    }

    public FDLibBaseCfgList getFDlib(int lUserID) {
        String getFDlibURL = "GET /ISAPI/Intelligent/FDLib";
        String contextXML = cmsUtil.passThrough(lUserID, getFDlibURL, "");
        log.info("getFDlib: {}", contextXML);
        return XmlUtil.fromXml(contextXML, FDLibBaseCfgList.class);
    }

    // 创建人脸库
    public FDLibInfoList addFDlib(int lUserID) {
        String addFDlibURL = "POST /ISAPI/Intelligent/FDLib";
        String addFDlib = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<CreateFDLibList xmlns=\"http://www.isapi.org/ver20/XMLSchema\" version=\"2.0\">" +
                "<CreateFDLib>" +
                "<id>1</id>" +
                "<name>isupFaceLib</name>" +
                "<thresholdValue>60</thresholdValue>" +
                "<customInfo>test</customInfo>" +
                "</CreateFDLib>" +
                "</CreateFDLibList>";
        String contextXML = cmsUtil.passThrough(lUserID, addFDlibURL, addFDlib);
        log.info("addFDlib: {}", contextXML);
        return XmlUtil.fromXml(contextXML, FDLibInfoList.class);
    }

    // 删除所有人脸库
    public String delFDlib(int lUserID) {
        String getFDlibURL = "DELETE /ISAPI/Intelligent/FDLib";
        String contextXML = cmsUtil.passThrough(lUserID, getFDlibURL, "");
        log.info("getFDlib: {}", contextXML);
        return contextXML;
    }

    /**
     * 远程抓图
     * URL中的<ID>，格式A0B，A表示实际通道号，B传1表示主码流，传2表示子码流。
     * 示例：GET /ISAPI/Streaming/channels/101/picture/async?format=json&imageType=JPEG&URLType=cloudURL表示获取通道1的主码流分辨率的抓图。
     *
     * @param lUserID
     */
    // 查询人脸图片数据
    public String FDSearch(int lUserID, String FDID) {
        String FDSearchURL = "POST /ISAPI/Intelligent/FDLib/FDSearch";
        String FDSearchDescription = "<FDSearchDescription version=\"2.0\" xmlns=\"http://www.std-cgi.org/ver20/XMLSchema\">" +
                "<searchID>CB6D4429-E7E0-0001-5D6A-166F29C0B790</searchID>" +
                // 从第几条开始查
                "<searchResultPosition>1</searchResultPosition>" +
                // 查询结果数量
                "<maxResults>50</maxResults>" +
                "<FDID>" + FDID + "</FDID>" +
                "<startTime>1970-01-01</startTime>" +
                "<endTime>2026-05-11</endTime>" +
//                "<name></name>" +
//                "<province></province>" +
//                "<city></city>" +
//                "<certificateType>ID</certificateType>" +
//                "<certificateNumber></certificateNumber>" +
//                "<phoneNumber></phoneNumber>" +
//                "<FaceModeList>" +
//                    "<FaceMode>" +
//                        "<ModeInfo>" +
//                            "<similarity></similarity>" +
//                            "<modeData></modeData>" +
//                        "</ModeInfo>" +
//                    "</FaceMode>" +
//                "</FaceModeList>" +
//                "<modelingStatus></modelingStatus>" +
//                "<modelStatus></modelStatus>" +
                "</FDSearchDescription>";
        String contextXML = cmsUtil.passThrough(lUserID, FDSearchURL, FDSearchDescription);
        log.info("FDSearch: {}", contextXML);
        return contextXML;
    }

    // 导入图片到人脸库
    public MaskInfo uploadPicture(int lUserID, String FDID, String picURL) {
        String uploadPictureURL = "POST /ISAPI/Intelligent/FDLib/pictureUpload";
        String uploadPictureDescription = "<?xml version='1.0' encoding='UTF-8'?>\n" +
                "<PictureUploadData xmlns=\"http://www.isapi.org/ver20/XMLSchema\" version=\"2.0\">\n" +
                "    <FDID>" + FDID + "</FDID>\n" +
                "    <FaceAppendData>\n" +
                "        <name>TEST888</name>\n" +
                "        <bornTime>2000-01-01</bornTime>\n" +
                "        <sex>male</sex>\n" +
                "        <province>11</province>\n" +
                "        <city>01</city>\n" +
                "        <certificateType>ID</certificateType>\n" +
                "        <certificateNumber>12345678</certificateNumber>\n" +
                "        <PersonInfoExtendList>\n" +
                "            <PersonInfoExtend>\n" +
                "                <id>1</id>\n" +
                "                <enable>true</enable>\n" +
                "                <name>phoneNumber</name>\n" +
                "                <value>12345678</value>\n" +
                "            </PersonInfoExtend>\n" +
                "        </PersonInfoExtendList>\n" +
                "    </FaceAppendData>\n" +
                "    <picURL>" + picURL + "</picURL>\n" +
                "</PictureUploadData>";
        String contextXML = cmsUtil.passThrough(lUserID, uploadPictureURL, uploadPictureDescription);
        log.info("uploadPicture: {}", contextXML);
        return XmlUtil.fromXml(contextXML, MaskInfo.class);
    }


    // 获取人员标签信息拓展配置
    public String getPersonlnfoExtend(int lUserID) {
        String getPersonlnfoExtendURL = "GET /ISAPI/Intelligent/faceContrast/personInfoExtend";
        String contextXML = cmsUtil.passThrough(lUserID, getPersonlnfoExtendURL, "");
        log.info("getPersonlnfoExtend: {}", contextXML);
        return contextXML;
    }

    // 配置人员标签信息拓展参数
    public String putPersonlnfoExtend(int lUserID) {
        String putPersonlnfoExtendURL = "PUT /ISAPI/Intelligent/faceContrast/personInfoExtend";
        String personlnfoExtendDescription = "<PersonInfoExtendList version=\"2.0\" xmlns=\"http://www.hikvision.com/ver20/XMLSchema\">\n" +
                "    <PersonInfoExtend>\n" +
                "        <id>1</id>\n" +
                "        <enable>true</enable>\n" +
                "        <name>phoneNumber</name>\n" +
                "        <value></value>\n" +
                "    </PersonInfoExtend>\n" +
                "    <PersonInfoExtend>\n" +
                "        <id>2</id>\n" +
                "        <enable>false</enable>\n" +
                "        <name>自定义字段2</name>\n" +
                "        <value></value>\n" +
                "    </PersonInfoExtend>\n" +
                "    <PersonInfoExtend>\n" +
                "        <id>3</id>\n" +
                "        <enable>false</enable>\n" +
                "        <name>自定义字段3</name>\n" +
                "        <value></value>\n" +
                "    </PersonInfoExtend>\n" +
                "</PersonInfoExtendList>";
        String contextXML = cmsUtil.passThrough(lUserID, putPersonlnfoExtendURL, personlnfoExtendDescription);
        log.info("putPersonlnfoExtend: {}", contextXML);
        return contextXML;
    }

    // 删除人脸图片
    public String delFDlibPicture(int lUserID, String FDID, String picturelD, String FDType) {
        String delFDlibPictureURL = "DELETE /ISAPI/Intelligent/FDLib/" + FDID + "/picture/" + picturelD;
        if (StringUtils.isNotBlank(FDType)) {
            delFDlibPictureURL += "?FDType=" + FDType;
        }
        String contextXML = cmsUtil.passThrough(lUserID, delFDlibPictureURL, "");
        log.info("getFDlib: {}", contextXML);
        return contextXML;
    }

    // 获取人脸库可导入人脸数量
    public String getPsurplusCapacity(int lUserID, String FDID, String FDType) {
        String getPsurplusCapacityURL = "GET /ISAPI/Intelligent/FDLib/" + FDID + "/picture/surplusCapacity";
        log.info("getFaceSchedules: {}", getPsurplusCapacityURL);
        if (StringUtils.isNotBlank(FDType)) {
            getPsurplusCapacityURL += "?FDType=" + FDType;
        }
        String contextXML = cmsUtil.passThrough(lUserID, getPsurplusCapacityURL, "");
        log.info("getPsurplusCapacity: {}", contextXML);
        return contextXML;
    }

    // 人脸对比
    // 配置人脸库单个通道时间布放参数
    public String putFaceSchedules(int lUserID, int channelId, String FDID, String FDType) {
        String putFaceSchedulesURL = "PUT /ISAPI/Event/schedules/faceLib/" + channelId + "/" + FDID;
        if (StringUtils.isNotBlank(FDType)) {
            putFaceSchedulesURL += "?FDType=" + FDType;
        }
        String FaceSchedulesDescription = "<Schedule version=\"2.0\" xmlns=\"http://www.hikvision.com/ver20/XMLSchema\">\n" +
                "    <id>faceLib-1</id>\n" +
                "    <eventType>faceLib</eventType>\n" +
                "    <videoInputChannelID>1</videoInputChannelID>\n" +
                "    <TimeBlockList size=\"8\" >\n" +
                "        <TimeBlock>\n" +
                "            <dayOfWeek>1</dayOfWeek>\n" +
                "            <TimeRange>\n" +
                "                <beginTime>00:00</beginTime>\n" +
                "                <endTime>24:00</endTime>\n" +
                "            </TimeRange>\n" +
                "        </TimeBlock>\n" +
                "        <TimeBlock>\n" +
                "            <dayOfWeek>2</dayOfWeek>\n" +
                "            <TimeRange>\n" +
                "                <beginTime>00:00</beginTime>\n" +
                "                <endTime>24:00</endTime>\n" +
                "            </TimeRange>\n" +
                "        </TimeBlock>\n" +
                "        <TimeBlock>\n" +
                "            <dayOfWeek>3</dayOfWeek>\n" +
                "            <TimeRange>\n" +
                "                <beginTime>00:00</beginTime>\n" +
                "                <endTime>24:00</endTime>\n" +
                "            </TimeRange>\n" +
                "        </TimeBlock>\n" +
                "        <TimeBlock>\n" +
                "            <dayOfWeek>4</dayOfWeek>\n" +
                "            <TimeRange>\n" +
                "                <beginTime>00:00</beginTime>\n" +
                "                <endTime>24:00</endTime>\n" +
                "            </TimeRange>\n" +
                "        </TimeBlock>\n" +
                "        <TimeBlock>\n" +
                "            <dayOfWeek>5</dayOfWeek>\n" +
                "            <TimeRange>\n" +
                "                <beginTime>00:00</beginTime>\n" +
                "                <endTime>24:00</endTime>\n" +
                "            </TimeRange>\n" +
                "        </TimeBlock>\n" +
                "        <TimeBlock>\n" +
                "            <dayOfWeek>6</dayOfWeek>\n" +
                "            <TimeRange>\n" +
                "                <beginTime>00:00</beginTime>\n" +
                "                <endTime>24:00</endTime>\n" +
                "            </TimeRange>\n" +
                "        </TimeBlock>\n" +
                "        <TimeBlock>\n" +
                "            <dayOfWeek>7</dayOfWeek>\n" +
                "            <TimeRange>\n" +
                "                <beginTime>00:00</beginTime>\n" +
                "                <endTime>24:00</endTime>\n" +
                "            </TimeRange>\n" +
                "        </TimeBlock>\n" +
                "    </TimeBlockList>\n" +
                "</Schedule>";
        String contextXML = cmsUtil.passThrough(lUserID, putFaceSchedulesURL, FaceSchedulesDescription);
        log.info("putFaceSchedules: {}", contextXML);
        return contextXML;
    }

    // 获取人脸库单个通道时间布放参数
    public String getFaceSchedules(int lUserID, int channelId, String FDID, String FDType) {
        String getFaceSchedulesURL = "GET /ISAPI/Event/schedules/faceLib/" + channelId + "/" + FDID;
        log.info("getFaceSchedules: {}", getFaceSchedulesURL);
        if (StringUtils.isNotBlank(FDType)) {
            getFaceSchedulesURL += "?FDType=" + FDType;
        }
        String contextXML = cmsUtil.passThrough(lUserID, getFaceSchedulesURL, "");
        log.info("getFaceSchedules: {}", contextXML);
        return contextXML;
    }

    // 获取人脸库联动配置
    public String getFaceTriggers(int lUserID, int channelId, String FDID, String FDType) {
        String getFaceTriggersURL = "GET /ISAPI/Event/triggers/faceLib-" + channelId + "/" + FDID;
        log.info("getFaceSchedules: {}", getFaceTriggersURL);
        if (StringUtils.isNotBlank(FDType)) {
            getFaceTriggersURL += "?FDType=" + FDType;
        }
        String contextXML = cmsUtil.passThrough(lUserID, getFaceTriggersURL, "");
        log.info("getFaceTriggers: {}", contextXML);
        return contextXML;
    }

    // 配置人脸库联动配置
    public String putFaceTriggers(int lUserID, int channelId, String FDID, String FDType) {
        String putFaceTriggersURL = "PUT /ISAPI/Event/triggers/faceLib-" + channelId + "/" + FDID;
        if (StringUtils.isNotBlank(FDType)) {
            putFaceTriggersURL += "?FDType=" + FDType;
        }
        String FaceTriggersDescription = "<EventTrigger version=\"2.0\" xmlns=\"http://www.hikvision.com/ver20/XMLSchema\">\n" +
                "    <id>faceLib-1</id>\n" +
                "    <eventType>faceLib</eventType>\n" +
                "    <eventDescription>faceLib Event trigger Information</eventDescription>\n" +
                "    <videoInputChannelID>1</videoInputChannelID>\n" +
                "    <dynVideoInputChannelID>1</dynVideoInputChannelID>\n" +
                "    <EventTriggerNotificationList>\n" +
                "        <EventTriggerNotification>\n" +
                "            <id>center</id>\n" +
                "            <notificationMethod>center</notificationMethod>\n" +
                "            <notificationRecurrence>beginning</notificationRecurrence>\n" +
                "        </EventTriggerNotification>\n" +
                "        <EventTriggerNotification>\n" +
                "            <id>FTP</id>\n" +
                "            <notificationMethod>FTP</notificationMethod>\n" +
                "            <notificationRecurrence>beginning</notificationRecurrence>\n" +
                "        </EventTriggerNotification>\n" +
                "    </EventTriggerNotificationList>\n" +
                "</EventTrigger>";
        String contextXML = cmsUtil.passThrough(lUserID, putFaceTriggersURL, FaceTriggersDescription);
        log.info("putFaceTriggers: {}", contextXML);
        return contextXML;
    }

    // 配置人脸对比参数
    public String putFaceContrast(int lUserID, int channelId) {
        String putFaceContrastURL = "PUT /ISAPI/Intelligent/channels/" + channelId + "/faceContrast";
        String FaceContrastDescription = "<FaceContrastList version=\"2.0\" xmlns=\"http://www.hikvision.com/ver20/XMLSchema\">\n" +
                "    <FaceContrast>\n" +
                "        <id>1</id>\n" +
                "        <enable>true</enable>\n" +
                "        <faceContrastType>faceContrast</faceContrastType>\n" +
                "        <contrastFailureAlarmUpload>true</contrastFailureAlarmUpload>\n" +
                "        <QuickContrast>\n" +
                "            <enabled>true</enabled>\n" +
                "            <snapTime>3</snapTime>\n" +
                "            <threshold>50</threshold>\n" +
                "            <quickConfigMode>custom</quickConfigMode>\n" +
                "            <Custom>\n" +
                "                <timeOutMode>infinite</timeOutMode>\n" +
                "                <duplicateContrastMode>success</duplicateContrastMode>\n" +
                "            </Custom>\n" +
                "        </QuickContrast>\n" +
                "        <contrastSuccessAlarmout>true</contrastSuccessAlarmout>\n" +
                "        <contrastFailureAlarmout>true</contrastFailureAlarmout>\n" +
                "    </FaceContrast>\n" +
                "</FaceContrastList>";
        String contextXML = cmsUtil.passThrough(lUserID, putFaceContrastURL, FaceContrastDescription);
        log.info("putFaceContrast: {}", contextXML);
        return contextXML;
    }

    // 获取人脸对比参数
    public String getFaceContrast(int lUserID, int channelId) {
        String getFaceContrastURL = "GET /ISAPI/Intelligent/channels/" + channelId + "/faceContrast";
        log.info("getFaceSchedules: {}", getFaceContrastURL);
        String contextXML = cmsUtil.passThrough(lUserID, getFaceContrastURL, "");
        log.info("getFaceContrast: {}", contextXML);
        return contextXML;
    }

    public String getFaceContrastCapabilities(int lUserID, int channelId) {
        String getFaceContrastURL = "GET /ISAPI/Intelligent/channels/" + channelId + "/faceContrast/capabilities";
        String contextXML = cmsUtil.passThrough(lUserID, getFaceContrastURL, "");
        log.info("getFaceContrast: {}", contextXML);
        return contextXML;
    }


    /**
     * 远程抓图
     * URL中的<ID>，格式A0B，A表示实际通道号，B传1表示主码流，传2表示子码流。
     * 示例：GET /ISAPI/Streaming/channels/101/picture/async?format=json&imageType=JPEG&URLType=cloudURL表示获取通道1的主码流分辨率的抓图。
     *
     * @param lUserID
     */
    public String searchLPListAudit(int lUserID, int channelId) {
        String searchLPListAuditURL = "POST /ISAPI/Traffic/channels/" + channelId + "/searchLPListAudit";
        String FDSearchDescription = "<FDSearchDescription version=\"2.0\" xmlns=\"http://www.std-cgi.org/ver20/XMLSchema\">" +
                "<searchID>CB6D4429-E7E0-0001-5D6A-166F29C0B790</searchID>" +
                "<searchResultPosition>0</searchResultPosition>" +
                "<maxResults>15</maxResults>" +
                "<FDID>1</FDID>" +
                "<startTime></startTime>" +
                "<endTime></endTime>" +
                "<name></name>" +
                "<province></province>" +
                "<city></city>" +
                "<certificateType>ID</certificateType>" +
                "<certificateNumber></certificateNumber>" +
                "<phoneNumber></phoneNumber>" +
                "<FaceModeList>" +
                "<FaceMode>" +
                "<ModeInfo>" +
                "<similarity></similarity>" +
                "<modeData></modeData>" +
                "</ModeInfo>" +
                "</FaceMode>" +
                "</FaceModeList>" +
                "<modelingStatus></modelingStatus>" +
                "<modelStatus></modelStatus>" +
                "</FDSearchDescription>";
        return cmsUtil.passThrough(lUserID, searchLPListAuditURL, FDSearchDescription);
    }


    /**
     * 远程抓图
     * URL中的<ID>，格式A0B，A表示实际通道号，B传1表示主码流，传2表示子码流。
     * 示例：GET /ISAPI/Streaming/channels/101/picture/async?format=json&imageType=JPEG&URLType=cloudURL表示获取通道1的主码流分辨率的抓图。
     *
     * @param lUserID
     */
    public String asyncImportDatas(Integer lUserID, String xmlUrl) {
        String searchLPListAuditURL = "POST /ISAPI/Intelligent/FDLib/asyncImportDatas?format=json";
        String faceLibId = "brecorder";
        String taskId = "1ad321f56dssfd1dsc";
        String FDSearchDescription = "{\n" +
                "    \"AsyncImportDatas\": {\n" +
                "        \"customFaceLibID\": \"" + faceLibId + "\",\n" +
                "        \"taskID\": \"" + taskId + "\",\n" +
                "        \"URL\": \"" + xmlUrl + "\",\n" +
                "        \"type\": 0,\n" +
                "        \"URLCertificationType\": \"AWS2_0\"\n" +
                "    }\n" +
                "}";
        return cmsUtil.passThrough(lUserID, searchLPListAuditURL, FDSearchDescription);
    }
}
