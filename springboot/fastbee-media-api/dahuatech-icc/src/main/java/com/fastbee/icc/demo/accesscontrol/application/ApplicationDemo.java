package com.fastbee.icc.demo.accesscontrol.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dahuatech.hutool.http.Method;
import com.dahuatech.hutool.json.JSONUtil;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.model.v202010.GeneralResponse;
import com.dahuatech.icc.oauth.model.v202010.OauthConfigUserPwdInfo;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.icc.config.OauthConfigUtil;
import com.fastbee.icc.model.accesscontrol.application.GenerateQRCodeResponse;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-01 16:37
 * @Description: 门禁应用
 */
@Slf4j
public class ApplicationDemo {

    /**
     * 远程开门
     *
     * @param channelCodeList 通道编码列表
     * @return 开门结果
     */
    public GeneralResponse remoteOpenDoor(List<String> channelCodeList) {
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        GeneralResponse response = null;
        Map<String, List<String>> requestBodyMap = null;
        try {

            requestBodyMap = new HashMap<>();
            requestBodyMap.put("channelCodeList", channelCodeList);
            log.info("ApplicationDemo,remoteOpenDoor,request:{}", JSONUtil.toJsonStr(requestBodyMap));
            response = HttpUtils.executeJson("/evo-apigw/evo-accesscontrol/1.0.0/card/accessControl/channelControl/openDoor", requestBodyMap, null, Method.POST, config, GeneralResponse.class);
            log.info("ApplicationDemo,remoteOpenDoor,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if (!response.isSuccess()) {
            log.info("远程开门失败:{}", response.getErrMsg());
        }
        return response;
    }

    /**
     * 远程关门
     *
     * @param channelCodeList 通道编码列表
     * @return 关门结果
     */
    public GeneralResponse remoteCloseDoor(List<String> channelCodeList) {
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        GeneralResponse response = null;
        Map<String, List<String>> requestBodyMap;
        try {

            requestBodyMap = new HashMap<>();
            requestBodyMap.put("channelCodeList", channelCodeList);
            log.info("ApplicationDemo,remoteCloseDoor,request:{}", JSONUtil.toJsonStr(requestBodyMap));
            response = HttpUtils.executeJson("/evo-apigw/evo-accesscontrol/1.0.0/card/accessControl/channelControl/closeDoor", requestBodyMap, null, Method.POST, config, GeneralResponse.class);
            log.info("ApplicationDemo,remoteCloseDoor,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if (!response.isSuccess()) {
            log.info("远程关门失败:{}", response.getErrMsg());
        }
        return response;
    }


    /**
     * 设置常开模式
     *
     * @param chanelCodeList
     * @return
     */
    public GeneralResponse setStayOpenMode(List<String> chanelCodeList) {
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        GeneralResponse response = null;
        Map<String, List<String>> requestBodyMap;
        try {

            requestBodyMap = new HashMap<>();
            requestBodyMap.put("channelCodeList", chanelCodeList);
            log.info("ApplicationDemo,setStayOpenMode,request:{}", JSONUtil.toJsonStr(requestBodyMap));
            response = HttpUtils.executeJson("/evo-apigw/evo-accesscontrol/1.0.0/card/accessControl/channelControl/stayOpen", requestBodyMap, null, Method.POST, config, GeneralResponse.class);
            log.info("ApplicationDemo,setStayOpenMode,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if (!response.isSuccess()) {
            log.info("设置常开模式失败:{}", response.getErrMsg());
        }
        return response;
    }

    /**
     * 设置常闭模式
     *
     * @param chanelCodeList
     * @return
     */
    public GeneralResponse setStayCloseMode(List<String> chanelCodeList) {
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        GeneralResponse response = null;
        Map<String, List<String>> requestBodyMap;
        try {

            requestBodyMap = new HashMap<>();
            requestBodyMap.put("channelCodeList", chanelCodeList);
            log.info("ApplicationDemo,setStayCloseMode,request:{}", JSONUtil.toJsonStr(requestBodyMap));
            response = HttpUtils.executeJson("/evo-apigw/evo-accesscontrol/1.0.0/card/accessControl/channelControl/stayClose", requestBodyMap, null, Method.POST, config, GeneralResponse.class);
            log.info("ApplicationDemo,setStayCloseMode,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if (!response.isSuccess()) {
            log.info("设置常闭模式失败:{}", response.getErrMsg());
        }
        return response;
    }

    /**
     * 设置普通模式
     *
     * @param chanelCodeList
     * @return
     */
    public GeneralResponse setStayNormalMode(List<String> chanelCodeList) {
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        GeneralResponse response = null;
        Map<String, List<String>> requestBodyMap;
        try {

            requestBodyMap = new HashMap<>();
            requestBodyMap.put("channelCodeList", chanelCodeList);
            log.info("ApplicationDemo,setStayNormalMode,request:{}", JSONUtil.toJsonStr(requestBodyMap));
            response = HttpUtils.executeJson("/evo-apigw/evo-accesscontrol/1.0.0/card/accessControl/channelControl/normal", requestBodyMap, null, Method.POST, config, GeneralResponse.class);
            log.info("ApplicationDemo,setStayNormalMode,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if (!response.isSuccess()) {
            log.info("设置普通模式失败:{}", response.getErrMsg());
        }
        return response;
    }


    /**
     * 生成二维码
     *
     * @param personCode 人员编码
     * @return 二维码字符串
     */
    public String generateQRCode(String personCode) {
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        GenerateQRCodeResponse response = null;
        try {
            log.info("ApplicationDemo,generateQRCode,personCode:{}", personCode);
            String url = "/evo-apigw/evo-accesscontrol/1.0.0/card/card/generateQRCode/" + personCode;
            response = HttpUtils.executeJson(url, null, null, Method.GET, config, GenerateQRCodeResponse.class);
            log.info("ApplicationDemo,generateQRCode,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if (!response.isSuccess()) {
            log.info("获取业主二维码字符串失败:{}", response.getErrMsg());
            return null;
        }
        String code = response.getData();
        log.info("二维码字符串:{}", code);
        return code;
    }

}
