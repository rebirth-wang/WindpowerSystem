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
import com.fastbee.iot.domain.ReportRule;
import com.fastbee.iot.mapper.ReportRuleMapper;
import com.fastbee.iot.model.vo.ReportRuleVO;
import com.fastbee.iot.service.impl.ReportRuleServiceImpl;

@DisplayName("报表规则 Service 单元测试")
class ReportRuleServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private ReportRuleServiceImpl reportRuleService;

    @Mock
    private ReportRuleMapper reportRuleMapper;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ReportRule.class);
        ReflectionTestUtils.setField(reportRuleService, "baseMapper", reportRuleMapper);
    }

    @Test
    @DisplayName("queryByIdWithCache - 应通过 getById 查询")
    void testQueryByIdWithCache_ShouldReturnEntity() {
        ReportRule entity = new ReportRule();
        when(reportRuleMapper.selectById(1L)).thenReturn(entity);

        ReportRule result = reportRuleService.queryByIdWithCache(1L);

        assertSame(entity, result);
    }

    @Test
    @DisplayName("pageReportRuleVO - 应返回转换后的分页结果")
    void testPageReportRuleVO_ShouldReturnConvertedPage() {
        ReportRule query = new ReportRule();
        query.setPageNum(1);
        query.setPageSize(10);
        ReportRule entity = new ReportRule();
        entity.setId(1L);
        Page<ReportRule> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(entity));
        when(reportRuleMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<ReportRuleVO> result = reportRuleService.pageReportRuleVO(query);

        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals(1L, result.getRecords().get(0).getId());
    }

    @Test
    @DisplayName("insertWithCache - 应调用 save")
    void testInsertWithCache_ShouldSave() {
        ReportRule entity = new ReportRule();
        when(reportRuleMapper.insert(entity)).thenReturn(1);

        Boolean result = reportRuleService.insertWithCache(entity);

        assertTrue(result);
    }

    @Test
    @DisplayName("updateWithCache - 应调用 updateById")
    void testUpdateWithCache_ShouldUpdate() {
        ReportRule entity = new ReportRule();
        entity.setId(1L);
        when(reportRuleMapper.updateById(entity)).thenReturn(1);

        Boolean result = reportRuleService.updateWithCache(entity);

        assertTrue(result);
    }

    @Test
    @DisplayName("deleteWithCacheByIds - 应批量删除")
    void testDeleteWithCacheByIds_ShouldRemoveByIds() {
        when(reportRuleMapper.deleteByIds(anyList())).thenReturn(2);

        Boolean result = reportRuleService.deleteWithCacheByIds(new Long[]{1L, 2L}, true);

        assertTrue(result);
    }
}
