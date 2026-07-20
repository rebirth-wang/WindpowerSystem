package com.fastbee.sip.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.sdp.SdpFactory;
import javax.sdp.SdpParseException;
import javax.sdp.SessionDescription;
import javax.sip.header.FromHeader;
import javax.sip.header.SubjectHeader;
import javax.sip.message.Request;

import gov.nist.javax.sip.address.AddressImpl;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.header.Subject;
import org.apache.commons.lang3.RandomStringUtils;

import com.fastbee.sip.model.GbSdp;

public class SipUtil {
    private static final String date_format_T = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String date_format = "yyyy-MM-dd HH:mm:ss";
    public static final Long DEFAULT_EXEC_TIMEOUT = 20000L;

    public final static String BUSINESS_GROUP = "215";
    public final static String VIRTUAL_ORGANIZATION = "216";
    public static final String TREETYPE_BUSINESS_GROUP = "BusinessGroup";
    public static final String TREETYPE_CIVIL_CODE = "CivilCode";

    public static final String PREFIX_PLAYRECORD = "gb_playrecord";
    public static final String PREFIX_PLAY = "gb_play";
    public static final String PREFIX_TALK = "gb_talk";
    
    public static String dateToISO8601(Date date) {
        SimpleDateFormat newsdf = new SimpleDateFormat(date_format_T, Locale.getDefault());
        return newsdf.format(date);
    }

    public static String timestampToISO8601(String formatTime) {
        Date date = new Date(Long.parseLong(formatTime) * 1000);
        SimpleDateFormat newsdf = new SimpleDateFormat(date_format_T, Locale.getDefault());
        return newsdf.format(date);
    }

    public static long ISO8601Totimestamp(String formatTime) {
        SimpleDateFormat oldsdf = new SimpleDateFormat(date_format_T, Locale.getDefault());
        try {
            return oldsdf.parse(formatTime).getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Date ISO8601ToDate(String formatTime) {
        SimpleDateFormat oldsdf = new SimpleDateFormat(date_format_T, Locale.getDefault());
        try {
            return oldsdf.parse(formatTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long DateStringToTimestamp(String formatTime) {
        SimpleDateFormat format = new SimpleDateFormat(date_format);
        //设置要读取的时间字符串格式
        Date date;
        try {
            date = format.parse(formatTime);
            //转换为Date类
            return date.getTime() / 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static Date timestampToDate(String formatTime) {
        return new Date(Long.parseLong(formatTime) * 1000);
    }

    public static String safeString(Object val) {
        if (val == null) {
            return "";
        }

        return val.toString();
    }
    public static String getUserIdFromFromHeader(Request request) {
        FromHeader fromHeader = (FromHeader)request.getHeader(FromHeader.NAME);
        return getUserIdFromFromHeader(fromHeader);
    }
    public static String getUserIdFromFromHeader(FromHeader fromHeader) {
        AddressImpl address = (AddressImpl)fromHeader.getAddress();
        SipUri uri = (SipUri) address.getURI();
        return uri.getUser();
    }


    public static String[] getChannelIdFromRequest(Request request) {
        SubjectHeader subject = (Subject)request.getHeader("subject");
        if (subject == null) {
            // 如果缺失subject
            return null;
        }
        String[] result = new String[2];
        String subjectStr = subject.getSubject();
        if (subjectStr.indexOf(",") > 0) {
            String[] subjectSplit = subjectStr.split(",");
            result[0] = subjectSplit[0].split(":")[0];
            result[1] = subjectSplit[1].split(":")[0];
        }else {
            result[0] = subjectStr.split(":")[0];
        }
        return result;
    }

    public static  String getNewViaTag() {
        return "z9hG4bK" + RandomStringUtils.randomNumeric(10);
    }

    public static String getNewFromTag(){
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String getNewTag(){
        return String.valueOf(System.currentTimeMillis());
    }

    public static GbSdp parseSDP(String sdpStr) throws SdpParseException {
        // jainSip不支持y= f=字段， 移除以解析。
        int ssrcIndex = sdpStr.indexOf("y=");
        int mediaDescriptionIndex = sdpStr.indexOf("f=");
        // 检查是否有y字段
        SessionDescription sdp;
        String ssrc = null;
        String mediaDescription = null;
        if (mediaDescriptionIndex == 0 && ssrcIndex == 0) {
            sdp = SdpFactory.getInstance().createSessionDescription(sdpStr);
        }else {
            String lines[] = sdpStr.split("\\r?\\n");
            StringBuilder sdpBuffer = new StringBuilder();
            for (String line : lines) {
                if (line.trim().startsWith("y=")) {
                    ssrc = line.substring(2);
                }else if (line.trim().startsWith("f=")) {
                    mediaDescription = line.substring(2);
                }else {
                    sdpBuffer.append(line.trim()).append("\r\n");
                }
            }
            sdp = SdpFactory.getInstance().createSessionDescription(sdpBuffer.toString());
        }
        return GbSdp.getInstance(sdp, ssrc, mediaDescription);
    }

    /**
     * 云台指令码计算
     *
     * @param leftRight  镜头左移右移 0:停止 1:左移 2:右移
     * @param upDown     镜头上移下移 0:停止 1:上移 2:下移
     * @param inOut      镜头放大缩小 0:停止 1:缩小 2:放大
     * @param moveSpeed  镜头移动速度 默认 0XFF (0-255)
     * @param zoomSpeed  镜头缩放速度 默认 0X1 (0-255)
     */
    public static String cmdString(int leftRight, int upDown, int inOut, int moveSpeed, int zoomSpeed) {
        int cmdCode = 0;
        if (leftRight == 2) {
            cmdCode|=0x01;		// 右移
        } else if(leftRight == 1) {
            cmdCode|=0x02;		// 左移
        }
        if (upDown == 2) {
            cmdCode|=0x04;		// 下移
        } else if(upDown == 1) {
            cmdCode|=0x08;		// 上移
        }
        if (inOut == 2) {
            cmdCode |= 0x10;	// 放大
        } else if(inOut == 1) {
            cmdCode |= 0x20;	// 缩小
        }
        StringBuilder builder = new StringBuilder("A50F01");
        String strTmp;
        strTmp = String.format("%02X", cmdCode);
        builder.append(strTmp, 0, 2);
        strTmp = String.format("%02X", moveSpeed);
        builder.append(strTmp, 0, 2);
        builder.append(strTmp, 0, 2);

        //优化zoom低倍速下的变倍速率
        if ((zoomSpeed > 0) && (zoomSpeed <16))
        {
            zoomSpeed = 16;
        }
        strTmp = String.format("%X", zoomSpeed);
        builder.append(strTmp, 0, 1).append("0");
        //计算校验码
        int checkCode = (0XA5 + 0X0F + 0X01 + cmdCode + moveSpeed + moveSpeed + (zoomSpeed /*<< 4*/ & 0XF0)) % 0X100;
        strTmp = String.format("%02X", checkCode);
        builder.append(strTmp, 0, 2);
        return builder.toString();
    }


}
