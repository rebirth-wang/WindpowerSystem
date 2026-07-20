package com.fastbee.system.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 操作日志记录对象 sys_oper_log
 *
 * @author zhuangpeng.li
 * @date 2024-12-13
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SysOperLog", description = "操作日志记录 sys_oper_log")
@Data
@TableName("sys_oper_log" )
public class SysOperLog extends PageEntity{
    private static final long serialVersionUID=1L;

    /** 操作日志ID */
    @TableId(value = "oper_id", type = IdType.AUTO)
    @ApiModelProperty("操作日志ID")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long operId;

    /** 模块标题 */
    @ApiModelProperty("模块标题")
    private String title;

    /** 业务类型（0其它 1新增 2修改 3删除） */
    @ApiModelProperty("业务类型")
    @AiSemanticField(
            semanticName = "业务类型",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "sys_oper_type"
    )
    private Integer businessType;

    /** 方法名称 */
    @ApiModelProperty("方法名称")
    private String method;

    /** 请求方式 */
    @ApiModelProperty("请求方式")
    private String requestMethod;

    /** 操作类别（0其它 1后台用户 2手机端用户） */
    @ApiModelProperty("操作类别")
    @AiSemanticField(
            semanticName = "操作类别",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private Integer operatorType;

    /** 操作人员 */
    @ApiModelProperty("操作人员")
    private String operName;

    /** 部门名称 */
    @ApiModelProperty("部门名称")
    private String deptName;

    /** 请求URL */
    @ApiModelProperty("请求URL")
    private String operUrl;

    /** 主机地址 */
    @ApiModelProperty("主机地址")
    private String operIp;

    /** 操作地点 */
    @ApiModelProperty("操作地点")
    private String operLocation;

    /** 请求参数 */
    @ApiModelProperty("请求参数")
    private String operParam;

    /** 返回参数 */
    @ApiModelProperty("返回参数")
    private String jsonResult;

    /** 操作状态（0正常 1异常） */
    @ApiModelProperty("操作状态")
    @AiSemanticField(
            semanticName = "操作状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "sys_common_status",
            queryHint = "check-value-mapping"
    )
    private Integer status;

    /** 错误消息 */
    @ApiModelProperty("错误消息")
    private String errorMsg;

    /** 操作时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("操作时间")
    private Date operTime;

}
