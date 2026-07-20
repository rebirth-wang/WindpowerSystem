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
 * 新闻分类对象 news_category
 *
 * @author kerwincui
 * @date 2022-04-09
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "NewsCategory", description = "新闻分类对象 news_category")
@Data
@TableName("news_category" )
public class NewsCategory extends BaseEntity {
    /** 分类ID */
    @TableId(value = "category_id", type = IdType.AUTO)
    @ApiModelProperty("分类ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "news_category.category_id=iot_category.category_id"
    )
    private Long categoryId;

    /** 分类名称 */
    @ApiModelProperty("分类名称")
    private String categoryName;

    /** 显示顺序 */
    @ApiModelProperty("显示顺序")
    private Long orderNum;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic
    @AiSemanticField(
            semanticName = "删除标志",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private String delFlag;
}
