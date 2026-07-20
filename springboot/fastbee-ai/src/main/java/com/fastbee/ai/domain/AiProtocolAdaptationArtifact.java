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
 * AI 协议适配产物对象 ai_protocol_adaptation_artifact。
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "AiProtocolAdaptationArtifact", description = "AI 协议适配产物")
@TableName("ai_protocol_adaptation_artifact")
public class AiProtocolAdaptationArtifact extends PageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "artifact_id", type = IdType.AUTO)
    @ApiModelProperty("产物 ID")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long artifactId;

    @ApiModelProperty("任务 ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "ai_protocol_adaptation_artifact.task_id=ai_protocol_adaptation_task.task_id"
    )
    private Long taskId;

    @ApiModelProperty("产物类型")
    @AiSemanticField(
            semanticName = "协议适配产物类型",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "ai_protocol_adaptation_artifact_type",
            queryHint = "check-value-mapping"
    )
    private String artifactType;

    @ApiModelProperty("产物名称")
    private String artifactName;

    @ApiModelProperty("文件路径")
    private String filePath;

    @ApiModelProperty("文件大小")
    private Long fileSize;

    @ApiModelProperty("文件校验码")
    private String checksum;

    @ApiModelProperty("来源类型")
    private String sourceType;

    @ApiModelProperty("产物状态")
    @AiSemanticField(
            semanticName = "协议适配产物状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "ai_protocol_adaptation_artifact_status",
            queryHint = "check-value-mapping"
    )
    private String artifactStatus;

    @ApiModelProperty("摘要")
    private String summary;

    @ApiModelProperty("状态")
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
