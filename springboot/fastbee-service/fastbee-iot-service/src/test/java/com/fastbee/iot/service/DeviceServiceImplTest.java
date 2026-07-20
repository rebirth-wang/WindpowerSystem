package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;
import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelValuesInput;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.cache.IDeviceCache;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.DeviceLog;
import com.fastbee.iot.domain.DeviceShare;
import com.fastbee.iot.mapper.*;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.ThingsModels.ThingsModelValueItem;
import com.fastbee.iot.model.ThingsModels.ValueItem;
import com.fastbee.iot.model.vo.DeviceUserVO;
import com.fastbee.iot.service.impl.DeviceServiceImpl;
import com.fastbee.iot.tsdb.model.TdLogDto;

@DisplayName("设备 Service 单元测试")
class DeviceServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private DeviceServiceImpl deviceService;

    @Mock
    private DeviceMapper deviceMapper;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private com.fastbee.iot.cache.ITSLValueCache itslValueCache;
    @Mock
    private com.fastbee.common.core.redis.RedisCache redisCache;
    @Mock
    private IDeviceCache deviceCache;
    @Mock
    private IToolService toolService;
    @Mock
    private com.fastbee.system.service.ISysUserService userService;
    @Mock
    private com.fastbee.iot.tsdb.service.ILogService logService;
    @Mock
    private IAlertLogService alertLogService;
    @Mock
    private IOrderControlService orderControlService;
    @Mock
    private IFunctionLogService functionLogService;
    @Mock
    private FunctionLogMapper functionLogMapper;
    @Mock
    private IProductAuthorizeService productAuthorizeService;
    @Mock
    private IDeviceShareService deviceShareService;
    @Mock
    private ISceneService sceneService;
    @Mock
    private ISipRelationService sipRelationService;
    @Mock
    private IThingsModelService thingsModelService;
    @Mock
    private SceneDeviceMapper sceneDeviceMapper;
    @Mock
    private DeviceUserMapper deviceUserMapper;
    @Mock
    private SubGatewayMapper subGatewayMapper;
    @Mock
    private ISubGatewayService subGatewayService;
    @Mock
    private ProductModbusJobMapper productModbusJobMapper;
    @Mock
    private IModbusJobService modbusJobService;
    @Mock
    private IProductSubGatewayService productSubGatewayService;
    @Mock
    private ModbusParamsMapper modbusParamsMapper;
    @Mock
    private com.fastbee.common.extend.config.HttpAuthConfig httpAuthConfig;
    @Mock
    private SceneModelDeviceMapper sceneModelDeviceMapper;
    @Mock
    private AlertLogMapper alertLogMapper;
    @Mock
    private IDeviceUpdateService deviceUpdateService;
    @Mock
    private ICardService cardService;
    @Mock
    private com.fastbee.system.mapper.SysDeptMapper sysDeptMapper;
    @Mock
    private com.fastbee.system.service.ISysDeptService sysDeptService;
    @Mock
    private DeviceRecordMapper deviceRecordMapper;

    private MockedStatic<SecurityUtils> securityUtilsMock;
    private MockedStatic<MessageUtils> messageUtilsMock;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), Device.class);
        ReflectionTestUtils.setField(deviceService, "baseMapper", deviceMapper);
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
    @DisplayName("checkDeviceDataScope - 设备拥有者应直接通过")
    void testCheckDeviceDataScope_Owner_ShouldPass() {
        DeviceUserVO deviceUserVO = new DeviceUserVO();
        deviceUserVO.setUserId(9L);
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getUserId).thenReturn(9L);
        when(deviceUserMapper.selectDeviceUserByDeviceId(1L)).thenReturn(deviceUserVO);

        assertDoesNotThrow(() -> deviceService.checkDeviceDataScope(1L));
    }

    @Test
    @DisplayName("checkDeviceDataScope - 分享用户应直接通过")
    void testCheckDeviceDataScope_SharedUser_ShouldPass() {
        DeviceUserVO deviceUserVO = new DeviceUserVO();
        deviceUserVO.setUserId(8L);
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getUserId).thenReturn(9L);
        when(deviceUserMapper.selectDeviceUserByDeviceId(1L)).thenReturn(deviceUserVO);
        when(deviceShareService.selectDeviceShareByDeviceIdAndUserId(1L, 9L)).thenReturn(new DeviceShare());

        assertDoesNotThrow(() -> deviceService.checkDeviceDataScope(1L));
    }

    @Test
    @DisplayName("checkDeviceDataScope - 无权限时应抛出异常")
    void testCheckDeviceDataScope_NoPermission_ShouldThrowException() {
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        messageUtilsMock = mockStatic(MessageUtils.class);
        securityUtilsMock.when(SecurityUtils::getUserId).thenReturn(9L);
        messageUtilsMock.when(() -> MessageUtils.message("no.operate.permission")).thenReturn("no permission");
        when(deviceUserMapper.selectDeviceUserByDeviceId(1L)).thenReturn(null);
        when(deviceShareService.selectDeviceShareByDeviceIdAndUserId(1L, 9L)).thenReturn(null);

        assertThrows(ServiceException.class, () -> deviceService.checkDeviceDataScope(1L));
    }

    @Test
    @DisplayName("selectShortDeviceBySerialNumber - 命中缓存状态时应回填物模型值")
    void testSelectShortDeviceBySerialNumber_ShouldFillThingsModelValue() {
        Device device = new Device();
        device.setDeviceId(1L);
        device.setProductId(10L);
        device.setSerialNumber("SN001");
        ValueItem valueItem = new ValueItem();
        valueItem.setId("temp");
        valueItem.setValue("26");
        when(deviceMapper.selectShortDeviceBySerialNumber("SN001")).thenReturn(device);
        when(itslValueCache.getCacheDeviceStatus(10L, "SN001")).thenReturn(List.of(valueItem));

        Device result = deviceService.selectShortDeviceBySerialNumber("SN001");

        assertNotNull(result);
        assertNotNull(result.getThingsModelValue());
        assertTrue(result.getThingsModelValue().contains("26"));
    }

    @Test
    @DisplayName("restoreDeviceByDeviceId - 重复设备记录时应返回失败")
    void testRestoreDeviceByDeviceId_Duplicate_ShouldReturnError() {
        Device device1 = new Device();
        Device device2 = new Device();
        messageUtilsMock = mockStatic(MessageUtils.class);
        messageUtilsMock.when(() -> MessageUtils.message("device.duplicate.by.serial.number")).thenReturn("duplicate");
        when(deviceMapper.selectListByDeviceId(1L)).thenReturn(List.of(device1, device2));

        AjaxResult result = deviceService.restoreDeviceByDeviceId(1L);

        assertNotEquals(200, result.get(AjaxResult.CODE_TAG));
    }

    @Test
    @DisplayName("deleteDeviceByIds - 任一设备删除成功时应返回成功")
    void testDeleteDeviceByIds_ShouldReturnSuccessWhenAnyDeleted() {
        when(deviceMapper.deleteByDeviceId(1L)).thenReturn(1);
        when(deviceMapper.deleteByDeviceId(2L)).thenReturn(0);

        AjaxResult result = deviceService.deleteDeviceByIds(new Long[]{1L, 2L});

        assertEquals(200, result.get(AjaxResult.CODE_TAG));
    }

    @Test
    @DisplayName("reportDeviceThingsModelValue - 历史属性日志应按设备号批量写入")
    void testReportDeviceThingsModelValue_ShouldBatchSaveLogs() {
        ThingsModelValuesInput input = new ThingsModelValuesInput();
        input.setProductId(100L);
        input.setDeviceNumber("SN001");
        input.setThingsModelValueRemarkItem(List.of(
                new ThingsModelSimpleItem("temp", "26", "first"),
                new ThingsModelSimpleItem("humidity", "70", "second")
        ));

        ThingsModelValueItem tempModel = new ThingsModelValueItem();
        tempModel.setId("temp");
        tempModel.setName("Temperature");
        tempModel.setType(1);
        tempModel.setIsHistory(1);
        tempModel.setIsMonitor(0);
        when(thingsModelService.getSingleThingModels(100L, "temp")).thenReturn(tempModel);

        ThingsModelValueItem humidityModel = new ThingsModelValueItem();
        humidityModel.setId("humidity");
        humidityModel.setName("Humidity");
        humidityModel.setType(1);
        humidityModel.setIsHistory(1);
        humidityModel.setIsMonitor(0);
        when(thingsModelService.getSingleThingModels(100L, "humidity")).thenReturn(humidityModel);

        Device device = new Device();
        device.setSerialNumber("SN001");
        device.setCreateBy("tester");
        device.setTenantId(9L);
        device.setTenantName("tenant");
        DeviceMetaData metaData = new DeviceMetaData();
        metaData.setDevice(device);
        when(deviceCache.getDeviceMetaDataCache("SN001")).thenReturn(metaData);

        List<ThingsModelSimpleItem> result = deviceService.reportDeviceThingsModelValue(input, 1, false);

        assertEquals(2, result.size());
        ArgumentCaptor<TdLogDto> captor = ArgumentCaptor.forClass(TdLogDto.class);
        verify(logService).saveBatch(captor.capture());
        verify(logService, never()).saveDeviceLog(any(DeviceLog.class));

        TdLogDto dto = captor.getValue();
        assertEquals("SN001", dto.getSerialNumber());
        assertEquals(2, dto.getList().size());
        assertTrue(dto.getList().stream().allMatch(log -> log.getTs() != null));
        assertNotEquals(dto.getList().get(0).getTs(), dto.getList().get(1).getTs());
    }
}
