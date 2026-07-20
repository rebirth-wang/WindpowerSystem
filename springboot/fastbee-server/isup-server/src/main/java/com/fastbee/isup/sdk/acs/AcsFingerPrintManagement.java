package com.fastbee.isup.sdk.acs;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.fastbee.common.utils.spring.SpringUtils;
import com.fastbee.isup.sdk.service.HCISUPCMS;
import com.fastbee.isup.sdk.structure.BYTE_ARRAY;
import com.fastbee.isup.sdk.structure.NET_EHOME_PTXML_PARAM;
import com.fastbee.isup.util.ConfigFileUtil;

/**
 * 门禁 指纹管理模块示例
 */
@Service
@Profile("isup")
public class AcsFingerPrintManagement {
    private static HCISUPCMS hCEhomeCMS = SpringUtils.getBean(HCISUPCMS.class);

    /**
     * 下发指纹数据
     *
     * @param loginID    登录设备句柄ID
     * @param employeeNo 人员工号，指纹数据绑定在人员工号上
     * @throws UnsupportedEncodingException
     */
    public static void addFingerPrintInfo(int loginID, String employeeNo) throws UnsupportedEncodingException {

        NET_EHOME_PTXML_PARAM m_struParam2 = new NET_EHOME_PTXML_PARAM();
        m_struParam2.read();
        //透传URL，不同功能对应不同的URL，完整协议报文说明需要参考ISAPI协议文档
        String urlInBuffer = "POST /ISAPI/AccessControl/FingerPrintDownload?format=json"; //添加指纹URL
        BYTE_ARRAY ptrurlInBuffer = new BYTE_ARRAY(urlInBuffer.length() + 1);
        System.arraycopy(urlInBuffer.getBytes(), 0, ptrurlInBuffer.byValue, 0, urlInBuffer.length());
        ptrurlInBuffer.write();
        m_struParam2.pRequestUrl = ptrurlInBuffer.getPointer();
        m_struParam2.dwRequestUrlLen = urlInBuffer.length();

        //输入参数，XML或者JSON数据
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("employeeNo", employeeNo); // 人员工号
        parameter.put("fingerData", "MzAxPBehFkiYDkKJJh6IhU/dFjuwElStJi7IhV2NJj/UhB3hFliUhmLJJivQEEURFyiQh1G1FT6cDH1NFo7Mep3FFi6g" +
                "CY0JJl607XXJFF6kAoT1FE6gdXYFFW60e5M5FW6kepuhFV6we5rtFSak64RBFW6sCS6VFDugBDwpJT64gFIZJU68ezDNJSd4EDQNF0iIimlJFl60g" +
                "WcNFyeMgjatFTeAgDCRFTd4f4X1FW60e5upJGucbqFZJHuE5ZF1JHus7oS1FE6o7ahZFGuIZbOpFTrEBDtdFSuQCQwhFUh4CRJxFUuEhDE5FCioBK" +
                "91JEeg4rhlFEucZbcBFi7IfrlBJXvYAc09FFjI4M2RFXioBNWJJaigeerFFXjQdu8ZFUjA6PzBJmiwKATiFTi4cgyyFTiQ6jppJ0jIiUxFF2ioC5Zp" +
                "Jyd4FaGVFxiglaxxFxu0ie6hFzjgGw52J1jYkGoJFGTQbncFFFTIZwAAAAAAUxMAFFYB4Lkce1JUEALZC+0zWRIAwxHrXFwQQKgQe+JKABDjCXora" +
                "DFgZxIJGjsQcEEPeu5IEDHiKX5zIBDgmRiBsC0xIH4NdUFdECKgKgGNXRNQnQgMaRkRgL4sBMUREODrBIR8UhFS1gwCskgAEAQg7UopF2CALXliEAA" +
                "B5R/unG0BVHAZggAAAAAAd7w="); // 指纹编码数据
        String input = ConfigFileUtil.getReqBodyFromTemplate("conf/acs/AddFingerPrintInfo.json", parameter);
        System.out.println(input);

        byte[] byInbuffer = input.getBytes(StandardCharsets.UTF_8);
        int iInBufLen = byInbuffer.length;
        BYTE_ARRAY ptrInBuffer = new BYTE_ARRAY(iInBufLen);
        ptrInBuffer.read();
        System.arraycopy(byInbuffer, 0, ptrInBuffer.byValue, 0, iInBufLen);
        ptrInBuffer.write();
        m_struParam2.pInBuffer = ptrInBuffer.getPointer();//GET获取时不需要输入参数，输入为null
        m_struParam2.dwInSize = iInBufLen;
        //输出参数，分配的内存用于存储返回的数据，需要大于等于实际内容大小
        int iOutSize2 = 2 * 1024 * 1024;
        BYTE_ARRAY ptrOutByte2 = new BYTE_ARRAY(iOutSize2);
        m_struParam2.pOutBuffer = ptrOutByte2.getPointer();
        m_struParam2.dwOutSize = iOutSize2;
        m_struParam2.dwRecvTimeOut = 5000; //接收超时时间，单位毫秒
        m_struParam2.write();
        if (!hCEhomeCMS.NET_ECMS_ISAPIPassThrough(loginID, m_struParam2)) {
            System.out.println("下发门禁指纹失败，NET_ECMS_ISAPIPassThrough failed,error：" + hCEhomeCMS.NET_ECMS_GetLastError());
            return;
        } else {
            m_struParam2.read();
            ptrOutByte2.read();
            System.out.println("NET_ECMS_ISAPIPassThrough succeed\n" + "输出报文:" + new String(ptrOutByte2.byValue).trim());
        }
        return;

    }

    /**
     * 查询指纹数据
     *
     * @param loginID    登录设备句柄ID
     * @param employeeNo 人员功能ID，根据工号查询指纹数据
     * @throws UnsupportedEncodingException
     */
    public static void searchFingerPrintInfo(int loginID, String employeeNo) throws UnsupportedEncodingException {

        NET_EHOME_PTXML_PARAM m_struParam2 = new NET_EHOME_PTXML_PARAM();
        m_struParam2.read();
        //透传URL，不同功能对应不同的URL，完整协议报文说明需要参考ISAPI协议文档
        String urlInBuffer = "POST /ISAPI/AccessControl/FingerPrintUpload?format=json"; //查询指纹URL
        BYTE_ARRAY ptrurlInBuffer = new BYTE_ARRAY(urlInBuffer.length() + 1);
        System.arraycopy(urlInBuffer.getBytes(), 0, ptrurlInBuffer.byValue, 0, urlInBuffer.length());
        ptrurlInBuffer.write();
        m_struParam2.pRequestUrl = ptrurlInBuffer.getPointer();
        m_struParam2.dwRequestUrlLen = urlInBuffer.length();
        UUID uuid = UUID.randomUUID();
        //输入参数，XML或者JSON数据
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("searchID", uuid); // 查询ID
        parameter.put("employeeNo", employeeNo); // 人员工号ID
        String input = ConfigFileUtil.getReqBodyFromTemplate("conf/acs/SearchFingerPrintParam.json", parameter);
        System.out.println(input);

        byte[] byInbuffer = input.getBytes(StandardCharsets.UTF_8);
        int iInBufLen = byInbuffer.length;
        BYTE_ARRAY ptrInBuffer = new BYTE_ARRAY(iInBufLen);
        ptrInBuffer.read();
        System.arraycopy(byInbuffer, 0, ptrInBuffer.byValue, 0, iInBufLen);
        ptrInBuffer.write();
        m_struParam2.pInBuffer = ptrInBuffer.getPointer();//GET获取时不需要输入参数，输入为null
        m_struParam2.dwInSize = iInBufLen;
        //输出参数，分配的内存用于存储返回的数据，需要大于等于实际内容大小
        int iOutSize2 = 2 * 1024 * 1024;
        BYTE_ARRAY ptrOutByte2 = new BYTE_ARRAY(iOutSize2);
        m_struParam2.pOutBuffer = ptrOutByte2.getPointer();
        m_struParam2.dwOutSize = iOutSize2;
        m_struParam2.dwRecvTimeOut = 5000; //接收超时时间，单位毫秒
        m_struParam2.write();
        if (!hCEhomeCMS.NET_ECMS_ISAPIPassThrough(loginID, m_struParam2)) {
            System.out.println("查询门禁指纹失败，NET_ECMS_ISAPIPassThrough failed,error：" + hCEhomeCMS.NET_ECMS_GetLastError());
            return;
        } else {
            m_struParam2.read();
            ptrOutByte2.read();
            System.out.println("NET_ECMS_ISAPIPassThrough succeed\n" + "输出报文:" + new String(ptrOutByte2.byValue).trim());
        }
        return;
    }

    /**
     * 删除门禁指纹数据
     *
     * @param loginID    登录设备句柄ID
     * @param employeeNo 人员工号，指导工号进行删除绑定的指纹数据
     * @throws UnsupportedEncodingException
     */
    public static void deleteFingerPrintInfo(int loginID, String employeeNo) throws UnsupportedEncodingException {
        NET_EHOME_PTXML_PARAM m_struParam2 = new NET_EHOME_PTXML_PARAM();
        m_struParam2.read();
        //透传URL，不同功能对应不同的URL，完整协议报文说明需要参考ISAPI协议文档
        String urlInBuffer = "POST /ISAPI/AccessControl/FingerPrint/SetUp?format=json"; //删除指纹URL
        BYTE_ARRAY ptrurlInBuffer = new BYTE_ARRAY(urlInBuffer.length() + 1);
        System.arraycopy(urlInBuffer.getBytes(), 0, ptrurlInBuffer.byValue, 0, urlInBuffer.length());
        ptrurlInBuffer.write();
        m_struParam2.pRequestUrl = ptrurlInBuffer.getPointer();
        m_struParam2.dwRequestUrlLen = urlInBuffer.length();
        UUID uuid = UUID.randomUUID();
        //输入参数，XML或者JSON数据,
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("employeeNo", employeeNo);  //人员工号
        String input = ConfigFileUtil.getReqBodyFromTemplate("conf/acs/DeleteFingerPrintInfo.json", parameter);
        System.out.println(input);

        byte[] byInbuffer = input.getBytes(StandardCharsets.UTF_8);
        int iInBufLen = byInbuffer.length;
        BYTE_ARRAY ptrInBuffer = new BYTE_ARRAY(iInBufLen);
        ptrInBuffer.read();
        System.arraycopy(byInbuffer, 0, ptrInBuffer.byValue, 0, iInBufLen);
        ptrInBuffer.write();
        m_struParam2.pInBuffer = ptrInBuffer.getPointer();//GET获取时不需要输入参数，输入为null
        m_struParam2.dwInSize = iInBufLen;
        //输出参数，分配的内存用于存储返回的数据，需要大于等于实际内容大小
        int iOutSize2 = 2 * 1024 * 1024;
        BYTE_ARRAY ptrOutByte2 = new BYTE_ARRAY(iOutSize2);
        m_struParam2.pOutBuffer = ptrOutByte2.getPointer();
        m_struParam2.dwOutSize = iOutSize2;
        m_struParam2.dwRecvTimeOut = 5000; //接收超时时间，单位毫秒
        m_struParam2.write();
        if (!hCEhomeCMS.NET_ECMS_ISAPIPassThrough(loginID, m_struParam2)) {
            System.out.println("删除门禁指纹失败，NET_ECMS_ISAPIPassThrough failed,error：" + hCEhomeCMS.NET_ECMS_GetLastError());
            return;
        } else {
            m_struParam2.read();
            ptrOutByte2.read();
            System.out.println("NET_ECMS_ISAPIPassThrough succeed\n" + "输出报文:" + new String(ptrOutByte2.byValue).trim());
        }
        return;

    }


}
