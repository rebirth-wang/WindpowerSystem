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
import lombok.experimental.Accessors;

import com.fastbee.common.core.domain.PageEntity;
import com.fastbee.common.extend.ai.annotation.AiSemanticField;
import com.fastbee.common.extend.ai.enums.AiSemanticSourceType;
import com.fastbee.common.extend.ai.enums.AiSemanticType;

/**
 * 设备记录对象 iot_device_record
 *
 * @author zhuangpeng.li
 * @date 2024-11-13
 */

@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "DeviceRecord", description = "设备记录 iot_device_record")
@Data
@Accessors(chain = true)
@TableName("iot_device_record" )
public class DeviceRecord extends PageEntity{
    /** 主键id */
    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键id")
    private Long id;

    /** 操作者机构id */
    @ApiModelProperty("操作者机构id")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long operateDeptId;

    /** 目标机构id */
    @ApiModelProperty("目标机构id")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long targetDeptId;

    /** 产品id */
    @ApiModelProperty("产品id")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_device_record.product_id=iot_product.product_id;可通过 iot_product.product_id=iot_things_model.product_id 继续关联物模型"
    )
    private Long productId;

    /** 设备id */
    @ApiModelProperty("设备id")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_device_record.device_id=iot_device.device_id"
    )
    private Long deviceId;

    /** 设备编号 */
    @ApiModelProperty("设备编号")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "iot_device_record.serial_number=iot_device.serial_number;可通过 iot_device.product_id=iot_product.product_id 继续关联产品",
            queryHint = "device-number"
    )
    private String serialNumber;

    /** 父id */
    @ApiModelProperty("父id")
    @AiSemanticField(semanticType = AiSemanticType.RELATION_KEY)
    private Long parentId;

    /** 设备记录类型（1-导入记录；2-回收记录；3-分配记录；4-分配详细记录） */
    @ApiModelProperty("设备记录类型")
    @AiSemanticField(
            semanticName = "设备记录类型",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private Integer type;

    /** 分配类型（1-选择分配；2-导入分配） */
    @ApiModelProperty("分配类型")
    @AiSemanticField(
            semanticName = "分配类型",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.AUTO_COMMENT)
    private Integer distributeType;

    /** 总数 */
    @ApiModelProperty("总数")
    private Integer total;

    /** 成功数量 */
    @ApiModelProperty("成功数量")
    private Integer successQuantity;

    /** 失败数量 */
    @ApiModelProperty("失败数量")
    private Integer failQuantity;

    /** 状态（0-失败；1-成功） */
    @ApiModelProperty("状态")
    @AiSemanticField(
            semanticName = "状态",
            semanticType = AiSemanticType.ENUM,
            sourceType = AiSemanticSourceType.DICT,
            sourceCode = "common_status_type",
            valueMappings = {"-失败", "1-成功=0"},
            queryHint = "check-value-mapping"
    )
    private Integer status;

    /** 租户id */
    @ApiModelProperty("租户id")
    @AiSemanticField(
            semanticType = AiSemanticType.RELATION_KEY,
            relationHint = "系统租户隔离字段，查询时自动追加租户过滤",
            queryHint = "auto-data-scope"
    )
    private Long tenantId;

    /** 租户名称 */
    @ApiModelProperty("租户名称")
    private String tenantName;

    /** 创建人 */
    @ApiModelProperty("创建人")
    private String createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("创建时间")
    private Date createTime;

    /** 更新人 */
    @ApiModelProperty("更新人")
    private String updateBy;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date updateTime;

    /** 逻辑删除标识 */
    @ApiModelProperty("逻辑删除标识")
    @TableLogic
    @AiSemanticField(semanticType = AiSemanticType.ENUM)
    private Integer delFlag;

}
