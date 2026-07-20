package com.fastbee.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.PageEntity;

/**
 * 菜单名称翻译对象 sys_menu_translate
 *
 * @author fastbee
 * @date 2025-12-26
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysMenuTranslate", description = "菜单名称翻译 sys_menu_translate")
@Data
@TableName("sys_menu_translate" )
public class SysMenuTranslate extends PageEntity {
    private static final long serialVersionUID=1L;

    /** 菜单ID */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("菜单ID")
    private Long id;

    /** zh_CN菜单名称 */
    @ApiModelProperty("zh_CN菜单名称")
    private String zhCn;

    /** en菜单名称 */
    @ApiModelProperty("en菜单名称")
    private String enUs;


}
