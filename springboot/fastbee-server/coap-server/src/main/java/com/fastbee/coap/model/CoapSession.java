package com.fastbee.coap.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.fastbee.iot.domain.Device;

@Data
@Slf4j
public class CoapSession {
    private String clientId;
    private String username;
    private String productId;
    private String serialNumber;
    private String token;
    private Device device;

    public CoapSession(String clientId,
                       String username,
                       String token) {
        this.clientId = clientId;
        this.username = username;
        this.token = token;
    }
}
