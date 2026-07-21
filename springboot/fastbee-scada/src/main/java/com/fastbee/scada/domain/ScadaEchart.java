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
 * 图管理对象 scada_echart
 *
 * @author zhuangpeng.li
 * @date 2024-12-04
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ScadaEchart", description = "图管理 scada_echart")
@Data
@TableName("scada_echart" )
public class ScadaEchart extends PageEntity implements Serializable{
    private static final long serialVersionUID=1L;

    /** id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("id")
    private Long id;

    /**  */
    @ApiModelProperty("")
    private String guid;

    /** 图表名称 */
    @ApiModelProperty("图表名称")
    private String echartName;

    /** 图表类别 */
    @ApiModelProperty("图表类别")
    private String echartType;

    /** 图表内容 */
    @ApiModelProperty("图表内容")
    private String echartData;

    /** 图表图片 */
    @ApiModelProperty("图表图片")
    private String echartImgae;

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

}
