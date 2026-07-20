package com.fastbee.isup.sdk.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jna.Pointer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.fastbee.isup.model.cb.*;
import com.fastbee.isup.model.vo.UploadData;
import com.fastbee.isup.model.xml.EventNotificationAlert;
import com.fastbee.isup.sdk.service.EHomeMsgCallBack;
import com.fastbee.isup.sdk.service.constant.EHOME_ALARM_TYPE;
import com.fastbee.isup.sdk.structure.BYTE_ARRAY;
import com.fastbee.isup.sdk.structure.NET_EHOME_ALARM_ISAPI_INFO;
import com.fastbee.isup.sdk.structure.NET_EHOME_ALARM_MSG;
import com.fastbee.isup.util.XmlUtil;

@Slf4j
@Service("alarmMsgCallBack")
@RequiredArgsConstructor
public class AlarmMsgCallBack implements EHomeMsgCallBack {


    @Override
    public boolean invoke(int iHandle, NET_EHOME_ALARM_MSG pAlarmMsg, Pointer pUser) {
        // log.info("--------报警回调函数被调用--------");
        // 输出事件信息到控制台上
        log.info("AlarmType: {}, dwAlarmInfoLen: {}, dwXmlBufLen: {}, iHandle: {}",
                pAlarmMsg.dwAlarmType, pAlarmMsg.dwAlarmInfoLen, pAlarmMsg.dwXmlBufLen, iHandle);

        switch (pAlarmMsg.dwAlarmType) {
            case EHOME_ALARM_TYPE.EHOME_ALARM_ACS://门禁事件上报
            {
                if (pAlarmMsg.dwXmlBufLen != 0) {
                    BYTE_ARRAY strXMLData = new BYTE_ARRAY(pAlarmMsg.dwXmlBufLen);
                    strXMLData.write();
                    Pointer pPlateInfo = strXMLData.getPointer();
                    pPlateInfo.write(0, pAlarmMsg.pXmlBuf.getByteArray(0, strXMLData.size()), 0, strXMLData.size());
                    strXMLData.read();
                    String strXML = new String(strXMLData.byValue).trim();
                    System.out.println(strXML + "\n");
                }
                break;
            }
            case EHOME_ALARM_TYPE.EHOME_ISAPI_ALARM://ISAPI报警上传
            {
                if (pAlarmMsg.pAlarmInfo != null) {
                    NET_EHOME_ALARM_ISAPI_INFO strISAPIData = new NET_EHOME_ALARM_ISAPI_INFO();
                    strISAPIData.write();
                    Pointer pISAPIInfo = strISAPIData.getPointer();
                    pISAPIInfo.write(0, pAlarmMsg.pAlarmInfo.getByteArray(0, strISAPIData.size()), 0, strISAPIData.size());
                    strISAPIData.read();
                    if (strISAPIData.dwAlarmDataLen != 0)//Json或者XML数据
                    {
                        BYTE_ARRAY m_strISAPIData = new BYTE_ARRAY(strISAPIData.dwAlarmDataLen);
                        m_strISAPIData.write();
                        Pointer pPlateInfo = m_strISAPIData.getPointer();
                        pPlateInfo.write(0, strISAPIData.pAlarmData.getByteArray(0, m_strISAPIData.size()), 0, m_strISAPIData.size());
                        m_strISAPIData.read();
                        // 0-invalid,1-xml,2-json
                        String dataType = String.valueOf(strISAPIData.byDataType).trim();
                        // log.info("ISAPI报警数据类型: {}", dataType);
                        String data = new String(m_strISAPIData.byValue).trim();
                        // log.info(new String(m_strISAPIData.byValue).trim());
                        UploadData uploadData = new UploadData();
                        ObjectMapper mapper = new ObjectMapper();
                        if (StringUtils.equals("1", dataType)) {
                            try {
                                log.info("ISAPI报警XML数据: {}", data);
                                EventNotificationAlert eventNotificationAlert = XmlUtil.fromXml(data, EventNotificationAlert.class);
                                if (eventNotificationAlert != null) {
                                    String jsonString = mapper.writeValueAsString(eventNotificationAlert);
                                    uploadData.setDataType("ANPR");
                                    uploadData.setData(jsonString);
                                    log.info("eventNotificationAlert 事件消息：{}", uploadData);
                                }
                            } catch (Exception e) {
                                // 或者使用日志记录
                                log.error("解析设备事件失败: {}\n 消息内容： {}", e.getMessage(), data);
                            }
                        } else if (StringUtils.equals("2", dataType)) {
                            try {
                                DeviceEventBase event = DeviceEventParser.parse(data);
                                if (event instanceof FaceCaptureEvent) {
                                    FaceCaptureEvent faceEvent = (FaceCaptureEvent) event;
                                    uploadData.setDataType("FaceCapture");
                                    // 序列化为 JSON 字符串
                                    String jsonString = mapper.writeValueAsString(faceEvent);
                                    log.info("检测到人脸抓拍：{}", jsonString);
                                    uploadData.setData(jsonString);
                                } else if (event instanceof GPSUploadEvent) {
                                    GPSUploadEvent gpsEvent = (GPSUploadEvent) event;
                                    log.info("GPS 上报：{}", gpsEvent.getGps());
                                    uploadData.setDataType("GPS");
                                    // 序列化为 JSON 字符串
                                    String jsonString = mapper.writeValueAsString(gpsEvent);
                                    uploadData.setData(jsonString);
                                } else if (event instanceof AlarmResultEvent) {
                                    AlarmResultEvent alarmResultEvent = (AlarmResultEvent) event;
                                    uploadData.setDataType("AlarmResult");
                                    // 序列化为 JSON 字符串
                                    String jsonString = mapper.writeValueAsString(alarmResultEvent);
                                    log.info("AlarmResultEvent 上报：{}", jsonString);
                                    uploadData.setData(jsonString);
//                                    for (AlarmResult alarmResult : alarmResultEvent.getAlarmResult()) {
//                                        for (AlarmFace fase : alarmResult.getFaces()) {
//                                            for (AlarmIdentify id : fase.getIdentify()) {
//                                                for (Candidate candidate : id.getCandidate()) {
//                                                    guardVerificationService.faceResult(candidate.getReserve_field().getCertificateNumber());
//                                                }
//                                            }
//                                        }
//                                    }
                                } else {
                                    log.info("未知事件类型：{}", event.getEventType());
                                    String jsonString = mapper.writeValueAsString(event);
                                    uploadData.setDataType(jsonString);
                                }
                            } catch (Exception e) {
                                // 或者使用日志记录
                                log.error("解析设备事件失败: {}\n 消息内容： {}", e.getMessage(), data);
                            }
                        }
                    }
                }
                break;
            }
        }

//        AlarmEventHandle.processAlarmData(pAlarmMsg.dwAlarmType,
//                pAlarmMsg.pAlarmInfo, pAlarmMsg.dwAlarmInfoLen,
//                pAlarmMsg.pXmlBuf, pAlarmMsg.dwXmlBufLen,
//                pAlarmMsg.pHttpUrl, pAlarmMsg.dwHttpUrlLen);

        return true;
    }
}
