package com.fastbee.rule.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 规则el对象 rule_el
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */

@ApiModel(value = "RuleElVO", description = "规则el rule_el")
@Data
public class RuleElVO{
    /** 代码生成区域 可直接覆盖**/
    /** 租户id */
    @Excel(name = "租户id")
    @ApiModelProperty("租户id")
    private Long tenantId;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private Long id;

    /** EL表达式ID */
    @Excel(name = "EL表达式ID")
    @ApiModelProperty("EL表达式ID")
    private String elId;

    /** EL表达式名称 */
    @Excel(name = "EL表达式名称")
    @ApiModelProperty("EL表达式名称")
    private String elName;

    /** EL表达式 */
    @Excel(name = "EL表达式")
    @ApiModelProperty("EL表达式")
    private String el;

    /** 流程数据 */
    @Excel(name = "流程数据")
    @ApiModelProperty("流程数据")
    private String flowJson;

    /** 原始数据 */
    @Excel(name = "原始数据")
    @ApiModelProperty("原始数据")
    private String sourceJson;

    /** Executor执行器ID */
    @Excel(name = "Executor执行器ID")
    @ApiModelProperty("Executor执行器ID")
    private Long executorId;

    /** 场景ID */
    @ApiModelProperty("场景ID")
    private Long sceneId;

    /** 是否生效（1-生效，2-不生效） */
    @ApiModelProperty("是否生效")
    @Excel(name = "是否生效")
    private Integer enable;

    /** 规则入参 */
    @Excel(name = "规则入参")
    @ApiModelProperty("规则入参")
    private String ruleParams;

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
