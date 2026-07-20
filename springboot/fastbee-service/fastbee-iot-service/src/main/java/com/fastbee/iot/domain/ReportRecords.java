package com.fastbee.iot.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 报表记录对象 report_records
 *
 * @author zzy
 * @date 2025-07-09
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ReportRecords", description = "报表记录 report_records")
@Data
@TableName("report_records" )
public class ReportRecords extends PageEntity {
    private static final long serialVersionUID=1L;

    /** 主键id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键id")
    private Long id;

    /** 报表id */
    @ApiModelProperty("报表id")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long reportId;

    /** 报表文件下载路径 */
    @ApiModelProperty("报表文件下载路径")
    private String reportFilePath;

    /** 状态 */
    @ApiModelProperty("状态")
    @AiSemanticField(
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "report_status"
    )
    private Integer state;

    /** 时间周期 */
    @ApiModelProperty("时间周期")
    private String timeCycle;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 删除标志（0代表存在 1代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic
    @AiSemanticField(
            semanticName = "删除标志",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private Integer delFlag;


}
