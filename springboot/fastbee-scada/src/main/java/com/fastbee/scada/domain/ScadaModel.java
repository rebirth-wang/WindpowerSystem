package com.fastbee.scada.domain;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.PageEntity;

/**
 * 三维配置对象 scada_model
 *
 * @author zhuangpeng.li
 * @date 2024-12-04
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ScadaModel", description = "三维配置 scada_model")
@Data
@TableName("scada_model" )
public class ScadaModel extends PageEntity implements Serializable{
    private static final long serialVersionUID=1L;

    /**  */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("")
    private Long id;

    /** 模型名称 */
    @ApiModelProperty("模型名称")
    private String modelName;

    /** 模型地址 */
    @ApiModelProperty("模型地址")
    private String modelUrl;

    /** 是否弃用 */
    @ApiModelProperty("是否弃用")
    private Long status;

    /** 缩略图url */
    @ApiModelProperty("缩略图url")
    private String imageUrl;

    /** 租户id */
    @ApiModelProperty("租户id")
    private Long tenantId;

    /** 租户名称 */
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 创建人 */
    @ApiModelProperty("创建人")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 更新人 */
    @ApiModelProperty("更新人")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /** 逻辑删除标识 */
    @ApiModelProperty("逻辑删除标识")
    private Long delFlag;

    /** 是否系统通用（0-否，1-是） */
    @ApiModelProperty("是否系统通用")
    private Integer sysFlag;

    /** 模型类型 */
    @ApiModelProperty("模型类型")
    private String modelType;

}
