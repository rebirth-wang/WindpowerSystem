package com.fastbee.iot.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 固件升级任务对象对象 iot_firmware_task
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */

@ApiModel(value = "FirmwareTaskVO", description = "固件升级任务对象 iot_firmware_task")
@Data
public class FirmwareTaskVO{

    /** 主键 */
    @Excel(name = "主键")
    @ApiModelProperty("主键")
    private Long id;

    /** 任务名称 */
    @Excel(name = "任务名称")
    @ApiModelProperty("任务名称")
    private String taskName;

    /** 关联固件ID */
    @Excel(name = "关联固件ID")
    @ApiModelProperty("关联固件ID")
    private Long firmwareId;

    /** 1:指定设备 2:产品级别 */
    @Excel(name = "1:指定设备 2:产品级别")
    @ApiModelProperty("1:指定设备 2:产品级别")
    private Long upgradeType;

    /** $column.columnComment */
    @ApiModelProperty("${comment}")
    @Excel(name = "${comment}")
    private String taskDesc;

    /** 选中的设备总数 */
    @Excel(name = "选中的设备总数")
    @ApiModelProperty("选中的设备总数")
    private Long deviceAmount;

    /** $column.columnComment */
    @ApiModelProperty("${comment}")
    @Excel(name = "${comment}")
    private Long delFlag;

    /** $column.columnComment */
    @ApiModelProperty("${comment}")
    @Excel(name = "${comment}")
    private Date updateTime;

    /** $column.columnComment */
    @ApiModelProperty("${comment}")
    @Excel(name = "${comment}")
    private Date createTime;

    /** 预定时间升级 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("预定时间升级")
    @Excel(name = "预定时间升级")
    private Date bookTime;


}
