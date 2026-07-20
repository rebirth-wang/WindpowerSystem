package com.fastbee.iot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.BaseEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 设备定时对象 iot_device_job
 *
 * @author zhuangpeng.li
 * @date 2025-01-07
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "DeviceJob", description = "设备定时 iot_device_job")
@Data
@TableName("iot_device_job" )
public class DeviceJob extends BaseEntity {
    /** 任务ID */
    @TableId(value = "job_id", type = IdType.AUTO)
    @ApiModelProperty("任务ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_device_job.job_id=sys_job.job_id"
    )
    private Long jobId;

    /** 任务名称 */
    @ApiModelProperty("任务名称")
    private String jobName;

    /** 任务组名 */
    @ApiModelProperty("任务组名")
    private String jobGroup;

    /** cron执行表达式 */
    @ApiModelProperty("cron执行表达式")
    private String cronExpression;

    /** 计划执行错误策略（1立即执行 2执行一次 3放弃执行） */
    @ApiModelProperty("计划执行错误策略")
    @AiSemanticField(
            semanticName = "计划执行错误策略",
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private String misfirePolicy;

    /** 是否并发执行（0允许 1禁止） */
    @ApiModelProperty("是否并发执行")
    @AiSemanticField(
            semanticName = "是否并发执行",
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private String concurrent;

    /** 状态（0正常 1暂停） */
    @ApiModelProperty("状态")
    @AiSemanticField(
            semanticName = "状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "sys_job_status",
            queryHint = "check-value-mapping"
    )
    private Integer status;

    /** 设备ID */
    @ApiModelProperty("设备ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_device_job.device_id=iot_device.device_id"
    )
    private Long deviceId;

    /** 设备编号 */
    @ApiModelProperty("设备编号")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_device_job.serial_number=iot_device.serial_number;可通过 iot_device.product_id=iot_product.product_id 继续关联产品",
            queryHint = "device-number"
    )
    private String serialNumber;

    /** 设备名称 */
    @ApiModelProperty("设备名称")
    private String deviceName;

    /** 是否详细corn表达式（1=是，0=否） */
    @ApiModelProperty("是否详细corn表达式")
    @AiSemanticField(
            semanticName = "是否详细corn表达式",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private Integer isAdvance;

    /** 执行的动作集合 */
    @ApiModelProperty("执行的动作集合")
    private String actions;

    /** 任务类型（1=设备定时，2=设备告警，3=场景联动） */
    @ApiModelProperty("任务类型")
    @AiSemanticField(
            semanticName = "任务类型",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private Integer jobType;

    /** 产品ID */
    @ApiModelProperty("产品ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_device_job.product_id=iot_product.product_id;可通过 iot_product.product_id=iot_things_model.product_id 继续关联物模型"
    )
    private Long productId;

    /** 产品名称 */
    @ApiModelProperty("产品名称")
    private String productName;

    /** 场景联动ID */
    @ApiModelProperty("场景联动ID")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long sceneId;

    /** 告警ID */
    @ApiModelProperty("告警ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_device_job.alert_id=iot_alert.alert_id"
    )
    private Long alertId;

    /** 定时告警触发器 */
    @ApiModelProperty("定时告警触发器")
    private String alertTrigger;

    /** 执行id,可共用，通过jobType区分 */
    @ApiModelProperty("执行id,可共用，通过jobType区分")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long datasourceId;

}
