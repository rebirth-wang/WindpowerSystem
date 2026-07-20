package com.fastbee.iot.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 物模型模板翻译对象 iot_things_model_template_translate
 *
 * @author fastbee
 * @date 2025-12-26
 */

@ApiModel(value = "ThingsModelTemplateTranslateVO", description = "物模型模板翻译 iot_things_model_template_translate")
@Data
public class ThingsModelTemplateTranslateVO{
    /** 代码生成区域 可直接覆盖**/
    /** ID */
    @Excel(name = "ID")
    @ApiModelProperty("ID")
    private Long id;

    /** zh_CN */
    @Excel(name = "zh_CN")
    @ApiModelProperty("zh_CN")
    private String zhCn;

    /** en_US */
    @Excel(name = "en_US")
    @ApiModelProperty("en_US")
    private String enUs;


    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
