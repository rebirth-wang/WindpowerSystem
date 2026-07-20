package com.fastbee.iot.domain;

import java.math.BigDecimal;

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
 * 产品固件对象 iot_firmware
 *
 * @author kerwincui
 * @date 2024-08-18
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Firmware" , description = "产品固件 iot_firmware" )
@Data
@TableName("iot_firmware" )
public class Firmware extends BaseEntity {

    /** 固件ID */
    @TableId(value = "firmware_id", type = IdType.AUTO)
    @ApiModelProperty("固件ID" )
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long firmwareId;


    /** 固件名称 */
    @ApiModelProperty("固件名称" )
    private String firmwareName;


    /** 1,二进制包升级2.http升级 */
    @ApiModelProperty("1,二进制包升级2.http升级" )
    @AiSemanticField(
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_firmware_type"
    )
    private Integer firmwareType;


    /** 产品ID */
    @ApiModelProperty("产品ID" )
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_firmware.product_id=iot_product.product_id;可通过 iot_product.product_id=iot_things_model.product_id 继续关联物模型"
    )
    private Long productId;


    /** 产品名称 */
    @ApiModelProperty("产品名称" )
    private String productName;


    /** 租户ID */
    @ApiModelProperty("租户ID" )
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "系统租户隔离字段，查询时自动追加租户过滤",
            queryHint = "auto-data-scope"
    )
    private Long tenantId;


    /** 租户名称 */
    @ApiModelProperty("租户名称" )
    private String tenantName;


    /** 是否系统通用（0-否，1-是） */
    @ApiModelProperty("是否系统通用" )
    @AiSemanticField(
            semanticName = "是否系统通用",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private Integer isSys;


    /** 是否最新版本（0-否，1-是） */
    @ApiModelProperty("是否最新版本" )
    @AiSemanticField(
            semanticName = "是否最新版本",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private Integer isLatest;


    /** 固件版本 */
    @ApiModelProperty("固件版本" )
    private BigDecimal version;


    /** 分包字节大小 */
    @ApiModelProperty("分包字节大小" )
    private Integer byteSize;


    /** 文件路径 */
    @ApiModelProperty("文件路径" )
    private String filePath;


    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志" )
    @TableLogic
    @AiSemanticField(
            semanticName = "删除标志",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private String delFlag;

}
