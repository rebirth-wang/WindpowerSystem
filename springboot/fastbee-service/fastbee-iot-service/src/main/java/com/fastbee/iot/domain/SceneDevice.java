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
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 场景设备对象 iot_scene_device
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SceneDevice", description = "场景设备 iot_scene_device")
@Data
@TableName("iot_scene_device" )
public class SceneDevice extends PageEntity{
    private static final long serialVersionUID=1L;

    /** 场景设备ID */
    @TableId(value = "scene_device_id", type = IdType.AUTO)
    @ApiModelProperty("场景设备ID")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long sceneDeviceId;

    /** 设备编号（产品触发的没有设备编号） */
    @ApiModelProperty("设备编号")
    @AiSemanticField(
            semanticName = "设备编号",
            semanticType = AiSemanticType.RELATION_KEY,
            sourceType = AiSemanticSourceType.AUTO_COMMENT,
            relationHint = "iot_scene_device.serial_number=iot_device.serial_number;可通过 iot_device.product_id=iot_product.product_id 继续关联产品",
            queryHint = "device-number"
    )
    private String serialNumber;

    /** 产品ID */
    @ApiModelProperty("产品ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_scene_device.product_id=iot_product.product_id;可通过 iot_product.product_id=iot_things_model.product_id 继续关联物模型"
    )
    private Long productId;

    /** 产品名称 */
    @ApiModelProperty("产品名称")
    private String productName;

    /** 触发源（1=设备触发，3=产品触发） */
    @ApiModelProperty("触发源")
    @AiSemanticField(
            semanticName = "触发源",
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private Integer source;

    /** 场景ID */
    @ApiModelProperty("场景ID")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long sceneId;

    /** 场景脚本ID */
    @ApiModelProperty("场景脚本ID")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private String scriptId;

    /** 类型（2=触发器，3=执行动作） */
    @ApiModelProperty("类型")
    @AiSemanticField(
            semanticName = "类型",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private Integer type;

}
