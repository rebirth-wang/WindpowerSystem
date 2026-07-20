package com.fastbee.common.extend.core.domin.model.device;

import lombok.Data;

/**
 * @author gsb
 * @date 2024/6/14 9:25
 */
@Data
public class DeviceAndProtocol {

    /**
     * 设备id
     */
    private Long deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备编号
     */
    private String serialNumber;

    /**
     * 协议编号
     */
    private String protocolCode;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 传输协议
     */
    private String transport;

    /**
     * 设备类型
     */
    private Integer deviceType;

    /**
     * 子设备地址
     */
    private String address;

    /**
     * 网关设备产品id
     */
    private Long gwProductId;

    /**
     * 网关设备编号
     */
    private String gwSerialNumber;

    private Long tenantId;

    private String tenantName;

    private String createBy;

    private String modbusAddress;

    private Integer devicePort;

    private String deviceIp;

    private Integer status;

}
