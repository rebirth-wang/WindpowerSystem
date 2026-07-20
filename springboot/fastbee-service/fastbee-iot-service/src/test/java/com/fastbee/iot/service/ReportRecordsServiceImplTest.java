package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
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
import com.fastbee.iot.domain.ReportRecords;
import com.fastbee.iot.mapper.ReportRecordsMapper;
import com.fastbee.iot.model.vo.ReportRecordsVO;
import com.fastbee.iot.service.impl.ReportRecordsServiceImpl;

@DisplayName("报表记录 Service 单元测试")
class ReportRecordsServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private ReportRecordsServiceImpl reportRecordsService;

    @Mock
    private ReportRecordsMapper reportRecordsMapper;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ReportRecords.class);
        ReflectionTestUtils.setField(reportRecordsService, "baseMapper", reportRecordsMapper);
    }

    @Test
    @DisplayName("queryByIdWithCache - 应返回单条记录")
    void testQueryByIdWithCache_ShouldReturnEntity() {
        ReportRecords entity = new ReportRecords();
        when(reportRecordsMapper.selectById(1L)).thenReturn(entity);

        ReportRecords result = reportRecordsService.queryByIdWithCache(1L);

        assertSame(entity, result);
    }

    @Test
    @DisplayName("pageReportRecordsVO - 应调用自定义分页查询")
    void testPageReportRecordsVO_ShouldDelegateMapper() {
        ReportRecordsVO query = new ReportRecordsVO();
        query.setPageNum(1);
        query.setPageSize(10);
        Page<ReportRecordsVO> expected = new Page<>();
        expected.setRecords(List.of(new ReportRecordsVO()));
        when(reportRecordsMapper.selectReportRecordsPage(any(Page.class), eq(query))).thenReturn(expected);

        Page<ReportRecordsVO> result = reportRecordsService.pageReportRecordsVO(query);

        assertSame(expected, result);
    }

    @Test
    @DisplayName("listReportRecordsVO - 应返回转换后的列表")
    void testListReportRecordsVO_ShouldReturnConvertedList() {
        ReportRecords query = new ReportRecords();
        ReportRecords entity = new ReportRecords();
        entity.setId(1L);
        when(reportRecordsMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(entity));

        List<ReportRecordsVO> result = reportRecordsService.listReportRecordsVO(query);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    @DisplayName("insertWithCache - 应保存记录")
    void testInsertWithCache_ShouldSave() {
        ReportRecords entity = new ReportRecords();
        when(reportRecordsMapper.insert(entity)).thenReturn(1);

        assertTrue(reportRecordsService.insertWithCache(entity));
    }

    @Test
    @DisplayName("deleteWithCacheByIds - 应批量删除记录")
    void testDeleteWithCacheByIds_ShouldRemoveByIds() {
        when(reportRecordsMapper.deleteByIds(anyList())).thenReturn(1);

        assertTrue(reportRecordsService.deleteWithCacheByIds(new Long[]{1L}, false));
    }
}
