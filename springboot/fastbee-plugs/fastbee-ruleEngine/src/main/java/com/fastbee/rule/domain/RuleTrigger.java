package com.fastbee.rule.domain;

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
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 规则触发条件对象 iot_rule_trigger
 *
 * @author fastbee
 * @date 2025-10-21
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "RuleTrigger", description = "规则触发条件 iot_rule_trigger")
@Data
@TableName("iot_rule_trigger" )
public class RuleTrigger extends PageEntity {
    /**  */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("")
    private Long id;

    /** 租户id */
    @ApiModelProperty("租户id")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "系统租户隔离字段，查询时自动追加租户过滤",
            queryHint = "auto-data-scope"
    )
    private Long tenantId;

    /** 规则ID */
    @ApiModelProperty("规则ID")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long ruleId;

    /** 条件类型: 1-设备属性, 2-产品属性, 3-定时, 4-自定义 */
    @ApiModelProperty("条件类型: 1-设备属性, 2-产品属性, 3-定时, 4-自定义")
    @AiSemanticField(
            semanticName = "条件类型",
            semanticType = AiSemanticType.ENUM
    )
    private Long triggerType;

    /** 设备编号(设备触发时使用) */
    @ApiModelProperty("设备编号(设备触发时使用)")
    @AiSemanticField(
            semanticName = "设备编号",
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_rule_trigger.serial_number=iot_device.serial_number;可通过 iot_device.product_id=iot_product.product_id 继续关联产品",
            queryHint = "device-number"
    )
    private String serialNumber;

    /** 产品ID(产品触发时使用) */
    @ApiModelProperty("产品ID(产品触发时使用)")
    @AiSemanticField(
            semanticName = "产品ID",
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_rule_trigger.product_id=iot_product.product_id;可通过 iot_product.product_id=iot_things_model.product_id 继续关联物模型"
    )
    private Long productId;

    /** 物模型标识 */
    @ApiModelProperty("物模型标识")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_rule_trigger.model_id=iot_things_model.model_id"
    )
    private String modelId;

    /** 操作符: &gt;, =, &lt;=, != */
    @ApiModelProperty("操作符: &gt;, =, &lt;=, !=")
    private String operator;

    /** 比较值 */
    @ApiModelProperty("比较值")
    private String value;

    /** 定时表达式(定时触发时使用) */
    @ApiModelProperty("定时表达式(定时触发时使用)")
    private String cronExpression;

    /** 自定义表达式(自定义触发时使用) */
    @ApiModelProperty("自定义表达式(自定义触发时使用)")
    private String customExpression;

    /** 排序号 */
    @ApiModelProperty("排序号")
    private Long orderNum;

    /** 设备状态: 1-在线, 0-离线 */
    @ApiModelProperty("设备状态: 1-在线, 0-离线")
    @AiSemanticField(
            semanticName = "设备状态",
            semanticType = AiSemanticType.ENUM
    )
    private Long deviceStatus;

    /** 触发参数 */
    @ApiModelProperty("触发参数")
    private String triggerParams;

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
    @AiSemanticField(semanticType = AiSemanticType.ENUM)
    private Long delFlag;

    /** 备注 */
    @ApiModelProperty("备注")
    private String remark;


}
