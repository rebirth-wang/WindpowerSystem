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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.ThingsModelTranslate;
import com.fastbee.iot.mapper.ThingsModelTranslateMapper;
import com.fastbee.iot.model.vo.ThingsModelTranslateVO;
import com.fastbee.iot.service.impl.ThingsModelTranslateServiceImpl;

@DisplayName("物模型翻译 Service 单元测试")
class ThingsModelTranslateServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private ThingsModelTranslateServiceImpl thingsModelTranslateService;

    @Mock
    private ThingsModelTranslateMapper thingsModelTranslateMapper;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ThingsModelTranslate.class);
        ReflectionTestUtils.setField(thingsModelTranslateService, "baseMapper", thingsModelTranslateMapper);
    }

    @Test
    @DisplayName("selectThingsModelTranslateById - 应返回实体")
    void testSelectThingsModelTranslateById_ShouldReturnEntity() {
        ThingsModelTranslate entity = new ThingsModelTranslate();
        entity.setId(1L);
        when(thingsModelTranslateMapper.selectById(1L)).thenReturn(entity);

        ThingsModelTranslate result = thingsModelTranslateService.selectThingsModelTranslateById(1L);

        assertSame(entity, result);
    }

    @Test
    @DisplayName("pageThingsModelTranslateVO - 应返回转换后的分页")
    void testPageThingsModelTranslateVO_ShouldReturnPage() {
        ThingsModelTranslate query = new ThingsModelTranslate();
        query.setPageNum(1);
        query.setPageSize(10);
        ThingsModelTranslate entity = new ThingsModelTranslate();
        entity.setId(1L);
        Page<ThingsModelTranslate> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(entity));
        when(thingsModelTranslateMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<ThingsModelTranslateVO> result = thingsModelTranslateService.pageThingsModelTranslateVO(query);

        assertEquals(1, result.getTotal());
        assertEquals(1L, result.getRecords().get(0).getId());
    }

    @Test
    @DisplayName("insertWithCache - 应保存数据")
    void testInsertWithCache_ShouldSave() {
        ThingsModelTranslate entity = new ThingsModelTranslate();
        when(thingsModelTranslateMapper.insert(entity)).thenReturn(1);

        Boolean result = thingsModelTranslateService.insertWithCache(entity);

        assertTrue(result);
    }

    @Test
    @DisplayName("deleteWithCacheByIds - 应批量删除")
    void testDeleteWithCacheByIds_ShouldRemoveBatch() {
        when(thingsModelTranslateMapper.deleteByIds(any())).thenReturn(2);

        Boolean result = thingsModelTranslateService.deleteWithCacheByIds(new Long[]{1L, 2L}, false);

        assertTrue(result);
    }
}
