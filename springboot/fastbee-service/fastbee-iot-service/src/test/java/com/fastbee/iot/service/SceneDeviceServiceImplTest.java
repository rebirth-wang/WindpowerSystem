package com.fastbee.iot.service;

import static com.fastbee.framework.core.util.RandomUtils.randomLongId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.SceneDevice;
import com.fastbee.iot.mapper.SceneDeviceMapper;
import com.fastbee.iot.model.MatchScenes;
import com.fastbee.iot.service.impl.SceneDeviceServiceImpl;

@DisplayName("场景设备 Service 单元测试")
class SceneDeviceServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private SceneDeviceServiceImpl sceneDeviceService;

    @Mock
    private SceneDeviceMapper sceneDeviceMapper;

    @Test
    @DisplayName("selectSceneDeviceList - 应返回 mapper 查询结果")
    void testSelectSceneDeviceList_ShouldReturnMapperResult() {
        SceneDevice query = new SceneDevice();
        List<SceneDevice> expected = List.of(new SceneDevice());
        when(sceneDeviceMapper.selectSceneDeviceList(query)).thenReturn(expected);

        List<SceneDevice> result = sceneDeviceService.selectSceneDeviceList(query);

        assertSame(expected, result);
    }

    @Test
    @DisplayName("deleteSceneDeviceBySceneIds - 应调用批量删除")
    void testDeleteSceneDeviceBySceneIds_ShouldDelegateMapper() {
        Long[] ids = {randomLongId()};
        when(sceneDeviceMapper.deleteSceneDeviceBySceneIds(ids)).thenReturn(1);

        int result = sceneDeviceService.deleteSceneDeviceBySceneIds(ids);

        assertEquals(1, result);
        verify(sceneDeviceMapper).deleteSceneDeviceBySceneIds(ids);
    }

    @Test
    @DisplayName("insertSceneDeviceList - 应返回插入数量")
    void testInsertSceneDeviceList_ShouldDelegateMapper() {
        List<SceneDevice> list = List.of(new SceneDevice(), new SceneDevice());
        when(sceneDeviceMapper.insertSceneDeviceList(list)).thenReturn(2);

        int result = sceneDeviceService.insertSceneDeviceList(list);

        assertEquals(2, result);
        verify(sceneDeviceMapper).insertSceneDeviceList(list);
    }

    @Test
    @DisplayName("selectTriggerDeviceRelateScenes - 应返回关联场景")
    void testSelectTriggerDeviceRelateScenes_ShouldReturnMatches() {
        SceneDevice query = new SceneDevice();
        List<MatchScenes> expected = List.of(new MatchScenes());
        when(sceneDeviceMapper.selectTriggerDeviceRelateScenes(query)).thenReturn(expected);

        List<MatchScenes> result = sceneDeviceService.selectTriggerDeviceRelateScenes(query);

        assertSame(expected, result);
    }
}
