package com.fastbee.system.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 字典类型翻译对象 sys_dict_type_translate
 *
 * @author zhuangpeng.li
 * @date 2025-01-10
 */

@ApiModel(value = "SysDictTypeTranslateVO", description = "字典类型翻译 sys_dict_type_translate")
@Data
public class SysDictTypeTranslateVO{
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
