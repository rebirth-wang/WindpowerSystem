package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.SceneModelDevice;
import com.fastbee.iot.mapper.DeviceMapper;
import com.fastbee.iot.mapper.SceneModelDataMapper;
import com.fastbee.iot.mapper.SceneModelDeviceMapper;
import com.fastbee.iot.mapper.ThingsModelMapper;
import com.fastbee.iot.model.vo.SceneModelDeviceVO;
import com.fastbee.iot.service.impl.SceneModelDeviceServiceImpl;

@DisplayName("场景模型设备 Service 单元测试")
class SceneModelDeviceServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private SceneModelDeviceServiceImpl sceneModelDeviceService;

    @Mock
    private SceneModelDeviceMapper sceneModelDeviceMapper;
    @Mock
    private SceneModelDataMapper sceneModelDataMapper;
    @Mock
    private DeviceMapper deviceMapper;
    @Mock
    private ThingsModelMapper thingsModelMapper;
    @Mock
    private IDeviceJobService jobService;

    private MockedStatic<SecurityUtils> securityUtilsMock;
    private MockedStatic<MessageUtils> messageUtilsMock;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), SceneModelDevice.class);
        ReflectionTestUtils.setField(sceneModelDeviceService, "baseMapper", sceneModelDeviceMapper);
        SecurityUtilsTestHelper.prepareSecurityUtils();
    }

    @AfterEach
    void tearDown() {
        if (securityUtilsMock != null) {
            securityUtilsMock.close();
        }
        if (messageUtilsMock != null) {
            messageUtilsMock.close();
        }
    }

    @Test
    @DisplayName("pageSceneModelDeviceVO - 无数据时应返回空分页")
    void testPageSceneModelDeviceVO_NoData_ShouldReturnEmptyPage() {
        SceneModelDevice query = new SceneModelDevice();
        query.setPageNum(1);
        query.setPageSize(10);
        Page<SceneModelDevice> page = new Page<>();
        page.setTotal(0);
        when(sceneModelDeviceMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<SceneModelDeviceVO> result = sceneModelDeviceService.pageSceneModelDeviceVO(query);

        assertEquals(0, result.getTotal());
        assertTrue(result.getRecords().isEmpty());
    }

    @Test
    @DisplayName("insertSceneModelDevice - 已绑定同设备时应抛异常")
    void testInsertSceneModelDevice_DuplicateDevice_ShouldThrowException() {
        SceneModelDevice entity = new SceneModelDevice();
        entity.setCusDeviceId(11L);
        entity.setSceneModelId(22L);
        SysUser user = new SysUser();
        user.setUserName("tester");
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getLoginUser).thenReturn(loginUser);
        messageUtilsMock = mockStatic(MessageUtils.class);
        messageUtilsMock.when(() -> MessageUtils.message(anyString())).thenReturn("duplicate");
        when(sceneModelDeviceMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(new SceneModelDevice()));

        assertThrows(ServiceException.class, () -> sceneModelDeviceService.insertSceneModelDevice(entity));
    }

    @Test
    @DisplayName("deleteSceneModelDeviceByIds - 被场景变量引用时应抛异常")
    void testDeleteSceneModelDeviceByIds_Quoted_ShouldThrowException() {
        messageUtilsMock = mockStatic(MessageUtils.class);
        messageUtilsMock.when(() -> MessageUtils.message(anyString())).thenReturn("quoted");
        when(sceneModelDeviceMapper.checkContainAliasFormule(1L)).thenReturn(1);

        assertThrows(ServiceException.class, () -> sceneModelDeviceService.deleteSceneModelDeviceByIds(new Long[]{1L}));
    }

    @Test
    @DisplayName("selectOneBySceneModelIdAndVariableType - 应返回单条数据")
    void testSelectOneBySceneModelIdAndVariableType_ShouldReturnEntity() {
        SceneModelDevice expected = new SceneModelDevice();
        when(sceneModelDeviceMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(expected);

        SceneModelDevice result = sceneModelDeviceService.selectOneBySceneModelIdAndVariableType(1L, 2);

        assertSame(expected, result);
    }
}
