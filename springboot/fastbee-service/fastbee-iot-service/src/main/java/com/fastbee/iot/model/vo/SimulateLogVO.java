package com.fastbee.iot.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.extend.core.domin.mq.message.MqttBo;

/**
 * 模拟设备日志对象 iot_simulate_log
 *
 * @author zhuangpeng.li
 * @date 2024-11-14
 */

@ApiModel(value = "SimulateLogVO", description = "模拟设备日志 iot_simulate_log")
@Data
public class SimulateLogVO{

    /** 设备模拟日志ID */
    @Excel(name = "设备模拟日志ID")
    @ApiModelProperty("设备模拟日志ID")
    private Long logId;

    /** 云端发送指令 */
    @Excel(name = "云端发送指令")
    @ApiModelProperty("云端发送指令")
    private String sendData;

    /** 设备回复 */
    @Excel(name = "设备回复")
    @ApiModelProperty("设备回复")
    private String callbackData;

    /** 设备ID */
    @Excel(name = "设备ID")
    @ApiModelProperty("设备ID")
    private Long deviceId;

    /** 设备名称 */
    @Excel(name = "设备名称")
    @ApiModelProperty("设备名称")
    private String deviceName;

    /** 设备编号 */
    @Excel(name = "设备编号")
    @ApiModelProperty("设备编号")
    private String serialNumber;

    /** 创建者 */
    @Excel(name = "创建者")
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    /** 备注 */
    @Excel(name = "备注")
    @ApiModelProperty("备注")
    private String remark;

    private MqttBo send;
    private MqttBo receive;

}
