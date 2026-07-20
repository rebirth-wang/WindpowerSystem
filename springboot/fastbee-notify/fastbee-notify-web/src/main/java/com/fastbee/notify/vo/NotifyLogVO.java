package com.fastbee.notify.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 通知日志对象 notify_log
 *
 * @author zhuangpeng.li
 * @date 2024-11-19
 */

@ApiModel(value = "NotifyLogVO", description = "通知日志 notify_log")
@Data
public class NotifyLogVO{

    /** 通知日志ID */
    @Excel(name = "通知日志ID")
    @ApiModelProperty("通知日志ID")
    private Long id;

    /** 渠道编号 */
    @Excel(name = "渠道编号")
    @ApiModelProperty("渠道编号")
    private Long channelId;

    /** 通知模版编号 */
    @Excel(name = "通知模版编号")
    @ApiModelProperty("通知模版编号")
    private Long notifyTemplateId;

    /** 消息内容 */
    @Excel(name = "消息内容")
    @ApiModelProperty("消息内容")
    private String msgContent;

    /** 发送账号 */
    @Excel(name = "发送账号")
    @ApiModelProperty("发送账号")
    private String sendAccount;

    /** 发送状态 */
    @Excel(name = "发送状态")
    @ApiModelProperty("发送状态")
    private Long sendStatus;

    /** 返回内容 */
    @Excel(name = "返回内容")
    @ApiModelProperty("返回内容")
    private String resultContent;

    /** 业务编码(唯一启用) */
    @Excel(name = "业务编码(唯一启用)")
    @ApiModelProperty("业务编码(唯一启用)")
    private String serviceCode;

    /** 创建人 */
    @Excel(name = "创建人")
    @ApiModelProperty("创建人")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    /** 更新人 */
    @Excel(name = "更新人")
    @ApiModelProperty("更新人")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    @Excel(name = "更新时间")
    private Date updateTime;

    /** 逻辑删除标识 */
    @Excel(name = "逻辑删除标识")
    @ApiModelProperty("逻辑删除标识")
    private Long delFlag;

    /** 渠道名称 */
    @Excel(name = "渠道名称")
    @ApiModelProperty("渠道名称")
    private String channelName;

    /** 模板名称 */
    @Excel(name = "模板名称")
    @ApiModelProperty("模板名称")
    private String templateName;

    /** 租户id */
    @Excel(name = "租户id")
    @ApiModelProperty("租户id")
    private Long tenantId;

    /** 租户名称 */
    @Excel(name = "租户名称")
    @ApiModelProperty("租户名称")
    private String tenantName;


}
