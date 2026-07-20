package com.fastbee.iot.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 产品modbus配置参数对象 iot_modbus_params
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */

@ApiModel(value = "ModbusParamsVO", description = "产品modbus配置参数 iot_modbus_params")
@Data
public class ModbusParamsVO{

    /** 业务id */
    @Excel(name = "业务id")
    @ApiModelProperty("业务id")
    private Long id;

    /** 产品id */
    @Excel(name = "产品id")
    @ApiModelProperty("产品id")
    private Long productId;

    /** 是否启动云端轮训(0-云端轮训,1-边缘采集) */
    @Excel(name = "是否启动云端轮训(0-云端轮训,1-边缘采集)")
    @ApiModelProperty("是否启动云端轮训(0-云端轮训,1-边缘采集)")
    private Integer pollType;

    /** 默认的子设备地址 */
    @Excel(name = "默认的子设备地址")
    @ApiModelProperty("默认的子设备地址")
    private String address;

    /** 子设备状态判断方式 1-设备数据 2- 网关 */
    @Excel(name = "子设备状态判断方式 1-设备数据 2- 网关")
    @ApiModelProperty("子设备状态判断方式 1-设备数据 2- 网关")
    private Integer statusDeter;

    /** 设备数据来判断子设备状态的时长(s) */
    @Excel(name = "设备数据来判断子设备状态的时长(s)")
    @ApiModelProperty("设备数据来判断子设备状态的时长(s)")
    private String deterTimer;

    /** 批量读取的个数 */
    @Excel(name = "批量读取的个数")
    @ApiModelProperty("批量读取的个数")
    private Integer pollLength;

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


}
