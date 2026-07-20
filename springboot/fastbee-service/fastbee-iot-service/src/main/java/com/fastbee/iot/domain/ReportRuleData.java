package com.fastbee.iot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 报表规则变量对象 report_rule_data
 *
 * @author zzy
 * @date 2025-07-10
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ReportRuleData", description = "报表规则变量 report_rule_data")
@Data
@TableName("report_rule_data" )
public class ReportRuleData extends PageEntity {
    private static final long serialVersionUID=1L;

    /** 主键id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键id")
    private Long id;

    /** 报表id */
    @ApiModelProperty("报表id")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long reportId;

    /** 报表id */
    @ApiModelProperty("报表id")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long reportRuleId;

    /** 场景或设备变量id */
    @ApiModelProperty("场景或设备变量id")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long cusDataId;

    /** 统计方式 ，用字典定义，暂时是”原值“ */
    @ApiModelProperty("统计方式 ，用字典定义，暂时是”原值“")
    @AiSemanticField(
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "report_rule_data_operation"
    )
    private Integer operation;

}
