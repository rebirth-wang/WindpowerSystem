package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.common.exception.ServiceException;
import com.fastbee.common.extend.core.domin.entity.SysDept;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.ThingsModelTemplate;
import com.fastbee.iot.domain.ThingsModelTemplateTranslate;
import com.fastbee.iot.mapper.ThingsModelTemplateMapper;
import com.fastbee.iot.model.vo.ThingsModelTemplateVO;
import com.fastbee.iot.service.impl.ThingsModelTemplateServiceImpl;
import com.fastbee.system.domain.vo.SysRoleVO;

@DisplayName("通用物模型模板 Service 单元测试")
class ThingsModelTemplateServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private ThingsModelTemplateServiceImpl thingsModelTemplateService;

    @Mock
    private ThingsModelTemplateMapper thingsModelTemplateMapper;
    @Mock
    private IThingsModelTemplateTranslateService translateService;

    private MockedStatic<SecurityUtils> securityUtilsMock;
    private MockedStatic<SysRoleVO> sysRoleVOMock;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ThingsModelTemplate.class);
        ReflectionTestUtils.setField(thingsModelTemplateService, "baseMapper", thingsModelTemplateMapper);
    }

    @AfterEach
    void tearDown() {
        if (securityUtilsMock != null) {
            securityUtilsMock.close();
        }
        if (sysRoleVOMock != null) {
            sysRoleVOMock.close();
        }
    }

    @Test
    @DisplayName("selectThingsModelTemplateByTemplateId - 应委托 Mapper")
    void testSelectThingsModelTemplateByTemplateId_ShouldDelegate() {
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getLanguage).thenReturn("zh_CN");
        ThingsModelTemplate entity = new ThingsModelTemplate();
        entity.setTemplateId(1L);
        when(thingsModelTemplateMapper.selectThingsModelTemplateByTemplateId(any(ThingsModelTemplateVO.class), eq("zh_CN"))).thenReturn(entity);

        ThingsModelTemplate result = thingsModelTemplateService.selectThingsModelTemplateByTemplateId(1L);

        assertSame(entity, result);
    }

    @Test
    @DisplayName("pageThingsModelTemplateVO - 有数据时应设置 owner")
    void testPageThingsModelTemplateVO_ShouldSetOwner() {
        ThingsModelTemplateVO query = new ThingsModelTemplateVO();
        query.setPageNum(1);
        query.setPageSize(10);
        SysDept dept = new SysDept();
        dept.setDeptUserId(100L);
        SysUser user = new SysUser();
        user.setUserId(2L);
        user.setDeptId(1L);
        user.setDept(dept);
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        sysRoleVOMock = mockStatic(SysRoleVO.class);
        securityUtilsMock.when(SecurityUtils::getLoginUser).thenReturn(loginUser);
        securityUtilsMock.when(SecurityUtils::getLanguage).thenReturn("zh_CN");
        sysRoleVOMock.when(() -> SysRoleVO.isAdmin(2L)).thenReturn(false);
        ThingsModelTemplate entity = new ThingsModelTemplate();
        entity.setTemplateId(1L);
        entity.setTenantId(100L);
        Page<ThingsModelTemplate> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(entity));
        when(thingsModelTemplateMapper.selectThingsModelTemplateList(any(Page.class), any(ThingsModelTemplateVO.class))).thenReturn(page);

        Page<ThingsModelTemplateVO> result = thingsModelTemplateService.pageThingsModelTemplateVO(query);

        assertEquals(1, result.getTotal());
        assertTrue(result.getRecords().get(0).getOwner());
    }

    @Test
    @DisplayName("insertThingsModelTemplate - 租户用户时应写入模板和翻译")
    void testInsertThingsModelTemplate_ShouldInsertTemplateAndTranslate() {
        ThingsModelTemplate template = new ThingsModelTemplate();
        template.setTemplateName("温度");
        template.setTemplateName_en_US("Temperature");
        SysDept dept = new SysDept();
        dept.setDeptUserId(100L);
        dept.setDeptName("tenant");
        SysUser user = new SysUser();
        user.setUserId(2L);
        user.setUserName("tester");
        user.setDeptId(1L);
        user.setDept(dept);
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        sysRoleVOMock = mockStatic(SysRoleVO.class);
        securityUtilsMock.when(SecurityUtils::getLoginUser).thenReturn(loginUser);
        sysRoleVOMock.when(() -> SysRoleVO.isAdmin(2L)).thenReturn(false);
        when(thingsModelTemplateMapper.insert(template)).thenAnswer(invocation -> {
            template.setTemplateId(8L);
            return 1;
        });
        when(translateService.insertWithCache(any(ThingsModelTemplateTranslate.class))).thenReturn(true);

        int result = thingsModelTemplateService.insertThingsModelTemplate(template);

        assertEquals(1, result);
        assertEquals(0, template.getIsSys());
        assertEquals(100L, template.getTenantId());
        assertEquals("tenant", template.getTenantName());
        verify(translateService).insertWithCache(any(ThingsModelTemplateTranslate.class));
    }

    @Test
    @DisplayName("insertThingsModelTemplate - 重复地址异常时应转为业务异常")
    void testInsertThingsModelTemplate_DuplicateAddress_ShouldThrowServiceException() {
        ThingsModelTemplate template = new ThingsModelTemplate();
        SysUser user = new SysUser();
        user.setUserId(1L);
        user.setUserName("admin");
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        sysRoleVOMock = mockStatic(SysRoleVO.class);
        securityUtilsMock.when(SecurityUtils::getLoginUser).thenReturn(loginUser);
        sysRoleVOMock.when(() -> SysRoleVO.isAdmin(1L)).thenReturn(true);
        when(thingsModelTemplateMapper.insert(template)).thenThrow(new RuntimeException("iot_things_modes_slaveId_reg"));

        assertThrows(ServiceException.class, () -> thingsModelTemplateService.insertThingsModelTemplate(template));
    }
}
