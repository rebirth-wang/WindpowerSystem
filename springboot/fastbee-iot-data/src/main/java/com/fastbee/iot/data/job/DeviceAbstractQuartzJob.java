package com.fastbee.iot.data.job;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fastbee.common.constant.Constants;
import com.fastbee.common.constant.ScheduleConstants;
import com.fastbee.common.utils.ExceptionUtil;
import com.fastbee.common.utils.StringUtils;
import com.fastbee.common.utils.bean.BeanUtils;
import com.fastbee.common.utils.spring.SpringUtils;
import com.fastbee.iot.domain.DeviceJob;
import com.fastbee.quartz.service.ISysJobLogService;
import com.fastbee.quartz.vo.SysJobLogVO;

/**
 * 抽象quartz调用
 *
 * @author ruoyi
 */
public abstract class DeviceAbstractQuartzJob implements Job
{
    private static final Logger log = LoggerFactory.getLogger(DeviceAbstractQuartzJob.class);

    /**
     * 线程本地变量
     */
    private static final ThreadLocal<Date> JOB_START_TIME = new ThreadLocal<>();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        DeviceJob deviceJob = copyJobData(context);
        try
        {
            before(context, deviceJob);
            doExecute(context, deviceJob);
            after(context, deviceJob, null);
        }
        catch (Exception e)
        {
            log.error("任务执行异常  - ：", e);
            after(context, deviceJob, e);
        }finally {
            JOB_START_TIME.remove();
        }
    }

    private DeviceJob copyJobData (JobExecutionContext context){
        DeviceJob deviceJob = new DeviceJob();
        Object data = context.getMergedJobDataMap().get(ScheduleConstants.TASK_PROPERTIES);
        if (data instanceof DeviceJob){
            BeanUtils.copyProperties(data, deviceJob);
        }
        return deviceJob;
    }

    /**
     * 执行前
     *
     * @param context 工作执行上下文对象
     * @param deviceJob 系统计划任务
     */
    protected void before(JobExecutionContext context, DeviceJob deviceJob)
    {
        JOB_START_TIME.set(new Date());
    }

    /**
     * 执行后
     *
     * @param context 工作执行上下文对象
     * @param deviceJob 系统计划任务
     */
    protected void after(JobExecutionContext context, DeviceJob deviceJob, Exception e)
    {
        Date startTime = JOB_START_TIME.get();
        JOB_START_TIME.remove();

        final SysJobLogVO sysJobLog = new SysJobLogVO();
        sysJobLog.setJobName(deviceJob.getJobName());
        sysJobLog.setJobGroup(deviceJob.getJobGroup());
        sysJobLog.setInvokeTarget(deviceJob.getDeviceName());
        sysJobLog.setStartTime(startTime);
        sysJobLog.setStopTime(new Date());
        long runMs = sysJobLog.getStopTime().getTime() - sysJobLog.getStartTime().getTime();
        sysJobLog.setJobMessage(sysJobLog.getJobName() + " 总共耗时：" + runMs + "毫秒");
        if (e != null)
        {
            sysJobLog.setStatus(Constants.FAIL);
            String errorMsg = StringUtils.substring(ExceptionUtil.getExceptionMessage(e), 0, 2000);
            sysJobLog.setExceptionInfo(errorMsg);
        }
        else
        {
            sysJobLog.setStatus(Constants.SUCCESS);
        }

        // 写入数据库当中
        SpringUtils.getBean(ISysJobLogService.class).addJobLog(sysJobLog);
    }

    /**
     * 执行方法，由子类重载
     *
     * @param context 工作执行上下文对象
     * @param deviceJob 系统计划任务
     * @throws Exception 执行过程中的异常
     */
    protected abstract void doExecute(JobExecutionContext context, DeviceJob deviceJob) throws Exception;
}
