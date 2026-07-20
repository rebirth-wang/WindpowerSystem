package com.fastbee.iot.data.job.strategy;

import org.springframework.stereotype.Component;

import com.fastbee.common.extend.enums.DeviceJobTypeEnum;
import com.fastbee.iot.domain.DeviceJob;

/**
 * modbus tcp 策略
 *
 * @author gsb
 * @date 2025/3/18 16:51
 */
@Component
public class ModbusTcpJobStrategy implements JobInvokeStrategy {

    @Override
    public boolean supports(int jobType) {
        return DeviceJobTypeEnum.MODBUS_TCP.getType() == jobType;
    }

    @Override
    public void invoke(DeviceJob job) {
        String modbusTcpId = job.getSerialNumber();
    }
}
