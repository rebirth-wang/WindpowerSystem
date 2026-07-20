package com.fastbee.iot.model.vo;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.BaseEntity;

/**
 * 场景联动对象 iot_scene
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SceneVO", description = "场景联动 iot_scene")
@Data
public class SceneVO extends BaseEntity {

    /** 场景ID */
    @Excel(name = "场景ID")
    @ApiModelProperty("场景ID")
    private Long sceneId;

    /** 场景名称 */
    @Excel(name = "场景名称")
    @ApiModelProperty("场景名称")
    private String sceneName;

    /** 规则名称 */
    @Excel(name = "规则名称")
    @ApiModelProperty("规则名称")
    private String chainName;

    /** 场景状态（1-启动，2-停止） */
    @ApiModelProperty("场景状态")
    @Excel(name = "场景状态")
    private Integer enable;

    /** 用户ID */
    @Excel(name = "用户ID")
    @ApiModelProperty("用户ID")
    private Long userId;

    /** 用户名称 */
    @Excel(name = "用户名称")
    @ApiModelProperty("用户名称")
    private String userName;

    /** 静默周期（分钟） */
    @ApiModelProperty("静默周期")
    @Excel(name = "静默周期")
    private Integer silentPeriod;

    /** 执行条件（1=或、任意条件，2=且、所有条件，3=非，不满足） */
    @ApiModelProperty("执行条件")
    @Excel(name = "执行条件")
    private Integer cond;

    /** 执行方式（1=串行，顺序执行，2=并行，同时执行） */
    @ApiModelProperty("执行方式")
    @Excel(name = "执行方式")
    private Integer executeMode;

    /** 延时执行（秒钟） */
    @ApiModelProperty("延时执行")
    @Excel(name = "延时执行")
    private Integer executeDelay;

    /** 是否包含告警推送（1=包含，2=不包含） */
    @ApiModelProperty("是否包含告警推送")
    @Excel(name = "是否包含告警推送")
    private Integer hasAlert;

    /** 应用名称 */
    @Excel(name = "应用名称")
    @ApiModelProperty("应用名称")
    private String applicationName;

    /** 规则数据 */
    @Excel(name = "规则数据")
    @ApiModelProperty("规则数据")
    private String elData;

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

    /** 是否终端用户（1-是，0-不是） */
    @ApiModelProperty("是否终端用户")
    @Excel(name = "是否终端用户")
    private Integer terminalUser;

    /** 延时匹配（秒钟） */
    @ApiModelProperty("延时匹配")
    @Excel(name = "延时匹配")
    private Integer checkDelay;

    /** 恢复告警场景ID */
    @Excel(name = "恢复告警场景ID")
    @ApiModelProperty("恢复告警场景ID")
    private Long recoverId;

    /**
     * 执行动作数量
     */
    private Integer actionCount = 0;

    /** 接收的触发器列表 */
    private List<SceneScriptVO> triggers;

    /** 接收的执行动作列表 */
    private List<SceneScriptVO> actions;

    private String alertName;

    private List<Long> alertIds;

}
