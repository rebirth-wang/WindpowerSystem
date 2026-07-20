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
 * 门禁 人员管理模块示例
 */
public class AcsUserManagement {
    private static HCISUPCMS hCEhomeCMS = SpringUtils.getBean(HCISUPCMS.class);

    /**
     * 添加人员工号信息
     *
     * @param loginID 登录设备句柄ID
     * @throws UnsupportedEncodingException
     */
    public static void addEmployeeInfo(int loginID) throws UnsupportedEncodingException {
        NET_EHOME_PTXML_PARAM m_struParam2 = new NET_EHOME_PTXML_PARAM();
        m_struParam2.read();
        //透传URL，不同功能对应不同的URL，完整协议报文说明需要参考ISAPI协议文档
        String urlInBuffer = "POST /ISAPI/AccessControl/UserInfo/Record?format=json"; //添加人员URL
        BYTE_ARRAY ptrurlInBuffer = new BYTE_ARRAY(urlInBuffer.length() + 1);
        System.arraycopy(urlInBuffer.getBytes(), 0, ptrurlInBuffer.byValue, 0, urlInBuffer.length());
        ptrurlInBuffer.write();
        m_struParam2.pRequestUrl = ptrurlInBuffer.getPointer();
        m_struParam2.dwRequestUrlLen = urlInBuffer.length();

        //输入参数，XML或者JSON数据,下发人员信息json报文
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("employeeNo", "test001"); // 员工ID
        parameter.put("name", "测试账号98"); // 员工名称
        parameter.put("enable", true); // 是否启用
        parameter.put("doorNo", 1); // 门编号
        String input = ConfigFileUtil.getReqBodyFromTemplate("conf/acs/AddUserInfoParam.json", parameter);

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
            System.out.println("添加人员失败，NET_ECMS_ISAPIPassThrough failed,error：" + hCEhomeCMS.NET_ECMS_GetLastError());
            return;
        } else {
            m_struParam2.read();
            ptrOutByte2.read();
            System.out.println("NET_ECMS_ISAPIPassThrough succeed\n" + "输出报文:" + new String(ptrOutByte2.byValue).trim());
        }
        return;

    }

    /**
     * 删除人员工号信息
     *
     * @param loginID 登录设备句柄ID
     * @throws UnsupportedEncodingException
     */
    public static void deleteEmployeeInfo(int loginID) throws UnsupportedEncodingException {
        NET_EHOME_PTXML_PARAM m_struParam2 = new NET_EHOME_PTXML_PARAM();
        m_struParam2.read();
        //透传URL，不同功能对应不同的URL，完整协议报文说明需要参考ISAPI协议文档
        String urlInBuffer = "PUT /ISAPI/AccessControl/UserInfoDetail/Delete?format=json"; //删除人员URL
        BYTE_ARRAY ptrurlInBuffer = new BYTE_ARRAY(urlInBuffer.length() + 1);
        System.arraycopy(urlInBuffer.getBytes(), 0, ptrurlInBuffer.byValue, 0, urlInBuffer.length());
        ptrurlInBuffer.write();
        m_struParam2.pRequestUrl = ptrurlInBuffer.getPointer();
        m_struParam2.dwRequestUrlLen = urlInBuffer.length();

        //输入参数，XML或者JSON数据,删除人员信息json报文
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("mode", "byEmployeeNo"); // 删除方式, byEmployeeNo: 按员工ID号
        parameter.put("employeeNo", "989898"); // 员工ID号
        String input = ConfigFileUtil.getReqBodyFromTemplate("conf/acs/DeleteUserInfoParam.json", parameter);

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
            System.out.println("删除人员失败，NET_ECMS_ISAPIPassThrough failed,error：" + hCEhomeCMS.NET_ECMS_GetLastError());
            return;
        } else {
            m_struParam2.read();
            ptrOutByte2.read();
            System.out.println("NET_ECMS_ISAPIPassThrough succeed\n" + "输出报文:" + new String(ptrOutByte2.byValue).trim());
        }
        return;
    }


    /**
     * 查询人员工号信息
     *
     * @param loginID 登录设备句柄ID
     * @throws UnsupportedEncodingException
     */
    public static void searchEmployeeInfo(int loginID) throws UnsupportedEncodingException {
        NET_EHOME_PTXML_PARAM m_struParam2 = new NET_EHOME_PTXML_PARAM();
        m_struParam2.read();
        //透传URL，不同功能对应不同的URL，完整协议报文说明需要参考ISAPI协议文档
        String urlInBuffer = "POST /ISAPI/AccessControl/UserInfo/Search?format=json"; //查询人员URL
        BYTE_ARRAY ptrurlInBuffer = new BYTE_ARRAY(urlInBuffer.length() + 1);
        System.arraycopy(urlInBuffer.getBytes(), 0, ptrurlInBuffer.byValue, 0, urlInBuffer.length());
        ptrurlInBuffer.write();
        m_struParam2.pRequestUrl = ptrurlInBuffer.getPointer();
        m_struParam2.dwRequestUrlLen = urlInBuffer.length();
        UUID uuid = UUID.randomUUID();

        //输入参数，XML或者JSON数据,查询多条人员信息json报文
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("searchID", uuid); // 查询id
        parameter.put("maxResults", 30); // 最大查询数量
        String input = ConfigFileUtil.getReqBodyFromTemplate("conf/acs/SearchUserInfoParam.json", parameter);
        //输入参数，XML或者JSON数据,指定人员工号查询json报文
        System.out.println(input);

        byte[] byInbuffer = input.getBytes(StandardCharsets.UTF_8);
        int iInBufLen = byInbuffer.length;
        BYTE_ARRAY ptrInBuffer = new BYTE_ARRAY(iInBufLen);
        ptrInBuffer.read();
        System.arraycopy(byInbuffer, 0, ptrInBuffer.byValue, 0, iInBufLen);
        ptrInBuffer.write();
        // GET获取时不需要输入参数，输入为null
        m_struParam2.pInBuffer = ptrInBuffer.getPointer();
        m_struParam2.dwInSize = iInBufLen;
        // 输出参数，分配的内存用于存储返回的数据，需要大于等于实际内容大小
        int iOutSize2 = 2 * 1024 * 1024;
        BYTE_ARRAY ptrOutByte2 = new BYTE_ARRAY(iOutSize2);
        m_struParam2.pOutBuffer = ptrOutByte2.getPointer();
        m_struParam2.dwOutSize = iOutSize2;
        m_struParam2.dwRecvTimeOut = 5000; //接收超时时间，单位毫秒
        m_struParam2.write();
        if (!hCEhomeCMS.NET_ECMS_ISAPIPassThrough(loginID, m_struParam2)) {
            System.out.println("查询人员失败，NET_ECMS_ISAPIPassThrough failed,error：" + hCEhomeCMS.NET_ECMS_GetLastError());
            return;
        } else {
            m_struParam2.read();
            ptrOutByte2.read();
            System.out.println("NET_ECMS_ISAPIPassThrough succeed\n" + "输出报文:" + new String(ptrOutByte2.byValue).trim());
        }
        return;
    }
}
