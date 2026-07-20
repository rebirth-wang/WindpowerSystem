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
 * AI 知识文档对象 ai_knowledge_document。
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "AiKnowledgeDocument", description = "AI 知识文档")
@TableName("ai_knowledge_document")
public class AiKnowledgeDocument extends PageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "document_id", type = IdType.AUTO)
    @ApiModelProperty("文档 ID")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long documentId;

    @ApiModelProperty("知识库 ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "ai_knowledge_document.knowledge_base_id=ai_knowledge_base.knowledge_base_id"
    )
    private Long knowledgeBaseId;

    @ApiModelProperty("文件名")
    private String fileName;

    @ApiModelProperty("文件路径")
    private String filePath;
    @ApiModelProperty("文件大小")
    private Long fileSize;

    @ApiModelProperty("文件校验码")
    private String checksum;

    @ApiModelProperty("解析状态")
    @AiSemanticField(
            semanticName = "解析状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "ai_knowledge_parse_status",
            queryHint = "check-value-mapping"
    )
    private String parseStatus;

    @ApiModelProperty("分片数量")
    private Integer chunkCount;

    @ApiModelProperty("解析快照路径")
    private String parsedSnapshotPath;

    @ApiModelProperty("解析摘要")
    private String parsedSummary;

    @ApiModelProperty("排序号")
    private Integer sortNum;

    @ApiModelProperty("来源类型")
    @AiSemanticField(
            semanticName = "来源类型",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "ai_knowledge_source_origin",
            queryHint = "check-value-mapping"
    )
    private String sourceOrigin;

    @ApiModelProperty("平台版本")
    private String appVersion;

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
