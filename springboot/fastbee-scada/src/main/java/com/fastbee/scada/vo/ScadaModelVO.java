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
 * 三维配置对象 scada_model
 *
 * @author zhuangpeng.li
 * @date 2024-12-04
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ScadaModelVO", description = "三维配置 scada_model")
@Data
public class ScadaModelVO extends PageEntity {

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private Long id;

    /** 模型名称 */
    @Excel(name = "模型名称")
    @ApiModelProperty("模型名称")
    private String modelName;

    /** 模型地址 */
    @Excel(name = "模型地址")
    @ApiModelProperty("模型地址")
    private String modelUrl;

    /** 是否弃用 */
    @Excel(name = "是否弃用")
    @ApiModelProperty("是否弃用")
    private Long status;

    /** 缩略图url */
    @Excel(name = "缩略图url")
    @ApiModelProperty("缩略图url")
    private String imageUrl;

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
    private Integer sysFlag;

    /** 模型类型 */
    @ApiModelProperty("模型类型")
    private String modelType;

}
