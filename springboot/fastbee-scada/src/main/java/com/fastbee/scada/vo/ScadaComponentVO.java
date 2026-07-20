package com.fastbee.scada.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.PageEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 组态组件对象 scada_component
 *
 * @author zhuangpeng.li
 * @date 2024-12-04
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ScadaComponentVO", description = "组态组件 scada_component")
@Data
public class ScadaComponentVO extends PageEntity {

    /** 主键id */
    @Excel(name = "主键id")
    @ApiModelProperty("主键id")
    private Long id;

    /** 组件名称 */
    @Excel(name = "组件名称")
    @ApiModelProperty("组件名称")
    private String componentName;

    /** 组件模版 */
    @Excel(name = "组件模版")
    @ApiModelProperty("组件模版")
    private String componentTemplate;

    /** 组件风格 */
    @Excel(name = "组件风格")
    @ApiModelProperty("组件风格")
    private String componentStyle;

    /** 组件脚本 */
    @Excel(name = "组件脚本")
    @ApiModelProperty("组件脚本")
    private String componentScript;

    /** 组件缩略图 */
    @Excel(name = "组件缩略图")
    @ApiModelProperty("组件缩略图")
    private String componentImage;

    /** 租户id */
    @Excel(name = "租户id")
    @ApiModelProperty("租户id")
    private Long tenantId;

    /** 租户名称 */
    @Excel(name = "租户名称")
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 创建人 */
    @Excel(name = "创建人")
    @ApiModelProperty("创建人")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    /** 更新人 */
    @Excel(name = "更新人")
    @ApiModelProperty("更新人")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    @Excel(name = "更新时间")
    private Date updateTime;

    /** 逻辑删除标识 */
    @Excel(name = "逻辑删除标识")
    @ApiModelProperty("逻辑删除标识")
    private Long delFlag;

    /** 是否系统通用（0-否，1-是） */
    @ApiModelProperty("是否系统通用")
    @Excel(name = "是否系统通用")
    private Integer sysFlag;

    /** 用户id */
    @Excel(name = "用户id")
    @ApiModelProperty("用户id")
    private Long userId;

    /** 转图片参数 */
    private String base64;

}
