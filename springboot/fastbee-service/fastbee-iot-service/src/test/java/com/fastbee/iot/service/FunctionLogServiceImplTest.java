package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.cache.ITSLCache;
import com.fastbee.iot.domain.FunctionLog;
import com.fastbee.iot.mapper.DeviceMapper;
import com.fastbee.iot.mapper.FunctionLogMapper;
import com.fastbee.iot.model.param.DataCenterParam;
import com.fastbee.iot.model.vo.FunctionLogVO;
import com.fastbee.iot.service.impl.FunctionLogServiceImpl;

@DisplayName("功能日志 Service 单元测试")
class FunctionLogServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private FunctionLogServiceImpl functionLogService;

    @Mock
    private FunctionLogMapper functionLogMapper;

    @Mock
    private DeviceMapper deviceMapper;

    @Mock
    private ITSLCache itslCache;

    @Test
    @DisplayName("selectFunctionLogList - 无数据时应返回空分页")
    void testSelectFunctionLogList_NoData_ShouldReturnEmptyPage() {
        FunctionLogVO query = new FunctionLogVO();
        query.setPageNum(1);
        query.setPageSize(10);
        Page<FunctionLogVO> page = new Page<>();
        page.setTotal(0);
        when(functionLogMapper.selectFunctionLogList(any(Page.class), eq(query))).thenReturn(page);

        Page<FunctionLogVO> result = functionLogService.selectFunctionLogList(query);

        assertEquals(0, result.getTotal());
        assertTrue(result.getRecords().isEmpty());
        verifyNoInteractions(deviceMapper, itslCache);
    }

    @Test
    @DisplayName("insertFunctionLog - 应设置创建时间")
    void testInsertFunctionLog_ShouldSetCreateTime() {
        FunctionLog log = new FunctionLog();
        when(functionLogMapper.insert(log)).thenReturn(1);

        int result = functionLogService.insertFunctionLog(log);

        assertEquals(1, result);
        assertNotNull(log.getCreateTime());
    }

    @Test
    @DisplayName("countThingsModelInvoke - 时间格式非法时应抛出异常")
    void testCountThingsModelInvoke_InvalidTime_ShouldThrowException() {
        DataCenterParam param = new DataCenterParam();
        param.setBeginTime("bad-time");
        param.setEndTime("2024-01-01 00:00:00");

        assertThrows(IllegalArgumentException.class, () -> functionLogService.countThingsModelInvoke(param));
    }

    @Test
    @DisplayName("selectLogByMessageId - 应根据消息 id 返回日志")
    void testSelectLogByMessageId_ShouldReturnEntity() {
        FunctionLog log = new FunctionLog();
        when(functionLogMapper.selectOne(any())).thenReturn(log);

        FunctionLog result = functionLogService.selectLogByMessageId("msg-1");

        assertSame(log, result);
    }
}
