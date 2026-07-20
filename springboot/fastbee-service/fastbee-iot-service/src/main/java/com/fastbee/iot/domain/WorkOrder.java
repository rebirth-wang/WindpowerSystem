package com.fastbee.iot.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * 工单管理对象 iot_work_order
 *
 * @author fastbee
 * @date 2025-08-18
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "WorkOrder", description = "工单管理 iot_work_order")
@Data
@TableName("iot_work_order" )
public class WorkOrder extends PageEntity {
    private static final long serialVersionUID=1L;

    /** 主键id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键id")
    private Long id;

    /** 工单名称 */
    @ApiModelProperty("工单名称")
    private String name;

    /** 详细信息 */
    @ApiModelProperty("详细信息")
    private String details;

    /** 工单状态 */
    @ApiModelProperty("工单状态")
    @AiSemanticField(
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "work_order_status",
            queryHint = "check-value-mapping"
    )
    private Integer status;

    /** 工单类型 */
    @ApiModelProperty("工单类型")
    @AiSemanticField(
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "work_order_type"
    )
    private Integer type;

    /** 工单编号 */
    @ApiModelProperty("工单编号")
    private String number;

    /** 联系人 */
    @ApiModelProperty("联系人")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_work_order.user_id=sys_user.user_id"
    )
    private Long userId;

    /** 设备id */
    @ApiModelProperty("设备id")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_work_order.device_id=iot_device.device_id"
    )
    private Long deviceId;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("截止时间")
    private Date endTime;

    /** 处理结果 */
    @ApiModelProperty("处理结果")
    private String result;

    /** 所属租户id */
    @ApiModelProperty("所属租户id")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "系统租户隔离字段，查询时自动追加租户过滤",
            queryHint = "auto-data-scope"
    )
    private Long tenantId;

    /** 租户名称 */
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 删除标志（0代表存在 1代表删除） */
    @ApiModelProperty("删除标志")
    @AiSemanticField(
            semanticName = "删除标志",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private Integer delFlag;

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

    /**
     * 设备维保ID
     */
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long deviceMaintenanceId;

}
