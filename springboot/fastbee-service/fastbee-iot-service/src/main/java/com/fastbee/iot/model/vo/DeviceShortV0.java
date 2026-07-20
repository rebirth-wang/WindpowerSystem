package com.fastbee.iot.model.vo;

import lombok.Data;

/**
 * @author admin
 * @version 1.0
 * @description: 设备简短VO类
 * @date 2025-02-08 11:40
 */
@Data
public class DeviceShortV0 {


    /** 设备ID */
    private Long deviceId;

    /**
     * 分组id
     */
    private Long groupId;

    /** 设备名称 */
    private String deviceName;

}
