package com.fastbee.iot.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import com.fastbee.common.annotation.Excel;

/**
 * 场景设备对象 iot_scene_device
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */

@ApiModel(value = "SceneDeviceVO", description = "场景设备 iot_scene_device")
@Data
public class SceneDeviceVO{

    /** 场景设备ID */
    @Excel(name = "场景设备ID")
    @ApiModelProperty("场景设备ID")
    private Long sceneDeviceId;

    /** 设备编号（产品触发的没有设备编号） */
    @ApiModelProperty("设备编号")
    @Excel(name = "设备编号")
    private String serialNumber;

    /** 产品ID */
    @Excel(name = "产品ID")
    @ApiModelProperty("产品ID")
    private Long productId;

    /** 产品名称 */
    @Excel(name = "产品名称")
    @ApiModelProperty("产品名称")
    private String productName;

    /** 触发源（1=设备触发，3=产品触发） */
    @ApiModelProperty("触发源")
    @Excel(name = "触发源")
    private Integer source;

    /** 场景ID */
    @Excel(name = "场景ID")
    @ApiModelProperty("场景ID")
    private Long sceneId;

    /** 场景脚本ID */
    @Excel(name = "场景脚本ID")
    @ApiModelProperty("场景脚本ID")
    private String scriptId;

    /** 类型（2=触发器，3=执行动作） */
    @ApiModelProperty("类型")
    @Excel(name = "类型")
    private Integer type;


}
