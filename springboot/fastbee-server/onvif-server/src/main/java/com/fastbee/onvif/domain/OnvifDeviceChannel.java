package com.fastbee.onvif.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.PageEntity;

/**
 * onvif设备通道对象
 *
 * @author fastbee
 * @date 2026-01-06
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "OnvifDeviceChannel", description = "onvif设备通道")
@Data
public class OnvifDeviceChannel extends PageEntity {
    /** 主键id */
    @ApiModelProperty("主键id")
    private Integer id;

    /** 设备id */
    @ApiModelProperty("设备id")
    private Integer deviceId;

    /** 通道名称 */
    @ApiModelProperty("通道名称")
    private String name;

    /** 序列号 */
    @ApiModelProperty("序列号")
    private String serialNumber;

    /** 固件版本 */
    @ApiModelProperty("固件版本")
    private String firmwareVersion;

    /** 硬件id */
    @ApiModelProperty("硬件id")
    private String hardwareId;

    /** 状态 */
    @ApiModelProperty("状态")
    private Long status;

    /** 使能音频 */
    @ApiModelProperty("使能音频")
    private Long enableAudio;

    /** 使能录像 */
    @ApiModelProperty("使能录像")
    private Long enableMp4;

    /** 厂商名称 */
    @ApiModelProperty("厂商名称")
    private String manufacture;

    /** 产品型号 */
    @ApiModelProperty("产品型号")
    private String model;

    /** 设备入网IP */
    @ApiModelProperty("设备入网IP")
    private String ip;

    /** 设备接入端口号 */
    @ApiModelProperty("设备接入端口号")
    private Integer port;

    /** 设备入网账号 */
    @ApiModelProperty("设备入网账号")
    private String username;

    /** 设备入网密码 */
    @ApiModelProperty("设备入网密码")
    private String password;

    /** 设备归属 */
    @ApiModelProperty("设备归属")
    private String owner;

    /** 设备地址 */
    @ApiModelProperty("设备地址")
    private String address;

    /** 设备经度 */
    @ApiModelProperty("设备经度")
    private Long longitude;

    /** 设备纬度 */
    @ApiModelProperty("设备纬度")
    private Long latitude;

    /** PTZ类型 */
    @ApiModelProperty("PTZ类型")
    private Long ptzType;

    /** 位置类型 */
    @ApiModelProperty("位置类型")
    private Long positionType;

    /** 空间类型 */
    @ApiModelProperty("空间类型")
    private Long roomType;

    /** 使用类型 */
    @ApiModelProperty("使用类型")
    private Long useType;

    /** 补光类型 */
    @ApiModelProperty("补光类型")
    private Long supplylightType;

    /** 方向类型 */
    @ApiModelProperty("方向类型")
    private Long directionType;

    /** tcp流播放地址 */
    @ApiModelProperty("tcp流播放地址")
    private String liveStreamTcp;

    /** udp流播放地址 */
    @ApiModelProperty("udp流播放地址")
    private String liveStreamUdp;

    /** multicast流播放地址 */
    @ApiModelProperty("multicast流播放地址")
    private String liveStreamMulticast;

    /** 回放流播放地址 */
    @ApiModelProperty("回放流播放地址")
    private String replayStream;

    /** ONVIF 配置文件 Token */
    @ApiModelProperty("ONVIF 配置文件 Token")
    private String profileToken;

    /** Media 服务地址 */
    @ApiModelProperty("Media 服务地址")
    private String mediaServiceUrl;

    /** PTZ 服务地址 */
    @ApiModelProperty("PTZ 服务地址")
    private String ptzServiceUrl;

    /** 快照地址 */
    @ApiModelProperty("快照地址")
    private String snapshotUri;

    /** 是否支持 PTZ */
    @ApiModelProperty("是否支持 PTZ")
    private Integer supportPtz;

    /** 视频编码格式 */
    @ApiModelProperty("视频编码格式")
    private String encoding;

    /** 分辨率宽 */
    @ApiModelProperty("分辨率宽")
    private Integer resolutionWidth;

    /** 分辨率高 */
    @ApiModelProperty("分辨率高")
    private Integer resolutionHeight;

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
