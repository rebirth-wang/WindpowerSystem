package com.fastbee.ai.service.impl;

import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fastbee.ai.domain.AiChatMessage;
import com.fastbee.ai.model.vo.AiChatIntentRouteVO;
import com.fastbee.ai.model.vo.AiDeviceControlContextSnapshotVO;
import com.fastbee.ai.model.vo.AiDeviceControlIntentVO;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.Product;
import com.fastbee.iot.enums.ThingsModelTypeEnum;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;

class AiDeviceControlConversationContextServiceImplTest {

    private final AiDeviceControlConversationContextServiceImpl service = new AiDeviceControlConversationContextServiceImpl();

    @Test
    void shouldResolveLatestContextFromHistory() {
        AiChatMessage message = new AiChatMessage();
        message.setRoleType("user");
        message.setAbilityType("AUTO");
        message.setMessageStatus("SUCCESS");
        message.setToolResult("{\"mode\":\"DEVICE_CONTROL\",\"contextSnapshot\":{\"serialNumber\":\"SN-001\",\"identifier\":\"switch\",\"actionValue\":\"1\"}}");

        AiDeviceControlContextSnapshotVO snapshot = service.resolveLatestContext(List.of(message));

        Assertions.assertNotNull(snapshot);
        Assertions.assertEquals("SN-001", snapshot.getSerialNumber());
        Assertions.assertEquals("switch", snapshot.getIdentifier());
        Assertions.assertEquals("1", snapshot.getActionValue());
    }

    @Test
    void shouldEnrichExecutionQuestionWithHistoryContext() {
        String enriched = service.enrichExecutionQuestion("把它调到 30", null, List.of(buildHistoryMessage("SN-001", "temp", "1")));

        Assertions.assertTrue(enriched.contains("serialNumber=SN-001"));
        Assertions.assertTrue(enriched.contains("identifier=temp"));
        Assertions.assertFalse(enriched.contains("actionValue=1"));
    }

    @Test
    void shouldNotOverrideExplicitCurrentHints() {
        AiChatIntentRouteVO route = new AiChatIntentRouteVO();
        route.setSerialNumberText("SN-NEW");
        route.setThingModelText("humidity");

        String enriched = service.enrichExecutionQuestion("把新设备湿度调到 50", route, List.of(buildHistoryMessage("SN-001", "temp", "1")));

        Assertions.assertFalse(enriched.contains("serialNumber=SN-001"));
        Assertions.assertFalse(enriched.contains("identifier=temp"));
    }

    @Test
    void shouldNotAppendHistoryIdentifierWhenQuestionContainsPossessiveThingModel() {
        String enriched = service.enrichExecutionQuestion("把智能开关的灯光色值设置为77",
                null, List.of(buildHistoryMessage("SN-001", "switch", "0")));

        Assertions.assertFalse(enriched.contains("identifier=switch"));
        Assertions.assertFalse(enriched.contains("actionValue=0"));
    }

    @Test
    void shouldReuseActionValueOnlyForRepeatQuestion() {
        String repeatQuestion = service.enrichExecutionQuestion("再执行一次", null, List.of(buildHistoryMessage("SN-001", "switch", "0")));
        String normalQuestion = service.enrichExecutionQuestion("把它打开", null, List.of(buildHistoryMessage("SN-001", "switch", "0")));

        Assertions.assertTrue(repeatQuestion.contains("actionValue=0"));
        Assertions.assertFalse(normalQuestion.contains("actionValue=0"));
    }

    @Test
    void shouldAttachContextSnapshotWithoutDroppingExistingToolResult() {
        AiChatMessage message = new AiChatMessage();
        message.setToolResult("{\"mode\":\"DEVICE_CONTROL\",\"businessType\":\"DEVICE_PROPERTY_CONTROL\"}");

        AiDeviceControlContextSnapshotVO snapshot = new AiDeviceControlContextSnapshotVO();
        snapshot.setSerialNumber("SN-001");
        snapshot.setIdentifier("switch");
        snapshot.setActionValue("1");

        service.attachContextSnapshot(message, snapshot);

        JSONObject result = JSON.parseObject(message.getToolResult());
        Assertions.assertEquals("DEVICE_CONTROL", result.getString("mode"));
        Assertions.assertEquals("SN-001", result.getJSONObject("contextSnapshot").getString("serialNumber"));
    }

    @Test
    void shouldBuildContextSnapshotFromControlIntent() {
        AiChatIntentRouteVO route = new AiChatIntentRouteVO();
        route.setBusinessType("DEVICE_PROPERTY_CONTROL");
        route.setActionText("打开");

        AiDeviceControlContextSnapshotVO snapshot = service.buildContextSnapshot(route, buildControlIntent(), "1");

        Assertions.assertNotNull(snapshot);
        Assertions.assertEquals("SN-001", snapshot.getSerialNumber());
        Assertions.assertEquals("switch", snapshot.getIdentifier());
        Assertions.assertEquals("1", snapshot.getRiskConfirmed());
    }

    private AiChatMessage buildHistoryMessage(String serialNumber, String identifier, String actionValue) {
        AiChatMessage message = new AiChatMessage();
        message.setRoleType("user");
        message.setAbilityType("DEVICE_CONTROL");
        message.setMessageStatus("SUCCESS");
        message.setToolResult("{\"contextSnapshot\":{\"serialNumber\":\"" + serialNumber
                + "\",\"identifier\":\"" + identifier
                + "\",\"actionValue\":\"" + actionValue + "\"}}");
        return message;
    }

    private AiDeviceControlIntentVO buildControlIntent() {
        Device device = new Device();
        device.setSerialNumber("SN-001");
        device.setDeviceName("一号泵房");

        Product product = new Product();
        product.setProductId(1001L);
        product.setProductName("环境监测产品");

        DeviceMetaData deviceMetaData = new DeviceMetaData();
        deviceMetaData.setDevice(device);
        deviceMetaData.setProduct(product);

        ThingsModelValueItem thingModel = new ThingsModelValueItem()
                .setId("switch")
                .setName("设备开关")
                .setType(ThingsModelTypeEnum.PROPERTY.getCode());

        AiDeviceControlIntentVO controlIntent = new AiDeviceControlIntentVO();
        controlIntent.setDeviceMetaData(deviceMetaData);
        controlIntent.setThingModel(thingModel);
        controlIntent.setActionText("打开");
        controlIntent.setActionValue("1");
        return controlIntent;
    }
}
