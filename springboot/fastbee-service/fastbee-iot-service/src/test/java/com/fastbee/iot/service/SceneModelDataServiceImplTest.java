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
import com.fastbee.iot.cache.SceneModelTagCache;
import com.fastbee.iot.domain.SceneModelData;
import com.fastbee.iot.mapper.SceneModelDataMapper;
import com.fastbee.iot.mapper.SceneModelDeviceMapper;
import com.fastbee.iot.mapper.SceneModelTagMapper;
import com.fastbee.iot.model.vo.SceneModelDataVO;
import com.fastbee.iot.service.impl.SceneModelDataServiceImpl;

@DisplayName("场景模型数据 Service 单元测试")
class SceneModelDataServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private SceneModelDataServiceImpl sceneModelDataService;

    @Mock
    private SceneModelDataMapper sceneModelDataMapper;
    @Mock
    private SceneModelTagMapper sceneModelTagMapper;
    @Mock
    private SceneModelDeviceMapper sceneModelDeviceMapper;
    @Mock
    private SceneModelTagCache sceneModelTagCache;
    @Mock
    private IDeviceJobService jobService;
    @Mock
    private IThingsModelService thingsModelService;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), SceneModelData.class);
        ReflectionTestUtils.setField(sceneModelDataService, "baseMapper", sceneModelDataMapper);
    }

    @Test
    @DisplayName("selectSceneModelDataById - 应返回实体")
    void testSelectSceneModelDataById_ShouldReturnEntity() {
        SceneModelData entity = new SceneModelData();
        when(sceneModelDataMapper.selectById(1L)).thenReturn(entity);

        SceneModelData result = sceneModelDataService.selectSceneModelDataById(1L);

        assertSame(entity, result);
    }

    @Test
    @DisplayName("pageSceneModelDataVO - 应返回转换后的分页")
    void testPageSceneModelDataVO_ShouldReturnPage() {
        SceneModelData query = new SceneModelData();
        query.setPageNum(1);
        query.setPageSize(10);
        SceneModelData entity = new SceneModelData();
        entity.setId(1L);
        Page<SceneModelData> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(entity));
        when(sceneModelDataMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<SceneModelDataVO> result = sceneModelDataService.pageSceneModelDataVO(query);

        assertEquals(1, result.getTotal());
        assertEquals(1L, result.getRecords().get(0).getId());
    }

    @Test
    @DisplayName("deleteSceneModelDataByIds - 应批量删除")
    void testDeleteSceneModelDataByIds_ShouldDeleteBatch() {
        when(sceneModelDataMapper.deleteBatchIds(anyList())).thenReturn(2);

        int result = sceneModelDataService.deleteSceneModelDataByIds(new Long[]{1L, 2L});

        assertEquals(2, result);
    }

    @Test
    @DisplayName("selectSceneModelDataListByIds - 应批量查询")
    void testSelectSceneModelDataListByIds_ShouldSelectBatch() {
        List<SceneModelData> expected = List.of(new SceneModelData());
        when(sceneModelDataMapper.selectBatchIds(List.of(1L))).thenReturn(expected);

        List<SceneModelData> result = sceneModelDataService.selectSceneModelDataListByIds(List.of(1L));

        assertSame(expected, result);
    }
}
