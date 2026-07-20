package com.fastbee.iot.service;

import static com.fastbee.framework.core.util.RandomUtils.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
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

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.cache.IDeviceCache;
import com.fastbee.iot.cache.ITSLCache;
import com.fastbee.iot.cache.ITSLValueCache;
import com.fastbee.iot.domain.*;
import com.fastbee.iot.enums.DeviceType;
import com.fastbee.iot.mapper.*;
import com.fastbee.iot.model.ChangeProductStatusModel;
import com.fastbee.iot.model.IdAndName;
import com.fastbee.iot.model.vo.ProductVO;
import com.fastbee.iot.model.vo.SceneDeviceBindVO;
import com.fastbee.iot.service.impl.ProductServiceImpl;
import com.fastbee.iot.service.impl.ThingsModelServiceImpl;
import com.fastbee.system.service.ISysDeptService;

/**
 * {@link ProductServiceImpl} 单元测试
 *
 * @author fastbee
 */
@DisplayName("产品 Service 单元测试")
public class ProductServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private ProductServiceImpl productService;

    private MockedStatic<MessageUtils> messageUtilsMock;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ProductAuthorizeMapper productAuthorizeMapper;

    @Mock
    private RedisCache redisCache;

    @Mock
    private DeviceMapper deviceMapper;

    @Mock
    private IDeviceUpdateService deviceUpdateService;

    @Mock
    private SceneDeviceMapper sceneDeviceMapper;

    @Mock
    private ITSLValueCache thingModelCache;

    @Mock
    private ITSLCache itslCache;

    @Mock
    private ProductSubGatewayMapper productSubGatewayMapper;

    @Mock
    private ProductModbusJobMapper productModbusJobMapper;

    @Mock
    private ThingsModelMapper thingsModelMapper;

    @Mock
    private ModbusParamsMapper modbusParamsMapper;

    @Mock
    private ModbusConfigMapper modbusConfigMapper;

    @Mock
    private IModbusConfigService modbusConfigService;

    @Mock
    private DeviceRecordMapper deviceRecordMapper;

    @Mock
    private ISysDeptService deptService;

    @Mock
    private FirmwareMapper firmwareMapper;

    @Mock
    private SubGatewayMapper subGatewayMapper;

    @Mock
    private ThingsModelServiceImpl thingsModelServiceImpl;

    @Mock
    private CategoryMapper categoryMapper;

    @Mock
    private IDeviceCache deviceCache;

    @Mock
    private IThingsModelService thingsModelService;

    @BeforeEach
    void setUp() {
        // 初始化 MyBatis-Plus Lambda 缓存，解决纯 Mockito 测试中 LambdaWrapper 报错
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), Product.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), Device.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), SubGateway.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), Firmware.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ProductAuthorize.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ProductSubGateway.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ProductModbusJob.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), DeviceRecord.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ModbusParams.class);
        // 将 productMapper 注入到 ServiceImpl 的 baseMapper 字段
        ReflectionTestUtils.setField(productService, "baseMapper", productMapper);
        // Mock MessageUtils.message() 静态方法，避免 SpringUtils 未初始化导致 NPE
        messageUtilsMock = mockStatic(MessageUtils.class);
        messageUtilsMock.when(() -> MessageUtils.message(anyString())).thenReturn("mocked message");
    }

    @AfterEach
    void tearDown() {
        if (messageUtilsMock != null) {
            messageUtilsMock.close();
        }
    }

    // ======================== selectProductByProductId ========================

    @Test
    @DisplayName("selectProductByProductId - 产品存在时，应正常返回产品对象")
    void testSelectProductByProductId_Exists_ShouldReturnProduct() {
        // given
        Long productId = randomLongId();
        Product product = new Product();
        product.setProductId(productId);
        product.setProductName(randomString());
        when(productMapper.selectById(productId)).thenReturn(product);

        // when
        Product result = productService.selectProductByProductId(productId);

        // then
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        verify(productMapper).selectById(productId);
    }

    @Test
    @DisplayName("selectProductByProductId - 产品不存在时，应返回 null")
    void testSelectProductByProductId_NotExists_ShouldReturnNull() {
        // given
        Long productId = randomLongId();
        when(productMapper.selectById(productId)).thenReturn(null);

        // when
        Product result = productService.selectProductByProductId(productId);

        // then
        assertNull(result);
    }

    // ======================== selectDeviceCountByProductId ========================

    @Test
    @DisplayName("selectDeviceCountByProductId - 返回产品下设备数量")
    void testSelectDeviceCountByProductId_ShouldReturnCount() {
        // given
        Long productId = randomLongId();
        when(deviceMapper.selectDeviceCountByProductId(productId)).thenReturn(5);

        // when
        int count = productService.selectDeviceCountByProductId(productId);

        // then
        assertEquals(5, count);
        verify(deviceMapper).selectDeviceCountByProductId(productId);
    }

    // ======================== getProtocolByProductId ========================

    @Test
    @DisplayName("getProtocolByProductId - 产品存在时，应返回协议编号")
    void testGetProtocolByProductId_Exists_ShouldReturnProtocol() {
        // given
        Long productId = randomLongId();
        String protocolCode = "MQTT";
        Product product = new Product();
        product.setProductId(productId);
        product.setProtocolCode(protocolCode);
        // 实现：selectOne 非空时再调 selectById 获取 protocolCode，两个 mock 均需配置
        when(productMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(product);
        when(productMapper.selectById(productId)).thenReturn(product);

        // when
        String result = productService.getProtocolByProductId(productId);

        // then
        assertEquals(protocolCode, result);
        verify(productMapper).selectOne(any(LambdaQueryWrapper.class));
        verify(productMapper).selectById(productId);
    }

    @Test
    @DisplayName("getProtocolByProductId - 产品不存在时，应返回 null")
    void testGetProtocolByProductId_NotExists_ShouldReturnNull() {
        // given
        Long productId = randomLongId();
        when(productMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // when
        String result = productService.getProtocolByProductId(productId);

        // then
        assertNull(result);
    }

    // ======================== updateProduct ========================

    @Test
    @DisplayName("updateProduct - 直连设备正常更新，应返回 1 并设置更新时间")
    void testUpdateProduct_DirectDevice_ShouldReturnOne() {
        // given
        Long productId = randomLongId();
        Product product = new Product();
        product.setProductId(productId);
        product.setProductName("新产品名");
        product.setDeviceType(1); // 直连设备，不走网关分支
        product.setTransport("MQTT");

        Product oldProduct = new Product();
        oldProduct.setProductName("旧产品名");
        oldProduct.setTransport("MQTT");

        when(productMapper.selectById(productId)).thenReturn(oldProduct);
        when(productMapper.updateById(product)).thenReturn(1);

        // when
        int result = productService.updateProduct(product);

        // then
        assertEquals(1, result);
        assertNotNull(product.getUpdateTime());
        verify(productMapper).updateById(product);
        // 直连设备不触发网关相关操作
        verifyNoInteractions(deviceUpdateService);
    }

    @Test
    @DisplayName("updateProduct - 网关设备名称变更，应触发设备名称同步并返回 1")
    void testUpdateProduct_GatewayDevice_NameChanged_ShouldSyncDeviceName() {
        // given
        Long productId = randomLongId();
        Product product = new Product();
        product.setProductId(productId);
        product.setProductName("新网关名");
        product.setDeviceType(2); // 网关设备
        product.setTransport("MQTT");

        Product oldProduct = new Product();
        oldProduct.setProductName("旧网关名");
        oldProduct.setTransport("MQTT"); // transport 未变更，不清除协议缓存

        when(productMapper.selectById(productId)).thenReturn(oldProduct);
        when(productMapper.updateById(product)).thenReturn(1);

        // when
        int result = productService.updateProduct(product);

        // then
        assertEquals(1, result);
        assertNotNull(product.getUpdateTime());
        verify(deviceUpdateService).updateProductNameByProductId(productId, "新网关名");
    }

    @Test
    @DisplayName("updateProduct - 网关传输方式变更时，应清理设备与子设备协议缓存")
    void testUpdateProduct_GatewayTransportChanged_ShouldClearProtocolCache() {
        // given
        Long productId = randomLongId();
        Product product = new Product();
        product.setProductId(productId);
        product.setProductName("网关");
        product.setDeviceType(DeviceType.GATEWAY.getCode());
        product.setTransport("TCP");

        Product oldProduct = new Product();
        oldProduct.setProductName("网关");
        oldProduct.setTransport("MQTT");

        Device device = new Device();
        device.setSerialNumber("device-sn");

        SubGateway subGateway = new SubGateway();
        subGateway.setSubClientId("sub-sn");

        when(productMapper.selectById(productId)).thenReturn(oldProduct);
        when(productMapper.updateById(product)).thenReturn(1);
        when(deviceMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(device));
        when(subGatewayMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(subGateway));

        // when
        int result = productService.updateProduct(product);

        // then
        assertEquals(1, result);
        verify(deviceCache).deleteDeviceProtocolDetailCache("device-sn");
        verify(deviceCache).deleteDeviceProtocolDetailCache("sub-sn");
    }

    // ======================== deleteProductByProductId ========================

    @Test
    @DisplayName("deleteProductByProductId - 应删除产品并清理缓存")
    void testDeleteProductByProductId_ShouldDeleteAndClearCache() {
        // given
        Long productId = randomLongId();
        when(productMapper.deleteById(productId)).thenReturn(1);

        // when
        int result = productService.deleteProductByProductId(productId);

        // then
        assertEquals(1, result);
        verify(redisCache).deleteObject(anyString());
        verify(productMapper).deleteById(productId);
    }

    // ======================== deleteProductByProductIds ========================

    @Test
    @DisplayName("deleteProductByProductIds - 产品下有固件时，应返回错误且不执行删除")
    void testDeleteProductByProductIds_HasFirmware_ShouldReturnError() {
        // given
        Long[] productIds = {randomLongId()};
        // 先清理 TSL 缓存（deleteProductByProductIds 会先删缓存再校验）
        when(firmwareMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        // when
        AjaxResult result = productService.deleteProductByProductIds(productIds);

        // then
        assertNotEquals(200, result.get(AjaxResult.CODE_TAG));
        verify(productMapper, never()).deleteBatchIds(anyList());
        // 有固件时不应检查设备、场景
        verify(deviceMapper, never()).selectCount(any(LambdaQueryWrapper.class));
        verify(sceneDeviceMapper, never()).listSceneProductBind(any());
    }

    @Test
    @DisplayName("deleteProductByProductIds - 产品下有设备时，应返回错误")
    void testDeleteProductByProductIds_HasDevices_ShouldReturnError() {
        // given
        Long[] productIds = {randomLongId()};
        when(firmwareMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(deviceMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(3L);

        // when
        AjaxResult result = productService.deleteProductByProductIds(productIds);

        // then
        assertNotEquals(200, result.get(AjaxResult.CODE_TAG));
        verify(productMapper, never()).deleteBatchIds(anyList());
    }

    @Test
    @DisplayName("deleteProductByProductIds - 产品下有场景联动时，应返回错误")
    void testDeleteProductByProductIds_HasScenes_ShouldReturnError() {
        // given
        Long[] productIds = {randomLongId()};
        SceneDeviceBindVO sceneDeviceBindVO = new SceneDeviceBindVO();
        sceneDeviceBindVO.setSceneName("测试场景");
        when(firmwareMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(deviceMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(sceneDeviceMapper.listSceneProductBind(productIds)).thenReturn(List.of(sceneDeviceBindVO));

        // when
        AjaxResult result = productService.deleteProductByProductIds(productIds);

        // then
        assertNotEquals(200, result.get(AjaxResult.CODE_TAG));
        verify(productMapper, never()).deleteBatchIds(anyList());
    }

    @Test
    @DisplayName("deleteProductByProductIds - 无依赖数据且删除成功，应返回成功并清理关联数据")
    void testDeleteProductByProductIds_NoDependencies_ShouldReturnSuccess() {
        // given
        Long[] productIds = {randomLongId()};
        when(firmwareMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(deviceMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(sceneDeviceMapper.listSceneProductBind(productIds)).thenReturn(Collections.emptyList());
        when(productMapper.deleteBatchIds(anyList())).thenReturn(1);

        // when
        AjaxResult result = productService.deleteProductByProductIds(productIds);

        // then
        assertEquals(200, result.get(AjaxResult.CODE_TAG));
        verify(productMapper).deleteBatchIds(anyList());
        // 删除物模型缓存
        verify(thingsModelServiceImpl).deleteProductThingsModelAndCacheByProductId(productIds[0]);
        // 删除子产品关系、轮询任务、设备分配记录
        verify(productSubGatewayMapper).delete(any(LambdaQueryWrapper.class));
        verify(productModbusJobMapper).delete(any(LambdaQueryWrapper.class));
        verify(deviceRecordMapper).delete(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("deleteProductByProductIds - 无依赖数据但删除失败，应返回错误")
    void testDeleteProductByProductIds_NoDependencies_DeleteFail_ShouldReturnError() {
        // given
        Long[] productIds = {randomLongId()};
        when(firmwareMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(deviceMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(sceneDeviceMapper.listSceneProductBind(productIds)).thenReturn(Collections.emptyList());
        when(productMapper.deleteBatchIds(anyList())).thenReturn(0);

        // when
        AjaxResult result = productService.deleteProductByProductIds(productIds);

        // then
        assertNotEquals(200, result.get(AjaxResult.CODE_TAG));
    }

    // ======================== getProductBySerialNumber ========================

    @Test
    @DisplayName("getProductBySerialNumber - 设备不存在时，应返回 null")
    void testGetProductBySerialNumber_DeviceNotExists_ShouldReturnNull() {
        // given
        String serialNumber = randomString();
        when(deviceMapper.selectDeviceBySerialNumber(serialNumber)).thenReturn(null);

        // when
        Product result = productService.getProductBySerialNumber(serialNumber);

        // then
        assertNull(result);
        verify(productMapper, never()).selectById(any());
    }

    @Test
    @DisplayName("getProductBySerialNumber - 设备存在时，应返回对应产品")
    void testGetProductBySerialNumber_DeviceExists_ShouldReturnProduct() {
        // given
        String serialNumber = randomString();
        Long productId = randomLongId();

        Device device = new Device();
        device.setSerialNumber(serialNumber);
        device.setProductId(productId);

        Product product = new Product();
        product.setProductId(productId);
        product.setProductName(randomString());

        when(deviceMapper.selectDeviceBySerialNumber(serialNumber)).thenReturn(device);
        when(productMapper.selectById(productId)).thenReturn(product);

        // when
        Product result = productService.getProductBySerialNumber(serialNumber);

        // then
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
    }

    // ======================== pageProductVO ========================

    @Test
    @DisplayName("pageProductVO - 无数据时，应返回空分页")
    void testPageProductVO_NoData_ShouldReturnEmptyPage() {
        // given
        ProductVO query = new ProductVO();
        query.setPageNum(1);
        query.setPageSize(10);

        Page<Product> emptyPage = new Page<>();
        emptyPage.setTotal(0);
        when(productMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(emptyPage);

        // when
        Page<ProductVO> result = productService.pageProductVO(query);

        // then
        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertTrue(result.getRecords().isEmpty());
    }

    @Test
    @DisplayName("pageProductVO - 有数据时，应补充组态与可选状态信息")
    void testPageProductVO_WithData_ShouldFillDerivedFields() {
        // given
        ProductVO query = new ProductVO();
        query.setPageNum(1);
        query.setPageSize(10);
        query.setTenantId(99L);
        query.setIsAdmin(false);

        Product product = new Product();
        product.setProductId(1L);
        product.setProductName("产品A");
        product.setGuid("guid-1");
        product.setTenantId(99L);
        product.setDeviceType(DeviceType.DIRECT_DEVICE.getCode());
        product.setProtocolCode("custom");

        Page<Product> productPage = new Page<>();
        productPage.setTotal(1);
        productPage.setRecords(List.of(product));

        ProductVO scadaBinding = new ProductVO();
        scadaBinding.setGuid("guid-1");
        scadaBinding.setScadaId(66L);

        when(productMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(productPage);
        when(productMapper.selectListScadaIdByGuidS(List.of("guid-1"))).thenReturn(List.of(scadaBinding));
        when(modbusParamsMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(Collections.emptyList());

        // when
        Page<ProductVO> result = productService.pageProductVO(query);

        // then
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        ProductVO record = result.getRecords().get(0);
        assertEquals(66L, record.getScadaId());
        assertEquals(1, record.getIsOwner());
        assertTrue(record.getCanSelect());
    }

    // ======================== selectProductShortList ========================

    @Test
    @DisplayName("selectProductShortList - 应返回产品简要列表")
    void testSelectProductShortList_ShouldReturnIdAndNameList() {
        // given
        ProductVO query = new ProductVO();
        query.setDeviceType(DeviceType.DIRECT_DEVICE.getCode());

        Product product1 = new Product();
        product1.setProductId(1L);
        product1.setProductName("产品1");
        Product product2 = new Product();
        product2.setProductId(2L);
        product2.setProductName("产品2");

        when(productMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(product1, product2));

        // when
        List<IdAndName> result = productService.selectProductShortList(query);

        // then
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("产品1", result.get(0).getName());
        assertEquals(2L, result.get(1).getId());
        assertEquals("产品2", result.get(1).getName());
    }

    // ======================== changeProductStatus ========================

    @Test
    @DisplayName("changeProductStatus - 状态值非法（非 1/2），应返回失败")
    void testChangeProductStatus_InvalidStatus_ShouldReturnError() {
        // given
        ChangeProductStatusModel model = new ChangeProductStatusModel();
        model.setProductId(randomLongId());
        model.setStatus(99); // 非法状态

        // when
        AjaxResult result = productService.changeProductStatus(model);

        // then
        assertNotEquals(200, result.get(AjaxResult.CODE_TAG));
        // 非法状态直接返回，不调用任何 mapper
        verifyNoInteractions(productMapper);
    }

    @Test
    @DisplayName("changeProductStatus - 发布产品（status=2）且更新成功，应返回成功并触发缓存更新")
    void testChangeProductStatus_PublishSuccess_ShouldReturnSuccess() {
        // given
        ChangeProductStatusModel model = new ChangeProductStatusModel();
        model.setProductId(randomLongId());
        model.setStatus(2); // 发布
        // updateDeviceStatusByProductIdAsync 异步调用 deviceMapper.selectSerialNumberByProductId
        when(deviceMapper.selectSerialNumberByProductId(anyLong())).thenReturn(Collections.emptyList());
        // this.update(wrapper) 底层走 baseMapper.update()
        when(productMapper.update(any(), any())).thenReturn(1);

        // when
        AjaxResult result = productService.changeProductStatus(model);

        // then
        assertEquals(200, result.get(AjaxResult.CODE_TAG));
        // 发布时应更新物模型缓存
        verify(itslCache).setCacheThingsModelByProductId(model.getProductId());
    }

    @Test
    @DisplayName("changeProductStatus - 取消发布（status=1）且更新成功，应返回成功")
    void testChangeProductStatus_UnpublishSuccess_ShouldReturnSuccess() {
        // given
        ChangeProductStatusModel model = new ChangeProductStatusModel();
        model.setProductId(randomLongId());
        model.setStatus(1); // 取消发布
        when(productMapper.update(any(), any())).thenReturn(1);

        // when
        AjaxResult result = productService.changeProductStatus(model);

        // then
        assertEquals(200, result.get(AjaxResult.CODE_TAG));
        // 取消发布不触发缓存更新
        verifyNoInteractions(itslCache);
    }

    @Test
    @DisplayName("changeProductStatus - 更新数据库失败，应返回失败")
    void testChangeProductStatus_UpdateFail_ShouldReturnError() {
        // given
        ChangeProductStatusModel model = new ChangeProductStatusModel();
        model.setProductId(randomLongId());
        model.setStatus(1);
        when(productMapper.update(any(), any())).thenReturn(0);

        // when
        AjaxResult result = productService.changeProductStatus(model);

        // then
        assertNotEquals(200, result.get(AjaxResult.CODE_TAG));
    }

    // ======================== updateDeviceStatusByProductIdAsync ========================

    @Test
    @DisplayName("updateDeviceStatusByProductIdAsync - 应为产品下所有设备刷新物模型缓存")
    void testUpdateDeviceStatusByProductIdAsync_ShouldRefreshAllDeviceStatus() {
        // given
        Long productId = randomLongId();
        when(deviceMapper.selectSerialNumberByProductId(productId)).thenReturn(List.of("sn-1", "sn-2"));

        // when
        productService.updateDeviceStatusByProductIdAsync(productId);

        // then
        verify(thingModelCache).addCacheDeviceStatus(productId, "sn-1");
        verify(thingModelCache).addCacheDeviceStatus(productId, "sn-2");
    }

    // ======================== selectGuidByProductId ========================

    @Test
    @DisplayName("selectGuidByProductId - 产品存在时，应返回 guid")
    void testSelectGuidByProductId_Exists_ShouldReturnGuid() {
        // given
        Product product = new Product();
        product.setGuid("guid-value");
        when(productMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(product);

        // when
        String result = productService.selectGuidByProductId(randomLongId());

        // then
        assertEquals("guid-value", result);
    }

    @Test
    @DisplayName("selectGuidByProductId - 产品不存在时，应返回 null")
    void testSelectGuidByProductId_NotExists_ShouldReturnNull() {
        // given
        when(productMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        // when
        String result = productService.selectGuidByProductId(randomLongId());

        // then
        assertNull(result);
    }

    // ======================== restoreProduct ========================

    @Test
    @DisplayName("restoreProduct - 产品存在且恢复成功时，应恢复产品与物模型")
    void testRestoreProduct_Success_ShouldRestoreThingsModel() {
        // given
        Long productId = randomLongId();
        Product product = new Product();
        product.setProductId(productId);
        when(productMapper.selectProductById(productId)).thenReturn(product);
        when(productMapper.restoreProduct(productId)).thenReturn(1);

        // when
        AjaxResult result = productService.restoreProduct(productId);

        // then
        assertEquals(200, result.get(AjaxResult.CODE_TAG));
        verify(thingsModelMapper).restoreThingsModelByProductId(productId);
    }

    @Test
    @DisplayName("restoreProduct - 产品不存在时，应返回失败")
    void testRestoreProduct_NotExists_ShouldReturnError() {
        // given
        Long productId = randomLongId();
        when(productMapper.selectProductById(productId)).thenReturn(null);

        // when
        AjaxResult result = productService.restoreProduct(productId);

        // then
        assertNotEquals(200, result.get(AjaxResult.CODE_TAG));
        verify(productMapper, never()).restoreProduct(anyLong());
    }

    // ======================== deleteProductByIds ========================

    @Test
    @DisplayName("deleteProductByIds - 应逐个删除产品并清理物模型")
    void testDeleteProductByIds_ShouldDeleteEachProductAndThingsModel() {
        // given
        Long[] productIds = {1L, 2L};
        when(productMapper.deleteProductById(1L)).thenReturn(1);
        when(productMapper.deleteProductById(2L)).thenReturn(1);

        // when
        int result = productService.deleteProductByIds(productIds);

        // then
        assertEquals(2, result);
        verify(productMapper).deleteProductById(1L);
        verify(productMapper).deleteProductById(2L);
        verify(thingsModelMapper).deleteThingsModelByModelIds(1L);
        verify(thingsModelMapper).deleteThingsModelByModelIds(2L);
    }
}
