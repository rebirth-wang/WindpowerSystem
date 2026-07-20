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
import com.fastbee.iot.domain.ThingsModelTag;
import com.fastbee.iot.mapper.ThingsModelTagMapper;
import com.fastbee.iot.model.vo.ThingsModelTagVO;
import com.fastbee.iot.service.impl.ThingsModelTagServiceImpl;

@DisplayName("物模型标签 Service 单元测试")
class ThingsModelTagServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private ThingsModelTagServiceImpl thingsModelTagService;

    @Mock
    private ThingsModelTagMapper thingsModelTagMapper;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ThingsModelTag.class);
        ReflectionTestUtils.setField(thingsModelTagService, "baseMapper", thingsModelTagMapper);
    }

    @Test
    @DisplayName("queryByIdWithCache - 应返回实体")
    void testQueryByIdWithCache_ShouldReturnEntity() {
        ThingsModelTag entity = new ThingsModelTag();
        when(thingsModelTagMapper.selectById(1L)).thenReturn(entity);

        ThingsModelTag result = thingsModelTagService.queryByIdWithCache(1L);

        assertSame(entity, result);
    }

    @Test
    @DisplayName("pageThingsModelTagVO - 应返回转换后的分页")
    void testPageThingsModelTagVO_ShouldReturnPage() {
        ThingsModelTag query = new ThingsModelTag();
        query.setPageNum(1);
        query.setPageSize(10);
        ThingsModelTag entity = new ThingsModelTag();
        entity.setId(1L);
        Page<ThingsModelTag> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(entity));
        when(thingsModelTagMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<ThingsModelTagVO> result = thingsModelTagService.pageThingsModelTagVO(query);

        assertEquals(1, result.getTotal());
        assertEquals(1L, result.getRecords().get(0).getId());
    }

    @Test
    @DisplayName("insertWithCache - 应保存实体")
    void testInsertWithCache_ShouldSave() {
        ThingsModelTag entity = new ThingsModelTag();
        when(thingsModelTagMapper.insert(entity)).thenReturn(1);

        assertTrue(thingsModelTagService.insertWithCache(entity));
    }

    @Test
    @DisplayName("deleteWithCacheByIds - 应批量删除")
    void testDeleteWithCacheByIds_ShouldRemoveByIds() {
        when(thingsModelTagMapper.deleteByIds(anyList())).thenReturn(1);

        assertTrue(thingsModelTagService.deleteWithCacheByIds(new Long[]{1L}, true));
    }
}
