package com.fastbee.iot.model.vo;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 报表管理对象 report
 *
 * @author zzy
 * @date 2025-07-09
 */

@ApiModel(value = "ReportVO", description = "报表管理 report")
@Data
public class ReportVO{
    /** 代码生成区域 可直接覆盖**/
    /** 主键id */
    @Excel(name = "主键id")
    @ApiModelProperty("主键id")
    private Long id;

    /** 报表名称 */
    @Excel(name = "报表名称")
    @ApiModelProperty("报表名称")
    private String name;

    /** 数据类型(1-历史数据 2-聚合数据) */
    @Excel(name = "数据类型(1-历史数据 2-聚合数据)")
    @ApiModelProperty("数据类型(1-历史数据 2-聚合数据)")
    private Integer dataType;

    /** 时间周期方式 1-周期计算 3-固定时间 */
    @Excel(name = "时间周期方式 1-周期计算 3-固定时间")
    @ApiModelProperty("时间周期方式 1-周期计算 3-固定时间")
    private Integer cycleType;

    /** 时间周期内容 */
    @Excel(name = "时间周期内容")
    @ApiModelProperty("时间周期内容")
    private String cycle;

    /** 聚合单位 1-分钟 2-小时 3-天 4-月 */
    @Excel(name = "聚合单位 1-分钟 2-小时 3-天 4-月")
    @ApiModelProperty("聚合单位 1-分钟 2-小时 3-天 4-月")
    private Integer aggregateUnits;

    /** 数据维度 1-设备 2-场景 */
    @Excel(name = "数据维度 1-设备 2-场景")
    @ApiModelProperty("数据维度 1-设备 2-场景")
    private Integer dataDimension;

    /** 导出格式 */
    @Excel(name = "导出格式")
    @ApiModelProperty("导出格式")
    private Integer exportFormat;

    /** 所属租户id */
    @Excel(name = "所属租户id")
    @ApiModelProperty("所属租户id")
    private Long tenantId;

    /** 租户名称 */
    @Excel(name = "租户名称")
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 创建者 */
    @Excel(name = "创建者")
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    /** 更新者 */
    @Excel(name = "更新者")
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    @Excel(name = "更新时间")
    private Date updateTime;

    /** 备注 */
    @Excel(name = "备注")
    @ApiModelProperty("备注")
    private String remark;

    /** 通知用户 */
    @ApiModelProperty("通知用户")
    private String notifyUsers;


    private List<ReportRuleVO> reportRuleVOList;

}
