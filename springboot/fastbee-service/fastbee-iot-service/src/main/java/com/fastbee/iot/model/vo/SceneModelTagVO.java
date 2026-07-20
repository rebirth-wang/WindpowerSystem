package com.fastbee.iot.model.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.PageEntity;

/**
 * 场景录入型变量对象 scene_model_tag
 *
 * @author zhuangpeng.li
 * @date 2024-12-26
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SceneModelTagVO", description = "场景录入型变量 scene_model_tag")
@Data
public class SceneModelTagVO extends PageEntity {
    /** 代码生成区域 可直接覆盖**/
    /** 主键id */
    @Excel(name = "主键id")
    @ApiModelProperty("主键id")
    private Long id;

    /** 场景id */
    @Excel(name = "场景id")
    @ApiModelProperty("场景id")
    private Long sceneModelId;

    /** 录入型变量名 */
    @Excel(name = "录入型变量名")
    @ApiModelProperty("录入型变量名")
    private String name;

    /** 单位 */
    @Excel(name = "单位")
    @ApiModelProperty("单位")
    private String unit;

    /** 数据类型 */
    @Excel(name = "数据类型")
    @ApiModelProperty("数据类型")
    private String dataType;

    /** 默认值 */
    @Excel(name = "默认值")
    @ApiModelProperty("默认值")
    private String defaultValue;

    /** 是否只读 0-否 1-是，默认0 */
    @Excel(name = "是否只读 0-否 1-是，默认0")
    @ApiModelProperty("是否只读 0-否 1-是，默认0")
    private Integer isReadonly;

    /** 存储方式 0-不存储 1-存储 */
    @Excel(name = "存储方式 0-不存储 1-存储")
    @ApiModelProperty("存储方式 0-不存储 1-存储")
    private Integer storage;

    /** 变量类型 2-录入型变量 3-运算型变量 */
    @Excel(name = "变量类型 2-录入型变量 3-运算型变量")
    @ApiModelProperty("变量类型 2-录入型变量 3-运算型变量")
    private Integer variableType;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @Excel(name = "删除标志")
    private String delFlag;

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

    /** 周期执行（0未执行 1执行） */
    @ApiModelProperty("周期执行")
    @Excel(name = "周期执行")
    private Integer cycleExecuted;

    /** 计算公式 ${id} + ${id} */
    @Excel(name = "计算公式 ${id} + ${id}")
    @ApiModelProperty("计算公式 ${id} + ${id}")
    private String formule;

    /** 显示的计算公式  A+B */
    @Excel(name = "显示的计算公式  A+B")
    @ApiModelProperty("显示的计算公式  A+B")
    private String aliasFormule;

    /** 时间周期方式 1-周期计算 2-自定义时间段 */
    @Excel(name = "时间周期方式 1-周期计算 2-自定义时间段")
    @ApiModelProperty("时间周期方式 1-周期计算 2-自定义时间段")
    private Integer cycleType;

    /** 时间周期内容 */
    @Excel(name = "时间周期内容")
    @ApiModelProperty("时间周期内容")
    private String cycle;

    /** 时间周期内容 */
    @Excel(name = "是否配置为场景属性")
    @ApiModelProperty("是否配置为场景属性")
    private Integer isSceneAttribute;

    /**
     * 场景关联设备id
     */
    private Long sceneModelDeviceId;

    /**
     * 变量运算
     */
    private List<SceneTagPointsVO> tagPointsVOList = new ArrayList<>();

    /**
     * 启用
     */
    private Integer enable;

    /**
     * 租户id
     */
    private Long tenantId;

    private String cacheValue;
}
