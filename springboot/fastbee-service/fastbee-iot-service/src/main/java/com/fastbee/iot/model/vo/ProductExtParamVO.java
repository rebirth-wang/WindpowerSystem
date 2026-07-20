package com.fastbee.iot.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 产品扩展参数对象 iot_product_ext_param
 *
 * @author fastbee
 * @date 2026-03-18
 */

@ApiModel(value = "ProductExtParamVO", description = "产品扩展参数 iot_product_ext_param")
@Data
public class ProductExtParamVO{
    /** 代码生成区域 可直接覆盖**/
    /** 参数ID */
    @Excel(name = "参数ID")
    @ApiModelProperty("参数ID")
    private Long paramId;

    /** 产品ID */
    @Excel(name = "产品ID")
    @ApiModelProperty("产品ID")
    private Long productId;

    /** 参数名称 */
    @Excel(name = "参数名称")
    @ApiModelProperty("参数名称")
    private String paramName;

    /** 参数类型 */
    @Excel(name = "参数类型")
    @ApiModelProperty("参数类型")
    private String paramType;

    /** 默认值 */
    @Excel(name = "默认值")
    @ApiModelProperty("默认值")
    private String defaultValue;

    /** 是否启用(0-否,1-是) */
    @Excel(name = "是否启用(0-否,1-是)")
    @ApiModelProperty("是否启用(0-否,1-是)")
    private Integer isEnabled;

    /** 是否在APP显示(0-否,1-是) */
    @Excel(name = "是否在APP显示(0-否,1-是)")
    @ApiModelProperty("是否在APP显示(0-否,1-是)")
    private Integer isAppShow;

    /** 规格 */
    @Excel(name = "规格")
    @ApiModelProperty("规格")
    private String spec;

    /** 描述 */
    @Excel(name = "描述")
    @ApiModelProperty("描述")
    private String description;

    /** 删除标志(0-存在,1-删除) */
    @Excel(name = "删除标志(0-存在,1-删除)")
    @ApiModelProperty("删除标志(0-存在,1-删除)")
    private Integer delFlag;

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

    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/
    /** 参数值（设备扩展参数值） */
    @ApiModelProperty("参数值")
    private String paramValue;


    /** 自定义代码区域 END**/

    /** 设备扩展参数id */
    private Long id;
}
