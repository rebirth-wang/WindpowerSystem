package com.fastbee.iot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.BaseEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 新闻资讯对象 news
 *
 * @author kerwincui
 * @date 2022-04-09
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "News", description = "新闻资讯对象 news")
@Data
@TableName("news" )
public class News extends BaseEntity {
    /** 新闻ID */
    @TableId(value = "news_id", type = IdType.AUTO)
    @ApiModelProperty("新闻ID")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long newsId;

    /** 标题 */
    @ApiModelProperty("标题")
    private String title;

    /** 内容 */
    @ApiModelProperty("内容")
    private String content;

    /** 封面 */
    @ApiModelProperty("封面")
    private String imgUrl;

    /** 是否置顶(0-置顶 1-置顶) */
    @ApiModelProperty("是否置顶(0-置顶 1-置顶)")
    @AiSemanticField(
            semanticName = "是否置顶",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_yes_no"
    )
    private Long isTop;

    /** 是否banner(0-是banner 1-不是banner) */
    @ApiModelProperty("是否banner(0-是banner 1-不是banner)")
    @AiSemanticField(
            semanticName = "是否banner",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_yes_no"
    )
    private Long isBanner;

    /** 分类ID */
    @ApiModelProperty("分类ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "news.category_id=iot_category.category_id"
    )
    private Long categoryId;

    /** 分类名称 */
    @ApiModelProperty("分类名称")
    private String categoryName;

    /** 新闻状态（0-未发布，1-已发布） */
    @ApiModelProperty("新闻状态")
    @AiSemanticField(
            semanticName = "新闻状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_yes_no",
            queryHint = "check-value-mapping"
    )
    private Long status;

    /** 作者 */
    @ApiModelProperty("作者")
    private String author;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic
    @AiSemanticField(
            semanticName = "删除标志",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private String delFlag;

}
