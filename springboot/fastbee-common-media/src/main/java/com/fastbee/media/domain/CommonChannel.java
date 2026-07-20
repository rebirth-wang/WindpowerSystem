package com.fastbee.media.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.PageEntity;

/**
 * 监控视频通道信息对象 common_channel
 *
 * @author fastbee
 * @date 2026-01-30
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "CommonChannel", description = "监控视频通道信息 common_channel")
@Data
@TableName("common_channel" )
public class CommonChannel extends PageEntity {
    /** 主键 */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Long id;

    /**  */
    @ApiModelProperty("")
    private Long tenantId;

    /** 产品ID */
    @ApiModelProperty("产品ID")
    private Long productId;

    /** 设备ID */
    @ApiModelProperty("设备ID")
    private String deviceId;

    /** 通道ID */
    @ApiModelProperty("通道ID")
    private String channelId;

    /** 通道名称 */
    @ApiModelProperty("通道名称")
    private String channelName;

    /** 注册时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("注册时间")
    private Date registerTime;

    /** 设备类型 */
    @ApiModelProperty("设备类型")
    private String deviceType;

    /** 通道类型 */
    @ApiModelProperty("通道类型")
    private String channelType;

    /** 流数据类型 */
    @ApiModelProperty("流数据类型")
    private Integer dataType;

    /** 城市编码 */
    @ApiModelProperty("城市编码")
    private String cityCode;

    /** 行政区域 */
    @ApiModelProperty("行政区域")
    private String civilCode;

    /** 厂商名称 */
    @ApiModelProperty("厂商名称")
    private String manufacture;

    /** 产品型号 */
    @ApiModelProperty("产品型号")
    private String model;

    /** 通道归属 */
    @ApiModelProperty("通道归属")
    private String owner;

    /** 通道口令 */
    @ApiModelProperty("通道口令")
    private String password;

    /** 父级id */
    @ApiModelProperty("父级id")
    private String parentId;

    /** 设备入网IP */
    @ApiModelProperty("设备入网IP")
    private String ipAddress;

    /** 设备接入端口号 */
    @ApiModelProperty("设备接入端口号")
    private Integer port;

    /** PTZ类型 */
    @ApiModelProperty("PTZ类型")
    private Long ptzType;

    /** PTZ类型描述字符串 */
    @ApiModelProperty("PTZ类型描述字符串")
    private String ptzTypeText;

    /** 设备状态（1-未激活，2-禁用，3-在线，4-离线） */
    @ApiModelProperty("设备状态")
    private Integer status;

    /** 设备经度 */
    @ApiModelProperty("设备经度")
    private BigDecimal longitude;

    /** 设备纬度 */
    @ApiModelProperty("设备纬度")
    private BigDecimal latitude;

    /** 流媒体ID */
    @ApiModelProperty("流媒体ID")
    private String streamId;

    /** 通道播放地址 */
    @ApiModelProperty("通道播放地址")
    private String playUrl;

    /** 流代理地址 */
    @ApiModelProperty("流代理地址")
    private String proxyUrl;

    /** 是否含有音频（1-有, 0-没有） */
    @ApiModelProperty("是否含有音频")
    private Integer hasAudio;

    /** 是否支持对讲（1-支持, 0-不支持） */
    @ApiModelProperty("是否支持对讲")
    private Long hasBroadcast;

    /** 设备控制配置 */
    @ApiModelProperty("设备控制配置")
    private String ctrlConfig;

    /** 拓展信息 */
    @ApiModelProperty("拓展信息")
    private String extendInfo;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    private String delFlag;

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
