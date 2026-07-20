package com.fastbee.iot.service;

import static com.fastbee.framework.core.util.RandomUtils.randomLongId;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
import org.quartz.SchedulerException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.DeviceJob;
import com.fastbee.iot.domain.DeviceShare;
import com.fastbee.iot.domain.DeviceUser;
import com.fastbee.iot.mapper.DeviceJobMapper;
import com.fastbee.iot.mapper.DeviceMapper;
import com.fastbee.iot.mapper.DeviceUserMapper;
import com.fastbee.iot.mapper.SceneDeviceMapper;
import com.fastbee.iot.model.vo.DeviceUserVO;
import com.fastbee.iot.service.impl.DeviceUserServiceImpl;
import com.fastbee.system.mapper.SysUserMapper;

/**
 * {@link DeviceUserServiceImpl} 单元测试
 *
 * @author fastbee
 */
@DisplayName("设备用户 Service 单元测试")
public class DeviceUserServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private DeviceUserServiceImpl deviceUserService;

    @Mock
    private DeviceUserMapper deviceUserMapper;
    @Mock
    private IDeviceShareService deviceShareService;
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
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), DeviceUser.class);
        ReflectionTestUtils.setField(deviceUserService, "baseMapper", deviceUserMapper);
    }

    @Test
    @DisplayName("selectDeviceUserByDeviceId - 应委托 Mapper")
    void testSelectDeviceUserByDeviceId_ShouldDelegate() {
        Long deviceId = randomLongId();
        DeviceUserVO vo = new DeviceUserVO();
        vo.setDeviceId(deviceId);
        when(deviceUserMapper.selectDeviceUserByDeviceId(deviceId)).thenReturn(vo);

        DeviceUserVO result = deviceUserService.selectDeviceUserByDeviceId(deviceId);

        assertNotNull(result);
        assertEquals(deviceId, result.getDeviceId());
        verify(deviceUserMapper).selectDeviceUserByDeviceId(deviceId);
    }

    @Test
    @DisplayName("selectDeviceUserList - 应委托 Mapper 分页查询")
    void testSelectDeviceUserList_ShouldDelegate() {
        DeviceUser query = new DeviceUser();
        query.setPageNum(1);
        query.setPageSize(10);
        Page<DeviceUser> page = new Page<>();
        page.setTotal(0);
        when(deviceUserMapper.selectDeviceUserList(any(Page.class), any(DeviceUser.class))).thenReturn(page);

        Page<DeviceUser> result = deviceUserService.selectDeviceUserList(query);

        assertNotNull(result);
        verify(deviceUserMapper).selectDeviceUserList(any(Page.class), any(DeviceUser.class));
    }

    @Test
    @DisplayName("insertDeviceUser - 已存在绑定时应抛异常")
    void testInsertDeviceUser_Exists_ShouldThrow() {
        DeviceUser input = new DeviceUser();
        input.setDeviceId(randomLongId());
        input.setUserId(randomLongId());

        Page<DeviceUser> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(new DeviceUser()));
        when(deviceUserMapper.selectDeviceUserList(any(Page.class), any(DeviceUser.class))).thenReturn(page);

        assertThrows(RuntimeException.class, () -> deviceUserService.insertDeviceUser(input));
        verify(deviceUserMapper, never()).insert(any(DeviceUser.class));
    }

    @Test
    @DisplayName("insertDeviceUser - 未存在绑定时应设置 createTime 并插入")
    void testInsertDeviceUser_NotExists_ShouldInsert() {
        DeviceUser input = new DeviceUser();
        input.setDeviceId(randomLongId());
        input.setUserId(randomLongId());

        Page<DeviceUser> page = new Page<>();
        page.setTotal(0);
        page.setRecords(List.of());
        when(deviceUserMapper.selectDeviceUserList(any(Page.class), any(DeviceUser.class))).thenReturn(page);
        when(deviceUserMapper.insert(input)).thenReturn(1);

        int rows = deviceUserService.insertDeviceUser(input);

        assertEquals(1, rows);
        assertNotNull(input.getCreateTime());
        verify(deviceUserMapper).insert(input);
    }

    @Test
    @DisplayName("updateDeviceUser - 应设置 updateTime 并更新")
    void testUpdateDeviceUser_ShouldUpdate() {
        DeviceUser input = new DeviceUser();
        input.setDeviceId(randomLongId());
        when(deviceUserMapper.updateDeviceUser(input)).thenReturn(1);

        int rows = deviceUserService.updateDeviceUser(input);

        assertEquals(1, rows);
        assertNotNull(input.getUpdateTime());
        verify(deviceUserMapper).updateDeviceUser(input);
    }

    @Test
    @DisplayName("deleteDeviceUserByDeviceIds - 应按 deviceIds 删除")
    void testDeleteDeviceUserByDeviceIds_ShouldDelete() {
        Long[] ids = {randomLongId(), randomLongId()};
        when(deviceUserMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(2);

        int rows = deviceUserService.deleteDeviceUserByDeviceIds(ids);

        assertEquals(2, rows);
        verify(deviceUserMapper).delete(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("insertDeviceUserList - DuplicateKeyException 时应抛业务异常")
    void testInsertDeviceUserList_DuplicateKey_ShouldThrow() {
        when(deviceUserMapper.insertBatch(anyList())).thenThrow(new DuplicateKeyException("dup"));

        assertThrows(RuntimeException.class, () -> deviceUserService.insertDeviceUserList(List.of(new DeviceUser())));
    }

    @Test
    @DisplayName("deleteDeviceUser - 删除失败时不应触发联动清理")
    void testDeleteDeviceUser_DeleteFail_ShouldNotCascade() {
        DeviceUser input = new DeviceUser();
        input.setDeviceId(randomLongId());
        input.setUserId(randomLongId());
        when(deviceUserMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(0);

        int rows = deviceUserService.deleteDeviceUser(input);

        assertEquals(0, rows);
        verifyNoInteractions(sceneService, deviceJobService, deviceShareService);
    }

    @Test
    @DisplayName("deleteDeviceUser - 删除成功时应触发场景/任务/分享清理")
    void testDeleteDeviceUser_DeleteSuccess_ShouldCascade() throws SchedulerException {
        Long deviceId = randomLongId();
        Long userId = randomLongId();

        DeviceUser input = new DeviceUser();
        input.setDeviceId(deviceId);
        input.setUserId(userId);
        when(deviceUserMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);

        Device device = new Device();
        device.setDeviceId(deviceId);
        device.setSerialNumber("SN001");
        when(deviceMapper.selectById(deviceId)).thenReturn(device);
        when(sceneDeviceMapper.listSceneIdByDeviceIdAndUserId(eq("SN001"), anyList())).thenReturn(new Long[]{1L});

        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setUserName("u1");
        when(userMapper.selectUserById(userId)).thenReturn(sysUser);

        DeviceJob job = new DeviceJob();
        job.setJobId(99L);
        when(deviceJobMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(job));

        DeviceShare share = new DeviceShare();
        share.setDeviceId(deviceId);
        share.setUserId(777L);
        when(deviceShareService.selectDeviceShareByDeviceId(deviceId)).thenReturn(List.of(share));

        int rows = deviceUserService.deleteDeviceUser(input);

        assertEquals(1, rows);
        verify(sceneService).deleteSceneBySceneIds(new Long[]{1L});
        verify(deviceJobService).deleteJobByIds(new Long[]{99L});
        verify(deviceShareService).deleteDeviceShareByDeviceIdAndUserId(share);
    }

    @Test
    @DisplayName("getDeviceUserAndShare - 应包含设备用户和分享用户")
    void testGetDeviceUserAndShare_ShouldReturnCombined() {
        Long deviceId = randomLongId();
        DeviceUserVO owner = new DeviceUserVO();
        owner.setDeviceId(deviceId);
        owner.setUserId(1L);
        owner.setPhonenumber("p");
        when(deviceUserMapper.selectDeviceUserByDeviceId(deviceId)).thenReturn(owner);

        DeviceShare share = new DeviceShare();
        share.setDeviceId(deviceId);
        share.setUserId(2L);
        share.setPhonenumber("p2");
        when(deviceShareService.selectDeviceShareByDeviceId(deviceId)).thenReturn(List.of(share));

        List<DeviceUser> result = deviceUserService.getDeviceUserAndShare(deviceId);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(u -> u.getUserId().equals(1L)));
        assertTrue(result.stream().anyMatch(u -> u.getUserId().equals(2L)));
    }
}

