package com.fastbee.iot.model.gateWay;

import lombok.Data;

import com.fastbee.common.core.domain.PageEntity;

/**
 * 网关子设备
 * @author bill
 */
@Data
public class GateSubDeviceVO extends PageEntity {

    /**
     * 设备id
     */
    private Long deviceId;
    /**
     * 设备编号
     */
    private String serialNumber;
    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 产品id
     */
    private Long productId;
}
