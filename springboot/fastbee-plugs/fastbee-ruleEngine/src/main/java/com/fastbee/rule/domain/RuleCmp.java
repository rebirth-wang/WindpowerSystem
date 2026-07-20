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
 * 规则组件对象 rule_cmp
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "RuleCmp", description = "规则组件 rule_cmp")
@Data
@TableName("rule_cmp" )
public class RuleCmp extends PageEntity {
    /** 租户id */
    @ApiModelProperty("租户id")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "系统租户隔离字段，查询时自动追加租户过滤",
            queryHint = "auto-data-scope"
    )
    private Long tenantId;

    /** id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("id")
    private Long id;

    /**  */
    @ApiModelProperty("")
    @AiSemanticField(
            semanticName = "component_id",
            semanticType = AiSemanticType.RELATION_KEY
    )
    private String componentId;

    /**  */
    @ApiModelProperty("")
    private String componentName;

    /**  */
    @ApiModelProperty("")
    @AiSemanticField(
            semanticName = "type",
            semanticType = AiSemanticType.ENUM
    )
    private String type;

    /**  */
    @ApiModelProperty("")
    private String script;

    /**  */
    @ApiModelProperty("")
    private String language;

    /**  */
    @ApiModelProperty("")
    private String clazz;

    /**  */
    @ApiModelProperty("")
    private String el;

    /**  */
    @ApiModelProperty("")
    private String cmpPre;

    /**  */
    @ApiModelProperty("")
    private String cmpFinallyOpt;

    /**  */
    @ApiModelProperty("")
    @AiSemanticField(
            semanticName = "cmp_id",
            semanticType = AiSemanticType.RELATION_KEY
    )
    private String cmpId;

    /**  */
    @ApiModelProperty("")
    private String cmpTag;

    /**  */
    @ApiModelProperty("")
    private Long cmpMaxWaitSeconds;

    /**  */
    @ApiModelProperty("")
    private String elFormat;

    /**  */
    @ApiModelProperty("")
    private String cmpDefaultOpt;

    /**  */
    @ApiModelProperty("")
    private String cmpTo;

    /**  */
    @ApiModelProperty("")
    private String cmpTrueOpt;

    /**  */
    @ApiModelProperty("")
    private String cmpFalseOpt;

    /**  */
    @ApiModelProperty("")
    private Long cmpParallel;

    /**  */
    @ApiModelProperty("")
    private String cmpDoOpt;

    /**  */
    @ApiModelProperty("")
    private String cmpBreakOpt;

    /**  */
    @ApiModelProperty("")
    private String cmpData;

    /**  */
    @ApiModelProperty("")
    private String cmpDataName;

    /**  */
    @ApiModelProperty("")
    @AiSemanticField(
            semanticName = "fallback_id",
            semanticType = AiSemanticType.RELATION_KEY
    )
    private Long fallbackId;

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
