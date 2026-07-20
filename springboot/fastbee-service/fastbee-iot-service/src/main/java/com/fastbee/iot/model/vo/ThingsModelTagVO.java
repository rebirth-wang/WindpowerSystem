package com.fastbee.iot.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 物模型计算对象 iot_things_model_tag
 *
 * @author fastbee
 * @date 2025-06-24
 */

@ApiModel(value = "ThingsModelTagVO", description = "物模型计算 iot_things_model_tag")
@Data
public class ThingsModelTagVO{
    /** 代码生成区域 可直接覆盖**/
    /** 主键id */
    @Excel(name = "主键id")
    @ApiModelProperty("主键id")
    private Long id;

    /** 物模型标识 */
    @Excel(name = "物模型标识")
    @ApiModelProperty("物模型标识")
    private String identifier;

    /** 别名，如 A */
    @Excel(name = "别名，如 A")
    @ApiModelProperty("别名，如 A")
    private String alias;

    /** 关联的模型id */
    @Excel(name = "关联的模型id")
    @ApiModelProperty("关联的模型id")
    private Long modelId;

    /** 统计方式 ，用字典定义，暂时是”原值“ */
    @Excel(name = "统计方式 ，用字典定义，暂时是”原值“")
    @ApiModelProperty("统计方式 ，用字典定义，暂时是”原值“")
    private Long operation;

    /** 数据源模型id */
    @Excel(name = "数据源模型id")
    @ApiModelProperty("数据源模型id")
    private Long sourceModelId;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @Excel(name = "删除标志")
    private String delFlag;

    /** 创建者 */
    @Excel(name = "创建者")
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    /** 更新者 */
    @Excel(name = "更新者")
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    @Excel(name = "更新时间")
    private Date updateTime;

    /** 备注 */
    @Excel(name = "备注")
    @ApiModelProperty("备注")
    private String remark;


    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
