package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import java.util.List;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.yomahub.liteflow.property.LiteflowConfigGetter;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.quartz.SchedulerException;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.Alert;
import com.fastbee.iot.domain.AlertScene;
import com.fastbee.iot.domain.DeviceJob;
import com.fastbee.iot.domain.Scene;
import com.fastbee.iot.mapper.AlertMapper;
import com.fastbee.iot.mapper.GroupMapper;
import com.fastbee.iot.mapper.SceneMapper;
import com.fastbee.iot.service.impl.SceneServiceImpl;
import com.fastbee.iot.util.LiteFlowConfig;

@DisplayName("场景联动 Service 单元测试")
class SceneServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private SceneServiceImpl sceneService;

    @Mock
    private RedisCache redisCache;
    @Mock
    private SceneMapper sceneMapper;
    @Mock
    private IDeviceJobService jobService;
    @Mock
    private IScriptService scriptService;
    @Mock
    private ISceneScriptService sceneScriptService;
    @Mock
    private ISceneDeviceService sceneDeviceService;
    @Mock
    private IAlertService alertService;
    @Mock
    private LiteFlowConfig liteFlowConfig;
    @Mock
    private GroupMapper groupMapper;
    @Mock
    private AlertMapper alertMapper;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), Scene.class);
        ReflectionTestUtils.setField(sceneService, "baseMapper", sceneMapper);
        LiteflowConfigGetter.setLiteflowConfig(new com.yomahub.liteflow.property.LiteflowConfig());
    }

    @AfterEach
    void tearDown() {
        LiteflowConfigGetter.clean();
    }

    @Test
    @DisplayName("selectScene - 应委托 Mapper 查询")
    void testSelectScene_ShouldDelegateToMapper() {
        Scene scene = new Scene();
        scene.setSceneId(1L);
        when(sceneMapper.selectById(1L)).thenReturn(scene);

        Scene result = sceneService.selectScene(1L);

        assertSame(scene, result);
    }

    @Test
    @DisplayName("deleteSceneBySceneId - 应级联删除脚本、设备、任务和静默缓存")
    void testDeleteSceneBySceneId_ShouldCascadeDelete() throws Exception {
        Scene scene = new Scene();
        scene.setSceneId(1L);
        scene.setChainName("CHAIN_1");
        when(sceneMapper.selectById(1L)).thenReturn(scene);
        when(sceneMapper.deleteById(1L)).thenReturn(1);

        int result = sceneService.deleteSceneBySceneId(1L);

        assertEquals(1, result);
        verify(scriptService).deleteRuleScriptBySceneIds(aryEq(new Long[]{1L}));
        verify(sceneScriptService).deleteSceneScriptBySceneIds(aryEq(new Long[]{1L}));
        verify(sceneDeviceService).deleteSceneDeviceBySceneIds(aryEq(new Long[]{1L}));
        verify(jobService).deleteJobBySceneIds(aryEq(new Long[]{1L}));
        verify(alertService).deleteAlertByAlertIds(aryEq(new Long[]{1L}));
        verify(redisCache).deleteObject("silent:scene_1");
    }

    @Test
    @DisplayName("updateStatus - 应同步更新关联定时任务状态")
    void testUpdateStatus_ShouldSyncJobs() throws SchedulerException {
        Scene scene = new Scene();
        scene.setSceneId(2L);
        scene.setEnable(1);
        DeviceJob job1 = new DeviceJob();
        job1.setJobId(11L);
        job1.setJobGroup("DEFAULT");
        DeviceJob job2 = new DeviceJob();
        job2.setJobId(12L);
        job2.setJobGroup("DEFAULT");
        when(jobService.listShortJobBySceneId(aryEq(new Long[]{2L}))).thenReturn(List.of(job1, job2));
        when(sceneMapper.updateById(any(Scene.class))).thenReturn(1);

        int result = sceneService.updateStatus(scene);

        assertEquals(1, result);
        ArgumentCaptor<DeviceJob> captor = ArgumentCaptor.forClass(DeviceJob.class);
        verify(jobService, times(2)).changeStatus(captor.capture());
        assertTrue(captor.getAllValues().stream().allMatch(job -> job.getStatus() == 0));
        verify(sceneMapper).updateById((Scene) argThat(Scene -> scene.getSceneId().equals(2L) && scene.getEnable().equals(1)));
    }

    @Test
    @DisplayName("bindAlertToScene - 应过滤已绑定和不存在的告警")
    void testBindAlertToScene_ShouldFilterExistingAndMissingAlerts() {
        AlertScene existing = new AlertScene();
        existing.setAlertId(10L);
        Alert alert = new Alert();
        alert.setAlertId(11L);
        when(alertService.listAlertScene(1L)).thenReturn(List.of(existing));
        when(alertService.getById(11L)).thenReturn(alert);
        when(alertService.getById(12L)).thenReturn(null);
        when(alertMapper.insertAlertSceneList(anyList())).thenReturn(1);

        int result = sceneService.bindAlertToScene(1L, List.of(10L, 11L, 12L));

        assertEquals(1, result);
        verify(alertMapper).insertAlertSceneList(argThat(list -> list.size() == 1
                && list.get(0).getSceneId().equals(1L)
                && list.get(0).getAlertId().equals(11L)));
    }

    @Test
    @DisplayName("unbindAlertFromScene - 参数为空时应直接返回 0")
    void testUnbindAlertFromScene_WithEmptyArgs_ShouldReturnZero() {
        assertEquals(0, sceneService.unbindAlertFromScene(null, List.of(1L)));
        assertEquals(0, sceneService.unbindAlertFromScene(1L, List.of()));
        verifyNoInteractions(alertMapper);
    }
}
