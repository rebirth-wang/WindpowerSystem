package com.fastbee.controller.tool;

import jakarta.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.mqttclient.EmqxApiClient;

/**
 * emq Rest API调用测试
 * @author gsb
 * @date 2025/6/3 15:55
 */
@RestController
@RequestMapping("/api/emq")
public class EmqxController {


    @Resource
    private EmqxApiClient emqxApiClient;

    @GetMapping("/getInfo")
    public AjaxResult getInfo(String clientId){
        EmqxApiClient.ClientInfoDTO clientInfo = emqxApiClient.getClientInfo(clientId);
        return AjaxResult.success(clientInfo);
    }

    @GetMapping("/checkStatus")
    public AjaxResult checkStatus(String clientId){
        boolean deviceOnline = emqxApiClient.isDeviceOnline(clientId);
        return AjaxResult.success(deviceOnline);
    }

}
