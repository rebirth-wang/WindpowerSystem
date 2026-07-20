package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.ModbusConfig;
import com.fastbee.iot.domain.ProductModbusJob;
import com.fastbee.iot.domain.ProductSubGateway;
import com.fastbee.iot.domain.SubGateway;
import com.fastbee.iot.enums.DeviceType;
import com.fastbee.iot.mapper.ModbusConfigMapper;
import com.fastbee.iot.mapper.ProductModbusJobMapper;
import com.fastbee.iot.mapper.ProductSubGatewayMapper;
import com.fastbee.iot.mapper.SubGatewayMapper;
import com.fastbee.iot.service.impl.ProductModbusJobServiceImpl;

@DisplayName("产品轮询任务 Service 单元测试")
class ProductModbusJobServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private ProductModbusJobServiceImpl productModbusJobService;

    @Mock
    private ProductModbusJobMapper productModbusJobMapper;

    @Mock
    private SubGatewayMapper subGatewayMapper;

    @Mock
    private ModbusConfigMapper modbusConfigMapper;

    @Mock
    private ProductSubGatewayMapper productSubGatewayMapper;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ProductModbusJob.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ModbusConfig.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ProductSubGateway.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), SubGateway.class);
        ReflectionTestUtils.setField(productModbusJobService, "baseMapper", productModbusJobMapper);
    }

    @Test
    @DisplayName("updateWithCache - 空命令数组时应删除任务")
    void testUpdateWithCache_EmptyCommand_ShouldRemoveById() {
        ProductModbusJob job = new ProductModbusJob();
        job.setTaskId(1L);
        job.setCommand("[]");
        when(productModbusJobMapper.deleteById(1L)).thenReturn(1);

        Boolean result = productModbusJobService.updateWithCache(job);

        assertTrue(result);
        verify(productModbusJobMapper).deleteById(1L);
    }

    @Test
    @DisplayName("getAddress - 直连产品时应返回 Modbus 配置地址去重结果")
    void testGetAddress_DirectDevice_ShouldReturnDistinctAddresses() {
        ModbusConfig a = new ModbusConfig();
        a.setAddress("01");
        ModbusConfig b = new ModbusConfig();
        b.setAddress("01");
        ModbusConfig c = new ModbusConfig();
        c.setAddress("02");
        when(modbusConfigMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(a, b, c));

        List<String> result = productModbusJobService.getAddress(1L, null, DeviceType.DIRECT_DEVICE.getCode());

        assertEquals(List.of("01", "02"), result);
    }

    @Test
    @DisplayName("getAddress - 网关且无设备编号时应返回子产品地址")
    void testGetAddress_GatewayWithoutSerial_ShouldReturnSubProductAddresses() {
        ProductSubGateway a = new ProductSubGateway();
        a.setAddress("11");
        ProductSubGateway b = new ProductSubGateway();
        b.setAddress("12");
        when(productSubGatewayMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(a, b));

        List<String> result = productModbusJobService.getAddress(2L, null, DeviceType.GATEWAY.getCode());

        assertEquals(List.of("11", "12"), result);
    }

    @Test
    @DisplayName("getAddress - 网关且有设备编号时应返回网关下子设备地址")
    void testGetAddress_GatewayWithSerial_ShouldReturnSubGatewayAddresses() {
        SubGateway a = new SubGateway();
        a.setAddress("21");
        when(subGatewayMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(a));

        List<String> result = productModbusJobService.getAddress(2L, "gw-sn", DeviceType.GATEWAY.getCode());

        assertEquals(List.of("21"), result);
    }
}
