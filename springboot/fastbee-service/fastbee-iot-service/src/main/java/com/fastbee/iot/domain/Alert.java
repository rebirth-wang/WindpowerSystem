package com.fastbee.iot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
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

/**
 * 设备告警对象 iot_alert
 *
 * @author zhuangpeng.li
 * @date 2024-11-12
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Alert", description = "设备告警 iot_alert")
@Data
@TableName("iot_alert" )
public class Alert extends BaseEntity{
    /** 告警ID */
    @TableId(value = "alert_id", type = IdType.AUTO)
    @ApiModelProperty("告警ID")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long alertId;

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

    /** 告警状态（1-启动，2-停止） */
    @ApiModelProperty("告警状态")
    @AiSemanticField(
            semanticName = "告警状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT,
            queryHint = "check-value-mapping"
    )
    private Integer status;

    /** 通知方式[1,2,3] */
    @ApiModelProperty("通知方式[1,2,3]")
    private String notify;

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

    /** 生成工单（1-是，0-否） */
    @ApiModelProperty("生成工单（1-是，0-否）")
    @AiSemanticField(
            semanticName = "生成工单",
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private Integer generateWorkOrder;

}
