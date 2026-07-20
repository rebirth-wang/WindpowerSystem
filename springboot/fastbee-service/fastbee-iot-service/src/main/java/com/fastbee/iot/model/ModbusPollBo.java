package com.fastbee.iot.model;

import java.util.List;

import lombok.Data;

import com.fastbee.common.extend.core.domin.model.device.DeviceAndProtocol;

/**
 * @author gsb
 * @date 2024/7/3 16:06
 */
@Data
public class ModbusPollBo {

    private DeviceAndProtocol gateway;
    private List<ModbusJobBo> commandList;
}
