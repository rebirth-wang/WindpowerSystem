package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

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
import org.quartz.SchedulerException;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.DeviceJob;
import com.fastbee.iot.domain.DeviceShare;
import com.fastbee.iot.mapper.DeviceJobMapper;
import com.fastbee.iot.mapper.DeviceMapper;
import com.fastbee.iot.mapper.DeviceShareMapper;
import com.fastbee.iot.mapper.SceneDeviceMapper;
import com.fastbee.iot.service.impl.DeviceShareServiceImpl;
import com.fastbee.system.mapper.SysUserMapper;

/**
 * {@link DeviceShareServiceImpl} 单元测试
 *
 * <p>跳过依赖 SecurityUtils 的 selectDeviceShareVOList/insert/update 等方法。</p>
 *
 * @author fastbee
 */
@DisplayName("设备分享 Service 单元测试")
public class DeviceShareServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private DeviceShareServiceImpl deviceShareService;

    @Mock
    private DeviceShareMapper deviceShareMapper;
    @Mock
    private SceneDeviceMapper sceneDeviceMapper;
    @Mock
    private DeviceMapper deviceMapper;
    @Mock
    private ISceneService sceneService;
    @Mock
    private SysUserMapper userMapper;
    @Mock
    private DeviceJobMapper deviceJobMapper;
    @Mock
    private IDeviceJobService deviceJobService;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), DeviceShare.class);
        ReflectionTestUtils.setField(deviceShareService, "baseMapper", deviceShareMapper);
    }

    @Test
    @DisplayName("selectDeviceShareByDeviceIdAndUserId - 应按条件查询")
    void testSelectDeviceShareByDeviceIdAndUserId_ShouldSelectOne() {
        DeviceShare row = new DeviceShare();
        when(deviceShareMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(row);

        DeviceShare result = deviceShareService.selectDeviceShareByDeviceIdAndUserId(1L, 2L);

        assertNotNull(result);
        verify(deviceShareMapper).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("selectDeviceShareByDeviceId - 应返回列表")
    void testSelectDeviceShareByDeviceId_ShouldSelectList() {
        when(deviceShareMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(new DeviceShare()));

        List<DeviceShare> result = deviceShareService.selectDeviceShareByDeviceId(1L);

        assertEquals(1, result.size());
        verify(deviceShareMapper).selectList(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("deleteDeviceShareByDeviceIds - 应按 deviceIds 删除")
    void testDeleteDeviceShareByDeviceIds_ShouldDelete() {
        when(deviceShareMapper.delete(any())).thenReturn(2);

        int rows = deviceShareService.deleteDeviceShareByDeviceIds(new Long[]{1L, 2L});

        assertEquals(2, rows);
        verify(deviceShareMapper).delete(any());
    }

    @Test
    @DisplayName("deleteDeviceShareByDeviceId - 应按 deviceId 删除")
    void testDeleteDeviceShareByDeviceId_ShouldDelete() {
        when(deviceShareMapper.delete(any())).thenReturn(1);

        int rows = deviceShareService.deleteDeviceShareByDeviceId(1L);

        assertEquals(1, rows);
        verify(deviceShareMapper).delete(any());
    }

    @Test
    @DisplayName("selectShareUser - 应委托 Mapper")
    void testSelectShareUser_ShouldDelegate() {
        DeviceShare share = new DeviceShare();
        SysUser u = new SysUser();
        when(deviceShareMapper.selectShareUser(share)).thenReturn(u);

        assertSame(u, deviceShareService.selectShareUser(share));
        verify(deviceShareMapper).selectShareUser(share);
    }

    @Test
    @DisplayName("deleteDeviceShareByDeviceIdAndUserId - 删除成功应清理场景与定时任务")
    void testDeleteDeviceShareByDeviceIdAndUserId_DeleteSuccess_ShouldCascade() {
        DeviceShare share = new DeviceShare();
        share.setDeviceId(10L);
        share.setUserId(20L);

        when(deviceShareMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);

        Device device = new Device();
        device.setDeviceId(10L);
        device.setSerialNumber("SN");
        when(deviceMapper.selectById(10L)).thenReturn(device);
        when(sceneDeviceMapper.listSceneIdByDeviceIdAndUserId(eq("SN"), anyList())).thenReturn(new Long[]{1L});

        SysUser user = new SysUser();
        user.setUserId(20L);
        user.setUserName("u1");
        when(userMapper.selectUserById(20L)).thenReturn(user);

        DeviceJob job = new DeviceJob();
        job.setJobId(99L);
        when(deviceJobMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(job));

        int rows = deviceShareService.deleteDeviceShareByDeviceIdAndUserId(share);

        assertEquals(1, rows);
        verify(sceneService).deleteSceneBySceneIds(new Long[]{1L});
        try {
            verify(deviceJobService).deleteJobByIds(new Long[]{99L});
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("deleteDeviceShareByDeviceIdAndUserId - 删除失败不应联动清理")
    void testDeleteDeviceShareByDeviceIdAndUserId_DeleteFail_ShouldNotCascade() {
        DeviceShare share = new DeviceShare();
        share.setDeviceId(10L);
        share.setUserId(20L);
        when(deviceShareMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(0);

        int rows = deviceShareService.deleteDeviceShareByDeviceIdAndUserId(share);

        assertEquals(0, rows);
        verifyNoInteractions(sceneService, deviceJobService);
    }
}

