package com.fastbee.iot.model.vo;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.iot.domain.Scene;
import com.fastbee.notify.domain.NotifyTemplate;

/**
 * 设备告警对象 iot_alert
 *
 * @author zhuangpeng.li
 * @date 2024-11-12
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "AlertVO", description = "设备告警 iot_alert")
@Data
public class AlertVO extends PageEntity {

    /** 告警ID */
    @Excel(name = "告警ID")
    @ApiModelProperty("告警ID")
    private Long alertId;

    /** 告警名称 */
    @Excel(name = "告警名称")
    @ApiModelProperty("告警名称")
    private String alertName;

    /** 告警级别（1=提醒通知，2=轻微问题，3=严重警告） */
    @ApiModelProperty("告警级别")
    @Excel(name = "告警级别")
    private Long alertLevel;

    /** 告警状态（1-启动，2-停止） */
    @ApiModelProperty("告警状态")
    @Excel(name = "告警状态")
    private Integer status;

    /** 通知方式[1,2,3] */
    @Excel(name = "通知方式[1,2,3]")
    @ApiModelProperty("通知方式[1,2,3]")
    private String notify;

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

    /** 备注 */
    @Excel(name = "备注")
    @ApiModelProperty("备注")
    private String remark;

    /** 租户id */
    @Excel(name = "租户id")
    @ApiModelProperty("租户id")
    private Long tenantId;

    /** 租户名称 */
    @Excel(name = "租户名称")
    @ApiModelProperty("租户名称")
    private String tenantName;

    private List<Scene> scenes;

    private List<NotifyTemplate> notifyTemplateList;

    /** 生成工单（1-是，0-否） */
    private Integer generateWorkOrder;

}
