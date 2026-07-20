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
 * 参数配置对象 sys_config
 *
 * @author zhuangpeng.li
 * @date 2024-12-12
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysConfigVO", description = "参数配置 sys_config")
@Data
public class SysConfigVO extends PageEntity {
    /** 代码生成区域 可直接覆盖**/
    /** 参数主键 */
    @Excel(name = "参数主键")
    @ApiModelProperty("参数主键")
    private Long configId;

    /** 参数名称 */
    @Excel(name = "参数名称")
    @ApiModelProperty("参数名称")
    private String configName;

    /** 参数键名 */
    @Excel(name = "参数键名")
    @ApiModelProperty("参数键名")
    private String configKey;

    /** 参数键值 */
    @Excel(name = "参数键值")
    @ApiModelProperty("参数键值")
    private String configValue;

    /** 系统内置（Y是 N否） */
    @ApiModelProperty("系统内置")
    @Excel(name = "系统内置")
    private String configType;

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


    /** 自定义代码区域 END**/
}
