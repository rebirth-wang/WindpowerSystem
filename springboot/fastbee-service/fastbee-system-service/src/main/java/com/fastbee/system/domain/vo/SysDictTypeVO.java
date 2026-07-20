package com.fastbee.system.domain.vo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.PageEntity;

/**
 * 字典类型对象 sys_dict_type
 *
 * @author gx_ma
 * @date 2024-12-26
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysDictTypeVO", description = "字典类型 sys_dict_type")
@Data
public class SysDictTypeVO extends PageEntity {
    /** 代码生成区域 可直接覆盖**/
    /** 字典主键 */
    @Excel(name = "字典主键")
    @ApiModelProperty("字典主键")
    private Long dictId;

    /** 字典名称 */
    @Excel(name = "字典名称")
    @ApiModelProperty("字典名称")
    private String dictName;

    /** 字典类型 */
    @Excel(name = "字典类型")
    @ApiModelProperty("字典类型")
    private String dictType;

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
    @Deprecated
    private String language;

    /** 字典类型标签 */
    @ApiModelProperty("中文字典标签")
    private String dictName_zh_CN;

    /** 字典类型标签 */
    @ApiModelProperty("英文字典标签")
    private String dictName_en_US;

    @Setter
    @ApiModelProperty("请求参数")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;

    public Map<String, Object> getParams(){
        if (params == null){
            params = new HashMap<>();
        }
        return params;
    }
    /** 自定义代码区域 END**/
}
