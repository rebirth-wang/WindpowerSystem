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
 * AI 协议代码生成记录对象 ai_protocol_generation_record。
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "AiProtocolGenerationRecord", description = "AI 协议代码生成记录")
@TableName("ai_protocol_generation_record")
public class AiProtocolGenerationRecord extends PageEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "record_id", type = IdType.AUTO)
    @ApiModelProperty("生成记录 ID")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long recordId;

    @ApiModelProperty("任务 ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "ai_protocol_generation_record.task_id=ai_protocol_adaptation_task.task_id"
    )
    private Long taskId;

    @ApiModelProperty("输入 DSL 快照路径")
    private String dslSnapshotPath;

    @ApiModelProperty("生成策略")
    private String generationStrategy;

    @ApiModelProperty("代码包路径")
    private String codePackagePath;

    @ApiModelProperty("文件清单路径")
    private String fileManifestPath;

    @ApiModelProperty("测试报告路径")
    private String testReportPath;

    @ApiModelProperty("编译状态")
    private String compileStatus;

    @ApiModelProperty("校验错误数")
    private Integer validationErrorCount;

    @ApiModelProperty("校验告警数")
    private Integer validationWarningCount;

    @ApiModelProperty("确认人")
    private String confirmBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("确认时间")
    private Date confirmTime;

    @ApiModelProperty("生成状态")
    @AiSemanticField(
            semanticName = "协议适配生成状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "ai_protocol_adaptation_generation_status",
            queryHint = "check-value-mapping"
    )
    private String generationStatus;

    @ApiModelProperty("失败原因")
    private String failureReason;

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
