package com.fastbee.iot.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.BaseEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 产品对象 iot_product
 *
 * @author zhuangpeng.li
 * @date 2024-12-20
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Product", description = "产品 iot_product")
@Data
@TableName("iot_product" )
public class Product extends BaseEntity{
    private static final long serialVersionUID=1L;

    /** 产品ID */
    @TableId(value = "product_id", type = IdType.AUTO)
    @ApiModelProperty("产品ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_product.product_id=dueros_related_product.product_id;可通过 iot_product.product_id=iot_things_model.product_id 继续关联物模型"
    )
    private Long productId;

    /** 产品名称 */
    @ApiModelProperty("产品名称")
    private String productName;

    /** 协议编号 */
    @ApiModelProperty("协议编号")
    private String protocolCode;

    /** 产品分类ID */
    @ApiModelProperty("产品分类ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_product.category_id=iot_category.category_id"
    )
    private Long categoryId;

    /** 产品分类名称 */
    @ApiModelProperty("产品分类名称")
    private String categoryName;

    /** 租户ID */
    @ApiModelProperty("租户ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "系统租户隔离字段，查询时自动追加租户过滤",
            queryHint = "auto-data-scope"
    )
    private Long tenantId;

    /** 租户名称 */
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 是否系统通用（0-否，1-是） */
    @ApiModelProperty("是否系统通用")
    @AiSemanticField(
            semanticName = "是否系统通用",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_yes_no"
    )
    private Integer isSys;

    /** 是否启用授权码（0-否，1-是） */
    @ApiModelProperty("是否启用授权码")
    @AiSemanticField(
            semanticName = "是否启用授权码",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "authorization_code_active",
            valueMappings = {"0=否，1=是"}
    )
    private Integer isAuthorize;

    /** mqtt账号 */
    @ApiModelProperty("认证账号")
    private String account;

    /** mqtt密码 */
    @ApiModelProperty("认证密码")
    private String authPassword;

    /** 产品秘钥 */
    @ApiModelProperty("产品认证秘钥")
    private String secret;

    /** 状态（1-未发布，2-已发布） */
    @ApiModelProperty("状态")
    @AiSemanticField(
            semanticName = "状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_product_status",
            queryHint = "check-value-mapping"
    )
    private Integer status;

    /** 物模型JSON（属性、功能、事件） */
    @ApiModelProperty("物模型JSON")
    private String thingsModelsJson;

    /** 设备类型（1-直连设备、2-网关设备、3-监控设备） */
    @ApiModelProperty("设备类型")
    @AiSemanticField(
            semanticName = "设备类型",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_device_type"
    )
    private Integer deviceType;

    /** 联网方式（1=wifi、2=蜂窝(2G/3G/4G/5G)、3=以太网、4=其他） */
    @ApiModelProperty("联网方式")
    @AiSemanticField(
            semanticName = "联网方式",
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_network_method",
            relationHint = "device_type=4时DICT为sub_gateway_type"
    )
    private Integer networkMethod;

    /** 认证方式（1-简单认证、2-加密认证、3-简单+加密） */
    @ApiModelProperty("认证方式")
    @AiSemanticField(
            semanticName = "认证方式",
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_vertificate_method"
    )
    private Integer vertificateMethod;

    /** 图片地址 */
    @ApiModelProperty("图片地址")
    private String imgUrl;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic
    @AiSemanticField(
            semanticName = "删除标志",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private String delFlag;

    /** 产品支持的传输协议 */
    @ApiModelProperty("产品支持的传输协议")
    @AiSemanticField(
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_transport_type"
    )
    private String transport;

    /** 定位方式(1=ip自动定位，2=设备定位，3=自定义) */
    @ApiModelProperty("定位方式(1=ip自动定位，2=设备定位，3=自定义)")
    @AiSemanticField(
            semanticName = "定位方式",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_location_way"
    )
    private Integer locationWay;

    /** 产品关联的组态id */
    @ApiModelProperty("产品关联的组态id")
    private String guid;

    /**
     * 固件类型(1-二进制包升级，2-http升级)
     */
    @ApiModelProperty("产品固件类型")
    @AiSemanticField(
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_firmware_type"
    )
    private Integer firmwareType;

    /**
     * 面板启用(0-停用，1-启用)
     */
    @ApiModelProperty("面板启用")
    private Integer panelEnable;

    /**
     * 拖拽面板json数据
     */
    @ApiModelProperty("拖拽面板json数据")
    private String panelModelsJson;

    /**
     * 物模型
     */
    @TableField(exist = false)
    private String model;

}
