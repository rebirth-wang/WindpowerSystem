package com.fastbee.iot.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 产品扩展参数对象 iot_product_ext_param
 *
 * @author fastbee
 * @date 2026-03-18
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ProductExtParam", description = "产品扩展参数 iot_product_ext_param")
@Data
@TableName("iot_product_ext_param" )
public class ProductExtParam extends PageEntity {
    private static final long serialVersionUID=1L;

    /** 参数ID */
    @TableId(value = "param_id", type = IdType.AUTO)
    @ApiModelProperty("参数ID")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long paramId;

    /** 产品ID */
    @ApiModelProperty("产品ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_product_ext_param.product_id=iot_product.product_id;可通过 iot_product.product_id=iot_things_model.product_id 继续关联物模型"
    )
    private Long productId;

    /** 参数名称 */
    @ApiModelProperty("参数名称")
    private String paramName;

    /** 参数类型 */
    @ApiModelProperty("参数类型")
    @AiSemanticField(semanticType = AiSemanticType.ENUM)
    private String paramType;

    /** 默认值 */
    @ApiModelProperty("默认值")
    private String defaultValue;

    /** 是否启用(0-否,1-是) */
    @ApiModelProperty("是否启用(0-否,1-是)")
    @AiSemanticField(
            semanticName = "是否启用",
            semanticType = AiSemanticType.ENUM
    )
    private Integer isEnabled;

    /** 是否在APP显示(0-否,1-是) */
    @ApiModelProperty("是否在APP显示(0-否,1-是)")
    @AiSemanticField(
            semanticName = "是否在APP显示",
            semanticType = AiSemanticType.ENUM
    )
    private Integer isAppShow;

    /** 规格 */
    @ApiModelProperty("规格")
    private String spec;

    /** 描述 */
    @ApiModelProperty("描述")
    private String description;

    /** 删除标志(0-存在,1-删除) */
    @ApiModelProperty("删除标志(0-存在,1-删除)")
    @AiSemanticField(
            semanticName = "删除标志",
            semanticType = AiSemanticType.ENUM
    )
    private Integer delFlag;

    /** 创建者 */
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 更新者 */
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

}
