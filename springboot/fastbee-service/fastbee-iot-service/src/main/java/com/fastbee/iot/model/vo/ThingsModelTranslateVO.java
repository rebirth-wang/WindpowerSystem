package com.fastbee.iot.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 物模型翻译对象 iot_things_model_translate
 *
 * @author fastbee
 * @date 2025-12-26
 */

@ApiModel(value = "ThingsModelTranslateVO", description = "物模型翻译 iot_things_model_translate")
@Data
public class ThingsModelTranslateVO{
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

    /** 产品ID */
    @Excel(name = "产品ID")
    @ApiModelProperty("产品ID")
    private Long productId;


    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
