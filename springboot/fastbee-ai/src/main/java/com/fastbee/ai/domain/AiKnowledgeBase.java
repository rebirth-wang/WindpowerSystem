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
 * AI 知识库对象 ai_knowledge_base。
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "AiKnowledgeBase", description = "AI 知识库")
@TableName("ai_knowledge_base")
public class AiKnowledgeBase extends PageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "knowledge_base_id", type = IdType.AUTO)
    @ApiModelProperty("知识库 ID")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long knowledgeBaseId;

    @ApiModelProperty("知识库编码")
    private String kbCode;

    @ApiModelProperty("知识库名称")
    private String kbName;

    @ApiModelProperty("知识库类型")
    @AiSemanticField(
            semanticName = "知识库类型",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "ai_knowledge_type",
            queryHint = "check-value-mapping"
    )
    private String kbType;

    @ApiModelProperty("向量库类型")
    @AiSemanticField(
            semanticName = "向量库类型",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "ai_knowledge_vector_store_type",
            queryHint = "check-value-mapping"
    )
    private String vectorStoreType;

    @ApiModelProperty("发布状态")
    @AiSemanticField(
            semanticName = "发布状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "ai_knowledge_publish_status",
            queryHint = "check-value-mapping"
    )
    private String publishStatus;

    @ApiModelProperty("当前激活版本ID")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long activeVersionId;

    @ApiModelProperty("扩展配置")
    private String extraConfig;

    @ApiModelProperty("租户 ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "系统租户隔离字段，查询时自动追加租户过滤",
            queryHint = "auto-data-scope"
    )
    private Long tenantId;

    @ApiModelProperty("租户名称")
    private String tenantName;

    @ApiModelProperty("状态")
    @AiSemanticField(
            semanticName = "状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "ai_knowledge_status",
            queryHint = "check-value-mapping"
    )
    private String status;

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
