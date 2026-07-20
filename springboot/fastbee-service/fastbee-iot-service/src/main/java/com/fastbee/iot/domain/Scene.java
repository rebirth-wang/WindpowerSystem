package com.fastbee.iot.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.BaseEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 场景联动对象 iot_scene
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Scene", description = "场景联动 iot_scene")
@Data
@TableName("iot_scene" )
public class Scene extends BaseEntity{
    private static final long serialVersionUID=1L;

    /** 场景ID */
    @TableId(value = "scene_id", type = IdType.AUTO)
    @ApiModelProperty("场景ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_scene.scene_id=iot_alert_scene.scene_id"
    )
    private Long sceneId;

    /** 场景名称 */
    @ApiModelProperty("场景名称")
    private String sceneName;

    /** 规则名称 */
    @ApiModelProperty("规则名称")
    private String chainName;

    /** 场景状态（1-启动，2-停止） */
    @ApiModelProperty("场景状态")
    @AiSemanticField(
            semanticName = "场景状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private Integer enable;

    /** 用户ID */
    @ApiModelProperty("用户ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_scene.user_id=sys_user.user_id"
    )
    private Long userId;

    /** 用户名称 */
    @ApiModelProperty("用户名称")
    private String userName;

    /** 静默周期（分钟） */
    @ApiModelProperty("静默周期")
    @AiSemanticField(
            semanticName = "静默周期",
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private Long silentPeriod;

    /** 执行条件（1=或、任意条件，2=且、所有条件，3=非，不满足） */
    @ApiModelProperty("执行条件")
    @AiSemanticField(
            semanticName = "执行条件",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "trigger_condition",
            valueMappings = {"=或、任意条件，2=且、所有条件，3=非，不满足=1"}
    )
    private Integer cond;

    /** 执行方式（1=串行，顺序执行，2=并行，同时执行） */
    @ApiModelProperty("执行方式")
    @AiSemanticField(
            semanticName = "执行方式",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "execution_method",
            valueMappings = {"=串行，顺序执行，2=并行，同时执行=1"}
    )
    private Integer executeMode;

    /** 延时执行（秒钟） */
    @ApiModelProperty("延时执行")
    @AiSemanticField(
            semanticName = "延时执行",
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private Integer executeDelay;

    /** 是否包含告警推送（1=包含，2=不包含） */
    @ApiModelProperty("是否包含告警推送")
    @AiSemanticField(
            semanticName = "是否包含告警推送",
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private Integer hasAlert;

    /** 应用名称 */
    @ApiModelProperty("应用名称")
    private String applicationName;

    /** 规则数据 */
    @ApiModelProperty("规则数据")
    private String elData;

    /** 是否终端用户（1-是，0-不是） */
    @ApiModelProperty("是否终端用户")
    @AiSemanticField(
            semanticName = "是否终端用户",
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private Integer terminalUser;

    /** 延时匹配（秒钟） */
    @ApiModelProperty("延时匹配")
    @AiSemanticField(
            semanticName = "延时匹配",
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private Integer checkDelay;

    /** 恢复告警场景ID */
    @ApiModelProperty("恢复告警场景ID")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long recoverId;

}
