package com.fastbee.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.PageEntity;

/**
 * 字典类型翻译对象 sys_dict_type_translate
 *
 * @author gx_ma
 * @date 2025-01-10
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysDictTypeTranslate", description = "字典类型翻译 sys_dict_type_translate")
@Data
@TableName("sys_dict_type_translate" )
public class SysDictTypeTranslate extends PageEntity {
    private static final long serialVersionUID=1L;

    /** ID */
    @TableId(value = "id")
    @ApiModelProperty("ID")
    private Long id;

    /** zh_CN */
    @ApiModelProperty("zh_CN")
    private String zhCn;

    /** en_US */
    @ApiModelProperty("en_US")
    private String enUs;

}
