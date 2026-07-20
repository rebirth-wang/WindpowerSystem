package com.fastbee.iot.model.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.BaseEntity;

/**
 * 网关与子设备关联对象 iot_sub_gateway
 *
 * @author zhuangpeng.li
 * @date 2024-11-14
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SubGatewayVO", description = "网关与子设备关联 iot_sub_gateway")
@Data
public class SubGatewayVO extends BaseEntity implements Serializable {

    /** 业务id */
    @Excel(name = "业务id")
    @ApiModelProperty("业务id")
    private Long id;

    /** 网关设备编号 */
    @Excel(name = "网关设备编号")
    @ApiModelProperty("网关设备编号")
    private String parentClientId;

    /** 子设备编号 */
    @Excel(name = "子设备编号")
    @ApiModelProperty("子设备编号")
    private String subClientId;

    /** 从机地址 */
    @Excel(name = "从机地址")
    @ApiModelProperty("从机地址")
    private String address;

    /**
     * 子设备名称
     */
    private String subDeviceName;
    /**
     * 子设备编号
     */
    private String subDeviceNo;

    /** 网关产品id */
    @ApiModelProperty("网关产品id")
    private Long parentProductId;

    /** 子设备产品id */
    @ApiModelProperty("子设备产品id")
    private Long subProductId;

    private Integer status;

}
