package com.fastbee.iot.model.vo;

import java.util.Date;

import lombok.Data;

/**
 * 设备状态
 * @author gsb
 * @date 2024/4/11 10:39
 */
@Data
public class DeviceStatusVO {

    private String serialNumber;

    private Integer status;

    private String transport;

    private String protocolCode;

    private Long productId;

    private Integer deviceType;

    private Integer rssi;

    private Integer isShadow;

    private Long tenantId;

    private Date activeTime;
}
