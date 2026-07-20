package com.fastbee.isup.sdk.acs;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.fastbee.common.utils.spring.SpringUtils;
import com.fastbee.isup.sdk.service.HCISUPCMS;
import com.fastbee.isup.sdk.structure.BYTE_ARRAY;
import com.fastbee.isup.sdk.structure.NET_EHOME_PTXML_PARAM;
import com.fastbee.isup.util.ConfigFileUtil;

/**
 * 人员权限计划模板配置
 */
public class AcsUserRightPlanTemplate {
    private static HCISUPCMS hCEhomeCMS = SpringUtils.getBean(HCISUPCMS.class);

    /**
     * 获取人员权限计划模板参数
     *
     * @param loginID        登录设备句柄ID
     * @param planTemplateID 计划模板编号，从1开始 1号计划模板默认24小时全天有权限，一般不建议修改
     */
    public static void getUserRightPlanTemplateParm(int loginID, int planTemplateID) {
        NET_EHOME_PTXML_PARAM m_struParam = new NET_EHOME_PTXML_PARAM();
        m_struParam.read();

        //透传URL，不同功能对应不同的URL，完整协议报文说明需要参考ISAPI协议文档
        String url = "GET /ISAPI/AccessControl/UserRightPlanTemplate/" + planTemplateID + "?format=json"; //获取人员权限计划模板参数
        BYTE_ARRAY ptrUrl = new BYTE_ARRAY(url.length() + 1);
        System.arraycopy(url.getBytes(), 0, ptrUrl.byValue, 0, url.length());
        ptrUrl.write();
        m_struParam.pRequestUrl = ptrUrl.getPointer();
        m_struParam.dwRequestUrlLen = url.length();

        //输入参数，XML或者JSON数据
        m_struParam.pInBuffer = null;//GET获取时不需要输入参数，输入为null
        m_struParam.dwInSize = 0;

        //输出参数，分配的内存用于存储返回的数据，需要大于等于实际内容大小
        int iOutSize = 2 * 1024 * 1024;
        BYTE_ARRAY ptrOutByte = new BYTE_ARRAY(iOutSize);
        m_struParam.pOutBuffer = ptrOutByte.getPointer();
        m_struParam.dwOutSize = iOutSize;

        m_struParam.dwRecvTimeOut = 5000; //接收超时时间，单位毫秒
        m_struParam.write();

        if (!hCEhomeCMS.NET_ECMS_ISAPIPassThrough(loginID, m_struParam)) {
            System.out.println("获取人员权限计划模板参数失败，NET_ECMS_ISAPIPassThrough failed,error：" + hCEhomeCMS.NET_ECMS_GetLastError());
            return;
        } else {
            m_struParam.read();
            ptrOutByte.read();
            System.out.println("NET_ECMS_ISAPIPassThrough succeed\n" + "ptrOutByte:" + new String(ptrOutByte.byValue).trim());
        }
        return;
    }


    /**
     * 配置人员权限计划模板参数
     *
     * @param loginID        登录设备句柄ID
     * @param planTemplateID 计划模板编号，从1开始 1号计划模板默认24小时全天有权限，一般不建议修改。下发人员输入json报文的planTemplateNo节点指定人员绑定的计划模板编号
     * @throws UnsupportedEncodingException
     */
    public static void setUserRightPlanTemplateParm(int loginID, int planTemplateID) throws UnsupportedEncodingException {

        NET_EHOME_PTXML_PARAM m_struParam2 = new NET_EHOME_PTXML_PARAM();
        m_struParam2.read();

        //透传URL，不同功能对应不同的URL，完整协议报文说明需要参考ISAPI协议文档
        String urlInBuffer = "PUT /ISAPI/AccessControl/UserRightPlanTemplate/" + planTemplateID + "?format=json"; //配置门禁参数
        BYTE_ARRAY ptrurlInBuffer = new BYTE_ARRAY(urlInBuffer.length() + 1);
        System.arraycopy(urlInBuffer.getBytes(), 0, ptrurlInBuffer.byValue, 0, urlInBuffer.length());
        ptrurlInBuffer.write();
        m_struParam2.pRequestUrl = ptrurlInBuffer.getPointer();
        m_struParam2.dwRequestUrlLen = urlInBuffer.length();

        //输入参数，XML或者JSON数据
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("templateName", "计划模板2"); //计划模板名称
        parameter.put("weekPlanNo", 2); //绑定的周计划模板编号
        String input = ConfigFileUtil.getReqBodyFromTemplate("conf/acs/AcsUserRightPlanTemplateParam.json", parameter);
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
            System.out.println("配置人员权限计划模板参数失败，NET_ECMS_ISAPIPassThrough failed,error：" + hCEhomeCMS.NET_ECMS_GetLastError());
            return;
        } else {
            m_struParam2.read();
            ptrOutByte2.read();
            System.out.println("NET_ECMS_ISAPIPassThrough succeed\n" + "输出报文:" + new String(ptrOutByte2.byValue).trim());
        }
        return;
    }

    /**
     * 获取人员权限周计划参数
     *
     * @param loginID    登录设备句柄ID
     * @param weekPlanID 周计划编号，从1开始，1号周计划模板默认全天24小时权限，不建议修改
     */
    public static void getUserRightWeekPlanCfg(int loginID, int weekPlanID) {
        NET_EHOME_PTXML_PARAM m_struParam = new NET_EHOME_PTXML_PARAM();
        m_struParam.read();

        //透传URL，不同功能对应不同的URL，完整协议报文说明需要参考ISAPI协议文档
        String url = "GET /ISAPI/AccessControl/UserRightWeekPlanCfg/" + weekPlanID + "?format=json"; //	获取人员权限周计划参数
        BYTE_ARRAY ptrUrl = new BYTE_ARRAY(url.length() + 1);
        System.arraycopy(url.getBytes(), 0, ptrUrl.byValue, 0, url.length());
        ptrUrl.write();
        m_struParam.pRequestUrl = ptrUrl.getPointer();
        m_struParam.dwRequestUrlLen = url.length();

        //输入参数，XML或者JSON数据
        m_struParam.pInBuffer = null;//GET获取时不需要输入参数，输入为null
        m_struParam.dwInSize = 0;

        //输出参数，分配的内存用于存储返回的数据，需要大于等于实际内容大小
        int iOutSize = 2 * 1024 * 1024;
        BYTE_ARRAY ptrOutByte = new BYTE_ARRAY(iOutSize);
        m_struParam.pOutBuffer = ptrOutByte.getPointer();
        m_struParam.dwOutSize = iOutSize;

        m_struParam.dwRecvTimeOut = 5000; //接收超时时间，单位毫秒
        m_struParam.write();

        if (!hCEhomeCMS.NET_ECMS_ISAPIPassThrough(loginID, m_struParam)) {
            System.out.println("获取人员权限周计划参数失败，NET_ECMS_ISAPIPassThrough failed,error：" + hCEhomeCMS.NET_ECMS_GetLastError());
            return;
        } else {
            m_struParam.read();
            ptrOutByte.read();
            System.out.println("NET_ECMS_ISAPIPassThrough succeed\n" + "ptrOutByte:" + new String(ptrOutByte.byValue).trim());
        }
        return;

    }

    /**
     * 配置人员周计划参数
     *
     * @param loginID    登录设备句柄ID
     * @param weekPlanID 周计划编号，从1开始，1号周计划模板默认全天24小时权限，不建议修改
     * @throws UnsupportedEncodingException
     */
    public static void setUserRightWeekPlanCfg(int loginID, int weekPlanID) throws UnsupportedEncodingException {

        NET_EHOME_PTXML_PARAM m_struParam2 = new NET_EHOME_PTXML_PARAM();
        m_struParam2.read();

        //透传URL，不同功能对应不同的URL，完整协议报文说明需要参考ISAPI协议文档
        String urlInBuffer = "PUT /ISAPI/AccessControl/UserRightWeekPlanCfg/" + weekPlanID + "?format=json"; //	配置人员权限周计划参数
        BYTE_ARRAY ptrurlInBuffer = new BYTE_ARRAY(urlInBuffer.length() + 1);
        System.arraycopy(urlInBuffer.getBytes(), 0, ptrurlInBuffer.byValue, 0, urlInBuffer.length());
        ptrurlInBuffer.write();
        m_struParam2.pRequestUrl = ptrurlInBuffer.getPointer();
        m_struParam2.dwRequestUrlLen = urlInBuffer.length();

        //输入参数，XML或者JSON数据
//        Map<String, Object> parameter = new HashMap<>();
//        parameter.put("enable",true); //计划模板名称
        //AcsUserRightWeekPlanParam.json配置周计划模板，周一到周天，每天"08:00:00"到"12:00:00"具备认证权限
        String input = ConfigFileUtil.readFileContent("conf/acs/AcsUserRightWeekPlanParam.json");
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
            System.out.println("配置人员权限周计划参数失败，NET_ECMS_ISAPIPassThrough failed,error：" + hCEhomeCMS.NET_ECMS_GetLastError());
            return;
        } else {
            m_struParam2.read();
            ptrOutByte2.read();
            System.out.println("NET_ECMS_ISAPIPassThrough succeed\n" + "输出报文:" + new String(ptrOutByte2.byValue).trim());
        }
        return;
    }


}
