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
import com.fastbee.iot.domain.SceneTagPoints;
import com.fastbee.iot.mapper.SceneTagPointsMapper;
import com.fastbee.iot.model.vo.SceneTagPointsVO;
import com.fastbee.iot.service.impl.SceneTagPointsServiceImpl;

@DisplayName("场景标签点位 Service 单元测试")
class SceneTagPointsServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private SceneTagPointsServiceImpl sceneTagPointsService;

    @Mock
    private SceneTagPointsMapper sceneTagPointsMapper;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), SceneTagPoints.class);
        ReflectionTestUtils.setField(sceneTagPointsService, "baseMapper", sceneTagPointsMapper);
    }

    @Test
    @DisplayName("queryByIdWithCache - 应返回实体")
    void testQueryByIdWithCache_ShouldReturnEntity() {
        SceneTagPoints entity = new SceneTagPoints();
        when(sceneTagPointsMapper.selectById(1L)).thenReturn(entity);

        SceneTagPoints result = sceneTagPointsService.queryByIdWithCache(1L);

        assertSame(entity, result);
    }

    @Test
    @DisplayName("pageSceneTagPointsVO - 应返回转换后的分页")
    void testPageSceneTagPointsVO_ShouldReturnPage() {
        SceneTagPoints query = new SceneTagPoints();
        query.setPageNum(1);
        query.setPageSize(10);
        SceneTagPoints entity = new SceneTagPoints();
        entity.setId(1L);
        Page<SceneTagPoints> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(entity));
        when(sceneTagPointsMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<SceneTagPointsVO> result = sceneTagPointsService.pageSceneTagPointsVO(query);

        assertEquals(1, result.getTotal());
        assertEquals(1L, result.getRecords().get(0).getId());
    }

    @Test
    @DisplayName("insertWithCache - 应保存实体")
    void testInsertWithCache_ShouldSave() {
        SceneTagPoints entity = new SceneTagPoints();
        when(sceneTagPointsMapper.insert(entity)).thenReturn(1);

        assertTrue(sceneTagPointsService.insertWithCache(entity));
    }

    @Test
    @DisplayName("deleteWithCacheByIds - 应批量删除")
    void testDeleteWithCacheByIds_ShouldRemoveByIds() {
        when(sceneTagPointsMapper.deleteByIds(anyList())).thenReturn(1);

        assertTrue(sceneTagPointsService.deleteWithCacheByIds(new Long[]{1L}, false));
    }
}
