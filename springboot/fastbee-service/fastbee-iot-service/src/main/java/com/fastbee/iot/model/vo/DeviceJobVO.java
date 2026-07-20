package com.fastbee.iot.model.vo;

import java.util.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 设备定时对象 iot_device_job
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */

@ApiModel(value = "DeviceJobVO", description = "设备定时 iot_device_job")
@Data
public class DeviceJobVO{

    /** 任务ID */
    @Excel(name = "任务ID")
    @ApiModelProperty("任务ID")
    private Long jobId;

    /** 任务名称 */
    @NotBlank(message = "任务名称不能为空")
    @Size(min = 0, max = 64, message = "任务名称不能超过64个字符")
    @Excel(name = "任务名称")
    @ApiModelProperty("任务名称")
    private String jobName;

    /** 任务组名 */
    @Excel(name = "任务组名")
    @ApiModelProperty("任务组名")
    private String jobGroup;

    /** cron执行表达式 */
    @NotBlank(message = "Cron执行表达式不能为空")
    @Size(min = 0, max = 255, message = "Cron执行表达式不能超过255个字符")
    @Excel(name = "cron执行表达式")
    @ApiModelProperty("cron执行表达式")
    private String cronExpression;

    /** 计划执行错误策略（1立即执行 2执行一次 3放弃执行） */
    @ApiModelProperty("计划执行错误策略")
    @Excel(name = "计划执行错误策略")
    private String misfirePolicy;

    /** 是否并发执行（0允许 1禁止） */
    @ApiModelProperty("是否并发执行")
    @Excel(name = "是否并发执行")
    private String concurrent;

    /** 状态（0正常 1暂停） */
    @ApiModelProperty("状态")
    @Excel(name = "状态")
    private Integer status;

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

    /** 备注信息 */
    @Excel(name = "备注信息")
    @ApiModelProperty("备注信息")
    private String remark;

    /** 设备ID */
    @Excel(name = "设备ID")
    @ApiModelProperty("设备ID")
    private Long deviceId;

    /** 设备编号 */
    @Excel(name = "设备编号")
    @ApiModelProperty("设备编号")
    private String serialNumber;

    /** 设备名称 */
    @Excel(name = "设备名称")
    @ApiModelProperty("设备名称")
    private String deviceName;

    /** 是否详细corn表达式（1=是，0=否） */
    @ApiModelProperty("是否详细corn表达式")
    @Excel(name = "是否详细corn表达式")
    private Integer isAdvance;

    /** 执行的动作集合 */
    @Excel(name = "执行的动作集合")
    @ApiModelProperty("执行的动作集合")
    private String actions;

    /** 任务类型（1=设备定时，2=设备告警，3=场景联动） */
    @ApiModelProperty("任务类型")
    @Excel(name = "任务类型")
    private Integer jobType;

    /** 产品ID */
    @Excel(name = "产品ID")
    @ApiModelProperty("产品ID")
    private Long productId;

    /** 产品名称 */
    @Excel(name = "产品名称")
    @ApiModelProperty("产品名称")
    private String productName;

    /** 场景联动ID */
    @Excel(name = "场景联动ID")
    @ApiModelProperty("场景联动ID")
    private Long sceneId;

    /** 告警ID */
    @Excel(name = "告警ID")
    @ApiModelProperty("告警ID")
    private Long alertId;

    /** 定时告警触发器 */
    @Excel(name = "定时告警触发器")
    @ApiModelProperty("定时告警触发器")
    private String alertTrigger;

    /** 执行id,可共用，通过jobType区分 */
    @Excel(name = "执行id,可共用，通过jobType区分")
    @ApiModelProperty("执行id,可共用，通过jobType区分")
    private Long datasourceId;

    /**
     * 设备通讯协议
     */
    private String protocolCode;


}
