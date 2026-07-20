package com.fastbee.iot.data.job;

import jakarta.annotation.Resource;

import com.yomahub.liteflow.builder.el.LiteFlowChainELBuilder;
import org.springframework.stereotype.Component;

import com.fastbee.bridge.domain.Bridge;
import com.fastbee.bridge.service.IBridgeService;
import com.fastbee.rule.context.MsgContext;
import com.fastbee.rule.core.FlowLogExecutor;

@Component
public class HttpBridgeJob {
    @Resource
    private IBridgeService bridgeService;

    @Resource
    private FlowLogExecutor flowlogExecutor;

    public void invokeMethod(String bridgeName, String scriptId) throws Exception {
        Bridge bridge = bridgeService.selectBridgeByName(bridgeName);
        MsgContext context = MsgContext.builder().build();
        context.setData("httpBridgeID", bridge.getId());
        context.setData("httpPull", true);

        System.out.println("------------------[定时执行http桥接拉取]---------------------");
        String el = "THEN(httpBridge," + scriptId + ")";
        String eChainName = "dataChain_" + scriptId;
        String requestId = "script/" + scriptId;
        LiteFlowChainELBuilder.createChain().setChainName(eChainName).setEL(el).build();
        // 执行规则脚本
        flowlogExecutor.execute2RespWithRid(eChainName, requestId, context);
    }

}
