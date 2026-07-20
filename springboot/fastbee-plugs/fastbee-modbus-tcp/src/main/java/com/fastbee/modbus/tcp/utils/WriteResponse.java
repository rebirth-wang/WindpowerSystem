package com.fastbee.modbus.tcp.utils;

import lombok.Data;

/**
 * @author gsb
 * @date 2025/3/27 11:11
 */
@Data
public class WriteResponse {

    private String messageId;

    private boolean success;
}
