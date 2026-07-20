package com.fastbee.ai.model.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 知识库版本质量预检结果。
 */
@Data
@ApiModel(value = "AiKnowledgeVersionQualityCheckVO", description = "知识库版本质量预检结果")
public class AiKnowledgeVersionQualityCheckVO {

    @ApiModelProperty("知识库 ID")
    private Long knowledgeBaseId;

    @ApiModelProperty("版本 ID")
    private Long versionId;

    @ApiModelProperty("知识库类型")
    private String kbType;

    @ApiModelProperty("版本号")
    private String versionNo;

    @ApiModelProperty("是否通过")
    private Boolean passed;

    @ApiModelProperty("摘要说明")
    private String summary;

    @ApiModelProperty("已检查问句数")
    private Integer checkedQuestionCount;

    @ApiModelProperty("P0 问句数")
    private Integer p0QuestionCount;

    @ApiModelProperty("阻断项数量")
    private Integer blockingIssueCount;

    @ApiModelProperty("告警项数量")
    private Integer warningIssueCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("检查时间")
    private Date checkedTime;

    @ApiModelProperty("问题明细")
    private List<AiKnowledgeVersionQualityIssueVO> issues = new ArrayList<>();
}
