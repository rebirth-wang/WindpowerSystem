package com.fastbee.iot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.PageEntity;

/**
 * 物模型模板翻译对象 iot_things_model_template_translate
 *
 * @author fastbee
 * @date 2025-12-26
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ThingsModelTemplateTranslate", description = "物模型模板翻译 iot_things_model_template_translate")
@Data
@TableName("iot_things_model_template_translate" )
public class ThingsModelTemplateTranslate extends PageEntity {
    private static final long serialVersionUID=1L;

    /** ID */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("ID")
    private Long id;

    /** zh_CN */
    @ApiModelProperty("zh_CN")
    private String zhCn;

    /** en_US */
    @ApiModelProperty("en_US")
    private String enUs;


}
