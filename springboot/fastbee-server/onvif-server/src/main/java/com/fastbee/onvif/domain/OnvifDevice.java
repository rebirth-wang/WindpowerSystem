package com.fastbee.onvif.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.PageEntity;

/**
 * ONVIF 节点扩展对象。
 * 持久化时映射到 iot_device，status/ip/lastHeartbeat 使用 iot_device 原字段，
 * 仅 ONVIF 专属扩展字段写入 iot_device.summary.onvif。
 *
 * @author fastbee
 * @date 2026-01-06
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "OnvifDevice", description = "ONVIF 节点扩展对象")
@Data
public class OnvifDevice extends PageEntity {
    /** 设备ID，对应 iot_device.device_id */
    @ApiModelProperty("设备ID，对应 iot_device.device_id")
    private Integer id;

    /** 设备名称 */
    @ApiModelProperty("设备名称")
    private String name;

    /** 设备状态 */
    @ApiModelProperty("设备状态")
    private Long status;

    /** 连接方式 */
    @ApiModelProperty("连接方式")
    private Long directConnection;

    /** 设备 IP（WS-Discovery 发现的地址） */
    @ApiModelProperty("设备 IP（WS-Discovery 发现的地址）")
    private String ip;

    /** 最后心跳时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("最后心跳时间")
    private Date lastHeartbeat;

    /** 创建者 */
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 更新者 */
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /** 备注 */
    @ApiModelProperty("备注")
    private String remark;


}
