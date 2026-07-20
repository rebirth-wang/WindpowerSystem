package com.fastbee.common.extend.core.domin.mq;

import lombok.AllArgsConstructor;
import lombok.Data;

// 响应DTO
@Data
@AllArgsConstructor
public class ModbusResponse {
    private int transactionId;
    private int protocolId;
    private int address; //从机地址
    private byte code;
    private String rawHex; //原始报文
    private String clientId;
    private int register; //寄存器
    private int quantity;

    public ModbusResponse(int transactionId,
                          int protocolId,
                          int address,
                          byte code,
                          String rawHex,
                          int register,
                          String clientId) {
        this.transactionId = transactionId;
        this.protocolId = protocolId;
        this.address = address;
        this.code = code;
        this.rawHex = rawHex;
        this.register = register;
        this.clientId = clientId;
    }


}
