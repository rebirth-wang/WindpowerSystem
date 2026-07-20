package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Date;
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
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.extend.core.domin.entity.SysDept;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.OrderControl;
import com.fastbee.iot.mapper.OrderControlMapper;
import com.fastbee.iot.model.vo.OrderControlVO;
import com.fastbee.iot.model.vo.ThingsModelVO;
import com.fastbee.iot.service.impl.OrderControlServiceImpl;
import com.fastbee.system.mapper.SysDeptMapper;

@DisplayName("指令权限控制 Service 单元测试")
class OrderControlServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private OrderControlServiceImpl orderControlService;

    @Mock
    private OrderControlMapper orderControlMapper;
    @Mock
    private IThingsModelService thingsModelService;
    @Mock
    private SysDeptMapper sysDeptMapper;

    private MockedStatic<SecurityUtils> securityUtilsMock;
    private MockedStatic<MessageUtils> messageUtilsMock;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), OrderControl.class);
        ReflectionTestUtils.setField(orderControlService, "baseMapper", orderControlMapper);
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
    @DisplayName("insertOrderControl - 应回填租户和创建信息")
    void testInsertOrderControl_ShouldFillFields() {
        OrderControl orderControl = new OrderControl();
        SysDept dept = new SysDept();
        dept.setDeptUserId(100L);
        SysUser user = new SysUser();
        user.setUserName("tester");
        user.setDept(dept);
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getLoginUser).thenReturn(loginUser);
        when(orderControlMapper.insert(orderControl)).thenReturn(1);

        int result = orderControlService.insertOrderControl(orderControl);

        assertEquals(1, result);
        assertEquals(100L, orderControl.getTenantId());
        assertEquals("tester", orderControl.getCreateBy());
        assertEquals(1, orderControl.getStatus());
        assertNotNull(orderControl.getCreateTime());
    }

    @Test
    @DisplayName("pageOrderControlVO - 应补充模型名和租户名")
    void testPageOrderControlVO_ShouldFillNames() {
        OrderControlVO query = new OrderControlVO();
        query.setPageNum(1);
        query.setPageSize(10);
        OrderControlVO record = new OrderControlVO();
        record.setTenantId(100L);
        record.setSelectOrder("1,2");
        record.setCount(1L);
        record.setStartTime(new Date(System.currentTimeMillis() - 1000));
        record.setEndTime(new Date(System.currentTimeMillis() + 1000));
        Page<OrderControlVO> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(record));
        SysDept dept = new SysDept();
        dept.setDeptUserId(100L);
        dept.setDeptName("tenantA");
        ThingsModelVO model1 = new ThingsModelVO();
        model1.setModelName("开关");
        ThingsModelVO model2 = new ThingsModelVO();
        model2.setModelName("温度");
        when(orderControlMapper.selectOrderControlVoPage(any(Page.class), eq(query))).thenReturn(page);
        when(sysDeptMapper.selectList(any())).thenReturn(List.of(dept));
        when(thingsModelService.selectThingsModelList(any(ThingsModelVO.class))).thenReturn(List.of(model1, model2));

        Page<OrderControlVO> result = orderControlService.pageOrderControlVO(query);

        assertEquals(1, result.getTotal());
        assertEquals("开关,温度", result.getRecords().get(0).getModelNames());
        assertEquals("tenantA", result.getRecords().get(0).getTenantName());
        assertEquals(1, result.getRecords().get(0).getStatus());
    }

    @Test
    @DisplayName("judgeThingsModel - 受限用户有权限时应返回成功")
    void testJudgeThingsModel_WithPermission_ShouldSuccess() {
        OrderControl orderControl = new OrderControl();
        orderControl.setUserId(9L);
        orderControl.setDeviceId(10L);
        orderControl.setSelectOrder("11,12");
        orderControl.setCount(1);
        orderControl.setStartTime(new Date(System.currentTimeMillis() - 1000));
        orderControl.setEndTime(new Date(System.currentTimeMillis() + 1000));
        SysDept dept = new SysDept();
        dept.setDeptUserId(100L);
        SysUser user = new SysUser();
        user.setDeptId(1L);
        user.setUserName("tenantUser");
        user.setDept(dept);
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        loginUser.setDeptId(1L);
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getUserId).thenReturn(9L);
        securityUtilsMock.when(SecurityUtils::getLoginUser).thenReturn(loginUser);
        when(orderControlMapper.selectList(any())).thenReturn(List.of(orderControl));

        AjaxResult result = orderControlService.judgeThingsModel(10L, 11L);

        assertEquals(200, result.get(AjaxResult.CODE_TAG));
    }

    @Test
    @DisplayName("judgeThingsModel - 无权限时应返回失败")
    void testJudgeThingsModel_NoPermission_ShouldError() {
        OrderControl orderControl = new OrderControl();
        orderControl.setUserId(9L);
        orderControl.setDeviceId(10L);
        orderControl.setSelectOrder("12,13");
        orderControl.setCount(1);
        orderControl.setStartTime(new Date(System.currentTimeMillis() - 1000));
        orderControl.setEndTime(new Date(System.currentTimeMillis() + 1000));
        SysDept dept = new SysDept();
        dept.setDeptUserId(100L);
        SysUser user = new SysUser();
        user.setDeptId(1L);
        user.setUserName("tenantUser");
        user.setDept(dept);
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        loginUser.setDeptId(1L);
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        messageUtilsMock = mockStatic(MessageUtils.class);
        securityUtilsMock.when(SecurityUtils::getUserId).thenReturn(9L);
        securityUtilsMock.when(SecurityUtils::getLoginUser).thenReturn(loginUser);
        messageUtilsMock.when(() -> MessageUtils.message(anyString())).thenReturn("no permission");
        when(orderControlMapper.selectList(any())).thenReturn(List.of(orderControl));

        AjaxResult result = orderControlService.judgeThingsModel(10L, 11L);

        assertNotEquals(200, result.get(AjaxResult.CODE_TAG));
    }
}
