package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.yomahub.liteflow.core.FlowExecutor;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.service.impl.RuleElServiceImpl;
import com.fastbee.rule.domain.RuleEl;
import com.fastbee.rule.mapper.RuleElMapper;

@DisplayName("规则EL Service 单元测试")
class RuleElServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    @Spy
    private RuleElServiceImpl ruleElService;

    @Mock
    private RuleElMapper ruleElMapper;
    @Mock
    private ISceneService sceneService;
    @Mock
    private FlowExecutor flowExecutor;
    @Mock
    private IRuleLogService ruleLogService;
    @Mock
    private IScriptService scriptService;
    @Mock
    private IDeviceService deviceService;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), RuleEl.class);
        ReflectionTestUtils.setField(ruleElService, "baseMapper", ruleElMapper);
    }

    @Test
    @DisplayName("selectRuleElById - 应返回实体")
    void testSelectRuleElById_ShouldReturnEntity() {
        RuleEl entity = new RuleEl();
        entity.setId(1L);
        when(ruleElMapper.selectById(1L)).thenReturn(entity);

        RuleEl result = ruleElService.selectRuleElById(1L);

        assertSame(entity, result);
    }

    @Test
    @DisplayName("exec - 有 elId 时应委托给 execByElid")
    void testExec_WithElId_ShouldDelegate() {
        RuleEl ruleEl = new RuleEl();
        ruleEl.setElId("EL-1");
        doReturn(true).when(ruleElService).execByElid("EL-1");

        Boolean result = ruleElService.exec(ruleEl);

        assertTrue(result);
        verify(ruleElService).execByElid("EL-1");
    }

    @Test
    @DisplayName("execByid - 查询不到规则时应返回 false")
    void testExecByid_NotFound_ShouldReturnFalse() {
        doReturn(null).when(ruleElService).getById(1L);
        Boolean result = ruleElService.execByid(1L);
        assertFalse(result);
    }

    @Test
    @DisplayName("deleteWithCacheByIds - 有场景时应级联删除场景")
    void testDeleteWithCacheByIds_WithScene_ShouldDeleteScene() {
        RuleEl entity = new RuleEl();
        entity.setId(1L);
        entity.setSceneId(9L);
        doReturn(entity).when(ruleElService).getById(1L);
        when(ruleElMapper.deleteByIds(any())).thenReturn(1);

        Boolean result = ruleElService.deleteWithCacheByIds(new Long[]{1L}, true);

        assertTrue(result);
        verify(sceneService).deleteSceneBySceneId(9L);
    }
}
