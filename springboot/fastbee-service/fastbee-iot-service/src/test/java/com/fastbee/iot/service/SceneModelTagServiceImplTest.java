package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.quartz.SchedulerException;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.core.redis.RedisKeyBuilder;
import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.cache.SceneModelTagCache;
import com.fastbee.iot.domain.SceneModelTag;
import com.fastbee.iot.mapper.SceneModelDataMapper;
import com.fastbee.iot.mapper.SceneModelDeviceMapper;
import com.fastbee.iot.mapper.SceneModelTagMapper;
import com.fastbee.iot.mapper.SceneTagPointsMapper;
import com.fastbee.iot.model.scenemodel.SceneModelDeviceDataDTO;
import com.fastbee.iot.model.vo.SceneModelTagVO;
import com.fastbee.iot.model.vo.SceneTagPointsVO;
import com.fastbee.iot.service.impl.SceneModelTagServiceImpl;
import com.fastbee.iot.tsdb.service.ILogService;

@DisplayName("场景模型变量 Service 单元测试")
class SceneModelTagServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private SceneModelTagServiceImpl sceneModelTagService;

    @Mock
    private SceneModelTagMapper sceneModelTagMapper;
    @Mock
    private SceneModelDataMapper sceneModelDataMapper;
    @Mock
    private SceneTagPointsMapper sceneTagPointsMapper;
    @Mock
    private SceneModelDeviceMapper sceneModelDeviceMapper;
    @Mock
    private SceneModelTagCache sceneModelTagCache;
    @Mock
    private ILogService logService;
    @Mock
    private IDeviceJobService deviceJobService;
    @Mock
    private IThingsModelService thingsModelService;
    @Mock
    private RedisCache redisCache;

    private MockedStatic<MessageUtils> messageUtilsMock;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), SceneModelTag.class);
        ReflectionTestUtils.setField(sceneModelTagService, "baseMapper", sceneModelTagMapper);
    }

    @AfterEach
    void tearDown() {
        if (messageUtilsMock != null) {
            messageUtilsMock.close();
        }
    }

    @Test
    @DisplayName("selectSceneModelTagById - 应组装变量点位信息")
    void testSelectSceneModelTagById_ShouldAssemblePoints() {
        SceneModelTagVO tagVO = new SceneModelTagVO();
        tagVO.setId(1L);
        tagVO.setSceneModelId(10L);
        com.fastbee.iot.domain.SceneTagPoints point = new com.fastbee.iot.domain.SceneTagPoints();
        point.setSceneModelDataId(100L);
        SceneModelDeviceDataDTO dto = new SceneModelDeviceDataDTO();
        dto.setSceneModelDataId(100L);
        dto.setSceneModelDeviceId(20L);
        dto.setSceneModelDeviceName("设备变量");
        when(sceneModelTagMapper.selectSceneModelTagById(1L)).thenReturn(tagVO);
        when(sceneTagPointsMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(point));
        when(sceneModelDataMapper.selectSceneModelDeviceByDataIdList(List.of(100L))).thenReturn(List.of(dto));

        SceneModelTagVO result = sceneModelTagService.selectSceneModelTagById(1L);

        assertNotNull(result);
        assertEquals(1, result.getTagPointsVOList().size());
        assertEquals(20L, result.getTagPointsVOList().get(0).getSceneModelDeviceId());
        assertEquals("设备变量", result.getTagPointsVOList().get(0).getSceneModelDeviceName());
    }

    @Test
    @DisplayName("checkAliasFormule - 公式为空但变量存在时应返回提示")
    void testCheckAliasFormule_FormulaEmpty_ShouldReturnMessage() {
        SceneModelTagVO tagVO = new SceneModelTagVO();
        tagVO.setAliasFormule("");
        SceneTagPointsVO point = new SceneTagPointsVO();
        point.setAlias("A");
        tagVO.setTagPointsVOList(List.of(point));
        messageUtilsMock = mockStatic(MessageUtils.class);
        messageUtilsMock.when(() -> MessageUtils.message("sceneModel.formula.cannot.empty")).thenReturn("formula empty");

        String result = sceneModelTagService.checkAliasFormule(tagVO);

        assertEquals("formula empty", result);
    }

    @Test
    @DisplayName("updateSceneModelTag - 名称重复时应抛异常")
    void testUpdateSceneModelTag_DuplicateName_ShouldThrowException() {
        SceneModelTagVO tagVO = new SceneModelTagVO();
        tagVO.setId(1L);
        messageUtilsMock = mockStatic(MessageUtils.class);
        messageUtilsMock.when(() -> MessageUtils.message("sceneModel.update.fail.variable.name.exist")).thenReturn("duplicate");
        when(sceneModelTagMapper.checkName(tagVO)).thenReturn(new SceneModelTag());

        assertThrows(ServiceException.class, () -> sceneModelTagService.updateSceneModelTag(tagVO));
    }

    @Test
    @DisplayName("deleteSceneModelTagByIds - 删除成功时应级联删除并清缓存")
    void testDeleteSceneModelTagByIds_ShouldCascadeDelete() throws SchedulerException {
        SceneModelTag tag = new SceneModelTag();
        tag.setId(1L);
        tag.setSceneModelId(10L);
        when(sceneModelTagMapper.selectById(1L)).thenReturn(tag);
        when(sceneModelTagMapper.deleteBatchIds(List.of(1L, 2L))).thenReturn(2);

        int result = sceneModelTagService.deleteSceneModelTagByIds(new Long[]{1L, 2L});

        assertEquals(2, result);
        verify(sceneModelDataMapper).delete(any(LambdaQueryWrapper.class));
        verify(sceneTagPointsMapper).delete(any(LambdaQueryWrapper.class));
        verify(deviceJobService).deleteJobByJobTypeAndDatasourceIds(new Long[]{1L, 2L}, 4);
        verify(redisCache).deleteCacheMapValue(RedisKeyBuilder.buildSceneModelTagCacheKey(10L), "1");
        verify(redisCache).deleteCacheMapValue(RedisKeyBuilder.buildSceneModelTagCacheKey(10L), "2");
    }
}
