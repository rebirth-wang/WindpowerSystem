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
 * 产品轮训任务列对象 iot_product_modbus_job
 *
 * @author zhuangpeng.li
 * @date 2024-09-04
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ProductModbusJob", description = "产品轮训任务列 iot_product_modbus_job")
@Data
@TableName("iot_product_modbus_job" )
public class ProductModbusJob extends PageEntity{
    private static final long serialVersionUID=1L;

    /** 任务id */
    @TableId(value = "task_id", type = IdType.AUTO)
    @ApiModelProperty("任务id")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_product_modbus_job.task_id=iot_modbus_job.task_id"
    )
    private Long taskId;

    /** 任务名称 */
    @ApiModelProperty("任务名称")
    private String jobName;

    /** 产品id */
    @ApiModelProperty("产品id")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_product_modbus_job.product_id=iot_product.product_id;可通过 iot_product.product_id=iot_things_model.product_id 继续关联物模型"
    )
    private Long productId;

    /** 指令 */
    @ApiModelProperty("指令")
    @AiSemanticField(relationHint = "JSON字段,code字段对应DICT为product_command_function_code")
    private String command;

    /** 创建者 */
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 备注信息 */
    @ApiModelProperty("备注信息")
    @AiSemanticField(relationHint = "根据周期不同,DICT为variable_operation_interval,variable_operation_time,variable_operation_week,variable_operation_day")
    private String remark;

    /**
     * 状态（0正常 1暂停）
     */
    @ApiModelProperty("状态")
    @AiSemanticField(
            semanticName = "状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "sys_job_status",
            queryHint = "check-value-mapping"
    )
    private Integer status;

    /** 默认的子设备地址 */
    @ApiModelProperty("默认的子设备地址")
    private String address;

    /** 类型（1轮询指令 2下发指令） */
    @AiSemanticField(semanticType = AiSemanticType.ENUM)
    private Integer commandType;

}
