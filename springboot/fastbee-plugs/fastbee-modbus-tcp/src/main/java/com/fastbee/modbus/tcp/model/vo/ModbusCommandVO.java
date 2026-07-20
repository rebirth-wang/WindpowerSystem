package com.fastbee.modbus.tcp.model.vo;

import java.util.List;

import lombok.Data;

@Data
public class ModbusCommandVO {
    private String clientId;
    private int transactionId;
    private int code;
    private int register;
    private int quantity;
    private int address;
    private List<Integer> values;

}
