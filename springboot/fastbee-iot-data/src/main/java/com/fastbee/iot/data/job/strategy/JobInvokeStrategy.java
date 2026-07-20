package com.fastbee.iot.data.job.strategy;

import com.fastbee.iot.domain.DeviceJob;

public interface JobInvokeStrategy {
    boolean supports(int jobType);
    void invoke(DeviceJob job);
}
