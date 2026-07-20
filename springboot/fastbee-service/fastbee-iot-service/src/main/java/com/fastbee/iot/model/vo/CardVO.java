package com.fastbee.iot.model.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 物联网卡对象 iot_card
 *
 * @author fastbee
 * @date 2025-11-13
 */

@ApiModel(value = "CardVO", description = "物联网卡 iot_card")
@Data
public class CardVO{
    /** 代码生成区域 可直接覆盖**/
    /** 主键 */
    @Excel(name = "主键")
    @ApiModelProperty("主键")
    private Long id;

    /** 接入号码 */
    @ApiModelProperty("接入号码")
    private String accessNumber;

    /** ICCID号 */
    @Excel(name = "ICCID号")
    @ApiModelProperty("ICCID号")
    private String iccid;

    /** IMSI号 */
    @Excel(name = "IMSI号")
    @ApiModelProperty("IMSI号")
    private String imsi;

    /** 卡号（手机号） */
    @ApiModelProperty("卡号")
    @Excel(name = "卡号")
    private String msisdn;

    /** IMEI号 */
    @ApiModelProperty("IMEI号")
    private String imei;

    /** 运营商 */
    @Excel(name = "运营商")
    @ApiModelProperty("运营商")
    private String operator;

    /** 卡状态 */
    @Excel(name = "卡状态")
    @ApiModelProperty("卡状态")
    private Integer cardStatus;

    /** 总流量(MB) */
    @Excel(name = "总流量(MB)")
    @ApiModelProperty("总流量(MB)")
    private BigDecimal totalData;

    /** 已用流量(MB) */
    @Excel(name = "已用流量(MB)")
    @ApiModelProperty("已用流量(MB)")
    private BigDecimal dataUsed;

    /** 剩余流量(MB) */
    @Excel(name = "剩余流量(MB)")
    @ApiModelProperty("剩余流量(MB)")
    private BigDecimal dataRemaining;

    /** 激活时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("开卡日期")
    private Date openDate;

    /** 激活时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("激活时间")
    @Excel(name = "激活时间")
    private Date activateTime;

    /** 到期时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("到期时间")
    @Excel(name = "到期时间")
    private Date expireTime;

    /** 停机时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("停机时间")
    @Excel(name = "停机时间")
    private Date downTime;

    /** 设备ID */
    @Excel(name = "设备ID")
    @ApiModelProperty("设备ID")
    private Long deviceId;

    /** 卡平台id */
    @Excel(name = "卡平台id")
    @ApiModelProperty("卡平台id")
    private Long cardPlatformId;

    /** 流量告警阈值(%) */
    @Excel(name = "流量告警阈值(%)")
    @ApiModelProperty("流量告警阈值(%)")
    private BigDecimal dataAlertThreshold;

    /** 流量套餐 */
    @ApiModelProperty("流量套餐")
    private String dataPlan;

    /** 租户id */
    @Excel(name = "租户id")
    @ApiModelProperty("租户id")
    private Long tenantId;

    /** 租户名称 */
    @Excel(name = "租户名称")
    @ApiModelProperty("租户名称")
    private String tenantName;

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

    /** 逻辑删除标识 */
    @Excel(name = "逻辑删除标识")
    @ApiModelProperty("逻辑删除标识")
    private Integer delFlag;

    /** 设备名称 */
    @Excel(name = "设备名称")
    @ApiModelProperty("设备名称")
    private String deviceName;

    /** 产品ID */
    @Excel(name = "产品ID")
    @ApiModelProperty("产品ID")
    private Long productId;

    /** 产品名称 */
    @Excel(name = "产品名称")
    @ApiModelProperty("产品名称")
    private String productName;

    /** 设备编号 */
    @Excel(name = "设备编号")
    @ApiModelProperty("设备编号")
    private String serialNumber;

    /** 通知用户 */
    @ApiModelProperty("通知用户")
    private String notifyUsers;

    /**
     * 是否报警
     */
    private Boolean alertFlag;

    /**
     * 流量占比
     */
    private BigDecimal trafficProportion;

    /**
     * 平台
     */
    private String platform;

    private String cardPlatformName;

}
