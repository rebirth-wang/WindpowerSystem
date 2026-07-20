package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * AI 协议适配自动编排结果。
 */
@Data
@ApiModel(value = "AiProtocolAdaptationAutoRunResultVO", description = "AI 协议适配自动编排结果")
public class AiProtocolAdaptationAutoRunResultVO {

    @ApiModelProperty("任务 ID")
    private Long taskId;

    @ApiModelProperty("运行状态：COMPLETED、COMPLETED_WITH_WARNINGS、NEED_REVIEW、FAILED")
    private String runStatus;

    @ApiModelProperty("当前停留阶段")
    private String currentStage;

    @ApiModelProperty("结果摘要")
    private String summary;

    @ApiModelProperty("已完成阶段")
    private List<String> completedStages = new ArrayList<>();

    @ApiModelProperty("下一步动作")
    private List<String> nextActions = new ArrayList<>();

    @ApiModelProperty("任务详情")
    private AiProtocolAdaptationTaskVO task;

    @ApiModelProperty("代码生成记录")
    private AiProtocolGenerationRecordVO generationRecord;
}
