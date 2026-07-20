package com.fastbee.iot.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fastbee.common.annotation.Excel;
import com.fastbee.common.core.domain.BaseEntity;

/**
 * 物模型模板对象 iot_things_model_template
 *
 * @author zhuangpeng.li
 * @date 2024-11-14
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ThingsModelTemplateVO", description = "物模型模板 iot_things_model_template")
@Data
public class ThingsModelTemplateVO extends BaseEntity {

    /** 物模型ID */
    @ApiModelProperty("物模型ID")
    private Long templateId;

    /** 物模型名称 */
    @Excel(name = "物模型名称")
    @ApiModelProperty("物模型名称")
    private String templateName;

    /** 租户ID */
    @ApiModelProperty("租户ID")
    private Long tenantId;

    /** 租户名称 */
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 标识符，产品下唯一 */
    @Excel(name = "标识符")
    @ApiModelProperty("标识符，产品下唯一")
    private String identifier;

    /** 模型类别（1-属性，2-功能，3-事件） */
    @ApiModelProperty("模型类别")
    @Excel(name = "模型类别")
    private Integer type;

    /** 数据类型（integer、decimal、string、bool、array、enum） */
    @ApiModelProperty("数据类型")
    @Excel(name = "数据类型")
    private String datatype;

    /** 数据定义 */
    @ApiModelProperty("数据定义")
    private String specs;

    /** 是否系统通用（0-否，1-是） */
    @ApiModelProperty("是否系统通用")
    @Excel(name = "是否系统通用")
    private Integer isSys;

    /** 是否图表展示（0-否，1-是） */
    @ApiModelProperty("是否图表展示")
    @Excel(name = "是否图表展示")
    private Integer isChart;

    /** 是否实时监测（0-否，1-是） */
    @ApiModelProperty("是否实时监测")
    @Excel(name = "是否实时监测")
    private Integer isMonitor;

    /** 是否历史存储 (0-否，1-是） */
    @Excel(name = "是否历史存储")
    @ApiModelProperty("是否历史存储 (0-否，1-是）")
    private Integer isHistory;

    /** 是否只读数据(0-否，1-是) */
    @Excel(name = "是否只读数据")
    @ApiModelProperty("是否只读数据(0-否，1-是)")
    private Integer isReadonly;

    /** 是否设备分享权限(0-否，1-是) */
    @Excel(name = "是否设备分享权限")
    @ApiModelProperty("是否设备分享权限(0-否，1-是)")
    private Integer isSharePerm;

    /** 排序，值越大，排序越靠前 */
    @Excel(name = "排序值")
    @ApiModelProperty("排序，值越大，排序越靠前")
    private Integer modelOrder;

    /** 删除标志（0代表存在 2代表删除） */
    @ApiModelProperty("删除标志")
    private String delFlag;

    /** 计算公式 */
    @Excel(name = "计算公式")
    @ApiModelProperty("计算公式")
    private String formula;

    /** 是否在APP显示(0-否，1-是) */
    @ApiModelProperty("是否在APP显示(0-否，1-是)")
    private Integer isApp;


    private String language;

    /**
     * 同一租户或用户为true
     */
    private Boolean owner;

    @Excel(name = "单位")
    private String unit;

    @ApiModelProperty("有效值范围")
    @Excel(name = "有效值范围")
    private String limitValue;

    private Boolean adminFlag;

    /** 物模型名称 */
    @TableField(exist = false)
    @ApiModelProperty("中文物模型名称")
    private String templateName_zh_CN;

    /** 物模型名称 */
    @TableField(exist = false)
    @ApiModelProperty("英文物模型名称")
    @Excel(name = "英文物模型名称")
    private String templateName_en_US;

}
