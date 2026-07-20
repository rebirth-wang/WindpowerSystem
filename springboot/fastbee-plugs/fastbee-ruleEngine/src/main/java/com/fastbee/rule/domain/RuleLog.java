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
 * 规则执行记录对象 iot_rule_log
 *
 * @author fastbee
 * @date 2025-10-21
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "RuleLog", description = "规则执行记录 iot_rule_log")
@Data
@TableName("iot_rule_log" )
public class RuleLog extends PageEntity {
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

    /** EL表达式ID */
    @ApiModelProperty("EL表达式ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_rule_log.el_id=iot_rule_el.el_id"
    )
    private String elId;

    /** 执行状态: 0-失败, 1-成功 */
    @ApiModelProperty("执行状态: 0-失败, 1-成功")
    @AiSemanticField(
            semanticName = "执行状态",
            semanticType = AiSemanticType.ENUM,
            queryHint = "check-value-mapping"
    )
    private Integer status;

    /** 触发类型 */
    @ApiModelProperty("触发类型")
    @AiSemanticField(semanticType = AiSemanticType.ENUM)
    private Integer triggerType;

    /** 规则入参 */
    @ApiModelProperty("规则入参")
    private String ruleParams;

    /** 组件步骤信息 */
    @ApiModelProperty("组件步骤信息")
    private String stepMsg;

    /** 执行结果信息 */
    @ApiModelProperty("执行结果信息")
    private String resultMsg;

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
