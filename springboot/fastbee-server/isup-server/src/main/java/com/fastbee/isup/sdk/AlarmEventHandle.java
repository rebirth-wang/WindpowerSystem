package com.fastbee.isup.sdk;

import static com.fastbee.isup.sdk.service.constant.EHOME_ALARM_TYPE.*;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

import com.fastbee.isup.sdk.structure.*;
import com.fastbee.isup.util.CommonMethod;

public class AlarmEventHandle {
    public static void processAlarmData(int dwAlarmType, Pointer pStru, int dwStruLen, Pointer pXml, int dwXmlLen, Pointer pUrl, int dwUrlLen) {
        if (pUrl != Pointer.NULL) {
            dwAlarmType = EHOME_ISAPI_ALARM;
        }

        switch (dwAlarmType) {
            // Ehome基本报警
            case EHOME_ALARM: {
                processEhomeAlarm(dwAlarmType, pStru, dwStruLen, pXml, dwXmlLen);
                break;
            }
            // 热度图报告
            case EHOME_ALARM_HEATMAP_REPORT: {
                processEhomeAlarmHeatMapReport(pStru, dwStruLen, pXml, dwXmlLen);
                break;
            }
            // 图片抓拍报告
            case EHOME_ALARM_FACESNAP_REPORT: {
                processEhomeFaceSnapReport(pStru, dwStruLen, pXml, dwXmlLen);
                break;
            }
            // GPS信息上传
            case EHOME_ALARM_GPS: {
                processEhomeGps(pStru, dwStruLen, pXml, dwXmlLen);
                break;
            }
            // 报警主机CID告警上传
            case EHOME_ALARM_CID_REPORT: {
                processEhomeCIDReport(pStru, dwStruLen, pXml, dwXmlLen);
                break;
            }
            // 图片URL上报
            case EHOME_ALARM_NOTICE_PICURL: {
                processEhomeAlarmNoticPicUrl(pStru, dwStruLen, pXml, dwXmlLen);
                break;
            }
            // 异步失败通知
            case EHOME_ALARM_NOTIFY_FAIL: {
                processEhomeNotifyFail(pStru, dwStruLen, pXml, dwXmlLen);
                break;
            }
            // 门禁事件上报
            case EHOME_ALARM_ACS: {
                processEhomeAlarmAcs(pStru, dwStruLen);
                break;
            }
            // 无线网络信息上传
            case EHOME_ALARM_WIRELESS_INFO: {
                processAlarmWirelessInfo(pStru, dwStruLen, pXml, dwXmlLen);
                break;
            }
            // ISAPI报警上传
            case EHOME_ISAPI_ALARM: {
                processEhomeIsapiAlarm(pStru, dwStruLen, pUrl, dwUrlLen);
                break;
            }
            // 车载设备的客流数据
            case EHOME_ALARM_MPDCDATA: {
                processEhomeAlarmMpdcData(pStru, dwStruLen, pXml, dwXmlLen);
                break;
            }
            // 二维码报警上传
            case EHOME_ALARM_QRCODE: {
                processEhomeAlarmQrcode(pXml, dwXmlLen);
                break;
            }
            // 人脸测温报警上传
            case EHOME_ALARM_FACETEMP: {
                processEhomeAlarmFaceTemp(pXml, dwXmlLen);
                break;
            }
            default: {
                System.out.println("unknown_Alarm_type: " + dwAlarmType);
            }
        }
    }

    /**
     * Ehome基本报警
     *
     * @param pStru
     * @param dwStruLen
     * @param pXml
     * @param dwXmlLen
     */
    public static void processEhomeAlarm(int alarmType, Pointer pStru, int dwStruLen, Pointer pXml, int dwXmlLen) {
        NET_EHOME_ALARM_INFO ehomeAlarmInfo = new NET_EHOME_ALARM_INFO();
        ehomeAlarmInfo.write();
        Pointer pEhomeAlarmInfo = ehomeAlarmInfo.getPointer();
        pEhomeAlarmInfo.write(0, pStru.getByteArray(0, ehomeAlarmInfo.size()), 0, ehomeAlarmInfo.size());
        ehomeAlarmInfo.read();

        StringBuffer bf = new StringBuffer();
        bf.append("[ALARM]DeviceID:").append(Native.toString(ehomeAlarmInfo.szDeviceID))
                .append(",\nTime:").append(Native.toString(ehomeAlarmInfo.szAlarmTime))
                .append(",\nType:").append(ehomeAlarmInfo.dwAlarmType)
                .append(",\nAction:").append(ehomeAlarmInfo.dwAlarmAction)
                .append(",\nChannel:").append(ehomeAlarmInfo.dwVideoChannel)
                .append(",\nAlarmIn:").append(ehomeAlarmInfo.dwAlarmInChannel)
                .append(",\nDiskNo:").append(ehomeAlarmInfo.dwDiskNumber);

        switch (alarmType) {
            case ALARM_TYPE_DEV_CHANGED_STATUS: {
                bf.append("\n[ALARM_TYPE_DEV_CHANGED_STATUS]byDeviceStatus:").append(ehomeAlarmInfo.uStatusUnion.struDevStatusChanged.byDeviceStatus);
                break;
            }
            case ALARM_TYPE_CHAN_CHANGED_STATUS: {
                bf.append("\n[ALARM_TYPE_CHAN_CHANGED_STATUS]byChanStatus:").append(ehomeAlarmInfo.uStatusUnion.struChanStatusChanged.byChanStatus).append(",wChanNO:").append(ehomeAlarmInfo.uStatusUnion.struChanStatusChanged.wChanNO);
                break;
            }
            case ALARM_TYPE_HD_CHANGED_STATUS: {
                bf.append("\n[ALARM_TYPE_HD_CHANGED_STATUS]byHDStatus:").append(ehomeAlarmInfo.uStatusUnion.struHdStatusChanged.byHDStatus).append(",wHDNo:").append(ehomeAlarmInfo.uStatusUnion.struHdStatusChanged.wHDNo).append(",dwVolume:").append(ehomeAlarmInfo.uStatusUnion.struHdStatusChanged.dwVolume);
                break;
            }
            case ALARM_TYPE_DEV_TIMING_STATUS: {
                bf.append("\n[ALARM_TYPE_DEV_TIMING_STATUS]byCPUUsage:%d").append(ehomeAlarmInfo.uStatusUnion.struDevTimeStatus.byCPUUsage).append(",byMainFrameTemp:%d").append(ehomeAlarmInfo.uStatusUnion.struDevTimeStatus.byMainFrameTemp).append(",byBackPanelTemp:%d").append(ehomeAlarmInfo.uStatusUnion.struDevTimeStatus.byBackPanelTemp).append(",dwMemoryTotal:%d").append(ehomeAlarmInfo.uStatusUnion.struDevTimeStatus.dwMemoryTotal).append(",dwMemoryUsage:%d").append(ehomeAlarmInfo.uStatusUnion.struDevTimeStatus.dwMemoryUsage);
                break;
            }
            case ALARM_TYPE_CHAN_TIMING_STATUS: {
                bf.append("\n[ALARM_TYPE_CHAN_TIMING_STATUS]byLinkNum:%d").append(ehomeAlarmInfo.uStatusUnion.struChanTimeStatus.byLinkNum).append(",wChanNO:%d").append(ehomeAlarmInfo.uStatusUnion.struChanTimeStatus.wChanNO).append(",dwBitRate:%d").append(ehomeAlarmInfo.uStatusUnion.struChanTimeStatus.dwBitRate);
                break;
            }
            case ALARM_TYPE_HD_TIMING_STATUS: {
                bf.append("\n[ALARM_TYPE_HD_TIMING_STATUS]wHDNo:%d").append(ehomeAlarmInfo.uStatusUnion.struHdTimeStatus.wHDNo).append(",dwHDFreeSpace:%d").append(ehomeAlarmInfo.uStatusUnion.struHdTimeStatus.dwHDFreeSpace);
                break;
            }
            default: {
                break;
            }
        }

        handleAlarmInfo(EHOME_ALARM, bf.toString());
    }

    /**
     * 热度图报告
     *
     * @param pStru
     * @param dwStruLen
     * @param pXml
     * @param dwXmlLen
     */
    public static void processEhomeAlarmHeatMapReport(Pointer pStru, int dwStruLen, Pointer pXml, int dwXmlLen) {
        if (pStru == Pointer.NULL) {
            return;
        }
        NET_EHOME_HEATMAP_REPORT struHeatMapReport = new NET_EHOME_HEATMAP_REPORT();
        struHeatMapReport.write();
        Pointer pStruHeatMapReport = struHeatMapReport.getPointer();
        pStruHeatMapReport.write(0, pStru.getByteArray(0, struHeatMapReport.size()), 0, struHeatMapReport.size());
        struHeatMapReport.read();

        String info = "[HEATMAPREPORT]DeviceID: " + Native.toString(struHeatMapReport.byDeviceID) +
                ",\nChannel: " + struHeatMapReport.dwVideoChannel +
                ",\nStart: " + Native.toString(struHeatMapReport.byStartTime) +
                ",\nStop: " + Native.toString(struHeatMapReport.byStopTime) +
                ",\nHeatMapValue: " + struHeatMapReport.struHeatmapValue.dwMaxHeatMapValue
                + "  " + struHeatMapReport.struHeatmapValue.dwMinHeatMapValue
                + "  " + struHeatMapReport.struHeatmapValue.dwTimeHeatMapValue +
                ",\nSize: " + struHeatMapReport.struPixelArraySize.dwLineValue
                + "  " + struHeatMapReport.struPixelArraySize.dwColumnValue;

        handleAlarmInfo(EHOME_ALARM_HEATMAP_REPORT, info);
    }


    /**
     * 图片抓拍报告
     *
     * @param pStru
     * @param dwStruLen
     * @param pXml
     * @param dwXmlLen
     */
    public static void processEhomeFaceSnapReport(Pointer pStru, int dwStruLen, Pointer pXml, int dwXmlLen) {
        if (pStru == Pointer.NULL) {
            return;
        }
        NET_EHOME_FACESNAP_REPORT struFaceSnapReport = new NET_EHOME_FACESNAP_REPORT();
        struFaceSnapReport.write();
        Pointer pStruFaceSnapReport = struFaceSnapReport.getPointer();
        pStruFaceSnapReport.write(0, pStru.getByteArray(0, struFaceSnapReport.size()), 0, struFaceSnapReport.size());
        struFaceSnapReport.read();

        StringBuffer bf = new StringBuffer();

        bf.append("[FACESNAPREPORT]DeviceID: ").append(Native.toString(struFaceSnapReport.byDeviceID)).append(",\nChannel:").append(struFaceSnapReport.dwVideoChannel).append(",\nTime:").append(Native.toString(struFaceSnapReport.byAlarmTime)).append(",\nPicID:").append(struFaceSnapReport.dwFacePicID).append(",\nScore:").append(struFaceSnapReport.dwFaceScore).append(",\nTargetID:").append(struFaceSnapReport.dwTargetID).append(",\nTarget Zone[").append(" ").append(struFaceSnapReport.struTarketZone.dwX).append(" ").append(struFaceSnapReport.struTarketZone.dwY).append(" ").append(struFaceSnapReport.struTarketZone.dwWidth).append(" ").append(struFaceSnapReport.struTarketZone.dwHeight).append("]").append(",\nFacePicZone[").append(" ").append(struFaceSnapReport.struFacePicZone.dwX).append(" ").append(struFaceSnapReport.struFacePicZone.dwY).append(" ").append(struFaceSnapReport.struFacePicZone.dwWidth).append(" ").append(struFaceSnapReport.struFacePicZone.dwHeight).append("]").append(",\nHumanFeature:[").append(" ").append(struFaceSnapReport.struHumanFeature.byAgeGroup).append(" ").append(struFaceSnapReport.struHumanFeature.bySex).append(" ").append(struFaceSnapReport.struHumanFeature.byEyeGlass).append(" ").append(struFaceSnapReport.struHumanFeature.byMask).append(" ").append("]").append(",\nDuration:").append(struFaceSnapReport.dwStayDuration).append(",\nFacePicLen:").append(struFaceSnapReport.dwFacePicLen).append(",\nBackGroundPicLen:").append(struFaceSnapReport.dwBackgroudPicLen);

        handleAlarmInfo(EHOME_ALARM_FACESNAP_REPORT, bf.toString());
    }

    /**
     * GPS信息上传
     *
     * @param pStru
     * @param dwStruLen
     * @param pXml
     * @param dwXmlLen
     */
    public static void processEhomeGps(Pointer pStru, int dwStruLen, Pointer pXml, int dwXmlLen) {
        if (pStru == Pointer.NULL) {
            return;
        }
        NET_EHOME_GPS_INFO struGps = new NET_EHOME_GPS_INFO();
        struGps.write();
        Pointer pStr = struGps.getPointer();
        pStr.write(0, pStru.getByteArray(0, struGps.size()), 0, struGps.size());
        struGps.read();

        StringBuffer bf = new StringBuffer();
        bf.append("[GPS]DeviceID:").append(Native.toString(struGps.byDeviceID)).append(",\nSampleTime:").append(Native.toString(struGps.bySampleTime)).append(",\nDivision:[").append(struGps.byDivision[0]).append(" ").append(struGps.byDivision[1]).append("]").append(",\nSatelites:").append(struGps.bySatelites).append(",\nPrecision:").append(struGps.byPrecision).append(",\nLongitude:").append(struGps.dwLongitude).append(",\nLatitude:").append(struGps.dwLatitude).append(",\nDirection:").append(struGps.dwDirection).append(",\nSpeed:").append(struGps.dwSpeed).append(",\nHeight:").append(struGps.dwHeight);
        handleAlarmInfo(EHOME_ALARM_GPS, bf.toString());
    }

    /**
     * 报警主机CID告警上传
     *
     * @param pStru
     * @param dwStruLen
     * @param pXml
     * @param dwXmlLen
     */
    public static void processEhomeCIDReport(Pointer pStru, int dwStruLen, Pointer pXml, int dwXmlLen) {
        if (pStru == Pointer.NULL) {
            return;
        }
        NET_EHOME_CID_INFO strCidInfo = new NET_EHOME_CID_INFO();
        NET_EHOME_CID_INFO_INTERNAL_EX strCidInfoEx = new NET_EHOME_CID_INFO_INTERNAL_EX();
        NET_EHOME_CID_INFO_PICTUREINFO_EX strPicInfoEx = new NET_EHOME_CID_INFO_PICTUREINFO_EX();

        strCidInfo.write();
        Pointer pStrCidInfo = strCidInfo.getPointer();
        pStrCidInfo.write(0, pStru.getByteArray(0, strCidInfo.size()), 0, strCidInfo.size());
        strCidInfo.read();

        strCidInfoEx.write();
        Pointer pStrCidInfoEx = strCidInfoEx.getPointer();
        pStrCidInfoEx.write(0, strCidInfo.pCidInfoEx.getByteArray(0, strCidInfoEx.size()), 0, strCidInfoEx.size());
        strCidInfoEx.read();

        strPicInfoEx.write();
        Pointer pStrPicInfoEx = strPicInfoEx.getPointer();
        pStrPicInfoEx.write(0, strCidInfo.pPicInfoEx.getByteArray(0, strPicInfoEx.size()), 0, strPicInfoEx.size());
        strPicInfoEx.read();

        String cDescribe = Native.toString(strCidInfo.byCIDDescribe);
        String uuid = null;
        StringBuffer bf = new StringBuffer();
        bf.append("\ncDescribe: ").append(cDescribe);

        // 有拓展字段则处理拓展字段信息
        if (strCidInfo.byExtend == 1) {
            cDescribe = Native.toString(strCidInfoEx.byCIDDescribeEx);
            uuid = Native.toString(strCidInfoEx.byUUID);
            bf.append("\n[CID_EX]uuid[").append(Native.toString(strCidInfoEx.byUUID)).append("]").append(",\nrecheck[").append(strCidInfoEx.byRecheck).append("]").append(",\nRecheck URL[").append(Native.toString(strCidInfoEx.byVideoURL)).append("]").append(",\nvideoType[").append(Native.toString(strCidInfoEx.byVideoType)).append("]");
            for (int i = 0; i < MAX_PICTURE_NUM; i++) {
                if ((strCidInfoEx.byRecheck == 1) && (strPicInfoEx.byPictureURL[i][0]) != '\0') {
                    bf.append("\n[CID_EX]uuid[").append(Native.toString(strCidInfoEx.byUUID)).append("]").append(",\nrecheck[").append(strCidInfoEx.byRecheck).append("]").append(",\nPicURL[").append(Native.toString(strPicInfoEx.byPictureURL[i])).append("]");
                }
            }

            StringBuilder LinkedSubSystem = new StringBuilder();
            for (int i = 0; i < MAX_SUBSYSTEM_LEN; i++) {
                if (strCidInfoEx.byLinkageSubSystem[i] > 0)//关联子系统最小值为1
                {
                    LinkedSubSystem.append(strCidInfoEx.byLinkageSubSystem[i]);
                }
            }
            bf.append("\n[CID_EX] All Linked SubSystem: ").append(LinkedSubSystem);
        }

        bf.append("\n[CID]uuid[%s]").append(uuid).append(",\nDeviceID:%s").append(Native.toString(strCidInfo.byDeviceID)).append(",\nCID code:%d").append(strCidInfo.dwCIDCode).append(",\nCID type:%d").append(strCidInfo.dwCIDType).append(",\nSubsys No:%d").append(strCidInfo.dwSubSysNo).append(",\nDescribe:%s").append(cDescribe).append(",\nTriggerTime:%s").append(Native.toString(strCidInfo.byTriggerTime)).append(",\nUploadTime:%s").append(Native.toString(strCidInfo.byUploadTime)).append(",\nCID param[").append("  ").append(strCidInfo.struCIDParam.dwUserType).append("  ").append(strCidInfo.struCIDParam.lUserNo).append("  ").append(strCidInfo.struCIDParam.lZoneNo).append("  ").append(strCidInfo.struCIDParam.lKeyboardNo).append("  ").append(strCidInfo.struCIDParam.lVideoChanNo).append("  ").append(strCidInfo.struCIDParam.lDiskNo).append("  ").append(strCidInfo.struCIDParam.lModuleAddr).append(
                //UTF-8转GBK
                "  ").append(CommonMethod.UTF8toGBKStr(strCidInfo.struCIDParam.byUserName)).append("]");

        handleAlarmInfo(EHOME_ALARM_CID_REPORT, bf.toString());
    }

    /**
     * 图片URL上报
     *
     * @param pStru
     * @param dwStruLen
     * @param pXml
     * @param dwXmlLen
     */
    public static void processEhomeAlarmNoticPicUrl(Pointer pStru, int dwStruLen, Pointer pXml, int dwXmlLen) {
        if (pStru == Pointer.NULL) {
            return;
        }

        NET_EHOME_NOTICE_PICURL pStruNoticePicUrl = new NET_EHOME_NOTICE_PICURL();
        pStruNoticePicUrl.write();
        Pointer pointer = pStruNoticePicUrl.getPointer();
        pointer.write(0, pStru.getByteArray(0, pStruNoticePicUrl.size()), 0, pStruNoticePicUrl.size());
        pStruNoticePicUrl.read();

        StringBuffer bf = new StringBuffer();
        bf.append("[NOTICEPICURL]DeviceID: ").append(Native.toString(pStruNoticePicUrl.byDeviceID)).append(",\nPicType: ").append(pStruNoticePicUrl.wPicType).append(",\nAlarmType: ").append(pStruNoticePicUrl.wAlarmType).append(",\nAlarmChan: ").append(pStruNoticePicUrl.dwAlarmChan).append(",\nAlarmTime: ").append(Native.toString(pStruNoticePicUrl.byAlarmTime)).append(",\nCaptureChan: ").append(pStruNoticePicUrl.dwCaptureChan).append(",\nPicTime: ").append(Native.toString(pStruNoticePicUrl.byPicTime)).append(",\nURL: ").append(Native.toString(pStruNoticePicUrl.byPicUrl)).append(",\nManualSeq: ").append(pStruNoticePicUrl.dwManualSnapSeq);

        handleAlarmInfo(EHOME_ALARM_NOTICE_PICURL, bf.toString());
    }

    /**
     * 异步失败通知
     *
     * @param pStru
     * @param dwStruLen
     * @param pXml
     * @param dwXmlLen
     */
    public static void processEhomeNotifyFail(Pointer pStru, int dwStruLen, Pointer pXml, int dwXmlLen) {
        if (pStru == Pointer.NULL) {
            return;
        }
        NET_EHOME_NOTIFY_FAIL_INFO strucInfo = new NET_EHOME_NOTIFY_FAIL_INFO();
        strucInfo.write();
        Pointer pointer = strucInfo.getPointer();
        pointer.write(0, pStru.getByteArray(0, strucInfo.size()), 0, strucInfo.size());
        strucInfo.read();

        StringBuffer sb = new StringBuffer();
        sb.append("[NOTIFYFAIL]DeviceID: ").append(Native.toString(strucInfo.byDeviceID)).append(",\nFailedCommand: ").append(strucInfo.wFailedCommand).append(",\nPicType: ").append(strucInfo.wPicType).append(",\nManualSeq: ").append(strucInfo.dwManualSnapSeq);

        handleAlarmInfo(EHOME_ALARM_NOTIFY_FAIL, sb.toString());
    }

    /**
     * 门禁事件上报
     *
     * @param pStru
     * @param dwStruLen
     */
    public static void processEhomeAlarmAcs(Pointer pStru, int dwStruLen) {
        if (pStru == Pointer.NULL || dwStruLen == 0) {
            return;
        }

        BYTE_ARRAY strXMLData = new BYTE_ARRAY(dwStruLen);
        strXMLData.write();
        Pointer pPlateInfo = strXMLData.getPointer();
        pPlateInfo.write(0, pStru.getByteArray(0, strXMLData.size()), 0, strXMLData.size());
        strXMLData.read();
        String strXML = new String(strXMLData.byValue).trim();

        handleAlarmInfo(EHOME_ALARM_ACS, strXML);
    }

    /**
     * 无线网络信息上传
     *
     * @param pStru
     * @param dwStruLen
     * @param pXml
     * @param dwXmlLen
     */
    public static void processAlarmWirelessInfo(Pointer pStru, int dwStruLen, Pointer pXml, int dwXmlLen) {
        NET_EHOME_ALARMWIRELESSINFO strucInfo = new NET_EHOME_ALARMWIRELESSINFO();
        strucInfo.write();
        Pointer pointer = strucInfo.getPointer();
        pointer.write(0, pStru.getByteArray(0, strucInfo.size()), 0, strucInfo.size());
        strucInfo.read();

        StringBuffer sb = new StringBuffer();
        sb.append("[Wireless]DeviceID: ").append(Native.toString(strucInfo.byDeviceID)).append(",\nDataTraffic: ").append(((float) strucInfo.dwDataTraffic) / 100).append(",\nSignalIntensity:").append(strucInfo.bySignalIntensity);

        handleAlarmInfo(EHOME_ALARM_WIRELESS_INFO, sb.toString());
    }

    /**
     * ISAPI报警上传
     *
     * @param pStru
     * @param dwStruLen
     * @param pUrl
     * @param dwUrlLen
     */
    public static void processEhomeIsapiAlarm(Pointer pStru, int dwStruLen, Pointer pUrl, int dwUrlLen) {
        if (pUrl != Pointer.NULL && dwUrlLen > 0) {
            // ISUP4.0事件上报，默认报警与图片数据不分离，此时返回的url不为空，这种情况增加下demo打印
            // 分离后的数据，url字段不为空
            System.out.println("ISAPI报警上传, ISAPI Alarm");
            return;
        }

        if (pStru == Pointer.NULL) {
            return;
        }

        NET_EHOME_ALARM_ISAPI_INFO strISAPIAlarm = new NET_EHOME_ALARM_ISAPI_INFO();
        strISAPIAlarm.write();
        Pointer pISAPIAlarm = strISAPIAlarm.getPointer();
        pISAPIAlarm.write(0, pStru.getByteArray(0, strISAPIAlarm.size()), 0, strISAPIAlarm.size());
        strISAPIAlarm.read();

        if (strISAPIAlarm.pAlarmData != Pointer.NULL) {
            String alarmData = null;
            // 判断报警数据的格式
            if (strISAPIAlarm.byDataType != 0) { // 1: xml格式数据 2：json格式数据
                BYTE_ARRAY m_strISAPIData = new BYTE_ARRAY(strISAPIAlarm.dwAlarmDataLen);
                m_strISAPIData.write();
                Pointer pPlateInfo = m_strISAPIData.getPointer();
                pPlateInfo.write(0, strISAPIAlarm.pAlarmData.getByteArray(0, m_strISAPIData.size()), 0, m_strISAPIData.size());
                m_strISAPIData.read();

                alarmData = new String(m_strISAPIData.byValue).trim();

                handleAlarmInfo(EHOME_ISAPI_ALARM, alarmData);
            }
        }
    }

    /**
     * 车载设备的客流数据
     *
     * @param pStru
     * @param dwStruLen
     * @param pXml
     * @param dwXmlLen
     */
    public static void processEhomeAlarmMpdcData(Pointer pStru, int dwStruLen, Pointer pXml, int dwXmlLen) {
        NET_EHOME_ALARM_MPDCDATA structure = new NET_EHOME_ALARM_MPDCDATA();
        structure.write();
        Pointer pointer = structure.getPointer();
        pointer.write(0, pStru.getByteArray(0, structure.size()), 0, structure.size());
        structure.read();

        StringBuffer sb = new StringBuffer();
        sb.append("[MPDCData]DeviceID:").append(Native.toString(structure.byDeviceID)).append(",\nSampleTime: ").append(Native.toString(structure.bySampleTime)).append(",\nRetranseFlag: ").append(structure.byRetranseFlag).append(",\nCount: ").append(structure.struMPData.dwCount);
        handleAlarmInfo(EHOME_ALARM_MPDCDATA, sb.toString());
    }

    /**
     * 二维码报警上传
     *
     * @param pXml
     * @param dwXmlLen
     */
    public static void processEhomeAlarmQrcode(Pointer pXml, int dwXmlLen) {
        if (pXml == Pointer.NULL) {
            return;
        }
        BYTE_ARRAY strXMLData = new BYTE_ARRAY(dwXmlLen);
        strXMLData.write();
        Pointer pPlateInfo = strXMLData.getPointer();
        pPlateInfo.write(0, pXml.getByteArray(0, strXMLData.size()), 0, strXMLData.size());
        strXMLData.read();

        String strXML = new String(strXMLData.byValue).trim();
        handleAlarmInfo(EHOME_ALARM_QRCODE, strXML);
    }

    /**
     * 人脸测温报警上传
     *
     * @param pXml
     * @param dwXmlLen
     */
    public static void processEhomeAlarmFaceTemp(Pointer pXml, int dwXmlLen) {
        if (pXml == Pointer.NULL || dwXmlLen == 0) {
            return;
        }

        BYTE_ARRAY strXMLData = new BYTE_ARRAY(dwXmlLen);
        strXMLData.write();
        Pointer pPlateInfo = strXMLData.getPointer();
        pPlateInfo.write(0, pXml.getByteArray(0, strXMLData.size()), 0, strXMLData.size());
        strXMLData.read();

        String strXML = new String(strXMLData.byValue).trim();
        handleAlarmInfo(EHOME_ALARM_FACETEMP, strXML);
    }

    /**
     * 处理告警信息（输出到文件或者是输出到控制台）
     *
     * @param alarmType
     * @param info
     */
    private static void handleAlarmInfo(int alarmType, String info) {
        // 输出事件信息到文件中
        CommonMethod.outputToFile("dwAlarmType_" + alarmType, ".txt", info);
        // 输出事件信息到控制台上
        System.out.println("打印到控制台: " + info);
    }
}
