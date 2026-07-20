package com.fastbee.ai.service.impl;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.ai.config.properties.FastBeeAiProperties;
import com.fastbee.ai.model.vo.AiSemanticFieldVO;
import com.fastbee.ai.service.IAiDeviceMetadataService;
import com.fastbee.ai.service.IAiDeviceResolveService;
import com.fastbee.iot.cache.ITSLCache;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.Product;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;

@ExtendWith(MockitoExtension.class)
class AiThingModelSemanticRuntimeProviderImplTest {

    @Mock
    private IAiDeviceResolveService aiDeviceResolveService;

    @Mock
    private IAiDeviceMetadataService aiDeviceMetadataService;

    @Mock
    private ITSLCache itslCache;

    private AiThingModelSemanticRuntimeProviderImpl service;

    @BeforeEach
    void setUp() {
        service = new AiThingModelSemanticRuntimeProviderImpl();
        ReflectionTestUtils.setField(service, "aiDeviceResolveService", aiDeviceResolveService);
        ReflectionTestUtils.setField(service, "aiDeviceMetadataService", aiDeviceMetadataService);
        ReflectionTestUtils.setField(service, "itslCache", itslCache);

        FastBeeAiProperties properties = new FastBeeAiProperties();
        properties.getNl2sql().setMaxThingModelSemanticMatches(1);
        ReflectionTestUtils.setField(service, "fastBeeAiProperties", properties);
    }

    @Test
    void shouldReturnSelectedThingModelWhenQuestionContainsExplicitIdentifier() {
        DeviceMetaData metaData = buildDeviceMetaData();
        when(aiDeviceResolveService.resolveOptionalDeviceMetaData(anyString())).thenReturn(metaData);
        when(itslCache.getThingsModelList(100L)).thenReturn(List.of(
                buildThingModel("co2", "二氧化碳", 1, 1, 1),
                buildThingModel("array_00_light_color", "灯光色值0", 2, 0, 0)
        ));

        List<AiSemanticFieldVO> fields = service.listSemanticFields(
                "查询智能开关产品10当前灯光色值是多少\n已确认物模型：灯光色值0\nidentifier=array_00_light_color"
        );

        Assertions.assertEquals(1, fields.size());
        Assertions.assertEquals("array_00_light_color", fields.get(0).getSourceCode());
        Assertions.assertEquals("灯光色值0", fields.get(0).getSemanticName());
    }

    @Test
    void shouldTreatDeviceMetricValueQuestionAsRedisRuntimeSemantic() {
        DeviceMetaData metaData = buildDeviceMetaData("远程采集控制设备001", "远程采集控制设备");
        when(aiDeviceResolveService.resolveOptionalDeviceMetaData(anyString())).thenReturn(metaData);
        when(itslCache.getThingsModelList(100L)).thenReturn(List.of(
                buildThingModel("pm10", "PM10", 1, 1, 1),
                buildThingModel("temperature", "空气温度", 1, 1, 1)
        ));

        List<AiSemanticFieldVO> fields = service.listSemanticFields("远程采集控制设备的PM10数值是多少");

        Assertions.assertEquals(1, fields.size());
        Assertions.assertEquals("pm10", fields.get(0).getSourceCode());
        Assertions.assertEquals("REDIS_VALUE", fields.get(0).getDataSourceType());
    }

    @Test
    void shouldNormalizeThingModelTextWithTemplateDecorators() {
        String normalized = ReflectionTestUtils.invokeMethod(service, "normalizeText", "“{温度}”");

        Assertions.assertEquals("温度", normalized);
    }

    private DeviceMetaData buildDeviceMetaData() {
        return buildDeviceMetaData("智能开关产品10", "智能开关产品");
    }

    private DeviceMetaData buildDeviceMetaData(String deviceName, String productName) {
        Device device = new Device();
        device.setDeviceId(10L);
        device.setDeviceName(deviceName);
        device.setSerialNumber("DEVICE88888888");
        device.setProductId(100L);

        Product product = new Product();
        product.setProductId(100L);
        product.setProductName(productName);

        DeviceMetaData metaData = new DeviceMetaData();
        metaData.setDevice(device);
        metaData.setProduct(product);
        return metaData;
    }

    private ThingsModelValueItem buildThingModel(String identifier, String name, Integer type, Integer isReadonly, Integer isHistory) {
        ThingsModelValueItem item = new ThingsModelValueItem();
        item.setId(identifier);
        item.setName(name);
        item.setName_zh_CN(name);
        item.setType(type);
        item.setIsReadonly(isReadonly);
        item.setIsHistory(isHistory);
        return item;
    }
}
