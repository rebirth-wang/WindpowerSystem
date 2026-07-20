package com.fastbee.ai.service.impl;

import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fastbee.ai.model.dto.AiChatSendRequest;
import com.fastbee.ai.model.enums.AiChatMode;
import com.fastbee.ai.model.vo.AiClarifyOptionVO;
import com.fastbee.ai.model.vo.AiClarifyPayloadVO;
import com.fastbee.ai.model.vo.AiResumeSelectionVO;
import com.fastbee.ai.support.AiClarifyConstants;

class AiClarifySupportServiceImplTest {

    private final AiClarifySupportServiceImpl service = new AiClarifySupportServiceImpl();

    @Test
    void shouldBuildClarifyPayloadWithToolResult() {
        AiClarifyOptionVO option = new AiClarifyOptionVO();
        option.setLabel("一号泵房温度");
        option.setValue("temp");
        option.setDescription("属性");
        option.setType(AiClarifyConstants.CLARIFY_TYPE_THING_MODEL);
        option.setScore(88);

        AiClarifyPayloadVO payload = service.buildPayload(
                AiClarifyConstants.CLARIFY_TYPE_THING_MODEL,
                AiClarifyConstants.CLARIFY_KEY_NL2SQL_THING_MODEL,
                "请选择物模型",
                "命中了多个物模型",
                "nl2sql_clarify_thing_model",
                "查询设备温度",
                List.of(option)
        );

        Assertions.assertEquals(AiClarifyConstants.CLARIFY_KEY_NL2SQL_THING_MODEL, payload.getClarifyKey());
        Assertions.assertFalse(payload.getOptions().isEmpty());
        JSONObject result = JSON.parseObject(payload.getToolResult());
        Assertions.assertEquals(AiClarifyConstants.CLARIFY_KEY_NL2SQL_THING_MODEL, result.getString("clarifyKey"));
        Assertions.assertEquals("查询设备温度", result.getString("resumeQuestion"));
    }

    @Test
    void shouldResolveResumeSelectionAndMode() {
        AiChatSendRequest request = new AiChatSendRequest();
        request.setClarifyKey(AiClarifyConstants.CLARIFY_KEY_DEVICE_CONTROL_DEVICE);
        request.setSelectedValue("SN-001");
        request.setSelectedLabel("一号泵房");
        request.setResumeQuestion("打开泵房设备");

        AiResumeSelectionVO selection = service.resolveResumeSelection(request);

        Assertions.assertEquals(AiChatMode.DEVICE_CONTROL.name(), selection.getEffectiveChatMode());
        Assertions.assertEquals("SN-001", selection.getSelectedValue());
        Assertions.assertEquals("一号泵房", selection.getSelectedLabel());
    }

    @Test
    void shouldBuildResumeExecutionQuestionForDeviceAndThingModel() {
        AiChatSendRequest deviceRequest = new AiChatSendRequest();
        deviceRequest.setClarifyKey(AiClarifyConstants.CLARIFY_KEY_DEVICE_CONTROL_DEVICE);
        deviceRequest.setSelectedValue("SN-001");
        deviceRequest.setSelectedLabel("一号泵房");
        deviceRequest.setResumeQuestion("打开设备");

        String deviceQuestion = service.buildResumeExecutionQuestion(deviceRequest);
        Assertions.assertTrue(deviceQuestion.contains("serialNumber=SN-001"));

        AiChatSendRequest thingModelRequest = new AiChatSendRequest();
        thingModelRequest.setClarifyKey(AiClarifyConstants.CLARIFY_KEY_DEVICE_CONTROL_THING_MODEL);
        thingModelRequest.setSelectedValue("switch");
        thingModelRequest.setSelectedLabel("开关");
        thingModelRequest.setResumeQuestion("打开设备");

        String thingModelQuestion = service.buildResumeExecutionQuestion(thingModelRequest);
        Assertions.assertTrue(thingModelQuestion.contains("identifier=switch"));
    }

    @Test
    void shouldBuildResumeExecutionQuestionForRiskConfirm() {
        AiChatSendRequest request = new AiChatSendRequest();
        request.setClarifyKey(AiClarifyConstants.CLARIFY_KEY_DEVICE_CONTROL_CONFIRM);
        request.setSelectedValue(AiClarifyConstants.CONFIRM_OPTION_CONTINUE);
        request.setSelectedLabel("继续执行");
        request.setResumeQuestion("关闭一号泵房设备");

        String displayMessage = service.buildResumeDisplayMessage(request);
        String executionQuestion = service.buildResumeExecutionQuestion(request);

        Assertions.assertEquals("已确认继续执行高风险设备控制", displayMessage);
        Assertions.assertTrue(executionQuestion.contains("riskConfirmed=1"));
    }

    @Test
    void shouldResolveResumeModeForAutoModeConfirm() {
        AiChatSendRequest request = new AiChatSendRequest();
        request.setClarifyKey(AiClarifyConstants.CLARIFY_KEY_AUTO_MODE_CONFIRM);
        request.setSelectedValue(AiChatMode.NL2SQL.name());
        request.setResumeQuestion("统计在线设备数量");

        AiResumeSelectionVO selection = service.resolveResumeSelection(request);
        String displayMessage = service.buildResumeDisplayMessage(request);
        String executionQuestion = service.buildResumeExecutionQuestion(request);

        Assertions.assertEquals(AiChatMode.NL2SQL.name(), selection.getEffectiveChatMode());
        Assertions.assertEquals("已确认本轮按智能问数继续执行", displayMessage);
        Assertions.assertTrue(executionQuestion.contains("已确认本轮能力：智能问数"));
    }
}
