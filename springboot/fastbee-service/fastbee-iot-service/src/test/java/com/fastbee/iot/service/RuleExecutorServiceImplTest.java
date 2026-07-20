package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
import com.fastbee.common.extend.core.domin.entity.SysDept;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.service.impl.RuleExecutorServiceImpl;
import com.fastbee.rule.domain.RuleExecutor;
import com.fastbee.rule.domain.vo.RuleExecutorVO;
import com.fastbee.rule.mapper.RuleExecutorMapper;

@DisplayName("规则执行器 Service 单元测试")
class RuleExecutorServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private RuleExecutorServiceImpl ruleExecutorService;

    @Mock
    private RuleExecutorMapper ruleExecutorMapper;

    private MockedStatic<SecurityUtils> securityUtilsMock;
    private MockedStatic<MessageUtils> messageUtilsMock;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), RuleExecutor.class);
        ReflectionTestUtils.setField(ruleExecutorService, "baseMapper", ruleExecutorMapper);
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
    @DisplayName("pageRuleExecutorVO - 应返回转换后的分页")
    void testPageRuleExecutorVO_ShouldReturnPage() {
        RuleExecutor query = new RuleExecutor();
        query.setPageNum(1);
        query.setPageSize(10);
        RuleExecutor entity = new RuleExecutor();
        entity.setId(1L);
        Page<RuleExecutor> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(entity));
        when(ruleExecutorMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<RuleExecutorVO> result = ruleExecutorService.pageRuleExecutorVO(query);

        assertEquals(1, result.getTotal());
        assertEquals(1L, result.getRecords().get(0).getId());
    }

    @Test
    @DisplayName("insertWithCache - 非租户用户时应抛异常")
    void testInsertWithCache_NoDept_ShouldThrowException() {
        RuleExecutor entity = new RuleExecutor();
        SysUser user = new SysUser();
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        messageUtilsMock = mockStatic(MessageUtils.class);
        securityUtilsMock.when(SecurityUtils::getLoginUser).thenReturn(loginUser);
        messageUtilsMock.when(() -> MessageUtils.message(anyString())).thenReturn("only tenant");

        assertThrows(ServiceException.class, () -> ruleExecutorService.insertWithCache(entity));
    }

    @Test
    @DisplayName("updateWithCache - 租户用户时应填充更新信息")
    void testUpdateWithCache_WithDept_ShouldFillUpdateFields() {
        RuleExecutor entity = new RuleExecutor();
        entity.setId(1L);
        SysUser user = new SysUser();
        user.setUserName("tester");
        user.setDeptId(1L);
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getLoginUser).thenReturn(loginUser);
        when(ruleExecutorMapper.updateById(entity)).thenReturn(1);

        Boolean result = ruleExecutorService.updateWithCache(entity);

        assertTrue(result);
        assertEquals("tester", entity.getUpdateBy());
        assertNotNull(entity.getUpdateTime());
    }

    @Test
    @DisplayName("insertWithCache - 租户用户时应填充创建信息")
    void testInsertWithCache_WithDept_ShouldFillFields() {
        RuleExecutor entity = new RuleExecutor();
        SysDept dept = new SysDept();
        dept.setDeptUserId(100L);
        SysUser user = new SysUser();
        user.setUserName("tester");
        user.setDeptId(1L);
        user.setDept(dept);
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getLoginUser).thenReturn(loginUser);
        when(ruleExecutorMapper.insert(entity)).thenReturn(1);

        Boolean result = ruleExecutorService.insertWithCache(entity);

        assertTrue(result);
        assertEquals(100L, entity.getTenantId());
        assertEquals("tester", entity.getCreateBy());
        assertNotNull(entity.getCreateTime());
    }
}
