package com.fastbee.quartz.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.quartz.SchedulerException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fastbee.common.annotation.Log;
import com.fastbee.common.constant.Constants;
import com.fastbee.common.core.domain.AjaxResult;
import com.fastbee.common.core.page.TableDataInfo;
import com.fastbee.common.enums.BusinessType;
import com.fastbee.common.exception.job.TaskException;
import com.fastbee.common.extend.core.controller.BaseController;
import com.fastbee.common.extend.utils.poi.ExcelUtil;
import com.fastbee.common.utils.MessageUtils;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.quartz.domain.SysJob;
import com.fastbee.quartz.service.ISysJobService;
import com.fastbee.quartz.util.CronUtils;
import com.fastbee.quartz.util.ScheduleUtils;
import com.fastbee.quartz.vo.SysJobVO;

/**
 * 调度任务信息操作处理
 *
 * @author ruoyi
 */
@Api(tags = "调度任务信息操作处理")
@RestController
@RequestMapping("/monitor/job")
public class SysJobController extends BaseController
{
    @Resource
    private ISysJobService jobService;

    /**
     * 查询定时任务列表
     */
    @ApiOperation("查询定时任务列表")
    @PreAuthorize("@ss.hasPermi('monitor:job:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysJob sysJob)
    {
        Page<SysJobVO> voPage = jobService.pageSysJobVO(sysJob);
        return getDataTable(voPage.getRecords(), voPage.getTotal());
    }

    /**
     * 导出定时任务列表
     */
    @ApiOperation("导出定时任务列表")
    @PreAuthorize("@ss.hasPermi('monitor:job:export')")
    @Log(title = "定时任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysJob sysJob)
    {
        Page<SysJobVO> voPage = jobService.pageSysJobVO(sysJob);
        ExcelUtil<SysJobVO> util = new ExcelUtil<SysJobVO>(SysJobVO.class);
        util.exportExcel(response, voPage.getRecords(), "定时任务调度数据");
    }

    /**
     * 获取定时任务详细信息
     */
    @ApiOperation("获取定时任务详细信息")
    @PreAuthorize("@ss.hasPermi('monitor:job:query')")
    @GetMapping(value = "/{jobId}")
    public AjaxResult getInfo(@PathVariable("jobId") Long jobId)
    {
        return success(jobService.selectJobById(jobId));
    }

    /**
     * 新增定时任务
     */
    @ApiOperation("新增定时任务")
    @PreAuthorize("@ss.hasPermi('monitor:job:add')")
    @Log(title = "定时任务", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysJob job) throws SchedulerException, TaskException
    {
        if (!CronUtils.isValid(job.getCronExpression()))
        {
            return error(StringUtils.format(MessageUtils.message("job.add.failed.cron.not.valid"), job.getJobName()));
        }
        else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI))
        {
            return error(StringUtils.format(MessageUtils.message("job.add.failed.rmi.not.valid"), job.getJobName()));
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.LOOKUP_LDAP, Constants.LOOKUP_LDAPS }))
        {
            return error(StringUtils.format(MessageUtils.message("job.add.failed.ldap.not.valid"), job.getJobName()));
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.HTTP, Constants.HTTPS }))
        {
            return error(StringUtils.format(MessageUtils.message("job.add.failed.http.not.valid"), job.getJobName()));
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), Constants.JOB_ERROR_STR))
        {
            return error(StringUtils.format(MessageUtils.message("job.add.failed.string.error"), job.getJobName()));
        }
        else if (!ScheduleUtils.whiteList(job.getInvokeTarget()))
        {
            return error(StringUtils.format(MessageUtils.message("job.add.failed.string.not.valid"), job.getJobName()));
        }
        job.setCreateBy(getUsername());
        return toAjax(jobService.insertJob(job));
    }

    /**
     * 修改定时任务
     */
    @ApiOperation("修改定时任务")
    @PreAuthorize("@ss.hasPermi('monitor:job:edit')")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysJob job) throws SchedulerException, TaskException
    {
        if (!CronUtils.isValid(job.getCronExpression()))
        {
            return error(StringUtils.format(MessageUtils.message("job.update.failed.cron.not.valid"), job.getJobName()));
        }
        else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI))
        {
            return error(StringUtils.format(MessageUtils.message("job.update.failed.rmi.not.valid"), job.getJobName()));
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.LOOKUP_LDAP, Constants.LOOKUP_LDAPS }))
        {
            return error(StringUtils.format(MessageUtils.message("job.update.failed.ldap.not.valid"), job.getJobName()));
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.HTTP, Constants.HTTPS }))
        {
            return error(StringUtils.format(MessageUtils.message("job.update.failed.http.not.valid"), job.getJobName()));
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), Constants.JOB_ERROR_STR))
        {
            return error(StringUtils.format(MessageUtils.message("job.update.failed.string.error"), job.getJobName()));
        }
        else if (!ScheduleUtils.whiteList(job.getInvokeTarget()))
        {
            return error(StringUtils.format(MessageUtils.message("job.update.failed.string.not.valid"), job.getJobName()));
        }
        job.setUpdateBy(getUsername());
        return toAjax(jobService.updateJob(job));
    }

    /**
     * 定时任务状态修改
     */
    @ApiOperation("定时任务状态修改")
    @PreAuthorize("@ss.hasPermi('monitor:job:changeStatus')")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysJob job) throws SchedulerException
    {
        SysJob newJob = jobService.selectJobById(job.getJobId());
        newJob.setStatus(job.getStatus());
        return toAjax(jobService.changeStatus(newJob));
    }

    /**
     * 定时任务立即执行一次
     */
    @ApiOperation("定时任务立即执行一次")
    @PreAuthorize("@ss.hasPermi('monitor:job:run')")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/run")
    public AjaxResult run(@RequestBody SysJob job) throws SchedulerException
    {
        boolean result = jobService.run(job);
        return result ? success() : error(MessageUtils.message("job.not.exists"));
    }

    /**
     * 删除定时任务
     */
    @ApiOperation("删除定时任务")
    @PreAuthorize("@ss.hasPermi('monitor:job:remove')")
    @Log(title = "定时任务", businessType = BusinessType.DELETE)
    @DeleteMapping("/{jobIds}")
    public AjaxResult remove(@PathVariable Long[] jobIds) throws SchedulerException, TaskException
    {
        jobService.deleteJobByIds(jobIds);
        return success();
    }
}
