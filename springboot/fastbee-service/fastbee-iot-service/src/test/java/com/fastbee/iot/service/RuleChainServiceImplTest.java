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
import com.fastbee.common.extend.core.domin.entity.SysDept;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.service.impl.RuleChainServiceImpl;
import com.fastbee.rule.domain.RuleChain;
import com.fastbee.rule.domain.vo.RuleChainVO;
import com.fastbee.rule.mapper.RuleChainMapper;

@DisplayName("规则链 Service 单元测试")
class RuleChainServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private RuleChainServiceImpl ruleChainService;

    @Mock
    private RuleChainMapper ruleChainMapper;

    private MockedStatic<SecurityUtils> securityUtilsMock;
    private MockedStatic<MessageUtils> messageUtilsMock;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), RuleChain.class);
        ReflectionTestUtils.setField(ruleChainService, "baseMapper", ruleChainMapper);
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
    @DisplayName("pageRuleChainVO - 应返回转换后的分页")
    void testPageRuleChainVO_ShouldReturnPage() {
        RuleChain query = new RuleChain();
        query.setPageNum(1);
        query.setPageSize(10);
        RuleChain entity = new RuleChain();
        entity.setId(1L);
        Page<RuleChain> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(entity));
        when(ruleChainMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<RuleChainVO> result = ruleChainService.pageRuleChainVO(query);

        assertEquals(1, result.getTotal());
        assertEquals(1L, result.getRecords().get(0).getId());
    }

    @Test
    @DisplayName("insertWithCache - 非租户用户时应抛异常")
    void testInsertWithCache_NoDept_ShouldThrowException() {
        RuleChain entity = new RuleChain();
        SysUser user = new SysUser();
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        messageUtilsMock = mockStatic(MessageUtils.class);
        securityUtilsMock.when(SecurityUtils::getLoginUser).thenReturn(loginUser);
        messageUtilsMock.when(() -> MessageUtils.message(anyString())).thenReturn("only tenant");

        assertThrows(ServiceException.class, () -> ruleChainService.insertWithCache(entity));
    }

    @Test
    @DisplayName("insertWithCache - 租户用户时应设置租户和创建信息")
    void testInsertWithCache_WithDept_ShouldFillFields() {
        RuleChain entity = new RuleChain();
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
        when(ruleChainMapper.insert(entity)).thenReturn(1);

        Boolean result = ruleChainService.insertWithCache(entity);

        assertTrue(result);
        assertEquals(100L, entity.getTenantId());
        assertEquals("tester", entity.getCreateBy());
        assertNotNull(entity.getCreateTime());
    }

    @Test
    @DisplayName("updateWithCache - 租户用户时应设置更新信息")
    void testUpdateWithCache_WithDept_ShouldFillUpdateFields() {
        RuleChain entity = new RuleChain();
        entity.setId(1L);
        SysUser user = new SysUser();
        user.setUserName("tester");
        user.setDeptId(1L);
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getLoginUser).thenReturn(loginUser);
        when(ruleChainMapper.updateById(entity)).thenReturn(1);

        Boolean result = ruleChainService.updateWithCache(entity);

        assertTrue(result);
        assertEquals("tester", entity.getUpdateBy());
        assertNotNull(entity.getUpdateTime());
    }
}
