package com.fastbee.iot.model.gateWay;

import java.util.List;

import lombok.Data;

/**
 * @author zzy
 * @description: 通过子产品批量新增设备VO类
 * @date 2025-07-24 10:23
 */
@Data
public class ProductSubDeviceAddVO {

    private Long parentProductId;

    private String parentClientId;

    private List<SubProduct> subProductList;

    @Data
    public static class SubDevice {

        private Long subProductId;

        private String deviceName;

        private String address;
    }

    @Data
    public static class SubProduct {
        private Long subProductId;
        private List<SubDevice> subDeviceList;
    }
}
