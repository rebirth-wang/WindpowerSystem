package com.fastbee.modbus.tcp.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModbusRequest {
    private final int transactionId;
    private final byte unitId;
    private final byte functionCode;
    private final int startAddress;
    private final int quantity;


}
