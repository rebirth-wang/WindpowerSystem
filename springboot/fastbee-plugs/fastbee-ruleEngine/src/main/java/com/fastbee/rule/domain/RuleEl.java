package com.fastbee.rule.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 规则el对象 rule_el
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "RuleEl", description = "规则el rule_el")
@Data
@TableName("iot_rule_el" )
public class RuleEl extends PageEntity {
    /** 租户id */
    @ApiModelProperty("租户id")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "系统租户隔离字段，查询时自动追加租户过滤",
            queryHint = "auto-data-scope"
    )
    private Long tenantId;

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("id")
    private Long id;

    /** EL表达式ID */
    @ApiModelProperty("EL表达式ID")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private String elId;

    /** EL表达式名称 */
    @ApiModelProperty("EL表达式名称")
    private String elName;

    /** EL表达式 */
    @ApiModelProperty("EL表达式")
    private String el;

    /** 流程数据 */
    @ApiModelProperty("流程数据")
    private String flowJson;

    /** 原始数据 */
    @ApiModelProperty("原始数据")
    private String sourceJson;

    /** Executor执行器ID */
    @ApiModelProperty("Executor执行器ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_rule_el.executor_id=rule_executor.executor_id"
    )
    private Long executorId;

    /** 场景ID */
    @ApiModelProperty("场景ID")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long sceneId;

    /** 是否生效（1-生效，2-不生效） */
    @ApiModelProperty("是否生效")
    @AiSemanticField(
            semanticName = "是否生效",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "rule_engine_status",
            valueMappings = {"-不生效，1-生效=0"}
    )
    private Integer enable;

    /** 规则入参 */
    @ApiModelProperty("规则入参")
    private String ruleParams;

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

    @Setter
    @TableField(exist = false)
    @ApiModelProperty("请求参数")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;

    public Map<String, Object> getParams(){
        if (params == null){
            params = new HashMap<>();
        }
        return params;
    }

}
