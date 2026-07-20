package com.fastbee.ai.service.impl;

import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fastbee.ai.model.enums.AiIntentBusinessType;
import com.fastbee.ai.model.enums.AiIntentThingModelTypeHint;
import com.fastbee.ai.model.vo.AiChatIntentRouteVO;
import com.fastbee.ai.model.vo.AiDeviceControlIntentVO;
import com.fastbee.ai.service.IAiDeviceMetadataService;
import com.fastbee.ai.service.IAiDeviceResolveService;
import com.fastbee.iot.cache.ITSLCache;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.Product;
import com.fastbee.iot.enums.ThingsModelTypeEnum;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.ThingsModelItem.Datatype;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;

/**
 * AI 设备控制意图解析测试。
 */
@ExtendWith(MockitoExtension.class)
class AiDeviceControlIntentServiceImplTest {

    @Mock
    private IAiDeviceResolveService aiDeviceResolveService;

    @Mock
    private IAiDeviceMetadataService aiDeviceMetadataService;

    @Mock
    private ITSLCache itslCache;

    @InjectMocks
    private AiDeviceControlIntentServiceImpl service;

    @Test
    void shouldResolveSwitchThingModelWhenQuestionContainsPossessiveThingModelText() {
        String question = "关闭设备智能开关的开关";
        DeviceMetaData metaData = buildDeviceMetaData();
        when(aiDeviceResolveService.resolveOptionalDeviceMetaData(question)).thenReturn(metaData);
        when(aiDeviceResolveService.listDeviceCandidates(question)).thenReturn(List.of());
        when(itslCache.getThingsModelList(1001L)).thenReturn(List.of(buildBoolModel("switch", "开关")));

        AiDeviceControlIntentVO result = service.resolveIntent(question, buildCloseRoute());

        Assertions.assertNotNull(result.getInvokeRequest());
        Assertions.assertEquals("switch", result.getInvokeRequest().getIdentifier());
        Assertions.assertEquals(0, result.getInvokeRequest().getRemoteCommand().get("switch"));
    }

    @Test
    void shouldUseSingleBooleanThingModelWhenQuestionOnlyContainsCloseDeviceAction() {
        String question = "关闭设备智能开关";
        DeviceMetaData metaData = buildDeviceMetaData();
        when(aiDeviceResolveService.resolveOptionalDeviceMetaData(question)).thenReturn(metaData);
        when(aiDeviceResolveService.listDeviceCandidates(question)).thenReturn(List.of());
        when(itslCache.getThingsModelList(1001L)).thenReturn(List.of(buildBoolModel("switch", "设备开关")));

        AiDeviceControlIntentVO result = service.resolveIntent(question, buildCloseRoute());

        Assertions.assertNotNull(result.getInvokeRequest());
        Assertions.assertEquals("switch", result.getInvokeRequest().getIdentifier());
        Assertions.assertEquals("0", result.getActionValue());
    }

    @Test
    void shouldResolveGenericPossessiveThingModelText() {
        String question = "把设备智能开关的亮度调到50";
        DeviceMetaData metaData = buildDeviceMetaData();
        when(aiDeviceResolveService.resolveOptionalDeviceMetaData(question)).thenReturn(metaData);
        when(aiDeviceResolveService.listDeviceCandidates(question)).thenReturn(List.of());
        when(itslCache.getThingsModelList(1001L)).thenReturn(List.of(buildIntegerModel("brightness", "亮度")));

        AiChatIntentRouteVO route = buildControlRoute("调到", null);
        AiDeviceControlIntentVO result = service.resolveIntent(question, route);

        Assertions.assertNotNull(result.getInvokeRequest());
        Assertions.assertEquals("brightness", result.getInvokeRequest().getIdentifier());
        Assertions.assertEquals("50", result.getActionValue());
    }

    @Test
    void shouldResolveLightColorThingModelAndNumericValueFromSetExpression() {
        String question = "把设备智能开关的灯光色值设置为77";
        DeviceMetaData metaData = buildDeviceMetaData();
        when(aiDeviceResolveService.resolveOptionalDeviceMetaData(question)).thenReturn(metaData);
        when(aiDeviceResolveService.listDeviceCandidates(question)).thenReturn(List.of());
        when(itslCache.getThingsModelList(1001L)).thenReturn(List.of(
                buildBoolModel("switch", "设备开关"),
                buildIntegerModel("light_color", "灯光色值")
        ));

        AiChatIntentRouteVO route = buildControlRoute("设置", null);
        AiDeviceControlIntentVO result = service.resolveIntent(question, route);

        Assertions.assertNotNull(result.getInvokeRequest());
        Assertions.assertEquals("light_color", result.getInvokeRequest().getIdentifier());
        Assertions.assertEquals("77", result.getActionValue());
        Assertions.assertEquals(77, result.getInvokeRequest().getRemoteCommand().get("light_color"));
    }

    @Test
    void shouldReturnMultipleThingModelCandidatesWhenGenericTargetMatchesManyModels() {
        String question = "打开设备智能开关的灯光";
        DeviceMetaData metaData = buildDeviceMetaData();
        when(aiDeviceResolveService.resolveOptionalDeviceMetaData(question)).thenReturn(metaData);
        when(aiDeviceResolveService.listDeviceCandidates(question)).thenReturn(List.of());
        when(itslCache.getThingsModelList(1001L)).thenReturn(List.of(
                buildBoolModel("light_1", "灯光1"),
                buildBoolModel("light_2", "灯光2")
        ));

        AiDeviceControlIntentVO result = service.resolveIntent(question, buildControlRoute("打开", "1"));

        Assertions.assertNull(result.getInvokeRequest());
        Assertions.assertEquals(2, result.getThingModelCandidates().size());
        Assertions.assertEquals("light_1", result.getThingModelCandidates().get(0).getId());
        Assertions.assertEquals("light_2", result.getThingModelCandidates().get(1).getId());
    }

    @Test
    void shouldIgnoreUntrustedRouteThingModelTextWhenQuestionContainsDifferentExplicitTarget() {
        String question = "关闭设备智能开关的开关";
        DeviceMetaData metaData = buildDeviceMetaData();
        when(aiDeviceResolveService.resolveOptionalDeviceMetaData(question)).thenReturn(metaData);
        when(aiDeviceResolveService.listDeviceCandidates(question)).thenReturn(List.of());
        when(itslCache.getThingsModelList(1001L)).thenReturn(List.of(
                buildBoolModel("co2", "二氧化碳"),
                buildBoolModel("switch", "开关")
        ));

        AiChatIntentRouteVO route = buildCloseRoute();
        route.setThingModelText("二氧化碳");
        AiDeviceControlIntentVO result = service.resolveIntent(question, route);

        Assertions.assertNotNull(result.getInvokeRequest());
        Assertions.assertEquals("switch", result.getInvokeRequest().getIdentifier());
    }

    @Test
    void shouldNotExcludeWritablePropertyWhenRouteMistakenlyHintsService() {
        String question = "关闭设备智能开关的开关";
        DeviceMetaData metaData = buildDeviceMetaData();
        when(aiDeviceResolveService.resolveOptionalDeviceMetaData(question)).thenReturn(metaData);
        when(aiDeviceResolveService.listDeviceCandidates(question)).thenReturn(List.of());
        when(itslCache.getThingsModelList(1001L)).thenReturn(List.of(buildBoolModel("switch", "开关")));

        AiChatIntentRouteVO route = buildCloseRoute();
        route.setBusinessType(AiIntentBusinessType.DEVICE_SERVICE_INVOKE.name());
        route.setThingModelTypeHint(AiIntentThingModelTypeHint.SERVICE.name());
        AiDeviceControlIntentVO result = service.resolveIntent(question, route);

        Assertions.assertNotNull(result.getInvokeRequest());
        Assertions.assertEquals("switch", result.getInvokeRequest().getIdentifier());
        Assertions.assertEquals(0, result.getInvokeRequest().getRemoteCommand().get("switch"));
    }

    private AiChatIntentRouteVO buildCloseRoute() {
        return buildControlRoute("关闭", "0");
    }

    private AiChatIntentRouteVO buildControlRoute(String actionText, String actionValue) {
        AiChatIntentRouteVO route = new AiChatIntentRouteVO();
        route.setBusinessType(AiIntentBusinessType.DEVICE_PROPERTY_CONTROL.name());
        route.setThingModelTypeHint(AiIntentThingModelTypeHint.PROPERTY.name());
        route.setDeviceNameText("智能开关");
        route.setActionText(actionText);
        route.setActionValue(actionValue);
        return route;
    }

    private DeviceMetaData buildDeviceMetaData() {
        Device device = new Device();
        device.setSerialNumber("SN-001");
        device.setDeviceName("智能开关");

        Product product = new Product();
        product.setProductId(1001L);
        product.setProductName("智能开关产品");

        DeviceMetaData metaData = new DeviceMetaData();
        metaData.setDevice(device);
        metaData.setProduct(product);
        return metaData;
    }

    private ThingsModelValueItem buildBoolModel(String identifier, String name) {
        Datatype datatype = new Datatype();
        datatype.setType("bool");
        datatype.setTrueText("打开");
        datatype.setFalseText("关闭");

        return buildModel(identifier, name, datatype);
    }

    private ThingsModelValueItem buildIntegerModel(String identifier, String name) {
        Datatype datatype = new Datatype();
        datatype.setType("integer");
        return buildModel(identifier, name, datatype);
    }

    private ThingsModelValueItem buildModel(String identifier, String name, Datatype datatype) {
        ThingsModelValueItem item = new ThingsModelValueItem();
        item.setId(identifier);
        item.setName(name);
        item.setName_zh_CN(name);
        item.setType(ThingsModelTypeEnum.PROPERTY.getCode());
        item.setIsReadonly(0);
        item.setDatatype(datatype);
        return item;
    }
}
