package com.fastbee.iot.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.extend.core.protocol.modbus.ModbusCode;

/**
 * modbus配置对象 iot_modbus_config
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */

@ApiModel(value = "ModbusConfigVO", description = "modbus配置 iot_modbus_config")
@Data
public class ModbusConfigVO{

    /** 业务id */
    @Excel(name = "业务id")
    @ApiModelProperty("业务id")
    private Long id;

    /** 所属产品id */
    @Excel(name = "所属产品id")
    @ApiModelProperty("所属产品id")
    private Long productId;

    /** 关联属性 */
    @Excel(name = "关联属性")
    @ApiModelProperty("关联属性")
    private String identifier;

    /** 从机地址 */
    @Excel(name = "从机地址")
    @ApiModelProperty("从机地址")
    private String address;

    /** 寄存器地址 */
    @Excel(name = "寄存器地址")
    @ApiModelProperty("寄存器地址")
    private Integer register;

    /** 是否只读(0-否，1-是) */
    @Excel(name = "是否只读(0-否，1-是)")
    @ApiModelProperty("是否只读(0-否，1-是)")
    private Integer isReadonly;

    /** modbus数据类型 */
    @Excel(name = "modbus数据类型")
    @ApiModelProperty("modbus数据类型")
    private String dataType;

    /** 读取个数 */
    @Excel(name = "读取个数")
    @ApiModelProperty("读取个数")
    private Integer quantity;

    /** 寄存器类型 1-IO寄存器 2-数据寄存器 */
    @Excel(name = "寄存器类型 1-IO寄存器 2-数据寄存器")
    @ApiModelProperty("寄存器类型 1-IO寄存器 2-数据寄存器")
    private Integer type;

    /** bit位排序 */
    @Excel(name = "bit位排序")
    @ApiModelProperty("bit位排序")
    private Integer bitOrder;

    /** 排序 */
    @Excel(name = "排序")
    @ApiModelProperty("排序")
    private Integer sort;

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

    private ModbusCode modbusCode;

}
