package com.fastbee.scada.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.PageEntity;

/**
 * 图管理对象 scada_echart
 *
 * @author zhuangpeng.li
 * @date 2024-12-04
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ScadaEchartVO", description = "图管理 scada_echart")
@Data
public class ScadaEchartVO extends PageEntity {

    /** id */
    @Excel(name = "id")
    @ApiModelProperty("id")
    private Long id;

    /**  */
    @Excel(name = "")
    @ApiModelProperty("")
    private String guid;

    /** 图表名称 */
    @Excel(name = "图表名称")
    @ApiModelProperty("图表名称")
    private String echartName;

    /** 图表类别 */
    @Excel(name = "图表类别")
    @ApiModelProperty("图表类别")
    private String echartType;

    /** 图表内容 */
    @Excel(name = "图表内容")
    @ApiModelProperty("图表内容")
    private String echartData;

    /** 图表图片 */
    @Excel(name = "图表图片")
    @ApiModelProperty("图表图片")
    private String echartImgae;

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
    @Excel(name = "是否系统通用")
    @ApiModelProperty("是否系统通用")
    private Integer sysFlag;

    private String base64;

}
