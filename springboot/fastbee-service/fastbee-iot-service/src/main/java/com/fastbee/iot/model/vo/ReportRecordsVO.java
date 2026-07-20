package com.fastbee.iot.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.PageEntity;

/**
 * 报表记录对象 report_records
 *
 * @author zzy
 * @date 2025-07-09
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ReportRecordsVO", description = "报表记录 report_records")
@Data
public class ReportRecordsVO extends PageEntity {
    /** 代码生成区域 可直接覆盖**/
    /** 主键id */
    @Excel(name = "主键id")
    @ApiModelProperty("主键id")
    private Long id;

    /** 报表id */
    @Excel(name = "报表id")
    @ApiModelProperty("报表id")
    private Long reportId;

    /** 报表文件下载路径 */
    @Excel(name = "报表文件下载路径")
    @ApiModelProperty("报表文件下载路径")
    private String reportFilePath;

    /** 状态 */
    @Excel(name = "状态")
    @ApiModelProperty("状态")
    private Integer state;

    /** 时间周期 */
    @Excel(name = "时间周期")
    @ApiModelProperty("时间周期")
    private String timeCycle;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    /** 数据类型(1-历史数据 2-聚合数据) */
    @ApiModelProperty("数据类型(1-历史数据 2-聚合数据)")
    private Integer dataType;

    /** 报表名称 */
    @ApiModelProperty("报表名称")
    private String reportName;

}
