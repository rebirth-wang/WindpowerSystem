package com.fastbee.onvif.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * onvif设备通道对象
 *
 * @author fastbee
 * @date 2026-01-06
 */

@ApiModel(value = "OnvifDeviceChannelVO", description = "onvif设备通道")
@Data
public class OnvifDeviceChannelVO{
    /** 代码生成区域 可直接覆盖**/
    /** 主键id */
    @Excel(name = "主键id")
    @ApiModelProperty("主键id")
    private Integer id;

    /** 设备id */
    @Excel(name = "设备id")
    @ApiModelProperty("设备id")
    private Integer deviceId;

    /** 通道名称 */
    @Excel(name = "通道名称")
    @ApiModelProperty("通道名称")
    private String name;

    /** 序列号 */
    @Excel(name = "序列号")
    @ApiModelProperty("序列号")
    private String serialNumber;

    /** 固件版本 */
    @Excel(name = "固件版本")
    @ApiModelProperty("固件版本")
    private String firmwareVersion;

    /** 硬件id */
    @Excel(name = "硬件id")
    @ApiModelProperty("硬件id")
    private String hardwareId;

    /** 状态 */
    @Excel(name = "状态")
    @ApiModelProperty("状态")
    private Long status;

    /** 使能音频 */
    @Excel(name = "使能音频")
    @ApiModelProperty("使能音频")
    private Long enableAudio;

    /** 使能录像 */
    @Excel(name = "使能录像")
    @ApiModelProperty("使能录像")
    private Long enableMp4;

    /** 厂商名称 */
    @Excel(name = "厂商名称")
    @ApiModelProperty("厂商名称")
    private String manufacture;

    /** 产品型号 */
    @Excel(name = "产品型号")
    @ApiModelProperty("产品型号")
    private String model;

    /** 设备入网IP */
    @Excel(name = "设备入网IP")
    @ApiModelProperty("设备入网IP")
    private String ip;

    /** 设备接入端口号 */
    @Excel(name = "设备接入端口号")
    @ApiModelProperty("设备接入端口号")
    private Integer port;

    /** 设备入网账号 */
    @Excel(name = "设备入网账号")
    @ApiModelProperty("设备入网账号")
    private String username;

    /** 设备入网密码 */
    @Excel(name = "设备入网密码")
    @ApiModelProperty("设备入网密码")
    private String password;

    /** 设备归属 */
    @Excel(name = "设备归属")
    @ApiModelProperty("设备归属")
    private String owner;

    /** 设备地址 */
    @Excel(name = "设备地址")
    @ApiModelProperty("设备地址")
    private String address;

    /** 设备经度 */
    @Excel(name = "设备经度")
    @ApiModelProperty("设备经度")
    private Long longitude;

    /** 设备纬度 */
    @Excel(name = "设备纬度")
    @ApiModelProperty("设备纬度")
    private Long latitude;

    /** PTZ类型 */
    @Excel(name = "PTZ类型")
    @ApiModelProperty("PTZ类型")
    private Long ptzType;

    /** 位置类型 */
    @Excel(name = "位置类型")
    @ApiModelProperty("位置类型")
    private Long positionType;

    /** 空间类型 */
    @Excel(name = "空间类型")
    @ApiModelProperty("空间类型")
    private Long roomType;

    /** 使用类型 */
    @Excel(name = "使用类型")
    @ApiModelProperty("使用类型")
    private Long useType;

    /** 补光类型 */
    @Excel(name = "补光类型")
    @ApiModelProperty("补光类型")
    private Long supplylightType;

    /** 方向类型 */
    @Excel(name = "方向类型")
    @ApiModelProperty("方向类型")
    private Long directionType;

    /** tcp流播放地址 */
    @Excel(name = "tcp流播放地址")
    @ApiModelProperty("tcp流播放地址")
    private String liveStreamTcp;

    /** udp流播放地址 */
    @Excel(name = "udp流播放地址")
    @ApiModelProperty("udp流播放地址")
    private String liveStreamUdp;

    /** multicast流播放地址 */
    @Excel(name = "multicast流播放地址")
    @ApiModelProperty("multicast流播放地址")
    private String liveStreamMulticast;

    /** 回放流播放地址 */
    @Excel(name = "回放流播放地址")
    @ApiModelProperty("回放流播放地址")
    private String replayStream;

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
