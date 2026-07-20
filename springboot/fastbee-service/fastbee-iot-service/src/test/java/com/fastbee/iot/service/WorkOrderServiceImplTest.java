package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.common.extend.core.domin.entity.SysDept;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.enums.ObjectOperationTypeEnum;
import com.fastbee.common.extend.enums.WorkOrderStatusEnum;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.DeviceMaintenance;
import com.fastbee.iot.domain.WorkOrder;
import com.fastbee.iot.mapper.DeviceMapper;
import com.fastbee.iot.mapper.WorkOrderMapper;
import com.fastbee.iot.model.vo.WorkOrderVO;
import com.fastbee.iot.service.impl.WorkOrderServiceImpl;
import com.fastbee.system.mapper.SysUserMapper;

@DisplayName("工单 Service 单元测试")
class WorkOrderServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    @Spy
    private WorkOrderServiceImpl workOrderService;

    @Mock
    private SysUserMapper sysUserMapper;
    @Mock
    private DeviceMapper deviceMapper;
    @Mock
    private IObjectOperationLogService objectOperationLogService;
    @Mock
    private WorkOrderMapper workOrderMapper;

    private MockedStatic<com.fastbee.common.extend.utils.SecurityUtils> securityUtilsMock;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), WorkOrder.class);
        ReflectionTestUtils.setField(workOrderService, "baseMapper", workOrderMapper);
    }

    @AfterEach
    void tearDown() {
        if (securityUtilsMock != null) {
            securityUtilsMock.close();
        }
    }

    @Test
    @DisplayName("insertWithCache - 应回填租户和创建信息并记录操作日志")
    void testInsertWithCache_ShouldFillFieldsAndInsertLog() {
        WorkOrder add = new WorkOrder();
        add.setId(1L);
        add.setName("维修工单");
        SysDept dept = new SysDept();
        dept.setDeptUserId(100L);
        dept.setDeptName("tenantA");
        SysUser user = new SysUser();
        user.setUserName("tester");
        user.setDept(dept);
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(com.fastbee.common.extend.utils.SecurityUtils.class);
        securityUtilsMock.when(com.fastbee.common.extend.utils.SecurityUtils::getLoginUser).thenReturn(loginUser);
        doReturn(true).when(workOrderService).save(add);
        WorkOrderVO savedVO = new WorkOrderVO();
        savedVO.setId(1L);
        when(workOrderMapper.selectVoById(1L)).thenReturn(savedVO);

        WorkOrderVO result = workOrderService.insertWithCache(add);

        assertNotNull(result);
        assertEquals(100L, add.getTenantId());
        assertEquals("tenantA", add.getTenantName());
        assertEquals("tester", add.getCreateBy());
        assertEquals(WorkOrderStatusEnum.ORDER_DISPATCHED.getStatus(), add.getStatus());
        assertNotNull(add.getNumber());
        assertTrue(result.getNotifyFlag());
        verify(objectOperationLogService).insert(null, savedVO, 1L, ObjectOperationTypeEnum.WORK_ORDER.getType(), "tester");
    }

    @Test
    @DisplayName("changeStatus - 派单状态应设置通知标记")
    void testChangeStatus_Dispatched_ShouldSetNotifyFlag() {
        WorkOrderVO input = new WorkOrderVO();
        input.setId(2L);
        input.setStatus(WorkOrderStatusEnum.ORDER_DISPATCHED.getStatus());
        WorkOrderVO oldVO = new WorkOrderVO();
        oldVO.setId(2L);
        oldVO.setUserId(9L);
        WorkOrderVO newVO = new WorkOrderVO();
        newVO.setId(2L);
        newVO.setUserId(9L);
        newVO.setStatus(WorkOrderStatusEnum.ORDER_DISPATCHED.getStatus());
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(com.fastbee.common.extend.utils.SecurityUtils.class);
        securityUtilsMock.when(com.fastbee.common.extend.utils.SecurityUtils::getUsername).thenReturn("tester");
        when(workOrderMapper.selectVoById(2L)).thenReturn(oldVO, newVO);
        doReturn(true).when(workOrderService).updateById(any(WorkOrder.class));

        WorkOrderVO result = workOrderService.changeStatus(input);

        assertNotNull(result);
        assertTrue(result.getNotifyFlag());
        verify(objectOperationLogService).insert(oldVO, newVO, 2L, ObjectOperationTypeEnum.WORK_ORDER.getType(), "tester");
    }

    @Test
    @DisplayName("endUserQuery - 非创建人查询时应返回空")
    void testEndUserQuery_NotCreator_ShouldReturnNull() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(3L);
        workOrder.setCreateBy("otherUser");
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(com.fastbee.common.extend.utils.SecurityUtils.class);
        securityUtilsMock.when(com.fastbee.common.extend.utils.SecurityUtils::getUsername).thenReturn("tester");
        when(workOrderMapper.selectOne(any())).thenReturn(workOrder);

        WorkOrder result = workOrderService.endUserQuery(3L);

        assertNull(result);
    }

    @Test
    @DisplayName("deleteWithCacheByIds - 删除成功时应级联删除操作日志")
    void testDeleteWithCacheByIds_ShouldDeleteOperationLogs() {
        doReturn(true).when(workOrderService).removeByIds(List.of(1L, 2L));

        Boolean result = workOrderService.deleteWithCacheByIds(new Long[]{1L, 2L}, true);

        assertTrue(result);
        verify(objectOperationLogService).deleteByObjectIdAndType(List.of(1L, 2L), ObjectOperationTypeEnum.WORK_ORDER.getType());
    }

    @Test
    @DisplayName("generateDeviceMaintenanceWorkOrder - 应生成默认巡检工单")
    void testGenerateDeviceMaintenanceWorkOrder_ShouldGenerateDefaultWorkOrder() {
        DeviceMaintenance maintenance = new DeviceMaintenance();
        maintenance.setId(8L);
        maintenance.setName("月度保养");
        maintenance.setDeviceId(6L);
        maintenance.setTenantId(100L);
        maintenance.setTenantName("tenantA");
        maintenance.setCreateBy("tester");

        WorkOrder result = workOrderService.generateDeviceMaintenanceWorkOrder(maintenance);

        assertEquals(8L, result.getDeviceMaintenanceId());
        assertEquals(6L, result.getDeviceId());
        assertEquals(100L, result.getTenantId());
        assertEquals("tenantA", result.getTenantName());
        assertEquals("tester", result.getCreateBy());
        assertEquals(WorkOrderStatusEnum.PENDING.getStatus(), result.getStatus());
        assertNotNull(result.getNumber());
        assertTrue(result.getName().contains("月度保养"));
    }
}
