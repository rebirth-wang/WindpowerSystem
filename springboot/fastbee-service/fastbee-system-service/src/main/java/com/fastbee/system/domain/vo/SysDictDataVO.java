package com.fastbee.system.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.PageEntity;

/**
 * 字典数据对象 sys_dict_data
 *
 * @author gx_ma
 * @date 2024-12-26
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysDictDataVO", description = "字典数据 sys_dict_data")
@Data
public class SysDictDataVO extends PageEntity {
    /** 代码生成区域 可直接覆盖**/
    /** 字典编码 */
    @Excel(name = "字典编码")
    @ApiModelProperty("字典编码")
    private Long dictCode;

    /** 字典排序 */
    @Excel(name = "字典排序")
    @ApiModelProperty("字典排序")
    private Long dictSort;

    /** 字典标签 */
    @Excel(name = "字典标签")
    @ApiModelProperty("字典标签")
    private String dictLabel;

    /** 字典键值 */
    @Excel(name = "字典键值")
    @ApiModelProperty("字典键值")
    private String dictValue;

    /** 字典类型 */
    @Excel(name = "字典类型")
    @ApiModelProperty("字典类型")
    private String dictType;

    /** 样式属性（其他样式扩展） */
    @ApiModelProperty("样式属性")
    @Excel(name = "样式属性")
    private String cssClass;

    /** 表格回显样式 */
    @Excel(name = "表格回显样式")
    @ApiModelProperty("表格回显样式")
    private String listClass;

    /** 是否默认（Y是 N否） */
    @ApiModelProperty("是否默认")
    @Excel(name = "是否默认")
    private String isDefault;

    /** 状态（0正常 1停用） */
    @ApiModelProperty("状态")
    @Excel(name = "状态")
    private Integer status;

    /** 创建者 */
    @Excel(name = "创建者")
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    /** 更新者 */
    @Excel(name = "更新者")
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    @Excel(name = "更新时间")
    private Date updateTime;

    /** 备注 */
    @Excel(name = "备注")
    @ApiModelProperty("备注")
    private String remark;


    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/

    /** 字典标签 */
    @ApiModelProperty("中文字典标签")
    private String dictLabel_zh_CN;

    /** 字典标签 */
    @ApiModelProperty("英文字典标签")
    private String dictLabel_en_US;

    @Deprecated
    private String language;

    /** 自定义代码区域 END**/
}
