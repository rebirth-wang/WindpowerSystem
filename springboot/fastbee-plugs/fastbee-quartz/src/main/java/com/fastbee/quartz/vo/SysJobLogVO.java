package com.fastbee.quartz.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.PageEntity;

/**
 * 定时任务调度日志对象 sys_job_log
 *
 * @author zhuangpeng.li
 * @date 2024-12-24
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysJobLogVO", description = "定时任务调度日志 sys_job_log")
@Data
public class SysJobLogVO extends PageEntity {
    /** 代码生成区域 可直接覆盖**/
    /** 任务日志ID */
    @Excel(name = "任务日志ID")
    @ApiModelProperty("任务日志ID")
    private Long jobLogId;

    /** 任务名称 */
    @Excel(name = "任务名称")
    @ApiModelProperty("任务名称")
    private String jobName;

    /** 任务组名 */
    @Excel(name = "任务组名")
    @ApiModelProperty("任务组名")
    private String jobGroup;

    /** 调用目标字符串 */
    @Excel(name = "调用目标字符串")
    @ApiModelProperty("调用目标字符串")
    private String invokeTarget;

    /** 日志信息 */
    @Excel(name = "日志信息")
    @ApiModelProperty("日志信息")
    private String jobMessage;

    /** 执行状态（0正常 1失败） */
    @ApiModelProperty("执行状态")
    @Excel(name = "执行状态")
    private Integer status;

    /** 异常信息 */
    @Excel(name = "异常信息")
    @ApiModelProperty("异常信息")
    private String exceptionInfo;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private Date createTime;


    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/

    /** 开始时间 */
    @ApiModelProperty("开始时间")
    private Date startTime;

    /** 停止时间 */
    @ApiModelProperty("停止时间")
    private Date stopTime;

    /** 自定义代码区域 END**/
}
