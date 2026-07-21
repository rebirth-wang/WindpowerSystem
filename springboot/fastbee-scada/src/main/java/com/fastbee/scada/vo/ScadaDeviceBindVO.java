package com.fastbee.scada.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.PageEntity;

/**
 * 组态设备关联对象 scada_device_bind
 *
 * @author zhuangpeng.li
 * @date 2024-12-04
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ScadaDeviceBindVO", description = "组态设备关联 scada_device_bind")
@Data
public class ScadaDeviceBindVO extends PageEntity {

    /** id唯一标识 */
    @Excel(name = "id唯一标识")
    @ApiModelProperty("id唯一标识")
    private Long id;

    /** 设备编号 */
    @Excel(name = "设备编号")
    @ApiModelProperty("设备编号")
    private String serialNumber;

    /** 组态guid */
    @Excel(name = "组态guid")
    @ApiModelProperty("组态guid")
    private String scadaGuid;

    /**
     * 设备名称
     */
    @Excel(name = "设备名称")
    private String deviceName;

    /**
     * 设备状态
     */
    private Integer status;

    /**
     * 组态类型
     */
    private Integer scadaType;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 设备类型
     */
    private Integer deviceType;

}
