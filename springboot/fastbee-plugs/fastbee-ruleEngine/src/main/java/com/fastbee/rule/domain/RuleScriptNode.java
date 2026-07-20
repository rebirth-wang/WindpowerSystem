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
 * 规则脚本节点对象 rule_script_node
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "RuleScriptNode", description = "规则脚本节点 rule_script_node")
@Data
@TableName("rule_script_node" )
public class RuleScriptNode extends PageEntity {
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

    /**  */
    @ApiModelProperty("")
    private String applicationName;

    /**  */
    @ApiModelProperty("")
    @AiSemanticField(
            semanticName = "script_node_id",
            semanticType = AiSemanticType.RELATION_KEY
    )
    private String scriptNodeId;

    /**  */
    @ApiModelProperty("")
    private String scriptNodeName;

    /**  */
    @ApiModelProperty("")
    @AiSemanticField(
            semanticName = "script_node_type",
            semanticType = AiSemanticType.ENUM
    )
    private String scriptNodeType;

    /**  */
    @ApiModelProperty("")
    private String scriptNodeData;

    /**  */
    @ApiModelProperty("")
    private String scriptLanguage;

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
