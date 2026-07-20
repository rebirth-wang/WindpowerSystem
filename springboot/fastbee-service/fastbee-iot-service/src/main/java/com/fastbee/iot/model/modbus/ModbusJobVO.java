package com.fastbee.iot.model.modbus;

import java.util.List;

import lombok.Data;

import com.fastbee.iot.domain.DeviceJob;

/**
 * @author gsb
 * @date 2024/6/21 9:32
 */
@Data
public class ModbusJobVO {

    private List<DeviceJob> list;

    private Long deviceId;
}
