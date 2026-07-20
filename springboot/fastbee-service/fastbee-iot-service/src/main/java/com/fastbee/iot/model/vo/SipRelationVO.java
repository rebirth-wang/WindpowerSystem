package com.fastbee.iot.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 监控设备关联对象 iot_sip_relation
 *
 * @author zhuangpeng.li
 * @date 2024-11-14
 */

@ApiModel(value = "SipRelationVO", description = "监控设备关联 iot_sip_relation")
@Data
public class SipRelationVO{

    /** 业务id */
    @Excel(name = "业务id")
    @ApiModelProperty("业务id")
    private Long id;

    /** 监控设备编号 */
    @Excel(name = "监控设备编号")
    @ApiModelProperty("监控设备编号")
    private String channelId;

    /** 关联的设备id */
    @Excel(name = "关联的设备id")
    @ApiModelProperty("关联的设备id")
    private Long reDeviceId;

    /** 关联的场景id */
    @Excel(name = "关联的场景id")
    @ApiModelProperty("关联的场景id")
    private Long reSceneModelId;

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

    /** 备注 */
    @Excel(name = "备注")
    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("通道名称")
    @Excel(name = "通道名称")
    private String channelName;

    @ApiModelProperty("产品型号")
    @Excel(name = "产品型号")
    private String model;

    @ApiModelProperty("监控设备id")
    private Long deviceId;

    @ApiModelProperty("sip设备编号")
    private String deviceSipId;

    @ApiModelProperty("通道状态")
    private Integer status;

    /** 通道播放地址 */
    @ApiModelProperty("通道播放地址")
    private String playUrl;

    /** 流代理地址 */
    @ApiModelProperty("流代理地址")
    private String proxyUrl;

    /** 设备类型 */
    @ApiModelProperty("设备类型")
    private String deviceType;

    /** 通道类型 */
    @ApiModelProperty("通道类型")
    private String channelType;

}
