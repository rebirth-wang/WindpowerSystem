package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
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
import com.fastbee.iot.domain.ModbusParams;
import com.fastbee.iot.domain.ProductSubGateway;
import com.fastbee.iot.mapper.ModbusParamsMapper;
import com.fastbee.iot.mapper.ProductSubGatewayMapper;
import com.fastbee.iot.model.gateWay.ProductSubGatewayAddVO;
import com.fastbee.iot.model.gateWay.ProductSubGatewayVO;
import com.fastbee.iot.service.impl.ProductSubGatewayServiceImpl;

@DisplayName("网关子产品关联 Service 单元测试")
class ProductSubGatewayServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private ProductSubGatewayServiceImpl productSubGatewayService;

    @Mock
    private ProductSubGatewayMapper productSubGatewayMapper;

    @Mock
    private ModbusParamsMapper modbusParamsMapper;

    private MockedStatic<SecurityUtils> securityUtilsMock;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ProductSubGateway.class);
        ReflectionTestUtils.setField(productSubGatewayService, "baseMapper", productSubGatewayMapper);
    }

    @AfterEach
    void tearDown() {
        if (securityUtilsMock != null) {
            securityUtilsMock.close();
        }
    }

    @Test
    @DisplayName("selectProductSubGatewayList - 应返回 mapper 分页结果")
    void testSelectProductSubGatewayList_ShouldReturnPage() {
        ProductSubGateway query = new ProductSubGateway();
        query.setPageNum(1);
        query.setPageSize(10);
        Page<ProductSubGatewayVO> expected = new Page<>();
        when(productSubGatewayMapper.selectListVO(any(Page.class), eq(query))).thenReturn(expected);

        Page<ProductSubGatewayVO> result = productSubGatewayService.selectProductSubGatewayList(query);

        assertSame(expected, result);
    }

    @Test
    @DisplayName("insertWithCache - 应调用 save")
    void testInsertWithCache_ShouldSave() {
        ProductSubGateway entity = new ProductSubGateway();
        when(productSubGatewayMapper.insert(entity)).thenReturn(1);

        assertTrue(productSubGatewayService.insertWithCache(entity));
    }

    @Test
    @DisplayName("addBatch - 空子产品列表时应直接返回")
    void testAddBatch_EmptySubProducts_ShouldReturnOne() {
        ProductSubGatewayAddVO vo = new ProductSubGatewayAddVO();
        vo.setGwProductId(1L);
        vo.setSubProductIds(new ArrayList<>());

        int result = productSubGatewayService.addBatch(vo);

        assertEquals(1, result);
        verifyNoInteractions(productSubGatewayMapper, modbusParamsMapper);
    }

    @Test
    @DisplayName("addBatch - 新增子产品时应带出默认地址并批量插入")
    void testAddBatch_NewSubProduct_ShouldInsertBatch() {
        ProductSubGatewayAddVO vo = new ProductSubGatewayAddVO();
        vo.setGwProductId(1L);
        vo.setSubProductIds(new ArrayList<>(List.of(2L)));

        ModbusParams params = new ModbusParams();
        params.setAddress("01");
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getUsername).thenReturn("tester");
        when(productSubGatewayMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of());
        when(modbusParamsMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(params);
        when(productSubGatewayMapper.insertBatch(anyList())).thenReturn(true);

        int result = productSubGatewayService.addBatch(vo);

        assertEquals(1, result);
        verify(productSubGatewayMapper).insertBatch(argThat(list -> {
            List<ProductSubGateway> gatewayList = new ArrayList<>(list);
            return gatewayList.size() == 1 && "01".equals(gatewayList.get(0).getAddress());
        }));
    }
}
