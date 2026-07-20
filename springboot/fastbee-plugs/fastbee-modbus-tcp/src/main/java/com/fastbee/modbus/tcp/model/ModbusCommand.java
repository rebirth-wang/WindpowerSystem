package com.fastbee.modbus.tcp.model;

import lombok.Data;

@Data
public class ModbusCommand {
    private int transactionId;
    private int code;
    private int register;
    private int quantity;
    private byte[] data;
    private int address;

}
