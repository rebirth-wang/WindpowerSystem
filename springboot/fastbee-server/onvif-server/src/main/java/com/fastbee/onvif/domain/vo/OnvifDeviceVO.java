package com.fastbee.onvif.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * ONVIF 节点扩展对象。
 * 数据来源于 iot_device，status/ip/lastHeartbeat 使用 iot_device 原字段，
 * 仅 ONVIF 专属扩展字段位于 iot_device.summary.onvif。
 *
 * @author fastbee
 * @date 2026-01-06
 */

@ApiModel(value = "OnvifDeviceVO", description = "ONVIF 节点扩展对象")
@Data
public class OnvifDeviceVO{
    /** 代码生成区域 可直接覆盖**/
    /** 设备ID，对应 iot_device.device_id */
    @Excel(name = "设备ID")
    @ApiModelProperty("设备ID，对应 iot_device.device_id")
    private Integer id;

    /** 设备名称 */
    @Excel(name = "设备名称")
    @ApiModelProperty("设备名称")
    private String name;

    /** 设备状态 */
    @Excel(name = "设备状态")
    @ApiModelProperty("设备状态")
    private Long status;

    /** 连接方式 */
    @Excel(name = "连接方式")
    @ApiModelProperty("连接方式")
    private Long directConnection;

    /** 创建者 */
    @Excel(name = "创建者")
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    /** 更新者 */
    @Excel(name = "更新者")
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    @Excel(name = "更新时间")
    private Date updateTime;

    /** 备注 */
    @Excel(name = "备注")
    @ApiModelProperty("备注")
    private String remark;


    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
