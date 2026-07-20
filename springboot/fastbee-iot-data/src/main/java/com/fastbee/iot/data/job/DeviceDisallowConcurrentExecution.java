package com.fastbee.iot.data.job;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;

import com.fastbee.common.utils.spring.SpringUtils;
import com.fastbee.iot.domain.DeviceJob;

/**
 * 定时任务处理（禁止并发执行）
 *
 * @author ruoyi
 *
 */
@DisallowConcurrentExecution
public class DeviceDisallowConcurrentExecution extends DeviceAbstractQuartzJob
{
    @Override
    protected void doExecute(JobExecutionContext context, DeviceJob deviceJob) throws Exception
    {
        DeviceJobInvoke jobInvoke = SpringUtils.getBean(DeviceJobInvoke.class);
        jobInvoke.invokeMethod(deviceJob);
    }
}
