package com.fastbee.icc.demo.accesscontrol.auth;

import com.dahuatech.hutool.http.Method;
import com.dahuatech.hutool.json.JSONUtil;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.model.v202010.GeneralResponse;
import com.dahuatech.icc.oauth.model.v202010.OauthConfigUserPwdInfo;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.icc.config.OauthConfigUtil;
import com.fastbee.icc.model.accesscontrol.auth.DepartmentAuthRequest;
import com.fastbee.icc.model.accesscontrol.auth.PersonAuthRequest;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-22 14:11
 * @Description: 门禁授权
 */
@Slf4j
public class AuthDemo {

    /**
     * 按人新增授权
     * @param personAuthRequest
     */
    public void personAuth(PersonAuthRequest personAuthRequest){
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        GeneralResponse response=null;
        try {
            log.info("AuthDemo,personAuth,request:{}", JSONUtil.toJsonStr(personAuthRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-accesscontrol/1.0.0/card/accessControl/personAuthority/batchAuthority", personAuthRequest,null, Method.POST , config,GeneralResponse.class);
            log.info("AuthDemo,personAuth,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.isSuccess()) {
            log.info("按人新增授权失败:{}",response.getErrMsg());
        }
    }

    /**
     * 按部门新增授权
     * @param departmentAuthRequest
     */
    public void deptAuth(DepartmentAuthRequest departmentAuthRequest){
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        GeneralResponse response=null;
        try {
            log.info("AuthDemo,deptAuth,request:{}", JSONUtil.toJsonStr(departmentAuthRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-accesscontrol/1.0.0/card/accessControl/deptAuthority", departmentAuthRequest,null, Method.POST , config,GeneralResponse.class);
            log.info("AuthDemo,deptAuth,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.isSuccess()) {
            log.info("按部门新增授权失败:{}",response.getErrMsg());
        }
    }


}
