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
 * AI 知识库版本对象 ai_knowledge_version。
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "AiKnowledgeVersion", description = "AI 知识库版本")
@TableName("ai_knowledge_version")
public class AiKnowledgeVersion extends PageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "version_id", type = IdType.AUTO)
    @ApiModelProperty("版本ID")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long versionId;

    @ApiModelProperty("知识库ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "ai_knowledge_version.knowledge_base_id=ai_knowledge_base.knowledge_base_id"
    )
    private Long knowledgeBaseId;

    @ApiModelProperty("版本号")
    private String versionNo;
    @ApiModelProperty("来源文档ID集合")
    private String sourceDocumentIds;

    @ApiModelProperty("结构化快照路径")
    private String snapshotPath;

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

    @ApiModelProperty("是否激活(0否 1是)")
    @AiSemanticField(
            semanticName = "是否激活",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT,
            valueMappings = {"0=否", "1=是"},
            queryHint = "check-value-mapping"
    )
    private String isActive;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("发布时间")
    private Date publishTime;

    @ApiModelProperty("发布人")
    private String publishedBy;

    @ApiModelProperty("回滚来源版本")
    private String rollbackFromVersion;

    @ApiModelProperty("参与文件数")
    private Integer sourceFileCount;

    @ApiModelProperty("合并条目数")
    private Integer mergedItemCount;

    @ApiModelProperty("覆盖次数")
    private Integer overrideCount;

    @ApiModelProperty("冲突次数")
    private Integer conflictCount;

    @ApiModelProperty("构建摘要")
    private String buildSummary;

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
