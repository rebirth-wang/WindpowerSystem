package com.fastbee.iot.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 工单管理对象 iot_work_order
 *
 * @author fastbee
 * @date 2025-08-18
 */

@ApiModel(value = "WorkOrderVO", description = "工单管理 iot_work_order")
@Data
public class WorkOrderChangeVO {

    /** 主键id */
    @Excel(name = "主键id")
    @ApiModelProperty("主键id")
    private Long id;

    /** 工单名称 */
    @Excel(name = "工单名称")
    @ApiModelProperty("工单名称")
    private String name;

    /** 工单描述 */
    @Excel(name = "工单描述")
    @ApiModelProperty("工单描述")
    private String description;

    /** 工单状态 */
    @Excel(name = "工单状态")
    @ApiModelProperty("工单状态")
    private Integer status;

    /** 工单类型 */
    @Excel(name = "工单类型")
    @ApiModelProperty("工单类型")
    private Integer type;

    /** 工单编号 */
    @Excel(name = "工单编号")
    @ApiModelProperty("工单编号")
    private String number;

    /** 联系人 */
    @Excel(name = "联系人")
    @ApiModelProperty("联系人")
    private Long userId;

    /** 设备id */
    @Excel(name = "设备id")
    @ApiModelProperty("设备id")
    private Long deviceId;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("截止时间")
    private Date endTime;

    /** 备注 */
    @Excel(name = "备注")
    @ApiModelProperty("备注")
    private String remark;

    private String userName;

    private String deviceName;

}
