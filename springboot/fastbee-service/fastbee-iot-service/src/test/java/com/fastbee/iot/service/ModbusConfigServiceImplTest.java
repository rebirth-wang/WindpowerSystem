package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.cache.IModbusConfigCache;
import com.fastbee.iot.domain.ModbusConfig;
import com.fastbee.iot.mapper.ModbusConfigMapper;
import com.fastbee.iot.service.impl.ModbusConfigServiceImpl;

@DisplayName("Modbus 配置 Service 单元测试")
class ModbusConfigServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private ModbusConfigServiceImpl modbusConfigService;

    @Mock
    private ModbusConfigMapper modbusConfigMapper;

    @Mock
    private IThingsModelService thingsModelService;

    @Mock
    private IModbusConfigCache modbusConfigCache;

    private MockedStatic<SecurityUtils> securityUtilsMock;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ModbusConfig.class);
        ReflectionTestUtils.setField(modbusConfigService, "baseMapper", modbusConfigMapper);
    }

    @AfterEach
    void tearDown() {
        if (securityUtilsMock != null) {
            securityUtilsMock.close();
        }
    }

    @Test
    @DisplayName("selectModbusConfigList - 应返回分页结果")
    void testSelectModbusConfigList_ShouldReturnPage() {
        ModbusConfig query = new ModbusConfig();
        query.setPageNum(1);
        query.setPageSize(10);
        Page<ModbusConfig> page = new Page<>();
        when(modbusConfigMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<ModbusConfig> result = modbusConfigService.selectModbusConfigList(query);

        assertSame(page, result);
    }

    @Test
    @DisplayName("insertModbusConfig - 应设置创建时间")
    void testInsertModbusConfig_ShouldSetCreateTime() {
        ModbusConfig config = new ModbusConfig();
        when(modbusConfigMapper.insert(config)).thenReturn(1);

        int result = modbusConfigService.insertModbusConfig(config);

        assertEquals(1, result);
        assertNotNull(config.getCreateTime());
    }

    @Test
    @DisplayName("addOrUpModbusConfigBatch - 新增和更新后应刷新缓存")
    void testAddOrUpModbusConfigBatch_ShouldInsertUpdateAndRefreshCache() {
        ModbusConfig add = new ModbusConfig();
        ModbusConfig update = new ModbusConfig();
        update.setId(2L);
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getUsername).thenReturn("tester");
        when(modbusConfigMapper.insert(add)).thenReturn(1);
        when(modbusConfigMapper.updateById(update)).thenReturn(1);

        modbusConfigService.addOrUpModbusConfigBatch(List.of(add, update), 10L, new Long[0]);

        assertEquals(10L, add.getProductId());
        assertEquals("tester", add.getCreateBy());
        assertEquals(10L, update.getProductId());
        assertEquals("tester", update.getUpdateBy());
        verify(modbusConfigCache).setModbusConfigCacheByProductId(10L);
    }

    @Test
    @DisplayName("selectByIdentify - 应按产品和标识查询")
    void testSelectByIdentify_ShouldReturnEntity() {
        ModbusConfig expected = new ModbusConfig();
        when(modbusConfigMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(expected);

        ModbusConfig result = modbusConfigService.selectByIdentify(1L, "temp");

        assertSame(expected, result);
    }
}
