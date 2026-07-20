package com.fastbee.iot.service;

import static com.fastbee.framework.core.util.RandomUtils.randomLongId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
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

import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.DeviceAlertUser;
import com.fastbee.iot.mapper.DeviceAlertUserMapper;
import com.fastbee.iot.model.vo.DeviceAlertUserVO;
import com.fastbee.iot.service.impl.DeviceAlertUserServiceImpl;
import com.fastbee.system.service.ISysUserService;

/**
 * {@link DeviceAlertUserServiceImpl} 单元测试
 *
 * @author fastbee
 */
@DisplayName("设备告警用户 Service 单元测试")
public class DeviceAlertUserServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private DeviceAlertUserServiceImpl deviceAlertUserService;

    @Mock
    private DeviceAlertUserMapper deviceAlertUserMapper;
    @Mock
    private ISysUserService sysUserService;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), DeviceAlertUser.class);
        ReflectionTestUtils.setField(deviceAlertUserService, "baseMapper", deviceAlertUserMapper);
    }

    @Test
    @DisplayName("selectDeviceAlertUserList - 应委托 Mapper 分页查询")
    void testSelectDeviceAlertUserList_ShouldDelegate() {
        DeviceAlertUser query = new DeviceAlertUser();
        query.setPageNum(1);
        query.setPageSize(10);
        Page<DeviceAlertUserVO> page = new Page<>();
        page.setTotal(0);
        when(deviceAlertUserMapper.selectDeviceAlertUserList(any(Page.class), any(DeviceAlertUser.class))).thenReturn(page);

        Page<DeviceAlertUserVO> result = deviceAlertUserService.selectDeviceAlertUserList(query);

        assertNotNull(result);
        verify(deviceAlertUserMapper).selectDeviceAlertUserList(any(Page.class), any(DeviceAlertUser.class));
    }

    @Test
    @DisplayName("insertDeviceAlertUser - 新增时应排除已存在用户并逐个插入")
    void testInsertDeviceAlertUser_ShouldInsertOnlyNewUsers() {
        Long deviceId = randomLongId();
        Long existedUserId = 1L;
        Long newUserId = 2L;

        DeviceAlertUserVO existed = new DeviceAlertUserVO();
        existed.setUserId(existedUserId);

        when(deviceAlertUserMapper.selectDeviceAlertUserList(any(DeviceAlertUser.class)))
                .thenReturn(List.of(existed));

        DeviceAlertUserVO input = new DeviceAlertUserVO();
        input.setDeviceId(deviceId);
        input.setUserIdList(new ArrayList<>(List.of(existedUserId, newUserId)));
        when(deviceAlertUserMapper.insert(any(DeviceAlertUser.class))).thenReturn(1);

        int result = deviceAlertUserService.insertDeviceAlertUser(input);

        assertEquals(1, result);
        // 只会插入 newUserId
        verify(deviceAlertUserMapper, times(1)).insert(any(DeviceAlertUser.class));
    }

    @Test
    @DisplayName("deleteByDeviceIdAndUserId - 应按 deviceId+userId 删除")
    void testDeleteByDeviceIdAndUserId_ShouldDelete() {
        when(deviceAlertUserMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);

        int rows = deviceAlertUserService.deleteByDeviceIdAndUserId(1L, 2L);

        assertEquals(1, rows);
        verify(deviceAlertUserMapper).delete(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("selectUserList - 应委托 sysUserService")
    void testSelectUserList_ShouldDelegate() {
        SysUser query = new SysUser();
        Page<SysUser> page = new Page<>();
        page.setTotal(1);
        when(sysUserService.queryUserList(query)).thenReturn(page);

        Page<SysUser> result = deviceAlertUserService.selectUserList(query);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        verify(sysUserService).queryUserList(query);
    }
}

