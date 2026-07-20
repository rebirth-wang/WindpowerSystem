package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.cache.IDeviceCache;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.Product;
import com.fastbee.iot.domain.Script;
import com.fastbee.iot.mapper.ScriptMapper;
import com.fastbee.iot.model.DeviceMetaData;
import com.fastbee.iot.model.ScriptCondition;
import com.fastbee.iot.model.vo.ScriptVO;
import com.fastbee.iot.service.impl.ScriptServiceImpl;
import com.fastbee.mqttclient.PubMqttClient;
import com.fastbee.rule.context.MsgContext;
import com.fastbee.rule.core.FlowLogExecutor;
import com.fastbee.system.service.ISysConfigService;

@DisplayName("规则脚本 Service 单元测试")
class ScriptServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    @Spy
    private ScriptServiceImpl scriptService;

    @Mock
    private ScriptMapper ruleScriptMapper;
    @Mock
    private FlowLogExecutor flowLogExecutor;
    @Mock
    private PubMqttClient mqttClient;
    @Mock
    private IDeviceCache deviceCache;
    @Mock
    private ISysConfigService sysConfigService;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), Script.class);
        ReflectionTestUtils.setField(scriptService, "baseMapper", ruleScriptMapper);
    }

    @Test
    @DisplayName("selectRuleScriptById - 应委托 Mapper")
    void testSelectRuleScriptById_ShouldDelegate() {
        Script query = new Script();
        ScriptVO scriptVO = new ScriptVO();
        scriptVO.setScriptId("S1");
        when(ruleScriptMapper.selectRuleScriptById(query)).thenReturn(scriptVO);

        ScriptVO result = scriptService.selectRuleScriptById(query);

        assertSame(scriptVO, result);
    }

    @Test
    @DisplayName("selectRuleScriptList - 应返回分页结果")
    void testSelectRuleScriptList_ShouldReturnPage() {
        Script query = new Script();
        query.setPageNum(1);
        query.setPageSize(10);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ScriptVO> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>();
        page.setTotal(1);
        when(ruleScriptMapper.selectRuleScriptList(any(com.baomidou.mybatisplus.extension.plugins.pagination.Page.class), eq(query))).thenReturn(page);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<ScriptVO> result = scriptService.selectRuleScriptList(query);

        assertEquals(1, result.getTotal());
    }

    @Test
    @DisplayName("validateScript - 包含危险关键字时应返回失败")
    void testValidateScript_DangerousKeyword_ShouldReturnFail() {
        Script script = new Script();
        script.setScriptData("for(int i=0;i<1;i++){}");

        com.fastbee.common.core.domain.AjaxResult result = scriptService.validateScript(script);

        assertEquals(Boolean.FALSE, result.get(com.fastbee.common.core.domain.AjaxResult.DATA_TAG));
    }

    @Test
    @DisplayName("insertRuleScriptList - insertBatch 成功时应返回 1")
    void testInsertRuleScriptList_ShouldReturnOne() {
        when(ruleScriptMapper.insertBatch(any())).thenReturn(true);

        int result = scriptService.insertRuleScriptList(List.of(new Script()));

        assertEquals(1, result);
    }

    @Test
    @DisplayName("processRuleScript - 设备元数据不存在时应返回空上下文")
    void testProcessRuleScript_NoDeviceMeta_ShouldReturnEmptyContext() {
        when(deviceCache.getDeviceMetaDataCache("SN001")).thenReturn(null);

        MsgContext result = scriptService.processRuleScript("SN001", 1, "/topic", "payload");

        assertNotNull(result);
    }

    @Test
    @DisplayName("processRuleScript - 设备元数据存在时应委托 execRuleScript")
    void testProcessRuleScript_WithDeviceMeta_ShouldDelegate() {
        DeviceMetaData metaData = new DeviceMetaData();
        Device device = new Device();
        device.setProductId(10L);
        Product product = new Product();
        product.setProtocolCode("mqtt");
        metaData.setDevice(device);
        metaData.setProduct(product);
        MsgContext context = new MsgContext();
        when(deviceCache.getDeviceMetaDataCache("SN001")).thenReturn(metaData);
        doReturn(context).when(scriptService).execRuleScript(any(ScriptCondition.class), any(MsgContext.class));

        MsgContext result = scriptService.processRuleScript("SN001", 1, "/topic", "payload");

        assertSame(context, result);
        verify(scriptService).execRuleScript(any(ScriptCondition.class), any(MsgContext.class));
    }
}
