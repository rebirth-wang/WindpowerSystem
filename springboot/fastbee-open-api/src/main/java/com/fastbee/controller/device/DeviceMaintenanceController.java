package com.fastbee.controller.device;

import java.util.Arrays;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.utils.SecurityUtils;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.iot.domain.DeviceMaintenance;
import com.fastbee.iot.model.vo.DeviceMaintenanceVO;
import com.fastbee.iot.service.IDeviceMaintenanceService;

/**
 * 设备维保Controller
 *
 * @author fastbee
 * @date 2025-12-25
 */
@RestController
@RequestMapping("/iot/maintenance")
@Api(tags = "设备维保")
public class DeviceMaintenanceController extends BaseController {

    @Resource
    private IDeviceMaintenanceService deviceMaintenanceService;

    /**
     * 查询设备维保列表
     */
    @PreAuthorize("@ss.hasPermi('iot:maintenance:list')")
    @GetMapping("/list")
    @ApiOperation("查询设备维保列表")
    public TableDataInfo list(DeviceMaintenance deviceMaintenance) {
        Page<DeviceMaintenanceVO> voPage = deviceMaintenanceService.pageDeviceMaintenanceVO(deviceMaintenance);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出设备维保列表
     */
    @ApiOperation("导出设备维保列表")
    @PreAuthorize("@ss.hasPermi('iot:maintenance:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, DeviceMaintenance deviceMaintenance) {
        Page<DeviceMaintenanceVO> voPage = deviceMaintenanceService.pageDeviceMaintenanceVO(deviceMaintenance);
        ExcelUtil<DeviceMaintenanceVO> util = new ExcelUtil<DeviceMaintenanceVO>(DeviceMaintenanceVO.class);
        util.exportExcel(response, voPage.getRecords(), "设备维保数据");
    }

    /**
     * 获取设备维保详细信息
     */
    @PreAuthorize("@ss.hasPermi('iot:maintenance:query')")
    @GetMapping(value = "/{id}")
    @ApiOperation("获取设备维保详细信息")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        DeviceMaintenance deviceMaintenance = new DeviceMaintenance();
        deviceMaintenance.setId(id);
        return success(deviceMaintenanceService.queryByIdWithCache(deviceMaintenance));
    }

    /**
     * 新增设备维保
     */
    @PreAuthorize("@ss.hasPermi('iot:maintenance:add')")
    @PostMapping
    @ApiOperation("新增设备维保")
    public AjaxResult add(@RequestBody DeviceMaintenance deviceMaintenance) {
        return deviceMaintenanceService.insertWithCache(deviceMaintenance);
    }

    /**
     * 修改设备维保
     */
    @PreAuthorize("@ss.hasPermi('iot:maintenance:edit')")
    @PutMapping
    @ApiOperation("修改设备维保")
    public AjaxResult edit(@RequestBody DeviceMaintenance deviceMaintenance) {
        SecurityUtils.checkUserOperatePermission(deviceMaintenance.getTenantId(), deviceMaintenance.getCreateBy());
        return deviceMaintenanceService.updateWithCache(deviceMaintenance);
    }

    /**
     * 删除设备维保
     */
    @PreAuthorize("@ss.hasPermi('iot:maintenance:remove')")
    @DeleteMapping("/{ids}")
    @ApiOperation("删除设备维保")
    public AjaxResult remove(@PathVariable Long[] ids) {
        List<DeviceMaintenance> deviceMaintenanceList = deviceMaintenanceService.listByIds(Arrays.asList(ids));
        for (DeviceMaintenance deviceMaintenance : deviceMaintenanceList) {
            SecurityUtils.checkUserOperatePermission(deviceMaintenance.getTenantId(), deviceMaintenance.getCreateBy());
        }
        return toAjax(deviceMaintenanceService.deleteWithCacheByIds(ids, true));
    }

}
