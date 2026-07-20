package com.fastbee.iot.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 设备扩展参数值对象 iot_device_ext_param_value
 *
 * @author fastbee
 * @date 2026-03-18
 */

@ApiModel(value = "DeviceExtParamValueVO", description = "设备扩展参数值 iot_device_ext_param_value")
@Data
public class DeviceExtParamValueVO{
    /** 代码生成区域 可直接覆盖**/
    /** 主键ID */
    @Excel(name = "主键ID")
    @ApiModelProperty("主键ID")
    private Long id;

    /** 设备ID */
    @Excel(name = "设备ID")
    @ApiModelProperty("设备ID")
    private Long deviceId;

    /** 参数ID */
    @Excel(name = "参数ID")
    @ApiModelProperty("参数ID")
    private Long paramId;

    /** 参数值 */
    @Excel(name = "参数值")
    @ApiModelProperty("参数值")
    private String paramValue;

    /** 更新者 */
    @Excel(name = "更新者")
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    @Excel(name = "更新时间")
    private Date updateTime;


    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
