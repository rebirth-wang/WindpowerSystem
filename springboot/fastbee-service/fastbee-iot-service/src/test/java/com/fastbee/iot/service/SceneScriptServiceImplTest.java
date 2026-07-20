package com.fastbee.iot.service;

import static com.fastbee.framework.core.util.RandomUtils.randomLongId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.SceneScript;
import com.fastbee.iot.mapper.SceneScriptMapper;
import com.fastbee.iot.service.impl.SceneScriptServiceImpl;

@DisplayName("场景脚本 Service 单元测试")
class SceneScriptServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private SceneScriptServiceImpl sceneScriptService;

    @Mock
    private SceneScriptMapper sceneScriptMapper;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), SceneScript.class);
        ReflectionTestUtils.setField(sceneScriptService, "baseMapper", sceneScriptMapper);
    }

    @Test
    @DisplayName("insertSceneScript - 应设置创建时间并插入")
    void testInsertSceneScript_ShouldSetCreateTime() {
        SceneScript sceneScript = new SceneScript();
        when(sceneScriptMapper.insert(sceneScript)).thenReturn(1);

        int result = sceneScriptService.insertSceneScript(sceneScript);

        assertEquals(1, result);
        assertNotNull(sceneScript.getCreateTime());
    }

    @Test
    @DisplayName("deleteSceneScriptBySceneIds - 应按场景批量删除")
    void testDeleteSceneScriptBySceneIds_ShouldDeleteByWrapper() {
        when(sceneScriptMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(2);

        int result = sceneScriptService.deleteSceneScriptBySceneIds(new Long[]{1L, 2L});

        assertEquals(2, result);
        verify(sceneScriptMapper).delete(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("insertSceneScriptList - 插入成功时应返回列表长度")
    void testInsertSceneScriptList_Success_ShouldReturnSize() {
        List<SceneScript> list = List.of(new SceneScript(), new SceneScript());
        when(sceneScriptMapper.insertBatch(list)).thenReturn(true);

        int result = sceneScriptService.insertSceneScriptList(list);

        assertEquals(2, result);
    }

    @Test
    @DisplayName("insertSceneScriptList - 插入失败时应返回 0")
    void testInsertSceneScriptList_Fail_ShouldReturnZero() {
        List<SceneScript> list = List.of(new SceneScript());
        when(sceneScriptMapper.insertBatch(list)).thenReturn(false);

        int result = sceneScriptService.insertSceneScriptList(list);

        assertEquals(0, result);
    }

    @Test
    @DisplayName("listSceneScriptByPurpose - 应根据条件查询")
    void testListSceneScriptByPurpose_ShouldSelectByWrapper() {
        List<SceneScript> expected = List.of(new SceneScript());
        when(sceneScriptMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(expected);

        List<SceneScript> result = sceneScriptService.listSceneScriptByPurpose(List.of(randomLongId()), 1);

        assertSame(expected, result);
        verify(sceneScriptMapper).selectList(any(LambdaQueryWrapper.class));
    }
}
