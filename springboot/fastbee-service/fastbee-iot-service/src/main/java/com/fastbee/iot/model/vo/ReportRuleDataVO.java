package com.fastbee.iot.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 报表规则变量对象 report_rule_data
 *
 * @author zzy
 * @date 2025-07-10
 */

@ApiModel(value = "ReportRuleDataVO", description = "报表规则变量 report_rule_data")
@Data
public class ReportRuleDataVO{
    /** 主键id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键id")
    private Long id;

    /** 报表id */
    @ApiModelProperty("报表id")
    private Long reportId;

    /** 报表id */
    @ApiModelProperty("报表id")
    private Long reportRuleId;

    /** 场景或设备变量id */
    @ApiModelProperty("场景或设备变量id")
    private Long cusDataId;

    /** 统计方式 ，用字典定义，暂时是”原值“ */
    @ApiModelProperty("统计方式 ，用字典定义，暂时是”原值“")
    private Integer operation;
}
