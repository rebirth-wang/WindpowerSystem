package com.fastbee.iot.model.vo;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.iot.util.KingbaseJsonTypeHandler;

/**
 * 设备告警日志对象 iot_alert_log
 *
 * @author zhuangpeng.li
 * @date 2024-11-12
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "AlertLogVO", description = "设备告警日志 iot_alert_log")
@Data
public class AlertLogVO extends PageEntity {

    /** 告警日志ID */
    @Excel(name = "告警日志ID")
    @ApiModelProperty("告警日志ID")
    private Long alertLogId;

    /** 告警名称 */
    @Excel(name = "告警名称")
    @ApiModelProperty("告警名称")
    private String alertName;

    /** 告警级别（1=提醒通知，2=轻微问题，3=严重警告） */
    @ApiModelProperty("告警级别")
    @Excel(name = "告警级别")
    private Long alertLevel;

    /** 处理状态(1=不需要处理,2=未处理,3=已处理) */
    @Excel(name = "处理状态(1=不需要处理,2=未处理,3=已处理)")
    @ApiModelProperty("处理状态(1=不需要处理,2=未处理,3=已处理)")
    private Integer status;

    /** 设备编号 */
    @Excel(name = "设备编号")
    @ApiModelProperty("设备编号")
    private String serialNumber;

    /** 产品ID */
    @Excel(name = "产品ID")
    @ApiModelProperty("产品ID")
    private Long productId;

    /** 告警详情（对应物模型） */
    @TableField(typeHandler = KingbaseJsonTypeHandler.class)
    @ApiModelProperty("告警详情")
    @Excel(name = "告警详情")
    private String detail;

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

    /** 用户id */
    @Excel(name = "用户id")
    @ApiModelProperty("用户id")
    private Long userId;

    /** 设备名称 */
    @Excel(name = "设备名称")
    @ApiModelProperty("设备名称")
    private String deviceName;

    private Long deviceId;
    private Long sceneId;
    private String sceneName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date beginTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    private Long deptUserId;

}
