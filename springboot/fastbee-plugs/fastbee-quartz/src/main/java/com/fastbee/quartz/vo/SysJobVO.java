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
 * 定时任务调度对象 sys_job
 *
 * @author zhuangpeng.li
 * @date 2024-12-13
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysJobVO", description = "定时任务调度 sys_job")
@Data
public class SysJobVO extends PageEntity {
    /** 代码生成区域 可直接覆盖**/
    /** 任务ID */
    @Excel(name = "任务ID")
    @ApiModelProperty("任务ID")
    private Long jobId;

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

    /** cron执行表达式 */
    @Excel(name = "cron执行表达式")
    @ApiModelProperty("cron执行表达式")
    private String cronExpression;

    /** 计划执行错误策略（1立即执行 2执行一次 3放弃执行） */
    @ApiModelProperty("计划执行错误策略")
    @Excel(name = "计划执行错误策略")
    private String misfirePolicy;

    /** 是否并发执行（0允许 1禁止） */
    @ApiModelProperty("是否并发执行")
    @Excel(name = "是否并发执行")
    private String concurrent;

    /** 状态（0正常 1暂停） */
    @ApiModelProperty("状态")
    @Excel(name = "状态")
    private Long status;

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

    /** 备注信息 */
    @Excel(name = "备注信息")
    @ApiModelProperty("备注信息")
    private String remark;


    /** 代码生成区域 可直接覆盖END**/

    /** 自定义代码区域 **/


    /** 自定义代码区域 END**/
}
