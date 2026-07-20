package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.ThingsModelTemplateTranslate;
import com.fastbee.iot.mapper.ThingsModelTemplateTranslateMapper;
import com.fastbee.iot.model.vo.ThingsModelTemplateTranslateVO;
import com.fastbee.iot.service.impl.ThingsModelTemplateTranslateServiceImpl;

@DisplayName("物模型模板翻译 Service 单元测试")
class ThingsModelTemplateTranslateServiceImplTest extends BaseMockitoUnitTest {

    @Mock
    private ThingsModelTemplateTranslateMapper thingsModelTemplateTranslateMapper;

    private ThingsModelTemplateTranslateServiceImpl thingsModelTemplateTranslateService;

    @BeforeEach
    void setUp() {
        thingsModelTemplateTranslateService = new ThingsModelTemplateTranslateServiceImpl();
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ThingsModelTemplateTranslate.class);
        ReflectionTestUtils.setField(thingsModelTemplateTranslateService, "baseMapper", thingsModelTemplateTranslateMapper);
    }

    @Test
    @DisplayName("selectThingsModelTemplateTranslateById - 应返回实体")
    void testSelectThingsModelTemplateTranslateById_ShouldReturnEntity() {
        ThingsModelTemplateTranslate entity = new ThingsModelTemplateTranslate();
        entity.setId(1L);
        when(thingsModelTemplateTranslateMapper.selectById(1L)).thenReturn(entity);

        ThingsModelTemplateTranslate result = thingsModelTemplateTranslateService.selectThingsModelTemplateTranslateById(1L);

        assertSame(entity, result);
    }

    @Test
    @DisplayName("pageThingsModelTemplateTranslateVO - 应返回转换后的分页")
    void testPageThingsModelTemplateTranslateVO_ShouldReturnPage() {
        ThingsModelTemplateTranslate query = new ThingsModelTemplateTranslate();
        query.setPageNum(1);
        query.setPageSize(10);
        ThingsModelTemplateTranslate entity = new ThingsModelTemplateTranslate();
        entity.setId(1L);
        Page<ThingsModelTemplateTranslate> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(entity));
        when(thingsModelTemplateTranslateMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<ThingsModelTemplateTranslateVO> result = thingsModelTemplateTranslateService.pageThingsModelTemplateTranslateVO(query);

        assertEquals(1, result.getTotal());
        assertEquals(1L, result.getRecords().get(0).getId());
    }

    @Test
    @DisplayName("insertWithCache - 应保存数据")
    void testInsertWithCache_ShouldSave() {
        ThingsModelTemplateTranslate entity = new ThingsModelTemplateTranslate();
        when(thingsModelTemplateTranslateMapper.insert(entity)).thenReturn(1);

        Boolean result = thingsModelTemplateTranslateService.insertWithCache(entity);

        assertTrue(result);
    }

    @Test
    @DisplayName("deleteWithCacheByIds - 应批量删除")
    void testDeleteWithCacheByIds_ShouldRemoveBatch() {
        when(thingsModelTemplateTranslateMapper.deleteByIds(any())).thenReturn(2);

        Boolean result = thingsModelTemplateTranslateService.deleteWithCacheByIds(new Long[]{1L, 2L}, false);

        assertTrue(result);
    }
}
