package com.fastbee.icc.demo.passengerflow.passengerflowConfig;

import com.dahuatech.hutool.http.Method;
import com.dahuatech.hutool.json.JSONUtil;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.model.v202010.OauthConfigUserPwdInfo;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.icc.config.OauthConfigUtil;
import com.fastbee.icc.model.passengerflow.passengerflowConfig.RegionConfigPageRequest;
import com.fastbee.icc.model.passengerflow.passengerflowConfig.RegionConfigPageResponse;

/**
 * program:java-sdk-demo
 *
 * @Author: 355079
 * @Date:2024-05-08 13:45
 * @Description: 客流配置
 */
@Slf4j
public class PassengerFlowConfigDemo {

    /**
     * 分页查询区域配置列表
     * @param regionConfigPageRequest
     * @return
     */
    public RegionConfigPageResponse getRegionConfigPage(RegionConfigPageRequest regionConfigPageRequest){
        RegionConfigPageResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            log.info("PassengerFlowConfigDemo,getRegionConfigPage,request:{}", JSONUtil.toJsonStr(regionConfigPageRequest));
            response = HttpUtils.executeJson("/evo-apigw/evo-passengerflow/1.0.0/statistics/regionConfig/page", regionConfigPageRequest,null, Method.POST , config, RegionConfigPageResponse.class);
            log.info("PassengerFlowConfigDemo,getRegionConfigPage,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.isSuccess()) {
            log.info("分页查询区域配置列表失败：{}",response.getErrMsg());
        }
        return response;
    }
}
