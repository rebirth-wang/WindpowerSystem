package com.fastbee.iot.service;

import static com.fastbee.framework.core.util.RandomUtils.randomLongId;
import static com.fastbee.framework.core.util.RandomUtils.randomString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.Group;
import com.fastbee.iot.mapper.GroupMapper;
import com.fastbee.iot.model.DeviceGroupInput;
import com.fastbee.iot.model.IdOutput;
import com.fastbee.iot.service.impl.GroupServiceImpl;

/**
 * {@link GroupServiceImpl} 单元测试
 *
 * <p>不覆盖依赖 {@code SecurityUtils} 的 {@code insertGroup}，建议由集成测试覆盖。</p>
 *
 * @author fastbee
 */
@DisplayName("设备分组 Service 单元测试")
public class GroupServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private GroupServiceImpl groupService;

    @Mock
    private GroupMapper groupMapper;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), Group.class);
        ReflectionTestUtils.setField(groupService, "baseMapper", groupMapper);
    }

    @Test
    @DisplayName("selectGroupByGroupId - 应返回分组")
    void testSelectGroupByGroupId_ShouldReturnGroup() {
        Long groupId = randomLongId();
        Group query = new Group();
        query.setGroupId(groupId);
        Group expected = new Group();
        expected.setGroupId(groupId);
        expected.setGroupName(randomString());
        when(groupMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(expected);

        Group result = groupService.selectGroupByGroupId(query);

        assertNotNull(result);
        assertEquals(groupId, result.getGroupId());
        verify(groupMapper).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("selectDeviceIdsByGroupId - 应返回设备 ID 数组")
    void testSelectDeviceIdsByGroupId_ShouldReturnIds() {
        Long groupId = randomLongId();
        IdOutput o1 = new IdOutput();
        o1.setId(10L);
        IdOutput o2 = new IdOutput();
        o2.setId(20L);
        when(groupMapper.selectDeviceIdsByGroupId(groupId)).thenReturn(List.of(o1, o2));

        Long[] result = groupService.selectDeviceIdsByGroupId(groupId);

        assertEquals(2, result.length);
        assertEquals(10L, result[0]);
        assertEquals(20L, result[1]);
    }

    @Test
    @DisplayName("updateGroup - 应设置更新时间并更新")
    void testUpdateGroup_ShouldUpdate() {
        Group group = new Group();
        group.setGroupId(randomLongId());
        when(groupMapper.updateById(any(Group.class))).thenReturn(1);

        int rows = groupService.updateGroup(group);

        assertEquals(1, rows);
        assertNotNull(group.getUpdateTime());
        verify(groupMapper).updateById(group);
    }

    @Test
    @DisplayName("updateDeviceGroups - 无设备时应只清空关联")
    void testUpdateDeviceGroups_NoDevices_ShouldOnlyDeleteRelations() {
        Long groupId = randomLongId();
        DeviceGroupInput input = new DeviceGroupInput();
        input.setGroupId(groupId);
        input.setDeviceIds(new Long[] {});

        int result = groupService.updateDeviceGroups(input);

        assertEquals(1, result);
        verify(groupMapper).deleteDeviceGroupByGroupIds(new Long[] {groupId});
        verify(groupMapper, never()).insertDeviceGroups(any(DeviceGroupInput.class));
    }

    @Test
    @DisplayName("updateDeviceGroups - 有设备时应删除后插入关联")
    void testUpdateDeviceGroups_WithDevices_ShouldReplace() {
        Long groupId = randomLongId();
        DeviceGroupInput input = new DeviceGroupInput();
        input.setGroupId(groupId);
        input.setDeviceIds(new Long[] {1L, 2L});

        int result = groupService.updateDeviceGroups(input);

        assertEquals(1, result);
        verify(groupMapper).deleteDeviceGroupByGroupIds(new Long[] {groupId});
        verify(groupMapper).insertDeviceGroups(input);
    }

    @Test
    @DisplayName("deleteGroupByGroupIds - 应先删设备关联再删分组")
    void testDeleteGroupByGroupIds_ShouldCascade() {
        Long[] groupIds = {randomLongId(), randomLongId()};
        when(groupMapper.deleteBatchIds(any(List.class))).thenReturn(2);

        int rows = groupService.deleteGroupByGroupIds(groupIds);

        assertEquals(2, rows);
        verify(groupMapper).deleteDeviceGroupByGroupIds(groupIds);
        verify(groupMapper).deleteBatchIds(Arrays.asList(groupIds));
    }

    @Test
    @DisplayName("deleteGroupByGroupId - 应按 ID 删除")
    void testDeleteGroupByGroupId_ShouldDelete() {
        Long id = randomLongId();
        when(groupMapper.deleteById(id)).thenReturn(1);

        assertEquals(1, groupService.deleteGroupByGroupId(id));
        verify(groupMapper).deleteById(id);
    }
}
