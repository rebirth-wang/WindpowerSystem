package com.fastbee.iot.model.vo;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 报表规则对象 report_rule
 *
 * @author zzy
 * @date 2025-07-10
 */

@ApiModel(value = "ReportRuleVO", description = "报表规则 report_rule")
@Data
public class ReportRuleVO{
    /** 代码生成区域 可直接覆盖**/
    /** 主键id */
    @Excel(name = "主键id")
    @ApiModelProperty("主键id")
    private Long id;

    /** 报表id */
    @Excel(name = "报表id")
    @ApiModelProperty("报表id")
    private Long reportId;

    /** 场景或设备id */
    @Excel(name = "场景或设备id")
    @ApiModelProperty("场景或设备id")
    private Long cusSourceId;

    /** 场景关联设备id */
    @ApiModelProperty("场景关联设备id")
    private Long sceneModelDeviceId;

    private List<ReportRuleDataVO> reportRuleDataVOList;


    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
