package com.fastbee.iot.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 场景脚本对象 iot_scene_script
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */

@ApiModel(value = "SceneScriptVO", description = "场景脚本 iot_scene_script")
@Data
public class SceneScriptVO{

    /** 脚本ID */
    @Excel(name = "脚本ID")
    @ApiModelProperty("脚本ID")
    private String scriptId;

    /** 场景ID */
    @Excel(name = "场景ID")
    @ApiModelProperty("场景ID")
    private Long sceneId;

    /** 触发源（1=设备触发，2=定时触发，3=产品触发,4=告警执行） */
    @ApiModelProperty("触发源")
    @Excel(name = "触发源")
    private Integer source;

    /** 脚本用途(1=数据流，2=触发器，3=执行动作) */
    @Excel(name = "脚本用途(1=数据流，2=触发器，3=执行动作)")
    @ApiModelProperty("脚本用途(1=数据流，2=触发器，3=执行动作)")
    private Integer scriptPurpose;

    /** 产品ID（用于获取对应物模型） */
    @ApiModelProperty("产品ID")
    @Excel(name = "产品ID")
    private Long productId;

    /** 产品名称 */
    @Excel(name = "产品名称")
    @ApiModelProperty("产品名称")
    private String productName;

    /** 物模型标识符 */
    @Excel(name = "物模型标识符")
    @ApiModelProperty("物模型标识符")
    private String id;

    /** 物模型名称 */
    @Excel(name = "物模型名称")
    @ApiModelProperty("物模型名称")
    private String name;

    /** 物模型值 */
    @Excel(name = "物模型值")
    @ApiModelProperty("物模型值")
    private String value;

    /** 操作符 */
    @Excel(name = "操作符")
    @ApiModelProperty("操作符")
    private String operator;

    /** 物模型类别（1=属性，2=功能，3=事件，4=设备升级，5=设备上线，6=设备下线） */
    @ApiModelProperty("物模型类别")
    @Excel(name = "物模型类别")
    private Integer type;

    /** 设备数量 */
    @Excel(name = "设备数量")
    @ApiModelProperty("设备数量")
    private Long deviceCount;

    /** 任务ID */
    @Excel(name = "任务ID")
    @ApiModelProperty("任务ID")
    private Long jobId;

    /** cron执行表达式 */
    @Excel(name = "cron执行表达式")
    @ApiModelProperty("cron执行表达式")
    private String cronExpression;

    /** 是否详细corn表达式（1=是，0=否） */
    @ApiModelProperty("是否详细corn表达式")
    @Excel(name = "是否详细corn表达式")
    private Integer isAdvance;

    /** 父物模id */
    @Excel(name = "父物模id")
    @ApiModelProperty("父物模id")
    private String parentId;

    /** 父物模名称 */
    @Excel(name = "父物模名称")
    @ApiModelProperty("父物模名称")
    private String parentName;

    /** 数组索引 */
    @Excel(name = "数组索引")
    @ApiModelProperty("数组索引")
    private String arrayIndex;

    /** 数组索引名称 */
    @Excel(name = "数组索引名称")
    @ApiModelProperty("数组索引名称")
    private String arrayIndexName;

    /** 创建者 */
    @Excel(name = "创建者")
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    /** 设备编号，仅用于传递*/
    private String[] deviceNums;

    /**
     * 分组id
     */
    @ApiModelProperty("分组id")
    private Long groupId;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 告警通知次数
     */
    private Integer notifyCount;

}
