package com.fastbee.oss.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 文件记录对象 oss_detail
 *
 * @author zhuangpeng.li
 * @date 2024-11-19
 */

@ApiModel(value = "OssDetailVO", description = "文件记录 oss_detail")
@Data
public class OssDetailVO{

    /** 文件id */
    @Excel(name = "文件id")
    @ApiModelProperty("文件id")
    private Long id;

    /** 租户ID */
    @Excel(name = "租户ID")
    @ApiModelProperty("租户ID")
    private Long tenantId;

    /** 租户名称 */
    @Excel(name = "租户名称")
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 文件名 */
    @Excel(name = "文件名")
    @ApiModelProperty("文件名")
    private String fileName;

    /** 原名 */
    @Excel(name = "原名")
    @ApiModelProperty("原名")
    private String originalName;

    /** 文件后缀名 */
    @Excel(name = "文件后缀名")
    @ApiModelProperty("文件后缀名")
    private String fileSuffix;

    /** URL地址 */
    @Excel(name = "URL地址")
    @ApiModelProperty("URL地址")
    private String url;

    /** 服务商 */
    @Excel(name = "服务商")
    @ApiModelProperty("服务商")
    private String service;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @Excel(name = "删除标志")
    private String delFlag;

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


}
