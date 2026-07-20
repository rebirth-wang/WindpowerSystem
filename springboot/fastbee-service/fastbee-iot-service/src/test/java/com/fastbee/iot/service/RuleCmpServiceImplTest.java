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
import com.fastbee.iot.service.impl.RuleCmpServiceImpl;
import com.fastbee.rule.domain.RuleCmp;
import com.fastbee.rule.domain.vo.RuleCmpVO;
import com.fastbee.rule.mapper.RuleCmpMapper;

@DisplayName("规则组件 Service 单元测试")
class RuleCmpServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private RuleCmpServiceImpl ruleCmpService;

    @Mock
    private RuleCmpMapper ruleCmpMapper;

    private MockedStatic<SecurityUtils> securityUtilsMock;
    private MockedStatic<MessageUtils> messageUtilsMock;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), RuleCmp.class);
        ReflectionTestUtils.setField(ruleCmpService, "baseMapper", ruleCmpMapper);
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
    @DisplayName("pageRuleCmpVO - 应返回转换后的分页")
    void testPageRuleCmpVO_ShouldReturnPage() {
        RuleCmp query = new RuleCmp();
        query.setPageNum(1);
        query.setPageSize(10);
        RuleCmp entity = new RuleCmp();
        entity.setId(1L);
        Page<RuleCmp> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(entity));
        when(ruleCmpMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<RuleCmpVO> result = ruleCmpService.pageRuleCmpVO(query);

        assertEquals(1, result.getTotal());
        assertEquals(1L, result.getRecords().get(0).getId());
    }

    @Test
    @DisplayName("insertWithCache - 非租户用户时应抛异常")
    void testInsertWithCache_NoDept_ShouldThrowException() {
        RuleCmp entity = new RuleCmp();
        SysUser user = new SysUser();
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        messageUtilsMock = mockStatic(MessageUtils.class);
        securityUtilsMock.when(SecurityUtils::getLoginUser).thenReturn(loginUser);
        messageUtilsMock.when(() -> MessageUtils.message(anyString())).thenReturn("only tenant");

        assertThrows(ServiceException.class, () -> ruleCmpService.insertWithCache(entity));
    }

    @Test
    @DisplayName("insertWithCache - 租户用户时应填充创建信息")
    void testInsertWithCache_WithDept_ShouldFillFields() {
        RuleCmp entity = new RuleCmp();
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
        when(ruleCmpMapper.insert(entity)).thenReturn(1);

        Boolean result = ruleCmpService.insertWithCache(entity);

        assertTrue(result);
        assertEquals(100L, entity.getTenantId());
        assertEquals("tester", entity.getCreateBy());
        assertNotNull(entity.getCreateTime());
    }

    @Test
    @DisplayName("updateWithCache - 租户用户时应填充更新信息")
    void testUpdateWithCache_WithDept_ShouldFillUpdateFields() {
        RuleCmp entity = new RuleCmp();
        entity.setId(1L);
        SysUser user = new SysUser();
        user.setUserName("tester");
        user.setDeptId(1L);
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getLoginUser).thenReturn(loginUser);
        when(ruleCmpMapper.updateById(entity)).thenReturn(1);

        Boolean result = ruleCmpService.updateWithCache(entity);

        assertTrue(result);
        assertEquals("tester", entity.getUpdateBy());
        assertNotNull(entity.getUpdateTime());
    }

    @Test
    @DisplayName("deleteWithCacheByIds - 应批量删除")
    void testDeleteWithCacheByIds_ShouldRemoveBatch() {
        when(ruleCmpMapper.deleteByIds(any())).thenReturn(2);

        Boolean result = ruleCmpService.deleteWithCacheByIds(new Long[]{1L, 2L}, true);

        assertTrue(result);
    }
}
