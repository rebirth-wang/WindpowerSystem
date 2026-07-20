package com.fastbee.iot.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * 轮训任务列对象 iot_modbus_job
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ModbusJob", description = "轮训任务列 iot_modbus_job")
@Data
@TableName("iot_modbus_job" )
public class ModbusJob extends PageEntity{
    /** 任务id */
    @TableId(value = "task_id", type = IdType.AUTO)
    @ApiModelProperty("任务id")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_modbus_job.task_id=iot_product_modbus_job.task_id"
    )
    private Long taskId;

    /** 任务名称 */
    @ApiModelProperty("任务名称")
    private String jobName;

    /** 设备id */
    @ApiModelProperty("设备id")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_modbus_job.device_id=iot_device.device_id"
    )
    private Long deviceId;

    /** 设备编号 */
    @ApiModelProperty("设备编号")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_modbus_job.serial_number=iot_device.serial_number;可通过 iot_device.product_id=iot_product.product_id 继续关联产品",
            queryHint = "device-number"
    )
    private String serialNumber;

    /** 指令 */
    @ApiModelProperty("指令")
    private String command;

    /** 任务id */
    @ApiModelProperty("任务id")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long jobId;

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

    /** 创建者 */
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 备注信息 */
    @ApiModelProperty("备注信息")
    private String remark;

    /** 默认的子设备地址 */
    @ApiModelProperty("默认的子设备地址")
    private String address;

    /** 类型（1轮询指令 2下发指令） */
    @AiSemanticField(semanticType = AiSemanticType.ENUM)
    private Integer commandType;

}
