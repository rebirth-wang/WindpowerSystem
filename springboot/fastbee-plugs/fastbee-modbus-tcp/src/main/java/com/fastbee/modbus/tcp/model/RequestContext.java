package com.fastbee.modbus.tcp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

// 新增请求上下文类
@Getter
@AllArgsConstructor
public class RequestContext {
    private final Long deviceId;
    private final int registerId;
    private final int quantity;
    @Setter
    private long timestamp;


    public RequestContext(Long deviceId, int quantity, int registerId) {
        this.deviceId = deviceId;
        this.quantity = quantity;
        this.registerId = registerId;
    }

}

