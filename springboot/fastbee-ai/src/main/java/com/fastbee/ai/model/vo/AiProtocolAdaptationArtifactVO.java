package com.fastbee.ai.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * AI 协议适配产物展示对象。
 */
@Data
@ApiModel(value = "AiProtocolAdaptationArtifactVO", description = "AI 协议适配产物展示对象")
public class AiProtocolAdaptationArtifactVO {

    @ApiModelProperty("产物 ID")
    private Long artifactId;

    @ApiModelProperty("任务 ID")
    private Long taskId;

    @ApiModelProperty("产物类型")
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
}
