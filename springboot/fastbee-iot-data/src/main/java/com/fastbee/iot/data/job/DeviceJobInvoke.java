package com.fastbee.iot.data.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.fastbee.iot.data.job.strategy.JobInvokeStrategy;
import com.fastbee.iot.domain.DeviceJob;

/**
 * 任务执行工具
 *
 * @author kerwincui
 */
@Slf4j
@Component
public class DeviceJobInvoke {

    private final JobInvokeStrategyFactory strategyFactory;


    public DeviceJobInvoke(JobInvokeStrategyFactory factory){
        this.strategyFactory = factory;
    }

    public void invokeMethod(DeviceJob deviceJob) throws Exception {
        JobInvokeStrategy strategy = strategyFactory.getStrategy(deviceJob.getJobType());
        strategy.invoke(deviceJob);
    }


}
