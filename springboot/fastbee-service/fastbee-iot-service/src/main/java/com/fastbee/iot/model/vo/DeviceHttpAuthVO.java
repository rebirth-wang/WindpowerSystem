package com.fastbee.iot.model.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class DeviceHttpAuthVO {
    private String type;
    private String username;
    private String password;
    private String clientId;
    private Long port;
}
