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
 * 报表管理对象 report
 *
 * @author zzy
 * @date 2025-07-09
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Report", description = "报表管理 report")
@Data
@TableName("report" )
public class Report extends PageEntity {
    private static final long serialVersionUID=1L;

    /** 主键id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键id")
    private Long id;

    /** 报表名称 */
    @ApiModelProperty("报表名称")
    private String name;

    /** 数据类型(1-历史数据 2-聚合数据) */
    @ApiModelProperty("数据类型(1-历史数据 2-聚合数据)")
    @AiSemanticField(
            semanticName = "数据类型",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "report_data_type"
    )
    private Integer dataType;

    /** 时间周期方式 1-周期计算 3-固定时间 */
    @ApiModelProperty("时间周期方式 1-周期计算 3-固定时间")
    @AiSemanticField(
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "report_time_period"
    )
    private Integer cycleType;

    /** 时间周期内容 */
    @ApiModelProperty("时间周期内容")
    @AiSemanticField(relationHint = "根据周期不同,DICT为variable_operation_interval,variable_operation_time,variable_operation_week,variable_operation_day")
    private String cycle;

    /** 聚合单位 1-分钟 2-小时 3-天 4-月 */
    @ApiModelProperty("聚合单位 1-分钟 2-小时 3-天 4-月")
    @AiSemanticField(
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "report_aggregation_unit"
    )
    private Integer aggregateUnits;

    /** 数据维度 1-设备 2-场景 */
    @ApiModelProperty("数据维度 1-设备 2-场景")
    @AiSemanticField(
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "data_dimension"
    )
    private Integer dataDimension;

    /** 导出格式 */
    @ApiModelProperty("导出格式")
    private Integer exportFormat;

    /** 所属租户id */
    @ApiModelProperty("所属租户id")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "系统租户隔离字段，查询时自动追加租户过滤",
            queryHint = "auto-data-scope"
    )
    private Long tenantId;

    /** 租户名称 */
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 删除标志（0代表存在 1代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic
    @AiSemanticField(
            semanticName = "删除标志",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private Integer delFlag;

    /** 创建者 */
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 更新者 */
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /** 备注 */
    @ApiModelProperty("备注")
    private String remark;

    /** 通知用户 */
    @ApiModelProperty("通知用户")
    private String notifyUsers;

}
