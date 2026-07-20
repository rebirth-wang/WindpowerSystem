package com.fastbee.icc.demo.ipms.deviceManage;

import com.dahuatech.hutool.http.Method;
import com.dahuatech.hutool.json.JSONUtil;
import com.dahuatech.icc.exception.ClientException;
import com.dahuatech.icc.oauth.model.v202010.OauthConfigUserPwdInfo;
import com.dahuatech.icc.oauth.utils.HttpUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.icc.config.OauthConfigUtil;
import com.fastbee.icc.model.ipms.deviceManage.DevicePageRequest;
import com.fastbee.icc.model.ipms.deviceManage.DevicePageResponse;
import com.fastbee.icc.model.ipms.deviceManage.SluiceChannelResponse;

/**
 * @className DeviceManageDemo
 * @Author 355079
 * @Date 2024/12/10
 * @Description 设备管理
 */
@Data
@Slf4j
public class DeviceManageDemo {
    /**
     * 查询设备列表
     * @param devicePageRequest 设备列表请求
     * @return 设备列表响应
     */
    public DevicePageResponse queryDeviceList(DevicePageRequest devicePageRequest) {
        DevicePageResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            String url = "/evo-apigw/ipms/device/list"+devicePageRequest.getUrlSuffix();
            log.info("DeviceManageDemo,queryDeviceList,url:{}", url);
            response = HttpUtils.executeJson(url, null,null, Method.GET , config, DevicePageResponse.class);
            log.info("DeviceManageDemo,queryDeviceList,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.isSuccess()) {
            log.info("查询设备列表失败：{}",response.getErrMsg());
        }
        return response;
    }

    /**
     * 道闸通道信息全量查询
     * @return 闸门通道列表
     */
    public SluiceChannelResponse fullQuerySluiceChannel() {
        SluiceChannelResponse response=null;
        OauthConfigUserPwdInfo config = OauthConfigUtil.getOauthConfig();
        try {
            String url = "/evo-apigw/ipms/device/sluice/channel";
            log.info("DeviceManageDemo,fullQuerySluiceChannel,url:{}", url);
            response = HttpUtils.executeJson(url, null,null, Method.GET , config, SluiceChannelResponse.class);
            log.info("DeviceManageDemo,fullQuerySluiceChannel,response:{}", JSONUtil.toJsonStr(response));
        } catch (ClientException e) {
            log.error(e.getErrMsg(), e);
        }
        if(!response.isSuccess()) {
            log.info("查询设备列表失败：{}",response.getErrMsg());
        }
        return response;
    }
}
