package com.fastbee.ai.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * AI 协议适配任务展示对象。
 */
@Data
@ApiModel(value = "AiProtocolAdaptationTaskVO", description = "AI 协议适配任务展示对象")
public class AiProtocolAdaptationTaskVO {

    @ApiModelProperty("任务 ID")
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
    private String taskStatus;

    @ApiModelProperty("解析状态")
    private String parseStatus;

    @ApiModelProperty("校验状态")
    private String validationStatus;

    @ApiModelProperty("生成状态")
    private String generationStatus;

    @ApiModelProperty("风险等级")
    private String riskLevel;

    @ApiModelProperty("错误摘要")
    private String errorSummary;

    @ApiModelProperty("租户 ID")
    private Long tenantId;

    @ApiModelProperty("租户名称")
    private String tenantName;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("产物数量")
    private Integer artifactCount;

    @ApiModelProperty("生成记录数量")
    private Integer generationRecordCount;

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
}
