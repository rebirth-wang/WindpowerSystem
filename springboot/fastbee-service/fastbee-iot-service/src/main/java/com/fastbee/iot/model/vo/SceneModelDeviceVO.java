package com.fastbee.iot.model.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 场景管理关联设备对象 scene_model_device
 *
 * @author zhuangpeng.li
 * @date 2024-12-26
 */

@ApiModel(value = "SceneModelDeviceVO", description = "场景管理关联设备 scene_model_device")
@Data
public class SceneModelDeviceVO{
    /** 代码生成区域 可直接覆盖**/
    /** 主键id */
    @Excel(name = "主键id")
    @ApiModelProperty("主键id")
    private Long id;

    /** 场景id */
    @Excel(name = "场景id")
    @ApiModelProperty("场景id")
    private Long sceneModelId;

    /** 关联设备id */
    @Excel(name = "关联设备id")
    @ApiModelProperty("关联设备id")
    private Long cusDeviceId;

    /** 排序 */
    @Excel(name = "排序")
    @ApiModelProperty("排序")
    private Integer sort;

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

    /** 类型（0设备 1录入型 2运算型） */
    @ApiModelProperty("类型")
    @Excel(name = "类型")
    private Integer variableType;

    /** 全部启用（0否 1是） */
    @ApiModelProperty("全部启用")
    @Excel(name = "全部启用")
    private Integer allEnable;

    /** 名称 */
    @Excel(name = "名称")
    @ApiModelProperty("名称")
    private String name;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 设备编号
     */
    private String serialNumber;

    /**
     * 租户 ID
     */
    private Long tenantId;

}
