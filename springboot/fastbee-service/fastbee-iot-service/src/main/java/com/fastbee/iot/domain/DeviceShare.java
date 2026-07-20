package com.fastbee.iot.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.BaseEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 设备分享对象 iot_device_share
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@TableName("iot_device_share")
public class DeviceShare extends BaseEntity {

    /** 设备id */
    @ApiModelProperty("设备ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_device_share.device_id=iot_device.device_id"
    )
    private Long deviceId;

    /** 用户id */
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_device_share.user_id=sys_user.user_id"
    )
    private Long userId;

    /** 手机 */
    @Excel(name = "手机")
    private String phonenumber;

    /** 用户物模型权限，多个以英文逗号分隔 */
    @Excel(name = "用户物模型权限，多个以英文逗号分隔")
    private String perms;

    /** 删除标志（0代表存在 2代表删除） */
    @TableLogic
    @AiSemanticField(
            semanticName = "删除标志",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private String delFlag;
}
