package com.fastbee.iot.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 设备维保对象 iot_device_maintenance
 *
 * @author fastbee
 * @date 2025-12-25
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "DeviceMaintenance", description = "设备维保 iot_device_maintenance")
@Data
@TableName("iot_device_maintenance" )
public class DeviceMaintenance extends PageEntity {
    /** 主键id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键id")
    private Long id;

    /** 维保名称 */
    @ApiModelProperty("维保名称")
    private String name;

    /** 设备id */
    @ApiModelProperty("设备id")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_device_maintenance.device_id=iot_device.device_id"
    )
    private Long deviceId;

    /** 设备名称 */
    @ApiModelProperty("设备名称")
    private String deviceName;

    /** 保内维保时间 */
    @ApiModelProperty("保内维保时间")
    private Integer maintenanceTime;

    /** 保内维保时间单位 */
    @ApiModelProperty("保内维保时间单位")
    @AiSemanticField(
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "maintenance_unit"
    )
    private Integer maintenanceTimeUnit;

    /** 维保周期 */
    @ApiModelProperty("维保周期")
    private Integer maintenancePeriod;

    /** 维保周期单位 */
    @ApiModelProperty("维保周期单位")
    private Integer maintenancePeriodUnit;

    /** 开始维保时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("开始维保时间")
    private Date startMaintenanceTime;

    /** 下次维保时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty("下次维保时间")
    private Date nextMaintenanceTime;

    /** 保内维保到期停止执行 */
    @ApiModelProperty("保内维保到期停止执行")
    @AiSemanticField(semanticType = AiSemanticType.ENUM)
    private Integer expireStopFlag;

    /** 提前派工单时间 */
    @ApiModelProperty("提前派工单时间")
    @AiSemanticField(
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "maintenance_pretime_type"
    )
    private Integer preWorkTimeType;

    /** 状态 */
    @ApiModelProperty("状态")
    @AiSemanticField(
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_device_maintenance_status",
            queryHint = "check-value-mapping"
    )
    private Integer status;

    /** 维保类型 */
    @ApiModelProperty("维保类型")
    @AiSemanticField(
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_device_maintenance_type"
    )
    private Integer maintenanceType;

    /** 租户id */
    @ApiModelProperty("租户id")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "系统租户隔离字段，查询时自动追加租户过滤",
            queryHint = "auto-data-scope"
    )
    private Long tenantId;

    /** 租户名称 */
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 创建人 */
    @ApiModelProperty("创建人")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 更新人 */
    @ApiModelProperty("更新人")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /** 逻辑删除标识 */
    @ApiModelProperty("逻辑删除标识")
    @TableLogic
    @AiSemanticField(semanticType = AiSemanticType.ENUM)
    private Integer delFlag;


}
