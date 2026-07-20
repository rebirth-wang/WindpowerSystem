package com.fastbee.iot.model.varTemp;

import java.util.List;

import lombok.Data;

/**
 * @author gsb
 * @date 2022/12/14 15:38
 */
@Data
public class DeviceTemp {

    private Long productId;

    private String serialNumber;

    private List<DeviceSlavePoint> pointList;
}
