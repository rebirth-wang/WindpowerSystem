package com.fastbee.iot.model.dashBoard;

import jakarta.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 大屏数据匹配bo
 * @author bill
 */
@Data
public class DeviceMatchBo {

    @NotNull(message = "设备id不能为空")
    @ApiModelProperty(value = "设备id")
    private Long deviceId;

    @ApiModelProperty(value = "数据结构")
    private Integer type;

    @NotNull(message = "属性不能为空")
    @ApiModelProperty(value = "属性")
    private String[] identifier;

    @ApiModelProperty(value = "从机id")
    private String slaveId;

}
