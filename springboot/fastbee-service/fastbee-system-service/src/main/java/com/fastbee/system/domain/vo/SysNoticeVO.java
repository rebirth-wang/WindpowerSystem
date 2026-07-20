package com.fastbee.system.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 通知公告对象 sys_notice
 *
 * @author zhuangpeng.li
 * @date 2024-11-26
 */

@ApiModel(value = "SysNoticeVO", description = "通知公告 sys_notice")
@Data
public class SysNoticeVO{

    /** 公告ID */
    @Excel(name = "公告ID")
    @ApiModelProperty("公告ID")
    private Long noticeId;

    /** 公告标题 */
    @Excel(name = "公告标题")
    @ApiModelProperty("公告标题")
    private String noticeTitle;

    /** 公告类型（1通知 2公告） */
    @ApiModelProperty("公告类型")
    @Excel(name = "公告类型")
    private String noticeType;

    /** 公告内容 */
    @Excel(name = "公告内容")
    @ApiModelProperty("公告内容")
    private String noticeContent;

    /** 公告状态（0正常 1关闭） */
    @ApiModelProperty("公告状态")
    @Excel(name = "公告状态")
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

}
