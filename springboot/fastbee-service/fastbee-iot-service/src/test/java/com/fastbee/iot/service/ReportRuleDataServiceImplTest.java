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
import com.fastbee.iot.domain.ReportRuleData;
import com.fastbee.iot.mapper.ReportRuleDataMapper;
import com.fastbee.iot.model.vo.ReportRuleDataVO;
import com.fastbee.iot.service.impl.ReportRuleDataServiceImpl;

@DisplayName("报表规则变量 Service 单元测试")
class ReportRuleDataServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private ReportRuleDataServiceImpl reportRuleDataService;

    @Mock
    private ReportRuleDataMapper reportRuleDataMapper;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ReportRuleData.class);
        ReflectionTestUtils.setField(reportRuleDataService, "baseMapper", reportRuleDataMapper);
    }

    @Test
    @DisplayName("queryByIdWithCache - 应返回单条变量记录")
    void testQueryByIdWithCache_ShouldReturnEntity() {
        ReportRuleData entity = new ReportRuleData();
        when(reportRuleDataMapper.selectById(1L)).thenReturn(entity);

        ReportRuleData result = reportRuleDataService.queryByIdWithCache(1L);

        assertSame(entity, result);
    }

    @Test
    @DisplayName("pageReportRuleDataVO - 应返回转换后的分页结果")
    void testPageReportRuleDataVO_ShouldReturnPage() {
        ReportRuleData query = new ReportRuleData();
        query.setPageNum(1);
        query.setPageSize(10);
        ReportRuleData entity = new ReportRuleData();
        entity.setId(1L);
        Page<ReportRuleData> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(entity));
        when(reportRuleDataMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<ReportRuleDataVO> result = reportRuleDataService.pageReportRuleDataVO(query);

        assertEquals(1, result.getTotal());
        assertEquals(1L, result.getRecords().get(0).getId());
    }

    @Test
    @DisplayName("insertWithCache - 应保存变量配置")
    void testInsertWithCache_ShouldSave() {
        ReportRuleData entity = new ReportRuleData();
        when(reportRuleDataMapper.insert(entity)).thenReturn(1);

        assertTrue(reportRuleDataService.insertWithCache(entity));
    }

    @Test
    @DisplayName("updateWithCache - 应更新变量配置")
    void testUpdateWithCache_ShouldUpdate() {
        ReportRuleData entity = new ReportRuleData();
        entity.setId(1L);
        when(reportRuleDataMapper.updateById(entity)).thenReturn(1);

        assertTrue(reportRuleDataService.updateWithCache(entity));
    }

    @Test
    @DisplayName("deleteWithCacheByIds - 应批量删除变量配置")
    void testDeleteWithCacheByIds_ShouldRemoveByIds() {
        when(reportRuleDataMapper.deleteByIds(anyList())).thenReturn(1);

        assertTrue(reportRuleDataService.deleteWithCacheByIds(new Long[]{1L}, true));
    }
}
