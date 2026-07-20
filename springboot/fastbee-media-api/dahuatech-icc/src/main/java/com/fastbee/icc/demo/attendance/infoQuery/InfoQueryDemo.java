package com.fastbee.icc.demo.attendance.infoQuery;

import com.dahuatech.hutool.http.Method;
import com.dahuatech.hutool.json.JSONUtil;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.model.v202010.OauthConfigUserPwdInfo;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.icc.config.OauthConfigUtil;
import com.fastbee.icc.model.attendance.infoQuery.GetRecordPageRequest;
import com.fastbee.icc.model.attendance.infoQuery.GetRecordPageResponse;
import com.fastbee.icc.model.attendance.infoQuery.GetResultPageRequest;
import com.fastbee.icc.model.attendance.infoQuery.GetResultPageResponse;

/**
 * program:java-sdk-demo
 * @Author: 355079
 * @Date:2024-04-28 14:01
 * @Description: 信息查询
 */
@Slf4j
public class InfoQueryDemo {


    /**
     * 打卡记录查询
     * @param getRecordPageRequest
     * @return
     * @throws ClientException
     */
    public GetRecordPageResponse getAttendanceRecordPage(GetRecordPageRequest getRecordPageRequest) {
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        GetRecordPageResponse response=null;
        try {
            log.info("InfoQueryDemo,getAttendanceRecordPage,request:{}", JSONUtil.toJsonStr(getRecordPageRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-attendance/1.1.0/attendance/record/page", getRecordPageRequest,null, Method.POST , config, GetRecordPageResponse.class);
            log.info("InfoQueryDemo,getAttendanceRecordPage,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.isSuccess()) {
            log.info("打卡记录查询失败:{}",response.getErrMsg());
        }
        return response;
    }

    /**
     * 分页获取考勤结果
     * @param getResultPageRequest
     * @return
     * @throws ClientException
     */
    public GetResultPageResponse getAttendanceResultPage(GetResultPageRequest getResultPageRequest) {
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        GetResultPageResponse response=null;
        try {
            log.info("InfoQueryDemo,getAttendanceRecordPage,request:{}", JSONUtil.toJsonStr(getResultPageRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-attendance/1.1.0/attendance/result/page", getResultPageRequest,null, Method.POST , config, GetResultPageResponse.class);
            log.info("InfoQueryDemo,getAttendanceRecordPage,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.isSuccess()) {
            log.info("分页获取考勤结果失败:{}",response.getErrMsg());
        }
        return response;
    }
}
