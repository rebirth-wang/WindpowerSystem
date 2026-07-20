package com.fastbee.iot.service;

import static com.fastbee.framework.core.util.RandomUtils.randomLongId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.common.extend.core.domin.entity.SysDept;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.DeviceRecord;
import com.fastbee.iot.domain.Product;
import com.fastbee.iot.mapper.DeviceMapper;
import com.fastbee.iot.mapper.DeviceRecordMapper;
import com.fastbee.iot.mapper.ProductMapper;
import com.fastbee.iot.model.vo.DeviceRecordVO;
import com.fastbee.iot.service.impl.DeviceRecordServiceImpl;
import com.fastbee.system.mapper.SysDeptMapper;

/**
 * {@link DeviceRecordServiceImpl} 单元测试
 *
 * @author fastbee
 */
@DisplayName("设备记录 Service 单元测试")
public class DeviceRecordServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private DeviceRecordServiceImpl deviceRecordService;

    @Mock
    private DeviceRecordMapper deviceRecordMapper;
    @Mock
    private SysDeptMapper sysDeptMapper;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private DeviceMapper deviceMapper;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), DeviceRecord.class);
        ReflectionTestUtils.setField(deviceRecordService, "baseMapper", deviceRecordMapper);
    }

    @Test
    @DisplayName("selectDeviceRecordById - 应返回实体")
    void testSelectDeviceRecordById_ShouldReturnEntity() {
        Long id = randomLongId();
        DeviceRecord row = new DeviceRecord();
        row.setId(id);
        when(deviceRecordMapper.selectById(id)).thenReturn(row);

        DeviceRecord result = deviceRecordService.selectDeviceRecordById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(deviceRecordMapper).selectById(id);
    }

    @Test
    @DisplayName("pageDeviceRecordVO - 无数据时应返回空 Page")
    void testPageDeviceRecordVO_NoData_ShouldReturnEmpty() {
        DeviceRecord query = new DeviceRecord();
        query.setPageNum(1);
        query.setPageSize(10);
        Page<DeviceRecord> empty = new Page<>();
        empty.setTotal(0);
        when(deviceRecordMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(empty);

        Page<DeviceRecordVO> result = deviceRecordService.pageDeviceRecordVO(query);

        assertNotNull(result);
        assertEquals(0, result.getTotal());
    }

    @Test
    @DisplayName("pageDeviceRecordVO - 有数据时应补充租户/产品/设备名称")
    void testPageDeviceRecordVO_WithData_ShouldEnrichNames() {
        Long operateDeptId = 10L;
        Long targetDeptId = 20L;
        Long productId = 30L;
        Long deviceId = 40L;

        DeviceRecord query = new DeviceRecord();
        query.setPageNum(1);
        query.setPageSize(10);

        DeviceRecord record = new DeviceRecord();
        record.setId(randomLongId());
        record.setOperateDeptId(operateDeptId);
        record.setTargetDeptId(targetDeptId);
        record.setProductId(productId);
        record.setDeviceId(deviceId);
        record.setDistributeType(1);

        Page<DeviceRecord> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(record));
        when(deviceRecordMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        SysDept dept1 = new SysDept();
        dept1.setDeptId(operateDeptId);
        dept1.setDeptName("操作机构");
        SysDept dept2 = new SysDept();
        dept2.setDeptId(targetDeptId);
        dept2.setDeptName("目标机构");
        when(sysDeptMapper.selectDeptByIds(anyList())).thenReturn(List.of(dept1, dept2));

        Product product = new Product();
        product.setProductId(productId);
        product.setProductName("产品A");
        when(productMapper.selectBatchIds(anyList())).thenReturn(List.of(product));

        Device device = new Device();
        device.setDeviceId(deviceId);
        device.setDeviceName("设备A");
        when(deviceMapper.selectDeviceListByDeviceIds(anyList())).thenReturn(List.of(device));

        Page<DeviceRecordVO> result = deviceRecordService.pageDeviceRecordVO(query);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        DeviceRecordVO vo = result.getRecords().get(0);
        assertEquals("操作机构", vo.getOperateDeptName());
        assertEquals("目标机构", vo.getTargetDeptName());
        assertEquals("产品A", vo.getProductName());
        assertEquals("设备A", vo.getDeviceName());
        assertNotNull(vo.getDistributeTypeDesc());
    }

    @Test
    @DisplayName("selectDeviceRecordVOList - 无数据时应返回空列表")
    void testSelectDeviceRecordVOList_NoData_ShouldReturnEmptyList() {
        when(deviceRecordMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of());

        List<DeviceRecordVO> result = deviceRecordService.selectDeviceRecordVOList(new DeviceRecord());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("insertDeviceRecord - 应设置 createTime 并插入")
    void testInsertDeviceRecord_ShouldSetCreateTimeAndInsert() {
        DeviceRecord record = new DeviceRecord();
        when(deviceRecordMapper.insert(record)).thenReturn(1);

        int rows = deviceRecordService.insertDeviceRecord(record);

        assertEquals(1, rows);
        assertNotNull(record.getCreateTime());
        verify(deviceRecordMapper).insert(record);
    }

    @Test
    @DisplayName("updateDeviceRecord - 应更新")
    void testUpdateDeviceRecord_ShouldUpdate() {
        DeviceRecord record = new DeviceRecord();
        record.setId(randomLongId());
        when(deviceRecordMapper.updateById(record)).thenReturn(1);

        assertEquals(1, deviceRecordService.updateDeviceRecord(record));
        verify(deviceRecordMapper).updateById(record);
    }

    @Test
    @DisplayName("deleteDeviceRecordByIds - 应批量删除")
    void testDeleteDeviceRecordByIds_ShouldBatchDelete() {
        Long[] ids = {randomLongId(), randomLongId()};
        when(deviceRecordMapper.deleteBatchIds(anyList())).thenReturn(2);

        int rows = deviceRecordService.deleteDeviceRecordByIds(ids);

        assertEquals(2, rows);
        verify(deviceRecordMapper).deleteBatchIds(anyList());
    }

    @Test
    @DisplayName("deleteDeviceRecordById - 应按 ID 删除")
    void testDeleteDeviceRecordById_ShouldDelete() {
        Long id = randomLongId();
        when(deviceRecordMapper.deleteById(id)).thenReturn(1);

        assertEquals(1, deviceRecordService.deleteDeviceRecordById(id));
        verify(deviceRecordMapper).deleteById(id);
    }
}

