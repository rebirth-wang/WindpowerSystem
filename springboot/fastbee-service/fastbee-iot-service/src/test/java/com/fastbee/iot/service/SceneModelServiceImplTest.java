package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import java.util.List;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.redis.RedisCache;
import com.fastbee.common.core.redis.RedisKeyBuilder;
import com.fastbee.common.extend.core.domin.entity.SysDept;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.enums.scenemodel.SceneModelVariableTypeEnum;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.SceneModel;
import com.fastbee.iot.domain.SceneModelDevice;
import com.fastbee.iot.domain.SceneModelTag;
import com.fastbee.iot.domain.SipRelation;
import com.fastbee.iot.mapper.SceneModelDataMapper;
import com.fastbee.iot.mapper.SceneModelMapper;
import com.fastbee.iot.mapper.SceneModelTagMapper;
import com.fastbee.iot.mapper.SceneTagPointsMapper;
import com.fastbee.iot.model.scenemodel.CusDeviceVO;
import com.fastbee.iot.model.vo.SceneModelDeviceVO;
import com.fastbee.iot.model.vo.SceneModelVO;
import com.fastbee.iot.model.vo.SipRelationVO;
import com.fastbee.iot.service.impl.SceneModelServiceImpl;

@DisplayName("场景模型 Service 单元测试")
class SceneModelServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private SceneModelServiceImpl sceneModelService;

    @Mock
    private SceneModelMapper sceneModelMapper;
    @Mock
    private ISceneModelDeviceService sceneModelDeviceService;
    @Mock
    private SceneModelDataMapper sceneModelDataMapper;
    @Mock
    private SceneModelTagMapper sceneModelTagMapper;
    @Mock
    private SceneTagPointsMapper sceneTagPointsMapper;
    @Mock
    private ISipRelationService sipRelationService;
    @Mock
    private com.fastbee.system.service.ISysDeptService sysDeptService;
    @Mock
    private IDeviceJobService deviceJobService;
    @Mock
    private RedisCache redisCache;
    @Mock
    private ISceneModelTagService sceneModelTagService;

    private MockedStatic<SecurityUtils> securityUtilsMock;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), SceneModel.class);
        ReflectionTestUtils.setField(sceneModelService, "baseMapper", sceneModelMapper);
    }

    @AfterEach
    void tearDown() {
        if (securityUtilsMock != null) {
            securityUtilsMock.close();
        }
    }

    @Test
    @DisplayName("selectSceneModelBySceneModelId - 应组装关联设备和监控关系")
    void testSelectSceneModelBySceneModelId_ShouldAssembleRelations() {
        SceneModel query = new SceneModel();
        query.setSceneModelId(1L);
        SceneModelVO sceneModelVO = new SceneModelVO();
        sceneModelVO.setSceneModelId(1L);
        when(sceneModelMapper.selectSceneModelBySceneModelId(query)).thenReturn(sceneModelVO);
        Page<SipRelationVO> sipPage = new Page<>();
        SipRelationVO sipRelationVO = new SipRelationVO();
        sipPage.setRecords(List.of(sipRelationVO));
        when(sipRelationService.selectSipRelationList(any(SipRelation.class))).thenReturn(sipPage);
        SceneModelDeviceVO thingsDevice = new SceneModelDeviceVO();
        thingsDevice.setVariableType(SceneModelVariableTypeEnum.THINGS_MODEL.getType());
        thingsDevice.setName("设备A");
        thingsDevice.setProductId(11L);
        thingsDevice.setSerialNumber("SN001");
        when(sceneModelDeviceService.listSceneModelDeviceVO(any(SceneModelDevice.class))).thenReturn(List.of(thingsDevice));

        SceneModelVO result = sceneModelService.selectSceneModelBySceneModelId(query);

        assertNotNull(result);
        assertEquals(1, result.getSipRelationVOList().size());
        assertEquals(1, result.getSceneModelDeviceVOList().size());
        assertEquals(1, result.getCusDeviceList().size());
        CusDeviceVO cusDeviceVO = result.getCusDeviceList().get(0);
        assertEquals("设备A", cusDeviceVO.getName());
        assertEquals("SN001", cusDeviceVO.getSerialNumber());
    }

    @Test
    @DisplayName("insertSceneModel - 应新增场景并初始化默认变量设备")
    void testInsertSceneModel_ShouldInitDefaultDevices() {
        SceneModel sceneModel = new SceneModel();
        sceneModel.setSceneModelId(10L);
        SysDept dept = new SysDept();
        dept.setDeptUserId(200L);
        SysUser user = new SysUser();
        user.setUserName("tester");
        user.setDept(dept);
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getLoginUser).thenReturn(loginUser);
        when(sceneModelMapper.insert(sceneModel)).thenReturn(1);
        when(sceneModelDeviceService.save(any(SceneModelDevice.class))).thenReturn(true);

        int result = sceneModelService.insertSceneModel(sceneModel);

        assertEquals(1, result);
        assertEquals(200L, sceneModel.getTenantId());
        assertEquals("tester", sceneModel.getCreateBy());
        assertEquals("tester", sceneModel.getUpdateBy());
        verify(sceneModelDeviceService, times(SceneModelVariableTypeEnum.ADD_LIST.size())).save(any(SceneModelDevice.class));
    }

    @Test
    @DisplayName("deleteSceneModelBySceneModelIds - 应级联删除并清理缓存")
    void testDeleteSceneModelBySceneModelIds_ShouldCascadeDelete() throws SchedulerException {
        SceneModelTag tag = new SceneModelTag();
        tag.setId(55L);
        tag.setSceneModelId(1L);
        when(sceneModelMapper.deleteBatchIds(anyList())).thenReturn(1);
        when(sceneModelTagMapper.selectList(any())).thenReturn(List.of(tag));

        int result = sceneModelService.deleteSceneModelBySceneModelIds(new Long[]{1L});

        assertEquals(1, result);
        verify(sceneModelDeviceService).deleteBySceneModelIds(List.of(1L));
        verify(sceneModelDataMapper).delete(any());
        verify(sceneTagPointsMapper).deleteBySceneModelIds(aryEq(new Long[]{1L}));
        verify(sceneModelTagMapper).delete(any());
        verify(deviceJobService).deleteJobByJobTypeAndDatasourceIds(aryEq(new Long[]{55L}), eq(4));
        verify(redisCache).deleteObject(RedisKeyBuilder.buildSceneModelTagCacheKey(1L));
    }

    @Test
    @DisplayName("restoreSceneModel - 重复场景时应返回失败")
    void testRestoreSceneModel_DuplicateScene_ShouldReturnError() {
        when(sceneModelMapper.selectByModelId(1L)).thenReturn(List.of(new SceneModel(), new SceneModel()));

        AjaxResult result = sceneModelService.restoreSceneModel(1L);

        assertNotEquals(200, result.get(AjaxResult.CODE_TAG));
    }

    @Test
    @DisplayName("restoreSceneModel - 恢复成功时应恢复关联数据和任务")
    void testRestoreSceneModel_Success_ShouldRestoreRelations() {
        SceneModelTag tag = new SceneModelTag();
        when(sceneModelMapper.selectByModelId(1L)).thenReturn(List.of(new SceneModel()));
        when(sceneModelMapper.restoreById(1L)).thenReturn(1);
        when(sceneModelTagService.selectEnableTagBySceneModelId(1L)).thenReturn(List.of(tag));

        AjaxResult result = sceneModelService.restoreSceneModel(1L);

        assertEquals(200, result.get(AjaxResult.CODE_TAG));
        verify(sceneModelDeviceService).restoreBySceneModelId(1L);
        verify(sceneModelDataMapper).restoreBySceneModelId(1L);
        verify(sceneModelTagMapper).restoreBySceneModelId(1L);
        verify(sceneModelTagService).restoreTagJob(List.of(tag));
        verify(sceneTagPointsMapper).restoreBySceneModelId(1L);
    }
}
