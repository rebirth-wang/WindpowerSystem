package com.fastbee.rule.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 规则组件对象 rule_cmp
 *
 * @author zhuangpeng.li
 * @date 2025-05-08
 */

@ApiModel(value = "RuleCmpVO", description = "规则组件 rule_cmp")
@Data
public class RuleCmpVO{
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
    private String componentId;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String componentName;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String type;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String script;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String language;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String clazz;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String el;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String cmpPre;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String cmpFinallyOpt;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String cmpId;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String cmpTag;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private Long cmpMaxWaitSeconds;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String elFormat;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String cmpDefaultOpt;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String cmpTo;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String cmpTrueOpt;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String cmpFalseOpt;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private Long cmpParallel;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String cmpDoOpt;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String cmpBreakOpt;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String cmpData;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String cmpDataName;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private Long fallbackId;

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
