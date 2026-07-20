package com.fastbee.icc.demo.visitor.visitorAppointment;

import com.dahuatech.hutool.http.Method;
import com.dahuatech.hutool.json.JSONUtil;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.model.v202010.OauthConfigUserPwdInfo;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.icc.config.OauthConfigUtil;
import com.fastbee.icc.model.visitor.visitorAppointment.VisitorAppointmentRequest;
import com.fastbee.icc.model.visitor.visitorAppointment.VisitorAppointmentResponse;
import com.fastbee.icc.model.visitor.visitorAppointment.VisitorTypePageRequest;
import com.fastbee.icc.model.visitor.visitorAppointment.VisitorTypePageResponse;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-17 15:04
 * @Description: 访客预约
 */
@Slf4j
public class VisitorAppointmentDemo {

    /**
     * 查询访客类型列表
     * @param visitorTypePageRequest
     * @return
     */
    public VisitorTypePageResponse getVisitorTypePage(VisitorTypePageRequest visitorTypePageRequest){
        VisitorTypePageResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            log.info("appointmentDemo,getVisitorTypePage,request:{}", JSONUtil.toJsonStr(visitorTypePageRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-visitor/1.0.0/visitor-type/page", visitorTypePageRequest,null, Method.POST , config, VisitorTypePageResponse.class);
            log.info("appointmentDemo,getVisitorTypePage,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.getCode().equals("0")) {
            log.info("查询访客类型列表失败：{}",response.getErrMsg());
        }
        return response;
    }

    /**
     * 新增访客预约
     * @param visitorAppointmentRequest
     * @return
     */
    public VisitorAppointmentResponse addVisitorAppointment(VisitorAppointmentRequest visitorAppointmentRequest){
        VisitorAppointmentResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            log.info("appointmentDemo,addAppointment,request:{}", JSONUtil.toJsonStr(visitorAppointmentRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-visitor/1.0.0/card/visitor/appointment", visitorAppointmentRequest,null, Method.POST , config, VisitorAppointmentResponse.class);
            log.info("appointmentDemo,addAppointment,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.getCode().equals("0")) {
            log.info("新增访客预约失败：{}",response.getErrMsg());
        }
        return response;
    }
}
