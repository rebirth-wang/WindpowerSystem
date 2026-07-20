package com.fastbee.icc.demo.visitor.visitorArea;

import com.dahuatech.hutool.http.Method;
import com.dahuatech.hutool.json.JSONUtil;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.model.v202010.OauthConfigUserPwdInfo;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.icc.config.OauthConfigUtil;
import com.fastbee.icc.model.visitor.visitorArea.VisitorAreaPageRequest;
import com.fastbee.icc.model.visitor.visitorArea.VisitorAreaPageResponse;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-18 16:38
 * @Description: 访问区域
 */
@Slf4j
public class VisitorAreaDemo {
    /**
     * 访问区域分页查询
     * @param visitorAreaPageRequest
     * @return
     */
    public VisitorAreaPageResponse getVisitorAreaPage(VisitorAreaPageRequest visitorAreaPageRequest){
        VisitorAreaPageResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            log.info("VisitorAreaDemo,getVisitorAreaPage,request:{}", JSONUtil.toJsonStr(visitorAreaPageRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-visitor/1.0.0/area/page", visitorAreaPageRequest,null, Method.POST , config, VisitorAreaPageResponse.class);
            log.info("VisitorAreaDemo,getVisitorAreaPage,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.getCode().equals("0")) {
            log.info("访问区域分页查询失败：{}",response.getErrMsg());
        }
        return response;
    }


}
