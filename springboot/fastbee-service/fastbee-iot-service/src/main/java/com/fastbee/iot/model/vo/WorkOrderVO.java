package com.fastbee.iot.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.common.extend.enums.WorkOrderStatusEnum;
import com.fastbee.common.extend.enums.WorkOrderTypeEnum;
import com.fastbee.common.extend.utils.ObjectChangeDetector;

/**
 * 工单管理对象 iot_work_order
 *
 * @author fastbee
 * @date 2025-08-18
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "WorkOrderVO", description = "工单管理 iot_work_order")
@Data
public class WorkOrderVO extends PageEntity {

    /** 主键id */
    @ObjectChangeDetector.IgnoreChange
    @Excel(name = "主键id")
    @ApiModelProperty("主键id")
    private Long id;

    /** 工单名称 */
    @ObjectChangeDetector.DisplayName("工单名称")
    @Excel(name = "工单名称")
    @ApiModelProperty("工单名称")
    private String name;

    /** 详细信息 */
    @ObjectChangeDetector.DisplayName("详细信息")
    @Excel(name = "详细信息")
    @ApiModelProperty("详细信息")
    private String details;

    /** 工单状态 */
    @ObjectChangeDetector.DisplayName("工单状态")
    @ObjectChangeDetector.EnumField(
            enumClass = WorkOrderStatusEnum.class,
            valueMethod = "getStatus"
    )
    @Excel(name = "工单状态")
    @ApiModelProperty("工单状态")
    private Integer status;

    /** 工单类型 */
    @ObjectChangeDetector.DisplayName("工单类型")
    @ObjectChangeDetector.EnumField(
            enumClass = WorkOrderTypeEnum.class,
            valueMethod = "getStatus"
    )
    @Excel(name = "工单类型")
    @ApiModelProperty("工单类型")
    private Integer type;

    /** 工单编号 */
    @ObjectChangeDetector.DisplayName("工单编号")
    @Excel(name = "工单编号")
    @ApiModelProperty("工单编号")
    private String number;

    /** 联系人 */
    @ObjectChangeDetector.IgnoreChange
    @Excel(name = "联系人")
    @ApiModelProperty("联系人")
    private Long userId;

    /** 联系人邮箱 */
    @ObjectChangeDetector.IgnoreChange
    @Excel(name = "联系人邮箱")
    @ApiModelProperty("联系人邮箱")
    private String userEmail;

    /** 设备id */
    @ObjectChangeDetector.IgnoreChange
    @Excel(name = "设备id")
    @ApiModelProperty("设备id")
    private Long deviceId;

    /** 设备编号 */
    @ObjectChangeDetector.IgnoreChange
    @Excel(name = "设备编号")
    @ApiModelProperty("设备编号")
    private String serialNumber;

    /** 截止时间 */
    @ObjectChangeDetector.DisplayName("截止时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("截止时间")
    private Date endTime;

    /** 处理结果 */
    @ObjectChangeDetector.DisplayName("处理结果")
    @Excel(name = "处理结果")
    @ApiModelProperty("处理结果")
    private String result;

    /** 所属租户id */
    @ObjectChangeDetector.IgnoreChange
    @Excel(name = "所属租户id")
    @ApiModelProperty("所属租户id")
    private Long tenantId;

    /** 租户名称 */
    @ObjectChangeDetector.IgnoreChange
    @Excel(name = "租户名称")
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 创建者 */
    @ObjectChangeDetector.IgnoreChange
    @Excel(name = "创建者")
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @ObjectChangeDetector.IgnoreChange
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    /** 更新者 */
    @ObjectChangeDetector.IgnoreChange
    @Excel(name = "更新者")
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 更新时间 */
    @ObjectChangeDetector.IgnoreChange
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    @Excel(name = "更新时间")
    private Date updateTime;

    /** 备注 */
    @ObjectChangeDetector.DisplayName("备注")
    @Excel(name = "备注")
    @ApiModelProperty("备注")
    private String remark;

    @ObjectChangeDetector.DisplayName("联系人")
    private String userName;

    @ObjectChangeDetector.IgnoreChange
    private String userPhone;

    @ObjectChangeDetector.IgnoreChange
    private String deviceName;

    @ObjectChangeDetector.IgnoreChange
    private String productName;

    @ObjectChangeDetector.IgnoreChange
    private Boolean canReceived;

    @ObjectChangeDetector.IgnoreChange
    private Boolean canEdit;

    @ObjectChangeDetector.IgnoreChange
    private Boolean canDelete;

    @ObjectChangeDetector.IgnoreChange
    private Boolean showOwner;

    @ObjectChangeDetector.IgnoreChange
    private Boolean notifyFlag;

    @ObjectChangeDetector.IgnoreChange
    private Long deviceMaintenanceId;

}
