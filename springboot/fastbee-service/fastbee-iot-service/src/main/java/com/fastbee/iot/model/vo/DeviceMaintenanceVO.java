package com.fastbee.iot.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 设备维保对象 iot_device_maintenance
 *
 * @author fastbee
 * @date 2025-12-25
 */

@ApiModel(value = "DeviceMaintenanceVO", description = "设备维保 iot_device_maintenance")
@Data
public class DeviceMaintenanceVO{

    /** 主键id */
    @Excel(name = "主键id")
    @ApiModelProperty("主键id")
    private Long id;

    /** 维保名称 */
    @Excel(name = "维保名称")
    @ApiModelProperty("维保名称")
    private String name;

    /** 设备id */
    @Excel(name = "设备id")
    @ApiModelProperty("设备id")
    private Long deviceId;

    /** 设备名称 */
    @Excel(name = "设备名称")
    @ApiModelProperty("设备名称")
    private String deviceName;

    /** 保内维保时间 */
    @Excel(name = "保内维保时间")
    @ApiModelProperty("保内维保时间")
    private Integer maintenanceTime;

    /** 保内维保时间单位 */
    @Excel(name = "保内维保时间单位")
    @ApiModelProperty("保内维保时间单位")
    private Integer maintenanceTimeUnit;

    /** 维保周期 */
    @Excel(name = "维保周期")
    @ApiModelProperty("维保周期")
    private Integer maintenancePeriod;

    /** 维保周期单位 */
    @Excel(name = "维保周期单位")
    @ApiModelProperty("维保周期单位")
    private Integer maintenancePeriodUnit;

    /** 开始维保时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("开始维保时间")
    @Excel(name = "开始维保时间")
    private Date startMaintenanceTime;

    /** 下次维保时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("下次维保时间")
    @Excel(name = "下次维保时间")
    private Date nextMaintenanceTime;

    /** 保内维保到期停止执行 */
    @Excel(name = "保内维保到期停止执行")
    @ApiModelProperty("保内维保到期停止执行")
    private Integer expireStopFlag;

    /** 提前派工单时间 */
    @Excel(name = "提前派工单时间")
    @ApiModelProperty("提前派工单时间")
    private Integer preWorkTimeType;

    /** 状态 */
    @Excel(name = "状态")
    @ApiModelProperty("状态")
    private Integer status;

    /** 维保类型 */
    @Excel(name = "维保类型")
    @ApiModelProperty("维保类型")
    private Integer maintenanceType;

    /** 租户id */
    @ApiModelProperty("租户id")
    private Long tenantId;

    /** 租户名称 */
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 创建人 */
    @Excel(name = "创建人")
    @ApiModelProperty("创建人")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    /** 更新人 */
    @Excel(name = "更新人")
    @ApiModelProperty("更新人")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    @Excel(name = "更新时间")
    private Date updateTime;

    /** 逻辑删除标识 */
    @Excel(name = "逻辑删除标识")
    @ApiModelProperty("逻辑删除标识")
    private Integer delFlag;

}
