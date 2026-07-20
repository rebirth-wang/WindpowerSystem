package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.CommandPreferences;
import com.fastbee.iot.mapper.CommandPreferencesMapper;
import com.fastbee.iot.model.vo.CommandPreferencesVO;
import com.fastbee.iot.service.impl.CommandPreferencesServiceImpl;

@DisplayName("指令偏好 Service 单元测试")
class CommandPreferencesServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private CommandPreferencesServiceImpl commandPreferencesService;

    @Mock
    private CommandPreferencesMapper commandPreferencesMapper;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), CommandPreferences.class);
        ReflectionTestUtils.setField(commandPreferencesService, "baseMapper", commandPreferencesMapper);
    }

    @Test
    @DisplayName("queryByIdWithCache - 应提取 command 字段")
    void testQueryByIdWithCache_ShouldExtractCommand() {
        CommandPreferences entity = new CommandPreferences();
        entity.setCommand("{\"command\":\"reboot\"}");
        when(commandPreferencesMapper.selectById(1L)).thenReturn(entity);

        CommandPreferences result = commandPreferencesService.queryByIdWithCache(1L);

        assertEquals("reboot", result.getCommand());
    }

    @Test
    @DisplayName("pageCommandPreferencesVO - 应分页并提取 command")
    void testPageCommandPreferencesVO_ShouldExtractCommands() {
        CommandPreferences query = new CommandPreferences();
        query.setPageNum(1);
        query.setPageSize(10);
        CommandPreferences entity = new CommandPreferences();
        entity.setCommand("{\"command\":\"status\"}");
        Page<CommandPreferences> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(entity));
        when(commandPreferencesMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<CommandPreferencesVO> result = commandPreferencesService.pageCommandPreferencesVO(query);

        assertEquals(1, result.getTotal());
        assertEquals("status", result.getRecords().get(0).getCommand());
    }

    @Test
    @DisplayName("insertWithCache - 应将指令包装为 JSON 后保存")
    void testInsertWithCache_ShouldWrapCommandJson() {
        CommandPreferences entity = new CommandPreferences();
        entity.setCommand("reboot");
        when(commandPreferencesMapper.insert(entity)).thenReturn(1);

        Boolean result = commandPreferencesService.insertWithCache(entity);

        assertTrue(result);
        assertTrue(entity.getCommand().contains("\"command\":\"reboot\""));
    }

    @Test
    @DisplayName("updateWithCache - 应将指令包装为 JSON 后更新")
    void testUpdateWithCache_ShouldWrapCommandJson() {
        CommandPreferences entity = new CommandPreferences();
        entity.setId(1L);
        entity.setCommand("status");
        when(commandPreferencesMapper.updateById(entity)).thenReturn(1);

        Boolean result = commandPreferencesService.updateWithCache(entity);

        assertTrue(result);
        assertTrue(entity.getCommand().contains("\"command\":\"status\""));
    }

    @Test
    @DisplayName("deleteWithCacheByIds - 应批量删除")
    void testDeleteWithCacheByIds_ShouldRemoveByIds() {
        when(commandPreferencesMapper.deleteByIds(anyList())).thenReturn(1);

        assertTrue(commandPreferencesService.deleteWithCacheByIds(new Long[]{1L}, false));
    }
}
