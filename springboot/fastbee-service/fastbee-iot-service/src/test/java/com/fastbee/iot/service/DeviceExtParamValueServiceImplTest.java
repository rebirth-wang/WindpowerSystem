package com.fastbee.iot.service;

import static com.fastbee.framework.core.util.RandomUtils.randomLongId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.DeviceExtParamValue;
import com.fastbee.iot.domain.ProductExtParam;
import com.fastbee.iot.mapper.DeviceExtParamValueMapper;
import com.fastbee.iot.mapper.DeviceMapper;
import com.fastbee.iot.mapper.ProductExtParamMapper;
import com.fastbee.iot.model.vo.DeviceExtParamValueVO;
import com.fastbee.iot.model.vo.ProductExtParamVO;
import com.fastbee.iot.service.impl.DeviceExtParamValueServiceImpl;

/**
 * {@link DeviceExtParamValueServiceImpl} 单元测试
 *
 * <p>跳过依赖 SecurityUtils 的 insert/update 方法，建议由集成测试覆盖。</p>
 *
 * @author fastbee
 */
@DisplayName("设备扩展参数值 Service 单元测试")
@MockitoSettings(strictness = Strictness.LENIENT)
public class DeviceExtParamValueServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    @Spy
    private DeviceExtParamValueServiceImpl deviceExtParamValueService;

    @Mock
    private DeviceExtParamValueMapper deviceExtParamValueMapper;
    @Mock
    private DeviceMapper deviceMapper;
    @Mock
    private ProductExtParamMapper productExtParamMapper;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), DeviceExtParamValue.class);
        ReflectionTestUtils.setField(deviceExtParamValueService, "baseMapper", deviceExtParamValueMapper);
    }

    @Test
    @DisplayName("selectDeviceExtParamValueById - 应返回实体")
    void testSelectDeviceExtParamValueById_ShouldReturnEntity() {
        Long id = randomLongId();
        DeviceExtParamValue row = new DeviceExtParamValue();
        row.setId(id);
        when(deviceExtParamValueMapper.selectById(id)).thenReturn(row);

        DeviceExtParamValue result = deviceExtParamValueService.selectDeviceExtParamValueById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(deviceExtParamValueMapper).selectById(id);
    }

    @Test
    @DisplayName("listDeviceExtParamValueVO - 无数据时应返回空列表")
    void testListDeviceExtParamValueVO_NoData_ShouldReturnEmptyList() {
        DeviceExtParamValue query = new DeviceExtParamValue();
        when(deviceExtParamValueMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of());

        List<DeviceExtParamValueVO> result = deviceExtParamValueService.listDeviceExtParamValueVO(query);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("pageDeviceExtParamValueVO - 设备不存在时应返回空分页")
    void testPageDeviceExtParamValueVO_DeviceNotFound_ShouldReturnEmptyPage() {
        DeviceExtParamValue query = new DeviceExtParamValue();
        query.setDeviceId(randomLongId());
        query.setPageNum(1);
        query.setPageSize(10);
        when(deviceMapper.selectListByDeviceId(anyLong())).thenReturn(List.of());

        Page<ProductExtParamVO> result = deviceExtParamValueService.pageDeviceExtParamValueVO(query);

        assertNotNull(result);
        assertEquals(0, result.getTotal());
        assertNotNull(result.getRecords());
        assertTrue(result.getRecords().isEmpty());
    }

    @Test
    @DisplayName("pageDeviceExtParamValueVO - 设备存在时应返回带 paramValue 的扩展参数分页")
    void testPageDeviceExtParamValueVO_DeviceExists_ShouldMergeValues() {
        Long deviceId = randomLongId();
        Long productId = randomLongId();
        Long paramId = randomLongId();

        DeviceExtParamValue query = new DeviceExtParamValue();
        query.setDeviceId(deviceId);
        query.setPageNum(1);
        query.setPageSize(10);

        Device device = new Device();
        device.setDeviceId(deviceId);
        device.setProductId(productId);
        when(deviceMapper.selectListByDeviceId(deviceId)).thenReturn(List.of(device));

        ProductExtParam param = new ProductExtParam();
        param.setParamId(paramId);
        param.setDefaultValue("def");
        Page<ProductExtParam> paramPage = new Page<>();
        paramPage.setTotal(1);
        paramPage.setRecords(List.of(param));
        when(productExtParamMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(paramPage);

        DeviceExtParamValue value = new DeviceExtParamValue();
        value.setId(randomLongId());
        value.setDeviceId(deviceId);
        value.setParamId(paramId);
        value.setParamValue("v1");
        when(deviceExtParamValueMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(value));

        Page<ProductExtParamVO> result = deviceExtParamValueService.pageDeviceExtParamValueVO(query);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals(paramId, result.getRecords().get(0).getParamId());
        assertEquals("v1", result.getRecords().get(0).getParamValue());
    }



    @Test
    @DisplayName("listDeviceExtParamValueVO - 有数据时应返回 VO 列表")
    void testListDeviceExtParamValueVO_HasData_ShouldReturnList() {
        DeviceExtParamValue query = new DeviceExtParamValue();
        query.setDeviceId(randomLongId());

        DeviceExtParamValue row = new DeviceExtParamValue();
        row.setId(randomLongId());
        row.setDeviceId(query.getDeviceId());
        row.setParamId(randomLongId());
        row.setParamValue("value1");

        when(deviceExtParamValueMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(row));

        List<DeviceExtParamValueVO> result = deviceExtParamValueService.listDeviceExtParamValueVO(query);

        assertEquals(1, result.size());
        assertEquals(row.getId(), result.get(0).getId());
        assertEquals(row.getParamValue(), result.get(0).getParamValue());
    }

//    无法引入上下文对象getLoginUser().getUser()，暂时不测试
//    @Test
//    @DisplayName("insertWithCache - 应保存成功并设置 updateBy")
//    void testInsertWithCache_ShouldSaveSuccess() {
//        DeviceExtParamValue add = new DeviceExtParamValue();
//        add.setDeviceId(randomLongId());
//        add.setParamId(randomLongId());
//        add.setParamValue("test_value");
//
//        doReturn(true).when(deviceExtParamValueService).save(any(DeviceExtParamValue.class));
//
//        Boolean result = deviceExtParamValueService.insertWithCache(add);
//
//        assertTrue(result);
//        assertNotNull(add.getUpdateBy());
//        verify(deviceExtParamValueService).save(add);
//    }

}


