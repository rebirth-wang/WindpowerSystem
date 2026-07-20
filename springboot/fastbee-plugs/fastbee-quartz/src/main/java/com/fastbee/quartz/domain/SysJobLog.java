package com.fastbee.quartz.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 定时任务调度日志对象 sys_job_log
 *
 * @author zhuangpeng.li
 * @date 2024-12-24
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysJobLog", description = "定时任务调度日志 sys_job_log")
@Data
@TableName("sys_job_log" )
public class SysJobLog extends PageEntity{
    /** 任务日志ID */
    @TableId(value = "job_log_id", type = IdType.AUTO)
    @ApiModelProperty("任务日志ID")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long jobLogId;

    /** 任务名称 */
    @ApiModelProperty("任务名称")
    private String jobName;

    /** 任务组名 */
    @ApiModelProperty("任务组名")
    private String jobGroup;

    /** 调用目标字符串 */
    @ApiModelProperty("调用目标字符串")
    private String invokeTarget;

    /** 日志信息 */
    @ApiModelProperty("日志信息")
    private String jobMessage;

    /** 执行状态（0正常 1失败） */
    @ApiModelProperty("执行状态")
    @AiSemanticField(
            semanticName = "执行状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "sys_common_status",
            queryHint = "check-value-mapping"
    )
    private Integer status;

    /** 异常信息 */
    @ApiModelProperty("异常信息")
    private String exceptionInfo;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    @Setter
    @TableField(exist = false)
    @ApiModelProperty("请求参数")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;

    public Map<String, Object> getParams(){
        if (params == null){
            params = new HashMap<>();
        }
        return params;
    }

}
