package com.fastbee.ai.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 知识库版本质量预检问题。
 */
@Data
@ApiModel(value = "AiKnowledgeVersionQualityIssueVO", description = "知识库版本质量预检问题")
public class AiKnowledgeVersionQualityIssueVO {

    @ApiModelProperty("Excel 行号，从 2 开始表示第一条数据行")
    private Integer rowNum;

    @ApiModelProperty("问题级别：ERROR / WARNING")
    private String level;

    @ApiModelProperty("问题编码")
    private String issueCode;

    @ApiModelProperty("对应问句")
    private String question;

    @ApiModelProperty("问题说明")
    private String message;
}
