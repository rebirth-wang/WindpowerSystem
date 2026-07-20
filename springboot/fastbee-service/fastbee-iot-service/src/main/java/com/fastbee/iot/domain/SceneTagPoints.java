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
 * 运算型变量点对象 scene_tag_points
 *
 * @author zhuangpeng.li
 * @date 2024-12-26
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SceneTagPoints", description = "运算型变量点 scene_tag_points")
@Data
@TableName("scene_tag_points" )
public class SceneTagPoints extends BaseEntity{
    private static final long serialVersionUID=1L;

    /** 运算型变量点id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("运算型变量点id")
    private Long id;

    /** 变量点名称 */
    @ApiModelProperty("变量点名称")
    private String name;

    /** 点别名，如 A */
    @ApiModelProperty("点别名，如 A")
    private String alias;

    /** 关联的变量id */
    @ApiModelProperty("关联的变量id")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long tagId;

    /** 统计方式 ，用字典定义，暂时是”原值“ */
    @ApiModelProperty("统计方式 ，用字典定义，暂时是”原值“")
    @AiSemanticField(
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "variable_operation_type"
    )
    private Integer operation;

    /** 数据来源方式 1-设备物模型 2-录入型变量 3-运算型变量 */
    @ApiModelProperty("数据来源方式 1-设备物模型 2-录入型变量 3-运算型变量")
    @AiSemanticField(semanticType = AiSemanticType.ENUM)
    private Integer variableType;

    /** 数据源id,对应scene_model_data表id */
    @ApiModelProperty("数据源id,对应scene_model_data表id")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long sceneModelDataId;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic
    @AiSemanticField(
            semanticName = "删除标志",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private String delFlag;

}
