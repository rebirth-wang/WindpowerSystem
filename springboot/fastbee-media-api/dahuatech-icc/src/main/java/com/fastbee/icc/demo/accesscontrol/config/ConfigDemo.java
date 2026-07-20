package com.fastbee.icc.demo.accesscontrol.config;

import com.dahuatech.hutool.http.Method;
import com.dahuatech.hutool.json.JSONUtil;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.model.v202010.OauthConfigUserPwdInfo;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.icc.config.OauthConfigUtil;
import com.fastbee.icc.model.accesscontrol.config.AddDoorGroupRequest;
import com.fastbee.icc.model.accesscontrol.config.AddDoorGroupResponse;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-04-22 19:50
 * @Description: 门禁配置
 */
@Slf4j
public class ConfigDemo {

    /**
     * 添加门组
     * @param addDoorGroupRequest
     */
    public void addDoorGroup(AddDoorGroupRequest addDoorGroupRequest){
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        AddDoorGroupResponse response=null;
        try {
            log.info("ConfigDemo,addDoorGroup,request:{}", JSONUtil.toJsonStr(addDoorGroupRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-accesscontrol/1.0.0/card/accessControl/doorGroup", addDoorGroupRequest,null, Method.POST , config, AddDoorGroupResponse.class);
            log.info("ConfigDemo,addDoorGroup,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.isSuccess()) {
            log.info("添加门组失败:{}",response.getErrMsg());
        }
    }
}
