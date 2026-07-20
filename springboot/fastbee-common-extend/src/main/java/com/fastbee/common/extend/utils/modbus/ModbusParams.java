package com.fastbee.common.extend.utils.modbus;

import lombok.Data;

/**
 * @author gsb
 * @date 2026/2/3 下午1:43
 */
@Data
public class ModbusParams {

    private int slaveId;
    private int code;
    private int address;
    private int length;
}
