package com.fastbee.ai.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.ai.model.dto.AiChatSendRequest;
import com.fastbee.ai.model.enums.AiIntentBusinessType;
import com.fastbee.ai.model.vo.AiChatIntentRouteVO;
import com.fastbee.ai.model.vo.AiClarifyPayloadVO;
import com.fastbee.ai.model.vo.AiDeviceControlIntentVO;
import com.fastbee.ai.support.AiClarifyConstants;
import com.fastbee.common.extend.core.domin.mq.InvokeReqDto;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.enums.ThingsModelTypeEnum;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;

class AiDeviceControlConfirmServiceImplTest {

    private final AiDeviceControlConfirmServiceImpl service = new AiDeviceControlConfirmServiceImpl();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "aiClarifySupportService", new AiClarifySupportServiceImpl());
    }

    @Test
    void shouldBuildConfirmPayloadForServiceInvoke() {
        AiChatIntentRouteVO route = new AiChatIntentRouteVO();
        route.setBusinessType(AiIntentBusinessType.DEVICE_SERVICE_INVOKE.name());

        AiDeviceControlIntentVO controlIntent = buildControlIntent("一号泵房", "SN-001", "restartService", "重启服务", "1");
        controlIntent.getThingModel().setType(ThingsModelTypeEnum.FUNCTION.getCode());

        AiClarifyPayloadVO payload = service.buildRiskConfirmPayload("调用一号泵房重启服务", route, controlIntent);

        Assertions.assertNotNull(payload);
        Assertions.assertEquals(AiClarifyConstants.CLARIFY_KEY_DEVICE_CONTROL_CONFIRM, payload.getClarifyKey());
        Assertions.assertEquals(2, payload.getOptions().size());
        Assertions.assertTrue(payload.getDisplayContent().contains("服务调用"));
    }

    @Test
    void shouldBuildConfirmPayloadForStopLikePropertyControl() {
        AiChatIntentRouteVO route = new AiChatIntentRouteVO();
        route.setBusinessType(AiIntentBusinessType.DEVICE_PROPERTY_CONTROL.name());
        route.setActionText("关闭");

        AiDeviceControlIntentVO controlIntent = buildControlIntent("二号风机", "SN-002", "switch", "设备开关", "0");
        controlIntent.getThingModel().setType(ThingsModelTypeEnum.PROPERTY.getCode());

        AiClarifyPayloadVO payload = service.buildRiskConfirmPayload("关闭二号风机", route, controlIntent);

        Assertions.assertNotNull(payload);
        Assertions.assertTrue(payload.getDisplayContent().contains("停机类指令"));
    }

    @Test
    void shouldSkipConfirmWhenRiskAlreadyConfirmed() {
        AiChatIntentRouteVO route = new AiChatIntentRouteVO();
        route.setBusinessType(AiIntentBusinessType.DEVICE_PROPERTY_CONTROL.name());

        AiDeviceControlIntentVO controlIntent = buildControlIntent("一号泵房", "SN-001", "switch", "设备开关", "0");

        AiClarifyPayloadVO payload = service.buildRiskConfirmPayload("关闭一号泵房\nriskConfirmed=1", route, controlIntent);

        Assertions.assertNull(payload);
    }

    @Test
    void shouldResolveApproveAndRejectSelection() {
        AiChatSendRequest continueRequest = new AiChatSendRequest();
        continueRequest.setClarifyKey(AiClarifyConstants.CLARIFY_KEY_DEVICE_CONTROL_CONFIRM);
        continueRequest.setSelectedValue(AiClarifyConstants.CONFIRM_OPTION_CONTINUE);

        AiChatSendRequest cancelRequest = new AiChatSendRequest();
        cancelRequest.setClarifyKey(AiClarifyConstants.CLARIFY_KEY_DEVICE_CONTROL_CONFIRM);
        cancelRequest.setSelectedValue(AiClarifyConstants.CONFIRM_OPTION_CANCEL);
        cancelRequest.setSelectedLabel("取消执行");

        Assertions.assertTrue(service.isApprovedSelection(continueRequest));
        Assertions.assertFalse(service.isRejectedSelection(continueRequest));
        Assertions.assertTrue(service.isRejectedSelection(cancelRequest));
        Assertions.assertTrue(service.buildRejectedAnswer(cancelRequest).contains("取消"));
    }

    private AiDeviceControlIntentVO buildControlIntent(String deviceName,
                                                       String serialNumber,
                                                       String identifier,
                                                       String thingModelName,
                                                       String actionValue) {
        Device device = new Device();
        device.setDeviceName(deviceName);
        device.setSerialNumber(serialNumber);

        DeviceMetaData deviceMetaData = new DeviceMetaData();
        deviceMetaData.setDevice(device);

        ThingsModelValueItem thingModel = new ThingsModelValueItem()
                .setId(identifier)
                .setName(thingModelName)
                .setType(ThingsModelTypeEnum.PROPERTY.getCode());

        InvokeReqDto invokeReqDto = new InvokeReqDto();
        invokeReqDto.setSerialNumber(serialNumber);
        invokeReqDto.setIdentifier(identifier);

        AiDeviceControlIntentVO controlIntent = new AiDeviceControlIntentVO();
        controlIntent.setDeviceMetaData(deviceMetaData);
        controlIntent.setThingModel(thingModel);
        controlIntent.setActionText(actionValue);
        controlIntent.setActionValue(actionValue);
        controlIntent.setInvokeRequest(invokeReqDto);
        return controlIntent;
    }
}
