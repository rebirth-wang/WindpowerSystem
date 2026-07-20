package com.fastbee.rule.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 规则执行记录对象 iot_rule_log
 *
 * @author fastbee
 * @date 2025-10-21
 */

@ApiModel(value = "RuleLogVO", description = "规则执行记录 iot_rule_log")
@Data
public class RuleLogVO{
    /** 代码生成区域 可直接覆盖**/
    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private Long id;

    /** 租户id */
    @Excel(name = "租户id")
    @ApiModelProperty("租户id")
    private Long tenantId;

    /** 规则ID */
    @Excel(name = "规则ID")
    @ApiModelProperty("规则ID")
    private Long ruleId;

    /** EL表达式ID */
    @Excel(name = "EL表达式ID")
    @ApiModelProperty("EL表达式ID")
    private String elId;

    /** 执行状态: 0-失败, 1-成功 */
    @Excel(name = "执行状态: 0-失败, 1-成功")
    @ApiModelProperty("执行状态: 0-失败, 1-成功")
    private Long status;

    /** 触发类型 */
    @Excel(name = "触发类型")
    @ApiModelProperty("触发类型")
    private Long triggerType;

    /** 规则入参 */
    @Excel(name = "规则入参")
    @ApiModelProperty("规则入参")
    private String ruleParams;

    /** 组件步骤信息 */
    @Excel(name = "组件步骤信息")
    @ApiModelProperty("组件步骤信息")
    private String stepMsg;

    /** 执行结果信息 */
    @Excel(name = "执行结果信息")
    @ApiModelProperty("执行结果信息")
    private String resultMsg;

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
