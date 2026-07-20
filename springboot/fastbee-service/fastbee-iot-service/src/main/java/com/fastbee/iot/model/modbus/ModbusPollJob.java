package com.fastbee.iot.model.modbus;

import lombok.Data;

import com.fastbee.common.extend.core.domin.mq.message.ModbusPollMsg;
import com.fastbee.iot.domain.DeviceJob;

/**
 * @author gsb
 * @date 2024/8/28 11:31
 */
@Data
public class ModbusPollJob {

    private ModbusPollMsg pollMsg;

    private DeviceJob deviceJob;
    /**
     * 1- 主动读取  2- 轮训下发
     */
    private Integer type;
}
