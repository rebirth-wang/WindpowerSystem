package com.fastbee.quartz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.BaseEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 定时任务调度表 sys_job
 *
 * @author ruoyi
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysJob", description = "定时任务调度表 sys_job")
@Data
@TableName("sys_job" )
public class SysJob extends BaseEntity
{
    /** 任务ID */
    @TableId(value = "job_id", type = IdType.AUTO)
    @ApiModelProperty("任务ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "sys_job.job_id=iot_device_job.job_id"
    )
    private Long jobId;

    /** 任务名称 */
    @ApiModelProperty("任务名称")
    private String jobName;

    /** 任务组名 */
    @ApiModelProperty("任务组名")
    @AiSemanticField(
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "sys_job_group"
    )
    private String jobGroup;

    /** 调用目标字符串 */
    @ApiModelProperty("调用目标字符串")
    private String invokeTarget;

    /** cron执行表达式 */
    @ApiModelProperty("cron执行表达式")
    private String cronExpression;

    /** cron计划策略 */
    @ApiModelProperty("计划执行错误策略")
    @AiSemanticField(
            semanticName = "计划执行错误策略",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "execution_strategy",
            valueMappings = {"立即执行=1", "执行一次=2", "放弃执行=3"}
    )
    private String misfirePolicy;

    /** 是否并发执行（0允许 1禁止） */
    @ApiModelProperty("是否并发执行")
    @AiSemanticField(
            semanticName = "是否并发执行",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "is_concurrency",
            valueMappings = {"允许=0", "禁止=1"}
    )
    private String concurrent;

    /** 状态（0正常 1暂停） */
    @ApiModelProperty("状态")
    @AiSemanticField(
            semanticName = "状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "sys_job_status",
            queryHint = "check-value-mapping"
    )
    private Integer status;

}
