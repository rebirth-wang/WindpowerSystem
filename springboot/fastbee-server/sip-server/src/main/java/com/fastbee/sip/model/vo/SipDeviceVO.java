package com.fastbee.sip.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 监控设备对象 sip_device
 *
 * @author zhuangpeng.li
 * @date 2024-11-20
 */

@ApiModel(value = "SipDeviceVO", description = "监控设备 sip_device")
@Data
public class SipDeviceVO{

    /** 设备ID */
    @Excel(name = "设备ID")
    @ApiModelProperty("设备ID")
    private Long deviceId;

    /** 产品ID */
    @Excel(name = "产品ID")
    @ApiModelProperty("产品ID")
    private Long productId;

    /** 产品名称 */
    @Excel(name = "产品名称")
    @ApiModelProperty("产品名称")
    private String productName;

    /** 设备SipID */
    @Excel(name = "设备SipID")
    @ApiModelProperty("设备SipID")
    private String deviceSipId;

    /** 设备名称 */
    @Excel(name = "设备名称")
    @ApiModelProperty("设备名称")
    private String deviceName;

    /** 厂商名称 */
    @Excel(name = "厂商名称")
    @ApiModelProperty("厂商名称")
    private String manufacturer;

    /** 产品型号 */
    @Excel(name = "产品型号")
    @ApiModelProperty("产品型号")
    private String model;

    /** 固件版本 */
    @Excel(name = "固件版本")
    @ApiModelProperty("固件版本")
    private String firmware;

    /** 传输模式 */
    @Excel(name = "传输模式")
    @ApiModelProperty("传输模式")
    private String transport;

    /** 流模式 */
    @Excel(name = "流模式")
    @ApiModelProperty("流模式")
    private String streamMode;

    /** 在线状态 */
    @Excel(name = "在线状态")
    @ApiModelProperty("在线状态")
    private String onlineStatus;

    /** 注册时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("注册时间")
    @Excel(name = "注册时间")
    private Date registerTime;

    /** 最后上线时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("最后上线时间")
    @Excel(name = "最后上线时间")
    private Date lastConnectTime;

    /** 激活时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("激活时间")
    @Excel(name = "激活时间")
    private Date activeTime;

    /** 设备入网IP */
    @Excel(name = "设备入网IP")
    @ApiModelProperty("设备入网IP")
    private String ip;

    /** 设备接入端口号 */
    @Excel(name = "设备接入端口号")
    @ApiModelProperty("设备接入端口号")
    private Integer port;

    /** 设备地址 */
    @Excel(name = "设备地址")
    @ApiModelProperty("设备地址")
    private String hostAddress;

    /** 接入sip服务ID */
    @Excel(name = "接入sip服务ID")
    @ApiModelProperty("接入sip服务ID")
    private String serverId;

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

}
