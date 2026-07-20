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
import com.fastbee.iot.service.impl.RuleScriptNodeServiceImpl;
import com.fastbee.rule.domain.RuleScriptNode;
import com.fastbee.rule.domain.vo.RuleScriptNodeVO;
import com.fastbee.rule.mapper.RuleScriptNodeMapper;

@DisplayName("规则脚本节点 Service 单元测试")
class RuleScriptNodeServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private RuleScriptNodeServiceImpl ruleScriptNodeService;

    @Mock
    private RuleScriptNodeMapper ruleScriptNodeMapper;

    private MockedStatic<SecurityUtils> securityUtilsMock;
    private MockedStatic<MessageUtils> messageUtilsMock;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), RuleScriptNode.class);
        ReflectionTestUtils.setField(ruleScriptNodeService, "baseMapper", ruleScriptNodeMapper);
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
    @DisplayName("pageRuleScriptNodeVO - 应返回转换后的分页")
    void testPageRuleScriptNodeVO_ShouldReturnPage() {
        RuleScriptNode query = new RuleScriptNode();
        query.setPageNum(1);
        query.setPageSize(10);
        RuleScriptNode entity = new RuleScriptNode();
        entity.setId(1L);
        Page<RuleScriptNode> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(entity));
        when(ruleScriptNodeMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<RuleScriptNodeVO> result = ruleScriptNodeService.pageRuleScriptNodeVO(query);

        assertEquals(1, result.getTotal());
        assertEquals(1L, result.getRecords().get(0).getId());
    }

    @Test
    @DisplayName("insertWithCache - 非租户用户时应抛异常")
    void testInsertWithCache_NoDept_ShouldThrowException() {
        RuleScriptNode entity = new RuleScriptNode();
        SysUser user = new SysUser();
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        messageUtilsMock = mockStatic(MessageUtils.class);
        securityUtilsMock.when(SecurityUtils::getLoginUser).thenReturn(loginUser);
        messageUtilsMock.when(() -> MessageUtils.message(anyString())).thenReturn("only tenant");

        assertThrows(ServiceException.class, () -> ruleScriptNodeService.insertWithCache(entity));
    }

    @Test
    @DisplayName("insertWithCache - 租户用户时应填充创建信息")
    void testInsertWithCache_WithDept_ShouldFillFields() {
        RuleScriptNode entity = new RuleScriptNode();
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
        when(ruleScriptNodeMapper.insert(entity)).thenReturn(1);

        Boolean result = ruleScriptNodeService.insertWithCache(entity);

        assertTrue(result);
        assertEquals(100L, entity.getTenantId());
        assertEquals("tester", entity.getCreateBy());
        assertNotNull(entity.getCreateTime());
    }

    @Test
    @DisplayName("updateWithCache - 租户用户时应填充更新信息")
    void testUpdateWithCache_WithDept_ShouldFillUpdateFields() {
        RuleScriptNode entity = new RuleScriptNode();
        entity.setId(1L);
        SysUser user = new SysUser();
        user.setUserName("tester");
        user.setDeptId(1L);
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getLoginUser).thenReturn(loginUser);
        when(ruleScriptNodeMapper.updateById(entity)).thenReturn(1);

        Boolean result = ruleScriptNodeService.updateWithCache(entity);

        assertTrue(result);
        assertEquals("tester", entity.getUpdateBy());
        assertNotNull(entity.getUpdateTime());
    }
}
