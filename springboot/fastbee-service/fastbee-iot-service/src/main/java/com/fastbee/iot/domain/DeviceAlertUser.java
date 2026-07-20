package com.fastbee.iot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 设备告警用户对象 iot_device_alert_user
 *
 * @author zhuangpeng.li
 * @date 2024-11-12
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "DeviceAlertUser", description = "设备告警用户 iot_device_alert_user")
@Data
@TableName("iot_device_alert_user" )
public class DeviceAlertUser extends PageEntity{
    /** 设备id */
    @TableId(value = "device_id", type = IdType.AUTO)
    @ApiModelProperty("设备id")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_device_alert_user.device_id=iot_device.device_id"
    )
    private Long deviceId;

    /** 用户id */
    @ApiModelProperty("用户id")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_device_alert_user.user_id=sys_user.user_id"
    )
    private Long userId;

}
