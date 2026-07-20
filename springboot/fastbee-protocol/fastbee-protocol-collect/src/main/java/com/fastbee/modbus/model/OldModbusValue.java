package com.fastbee.modbus.model;

import lombok.Data;

/**
 * @author gsb
 * @date 2025/2/28 11:49
 */
@Data
public class OldModbusValue {

    private Integer address;
    private Integer register;
    private int ioCount;
}
