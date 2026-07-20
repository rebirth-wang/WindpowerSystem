package com.fastbee.iot.model.gateWay;

import java.util.List;

import lombok.Data;

/**
 * @author gsb
 * @date 2024/5/29 15:11
 */
@Data
public class SubDeviceAddVO {
    /**
     * 网关设备id
     */
    private String parentClientId;
    /**
     * 子设备id集合
     */
    private List<SubDeviceAddInfoVO> subDeviceAddInfoVOList;

    /**
     * 网关产品id
     */
    private Long parentProductId;

    private Long parentDeviceId;

    @Data
    public static class SubDeviceAddInfoVO {

        private Long subProductId;

        private String subClientId;

        private String address;
    }
}
