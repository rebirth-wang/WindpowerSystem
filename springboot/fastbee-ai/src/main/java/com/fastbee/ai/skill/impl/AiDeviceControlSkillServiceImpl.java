package com.fastbee.ai.skill.impl;

import static com.fastbee.ai.support.AiI18nMessageUtils.message;

import jakarta.annotation.Resource;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.stereotype.Service;

import com.fastbee.ai.service.IAiDeviceControlGovernanceService;
import com.fastbee.ai.skill.IAiDeviceControlSkillService;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.core.domin.mq.InvokeReqDto;
import com.fastbee.common.extend.core.domin.mq.MQSendMessageBo;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.data.service.IDeviceMessageService;
import com.fastbee.iot.data.service.IFunctionInvoke;
import com.fastbee.iot.data.service.IRuleEngine;

/**
 * AI 设备控制技能服务实现。
 */
@Service
public class AiDeviceControlSkillServiceImpl implements IAiDeviceControlSkillService {

    @Resource
    private IFunctionInvoke functionInvoke;

    @Resource
    private IDeviceMessageService deviceMessageService;

    @Resource
    private IRuleEngine ruleEngine;

    @Resource
    private IAiDeviceControlGovernanceService aiDeviceControlGovernanceService;

    @Override
    public AjaxResult invokeNoReply(InvokeReqDto reqDto) {
        InvokeReqDto request = normalizeInvokeReq(reqDto);
        aiDeviceControlGovernanceService.validateControlPermission(request.getSerialNumber(), request.getIdentifier());
        return functionInvoke.invokeNoReply(request);
    }

    @Override
    public AjaxResult invokeReply(InvokeReqDto reqDto) {
        InvokeReqDto request = normalizeInvokeReq(reqDto);
        aiDeviceControlGovernanceService.validateControlPermission(request.getSerialNumber(), request.getIdentifier());
        return functionInvoke.invokeReply(request);
    }

    @Override
    public String commandGenerate(MQSendMessageBo messageBo) {
        MQSendMessageBo request = normalizeCommandRequest(messageBo);
        aiDeviceControlGovernanceService.validateControlPermission(request.getSerialNumber(), request.getIdentifier());
        return deviceMessageService.commandGenerate(request);
    }

    @Override
    public void runScene(Long sceneId) {
        if (sceneId == null) {
            throw new ServiceException(message("ai.device.control.scene.id.required"));
        }
        ruleEngine.ruleMatchBySceneId(sceneId);
    }

    private InvokeReqDto normalizeInvokeReq(InvokeReqDto reqDto) {
        if (reqDto == null) {
            throw new ServiceException(message("ai.device.control.params.required"));
        }
        if (StringUtils.isBlank(reqDto.getSerialNumber())) {
            throw new ServiceException(message("ai.device.serial.number.required"));
        }
        if (StringUtils.isBlank(reqDto.getIdentifier())) {
            throw new ServiceException(message("ai.device.identifier.required"));
        }
        if (reqDto.getRemoteCommand() != null && !reqDto.getRemoteCommand().isEmpty()) {
            reqDto.setParams(new JSONObject(reqDto.getRemoteCommand()));
        }
        reqDto.setUserId(resolveLoginUserId());
        return reqDto;
    }

    private MQSendMessageBo normalizeCommandRequest(MQSendMessageBo messageBo) {
        if (messageBo == null) {
            throw new ServiceException(message("ai.device.control.params.required"));
        }
        if (StringUtils.isBlank(messageBo.getSerialNumber())) {
            throw new ServiceException(message("ai.device.serial.number.required"));
        }
        if (messageBo.getParams() == null || messageBo.getParams().isEmpty()) {
            throw new ServiceException(message("ai.device.control.thing.model.params.required"));
        }
        if (StringUtils.isBlank(messageBo.getIdentifier())) {
            String identifier = resolveIdentifierFromParams(messageBo.getParams());
            if (StringUtils.isBlank(identifier)) {
                throw new ServiceException(message("ai.device.identifier.required"));
            }
            messageBo.setIdentifier(identifier);
        }
        return messageBo;
    }

    private String resolveIdentifierFromParams(JSONObject params) {
        if (params == null || params.isEmpty() || params.size() != 1) {
            return null;
        }
        return params.keySet().iterator().next();
    }

    private Long resolveLoginUserId() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            throw new ServiceException(message("ai.device.control.current.user.required"));
        }
        return loginUser.getUserId();
    }
}
