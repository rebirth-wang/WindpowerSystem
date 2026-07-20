package com.fastbee.iot.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 物模型计算对象 iot_things_model_tag
 *
 * @author fastbee
 * @date 2025-06-24
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ThingsModelTag", description = "物模型计算 iot_things_model_tag")
@Data
@TableName("iot_things_model_tag" )
public class ThingsModelTag extends PageEntity {
    private static final long serialVersionUID=1L;

    /** 主键id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键id")
    private Long id;

    /** 物模型标识 */
    @ApiModelProperty("物模型标识")
    private String identifier;

    /** 别名，如 A */
    @ApiModelProperty("别名，如 A")
    private String alias;

    /** 关联的模型id */
    @ApiModelProperty("关联的模型id")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_things_model_tag.model_id=iot_things_model.model_id"
    )
    private Long modelId;

    /** 统计方式 ，用字典定义，暂时是”原值“ */
    @ApiModelProperty("统计方式 ，用字典定义，暂时是”原值“")
    private Long operation;

    /** 数据源模型id */
    @ApiModelProperty("数据源模型id")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long sourceModelId;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @AiSemanticField(
            semanticName = "删除标志",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private String delFlag;

    /** 创建者 */
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 更新者 */
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /** 备注 */
    @ApiModelProperty("备注")
    private String remark;

    @Setter
    @TableField(exist = false)
    @ApiModelProperty("请求参数")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;

    public Map<String, Object> getParams(){
        if (params == null){
            params = new HashMap<>();
        }
        return params;
    }

}
