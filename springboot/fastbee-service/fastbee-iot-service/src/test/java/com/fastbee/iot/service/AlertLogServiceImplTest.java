package com.fastbee.iot.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.core.domin.model.LoginUser;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.framework.core.ut.BaseMockitoUnitTest;
import com.fastbee.iot.domain.AlertLog;
import com.fastbee.iot.mapper.AlertLogMapper;
import com.fastbee.iot.model.param.DataCenterParam;
import com.fastbee.iot.model.vo.AlertLogVO;
import com.fastbee.iot.service.impl.AlertLogServiceImpl;

@DisplayName("告警日志 Service 单元测试")
class AlertLogServiceImplTest extends BaseMockitoUnitTest {

    @InjectMocks
    private AlertLogServiceImpl alertLogService;

    @Mock
    private AlertLogMapper alertLogMapper;

    private MockedStatic<SecurityUtils> securityUtilsMock;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), AlertLog.class);
        ReflectionTestUtils.setField(alertLogService, "baseMapper", alertLogMapper);
    }

    @AfterEach
    void tearDown() {
        if (securityUtilsMock != null) {
            securityUtilsMock.close();
        }
    }

    @Test
    @DisplayName("selectAlertLogByAlertLogId - 非机构用户时应按 userId 过滤")
    void testSelectAlertLogByAlertLogId_NoDept_ShouldQueryByUserId() {
        AlertLog query = new AlertLog();
        SysUser user = new SysUser();
        user.setUserId(10L);
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);
        AlertLog expected = new AlertLog();

        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getLoginUser).thenReturn(loginUser);
        when(alertLogMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(expected);

        AlertLog result = alertLogService.selectAlertLogByAlertLogId(query);

        assertSame(expected, result);
        verify(alertLogMapper).selectOne(any(LambdaQueryWrapper.class));
    }

    @Test
    @DisplayName("insertAlertLogBatch - mapper 成功时应返回 1")
    void testInsertAlertLogBatch_Success_ShouldReturnOne() {
        when(alertLogMapper.insertBatch(anyList())).thenReturn(true);

        int result = alertLogService.insertAlertLogBatch(List.of(new AlertLog()));

        assertEquals(1, result);
    }

    @Test
    @DisplayName("updateAlertLog - status=1 且 remark 为空时应补默认备注")
    void testUpdateAlertLog_StatusOne_ShouldFillDefaultRemark() {
        AlertLog log = new AlertLog();
        log.setStatus(1);
        when(alertLogMapper.updateById(log)).thenReturn(1);

        int result = alertLogService.updateAlertLog(log);

        assertEquals(1, result);
        assertEquals("无需处理", log.getRemark());
        assertNotNull(log.getUpdateTime());
    }

    @Test
    @DisplayName("countAlertProcess - 时间格式错误时应抛出异常")
    void testCountAlertProcess_InvalidTime_ShouldThrowException() {
        DataCenterParam param = new DataCenterParam();
        param.setBeginTime("bad");
        param.setEndTime("2024-01-01 00:00:00");

        assertThrows(IllegalArgumentException.class, () -> alertLogService.countAlertProcess(param));
    }

    @Test
    @DisplayName("pageAlertLogVO - 机构用户并指定 deptUserId 时应返回转换分页")
    void testPageAlertLogVO_WithDeptUserId_ShouldReturnConvertedPage() {
        AlertLogVO query = new AlertLogVO();
        query.setPageNum(1);
        query.setPageSize(10);
        query.setDeptUserId(20L);

        SysUser user = new SysUser();
        user.setDeptId(1L);
        LoginUser loginUser = new LoginUser();
        loginUser.setUser(user);

        AlertLog entity = new AlertLog();
        entity.setAlertLogId(1L);
        Page<AlertLog> page = new Page<>();
        page.setTotal(1);
        page.setRecords(List.of(entity));

        SecurityUtilsTestHelper.prepareSecurityUtils();
        securityUtilsMock = mockStatic(SecurityUtils.class);
        securityUtilsMock.when(SecurityUtils::getLoginUser).thenReturn(loginUser);
        when(alertLogMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        Page<AlertLogVO> result = alertLogService.pageAlertLogVO(query);

        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
    }
}
