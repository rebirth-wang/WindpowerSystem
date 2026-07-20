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
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 规则执行器对象 rule_executor
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "RuleExecutor", description = "规则执行器 rule_executor")
@Data
@TableName("rule_executor" )
public class RuleExecutor extends PageEntity {
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

    /** 执行器ID */
    @ApiModelProperty("执行器ID")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private String executorId;

    /** 执行器名称 */
    @ApiModelProperty("执行器名称")
    private String executorName;

    /** 执行器配置IvyConfig */
    @ApiModelProperty("执行器配置IvyConfig")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long ivyConfigId;

    /** 执行器类型【execute2Resp:execute2Resp,execute2Future:execute2Future】 */
    @ApiModelProperty("执行器类型【execute2Resp:execute2Resp,execute2Future:execute2Future】")
    @AiSemanticField(
            semanticName = "执行器类型【execute2Resp",
            semanticType = AiSemanticType.ENUM
    )
    private String executorType;

    /** 上下文bean */
    @ApiModelProperty("上下文bean")
    private String contextBeans;

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
