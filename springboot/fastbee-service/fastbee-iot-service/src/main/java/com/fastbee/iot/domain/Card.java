package com.fastbee.iot.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 物联网卡对象 iot_card
 *
 * @author fastbee
 * @date 2025-11-13
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Card", description = "物联网卡 iot_card")
@Data
@TableName("iot_card" )
public class Card extends PageEntity {
    /** 主键 */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Long id;

    /** 接入号码 */
    @ApiModelProperty("接入号码")
    private String accessNumber;

    /** ICCID号 */
    @ApiModelProperty("ICCID号")
    private String iccid;

    /** IMSI号 */
    @ApiModelProperty("IMSI号")
    private String imsi;

    /** 卡号（手机号） */
    @ApiModelProperty("卡号")
    @AiSemanticField(
            semanticName = "卡号",
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private String msisdn;

    /** IMEI号 */
    @ApiModelProperty("IMEI号")
    private String imei;

    /** 运营商 */
    @ApiModelProperty("运营商")
    @AiSemanticField(
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_card_operator"
    )
    private String operator;

    /** 卡状态 */
    @ApiModelProperty("卡状态")
    @AiSemanticField(
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_card_status"
    )
    private Integer cardStatus;

    /** 总流量(MB) */
    @ApiModelProperty("总流量(MB)")
    private BigDecimal totalData;

    /** 已用流量(MB) */
    @ApiModelProperty("已用流量(MB)")
    private BigDecimal dataUsed;

    /** 剩余流量(MB) */
    @ApiModelProperty("剩余流量(MB)")
    private BigDecimal dataRemaining;

    /** 激活时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("开卡日期")
    private Date openDate;

    /** 激活时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("激活时间")
    private Date activateTime;

    /** 到期时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("到期时间")
    private Date expireTime;

    /** 停机时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("停机时间")
    private Date downTime;

    /** 设备ID */
    @ApiModelProperty("设备ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_card.device_id=iot_device.device_id"
    )
    private Long deviceId;

    /** 卡平台id */
    @ApiModelProperty("卡平台id")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long cardPlatformId;

    /** 流量套餐 */
    @ApiModelProperty("流量套餐")
    private String dataPlan;

    /** 流量告警阈值(%) */
    @ApiModelProperty("流量告警阈值(%)")
    private BigDecimal dataAlertThreshold;

    /** 通知用户 */
    @ApiModelProperty("通知用户")
    private String notifyUsers;

    /** 租户id */
    @ApiModelProperty("租户id")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "系统租户隔离字段，查询时自动追加租户过滤",
            queryHint = "auto-data-scope"
    )
    private Long tenantId;

    /** 租户名称 */
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 创建人 */
    @ApiModelProperty("创建人")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 更新人 */
    @ApiModelProperty("更新人")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /** 逻辑删除标识 */
    @ApiModelProperty("逻辑删除标识")
    @TableLogic
    @AiSemanticField(semanticType = AiSemanticType.ENUM)
    private Integer delFlag;

}
