package com.fastbee.controller.deviceLog;

import java.util.Arrays;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.annotation.Log;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.enums.BusinessType;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.core.domin.entity.SysUser;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.iot.domain.AlertLog;
import com.fastbee.iot.model.vo.AlertLogVO;
import com.fastbee.iot.service.IAlertLogService;

/**
 * 设备告警Controller
 *
 * @author kerwincui
 * @date 2022-01-13
 */
@Api(tags = "设备告警alertLog模块")
@RestController
@RequestMapping("/iot/alertLog")
public class AlertLogController extends BaseController
{
    @Resource
    private IAlertLogService alertLogService;

    /**
     * 查询设备告警列表
     */
    @ApiOperation("查询设备告警列表")
    @PreAuthorize("@ss.hasPermi('iot:alertLog:list')")
    @GetMapping("/list")
    public TableDataInfo list(AlertLogVO alertLogVO)
    {
        Page<AlertLogVO> voPage = alertLogService.pageAlertLogVO(alertLogVO);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出设备告警列表
     */
    @ApiOperation("导出设备告警列表")
    @PreAuthorize("@ss.hasPermi('iot:alertLog:export')")
    @Log(title = "设备告警", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, AlertLogVO alertLogVO)
    {
        Page<AlertLogVO> voPage = alertLogService.pageAlertLogVO(alertLogVO);
        ExcelUtil<AlertLogVO> util = new ExcelUtil<AlertLogVO>(AlertLogVO.class);
        util.exportExcel(response, voPage.getRecords(), "设备告警日志数据");
    }

    /**
     * 获取设备告警详细信息
     */
    @ApiOperation("获取设备告警详细信息")
    @PreAuthorize("@ss.hasPermi('iot:alertLog:query')")
    @GetMapping(value = "/{alertLogId}")
    public AjaxResult getInfo(@PathVariable("alertLogId") Long alertLogId)
    {
        AlertLog alertLog = new AlertLog();
        alertLog.setAlertLogId(alertLogId);
        return AjaxResult.success(alertLogService.selectAlertLogByAlertLogId(alertLog));
    }

    /**
     * 新增设备告警
     */
    @ApiOperation("新增设备告警")
    @PreAuthorize("@ss.hasPermi('iot:alertLog:add')")
    @Log(title = "设备告警", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AlertLog alertLog)
    {
        return toAjax(alertLogService.insertAlertLog(alertLog));
    }

    /**
     * 修改设备告警
     */
    @ApiOperation("修改设备告警")
    @PreAuthorize("@ss.hasPermi('iot:alertLog:edit')")
    @Log(title = "设备告警", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AlertLog alertLog)
    {
        SysUser user = getLoginUser().getUser();
        if (null == user.getDeptId()) {
            if (!user.getUserId().equals(alertLog.getUserId())) {
                return error(MessageUtils.message("no.operate.permission"));
            }
        } else {
            SecurityUtils.checkUserOperatePermission(alertLog.getUserId(), alertLog.getCreateBy());
        }
        alertLog.setUpdateBy(getUsername());
        return toAjax(alertLogService.updateAlertLog(alertLog));
    }

    /**
     * 修改设备告警
     */
    @ApiOperation("修改设备告警")
    @PreAuthorize("@ss.hasPermi('iot:alertLog:remove')")
    @Log(title = "设备告警", businessType = BusinessType.DELETE)
	@DeleteMapping("/{alertLogIds}")
    public AjaxResult remove(@PathVariable Long[] alertLogIds)
    {
        SysUser user = getLoginUser().getUser();
        List<AlertLog> alertLogList = alertLogService.listByIds(Arrays.asList(alertLogIds));
        for (AlertLog alertLog : alertLogList) {
            if (null == user.getDeptId()) {
                if (!user.getUserId().equals(alertLog.getUserId())) {
                    return error(MessageUtils.message("no.operate.permission"));
                }
            } else {
                SecurityUtils.checkUserOperatePermission(alertLog.getUserId(), alertLog.getCreateBy());
            }
        }
        return toAjax(alertLogService.removeBatchByIds(Arrays.asList(alertLogIds)));
    }
}
