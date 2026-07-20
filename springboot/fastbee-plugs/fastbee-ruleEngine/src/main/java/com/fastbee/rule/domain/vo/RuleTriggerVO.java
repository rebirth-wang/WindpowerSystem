package com.fastbee.rule.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 规则触发条件对象 iot_rule_trigger
 *
 * @author fastbee
 * @date 2025-10-21
 */

@ApiModel(value = "RuleTriggerVO", description = "规则触发条件 iot_rule_trigger")
@Data
public class RuleTriggerVO{
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

    /** 条件类型: 1-设备属性, 2-产品属性, 3-定时, 4-自定义 */
    @Excel(name = "条件类型: 1-设备属性, 2-产品属性, 3-定时, 4-自定义")
    @ApiModelProperty("条件类型: 1-设备属性, 2-产品属性, 3-定时, 4-自定义")
    private Long triggerType;

    /** 设备编号(设备触发时使用) */
    @Excel(name = "设备编号(设备触发时使用)")
    @ApiModelProperty("设备编号(设备触发时使用)")
    private String serialNumber;

    /** 产品ID(产品触发时使用) */
    @Excel(name = "产品ID(产品触发时使用)")
    @ApiModelProperty("产品ID(产品触发时使用)")
    private Long productId;

    /** 物模型标识 */
    @Excel(name = "物模型标识")
    @ApiModelProperty("物模型标识")
    private String modelId;

    /** 操作符: &gt;, =, &lt;=, != */
    @Excel(name = "操作符: &gt;, =, &lt;=, !=")
    @ApiModelProperty("操作符: &gt;, =, &lt;=, !=")
    private String operator;

    /** 比较值 */
    @Excel(name = "比较值")
    @ApiModelProperty("比较值")
    private String value;

    /** 定时表达式(定时触发时使用) */
    @Excel(name = "定时表达式(定时触发时使用)")
    @ApiModelProperty("定时表达式(定时触发时使用)")
    private String cronExpression;

    /** 自定义表达式(自定义触发时使用) */
    @Excel(name = "自定义表达式(自定义触发时使用)")
    @ApiModelProperty("自定义表达式(自定义触发时使用)")
    private String customExpression;

    /** 排序号 */
    @Excel(name = "排序号")
    @ApiModelProperty("排序号")
    private Long orderNum;

    /** 设备状态: 1-在线, 0-离线 */
    @Excel(name = "设备状态: 1-在线, 0-离线")
    @ApiModelProperty("设备状态: 1-在线, 0-离线")
    private Long deviceStatus;

    /** 触发参数 */
    @Excel(name = "触发参数")
    @ApiModelProperty("触发参数")
    private String triggerParams;

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
