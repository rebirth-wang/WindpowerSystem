package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
import org.mockito.Spy;
import org.quartz.SchedulerException;
import org.springframework.test.util.ReflectionTestUtils;

import com.fastbee.common.exception.job.TaskException;
import com.fastbee.common.extend.core.domin.entity.SysDept;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.enums.DeviceJobTypeEnum;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.DeviceJob;
import com.fastbee.iot.domain.Report;
import com.fastbee.iot.domain.ReportRule;
import com.fastbee.iot.domain.ReportRuleData;
import com.fastbee.iot.mapper.*;
import com.fastbee.iot.model.vo.ReportRuleVO;
import com.fastbee.iot.model.vo.ReportVO;
import com.fastbee.iot.service.impl.ReportServiceImpl;
import com.fastbee.system.mapper.SysUserMapper;

@DisplayName("报表管理 Service 单元测试")
class ReportServiceImplTest extends BaseMockitoUnitTest {

    @Spy
    @InjectMocks
    private ReportServiceImpl reportService;

    @Mock
    private ReportMapper reportMapper;
    @Mock
    private ReportRuleMapper reportRuleMapper;
    @Mock
    private ReportRuleDataMapper reportRuleDataMapper;
    @Mock
    private IDeviceJobService deviceJobService;
    @Mock
    private ReportRecordsMapper reportRecordsMapper;
    @Mock
    private DeviceMapper deviceMapper;
    @Mock
    private ThingsModelMapper thingsModelMapper;
    @Mock
    private SceneModelDeviceMapper sceneModelDeviceMapper;
    @Mock
    private DataCenterService dataCenterService;
    @Mock
    private SysUserMapper sysUserMapper;

    private MockedStatic<SecurityUtils> securityUtilsMock;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), Report.class);
        ReflectionTestUtils.setField(reportService, "baseMapper", reportMapper);
    }

    @AfterEach
    void tearDown() {
        if (securityUtilsMock != null) {
            securityUtilsMock.close();
        }
    }

    @Test
    @DisplayName("queryByIdWithCache - 为空时应返回 null")
    void testQueryByIdWithCache_NoData_ShouldReturnNull() {
        Report query = new Report();
        query.setId(1L);
        when(reportMapper.selectOne(any(LambdaQueryWrapper.class), eq(true))).thenReturn(null);

        ReportVO result = reportService.queryByIdWithCache(query);

        assertNull(result);
    }

    @Test
    @DisplayName("queryByIdWithCache - 有规则时应组装规则和变量")
    void testQueryByIdWithCache_ShouldAssembleRules() {
        Report query = new Report();
        query.setId(1L);
        Report report = new Report();
        report.setId(1L);
        ReportRule rule = new ReportRule();
        rule.setId(11L);
        rule.setReportId(1L);
        ReportRuleData ruleData = new ReportRuleData();
        ruleData.setReportId(1L);
        ruleData.setReportRuleId(11L);
        when(reportMapper.selectOne(any(LambdaQueryWrapper.class), eq(true))).thenReturn(report);
        when(reportRuleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(rule));
        when(reportRuleDataMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(ruleData));

        ReportVO result = reportService.queryByIdWithCache(query);

        assertNotNull(result);
        assertEquals(1, result.getReportRuleVOList().size());
        ReportRuleVO reportRuleVO = result.getReportRuleVOList().get(0);
        assertEquals(1, reportRuleVO.getReportRuleDataVOList().size());
    }

    @Test
    @DisplayName("insertWithCache - 保存成功时应写入规则并创建任务")
    void testInsertWithCache_ShouldSaveAndCreateJob() throws SchedulerException, TaskException {
        ReportVO reportVO = new ReportVO();
        reportVO.setName("日报");
        reportVO.setCycleType(1);
        reportVO.setCycle("[{\"interval\":300}]");
        ReportRuleVO ruleVO = new ReportRuleVO();
        reportVO.setReportRuleVOList(List.of(ruleVO));
        SysDept dept = new SysDept();
        dept.setDeptUserId(100L);
        dept.setDeptName("tenant");
        SysUser user = new SysUser();
        user.setUserName("tester");
        user.setDept(dept);
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getLoginUser).thenReturn(loginUser);
        doAnswer(invocation -> {
            Report saved = invocation.getArgument(0);
            saved.setId(9L);
            return true;
        }).when(reportService).save(any(Report.class));
        when(reportRuleMapper.insert(any(ReportRule.class))).thenAnswer(invocation -> {
            ReportRule saved = invocation.getArgument(0);
            saved.setId(18L);
            return 1;
        });

        Boolean result = reportService.insertWithCache(reportVO);

        assertTrue(result);
        verify(reportRuleMapper).insert(any(ReportRule.class));
        verify(deviceJobService).insertJob(any(DeviceJob.class));
    }

    @Test
    @DisplayName("deleteWithCacheByIds - 删除成功时应级联删除记录和任务")
    void testDeleteWithCacheByIds_ShouldCascadeDelete() throws SchedulerException {
        doReturn(true).when(reportService).removeByIds(anyList());

        Boolean result = reportService.deleteWithCacheByIds(new Long[]{1L, 2L}, true);

        assertTrue(result);
        verify(reportRecordsMapper).delete(any(LambdaQueryWrapper.class));
        verify(deviceJobService).deleteJobByJobTypeAndDatasourceIds(new Long[]{1L}, DeviceJobTypeEnum.REPORT.getType());
        verify(deviceJobService).deleteJobByJobTypeAndDatasourceIds(new Long[]{2L}, DeviceJobTypeEnum.REPORT.getType());
    }
}
