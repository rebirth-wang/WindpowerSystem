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
import com.fastbee.iot.service.impl.RuleLogServiceImpl;
import com.fastbee.rule.domain.RuleLog;
import com.fastbee.rule.domain.vo.RuleLogVO;
import com.fastbee.rule.mapper.RuleLogMapper;

@DisplayName("规则执行日志 Service 单元测试")
class RuleLogServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private RuleLogServiceImpl ruleLogService;

    @Mock
    private RuleLogMapper ruleLogMapper;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), RuleLog.class);
        ReflectionTestUtils.setField(ruleLogService, "baseMapper", ruleLogMapper);
    }

    @Test
    @DisplayName("queryByIdWithCache - 应返回实体")
    void testQueryByIdWithCache_ShouldReturnEntity() {
        RuleLog entity = new RuleLog();
        when(ruleLogMapper.selectById(1L)).thenReturn(entity);

        RuleLog result = ruleLogService.queryByIdWithCache(1L);

        assertSame(entity, result);
    }

    @Test
    @DisplayName("queryByNodeId - 应只保留匹配节点的调试步骤")
    void testQueryByNodeId_ShouldFilterSteps() {
        RuleLog log = new RuleLog();
        log.setRuleId(10L);
        log.setStepMsg("[{\"tag\":\"node-1\",\"msg\":\"a\"},{\"tag\":\"node-2\",\"msg\":\"b\"}]");
        when(ruleLogMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(log));

        List<RuleLogVO> result = ruleLogService.queryByNodeId(10L, "node-1");

        assertEquals(1, result.size());
        assertTrue(result.get(0).getStepMsg().contains("node-1"));
        assertFalse(result.get(0).getStepMsg().contains("node-2"));
    }

    @Test
    @DisplayName("pageRuleLogVO - 应返回转换后的分页结果")
    void testPageRuleLogVO_ShouldReturnPage() {
        RuleLog query = new RuleLog();
        query.setPageNum(1);
        query.setPageSize(10);
        RuleLog entity = new RuleLog();
        entity.setId(1L);
        Page<RuleLog> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(entity));
        when(ruleLogMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<RuleLogVO> result = ruleLogService.pageRuleLogVO(query);

        assertEquals(1, result.getTotal());
        assertEquals(1L, result.getRecords().get(0).getId());
    }

    @Test
    @DisplayName("deleteWithCacheByIds - 应批量删除")
    void testDeleteWithCacheByIds_ShouldRemoveByIds() {
        when(ruleLogMapper.deleteByIds(anyList())).thenReturn(1);

        assertTrue(ruleLogService.deleteWithCacheByIds(new Long[]{1L}, false));
    }
}
