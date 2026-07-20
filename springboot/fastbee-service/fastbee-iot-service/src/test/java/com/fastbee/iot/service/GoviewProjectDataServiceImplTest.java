package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
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
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.GoviewProjectData;
import com.fastbee.iot.mapper.GoviewProjectDataMapper;
import com.fastbee.iot.service.impl.GoviewProjectDataServiceImpl;

@DisplayName("Goview 项目数据 Service 单元测试")
class GoviewProjectDataServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private GoviewProjectDataServiceImpl goviewProjectDataService;

    @Mock
    private GoviewProjectDataMapper goviewProjectDataMapper;

    private MockedStatic<SecurityUtils> securityUtilsMock;
    private MockedStatic<MessageUtils> messageUtilsMock;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), GoviewProjectData.class);
        ReflectionTestUtils.setField(goviewProjectDataService, "baseMapper", goviewProjectDataMapper);
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
    @DisplayName("insertGoviewProjectData - 应设置创建人和时间")
    void testInsertGoviewProjectData_ShouldSetFields() {
        GoviewProjectData data = new GoviewProjectData();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getUserId).thenReturn(100L);
        when(goviewProjectDataMapper.insert(data)).thenReturn(1);

        int result = goviewProjectDataService.insertGoviewProjectData(data);

        assertEquals(1, result);
        assertNotNull(data.getId());
        assertEquals("100", data.getCreateBy());
        assertNotNull(data.getCreateTime());
        assertNotNull(data.getUpdateTime());
    }

    @Test
    @DisplayName("insertOrUpdateGoviewProjectData - 不存在时应走新增")
    void testInsertOrUpdateGoviewProjectData_InsertPath_ShouldInsert() {
        GoviewProjectData data = new GoviewProjectData();
        data.setProjectId("p1");
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getUserId).thenReturn(200L);
        when(goviewProjectDataMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(goviewProjectDataMapper.insert(data)).thenReturn(1);

        int result = goviewProjectDataService.insertOrUpdateGoviewProjectData(data);

        assertEquals(1, result);
        verify(goviewProjectDataMapper).insert(data);
    }

    @Test
    @DisplayName("insertOrUpdateGoviewProjectData - 已存在时应走更新")
    void testInsertOrUpdateGoviewProjectData_UpdatePath_ShouldUpdate() {
        GoviewProjectData data = new GoviewProjectData();
        data.setProjectId("p1");
        GoviewProjectData existing = new GoviewProjectData();
        existing.setId("existing-id");
        when(goviewProjectDataMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existing);
        when(goviewProjectDataMapper.updateById(data)).thenReturn(1);

        int result = goviewProjectDataService.insertOrUpdateGoviewProjectData(data);

        assertEquals(1, result);
        assertEquals("existing-id", data.getId());
        assertNotNull(data.getUpdateTime());
    }

    @Test
    @DisplayName("executeSql - 非 select 语句时应返回失败")
    void testExecuteSql_InvalidSql_ShouldReturnError() {
        messageUtilsMock = mockStatic(MessageUtils.class);
        messageUtilsMock.when(() -> MessageUtils.message(anyString())).thenReturn("only select");

        AjaxResult result = goviewProjectDataService.executeSql("delete from test");

        assertNotEquals(200, result.get(AjaxResult.CODE_TAG));
        verify(goviewProjectDataMapper, never()).executeSql(anyString());
    }

    @Test
    @DisplayName("executeSql - select 语句时应返回成功")
    void testExecuteSql_SelectSql_ShouldReturnSuccess() {
        List<LinkedHashMap> mockResult = Arrays.asList(new LinkedHashMap<>());
        when(goviewProjectDataMapper.executeSql("select * from test")).thenReturn(mockResult);

        AjaxResult result = goviewProjectDataService.executeSql("select * from test");

        assertEquals(200, result.get(AjaxResult.CODE_TAG));
        assertEquals(mockResult, result.get(AjaxResult.DATA_TAG));
    }
}
