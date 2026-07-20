package com.fastbee.iot.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 新闻资讯对象 news
 *
 * @author zhuangpeng.li
 * @date 2024-11-18
 */

@ApiModel(value = "NewsVO", description = "新闻资讯 news")
@Data
public class NewsVO{

    /** 新闻ID */
    @Excel(name = "新闻ID")
    @ApiModelProperty("新闻ID")
    private Long newsId;

    /** 标题 */
    @Excel(name = "标题")
    @ApiModelProperty("标题")
    private String title;

    /** 内容 */
    @Excel(name = "内容")
    @ApiModelProperty("内容")
    private String content;

    /** 封面 */
    @Excel(name = "封面")
    @ApiModelProperty("封面")
    private String imgUrl;

    /** 是否置顶(0-置顶 1-置顶) */
    @Excel(name = "是否置顶(0-置顶 1-置顶)")
    @ApiModelProperty("是否置顶(0-置顶 1-置顶)")
    private Long isTop;

    /** 是否banner(0-是banner 1-不是banner) */
    @Excel(name = "是否banner(0-是banner 1-不是banner)")
    @ApiModelProperty("是否banner(0-是banner 1-不是banner)")
    private Long isBanner;

    /** 分类ID */
    @Excel(name = "分类ID")
    @ApiModelProperty("分类ID")
    private Long categoryId;

    /** 分类名称 */
    @Excel(name = "分类名称")
    @ApiModelProperty("分类名称")
    private String categoryName;

    /** 新闻状态（0-未发布，1-已发布） */
    @ApiModelProperty("新闻状态")
    @Excel(name = "新闻状态")
    private Long status;

    /** 作者 */
    @Excel(name = "作者")
    @ApiModelProperty("作者")
    private String author;

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
