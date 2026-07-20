package com.fastbee.iot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 指令偏好设置对象 command_preferences
 *
 * @author kerwincui
 * @date 2024-06-29
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "CommandPreferences", description = "指令偏好设置 command_preferences")
@Data
@TableName("command_preferences")
public class CommandPreferences extends PageEntity {

    /**
     * 指令id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 指令名称
     */
    @Excel(name = "指令名称")
    @ApiModelProperty("指令名称")
    private String name;

    /**
     * 指令
     */
    @Excel(name = "指令")
    @ApiModelProperty("指令")
    private String command;

    /**
     * 设备编号
     */
    @Excel(name = "设备编号")
    @ApiModelProperty("设备编号")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "command_preferences.serial_number=iot_device.serial_number;可通过 iot_device.product_id=iot_product.product_id 继续关联产品",
            queryHint = "device-number"
    )
    private String serialNumber;

}
