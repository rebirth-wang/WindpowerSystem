package com.fastbee.iot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
 * 产品授权码对象 iot_product_authorize
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ProductAuthorize", description = "产品授权码 iot_product_authorize")
@Data
@TableName("iot_product_authorize" )
public class ProductAuthorize extends BaseEntity{
    private static final long serialVersionUID=1L;

    /** 授权码ID */
    @TableId(value = "authorize_id", type = IdType.AUTO)
    @ApiModelProperty("授权码ID")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long authorizeId;

    /** 授权码 */
    @ApiModelProperty("授权码")
    private String authorizeCode;

    /** 产品ID */
    @ApiModelProperty("产品ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_product_authorize.product_id=iot_product.product_id;可通过 iot_product.product_id=iot_things_model.product_id 继续关联物模型"
    )
    private Long productId;

    /** 设备ID */
    @ApiModelProperty("设备ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_product_authorize.device_id=iot_device.device_id"
    )
    private Long deviceId;

    /** 设备编号 */
    @ApiModelProperty("设备编号")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_product_authorize.serial_number=iot_device.serial_number;可通过 iot_device.product_id=iot_product.product_id 继续关联产品",
            queryHint = "device-number"
    )
    private String serialNumber;

    /** 用户ID */
    @ApiModelProperty("用户ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_product_authorize.user_id=sys_user.user_id"
    )
    private Long userId;

    /** 用户名称 */
    @ApiModelProperty("用户名称")
    private String userName;

    /** 状态（1-未使用，2-使用中） */
    @ApiModelProperty("状态")
    @AiSemanticField(
            semanticName = "状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_auth_status",
            queryHint = "check-value-mapping"
    )
    private Integer status;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic
    @AiSemanticField(
            semanticName = "删除标志",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private String delFlag;

    public ProductAuthorize() {
    }

    public ProductAuthorize(String authorizeCode, Long productId) {
        this.authorizeCode = authorizeCode;
        this.productId = productId;
    }

}
