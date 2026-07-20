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
 * 门禁 卡片管理模块示例
 */
public class AcsCardManagement {

    private static HCISUPCMS hCEhomeCMS = SpringUtils.getBean(HCISUPCMS.class);

    /**
     * 下发卡号
     *
     * @param loginID    登录设备句柄ID
     * @param employeeNo 人员工号，卡号信息关联在工号上，需要先进行工号下发
     * @throws UnsupportedEncodingException
     */
    public static void addCardInfo(int loginID, String employeeNo) throws UnsupportedEncodingException {
        NET_EHOME_PTXML_PARAM m_struParam2 = new NET_EHOME_PTXML_PARAM();
        m_struParam2.read();
        //透传URL，不同功能对应不同的URL，完整协议报文说明需要参考ISAPI协议文档
        String urlInBuffer = "POST /ISAPI/AccessControl/CardInfo/Record?format=json"; //下发卡号URL
        BYTE_ARRAY ptrurlInBuffer = new BYTE_ARRAY(urlInBuffer.length() + 1);
        System.arraycopy(urlInBuffer.getBytes(), 0, ptrurlInBuffer.byValue, 0, urlInBuffer.length());
        ptrurlInBuffer.write();
        m_struParam2.pRequestUrl = ptrurlInBuffer.getPointer();
        m_struParam2.dwRequestUrlLen = urlInBuffer.length();

        //输入参数，XML或者JSON数据,下发卡号json报文
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("employeeNo", employeeNo); // 员工ID
        parameter.put("cardNo", "123456789"); // 员工名称
        String input = ConfigFileUtil.getReqBodyFromTemplate("conf/acs/AddCardInfoParam.json", parameter);
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
            System.out.println("下发卡号失败，NET_ECMS_ISAPIPassThrough failed,error：" + hCEhomeCMS.NET_ECMS_GetLastError());
            return;
        } else {
            m_struParam2.read();
            ptrOutByte2.read();
            System.out.println("NET_ECMS_ISAPIPassThrough succeed\n" + "输出报文:" + new String(ptrOutByte2.byValue).trim());
        }
        return;

    }

    /**
     * 删除卡号
     *
     * @param loginID 登录设备句柄ID
     * @param cardNo  删除的卡号参数
     * @throws UnsupportedEncodingException
     */
    public static void deleteCardInfo(int loginID, String cardNo) throws UnsupportedEncodingException {
        NET_EHOME_PTXML_PARAM m_struParam2 = new NET_EHOME_PTXML_PARAM();
        m_struParam2.read();
        //透传URL，不同功能对应不同的URL，完整协议报文说明需要参考ISAPI协议文档
        String urlInBuffer = "PUT /ISAPI/AccessControl/CardInfo/Delete?format=json"; //下发卡号URL
        BYTE_ARRAY ptrurlInBuffer = new BYTE_ARRAY(urlInBuffer.length() + 1);
        System.arraycopy(urlInBuffer.getBytes(), 0, ptrurlInBuffer.byValue, 0, urlInBuffer.length());
        ptrurlInBuffer.write();
        m_struParam2.pRequestUrl = ptrurlInBuffer.getPointer();
        m_struParam2.dwRequestUrlLen = urlInBuffer.length();

        //输入参数，XML或者JSON数据,下发卡号json报文
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("cardNo", cardNo); // 员工名称
        String input = ConfigFileUtil.getReqBodyFromTemplate("conf/acs/DeleteCardInfo.json", parameter);
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
            System.out.println("删除卡号失败，NET_ECMS_ISAPIPassThrough failed,error：" + hCEhomeCMS.NET_ECMS_GetLastError());
            return;
        } else {
            m_struParam2.read();
            ptrOutByte2.read();
            System.out.println("NET_ECMS_ISAPIPassThrough succeed\n" + "输出报文:" + new String(ptrOutByte2.byValue).trim());
        }
        return;
    }

    /**
     * 查询卡号信息
     *
     * @param loginID    登录设备句柄ID
     * @param employeeNo 按照工号进行查询，查询工号下绑定的所有卡号信息
     * @throws UnsupportedEncodingException
     */
    public static void searchCardInfo(int loginID, String employeeNo) throws UnsupportedEncodingException {
        NET_EHOME_PTXML_PARAM m_struParam2 = new NET_EHOME_PTXML_PARAM();
        m_struParam2.read();
        //透传URL，不同功能对应不同的URL，完整协议报文说明需要参考ISAPI协议文档
        String urlInBuffer = "POST /ISAPI/AccessControl/CardInfo/Search?format=json"; //下发卡号URL
        BYTE_ARRAY ptrurlInBuffer = new BYTE_ARRAY(urlInBuffer.length() + 1);
        System.arraycopy(urlInBuffer.getBytes(), 0, ptrurlInBuffer.byValue, 0, urlInBuffer.length());
        ptrurlInBuffer.write();
        m_struParam2.pRequestUrl = ptrurlInBuffer.getPointer();
        m_struParam2.dwRequestUrlLen = urlInBuffer.length();

        //输入参数，XML或者JSON数据,下发卡号json报文
        Map<String, Object> parameter = new HashMap<>();
        UUID uuid = UUID.randomUUID();
        parameter.put("searchID", uuid); //搜索ID，每次查询条件不同搜索ID不一样
        parameter.put("employeeNo", employeeNo); // 员工名称
        String input = ConfigFileUtil.getReqBodyFromTemplate("conf/acs/SearchCardInfoParam.json", parameter);
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
            System.out.println("查询卡号失败，NET_ECMS_ISAPIPassThrough failed,error：" + hCEhomeCMS.NET_ECMS_GetLastError());
            return;
        } else {
            m_struParam2.read();
            ptrOutByte2.read();
            System.out.println("NET_ECMS_ISAPIPassThrough succeed\n" + "输出报文:" + new String(ptrOutByte2.byValue).trim());
        }
        return;
    }

}
