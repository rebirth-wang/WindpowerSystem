package com.fastbee.iot.data.job.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.fastbee.common.extend.enums.DeviceJobTypeEnum;
import com.fastbee.iot.domain.DeviceJob;
import com.fastbee.iot.model.modbus.ModbusPollJob;
import com.fastbee.mq.producer.MessageProducer;

/**
 * Modbus-rtu 策略
 * @author gsb
 * @date 2025/3/18 15:38
 */
@Component
@Slf4j
public class ModbusJobStrategy implements JobInvokeStrategy{


    @Override
    public boolean supports(int jobType) {
        return DeviceJobTypeEnum.MODBUS_JOB.getType() == jobType;
    }

    @Override
    public void invoke(DeviceJob job) {
        log.info("----------------执行modbus轮训指令----------------------");
        ModbusPollJob pollJob = new ModbusPollJob();
        pollJob.setType(2);
        pollJob.setDeviceJob(job);
        MessageProducer.sendPropFetch(pollJob);
    }
}
