package com.fastbee.iot.service;

import static com.fastbee.framework.core.util.RandomUtils.randomString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import com.fastbee.iot.domain.EventLog;
import com.fastbee.iot.mapper.EventLogMapper;
import com.fastbee.iot.model.HistoryModel;
import com.fastbee.iot.model.vo.EventLogVO;
import com.fastbee.iot.service.impl.EventLogServiceImpl;

/**
 * {@link EventLogServiceImpl} 单元测试
 *
 * @author fastbee
 */
@DisplayName("事件日志 Service 单元测试")
public class EventLogServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private EventLogServiceImpl eventLogService;

    @Mock
    private EventLogMapper eventLogMapper;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), EventLog.class);
        ReflectionTestUtils.setField(eventLogService, "baseMapper", eventLogMapper);
    }

    @Test
    @DisplayName("selectEventLogList - 应返回分页")
    void testSelectEventLogList_ShouldReturnPage() {
        EventLog query = new EventLog();
        query.setPageNum(1);
        query.setPageSize(10);
        query.setSerialNumber(randomString());

        Page<EventLog> page = new Page<>();
        page.setTotal(1);
        when(eventLogMapper.selectPage(any(Page.class), any())).thenReturn(page);

        Page<EventLog> result = eventLogService.selectEventLogList(query);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        verify(eventLogMapper).selectPage(any(Page.class), any());
    }

    @Test
    @DisplayName("deleteEventLogBySerialNumber - 应按 serialNumber 删除")
    void testDeleteEventLogBySerialNumber_ShouldDelete() {
        String serialNumber = randomString();
        when(eventLogMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);

        eventLogService.deleteEventLogBySerialNumber(serialNumber);

        verify(eventLogMapper).delete(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("listHistory - 应委托 Mapper")
    void testListHistory_ShouldDelegate() {
        EventLogVO vo = new EventLogVO();
        HistoryModel h = new HistoryModel();
        when(eventLogMapper.listHistory(vo)).thenReturn(List.of(h));

        List<HistoryModel> result = eventLogService.listHistory(vo);

        assertEquals(1, result.size());
        verify(eventLogMapper).listHistory(vo);
    }
}

