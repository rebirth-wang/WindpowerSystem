package com.fastbee.iot.model;

import java.util.List;

import lombok.Data;

import com.fastbee.iot.domain.Device;
import com.fastbee.iot.domain.ModbusParams;
import com.fastbee.iot.domain.Product;
import com.fastbee.iot.domain.SubGateway;

/**
 * @author zzy
 * @description: TODO
 * @date 2025-10-24 10:00
 */
@Data
public class DeviceMetaData {

    private Device device;

    private Product product;

    private SubGateway subGateway;

    private List<SubGateway> subGatewayList;

    private ModbusParams modbusParams;
}
