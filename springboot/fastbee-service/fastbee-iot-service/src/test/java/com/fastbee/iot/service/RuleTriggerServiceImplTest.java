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
import com.fastbee.iot.service.impl.RuleTriggerServiceImpl;
import com.fastbee.rule.domain.RuleTrigger;
import com.fastbee.rule.domain.vo.RuleTriggerVO;
import com.fastbee.rule.mapper.RuleTriggerMapper;

@DisplayName("规则触发条件 Service 单元测试")
class RuleTriggerServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private RuleTriggerServiceImpl ruleTriggerService;

    @Mock
    private RuleTriggerMapper ruleTriggerMapper;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), RuleTrigger.class);
        ReflectionTestUtils.setField(ruleTriggerService, "baseMapper", ruleTriggerMapper);
    }

    @Test
    @DisplayName("queryByIdWithCache - 应返回实体")
    void testQueryByIdWithCache_ShouldReturnEntity() {
        RuleTrigger entity = new RuleTrigger();
        when(ruleTriggerMapper.selectById(1L)).thenReturn(entity);

        RuleTrigger result = ruleTriggerService.queryByIdWithCache(1L);

        assertSame(entity, result);
    }

    @Test
    @DisplayName("pageRuleTriggerVO - 应返回转换后的分页")
    void testPageRuleTriggerVO_ShouldReturnPage() {
        RuleTrigger query = new RuleTrigger();
        query.setPageNum(1);
        query.setPageSize(10);
        RuleTrigger entity = new RuleTrigger();
        entity.setId(1L);
        Page<RuleTrigger> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(entity));
        when(ruleTriggerMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<RuleTriggerVO> result = ruleTriggerService.pageRuleTriggerVO(query);

        assertEquals(1, result.getTotal());
        assertEquals(1L, result.getRecords().get(0).getId());
    }

    @Test
    @DisplayName("insertWithCache - 应保存实体")
    void testInsertWithCache_ShouldSave() {
        RuleTrigger entity = new RuleTrigger();
        when(ruleTriggerMapper.insert(entity)).thenReturn(1);

        assertTrue(ruleTriggerService.insertWithCache(entity));
    }

    @Test
    @DisplayName("deleteWithCacheByIds - 应批量删除")
    void testDeleteWithCacheByIds_ShouldRemoveByIds() {
        when(ruleTriggerMapper.deleteByIds(anyList())).thenReturn(1);

        assertTrue(ruleTriggerService.deleteWithCacheByIds(new Long[]{1L}, true));
    }
}
