package com.fastbee.notify.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 通知渠道对象 notify_channel
 *
 * @author zhuangpeng.li
 * @date 2024-11-18
 */

@ApiModel(value = "NotifyChannelVO", description = "通知渠道 notify_channel")
@Data
public class NotifyChannelVO{

    /** 编号 */
    @Excel(name = "编号")
    @ApiModelProperty("编号")
    private Long id;

    /** 通知名称 */
    @Excel(name = "通知名称")
    @ApiModelProperty("通知名称")
    private String name;

    /** 渠道类型 */
    @Excel(name = "渠道类型")
    @ApiModelProperty("渠道类型")
    private String channelType;

    /** 服务商 */
    @Excel(name = "服务商")
    @ApiModelProperty("服务商")
    private String provider;

    /** 配置内容 */
    @Excel(name = "配置内容")
    @ApiModelProperty("配置内容")
    private String configContent;

    /** 租户id */
    @Excel(name = "租户id")
    @ApiModelProperty("租户id")
    private Long tenantId;

    /** 租户名称 */
    @Excel(name = "租户名称")
    @ApiModelProperty("租户名称")
    private String tenantName;

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


}
