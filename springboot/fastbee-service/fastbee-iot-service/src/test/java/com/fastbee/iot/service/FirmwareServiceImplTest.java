package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
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

import com.fastbee.common.extend.core.domin.entity.SysDept;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.Firmware;
import com.fastbee.iot.domain.FirmwareTask;
import com.fastbee.iot.mapper.FirmwareMapper;
import com.fastbee.iot.mapper.FirmwareTaskDetailMapper;
import com.fastbee.iot.mapper.FirmwareTaskMapper;
import com.fastbee.iot.service.impl.FirmwareServiceImpl;

@DisplayName("固件 Service 单元测试")
class FirmwareServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private FirmwareServiceImpl firmwareService;

    @Mock
    private FirmwareMapper firmwareMapper;

    @Mock
    private FirmwareTaskMapper firmwareTaskMapper;

    @Mock
    private FirmwareTaskDetailMapper firmwareTaskDetailMapper;

    private MockedStatic<SecurityUtils> securityUtilsMock;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), Firmware.class);
        ReflectionTestUtils.setField(firmwareService, "baseMapper", firmwareMapper);
    }

    @AfterEach
    void tearDown() {
        if (securityUtilsMock != null) {
            securityUtilsMock.close();
        }
    }

    @Test
    @DisplayName("insertFirmware - 最新版本时应重置旧版本并补齐租户信息")
    void testInsertFirmware_Latest_ShouldResetOldAndFillTenant() {
        Firmware firmware = new Firmware();
        firmware.setProductId(10L);
        firmware.setIsLatest(1);

        Firmware old = new Firmware();
        old.setFirmwareId(1L);
        old.setIsLatest(1);

        SysDept dept = new SysDept();
        dept.setDeptUserId(200L);
        dept.setDeptName("租户A");
        SysUser user = new SysUser();
        user.setUserName("tester");
        user.setDept(dept);
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);

        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getLoginUser).thenReturn(loginUser);
        when(firmwareMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(old));
        when(firmwareMapper.insert(firmware)).thenReturn(1);

        int result = firmwareService.insertFirmware(firmware);

        assertEquals(1, result);
        assertEquals(0, old.getIsLatest());
        assertEquals(200L, firmware.getTenantId());
        assertEquals("租户A", firmware.getTenantName());
        assertEquals("tester", firmware.getCreateBy());
        assertNotNull(firmware.getCreateTime());
        verify(firmwareMapper).updateById(old);
    }

    @Test
    @DisplayName("selectLatestFirmware - 有记录时应返回第一页第一条")
    void testSelectLatestFirmware_WithData_ShouldReturnFirstRecord() {
        Firmware firmware = new Firmware();
        Page<Firmware> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(firmware));
        when(firmwareMapper.selectLatestFirmware(any(IPage.class), eq(1L), eq(2L))).thenReturn(page);

        Firmware result = firmwareService.selectLatestFirmware(1L, 2L);

        assertSame(firmware, result);
    }

    @Test
    @DisplayName("deleteBatchByIds - 删除成功时应级联清理任务和详情")
    void testDeleteBatchByIds_ShouldDeleteTasksAndDetails() {
        FirmwareTask task = new FirmwareTask();
        task.setId(11L);
        task.setFirmwareId(1L);
        when(firmwareMapper.deleteBatchIds(List.of(1L, 2L))).thenReturn(2);
        when(firmwareTaskMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(task));
        when(firmwareTaskMapper.deleteBatchIds(List.of(11L))).thenReturn(1);
        when(firmwareTaskDetailMapper.delete(any(LambdaQueryWrapper.class))).thenReturn(1);

        int result = firmwareService.deleteBatchByIds(List.of(1L, 2L));

        assertEquals(2, result);
        verify(firmwareTaskMapper).deleteBatchIds(List.of(11L));
        verify(firmwareTaskDetailMapper).delete(any(LambdaQueryWrapper.class));
    }
}
