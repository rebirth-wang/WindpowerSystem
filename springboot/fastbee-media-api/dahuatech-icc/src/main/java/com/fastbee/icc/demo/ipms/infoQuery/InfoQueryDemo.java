package com.fastbee.icc.demo.ipms.infoQuery;

import java.io.UnsupportedEncodingException;

import com.dahuatech.hutool.http.Method;
import com.dahuatech.hutool.json.JSONUtil;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.model.v202010.OauthConfigUserPwdInfo;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.icc.config.OauthConfigUtil;
import com.fastbee.icc.model.ipms.infoQuery.InAndOutRecordCountRequest;
import com.fastbee.icc.model.ipms.infoQuery.InAndOutRecordCountResponse;
import com.fastbee.icc.model.ipms.infoQuery.InAndOutRecordPageRequest;
import com.fastbee.icc.model.ipms.infoQuery.InAndOutRecordPageResponse;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-06-06 17:16
 * @Description: 信息查询
 */
@Slf4j
public class InfoQueryDemo {

    /**
     * 统计车辆进出总数
     * @param inAndOutRecordCountRequest
     * @return
     */
    public InAndOutRecordCountResponse getInAndOutRecordCount(InAndOutRecordCountRequest inAndOutRecordCountRequest){
        InAndOutRecordCountResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            String url = "/evo-apigw/ipms/caraccess/find/hisCount";
            if(inAndOutRecordCountRequest.getUrlSuffix()!=null){
                url+= inAndOutRecordCountRequest.getUrlSuffix();
            }
            log.info("InfoQueryDemo,getInAndOutRecordCount,url:{}", url);
            response = HttpUtils.executeJson(url, null,null, Method.GET , config, InAndOutRecordCountResponse.class);
            log.info("InfoQueryDemo,getInAndOutRecordCount,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException |UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        if(!response.getCode().equals("200")) {
            log.info("统计车辆进出总数失败：{}",response.getErrMsg());
        }
        return response;
    }

    /**
     * 车辆进出数据
     * @param inAndOutRecordPageRequest
     * @return
     */
    public InAndOutRecordPageResponse getInAndOutRecordPage(InAndOutRecordPageRequest inAndOutRecordPageRequest){
        InAndOutRecordPageResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            String url = "/evo-apigw/ipms/caraccess/find/his"+inAndOutRecordPageRequest.getUrlSuffix();
            log.info("InfoQueryDemo,getInAndOutRecordPage,url:{}", url);
            response = HttpUtils.executeJson(url, null,null, Method.GET , config, InAndOutRecordPageResponse.class);
            log.info("InfoQueryDemo,getInAndOutRecordPage,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException | UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        if(!response.getCode().equals("200")) {
            log.info("车辆进出数据查询失败：{}",response.getErrMsg());
        }
        return response;
    }

}
