package com.fastbee.iot.service;

import static com.fastbee.framework.core.util.RandomUtils.randomLongId;
import static com.fastbee.framework.core.util.RandomUtils.randomString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.common.extend.enums.ModbusParamsPollTypeEnum;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.ModbusParams;
import com.fastbee.iot.mapper.ModbusParamsMapper;
import com.fastbee.iot.mapper.ProductModbusJobMapper;
import com.fastbee.iot.service.impl.ModbusParamsServiceImpl;

/**
 * {@link ModbusParamsServiceImpl} 单元测试
 *
 * @author fastbee
 */
@DisplayName("Modbus 参数 Service 单元测试")
public class ModbusParamsServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    @Spy
    private ModbusParamsServiceImpl modbusParamsService;

    @Mock
    private ModbusParamsMapper modbusParamsMapper;
    @Mock
    private ProductModbusJobMapper productModbusJobMapper;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ModbusParams.class);
        ReflectionTestUtils.setField(modbusParamsService, "baseMapper", modbusParamsMapper);
    }

    @Test
    @DisplayName("selectModbusParamsList - 应返回分页结果")
    void testSelectModbusParamsList_ShouldReturnPage() {
        ModbusParams query = new ModbusParams();
        query.setPageNum(1);
        query.setPageSize(10);
        Page<ModbusParams> page = new Page<>();
        page.setTotal(0);
        when(modbusParamsMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<ModbusParams> result = modbusParamsService.selectModbusParamsList(query);

        assertNotNull(result);
        verify(modbusParamsMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("getModbusParamsByProductId - 应委托 selectOne")
    void testGetModbusParamsByProductId_ShouldSelectOne() {
        Long productId = randomLongId();
        ModbusParams row = new ModbusParams();
        row.setId(randomLongId());
        when(modbusParamsMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(row);

        ModbusParams result = modbusParamsService.getModbusParamsByProductId(productId);

        assertNotNull(result);
        verify(modbusParamsMapper).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("getModbusParamsByClientId - 应委托 Mapper 自定义方法")
    void testGetModbusParamsByClientId_ShouldDelegate() {
        String clientId = randomString();
        ModbusParams row = new ModbusParams();
        when(modbusParamsMapper.getModbusParamsByClientId(clientId)).thenReturn(row);

        assertSame(row, modbusParamsService.getModbusParamsByClientId(clientId));
        verify(modbusParamsMapper).getModbusParamsByClientId(clientId);
    }

    @Test
    @DisplayName("selectModbusParamsListByProductIds - 应按 productIdList 查询")
    void testSelectModbusParamsListByProductIds_ShouldSelectList() {
        List<Long> productIds = List.of(randomLongId(), randomLongId());
        when(modbusParamsMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(new ModbusParams()));

        List<ModbusParams> result = modbusParamsService.selectModbusParamsListByProductIds(productIds);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(modbusParamsMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("addOrUpdate - 已存在配置时应设置 id+updateBy 并 saveOrUpdate")
    void testAddOrUpdate_Exists_ShouldSetIdAndUpdateBy() {
        Long productId = randomLongId();
        ModbusParams input = new ModbusParams();
        input.setProductId(productId);
        input.setPollType(ModbusParamsPollTypeEnum.CLOUD_POLL.getType());

        ModbusParams existed = new ModbusParams();
        existed.setId(randomLongId());

        // getModbusParamsByProductId -> baseMapper.selectOne
        when(modbusParamsMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existed);
        // saveOrUpdate -> 走 baseMapper.updateById 或 insert；这里直接 mock updateById 返回 1
        doReturn(true).when(modbusParamsService).saveOrUpdate(any(ModbusParams.class));

        boolean result = modbusParamsService.addOrUpdate(input);

        assertTrue(result);
        assertEquals(existed.getId(), input.getId());
        assertNotNull(input.getUpdateBy());
        verify(productModbusJobMapper, never()).delete(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("addOrUpdate - 边缘采集时保存成功应删除产品轮询任务配置")
    void testAddOrUpdate_EdgeCollection_ShouldDeletePollingJobs() {
        Long productId = randomLongId();
        ModbusParams input = new ModbusParams();
        input.setProductId(productId);
        input.setPollType(ModbusParamsPollTypeEnum.EDGE_COLLECTION.getType());

        when(modbusParamsMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        doReturn(true).when(modbusParamsService).saveOrUpdate(any(ModbusParams.class));
        when(productModbusJobMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);

        boolean result = modbusParamsService.addOrUpdate(input);

        assertTrue(result);
        assertNotNull(input.getCreateBy());
        verify(productModbusJobMapper).delete(any(LambdaQueryWrapper.class));
    }
}

