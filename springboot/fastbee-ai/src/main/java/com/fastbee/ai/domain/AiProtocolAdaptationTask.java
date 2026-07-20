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
 * AI 协议适配任务对象 ai_protocol_adaptation_task。
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "AiProtocolAdaptationTask", description = "AI 协议适配任务")
@TableName("ai_protocol_adaptation_task")
public class AiProtocolAdaptationTask extends PageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "task_id", type = IdType.AUTO)
    @ApiModelProperty("任务 ID")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long taskId;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("协议编码")
    private String protocolCode;

    @ApiModelProperty("协议名称")
    private String protocolName;

    @ApiModelProperty("当前 DSL 快照路径")
    private String dslSnapshotPath;

    @ApiModelProperty("当前工作簿路径")
    private String workbookPath;

    @ApiModelProperty("任务状态")
    @AiSemanticField(
            semanticName = "协议适配任务状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "ai_protocol_adaptation_task_status",
            queryHint = "check-value-mapping"
    )
    private String taskStatus;

    @ApiModelProperty("解析状态")
    @AiSemanticField(
            semanticName = "协议适配解析状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "ai_protocol_adaptation_parse_status",
            queryHint = "check-value-mapping"
    )
    private String parseStatus;

    @ApiModelProperty("校验状态")
    @AiSemanticField(
            semanticName = "协议适配校验状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "ai_protocol_adaptation_validation_status",
            queryHint = "check-value-mapping"
    )
    private String validationStatus;

    @ApiModelProperty("生成状态")
    @AiSemanticField(
            semanticName = "协议适配生成状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "ai_protocol_adaptation_generation_status",
            queryHint = "check-value-mapping"
    )
    private String generationStatus;

    @ApiModelProperty("风险等级")
    @AiSemanticField(
            semanticName = "协议适配风险等级",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "ai_protocol_adaptation_risk_level",
            queryHint = "check-value-mapping"
    )
    private String riskLevel;

    @ApiModelProperty("错误摘要")
    private String errorSummary;

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
            sourceType = AiSemanticSourceType.AUTO_COMMENT,
            valueMappings = {"0=正常", "1=停用"},
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
