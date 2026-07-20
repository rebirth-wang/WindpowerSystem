package com.fastbee.iot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.BaseEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;
import com.fastbee.iot.util.KingbaseJsonTypeHandler;

/**
 * 设备告警日志对象 iot_alert_log
 *
 * @author zhuangpeng.li
 * @date 2024-11-12
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "AlertLog", description = "设备告警日志 iot_alert_log")
@Data
@TableName("iot_alert_log" )
public class AlertLog extends BaseEntity{
    /** 告警日志ID */
    @TableId(value = "alert_log_id", type = IdType.AUTO)
    @ApiModelProperty("告警日志ID")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long alertLogId;

    /** 告警名称 */
    @ApiModelProperty("告警名称")
    private String alertName;

    /** 告警级别（1=提醒通知，2=轻微问题，3=严重警告） */
    @ApiModelProperty("告警级别")
    @AiSemanticField(
            semanticName = "告警级别",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_alert_level"
    )
    private Long alertLevel;

    /** 处理状态(1=不需要处理,2=未处理,3=已处理) */
    @ApiModelProperty("处理状态(1=不需要处理,2=未处理,3=已处理)")
    @AiSemanticField(
            semanticName = "处理状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_process_status",
            queryHint = "check-value-mapping"
    )
    private Integer status;

    /** 设备编号 */
    @ApiModelProperty("设备编号")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_alert_log.serial_number=iot_device.serial_number;可通过 iot_device.product_id=iot_product.product_id 继续关联产品",
            queryHint = "device-number"
    )
    private String serialNumber;

    /** 产品ID */
    @ApiModelProperty("产品ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_alert_log.product_id=iot_product.product_id;可通过 iot_product.product_id=iot_things_model.product_id 继续关联物模型"
    )
    private Long productId;

    /** 告警详情（对应物模型） */
    @TableField(typeHandler = KingbaseJsonTypeHandler.class)
    @ApiModelProperty("告警详情")
    @AiSemanticField(
            semanticName = "告警详情",
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private String detail;

    /** 用户id */
    @ApiModelProperty("用户id")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_alert_log.user_id=sys_user.user_id"
    )
    private Long userId;

    /** 设备名称 */
    @ApiModelProperty("设备名称")
    private String deviceName;

    /**
     * 场景id
     */
    @ApiModelProperty("场景id")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long sceneId;

}
