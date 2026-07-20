package com.fastbee.icc.demo.alarm.alarmOperate;

import com.dahuatech.hutool.http.Method;
import com.dahuatech.hutool.json.JSONUtil;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.model.v202010.OauthConfigUserPwdInfo;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.icc.config.OauthConfigUtil;
import com.fastbee.icc.model.alarm.alarmOperate.AlarmOperateRequest;
import com.fastbee.icc.model.alarm.alarmOperate.AlarmOperateResponse;

/**
 * program:java-sdk-demo
 * @Author: 355079
 * @Date:2024-04-28 11:58:00
 * @Description: 远程控制
 */

@Slf4j
public class AlarmOperateDemo {

    /**
     * 报警主机操作控制(用于处理资源绑定数据)
     * @param alarmOperateRequest
     * @return
     * @throws ClientException
     */
    public AlarmOperateResponse operateAlarmHost(AlarmOperateRequest alarmOperateRequest) {
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        AlarmOperateResponse response=null;
        try {
            log.info("AlarmOperateDemo,alarmOperate,request:{}", JSONUtil.toJsonStr(alarmOperateRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-alarm/1.0.0/alarmhosts/operate", alarmOperateRequest,null, Method.POST , config, AlarmOperateResponse.class);
            log.info("AlarmOperateDemo,alarmOperate,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.isSuccess()) {
            log.info("报警主机操作控制失败:{}",response.getErrMsg());
        }
        return response;
    }
}
