package com.fastbee.iot.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 场景管理关联设备对象 scene_model_device
 *
 * @author zhuangpeng.li
 * @date 2024-12-26
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SceneModelDevice", description = "场景管理关联设备 scene_model_device")
@Data
@TableName("scene_model_device" )
public class SceneModelDevice extends PageEntity{
    private static final long serialVersionUID=1L;

    /** 主键id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键id")
    private Long id;

    /** 场景id */
    @ApiModelProperty("场景id")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "scene_model_device.scene_model_id=scene_model.scene_model_id"
    )
    private Long sceneModelId;

    /** 关联设备id */
    @ApiModelProperty("关联设备id")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long cusDeviceId;

    /** 排序 */
    @ApiModelProperty("排序")
    private Integer sort;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic
    @AiSemanticField(
            semanticName = "删除标志",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private String delFlag;

    /** 创建者 */
    @ApiModelProperty("创建者")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 更新者 */
    @ApiModelProperty("更新者")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /** 备注 */
    @ApiModelProperty("备注")
    private String remark;

    /** 类型（0设备 1录入型 2运算型） */
    @ApiModelProperty("类型")
    @AiSemanticField(
            semanticName = "类型",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private Integer variableType;

    /** 全部启用（0否 1是） */
    @ApiModelProperty("全部启用")
    @AiSemanticField(
            semanticName = "全部启用",
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private Integer allEnable;

    /** 名称 */
    @ApiModelProperty("名称")
    private String name;

}
