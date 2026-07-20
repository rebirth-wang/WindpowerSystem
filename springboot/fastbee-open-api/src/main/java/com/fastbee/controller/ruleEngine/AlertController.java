package com.fastbee.controller.ruleEngine;

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
import com.fastbee.iot.domain.Alert;
import com.fastbee.iot.domain.Scene;
import com.fastbee.iot.model.vo.AlertVO;
import com.fastbee.iot.service.IAlertService;
import com.fastbee.notify.domain.NotifyTemplate;

/**
 * 设备告警Controller
 *
 * @author kerwincui
 * @date 2022-01-13
 */
@Api(tags = "设备告警alert模块")
@RestController
@RequestMapping("/iot/alert")
public class AlertController extends BaseController
{
    @Resource
    private IAlertService alertService;

    /**
     * 查询设备告警列表
     */
    @ApiOperation("查询设备告警列表")
    @PreAuthorize("@ss.hasPermi('iot:alert:list')")
    @GetMapping("/list")
    public TableDataInfo list(Alert alert)
    {
        Page<AlertVO> voPage = alertService.pageAlertVO(alert);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 查询告警管理场景列表
     */
    @ApiOperation("查询告警管理场景列表")
    @GetMapping("/getScenesByAlertId/{alertId}")
    public TableDataInfo getScenesByAlertId(@PathVariable("alertId") Long alertId)
    {
        List<Scene> list = alertService.selectScenesByAlertId(alertId);
        return getDataTable(list);
    }

    /**
     * 查询告警通知模版列表
     */
    @ApiOperation("查询告警通知模版列表")
    @GetMapping("/listNotifyTemplate/{alertId}")
    public TableDataInfo listNotifyTemplate(@PathVariable("alertId") Long alertId)
    {
        List<NotifyTemplate> list = alertService.listNotifyTemplateByAlertId(alertId);
        return getDataTable(list);
    }

    /**
     * 导出设备告警列表
     */
    @ApiOperation("导出设备告警列表")
    @PreAuthorize("@ss.hasPermi('iot:alert:export')")
    @Log(title = "设备告警", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Alert alert)
    {
        Page<AlertVO> voPage = alertService.pageAlertVO(alert);
        ExcelUtil<AlertVO> util = new ExcelUtil<AlertVO>(AlertVO.class);
        util.exportExcel(response, voPage.getRecords(), "设备告警数据");
    }

    /**
     * 获取设备告警详细信息
     */
    @ApiOperation("获取设备告警详细信息")
    @PreAuthorize("@ss.hasPermi('iot:alert:query')")
    @GetMapping(value = "/{alertId}")
    public AjaxResult getInfo(@PathVariable("alertId") Long alertId)
    {
        Alert alert = new Alert();
        alert.setAlertId(alertId);
        return AjaxResult.success(alertService.selectAlertByAlertId(alert));
    }

    /**
     * 新增设备告警
     */
    @ApiOperation("新增设备告警")
    @PreAuthorize("@ss.hasPermi('iot:alert:add')")
    @Log(title = "设备告警", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody AlertVO alertVO)
    {
        // 查询所属机构
        SysUser user = getLoginUser().getUser();
        if (null != user.getDeptId()) {
            alertVO.setTenantId(user.getDept().getDeptUserId());
            alertVO.setTenantName(user.getDept().getDeptName());
        }
        alertVO.setCreateBy(user.getUserName());
        return toAjax(alertService.insertAlert(alertVO));
    }

    /**
     * 修改设备告警
     */
    @ApiOperation("修改设备告警")
    @PreAuthorize("@ss.hasPermi('iot:alert:edit')")
    @Log(title = "设备告警", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody AlertVO alertVO)
    {
        SecurityUtils.checkUserOperatePermission(alertVO.getTenantId(), alertVO.getCreateBy());
        alertVO.setUpdateBy(getUsername());
        return toAjax(alertService.updateAlert(alertVO));
    }

    /**
     * 删除设备告警
     */
    @ApiOperation("删除设备告警")
    @PreAuthorize("@ss.hasPermi('iot:alert:remove')")
    @Log(title = "设备告警", businessType = BusinessType.DELETE)
	@DeleteMapping("/{alertIds}")
    public AjaxResult remove(@PathVariable Long[] alertIds)
    {
        List<Alert> alertList = alertService.listByIds(Arrays.asList(alertIds));
        for (Alert alert : alertList) {
            SecurityUtils.checkUserOperatePermission(alert.getTenantId(), alert.getCreateBy());
        }
        return toAjax(alertService.deleteAlertByAlertIds(alertIds));
    }

    /**
     * 修改设备告警状态
     * @param alertId 告警id
     * @param status 状态
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('iot:alert:edit')")
    @Log(title = "设备告警", businessType = BusinessType.UPDATE)
    @PostMapping("/editStatus")
    public AjaxResult editStatus(Long alertId, Integer status)
    {
        Alert alert = alertService.getById(alertId);
        SecurityUtils.checkUserOperatePermission(alert.getTenantId(), alert.getCreateBy());
        return toAjax(alertService.editStatus(alertId, status));
    }
}
