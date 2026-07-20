package com.fastbee.iot.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.PageEntity;

/**
 * 运算型变量点对象 scene_tag_points
 *
 * @author zhuangpeng.li
 * @date 2024-12-26
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SceneTagPointsVO", description = "运算型变量点 scene_tag_points")
@Data
public class SceneTagPointsVO extends PageEntity {
    /** 代码生成区域 可直接覆盖**/
    /** 运算型变量点id */
    @Excel(name = "运算型变量点id")
    @ApiModelProperty("运算型变量点id")
    private Long id;

    /** 变量点名称 */
    @Excel(name = "变量点名称")
    @ApiModelProperty("变量点名称")
    private String name;

    /** 点别名，如 A */
    @Excel(name = "点别名，如 A")
    @ApiModelProperty("点别名，如 A")
    private String alias;

    /** 关联的变量id */
    @Excel(name = "关联的变量id")
    @ApiModelProperty("关联的变量id")
    private Long tagId;

    /** 统计方式 ，用字典定义，暂时是”原值“ */
    @Excel(name = "统计方式 ，用字典定义，暂时是”原值“")
    @ApiModelProperty("统计方式 ，用字典定义，暂时是”原值“")
    private Integer operation;

    /** 数据来源方式 1-设备物模型 2-录入型变量 3-运算型变量 */
    @Excel(name = "数据来源方式 1-设备物模型 2-录入型变量 3-运算型变量")
    @ApiModelProperty("数据来源方式 1-设备物模型 2-录入型变量 3-运算型变量")
    private Integer variableType;

    /** 数据源id,对应scene_model_data表id */
    @Excel(name = "数据源id,对应scene_model_data表id")
    @ApiModelProperty("数据源id,对应scene_model_data表id")
    private Long sceneModelDataId;

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

    /**
     * 场景关联设备id
     */
    private Long sceneModelDeviceId;

    /**
     * 场景关联设备名称
     */
    private String sceneModelDeviceName;
}
