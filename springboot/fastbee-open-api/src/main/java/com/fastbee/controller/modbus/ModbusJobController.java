package com.fastbee.controller.modbus;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.quartz.SchedulerException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.iot.domain.ModbusJob;
import com.fastbee.iot.model.vo.ModbusJobVO;
import com.fastbee.iot.service.IModbusJobService;

/**
 * 轮训任务列Controller
 *
 * @author kerwincui
 * @date 2024-07-05
 */
@RestController
@RequestMapping("/modbus/job")
@Api(tags = "modbus轮询任务")
public class ModbusJobController extends BaseController {

    @Resource
    private IModbusJobService modbusJobService;

    /**
     * 查询轮训任务列列表
     */
    @PreAuthorize("@ss.hasPermi('modbus:job:list')")
    @GetMapping("/list")
    @ApiOperation("查询轮训任务列列表")
    public TableDataInfo list(ModbusJobVO modbusJobVO) {
        Page<ModbusJobVO> voPage = modbusJobService.pageModbusJobVO(modbusJobVO);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出轮训任务列列表
     */
    @ApiOperation("导出轮训任务列列表")
    @PreAuthorize("@ss.hasPermi('modbus:job:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, ModbusJobVO modbusJobVO) {
        Page<ModbusJobVO> voPage = modbusJobService.pageModbusJobVO(modbusJobVO);
        ExcelUtil<ModbusJobVO> util = new ExcelUtil<ModbusJobVO>(ModbusJobVO.class);
        util.exportExcel(response, voPage.getRecords(), "轮训任务列数据");
    }

    /**
     * 获取轮训任务列详细信息
     */
    @PreAuthorize("@ss.hasPermi('modbus:job:query')")
    @GetMapping(value = "/{taskId}")
    @ApiOperation("获取轮训任务列详细信息")
    public AjaxResult getInfo(@PathVariable("taskId") Long taskId) {
        return success(modbusJobService.selectModbusJobByTaskId(taskId));
    }

    /**
     * 新增轮训任务列
     */
    @PreAuthorize("@ss.hasPermi('modbus:job:add')")
    @PostMapping
    @ApiOperation("新增轮训任务列")
    public AjaxResult add(@RequestBody ModbusJobVO modbusJobVO) {
        modbusJobVO.setCreateBy(getUsername());
        return toAjax(modbusJobService.insertModbusJob(modbusJobVO));
    }

    /**
     * 修改轮训任务列
     */
    @PreAuthorize("@ss.hasPermi('modbus:job:edit')")
    @PutMapping
    @ApiOperation("修改轮训任务列")
    public AjaxResult edit(@RequestBody ModbusJob modbusJob) {
        return toAjax(modbusJobService.updateModbusJob(modbusJob));
    }

    /**
     * 删除轮训任务列
     */
    @PreAuthorize("@ss.hasPermi('modbus:job:remove')")
    @PostMapping("/del")
    @ApiOperation("删除轮训任务列")
    public AjaxResult remove(@RequestBody ModbusJobVO modbusJobVO) throws SchedulerException {
        return toAjax(modbusJobService.deleteModbusJobByTaskId(modbusJobVO));
    }
}
