package com.fastbee.iot.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.core.domain.BaseEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;
import com.fastbee.iot.util.KingbaseJsonTypeHandler;

/**
 * 物模型模板对象 iot_things_model_template
 *
 * @author zhuangpeng.li
 * @date 2024-11-14
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ThingsModelTemplate", description = "物模型模板 iot_things_model_template")
@Data
@TableName("iot_things_model_template" )
public class ThingsModelTemplate extends BaseEntity{
    private static final long serialVersionUID=1L;

    /** 物模型ID */
    @TableId(value = "template_id", type = IdType.AUTO)
    @ApiModelProperty("物模型ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_things_model_template.template_id=iot_var_temp.template_id;iot_things_model_template.template_id=iot_device_template.template_id"
    )
    private Long templateId;

    /** 物模型名称 */
    @ApiModelProperty("物模型名称")
    private String templateName;

    /** 租户ID */
    @ApiModelProperty("租户ID")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "系统租户隔离字段，查询时自动追加租户过滤",
            queryHint = "auto-data-scope"
    )
    private Long tenantId;

    /** 租户名称 */
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 标识符，产品下唯一 */
    @ApiModelProperty("标识符，产品下唯一")
    private String identifier;

    /** 模型类别（1-属性，2-功能，3-事件） */
    @ApiModelProperty("模型类别")
    @AiSemanticField(
            semanticName = "模型类别",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_things_type"
    )
    private Integer type;

    /** 数据类型（integer、decimal、string、bool、array、enum） */
    @ApiModelProperty("数据类型")
    @AiSemanticField(
            semanticName = "数据类型",
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_data_type"
    )
    private String datatype;

    /** 数据定义
     * KingbaseJsonTypeHandler为人大金仓数据库适配新增适配器
     * */
    @TableField(typeHandler = KingbaseJsonTypeHandler.class)
    @ApiModelProperty("数据定义")
    private String specs;

    /** 是否系统通用（0-否，1-是） */
    @ApiModelProperty("是否系统通用")
    @AiSemanticField(
            semanticName = "是否系统通用",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "system_type_status",
            valueMappings = {"0=否，1=是"}
    )
    private Integer isSys;

    /** 是否图表展示（0-否，1-是） */
    @ApiModelProperty("是否图表展示")
    @AiSemanticField(
            semanticName = "是否图表展示",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_yes_no"
    )
    private Integer isChart;

    /** 是否实时监测（0-否，1-是） */
    @ApiModelProperty("是否实时监测")
    @AiSemanticField(
            semanticName = "是否实时监测",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_yes_no"
    )
    private Integer isMonitor;

    /** 是否历史存储 (0-否，1-是） */
    @ApiModelProperty("是否历史存储 (0-否，1-是）")
    @AiSemanticField(
            semanticName = "是否历史存储",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_yes_no"
    )
    private Integer isHistory;

    /** 是否只读数据(0-否，1-是) */
    @ApiModelProperty("是否只读数据(0-否，1-是)")
    @AiSemanticField(
            semanticName = "是否只读数据",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "iot_yes_no"
    )
    private Integer isReadonly;

    /** 是否设备分享权限(0-否，1-是) */
    @ApiModelProperty("是否设备分享权限(0-否，1-是)")
    @AiSemanticField(
            semanticName = "是否设备分享权限",
            semanticType = AiSemanticType.ENUM
    )
    private Integer isSharePerm;

    /** 排序，值越大，排序越靠前 */
    @ApiModelProperty("排序，值越大，排序越靠前")
    private Integer modelOrder;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    @TableLogic
    @AiSemanticField(
            semanticName = "删除标志",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private String delFlag;

    /** 计算公式 */
    @ApiModelProperty("计算公式")
    private String formula;

    /** 是否在APP显示(0-否，1-是) */
    @ApiModelProperty("是否在APP显示(0-否，1-是)")
    @AiSemanticField(
            semanticName = "是否在APP显示",
            semanticType = AiSemanticType.ENUM
    )
    private Integer isApp;

    /** 物模型名称 */
    @TableField(exist = false)
    @ApiModelProperty("中文菜单名称")
    private String templateName_zh_CN;

    /** 物模型名称 */
    @TableField(exist = false)
    @ApiModelProperty("英文菜单名称")
    private String templateName_en_US;

}
