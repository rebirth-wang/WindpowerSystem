package com.fastbee.controller.device;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.quartz.SchedulerException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.annotation.Log;
import com.fastbee.common.constant.FastBeeConstant;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.enums.BusinessType;
import com.fastbee.common.exception.job.TaskException;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.iot.convert.DeviceJobConvert;
import com.fastbee.iot.domain.DeviceJob;
import com.fastbee.iot.domain.ModbusConfig;
import com.fastbee.iot.domain.ModbusJob;
import com.fastbee.iot.mapper.ModbusJobMapper;
import com.fastbee.iot.model.Action;
import com.fastbee.iot.model.vo.DeviceJobVO;
import com.fastbee.iot.service.IDeviceJobService;
import com.fastbee.iot.service.IModbusConfigService;
import com.fastbee.quartz.util.CronUtils;

/**
 * 调度任务信息操作处理
 *
 * @author kerwincui
 */
@Api(tags = "调度任务信息操作处理模块")
@RestController
@RequestMapping("/iot/job")
public class DeviceJobController extends BaseController
{
    @Resource
    private IDeviceJobService jobService;

    @Resource
    private IModbusConfigService modbusConfigService;

    @Resource
    private ModbusJobMapper modbusJobMapper;


    /**
     * 查询定时任务列表
     */
    @ApiOperation("查询定时任务列表")
    @PreAuthorize("@ss.hasPermi('iot:device:timer:list')")
    @GetMapping("/list")
    public TableDataInfo list(DeviceJob deviceJob)
    {
        Page<DeviceJobVO> voPage = jobService.pageDeviceJobVO(deviceJob);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出定时任务列表
     */
    @ApiOperation("导出定时任务列表")
    @PreAuthorize("@ss.hasPermi('iot:device:timer:export')")
    @Log(title = "定时任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DeviceJob deviceJob)
    {
        Page<DeviceJobVO> voPage = jobService.pageDeviceJobVO(deviceJob);
        ExcelUtil<DeviceJobVO> util = new ExcelUtil<DeviceJobVO>(DeviceJobVO.class);
        util.exportExcel(response, voPage.getRecords(), "设备定时数据");
    }

    /**
     * 获取定时任务详细信息
     */
    @ApiOperation("获取定时任务详细信息")
    @PreAuthorize("@ss.hasPermi('iot:device:timer:query')")
    @GetMapping(value = "/{jobId}")
    public AjaxResult getInfo(@PathVariable("jobId") Long jobId)
    {
        return AjaxResult.success(jobService.selectJobById(jobId));
    }

    /**
     * 新增定时任务
     */
    @ApiOperation("新增定时任务")
    @PreAuthorize("@ss.hasPermi('iot:device:timer:add')")
    @Log(title = "定时任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DeviceJobVO jobVO) throws SchedulerException, TaskException
    {
        if (!CronUtils.isValid(jobVO.getCronExpression()))
        {
            return error(StringUtils.format(MessageUtils.message("job.add.failed.cron.not.valid"), jobVO.getJobName()));
        }
        jobVO.setCreateBy(getUsername());
        List<Action> actions = JSON.parseArray(jobVO.getActions(), Action.class);
        List<String> identifiers = actions.stream().map(Action::getId).collect(Collectors.toList());
        if (FastBeeConstant.PROTOCOL.ModbusRtu.equals(jobVO.getProtocolCode())
                || FastBeeConstant.PROTOCOL.ModbusTcpOverRtu.equals(jobVO.getProtocolCode())) {
            // modbus配置
            List<ModbusConfig> modbusConfigList = modbusConfigService.listByProductIdAndIdentifiers(jobVO.getProductId(), identifiers);
            if (CollectionUtils.isEmpty(modbusConfigList)) {
                return error(StringUtils.format(MessageUtils.message("job.add.failed.product.not.modbus.config"), jobVO.getJobName()));
            }
            jobVO.setJobGroup("MODBUS");
        }
        DeviceJob job = DeviceJobConvert.INSTANCE.convertDeviceJob(jobVO);
        return toAjax(jobService.insertJob(job));
    }

    /**
     * 修改定时任务
     */
    @ApiOperation("修改定时任务")
    @PreAuthorize("@ss.hasPermi('iot:device:timer:edit')")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DeviceJobVO jobVO) throws SchedulerException, TaskException
    {
        if (!CronUtils.isValid(jobVO.getCronExpression()))
        {
            return error(StringUtils.format(MessageUtils.message("job.update.failed.cron.not.valid"), jobVO.getJobName()));
        }
        jobVO.setUpdateBy(getUsername());
        List<Action> actions = JSON.parseArray(jobVO.getActions(), Action.class);
        List<String> identifiers = actions.stream().map(Action::getId).collect(Collectors.toList());
        if (FastBeeConstant.PROTOCOL.ModbusRtu.equals(jobVO.getProtocolCode())
                || FastBeeConstant.PROTOCOL.ModbusTcpOverRtu.equals(jobVO.getProtocolCode())) {
            // modbus配置
            List<ModbusConfig> modbusConfigList = modbusConfigService.listByProductIdAndIdentifiers(jobVO.getProductId(), identifiers);
            if (CollectionUtils.isEmpty(modbusConfigList)) {
                return error(StringUtils.format(MessageUtils.message("job.update.failed.product.not.modbus.config"), jobVO.getJobName()));
            }
            jobVO.setJobGroup("MODBUS");
        }
        jobVO.setUpdateBy(getUsername());
        DeviceJob job = DeviceJobConvert.INSTANCE.convertDeviceJob(jobVO);
        return toAjax(jobService.updateJob(job));
    }

    /**
     * 定时任务状态修改
     */
    @ApiOperation("定时任务状态修改")
    @PreAuthorize("@ss.hasPermi('iot:device:timer:edit')")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody DeviceJob job) throws SchedulerException
    {
        DeviceJob newJob = jobService.selectJobById(job.getJobId());
        newJob.setStatus(job.getStatus());
        newJob.setUpdateBy(getUsername());
        return toAjax(jobService.changeStatus(newJob));
    }

    /**
     * 定时任务立即执行一次
     */
    @ApiOperation("定时任务立即执行一次")
    @PreAuthorize("@ss.hasPermi('iot:device:timer:execute')")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/run")
    public AjaxResult run(@RequestBody DeviceJob job) throws SchedulerException
    {
        jobService.run(job);
        return AjaxResult.success();
    }

    /**
     * 删除定时任务
     */
    @ApiOperation("删除定时任务")
    @PreAuthorize("@ss.hasPermi('iot:device:timer:remove')")
    @Log(title = "定时任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{jobIds}")
    public AjaxResult remove(@PathVariable Long[] jobIds) throws SchedulerException, TaskException
    {
        jobService.deleteJobByIds(jobIds);
        LambdaQueryWrapper<ModbusJob> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ModbusJob::getJobId, Arrays.asList(jobIds));
        modbusJobMapper.delete(queryWrapper);
        return AjaxResult.success();
    }
}
