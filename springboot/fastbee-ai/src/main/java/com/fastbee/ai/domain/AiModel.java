package com.fastbee.ai.domain;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * AI 模型配置对象 ai_model。
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "AiModel", description = "AI 模型配置")
@TableName("ai_model")
public class AiModel extends PageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "model_id", type = IdType.AUTO)
    @ApiModelProperty("模型 ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "ai_model.model_id=iot_things_model.model_id"
    )
    private Long modelId;

    @ApiModelProperty("厂商 ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "ai_model.provider_id=ai_provider.provider_id"
    )
    private Long providerId;

    @ApiModelProperty("模型编码")
    private String modelCode;

    @ApiModelProperty("模型名称")
    private String modelName;

    @ApiModelProperty("模型类型")
    @AiSemanticField(
            semanticName = "模型类型",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "ai_model_type",
            synonyms = {"模型类别", "模型能力类型"},
            queryHint = "check-value-mapping"
    )
    private String modelType;

    @ApiModelProperty("上下文长度")
    private Integer contextLength;

    @ApiModelProperty("默认推理参数")
    @AiSemanticField(
            semanticName = "默认推理参数",
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "ai_model_param_key",
            relationHint = "该字段保存 JSON 键值对；参数 key 建议参考字典 ai_model_param_key，如 temperature、topP、maxTokens、presencePenalty、frequencyPenalty、stop、seed、user",
            remark = "该字段不是单一枚举值，而是参数键值配置集合"
    )
    private String requestOptions;

    @ApiModelProperty("是否默认")
    @AiSemanticField(
            semanticName = "是否默认",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT,
            valueMappings = {"0=否", "1=是"},
            queryHint = "check-value-mapping"
    )
    private String isDefault;

    @ApiModelProperty("状态")
    @AiSemanticField(
            semanticName = "状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "ai_provider_status",
            queryHint = "check-value-mapping"
    )
    private String status;

    @ApiModelProperty("租户 ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "系统租户隔离字段，查询时自动追加租户过滤",
            queryHint = "auto-data-scope"
    )
    private Long tenantId;

    @ApiModelProperty("租户名称")
    private String tenantName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建者")
    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新者")
    private String updateBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    @TableLogic
    @ApiModelProperty("删除标记")
    private String delFlag;
}
