package com.fastbee.ai.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * AI 协议代码生成记录展示对象。
 */
@Data
@ApiModel(value = "AiProtocolGenerationRecordVO", description = "AI 协议代码生成记录展示对象")
public class AiProtocolGenerationRecordVO {

    @ApiModelProperty("生成记录 ID")
    private Long recordId;

    @ApiModelProperty("任务 ID")
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
}
