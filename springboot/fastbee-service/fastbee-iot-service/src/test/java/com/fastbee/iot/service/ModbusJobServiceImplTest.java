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
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.ModbusJob;
import com.fastbee.iot.mapper.ModbusJobMapper;
import com.fastbee.iot.model.vo.ModbusJobVO;
import com.fastbee.iot.service.impl.ModbusJobServiceImpl;

/**
 * {@link ModbusJobServiceImpl} 单元测试
 *
 * @author fastbee
 */
@DisplayName("Modbus 轮询任务 Service 单元测试")
public class ModbusJobServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private ModbusJobServiceImpl modbusJobService;

    @Mock
    private ModbusJobMapper modbusJobMapper;
    @Mock
    private IDeviceJobService deviceJobService;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ModbusJob.class);
        ReflectionTestUtils.setField(modbusJobService, "baseMapper", modbusJobMapper);
    }

    @Test
    @DisplayName("selectModbusJobByTaskId - 应返回任务实体")
    void testSelectModbusJobByTaskId_ShouldReturnEntity() {
        Long id = randomLongId();
        ModbusJob row = new ModbusJob();
        row.setTaskId(id);
        when(modbusJobMapper.selectById(id)).thenReturn(row);

        ModbusJob result = modbusJobService.selectModbusJobByTaskId(id);

        assertNotNull(result);
        assertEquals(id, result.getTaskId());
        verify(modbusJobMapper).selectById(id);
    }

    @Test
    @DisplayName("pageModbusJobVO - 无数据时应返回空 Page")
    void testPageModbusJobVO_NoData_ShouldReturnEmptyPage() {
        ModbusJobVO vo = new ModbusJobVO();
        vo.setPageNum(1);
        vo.setPageSize(10);
        Page<ModbusJob> empty = new Page<>();
        empty.setTotal(0);
        when(modbusJobMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(empty);

        Page<ModbusJobVO> result = modbusJobService.pageModbusJobVO(vo);

        assertNotNull(result);
        assertEquals(0, result.getTotal());
    }

    @Test
    @DisplayName("selectModbusJobList - 无数据时应返回空列表")
    void testSelectModbusJobList_NoData_ShouldReturnEmptyList() {
        ModbusJobVO vo = new ModbusJobVO();
        vo.setSerialNumber(randomString());
        when(modbusJobMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of());

        List<ModbusJobVO> result = modbusJobService.selectModbusJobList(vo);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("updateModbusJob - taskId 非空且 command 为 [] 时应删除")
    void testUpdateModbusJob_TaskIdWithEmptyCommand_ShouldRemove() {
        Long taskId = randomLongId();
        ModbusJob job = new ModbusJob();
        job.setTaskId(taskId);
        job.setCommand("[]");
        when(modbusJobMapper.deleteById(taskId)).thenReturn(1);

        Boolean result = modbusJobService.updateModbusJob(job);

        assertTrue(result);
        verify(modbusJobMapper).deleteById(taskId);
    }

    @Test
    @DisplayName("updateModbusJob - taskId 非空且 command 非 [] 时应更新")
    void testUpdateModbusJob_TaskIdWithCommand_ShouldUpdate() {
        Long taskId = randomLongId();
        ModbusJob job = new ModbusJob();
        job.setTaskId(taskId);
        job.setCommand("{\"a\":1}");
        when(modbusJobMapper.updateById(job)).thenReturn(1);

        Boolean result = modbusJobService.updateModbusJob(job);

        assertTrue(result);
        verify(modbusJobMapper).updateById(job);
    }

    @Test
    @DisplayName("updateModbusJob - taskId 为空时应新增保存")
    void testUpdateModbusJob_NoTaskId_ShouldSave() {
        ModbusJob job = new ModbusJob();
        job.setTaskId(null);
        when(modbusJobMapper.insert(job)).thenReturn(1);

        Boolean result = modbusJobService.updateModbusJob(job);

        assertTrue(result);
        verify(modbusJobMapper).insert(job);
    }

    @Test
    @DisplayName("deleteModbusJobByTaskIds - 应批量删除")
    void testDeleteModbusJobByTaskIds_ShouldBatchDelete() {
        Long[] ids = {randomLongId(), randomLongId()};
        when(modbusJobMapper.deleteBatchIds(any(List.class))).thenReturn(2);

        int rows = modbusJobService.deleteModbusJobByTaskIds(ids);

        assertEquals(2, rows);
        verify(modbusJobMapper).deleteBatchIds(any(List.class));
    }
}

