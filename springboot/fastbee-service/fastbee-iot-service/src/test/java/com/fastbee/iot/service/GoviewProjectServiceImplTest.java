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
import com.fastbee.iot.domain.GoviewProject;
import com.fastbee.iot.mapper.GoviewProjectMapper;
import com.fastbee.iot.service.impl.GoviewProjectServiceImpl;

@DisplayName("Goview 项目 Service 单元测试")
class GoviewProjectServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private GoviewProjectServiceImpl goviewProjectService;

    @Mock
    private GoviewProjectMapper goviewProjectMapper;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), GoviewProject.class);
        ReflectionTestUtils.setField(goviewProjectService, "baseMapper", goviewProjectMapper);
    }

    @Test
    @DisplayName("selectGoviewProjectById - 应返回单条项目")
    void testSelectGoviewProjectById_ShouldReturnProject() {
        GoviewProject query = new GoviewProject();
        GoviewProject expected = new GoviewProject();
        when(goviewProjectMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(expected);

        GoviewProject result = goviewProjectService.selectGoviewProjectById(query);

        assertSame(expected, result);
    }

    @Test
    @DisplayName("selectGoviewProjectList - 应返回分页结果")
    void testSelectGoviewProjectList_ShouldReturnPage() {
        GoviewProject query = new GoviewProject();
        query.setPageNum(1);
        query.setPageSize(10);
        Page<GoviewProject> expected = new Page<>();
        expected.setRecords(List.of(new GoviewProject()));
        when(goviewProjectMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(expected);

        Page<GoviewProject> result = goviewProjectService.selectGoviewProjectList(query);

        assertSame(expected, result);
    }

    @Test
    @DisplayName("insertGoviewProject - 成功时应生成 id 并返回")
    void testInsertGoviewProject_Success_ShouldReturnId() {
        GoviewProject project = new GoviewProject();
        when(goviewProjectMapper.insert(project)).thenReturn(1);

        String result = goviewProjectService.insertGoviewProject(project);

        assertNotNull(result);
        assertEquals(project.getId(), result);
        assertNotNull(project.getCreateTime());
        assertNotNull(project.getUpdateTime());
    }

    @Test
    @DisplayName("insertGoviewProject - 失败时应返回 null")
    void testInsertGoviewProject_Fail_ShouldReturnNull() {
        GoviewProject project = new GoviewProject();
        when(goviewProjectMapper.insert(project)).thenReturn(0);

        String result = goviewProjectService.insertGoviewProject(project);

        assertNull(result);
    }

    @Test
    @DisplayName("updateGoviewProject - 应设置更新时间")
    void testUpdateGoviewProject_ShouldSetUpdateTime() {
        GoviewProject project = new GoviewProject();
        when(goviewProjectMapper.updateById(project)).thenReturn(1);

        int result = goviewProjectService.updateGoviewProject(project);

        assertEquals(1, result);
        assertNotNull(project.getUpdateTime());
    }
}
