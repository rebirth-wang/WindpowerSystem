package com.fastbee.iot.data.ruleEngine;

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSONObject;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;

import com.fastbee.common.extend.core.domin.mq.InvokeReqDto;
import com.fastbee.iot.data.service.IFunctionInvoke;
import com.fastbee.rule.cmp.data.DevExecuteCData;
import com.fastbee.rule.context.BaseContext;

@LiteflowComponent("devExecute")
public class DevExecuteCmp  extends NodeComponent {
    @Resource
    private IFunctionInvoke functionInvoke;

    @Override
    public void process() throws Exception {
        DevExecuteCData data = this.getCmpData(DevExecuteCData.class);
        BaseContext cxt = this.getContextBean(BaseContext.class);
        InvokeReqDto reqDto = new InvokeReqDto();
        reqDto.setProductId(data.getProductId());
        reqDto.setSerialNumber(data.getDeviceId());
        reqDto.setModelName("");
        reqDto.setType(1);
        reqDto.setIdentifier(data.getModelId());
        Map<String, Object> params = new HashMap<>();
        params.put(data.getModelId(), data.getValue());
        reqDto.setRemoteCommand(params);
        reqDto.setParams(new JSONObject(reqDto.getRemoteCommand()));
        cxt.printDebugLog(this,"设备执行，reqDto=%s", reqDto.toString());
        functionInvoke.invokeNoReply(reqDto);
    }
}
