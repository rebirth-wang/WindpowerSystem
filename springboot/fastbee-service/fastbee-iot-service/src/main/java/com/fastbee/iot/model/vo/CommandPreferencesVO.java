package com.fastbee.iot.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 指令偏好设置对象 command_preferences
 *
 * @author zhuangpeng.li
 * @date 2024-11-12
 */

@ApiModel(value = "CommandPreferencesVO", description = "指令偏好设置 command_preferences")
@Data
public class CommandPreferencesVO{

    /** 指令id */
    @Excel(name = "指令id")
    @ApiModelProperty("指令id")
    private Long id;

    /** 指令名称 */
    @Excel(name = "指令名称")
    @ApiModelProperty("指令名称")
    private String name;

    /** 指令 */
    @Excel(name = "指令")
    @ApiModelProperty("指令")
    private String command;

    /** 设备编号 */
    @Excel(name = "设备编号")
    @ApiModelProperty("设备编号")
    private String serialNumber;


}
