package com.fastbee.common.extend.core.domin.mq;

import java.util.List;

import lombok.Data;

import com.fastbee.common.extend.core.domin.thingsModel.ThingsModelSimpleItem;

/**
 * @author bill
 */
@Data
public class SubDeviceBo {
    /**
     * 网关设备id
     */

    private String parentClientId;

    /**
     * 子设备id
     */
    private String subClientId;

    /**
     * 从机地址
     */
    private String address;
    /**
     * 子设备名称
     */
    private String subDeviceName;

    private Long subProductId;

    /** 设备物模型值的集合 **/
    private List<ThingsModelSimpleItem> thingsModelSimpleItem;
}
