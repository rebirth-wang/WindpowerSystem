package com.fastbee.isup.sdk.acs;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fastbee.common.utils.spring.SpringUtils;
import com.fastbee.isup.sdk.service.HCISUPCMS;
import com.fastbee.isup.sdk.structure.BYTE_ARRAY;
import com.fastbee.isup.sdk.structure.NET_EHOME_PTXML_PARAM;
import com.fastbee.isup.util.ConfigFileUtil;

/**
 * 门禁 事件查询模块示例
 */
public class AcsSearchEvent {
    private static HCISUPCMS hCEhomeCMS = SpringUtils.getBean(HCISUPCMS.class);

    /**
     * 查询门禁历史事件，指定查询时间范围和门禁事件类型
     *
     * @param loginID 登录设备句柄ID
     * @throws UnsupportedEncodingException
     */
    public static void searchAcsEventInfo(int loginID) throws UnsupportedEncodingException {

        NET_EHOME_PTXML_PARAM m_struParam2 = new NET_EHOME_PTXML_PARAM();
        m_struParam2.read();

        //透传URL，不同功能对应不同的URL，完整协议报文说明需要参考ISAPI协议文档
        String urlInBuffer = "POST /ISAPI/AccessControl/AcsEvent?format=json"; //查询门禁事件URL
        BYTE_ARRAY ptrurlInBuffer = new BYTE_ARRAY(urlInBuffer.length() + 1);
        System.arraycopy(urlInBuffer.getBytes(), 0, ptrurlInBuffer.byValue, 0, urlInBuffer.length());
        ptrurlInBuffer.write();
        m_struParam2.pRequestUrl = ptrurlInBuffer.getPointer();
        m_struParam2.dwRequestUrlLen = urlInBuffer.length();

        UUID uuid = UUID.randomUUID();
        //输入参数，XML或者JSON数据,
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("searchID", uuid);
        parameter.put("startTime", "2023-08-19T00:00:00+08:00");//查询开始时间
        parameter.put("endTime", "2023-08-19T23:59:59+08:00");//查询截止时间
        String input = ConfigFileUtil.getReqBodyFromTemplate("conf/acs/SearchAcsEventInfo.json", parameter);
        System.out.println(input);

        byte[] byInbuffer = input.getBytes(StandardCharsets.UTF_8);
        int iInBufLen = byInbuffer.length;
        BYTE_ARRAY ptrInBuffer = new BYTE_ARRAY(iInBufLen);
        ptrInBuffer.read();
        System.arraycopy(byInbuffer, 0, ptrInBuffer.byValue, 0, iInBufLen);
        ptrInBuffer.write();
        m_struParam2.pInBuffer = ptrInBuffer.getPointer();
        m_struParam2.dwInSize = iInBufLen;
        //输出参数，分配的内存用于存储返回的数据，需要大于等于实际内容大小
        int iOutSize2 = 2 * 1024 * 1024;
        BYTE_ARRAY ptrOutByte2 = new BYTE_ARRAY(iOutSize2);
        m_struParam2.pOutBuffer = ptrOutByte2.getPointer();
        m_struParam2.dwOutSize = iOutSize2;
        m_struParam2.dwRecvTimeOut = 5000; //接收超时时间，单位毫秒
        m_struParam2.write();

        if (!hCEhomeCMS.NET_ECMS_ISAPIPassThrough(loginID, m_struParam2)) {
            System.out.println("查询门禁历史事件失败，NET_ECMS_ISAPIPassThrough failed,error：" + hCEhomeCMS.NET_ECMS_GetLastError());
            return;
        } else {
            m_struParam2.read();
            ptrOutByte2.read();
            System.out.println("NET_ECMS_ISAPIPassThrough succeed\n" + "输出报文:" + new String(ptrOutByte2.byValue).trim());
        }
        return;


    }

    /**
     * 查询门禁事件总条数，指定查询时间范围和门禁事件类型
     *
     * @param loginID 登录设备句柄ID
     * @throws UnsupportedEncodingException
     */
    public static void SearchAcsEventTotalNum(int loginID) throws UnsupportedEncodingException {
        NET_EHOME_PTXML_PARAM m_struParam2 = new NET_EHOME_PTXML_PARAM();
        m_struParam2.read();

        //透传URL，不同功能对应不同的URL，完整协议报文说明需要参考ISAPI协议文档
        String urlInBuffer = "POST /ISAPI/AccessControl/AcsEventTotalNum?format=json"; //查询门禁事件总条数URL
        BYTE_ARRAY ptrurlInBuffer = new BYTE_ARRAY(urlInBuffer.length() + 1);
        System.arraycopy(urlInBuffer.getBytes(), 0, ptrurlInBuffer.byValue, 0, urlInBuffer.length());
        ptrurlInBuffer.write();
        m_struParam2.pRequestUrl = ptrurlInBuffer.getPointer();
        m_struParam2.dwRequestUrlLen = urlInBuffer.length();

        //输入参数，XML或者JSON数据,
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("startTime", "2023-08-19T00:00:00+08:00");//查询开始时间
        parameter.put("endTime", "2023-08-19T23:59:59+08:00");//查询截止时间
        String input = ConfigFileUtil.getReqBodyFromTemplate("conf/acs/AcsEventTotalNumParam.json", parameter);
        System.out.println(input);

        byte[] byInbuffer = input.getBytes(StandardCharsets.UTF_8);
        int iInBufLen = byInbuffer.length;
        BYTE_ARRAY ptrInBuffer = new BYTE_ARRAY(iInBufLen);
        ptrInBuffer.read();
        System.arraycopy(byInbuffer, 0, ptrInBuffer.byValue, 0, iInBufLen);
        ptrInBuffer.write();
        m_struParam2.pInBuffer = ptrInBuffer.getPointer();
        m_struParam2.dwInSize = iInBufLen;
        //输出参数，分配的内存用于存储返回的数据，需要大于等于实际内容大小
        int iOutSize2 = 2 * 1024 * 1024;
        BYTE_ARRAY ptrOutByte2 = new BYTE_ARRAY(iOutSize2);
        m_struParam2.pOutBuffer = ptrOutByte2.getPointer();
        m_struParam2.dwOutSize = iOutSize2;
        m_struParam2.dwRecvTimeOut = 5000; //接收超时时间，单位毫秒
        m_struParam2.write();

        if (!hCEhomeCMS.NET_ECMS_ISAPIPassThrough(loginID, m_struParam2)) {
            System.out.println("查询门禁事件总条数失败，NET_ECMS_ISAPIPassThrough failed,error：" + hCEhomeCMS.NET_ECMS_GetLastError());
            return;
        } else {
            m_struParam2.read();
            ptrOutByte2.read();
            System.out.println("NET_ECMS_ISAPIPassThrough succeed\n" + "输出报文:" + new String(ptrOutByte2.byValue).trim());
        }
        return;


    }

}
