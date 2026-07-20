package com.fastbee.rule.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 规则脚本节点对象 rule_script_node
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */

@ApiModel(value = "RuleScriptNodeVO", description = "规则脚本节点 rule_script_node")
@Data
public class RuleScriptNodeVO{
    /** 代码生成区域 可直接覆盖**/
    /** 租户id */
    @Excel(name = "租户id")
    @ApiModelProperty("租户id")
    private Long tenantId;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private Long id;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String applicationName;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String scriptNodeId;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String scriptNodeName;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String scriptNodeType;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String scriptNodeData;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String scriptLanguage;

    /** 创建人 */
    @Excel(name = "创建人")
    @ApiModelProperty("创建人")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    /** 更新人 */
    @Excel(name = "更新人")
    @ApiModelProperty("更新人")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    @Excel(name = "更新时间")
    private Date updateTime;

    /** 逻辑删除标识 */
    @Excel(name = "逻辑删除标识")
    @ApiModelProperty("逻辑删除标识")
    private Long delFlag;

    /** 备注 */
    @Excel(name = "备注")
    @ApiModelProperty("备注")
    private String remark;


    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
