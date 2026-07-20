package com.fastbee.notify.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 通知模版对象 notify_template
 *
 * @author zhuangpeng.li
 * @date 2024-11-19
 */

@ApiModel(value = "NotifyTemplateVO", description = "通知模版 notify_template")
@Data
public class NotifyTemplateVO{

    /** 编号 */
    @Excel(name = "编号")
    @ApiModelProperty("编号")
    private Long id;

    /** 渠道名称 */
    @Excel(name = "渠道名称")
    @ApiModelProperty("渠道名称")
    private String name;

    /** 业务编码(唯一启用) */
    @Excel(name = "业务编码(唯一启用)")
    @ApiModelProperty("业务编码(唯一启用)")
    private String serviceCode;

    /** 通知渠道账号 */
    @Excel(name = "通知渠道账号")
    @ApiModelProperty("通知渠道账号")
    private Long channelId;

    /** 渠道类型 */
    @Excel(name = "渠道类型")
    @ApiModelProperty("渠道类型")
    private String channelType;

    /** 服务商 */
    @Excel(name = "服务商")
    @ApiModelProperty("服务商")
    private String provider;

    /** 模板配置参数 */
    @Excel(name = "模板配置参数")
    @ApiModelProperty("模板配置参数")
    private String msgParams;

    /** 是否启用 0-不启用 1-启用 */
    @Excel(name = "是否启用 0-不启用 1-启用")
    @ApiModelProperty("是否启用 0-不启用 1-启用")
    private Integer status;

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

    /** 租户id */
    @Excel(name = "租户id")
    @ApiModelProperty("租户id")
    private Long tenantId;

    /** 租户名称 */
    @Excel(name = "租户名称")
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 渠道名称 */
    @Excel(name = "渠道名称")
    @ApiModelProperty("渠道名称")
    private String channelName;

}
