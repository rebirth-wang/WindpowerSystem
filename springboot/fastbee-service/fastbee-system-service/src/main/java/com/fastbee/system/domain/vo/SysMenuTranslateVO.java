package com.fastbee.system.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 菜单名称翻译对象 sys_menu_translate
 *
 * @author fastbee
 * @date 2025-12-26
 */

@ApiModel(value = "SysMenuTranslateVO", description = "菜单名称翻译 sys_menu_translate")
@Data
public class SysMenuTranslateVO{
    /** 代码生成区域 可直接覆盖**/
    /** 菜单ID */
    @Excel(name = "菜单ID")
    @ApiModelProperty("菜单ID")
    private Long id;

    /** zh_CN菜单名称 */
    @Excel(name = "zh_CN菜单名称")
    @ApiModelProperty("zh_CN菜单名称")
    private String zhCn;

    /** en菜单名称 */
    @Excel(name = "en菜单名称")
    @ApiModelProperty("en菜单名称")
    private String enUs;


    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
