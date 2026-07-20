package com.fastbee.controller.ruleEngine;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.extend.enums.ScriptEventEnum;
import com.fastbee.iot.enums.ScriptPurposeType;
import com.fastbee.iot.model.ScriptCondition;
import com.fastbee.iot.service.IScriptService;
import com.fastbee.rule.context.MsgContext;

@Slf4j
@RestController
@RequestMapping("/bridge")
public class HttpBridgeController {
    @Resource
    private IScriptService scriptService;

    @ApiOperation("数据桥接get入口")
    @GetMapping(value = "/get")
    public AjaxResult bridgeGet(HttpServletRequest request)
    {
        ScriptCondition scriptCondition = ScriptCondition.builder()
                .scriptPurpose(ScriptPurposeType.DATA_STREAM.getCode())
                // Http接入
                .scriptEvent(ScriptEventEnum.HTTP_BRIDGE.getType())
                .route("/bridge/get")
                .build();
        MsgContext context = new MsgContext();
        context.getDataMap().putAll(buildDataMap(request));
        //返回处理完的消息上下文
        return AjaxResult.success(scriptService.execRuleScript(scriptCondition,context));
    }
    @ApiOperation("数据桥接put入口")
    @PutMapping(value = "/put")
    public AjaxResult bridgePut(HttpServletRequest request, @RequestBody Object body)
    {
        ScriptCondition scriptCondition = ScriptCondition.builder()
                .scriptPurpose(ScriptPurposeType.DATA_STREAM.getCode())
                // Http接入
                .scriptEvent(ScriptEventEnum.HTTP_BRIDGE.getType())
                .route("/bridge/put")
                .build();
        MsgContext context = MsgContext.builder().payload(JSON.toJSONString(body)).build();
        context.getDataMap().putAll(buildDataMap(request));
        //返回处理完的消息上下文
        return AjaxResult.success(scriptService.execRuleScript(scriptCondition,context));
    }
    @ApiOperation("数据桥接post入口")
    @PostMapping(value = "/post")
    public AjaxResult bridgePost(HttpServletRequest request, @RequestBody Object body)
    {
        ScriptCondition scriptCondition = ScriptCondition.builder()
                .scriptPurpose(ScriptPurposeType.DATA_STREAM.getCode())
                // Http接入
                .scriptEvent(ScriptEventEnum.HTTP_BRIDGE.getType())
                .route("/bridge/post")
                .build();
        MsgContext context = MsgContext.builder().payload(JSON.toJSONString(body)).build();
        context.getDataMap().putAll(buildDataMap(request));
        //返回处理完的消息上下文
        return AjaxResult.success(scriptService.execRuleScript(scriptCondition,context));
    }

    private ConcurrentHashMap<String, Object> buildDataMap(HttpServletRequest request)
    {
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, List<String>> headers = new HashMap<>();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, Collections.list(request.getHeaders(headerName)));
        }
        JSONObject headersjson = new JSONObject(headers);
        ConcurrentHashMap<String, Object> dataMap = new ConcurrentHashMap<>();
        dataMap.put("headers", headersjson.toJSONString());
        JSONObject paramsjson = new JSONObject(request.getParameterMap());
        dataMap.put("params", paramsjson.toJSONString());
        return dataMap;
    }

}
