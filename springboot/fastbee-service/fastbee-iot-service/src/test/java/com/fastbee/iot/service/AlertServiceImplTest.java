package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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

import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.Alert;
import com.fastbee.iot.domain.AlertNotifyTemplate;
import com.fastbee.iot.domain.AlertScene;
import com.fastbee.iot.domain.Scene;
import com.fastbee.iot.mapper.AlertMapper;
import com.fastbee.iot.model.vo.AlertVO;
import com.fastbee.iot.model.vo.SceneVO;
import com.fastbee.iot.service.impl.AlertServiceImpl;
import com.fastbee.notify.domain.NotifyTemplate;

@DisplayName("告警配置 Service 单元测试")
class AlertServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private AlertServiceImpl alertService;

    @Mock
    private AlertMapper alertMapper;

    private MockedStatic<SecurityUtils> securityUtilsMock;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), Alert.class);
        ReflectionTestUtils.setField(alertService, "baseMapper", alertMapper);
    }

    @AfterEach
    void tearDown() {
        if (securityUtilsMock != null) {
            securityUtilsMock.close();
        }
    }

    @Test
    @DisplayName("pageAlertVO - 非机构用户时应按租户查询并返回分页")
    void testPageAlertVO_NoDept_ShouldReturnConvertedPage() {
        Alert query = new Alert();
        query.setPageNum(1);
        query.setPageSize(10);

        SysUser user = new SysUser();
        user.setUserId(99L);
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);

        Alert entity = new Alert();
        entity.setAlertId(1L);
        Page<Alert> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(entity));

        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getLoginUser).thenReturn(loginUser);
        when(alertMapper.selectPage(any(Page.class), any())).thenReturn(page);

        Page<AlertVO> result = alertService.pageAlertVO(query);

        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
    }

    @Test
    @DisplayName("insertAlert - 有场景和通知模板时应插入关联数据")
    void testInsertAlert_WithRelations_ShouldInsertRelations() {
        AlertVO vo = new AlertVO();
        Scene scene = new Scene();
        scene.setSceneId(11L);
        NotifyTemplate template = new NotifyTemplate();
        template.setId(22L);
        vo.setScenes(List.of(scene));
        vo.setNotifyTemplateList(List.of(template));

        doAnswer(invocation -> {
            Alert alert = invocation.getArgument(0);
            alert.setAlertId(100L);
            return 1;
        }).when(alertMapper).insert(any(Alert.class));

        int result = alertService.insertAlert(vo);

        assertEquals(1, result);
        assertNotNull(vo.getCreateTime());
        verify(alertMapper).insertAlertSceneList(argThat(list ->
                list.size() == 1 && ((AlertScene) list.get(0)).getAlertId().equals(100L)
        ));
        verify(alertMapper).insertAlertNotifyTemplateList(argThat(list ->
                list.size() == 1 && ((AlertNotifyTemplate) list.get(0)).getNotifyTemplateId().equals(22L)
        ));
    }

    @Test
    @DisplayName("updateAlert - 应先删除旧关联再更新并重建")
    void testUpdateAlert_ShouldReplaceRelations() {
        AlertVO vo = new AlertVO();
        vo.setAlertId(1L);
        Scene scene = new Scene();
        scene.setSceneId(11L);
        vo.setScenes(List.of(scene));
        when(alertMapper.updateById(any(Alert.class))).thenReturn(1);

        int result = alertService.updateAlert(vo);

        assertEquals(1, result);
        verify(alertMapper).deleteAlertSceneByAlertIds(new Long[]{1L});
        verify(alertMapper).deleteAlertNotifyTemplateByAlertIds(new Long[]{1L});
        verify(alertMapper).insertAlertSceneList(anyList());
    }

    @Test
    @DisplayName("editStatus - update 成功时应返回 1")
    void testEditStatus_Success_ShouldReturnOne() {
        when(alertMapper.update(any(), any(LambdaUpdateWrapper.class))).thenReturn(1);

        int result = alertService.editStatus(1L, 2);

        assertEquals(1, result);
    }

    @Test
    @DisplayName("listAlertByScene - sceneId 为空时应返回空分页")
    void testListAlertByScene_NullSceneId_ShouldReturnEmptyPage() {
        SceneVO sceneVO = new SceneVO();
        sceneVO.setPageNum(1);
        sceneVO.setPageSize(10);

        Page<Alert> result = alertService.listAlertByScene(sceneVO);

        assertEquals(0, result.getTotal());
        assertTrue(result.getRecords().isEmpty());
    }
}
