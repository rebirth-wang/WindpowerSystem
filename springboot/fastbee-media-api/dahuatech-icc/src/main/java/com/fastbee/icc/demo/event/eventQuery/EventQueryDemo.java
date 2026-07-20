package com.fastbee.icc.demo.event.eventQuery;

import com.dahuatech.hutool.http.Method;
import com.dahuatech.hutool.json.JSONUtil;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.model.v202010.OauthConfigUserPwdInfo;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.icc.config.OauthConfigUtil;
import com.fastbee.icc.model.event.eventQuery.AlarmRecordCountRequest;
import com.fastbee.icc.model.event.eventQuery.AlarmRecordCountResponse;
import com.fastbee.icc.model.event.eventQuery.AlarmRecordPageRequest;
import com.fastbee.icc.model.event.eventQuery.AlarmRecordPageResponse;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-06-11 14:29
 * @Description: 事件订阅
 */
@Slf4j
public class EventQueryDemo {
    

    /**
     * alarm事件统计
     * @param alarmRecordCountRequest
     * @return
     */
    public AlarmRecordCountResponse getAlarmRecordCount(AlarmRecordCountRequest alarmRecordCountRequest){
        AlarmRecordCountResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            log.info("EventQueryDemo,getAlarmRecordCount,request:{}", JSONUtil.toJsonStr(alarmRecordCountRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-event/1.0.0/alarm-record/count-num", alarmRecordCountRequest,null, Method.POST , config, AlarmRecordCountResponse.class);
            log.info("EventQueryDemo,getAlarmRecordCount,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.getCode().equals("0")) {
            log.info("alarm事件统计失败:{}",response.getErrMsg());
        }
        return response;
    }

    /**
     * alarm事件分页查询
     * @param alarmRecordPageRequest
     * @return
     */
    public AlarmRecordPageResponse getAlarmRecordPage(AlarmRecordPageRequest alarmRecordPageRequest){
        AlarmRecordPageResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            log.info("EventQueryDemo,getAlarmRecordPage,request:{}", JSONUtil.toJsonStr(alarmRecordPageRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-event/1.2.0/alarm-record/page", alarmRecordPageRequest,null, Method.POST , config, AlarmRecordPageResponse.class);
            log.info("EventQueryDemo,getAlarmRecordPage,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.getCode().equals("0")) {
            log.info("alarm事件分页查询失败:{}",response.getErrMsg());
        }
        return response;
    }
}
