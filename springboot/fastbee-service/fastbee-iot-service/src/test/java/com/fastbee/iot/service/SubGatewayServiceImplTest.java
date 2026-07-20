package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.protobuf.ServiceException;
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
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.cache.IDeviceCache;
import com.fastbee.iot.domain.SubGateway;
import com.fastbee.iot.mapper.SubGatewayMapper;
import com.fastbee.iot.model.gateWay.SubDeviceAddVO;
import com.fastbee.iot.model.gateWay.SubDeviceListVO;
import com.fastbee.iot.model.vo.SubGatewayVO;
import com.fastbee.iot.service.impl.SubGatewayServiceImpl;

@DisplayName("子设备关联 Service 单元测试")
class SubGatewayServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private SubGatewayServiceImpl subGatewayService;

    @Mock
    private SubGatewayMapper gatewayMapper;

    @Mock
    private IDeviceCache deviceCache;

    private MockedStatic<SecurityUtils> securityUtilsMock;
    private MockedStatic<MessageUtils> messageUtilsMock;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), SubGateway.class);
        ReflectionTestUtils.setField(subGatewayService, "baseMapper", gatewayMapper);
    }

    @AfterEach
    void tearDown() {
        if (securityUtilsMock != null) {
            securityUtilsMock.close();
        }
        if (messageUtilsMock != null) {
            messageUtilsMock.close();
        }
    }

    @Test
    @DisplayName("selectGatewayList - 应返回分页列表")
    void testSelectGatewayList_ShouldReturnPage() {
        SubGatewayVO query = new SubGatewayVO();
        query.setPageNum(1);
        query.setPageSize(10);
        Page<SubDeviceListVO> expected = new Page<>();
        when(gatewayMapper.selectGatewayList(any(Page.class), eq(query))).thenReturn(expected);

        Page<SubDeviceListVO> result = subGatewayService.selectGatewayList(query);

        assertSame(expected, result);
    }

    @Test
    @DisplayName("insertSubDeviceBatch - 地址重复时应抛异常")
    void testInsertSubDeviceBatch_RepeatAddress_ShouldThrowException() {
        messageUtilsMock = mockStatic(MessageUtils.class);
        messageUtilsMock.when(() -> MessageUtils.message(anyString())).thenReturn("repeat");

        SubDeviceAddVO vo = new SubDeviceAddVO();
        vo.setParentClientId("gw-1");
        vo.setSubDeviceAddInfoVOList(List.of(
                buildSubDevice("sub-1", "01", 1L),
                buildSubDevice("sub-2", "01", 2L)
        ));

        assertThrows(ServiceException.class, () -> subGatewayService.insertSubDeviceBatch(vo));
    }

    @Test
    @DisplayName("insertSubDeviceBatch - 正常时应批量插入并清理缓存")
    void testInsertSubDeviceBatch_Normal_ShouldInsertAndClearCache() throws Exception {
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getUsername).thenReturn("tester");
        SubDeviceAddVO vo = new SubDeviceAddVO();
        vo.setParentClientId("gw-1");
        vo.setParentProductId(9L);
        vo.setSubDeviceAddInfoVOList(List.of(buildSubDevice("sub-1", "01", 1L)));
        when(gatewayMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of());
        when(gatewayMapper.insertBatch(anyList())).thenReturn(true);

        int result = subGatewayService.insertSubDeviceBatch(vo);

        assertEquals(1, result);
        verify(deviceCache).deleteDeviceProtocolDetailCache("sub-1");
        verify(deviceCache).deleteDeviceProtocolDetailCache("gw-1");
    }

    @Test
    @DisplayName("deleteByParentClientId - 有子设备时应删除并清理缓存")
    void testDeleteByParentClientId_WithChildren_ShouldDeleteAndClearCache() {
        SubGateway subGateway = new SubGateway();
        subGateway.setSubClientId("sub-1");
        when(gatewayMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(subGateway));
        when(gatewayMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);

        subGatewayService.deleteByParentClientId("gw-1");

        verify(gatewayMapper).delete(any(LambdaQueryWrapper.class));
        verify(deviceCache).deleteDeviceProtocolDetailCache("sub-1");
        verify(deviceCache).deleteDeviceProtocolDetailCache("gw-1");
    }

    @Test
    @DisplayName("checkRepeatAddress - 产品导入子设备地址重复时应返回提示")
    void testCheckRepeatAddress_Repeat_ShouldReturnMessage() {
        String result = subGatewayService.checkRepeatAddress(List.of(
                buildSubProduct("01"),
                buildSubProduct("01")
        ), null);

        assertNotNull(result);
    }

    private SubDeviceAddVO.SubDeviceAddInfoVO buildSubDevice(String subClientId, String address, Long subProductId) {
        SubDeviceAddVO.SubDeviceAddInfoVO infoVO = new SubDeviceAddVO.SubDeviceAddInfoVO();
        infoVO.setSubClientId(subClientId);
        infoVO.setAddress(address);
        infoVO.setSubProductId(subProductId);
        return infoVO;
    }

    private com.fastbee.iot.model.gateWay.ProductSubDeviceAddVO.SubProduct buildSubProduct(String address) {
        com.fastbee.iot.model.gateWay.ProductSubDeviceAddVO.SubDevice device = new com.fastbee.iot.model.gateWay.ProductSubDeviceAddVO.SubDevice();
        device.setAddress(address);
        com.fastbee.iot.model.gateWay.ProductSubDeviceAddVO.SubProduct product = new com.fastbee.iot.model.gateWay.ProductSubDeviceAddVO.SubProduct();
        product.setSubDeviceList(List.of(device));
        return product;
    }
}
