package com.fastbee.iot.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 项目对象 iot_goview_project
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */

@ApiModel(value = "GoviewProjectVO", description = "项目 iot_goview_project")
@Data
public class GoviewProjectVO{

    /** 主键 */
    @Excel(name = "主键")
    @ApiModelProperty("主键")
    private String id;

    /** 项目名称 */
    @Excel(name = "项目名称")
    @ApiModelProperty("项目名称")
    private String projectName;

    /** 项目状态[0未发布,1发布] */
    @Excel(name = "项目状态[0未发布,1发布]")
    @ApiModelProperty("项目状态[0未发布,1发布]")
    private Integer state;

    /** 首页图片 */
    @Excel(name = "首页图片")
    @ApiModelProperty("首页图片")
    private String indexImage;

    /** 删除状态[1删除,-1未删除] */
    @Excel(name = "删除状态[1删除,-1未删除]")
    @ApiModelProperty("删除状态[1删除,-1未删除]")
    private Long delFlag;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    /** 创建人id */
    @Excel(name = "创建人id")
    @ApiModelProperty("创建人id")
    private String createBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    @Excel(name = "更新时间")
    private Date updateTime;

    /** 项目介绍 */
    @Excel(name = "项目介绍")
    @ApiModelProperty("项目介绍")
    private String remarks;

    /** 租户id */
    @Excel(name = "租户id")
    @ApiModelProperty("租户id")
    private Long tenantId;

    /** 租户名称 */
    @Excel(name = "租户名称")
    @ApiModelProperty("租户名称")
    private String tenantName;


}
